package visualizer.gui;

import visualizer.logic.AlgorithmsManager;
import visualizer.logic.GraphManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final int frameWidth;
    private final int frameHeight;
    private final int screenWidth;
    private final int screenHeight;

    private final JLabel algorithmResultsInfo;
    private Graph graph;

    private final GraphManager graphManager;
    private final AlgorithmsManager algorithmsManager;

    public MainFrame() {
        super("Graph-Algorithms Visualizer");
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = screen.width;
        this.screenHeight = screen.height;
        this.frameWidth = 800;
        this.frameHeight = 600;
        this.algorithmResultsInfo = getAlgorithmResultsInfoLabel();
        this.graphManager = new GraphManager();
        this.algorithmsManager = new AlgorithmsManager(graphManager);
        initFrame();
        initGraph();
        initMenuBar();
        setVisible(true);
    }

    private void initFrame() {
        setName("Graph-Algorithms Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(screenWidth / 2 - frameWidth / 2, screenHeight / 2 - frameHeight / 2, frameWidth, frameHeight);
        getContentPane().setBackground(AppColors.LOWER_BACKGROUND_COLOR);
        setLayout(null);
    }

    private void initGraph() {
        graph = new Graph(graphManager, frameWidth, frameHeight);
        add(graph);
        add(algorithmResultsInfo);

        SwingUtilities.invokeLater(() -> {
            int contentHeight = getContentPane().getHeight();
            algorithmResultsInfo.setBounds(0, contentHeight - 20, frameWidth, 20);
        });
    }
    
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setName("MenuBar");
        menuBar.setBackground(AppColors.UPPER_BACKGROUND_COLOR);
        menuBar.setBorder(BorderFactory.createLineBorder(AppColors.CAPTION_COLOR));

        menuBar.add(getFileMenu());
        menuBar.add(getModeMenu());
        menuBar.add(getAlgorithmsMenu());

        this.setJMenuBar(menuBar);
    }

    private JMenu getFileMenu() {
        JMenu menu = new JMenu("File");
        menu.setName("File");
        menu.setForeground(AppColors.DEFAULT_FONT_COLOR);

        JMenuItem newOption = new JMenuItem("New");
        newOption.setName("New");
        newOption.addActionListener(_ -> {
            this.remove(graph);
            graphManager.resetGraph();
            algorithmsManager.resetTraverser();
            initGraph();
            repaint();
        });

        JMenuItem exitOption = new JMenuItem("Exit");
        exitOption.setName("Exit");
        exitOption.addActionListener(_ -> {
            graph.setMode(Graph.Mode.NONE);
            cleanup();
            this.dispose();
        });

        menu.add(newOption);
        menu.add(exitOption);

        return menu;
    }

    private JMenu getModeMenu() {
        JMenu menu = new JMenu("Mode");
        menu.setName("Mode");
        menu.setForeground(AppColors.DEFAULT_FONT_COLOR);

        JMenuItem addVertexOption = new JMenuItem("Add a Vertex");
        addVertexOption.setName("Add a Vertex");
        addVertexOption.addActionListener(_ -> graph.setMode(Graph.Mode.ADD_VERTEX));

        JMenuItem addEdgeOption = new JMenuItem("Add an Edge");
        addEdgeOption.setName("Add an Edge");
        addEdgeOption.addActionListener(_ -> {
            if (graph.getMode() != Graph.Mode.ADD_EDGE) {
                graph.setMode(Graph.Mode.ADD_EDGE);
                graph.addEdge();
            }
        });

        JMenuItem removeVertexOption = new JMenuItem("Remove a Vertex");
        removeVertexOption.setName("Remove a Vertex");
        removeVertexOption.addActionListener(_ -> {
            if (graph.getMode() != Graph.Mode.REMOVE_VERTEX) {
                graph.setMode(Graph.Mode.REMOVE_VERTEX);
                graph.removeVertexes();
            }
        });

        JMenuItem removeEdgeOption = new JMenuItem("Remove an Edge");
        removeEdgeOption.setName("Remove an Edge");
        removeEdgeOption.addActionListener(_ -> {
            if (graph.getMode() != Graph.Mode.REMOVE_EDGE) {
                graph.setMode(Graph.Mode.REMOVE_EDGE);
                graph.removeEdges();
            }
        });

        JMenuItem noneOption = new JMenuItem("None");
        noneOption.setName("None");
        noneOption.addActionListener(_ -> graph.setMode(Graph.Mode.NONE));

        menu.add(addVertexOption);
        menu.add(addEdgeOption);
        menu.add(removeVertexOption);
        menu.add(removeEdgeOption);
        menu.add(noneOption);

        return menu;
    }

    private JMenu getAlgorithmsMenu() {

        JMenu menu = new JMenu("Algorithms");
        menu.setName("Algorithms");
        menu.setForeground(AppColors.DEFAULT_FONT_COLOR);

        JMenuItem depthFirstOption = new JMenuItem("Depth-First Search");
        depthFirstOption.setName("Depth-First Search");
        depthFirstOption.addActionListener(_ -> {
            graph.setMode(Graph.Mode.NONE);
            algorithmsManager.resetTraverser();
            algorithmsManager.runDepthFirstTraversal(algorithmResultsInfo::setText, algorithmResultsInfo::setText, algorithmResultsInfo::setText);
        });

        JMenuItem breadthFirstOption = new JMenuItem("Breadth-First Search");
        breadthFirstOption.setName("Breadth-First Search");
        breadthFirstOption.addActionListener(_ -> {
            graph.setMode(Graph.Mode.NONE);
            algorithmsManager.resetTraverser();
            algorithmsManager.runBreadthFirstTraversal(algorithmResultsInfo::setText, algorithmResultsInfo::setText, algorithmResultsInfo::setText);
        });

        JMenuItem dijkstraOption = new JMenuItem("Dijkstra's Algorithm");
        dijkstraOption.setName("Dijkstra's Algorithm");
        dijkstraOption.addActionListener(_ -> {
            graph.setMode(Graph.Mode.NONE);
            algorithmsManager.resetTraverser();
            algorithmsManager.runDijkstraAlgorithm(algorithmResultsInfo::setText, algorithmResultsInfo::setText, algorithmResultsInfo::setText);
        });

        JMenuItem primOption = new JMenuItem("Prim's Algorithm");
        primOption.setName("Prim's Algorithm");
        primOption.addActionListener(_ -> {
            graph.setMode(Graph.Mode.NONE);
            algorithmsManager.resetTraverser();
            algorithmsManager.runPrimAlgorithm(algorithmResultsInfo::setText, algorithmResultsInfo::setText, algorithmResultsInfo::setText);
        });

        menu.add(depthFirstOption);
        menu.add(breadthFirstOption);
        menu.add(dijkstraOption);
        menu.add(primOption);

        return menu;
    }

    private JLabel getAlgorithmResultsInfoLabel() {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBackground(AppColors.UPPER_BACKGROUND_COLOR);
        label.setOpaque(true);
        label.setForeground(AppColors.DEFAULT_FONT_COLOR);
        label.setBorder(BorderFactory.createLineBorder(AppColors.CAPTION_COLOR));
        return label;
    }

    public void cleanup() {
        graphManager.setGraphMode(Graph.Mode.NONE);
        algorithmsManager.cleanup();
        graph.cleanup();
    }


}