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

    // initialize a new calendarObject
    MyCalendarObject nextEvent;

    ListView calendarListView;
    ListView todoListView;

    int time_start, time_gap;
    int time_end = 0;
    int time_end_old = 0;
    int i = 0;
    int time_gap_morning;
    int time_gap_evening;

    String day;

    String[] searchFor = new String[2];

    String date_old = "nog niks";

    int currentTimeMin, currentTimeHour, currentTime, currentModulo, bedtime_start, bedtime_end;

    String title_string, date_string, start_string, end_string, current_date, date_todo;
    int end_last_event_rise_ct, end_todo, duration, start_event;
    int time_gap_between_todos = 5;
//    int bedtime_start = (23*60);
//    int bedtime_end = (7*60);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_list);

        calendarListView = (ListView) findViewById(R.id.list_calendar);

        databaseHelper = new DatabaseHelper(this);

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
            case R.id.menu_day:
                LayoutInflater layoutInflaterDay = LayoutInflater.from(this);
                final View dialogViewDay = layoutInflaterDay.inflate(R.layout.calendar_main, null);

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

        // make a new arraylist where calendarObjects will be stored in
        ArrayList<MyCalendarObject> calendarObjects = new ArrayList<>();

        // move the cursor from myCalendar to the first entry
        cursor.moveToFirst();
        // move the cursor from myTodos to the one before the first entry
        todo_cursor.moveToFirst();
        todo_cursor.moveToPrevious();

        // if not looking for specific date or title
        if(search_for[0].equals("no")){


            Log.d("DATE OLD", date_old);
            Log.d("TIME END OLD", String.valueOf(time_end_old));

            // get details of next event
            String[] everythingToKnow = nextEvent(cursor, date_old, time_end_old);

            // how much time is there between the last event and this new one?

            title_string = everythingToKnow[0];
            date_string = everythingToKnow[1];
            start_string = everythingToKnow[2];
            end_string = everythingToKnow[3];
            end_last_event_rise_ct = Integer.valueOf(everythingToKnow[4]);
            current_date = everythingToKnow[5];
            time_gap = Integer.valueOf(everythingToKnow[6]);
            time_gap_evening = Integer.valueOf(everythingToKnow[7]);
            time_gap_morning = Integer.valueOf(everythingToKnow[8]);
            date_todo = everythingToKnow[9];
            Log.d("0", title_string);
            Log.d("2", start_string);
            Log.d("3", end_string);
            Log.d("4", String.valueOf(end_last_event_rise_ct));
            Log.d("5", current_date);
            Log.d("6", String.valueOf(time_gap));
            Log.d("7", String.valueOf(time_gap_evening));
            Log.d("8", String.valueOf(time_gap_morning));

            // new is the new old, initialize here so it will be passed on the next nextEvent-call
            // date_old to determine if next event is on another day, hence to determine if the user will sleep in between
            date_old = date_string;
            start_event = convertToMins(start_string);
            Log.d("START EVENT", String.valueOf(start_event));

            // get todos one at a time, they are already organized on importance
            while(todo_cursor.moveToNext()) {

                String id_string = todo_cursor.getString(todo_cursor.getColumnIndex(TableNames.TodoEntry._ID));
                String todo_title_string = todo_cursor.getString(todo_cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_TITLE));
                String todo_duration_string = todo_cursor.getString(todo_cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DURATION));
                String todo_deadline_string = todo_cursor.getString(todo_cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DEADLINE));

                duration = Integer.valueOf(todo_duration_string);

                // calculate end of todo
                end_todo = end_last_event_rise_ct + Integer.valueOf(todo_duration_string);

                // convert minutes to HH:mm
                String begin = convertToHour(end_last_event_rise_ct);
                String eind = convertToHour(end_todo);

                // new is the new old
                end_last_event_rise_ct = end_todo;

                // time_gap = start_activity - end_activity_rise_ct
                // is it a "normal" time gap
                if (time_gap > duration) {
                    MyCalendarObject todo = new MyCalendarObject(todo_title_string, date_todo, begin, eind);
                    calendarObjects.add(todo);
                    time_gap = time_gap - duration;
                // is it a time gap that has a part 1 before bedtime and a part 2 after bedtime?
                } else if (time_gap_evening > duration) {

                    MyCalendarObject todo = new MyCalendarObject(todo_title_string, date_todo, begin, eind);
                    calendarObjects.add(todo);

                    i = 0;

                    time_gap_evening = time_gap_evening - duration;
                    // if time gap after bedtime isn't filled yet
                } else if (time_gap_morning > duration) {
                    // first make sure that the bedtimes are printed in Calendar
                    if (i == 0) {

                        String bed_start = convertToHour(bedtime_start);
                        String bed_end = convertToHour(bedtime_end);

                        MyCalendarObject to2 = new MyCalendarObject("Bedtime", date_todo + " +1", bed_start, bed_end);
//                        calendarObjects.add(to2);
                    }

                    // after that we can calculate the start time and end time of a todo
                    if (i == 0) {
                        begin = convertToHour(bedtime_end);
                        eind = convertToHour(bedtime_end + duration);
                        end_last_event_rise_ct = bedtime_end;
                    }

                        MyCalendarObject todo = new MyCalendarObject(todo_title_string, date_string, begin, eind);
                        calendarObjects.add(todo);

                        i = i + 1;

                        time_gap_morning = time_gap_morning - duration;
                    // else all the time gaps are filled and we should look for a next event
                } else {
                    // make sure cursor doesn't skip a to do
                    todo_cursor.moveToPrevious();

                    // gap is filled, so next event can be added
                    nextEvent = new MyCalendarObject(title_string, date_string, start_string, end_string);
                    calendarObjects.add(nextEvent);

                    end_last_event_rise_ct = convertToMins(end_string);

                    cursor.moveToNext();
                    // look for the next event (and check how big the gap is)
                    everythingToKnow = nextEvent(cursor, date_old, time_end_old);

                    title_string = everythingToKnow[0];
                    date_string = everythingToKnow[1];
                    start_string = everythingToKnow[2];
                    end_string = everythingToKnow[3];
                    end_last_event_rise_ct = Integer.valueOf(everythingToKnow[4]);
                    current_date = everythingToKnow[5];
                    time_gap = Integer.valueOf(everythingToKnow[6]);
                    time_gap_morning = Integer.valueOf(everythingToKnow[7]);
                    time_gap_evening = Integer.valueOf(everythingToKnow[8]);
                    Log.d("0", title_string);
                    Log.d("1", date_string);
                    Log.d("2", start_string);
                    Log.d("3", end_string);
                    Log.d("4", String.valueOf(end_last_event_rise_ct));
                    Log.d("5", current_date);
                    Log.d("6", String.valueOf(time_gap));
                    Log.d("7", String.valueOf(time_gap_evening));
                    Log.d("8", String.valueOf(time_gap_morning));

                    // new is the new old
                    date_old = date_string;
                }
            }

            // there are no more todos left
            nextEvent = new MyCalendarObject(title_string, date_string, start_string, end_string);
            calendarObjects.add(nextEvent);

            while (cursor.moveToNext()) {

                everythingToKnow = nextEvent(cursor, date_old, time_end_old);

                title_string = everythingToKnow[0];
                date_string = everythingToKnow[1];
                start_string = everythingToKnow[2];
                end_string = everythingToKnow[3];

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

        String title_string, date_string, start_string, end_string;

        bedtime_start = (23*60);
        bedtime_end = (7*60);

        time_gap_evening = 0;
        time_gap_morning = 0;

        current_date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        currentTimeMin = Integer.valueOf(new SimpleDateFormat("mm").format(Calendar.getInstance().getTime()));

        currentModulo = currentTimeMin % 10;
        if(currentModulo > 0 && currentModulo < 10){
            currentTimeMin = (currentTimeMin - currentModulo) + 10;
        }

        Log.d("CURRENT MODULO", String.valueOf(currentModulo));

        currentTimeHour = Integer.valueOf(new SimpleDateFormat("HH").format(Calendar.getInstance().getTime()));
        currentTime = (currentTimeHour * 60) + currentTimeMin;

        Log.d("CURRENT TIME", String.valueOf(currentTime));
        Log.d("CURRENT REAL TIME", convertToHour(currentTime));

        if (!cursor.isAfterLast()){

            String[] sendBack = new String[10];

            title_string = cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_TITLE));
            date_string = cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_DATE));
            start_string = cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_START));
            end_string = cursor.getString(cursor.getColumnIndex(TableNames.CalendarEntry.COL_CAL_END));
            Log.d("NE: TITLE", title_string);
            Log.d("NE: DATE", date_string);
            Log.d("CURRENT DATE", current_date);
            Log.d("OLD DATE", date_old);
            Log.d("NE: START", start_string);
            Log.d("NE: END", end_string);

            time_end = convertToMins(end_string);
            time_start = convertToMins(start_string);
            Log.d("START MIN", String.valueOf(time_start));
            Log.d("END MIN", String.valueOf(time_end));

            // check if the next event is on the same day as the last event
            if (date_string.equals(date_old)) {

                day = "same day";
                date_todo = date_string;

                Log.d("SAME SAME DAY", day);

                time_gap = time_start - time_end_old;
                end_last_event_rise_ct = time_end_old;

            }
            // it's also possible that date_old is empty, therefore it's impossible to be on the same date as date_new
            // so, check if the first event is today
            // what if the first event is tomorrow
            else if (date_string.equals(current_date) && date_old.equals("nog niks")){

                day = "today";
                date_todo = date_string;

                Log.d("TODAY TODAY TODAY", day);

                // check how much time there is between current time and start of next activity
                time_gap = time_start - currentTime;
                end_last_event_rise_ct = currentTime;
            }
            // else it has to be on another day
            // which means we can fill the gap between the last activity (time_end_old) and bedtime
            // but after filling this gap between the last and bedtime
            // we can also fill the gap between waking up and the next event
            else {

                date_todo = date_old;

                time_gap_evening = bedtime_start - time_end_old;
                time_gap = 0;
                time_gap_morning = time_start - bedtime_end;
            }

            sendBack[0] = title_string;
            sendBack[1] = date_string;
            sendBack[2] = start_string;
            sendBack[3] = end_string;
            sendBack[4] = String.valueOf(end_last_event_rise_ct);
            sendBack[5] = current_date;
            sendBack[6] = String.valueOf(time_gap);
            sendBack[7] = String.valueOf(time_gap_evening);
            sendBack[8] = String.valueOf(time_gap_morning);
            sendBack[9] = date_todo;

            return sendBack;
        }
        else{
            Log.d("PROBLEMOOO", "JUP");
        }
        return null;
    }

}


