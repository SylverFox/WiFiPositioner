package nl.utwente.localizer.datatypes;

import java.util.ArrayList;

/**
 * Created by Joris on 15/06/2014.
 * Ensures no duplicate points
 */
public class PointArrayList extends ArrayList<Point> {
    @Override
    public boolean add(Point p) {
        for(int i = 0; i < size(); i++) {
            if(get(i).x == p.x && get(i).y == p.y)
                return false;
        }
        add(size(),p);
        return true;
    }
}
