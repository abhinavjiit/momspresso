package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.ExploreTopicsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 31/5/18.
 */

public class ShortStoryTopicsRecyclerAdapter extends RecyclerView.Adapter<ShortStoryTopicsRecyclerAdapter.ShortStoryTopicsViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ExploreTopicsModel> topicsList;
    private final float density;
    private RecyclerViewClickListener mListener;

    public ShortStoryTopicsRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    public void setListData(ArrayList<ExploreTopicsModel> topicsList) {
        this.topicsList = topicsList;
    }

    @Override
    public ShortStoryTopicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ShortStoryTopicsViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.short_story_topics_horizontal_item, parent, false);
        viewHolder = new ShortStoryTopicsViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShortStoryTopicsViewHolder holder, int position) {

        holder.topicsNameTextView.setText(topicsList.get(position).getDisplay_name().toUpperCase());

        try {
            Picasso.with(mContext).load(topicsList.get(position).getExtraData().get(0).getCategoryBackImage().getApp()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .fit().into(holder.tagsImageView);
        } catch (Exception e) {
            holder.tagsImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_article));
        }

        if (topicsList.get(position).isSelected()) {
            holder.selectedLayerLayout.setVisibility(View.VISIBLE);
        } else {
            holder.selectedLayerLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return topicsList == null ? 0 : topicsList.size();
    }

    public class ShortStoryTopicsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView tagsImageView;
        TextView topicsNameTextView;
        LinearLayout selectedLayerLayout;

        public ShortStoryTopicsViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            tagsImageView = (ImageView) itemView.findViewById(R.id.tagImageView);
            topicsNameTextView = (TextView) itemView.findViewById(R.id.topicsNameTextView);
            selectedLayerLayout = (LinearLayout) itemView.findViewById(R.id.selectedLayerLayout);
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
