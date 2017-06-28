package com.example.lisahabermehl.calendar;

import android.provider.BaseColumns;

/**
 * Created by lisahabermehl on 24/06/2017.
 */

public class TableNames {

    public static final String DB_NAME = "myCalendarActivity";
    public static final int DB_VERSION = 8;

    public class CalendarEntry implements BaseColumns {
        public static final String TABLE_CALENDAR = "calendar";
        public static final String _ID = "id";
        public static final String COL_CAL_TITLE = "title_cal";
        public static final String COL_CAL_DATE = "date";
        public static final String COL_CAL_START = "start_time";
        public static final String COL_CAL_END = "end_time";
    }

    public class TodoEntry implements BaseColumns {
        public static final String TABLE_TODO = "todos";
        public static final String _ID = "id";
        public static final String COL_TODO_TITLE = "title";
        public static final String COL_TODO_DURATION = "duration";
        public static final String COL_TODO_DEADLINE = "deadline";
    }
}
