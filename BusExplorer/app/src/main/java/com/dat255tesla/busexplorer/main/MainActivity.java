package com.dat255tesla.busexplorer.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.dat255tesla.busexplorer.settingscontent.SettingsActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IBusWifiListener {
    ConnectivityManager cm;
    NetworkInfo netInfo;
    private CheckBusWIFI cbw;
    private String busSystemId = "0";
    private String dgw = "0";
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private boolean[] categories;

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
        categories[0] = true;

        connectionDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionDialog();
    }

    @Override
    public void onBackPressed() {
        // Check, if active fragment is of class DetailActivity
        // If it is, we go back to the map. Otherwise we ask to exit.
//        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame);
//        if (f instanceof DetailActivity) {
//            openExplorerActivity();
//        } else {
            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            b.setCancelable(true);
            b.setTitle("\ud83d\ude22 \uD83D\uDE22 \uD83D\uDE22 \uD83D\uDE22");
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
//        }

    }

    /**
     * Please rename me.
     */
    public void connectionDialog() {
 /*       // Check if online
        if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
            // Check connection type
            if (netInfo.getTypeName().equals("WIFI")) {
                // This triggers with WiFI connection
                // We can check the WiFi name here, to see if it's the same name as the bus wifi
                // If we care. netInfo.getExtraInfo() will return the WiFi name
                //cbw.execute(); // Call the AsyncTask and get the system id of the bus.
                //new CheckBusWIFI(this).execute(); // TODO: Enable when in a bus or testing...

                dgw = "Vin_Num_001";
                getAccess();

            } else {
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
        }*/

        initNavDrawer();
    }

    private void initNavDrawer() {
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
                switch (menuItem.getItemId()){
                    case R.id.navdrawer_map:
                        openExplorerActivity();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.navdrawer_category_1:
                        if (!categories[0]) {
                            categories[0] = true;
                            menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_triangle_fill));
                        } else {
                            categories[0] = false;
                            menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_triangle_nofill));
                        }
                        return true;
                    case R.id.navdrawer_category_2:
                        if (!categories[1]) {
                            categories[1] = true;
                            menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_square_fill));
                        } else {
                            categories[1] = false;
                            menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_square_nofill));
                        }
                        return true;
                    case R.id.navdrawer_category_3:
                        if (!categories[2]) {
                            categories[2] = true;
                            menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_circle_fill));
                        } else {
                            categories[2] = false;
                            menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_circle_nofill));
                        }
                        return true;
                    case R.id.navdrawer_settings:
                        openSettings();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.navdrawer_about:
                        openAbout();
                        drawerLayout.closeDrawers();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"SOMETHING.IS.WRONG.",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

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

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        //Start the next activity
        //openExplorerActivity();
        openAbout();

    }

    private void openAbout() {
        AboutActivity fragment = new AboutActivity();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openSettings() {
//        SettingsActivity fragment = new SettingsActivity();
//        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.frame, fragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
    }

    private void openExplorerActivity() {
//        ExplorerActivity fragment = new ExplorerActivity();
//        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.frame, fragment, "ExplorerActivity");
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
    }

    public void refreshClick(View view) {
        netInfo = cm.getActiveNetworkInfo();
        connectionDialog();
        /*
        if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
            TextView text = (TextView) findViewById(R.id.tv_refreshText);
            text.setText(getResources().getText(R.string.main_refreshYesConn));

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.openExplorer();
                }
            }, 200);
        }
        */
    }

    public void openWiFiSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    public void openExplorer() {
        Intent intent = new Intent(this, ExplorerActivity.class);
        intent.putExtra("dgw", dgw);
        startActivity(intent);
        finish();
    }


    private void getAccess() {
        System.out.println(netInfo.getTypeName());
        System.out.println(netInfo.getExtraInfo());
        MainActivity.this.openExplorer();
    }

    private void getDgwFromBus(final String systemId) {
        ParseQuery.getQuery("Bus").findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                boolean getAccess = false;
                if (e == null) {
                    for (ParseObject object : list) {
                        if (systemId.equals(object.getString("systemID")) &&
                                object.getBoolean("isActive")) {
                            dgw = object.getNumber("dgw").toString();
                            getAccess = true;
                            break;
                        }
                    }
                    if (getAccess) {
                        getAccess();
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
            getDgwFromBus(systemId);
            // Check if the Bus is active and get its dgw to know
            // which "API" to call (this data is on the server database).
        } else {
            Toast.makeText(getApplicationContext(), "ERROR: You're either not connected " +
                    "to the bus wifi or on the wrong bus!", Toast.LENGTH_SHORT).show();
        }
    }

}
