package com.dat255tesla.busexplorer.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dat255tesla.busexplorer.R;
import com.dat255tesla.busexplorer.aboutcontent.AboutActivity;
import com.dat255tesla.busexplorer.apirequest.CheckBusWIFI;
import com.dat255tesla.busexplorer.apirequest.IBusWifiListener;
import com.dat255tesla.busexplorer.detailcontent.DetailActivity;
import com.dat255tesla.busexplorer.explorercontent.ExplorerActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IBusWifiListener {
    private ConnectivityManager cm;
    private NetworkInfo netInfo;
    private CheckBusWIFI cbw;
    private String busSystemId = "0";
    private String dgw = "0";
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private boolean[] categories;
    private boolean mapAccess = false;
    private ExplorerActivity explorerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set (AsyncTask) CheckBusWIFI with this class as a listener (IBusWifiListener)
        cbw = new CheckBusWIFI(this);
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        categories = new boolean[3];
        loadSavedPreferences();
        initNavDrawer();
        setNavDrawerIcons();
        connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!mapAccess && item.getItemId() == R.id.refresh_button) {
            refreshClick();
        } else if (mapAccess && item.getItemId() == R.id.refresh_button) {
            openExplorer();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Close the navigation drawer, if it is open.
     * If not, check if the active fragment is of class DetailActivity.
     * If it is, we go back to the map or the favorites depending on, where we came from.
     * Otherwise we ask to exit application using an AlertDialog.
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        } else if (getSupportFragmentManager().findFragmentById(R.id.frame) instanceof DetailActivity) {
            int size = getSupportFragmentManager().getBackStackEntryCount();
            android.support.v4.app.FragmentManager.BackStackEntry bs = getSupportFragmentManager().getBackStackEntryAt(size-2);
            if (bs.getName().equals("ExplorerActivity")) {
                openExplorer();
            } else {
                openFavorites();
            }
        } else {
            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            b.setCancelable(true);
            b.setTitle("\ud83d\ude22");
            b.setMessage(getResources().getText(R.string.main_wanttoexit));
            b.setPositiveButton(getResources().getText(R.string.main_exit),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
            b.setNegativeButton(getResources().getText(R.string.main_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = b.create();
            alert.show();
        }
    }

    /**
     * Checks if there is an active WIFI or Mobile Network (3G/4G) connection, and starts the
     * ExplorerActivity if that is the case. Else, shows an AlertDialog.
     * (The application is intended to only work if there is an active WIFI connection to
     * one of the electric buses, but for demonstration purposes, we chose this temporal solution).
     */
    public void connect() {
        // Check if online
        if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
            // Check connection type
            if (netInfo.getTypeName().equals("WIFI")) {
                // This triggers with WiFI connection
                // We can check the WiFi name here, to see if it's the same name as the bus wifi
                /*
                 * new CheckBusWIFI(this).execute();
                 * TODO: Enable when (testing) in an electric bus.
                 * Disable rest of the code in this if() case below.
                 * This is how the application is intended to work
                 * when using it without any simulations/testing.
                 */

                // We use this to demonstrate on a simulated bus with corresponding dgw.
                // We could also change this dgw value and hard code a real bus.
                dgw = "Vin_Num_001";
                mapAccess = true;
                openExplorer();

            } else { // Also for testing purposes. Disable this else-case when using on an electric bus.
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setCancelable(true);
                // This triggers with mobile & no WiFi
                b.setTitle(getResources().getString(R.string.main_title_noWifi)
                        + " \uD83D\uDE0F");
                b.setMessage(getResources().getString(R.string.main_text_noWifi));

                b.setPositiveButton(getResources().getString(R.string.main_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dgw = "Vin_Num_001";
                                mapAccess = true;
                                openExplorer();
                            }
                        });
                b.setNegativeButton(getResources().getString(R.string.app_settings),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.openWiFiSettings();
                            }
                        });
                AlertDialog alert = b.create();
                alert.show();
            }
        } else {
            // This triggers with no internet connection
            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            b.setCancelable(true);
            b.setTitle(getResources().getString(R.string.main_title_noConn)
                    + " \uD83D\uDE1E");
            b.setMessage(getResources().getString(R.string.main_text_noConn));

            b.setPositiveButton(getResources().getString(R.string.main_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            b.setNegativeButton(getResources().getString(R.string.main_exit),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
            AlertDialog alert = b.create();
            alert.show();
        }

    }

    /**
     * Initializes the navigation drawer (left side menu) and its list items with listeners.
     */
    private void initNavDrawer() {
        // Easter egg when tapping and holding the icon. Try it!
        findViewById(R.id.app_image).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    explorerActivity.prideMode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Allow natural icon colors
        navigationView.setItemIconTintList(null);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.navdrawer_map:
                        if (mapAccess) {
                            openExplorer();
                        } else {
                            refreshClick();
                        }
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.navdrawer_favorites:
                        openFavorites();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.navdrawer_category_1:
                        menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), !categories[0] ? R.drawable.marker_triangle_fill : R.drawable.marker_triangle_nofill));
                        categories[0] = !categories[0];
                        savePreferences("CheckBox_sightseeing", categories[0]);
                        if (getSupportFragmentManager().findFragmentById(R.id.frame) instanceof ExplorerActivity) {
                            explorerActivity.onResume();
                        }
                        return true;
                    case R.id.navdrawer_category_2:
                        menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), !categories[1] ? R.drawable.marker_square_fill : R.drawable.marker_square_nofill));
                        categories[1] = !categories[1];
                        savePreferences("CheckBox_shopping", categories[1]);
                        if (getSupportFragmentManager().findFragmentById(R.id.frame) instanceof ExplorerActivity) {
                            explorerActivity.onResume();
                        }
                        return true;
                    case R.id.navdrawer_category_3:
                        menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), !categories[2] ? R.drawable.marker_circle_fill : R.drawable.marker_circle_nofill));
                        categories[2] = !categories[2];
                        savePreferences("CheckBox_bars", categories[2]);
                        if (getSupportFragmentManager().findFragmentById(R.id.frame) instanceof ExplorerActivity) {
                            explorerActivity.onResume();
                        }
                        return true;
                    case R.id.navdrawer_about:
                        openAbout();
                        drawerLayout.closeDrawers();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "SOMETHING.IS.WRONG.", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your icon wont show up
        actionBarDrawerToggle.syncState();

    }


    private void setNavDrawerIcons() {
        MenuItem menuItem1 = navigationView.getMenu().findItem(R.id.navdrawer_category_1);
        if (categories[0]) {
            menuItem1.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_triangle_fill));
        } else {
            menuItem1.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_triangle_nofill));
        }

        MenuItem menuItem2 = navigationView.getMenu().findItem(R.id.navdrawer_category_2);
        if (categories[1]) {
            menuItem2.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_square_fill));
        } else {
            menuItem2.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_square_nofill));
        }

        MenuItem menuItem3 = navigationView.getMenu().findItem(R.id.navdrawer_category_3);
        if (categories[2]) {
            menuItem3.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_circle_fill));
        } else {
            menuItem3.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_circle_nofill));
        }
    }

    /**
     * Load the values of categories to be filtered and save them in the categories array
     */
    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        categories[0] = sharedPreferences.getBoolean("CheckBox_sightseeing", true);
        categories[1] = sharedPreferences.getBoolean("CheckBox_shopping", true);
        categories[2] = sharedPreferences.getBoolean("CheckBox_bars", true);
    }

    /**
     * Save a key-value pair to SharedPreferences (settings)
     */
    private void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Switches the current visible fragment to AboutActivity.
     */
    private void openAbout() {
        AboutActivity fragment = new AboutActivity();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "AboutActivity");
        fragmentTransaction.addToBackStack("AboutActivity");
        fragmentTransaction.commit();
    }

    /**
     * Switches the current visible fragment to ExplorerActivity.
     */
    private void openExplorer() {
        explorerActivity = new ExplorerActivity(); //Use this to notify settings change instantly
        Bundle args = new Bundle();
        args.putString("dgw", dgw);
        explorerActivity.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, explorerActivity, "ExplorerActivity");
        fragmentTransaction.addToBackStack("ExplorerActivity");
        fragmentTransaction.commit();
    }

    /**
     * Switches the current visible fragment to FavoritesActivity.
     */
    public void openFavorites() {
        explorerActivity.openFavorites();
    }

    /**
     * Checks if there's a new valid connection to be able to start ExplorerActivity.
     */
    private void refreshClick() {
        netInfo = cm.getActiveNetworkInfo();
        connect();
    }

    /**
     * Open the settings for WIFI connection on the device.
     */
    public void openWiFiSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    /**
     * Retrieves Bus-data from the server (Parse) and checks if the given #systemId of the bus
     * is valid (i.e. if the #systemId matches any bus on the server database).
     * If that is the case; retrieve and save the #dgw string of the bus (with corresponding systemId)
     * and call openExplorer(). Else, deny authentication to start ExplorerActivity and display a Toast.
     * (This method is used to get the right dgw of the current bus you're in. dgw of the bus is needed
     * to know which bus we want to get data from when calling the API in the APIHelper class (AsyncTask)).
     *
     * @param systemId
     */
    private void getDgwFromBus(final String systemId) {
        ParseQuery.getQuery("Bus").findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                boolean getAccess = false;
                if (e == null) {
                    for (ParseObject object : list) {
                        if (systemId.equals(object.getString("systemID"))) {
                            dgw = object.getNumber("dgw").toString();
                            getAccess = true;
                            break;
                        }
                    }
                    if (getAccess) {
                        //Start the ExplorerActivity
                        openExplorer();
                    } else {
                        Toast.makeText(getApplicationContext(), "You're in a bus and connected" +
                                " to its wifi, but this bus doesn't" +
                                " share data :(", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to connect to " +
                            "the server database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * A listener method from implementing IBusWifiListener.
     * Gets notified with a String #systemId after executing (AsyncTask) CheckBusWIFI.
     * Saves the value to #this.busSystemId if the retrieved value #systemId is not "0",
     * and calls getDgwFromBus(#systemId). Else, displays a Toast with error message.
     *
     * @param systemId
     */
    @Override
    public void notifySystemId(String systemId) {
        if (!systemId.equals("0")) {
            busSystemId = systemId;
            System.out.println("*******SystemId of Bus: " + systemId);
            // Check if the Bus is active and get its dgw to know
            // which "API" to call (this data is on the server database).
            getDgwFromBus(systemId);
        } else {
            Toast.makeText(getApplicationContext(), "ERROR: You're either not connected " +
                    "to the bus wifi or on the wrong bus!", Toast.LENGTH_SHORT).show();
        }
    }

}
