package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 08/06/2017.
 *
 * Source code: http://www.viralandroid.com/2015/11/android-calendarview-example.html
 */


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyCalendar extends AppCompatActivity {

    CalendarView calendarView;

    MyCalendarDbHelper myCalendarDbHelper;
    MyCalendarAdapter myCalendarAdapter;
    ListView listView;

    Context context;

    int time_end_mins = 0;
    int time_end_hour = 0;
    int time_end = 0;
    int time_start_mins;
    int time_start_hour;
    String date_old = "nog niks";
    String date_new;

    String time2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calendar_list);

        listView = (ListView) findViewById(R.id.list_calendar);

        myCalendarDbHelper = new MyCalendarDbHelper(this);

        updateUI();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_calendar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:

                return true;
            case R.id.menu_day:
                selectDate();
                return true;
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

    private void updateUI(){
        SQLiteDatabase db = myCalendarDbHelper.getReadableDatabase();
        Cursor cursor = db.query(MyCalendarTable.CalendarEntry.TABLE,
                new String[]{TaskTable.TaskEntry._ID,
                        MyCalendarTable.CalendarEntry.COL_CAL_TITLE,
                        MyCalendarTable.CalendarEntry.COL_CAL_DATE,
                        MyCalendarTable.CalendarEntry.COL_CAL_START,
                        MyCalendarTable.CalendarEntry.COL_CAL_END},
                null, null, null, null, MyCalendarTable.CalendarEntry.COL_CAL_DATE + ", " +
                        MyCalendarTable.CalendarEntry.COL_CAL_START + " ASC");

        ArrayList<MyCalendarObject> calendarObjects = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getColumnIndex(MyCalendarTable.CalendarEntry._ID);
            int idx = cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_TITLE);
            int idxx = cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_DATE);
            int idxxx = cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_START);
            int idxxxx = cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_END);

            Log.d("ID", cursor.getString(id));
            Log.d("ACTIVITY", cursor.getString(idx));
            Log.d("DATE", cursor.getString(idxx));
            Log.d("START", cursor.getString(idxxx));
            Log.d("END", cursor.getString(idxxxx));

            MyCalendarObject to = new MyCalendarObject(cursor.getString(idx),
                    cursor.getString(idxx), cursor.getString(idxxx), cursor.getString(idxxxx));
            calendarObjects.add(to);

            String time = cursor.getString(idxxx);
            date_new = cursor.getString(idxx);
            time2 = cursor.getString(idxxxx);

            String[] time_split = time.split(":");
            time_start_hour = Integer.valueOf(time_split[0]);
            time_start_mins = Integer.valueOf(time_split[1]);

            Log.d("START HOUR", String.valueOf(time_start_hour));
            Log.d("START MINS", String.valueOf(time_start_mins));

            int time_start = (time_start_hour * 60) + time_start_mins;
            int time_gap = time_start - time_end;

            Log.d("TIME START", String.valueOf(time_start));
            Log.d("TIME END", String.valueOf(time_end));
            Log.d("TIME GAP", String.valueOf(time_gap));


            if (date_new.equals(date_old)) {
                if(time_gap > 15) {
                    MyCalendarObject to2 = new MyCalendarObject("TEST", cursor.getString(idxx),
                            String.valueOf(time_start), String.valueOf(time_start + 30));
                    Log.d("HE GOT HERE", "YAY");
                    calendarObjects.add(to2);
                }
            }

            date_old = cursor.getString(idxx);

            Log.d("COUNT", Integer.toString(calendarObjects.size()));
        }
        Log.d("COUNT2", Integer.toString(calendarObjects.size()));


        String[] time_split_end = time2.split(":");
        time_end_hour = Integer.valueOf(time_split_end[0]);
        time_end_mins = Integer.valueOf(time_split_end[1]);
        time_end = (time_end_hour * 60) + time_end_mins;

        if (myCalendarAdapter == null) {
            myCalendarAdapter = new MyCalendarAdapter(this, 0, calendarObjects);
            listView.setAdapter(myCalendarAdapter);
        } else {
            myCalendarAdapter.clear();
            myCalendarAdapter.addAll(calendarObjects);
            myCalendarAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
    }

    private void selectDate() {
        LayoutInflater layoutInflaterDay = LayoutInflater.from(this);
        final View dialogViewDay = layoutInflaterDay.inflate(R.layout.calendar_main, null);

        AlertDialog.Builder builderDay = new AlertDialog.Builder(this);
        builderDay
                .setView(dialogViewDay)
                .setPositiveButton("SELECT DATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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