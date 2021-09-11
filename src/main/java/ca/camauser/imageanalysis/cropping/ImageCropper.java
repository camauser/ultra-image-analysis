package ca.camauser.imageanalysis.cropping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCropper {
    private static final int RGB_MASK = 0x00FFFFFF;
    private static final int BACKGROUND_COLOR = 0X00FF0000;
    private static final int START_OF_IMAGE_COLOR = 0X00000000;

    private final BufferedImage image;

    public ImageCropper(File imageFile) throws IOException {
        image = ImageIO.read(imageFile);
    }

    public BufferedImage crop() {
        int startColumn = getImageStartColumn();
        int endColumn = getImageEndColumn();
        int startRow = getStartRow(startColumn);
        int endRow = image.getHeight() - 1;
        BufferedImage output = new BufferedImage(endColumn - startColumn + 1, endRow - startRow + 1, BufferedImage.TYPE_INT_RGB);

        int[] pixelArray = image.getRGB(startColumn, startRow, output.getWidth(), output.getHeight(), null, 0, output.getWidth());
        output.setRGB(0, 0, output.getWidth(), output.getHeight(), pixelArray, 0, output.getWidth());

        return output;
    }

    private int getImageStartColumn() {
        int row = image.getHeight() - 1;
        for (int currentColumn = 0; currentColumn < image.getWidth(); currentColumn++) {
            int color = image.getRGB(currentColumn, row);
            int maskedColor = color & RGB_MASK;
            if (maskedColor != BACKGROUND_COLOR) {
                return currentColumn;
            }
        }

        throw new RuntimeException("Didn't find a non-background-color");
    }

    private int getImageEndColumn() {
        int row = image.getHeight() - 1;
        for (int currentColumn = image.getWidth() - 1; currentColumn >= 0; currentColumn--) {
            int color = image.getRGB(currentColumn, row);
            int maskedColor = color & RGB_MASK;
            if (maskedColor != BACKGROUND_COLOR) {
                return currentColumn;
            }
        }

        throw new RuntimeException("Didn't find a non-background-color");
    }

    private int getStartRow(int column) {
        for (int row = 0; row < image.getHeight(); row++) {
            int color = image.getRGB(column, row);
            int maskedColor = color & RGB_MASK;
            if (maskedColor == START_OF_IMAGE_COLOR) {
                return row;
            }
        }

        throw new RuntimeException("Didn't find end of header");
    }
}
