package com.dat255tesla.busexplorer;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by roy lena on 2015-09-23.
 */
public class MapUtils {
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
