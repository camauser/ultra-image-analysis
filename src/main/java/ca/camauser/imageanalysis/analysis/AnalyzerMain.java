package ca.camauser.imageanalysis.analysis;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class AnalyzerMain {

    private static final int COMPUTER_IMAGE_DIRECTORY = 0;
    private static final int HUMAN_IMAGE_DIRECTORY = 1;
    private static final int COLORED_ANALYSIS_OUTPUT_DIRECTORY = 2;
    private static final int SUMMARY_FILE = 3;
    private static final int EXPECTED_ARG_COUNT = 4;

    private static void printUsage() {
        System.out.println("Expected arguments: <cropped-and-filled-computer-image-directory> <cropped-and-filled-human-image-directory> <colored-analysis-output-directory> <summary-file>");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != EXPECTED_ARG_COUNT) {
            printUsage();
            return;
        }

        Path computerDirectory = Paths.get(args[COMPUTER_IMAGE_DIRECTORY]);
        Path humanDirectory = Paths.get(args[HUMAN_IMAGE_DIRECTORY]);

        ConcurrentLinkedQueue<ImageAnalysisTask> taskQueue = generateTaskQueue(computerDirectory, humanDirectory);
        List<ImageAnalysis> outputList = Collections.synchronizedList(new ArrayList<>());

        List<ImageAnalyzerThread> threads = new LinkedList<>();
        int threadCount = 8;

        for (int threadId = 1; threadId <= threadCount; threadId++) {
            ImageAnalyzerThread thread = new ImageAnalyzerThread(threadId, taskQueue, outputList);
            threads.add(thread);
            thread.start();
        }

        for (ImageAnalyzerThread thread : threads) {
            thread.join();
        }

        System.out.println("Analysis complete!");

        String csvContents = outputList.stream().parallel().map(AnalyzerMain::toCsv).collect(Collectors.joining("\n"));
        String csv = "file,TP,TN,FP,FN\n" + csvContents;
        Files.write(Paths.get(args[SUMMARY_FILE]), csv.getBytes());

        for (ImageAnalysis analysis : outputList) {
            Path outputDirectory = Paths.get(args[COLORED_ANALYSIS_OUTPUT_DIRECTORY]);
            outputDirectory.toFile().mkdirs();
            Path outputFile = Paths.get(outputDirectory.toString(), analysis.getComputerGeneratedImage().getFileName().toString());
            ImageIO.write(analysis.getVisualResult(), "png", outputFile.toFile());
        }

        System.out.println("Analysis files saved");
    }

    private static String toCsv(ImageAnalysis analysis) {
        return String.format("%s,%s,%s,%s,%s", analysis.getComputerGeneratedImage().getFileName(),
                analysis.getTruePositives(), analysis.getTrueNegatives(), analysis.getFalsePositives(),
                analysis.getFalseNegatives());
    }

    private static ConcurrentLinkedQueue<ImageAnalysisTask> generateTaskQueue(Path computerDirectory, Path evaluatorDirectory) throws IOException {
        ConcurrentLinkedQueue<ImageAnalysisTask> taskQueue = new ConcurrentLinkedQueue<>();
        Files.list(computerDirectory).forEach(computerImage -> {
            Path humanImage = evaluatorDirectory.resolve(computerImage.getFileName());
            taskQueue.add(new ImageAnalysisTask(computerImage, humanImage));
        });
        return taskQueue;
    }
}
