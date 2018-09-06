package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.ExploreTopicsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 5/9/18.
 */

public class TopicsRecyclerGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private Context mContext;
    private RecyclerViewClickListener mListener;
    private ArrayList<ExploreTopicsModel> topicsList;
    private ArrayList<ExploreTopicsModel> arraylist;

    public TopicsRecyclerGridAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        this.mListener = listener;
    }

    public void setDatalist(ArrayList<ExploreTopicsModel> datalist) {
        this.topicsList = datalist;
        this.arraylist = new ArrayList<ExploreTopicsModel>();
        this.arraylist.addAll(datalist);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_view_header, parent, false);
            return new QuickLinkHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.explore_topics_grid_item, parent, false);
            return new TopicViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof QuickLinkHolder) {

        } else {
            ((TopicViewHolder) holder).topicsNameTextView.setText(topicsList.get(position - 1).getDisplay_name().toUpperCase());

            if ("exploreSectionId".equals(topicsList.get(position - 1).getId())) {
                ((TopicViewHolder) holder).tagsImageView.setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.events_card_bg));
            } else {
                //useless backend can't do shit. tired of checking null, empty, json object or json array.
                try {
                    Picasso.with(BaseApplication.getAppContext()).load(topicsList.get(position - 1).getExtraData().get(0).getCategoryBackImage().getApp()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .fit().into(((TopicViewHolder) holder).tagsImageView);
                } catch (Exception e) {
                    ((TopicViewHolder) holder).tagsImageView.setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.default_article));
                }
            }
            ((TopicViewHolder) holder).selectorView.setVisibility(View.GONE);
        }

    }

    public class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView tagsImageView;
        TextView topicsNameTextView;
        View selectorView;

        public TopicViewHolder(View itemView) {
            super(itemView);
            tagsImageView = (ImageView) itemView.findViewById(R.id.tagImageView);
            topicsNameTextView = (TextView) itemView.findViewById(R.id.topicsNameTextView);
            selectorView = itemView.findViewById(R.id.selectorView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public class QuickLinkHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView tagsImageView;
        TextView topicsNameTextView;
        View selectorView;

        public QuickLinkHolder(View itemView) {
            super(itemView);
            tagsImageView = (ImageView) itemView.findViewById(R.id.tagImageView);
            topicsNameTextView = (TextView) itemView.findViewById(R.id.topicsNameTextView);
            selectorView = itemView.findViewById(R.id.selectorView);
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

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return topicsList.size() + 1;
    }

}