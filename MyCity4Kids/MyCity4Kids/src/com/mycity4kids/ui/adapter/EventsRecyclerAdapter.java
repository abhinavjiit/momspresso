package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.widget.FeedNativeAd;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/17.
 */
public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.EventViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    private RecyclerViewClickListener mListener;

    public EventsRecyclerAdapter(Context pContext) {

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
//        this.mListener = listener;
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
    }


    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = mInflator.inflate(R.layout.events_recycler_item_layout, parent, false);
        return new EventViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

//    public void refreshArticleList(ArrayList<SearchArticleResult> newList) {
//        this.articleDataModelsNew = newList;
//    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView eventsImageView;
        TextView eventsTextView;

        EventViewHolder(View adView) {
            super(adView);
            eventsTextView = (TextView) itemView.findViewById(R.id.eventsTextView);
            eventsImageView = (ImageView) itemView.findViewById(R.id.eventsImageView);
            adView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            mListener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

}