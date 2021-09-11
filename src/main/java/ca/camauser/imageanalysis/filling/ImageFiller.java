package ca.camauser.imageanalysis.filling;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class ImageFiller {

    private final BufferedImage image;
    private final boolean[][] highlightedPixelArray;
    private final boolean[][] exteriorPixelArray;

    public ImageFiller(File imageFile) {
        try {
            image = ImageIO.read(imageFile);
            highlightedPixelArray = new boolean[image.getHeight()][image.getWidth()];
            exteriorPixelArray = new boolean[image.getHeight()][image.getWidth()];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ImageFillResult process(int highlightColorRgb) {
        fillHighlightedPixelArray(highlightColorRgb);
        fillExteriorPixelArray();

        BufferedImage filledImage = toFilledImage();
        return new ImageFillResult(filledImage);
    }

    private void fillHighlightedPixelArray(int highlightColorRgb) {
        for (int row = 0; row < image.getHeight(); row++) {
            for (int column = 0; column < image.getWidth(); column++) {
                int color = image.getRGB(column, row);
                int maskedColor = color & 0x00FFFFFF;
                if (withinNPercent(highlightColorRgb, maskedColor, 0.20)) {
                    markAsHighlighted(row, column);
                } else {
                    markAsUnhighlighted(row, column);
                }
            }
        }
    }

    private void fillExteriorPixelArray() {
        Queue<Pixel> pixelsToProcess = new LinkedList<>();
        Set<Pixel> processedPixels = new HashSet<>();
        pixelsToProcess.add(new Pixel(0, 0));

        while (!pixelsToProcess.isEmpty()) {
            Pixel pixel = pixelsToProcess.remove();
            int row = pixel.getRow();
            int column = pixel.getColumn();
            boolean outOfBounds = row < 0 || column < 0 || row >= image.getHeight() || column >= image.getWidth();
            if (outOfBounds) {
                continue;
            }

            if (processedPixels.contains(pixel)) {
                continue;
            }

            boolean outlined = highlightedPixelArray[row][column];
            if (outlined) {
                continue;
            }

            // exterior pixel
            exteriorPixelArray[row][column] = true;
            processedPixels.add(pixel);
            pixelsToProcess.add(new Pixel(row - 1, column));
            pixelsToProcess.add(new Pixel(row + 1, column));
            pixelsToProcess.add(new Pixel(row, column - 1));
            pixelsToProcess.add(new Pixel(row, column + 1));
        }
    }

    // from https://stackoverflow.com/questions/9018016/how-to-compare-two-colors-for-similarity-difference
    private boolean withinNPercent(int desiredRgb, int actualRgb, double percentage) {
        double redDifference = Math.pow((getRed(desiredRgb) - getRed(actualRgb)), 2);
        double blueDifference = Math.pow((getBlue(desiredRgb) - getBlue(actualRgb)), 2);
        double greenDifference = Math.pow((getGreen(desiredRgb) - getGreen(actualRgb)), 2);
        double distance = Math.sqrt(redDifference + blueDifference + greenDifference);
        double percentageDifferent = distance / Math.sqrt(Math.pow(255, 2) + Math.pow(255, 2) + Math.pow(255, 2));
        return percentage >= percentageDifferent;
    }

    private int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    private int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    private int getBlue(int color) {
        return color & 0xFF;
    }

    private BufferedImage generateImage(boolean[][] highlightedPixelArray) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), TYPE_INT_RGB);
        for (int row = 0; row < bufferedImage.getHeight(); row++) {
            for (int column = 0; column < bufferedImage.getWidth(); column++) {
                boolean marked = highlightedPixelArray[row][column];
                if (marked) {
                    bufferedImage.setRGB(column, row, 0xFF000000);
                } else {
                    bufferedImage.setRGB(column, row, 0xFFFFFFFF);
                }
            }
        }

        return bufferedImage;
    }

    private BufferedImage toFilledImage() {
        return generateImage(exteriorPixelArray);
    }

    private void markAsHighlighted(int row, int column) {
        highlightedPixelArray[row][column] = true;
    }

    private void markAsUnhighlighted(int row, int column) {
        highlightedPixelArray[row][column] = false;
    }
}
