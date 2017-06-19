package com.example.lisahabermehl.calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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

    String dateOne = "nogniks";
    String dateTwo;

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

        TextView activity = (TextView) view.findViewById(R.id.calendar_activity);
        TextView date = (TextView) view.findViewById(R.id.calendar_date);
        TextView start = (TextView) view.findViewById(R.id.calendar_start);
        TextView end = (TextView) view.findViewById(R.id.calendar_end);

//        dateTwo = myCalendarObject.getDate();
//        Log.d("ACT 2", dateTwo);

//        if (dateOne.equals(dateTwo)) {
            date.setText(myCalendarObject.getDate());
            activity.setText(myCalendarObject.getActivity());
            start.setText(myCalendarObject.getStart());
            end.setText(myCalendarObject.getEnd());
//        }
//        else {
//            activity.setText(myCalendarObject.getActivity());
//            start.setText(myCalendarObject.getStart());
//            end.setText(myCalendarObject.getEnd());
//        }

//        dateOne = myCalendarObject.getDate();
//        Log.d("ACT 1", dateOne);

        return view;
    }

}
