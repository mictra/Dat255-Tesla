package com.dat255tesla.busexplorer;

import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by roy lena on 2015-09-16.
 */
public class BusMap {
    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private HashSet<Marker> markers;

    public BusMap(GoogleMap map) {
        this.map = map;

        markers = new HashSet<>();
    }

    @Deprecated
    public boolean addMarker(LatLng lat) {
        MarkerOptions marker = new MarkerOptions().position(lat);
        return markers.add(map.addMarker(marker));
    }

    public boolean addMarker(MarkerOptions opt) {
        return markers.add(map.addMarker(opt));
    }

    public ArrayList<Marker> getMarkersInRange(Location center, int maxDist) {
        ArrayList<Marker> out = new ArrayList<>();
        Location tempLoc = new Location("temp");

        for (Marker temp : markers) {
            LatLng tempPos = temp.getPosition();
            tempLoc.setLatitude(tempPos.latitude);
            tempLoc.setLongitude(tempPos.longitude);

            float distance = center.distanceTo(tempLoc);

            if (distance < maxDist) {
                out.add(temp);
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
