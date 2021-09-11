package ca.camauser.imageanalysis.cropping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CroppingMain {
    private static final int INPUT_DIRECTORY_INDEX = 0;
    private static final int OUTPUT_DIRECTORY_INDEX = 1;
    private static final int EXPECTED_ARG_COUNT = 2;

    public static void printUsage() {
        System.out.println("Expected arguments: <input-directory> <output-directory>");
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length != EXPECTED_ARG_COUNT) {
            printUsage();
            return;
        }

        Path imageDirectory = Paths.get(args[INPUT_DIRECTORY_INDEX]);
        Path outputDirectory = Paths.get(args[OUTPUT_DIRECTORY_INDEX]);
        outputDirectory.toFile().mkdirs();

        ConcurrentLinkedQueue<CropTask> taskQueue = generateTaskQueue(imageDirectory, outputDirectory);

        int threadCount = 8;
        List<Thread> threads = new LinkedList<>();
        for (int threadId = 1; threadId <= threadCount; threadId++) {
            ImageCropperThread thread = new ImageCropperThread(threadId, taskQueue);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Cropping complete!");
    }

    private static ConcurrentLinkedQueue<CropTask> generateTaskQueue(Path imageDirectory, Path outputDirectory) throws IOException {
        ConcurrentLinkedQueue<CropTask> taskQueue = new ConcurrentLinkedQueue<>();
        Files.list(imageDirectory).forEach(img -> taskQueue.add(new CropTask(img, outputDirectory)));
        return taskQueue;
    }
}
