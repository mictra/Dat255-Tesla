package com.dat255tesla.busexplorer;

/**
 * Created by roy lena on 2015-09-23.
 */
public class InfoNode {
    private final long id;
    private final String title;
    private final double lat;
    private final double lng;
    private final int type;
    private final String info;
    private final String addr;
    private final long lastMod;
    private final int nbrofimgs;

    public InfoNode(long id, String title, double lat, double lng, int type,
                    String info, String addr, long lastMod, int nbrofimgs) {
        this.id = id;
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.info = info;
        this.addr = addr;
        this.lastMod = lastMod;
        this.nbrofimgs = nbrofimgs;
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

    public int getNbrofimgs(){
        return nbrofimgs;
    }

    @Override
    public String toString() {
        return title + " at " + addr + " (" + lat + ", " + lng +
                ") with type " + type;
    }
}
