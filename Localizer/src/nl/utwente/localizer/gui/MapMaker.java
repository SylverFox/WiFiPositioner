package nl.utwente.localizer.gui;

import nl.utwente.localizer.datatypes.GPS;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Joris on 05/06/2014.
 */
public class MapMaker {

    public static final int STYLE_RED = 0;
    public static final int STYLE_BLUE = 1;
    public static final int STYLE_GREEN = 2;

    private static final int MAX_MARKERS = 75;

    private String MAP_TYPE = "roadmap";
    private int ZOOM_LEVEL = 20;

    private BufferedImage currentMap;
    private Map<GPS,Integer> markers;
    private int width;
    private int height;

    public MapMaker(int width, int height) {
        markers = new HashMap<>();
        this.width = width;
        this.height = height;
    }

    public void setMapType(String mapType) {
        MAP_TYPE = mapType;
    }

    public void setZoomLevel(int zoomLevel) {
        ZOOM_LEVEL = zoomLevel;
    }

    public void clearMarkers() {
        markers = new HashMap<>();
    }

    public BufferedImage getMap() {
        if(markers == null)
            return null;
        if(markers.size() == 0)
            return null;
        String url = "http://maps.google.com/maps/api/staticmap";
        url += "?zoom="+ZOOM_LEVEL;
        url += "&size=" + width/2 + "x" + height/2;
        url += "&scale=2";
        url += "&maptype="+MAP_TYPE;

        String markerBase1 = "&markers=size:tiny|color:red|";
        String markerBase2 = "&markers=size:mid|color:blue|";
        String markerBase3 = "&markers=size:mid|color:green|";
        int displayedMarkers = 0;
        for(Map.Entry<GPS,Integer> marker : markers.entrySet()) {
            GPS loc = marker.getKey().trim(8);
            if (marker.getValue() == STYLE_RED) {
                if(displayedMarkers < MAX_MARKERS) {
                    markerBase1 += loc.latitude + "," + loc.longitude + "|";
                    displayedMarkers++;
                }
            } else if (marker.getValue() == STYLE_BLUE) {
                markerBase2 += loc.latitude + "," + loc.longitude + "|";
            } else if (marker.getValue() == STYLE_GREEN) {
                markerBase3 += loc.latitude + "," + loc.longitude + "|";
            }
        }
        markerBase1 = markerBase1.substring(0,markerBase1.length()-1);
        markerBase2 = markerBase2.substring(0,markerBase2.length()-1);
        markerBase3 = markerBase3.substring(0,markerBase3.length()-1);
        url += markerBase1 + markerBase2 + markerBase3;
        System.out.println(url);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        InputStream in = null;
        try {
            in = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        byte[] imgBytes = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = in.read(buffer)) != -1) {
                bos.write(buffer, 0, n);
            }
            imgBytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            in.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage img = null;
        try {
            img = ImageIO.read(new ByteArrayInputStream(imgBytes));
        } catch (IOException e) {
            e.printStackTrace();
            return img;
        }
        currentMap = img;
        return img;
    }

    public void addMarker(GPS marker,int style) {
        markers.put(marker, style);
    }

    public void clear() {
        markers.clear();
    }
}
