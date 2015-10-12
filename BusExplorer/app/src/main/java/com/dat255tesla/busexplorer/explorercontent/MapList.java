package com.dat255tesla.busexplorer.explorercontent;

import android.app.ListActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Bundle;
import com.dat255tesla.busexplorer.R;

public class MapList extends ListActivity {

    private ListView belowMapList;

    // Using an boolean to check if list is open, instead of checking its visibility.
    private boolean isListOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("Creating the list & Button");

        // Temp-list below map
        String[] sites = {"Poseidon", "Zeus", "Hades", "Demeter", "Ares", "Athena", "Apollo"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sites);
        belowMapList = (ListView) findViewById(R.id.listBelowMap);
        belowMapList.setAdapter(adapter);
        belowMapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        (String) parent.getItemAtPosition(position) + " clicked", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // List is hidden by default.
        setListVisibility(true);

        final Button listButton = (Button) findViewById(R.id.openListButton);
        final View.OnClickListener openListListener = new View.OnClickListener() {
            @Override
            public void onClick(View v){

                String listStatus = (isListOpen) ? "Open List" : "Close List";
                listButton.setText(listStatus);
                setListVisibility(!isListOpen);
                isListOpen = !isListOpen;

                System.out.println("clicked button");

            }
        };

        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.maplist_layout,
                R.id.listIcon, sites));

        listButton.setOnClickListener(openListListener);
    }

    private void setListVisibility (boolean isVisible) {
        getListView().setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE );
    }

//    private ListView getListView() {
//        return belowMapList;
//    }
}