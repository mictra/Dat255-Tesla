//package com.dat255tesla.busexplorer.explorercontent;
//
//import android.app.Fragment;
//import android.app.ListActivity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ListView;
//import android.widget.Toast;
//import android.os.Bundle;
//import com.dat255tesla.busexplorer.R;
//
//public class MapList extends Fragment {
//
//    private ListView belowMapList;
//
//    // Using an boolean to check, instead of checking its visibility or a status.
//    private boolean isListOpen = false;
//    private boolean isFavorite = false;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.maplist_layout, container, false);
//    }
//
//
//    public void createList() {
//
//        System.out.println("Creating the list & Button");
//
//        // Temp-list below map
//        String[] sites = {"Poseidon", "Zeus", "Hades", "Demeter", "Ares", "Athena", "Apollo"};
//        belowMapList = (ListView) findViewById(R.id.listBelowMap);
//        belowMapList.setAdapter(new ArrayAdapter<>(
//                this, R.layout.maplist_layout,
//                R.id.listIcon, sites));
//        belowMapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        parent.getItemAtPosition(position) + " clicked", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
//
//        // List is hidden by default.
//        setListVisibility(false);
//
//        // Button to open and close list.
//        final Button listButton = (Button) findViewById(R.id.openListButton);
//        final View.OnClickListener openListListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//
//                String listStatus = (isListOpen) ? "Open List" : "Close List";
//                listButton.setText(listStatus);
//                setListVisibility(!isListOpen);
//                isListOpen = !isListOpen;
//
//            }
//        };
//        listButton.setOnClickListener(openListListener);
//
////        // Button to favorite an list-object.
////        final ImageButton favButton = (ImageButton) findViewById(R.id.favButton);
////        final View.OnClickListener favItemListener = new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////                if(isFavorite){
////                    v.setBackgroundResource(R.drawable.poseidon2_thumb);
////
////                } else {
////                    v.setBackgroundResource(R.drawable.poseidon3_thumb);
////                }
////
////                isFavorite = !isFavorite;
////            }
////        };
////        favButton.setOnClickListener(favItemListener);
//
////        favButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////                if(isFavorite){
////                    v.setBackgroundResource(R.drawable.poseidon2_thumb);
////
////                } else {
////                    v.setBackgroundResource(R.drawable.poseidon3_thumb);
////                }
////
////                isFavorite = !isFavorite;
////            }
////        });
//    }
//
//    private void setListVisibility (boolean isVisible) {
//        getListView().setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE );
//    }
//
//    private ListView getListView() {
//        return belowMapList;
//    }
//}