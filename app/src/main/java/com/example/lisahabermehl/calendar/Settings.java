package com.example.lisahabermehl.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by lisahabermehl on 09/06/2017.
 */

public class Settings extends AppCompatActivity {

    TextView settings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = (TextView) findViewById(R.id.settings);
    }
}

