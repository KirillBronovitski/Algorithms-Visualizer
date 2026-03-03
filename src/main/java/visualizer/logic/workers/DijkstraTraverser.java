package visualizer.logic.workers;

import visualizer.gui.Edge;
import visualizer.gui.Vertex;
import visualizer.logic.GraphManager;

import java.util.*;
import java.util.function.Consumer;

public class DijkstraTraverser extends Traverser {

    static class DijkstraVertex {

        Vertex vertex;
        List<String> pathToVertex;
        int distanceToVertex;

        DijkstraVertex(Vertex vertex) {
            this.vertex = vertex;
            this.pathToVertex = new ArrayList<>(List.of(vertex.getName()));
            this.distanceToVertex = Integer.MAX_VALUE;
        }
    }

    private final Map<String, DijkstraVertex> vertexRecords = new HashMap<>();
    private final List<DijkstraVertex> visitedVertexes = new ArrayList<>();

    public DijkstraTraverser(Consumer<String> duringProcess, GraphManager graphManager) {
        super(duringProcess, graphManager);
        for (String vertexName: graphManager.getVertexes().keySet()) {
            vertexRecords.put(vertexName, new DijkstraVertex(graphManager.getVertex(vertexName)));
        }
    }

    @Override
    protected String doInBackground() throws Exception {
        Queue<DijkstraVertex> vertexQueue = new PriorityQueue<>(Comparator.comparingInt(vertex -> vertex.distanceToVertex));
        DijkstraVertex currentVertex = vertexRecords.get(graphManager.awaitSelectedVertexName(duringProcess));
        if (isCancelled()) {
            return "";
        }
        currentVertex.distanceToVertex = 0;
        currentVertex.pathToVertex.add(currentVertex.vertex.getName());
        vertexQueue.offer(currentVertex);
        while (!vertexQueue.isEmpty()) {
            currentVertex = vertexQueue.poll();
            for (Edge edge : currentVertex.vertex.getAdjacentEdges()) {
                DijkstraVertex nextVertex = vertexRecords.get(edge.getEndVertex().getName());
                if (nextVertex != currentVertex && !visitedVertexes.contains(nextVertex)) {
                    if (nextVertex.distanceToVertex > currentVertex.distanceToVertex + edge.getWeight()) {
                        nextVertex.distanceToVertex = currentVertex.distanceToVertex + edge.getWeight();
                        nextVertex.pathToVertex.clear();
                        nextVertex.pathToVertex.addAll(currentVertex.pathToVertex);
                        nextVertex.pathToVertex.add(nextVertex.vertex.getName());
                    }
                    vertexQueue.offer(nextVertex);
                }
            }
            visitedVertexes.add(currentVertex);
        }
        StringBuilder vertexDistances = new StringBuilder();
        for (String vertexName: vertexRecords.keySet()) {
            DijkstraVertex vertex = vertexRecords.get(vertexName);
            vertexDistances.append(String.format("%s=%d, ", vertex.vertex.getLabelText(), vertex.distanceToVertex));
        }
        vertexDistances.delete(vertexDistances.length() - 2, vertexDistances.length());
        return vertexDistances.toString();
    }

}
