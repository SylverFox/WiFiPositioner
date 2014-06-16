package nl.utwente.localizer.main;

import nl.utwente.localizer.datatypes.GPS;
import nl.utwente.localizer.datatypes.Node;
import nl.utwente.localizer.datatypes.Point;

/**
 * Created by Joris on 05/06/2014.
 */
public interface LocalizerProgressListener {
    public void newNode(Node node);
    public void newMarker(GPS gps);
    public void newResult(GPS gps, Point point);
    //public void progressUpdate();
    public void onError(String error);
}
