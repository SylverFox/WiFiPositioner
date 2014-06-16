package nl.utwente.localizer.tools;

import nl.utwente.localizer.datatypes.GPS;
import nl.utwente.localizer.datatypes.Point;

import java.util.ArrayList;

/**
 * Created by Joris on 03/06/2014.
 */
public class AreaConverter {
    private static final double EARTH_RADIUS = 6378.1;
    private static final double DEG2RAD = Math.PI/180;
    private static final double RAD2DEG = 180/Math.PI;
    private static final int KM2METER = 1000;

    private double cosAvgLatitude = 0;

    public AreaConverter(double avgLatitude) {
        this.cosAvgLatitude = Math.cos(avgLatitude*DEG2RAD);
    }

    public Point gpsToPlane(GPS gps) {
        double x = EARTH_RADIUS * gps.longitude * DEG2RAD * cosAvgLatitude * KM2METER;
        double y = EARTH_RADIUS * gps.latitude * DEG2RAD * KM2METER;
        return new Point(x,y);
    }

    public GPS planeToGPS(Point point) {
        double longitude = point.x / (EARTH_RADIUS * cosAvgLatitude * KM2METER) * RAD2DEG;
        double latitude = point.y / (EARTH_RADIUS * KM2METER) * RAD2DEG;
        return new GPS(latitude,longitude);
    }

    public static double calculateAvgLatitude(ArrayList<GPS> gpsList) {
        double totalLat = 0;
        for(GPS gps : gpsList) {
            totalLat += gps.latitude;
        }
        double avgLat = totalLat / gpsList.size();
        return avgLat;
    }
}
