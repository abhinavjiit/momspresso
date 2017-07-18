package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.SubscriptionAndLanguageSettingsModel;

import java.util.ArrayList;

/**
 * Created by hemant on 7/12/16.
 */
public class LanguageSettingsListAdapter extends BaseAdapter {

    private ArrayList<SubscriptionAndLanguageSettingsModel> languageSettingsList;
    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<String> tempList;

    public LanguageSettingsListAdapter(Context pContext, ArrayList<SubscriptionAndLanguageSettingsModel> languageSettingsList) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.languageSettingsList = languageSettingsList;
        tempList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return languageSettingsList == null ? 0 : languageSettingsList.size();
    }

    @Override
    public Object getItem(int position) {
        return languageSettingsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        //        SwitchCompat aSwitch;
        TextView languageNameTextView;
        TextView storyCountTextView;
        CheckBox languageStatus;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.language_settings_item, null);
            holder = new ViewHolder();
            holder.languageNameTextView = (TextView) view.findViewById(R.id.languageTextView);
            holder.storyCountTextView = (TextView) view.findViewById(R.id.storiesCountTextView);
            holder.languageStatus = (CheckBox) view.findViewById(R.id.languageStatus);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.languageNameTextView.setText(languageSettingsList.get(position).getName());
        holder.storyCountTextView.setText(languageSettingsList.get(position).getStories() + " " + mContext.getString(R.string.lang_setting_stories_label));

        holder.languageStatus.setTag(position);

        holder.languageStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    languageSettingsList.get((int) holder.languageStatus.getTag()).setStatus("1");
                } else {
                    languageSettingsList.get((int) holder.languageStatus.getTag()).setStatus("0");
                }
            }
        });
        holder.languageNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.languageStatus.performClick();
            }
        });
        holder.storyCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.languageStatus.performClick();
            }
        });
        if ("1".equals(languageSettingsList.get(position).getStatus())) {
            holder.languageStatus.setChecked(true);
        } else {
            holder.languageStatus.setChecked(false);
        }

        return view;
    }
}