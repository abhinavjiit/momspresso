package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.SubscriptionAndLanguageSettingsModel;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/17.
 */
public class PreferredLanguagesAdapter extends RecyclerView.Adapter<PreferredLanguagesAdapter.PrefLanguagesViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<SubscriptionAndLanguageSettingsModel> languageSettingsList;
    private final float density;
    private RecyclerViewClickListener mListener;

    public PreferredLanguagesAdapter(Context pContext, ArrayList<SubscriptionAndLanguageSettingsModel> languageSettingsList) {
        this.languageSettingsList = languageSettingsList;
        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
    }

    @Override
    public PrefLanguagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PrefLanguagesViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.preferred_language_recyler_item, parent, false);
        viewHolder = new PrefLanguagesViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PrefLanguagesViewHolder holder, int position) {
        holder.languageStatus.setText(languageSettingsList.get(position).getName());
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
//        holder.languageNameTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.languageStatus.performClick();
//            }
//        });
//        holder.storyCountTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.languageStatus.performClick();
//            }
//        });
        if ("1".equals(languageSettingsList.get(position).getStatus())) {
            holder.languageStatus.setChecked(true);
        } else {
            holder.languageStatus.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return languageSettingsList == null ? 0 : languageSettingsList.size();
    }

    public class PrefLanguagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView languageNameTextView;
        TextView storyCountTextView;
        CheckBox languageStatus;

        public PrefLanguagesViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
//            languageNameTextView = (TextView) itemView.findViewById(R.id.languageTextView);
//            storyCountTextView = (TextView) itemView.findViewById(R.id.storiesCountTextView);
            languageStatus = (CheckBox) itemView.findViewById(R.id.languageStatus);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/oswald.ttf");
            languageStatus.setTypeface(font);
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