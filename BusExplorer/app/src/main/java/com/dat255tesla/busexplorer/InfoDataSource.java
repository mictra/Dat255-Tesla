package com.dat255tesla.busexplorer;

import android.database.sqlite.SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    // Method for adding new InfoNodes
    public InfoNode createInfoNode(String title, double lat, double lng, int type,
                                   String info, String addr) {

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, title);
        values.put(SQLiteHelper.COLUMN_LAT, lat);
        values.put(SQLiteHelper.COLUMN_LNG, lng);
        values.put(SQLiteHelper.COLUMN_TYPE, type);
        values.put(SQLiteHelper.COLUMN_INFO, info);
        values.put(SQLiteHelper.COLUMN_ADDR, addr);

        // Insert values and grab the id of the new entry
        long insertId = db.insert(SQLiteHelper.TABLE_MARKERS, null, values);

        // Cursor at the newly added entry
        Cursor cursor = db.query(SQLiteHelper.TABLE_MARKERS, allColumns,
                SQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();

        // Create a new InfoNode from the db, making sure it's in
        InfoNode newInfo = cursorToInfo(cursor);
        cursor.close();
        return newInfo;
    }

    // Method for deleting InfoNodes
    public void deleteInfoNode(InfoNode node) {
        long id = node.getId();
        db.delete(SQLiteHelper.TABLE_MARKERS, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    // Method for grabbing all InfoNodes
    public List<InfoNode> getAllInfoNodes() {
        List<InfoNode> nodes = new ArrayList<>();

        Cursor cursor = db.query(SQLiteHelper.TABLE_MARKERS, allColumns,
                null, null, null, null, null);

        // Loop through all entries
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            InfoNode node = cursorToInfo(cursor);
            nodes.add(node);
            cursor.moveToNext();
        }

        cursor.close();
        return nodes;
    }

    // Creates an InfoNode from the database at cursor
    private InfoNode cursorToInfo(Cursor cursor) {
        return new InfoNode(cursor.getInt(0), cursor.getString(1),
                cursor.getDouble(2), cursor.getDouble(3), cursor.getInt(4),
                cursor.getString(5), cursor.getString(6));
    }
}
