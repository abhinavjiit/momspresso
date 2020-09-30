package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.widget.RelativeLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;

import com.mycity4kids.models.LanguageSelectionData;
import java.util.ArrayList;

/**
 * Created by hemant on 4/9/18.
 */

public class LanguageSelectionRecyclerAdapter extends
        RecyclerView.Adapter<LanguageSelectionRecyclerAdapter.LanguageViewHolder> {

    private ArrayList<LanguageSelectionData> languageSelectionDataArrayList;
    private RecyclerViewClickListener mListener;
    private int selectedPosition = -1;
    private boolean firstTimeInit = true;

    public LanguageSelectionRecyclerAdapter(RecyclerViewClickListener listener) {
        mListener = listener;
    }

    public void setNewListData(ArrayList<LanguageSelectionData> languageSelectionDataArrayList) {
        this.languageSelectionDataArrayList = languageSelectionDataArrayList;
    }

    @Override
    public int getItemCount() {
        return languageSelectionDataArrayList.size();
    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_selection_item, parent, false);
        return new LanguageViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(final LanguageViewHolder holder, final int position) {
        holder.languageTextView.setText(languageSelectionDataArrayList.get(position).getLanguageName());
        holder.regionalLanguageTextView.setText(languageSelectionDataArrayList.get(position).getRegionalLanguageName());
        if (firstTimeInit) {
            holder.languageTextView
                    .setTextColor(ContextCompat.getColor(holder.languageTextView.getContext(), R.color.white_color));
            holder.regionalLanguageTextView
                    .setTextColor(
                            ContextCompat.getColor(holder.regionalLanguageTextView.getContext(), R.color.white_color));
            holder.langImageView.setImageResource(languageSelectionDataArrayList.get(position).getEnabledImage());
            holder.tickImageView.setVisibility(View.GONE);
        } else if (position == selectedPosition) {
            holder.tickImageView.setVisibility(View.VISIBLE);
            holder.languageTextView
                    .setTextColor(ContextCompat.getColor(holder.languageTextView.getContext(), R.color.white_color));
            holder.regionalLanguageTextView
                    .setTextColor(
                            ContextCompat.getColor(holder.regionalLanguageTextView.getContext(), R.color.white_color));
            holder.langImageView.setImageResource(languageSelectionDataArrayList.get(position).getEnabledImage());
        } else {
            holder.languageTextView
                    .setTextColor(ContextCompat.getColor(holder.languageTextView.getContext(), R.color.color_888989));
            holder.regionalLanguageTextView
                    .setTextColor(
                            ContextCompat.getColor(holder.regionalLanguageTextView.getContext(), R.color.color_888989));
            holder.tickImageView.setVisibility(View.GONE);
            holder.langImageView.setImageResource(languageSelectionDataArrayList.get(position).getDisabledImage());
        }
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView languageTextView;
        TextView regionalLanguageTextView;
        ImageView tickImageView;
        ImageView langImageView;
        RelativeLayout languageContainer;

        LanguageViewHolder(View view) {
            super(view);
            languageContainer = view.findViewById(R.id.languageContainer);
            languageTextView = view.findViewById(R.id.languageTextView);
            regionalLanguageTextView = view.findViewById(R.id.regionalLanguageTextView);
            langImageView = view.findViewById(R.id.langImageView);
            tickImageView = view.findViewById(R.id.tickImageView);
            view.setOnClickListener(v -> {
                firstTimeInit = false;
                selectedPosition = getAdapterPosition();
                if (selectedPosition == RecyclerView.NO_POSITION) {
                    return;
                }
                mListener.onRecyclerItemClick(v, selectedPosition);
                notifyDataSetChanged();
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {

        void onRecyclerItemClick(View view, int position);
    }

}