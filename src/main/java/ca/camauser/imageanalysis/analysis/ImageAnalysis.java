package ca.camauser.imageanalysis.analysis;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class ImageAnalysis {

    private final Path computerGeneratedImage;
    private int falseNegatives = 0;
    private int trueNegatives = 0;
    private int falsePositives = 0;
    private int truePositives = 0;
    private BufferedImage visualResult;

    public ImageAnalysis(Path computerGeneratedImage) {
        this.computerGeneratedImage = computerGeneratedImage;
    }

    public void addFalseNegative() {
        falseNegatives++;
    }

    public void addTrueNegative() {
        trueNegatives++;
    }

    public void addFalsePositive() {
        falsePositives++;
    }

    public void addTruePositive() {
        truePositives++;
    }

    public void setVisualResult(BufferedImage visualResult) {
        this.visualResult = visualResult;
    }

    public Path getComputerGeneratedImage() {
        return computerGeneratedImage;
    }

    public int getFalseNegatives() {
        return falseNegatives;
    }

    public int getTrueNegatives() {
        return trueNegatives;
    }

    public int getFalsePositives() {
        return falsePositives;
    }

    public int getTruePositives() {
        return truePositives;
    }

    public BufferedImage getVisualResult() {
        return visualResult;
    }
}
