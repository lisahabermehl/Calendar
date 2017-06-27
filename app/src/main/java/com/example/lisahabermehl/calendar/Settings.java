package com.example.lisahabermehl.calendar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by lisahabermehl on 09/06/2017.
 */

public class Settings extends AppCompatActivity {

    Button edit_bedtime;
    Button do_magic;

    DatabaseHelper databaseHelper;

    TimeGapAdapter timeGapAdapter;

    ListView timeGapListView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        timeGapListView = (ListView) findViewById(R.id.list_time_gap);

        edit_bedtime = (Button) findViewById(R.id.edit_bedtime);
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
                startActivity(new Intent(this, MyCalendar.class));
                return true;
            case R.id.menu_todo:
                startActivity(new Intent(this, Todo.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        int set_time_gap = 5;
        int time_gap = sp.getInt("time_gap", 0);
        if(String.valueOf(time_gap) == null){
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("time_gap", set_time_gap);
            editor.apply();
        }
        else{
            set_time_gap = time_gap;
        }

        String[] time_gaps = new String[7];
        int i, gap;
        for(gap = 0, i = 0; gap < 31; i++, gap=gap+5){
            time_gaps[i] = String.valueOf(gap);
        }
        timeGap.setMinValue(0);
        timeGap.setMaxValue(time_gaps.length-1);
        timeGap.setDisplayedValues(time_gaps);
        timeGap.setValue(set_time_gap);
        timeGap.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        AlertDialog.Builder timeGapBuilder = new AlertDialog.Builder(this);
        timeGapBuilder
                .setView(timeGap)
                .setTitle("Time in minutes between suggested Todos")
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int timeGapInt = timeGap.getValue();

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("time_gap", timeGapInt);
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

    public void editBedtime(View view) {
        startActivity(new Intent(this, TimeGap.class));
    }
}

