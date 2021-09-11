package ca.camauser.imageanalysis.filling;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Queue;

public class ImageProcessorThread extends Thread {
    private final int id;
    private final Queue<ImageFillTask> taskQueue;

    public ImageProcessorThread(int id, Queue<ImageFillTask> taskQueue) {
        this.id = id;
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        ImageFillTask task = taskQueue.poll();
        while (task != null) {
            File imageFile = task.getImagePath().toFile();
            ImageFiller filler = new ImageFiller(imageFile);
            ImageFillResult result = filler.process(task.getOutlineColor());
            try {
                ImageIO.write(result.getFilledImage(), "png", getFilledFile(task.getOutputDirectory(), imageFile.getName()));
            } catch (IOException e) {
                System.err.printf("Thread %d: failed to process image %s: %s%n", id, imageFile, e.getMessage());
            }

            System.out.printf("Thread %d finished %s%n", id, task.getImagePath());
            task = taskQueue.poll();
        }
    }

    private File getFilledFile(Path outputDir, String imageFileName) {
        int extensionStartIndex = imageFileName.indexOf(".");
        String fileName = imageFileName.substring(0, extensionStartIndex) + "-filled.png";
        return outputDir.resolve(fileName).toFile();
    }
}
