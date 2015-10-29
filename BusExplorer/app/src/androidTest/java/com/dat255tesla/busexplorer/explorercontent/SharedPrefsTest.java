package com.dat255tesla.busexplorer.explorercontent;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.dat255tesla.busexplorer.database.InfoDataSource;

import junit.framework.TestCase;

/**
 * Created by roy lena on 2015-10-29.
 */
public class SharedPrefsTest extends AndroidTestCase {
    private SharedPreferences prefs;
    private RenamingDelegatingContext context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Mock context
        context = new RenamingDelegatingContext(getContext(), "test_");

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void testPrefs() {
        /**
         * Tests the SharedPreferences, to make sure data is saved.
         * This tests two booleans and two integers.
         * We make sure to grab a new set of SharedPreferences with the same context
         * to make sure that the data is truly saved between contexts.
         */
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("testBool1_", true);
        editor.putBoolean("testBool2_", false);
        editor.putInt("testInt1_", 1);
        editor.putInt("testInt2_", 3);
        editor.apply();

        // Get the current contexts preferences
        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(context);
        assertTrue(prefs2.getBoolean("testBool1_", false));
        assertFalse(prefs2.getBoolean("testBool2_", true));

        assertTrue(prefs2.getInt("testInt1_", 0) == 1);
        assertTrue(prefs2.getInt("testInt2_", 0) == 3);
    }
}
