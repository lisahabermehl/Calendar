package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 08/06/2017.
 *
 * Source code: http://www.viralandroid.com/2015/11/android-calendarview-example.html
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MyCalendar extends AppCompatActivity {

    CalendarView calendarView;
    TextView dateDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_calendar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        dateDisplay = (TextView) findViewById(R.id.date_display);
        dateDisplay.setText("Date: ");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                dateDisplay.setText("Date: " + i2 + " / " + i1 + " / " + i);
//                go to a listview with all the things that have to be done on that

                // startActivity and send data
                Intent newActivity = new Intent(getApplicationContext(), Settings.class);
                Bundle extras = new Bundle();

//                String test = "something something";

                extras.putInt("callingActivity", i);
                Log.d(String.valueOf(i), "THIS");
                newActivity.putExtras(extras);
                startActivity(newActivity);
//                startActivity(new Intent(getApplicationContext(), Settings.class));

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // intitialize the xml document that stores the settings for the menu
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
