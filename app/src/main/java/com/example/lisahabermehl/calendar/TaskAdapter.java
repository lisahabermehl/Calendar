package com.example.lisahabermehl.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lisahabermehl on 14/06/2017.
 */

public class TaskAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<TaskObject> objects;

    private class ViewHolder {
        TextView textView1;
        TextView textView2;
    }

    public TaskAdapter(Context context, ArrayList<TaskObject> objects) {
        layoutInflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    public int getCount() {
        return objects.size();
    }

    public TaskObject getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.todo_item, null);
            holder.textView1 = (TextView) convertView.findViewById(R.id.task_title);
            holder.textView2 = (TextView) convertView.findViewById(R.id.task_duration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView1.setText(objects.get(position).getDuration());
        holder.textView2.setText(objects.get(position).getTask());
        return convertView;
    }
}

