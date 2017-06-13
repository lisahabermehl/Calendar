package com.example.lisahabermehl.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by lisahabermehl on 22/11/16.
 */

public class Helper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String LIST = "todo_list";
        public static final String TODO = "todo";
        public static final String CHECK = "item_checked";
    }

    // constructor
    // has to be public, because we have to be able to make this from another class
    public Helper(Context context) {
        // this has to be 'set'
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase){
        String CREATE_TABLE = "CREATE TABLE " + TaskEntry.LIST + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskEntry.TODO + " TEXT, " + TaskEntry.CHECK + " INT);";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
        // drop the existing table and call onCreate
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TaskEntry.LIST);
        onCreate(sqLiteDatabase);
    }

}
