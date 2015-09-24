package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.newmodels.AppointmentMappingModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by manish.soni on 26-06-2015.
 */
public class AdapterHomeAppointment extends BaseAdapter {
    Context context;
    ArrayList<AppointmentMappingModel> datalist;

    public AdapterHomeAppointment(Context context, ArrayList<AppointmentMappingModel> datalist) {
        this.context = context;
        this.datalist = datalist;
    }


    @Override
    public int getCount() {
        return datalist == null ? 0 : datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        final ViewHolder holder;

        LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            convertView = infalInflater.inflate(R.layout.aa_cal_list_item, null);
            holder = new ViewHolder();

            holder.hasAppontment = (LinearLayout) convertView.findViewById(R.id.appointment_layout);
            holder.noAppontment = (TextView) convertView.findViewById(R.id.no_appointment);
            holder.time = (TextView) convertView.findViewById(R.id.appoint_time);
            holder.title = (TextView) convertView.findViewById(R.id.appoint_title);
            holder.address = (TextView) convertView.findViewById(R.id.appoint_address);
            holder.colorCode = (LinearLayout) convertView.findViewById(R.id.colorCode);
            holder.ifNotes = (ImageView) convertView.findViewById(R.id.appointment_notes);
            holder.eventIcon = (ImageView) convertView.findViewById(R.id.event_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (datalist.get(i).getAppointment_name() == null) {
            holder.noAppontment.setVisibility(View.VISIBLE);
            holder.hasAppontment.setVisibility(View.GONE);
        } else {

            holder.hasAppontment.setVisibility(View.VISIBLE);
            holder.noAppontment.setVisibility(View.GONE);

            LinearLayout.LayoutParams dummyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

            holder.time.setText(getTime(datalist.get(i).getStarttime()));
            holder.title.setText(datalist.get(i).getAppointment_name());
            holder.address.setText(datalist.get(i).getLocality());
            holder.colorCode.removeAllViews();

            if (datalist.get(i).isHasNotes()) {
                holder.ifNotes.setVisibility(View.VISIBLE);
            } else {
                holder.ifNotes.setVisibility(View.GONE);
            }

            for (int j = 0; j < datalist.get(i).getAttendee().size(); j++) {

                View dummyView = new View(context);
                dummyView.setLayoutParams(dummyParams);

                try {
                    dummyView.setBackgroundColor(Color.parseColor(datalist.get(i).getAttendee().get(j).getColorCode()));
                    dummyParams.weight = 1f;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                holder.colorCode.addView(dummyView);
            }

            if (datalist.get(i).getIs_bday() == 1 || datalist.get(i).getIs_holiday() == 1) {
                holder.time.setVisibility(View.GONE);
                holder.eventIcon.setVisibility(View.VISIBLE);

                if (datalist.get(i).getIs_holiday() == 1) {
                    holder.eventIcon.setBackgroundResource(R.drawable.holiday);
                } else if (datalist.get(i).getIs_bday() == 1) {
                    holder.eventIcon.setBackgroundResource(R.drawable.cake_xxhdpi);
                }
            } else {
                holder.time.setVisibility(View.VISIBLE);
                holder.eventIcon.setVisibility(View.GONE);
            }
        }

        return convertView;
    }


    class ViewHolder {
        TextView time;
        TextView title;
        TextView address;
        LinearLayout colorCode;
        LinearLayout hasAppontment;
        TextView noAppontment;
        ImageView ifNotes;
        ImageView eventIcon;
    }

    private String getTime(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("h:mm a");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate).toUpperCase();
        } catch (Exception ex) {
            return "xx";
        }
    }

    public void notifyList(ArrayList<AppointmentMappingModel> appointmentListData) {

        this.datalist = appointmentListData;
        notifyDataSetChanged();

    }

}