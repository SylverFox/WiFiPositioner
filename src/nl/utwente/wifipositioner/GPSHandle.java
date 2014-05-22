package nl.utwente.wifipositioner;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Joris on 22/05/2014.
 */
public class GPSHandle extends Thread implements LocationListener {

    private DataListener listener;
    private LocationManager locationManager;

    public GPSHandle(DataListener listener, LocationManager locationManager) {
        this.listener = listener;
        this.locationManager = locationManager;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    public void run() {}

    @Override
    public void onLocationChanged(Location location) {
        listener.onGPSUpdate(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}
