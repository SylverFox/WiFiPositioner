package nl.utwente.localizer.datatypes;

/**
 * Created by Joris on 27/05/2014.
 */
public class DataPoint {
    public GPS gps = new GPS();
    public String mac = "";
    public int rssi = 0;

    public DataPoint() {}

    public DataPoint(GPS gps, String mac, int rssi) {
        this.gps = gps;
        this.mac = mac;
        this.rssi = rssi;
    }
}
