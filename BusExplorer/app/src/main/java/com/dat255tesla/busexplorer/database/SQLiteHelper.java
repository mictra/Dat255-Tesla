package com.dat255tesla.busexplorer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by roy lena on 2015-09-24.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    // SQL table name and column names
    public static final String TABLE_MARKERS = "markers";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_INFO = "info";
    public static final String COLUMN_ADDR = "addr";
    public static final String COLUMN_LASTMOD = "lastmod";
    public static final String COLUMN_OBJID = "objid";

    private static final String DATABASE_NAME = "markers.db";
    private static final int DATABASE_VERSION = 1;

    // SQL statement to create database
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_MARKERS + "(" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TITLE
            + " TEXT NOT NULL, " + COLUMN_LAT
            + " REAL NOT NULL, " + COLUMN_LNG
            + " REAL NOT NULL, " + COLUMN_TYPE
            + " INTEGER NOT NULL, " + COLUMN_INFO
            + " TEXT NOT NULL, " + COLUMN_ADDR
            + " TEXT, " + COLUMN_LASTMOD
            + " TEXT NOT NULL, " + COLUMN_OBJID
            + " INTEGER NOT NULL"
            + ");";

    public SQLiteHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);
        onCreate(db);
    }
}
