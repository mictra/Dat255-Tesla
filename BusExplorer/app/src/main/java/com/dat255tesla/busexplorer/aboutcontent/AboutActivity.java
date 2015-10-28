package com.dat255tesla.busexplorer.aboutcontent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dat255tesla.busexplorer.R;

public class AboutActivity extends Fragment {

    private TextView toolbar_title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_about, container, false);

        toolbar_title = (TextView) getActivity().findViewById(R.id.toolbar_title);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar_title.setText(getResources().getString(R.string.about_aboutHeader));
    }

}
