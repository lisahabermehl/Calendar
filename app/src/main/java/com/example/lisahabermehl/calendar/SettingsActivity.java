package com.example.lisahabermehl.calendar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by lisahabermehl on 09/06/2017.
 *
 * Everything that has to do with personalising the Todo suggestions to the user:
 * what time does the user go to bed, what time does he/she wakes up,
 * how much time does the user want to have between events and todo items,
 * and for how many days does the user want to plan something.
 */

public class SettingsActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_calendar:
                startActivity(new Intent(this, MyCalendarActivity.class));
                return true;
            case R.id.menu_todo:
                startActivity(new Intent(this, TodoActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editStartBedtime(View view) {
        final SharedPreferences sp = getSharedPreferences("shared_preferences", Activity.MODE_PRIVATE);

        final TimePicker bedtimeStart = new TimePicker(this);
        bedtimeStart.setIs24HourView(true);

        bedtimeStart.setCurrentHour(sp.getInt("bedtime_start_hour", 23));
        bedtimeStart.setCurrentMinute(sp.getInt("bedtime_start_minute", 0));

        AlertDialog.Builder timeSpanBuilder = new AlertDialog.Builder(this);
        timeSpanBuilder
                .setView(bedtimeStart)
                .setTitle("Set bedtime")
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("bedtime_start_hour", bedtimeStart.getCurrentHour());
                        editor.putInt("bedtime_start_minute", bedtimeStart.getCurrentMinute());
                        editor.apply();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    public void editEndBedtime(View view) {
        final SharedPreferences sp = getSharedPreferences("shared_preferences", Activity.MODE_PRIVATE);

        final TimePicker bedtimeEnd = new TimePicker(this);
        bedtimeEnd.setIs24HourView(true);

        bedtimeEnd.setCurrentHour(sp.getInt("bedtime_end_hour", 7));
        bedtimeEnd.setCurrentMinute(sp.getInt("bedtime_end_minute", 0));

        AlertDialog.Builder timeSpanBuilder = new AlertDialog.Builder(this);
        timeSpanBuilder
                .setView(bedtimeEnd)
                .setTitle("Set bedtime")
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("bedtime_end_hour", bedtimeEnd.getCurrentHour());
                        editor.putInt("bedtime_end_minute", bedtimeEnd.getCurrentMinute());
                        editor.apply();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    public void setTimeSpan(View view){
        final SharedPreferences sp = getSharedPreferences("shared_preferences", Activity.MODE_PRIVATE);

        final NumberPicker timeSpan = new NumberPicker(this);
        int set_time_span = 14;
        int time_span = sp.getInt("time_span", 0);
        if(String.valueOf(time_span) == null){
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("time_span", set_time_span);
            editor.apply();
        }
        else{
            set_time_span = time_span;
        }
        timeSpan.setMinValue(1);
        timeSpan.setMaxValue(7*4*3);
        timeSpan.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        timeSpan.setValue(set_time_span);

        AlertDialog.Builder timeSpanBuilder = new AlertDialog.Builder(this);
        timeSpanBuilder
                .setView(timeSpan)
                .setTitle("Time span in days")
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int timeSpanInt = timeSpan.getValue();

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("time_span", timeSpanInt);
                        editor.apply();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    public void setTimeGap(View view){
        final SharedPreferences sp = getSharedPreferences("shared_preferences", Activity.MODE_PRIVATE);

        final NumberPicker timeGap = new NumberPicker(this);

        String[] time_gaps = new String[7];
        int i, gap;
        for(gap = 0, i = 0; gap < 31; i++, gap=gap+5){
            time_gaps[i] = String.valueOf(gap);
        }
        timeGap.setMinValue(0);
        timeGap.setMaxValue(time_gaps.length-1);
        timeGap.setDisplayedValues(time_gaps);
        timeGap.setValue(sp.getInt("time_gap", 1));
        timeGap.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        Log.d("TIME GAP", String.valueOf(sp.getInt("time_gap", 5)));

        AlertDialog.Builder timeGapBuilder = new AlertDialog.Builder(this);
        timeGapBuilder
                .setView(timeGap)
                .setTitle("Time in minutes between suggested Todos")
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SharedPreferences.Editor editor = sp.edit();
                        // have to multiply by 5 because I'm using intervals of five
                        // apparently getValue gives me the position of selected number in list
                        editor.putInt("time_gap", (timeGap.getValue() * 5));
                        editor.apply();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }


}

