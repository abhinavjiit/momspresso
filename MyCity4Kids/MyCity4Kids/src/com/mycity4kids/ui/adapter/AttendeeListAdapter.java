package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.R;

import java.util.ArrayList;

public class AttendeeListAdapter extends BaseAdapter {

    private final Activity context;
    private final ArrayList<AttendeeModel> list;

    public AttendeeListAdapter(Activity context, ArrayList<AttendeeModel> list_data) {
        this.context = context;
        this.list = list_data;
        this.list.add(0, new AttendeeModel("0", " ", "All", "#1C55F1"));
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.filter_attendee, null, true);
            holder.colorCode = view.findViewById(R.id.attendee_colorcode);
            holder.name = (TextView) view.findViewById(R.id.attendee_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        try
        {
            holder.colorCode.setBackgroundColor(Color.parseColor(list.get(position).getColorCode()));
            holder.name.setText(list.get(position).getName());
            holder.name.setTextColor(Color.parseColor(list.get(position).getColorCode()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



        return view;
    }

    public static class ViewHolder {
        View colorCode;
        TextView name;
    }

}
