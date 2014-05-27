package tools;

/**
 * Created by Joris on 27/05/2014.
 */
public class SignalStrengthConverter {
    private static final int BASE_STRENGTH_AT_1M = -40;
    private static final double PROPAGATION_CONSTANT = 2.5;

    public static int SSToMeters(int rssi) {
        return (int) Math.pow(10,((rssi - BASE_STRENGTH_AT_1M) / (-10 * PROPAGATION_CONSTANT)));
    }

    public static int MetersToSS(int meters) {
        return (int)(-10 * PROPAGATION_CONSTANT * Math.log10(meters) + BASE_STRENGTH_AT_1M);
    }
}
