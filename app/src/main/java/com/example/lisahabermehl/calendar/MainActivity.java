package com.example.lisahabermehl.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by lisahabermehl on 09/06/2017.
 */

public class MainActivity extends AppCompatActivity{

    private Button myCalendar;
    private Button toDo;
    private Button settings;

    private String string;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCalendar = (Button) findViewById(R.id.my_calendar_button);
        toDo = (Button) findViewById(R.id.todo_button);
        settings = (Button) findViewById(R.id.settings_button);

    }

    public void onCalendarClick(View v) {
        startActivity(new Intent(this, MyCalendar.class));
    }

    public void onTodoClick(View view) {
        startActivity(new Intent(this, Todo.class));
    }


    public void onSettingsClick(View view) {
        startActivity(new Intent(this, Settings.class));
    }
}
