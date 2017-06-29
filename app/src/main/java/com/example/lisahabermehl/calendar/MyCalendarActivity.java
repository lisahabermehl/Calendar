package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 08/06/2017.
 *
 * Source code: http://www.viralandroid.com/2015/11/android-calendarview-example.html
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Handler;

public class MyCalendarActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    MyCalendarAdapter myCalendarAdapter;
    MyCalendarObject nextEvent;

    ListView calendarListView;
    SwipeRefreshLayout swipeRefreshLayout;

    int time_start, time_gap, time_gap_morning, time_gap_evening, time_between_todos;
    int current_time_min, current_time, current_modulo, bedtime_start, bedtime_end;
    int end_last_event_rise_ct, end_todo, duration;
    int time_end = 0;
    int time_end_old = 0;
    int i = 0;

    String[] searchFor = new String[2];
    String date_old = "no date yet";
    String title_string, date_string, current_date, date_todo, begin, eind;
    String todo_title_string, todo_deadline_string;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_list);

        calendarListView = (ListView) findViewById(R.id.list_calendar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        databaseHelper = new DatabaseHelper(this);

        searchFor[0] = "no";
        searchFor[1] = "no";

        updateUI(searchFor);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                time_end = 0;
                time_end_old = 0;
                date_old = "no date yet";
                updateUI(searchFor);
            }
        });
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
            case R.id.refresh:
                refreshGoogleCalendar();
                return true;
            case R.id.search_by_title:
                searchByTitle();
                return true;
            case R.id.search_by_date:
                searchByDate();
                return true;
            case R.id.insert_event:
                addNewEvent();
                refreshGoogleCalendar();
                return true;
            case R.id.menu_todo:
                startActivity(new Intent(this, TodoActivity.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI(String[] search_for) {
        // open the myCalendarActivity database
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(TableNames.CalendarEntry.TABLE_CALENDAR,
                new String[]{TableNames.CalendarEntry._ID,
                        TableNames.CalendarEntry.COL_CAL_TITLE,
                        TableNames.CalendarEntry.COL_CAL_DATE,
                        TableNames.CalendarEntry.COL_CAL_START,
                        TableNames.CalendarEntry.COL_CAL_END},
                null, null, null, null, TableNames.CalendarEntry.COL_CAL_DATE + ", " +
                        TableNames.CalendarEntry.COL_CAL_START + " ASC");

        // open the myTodo database
        // show data from to do list: first deadline first, and tasks with the "biggest time needed" first
        Cursor todo_cursor = db.query(TableNames.TodoEntry.TABLE_TODO,
                new String[]{TableNames.TodoEntry._ID,
                        TableNames.TodoEntry.COL_TODO_TITLE,
                        TableNames.TodoEntry.COL_TODO_DURATION,
                        TableNames.TodoEntry.COL_TODO_DEADLINE},
                null, null, null, null, TableNames.TodoEntry.COL_TODO_DEADLINE + " ASC "+ ", " +
                        TableNames.TodoEntry.COL_TODO_DURATION + " DESC");

        SharedPreferences sp = getSharedPreferences("shared_preferences", Activity.MODE_PRIVATE);
        int time_between_todos = sp.getInt("time_gap", 5);

        ArrayList<MyCalendarObject> calendarObjects = new ArrayList<>();

        cursor.moveToFirst();
        todo_cursor.moveToFirst();
        todo_cursor.moveToPrevious();

        if(search_for[0].equals("no")){
            String[] everythingToKnow = nextEvent(cursor, date_old, time_end_old);

            title_string = everythingToKnow[0];
            date_string = everythingToKnow[1];
            time_start = Integer.valueOf(everythingToKnow[2]);
            time_end = Integer.valueOf(everythingToKnow[3]);
            end_last_event_rise_ct = Integer.valueOf(everythingToKnow[4]);
            current_date = everythingToKnow[5];
            time_gap = Integer.valueOf(everythingToKnow[6]);
            time_gap_evening = Integer.valueOf(everythingToKnow[7]);
            time_gap_morning = Integer.valueOf(everythingToKnow[8]);
            date_todo = everythingToKnow[9];
            current_time = Integer.valueOf(everythingToKnow[10]);

            while (time_end < current_time & date_string.equals(current_date)){
                cursor.moveToNext();
                everythingToKnow = nextEvent(cursor, date_old, time_end_old);
                date_string = everythingToKnow[1];
                time_end = Integer.valueOf(everythingToKnow[3]);
                current_date = everythingToKnow[5];
                current_time = Integer.valueOf(everythingToKnow[10]);
            }
            date_old = date_string;

            while (todo_cursor.moveToNext()) {
                todo_title_string = todo_cursor.getString(todo_cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_TITLE));
                duration = Integer.valueOf(todo_cursor.getString(todo_cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DURATION)));
                todo_deadline_string = todo_cursor.getString(todo_cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DEADLINE));

                if (time_gap > duration | time_gap_evening > duration) {
                    begin = convertToHour(end_last_event_rise_ct + time_between_todos);
                    end_todo = end_last_event_rise_ct + duration;
                    eind = convertToHour(end_todo);

                    if(todo_deadline_string.equals(date_todo)){
                        Toast.makeText(this, "Oops you can't finish " + todo_title_string + " on time!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        MyCalendarObject todo = new MyCalendarObject(todo_title_string, date_todo, begin, eind);
                        calendarObjects.add(todo);
                        if (time_gap == 0){
                            i = 0;

                            time_gap_evening = time_gap_evening - (duration + time_between_todos);
                            end_last_event_rise_ct = end_last_event_rise_ct + duration;
                        }
                        else if (time_gap_evening == 0){
                            time_gap = time_gap - (duration + time_between_todos);
                            // new is the new old
                            end_last_event_rise_ct = end_todo;
                        }
                    }
                } else if (time_gap_morning > duration) {
                    if(todo_deadline_string.equals(date_string)){
                        Toast.makeText(this, "Oops you can't finish " + todo_title_string + " on time!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        if (i == 0){
                            MyCalendarObject todo = new MyCalendarObject(todo_title_string, date_string,
                                    convertToHour(bedtime_end + time_between_todos),
                                    convertToHour(bedtime_end + duration));
                            calendarObjects.add(todo);
                            end_last_event_rise_ct = bedtime_end + duration;
                            time_gap_evening = 0;
                        }
                        else{
                            MyCalendarObject todo = new MyCalendarObject(todo_title_string, date_string,
                                    convertToHour(end_last_event_rise_ct + time_between_todos),
                                    convertToHour(end_last_event_rise_ct + duration));
                            calendarObjects.add(todo);
                            end_last_event_rise_ct = end_last_event_rise_ct + duration;
                        }
                        time_gap_morning = time_gap_morning - (duration + time_between_todos);
                        i = i + 1;
                    }
                } else {
                    // make sure cursor doesn't skip a to do
                    todo_cursor.moveToPrevious();

                    // gap is filled, so next event can be added
                    nextEvent = new MyCalendarObject(title_string, date_string,
                            convertToHour(time_start), convertToHour(time_end));
                    calendarObjects.add(nextEvent);

                    end_last_event_rise_ct = time_end;

                    cursor.moveToNext();
                    everythingToKnow = nextEvent(cursor, date_old, end_last_event_rise_ct);

                    title_string = everythingToKnow[0];
                    date_string = everythingToKnow[1];
                    time_start = Integer.valueOf(everythingToKnow[2]);
                    time_end = Integer.valueOf(everythingToKnow[3]);
                    end_last_event_rise_ct = Integer.valueOf(everythingToKnow[4]);
                    current_date = everythingToKnow[5];
                    time_gap = Integer.valueOf(everythingToKnow[6]);
                    time_gap_morning = Integer.valueOf(everythingToKnow[7]);
                    time_gap_evening = Integer.valueOf(everythingToKnow[8]);

                    // new is the new old
                    date_old = date_string;
                }
            }
            // there are no more todos left
            nextEvent = new MyCalendarObject(title_string, date_string,
                    convertToHour(time_start), convertToHour(time_end));
            calendarObjects.add(nextEvent);

            while (cursor.moveToNext()) {
                everythingToKnow = nextEvent(cursor, date_old, time_end_old);
                title_string = everythingToKnow[0];
                date_string = everythingToKnow[1];
                time_start = Integer.valueOf(everythingToKnow[2]);
                time_end = Integer.valueOf(everythingToKnow[3]);

                nextEvent = new MyCalendarObject(title_string, date_string,
                        convertToHour(time_start), convertToHour(time_end));
                calendarObjects.add(nextEvent);

                // new is the new old
                date_old = date_string;
                time_end_old = time_end;
            }
        }
        else if (search_for[0].equals("date")){
            // look for specific date in table and print details while looking for gaps and filling these gaps with todos
            // only add to calendarObjects when date_string == search_for[1]
        }
        else if(search_for[0].equals("title")){
            // only add to calendarObjects when title_string == search_for[1]
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
        todo_cursor.close();
        db.close();

        swipeRefreshLayout.setRefreshing(false);
    }

    private int convertToMins(String startTime) {

        String[] original = startTime.split(":");
        int time_in_mins = (Integer.valueOf(original[0]) * 60) + Integer.valueOf(original[1]);

        return time_in_mins;
    }

    private String convertToHour(int timeInMins) {

        int hours = 0;

        while(timeInMins > 59) {
            timeInMins = timeInMins - 60;
            hours = hours + 1;
        }

        String time_in_hours = String.valueOf(hours) + ":" + String.valueOf(timeInMins);

        if(timeInMins >= 0 && timeInMins <= 9){
            time_in_hours = String.valueOf(hours) + ":0" + String.valueOf(timeInMins);
        }

        return time_in_hours;
    }

    private void refreshGoogleCalendar(){
        SQLiteDatabase db = new DatabaseHelper(this).getWritableDatabase();
        db.delete(TableNames.CalendarEntry.TABLE_CALENDAR, null, null);

        Intent intent = new Intent(this, GoogleCalendar.class);
        Bundle extras = new Bundle();
        extras.putString("zero", "get");
        intent.putExtras(extras);
        startActivity(intent);
    }

    private void addNewEvent(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View dialogView = layoutInflater.inflate(R.layout.alert_dialog_add_event, null);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(dialogView)
                .setTitle("Add event")
                .setPositiveButton("INSERT EVENT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        EditText editText = (EditText) dialogView.findViewById(R.id.event_title);
                        String title = editText.getText().toString();

                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.event_date_start);
                        String date = String.valueOf(datePicker.getYear()) + "-" +
                                String.valueOf(datePicker.getMonth() + 1) + "-" +
                                String.valueOf(datePicker.getDayOfMonth());

                        TimePicker startTime = (TimePicker) dialogView.findViewById(R.id.event_time_start);
                        TimePicker endTime = (TimePicker) dialogView.findViewById(R.id.event_time_end);

                        String startHour, startMinute, endHour, endMinute;

                        if(startTime.getCurrentHour() > -1 && startTime.getCurrentHour() < 10){
                            startHour = "0" + String.valueOf(startTime.getCurrentHour());
                        }
                        else{
                            startHour = String.valueOf(startTime.getCurrentHour());
                        }
                        if(startTime.getCurrentMinute() > -1 && startTime.getCurrentMinute() < 10){
                            startMinute = "0" + String.valueOf(startTime.getCurrentMinute());
                        }
                        else{
                            startMinute = String.valueOf(startTime.getCurrentMinute());

                        }
                        if(endTime.getCurrentHour() > -1 && endTime.getCurrentHour() < 10){
                            endHour = "0" + String.valueOf(endTime.getCurrentHour());
                        }
                        else{
                            endHour = String.valueOf(endTime.getCurrentHour());
                        }
                        if(endTime.getCurrentMinute() > -1 & endTime.getCurrentMinute() < 10){
                            endMinute = "0" + String.valueOf(endTime.getCurrentMinute());
                        }
                        else{
                            endMinute = String.valueOf(endTime.getCurrentMinute());
                        }

                        String start = date + " " + startHour + ":" + startMinute;
                        String end = date + " " + endHour + ":" + endMinute;

                        // startActivity to show activities on a specific day
                        Bundle extras = new Bundle();
                        extras.putString("zero", "add");
                        extras.putString("one", title);
                        extras.putString("two", start);
                        extras.putString("three", end);
                        startActivity(new Intent(getApplicationContext(), GoogleCalendar.class)
                                .putExtras(extras));
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

    private void searchByTitle(){
        final EditText textView = new EditText(this);
        final AlertDialog.Builder search_builder = new AlertDialog.Builder(this);
        search_builder
                .setView(textView)
                .setTitle("SEARCH BY TITLE")
                .setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String title = textView.getText().toString();
                        searchFor[0] = "title";
                        searchFor[1] = title;
                        updateUI(searchFor);
                    }
                })
                .setNegativeButton("SHOW EVERYTHING", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        searchFor[0] = "no";
                        searchFor[1] = "no";
                        updateUI(searchFor);
                    }
                })
                .create()
                .show();

    }

    private void searchByDate(){
        final DatePicker datePicker = new DatePicker(this);
        datePicker.setSpinnersShown(false);
        AlertDialog.Builder builderDay = new AlertDialog.Builder(this);
        builderDay
                .setView(datePicker)
                .setTitle("SEARCH BY DATE")
                .setPositiveButton("SELECT DATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String date = String.valueOf(datePicker.getYear())
                                + "-0" + String.valueOf(datePicker.getMonth() + 1)
                                + "-" + String.valueOf(datePicker.getDayOfMonth());
                        searchFor[0] = "date";
                        searchFor[1] = date;
                        updateUI(searchFor);
                    }
                })
                .setNegativeButton("SHOW ALL DATES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        searchFor[0] = "no";
                        searchFor[1] = "no";
                        updateUI(searchFor);
                    }
                })
                .create()
                .show();
    }

    private String[] nextEvent(Cursor cursor, String date_old, int end_last_event_rise_ct){

        String title_string, date_string;

        SharedPreferences sp = getSharedPreferences("shared_preferences", Activity.MODE_PRIVATE);
        bedtime_start = (sp.getInt("bedtime_start_hour", 23)*60) + sp.getInt("bedtime_start_minute", 0);
        bedtime_end = (sp.getInt("bedtime_end_hour", 7)*60) + sp.getInt("bedtime_end_minute", 0);
        time_between_todos = sp.getInt("time_gap", 1);

        time_gap_evening = 0;
        time_gap_morning = 0;

        current_date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        current_time_min = Integer.valueOf(new SimpleDateFormat("mm").format(Calendar.getInstance().getTime()));

        current_modulo = current_time_min % 10;
        if(current_modulo > 0 && current_modulo < 10){
            current_time_min = (current_time_min - current_modulo) + 10;
        }
        current_time = (Integer.valueOf(new SimpleDateFormat("HH").format(Calendar.getInstance().getTime())) * 60)
                + current_time_min;

        if (!cursor.isAfterLast()){
            String[] sendBack = new String[11];
            title_string = cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_TITLE));
            date_string = cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_DATE));
            time_start = convertToMins(cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_START)));
            time_end = convertToMins(cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_END)));

            // check if the next event is on the same day as the last event
            if (date_string.equals(date_old)) {
                date_todo = date_string;
                time_gap = time_start - end_last_event_rise_ct;
                // end_last_event stays the same
            }
            else if (date_string.equals(current_date) && date_old.equals("no date yet")){
                date_todo = date_string;

                time_gap = time_start - current_time;
                end_last_event_rise_ct = current_time;
                Log.d("TODAY", String.valueOf(end_last_event_rise_ct));
            }
            else if (time_end_old == 0 && date_old.equals("no date yet")){
                date_todo = current_date;
                time_gap_evening = bedtime_start - current_time;
                end_last_event_rise_ct = current_time;
                time_gap = 0;
                time_gap_morning = time_start - bedtime_end;
            }
            else{
                date_todo = date_old;
                Log.d("ELSE", String.valueOf(end_last_event_rise_ct));
                time_gap_evening = bedtime_start - time_end_old;
                time_gap = 0;
                time_gap_morning = time_start - bedtime_end;
            }

            sendBack[0] = title_string;
            sendBack[1] = date_string;
            sendBack[2] = String.valueOf(time_start);
            sendBack[3] = String.valueOf(time_end);
            sendBack[4] = String.valueOf(end_last_event_rise_ct);
            sendBack[5] = current_date;
            sendBack[6] = String.valueOf(time_gap);
            sendBack[7] = String.valueOf(time_gap_evening);
            sendBack[8] = String.valueOf(time_gap_morning);
            sendBack[9] = date_todo;
            sendBack[10] = String.valueOf(current_time);

            return sendBack;
        }
        else{
            Log.d("PROBLEMOOO", "JUP");
        }
        return null;
    }
}


