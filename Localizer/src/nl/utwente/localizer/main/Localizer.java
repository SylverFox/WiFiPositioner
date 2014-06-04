package nl.utwente.localizer.main;

import nl.utwente.localizer.Tools.AreaConverter;
import nl.utwente.localizer.Tools.RSSIConverter;
import nl.utwente.localizer.datatypes.DataPoint;
import nl.utwente.localizer.datatypes.GPS;
import nl.utwente.localizer.datatypes.Node;
import nl.utwente.localizer.datatypes.Point;
import nl.utwente.localizer.sql.ImportDB;
import nl.utwente.localizer.sql.SQLHandle;

import java.sql.SQLException;
import java.util.ArrayList;

import static nl.utwente.localizer.main.Helper.Log;

/**
 * Created by Joris on 02/06/2014.
 */
public class Localizer {
    public static void main(String[] args) { new Localizer(); }

    private final String DATABASE_REMOTE = "/data/data/nl.utwente.wifipositioner/databases/CaptureDatabase";
    private final String DATABASE_LOCAL = "res/CaptureDatabase";

    private SQLHandle sqlHandle;


    public Localizer() {
        ImportDB.importDB(DATABASE_REMOTE,DATABASE_LOCAL);
        sqlHandle = new SQLHandle(DATABASE_LOCAL);

        ArrayList<String> macs = getMacs();

        Point result = calculatePosition("00:21:6A:5B:AA:20");
    }

    private Point calculatePosition(String mac) {
        Log("Getting data points");
        ArrayList<DataPoint> dataPoints = getDataPoints(mac);
        if(dataPoints == null || dataPoints.size() == 0) {
            Log("Unable to get data points or no data points available");
            return null;
        }
        Log("Total data points: "+dataPoints.size());

        Log("Preparing area conversion");
        ArrayList<GPS> gpsList = new ArrayList<>();
        for(DataPoint dp : dataPoints) {
            gpsList.add(dp.gps);
        }
        double avgLat = AreaConverter.calculateAvgLatitude(gpsList);
        AreaConverter areaConverter = new AreaConverter(avgLat);

        Log("Mapping coordinates over 2d plane and converting RSSI values");
        ArrayList<Node> nodeList = new ArrayList<>();
        for(DataPoint dp : dataPoints) {
            Point p = areaConverter.gpsToPlane(dp.gps);
            double r = RSSIConverter.SSToMeters(dp.rssi);
            nodeList.add(new Node(p.x,p.y,r));
        }

        Log("Determining candidates");
        ArrayList<Point> candidates = Algorithms.determineIntersectionCandidates(nodeList);
        Log("Total candidate points: "+candidates.size());

        Log("Calculating average density");
        double avgDensity = Algorithms.averageDensity(candidates);
        Log("Average node density: "+avgDensity);

        Log("Determining valid candidates");
        ArrayList<Point> closeCandidates = new ArrayList<>();
        for(Point p : candidates) {
            ArrayList<Point> usedList = (ArrayList<Point>) candidates.clone();
            usedList.remove(p);
            double density = Algorithms.density(p,usedList);
            if(density > avgDensity)
                closeCandidates.add(p);
        }
        Log("Selected candidates: "+closeCandidates.size());

        Log("Calculating estimated location");
        Point estimatedLocation = Algorithms.estimateLocation(closeCandidates);
        GPS estLocation = areaConverter.planeToGPS(estimatedLocation);

        Log("Estimated location: "+estLocation.latitude+","+estLocation.longitude);

        return estimatedLocation;
    }

    private ArrayList<String> getMacs() {
        ArrayList<String> out = null;
        try {
            out = sqlHandle.getMacs();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    private ArrayList<DataPoint> getDataPoints(String mac) {
        ArrayList<DataPoint> out = null;
        try {
            out = sqlHandle.getDataPoints(mac.toLowerCase());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

}
