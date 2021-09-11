package ca.camauser.imageanalysis.analysis;

import java.nio.file.Path;

public class ImageAnalysisTask {

    private final Path computerImage;
    private final Path humanImage;

    public ImageAnalysisTask(Path computerImage, Path humanImage) {
        this.computerImage = computerImage;
        this.humanImage = humanImage;
    }

    public Path getComputerImage() {
        return computerImage;
    }

    public Path getHumanImage() {
        return humanImage;
    }
}
