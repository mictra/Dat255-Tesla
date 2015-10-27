package com.dat255tesla.busexplorer.explorercontent;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dat255tesla.busexplorer.R;
import com.dat255tesla.busexplorer.database.InfoNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class used to make a custom-list-view(Adapter) to be able to show a unique icon for each list-category.
 */
public class ListArrayAdapterFavorites extends ArrayAdapter<InfoNode> {
    private final Context context;
    private List<InfoNode> values;

    public ListArrayAdapterFavorites(Context context, final List<InfoNode> values) {
        super(context, R.layout.maplist_layout, new ArrayList(values));
        this.context = context;
        List<InfoNode> copyValues = new ArrayList(values);
        this.values = copyValues;
    }

    /**
     * Returns View #rowView of the adapter containing information on the InfoNode at position
     * #position in the adapter (with the right icon depending on type, a title and a favorite-icon
     * with a listener that saves if it was clicked/marked or not in SharedPreferences.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return rowView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final InfoNode currNode = values.get(position);
        View rowView = inflater.inflate(R.layout.favlist_layout, parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.favlist_icon);
        TextView title = (TextView) rowView.findViewById(R.id.favlist_title);
        title.setText(currNode.getTitle());
        TextView address = (TextView) rowView.findViewById(R.id.favlist_address);
        address.setText(currNode.getAddress());


        final ImageView favButton = (ImageView) rowView.findViewById(R.id.favlist_favButton);
        favButton.setOnClickListener(new View.OnClickListener() {
            boolean isFavorite = false;

            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;

                if (isFavorite)
                    favButton.setImageResource(R.drawable.star_filled);
                else
                    favButton.setImageResource(R.drawable.star_unfilled);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(currNode.getTitle(), isFavorite);
                editor.apply();
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        boolean isFavorite = sharedPreferences.getBoolean(currNode.getTitle(), false);

        if (isFavorite)
            favButton.setImageResource(R.drawable.star_filled);
        else
            favButton.setImageResource(R.drawable.star_unfilled);


        imageView.setImageResource(R.drawable.ic_local_see_black_48dp);
        switch (currNode.getType()) {
            case 1:
                imageView.setImageResource(R.drawable.ic_local_see_black_48dp);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_local_mall_black_48dp);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_restaurant_menu_black_48dp);
                break;
        }
        return rowView;
    }

    @Override
    public void clear() {
        super.clear();
        values.clear();
    }

    @Override
    public void addAll(Collection<? extends InfoNode> collection) {
        super.addAll(collection);
        values.addAll(collection);
    }
}

