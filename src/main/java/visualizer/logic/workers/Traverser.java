package visualizer.logic.workers;

import visualizer.gui.Edge;
import visualizer.logic.GraphManager;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public abstract class Traverser extends SwingWorker<String, String> {

    protected final Consumer<String> duringProcess;

    protected final GraphManager graphManager;

    public Traverser(Consumer<String> duringProcess, GraphManager graphManager) {
        this.graphManager = graphManager;
        this.duringProcess = duringProcess;
    }

    @Override
    protected void process(List<String> elementNames) {
        for (String elementName: elementNames) {
            Edge edge = graphManager.getEdge(elementName);
            edge.setSelected(true);
            edge.getEndVertex().setSelected(true);
        }
    }
}
