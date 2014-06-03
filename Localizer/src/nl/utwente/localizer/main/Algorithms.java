package nl.utwente.localizer.main;

import javafx.util.Pair;
import nl.utwente.localizer.datatypes.*;

import java.util.ArrayList;

/**
 * Created by Joris on 02/06/2014.
 */
public class Algorithms {
    public static double density(Point point,int depth) {
        return 0.0;
    }

    public static double averageDensity(ArrayList<Point> points) {
        return 0.0;
    }

    public static Pair<Point,Point> intersect(DataPoint dp1, DataPoint dp2) {
        Point p1 = dp1.gps.getPoint();
        Point p2 = dp2.gps.getPoint();
        int r1 = dp1.rssi;
        int r2 = dp2.rssi;
        double d = distance(dp1,dp2);

        double avgDistX = (p2.x + p1.x) / 2;


        return null;
    }

    public static double distance(DataPoint A, DataPoint B) {
        return distanceGPS(A.gps,B.gps);
    }

    //returns distance between two coordinates in meters
    public static double distanceGPS(GPS A, GPS B) {
        double d2r = (float) (Math.PI/180.0);
        double dlong = Math.abs(A.longitude - B.longitude) * d2r;
        double dlat = Math.abs(A.latitude-B.latitude) * d2r;

        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(B.latitude * d2r) * Math.cos(A.latitude * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6367 * c * 1000;
        return distance;
    }

    public static Point estimaeLocation(ArrayList<Point> points) {
        return null;
    }
}
