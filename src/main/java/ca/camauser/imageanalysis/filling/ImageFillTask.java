package ca.camauser.imageanalysis.filling;

import java.nio.file.Path;

public class ImageFillTask {
    private final Path imagePath;
    private final Path outputDirectory;
    private final int outlineColor;

    public ImageFillTask(Path imagePath, Path outputDirectory, int outlineColor) {
        this.imagePath = imagePath;
        this.outputDirectory = outputDirectory;
        this.outlineColor = outlineColor;
    }

    public Path getImagePath() {
        return imagePath;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public int getOutlineColor() {
        return outlineColor;
    }
}
