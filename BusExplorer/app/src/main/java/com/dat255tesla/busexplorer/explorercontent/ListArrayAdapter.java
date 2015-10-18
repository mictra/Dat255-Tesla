package com.dat255tesla.busexplorer.explorercontent;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dat255tesla.busexplorer.R;
import com.dat255tesla.busexplorer.database.InfoNode;

/*
    Class used to make a custom-list-view(Adapter) to be able to show a unique icon for each list-category.
 */
public class ListArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> stringList;
    private final List<InfoNode> values;

    public ListArrayAdapter(Context context, ArrayList<String> stringList, List<InfoNode> values) {
        super(context, R.layout.maplist_layout, stringList);
        this.context = context;
        this.stringList = stringList;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.maplist_layout, parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_icon);
        TextView textView = (TextView) rowView.findViewById(R.id.listString);
        textView.setText(stringList.get(position));

        // Change icon based on name
        String stringObj = stringList.get(position);
        InfoNode currNode = findNode(stringObj);

        imageView.setImageResource(R.drawable.marker_triangle);
//        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA " + currNode.getTitle());
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

    private InfoNode findNode(String inputString){
        InfoNode currentNode = null;

        for (InfoNode node : values) {
            if(node.getTitle().equals(inputString)) {
                currentNode = node;
            }
        }
        return currentNode;
    }

    public void addAll(List<InfoNode> values) {
        this.values.addAll(values);
    }

    public void clear() {
        values.clear();
    }

}