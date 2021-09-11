package ca.camauser.imageanalysis.filling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FillingMain {
    private static final int CROPPED_IMAGE_DIRECTORY = 0;
    private static final int OUTPUT_DIRECTORY_INDEX = 1;
    private static final int OUTLINE_COLOR_INDEX = 2;
    private static final int EXPECTED_ARG_COUNT = 3;

    private static void printUsage() {
        System.out.println("Expected arguments: <cropped-image-directory> <output-directory> <outline-rgb>");
        System.out.println("Example: images/cropped images/output 0x00FF00");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != EXPECTED_ARG_COUNT) {
            printUsage();
            return;
        }

        Path imagePath = Paths.get(args[CROPPED_IMAGE_DIRECTORY]);
        Path outputDirectory = Paths.get(args[OUTPUT_DIRECTORY_INDEX]);
        int outlineColor = Integer.decode(args[OUTLINE_COLOR_INDEX]);
        outputDirectory.toFile().mkdirs();

        ConcurrentLinkedQueue<ImageFillTask> tasks = generateTaskQueue(imagePath, outputDirectory, outlineColor);

        List<Thread> processorThreads = new LinkedList<>();
        int threadCount = 8;
        for (int id = 1; id <= threadCount; id++) {
            ImageProcessorThread thread = new ImageProcessorThread(id, tasks);
            processorThreads.add(thread);
            thread.start();
            System.out.println("Started thread " + id);
        }

        for (int id = 1; id <= threadCount; id++) {
            processorThreads.get(id - 1).join();
            System.out.println("Thread " + id + " stopped");
        }

        System.out.println("Processing finished");
    }

    private static ConcurrentLinkedQueue<ImageFillTask> generateTaskQueue(Path imageDirectory, Path outputDirectory, int outlineColor) throws IOException {
        ConcurrentLinkedQueue<ImageFillTask> taskQueue = new ConcurrentLinkedQueue<>();
        Files.list(imageDirectory).forEach(img -> taskQueue.add(new ImageFillTask(img, outputDirectory, outlineColor)));
        return taskQueue;
    }
}
