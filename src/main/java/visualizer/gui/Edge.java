package visualizer.gui;

import visualizer.logic.GraphManager;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

import static visualizer.gui.AppColors.*;

public class Edge extends JComponent implements MouseInputListener {

    private static final int PADDING = 8;

    private final GraphManager graphManager;
    private final Vertex startVertex;
    private final Vertex endVertex;
    private final JLabel edgeLabel;
    private final Point startPoint;
    private final Point endPoint;

    private int weight;
    private boolean erased = false;
    private boolean selected = false;

    public Edge(GraphManager graphManager, Vertex startVertex, Vertex endVertex, int weight) {
        setLayout(null);
        this.graphManager = graphManager;
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        startVertex.addAdjacentEdge(this);
        endVertex.addAdjacentEdge(this);
        int x1 = startVertex.getX() + Vertex.VERTEX_DIAMETER / 2;
        int y1 = startVertex.getY() + Vertex.VERTEX_DIAMETER / 2;
        int x2 = endVertex.getX() + Vertex.VERTEX_DIAMETER / 2;
        int y2 = endVertex.getY() + Vertex.VERTEX_DIAMETER / 2;
        double angle = Math.acos((x2 - x1) / Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
        int xOffsetFromCentre = (int) (((double) Vertex.VERTEX_DIAMETER / 2) * Math.cos(angle));
        int yOffestFromCentre = (int) (((double) Vertex.VERTEX_DIAMETER / 2) * Math.sin(angle));
        if (y2 - y1 >= 0) {
            yOffestFromCentre = -yOffestFromCentre;
        }
        this.startPoint = new Point(x1 + xOffsetFromCentre, y1 - yOffestFromCentre);
        this.endPoint = new Point(x2 - xOffsetFromCentre, y2 + yOffestFromCentre);
        this.weight = weight;
        int x = Math.min(startPoint.x, endPoint.x) - PADDING;
        int y = Math.min(startPoint.y, endPoint.y) - PADDING;
        int width = Math.abs(startPoint.x - endPoint.x) + 2 * PADDING;
        int height = Math.abs(startPoint.y - endPoint.y) + 2 * PADDING;
        if (width < 2 * PADDING) {
            width = 2 * PADDING;
        }
        if (height < 2 * PADDING) {
            height = 2 * PADDING;
        }
        this.setBounds(x, y, width, height);
        this.setOpaque(false);
        this.setName(String.format("Edge <%s -> %s>", startVertex.getLabelText(), endVertex.getLabelText()));
        this.addMouseListener(this);
        edgeLabel = new JLabel();
        edgeLabel.setText(String.valueOf(weight));
        edgeLabel.setName(String.format("EdgeLabel <%s -> %s>", startVertex.getLabelText(), endVertex.getLabelText()));
        edgeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        edgeLabel.setForeground(Color.WHITE);
        edgeLabel.setBounds(Math.abs(startPoint.x - endPoint.x) / 2, Math.abs(startPoint.y - endPoint.y) / 2, 30, 30);
        add(edgeLabel);
        //TODO currently because each edge has a label and each edge an its reverse share the center point, two labels are created at the same place.
        // The point where the label is placed will most likely have to be updated when weighted edges will be implemented
        this.setComponentZOrder(edgeLabel, 0);
    }

    public int getWeight() {
        return this.weight;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public Vertex getStartVertex() {
        return this.startVertex;
    }

    public Vertex getEndVertex() {
        return this.endVertex;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            graphManager.addEdgeToSelected(this);
        } else {
            graphManager.removeEdgeFromSelected(this);
        }
        //TODO This block of code selects the reverse edge. Connected to auto creation of reverse edge but may be useful in the final version.
        // Will probably be deleted/changed in future versions where each edge will be directed
        {
            Edge edgeReverse = graphManager.getEdge(String.format("Edge <%s -> %s>", endVertex.getLabelText(), startVertex.getLabelText()));
            if (edgeReverse != null) {
                edgeReverse.selected = selected;
                if (edgeReverse.isSelected()) {
                    graphManager.addEdgeToSelected(edgeReverse);
                } else {
                    graphManager.removeEdgeFromSelected(edgeReverse);
                }
            }
        }

        repaint();
    }

    public void setErased(boolean erased) {
        this.erased = erased;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (erased) {
            graphics.setColor(LOWER_BACKGROUND_COLOR);
        } else if (selected) {
            graphics.setColor(SELECTED_COLOR);
        } else {
            graphics.setColor(ELEMENT_COLOR);
        }
        ((Graphics2D) graphics).setStroke(new BasicStroke(PADDING));
        int x1 = startPoint.x - getX();
        int y1 = startPoint.y - getY();
        int x2 = endPoint.x - getX();
        int y2 = endPoint.y - getY();
        graphics.drawLine(x1, y1, x2, y2);
    }

    @Override
    public boolean contains(int x, int y) {
        Point clickPoint = new Point(x + getX(), y + getY());
        double distance = distanceToLineSegment(clickPoint, startPoint, endPoint);
        return distance <= PADDING;
    }

    private double distanceToLineSegment(Point p, Point start, Point end) {
        double lineLength = start.distance(end);
        if (lineLength == 0) return p.distance(start);
        double t = Math.max(0, Math.min(1,
                ((p.x - start.x) * (end.x - start.x) + (p.y - start.y) * (end.y - start.y))
                        / (lineLength * lineLength)));
        double projX = start.x + t * (end.x - start.x);
        double projY = start.y + t * (end.y - start.y);
        return Math.sqrt(Math.pow(p.x - projX, 2) + Math.pow(p.y - projY, 2));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        setSelected(!selected);
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    public void setWeight(int weight) {
        this.weight = weight;
        edgeLabel.setText(String.valueOf(weight));
        //TODO the code below is needed to update the weight of the reverse edge. Might need an update in the future
        Edge edgeReverse = graphManager.getEdge(String.format("Edge <%s -> %s>", endVertex.getLabelText(), startVertex.getLabelText()));
        if (edgeReverse != null) {
            edgeReverse.edgeLabel.setText(String.valueOf(weight));
        }
    }
}
