package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;

import java.util.ArrayList;

/**
 * Created by hemant on 30/5/18.
 */

public class ShortStoriesRecyclerAdapter extends RecyclerView.Adapter<ShortStoriesRecyclerAdapter.ShortStoriesViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    private final float density;
    private RecyclerViewClickListener mListener;

    public ShortStoriesRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public ShortStoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ShortStoriesViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.short_story_listing_item, parent, false);
        viewHolder = new ShortStoriesViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShortStoriesViewHolder holder, int position) {

        switch (position % 6) {
            case 0:
                holder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_1));
                break;
            case 1:
                holder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_2));
                break;
            case 2:
                holder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_3));
                break;
            case 3:
                holder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_4));
                break;
            case 4:
                holder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_5));
                break;
            case 5:
                holder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_6));
                break;
        }

        holder.storyTitleTextView.setText(articleDataModelsNew.get(position).getTitle().trim());
        holder.storyBodyTextView.setText(articleDataModelsNew.get(position).getBody().trim());
        holder.authorNameTextView.setText(articleDataModelsNew.get(position).getUserName());

        if (null == articleDataModelsNew.get(position).getCommentsCount()) {
            holder.storyCommentCountTextView.setText("0");
        } else {
            holder.storyCommentCountTextView.setText(articleDataModelsNew.get(position).getCommentsCount());
        }

        if (null == articleDataModelsNew.get(position).getLikesCount()) {
            holder.storyRecommendationCountTextView.setText("0");
        } else {
            holder.storyRecommendationCountTextView.setText(articleDataModelsNew.get(position).getLikesCount());
        }

        if (articleDataModelsNew.get(position).isLiked()) {
            holder.likeImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_recommended));
        } else {
            holder.likeImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_ss_like));
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class ShortStoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView storyTitleTextView;
        TextView storyBodyTextView;
        TextView authorNameTextView;
        TextView storyCommentCountTextView;
        LinearLayout storyRecommendationContainer, storyCommentContainer;
        TextView storyRecommendationCountTextView;
        ImageView storyOptionImageView, likeImageView;
        ImageView facebookShareImageView, whatsappShareImageView, instagramShareImageView, genericShareImageView;
        RelativeLayout mainView;

        public ShortStoriesViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mainView = (RelativeLayout) itemView.findViewById(R.id.mainView);
            storyTitleTextView = (TextView) itemView.findViewById(R.id.storyTitleTextView);
            storyBodyTextView = (TextView) itemView.findViewById(R.id.storyBodyTextView);
            authorNameTextView = (TextView) itemView.findViewById(R.id.authorNameTextView);
            storyRecommendationContainer = (LinearLayout) itemView.findViewById(R.id.storyRecommendationContainer);
            storyCommentContainer = (LinearLayout) itemView.findViewById(R.id.storyCommentContainer);
            storyCommentCountTextView = (TextView) itemView.findViewById(R.id.storyCommentCountTextView);
            storyRecommendationCountTextView = (TextView) itemView.findViewById(R.id.storyRecommendationCountTextView);
            storyOptionImageView = (ImageView) itemView.findViewById(R.id.storyOptionImageView);
            likeImageView = (ImageView) itemView.findViewById(R.id.likeImageView);
            facebookShareImageView = (ImageView) itemView.findViewById(R.id.facebookShareImageView);
            whatsappShareImageView = (ImageView) itemView.findViewById(R.id.whatsappShareImageView);
            instagramShareImageView = (ImageView) itemView.findViewById(R.id.instagramShareImageView);
            genericShareImageView = (ImageView) itemView.findViewById(R.id.genericShareImageView);

            whatsappShareImageView.setTag(itemView);

            storyRecommendationContainer.setOnClickListener(this);
            facebookShareImageView.setOnClickListener(this);
            whatsappShareImageView.setOnClickListener(this);
            instagramShareImageView.setOnClickListener(this);
            genericShareImageView.setOnClickListener(this);
            authorNameTextView.setOnClickListener(this);
            storyOptionImageView.setOnClickListener(this);
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