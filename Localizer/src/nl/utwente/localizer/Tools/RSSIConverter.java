package nl.utwente.localizer.Tools;

/**
 * Created by Joris on 03/06/2014.
 */
public class RSSIConverter {
    private static final int BASE_STRENGTH_AT_1M = -40;
    private static final double PROPAGATION_CONSTANT = 2.5;

    public static double SSToMeters(int rssi) {
        return Math.pow(10,((rssi - BASE_STRENGTH_AT_1M) / (-10 * PROPAGATION_CONSTANT)));
    }

    public static int MetersToSS(double meters) {
        return (int)((-1)*(10 * PROPAGATION_CONSTANT * Math.log10(meters) + BASE_STRENGTH_AT_1M));
    }
}
