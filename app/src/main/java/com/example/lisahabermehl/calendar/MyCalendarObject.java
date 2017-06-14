package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 14/06/2017.
 */

public class MyCalendarObject {
    private String activity;
    private String date;
    private String start;
    private String end;

    // setters
    public MyCalendarObject(String activity, String date, String start, String end) {
        this.activity = activity;
        this.date = date;
        this.start = start;
        this.end = end;
    }

    // getters
    public String getActivity() {
        return activity;
    }

    public String getDate() {
        return date;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}
