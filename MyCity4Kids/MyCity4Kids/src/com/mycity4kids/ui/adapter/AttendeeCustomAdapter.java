package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.newmodels.AttendeeModel;

import java.util.ArrayList;

/**
 * Created by user on 08-06-2015.
 */
public class AttendeeCustomAdapter extends BaseAdapter {

    Context context;
    ArrayList<AttendeeModel> dataList;

    public AttendeeCustomAdapter(Context context, ArrayList<AttendeeModel> dataList) {
        this.context = context;
        this.dataList = dataList;

    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.aa_attendee_item, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.attendee);
            holder.dots = (ImageView) view.findViewById(R.id.dot);
            holder.checkBox = (CheckBox) view.findViewById(R.id.check);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(dataList.get(position).getName());

        try {
            if (dataList.get(position).getColorCode().equals("#ff8a65")) {
                holder.dots.setImageResource(R.drawable.color_1xxhdpi);
            } else if (dataList.get(position).getColorCode().equals("#ef5350")) {
                holder.dots.setImageResource(R.drawable.color_2xxhdpi);
            } else if (dataList.get(position).getColorCode().equals("#ff1744")) {
                holder.dots.setImageResource(R.drawable.color_3xxhdpi);
            } else if (dataList.get(position).getColorCode().equals("#d81b60")) {
                holder.dots.setImageResource(R.drawable.color_4xxhdpi);
            } else if (dataList.get(position).getColorCode().equals("#ab47bc")) {
                holder.dots.setImageResource(R.drawable.color_5xxhdpi);
            } else if (dataList.get(position).getColorCode().equals("#7e57c2")) {
                holder.dots.setImageResource(R.drawable.color_6xxhdpi);
            } else if (dataList.get(position).getColorCode().equals("#3949ab")) {
                holder.dots.setImageResource(R.drawable.color_7xxhdpi);
            } else if (dataList.get(position).getColorCode().equals("#42a5f5")) {
                holder.dots.setImageResource(R.drawable.color_8xxhdpi);
            } else if (dataList.get(position).getColorCode().equals("#00acc1")) {
                holder.dots.setImageResource(R.drawable.color_9xxhdpi);
            } else if (dataList.get(position).getColorCode().equals("#26a69a")) {
                holder.dots.setImageResource(R.drawable.color_10xxhdpi);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        final ViewHolder finalHolder = holder;

        holder.checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    //Case 1
                    if (position == 0) // chking for all case
                    {
                        // set all other chk
                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.get(i).setCheck(true);

                        }
                        notifyDataSetChanged();
                    }

                    dataList.get(position).setCheck(true);
                } else {
                    if (position == 0) {

                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.get(i).setCheck(false);

                        }
                        notifyDataSetChanged();
                    } else {
                        if (dataList.get(0).getCheck() == true) {

                            dataList.get(0).setCheck(false);

                        }
                        notifyDataSetChanged();
                    }

                    dataList.get(position).setCheck(false);
                }
                //case 2

            }
        });


        if (dataList.get(position).getCheck() == true)
        {
            holder.checkBox.setChecked(true);
        }

        else{
            holder.checkBox.setChecked(false);
        }


//        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (finalHolder.checkBox.isChecked()) {
//
//                    if (position == 0) // chking for all case
//                    {
//                        // set all other chk
//                        for (AttendeeModel model : dataList) {
//                            model.setCheck(true);
//                            notifyDataSetChanged();
//                        }
//
//                    }
//
//                    dataList.get(position).setCheck(true);
//                } else {
//
//                    if (position == 0) {
//
//                        for (AttendeeModel model : dataList) {
//                            model.setCheck(false);
//                            notifyDataSetChanged();
//                        }
//                    } else {
//                        if (dataList.get(0).getCheck() == true) {
//
//                            dataList.get(0).setCheck(false);
//                            notifyDataSetChanged();
//                        }
//                    }
//
//                    dataList.get(position).setCheck(false);
//                }
//
//            }
//        });


        return view;
    }

    public static class ViewHolder {
        TextView name;
        ImageView dots;
        CheckBox checkBox;
    }

    public ArrayList<AttendeeModel> getAttendeeList() {
        return dataList;
    }

}
