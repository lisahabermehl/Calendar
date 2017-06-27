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

public class TimeGapAdapter extends ArrayAdapter<TimeGapObject> {

    Context context;
    int layoutResourceId;
    ArrayList<TimeGapObject> data = null;

    public TimeGapAdapter(Context context, int layoutResourceId, ArrayList<TimeGapObject> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TimeGapObject timeGapObject = data.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.time_gap_item, null);

        TextView title = (TextView) view.findViewById(R.id.time_gap_title);
        TextView start = (TextView) view.findViewById(R.id.time_gap_start);
        TextView end = (TextView) view.findViewById(R.id.time_gap_end);
        TextView days = (TextView) view.findViewById(R.id.time_gap_days);

        title.setText(timeGapObject.getTitleTimeGap());
        start.setText(timeGapObject.getStartTimeGap());
        end.setText(timeGapObject.getEndTimeGap());
        days.setText(timeGapObject.getDaysTimeGap());

        return view;
    }
}

