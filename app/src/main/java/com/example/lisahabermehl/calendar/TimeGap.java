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

                Toast.makeText(TimeGap.this, "Supposed to delete this time gap now", Toast.LENGTH_SHORT).show();
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
        final CheckedTextView monday = (CheckedTextView) dialogView
                .findViewById(R.id.monday);
        final CheckedTextView tuesday = (CheckedTextView) dialogView
                .findViewById(R.id.tuesday);
        final CheckedTextView wednesday = (CheckedTextView) dialogView
                .findViewById(R.id.wednesday);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(dialogView)
                .setPositiveButton("ADD",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String task = String.valueOf(description.getText());

                                int start_hour = start.getCurrentHour();
                                int start_minute = start.getCurrentMinute();
                                String start_time = String.valueOf(start_hour + ":" + start_minute);
                                int hour = end.getCurrentHour();
                                int minute = end.getCurrentMinute();
                                String end_time = String.valueOf(hour + ":" + minute);

                                String[] days = new String[7];

                                Boolean mondayChecked = monday.isChecked();
                                if(mondayChecked.equals(true)){
                                    days[0] = "MON";
                                }
                                else{
                                    days[0] = null;
                                }
                                Boolean tuesdayChecked = tuesday.isChecked();
                                if(tuesdayChecked.equals(true)){
                                    days[1] = "TUE";
                                }
                                else{
                                    days[1] = null;
                                }
                                Boolean wednesdayChecked = wednesday.isChecked();
                                if(wednesdayChecked.equals(true)){
                                    days[2] = "WED";
                                }
                                else{
                                    days[2] = null;
                                }


                                String all_together = "MON-";

                                for(int i = 0; i<3; i++){
                                    if(i == 0 && !days[i].equals(null)){
                                        all_together = days[i] + "-";
                                    }
                                    else if(!days[i].equals(null)){
                                        all_together = all_together + days[i] + "-";
                                    }

                                }


                                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                                ContentValues values = new ContentValues();
                                values.put(TableNames.TodoEntry.COL_TODO_TITLE, task);
                                values.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_START, start_time);
                                values.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_END, end_time);
                                db.insertWithOnConflict(TableNames.TodoEntry.TABLE_TODO,
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

//        if(cursor != null) {
//            if (!cursor.moveToFirst()) {
//
//                String one = "can't move to first";
//                Log.d("This", one);
//
//                db = databaseHelper.getWritableDatabase();
//                ContentValues cv = new ContentValues();
//                cv.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_TITLE, "Default");
//                cv.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_START, start);
//                cv.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_END, end);
//                cv.put(TableNames.SettingsEntry.COL_SET_TIME_OFF_DAYS, days);
//
//                db.insertWithOnConflict(TableNames.SettingsEntry.TABLE_SETTINGS,
//                        null, cv, SQLiteDatabase.CONFLICT_REPLACE);
//
//                db.close();
//            } else if (cursor.moveToFirst()) {
//                String two = "can move to first";
//                Log.d("This", two);
//
//
//
//            }
//        }
    }
}
