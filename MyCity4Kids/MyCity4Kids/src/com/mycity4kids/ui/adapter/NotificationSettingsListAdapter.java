package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.mycity4kids.R;
import com.mycity4kids.models.NotificationSettingsModel;

import java.util.ArrayList;

/**
 * Created by hemant on 7/12/16.
 */
public class NotificationSettingsListAdapter extends BaseAdapter {

    private ArrayList<NotificationSettingsModel> notificationSettingsList;
    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<String> tempList;

    public NotificationSettingsListAdapter(Context pContext, ArrayList<NotificationSettingsModel> notificationSettingsList) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.notificationSettingsList = notificationSettingsList;
        tempList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return notificationSettingsList == null ? 0 : notificationSettingsList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationSettingsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        Switch aSwitch;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.notification_settings_item, null);
            holder = new ViewHolder();
            holder.aSwitch = (Switch) view.findViewById(R.id.notificationStatus);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.aSwitch.setText(notificationSettingsList.get(position).getName());
        if ("1".equals(notificationSettingsList.get(position).getStatus())) {
            holder.aSwitch.setChecked(true);
        } else {
            holder.aSwitch.setChecked(false);
        }

        holder.aSwitch.setTag(position);
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notificationSettingsList.get((int) holder.aSwitch.getTag()).setStatus("1");
//                    tempList.add((String) holder.aSwitch.getTag());
                } else {
//                    tempList.remove((String) holder.aSwitch.getTag());
                    notificationSettingsList.get((int) holder.aSwitch.getTag()).setStatus("0");
                }
            }
        });

        return view;
    }

    public ArrayList<NotificationSettingsModel> getAllNotificationStatus() {
        return notificationSettingsList;
    }
}