package visualizer.logic.workers;

import visualizer.gui.Edge;
import visualizer.gui.Vertex;
import visualizer.logic.GraphManager;

import java.util.*;
import java.util.function.Consumer;

public class BreadthFirstTraverser extends Traverser {

    public BreadthFirstTraverser(Consumer<String> duringProcess, GraphManager graphManager) {
        super(duringProcess, graphManager);
    }

    @Override
    protected String doInBackground() throws Exception {
        List<Edge> visitedEdges = new ArrayList<>();
        Deque<Edge> traversingOrderEdges = new ArrayDeque<>();
        Vertex currentVertex = graphManager.getVertex(graphManager.awaitSelectedVertexName(duringProcess));
        if (isCancelled()) {
            return "";
        }
        StringBuilder searchPathString = new StringBuilder("BFS : " + currentVertex.getLabelText());
        do {
            if (!traversingOrderEdges.isEmpty()) {
                Edge chosenEdge = traversingOrderEdges.pollFirst();
                currentVertex = chosenEdge.getEndVertex();
                searchPathString.append(" -> ").append(chosenEdge.getEndVertex().getLabelText());
                publish(chosenEdge.getName());
            }
            Thread.sleep(500);
            List<Edge> adjacentEdges = currentVertex.getAdjacentEdges();
            adjacentEdges.sort(Comparator.comparingInt(Edge::getWeight));
            for (Edge edge : adjacentEdges) {
                if (edge.getEndVertex() != currentVertex && !visitedEdges.contains(edge)) {
                    traversingOrderEdges.offerLast(edge);
                    visitedEdges.add(edge);
                    Edge edgeReverse = graphManager.getEdge(String.format("Edge <%s -> %s>", edge.getEndVertex().getLabelText(), edge.getStartVertex().getLabelText()));
                    visitedEdges.add(edgeReverse);
                }
            }
        } while (!traversingOrderEdges.isEmpty());
        return searchPathString.toString();
    }
}