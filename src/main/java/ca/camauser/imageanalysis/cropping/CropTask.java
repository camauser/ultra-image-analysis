package ca.camauser.imageanalysis.cropping;

import java.nio.file.Path;
import java.util.Objects;

public class CropTask {
    private final Path originalImage;
    private final Path outputDirectory;

    public CropTask(Path originalImage, Path outputDirectory) {
        this.originalImage = originalImage;
        this.outputDirectory = outputDirectory;
    }

    public Path getOriginalImage() {
        return originalImage;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CropTask cropTask = (CropTask) o;
        return Objects.equals(originalImage, cropTask.originalImage) && Objects.equals(outputDirectory, cropTask.outputDirectory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalImage, outputDirectory);
    }
}
