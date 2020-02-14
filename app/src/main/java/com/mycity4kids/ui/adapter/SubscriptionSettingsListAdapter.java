package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;

import com.mycity4kids.R;
import com.mycity4kids.models.SubscriptionAndLanguageSettingsModel;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by hemant on 7/12/16.
 */
public class SubscriptionSettingsListAdapter extends BaseAdapter {

    private ArrayList<SubscriptionAndLanguageSettingsModel> subscriptionSettingsList;
    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<String> tempList;

    public SubscriptionSettingsListAdapter(Context pContext, ArrayList<SubscriptionAndLanguageSettingsModel> subscriptionSettingsList) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.subscriptionSettingsList = subscriptionSettingsList;
        tempList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return subscriptionSettingsList == null ? 0 : subscriptionSettingsList.size();
    }

    @Override
    public Object getItem(int position) {
        return subscriptionSettingsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        SwitchCompat aSwitch;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.subscription_settings_item, null);
            holder = new ViewHolder();
            holder.aSwitch = (SwitchCompat) view.findViewById(R.id.subscriptionStatus);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.aSwitch.setText(StringUtils.capitalize(subscriptionSettingsList.get(position).getDisplayName()));

        holder.aSwitch.setTag(position);
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    subscriptionSettingsList.get((int) holder.aSwitch.getTag()).setStatus("1");
//                    tempList.add((String) holder.aSwitch.getTag());
                } else {
//                    tempList.remove((String) holder.aSwitch.getTag());
                    subscriptionSettingsList.get((int) holder.aSwitch.getTag()).setStatus("0");
                }
            }
        });

        if ("1".equals(subscriptionSettingsList.get(position).getStatus())) {
            holder.aSwitch.setChecked(true);
        } else {
            holder.aSwitch.setChecked(false);
        }
        return view;
    }
}