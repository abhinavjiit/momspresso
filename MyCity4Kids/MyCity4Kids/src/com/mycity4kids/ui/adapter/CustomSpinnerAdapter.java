package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mycity4kids.R;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context1;
    private ArrayList<String> data;
    public Resources res;
    LayoutInflater inflater;
    private String from;

    public CustomSpinnerAdapter(Context context, ArrayList<String> objects, String from) {
        super(context, R.layout.spinner_row, objects);
        context1 = context;
        data = objects;
        this.from = from;
        inflater = (LayoutInflater) context1
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_row, parent, false);
        TextView tvCategory = (TextView) row.findViewById(R.id.tvCategory);
        TextView reportSpamSpinnerText = row.findViewById(R.id.report_spam_spinner_text);
        if (from.equals("ReportSpam")){
            reportSpamSpinnerText.setVisibility(View.VISIBLE);
            reportSpamSpinnerText.setText(data.get(position).toString());
            tvCategory.setVisibility(View.GONE);
        } else {
            tvCategory.setVisibility(View.VISIBLE);
            reportSpamSpinnerText.setVisibility(View.GONE);
            tvCategory.setText(data.get(position).toString());
        }
        return row;
    }
}