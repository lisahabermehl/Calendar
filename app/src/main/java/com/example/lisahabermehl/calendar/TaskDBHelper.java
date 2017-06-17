package com.example.lisahabermehl.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {

    public TaskDbHelper(Context context){
        super(context, TaskTable.DB_NAME, null, TaskTable.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TaskTable.TaskEntry.TABLE + " ( " +
                TaskTable.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskTable.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL, " +
                TaskTable.TaskEntry.COL_TASK_DURATION + " TEXT NOT NULL, " +
                TaskTable.TaskEntry.COL_TASK_DEADLINE + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskTable.TaskEntry.TABLE);
        onCreate(db);
    }
}
