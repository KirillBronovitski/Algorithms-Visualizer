package visualizer.logic.workers;

import visualizer.gui.Graph;
import visualizer.logic.GraphManager;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public class EdgeEraser extends SwingWorker<Void, String> {

    private final GraphManager graphManager;
    private final Consumer<String> edgeErasureConsumer;

    public EdgeEraser(GraphManager graphManager, Consumer<String> edgeErasureConsumer) {
        this.graphManager = graphManager;
        this.edgeErasureConsumer = edgeErasureConsumer;
    }

    @Override
    protected Void doInBackground() throws InterruptedException {
        while (!isCancelled()) {
            List<String> edgesToReset = graphManager.awaitSelectedEdgeNamesForRemoval();
            for (String edgeName: edgesToReset) {
                publish(edgeName);
            }
            if (graphManager.getGraphMode() != Graph.Mode.REMOVE_EDGE || graphManager.getEdgeCount() == 0) {
                cancel(false);
                break;
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> labels) {
        for (String labelText: labels) {
            edgeErasureConsumer.accept(labelText);
        }
    }

}
