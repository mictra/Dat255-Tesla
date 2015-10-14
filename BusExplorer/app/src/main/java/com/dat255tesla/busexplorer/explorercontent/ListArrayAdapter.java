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
import java.util.List;


/*
    Class used to make a custom-list-view to be able to show a unique icon for each list-category.
 */
public class ListArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;
    private final List<InfoNode> nodes;

    public ListArrayAdapter(Context context, ArrayList<String> values, List<InfoNode> nodes) {
        super(context, R.layout.maplist_layout, values);
        this.context = context;
        this.values = values;
        this.nodes = nodes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.maplist_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.listString);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_icon);
        textView.setText(values.get(position));

        // Change icon based on name
        String stringObj = values.get(position);

        System.out.println(stringObj);

//        imageView.setImageResource(nodes.get(position));

//        if (s.equals("WindowsMobile")) {
//            imageView.setImageResource(R.drawable.windowsmobile_logo);
//        } else if (s.equals("iOS")) {
//            imageView.setImageResource(R.drawable.ios_logo);
//        } else if (s.equals("Blackberry")) {
//            imageView.setImageResource(R.drawable.blackberry_logo);
//        } else {
//            imageView.setImageResource(R.drawable.android_logo);
//        }

        return rowView;
    }
}