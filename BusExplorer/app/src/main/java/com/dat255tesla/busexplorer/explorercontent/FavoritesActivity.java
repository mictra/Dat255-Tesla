package com.dat255tesla.busexplorer.explorercontent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dat255tesla.busexplorer.R;
import com.dat255tesla.busexplorer.database.InfoNode;
import com.dat255tesla.busexplorer.detailcontent.DetailActivity;

import java.util.List;


public class FavoritesActivity extends Fragment {
    private List<InfoNode> favorites;
    private ListArrayAdapterFavorites adapter;
    private ListView favlist;
    private TextView toolbar_title;


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

        favlist = (ListView) v.findViewById(R.id.fav_list);

        adapter = new ListArrayAdapterFavorites(getActivity(), favorites);
        favlist.setAdapter(adapter);

        favlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDetailView((InfoNode) parent.getItemAtPosition(position));
            }
        });

        adapter.clear();
        adapter.addAll(favorites);

        toolbar_title = (TextView) getActivity().findViewById(R.id.toolbar_title);

        return v;
    }

    private void openDetailView(InfoNode node) {
        DetailActivity fragment = new DetailActivity();
        Bundle args = new Bundle();
        args.putSerializable("InfoNode", node);
        fragment.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.addToBackStack("DetailView");
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar_title.setText(getResources().getString(R.string.favorites));
    }


}
