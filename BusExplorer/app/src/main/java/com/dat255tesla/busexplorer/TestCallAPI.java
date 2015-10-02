package com.dat255tesla.busexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class TestCallAPI extends AppCompatActivity implements IPositionChangedListener {

    private Button callButton;
    private TextView apiTxt;
    private APIHelper apiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_test_call_api);
        apiTxt = (TextView) findViewById(R.id.apiTxt);
        apiHelper = new APIHelper(this);
        callButton = (Button) findViewById(R.id.callAPIBtn);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_call_api, menu);
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

    public void callApi(View v) {
        callButton.setEnabled(false); // Invoke only once, else exception
        apiHelper.execute();
    }

    @Override
    public void onBackPressed() {
        apiHelper.setUpdate(false);
        super.onBackPressed();
        finish();
    }

    @Override
    public void positionChanged(LatLng pos) {
        apiTxt.setText("\nLatitude: " + pos.latitude + "\nLongitude: " + pos.longitude);
    }

}
