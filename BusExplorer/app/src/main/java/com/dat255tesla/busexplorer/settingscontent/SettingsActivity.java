package com.dat255tesla.busexplorer.settingscontent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.dat255tesla.busexplorer.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "MyPrefsFile";

    CheckBox checkBox_sightseeing;
    CheckBox checkBox_shopping;
    CheckBox checkBox_misc;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_settings);

        checkBox_sightseeing = (CheckBox) findViewById(R.id.checkBox_sightseeing);
        checkBox_shopping = (CheckBox) findViewById(R.id.checkBox_shopping);
        checkBox_misc = (CheckBox) findViewById(R.id.checkBox_misc);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        loadSavedPreferences();


        checkBox_sightseeing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (checkBox_sightseeing.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Checked", Toast.LENGTH_SHORT).show();
                    /*
                    Function here
                     */
                } else {
                    Toast.makeText(getApplicationContext(), "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBox_shopping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox_shopping.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Checked", Toast.LENGTH_SHORT).show();
                     /*
                    Function here
                     */
                } else {
                    Toast.makeText(getApplicationContext(), "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBox_misc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox_misc.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Checked", Toast.LENGTH_SHORT).show();
                     /*
                    Function here
                     */
                } else {
                    Toast.makeText(getApplicationContext(), "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadSavedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
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
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void onClick(View v) {
        savePreferences("CheckBox_sightseeing", checkBox_sightseeing.isChecked());
        savePreferences("CheckBox_shopping", checkBox_shopping.isChecked());
        savePreferences("CheckBox_misc", checkBox_misc.isChecked());
        Toast.makeText(getApplicationContext(), "SettingsActivity saved", Toast.LENGTH_LONG).show();
        finish();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


}
