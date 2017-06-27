package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 14/06/2017.
 */

public class TimeGapObject {

    private String title_time_gap;
    private String start_time_gap;
    private String end_time_gap;
    private String days_time_gap;

    public TimeGapObject(String title, String start, String end, String days) {
        this.title_time_gap = title;
        this.start_time_gap = start;
        this.end_time_gap = end;
        this.days_time_gap = days;
    }

    // getters
    public String getTitleTimeGap() {
        return title_time_gap;
    }

    public String getStartTimeGap() {
        return start_time_gap;
    }

    public String getEndTimeGap() {
        return end_time_gap;
    }

    public String getDaysTimeGap() {
        return days_time_gap;
    }
}
