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
import java.util.HashMap;

/**
 * Created by roy lena on 2015-09-23.
 */
public class MapUtils {

    /**
     * Will animate the movement of a Marker from its current position to its 'toPosition'.
     * This is a slightly shortened version of the same method given in the Google Maps V2 Demo app.
     *
     * @param marker Marker to be animated
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

    public static ArrayList<Marker> MarkersInRange(HashMap<Marker, InfoNode> map, Location center,
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
     * @param loc   An existing object with a latitude and longitude
     * @return      A new LatLng instance at provided Location.
     * @see         LatLng
     */
    public static LatLng LocToLatLng (Location loc) {
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
     * @return       A new Location instance at provided LatLng
     * @see          Location
     */
    public static Location LatLngToLoc (LatLng latlng, String name) {
        Location loc = new Location(name);
        loc.setLatitude(latlng.latitude);
        loc.setLongitude(latlng.longitude);
        return loc;
    }
}
