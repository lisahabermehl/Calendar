package com.example.lisahabermehl.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by lisahabermehl on 09/06/2017.
 */

public class Todo extends AppCompatActivity {

    private ListView list_view;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        list_view = (ListView) findViewById(R.id.todo_list);
    }
}