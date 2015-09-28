package com.dat255tesla.busexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DeveloperActivity extends AppCompatActivity {
    private InfoDataSource ds;
    private ListView list;

    private EditText et_title;
    private EditText et_lat;
    private EditText et_lng;
    private EditText et_type;
    private EditText et_info;
    private EditText et_addr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        // Find all views
        et_title    = (EditText) findViewById(R.id.et_Title);
        et_lat      = (EditText) findViewById(R.id.et_Latitude);
        et_lng      = (EditText) findViewById(R.id.et_Longitude);
        et_type     = (EditText) findViewById(R.id.et_Type);
        et_info     = (EditText) findViewById(R.id.et_Info);
        et_addr     = (EditText) findViewById(R.id.et_Address);

        list = (ListView) findViewById(R.id.lv_Database);

        // Establish database connection
        ds = new InfoDataSource(this);

        try {
            ds.open();
            ds.clearTable();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        /*
        Add nodes from server database
         */

        try {
            for(ParseObject object : ParseQuery.getQuery("Marker").find()){
                ds.createInfoNode(object.getString("title"), object.getParseGeoPoint("location").getLatitude(), object.getParseGeoPoint("location").getLongitude(), object.getInt("type"), object.getString("info"), object.getString("address"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<InfoNode> values = ds.getAllInfoNodes();

        // Adapt the values to fit a ListView
        ArrayAdapter<InfoNode> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        list.setAdapter(adapter);
    }

    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<InfoNode> adapter = (ArrayAdapter<InfoNode>) list.getAdapter();

        InfoNode node;
        switch (view.getId()) {
            case R.id.b_AddEntry:
                String title = et_title.getText().toString();
                double lat = Double.parseDouble(et_lat.getText().toString());
                double lng = Double.parseDouble(et_lng.getText().toString());
                int type = Integer.parseInt(et_type.getText().toString());
                String info = et_info.getText().toString();
                String addr = et_addr.getText().toString();

                // save the new comment to the database
                node = ds.createInfoNode(title, lat, lng, type, info, addr);
                adapter.add(node);
                break;
            case R.id.b_DelEntry:
                if (list.getAdapter().getCount() > 0) {
                    node = (InfoNode) list.getAdapter().getItem(0);
                    ds.deleteInfoNode(node);
                    adapter.remove(node);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        try {
            ds.open();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        ds.close();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_developer, menu);
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
