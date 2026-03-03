package visualizer.logic.workers;

import visualizer.gui.Graph;
import visualizer.logic.GraphManager;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public class VertexEraser extends SwingWorker<Void, String> {

    private final GraphManager graphManager;

    private final Consumer<String> vertexErasureConsumer;

    public VertexEraser(GraphManager graphManager, Consumer<String> vertexErasureConsumer) {
        this.graphManager = graphManager;
        this.vertexErasureConsumer = vertexErasureConsumer;
    }

    @Override
    protected Void doInBackground() throws InterruptedException {
        while (!isCancelled()) {
            List<String> vertexesToReset = graphManager.awaitSelectedVertexNamesForRemoval();
            for (String vertexName: vertexesToReset) {
                publish(vertexName);
            }
            if (graphManager.getGraphMode() != Graph.Mode.REMOVE_VERTEX || graphManager.getVertexCount() == 0) {
                cancel(false);
                break;
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> labels) {
        for (String labelText: labels) {
            vertexErasureConsumer.accept(labelText);
        }
    }


}
