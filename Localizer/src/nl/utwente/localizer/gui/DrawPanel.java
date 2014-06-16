package nl.utwente.localizer.gui;

import nl.utwente.localizer.Tools.AreaMapper;
import nl.utwente.localizer.datatypes.Area;
import nl.utwente.localizer.datatypes.Node;
import nl.utwente.localizer.datatypes.Point;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Joris on 05/06/2014.
 */
@Deprecated
public class DrawPanel extends JPanel {

    private AreaMapper areaMapper;
    private ArrayList<Node> nodes;
    private ArrayList<Node> specialnodes;
    private ArrayList<Point> points;

    public DrawPanel() {
        setSize(600, 600);
        nodes = new ArrayList<>();
        points = new ArrayList<>();
        specialnodes = new ArrayList<>();
    }

    public void setAreaMapper(Area area) {
        Area thisArea = new Area(new Point(0,0),new Point(600,400));
        areaMapper = new AreaMapper(area,thisArea,0.8);
    }

    public void drawNode(Node node) {
        Node newNode = areaMapper.mapNode(node);
        nodes.add(newNode);
    }

    public void drawPoint(Point point) {
        Point newPoint = areaMapper.mapPoint(point);
        points.add(newPoint);
    }

    @Override
    public void paintComponent(Graphics g) {
        for(Node n : nodes) {
            g.fillOval((int) n.x, (int) n.y, 10, 10);
            g.drawOval((int) (n.x - n.r), (int) (n.y - n.r), (int) n.r * 2, (int) n.r * 2);
        }

        for(Point p : points) {
            g.fillOval((int)p.x,(int)p.y,10,10);
        }
    }
}
