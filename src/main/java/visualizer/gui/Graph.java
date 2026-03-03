package visualizer.gui;

import visualizer.logic.*;
import visualizer.logic.workers.EdgeCreator;
import visualizer.logic.workers.EdgeEraser;
import visualizer.logic.workers.VertexCreator;
import visualizer.logic.workers.VertexEraser;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;


public class Graph extends JPanel implements MouseInputListener {

    private final GraphManager graphManager;

    private final JLabel modeInfo;

    private final ExecutorService threadPool;

    public enum Mode {
        ADD_VERTEX("Add a Vertex"),
        ADD_EDGE("Add an Edge"),
        REMOVE_VERTEX("Remove a Vertex"),
        REMOVE_EDGE("Remove an Edge"),
        NONE("None");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    private Mode mode = Mode.ADD_VERTEX;

    public Graph(GraphManager graphManager, int width, int height) {
        setName("Graph");
        setLayout(null);
        setBounds(0, 0, width, height);
        setBackground(AppColors.LOWER_BACKGROUND_COLOR);
        addMouseListener(this);
        this.graphManager = graphManager;
        modeInfo = new JLabel("Current Mode -> " + mode.getName(), SwingConstants.RIGHT);
        modeInfo.setName("Mode");
        modeInfo.setBounds(0, 0, width, 30);
        modeInfo.setForeground(Color.WHITE);
        modeInfo.setBackground(AppColors.LOWER_BACKGROUND_COLOR);
        threadPool = Executors.newSingleThreadExecutor();
        add(modeInfo);
    }

    private void addVertex(int x, int y) {
        VertexCreator vertexCreator = new VertexCreator(x, y, this::vertexCreationProcedure);
        threadPool.submit(vertexCreator);
    }

    public void removeEdges() {
        EdgeEraser edgeEraser = new EdgeEraser(graphManager, this::edgeErasureProcedure);
        threadPool.submit(edgeEraser);
    }

    public void removeVertexes() {
        VertexEraser vertexEraser = new VertexEraser(graphManager, this::vertexErasureProcedure);
        threadPool.submit(vertexEraser);
    }

    private void vertexCreationProcedure(int x, int y) {
        while (true) {
            String vertexLabel = JOptionPane.showInputDialog(null, "Enter the Vertex ID (Should be 1 char)", "Vertex", JOptionPane.PLAIN_MESSAGE);
            if (vertexLabel == null) {
                break;
            }
            if (vertexLabel.trim().length() == 1 && !graphManager.vertexExists(vertexLabel) && !vertexLabel.contains("-") && !vertexLabel.contains(">")) {
                Vertex vertex = new Vertex(graphManager, vertexLabel, x - 25, y - 25);
                this.add(vertex);
                graphManager.saveVertex(vertex);
                revalidate();
                repaint();
                break;
            }
        }
    }

    private void edgeErasureProcedure(String edgeName) {
        Edge removedEdge = graphManager.getEdge(edgeName);
        removedEdge.setErased(true);
        removedEdge.getStartVertex().getAdjacentEdges().remove(removedEdge);
        removedEdge.getEndVertex().getAdjacentEdges().remove(removedEdge);
        this.remove(removedEdge);
        graphManager.removeEdgeFromSelected(graphManager.getEdge(edgeName)); //TODO this one looks a bit messy, try to do something with method removeEdgeFromSelected
        graphManager.deleteEdge(edgeName);
        this.revalidate();
        this.repaint();
    }

    private void vertexErasureProcedure(String vertexName) {
        List<String> selectedEdgeNames = graphManager.getSelectedEdgeNames();
        for (String edgeName: selectedEdgeNames) {
            edgeErasureProcedure(edgeName);
        }
        Vertex removedVertex = graphManager.getVertex(vertexName);
        List<Edge> edgesToRemove = new ArrayList<>();
        List<Vertex> vertexesToUpdate = new ArrayList<>();
        for (Edge edge: removedVertex.getAdjacentEdges()) {
            Vertex adjacentVertex1 = edge.getStartVertex();
            Vertex adjacentVertex2 = edge.getEndVertex();
            vertexesToUpdate.add(adjacentVertex1);
            vertexesToUpdate.add(adjacentVertex2);
            edgesToRemove.add(edge);
        }
        for (Vertex vertex: vertexesToUpdate) {
            vertex.getAdjacentEdges().removeAll(edgesToRemove);
        }
        removedVertex.setErased(true);
        this.remove(removedVertex);
        graphManager.deleteVertex(vertexName);
        this.revalidate();
        this.repaint();
    }

    public void addEdge() {
        EdgeCreator edgeCreator = new EdgeCreator(graphManager, (pair) -> {
            Vertex vertex1 = graphManager.getVertex(pair[0]);
            Vertex vertex2 = graphManager.getVertex(pair[1]);
            int weight;
            while (true) {
                String weightString = JOptionPane.showInputDialog(null, "Enter Weight", "Input", JOptionPane.PLAIN_MESSAGE);
                if (weightString == null) {
                    break;
                }
                if (!weightString.matches("-?[0-9]+")) {
                    continue;
                }
                weight = Integer.parseInt(weightString);
                String edgeName = String.format("Edge <%s -> %s>", vertex1.getLabelText(), vertex2.getLabelText());
                if (graphManager.edgeExists(edgeName) && weight != graphManager.getEdge(edgeName).getWeight()) {
                    graphManager.getEdge(edgeName).setWeight(weight);
                    break;
                } else if (!graphManager.edgeExists(edgeName)) {
                    Edge edge = new Edge(graphManager, vertex1, vertex2, weight);
                    Edge edgeReverse = new Edge(graphManager, vertex2, vertex1, weight);//TODO delete auto reverse edge creation when directed edges will be implemented
                    this.add(edge);
                    this.add(edgeReverse);//TODO delete auto reverse edge creation when directed edges will be implemented
                    graphManager.saveEdge(edge);
                    graphManager.saveEdge(edgeReverse); //TODO delete auto reverse edge creation when directed edges will be implemented
                    this.setComponentZOrder(edge, 0);
                    this.setComponentZOrder(edgeReverse, 0);//TODO delete auto reverse edge creation when directed edges will be implemented
                    this.revalidate();
                    this.repaint();
                    break;
                }
            }
        });
        threadPool.submit(edgeCreator);
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        graphManager.setGraphMode(mode);
        modeInfo.setText("Current Mode -> " + mode.getName());
    }

    public void cleanup() {
        threadPool.shutdown();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (mode == Mode.ADD_VERTEX) {
            addVertex(mouseEvent.getX(), mouseEvent.getY());
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
