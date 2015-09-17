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

    public boolean addMarker(LatLng lat) {
        MarkerOptions marker = new MarkerOptions().position(lat);
        return markers.add(map.addMarker(marker));
    }

    // Experiment - don't actually use this
    public boolean removeMarker(Marker m) {
        return markers.remove(m);
    }

    public ArrayList<Marker> getMarkersInRange(Location center, int maxDist) {
        ArrayList<Marker> out = new ArrayList<Marker>();
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
}
