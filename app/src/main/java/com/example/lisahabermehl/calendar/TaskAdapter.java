package com.example.lisahabermehl.calendar;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * https://www.sitepoint.com/custom-data-layouts-with-your-own-android-arrayadapter/
 */

public class TaskAdapter extends ArrayAdapter<TaskObject> {

    Context context;
    int layoutResourceId;
    ArrayList<TaskObject> data = null;

    public TaskAdapter(Context context, int layoutResourceId, ArrayList<TaskObject> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskObject taskObject = data.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.todo_item, null);

        TextView description = (TextView) view.findViewById(R.id.task_title);
        TextView duration = (TextView) view.findViewById(R.id.task_duration);
        TextView deadline = (TextView) view.findViewById(R.id.task_deadline);

        description.setText(taskObject.getTask());
        duration.setText(taskObject.getDuration());
        deadline.setText(taskObject.getDeadline());

        return view;
    }
}

