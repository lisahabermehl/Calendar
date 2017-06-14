package com.example.lisahabermehl.calendar;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lisahabermehl on 14/06/2017.
 */

public class TaskAdapter extends ArrayAdapter<TaskObject> {

    Context context;
    int layoutResourceId;
    TaskObject data[] = null;

    public TaskAdapter(Context context, int layoutResourceId, TaskObject[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WeatherHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new WeatherHolder();
            holder.imgIcon = (TextView)row.findViewById(R.id.task_title);
            holder.txtTitle = (TextView)row.findViewById(R.id.task_duration);

            row.setTag(holder);
        }
        else
        {
            holder = (WeatherHolder)row.getTag();
        }

        TaskObject weather = data[position];
        holder.txtTitle.setText(weather.getTask());
        holder.imgIcon.setText(weather.getDuration());

        return row;
    }

    static class WeatherHolder
    {
        TextView imgIcon;
        TextView txtTitle;
    }
}

