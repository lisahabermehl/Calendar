package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 08/06/2017.
 *
 * Source code: http://www.viralandroid.com/2015/11/android-calendarview-example.html
 */


import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.MenuItemHoverListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;

public class MyCalendar extends AppCompatActivity {

    CalendarView calendarView;
    BottomNavigationView bottomNavigationView;
    ActivityManager activityManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_calendar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                // make a string of the day
                String day = Integer.toString(i2);
                // extra check just to make sure that date will be send correctly to GoogleCalendar
                if (day.length() == 1) {
                    day = ("0" + day);
                }

                // the first month is 0, but in Google Calendar this is 1
                // so +1
                String month = Integer.toString(i1 + 1);
                if (month.length() == 1) {
                    month = ("0" + month);
                }
                String year = Integer.toString(i);
                String date = day + "/" + month + "/" + year;

                // startActivity to show activities on a specific day
                Intent newActivity = new Intent(getApplicationContext(), GoogleCalendarTest.class);
                Bundle extras = new Bundle();

                extras.putString("date", date);
                newActivity.putExtras(extras);
                startActivity(newActivity);
            }
        });

        // https://stackoverflow.com/questions/36060883/how-to-implement-bottom-navigation-tab-as-per-the-google-new-guideline
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_calendar:
                        Log.d("CALENDAR", "YES");
                        startActivity(new Intent(MyCalendar.this, MyCalendar.class));
                    case R.id.menu_todo:
                        Log.d("TODO", "YES");
                        startActivity(new Intent(MyCalendar.this, Todo.class));
                    case R.id.menu_settings:
                        Log.d("SETTINGS", "YES");
                        startActivity(new Intent(MyCalendar.this, Settings.class));
                }
                return true;
            }
        });
    }

}