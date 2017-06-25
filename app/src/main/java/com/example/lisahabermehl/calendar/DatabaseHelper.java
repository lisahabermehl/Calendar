package com.example.lisahabermehl.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_CALENDAR = "CREATE TABLE IF NOT EXISTS "
            + TableNames.CalendarEntry.TABLE_CALENDAR + "("
            + TableNames.CalendarEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TableNames.CalendarEntry.COL_CAL_TITLE + " TEXT NOT NULL, "
            + TableNames.CalendarEntry.COL_CAL_DATE + " TEXT NOT NULL, "
            + TableNames.CalendarEntry.COL_CAL_START + " TEXT NOT NULL, "
            + TableNames.CalendarEntry.COL_CAL_END + " TEXT NOT NULL" + ")";

    private static final String CREATE_TABLE_TODO = "CREATE TABLE IF NOT EXISTS "
            + TableNames.TodoEntry.TABLE_TODO + "("
            + TableNames.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TableNames.TodoEntry.COL_TODO_TITLE + " TEXT NOT NULL, "
            + TableNames.TodoEntry.COL_TODO_DURATION + " INTEGER NOT NULL, "
            + TableNames.TodoEntry.COL_TODO_DEADLINE + " TEXT NOT NULL" + ")";

    private static final String CREATE_TABLE_SETTINGS = "CREATE TABLE IF NOT EXISTS "
            + TableNames.SettingsEntry.TABLE_SETTINGS + "("
            + TableNames.SettingsEntry._ID + " INTEGER PRIMARY KEY, "
            + TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE + " TEXT, "
            + TableNames.SettingsEntry.COL_SET_TIME_OFF_START + " TEXT, "
            + TableNames.SettingsEntry.COL_SET_TIME_OFF_END + " TEXT, "
            + TableNames.SettingsEntry.COL_SET_TIME_GAP + " TEXT, "
            + TableNames.SettingsEntry.COL_SET_TIME_SPAN + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, TableNames.DB_NAME, null, TableNames.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_CALENDAR);
        db.execSQL(CREATE_TABLE_TODO);
        db.execSQL(CREATE_TABLE_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TableNames.CalendarEntry.TABLE_CALENDAR);
        db.execSQL("DROP TABLE IF EXISTS " + TableNames.TodoEntry.TABLE_TODO);
        db.execSQL("DROP TABLE IF EXISTS " + TableNames.SettingsEntry.TABLE_SETTINGS);

        // create new tables
        onCreate(db);
    }

}