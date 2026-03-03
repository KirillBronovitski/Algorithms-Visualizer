package visualizer.logic.workers;

import visualizer.gui.Edge;
import visualizer.gui.Vertex;
import visualizer.logic.GraphManager;

import java.util.*;
import java.util.function.Consumer;

public class PrimTraverser extends Traverser {

    public PrimTraverser(Consumer<String> duringProcess, GraphManager graphManager) {
        super(duringProcess, graphManager);
    }

    @Override
    protected String doInBackground() throws Exception {
        Vertex currentVertex = graphManager.getVertex(graphManager.awaitSelectedVertexName(duringProcess));
        List<String> visitedVertexes = new ArrayList<>(List.of(currentVertex.getName()));
        List<Edge> edgesToAnalyze = new ArrayList<>();
        StringBuilder output = new StringBuilder();
        while (!(new HashSet<>(visitedVertexes).containsAll(graphManager.getVertexes().keySet()))) {
            for (Edge edge: currentVertex.getAdjacentEdges()) {
                if (!edgesToAnalyze.contains(edge)) {
                    edgesToAnalyze.add(edge);
                }
            }
            int minimumWeight = Integer.MAX_VALUE;
            Edge chosenEdge = edgesToAnalyze.getFirst();
            for (Edge edge: edgesToAnalyze) {
                if (edge.getWeight() <= minimumWeight && !visitedVertexes.contains(edge.getEndVertex().getName())) {
                    minimumWeight = edge.getWeight();
                    chosenEdge = edge;
                }
            }
            visitedVertexes.add(chosenEdge.getEndVertex().getName());
            output.append(String.format("%s=%s, ", chosenEdge.getEndVertex().getLabelText(), chosenEdge.getStartVertex().getLabelText()));
            publish(chosenEdge.getName());
            Thread.sleep(500);
            edgesToAnalyze.remove(chosenEdge);
            currentVertex = chosenEdge.getEndVertex();
        }
        output.delete(output.length() - 2, output.length());
        return output.toString();
    }
}
