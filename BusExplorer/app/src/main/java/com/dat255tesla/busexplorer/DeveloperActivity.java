package com.dat255tesla.busexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DeveloperActivity extends AppCompatActivity {
    private InfoDataSource ds;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        ds = new InfoDataSource(this);

        try {
            ds.open();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        List<InfoNode> values = ds.getAllInfoNodes();

        list = (ListView) findViewById(R.id.lv_Database);
        // Adapt the values to fit a ListView
        ArrayAdapter<InfoNode> adapter = new ArrayAdapter<InfoNode>(this,
                android.R.layout.simple_list_item_1, values);
        list.setAdapter(adapter);
    }

    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<InfoNode> adapter = (ArrayAdapter<InfoNode>) list.getAdapter();

        InfoNode node;
        switch (view.getId()) {
            case R.id.b_AddEntry:
                /*
                String title = findViewById(R.id.et_Title).toString();
                double lat = Double.parseDouble(findViewById(R.id.et_Latitude).toString());
                double lng = Double.parseDouble(findViewById(R.id.et_Longitude).toString());
                int type = Integer.parseInt(findViewById(R.id.et_Type).toString());
                String info = findViewById(R.id.et_Info).toString();
                String addr = findViewById(R.id.et_Address).toString();
                */ 
                // save the new comment to the database
                node = ds.createInfoNode("test", 2.32d, 2.32d, 1, "none", "none street 2");
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
