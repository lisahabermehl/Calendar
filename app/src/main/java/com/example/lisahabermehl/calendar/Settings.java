package com.example.lisahabermehl.calendar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;

/**
 * Created by lisahabermehl on 09/06/2017.
 */

public class Settings extends AppCompatActivity {

    Button edit_bedtime_start;
    Button edit_bedtime_end;

    DialogInterface dialogInterface;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edit_bedtime_start = (Button) findViewById(R.id.edit_bedtime_start);
        edit_bedtime_end = (Button) findViewById(R.id.edit_bedtime_end);


    }

    public void editBedtimeStart(View view) {

        LayoutInflater layoutInflaterDay = LayoutInflater.from(this);
        final View dialogViewDay = layoutInflaterDay.inflate(R.layout.alert_dialog_timepicker, null);

        AlertDialog.Builder builderDay = new AlertDialog.Builder(this);
        builderDay
                .setView(dialogViewDay)
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
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

    }

    public void editBedtimeEnd(View view) {

        LayoutInflater layoutInflaterDay = LayoutInflater.from(this);
        final View dialogViewDay = layoutInflaterDay.inflate(R.layout.alert_dialog_timepicker, null);

        AlertDialog.Builder builderDay = new AlertDialog.Builder(this);
        builderDay
                .setView(dialogViewDay)
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
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

    }
}

