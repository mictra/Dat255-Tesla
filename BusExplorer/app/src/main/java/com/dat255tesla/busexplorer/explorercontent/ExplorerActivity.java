package com.dat255tesla.busexplorer.explorercontent;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dat255tesla.busexplorer.R;
import com.dat255tesla.busexplorer.apirequest.APIHelper;
import com.dat255tesla.busexplorer.apirequest.IBusDataListener;
import com.dat255tesla.busexplorer.database.IValuesChangedListener;
import com.dat255tesla.busexplorer.database.InfoDataSource;
import com.dat255tesla.busexplorer.database.InfoNode;
import com.dat255tesla.busexplorer.detailcontent.DetailActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExplorerActivity extends Fragment implements IValuesChangedListener, IBusDataListener {
    private GoogleMap mMap;
    private APIHelper apiHelper;
    private InfoDataSource ds;
    private String dgw;
    private boolean[] categories;
    private boolean prideMode = false;
    private MediaPlayer mPlayer;
    private int mPlayer_pos = 0;
    private View v;

    private String nextStop = "";
    private boolean isLockedToBus = true;

    private HashMap<Marker, InfoNode> markers;
    private Marker busMarker;
    private MarkerOptions opt_stops;
    private MarkerOptions opt_sights;
    private MarkerOptions opt_stores;
    private MarkerOptions opt_bars;
    private MarkerOptions busPositionOptions;

    private ListArrayAdapter adapter;
    private ListView belowMapList;
    private List<InfoNode> originalValues;
    private List<InfoNode> favoriteList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Receive dgw number from the identified bus when connecting to its wifi
        dgw = getArguments().getString("dgw");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_explorer, container, false);

        // Create an AsyncTask (APIHelper), and reference this class as a listener
        apiHelper = new APIHelper(this, dgw);
        markers = new HashMap<>();
        nextStop = "";

        // Retrieve settings data (filter categories settings)
        categories = new boolean[3];
        loadSavedPreferences();

        // Establish internal database connection and retrieve the values from it (with type List<InfoNode>)
        ds = new InfoDataSource(getActivity());
        ds.setValuesChangedListener(this);
        try {
            ds.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        originalValues = ds.getAllInfoNodes();

        // Set marker icons for marker options
        opt_stops = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_stop));
        opt_sights = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_triangle));
        opt_stores = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_square));
        opt_bars = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_circle));
        busPositionOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_02))
                .anchor(0.5f, 0.5f);

        createList();
        setUpMapIfNeeded();
        ds.updateDatabaseIfNeeded();

        favoriteList = new ArrayList<>();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load filtered categories from settings and show filtered markers on markers list
        loadSavedPreferences();
        sortFilterShow();
        if (apiHelper.isCancelled()) {
            apiHelper = new APIHelper(this, dgw);
            apiHelper.execute();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        apiHelper.cancel(true);
    }

    /**
     * Load the values of categories to be filtered and save them in the categories array
     */
    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        categories[0] = sharedPreferences.getBoolean("CheckBox_sightseeing", true);
        categories[1] = sharedPreferences.getBoolean("CheckBox_shopping", true);
        categories[2] = sharedPreferences.getBoolean("CheckBox_bars", true);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call setUpMap() once when mMap is not null.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * This should only be called once and when we are sure that mMap is not null.
     * We also execute the AsyncTask #apiHelper to get relevant data from the bus in the background.
     */
    private void setUpMap() {
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                isLockedToBus = false;
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle().equals("Buss")) {
                    isLockedToBus = true;
                }

                return false;
            }
        });

        // Start location
        LatLng startloc = new LatLng(57.704874, 11.965345);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startloc, 8));
        apiHelper.execute();
    }

    /**
     * Given an InfoNode, determine its category/type and set the right marker icon for it.
     * Add #node and marker as an entry in HashMap #markers,
     * and display the marker in Google Maps #mMap fragment.
     *
     * @param node
     */
    private void addMarker(InfoNode node) {
        LatLng pos = new LatLng(node.getLatitude(), node.getLongitude());

        switch (node.getType()) {
            case 0:
                opt_stops.position(pos)
                        .title(node.getTitle());
                markers.put(mMap.addMarker(opt_stops), node);
                return;
            case 1:
                opt_sights.position(pos)
                        .title(node.getTitle());
                markers.put(mMap.addMarker(opt_sights), node);
                return;
            case 2:
                opt_stores.position(pos)
                        .title(node.getTitle());
                markers.put(mMap.addMarker(opt_stores), node);
                return;
            case 3:
                opt_bars.position(pos)
                        .title(node.getTitle());
                markers.put(mMap.addMarker(opt_bars), node);
            default:
                return;
        }
    }

    /**
     * Switches the current visible fragment to a detail view/activity of a given InfoNode #node.
     *
     * @param node
     */
    private void openDetailView(InfoNode node) {
        DetailActivity fragment = new DetailActivity();
        Bundle args = new Bundle();
        args.putSerializable("InfoNode", node);
        fragment.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.addToBackStack("DetailView");
        fragmentTransaction.commit();
    }

    /**
     * Sorts the values from #originalValues first, then filters #sortedValues, and then display
     * #filteredValues to list adapter.
     */
    private void sortFilterShow() {
        List<InfoNode> sortedValues = MapUtils.sortByDistance(originalValues, nextStop);
        List<InfoNode> filteredValues = MapUtils.filterValues(sortedValues, categories);
        visibleValuesChanged(filteredValues);
    }

    /**
     * A listener method from implementing IValuesChangedListener.
     * Gets notified with new List<InfoNode> #values if the internal
     * database has been updated with the server database from Parse.
     * Calls sortFilterShow() to display new #values to list adapter.
     *
     * @param values
     */
    @Override
    public void originalValuesChanged(List<InfoNode> values) {
        this.originalValues = values;
        sortFilterShow();
    }

    /**
     * If #values is not null, remove all old/current markers on Google Maps, clear #markers HashMap
     * and clear the list adapter. Update visible markers on Google Maps fragment, #markers HashMap
     * and list adapter with new #values.
     *
     * @param values
     */
    private void visibleValuesChanged(List<InfoNode> values) {
        if (values != null) {
            for (Marker marker : markers.keySet()) {
                marker.remove();
            }
            markers.clear();
            for (InfoNode node : values) {
                addMarker(node);
            }
            adapter.clear();
            adapter.addAll(values);
        }
    }

    /**
     * A listener method from implementing IBusDataListener.
     * Gets notified with a new LatLng #pos from (AsyncTask) #apiHelper after executing it.
     * Adds and animates the newly added Bus marker in the Google Maps with the new position #pos,
     * if it is a valid LatLng #pos (see if() case).
     *
     * @param pos
     */
    @Override
    public void positionChanged(LatLng pos) {
        if (pos != null && pos.latitude != 0 && pos.longitude != 0) {
            busPositionOptions.position(pos).title("Buss");
            if (busMarker == null) {
                busMarker = mMap.addMarker(busPositionOptions);
            } else {
                MapUtils.animateMarker(busMarker, pos, mMap);
            }

            if (isLockedToBus)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16));
        }
    }

    /**
     * A listener method from implementing IBusDataListener.
     * Gets notified with a new String #nextStop from (AsyncTask) #apiHelper after executing it.
     * Sets the local class variable #this.nextStop to received value #nextStop, if they do not
     * have the same value and the received value #nextStop isn't an empty string ("").
     * Calls sortFilterShow() if this is the case.
     *
     * @param nextStop
     */
    @Override
    public void nextStopChanged(String nextStop) {
        if (!this.nextStop.equals(nextStop) && !nextStop.equals("")) {
            this.nextStop = nextStop;
            sortFilterShow();
        }
    }

    /**
     * Creates the custom-made list below the map and adds a listener for every list item
     * (switch fragment to/opens the detail view/activity).
     */
    private void createList() {
        belowMapList = (ListView) v.findViewById(R.id.listBelowMap);

        adapter = new ListArrayAdapter(getActivity(), originalValues);
        belowMapList.setAdapter(adapter);

        belowMapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDetailView((InfoNode) parent.getItemAtPosition(position));
            }
        });

    }

    /**
     * Easter egg method to be invoked when user taps and holds the application icon in Navigation
     * Drawer (left menu). Changes the bus marker icon and plays an audio file using MediaPlayer #mPlayer.
     *
     * @throws IOException
     */
    public void prideMode() throws IOException {
        if (busMarker != null) {
            if (!prideMode) {
                busMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_03));
                prideMode = true;
                Toast.makeText(getActivity().getApplicationContext(), "All aboard the Pride Bus!", Toast.LENGTH_SHORT).show();
                mPlayer = MediaPlayer.create(getActivity(), R.raw.pride_music);
                mPlayer.setLooping(true);
                mPlayer.seekTo(mPlayer_pos);
                mPlayer.start();
            } else {
                busMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_02));
                prideMode = false;
                Toast.makeText(getActivity().getApplicationContext(), "Pride-Mode deactivated.", Toast.LENGTH_SHORT).show();
                mPlayer.pause();
                mPlayer_pos = mPlayer.getCurrentPosition();
                mPlayer.release();
            }
        } else if (mPlayer.isPlaying()) { // To be able to stop music, if API doesn't work
            mPlayer.pause();
            mPlayer_pos = mPlayer.getCurrentPosition();
            mPlayer.release();
        }
    }

    /**
     * Switches the current visible fragment to FavoritesActivity.
     */
    public void openFavorites() {
        FavoritesActivity fragment = new FavoritesActivity();
        favoriteList.clear();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        for (InfoNode node : originalValues) {
            if (sharedPreferences.getBoolean(node.getTitle(), false)) {
                favoriteList.add(node);
            }
        }

        Bundle args = new Bundle();
        args.putSerializable("Favorites", (Serializable) favoriteList);
        fragment.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.addToBackStack("FavoritesActivity");
        fragmentTransaction.commit();
    }

}
