package nl.utwente.localizer.datatypes;

/**
 * Created by Joris on 03/06/2014.
 */
public class Node {
    public double x = 0;
    public double y = 0;
    public double r = 0;

    public Node() {
    }

    public Node(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public Point getPoint() {
        return new Point(x,y);
    }
}
