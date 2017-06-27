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

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyCalendar extends AppCompatActivity {

    DatePicker datePicker;

    DatabaseHelper databaseHelper;

    MyCalendarAdapter myCalendarAdapter;

    ListView calendarListView;
    ListView todoListView;

    int time_start, time_gap, time_end;
    int time_end_old = 40;
    int bedtime_start;
    int bedtime_end;
    int time_gap_morning;
    int time_gap_evening;

    String day;

    String[] searchFor = new String[2];

    String date_old = "nog niks";
    String date_new;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calendar_list);

        calendarListView = (ListView) findViewById(R.id.list_calendar);
        todoListView = (ListView) findViewById(R.id.list_todo);

        databaseHelper = new DatabaseHelper(this);

        time_end = 0;
        time_end_old = 0;

        searchFor[0] = "no";
        searchFor[1] = "no";

        updateUI(searchFor);
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

                databaseHelper = new DatabaseHelper(this);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                db.delete(TableNames.CalendarEntry.TABLE_CALENDAR, null, null);

                Intent intent = new Intent(this, GoogleCalendar.class);
                Bundle extras = new Bundle();
                extras.putString("zero", "get");
                intent.putExtras(extras);
                startActivity(intent);

            case R.id.menu_search:

                final EditText textView = new EditText(this);

                final AlertDialog.Builder search_builder = new AlertDialog.Builder(this);
                search_builder
                        .setView(textView)
                        .setTitle("SEARCH BY TITLE")
                        .setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String title = textView.getText().toString();
                                Toast.makeText(MyCalendar.this, title, Toast.LENGTH_SHORT).show();

                                searchFor[0] = "title";
                                searchFor[1] = title;

                                Toast.makeText(MyCalendar.this, searchFor[1], Toast.LENGTH_SHORT).show();

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

                return true;
            case R.id.insert_event:
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                final View dialogView = layoutInflater.inflate(R.layout.alert_dialog_insert_event, null);

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

                                String startHour = null;
                                String startMinute = null;
                                String endHour = null;
                                String endMinute = null;

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

                                Log.d("TITLE", title);
                                Log.d("START", start);
                                Log.d("END", end);

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
                                datePicker = (DatePicker) dialogViewDay.findViewById(R.id.search_by_date);

                                String day = String.valueOf(datePicker.getDayOfMonth());
                                String month = String.valueOf(datePicker.getMonth() + 1);
                                String year = String.valueOf(datePicker.getYear());
                                String date = year + "-" + "0" + month + "-" + day;
                                Toast.makeText(MyCalendar.this, date, Toast.LENGTH_LONG).show();

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
                return true;
            case R.id.menu_calendar:
                startActivity(new Intent(this, MyCalendar.class));
                return true;
            case R.id.menu_todo:
                startActivity(new Intent(this, Todo.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, Settings.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI(String[] search_for) {

        // open the myCalendar database
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

        Cursor settings_cursor = db.query(TableNames.SettingsEntry.TABLE_SETTINGS,
                new String[]{TableNames.SettingsEntry._ID,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_START,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_END,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_DAYS},
                null, null, null, null, null);

        // make a new arraylist where calendarObjects will be stored in
        ArrayList<MyCalendarObject> calendarObjects = new ArrayList<>();

        // all the strings and ints that we need
        String time_gap_string, title_string, date_string, start_string, end_string, bedtime,
                old_end_string, do_not_disturb_title, do_not_disturb_start,
                do_not_disturb_end, do_not_disturb_days;
        int end_last_event_rise_ct, end_todo, duration, start_event, time_gap_part1, time_gap_part2;

        // move the cursor from myCalendar to the first entry
        cursor.moveToFirst();
        cursor.moveToPrevious();
        // move the cursor from myTodos to the one before the first entry
        todo_cursor.moveToFirst();
        todo_cursor.moveToPrevious();
        // cursor of settings
        settings_cursor.moveToFirst();
        settings_cursor.moveToPrevious();

        // initialize a new calendarObject
        MyCalendarObject nextEvent;

        // check how big the time gap between Todos should be
        SharedPreferences sp = getSharedPreferences("shared_preferences", Activity.MODE_PRIVATE);
        int time_gap_sp = sp.getInt("time_gap", 0);

        ArrayList<MyCalendarObject> doNotDisturbObjects = new ArrayList<>();
        MyCalendarObject doNotDisturb;
//        while(!settings_cursor.isAfterLast()){
//            settings_cursor.moveToNext();
//            // check on at which times the user doesn't want to be disturbed
//            do_not_disturb_title = settings_cursor.getString(settings_cursor
//                    .getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE));
//            do_not_disturb_start = settings_cursor.getString(settings_cursor
//                    .getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_START));
//            do_not_disturb_end = settings_cursor.getString(settings_cursor
//                    .getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_END));
//            do_not_disturb_days = settings_cursor.getString(settings_cursor
//                    .getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_DAYS));
//            doNotDisturb = new MyCalendarObject(do_not_disturb_title, do_not_disturb_start,
//                    do_not_disturb_end, do_not_disturb_days);
//            doNotDisturbObjects.add(doNotDisturb);
//        }

        // if not looking for specific date or title
        if(search_for[0].equals("no")){

            int i = 0;

            // get details of next event
            cursor.moveToNext();
            String[] everythingToKnow = nextEvent(cursor, date_old, time_end_old);

            // how much time is there between the last event and this new one?
            time_gap_string = everythingToKnow[0];
            // details
            title_string = everythingToKnow[1];
            date_string = everythingToKnow[2];
            start_string = everythingToKnow[3];
            end_string = everythingToKnow[4];
            // what is the end time of the last event?
            old_end_string = everythingToKnow[6];

            // new is the new old, initialize here so it will be passed on the next nextEvent-call
            date_old = date_string;
            time_gap = Integer.valueOf(time_gap_string);
            time_gap_part2 = 0;
            start_event = Integer.valueOf(convertToMins(start_string));

            do_not_disturb_title = null;
            do_not_disturb_start = null;
            do_not_disturb_end = null;
            do_not_disturb_days = null;

            // store the end time in minutes in an Integer
            end_last_event_rise_ct = Integer.valueOf(old_end_string);

            // check if there are any "do-not-disturb" times set by the user in this time gap
            while(settings_cursor.moveToNext()){
                do_not_disturb_title = settings_cursor.getString(settings_cursor
                        .getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE));
                do_not_disturb_start = settings_cursor.getString(settings_cursor
                        .getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_START));
                do_not_disturb_end = settings_cursor.getString(settings_cursor
                        .getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_END));
                do_not_disturb_days = settings_cursor.getString(settings_cursor
                        .getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_DAYS));

                int dnd_start = convertToMins(do_not_disturb_start);
                int dnd_end = convertToMins(do_not_disturb_end);

                // if dnd time is somewhere in the timegap
                // split the time gap
                // it's not possible to set a do not disturb time that overlaps
                if(dnd_start > end_last_event_rise_ct && dnd_start < start_event){
                    time_gap = dnd_start - end_last_event_rise_ct;
                    if(start_event > dnd_end){
                        time_gap_part2 = start_event - dnd_end;
                    }
                    else{
                        time_gap_part2 = 0;
                    }
                }
            }

            // get todos one at a time, they are already organized on importance
            while(todo_cursor.moveToNext()) {

                String id_string = todo_cursor.getString(todo_cursor.getColumnIndex(TableNames.TodoEntry._ID));
                String todo_title_string = todo_cursor.getString(todo_cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_TITLE));
                String todo_duration_string = todo_cursor.getString(todo_cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DURATION));
                String todo_deadline_string = todo_cursor.getString(todo_cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DEADLINE));

                duration = Integer.valueOf(todo_duration_string);

                String begin = convertToHour(end_last_event_rise_ct);
                end_todo = end_last_event_rise_ct + Integer.valueOf(todo_duration_string);
                String eind = convertToHour(end_todo);

                // new is the new old
                end_last_event_rise_ct = end_todo;

                // time_gap = start_activity - end_activity_rise_ct
                if(time_gap > duration){
                    MyCalendarObject todo = new MyCalendarObject(todo_title_string, date_string, begin, eind);
                    calendarObjects.add(todo);
                    time_gap = time_gap - duration;
                }
                else {
                    // make sure cursor doesn't skip a to do
                    todo_cursor.moveToPrevious();
                    doNotDisturb = new MyCalendarObject(do_not_disturb_title, do_not_disturb_start,
                            do_not_disturb_end, do_not_disturb_days);
                    // gap is filled, so dnd can be added
                    calendarObjects.add(doNotDisturb);
                }
                if(time_gap_part2 > duration){
                    MyCalendarObject todo = new MyCalendarObject(todo_title_string, date_string, begin, eind);
                    calendarObjects.add(todo);
                    time_gap_part2 = time_gap_part2 - duration;
                }
                else {
                    // make sure cursor doesn't skip a to do
                    todo_cursor.moveToPrevious();

                    // gap is filled, so next event can be added
                    nextEvent = new MyCalendarObject(title_string, date_string, start_string, end_string);
                    calendarObjects.add(nextEvent);

                    time_end_old = convertToMins(end_string);

                    // look for the next event (and check how big the gap is)
                    everythingToKnow = nextEvent(cursor, date_old, time_end_old);

                    time_gap = Integer.valueOf(everythingToKnow[0]);
                    Log.d("TIME GAP", String.valueOf(time_gap));
                    title_string = everythingToKnow[1];
                    date_string = everythingToKnow[2];
                    start_string = everythingToKnow[3];
                    end_string = everythingToKnow[4];
                    bedtime = everythingToKnow[5];
                    old_end_string = everythingToKnow[6];

                    end_last_event_rise_ct = Integer.valueOf(old_end_string);

                    // new is the new old
                    date_old = date_string;
                }
            }

            // there are no more todos left
            nextEvent = new MyCalendarObject(title_string, date_string, start_string, end_string);
            calendarObjects.add(nextEvent);

            while(!cursor.isAfterLast()){
                everythingToKnow = nextEvent(cursor, date_old, time_end_old);

                title_string = everythingToKnow[1];
                date_string = everythingToKnow[2];
                start_string = everythingToKnow[3];
                end_string = everythingToKnow[4];
                bedtime = everythingToKnow[5];

                nextEvent = new MyCalendarObject(title_string, date_string, start_string, end_string);

                calendarObjects.add(nextEvent);

                // new is the new old
                date_old = date_string;
                time_end_old = convertToMins(end_string);
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
    }

    private int convertToMins (String startTime) {

        String[] original = startTime.split(":");
        int time_in_mins = (Integer.valueOf(original[0]) * 60) + Integer.valueOf(original[1]);

        return time_in_mins;
    }

    private String convertToHour (int timeInMins) {

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

    private String[] nextEvent(Cursor cursor, String date_old, int time_end_old){

        String title_string, date_string, start_string, end_string, bedtime;
        String old_end_string = String.valueOf(time_end_old);

        if(!cursor.isAfterLast()){

            String[] sendBack = new String[10];

            title_string = cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_TITLE));
            date_string = cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_DATE));
            start_string = cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_START));
            end_string = cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_END));
            Log.d("TITLE", title_string);

            bedtime = "bedtime_no";

            time_end = convertToMins(end_string);
            time_start = convertToMins(start_string);
            date_new = date_string;

            cursor.moveToNext();

            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            String currentTimeMin = new SimpleDateFormat("mm").format(Calendar.getInstance().getTime());

            int currentTimeMinInt = Integer.valueOf(currentTimeMin);
            int currentMin = currentTimeMinInt % 10;
            if(currentMin > 0 && currentMin < 10){
                currentTimeMinInt = (currentTimeMinInt - currentMin) + 10;
            }
            currentTimeMin = String.valueOf(currentTimeMinInt);

            Log.d("CURRENT MODULO", String.valueOf(currentMin));

            String currentTimeHour = new SimpleDateFormat("HH").format(Calendar.getInstance().getTime());
            int currentTime = (Integer.valueOf(currentTimeHour) * 60) + Integer.valueOf(currentTimeMin);

            // check if the next event is on the same day as the last event
            if (date_new.equals(date_old)) {

                day = "same day";
                time_gap = time_start - time_end_old;

                old_end_string = String.valueOf(time_end_old);
            }
            // it's also possible that date_old is empty, therefore it's impossible to be on the same date as date_new
            // so, check if the first event is today
            // what if the first event is tomorrow
            else if (date_new.equals(currentDate) && date_old.equals("nog niks")){

                day = "today";

                // check how much time there is between current time and start of next activity
                time_gap = time_start - currentTime;
                old_end_string = String.valueOf(currentTime);
            }
            // else it has to be on another day
            // which means we can fill the gap between the last activity (time_end_old) and bedtime
            // but after filling this gap between the last and bedtime
            // we can also fill the gap between waking up and the next event
            else {

                day = "other day";

                if(date_old.equals("nog niks")){
                    time_gap = convertToMins("23:00") - currentTime;
                }

            }

            sendBack[0] = String.valueOf(time_gap);
            sendBack[1] = title_string;
            sendBack[2] = date_string;
            sendBack[3] = start_string;
            sendBack[4] = end_string;
            sendBack[5] = bedtime;
            sendBack[6] = old_end_string;
            sendBack[7] = String.valueOf(time_gap_evening);
            sendBack[8] = String.valueOf(time_gap_morning);

            return sendBack;
        }
        else{
            Log.d("PROBLEMOOO", "JUP");
        }
        return null;
    }

}


