package com.dat255tesla.busexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class TestCallAPI extends AppCompatActivity {

    private TextView apiTxt;
    private final String userPwd = "grp42:v9aD7MvAOG";
    private String encoded;
    private APIHelper apiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_test_call_api);

        encoded = Base64.encodeToString(userPwd.getBytes(), Base64.DEFAULT); //Encode to Base64 format
        apiTxt = (TextView) findViewById(R.id.apiTxt);
        apiHelper = new APIHelper(apiTxt);

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

    public void callApi(View v){
        apiHelper.execute(encoded);
    }
}
