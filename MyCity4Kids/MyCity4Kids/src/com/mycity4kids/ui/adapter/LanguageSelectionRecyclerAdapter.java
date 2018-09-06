package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;

import java.util.ArrayList;

/**
 * Created by hemant on 4/9/18.
 */

public class LanguageSelectionRecyclerAdapter extends RecyclerView.Adapter<LanguageSelectionRecyclerAdapter.LanguageViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<String> articleDataModelsNew;
    private RecyclerViewClickListener mListener;
    private int selectedPosition = -1;

    public LanguageSelectionRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }

    public void setNewListData(ArrayList<String> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew.size();
    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = mInflator.inflate(R.layout.language_selection_item, parent, false);
        return new LanguageViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(final LanguageViewHolder holder, final int position) {
        holder.languageTextView.setText(articleDataModelsNew.get(position));
        holder.languageTextView.setSelected(position == selectedPosition);

        if (position == selectedPosition) {
            holder.tickImageView.setVisibility(View.VISIBLE);
        } else {
            holder.tickImageView.setVisibility(View.GONE);
        }
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView languageTextView;
        ImageView tickImageView;

        LanguageViewHolder(View view) {
            super(view);
            languageTextView = (TextView) view.findViewById(R.id.languageTextView);
            tickImageView = (ImageView) view.findViewById(R.id.tickImageView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = getAdapterPosition();
                    if (selectedPosition == RecyclerView.NO_POSITION) return;
                    mListener.onRecyclerItemClick(v, selectedPosition);
                    notifyDataSetChanged();
                }
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