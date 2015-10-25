package com.dat255tesla.busexplorer.database;

import java.io.Serializable;

/**
 * Created by roy lena on 2015-09-23.
 */

/**
 * Class representing a row in database table named "markers" with corresponding primitive data
 */
public class InfoNode implements Serializable {
    private final long id;
    private final String title;
    private final double lat;
    private final double lng;
    private final int type;
    private final String info;
    private final String addr;
    private final long lastMod;
    private final String objId;

    public InfoNode(long id, String title, double lat, double lng, int type,
                    String info, String addr, long lastMod, String objId) {
        this.id = id;
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.info = info;
        this.addr = addr;
        this.lastMod = lastMod;
        this.objId = objId;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lng;
    }

    public int getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    public String getAddress() {
        return addr;
    }

    public long getLatestModified(){
        return lastMod;
    }

    public String getObjId(){
        return objId;
    }

    @Override
    public String toString() {
        return title;
    }
}
