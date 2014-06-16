package nl.utwente.localizer.datatypes;

/**
 * Created by Joris on 05/06/2014.
 */
public class Area {
    public Point left_top;
    public Point right_bottom;
    public double width;
    public double height;

    public Area(Point left_top, Point right_bottom) {
        this.left_top = left_top;
        this.right_bottom = right_bottom;
        this.width = right_bottom.x-left_top.x;
        this.height = right_bottom.y-left_top.y;
    }
}
