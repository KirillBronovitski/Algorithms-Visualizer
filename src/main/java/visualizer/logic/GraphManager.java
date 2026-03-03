package visualizer.logic;

import visualizer.gui.Edge;
import visualizer.gui.Graph;
import visualizer.gui.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GraphManager {

    private final Map<String, Vertex> vertexes = new HashMap<>();
    private final Map<String, Edge> edges = new HashMap<>();

    private final List<Vertex> selectedVertexes = new ArrayList<>();
    private final List<Edge> selectedEdges = new ArrayList<>();

    private volatile Graph.Mode graphMode;

    private final Object lock = new Object();

    public String awaitSelectedVertexName(Consumer<String> duringWait) throws InterruptedException {
        synchronized (lock) {
            while (selectedVertexes.size() != 1 || !selectedEdges.isEmpty()) {
                lock.wait();
            }
            duringWait.accept("Please wait...");
            return selectedVertexes.getFirst().getName();
        }
    }

    public String[] waitForPair() throws InterruptedException {
        synchronized (lock) {
            while (graphMode == Graph.Mode.ADD_EDGE && selectedVertexes.size() < 2) {
                lock.wait();
            }

            if (graphMode == Graph.Mode.ADD_EDGE && selectedVertexes.size() >= 2) {
                String[] pair = new String[]{selectedVertexes.getFirst().getName(), selectedVertexes.get(1).getName()};
                resetSelectedVertexes();
                return pair;
            }
            return null;
        }
    }

    public Map<String, Vertex> getVertexes() {
        synchronized (lock) {
            return vertexes;
        }
    }

    public Vertex getVertex(String vertexName) {
        synchronized (lock) {
            return vertexes.get(vertexName);
        }
    }

    public Edge getEdge(String edgeName) {
        synchronized (lock) {
            return edges.get(edgeName);
        }
    }

    public void saveVertex(Vertex vertex) {
        synchronized (lock) {
            vertexes.put(vertex.getName(), vertex);
            lock.notifyAll();
        }
    }

    public void saveEdge(Edge edge) {
        synchronized (lock) {
            edges.put(edge.getName(), edge);
            lock.notifyAll();
        }
    }

    public void deleteVertex(String vertexName) {
        synchronized (lock) {
            vertexes.remove(vertexName);
        }
    }

    public void deleteEdge(String edgeName) {
        synchronized (lock) {
            edges.remove(edgeName);
        }
    }

    public void addVertexToSelected(Vertex vertex) {
        synchronized (lock) {
            if (!selectedVertexes.contains(vertex)) {
                selectedVertexes.add(vertex);
                lock.notifyAll();
            }
        }
    }

    public void addEdgeToSelected(Edge edge) {
        synchronized (lock) {
            if (!selectedEdges.contains(edge)) {
                selectedEdges.add(edge);
                lock.notifyAll();
            }
        }
    }

    public void removeVertexFromSelected(Vertex vertex) {
        synchronized (lock) {
            selectedVertexes.remove(vertex);
            lock.notifyAll();
        }
    }

    public void removeEdgeFromSelected(Edge edge) {
        synchronized (lock) {
            selectedEdges.remove(edge);
            lock.notifyAll();
        }
    }

    public boolean vertexExists(String vertexLabelText) {
        synchronized (lock) {
            return vertexes.containsKey("Vertex " + vertexLabelText);
        }
    }

    public boolean edgeExists(String edgeName) {
        synchronized (lock) {
            return edges.containsKey(edgeName);
        }
    }

    public void resetSelectedVertexes() {
        synchronized (lock) {
            while (!selectedVertexes.isEmpty()) {
                selectedVertexes.getFirst().setSelected(false);
            }
        }
    }

    public void resetSelectedEdges() {
        synchronized (lock) {
            while (!selectedEdges.isEmpty()) {
                selectedEdges.getFirst().setSelected(false);
            }
        }
    }

    public List<String> awaitSelectedVertexNamesForRemoval() throws InterruptedException {
        synchronized (lock) {
            while (graphMode == Graph.Mode.REMOVE_VERTEX && selectedVertexes.isEmpty() && !vertexes.isEmpty()) {
                lock.wait();
            }
            if (graphMode == Graph.Mode.REMOVE_VERTEX && !selectedVertexes.isEmpty()) {
                List<String> vertexesToRemove = getSelectedVertexNames();
                resetSelectedVertexes();
                for (String vertexName : vertexesToRemove) {
                    selectAdjacentEdges(vertexes.get(vertexName));
                }
                return vertexesToRemove;
            }
            return List.of();
        }
    }

    public List<String> awaitSelectedEdgeNamesForRemoval() throws InterruptedException {
        synchronized (lock) {
            while (graphMode == Graph.Mode.REMOVE_EDGE && selectedEdges.isEmpty() && !edges.isEmpty()) {
                lock.wait();
            }
            if (graphMode == Graph.Mode.REMOVE_EDGE && !selectedEdges.isEmpty()) {
                List<String> edgesToRemove = getSelectedEdgeNames();
                resetSelectedEdges();
                return edgesToRemove;
            }
            return List.of();
        }
    }

    public List<String> getSelectedEdgeNames() {
        synchronized (lock) {
            List<String> selectedEdgeNames = new ArrayList<>();
            for (Edge edge : selectedEdges) {
                selectedEdgeNames.add(edge.getName());
            }
            return selectedEdgeNames;
        }
    }

    public List<String> getSelectedVertexNames() {
        synchronized (lock) {
            List<String> selectedVertexNames = new ArrayList<>();
            for (Vertex vertex : selectedVertexes) {
                selectedVertexNames.add(vertex.getName());
            }
            return selectedVertexNames;
        }
    }

    public void selectAdjacentEdges(Vertex vertex) {
        synchronized (lock) {
            for (Edge edge : vertex.getAdjacentEdges()) {
                edge.setSelected(true);
            }
        }
    }

    public int getVertexCount() {
        return vertexes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public Graph.Mode getGraphMode() {
        return this.graphMode;
    }

    public void setGraphMode(Graph.Mode graphMode) {
        synchronized (lock) {
            this.graphMode = graphMode;
            lock.notifyAll();
        }
    }

    public void resetGraph() {
        vertexes.clear();
        edges.clear();
        selectedVertexes.clear();
        selectedEdges.clear();
        graphMode = Graph.Mode.ADD_VERTEX;
    }

}