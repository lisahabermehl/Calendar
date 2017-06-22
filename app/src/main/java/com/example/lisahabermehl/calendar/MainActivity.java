package com.example.lisahabermehl.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

/**
 * Created by lisahabermehl on 09/06/2017.
 */

public class MainActivity extends AppCompatActivity{

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }

    // go to MyCalendar
    public void onCalendarClick(View v) {
        startActivity(new Intent(this, MyCalendar.class));
    }
    // go to Todo
    public void onTodoClick(View view) {
        startActivity(new Intent(this, Todo.class));
    }
    // go to Settings
    public void onSettingsClick(View view) {
        startActivity(new Intent(this, Settings.class));
    }
}
