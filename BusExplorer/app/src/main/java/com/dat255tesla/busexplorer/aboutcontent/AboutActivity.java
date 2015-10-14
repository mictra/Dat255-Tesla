package com.dat255tesla.busexplorer.aboutcontent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dat255tesla.busexplorer.R;
import com.dat255tesla.busexplorer.apirequest.CheckBusWifi;
import com.dat255tesla.busexplorer.apirequest.IBusWifiListener;

public class AboutActivity extends AppCompatActivity implements IBusWifiListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        CheckBusWifi c = new CheckBusWifi(this);
        c.execute();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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

    @Override
    public void notifySystemId(String systemId) {
        // If systemId is 0, we can notify an error! (in this case it will be)
        Toast.makeText(getApplicationContext(), systemId, Toast.LENGTH_SHORT).show();
    }
}
