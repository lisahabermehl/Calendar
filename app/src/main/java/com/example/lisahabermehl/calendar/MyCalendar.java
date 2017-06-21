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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyCalendar extends AppCompatActivity {

    CalendarView calendarView;

    MyCalendarDbHelper myCalendarDbHelper;
    MyCalendarAdapter myCalendarAdapter;
    ListView calendarListView;

    TaskDbHelper taskDbHelper;
    TaskAdapter taskAdapter;
    ListView todoListView;

    int time_start, time_gap, time_end;
    int time_end_old = 40;
    int bedtime_start = (23 * 60);
    int bedtime_end = (7 * 60);
    int ID = 0;
    int time_gap_morning;
    int time_gap_evening;

    String time_gap_hour;
    String day;

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

        updateUI("no");
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

        SQLiteDatabase db = myCalendarDbHelper.getReadableDatabase();
        Cursor cursor = db.query(MyCalendarTable.CalendarEntry.TABLE,
                new String[]{TaskTable.TaskEntry._ID,
                        MyCalendarTable.CalendarEntry.COL_CAL_TITLE,
                        MyCalendarTable.CalendarEntry.COL_CAL_DATE,
                        MyCalendarTable.CalendarEntry.COL_CAL_START,
                        MyCalendarTable.CalendarEntry.COL_CAL_END},
                null, null, null, null, MyCalendarTable.CalendarEntry.COL_CAL_DATE + ", " +
                        MyCalendarTable.CalendarEntry.COL_CAL_START + " ASC");

        // show data from to do list: first deadline first, and tasks with the "biggest time needed" first
        SQLiteDatabase todo_db = taskDbHelper.getReadableDatabase();
        Cursor todo_cursor = todo_db.query(TaskTable.TaskEntry.TABLE,
                new String[]{TaskTable.TaskEntry._ID,
                        TaskTable.TaskEntry.COL_TASK_TITLE,
                        TaskTable.TaskEntry.COL_TASK_DURATION,
                        TaskTable.TaskEntry.COL_TASK_DEADLINE},
                null, null, null, null, TaskTable.TaskEntry.COL_TASK_DEADLINE + " ASC "+ ", " +
                        TaskTable.TaskEntry.COL_TASK_DURATION + " DESC");

        ArrayList<MyCalendarObject> calendarObjects = new ArrayList<>();


        if(specific_date.equals("no")){

            while(cursor.moveToNext()) {

                String title_string = cursor.getString(cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_TITLE));
                String date_string = cursor.getString(cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_DATE));
                String start_string = cursor.getString(cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_START));
                String end_string = cursor.getString(cursor.getColumnIndex(MyCalendarTable.CalendarEntry.COL_CAL_END));

                MyCalendarObject to = new MyCalendarObject(title_string, date_string, start_string, end_string);

                time_end = convertToMins(end_string);
                time_start = convertToMins(start_string);
                time_gap = time_start - time_end_old;
                date_new = date_string;

                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                Log.d("CURRENT DATE", currentDate);
                String currentTimeMin = new SimpleDateFormat("mm").format(Calendar.getInstance().getTime());
                Log.d("CURRENT TIME MIN", currentTimeMin);
                String currentTimeHour = new SimpleDateFormat("HH").format(Calendar.getInstance().getTime());
                Log.d("CURRENT TIME HOUR", currentTimeHour);
                int currentTime = (Integer.valueOf(currentTimeHour) * 60) + Integer.valueOf(currentTimeMin);

                if (date_new.equals(date_old)) {
                    day = "same day";
                    Log.d("SAME DAY", String.valueOf(date_new)+" "+String.valueOf(date_old));
                }
                else if (date_new.equals(currentDate)){
                    day = "today";
                    time_gap = time_start - currentTime;
                    time_gap_hour = convertToHour(time_gap);
                    Log.d("CURRENT DAY", String.valueOf(date_new)+" "+currentDate);
                }
                else {
                    day = "other day";
                    // 23:00 - 19:00 = 04:00
                    time_gap_evening = bedtime_start - time_end_old;
                    // 10:00 - 07:00 = 03:00
                    time_gap_morning = time_start - bedtime_end;
                    MyCalendarObject bedtime = new MyCalendarObject("Bedtime", date_string+" + 1", "23:00", "07:00");
                    calendarObjects.add(bedtime);
                    Log.d("OTHER DAY", String.valueOf(date_new)+" "+String.valueOf(date_old));
                }

                date_old = date_new;

                calendarObjects.add(to);

                    if(todo_cursor.moveToNext()) {

                        String[] details;
                        details = getDetails(myCalendarDbHelper, todo_cursor, todo_cursor.getColumnIndex(TaskTable.TaskEntry._ID));

                        String todo_title_string = details[0];
                        String todo_duration_string = details[1];
                        String todo_deadline_string = details[2];

//                        String id_string = todo_cursor.getString(todo_cursor.getColumnIndex(TaskTable.TaskEntry._ID));
//                        String todo_title_string = todo_cursor.getString(todo_cursor.getColumnIndex(TaskTable.TaskEntry.COL_TASK_TITLE));
//                        String todo_duration_string = todo_cursor.getString(todo_cursor.getColumnIndex(TaskTable.TaskEntry.COL_TASK_DURATION));
//                        String todo_deadline_string = todo_cursor.getString(todo_cursor.getColumnIndex(TaskTable.TaskEntry.COL_TASK_DEADLINE));

                        MyCalendarObject todo = new MyCalendarObject(todo_title_string,
                                date_string, todo_duration_string, todo_deadline_string);

                        if(time_gap_morning > Integer.valueOf(todo_duration_string)){

                            calendarObjects.add(todo);
                            time_gap_morning = time_gap_morning - Integer.valueOf(todo_duration_string);


                            details = getDetails(myCalendarDbHelper, todo_cursor, todo_cursor.getColumnIndex(TaskTable.TaskEntry._ID));
                            todo_duration_string = details[1];

                            while(time_gap_morning > Integer.valueOf(todo_duration_string)){
                                todo_title_string = details[0];
                                todo_duration_string = details[1];
                                todo_deadline_string = details[2];

                                todo = new MyCalendarObject(todo_title_string,
                                        date_string, todo_duration_string, todo_deadline_string);

                                calendarObjects.add(todo);

                                time_gap_morning = time_gap_morning - Integer.valueOf(todo_duration_string);

                                int ID = todo_cursor.getColumnIndex(TaskTable.TaskEntry._ID);

                                details = getDetails(myCalendarDbHelper, todo_cursor, ID+1);
                                todo_duration_string = details[1];
                            }

                        }
                        else if(time_gap > Integer.valueOf(todo_duration_string)){
                            calendarObjects.add(todo);
                        }
                        else if(time_gap_evening > Integer.valueOf(todo_duration_string)){
                            calendarObjects.add(todo);
                        }
                        else{
                            todo_cursor.move(ID-1);
                        }

                    }
            }
        }
        else{
            // look for specific date in table and print details while looking for gaps and filling these gaps with todos
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

    private String[] getDetails (MyCalendarDbHelper helper, Cursor cursor, int ID) {
        String[] details = new String[3];

        cursor.move(ID);

        details[0] = cursor.getString(cursor.getColumnIndex(TaskTable.TaskEntry.COL_TASK_TITLE));
        details[1] = cursor.getString(cursor.getColumnIndex(TaskTable.TaskEntry.COL_TASK_DURATION));
        details[2] = cursor.getString(cursor.getColumnIndex(TaskTable.TaskEntry.COL_TASK_DEADLINE));

        return details;
    }
}


