package com.example.lisahabermehl.calendar;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * https://www.sitepoint.com/starting-android-development-creating-todo-app/
 */

public class Todo extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TaskDbHelper mHelper;

    // this arrayadapter will help populate the listview with data
    private ArrayAdapter<String> mAdapter;
    private TaskAdapter taskAdapter;

    // add an instance of the ListView
    private ListView mTaskListView;

    public String duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        mHelper = new TaskDbHelper(this);

        // initialize the reference by adding:
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
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_task:

                LayoutInflater layoutInflater = LayoutInflater.from(this);

                final View dialogView = layoutInflater.inflate(R.layout.alert_dialog, null);
                final EditText description = (EditText) dialogView
                        .findViewById(R.id.new_todo);
                final EditText duration = (EditText) dialogView
                        .findViewById(R.id.time_needed);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setView(dialogView)
                        .setPositiveButton("ADD",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {
                                        String task = String.valueOf(description.getText());
                                        String time = String.valueOf(duration.getText());

                                        SQLiteDatabase db = mHelper.getWritableDatabase();

                                        ContentValues values = new ContentValues();
                                        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                                        values.put(TaskContract.TaskEntry.COL_TASK_DURATION, time);
                                        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // move the code that was logging the tasks into the following method
    // instead of logging the tasks we will add m into an arraylist of strings
    // checks if mAdapter is created or not
    // if it isn't we'll create and set it as the adapter of the listview

    // to see the updated data you need to call the updateUI method every time the underlying data of the app changes
    // so we add it in two places: onCreate() and after adding a new task using the AlertDialog
    private void updateUI() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COL_TASK_TITLE,
                        TaskContract.TaskEntry.COL_TASK_DURATION},
                null, null, null, null, null);

        ArrayList<TaskObject> taskObject = new ArrayList<>();

        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            int idxx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DURATION);
            TaskObject to = new TaskObject(cursor.getString(idx), cursor.getString(idxx));
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

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }
}
