package com.example.lisahabermehl.calendar;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

/**
 * https://www.sitepoint.com/starting-android-development-creating-todo-app/
 */

public class Todo extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TaskAdapter taskAdapter;

    private ListView mTaskListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        databaseHelper = new DatabaseHelper(this);

        // initialize the list
        mTaskListView = (ListView) findViewById(R.id.list_todo);
        mTaskListView.setLongClickable(true);
        mTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                deleteTask(view);
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
                addTask();
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

    // to see the updated data you need to call the updateUI method every time the underlying data of the app changes
    // so we add it in two places: onCreate() and after adding a new task using the AlertDialog
    private void updateUI() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(TableNames.TodoEntry.TABLE_TODO,
                new String[]{TableNames.TodoEntry._ID,
                        TableNames.TodoEntry.COL_TODO_TITLE,
                        TableNames.TodoEntry.COL_TODO_DURATION,
                        TableNames.TodoEntry.COL_TODO_DEADLINE},
                null, null, null, null, null);

        ArrayList<TaskObject> taskObject = new ArrayList<>();

        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_TITLE);
            int idxx = cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DURATION);
            int idxxx = cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DEADLINE);
            TaskObject to = new TaskObject(cursor.getString(idx), cursor.getString(idxx), cursor.getString(idxxx));
            taskObject.add(to);
        }

        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(this, 3, taskObject);
            mTaskListView.setAdapter(taskAdapter);
        } else {
            taskAdapter.clear();
            taskAdapter.addAll(taskObject);
            taskAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
    }

    private void addTask() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        final View dialogView = layoutInflater.inflate(R.layout.alert_dialog, null);
        final EditText description = (EditText) dialogView
                .findViewById(R.id.new_todo);
        final EditText duration = (EditText) dialogView
                .findViewById(R.id.time_needed);
        final DatePicker deadline = (DatePicker) dialogView
                .findViewById(R.id.task_deadline);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(dialogView)
                .setPositiveButton("ADD",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String task = String.valueOf(description.getText());
                                String time = String.valueOf(duration.getText());
                                int day = deadline.getDayOfMonth();
                                int month = deadline.getMonth();
                                int year = deadline.getYear();
                                String deadline = String.valueOf(year + "-" + month + "-" + day);

                                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                                ContentValues values = new ContentValues();
                                values.put(TableNames.TodoEntry.COL_TODO_TITLE, task);
                                values.put(TableNames.TodoEntry.COL_TODO_DURATION, time);
                                values.put(TableNames.TodoEntry.COL_TODO_DEADLINE, deadline);
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

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TableNames.TodoEntry.TABLE_TODO,
                TableNames.TodoEntry.COL_TODO_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }
}
