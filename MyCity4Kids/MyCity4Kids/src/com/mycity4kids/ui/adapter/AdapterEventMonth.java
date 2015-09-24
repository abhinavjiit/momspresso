package com.mycity4kids.ui.adapter;

import android.app.Activity;
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
import java.util.TimeZone;

/**
 * Created by manish.soni on 21-06-2015.
 */
public class AdapterEventMonth extends BaseAdapter {

    Context mContext;
    ArrayList<AppointmentMappingModel> appointmentList;

    public AdapterEventMonth(Context context, ArrayList<AppointmentMappingModel> appointmentList) {
        mContext = context;
        this.appointmentList = appointmentList;
    }

    @Override
    public int getCount() {
        return this.appointmentList == null ? 0 : this.appointmentList.size();
    }

    @Override
    public Object getItem(int i) {
        return appointmentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(R.layout.aa_list_event_month, viewGroup, false);

            holder = new ViewHolder();
            holder.hasAppontment = (LinearLayout) view.findViewById(R.id.appointment_layout);
            holder.noAppontment = (TextView) view.findViewById(R.id.no_appointment);
            holder.time = (TextView) view.findViewById(R.id.appoint_time);
            holder.title = (TextView) view.findViewById(R.id.appoint_title);
            holder.address = (TextView) view.findViewById(R.id.appoint_address);
            holder.colorCode = (LinearLayout) view.findViewById(R.id.colorCode);
            holder.ifNotes = (ImageView) view.findViewById(R.id.appointment_notes);
            holder.eventIcon = (ImageView) view.findViewById(R.id.event_icon);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (appointmentList.get(position).getAppointment_name() == null) {
            holder.hasAppontment.setVisibility(View.GONE);
            holder.noAppontment.setVisibility(View.VISIBLE);
        } else {
            holder.hasAppontment.setVisibility(View.VISIBLE);
            holder.noAppontment.setVisibility(View.GONE);

            LinearLayout.LayoutParams dummyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

            holder.time.setText(getTime(appointmentList.get(position).getStarttime()));
            holder.title.setText(appointmentList.get(position).getAppointment_name());
            holder.address.setText(appointmentList.get(position).getLocality());
            holder.colorCode.removeAllViews();

            try {

                for (int i = 0; i < appointmentList.get(position).getAttendee().size(); i++) {

                    View dummyView = new View(mContext);
                    dummyView.setLayoutParams(dummyParams);

                    dummyView.setBackgroundColor(Color.parseColor(appointmentList.get(position).getAttendee().get(i).getColorCode()));
                    dummyParams.weight = 1f;

                    holder.colorCode.addView(dummyView);
                }

                if (appointmentList.get(position).isHasNotes()) {
                    holder.ifNotes.setVisibility(View.VISIBLE);
                } else {
                    holder.ifNotes.setVisibility(View.GONE);
                }

                if (appointmentList.get(position).getIs_bday() == 1 || appointmentList.get(position).getIs_holiday() == 1) {
                    holder.time.setVisibility(View.GONE);
                    holder.eventIcon.setVisibility(View.VISIBLE);

                    if (appointmentList.get(position).getIs_holiday() == 1) {
                        holder.eventIcon.setBackgroundResource(R.drawable.holiday);
                    } else if (appointmentList.get(position).getIs_bday() == 1) {
                        holder.eventIcon.setBackgroundResource(R.drawable.cake_xxhdpi);
                    }
                } else {
                    holder.time.setVisibility(View.VISIBLE);
                    holder.eventIcon.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    public static class ViewHolder {
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
            DateFormat sdf = new SimpleDateFormat("hh:mm a");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate).toUpperCase();
        } catch (Exception ex) {
            return "xx";
        }
    }


    public void updateEvent(ArrayList<AppointmentMappingModel> appointmentList) {

        this.appointmentList = appointmentList;
        notifyDataSetChanged();
    }
}