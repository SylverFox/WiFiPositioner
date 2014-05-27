package tools;


/**
 * Created by Joris on 27/05/2014.
 */
public class AreaConverter {
    double originalMinX,originalMaxX,originalMinY,originalMaxY;
    double newMinX,newMaxX,newMinY,newMaxY;
    double paddingPerc;

    public AreaConverter(double originalMinX, double originalMaxX, double originalMinY, double originalMaxY, double newMinX, double newMaxX, double newMinY, double newMaxY, double paddingPerc) {
        this.originalMinX = originalMinX;
        this.originalMaxX = originalMaxX;
        this.originalMinY = originalMinY;
        this.originalMaxY = originalMaxY;
        this.newMinX = newMinX;
        this.newMaxX = newMaxX;
        this.newMinY = newMinY;
        this.newMaxY = newMaxY;
        this.paddingPerc = paddingPerc;

        adjustToPadding();
    }

    private void adjustToPadding() {
        double newWidth = newMaxX - newMinX;
        double newHeight = newMaxY - newMinY;
        double widthPadding = paddingPerc*newWidth/2;
        double heightPadding = paddingPerc*newHeight/2;

        newMaxX -= widthPadding;
        newMinX -= widthPadding;
        newMaxY -= heightPadding;
        newMinY -= heightPadding;
    }
}
