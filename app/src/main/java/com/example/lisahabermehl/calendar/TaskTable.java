package com.example.lisahabermehl.calendar;

import android.provider.BaseColumns;

/**
 * Created by lisahabermehl on 22/11/16.
 */

public class TaskTable {
    public static final String DB_NAME = "com.lisahabermehl.todo.db";
    public static final int DB_VERSION = 4;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String COL_TASK_TITLE = "title";
        public static final String COL_TASK_DURATION = "duration";
        public static final String COL_TASK_DEADLINE = "deadline";
    }
}