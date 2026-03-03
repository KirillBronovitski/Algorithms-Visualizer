package visualizer.gui;

import visualizer.logic.GraphManager;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import static visualizer.gui.AppColors.*;

public class Vertex extends JPanel implements MouseInputListener {

    public static final int VERTEX_DIAMETER = 50;

    private final GraphManager graphManager;
    private final List<Edge> adjacentEdges = new ArrayList<>();
    private final JLabel label;

    private boolean selected = false;
    private boolean erased = false;

    public Vertex(GraphManager graphManager, String labelText, int x, int y) {
        this.graphManager = graphManager;
        setBounds(x, y, VERTEX_DIAMETER , VERTEX_DIAMETER);
        setLayout(new BorderLayout());
        setOpaque(false);
        label = new JLabel(labelText, SwingConstants.CENTER);
        label.setName("VertexLabel " + labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setOpaque(false);
        add(label, BorderLayout.CENTER);
        setName("Vertex " + labelText);
        this.setOpaque(false);
        addMouseListener(this);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (erased) {
            graphics.clearRect(0, 0, VERTEX_DIAMETER, VERTEX_DIAMETER);
        } else {
            graphics.setColor(LOWER_BACKGROUND_COLOR);
            graphics.clearRect(0, 0, VERTEX_DIAMETER, VERTEX_DIAMETER);
            graphics.fillRect(0, 0, VERTEX_DIAMETER, VERTEX_DIAMETER);
            graphics.setColor(ELEMENT_COLOR);
            graphics.drawOval(0, 0, VERTEX_DIAMETER, VERTEX_DIAMETER);
            graphics.fillOval(0, 0, VERTEX_DIAMETER, VERTEX_DIAMETER);
            graphics.setColor(selected ? SELECTED_COLOR : DETAIL_COLOR);
            ((Graphics2D) graphics).setStroke(new BasicStroke(5));
            graphics.drawOval(10, 10, 30, 30);
        }
    }

    public void addAdjacentEdge(Edge edge) {
        adjacentEdges.add(edge);
    }

    public List<Edge> getAdjacentEdges() {
        return this.adjacentEdges;
    }

    public String getLabelText() {
        return label.getText();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            graphManager.addVertexToSelected(this);
        } else {
            graphManager.removeVertexFromSelected(this);
        }
        repaint();
    }

    public void setErased(boolean erased) {
        this.erased = erased;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        setSelected(!selected);
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
