package com.dat255tesla.busexplorer.apirequest;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Michael on 2015-10-03.
 */
public interface IPositionChangedListener {

    void positionChanged(LatLng pos);

}
