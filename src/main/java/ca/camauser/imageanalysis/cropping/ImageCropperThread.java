package ca.camauser.imageanalysis.cropping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Queue;

public class ImageCropperThread extends Thread {

    private final int id;
    private final Queue<CropTask> taskQueue;

    public ImageCropperThread(int id, Queue<CropTask> taskQueue) {
        this.id = id;
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        CropTask task = taskQueue.poll();
        while (task != null) {
            try {
                ImageCropper cropper = new ImageCropper(task.getOriginalImage().toFile());
                BufferedImage croppedImage = cropper.crop();
                File outputFile = task.getOutputDirectory().resolve(task.getOriginalImage().getFileName()).toFile();
                ImageIO.write(croppedImage, "png", outputFile);
                System.out.printf("Thread %d wrote out %s%n", id, outputFile);
                task = taskQueue.poll();
            } catch (Exception e) {
                System.out.println("Failed to process " + task.getOriginalImage());
                e.printStackTrace();
            }
        }
    }
}
