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

import com.dat255tesla.busexplorer.R;
import com.dat255tesla.busexplorer.explorercontent.ExplorerActivity;

public class MainActivity extends AppCompatActivity {
    ConnectivityManager cm;
    NetworkInfo netInfo;
    static boolean alertSemaphore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        doStuff();
    }

    @Override
    protected void onResume() {
        super.onResume();

        doStuff();
    }

    /**
     * Please rename me.
     */
    public void doStuff() {
        if(!alertSemaphore) {
            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            b.setCancelable(true);
            alertSemaphore = true;

            System.out.println("GOT THIS FAR 1");

            // Check if online
            if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
                // Check connection type
                if (netInfo.getTypeName().equals("WIFI")) {
                    // This triggers with WiFI connection
                    // We can check the WiFi name here, to see if it's the same name as the bus wifi
                    // If we care. netInfo.getExtraInfo() will return the WiFi name
                    MainActivity.this.openExplorer();
                    System.out.println(netInfo.getTypeName());
                    System.out.println(netInfo.getExtraInfo());
                } else {
                    System.out.println("GOT THIS FAR 3");
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
                System.out.println("GOT THIS FAR 4");
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

            System.out.println("GOT THIS FAR 5");

            AlertDialog alert = b.create();
            alert.show();
        }
    }

    public void refreshClick(View view) {
        netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
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
    }

    public void openWiFiSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    public void openExplorer() {
        Intent intent = new Intent(this, ExplorerActivity.class);
        startActivity(intent);
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
}
