package com.dat255tesla.busexplorer.explorercontent;

import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.dat255tesla.busexplorer.database.InfoNode;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by roy lena on 2015-09-23.
 */
public class MapUtils {

    /**
     * Will animate the movement of a Marker from its current position to its 'toPosition'.
     * This is a slightly shortened version of the same method given in the Google Maps V2 Demo app.
     *
     * @param marker     Marker to be animated
     * @param toPosition The final position of the marker after its animation
     */
    public static void animateMarker(final Marker marker, final LatLng toPosition, GoogleMap mMap) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        // each update is every 3 seconds, so...
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    /**
     * Parses an RMC string returning its latitude and longitude.
     * What a shitty format. Seriously.
     *
     * @param gprmc The string to be decoded
     * @return A {@link LatLng} containing the coordinates given in the RMC string.
     */
    public static LatLng ParseRMC(String gprmc) {
        LatLng latlng;

        // TODO null checks, size checks, etc

        String[] parts = gprmc.replaceAll("^[,\\s]+", "").split("[,\\s]+");
        if (5 < parts.length) {
            String lat = parts[3];
            String lng = parts[5];
            String ns = parts[4];
            String ew = parts[6];

            Double newLat = Double.parseDouble(lat.substring(0, 2)) +
                    (Double.parseDouble(lat.substring(2)) / 60);

            Double newLng = Double.parseDouble(lng.substring(0, 3)) +
                    (Double.parseDouble(lng.substring(3)) / 60);

            // If south of equator
            if (ns.equals('S')) {
                newLat *= -1;
            }

            // If west of greenwich
            if (ew.equals('W')) {
                newLng *= -1;
            }

            latlng = new LatLng(newLat, newLng);
        } else {
            latlng = new LatLng(0, 0);
        }

        return latlng;
    }

    public static List<Marker> MarkersInRange(HashMap<Marker, InfoNode> map, Location center,
                                              int maxDist) {

        ArrayList<Marker> out = new ArrayList<>();
        Location tempLoc;

        for (Marker key : map.keySet()) {
            tempLoc = LatLngToLoc(key.getPosition(), "temp");

            float distance = center.distanceTo(tempLoc);
            if (distance < maxDist) {
                out.add(key);
            }
        }
        return out;
    }

    /**
     * Returns a {@link LatLng} that has the same positional properties as the supplied
     * {@link Location}
     * Useful for easily switching between the normal Location class used by Android and the LatLng
     * class that is used by the Google Maps V2 API.
     *
     * @param loc An existing object with a latitude and longitude
     * @return A new LatLng instance at provided Location.
     * @see LatLng
     */
    public static LatLng LocToLatLng(Location loc) {
        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    /**
     * Returns a {@link Location} that has the same positional properties as the supplied
     * {@link LatLng}
     * Useful for easily switching between the normal Location class used by Android and the LatLng
     * class that is used by the Google Maps V2 API.
     *
     * @param latlng An existing object with a latitude and longitude
     * @param name   A name/identifier used by the Location class
     * @return A new Location instance at provided LatLng
     * @see Location
     */
    public static Location LatLngToLoc(LatLng latlng, String name) {
        Location loc = new Location(name);
        loc.setLatitude(latlng.latitude);
        loc.setLongitude(latlng.longitude);
        return loc;
    }

    public static List<InfoNode> sortByDistance(final List<InfoNode> values, String nextStop) {
        System.out.println("+++++++++++++++++++++++LENGTH OF LIST: " + values.size());
        List<InfoNode> valuesCopy = new ArrayList(values);
        InfoNode node = null;
        for (InfoNode infoNode : valuesCopy) {
            if (nextStop.toLowerCase().contains(infoNode.getTitle().toLowerCase())) {
                node = infoNode;
                valuesCopy.remove(infoNode);
                break;
            }
        }

        if (node != null) {
            HashMap<InfoNode, Float> map = new HashMap<>();
            float[] dist = new float[1];
            double lati = node.getLatitude();
            double longi = node.getLongitude();
            for (InfoNode infoNode : valuesCopy) {
                Location.distanceBetween(lati, longi, infoNode.getLatitude(), infoNode.getLongitude(), dist);
                map.put(infoNode, dist[0]);
            }
            return sortByComparator(map);
        } else {
            return null;
        }

    }

    private static List<InfoNode> sortByComparator(Map<InfoNode, Float> unsortedMap) {

        // Convert Map to List
        List<Map.Entry<InfoNode, Float>> list = new LinkedList<>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<InfoNode, Float>>() {
            public int compare(Map.Entry<InfoNode, Float> o1, Map.Entry<InfoNode, Float> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Print out sorted list
        printMap(list);

        List<InfoNode> sortedList = new ArrayList<>();
        for (Map.Entry<InfoNode, Float> entry : list) {
            sortedList.add(entry.getKey());
        }
        return sortedList;
    }

    public static void printMap(List<Map.Entry<InfoNode, Float>> list) {
        for (Map.Entry<InfoNode, Float> entry : list) {
            System.out.println("\n-------------[InfoNode title (marker name)] : " + entry.getKey().getTitle()
                    + " [Value (distance in meters)] : " + entry.getValue());
        }
    }

}
