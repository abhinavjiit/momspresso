package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.StoryShareCardWidget;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by hemant on 30/5/18.
 */

public class ShortStoriesRecyclerAdapter extends
        RecyclerView.Adapter<ShortStoriesRecyclerAdapter.ShortStoriesViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    private RecyclerViewClickListener mListener;

    public ShortStoriesRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
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
        viewHolder = new ShortStoriesViewHolder(v0);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShortStoriesViewHolder holder, int position) {
        try {
            Picasso.get().load(articleDataModelsNew.get(position).getStoryImage()).into(holder.shareStoryImageView);
            holder.storyAuthorTextView.setText(articleDataModelsNew.get(position).getUserName());
            AppUtils.populateLogoImageLanguageWise(holder.itemView.getContext(), holder.logoImageView,
                    articleDataModelsNew.get(position).getLang());
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        try {
            Picasso.get().load(articleDataModelsNew.get(position).getStoryImage().trim())
                    .placeholder(R.drawable.default_article).into(holder.storyImage);
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
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView;
        TextView storyAuthorTextView;
        ImageView logoImageView;

        ShortStoriesViewHolder(View itemView) {
            super(itemView);
            authorNameTextView = itemView.findViewById(R.id.authorNameTextView);
            followAuthorTextView = itemView.findViewById(R.id.followAuthorTextView);
            storyRecommendationContainer = itemView.findViewById(R.id.storyRecommendationContainer);
            storyCommentContainer = itemView.findViewById(R.id.storyCommentContainer);
            storyCommentCountTextView = itemView.findViewById(R.id.storyCommentCountTextView);
            storyRecommendationCountTextView = itemView.findViewById(R.id.storyRecommendationCountTextView);
            likeImageView = itemView.findViewById(R.id.likeImageView);
            facebookShareImageView = itemView.findViewById(R.id.facebookShareImageView);
            whatsappShareImageView = itemView.findViewById(R.id.whatsappShareImageView);
            instagramShareImageView = itemView.findViewById(R.id.instagramShareImageView);
            genericShareImageView = itemView.findViewById(R.id.genericShareImageView);
            storyImage = itemView.findViewById(R.id.storyImageView1);
            menuItem = itemView.findViewById(R.id.menuItem);
            storyShareCardWidget = itemView.findViewById(R.id.storyShareCardWidget);
            shareStoryImageView = storyShareCardWidget.findViewById(R.id.storyImageView);
            storyAuthorTextView = storyShareCardWidget.findViewById(R.id.storyAuthorTextView);
            logoImageView = storyShareCardWidget.findViewById(R.id.logoImageView);

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