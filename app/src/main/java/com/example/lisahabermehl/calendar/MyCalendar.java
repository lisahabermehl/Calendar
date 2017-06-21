package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 08/06/2017.
 *
 * Source code: http://www.viralandroid.com/2015/11/android-calendarview-example.html
 */

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyCalendar extends AppCompatActivity {

    CalendarView calendarView;

    MyCalendarDbHelper myCalendarDbHelper;
    MyCalendarAdapter myCalendarAdapter;
    ListView calendarListView;

    TaskDbHelper taskDbHelper;
    TaskAdapter taskAdapter;
    ListView todoListView;

    int time_start, time_gap, time_end, time_end_old;
    int bedtime_start = (23 * 60);
    int bedtime_end = (7 * 60);
    int ID = 0;

    String time_gap_hour;

    String date_old = "nog niks";
    String date_new;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calendar_list);

        calendarListView = (ListView) findViewById(R.id.list_calendar);
        myCalendarDbHelper = new MyCalendarDbHelper(this);

        todoListView = (ListView) findViewById(R.id.list_todo);
        taskDbHelper = new TaskDbHelper(this);

        time_end = 0;
        time_end_old = 0;

        updateUI("no specific date");
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
                final TextView textView = new TextView(this);

                AlertDialog.Builder search_builder = new AlertDialog.Builder(this);
                search_builder
                        .setView(textView)
                        .setPositiveButton("BY EVENT TITLE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("BY DATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MyCalendar.this, "By date", Toast.LENGTH_LONG).show();
                            }
                        })
                        .create()
                        .show();

                return true;
            case R.id.insert_event:
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                final View dialogView = layoutInflater.inflate(R.layout.alert_dialog_insert_event, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setView(dialogView)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MyCalendar.this, "Okeee dan", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("INSERT EVENT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText editText = (EditText) dialogView.findViewById(R.id.event_title);
                                String title = editText.getText().toString();

                                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.event_date_start);
                                String date = String.valueOf(datePicker.getDayOfMonth()) + "/" +
                                        String.valueOf(datePicker.getMonth() + 1) + "/" +
                                        String.valueOf(datePicker.getYear());

                                EditText startTime = (EditText) dialogView.findViewById(R.id.event_time_start);
                                EditText endTime = (EditText) dialogView.findViewById(R.id.event_time_end);

                                String start = date + " " + startTime.getText().toString();
                                String end = date + " " + endTime.getText().toString();

                                // startActivity to show activities on a specific day
                                Bundle extras = new Bundle();
                                extras.putString("zero", "add");
                                extras.putString("one", title);
                                extras.putString("two", start);
                                extras.putString("three", end);
                                startActivity(new Intent(getApplicationContext(), GoogleCalendarTest.class)
                                        .putExtras(extras));
                            }
                        })
                        .create()
                        .show();
                return true;
            case R.id.menu_day:
                LayoutInflater layoutInflaterDay = LayoutInflater.from(this);
                final View dialogViewDay = layoutInflaterDay.inflate(R.layout.calendar_main, null);

                // TODO add a "show all" button

                AlertDialog.Builder builderDay = new AlertDialog.Builder(this);
                builderDay
                        .setView(dialogViewDay)
                        .setPositiveButton("SELECT DATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                calendarView = (CalendarView) findViewById(R.id.calendarView);
                                String date = "2017-06-20";
                                updateUI(date);
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

    private void updateUI(String specific_date) {
//        calendarListView = (ListView) findViewById(R.id.list_calendar);
//
//        myCalendarDbHelper = new MyCalendarDbHelper(this);
        SQLiteDatabase db = myCalendarDbHelper.getReadableDatabase();
        Cursor cursor = db.query(MyCalendarTable.CalendarEntry.TABLE,
                new String[]{TaskTable.TaskEntry._ID,
                        MyCalendarTable.CalendarEntry.COL_CAL_TITLE,
                        MyCalendarTable.CalendarEntry.COL_CAL_DATE,
                        MyCalendarTable.CalendarEntry.COL_CAL_START,
                        MyCalendarTable.CalendarEntry.COL_CAL_END},
                null, null, null, null, MyCalendarTable.CalendarEntry.COL_CAL_DATE + ", " +
                        MyCalendarTable.CalendarEntry.COL_CAL_START + " ASC");

//         look for todos that take a lot of time
        SQLiteDatabase todo_db = taskDbHelper.getReadableDatabase();
        Cursor todo_cursor = todo_db.query(TaskTable.TaskEntry.TABLE,
                new String[]{TaskTable.TaskEntry._ID,
                        TaskTable.TaskEntry.COL_TASK_TITLE,
                        TaskTable.TaskEntry.COL_TASK_DURATION,
                        TaskTable.TaskEntry.COL_TASK_DEADLINE},
                null, null, null, null, null);

        ArrayList<MyCalendarObject> calendarObjects = new ArrayList<>();

        if (specific_date.equals("no specific date")) {
            while (cursor.moveToNext()) {

                String title_string = cursor.getString(cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_TITLE));
                String date_string = cursor.getString(cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_DATE));
                String start_string = cursor.getString(cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_START));
                String end_string = cursor.getString(cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_END));

                MyCalendarObject to = new MyCalendarObject(title_string,
                        date_string, start_string, end_string);

                time_end = convertToMins(end_string);
                time_start = convertToMins(start_string);
                time_gap = time_start - time_end_old;
                time_gap_hour = convertToHour(time_gap);

                date_new = date_string;

                // check if you're still on the same day or going to the next day
                if (date_new.equals(date_old)) {

                    // check if you have a lot of time between two activities
                    // more than two hours is considered as a big time gap
                    if (time_gap > 120) {

                        MyCalendarObject to2 = new MyCalendarObject("Big time gap", date_string,
                                String.valueOf(time_gap), time_gap_hour);

                        todo_cursor.move(ID);

                        while (todo_cursor.moveToNext()) {
                            String id_string = todo_cursor.getString(todo_cursor.getColumnIndex(TaskTable.TaskEntry._ID));
                            String todo_title_string = todo_cursor.getString(todo_cursor.getColumnIndex(TaskTable.TaskEntry.COL_TASK_TITLE));
                            String todo_duration_string = todo_cursor.getString(todo_cursor.getColumnIndex(TaskTable.TaskEntry.COL_TASK_DURATION));
                            String todo_deadline_string = todo_cursor.getString(todo_cursor.getColumnIndex(TaskTable.TaskEntry.COL_TASK_DEADLINE));

                            Log.d("ID", id_string);
                            Log.d("TODO TITLE", todo_title_string);
                            Log.d("TODO DURATION", todo_duration_string);
                            Log.d("TIME GAP", String.valueOf(time_gap));

                            if (time_gap > Integer.valueOf(todo_duration_string)) {
                                MyCalendarObject todo = new MyCalendarObject(todo_title_string,
                                        date_string, String.valueOf(time_gap), String.valueOf(time_gap - Integer.valueOf(todo_duration_string)));

                                time_gap = time_gap - Integer.valueOf(todo_duration_string);

                                Log.d("CHECK", "add todo to calendar");
                                calendarObjects.add(todo);
                            }
                            else {
                                // remember ID of task where you left off
                                ID = Integer.valueOf(id_string);
                            }
                        }

                        // swap
                        time_end_old = time_end;
                        date_old = date_new;

                        calendarObjects.add(to2);
                        calendarObjects.add(to);

                    // less than two hours
                    } else {
                        MyCalendarObject to3 = new MyCalendarObject("Small time gap", date_string,
                                String.valueOf(time_gap), String.valueOf(time_gap_hour));

                        // look for todos that don't take a lot of time
                        time_end_old = time_end;
                        date_old = date_new;

                        calendarObjects.add(to3);
                        calendarObjects.add(to);
                    }
                } else {
                    // calculate based on different date
                    int time_gap_evening = bedtime_start - time_end_old;
                    int time_gap_morning = time_start - bedtime_end;

                    MyCalendarObject to_gap_evening = new MyCalendarObject("Gap evening", date_string,
                            String.valueOf(time_gap_evening), String.valueOf(time_gap_evening / 60));
                    MyCalendarObject to_gap_morning = new MyCalendarObject("Gap morning", date_string,
                            String.valueOf(time_gap_morning), String.valueOf(time_gap_morning / 60));

                    time_end_old = time_end;
                    date_old = date_new;
                    calendarObjects.add(to_gap_evening);
                    calendarObjects.add(to_gap_morning);
                    calendarObjects.add(to);
                }
            }
        } else {
            String date_of_choice = "2017-06-20";
            int time_end_old = (7 * 60);

            while (cursor.moveToNext()) {
                int date = cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_DATE);
                String date_string = cursor.getString(date);
                Log.d("Date string", date_string);

                if (date_of_choice.equals(date_string)) {
                    String title_string = cursor.getString(cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_TITLE));
                    String start_string = cursor.getString(cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_START));
                    String end_string = cursor.getString(cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_END));

                    MyCalendarObject to = new MyCalendarObject(title_string,
                            date_string, start_string, end_string);

                    String[] start_split = start_string.split(":");
                    time_start = (Integer.valueOf(start_split[0]) * 60) + Integer.valueOf(start_split[1]);

                    String[] time_split_end = end_string.split(":");
                    time_end = (Integer.valueOf(time_split_end[0]) * 60) + Integer.valueOf(time_split_end[1]);

                    time_gap = time_start - time_end_old;
                    time_gap_hour = convertToHour(time_gap);

                        if (time_gap > 120) {
                            MyCalendarObject to2 = new MyCalendarObject("Big time gap", date_string,
                                    String.valueOf(time_gap), String.valueOf(time_gap_hour));

                            time_end_old = time_end;
                            date_old = date_new;

                            calendarObjects.add(to2);
                            calendarObjects.add(to);
                        } else {
                            MyCalendarObject to3 = new MyCalendarObject("Small time gap", date_string,
                                    String.valueOf(time_gap), String.valueOf(time_gap_hour));

                            time_end_old = time_end;
                            date_old = date_new;

                            calendarObjects.add(to3);
                            calendarObjects.add(to);
                        }
                    // TODO: how can you calculate that last time gap
                }
            }
        }
            if (myCalendarAdapter == null) {
                myCalendarAdapter = new MyCalendarAdapter(this, 0, calendarObjects);
                calendarListView.setAdapter(myCalendarAdapter);
            } else {
                myCalendarAdapter.clear();
                myCalendarAdapter.addAll(calendarObjects);
                myCalendarAdapter.notifyDataSetChanged();
            }
            cursor.close();
            db.close();
            todo_cursor.close();
            todo_db.close();
        }

    private int convertToMins (String startTime) {

        String[] original = startTime.split(":");
        int time_in_mins = (Integer.valueOf(original[0]) * 60) + Integer.valueOf(original[1]);

        return time_in_mins;
    }

    private String convertToHour (int timeInMins) {

        int hours = timeInMins / 60;
        String time_in_hours = String.valueOf(hours);

        return time_in_hours;
    }

}


