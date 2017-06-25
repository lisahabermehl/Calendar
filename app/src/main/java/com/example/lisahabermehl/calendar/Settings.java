package com.example.lisahabermehl.calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;

/**
 * Created by lisahabermehl on 09/06/2017.
 */

public class Settings extends AppCompatActivity {

    Button edit_bedtime;

    DatabaseHelper databaseHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        edit_bedtime = (Button) findViewById(R.id.edit_bedtime);
    }

    public void setTimeSpan(View view){
        final EditText timeSpan = new EditText(this);

        AlertDialog.Builder timeSpanBuilder = new AlertDialog.Builder(this);
        timeSpanBuilder
                .setView(timeSpan)
                .setTitle("Enter the amount of days you want to plan for")
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int timeSpanInt = Integer.valueOf(timeSpan.getText().toString());

                        SharedPreferences sp = getSharedPreferences("shared_preferences", Activity.MODE_PRIVATE);
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
        final EditText timeGap = new EditText(this);
        timeGap.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder timeGapBuilder = new AlertDialog.Builder(this);
        timeGapBuilder
                .setView(timeGap)
                .setTitle("Enter the gap you'd like to have between todos")
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int timeGapInt = Integer.valueOf(timeGap.getText().toString());

                        SharedPreferences sp = getSharedPreferences("shared_preferences", Activity.MODE_PRIVATE);
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

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor settings_cursor = db.query(TableNames.SettingsEntry.TABLE_SETTINGS,
                new String[]{TableNames.SettingsEntry._ID,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_START,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_END},
                null, null, null, null, null);

        settings_cursor.moveToLast();
        int start = Integer.valueOf(settings_cursor.getString(settings_cursor.getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_START)));
        int end = Integer.valueOf(settings_cursor.getString(settings_cursor.getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_END)));
        settings_cursor.close();
        db.close();

        int start_hour = 0;
        int end_hour = 0;

        while(start > 59) {
            start = start - 60;
            start_hour = start_hour + 1;
        }

        while(end > 59) {
            end = end - 60;
            end_hour = end_hour + 1;
        }

        LayoutInflater layoutInflaterDay = LayoutInflater.from(this);
        final View dialogViewDay = layoutInflaterDay.inflate(R.layout.alert_dialog_timepicker, null);
        final TimePicker bedtimeStart = (TimePicker) dialogViewDay.findViewById(R.id.bedtime_start);
        final TimePicker bedtimeEnd = (TimePicker) dialogViewDay.findViewById(R.id.bedtime_end);
        bedtimeStart.setIs24HourView(true);
        bedtimeStart.setCurrentHour(start_hour);
        bedtimeStart.setCurrentMinute(start);
        bedtimeEnd.setIs24HourView(true);
        bedtimeEnd.setCurrentHour(end_hour);
        bedtimeEnd.setCurrentMinute(end);

        AlertDialog.Builder builderDay = new AlertDialog.Builder(this);
        builderDay
                .setView(dialogViewDay)
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int start_hour = bedtimeStart.getCurrentHour();
                        int start_mins = bedtimeStart.getCurrentMinute();

                        int end_hour = bedtimeEnd.getCurrentHour();
                        int end_mins = bedtimeEnd.getCurrentMinute();

                        String start = String.valueOf((start_hour * 60) + start_mins);
                        String end = String.valueOf((end_hour * 60) + end_mins);

                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_START, start);
                        cv.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_END, end);

//                        db.update(TableNames.SettingsEntry.TABLE_SETTINGS, cv,
//                                TableNames.SettingsEntry._ID + "=0", null);

                        db.insertWithOnConflict(TableNames.SettingsEntry.TABLE_SETTINGS,
                                null, cv, SQLiteDatabase.CONFLICT_REPLACE);

                        db.close();

                        Toast.makeText(Settings.this, start + " " + end, Toast.LENGTH_SHORT).show();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_todo, menu);
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
}

