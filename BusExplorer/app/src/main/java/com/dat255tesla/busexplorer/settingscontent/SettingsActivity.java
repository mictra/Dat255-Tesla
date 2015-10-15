package com.dat255tesla.busexplorer.settingscontent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.dat255tesla.busexplorer.R;

public class SettingsActivity extends Fragment {

    public static final String PREFS_NAME = "MyPrefsFile";

    CheckBox checkBox_sightseeing;
    CheckBox checkBox_shopping;
    CheckBox checkBox_misc;
    Button button;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_settings,container,false);

        checkBox_sightseeing = (CheckBox) v.findViewById(R.id.checkBox_sightseeing);
        checkBox_shopping = (CheckBox) v.findViewById(R.id.checkBox_shopping);
        checkBox_misc = (CheckBox) v.findViewById(R.id.checkBox_misc);
        button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences("CheckBox_sightseeing", checkBox_sightseeing.isChecked());
                savePreferences("CheckBox_shopping", checkBox_shopping.isChecked());
                savePreferences("CheckBox_misc", checkBox_misc.isChecked());
                Toast.makeText(getActivity().getApplicationContext(), "SettingsActivity saved", Toast.LENGTH_LONG).show();
            }
        });

        loadSavedPreferences();


        checkBox_sightseeing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (checkBox_sightseeing.isChecked()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Checked", Toast.LENGTH_SHORT).show();
                    /*
                    Function here
                     */
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBox_shopping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox_shopping.isChecked()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Checked", Toast.LENGTH_SHORT).show();
                     /*
                    Function here
                     */
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBox_misc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox_misc.isChecked()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Checked", Toast.LENGTH_SHORT).show();
                     /*
                    Function here
                     */
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private void loadSavedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        boolean checkBoxValue_sight = sharedPreferences.getBoolean("CheckBox_sightseeing", true);
        boolean checkBoxValue_shop = sharedPreferences.getBoolean("CheckBox_shopping", true);
        boolean checkBoxValue_misc = sharedPreferences.getBoolean("CheckBox_misc", true);

        if (checkBoxValue_sight) {
            checkBox_sightseeing.setChecked(true);
        } else {
            checkBox_sightseeing.setChecked(false);
        }
        if (checkBoxValue_shop) {
            checkBox_shopping.setChecked(true);
        } else {
            checkBox_shopping.setChecked(false);
        }
        if (checkBoxValue_misc) {
            checkBox_misc.setChecked(true);
        } else {
            checkBox_misc.setChecked(false);
        }

    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }



    @Override
    public void onPause() {
        super.onPause();
    }


}
