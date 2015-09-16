package com.dat255tesla.busexplorer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {
    private BusMap bMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bMap = new BusMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
