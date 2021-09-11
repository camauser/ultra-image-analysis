package ca.camauser.imageanalysis.filling;

import java.awt.image.BufferedImage;

public class ImageFillResult {
    private final BufferedImage filledImage;

    public ImageFillResult(BufferedImage filledImage) {
        this.filledImage = filledImage;
    }

    public BufferedImage getFilledImage() {
        return filledImage;
    }
}
