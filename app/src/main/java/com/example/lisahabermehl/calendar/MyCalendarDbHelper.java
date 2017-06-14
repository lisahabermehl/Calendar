package com.example.lisahabermehl.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lisahabermehl on 14/06/2017.
 */

public class MyCalendarDbHelper extends SQLiteOpenHelper {

    public MyCalendarDbHelper(Context context){
        super(context, TaskTable.DB_NAME, null, TaskTable.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + MyCalendarTable.CalendarEntry.TABLE + " ( " +
                MyCalendarTable.CalendarEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MyCalendarTable.CalendarEntry.COL_CAL_TITLE + " TEXT NOT NULL, " +
                MyCalendarTable.CalendarEntry.COL_CAL_DATE + " TEXT NOT NULL, " +
                MyCalendarTable.CalendarEntry.COL_CAL_START + " TEXT NOT NULL, " +
                MyCalendarTable.CalendarEntry.COL_CAL_END + " TEXT NOT NULL);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MyCalendarTable.CalendarEntry.TABLE);
        onCreate(db);
    }
}
