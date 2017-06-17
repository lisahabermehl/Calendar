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

import java.util.ArrayList;

public class MyCalendar extends AppCompatActivity {

    CalendarView calendarView;

    MyCalendarDbHelper myCalendarDbHelper;
    MyCalendarAdapter myCalendarAdapter;
    ListView listView;

    Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calendar_list);

        listView = (ListView) findViewById(R.id.list_calendar);
        myCalendarDbHelper = new MyCalendarDbHelper(this);



        updateUI();

//        calendarView = (CalendarView) findViewById(R.id.calendarView);
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
//                // make a string of the day
//                String day = Integer.toString(i2);
//                // extra check just to make sure that date will be send correctly to GoogleCalendar
//                if (day.length() == 1) {
//                    day = ("0" + day);
//                }
//
//                // the first month is 0, but in Google Calendar this is 1
//                // so +1
//                String month = Integer.toString(i1 + 1);
//                if (month.length() == 1) {
//                    month = ("0" + month);
//                }
//                String year = Integer.toString(i);
//                String date = year + "-" + month + "-" + day;
//
//                // startActivity to show activities on a specific day
//                Intent newActivity = new Intent(getApplicationContext(), GoogleCalendarTest.class);
//                Bundle extras = new Bundle();
//
//                extras.putString("date", date);
//                newActivity.putExtras(extras);
//                startActivity(newActivity);
//
//                }
//        });
    }
//}


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
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                final View dialogView = layoutInflater.inflate(R.layout.alert_dialog_search, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setView(dialogView)
                        .setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
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
                return true;
            case R.id.menu_day:
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

            Log.d("COUNT", Integer.toString(calendarObjects.size()));
        }
        Log.d("COUNT2", Integer.toString(calendarObjects.size()));

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
}