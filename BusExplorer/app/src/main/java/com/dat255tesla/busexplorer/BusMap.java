package com.dat255tesla.busexplorer;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by roy lena on 2015-09-16.
 */
public class BusMap extends FragmentActivity {
    private GoogleMap map; // Might be null if Google Play services APK is not available.

    public BusMap(GoogleMap map) {
        this.map = map;
    }
}
