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

import java.util.ArrayList;

/**
 * https://www.sitepoint.com/starting-android-development-creating-todo-app/
 */

public class TodoActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TodoAdapter todoAdapter;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        databaseHelper = new DatabaseHelper(this);

        // initialize the list
        listView = (ListView) findViewById(R.id.list_todo);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                addTodo();
                return true;
            case R.id.menu_calendar:
                startActivity(new Intent(this, MyCalendarActivity.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
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

        ArrayList<TodoObject> todoObject = new ArrayList<>();

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_TITLE));
            String duration = cursor.getString(cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DURATION));
            String deadline = cursor.getString(cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DEADLINE));
            TodoObject to = new TodoObject(title, duration, deadline);
            todoObject.add(to);
        }

        if (todoAdapter == null) {
            todoAdapter = new TodoAdapter(this, 3, todoObject);
            listView.setAdapter(todoAdapter);
        } else {
            todoAdapter.clear();
            todoAdapter.addAll(todoObject);
            todoAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
    }

    private void addTodo() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        final View dialogView = layoutInflater.inflate(R.layout.alert_dialog_add_todo, null);
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
                                int month = deadline.getMonth()+1;
                                int year = deadline.getYear();
                                String deadline = String.valueOf(year + "-0" + month + "-" + day);

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

    public void editTask(View view){
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        final String task_title_old = String.valueOf(taskTextView.getText());

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(TableNames.TodoEntry.TABLE_TODO,
                new String[]{TableNames.TodoEntry._ID,
                        TableNames.TodoEntry.COL_TODO_TITLE,
                        TableNames.TodoEntry.COL_TODO_DURATION,
                        TableNames.TodoEntry.COL_TODO_DEADLINE},
                null, null, null, null, null);

        String title = null;
        String duration = null;
        String deadline = null;

        while (cursor.moveToNext()) {
            if(cursor.getString(cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_TITLE)).equals(task_title_old)){
                title = cursor.getString(cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_TITLE));
                duration = cursor.getString(cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DURATION));
                deadline = cursor.getString(cursor.getColumnIndex(TableNames.TodoEntry.COL_TODO_DEADLINE));
            }

        }

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        final View dialogView = layoutInflater.inflate(R.layout.alert_dialog_add_todo, null);
        final EditText title_edit_text = (EditText) dialogView
                .findViewById(R.id.new_todo);
        title_edit_text.setText(title);
        final EditText duration_edit_text = (EditText) dialogView
                .findViewById(R.id.time_needed);
        duration_edit_text.setText(duration);
        final DatePicker deadline_edit_text = (DatePicker) dialogView
                .findViewById(R.id.task_deadline);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(dialogView)
                .setPositiveButton("ADD",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String task = String.valueOf(title_edit_text.getText());
                                String time = String.valueOf(duration_edit_text.getText());
                                int day = deadline_edit_text.getDayOfMonth();
                                int month = deadline_edit_text.getMonth();
                                int year = deadline_edit_text.getYear();
                                String deadline = String.valueOf(year + "-" + month + "-" + day);

                                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                                ContentValues values = new ContentValues();
                                values.put(TableNames.TodoEntry.COL_TODO_TITLE, task);
                                values.put(TableNames.TodoEntry.COL_TODO_DURATION, time);
                                values.put(TableNames.TodoEntry.COL_TODO_DEADLINE, deadline);
                                db.update(TableNames.TodoEntry.TABLE_TODO,
                                        values, TableNames.TodoEntry.COL_TODO_TITLE + "=?",
                                        new String[]{task_title_old});
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
