package nl.utwente.localizer.Tools;

import nl.utwente.localizer.datatypes.Area;
import nl.utwente.localizer.datatypes.Node;
import nl.utwente.localizer.datatypes.Point;

import java.util.ArrayList;

/**
 * Created by Joris on 05/06/2014.
 */
public class AreaMapper {

    private double scaleX = 0;
    private double scaleY = 0;
    private double moveX = 0;
    private double moveY = 0;
    private double zoomF = 1;
    private double moveZoomX = 0;
    private double moveZoomY = 0;

    public AreaMapper(Area orig, Area plane, double zoomFactor) {
        zoomF = zoomFactor;
        // scale factors
        scaleX = plane.width / orig.width * zoomF;
        scaleY = plane.height / orig.height * zoomF;

        // move factors
        moveX = plane.left_top.x - orig.left_top.x;
        moveY = plane.left_top.y - orig.left_top.y;

        moveZoomX = plane.width * (1-zoomFactor) / 2;
        moveZoomY = plane.height * (1-zoomFactor) / 2;
    }

    public Point mapPoint(Point point) {
        double newX = (point.x + moveX) * scaleX + moveZoomX;
        double newY = (point.y + moveY) * scaleY + moveZoomY;
        return new Point(newX,newY);
    }

    public Node mapNode(Node node) {
        double newX = (node.x + moveX) * scaleX + moveZoomX;
        double newY = (node.y + moveY) * scaleY + moveZoomY;
        double newR = node.r * (scaleX + scaleY) / 2;
        return new Node(newX,newY,newR);
    }

    public static Area getNodeBounds(ArrayList<Node> nodeList) {
        double xmin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymin = Double.MAX_VALUE;
        double ymax = Double.MIN_VALUE;

        for(Node n : nodeList) {
            double x = n.x;
            double y = n.y;

            if(x < xmin)
                xmin = x;
            if(x > xmax)
                xmax = x;
            if(y < ymin)
                ymin = y;
            if(y > ymax)
                ymax = y;
        }

        Point left_top = new Point(xmin,ymin);
        Point right_bottom = new Point(xmax,ymax);
        Area out = new Area(left_top,right_bottom);
        return out;
    }
}
