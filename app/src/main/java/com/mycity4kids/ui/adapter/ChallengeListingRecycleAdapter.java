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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.StoryShareCardWidget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import q.rorbin.badgeview.QBadgeView;

public class ChallengeListingRecycleAdapter extends RecyclerView.Adapter<ChallengeListingRecycleAdapter.ChallengeListingViewHolder> {
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    private RecyclerViewClickListener recyclerViewClickListener;
    private String selected_Name;
    private String ActiveUrl;

    public ChallengeListingRecycleAdapter(RecyclerViewClickListener recyclerViewClickListener, Context mContext, int pos, String selected_Name, String ActiveUrl) {
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.selected_Name = selected_Name;
        this.mContext = mContext;
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.ActiveUrl = ActiveUrl;
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public ChallengeListingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChallengeListingViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.challenge_listing_adapter, parent, false);
        viewHolder = new ChallengeListingViewHolder(v0, recyclerViewClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChallengeListingViewHolder holder, int position) {
        new QBadgeView(mContext)
                .setBadgeText(" " + mContext.getString(R.string.new_label) + " ")
                .setBadgeBackgroundColor(mContext.getResources().getColor(R.color.orange_new))
                .setBadgeTextSize(7, true)
                .setBadgePadding(3, true)
                .setBadgeGravity(Gravity.TOP | Gravity.END)
                .setGravityOffset(4, -2, true)
                .bindTarget(holder.genericShareImageView);
        if (position == 0) {
            holder.rootview.setVisibility(View.GONE);
            holder.challengeHeaderText.setVisibility(View.VISIBLE);
            if (ActiveUrl != null) {
                holder.challengeNameImage.setVisibility(View.VISIBLE);
                try {
                    Glide.with(mContext).load(ActiveUrl).into(holder.challengeNameImage);
                } catch (Exception e) {
                    holder.challengeNameImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_article));
                }
            } else {
                holder.ChallengeNameText.setVisibility(View.VISIBLE);
                holder.ChallengeNameText.setText(selected_Name);
            }
        } else {
            holder.rootview.setVisibility(View.VISIBLE);
            holder.challengeHeaderText.setVisibility(View.GONE);
            viewListingResult(holder, position - 1);
        }
    }

    private void viewListingResult(ChallengeListingViewHolder holder, int position) {
        if (articleDataModelsNew.get(position).getIsfollowing().equals("1"))
            holder.followAuthorTextView.setText(mContext.getResources().getString(R.string.ad_following_author));
        else
            holder.followAuthorTextView.setText(mContext.getResources().getString(R.string.ad_follow_author));
        try {
            Picasso.get().load(articleDataModelsNew.get(position).getStoryImage().trim()).placeholder(R.drawable.default_article).into(holder.storyImage);
        } catch (Exception e) {
            holder.storyImage.setImageResource(R.drawable.default_article);
        }
        try {
            Picasso.get().load(articleDataModelsNew.get(position).getStoryImage()).into(holder.shareStoryImageView);
            holder.storyAuthorTextView.setText(articleDataModelsNew.get(position).getUserName());
            AppUtils.populateLogoImageLanguageWise(holder.itemView.getContext(), holder.logoImageView, articleDataModelsNew.get(position).getLang());
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
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
        holder.authorNameTextView.setText(articleDataModelsNew.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size() + 1;
    }

    public class ChallengeListingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout rootview;
        private RelativeLayout challengeHeaderText;
        private TextView ChallengeNameText;
        TextView authorNameTextView;
        TextView storyCommentCountTextView;
        LinearLayout storyRecommendationContainer, storyCommentContainer;
        TextView storyRecommendationCountTextView;
        ImageView storyImage, likeImageView;
        ImageView facebookShareImageView, whatsappShareImageView, instagramShareImageView, genericShareImageView;
        TextView submitChallenegLayout, followAuthorTextView;
        ImageView challengeNameImage, menuItem;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView;
        TextView storyAuthorTextView;
        ImageView logoImageView;


        public ChallengeListingViewHolder(View itemView, RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            challengeNameImage = (ImageView) itemView.findViewById(R.id.ChallengeNameImage);
            challengeHeaderText = (RelativeLayout) itemView.findViewById(R.id.challenge_header_text);
            ChallengeNameText = (TextView) itemView.findViewById(R.id.ChallengeNameText);
            authorNameTextView = (TextView) itemView.findViewById(R.id.authorNameTextView);
            storyRecommendationContainer = (LinearLayout) itemView.findViewById(R.id.storyRecommendationContainer);
            storyCommentContainer = (LinearLayout) itemView.findViewById(R.id.storyCommentContainer);
            storyCommentCountTextView = (TextView) itemView.findViewById(R.id.storyCommentCountTextView);
            storyRecommendationCountTextView = (TextView) itemView.findViewById(R.id.storyRecommendationCountTextView);
            storyImage = (ImageView) itemView.findViewById(R.id.storyImageView1);
            likeImageView = (ImageView) itemView.findViewById(R.id.likeImageView);
            facebookShareImageView = (ImageView) itemView.findViewById(R.id.facebookShareImageView);
            whatsappShareImageView = (ImageView) itemView.findViewById(R.id.whatsappShareImageView);
            instagramShareImageView = (ImageView) itemView.findViewById(R.id.instagramShareImageView);
            genericShareImageView = (ImageView) itemView.findViewById(R.id.genericShareImageView);
            rootview = (RelativeLayout) itemView.findViewById(R.id.rootView);
            submitChallenegLayout = (TextView) itemView.findViewById(R.id.submit_story_text);
            menuItem = itemView.findViewById(R.id.menuItem);
            followAuthorTextView = itemView.findViewById(R.id.followAuthorTextView);
            storyShareCardWidget = (StoryShareCardWidget) itemView.findViewById(R.id.storyShareCardWidget);
            shareStoryImageView = (ImageView) storyShareCardWidget.findViewById(R.id.storyImageView);
            storyAuthorTextView = (TextView) storyShareCardWidget.findViewById(R.id.storyAuthorTextView);
            logoImageView = storyShareCardWidget.findViewById(R.id.logoImageView);
            whatsappShareImageView.setTag(itemView);
            submitChallenegLayout.setOnClickListener(this);
            storyRecommendationContainer.setOnClickListener(this);
            facebookShareImageView.setOnClickListener(this);
            whatsappShareImageView.setOnClickListener(this);
            instagramShareImageView.setOnClickListener(this);
            genericShareImageView.setOnClickListener(this);
            authorNameTextView.setOnClickListener(this);
            storyImage.setOnClickListener(this);
            challengeHeaderText.setOnClickListener(this);
            itemView.setOnClickListener(this);
            menuItem.setOnClickListener(this);
            followAuthorTextView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            recyclerViewClickListener.onClick(view, getAdapterPosition() - 1, ActiveUrl);
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position, String activeUrl);
    }
}
