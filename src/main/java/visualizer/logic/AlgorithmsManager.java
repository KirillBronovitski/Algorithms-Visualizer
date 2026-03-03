package visualizer.logic;

import visualizer.logic.workers.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AlgorithmsManager {

    private final GraphManager graphManager;
    private final ExecutorService threadPool;
    private Traverser traverser;

    public AlgorithmsManager(GraphManager graphManager) {
        this.graphManager = graphManager;
        this.threadPool = Executors.newSingleThreadExecutor();
    }

    public void runDepthFirstTraversal(Consumer<String> onStart, Consumer<String> duringProcess, Consumer<String> onComplete) {
        traverser = new DepthFirstTraverser(duringProcess, graphManager);
        runSearch(onStart, onComplete);
    }

    public void runBreadthFirstTraversal(Consumer<String> onStart, Consumer<String> duringProcess, Consumer<String> onComplete) {
        traverser = new BreadthFirstTraverser(duringProcess, graphManager);
        runSearch(onStart, onComplete);
    }

    public void runDijkstraAlgorithm(Consumer<String> onStart, Consumer<String> duringProcess, Consumer<String> onComplete) {
        traverser = new DijkstraTraverser(duringProcess, graphManager);
        runSearch(onStart, onComplete);
    }

    public void runPrimAlgorithm(Consumer<String> onStart, Consumer<String> duringProcess, Consumer<String> onComplete) {
        traverser = new PrimTraverser(duringProcess, graphManager);
        runSearch(onStart, onComplete);
    }
    private void runSearch(Consumer<String> onStart, Consumer<String> onComplete) {
        CompletableFuture<String> searcherVertexSequence = CompletableFuture.supplyAsync(() -> {
            try {
                onStart.accept("Please choose a starting vertex");
                traverser.execute();
                return traverser.get();
            } catch (ExecutionException | InterruptedException e) {
                return "Error during graph traversing";
            }
        }, threadPool);
        searcherVertexSequence.whenComplete((sequence, _) -> onComplete.accept(sequence));
    }

    public void resetTraverser() {
        if (traverser != null) {
            traverser.cancel(false);
        }
    }

    public void cleanup() {
        resetTraverser();
        threadPool.shutdown();
    }

}
