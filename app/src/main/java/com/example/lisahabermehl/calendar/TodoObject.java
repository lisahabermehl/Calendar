package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 14/06/2017.
 *
 * Makes it possible to get specific details of a single Todo item.
 */

public class TodoObject {

    private String todo;
    private String duration;
    private String deadline;

    public TodoObject(String todo, String duration, String deadline) {
        this.todo = todo;
        this.duration = duration;
        this.deadline = deadline;
    }

    // getters
    public String getTodo() {
        return todo;
    }

    public String getDuration() {
        return duration;
    }

    public String getDeadline() {
        return deadline;
    }
}
