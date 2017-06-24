package com.example.lisahabermehl.calendar;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;

/**
 * Created by lisahabermehl on 09/06/2017.
 */

public class Settings extends AppCompatActivity {

    Button edit_bedtime;
    Button sync_calendar;

    DatabaseHelper databaseHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edit_bedtime = (Button) findViewById(R.id.edit_bedtime);
        sync_calendar = (Button) findViewById(R.id.sync_calendar);
    }

    public void syncCalendar(View view) {
        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TableNames.CalendarEntry.TABLE_CALENDAR, null, null);

        Intent intent = new Intent(this, GoogleCalendarTest.class);
        Bundle extras = new Bundle();
        extras.putString("zero", "get");
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void setTimeSpan(View view){
        final EditText timeSpan = new EditText(this);

        AlertDialog.Builder timeSpanBuilder = new AlertDialog.Builder(this);
        timeSpanBuilder
                .setView(timeSpan)
                .setTitle("Enter the amount of days you want to plan for")
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String timeSpanString = timeSpan.getText().toString();
                        Toast.makeText(Settings.this, timeSpanString, Toast.LENGTH_SHORT).show();
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

    public void editBedtime(View view) {

        LayoutInflater layoutInflaterDay = LayoutInflater.from(this);
        final View dialogViewDay = layoutInflaterDay.inflate(R.layout.alert_dialog_timepicker, null);

        AlertDialog.Builder builderDay = new AlertDialog.Builder(this);
        builderDay
                .setView(dialogViewDay)
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        TimePicker bedtimeStart = (TimePicker) dialogViewDay.findViewById(R.id.bedtime_start);
                        TimePicker bedtimeEnd = (TimePicker) dialogViewDay.findViewById(R.id.bedtime_end);

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
}

