package ca.camauser.imageanalysis.analysis;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

public class ImageAnalyzerThread extends Thread {

    private final int id;
    private final Queue<ImageAnalysisTask> taskQueue;
    private final List<ImageAnalysis> outputList;

    public ImageAnalyzerThread(int id, Queue<ImageAnalysisTask> taskQueue, List<ImageAnalysis> outputList) {
        this.id = id;
        this.taskQueue = taskQueue;
        this.outputList = outputList;
    }


    @Override
    public void run() {
        ImageAnalysisTask task = taskQueue.poll();
        while (task != null) {
            try {
                ImageAnalysis result = new ImageAnalyzer(task.getComputerImage(), task.getHumanImage()).analyze();
                outputList.add(result);
                System.out.printf("Thread %d finished %s%n", id, task.getComputerImage().getFileName());
            } catch (IOException e) {
                System.out.printf("Thread %d FAILED on %s%n", id, task.getComputerImage().getFileName());
                e.printStackTrace();
            }
            task = taskQueue.poll();
        }
    }
}
