package com.example.lisahabermehl.calendar;

import android.provider.BaseColumns;

/**
 * Created by lisahabermehl on 14/06/2017.
 */

public class MyCalendarTable {

    public static final String DB_NAME = "com.lisahabermehl.calendar.db";
    public static final int DB_VERSION = 5;

    public class CalendarEntry implements BaseColumns {
        public static final String TABLE = "calendar";
        public static final String COL_CAL_TITLE = "title_cal";
        public static final String COL_CAL_DATE = "date";
        public static final String COL_CAL_START = "start_time";
        public static final String COL_CAL_END = "end_time";
    }
}
