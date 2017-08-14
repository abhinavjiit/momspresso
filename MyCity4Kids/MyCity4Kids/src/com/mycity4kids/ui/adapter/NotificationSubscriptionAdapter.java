package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.mycity4kids.R;
import com.mycity4kids.models.NotificationSettingsModel;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/17.
 */
public class NotificationSubscriptionAdapter extends RecyclerView.Adapter<NotificationSubscriptionAdapter.NotificationViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<NotificationSettingsModel> notificationSettingsList;
    private final float density;
    private RecyclerViewClickListener mListener;

    public NotificationSubscriptionAdapter(Context pContext, ArrayList<NotificationSettingsModel> notificationSettingsList) {
        this.notificationSettingsList = notificationSettingsList;
        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NotificationViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.notification_settings_item, parent, false);
        viewHolder = new NotificationViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NotificationViewHolder holder, int position) {
        holder.aSwitch.setText(StringUtils.capitalize(notificationSettingsList.get(position).getName()));
        holder.aSwitch.setTag(position);
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notificationSettingsList.get((int) holder.aSwitch.getTag()).setStatus("1");
                } else {
                    notificationSettingsList.get((int) holder.aSwitch.getTag()).setStatus("0");
                }
            }
        });
        if ("1".equals(notificationSettingsList.get(position).getStatus())) {
            holder.aSwitch.setChecked(true);
        } else {
            holder.aSwitch.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return notificationSettingsList == null ? 0 : notificationSettingsList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SwitchCompat aSwitch;

        public NotificationViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            aSwitch = (SwitchCompat) itemView.findViewById(R.id.notificationStatus);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/oswald.ttf");
            aSwitch.setTypeface(font);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

}