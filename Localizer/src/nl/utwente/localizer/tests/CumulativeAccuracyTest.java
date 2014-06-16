package nl.utwente.localizer.tests;

import nl.utwente.localizer.datatypes.DataPoint;
import nl.utwente.localizer.datatypes.GPS;
import nl.utwente.localizer.datatypes.Node;
import nl.utwente.localizer.datatypes.Point;
import nl.utwente.localizer.gui.MapMaker;
import nl.utwente.localizer.main.Algorithms;
import nl.utwente.localizer.main.Localizer;
import nl.utwente.localizer.main.LocalizerProgressListener;

import java.util.ArrayList;

/**
 * Created by Joris on 16/06/2014.
 */
public class CumulativeAccuracyTest implements LocalizerProgressListener {
    private Localizer localizer;
    private MapMaker mapMaker;

    private final GPS ACTUAL_POSITION = new GPS(51.99996431472594,6.3078273832798);
    private final String TARGET_MAC = "00:21:6a:5b:aa:20";

    private int currentLimit = 3;
    private int maxLimit;

    private double lastError = 0;

    public static void main(String[] args) {
        new CumulativeAccuracyTest();
    }

    public CumulativeAccuracyTest() {
        localizer = new Localizer(this);
        ArrayList<DataPoint> availablePoints = localizer.getDataPoints(TARGET_MAC);
        if(availablePoints.size() < 3) {
            System.err.println("Not enough available datapoints");
            return;
        }

        maxLimit = availablePoints.size();

        localizer.setLimit(currentLimit);
        localizer.setTarget(TARGET_MAC);
        localizer.start();
    }

    @Override
    public void newNode(Node node) {}

    @Override
    public void newMarker(GPS gps) {}

    @Override
    public void newResult(GPS gps, Point point) {
        double error = Algorithms.distanceGPS(gps,ACTUAL_POSITION);
        System.out.println("Data Points: "+currentLimit+". Position error: "+error+" meters. difference with last error: "+(lastError-error));
        lastError = error;
        if(currentLimit < maxLimit) {
            currentLimit++;
            localizer.setTarget(TARGET_MAC);
        } else {
            System.out.println("done!");
        }
    }

    @Override
    public void onError(String error) { System.err.println(error); }
}
