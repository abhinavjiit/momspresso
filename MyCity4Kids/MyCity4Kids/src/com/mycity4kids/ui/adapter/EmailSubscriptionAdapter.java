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
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.SubscriptionAndLanguageSettingsModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/17.
 */
public class EmailSubscriptionAdapter extends RecyclerView.Adapter<EmailSubscriptionAdapter.EmailSubscriptionViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<SubscriptionAndLanguageSettingsModel> subscriptionSettingsList;
    private final float density;
    private RecyclerViewClickListener mListener;
    private String userId;

    public EmailSubscriptionAdapter(Context pContext, ArrayList<SubscriptionAndLanguageSettingsModel> subscriptionSettingsList) {
        this.subscriptionSettingsList = subscriptionSettingsList;
        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        userId = SharedPrefUtils.getUserDetailModel(mContext).getDynamoId();
    }

    @Override
    public EmailSubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EmailSubscriptionViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.subscription_settings_item, parent, false);
        viewHolder = new EmailSubscriptionViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final EmailSubscriptionViewHolder holder, final int position) {
        holder.aSwitch.setText(StringUtils.capitalize(subscriptionSettingsList.get(position).getDisplayName()));
        holder.aSwitch.setTag(position);
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    subscriptionSettingsList.get((int) holder.aSwitch.getTag()).setStatus("1");
//                    Utils.pushEnableSubscriptionEvent(mContext, "", userId, subscriptionSettingsList.get(position).getName());
                } else {
                    subscriptionSettingsList.get((int) holder.aSwitch.getTag()).setStatus("0");
//                    Utils.pushDisableSubscriptionEvent(mContext, "", userId, subscriptionSettingsList.get(position).getName());
                }
            }
        });
        if ("1".equals(subscriptionSettingsList.get(position).getStatus())) {
            holder.aSwitch.setChecked(true);
        } else {
            holder.aSwitch.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return subscriptionSettingsList == null ? 0 : subscriptionSettingsList.size();
    }

//    public void refreshArticleList(ArrayList<SearchArticleResult> newList) {
//        this.articleDataModelsNew = newList;
//    }

    public class EmailSubscriptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SwitchCompat aSwitch;

        public EmailSubscriptionViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            aSwitch = (SwitchCompat) itemView.findViewById(R.id.subscriptionStatus);
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