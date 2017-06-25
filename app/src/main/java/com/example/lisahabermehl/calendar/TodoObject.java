package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 14/06/2017.
 */

public class TodoObject {

    private String task;
    private String duration;
    private String deadline;

    public TodoObject(String task, String duration, String deadline) {
        this.task = task;
        this.duration = duration;
        this.deadline = deadline;
    }

    // getters
    public String getTask() {
        return task;
    }

    public String getDuration() {
        return duration;
    }

    public String getDeadline() {
        return deadline;
    }
}
