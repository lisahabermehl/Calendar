package com.example.lisahabermehl.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

/**
 * Created by lisahabermehl on 09/06/2017.
 */

public class MainActivity extends AppCompatActivity{

    private Button myCalendar;
    private Button toDo;
    private Button settings;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCalendar = (Button) findViewById(R.id.my_calendar_button)
    }
}
