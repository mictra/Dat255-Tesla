package com.dat255tesla.busexplorer.explorercontent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.dat255tesla.busexplorer.apirequest.APIHelper;
import com.dat255tesla.busexplorer.aboutcontent.AboutActivity;
import com.dat255tesla.busexplorer.detailcontent.DetailActivity;
import com.dat255tesla.busexplorer.apirequest.IAPIListener;
import com.dat255tesla.busexplorer.database.IValuesChangedListener;
import com.dat255tesla.busexplorer.database.InfoDataSource;
import com.dat255tesla.busexplorer.database.InfoNode;
import com.dat255tesla.busexplorer.R;
import com.dat255tesla.busexplorer.settingscontent.SettingsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class ExplorerActivity extends AppCompatActivity implements IValuesChangedListener, IAPIListener {
    private GoogleMap mMap;
    private APIHelper apiHelper;
    private HashMap<Marker, InfoNode> markers;
    private InfoDataSource ds;
    private List<InfoNode> values;
    private Marker busMarker;
    private ArrayAdapter<InfoNode> adapter;
    private String nextStop;

    private MarkerOptions opt_sights;
    private MarkerOptions opt_stores;
    private MarkerOptions opt_misc;

    private MarkerOptions busPositionOptions;

    // examples
    private Location pretendLocation;
    private ListView belowMapList;

    // Using an boolean to check, instead of checking its visibility or a status.
    private boolean isListOpen = false;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        apiHelper = new APIHelper(this);
        pretendLocation = new Location("pretend");
        // (57.707373, 11.973864) - "Nara" (<2km) goteborgsmarkaren
        pretendLocation.setLatitude(57.707373);
        pretendLocation.setLongitude(11.973864);
        markers = new HashMap<>();
        nextStop = "";

        // Establish database connection
        ds = new InfoDataSource(this);
        ds.setValuesChangedListener(this);

        try {
            ds.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        MapList mapList = new MapList();
//        mapList.createList();
        createList();

//        // Temp-list below map
//        String[] sites = {"Poseidon", "Zeus", "Hades", "Demeter", "Ares", "Athena", "Apollo"};
//        belowMapList = (ListView) findViewById(R.id.listBelowMap);
//        belowMapList.setAdapter(new ArrayAdapter<>(
//                this, R.layout.maplist_layout,
//                R.id.listIcon, sites));
//        belowMapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        parent.getItemAtPosition(position) + " clicked", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
//
//        // List is hidden by default.
//        setListVisibility(false);
//
//        // Button to open and close list.
//        final Button listButton = (Button) findViewById(R.id.openListButton);
//        final View.OnClickListener openListListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//
//                String listStatus = (isListOpen) ? "Open List" : "Close List";
//                listButton.setText(listStatus);
//                setListVisibility(!isListOpen);
//                isListOpen = !isListOpen;
//
//            }
//        };
//        listButton.setOnClickListener(openListListener);
//
////        // Button to favorite an list-object.
////        final ImageButton favButton = (ImageButton) findViewById(R.id.favButton);
////        final View.OnClickListener favItemListener = new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////                if(isFavorite){
////                    v.setBackgroundResource(R.drawable.poseidon2_thumb);
////
////                } else {
////                    v.setBackgroundResource(R.drawable.poseidon3_thumb);
////                }
////
////                isFavorite = !isFavorite;
////            }
////        };
////        favButton.setOnClickListener(favItemListener);
//
////        favButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////                if(isFavorite){
////                    v.setBackgroundResource(R.drawable.poseidon2_thumb);
////
////                } else {
////                    v.setBackgroundResource(R.drawable.poseidon3_thumb);
////                }
////
////                isFavorite = !isFavorite;
////            }
////        });

//        opt_sights = new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_triangle));
//        opt_stores = new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_square));
//        opt_misc = new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_circle));
//
//        busPositionOptions = new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_02))
//                .anchor(0.5f, 0.5f);
        setUpMapIfNeeded();
        ds.updateDatabaseIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (apiHelper.isCancelled()) {
            apiHelper = new APIHelper(this);
            apiHelper.execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        apiHelper.cancel(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        apiHelper.cancel(true);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_explorer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_about:
                openAbout();
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));

        // Add all markers from the internal database
        values = ds.getAllInfoNodes();

        for (InfoNode node : values) {
            addMarker(node);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();

                showPopup(ExplorerActivity.this);

                return false;
            }
        });
        // List is hidden by default.
        setListVisibility(false);
        apiHelper.execute();
        //nextStopChanged("Poseidon"); //TODO: Enable to see result in terminal.
    }

    private void addMarker(InfoNode node) {
        LatLng pos = new LatLng(node.getLatitude(), node.getLongitude());

        switch (node.getType()) {
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

    public void openAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Open the SettingsActivity
     */
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Open the DetailActivity
     * This is just a temporary method. Will be moved to ListView-listener once available.
     *
     * @param node
     */

    private void openDetailView(InfoNode node) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("InfoNode", node);
        startActivity(intent);
    }

    private void showPopup(final Activity context) {
        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.popup_element);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_layout, viewGroup);

        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setFocusable(true);
        popup.setWidth(800);
        popup.setHeight(900);
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);

        System.out.println("popup should show!");
        System.out.println("popup should show!");
        System.out.println("popup should show!");
        System.out.println("popup should show!");
        System.out.println("popup should show!");
        System.out.println("popup should show!");

        Button close = (Button) layout.findViewById(R.id.b_ClosePopup);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    @Override
    public void valuesChanged(List<InfoNode> values) {
        // Add all markers from the internal database
        this.values = values;
        markers.clear();
        for (InfoNode node : values) {
            addMarker(node);
        }
        adapter.clear();
        adapter.addAll(values);
    }

    @Override
    public void positionChanged(LatLng pos) {
        if (pos.latitude != 0 && pos.longitude != 0) { // Ugly solution for now
            busPositionOptions.position(pos).title("SIMULATED BUS MOVING!");
            if (busMarker == null) {
                busMarker = mMap.addMarker(busPositionOptions);
            } else {
                MapUtils.animateMarker(busMarker, pos, mMap);
                //busMarker.setPosition(pos);
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16));
        }
    }

    @Override
    public void nextStopChanged(String nextStop) {
        if(!this.nextStop.equals(nextStop)){
            List<InfoNode> sortedValues = MapUtils.sortByDistance(values, nextStop);
            //valuesChanged(sortedValues); //TODO: Enable later when we get real values
            this.nextStop = nextStop;
            Toast.makeText(getApplicationContext(), "nextStop changed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createList(){

        // Temp-list below map
        String[] sites = {"Poseidon", "Zeus", "Hades", "Demeter", "Ares", "Athena", "Apollo"};
        belowMapList = (ListView) findViewById(R.id.listBelowMap);
        belowMapList.setAdapter(new ArrayAdapter<>(
                this, R.layout.maplist_layout,
                R.id.listIcon, sites));
        belowMapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        parent.getItemAtPosition(position) + " clicked", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // List is hidden by default.
        setListVisibility(false);

        // Button to open and close list.
        final Button listButton = (Button) findViewById(R.id.openListButton);
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

        View inflatedView = getLayoutInflater().inflate(R.layout.maplist_layout, null);

        // Button to favorite an list-object.
        final ImageButton favButton = (ImageButton) inflatedView.findViewById(R.id.favButton);
        final View.OnClickListener favItemListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Test....................................................................");
                if(isFavorite){
                    v.setBackgroundResource(R.drawable.poseidon2_thumb);

                } else {
                    v.setBackgroundResource(R.drawable.poseidon3_thumb);
                }

                isFavorite = !isFavorite;
            }
        };
        favButton.setOnClickListener(favItemListener);

//        favButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(isFavorite){
//                    v.setBackgroundResource(R.drawable.poseidon2_thumb);
//
//                } else {
//                    v.setBackgroundResource(R.drawable.poseidon3_thumb);
//                }
//
//                isFavorite = !isFavorite;
//            }
//        });
    }

    public void favoriteClickHandle(View v) {
        System.out.println("Bengtttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt");

        if(isFavorite){
            v.setBackgroundResource(R.drawable.poseidon2_thumb);

        } else {
            v.setBackgroundResource(R.drawable.poseidon3_thumb);
        }

        isFavorite = !isFavorite;
    }

    private void setListVisibility(boolean isVisible) {
        getListView().setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private ListView getListView() {
        return belowMapList;
    }
}