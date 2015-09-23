package com.dat255tesla.busexplorer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by roy lena on 2015-09-23.
 */
public class InfoNode {
    private String title;
    private String info;
    //private ArrayList<String> images; // Image names? Easy to send via intent.

    // TODO: Load info from file, possibly images as well / links to.
    public InfoNode(String title, String info) {
        this.title = title;
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }
}
