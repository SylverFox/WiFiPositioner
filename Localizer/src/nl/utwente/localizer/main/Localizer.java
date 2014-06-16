package nl.utwente.localizer.main;

import nl.utwente.localizer.Tools.AreaConverter;
import nl.utwente.localizer.Tools.AreaMapper;
import nl.utwente.localizer.Tools.RSSIConverter;
import nl.utwente.localizer.datatypes.*;
import nl.utwente.localizer.sql.ImportDB;
import nl.utwente.localizer.sql.SQLHandle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import static nl.utwente.localizer.main.Helper.Log;

/**
 * Created by Joris on 02/06/2014.
 */
public class Localizer extends Thread {
    private final String DATABASE_REMOTE = "/data/data/nl.utwente.wifipositioner/databases/CaptureDatabase";
    private final String DATABASE_LOCAL = "db/CaptureDatabase";

    private SQLHandle sqlHandle;
    private LocalizerProgressListener progressListener;
    private String target;
    private boolean running;

    public Localizer(LocalizerProgressListener progressListener) {
        this.progressListener = progressListener;
        ImportDB.importDB(DATABASE_REMOTE,DATABASE_LOCAL);
        sqlHandle = new SQLHandle(DATABASE_LOCAL);
    }

    @Override
    public void run() {
        while(true) {
            if(target == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                running = true;
                calculatePosition(target);
                target = null;
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    private void calculatePosition(String mac) {
        // mac laptop: 00:21:6A:5B:AA:20
        // mac phone : bc:cf:cc:73:71:8d
        Log("Calculating position for target: "+mac);
        Log("Getting data points");
        ArrayList<DataPoint> dataPoints = getDataPoints(mac);
        if(dataPoints == null || dataPoints.size() == 0) {
            Log("Unable to get data points or no data points available");
            return;
        }
        Log("Total data points: "+dataPoints.size());

        Log("Preparing area conversion");
        ArrayList<GPS> gpsList = new ArrayList<>();
        for(DataPoint dp : dataPoints) {
            gpsList.add(dp.gps);
        }
        double avgLat = AreaConverter.calculateAvgLatitude(gpsList);
        AreaConverter areaConverter = new AreaConverter(avgLat);

        //show nodes in map
        for(GPS gps : gpsList) {
            if(progressListener != null)
                progressListener.newMarker(gps);
        }


        Log("Mapping coordinates over 2d plane and converting RSSI values");
        ArrayList<Node> nodeList = new ArrayList<>();
        for(DataPoint dp : dataPoints) {
            Point p = areaConverter.gpsToPlane(dp.gps);
            double r = RSSIConverter.SSToMeters(dp.rssi);
            nodeList.add(new Node(p.x,p.y,r));
        }

        //show nodes in map
        Area a = AreaMapper.getNodeBounds(nodeList);
        //gui.drawPanel.setAreaMapper(a);
        for(Node n : nodeList) {
            if(progressListener != null)
                progressListener.newNode(n);
        }

        Log("Determining candidates");
        ArrayList<Point> candidates = Algorithms.determineIntersectionCandidates(nodeList);
        Log("Total candidate points: "+candidates.size());
        if(candidates.size()==0) {
            Log("No candidates for localization found!");
            return;
        } else if(candidates.size()==1) {
            //done
            progressListener.newResult(areaConverter.planeToGPS(candidates.get(0)),candidates.get(0));
            return;
        }

        Log("Calculating average density");
        DensityMap densityMap = Algorithms.averageDensity(candidates);
        double avgDensity = densityMap.getAverageDensity();
        Log("Average node density: "+avgDensity);

        Log("Determining valid candidates");
        ArrayList<Point> closeCandidates = new ArrayList<>();
        for(Map.Entry<Point,Double> entry : densityMap.entrySet()) {
            if(entry.getValue() > avgDensity)
                closeCandidates.add(entry.getKey());
        }
        Log("Selected candidates: "+closeCandidates.size());

        Log("Calculating estimated location");
        Point estimatedLocation = Algorithms.estimateLocation(closeCandidates);
        GPS gpsLocation = areaConverter.planeToGPS(estimatedLocation);

        Log("Estimated location: "+gpsLocation.latitude+","+gpsLocation.longitude);

        if(progressListener != null)
            progressListener.newResult(gpsLocation,estimatedLocation);
    }

    public ArrayList<String> getMacs() {
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
