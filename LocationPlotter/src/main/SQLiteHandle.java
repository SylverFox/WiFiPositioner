package main;

import datatypes.DataPoint;
import datatypes.GPS;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Joris on 27/05/2014.
 */
public class SQLiteHandle {
    private Connection c = null;

    public SQLiteHandle(String databaseFile) {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:"+databaseFile);
        } catch ( Exception e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public ArrayList<String> getMacs() throws SQLException {
        Statement stat = c.createStatement();
        ResultSet rs = stat.executeQuery("SELECT mac FROM capturedata GROUP BY mac");
        ArrayList<String> output = new ArrayList<>();
        while(rs.next()) {
            String mac = rs.getString("mac");
            output.add(mac);
        }
        rs.close();
        stat.close();
        return output;
    }

    public ArrayList<DataPoint> getDataPoints(String mac) throws SQLException {
        Statement stat = c.createStatement();
        ResultSet rs = stat.executeQuery("SELECT gps,rssi FROM capturedata WHERE mac = \"" + mac + "\"");
        ArrayList<DataPoint> output = new ArrayList<>();
        while(rs.next()) {
            String gpsValue = rs.getString("gps");
            String rssiValue = rs.getString("rssi");

            String[] longlat = gpsValue.split(",");

            GPS gps = new GPS();
            gps.latitude = Double.parseDouble(longlat[0]);
            gps.longitude = Double.parseDouble(longlat[1]);

            int rssi = Integer.parseInt(rssiValue);

            DataPoint dp = new DataPoint(gps,mac,rssi);

            output.add(dp);
        }
        rs.close();
        stat.close();
        return output;
    }
}
