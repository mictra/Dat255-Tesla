package com.dat255tesla.busexplorer;

import com.google.android.gms.maps.SupportMapFragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private GoogleMap mMap;
    private HashMap<Marker, InfoNode> markers;

    private MarkerOptions busStopOptions;

    // examples
    private Location pretendLocation;
    private InfoNode exampleNode; //TODO: Create all InfoNodes from file or db

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pretendLocation = new Location("pretend");
        // (57.707373, 11.973864) - "Nära" (<2km) göteborgsmarkören
        pretendLocation.setLatitude(57.707373);
        pretendLocation.setLongitude(11.973864);
        exampleNode = new InfoNode("test", "information");
        markers = new HashMap<>();

        busStopOptions =  new MarkerOptions()
                .alpha(0.8f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_01));

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_detailview:
                openDetailView();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng latlng = new LatLng(pretendLocation.getLatitude(), pretendLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));

        // EXPERIMENTERING!!
        // Bra att veta: det finns mer lyssnare (och callbacks) än bara OnMapClickListener()
        // skriv GoogleMap.On utanför kommentaren och se alternativen!
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addBusStop(latLng, exampleNode);

                ArrayList<Marker> nearMyLocation =
                        MapUtils.MarkersInRange(markers, pretendLocation, 5000);

                Toast.makeText(getApplicationContext(),
                        nearMyLocation.size() + " markers within 5000 meters (of cyan)",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = markers.get(marker).getTitle();
                Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();

                return false;
            }
        });
    }

    /**
     * Creates a new {@link Marker} which is used as a key with its information {@link InfoNode} as
     * value in the busStops map.
     * @param latLng    The position of the new bus stop in {@link LatLng} coordinates.
     * @param info      Contains information about the bus stop.
     */
    private void addBusStop(LatLng latLng, InfoNode info) {
        busStopOptions.position(latLng)
                .title(info.getTitle());
        markers.put(mMap.addMarker(busStopOptions), info);
    }

    /**
     * Open the Settings
     */
    private void openSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    /**
     * Open the DetailView
     * This is just a temporary method. Will be moved to ListView-listener once available.
     */

    private void openDetailView() {
        Intent intent = new Intent(this, DetailView.class);
        startActivity(intent);
    }
}