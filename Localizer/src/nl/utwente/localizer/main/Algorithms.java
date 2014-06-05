package nl.utwente.localizer.main;

import javafx.util.Pair;
import nl.utwente.localizer.datatypes.DataPoint;
import nl.utwente.localizer.datatypes.GPS;
import nl.utwente.localizer.datatypes.Node;
import nl.utwente.localizer.datatypes.Point;

import java.util.ArrayList;

/**
 * Created by Joris on 02/06/2014.
 */
public class Algorithms {

    public static double density(Point point, ArrayList<Point> pointsList) {
        double totalDistance = 0.0;
        for(Point dp : pointsList) {
            double dist = distance(point, dp);
            totalDistance += dist;
        }
        double avgDist = totalDistance / pointsList.size();
        double densityIndex = 1 / avgDist;
        return densityIndex;
    }

    public static double averageDensity(ArrayList<Point> points) {
        double totalDensity = 0;
        for(Point p : points) {
            ArrayList<Point> usedList = (ArrayList<Point>) points.clone();
            usedList.remove(p);
            double density = density(p,usedList);
            totalDensity += density;
        }
        double avgDensity = totalDensity / points.size();
        return avgDensity;
    }

    public static ArrayList<Point> determineIntersectionCandidates(ArrayList<Node> nodeList) {
        ArrayList<Point> output = new ArrayList<>();
        for(int i = 0; i < nodeList.size() - 2; i++) {
            for(int j = i + 1; j < nodeList.size() - 1; j++) {
                Pair<Point,Point> candidates = intersect(nodeList.get(i),nodeList.get(j));
                if(candidates != null) {
                    if(candidates.getKey() != null)
                        output.add(candidates.getKey());
                    if(candidates.getValue() != null)
                        output.add(candidates.getValue());
                }
            }
        }
        return output;
    }

    /**
     * See: https://stackoverflow.com/questions/3349125/circle-circle-intersection-points
     */
    public static Pair<Point,Point> intersect(Node node1, Node node2) {
        Point p1 = node1.getPoint();
        Point p2 = node2.getPoint();
        double r1 = node1.r;
        double r2 = node2.r;
        double d = distance(p1,p2);

        if(d == 0) {
            //points are in the same location, no intersection points or infinite!
            return null;
        } else if (d > (r1+r2) || d < Math.abs(r1-r2)) {
            //circles do not touch, calculate real part of complex values
            double x = (p2.x+p1.x)/2;
            double y = (p2.y+p1.y)/2;
            Point p = new Point(x,y);
            return new Pair<Point,Point>(p,null);
        } else {
            // circles touch
            double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);

            double h = Math.sqrt( (r1*r1-a*a) );
            // middle point
            double pmx = p1.x + a * ( p2.x - p1.x ) / d;
            double pmy = p1.y + a * ( p2.y - p1.y ) / d;
            // intersection points
            double pi1x = pmx + h * ( p2.y - p1.y ) / d;
            double pi1y = pmy - h * ( p2.x - p1.x ) / d;
            double pi2x = pmx - h * ( p2.y - p1.y ) / d;
            double pi2y = pmy + h * ( p2.x - p1.x ) / d;
            //return points
            Point p12 = new Point(pi1x,pi1y);
            Point p21 = new Point(pi2x,pi2y);
            return new Pair<Point,Point>(p12,p21);
        }
    }

    public static double distance(DataPoint A, DataPoint B) {
        return distanceGPS(A.gps,B.gps);
    }

    public static double distance(Point A, Point B) {
        return Math.sqrt(Math.pow(A.x-B.x,2)+Math.pow(A.y-B.y,2));
    }

    //returns distance between two coordinates in meters
    public static double distanceGPS(GPS A, GPS B) {
        double d2r = (float) (Math.PI/180.0);
        double dlong = Math.abs(A.longitude - B.longitude) * d2r;
        double dlat = Math.abs(A.latitude-B.latitude) * d2r;

        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(B.latitude * d2r) * Math.cos(A.latitude * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6367 * c * 1000;
        return distance;
    }

    public static Point estimateLocation(ArrayList<Point> points) {
        double totalX = 0;
        double totalY = 0;
        for(Point p : points) {
            totalX += p.x;
            totalY += p.y;
        }
        double avgX = totalX / points.size();
        double avgY = totalY / points.size();
        return new Point(avgX,avgY);
    }

    public static int totalGroups(int nodes) {
        return factorial(nodes)/(factorial(2)*factorial(nodes-2));
    }

    public static int factorial(int m) {
        if(m == 1)
            return 1;
        else
            return m * factorial(m-1);
    }
}
