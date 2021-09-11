package ca.camauser.imageanalysis.analysis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class ImageAnalyzer {
    private static final int PIXEL_COLOR_MASK = 0x00FFFFFF;
    private static final int HIGHLIGHTED_PIXEL_COLOR = 0x00FFFFFF;
    private static final int TRUE_POSITIVE_COLOR = 0x00FFFF00;
    private static final int TRUE_NEGATIVE_COLOR = 0x00FFFFFF;
    private static final int FALSE_POSITIVE_COLOR = 0x00FF0000;
    private static final int FALSE_NEGATIVE_COLOR = 0x0000FF00;
    private final Path computerHighlighted;
    private final Path humanHighlighted;

    public ImageAnalyzer(Path computerHighlighted, Path humanHighlighted) {
        this.computerHighlighted = computerHighlighted;
        this.humanHighlighted = humanHighlighted;
    }

    public ImageAnalysis analyze() throws IOException {
        BufferedImage computerImage = ImageIO.read(computerHighlighted.toFile());
        BufferedImage humanImage = ImageIO.read(humanHighlighted.toFile());

        validateSameDimensions(computerImage, humanImage);
        BufferedImage visualResult = new BufferedImage(humanImage.getWidth(), humanImage.getHeight(), TYPE_INT_RGB);

        ImageAnalysis analysis = new ImageAnalysis(computerHighlighted);
        for (int row = 0; row < humanImage.getHeight(); row++) {
            for (int column = 0; column < humanImage.getWidth(); column++) {
                boolean humanHighlighted = (humanImage.getRGB(column, row) & PIXEL_COLOR_MASK) == HIGHLIGHTED_PIXEL_COLOR;
                boolean computerHighlighted = (computerImage.getRGB(column, row) & PIXEL_COLOR_MASK) == HIGHLIGHTED_PIXEL_COLOR;

                if (humanHighlighted && computerHighlighted) {
                    analysis.addTruePositive();
                    visualResult.setRGB(column, row, TRUE_POSITIVE_COLOR);
                } else if (humanHighlighted && !computerHighlighted) {
                    analysis.addFalseNegative();
                    visualResult.setRGB(column, row, FALSE_NEGATIVE_COLOR);
                } else if (!humanHighlighted && !computerHighlighted) {
                    analysis.addTrueNegative();
                    visualResult.setRGB(column, row, TRUE_NEGATIVE_COLOR);
                } else if (!humanHighlighted && computerHighlighted) {
                    analysis.addFalsePositive();
                    visualResult.setRGB(column, row, FALSE_POSITIVE_COLOR);
                }
            }
        }

        analysis.setVisualResult(visualResult);

        return analysis;
    }

    private void validateSameDimensions(BufferedImage computerImage, BufferedImage humanImage) {
        if (computerImage.getWidth() != humanImage.getWidth() || computerImage.getHeight() != humanImage.getHeight()) {
            throw new IllegalArgumentException(String.format("Image dimensions don't match. Images were %s x %s and %s x %s",
                    computerImage.getWidth(), computerImage.getHeight(), humanImage.getWidth(), humanImage.getHeight()));
        }
    }

}
