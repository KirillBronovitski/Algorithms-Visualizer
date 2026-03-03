package visualizer.logic.workers;

import visualizer.gui.Graph;
import visualizer.logic.GraphManager;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public class EdgeCreator extends SwingWorker<Void, String[]> {

    private final GraphManager graphManager;
    private final Consumer<String[]> onFoundPair;

    public EdgeCreator(GraphManager graphManager, Consumer<String[]> onFoundPair) {
        this.graphManager = graphManager;
        this.onFoundPair = onFoundPair;
    }

    @Override
    protected Void doInBackground() {
        String[] pair;
        while (!isCancelled()) {
            try {
                pair = graphManager.waitForPair();
                if (pair != null) {
                    publish(pair);
                }
                if (graphManager.getGraphMode() != Graph.Mode.ADD_EDGE) {
                    cancel(false);
                }
            } catch (InterruptedException ignored) {}
        }
        return null;
    }

    @Override
    protected void process(List<String[]> pairs) {
        for (String[] pair : pairs) {
            onFoundPair.accept(pair);
        }
    }

}
