package com.dat255tesla.busexplorer.explorercontent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dat255tesla.busexplorer.R;
import com.dat255tesla.busexplorer.aboutcontent.AboutActivity;
import com.dat255tesla.busexplorer.apirequest.APIHelper;
import com.dat255tesla.busexplorer.apirequest.IBusDataListener;
import com.dat255tesla.busexplorer.database.IValuesChangedListener;
import com.dat255tesla.busexplorer.database.InfoDataSource;
import com.dat255tesla.busexplorer.database.InfoNode;
import com.dat255tesla.busexplorer.detailcontent.DetailActivity;
import com.dat255tesla.busexplorer.settingscontent.SettingsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class ExplorerActivity extends Fragment implements IValuesChangedListener, IBusDataListener {
    private GoogleMap mMap;
    private APIHelper apiHelper;
    private HashMap<Marker, InfoNode> markers;
    private InfoDataSource ds;
    private List<InfoNode> originalValues;
    private Marker busMarker;
    private ListArrayAdapter adapter;
    private String nextStop;
    private boolean isLockedToBus = true;

    private MarkerOptions opt_stops;
    private MarkerOptions opt_sights;
    private MarkerOptions opt_stores;
    private MarkerOptions opt_misc;

    private MarkerOptions busPositionOptions;

    private ListView belowMapList;

    // Using an boolean to check, instead of checking its visibility or a status.
    private boolean isListOpen = false;
    private boolean isFavorite = false;

    // our empty arrayList
    private List<String> favoriteList;

    private String dgw;
    private boolean checkBoxValue_sight;
    private boolean checkBoxValue_shop;
    private boolean checkBoxValue_misc;
    private boolean[] typeFilters;

    private View v;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dgw = getArguments().getString("dgw");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_explorer,container,false);

        apiHelper = new APIHelper(this, dgw);
        markers = new HashMap<>();
        nextStop = "";

        // Retrieve settings data
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        checkBoxValue_sight = sharedPreferences.getBoolean("CheckBox_sightseeing", true);
        checkBoxValue_shop = sharedPreferences.getBoolean("CheckBox_shopping", true);
        checkBoxValue_misc = sharedPreferences.getBoolean("CheckBox_misc", true);

        typeFilters = new boolean[]{checkBoxValue_sight, checkBoxValue_shop, checkBoxValue_misc};

        // Establish database connection
        ds = new InfoDataSource(getActivity());
        ds.setValuesChangedListener(this);

        try {
            ds.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        opt_stops = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_stop));
        opt_sights = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_triangle));
        opt_stores = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_square));
        opt_misc = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_circle));

        busPositionOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_02))
                .anchor(0.5f, 0.5f);
        setUpMapIfNeeded();
        ds.updateDatabaseIfNeeded();

        createList();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        checkBoxValue_sight = sharedPreferences.getBoolean("CheckBox_sightseeing", true);
        checkBoxValue_shop = sharedPreferences.getBoolean("CheckBox_shopping", true);
        checkBoxValue_misc = sharedPreferences.getBoolean("CheckBox_misc", true);
        System.out.println("-----------------1: " + checkBoxValue_sight + " 2: " + checkBoxValue_shop + " 3: " + checkBoxValue_misc);
        typeFilters = new boolean[]{checkBoxValue_sight, checkBoxValue_shop, checkBoxValue_misc};
        //setUpMapIfNeeded();
        if (!nextStop.equals("")) {
            visibleValuesChanged(MapUtils.
                    filterValues(MapUtils.
                            sortByDistance(originalValues, nextStop), typeFilters));
        } else {
            visibleValuesChanged(MapUtils.
                    filterValues(originalValues, typeFilters));
        }
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
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Add all markers from the internal database
        originalValues = ds.getAllInfoNodes();

        List<InfoNode> filteredValues = MapUtils.filterValues(originalValues, typeFilters);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle().equals("Buss"))
                    isLockedToBus = true;

                if (marker.getTitle().equals("Regnbågsgatan")) {
                    busMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_03));
                }

                showPopup(getActivity());

                return false;
            }
        });

        // Start location
        LatLng startloc = new LatLng(57.704874, 11.965345);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startloc, 8));
        apiHelper.execute();
    }

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
                opt_misc.position(pos)
                        .title(node.getTitle());
                markers.put(mMap.addMarker(opt_misc), node);
            default:
                return;
        }
    }


    /**
     * Open the DetailActivity
     * This is just a temporary method. Will be moved to ListView-listener once available.
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
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showPopup(final Activity context) {
        LinearLayout viewGroup = (LinearLayout) v.findViewById(R.id.popup_element);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_layout, viewGroup);

        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setFocusable(true);
        popup.setWidth(800);
        popup.setHeight(900);
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);

        Button close = (Button) layout.findViewById(R.id.b_ClosePopup);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    @Override
    public void originalValuesChanged(List<InfoNode> values) {
        this.originalValues = values;
    }

    public void visibleValuesChanged(List<InfoNode> values) {
        if (values != null) {
            for (Marker marker : markers.keySet()) {
                marker.remove();
            }
            markers.clear();
            for (InfoNode node : values) {
                addMarker(node);
            }
            adapter.clear();
            adapter.addAll(values); // This mutates this.originalValues variable.
        }
    }

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

    @Override
    public void nextStopChanged(String nextStop) {
        if (!this.nextStop.equals(nextStop) && !nextStop.equals("")) {
            List<InfoNode> sortedValues = MapUtils.sortByDistance(originalValues, nextStop); //TODO: Call this in separate AsyncTask?
            List<InfoNode> filteredValues = MapUtils.filterValues(sortedValues, typeFilters);
            visibleValuesChanged(filteredValues);
            this.nextStop = nextStop;
            Toast.makeText(getActivity().getApplicationContext(), "nextStop changed, list should be sorted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createList(){
        belowMapList = (ListView) v.findViewById(R.id.listBelowMap);

//        // Temp-list below map
//        ArrayList<String> sites = new ArrayList<>(
//                Arrays.asList("Poseidon", "Zeus", "Hades", "Demeter", "Ares", "Athena", "Apollo"));

//        ArrayList<InfoNode> valuesClone = new ArrayList<>();
//        for(int i = 0; i < sites.size(); i++){
//            InfoNode node = new InfoNode(0, sites.get(0), 0.0, 0.0, 1, "info", "Adr", 0, "objID");
//            valuesClone.add(node);
//        }

        ArrayList<String> sites = new ArrayList<>();
        for (InfoNode node : originalValues) {
            sites.add(node.getTitle());
        }

//        belowMapList.setAdapter(new ArrayAdapter<>(
//                this, R.layout.maplist_layout,
//                R.id.listString, ourSites));

//        origAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList());
        adapter = new ListArrayAdapter(getActivity(), sites, originalValues);

        belowMapList.setAdapter(new ListArrayAdapter(getActivity(), sites, originalValues));

        belowMapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(),
                        parent.getItemAtPosition(position) + " clicked", Toast.LENGTH_SHORT)
                        .show();
            }
        });

//        // List is hidden by default.
//        setListVisibility(false);

        // Button to open and close list.
        final Button listButton = (Button) v.findViewById(R.id.openListButton);
        final View.OnClickListener openListListener = new View.OnClickListener() {
            @Override
            public void onClick(View v){

                String listStatus = (isListOpen) ? "Open List" : "Close List";
                listButton.setText(listStatus);
                setListVisibility(!isListOpen);
                isListOpen = !isListOpen;

            }
        };
        listButton.setOnClickListener(openListListener);
    }

    public void favoriteClickHandle(View v) {
        ImageView favButt = (ImageView)v;

        favoriteList = new ArrayList<>();

        if(isFavorite){
            favButt.setImageResource(R.drawable.star_unfilled);

        } else {
            favButt.setImageResource(R.drawable.star_filled);
        }

        isFavorite = !isFavorite;
    }

    private void setListVisibility(boolean isVisible) {
        getListView().setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private ListView getListView() {
        return belowMapList;
    }

    /*
        Method to read/write to an favorite.txt, which will be used to be excluded when our
        "listBelowMap is being refreshed and removing things to far away, then favorites will still be shown.
     */
//    private void updateFavList(){
//        favoriteList = new ArrayList<>();
//
//        Scanner sc = null;
//        try {
//            sc = new Scanner(new BufferedReader(new FileReader("favorites.txt")));
//
//            while (sc.hasNext()) {
//                favoriteList.add(sc.nextLine());
//            }
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        finally {
//            if(sc != null) {
//                sc.close();
//            }
//        }
//    }
}