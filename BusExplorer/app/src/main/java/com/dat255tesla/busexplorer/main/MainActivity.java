package com.dat255tesla.busexplorer.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dat255tesla.busexplorer.R;
import com.dat255tesla.busexplorer.apirequest.CheckBusWifi;
import com.dat255tesla.busexplorer.apirequest.IBusWifiListener;
import com.dat255tesla.busexplorer.explorercontent.ExplorerActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IBusWifiListener {
    ConnectivityManager cm;
    NetworkInfo netInfo;
    static boolean alertSemaphore = false;
    private CheckBusWifi cbw;
    private String busSystemId = "0";
    private String dgw = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cbw = new CheckBusWifi(this);
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionDialog();
    }

    /**
     * Please rename me.
     */
    public void connectionDialog() {
        if (!alertSemaphore) {
            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            b.setCancelable(true);
            alertSemaphore = true;

            // Check if online
            if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
                // Check connection type
                if (netInfo.getTypeName().equals("WIFI")) {
                    // This triggers with WiFI connection
                    // We can check the WiFi name here, to see if it's the same name as the bus wifi
                    // If we care. netInfo.getExtraInfo() will return the WiFi name
                    //cbw.execute(); // Call the AsyncTask and get the system id of the bus.
                    //new CheckBusWifi(this).execute(); // TODO: Enable when in a bus or testing...

                    dgw = "Vin_Num_001";
                    getAccess();

                } else {
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
                                    alertSemaphore = false;
                                }
                            });
                }
            } else {
                // This triggers with no internet connection
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
            }
            alertSemaphore = false;
            AlertDialog alert = b.create();
            alert.show();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getAccess() {
        MainActivity.this.openExplorer();
        System.out.println(netInfo.getTypeName());
        System.out.println(netInfo.getExtraInfo());
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
