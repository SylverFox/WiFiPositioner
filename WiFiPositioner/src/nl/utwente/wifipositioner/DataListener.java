package nl.utwente.wifipositioner;

import android.location.Location;

/**
 * Created by Joris on 22/05/2014.
 */
public interface DataListener {
    public void onConsoleMessage(String message);
    public void onGPSUpdate(Location location);
}