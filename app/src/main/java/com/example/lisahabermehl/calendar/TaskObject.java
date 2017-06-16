package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 14/06/2017.
 */

public class TaskObject {

    private String task;
    private String duration;

    // setters
    public TaskObject(String task, String duration) {
        this.task = task;
        this.duration = duration;
    }

    // getters
    public String getTask() {
        return task;
    }

    public String getDuration() {
        return duration;
    }
}