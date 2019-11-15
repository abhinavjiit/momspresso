package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycity4kids.R;
import com.mycity4kids.models.response.FeaturedOnListResponse;

import java.util.ArrayList;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;


public class FeatureOnRecyclerAdapter extends RecyclerView.Adapter<FeatureOnRecyclerAdapter.FeatureOnViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<FeaturedOnListResponse.FeaturedListResult> featuredList;
    private RecyclerViewClickListener mListener;
    private String userId;
    RecyclerView recyclerView;

    public FeatureOnRecyclerAdapter(Context pContext, ArrayList<FeaturedOnListResponse.FeaturedListResult> featuredList) {
        mContext = pContext;
        this.featuredList = featuredList;
    }

    @Override
    public FeatureOnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FeatureOnViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.subscription_settings_item, parent, false);
        viewHolder = new FeatureOnViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FeatureOnViewHolder holder, final int position) {
       /* holder.aSwitch.setText(StringUtils.capitalize(subscriptionSettingsList.get(position).getDisplayName()));
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
        }*/
    }

    @Override
    public int getItemCount() {
        return featuredList == null ? 0 : featuredList.size();
    }

    public class FeatureOnViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SwitchCompat aSwitch;

        public FeatureOnViewHolder(View itemView, RecyclerViewClickListener listener) {
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