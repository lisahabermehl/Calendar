package com.example.lisahabermehl.calendar;

import android.app.TimePickerDialog;
import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * https://www.sitepoint.com/starting-android-development-creating-todo-app/
 */

public class TimeGap extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TimeGapAdapter timeGapAdapter;

    private ListView timeGapListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_gap_list);

        databaseHelper = new DatabaseHelper(this);

        // initialize the list
        timeGapListView = (ListView) findViewById(R.id.list_time_gap);
        timeGapListView.setLongClickable(true);
        timeGapListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                deleteTimeGap(view);
                return true;
            }
        });
        updateUI();
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
            case R.id.action_add_task:
                addTimeGap();
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

    private void addTimeGap() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        final View dialogView = layoutInflater.inflate(R.layout.alert_dialog_timepicker, null);
        final EditText description = (EditText) dialogView
                .findViewById(R.id.title_time_gap);
        final TimePicker start = (TimePicker) dialogView
                .findViewById(R.id.start_time_gap);
        final TimePicker end = (TimePicker) dialogView
                .findViewById(R.id.end_time_gap);
        start.setIs24HourView(true);
        end.setIs24HourView(true);

        final CheckBox monday = (CheckBox) dialogView.findViewById(R.id.monday);
        final CheckBox tuesday = (CheckBox) dialogView.findViewById(R.id.tuesday);
        final CheckBox wednesday = (CheckBox) dialogView.findViewById(R.id.wednesday);
        final CheckBox thursday = (CheckBox) dialogView.findViewById(R.id.thursday);
        final CheckBox friday = (CheckBox) dialogView.findViewById(R.id.friday);
        final CheckBox saturday = (CheckBox) dialogView.findViewById(R.id.saturday);
        final CheckBox sunday = (CheckBox) dialogView.findViewById(R.id.sunday);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(dialogView)
                .setPositiveButton("ADD",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String task = String.valueOf(description.getText());

                                int start_hour = start.getCurrentHour();
                                int start_minute = start.getCurrentMinute();
                                String start_hour_string = String.valueOf(start_hour);
                                String start_minute_string = String.valueOf(start_minute);
                                if(start_hour > -1 && start_hour < 10){
                                    start_hour_string = "0" + String.valueOf(start_hour);
                                }
                                if(start_minute > -1 && start_minute < 10){
                                    start_minute_string = "0" + String.valueOf(start_minute);
                                }
                                String start_time = start_hour_string + ":" + start_minute_string;

                                int hour = end.getCurrentHour();
                                int minute = end.getCurrentMinute();
                                String end_hour_string = String.valueOf(hour);
                                String end_minute_string = String.valueOf(minute);
                                if(hour > -1 && hour < 10){
                                    end_hour_string = "0" + String.valueOf(hour);
                                }
                                if(minute > -1 && minute < 10){
                                    end_minute_string = "0" + String.valueOf(minute);
                                }
                                String end_time = end_hour_string + ":" + end_minute_string;

                                String[] days = new String[7];
                                String days_final = "-";

                                if(monday.isChecked()){
                                    days[0] = "MON";
                                }
                                if(tuesday.isChecked()){
                                    days[1] = "TUE";
                                }
                                if(wednesday.isChecked()){
                                    days[2] = "WED";
                                }
                                if(thursday.isChecked()){
                                    days[3] = "THU";
                                }
                                if(friday.isChecked()){
                                    days[4] = "FRI";
                                }
                                if(saturday.isChecked()){
                                    days[5] = "SAT";
                                }
                                if(sunday.isChecked()){
                                    days[6] = "SUN";
                                }

                                for(int i = 0; i < 7; i++){
                                    if(days[i] != null){
                                        days_final = days_final + days[i] + "-";
                                    }
                                }

                                Toast.makeText(TimeGap.this, days_final, Toast.LENGTH_SHORT).show();

                                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                                ContentValues values = new ContentValues();
                                values.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE, task);
                                values.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_START, start_time);
                                values.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_END, end_time);
                                values.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_DAYS, days_final);
                                db.insertWithOnConflict(TableNames.SettingsEntry.TABLE_SETTINGS,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUI();
                            }
                        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();
    }

    // to see the updated data you need to call the updateUI method every time the underlying data of the app changes
    // so we add it in two places: onCreate() and after adding a new task using the AlertDialog
    private void updateUI() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(TableNames.SettingsEntry.TABLE_SETTINGS,
                new String[]{TableNames.SettingsEntry._ID,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_START,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_END,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_DAYS},
                null, null, null, null, null);

        ArrayList<TimeGapObject> timeGapObjects = new ArrayList<>();

        int start = 1320;
        int end = 420;
        String days = "MON-TUE-WED-THU-FRI-SAT-SUN";

        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE);
            int idxx = cursor.getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_START);
            int idxxx = cursor.getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_END);
            int idxxxx = cursor.getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_DAYS);
            TimeGapObject to = new TimeGapObject(cursor.getString(idx), cursor.getString(idxx),
                    cursor.getString(idxxx), cursor.getString(idxxxx));
            timeGapObjects.add(to);
            String three = "can move to first";
            Log.d("THIS 3", three);
        }

        if (timeGapAdapter == null) {
            timeGapAdapter = new TimeGapAdapter(this, 0, timeGapObjects);
            timeGapListView.setAdapter(timeGapAdapter);
        } else {
            timeGapAdapter.clear();
            timeGapAdapter.addAll(timeGapObjects);
            timeGapAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
    }

    private void deleteTimeGap(View view){
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.time_gap_days);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TableNames.SettingsEntry.TABLE_SETTINGS,
                TableNames.SettingsEntry.COL_SET_TIME_OFF_DAYS + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }

    public void editTimeGap(View view){
        View parent = (View) view.getParent();
        TextView title_textview = (TextView) parent.findViewById(R.id.time_gap_title);
        final String time_gap_title_old = String.valueOf(title_textview.getText());

        Toast.makeText(this, time_gap_title_old, Toast.LENGTH_LONG).show();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(TableNames.SettingsEntry.TABLE_SETTINGS,
                new String[]{TableNames.SettingsEntry._ID,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_START,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_END,
                        TableNames.SettingsEntry.COL_SET_TIME_OFF_DAYS},
                null, null, null, null, null);

        String title_string = null;
        String start_string = null;
        String end_string = null;
        String days_string = null;

        while(cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE)).equals(time_gap_title_old)){
                title_string = cursor.getString(cursor.getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE));
                start_string = cursor.getString(cursor.getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_START));
                end_string = cursor.getString(cursor.getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_END));
                days_string = cursor.getString(cursor.getColumnIndex(TableNames.SettingsEntry.COL_SET_TIME_OFF_DAYS));
            }
        }

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View dialogView = layoutInflater.inflate(R.layout.alert_dialog_edit_time_gap, null);
        final EditText description = (EditText) dialogView
                .findViewById(R.id.title_time_gap);
        description.setText(title_string);

        final TimePicker start = (TimePicker) dialogView
                .findViewById(R.id.start_time_gap);
        start.setIs24HourView(true);
        String[] original_start;
        original_start = start_string.split(":");
        int start_hour = Integer.valueOf(original_start[0]);
        int start_mins = Integer.valueOf(original_start[1]);
        start.setCurrentHour(start_hour);
        start.setCurrentMinute(start_mins);

        final TimePicker end = (TimePicker) dialogView
                .findViewById(R.id.end_time_gap);
        end.setIs24HourView(true);
        String[] original_end;
        original_end = end_string.split(":");
        int end_hour = Integer.valueOf(original_end[0]);
        int end_mins = Integer.valueOf(original_end[1]);
        end.setCurrentHour(end_hour);
        end.setCurrentMinute(end_mins);

        final CheckBox monday = (CheckBox) dialogView.findViewById(R.id.monday);
        final CheckBox tuesday = (CheckBox) dialogView.findViewById(R.id.tuesday);
        final CheckBox wednesday = (CheckBox) dialogView.findViewById(R.id.wednesday);
        final CheckBox thursday = (CheckBox) dialogView.findViewById(R.id.thursday);
        final CheckBox friday = (CheckBox) dialogView.findViewById(R.id.friday);
        final CheckBox saturday = (CheckBox) dialogView.findViewById(R.id.saturday);
        final CheckBox sunday = (CheckBox) dialogView.findViewById(R.id.sunday);

        String[] checkboxes = days_string.split("-");
        for(int j = 0; j < checkboxes.length; j++){
            if(checkboxes[j].equals("MON")){
                monday.setChecked(true);
            }
            if(checkboxes[j].equals("TUE")){
                tuesday.setChecked(true);
            }
            if(checkboxes[j].equals("WED")){
                wednesday.setChecked(true);
            }
            if(checkboxes[j].equals("THU")){
                thursday.setChecked(true);
            }
            if(checkboxes[j].equals("FRI")){
                friday.setChecked(true);
            }
            if(checkboxes[j].equals("SAT")){
                saturday.setChecked(true);
            }
            if(checkboxes[j].equals("SUN")){
                sunday.setChecked(true);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(dialogView)
                .setPositiveButton("UPDATE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String task = String.valueOf(description.getText());
                                Toast.makeText(TimeGap.this, task, Toast.LENGTH_SHORT).show();

                                int start_hour = start.getCurrentHour();
                                int start_minute = start.getCurrentMinute();
                                String start_hour_string = String.valueOf(start_hour);
                                String start_minute_string = String.valueOf(start_minute);
                                if(start_hour > -1 && start_hour < 10){
                                    start_hour_string = "0" + String.valueOf(start_hour);
                                }
                                if(start_minute > -1 && start_minute < 10){
                                    start_minute_string = "0" + String.valueOf(start_minute);
                                }
                                String start_time = start_hour_string + ":" + start_minute_string;

                                int hour = end.getCurrentHour();
                                int minute = end.getCurrentMinute();
                                String end_hour_string = String.valueOf(hour);
                                String end_minute_string = String.valueOf(minute);
                                if(hour > -1 && hour < 10){
                                    end_hour_string = "0" + String.valueOf(hour);
                                }
                                if(minute > -1 && minute < 10){
                                    end_minute_string = "0" + String.valueOf(minute);
                                }
                                String end_time = end_hour_string + ":" + end_minute_string;

                                String[] days = new String[7];
                                String days_final = "-";

                                if(monday.isChecked()){
                                    days[0] = "MON";
                                }
                                if(tuesday.isChecked()){
                                    days[1] = "TUE";
                                }
                                if(wednesday.isChecked()){
                                    days[2] = "WED";
                                }
                                if(thursday.isChecked()){
                                    days[3] = "THU";
                                }
                                if(friday.isChecked()){
                                    days[4] = "FRI";
                                }
                                if(saturday.isChecked()){
                                    days[5] = "SAT";
                                }
                                if(sunday.isChecked()){
                                    days[6] = "SUN";
                                }

                                for(int i = 0; i < 7; i++){
                                    if(days[i] != null){
                                        days_final = days_final + days[i] + "-";
                                    }
                                }

                                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                                ContentValues values = new ContentValues();
                                values.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE, task);
                                values.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_START, start_time);
                                values.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_END, end_time);
                                values.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_DAYS, days_final);
                                db.update(TableNames.SettingsEntry.TABLE_SETTINGS,
                                        values, TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE + "=?",
                                        new String[]{time_gap_title_old});
                                db.close();
                                updateUI();
                            }
                        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();
    }
}
