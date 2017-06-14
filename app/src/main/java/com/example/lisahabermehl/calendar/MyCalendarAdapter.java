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
 * Created by lisahabermehl on 14/06/2017.
 */

public class MyCalendarAdapter extends ArrayAdapter<MyCalendarObject> {

    Context context;
    int layoutResourceId;
    ArrayList<MyCalendarObject> data = null;

    public MyCalendarAdapter(Context context, int layoutResourceId, ArrayList<MyCalendarObject> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyCalendarObject myCalendarObject = data.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.calendar_item, null);

        TextView description = (TextView) view.findViewById(R.id.calendar_activity);
        TextView duration = (TextView) view.findViewById(R.id.calendar_date);

        description.setText(myCalendarObject.getActivity());
        duration.setText(myCalendarObject.getDate());

        return view;
    }

}
