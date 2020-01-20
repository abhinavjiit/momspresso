package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.widget.StoryShareCardWidget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by hemant on 30/5/18.
 */

public class ShortStoriesRecyclerAdapter extends RecyclerView.Adapter<ShortStoriesRecyclerAdapter.ShortStoriesViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    private RecyclerViewClickListener mListener;

    public ShortStoriesRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {

        float density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        try {
            Picasso.with(mContext).load(articleDataModelsNew.get(position).getStoryImage()).into(holder.shareStoryImageView);
            holder.storyAuthorTextView.setText(articleDataModelsNew.get(position).getUserName());
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        try {
            Picasso.with(holder.itemView.getContext()).load(articleDataModelsNew.get(position).getStoryImage().trim()).placeholder(R.drawable.default_article).into(holder.storyImage);
        } catch (Exception e) {
            holder.storyImage.setImageResource(R.drawable.default_article);
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

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

        if (articleDataModelsNew.get(position).getIsfollowing().equals("1")) {
            holder.followAuthorTextView.setText(mContext.getResources().getString(R.string.ad_following_author));
        } else {
            holder.followAuthorTextView.setText(mContext.getResources().getString(R.string.ad_follow_author));
        }

        new QBadgeView(mContext)
                .setBadgeText(" " + mContext.getString(R.string.new_label) + " ")
                .setBadgeBackgroundColor(mContext.getResources().getColor(R.color.orange_new))
                .setBadgeTextSize(7, true)
                .setBadgePadding(3, true)
                .setBadgeGravity(Gravity.TOP | Gravity.END)
                .setGravityOffset(4, -2, true)
                .bindTarget(holder.genericShareImageView);
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class ShortStoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView authorNameTextView, followAuthorTextView;
        TextView storyCommentCountTextView;
        LinearLayout storyRecommendationContainer, storyCommentContainer;
        TextView storyRecommendationCountTextView;
        ImageView likeImageView, menuItem;
        ImageView facebookShareImageView, whatsappShareImageView, instagramShareImageView, genericShareImageView, storyImage;
        ImageView storyOptionImageView;
        RelativeLayout mainView;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView;
        TextView storyAuthorTextView;

        public ShortStoriesViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            authorNameTextView = (TextView) itemView.findViewById(R.id.authorNameTextView);
            followAuthorTextView = (TextView) itemView.findViewById(R.id.followAuthorTextView);
            storyRecommendationContainer = (LinearLayout) itemView.findViewById(R.id.storyRecommendationContainer);
            storyCommentContainer = (LinearLayout) itemView.findViewById(R.id.storyCommentContainer);
            storyCommentCountTextView = (TextView) itemView.findViewById(R.id.storyCommentCountTextView);
            storyRecommendationCountTextView = (TextView) itemView.findViewById(R.id.storyRecommendationCountTextView);
            likeImageView = (ImageView) itemView.findViewById(R.id.likeImageView);
            facebookShareImageView = (ImageView) itemView.findViewById(R.id.facebookShareImageView);
            whatsappShareImageView = (ImageView) itemView.findViewById(R.id.whatsappShareImageView);
            instagramShareImageView = (ImageView) itemView.findViewById(R.id.instagramShareImageView);
            genericShareImageView = (ImageView) itemView.findViewById(R.id.genericShareImageView);
            storyImage = (ImageView) itemView.findViewById(R.id.storyImageView1);
            menuItem = (ImageView) itemView.findViewById(R.id.menuItem);
            storyShareCardWidget = (StoryShareCardWidget) itemView.findViewById(R.id.storyShareCardWidget);
            shareStoryImageView = (ImageView) storyShareCardWidget.findViewById(R.id.storyImageView);
            storyAuthorTextView = (TextView) storyShareCardWidget.findViewById(R.id.storyAuthorTextView);

            whatsappShareImageView.setTag(itemView);

            storyRecommendationContainer.setOnClickListener(this);
            facebookShareImageView.setOnClickListener(this);
            whatsappShareImageView.setOnClickListener(this);
            instagramShareImageView.setOnClickListener(this);
            genericShareImageView.setOnClickListener(this);
            authorNameTextView.setOnClickListener(this);
            storyImage.setOnClickListener(this);
            itemView.setOnClickListener(this);
            menuItem.setOnClickListener(this);
            followAuthorTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition(), whatsappShareImageView);
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position, View shareImageView);
    }

}