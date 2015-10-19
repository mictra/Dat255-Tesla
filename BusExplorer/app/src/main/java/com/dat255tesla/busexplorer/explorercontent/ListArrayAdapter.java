package com.dat255tesla.busexplorer.explorercontent;

import android.content.Context;
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
    Class used to make a custom-list-view(Adapter) to be able to show a unique icon for each list-category.
 */
public class ListArrayAdapter extends ArrayAdapter<InfoNode> {
    private final Context context;
    private List<InfoNode> values;

    public ListArrayAdapter(Context context, final List<InfoNode> values) {
        super(context, R.layout.maplist_layout, new ArrayList(values));
        this.context = context;
        List<InfoNode> copyValues = new ArrayList(values);
        this.values = copyValues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        InfoNode currNode = values.get(position);
        View rowView = inflater.inflate(R.layout.maplist_layout, parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_icon);
        TextView textView = (TextView) rowView.findViewById(R.id.listString);
        textView.setText(currNode.getTitle());

        imageView.setImageResource(R.drawable.marker_triangle);
        switch (currNode.getType()) {
            case 1:
                imageView.setImageResource(R.drawable.marker_triangle);
                break;
            case 2:
                imageView.setImageResource(R.drawable.marker_square);
                break;
            case 3:
                imageView.setImageResource(R.drawable.marker_circle);
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
