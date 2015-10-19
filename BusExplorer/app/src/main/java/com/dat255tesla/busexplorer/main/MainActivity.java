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
        //connect();
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

    @Override
    public void onBackPressed() {
        // Check, if active fragment is of class DetailActivity
        // If it is, we go back to the map. Otherwise we ask to exit.

        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        } else if (getSupportFragmentManager().findFragmentById(R.id.frame) instanceof DetailActivity) {
            openExplorer();
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

    public void connect() {
        // Check if online
        if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
            // Check connection type
            if (netInfo.getTypeName().equals("WIFI")) {
                // This triggers with WiFI connection
                // We can check the WiFi name here, to see if it's the same name as the bus wifi
                // If we care. netInfo.getExtraInfo() will return the WiFi name
                //new CheckBusWIFI(this).execute(); // TODO: Enable when in a bus or testing...

                dgw = "Vin_Num_001";
                mapAccess = true;
                openExplorer();

            } else { // TODO: Ask; Should only work with WIFI since we restrict the application to only work while in a bus?
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setCancelable(true);
                // This triggers with mobile & no WiFi
                b.setTitle(getResources().getString(R.string.main_title_noWifi)
                        + " \uD83D\uDE0F");
                b.setMessage(getResources().getString(R.string.main_text_noWifi));

                b.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.openExplorer();
                            }
                        });
                b.setNegativeButton("Settings",
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
            b.setMessage(getResources().getString(R.string.main_text_noConn)
                    + " " + getResources().getString(R.string.app_name));

            b.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            b.setNegativeButton("Exit",
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

    private void initNavDrawer() {
        findViewById(R.id.app_image).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                explorerActivity.prideMode();
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

                //Checking if the item is in checked state or not, if not make it in checked state
                //menuItem.setChecked(menuItem.isChecked() ? false : true);

                //Closing drawer on item click
                //drawerLayout.closeDrawers();

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
                    case R.id.navdrawer_category_1:
                        menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), !categories[0] ? R.drawable.marker_triangle_fill : R.drawable.marker_triangle_nofill));
                        categories[0] = categories[0] ? false : true;
                        savePreferences("CheckBox_sightseeing", categories[0]);
                        if (getSupportFragmentManager().findFragmentById(R.id.frame) instanceof ExplorerActivity) {
                            explorerActivity.onResume();
                        }
                        return true;
                    case R.id.navdrawer_category_2:
                        menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), !categories[1] ? R.drawable.marker_square_fill : R.drawable.marker_square_nofill));
                        categories[1] = categories[1] ? false : true;
                        savePreferences("CheckBox_shopping", categories[1]);
                        if (getSupportFragmentManager().findFragmentById(R.id.frame) instanceof ExplorerActivity) {
                            explorerActivity.onResume();
                        }
                        return true;
                    case R.id.navdrawer_category_3:
                        menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), !categories[2] ? R.drawable.marker_circle_fill : R.drawable.marker_circle_nofill));
                        categories[2] = categories[2] ? false : true;
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

    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        categories[0] = sharedPreferences.getBoolean("CheckBox_sightseeing", true);
        categories[1] = sharedPreferences.getBoolean("CheckBox_shopping", true);
        categories[2] = sharedPreferences.getBoolean("CheckBox_bars", true);
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void openAbout() {
        AboutActivity fragment = new AboutActivity();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "AboutActivity");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openExplorer() {
        explorerActivity = new ExplorerActivity(); //Use this to notify settings change instantly
        Bundle args = new Bundle();
        args.putString("dgw", dgw);
        explorerActivity.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, explorerActivity, "ExplorerActivity");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void refreshClick() {
        netInfo = cm.getActiveNetworkInfo();
        connect();
    }

    public void openWiFiSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

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

    @Override
    public void notifySystemId(String systemId) {
        if (!systemId.equals("0")) {
            busSystemId = systemId;
            System.out.println("*******SystemId of Bus: " + systemId);
            Toast.makeText(getApplicationContext(), "System ID: " + systemId, Toast.LENGTH_SHORT).show();
            getDgwFromBus(systemId);
            // Check if the Bus is active and get its dgw to know
            // which "API" to call (this data is on the server database).
        } else {
            Toast.makeText(getApplicationContext(), "ERROR: You're either not connected " +
                    "to the bus wifi or on the wrong bus!", Toast.LENGTH_SHORT).show();
        }
    }

}
