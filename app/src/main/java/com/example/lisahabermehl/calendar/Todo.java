package com.example.lisahabermehl.calendar;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;

/**
 * https://www.sitepoint.com/starting-android-development-creating-todo-app/
 */
// edittext empty'en
// enter in plaats van button klikken

public class Todo extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TaskDbHelper mHelper;

    // this arrayadapter will help populate the listview with data
    private ArrayAdapter<String> mAdapter;
    // add an instance of the ListView
    private ListView mTaskListView;
//    private EditText taskEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        mHelper = new TaskDbHelper(this);

        // initialize the reference by adding:
        mTaskListView = (ListView) findViewById(R.id.list_todo);
        mTaskListView.setLongClickable(true);
        mTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id){
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

                String colors[] = {"Red","Blue","White","Yellow","Black", "Green","Purple","Orange","Grey"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("NEW TODO");

                LayoutInflater layoutInflater = LayoutInflater.from(this);
                final View dialogView = layoutInflater.inflate(R.layout.alert_dialog, null);
                final EditText input = (EditText) dialogView
                        .findViewById(R.id.txtConnectedBy);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colors);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                final Spinner spinnercategory = (Spinner) dialogView
                        .findViewById(R.id.viewSpin);
                spinnercategory.setAdapter(adapter);
                spinnercategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });

                builder
                        .setView(dialogView)
                        .setPositiveButton("ADD",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .create()
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


//                spinnercategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener());

//                // Array of choices
//                String colors[] = {"Red","Blue","White","Yellow","Black", "Green","Purple","Orange","Grey"};
//
//                // Application of the Array to the Spinner
//                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
//                        android.R.layout.simple_spinner_item, colors);
//                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);// The drop down view
//
//                final Spinner sp = new Spinner(this);
////                sp.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
//                sp.setAdapter(spinnerArrayAdapter);
//
//                final EditText description = new EditText(this);
//                description.setHint("Description");
//
//                final AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                        .setTitle("New TODO")
//                        .setView(description)
//                        .setView(sp)
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.cancel();
//                    }
//                });
//                builder.show();

//                final EditText taskEditText = new EditText(this);
//                AlertDialog dialog = new AlertDialog.Builder(this)
//                        .setTitle("Add a new task")
//                        .setView(taskEditText)
//                        .setPositiveButton("ADD", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which){
//                        String task = String.valueOf(taskEditText.getText());
//                        SQLiteDatabase db = mHelper.getWritableDatabase();
//                        ContentValues values = new ContentValues();
//                        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
//                        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
//                                null,
//                                values,
//                                SQLiteDatabase.CONFLICT_REPLACE);
//                        db.close();
//                        updateUI();
//                    }
//                })
//                        .setNegativeButton("CANCEL", null).create();
//                dialog.show();


    // move the code that was logging the tasks into the following method
    // instead of logging the tasks we will add m into an arraylist of strings
    // checks if mAdapter is created or not
    // if it isn't we'll create and set it as the adapter of the listview

    // to see the updated data you need to call the updateUI method every time the underlying data of the app changes
    // so we add it in two places: onCreate() and after adding a new task using the AlertDialog
    private void updateUI() {
        final ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.todo_item, // what view to use for the items
                    R.id.task_title, // where to put the String of data
                    taskList); // where to get all the data
            mTaskListView.setAdapter(mAdapter); // set it as the adapter of the listview instance
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
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