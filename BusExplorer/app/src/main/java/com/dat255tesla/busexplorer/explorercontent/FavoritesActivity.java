package com.dat255tesla.busexplorer.explorercontent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dat255tesla.busexplorer.R;
import com.dat255tesla.busexplorer.database.InfoNode;

import java.util.List;


public class FavoritesActivity extends Fragment {
    private List<InfoNode> favorites;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favorites = (List<InfoNode>) getArguments().getSerializable("Favorites");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_favorites, container, false);

        TextView tv = (TextView) v.findViewById(R.id.fav_test);

        for (InfoNode node : favorites) {
            tv.append(node.getTitle() + " ");
        }

        return v;
    }


}
