package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 14/06/2017.
 */

public class MyCalendarObject {
    private String event;
    private String date;
    private String start;
    private String end;

    public MyCalendarObject(String event, String date, String start, String end) {
        this.event = event;
        this.date = date;
        this.start = start;
        this.end = end;
    }

    // getters
    public String getEvent() {
        return event;
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
