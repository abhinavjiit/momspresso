package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycity4kids.R;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/17.
 */
public class SuggestedTopicsAdapter extends RecyclerView.Adapter<SuggestedTopicsAdapter.SuggestedTopicsViewHolder> {

    private LayoutInflater mInflator;
    ArrayList<String> articleDataModelsNew;
    private RecyclerViewClickListener mListener;

    public SuggestedTopicsAdapter(Context pContext, RecyclerViewClickListener listener) {

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mListener = listener;
    }

    public void setListData(ArrayList<String> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public SuggestedTopicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SuggestedTopicsViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.suggested_topic_list_item, parent, false);
        viewHolder = new SuggestedTopicsViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SuggestedTopicsViewHolder holder, int position) {
        holder.txvSuggestedTopic.setText(articleDataModelsNew.get(position));
        holder.txvIndex.setText("" + (position + 1));
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class SuggestedTopicsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txvSuggestedTopic;
        TextView txvIndex;

        public SuggestedTopicsViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            txvSuggestedTopic = (TextView) itemView.findViewById(R.id.suggestedTopicTextView);
            txvIndex = (TextView) itemView.findViewById(R.id.indexTextView);
            itemView.setOnClickListener(this);
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