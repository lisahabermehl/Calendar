package com.example.lisahabermehl.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by lisahabermehl on 10/06/2017.
 */

public class MyCalendarDetail extends AppCompatActivity {

    TextView settings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = (TextView) findViewById(R.id.settings);

        Bundle extras = getIntent().getExtras();
        String previousActivity = extras.getString("something2");
        Log.d(String.valueOf(previousActivity), "THIS2");

        settings.setText(previousActivity);


    }
}
