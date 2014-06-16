package nl.utwente.localizer.datatypes;

import java.text.NumberFormat;

/**
 * Created by Joris on 27/05/2014.
 */
public class GPS {
    public double latitude = 0.0;
    public double longitude = 0.0;

    public GPS() {}

    public GPS(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Point getPoint() {
        return new Point(latitude,longitude);
    }

    public GPS trim(int digits) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(digits);
        String temp = nf.format(latitude);
        latitude = Double.parseDouble(nf.format(latitude));
        longitude = Double.parseDouble(nf.format(longitude));
        return this;
    }
}
