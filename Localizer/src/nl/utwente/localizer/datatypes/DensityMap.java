package nl.utwente.localizer.datatypes;

import java.util.HashMap;

/**
 * Created by Joris on 15/06/2014.
 */
public class DensityMap extends HashMap<Point,Double> {
    private double AVERAGE_DENSITY = 0;

    public void setAverageDensity(double d) {
        AVERAGE_DENSITY = d;
    }

    public double getAverageDensity() {
        return AVERAGE_DENSITY;
    }
}
