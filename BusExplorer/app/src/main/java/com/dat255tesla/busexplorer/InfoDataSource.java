package com.dat255tesla.busexplorer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by roy lena on 2015-09-24.
 */
public class InfoDataSource {
    private SQLiteDatabase db;
    private SQLiteHelper helper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_TITLE,
            SQLiteHelper.COLUMN_LAT,
            SQLiteHelper.COLUMN_LNG,
            SQLiteHelper.COLUMN_TYPE,
            SQLiteHelper.COLUMN_INFO,
            SQLiteHelper.COLUMN_ADDR};

    public InfoDataSource(Context context) {
        helper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        db = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }
}
