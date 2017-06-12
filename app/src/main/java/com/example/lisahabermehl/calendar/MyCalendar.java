package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 08/06/2017.
 *
 * Source code: http://www.viralandroid.com/2015/11/android-calendarview-example.html
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;


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


                // go to a listview with all the things that have to be done on that
                // startActivity and send data
                Intent newActivity = new Intent(getApplicationContext(), GoogleCalendarTest.class);
                Bundle extras = new Bundle();

                extras.putString("callingActivity", date);
                Log.d(String.valueOf(date), "THIS");
                newActivity.putExtras(extras);
                startActivity(newActivity);
            }
        });
    }
}