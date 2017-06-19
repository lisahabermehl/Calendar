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
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyCalendar extends AppCompatActivity {

    CalendarView calendarView;

    MyCalendarDbHelper myCalendarDbHelper;
    MyCalendarAdapter myCalendarAdapter;
    ListView listView;

    Context context;

    int start_hour;
    int start_mins;
    int time_start;
    int time_gap_hour;
    int time_gap;
    int end_hour;
    int end_mins;
    int time_end;

    int time_end_old;

    int bedtime_start = (23*60);
    int bedtime_end = (7*60);

    String date_old = "nog niks";
    String date_new;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calendar_list);

        listView = (ListView) findViewById(R.id.list_calendar);
        // initialize the list
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
//                deleteActivity(view);
                return true;
            }
        });

        myCalendarDbHelper = new MyCalendarDbHelper(this);

        time_end = 0;
        time_end_old = 0;

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
            int title = cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_TITLE);
            int date = cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_DATE);
            int start = cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_START);
            int end = cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_END);

            String title_string = cursor.getString(title);
            String date_string = cursor.getString(date);
            String start_string = cursor.getString(start);
            String end_string = cursor.getString(end);
            Log.d("ID", cursor.getString(id));
            Log.d("ACTIVITY", cursor.getString(title));
            Log.d("DATE", cursor.getString(date));
            Log.d("START", cursor.getString(start));
            Log.d("END", cursor.getString(end));

//            MyCalendarObject to = new MyCalendarObject(cursor.getString(title),
//                    cursor.getString(date), cursor.getString(start), cursor.getString(end));
//            calendarObjects.add(to);
            MyCalendarObject to = new MyCalendarObject(title_string,
                    date_string, start_string, end_string);
//            calendarObjects.add(to);


            String[] start_split = start_string.split(":");
            start_hour = Integer.valueOf(start_split[0]);
            start_mins = Integer.valueOf(start_split[1]);
            time_start = (start_hour * 60) + start_mins;

            String[] time_split_end = end_string.split(":");
            end_hour = Integer.valueOf(time_split_end[0]);
            end_mins = Integer.valueOf(time_split_end[1]);
            time_end = (end_hour * 60) + end_mins;

            time_gap = time_start - time_end_old;
            Log.d("TIME END OLD", String.valueOf(time_end_old));
            Log.d("TIME START", String.valueOf(time_start));
            Log.d("TIME END", String.valueOf(time_end));
            Log.d("TIME GAP", String.valueOf(time_gap));
            time_gap_hour = time_gap / 60;
            Log.d("TIME GAP HOUR", String.valueOf(time_gap_hour));

            date_new = date_string;

            Log.d("DATE NEW", date_new);
            Log.d("DATE OLD", date_old);

            if (date_new.equals(date_old)) {
                if(time_gap > 120) {
                    MyCalendarObject to2 = new MyCalendarObject("Big time gap", date_string,
                            String.valueOf(time_gap), String.valueOf(time_gap_hour));

                    Log.d("NEW IS OLD", "TIME GAP > 15");

                    time_end_old = time_end;
                    date_old = date_new;

                    calendarObjects.add(to2);
                    calendarObjects.add(to);
                }
                else {
                    Log.d("NEW IS OLD", "TIME GAP < 15");

                    MyCalendarObject to3 = new MyCalendarObject("Small time gap", date_string,
                            String.valueOf(time_gap), String.valueOf(time_gap_hour));

                    time_end_old = time_end;
                    date_old = date_new;
                    calendarObjects.add(to3);
                    calendarObjects.add(to);
                }
            }
            else {
                Log.d("NEW ISN'T OLD", "TIME GAP > 15");
                // calculate based on different date

                int time_gap_evening = bedtime_start - time_end_old;
                int time_gap_evening_hour = time_gap_evening / 60;
                int time_gap_morning = time_start - bedtime_end;
                int time_gap_morning_hour = time_gap_morning / 60;

                MyCalendarObject to_gap_evening = new MyCalendarObject("Gap evening", date_string,
                        String.valueOf(time_gap_evening), String.valueOf(time_gap_evening_hour));
                MyCalendarObject to_gap_morning = new MyCalendarObject("Gap morning", date_string,
                        String.valueOf(time_gap_morning), String.valueOf(time_gap_morning_hour));

                time_end_old = time_end;
                date_old = date_new;
                calendarObjects.add(to_gap_evening);
                calendarObjects.add(to_gap_morning);
                calendarObjects.add(to);
            }

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

//    public void deleteActivity(View view) {
//        View parent = (View) view.getParent();
//        TextView activityTextView = (TextView) parent.findViewById(R.id.calendar_activity);
//        Log.d("GET TEXT", String.valueOf(activityTextView.getText()));
//        String task = String.valueOf(activityTextView.getText());
//        SQLiteDatabase db = myCalendarDbHelper.getWritableDatabase();
//        db.delete(MyCalendarTable.CalendarEntry.TABLE,
//                MyCalendarTable.CalendarEntry.COL_CAL_TITLE + " = ?",
//                new String[]{task});
//        db.close();
//        updateUI();
//    }

    private void selectDate() {
        LayoutInflater layoutInflaterDay = LayoutInflater.from(this);
        final View dialogViewDay = layoutInflaterDay.inflate(R.layout.calendar_main, null);

        AlertDialog.Builder builderDay = new AlertDialog.Builder(this);
        builderDay
                .setView(dialogViewDay)
                .setPositiveButton("SELECT DATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        calendarView = (CalendarView) findViewById(R.id.calendarView);

                            String date = "2017-06-20";

                            // startActivity to show activities on a specific day
                            Intent newActivity = new Intent(getApplicationContext(), GoogleCalendarTest.class);
                            Bundle extras = new Bundle();

                            extras.putString("date", date);
                            newActivity.putExtras(extras);
                            startActivity(newActivity);

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