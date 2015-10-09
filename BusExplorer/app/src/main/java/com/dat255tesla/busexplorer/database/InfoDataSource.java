package com.dat255tesla.busexplorer.database;

import android.database.sqlite.SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy lena on 2015-09-24.
 */
public class InfoDataSource {
    private IValuesChangedListener vcl; // TODO: Maybe abstract this to another class, more like Observer pattern?
    private SQLiteDatabase db;
    private SQLiteHelper helper;
    private String[] allColumns = {SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_TITLE,
            SQLiteHelper.COLUMN_LAT,
            SQLiteHelper.COLUMN_LNG,
            SQLiteHelper.COLUMN_TYPE,
            SQLiteHelper.COLUMN_INFO,
            SQLiteHelper.COLUMN_ADDR,
            SQLiteHelper.COLUMN_LASTMOD,
            SQLiteHelper.COLUMN_OBJID};

    public InfoDataSource(Context context) {
        helper = new SQLiteHelper(context);
    }

    public void setValuesChangedListener(IValuesChangedListener vcl) {
        this.vcl = vcl;
    }

    public void open() throws SQLException {
        db = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    public void clearTable() {
        helper.onUpgrade(db, 1, 1);
    }

    // Method for getting the Date in millis (long) for the latest modified object.
    public long getLatestModified() {
        Cursor cursor = db.rawQuery("SELECT * FROM markers WHERE " +
                "lastmod = (SELECT MAX(lastmod) FROM markers) " +
                "order by lastmod limit 1", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long lastMod = cursor.getLong(7);
            cursor.close();
            return lastMod;
        } else {
            return 0;
        }
    }

    // Method for adding new InfoNodes
    public InfoNode createInfoNode(String title, double lat, double lng, int type,
                                   String info, String addr, long date, String objId) {

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, title);
        values.put(SQLiteHelper.COLUMN_LAT, lat);
        values.put(SQLiteHelper.COLUMN_LNG, lng);
        values.put(SQLiteHelper.COLUMN_TYPE, type);
        values.put(SQLiteHelper.COLUMN_INFO, info);
        values.put(SQLiteHelper.COLUMN_ADDR, addr);
        values.put(SQLiteHelper.COLUMN_LASTMOD, date);
        values.put(SQLiteHelper.COLUMN_OBJID, objId);

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
                cursor.getString(5), cursor.getString(6), cursor.getLong(7), cursor.getString(8));
    }

    /*
    Update the internal database with the server database if
    the server database was modified at a latter date than the internal.
     */
    public void updateDatabaseIfNeeded() {
        /*
        Anonymous function that is invoked once the callback succeeds with either a ParseObject or ParseException.
        Checks the date where the server database was last updated (through latest modified object).
         */
        ParseQuery.getQuery("Marker").addDescendingOrder("updatedAt").getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    long latestUpdate = parseObject.getUpdatedAt().getTime();

                    if (latestUpdate > getLatestModified()) {
                        //Toast.makeText(getApplicationContext(), "UPDATE IS NEEDED, SERVER DATABASE NEWER VERSION: " + ds.getLatestModified(), Toast.LENGTH_SHORT).show();
                        clearTable();
                        updateDatabase();
                            /*
                            values = ds.getAllInfoNodes();

                            adapter.clear();
                            adapter.addAll(values);
                            */
                    } else {
                        // TODO: No update needed, notify somehow?
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });

    }

    /*
    Update the internal database with the server database.
     */
    private void updateDatabase() {
        ParseQuery.getQuery("Marker").findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject object : list) {
                        String title = object.getString("title");
                        ParseGeoPoint location = object.getParseGeoPoint("location");
                        int type = object.getInt("type");
                        String info = object.getString("info");
                        String address = object.getString("address");
                        long lastMod = object.getUpdatedAt().getTime();
                        String objId = object.getObjectId();
                        createInfoNode(title, location.getLatitude(), location.getLongitude(), type, info, address, lastMod, objId);
                    }
                    vcl.valuesChanged(getAllInfoNodes());
                } else {
                    e.printStackTrace();
                    // TODO: Error message, failed to retrieve database from server
                }
            }
        });
    }

}
