package com.mycity4kids.ui.adapter;

import android.util.Log;
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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.StoryShareCardWidget;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ChallengeListingRecycleAdapter extends
        RecyclerView.Adapter<ChallengeListingRecycleAdapter.ChallengeListingViewHolder> {

    ArrayList<ArticleListingResult> articleDataModelsNew;
    private RecyclerViewClickListener recyclerViewClickListener;
    private String challengeName;
    private String challengeImageUrl;

    public ChallengeListingRecycleAdapter(RecyclerViewClickListener recyclerViewClickListener, String challengeName,
            String challengeImageUrl) {
        this.challengeName = challengeName;
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.challengeImageUrl = challengeImageUrl;
    }

    public void setListData(ArrayList<ArticleListingResult> articleListingResults) {
        articleDataModelsNew = articleListingResults;
    }

    @Override
    public ChallengeListingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChallengeListingViewHolder viewHolder;
        View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.challenge_listing_adapter, parent, false);
        viewHolder = new ChallengeListingViewHolder(v0);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChallengeListingViewHolder holder, int position) {
        if (position == 0) {
            holder.rootview.setVisibility(View.GONE);
            holder.challengeHeaderText.setVisibility(View.VISIBLE);
            if (challengeImageUrl != null) {
                holder.challengeNameImage.setVisibility(View.VISIBLE);
                try {
                    Glide.with(holder.challengeNameImage.getContext()).load(challengeImageUrl)
                            .into(holder.challengeNameImage);
                } catch (Exception e) {
                    holder.challengeNameImage
                            .setImageDrawable(ContextCompat
                                    .getDrawable(holder.challengeNameImage.getContext(), R.drawable.default_article));
                }
            } else {
                holder.challengeNameTextView.setVisibility(View.VISIBLE);
                holder.challengeNameTextView.setText(challengeName);
            }
        } else {
            holder.rootview.setVisibility(View.VISIBLE);
            holder.challengeHeaderText.setVisibility(View.GONE);
            viewListingResult(holder, position - 1);
        }
    }

    private void viewListingResult(ChallengeListingViewHolder holder, int position) {
        if (articleDataModelsNew.get(position).getIsfollowing().equals("1")) {
            holder.followAuthorTextView.setText(
                    holder.followAuthorTextView.getContext().getResources().getString(R.string.ad_following_author));
        } else {
            holder.followAuthorTextView.setText(
                    holder.followAuthorTextView.getContext().getResources().getString(R.string.ad_follow_author));
        }
        try {
            Picasso.get().load(articleDataModelsNew.get(position).getStoryImage().trim())
                    .placeholder(R.drawable.default_article).into(holder.storyImage);
        } catch (Exception e) {
            holder.storyImage.setImageResource(R.drawable.default_article);
        }
        try {
            Picasso.get().load(articleDataModelsNew.get(position).getStoryImage()).into(holder.shareStoryImageView);
            holder.storyAuthorTextView.setText(articleDataModelsNew.get(position).getUserName());
            AppUtils.populateLogoImageLanguageWise(holder.itemView.getContext(), holder.logoImageView,
                    articleDataModelsNew.get(position).getLang());
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
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
            holder.likeImageView.setImageDrawable(
                    ContextCompat.getDrawable(holder.likeImageView.getContext(), R.drawable.ic_recommended));
        } else {
            holder.likeImageView.setImageDrawable(
                    ContextCompat.getDrawable(holder.likeImageView.getContext(), R.drawable.ic_ss_like));
        }
        holder.authorNameTextView.setText(articleDataModelsNew.get(position).getUserName());
        setWinnerOrGoldFlag(holder.trophyImageView, articleDataModelsNew.get(position));
    }

    private void setWinnerOrGoldFlag(ImageView winnerGoldImageView, ArticleListingResult articleListingResult) {
        try {
            if ("1".equals(articleListingResult.getWinner()) || "true".equals(articleListingResult.getWinner())) {
                winnerGoldImageView.setImageResource(R.drawable.ic_trophy);
                winnerGoldImageView.setVisibility(View.VISIBLE);
            } else if ("1".equals(articleListingResult.getIsGold()) || "true"
                    .equals(articleListingResult.getIsGold())) {
                winnerGoldImageView.setImageResource(R.drawable.ic_star_yellow);
                winnerGoldImageView.setVisibility(View.VISIBLE);
            } else {
                winnerGoldImageView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            winnerGoldImageView.setVisibility(View.GONE);
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size() + 1;
    }

    public class ChallengeListingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RelativeLayout rootview;
        private RelativeLayout challengeHeaderText;
        private TextView challengeNameTextView;
        TextView authorNameTextView;
        TextView storyCommentCountTextView;
        LinearLayout storyRecommendationContainer;
        LinearLayout storyCommentContainer;
        TextView storyRecommendationCountTextView;
        ImageView storyImage;
        ImageView likeImageView;
        ImageView facebookShareImageView;
        ImageView whatsappShareImageView;
        ImageView instagramShareImageView;
        ImageView genericShareImageView;
        TextView submitChallenegLayout;
        TextView followAuthorTextView;
        ImageView challengeNameImage;
        ImageView menuItem;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView;
        TextView storyAuthorTextView;
        ImageView logoImageView;
        ImageView trophyImageView;

        ChallengeListingViewHolder(View itemView) {
            super(itemView);
            challengeNameImage = itemView.findViewById(R.id.ChallengeNameImage);
            challengeHeaderText = itemView.findViewById(R.id.challenge_header_text);
            challengeNameTextView = itemView.findViewById(R.id.ChallengeNameText);
            authorNameTextView = itemView.findViewById(R.id.authorNameTextView);
            storyRecommendationContainer = itemView.findViewById(R.id.storyRecommendationContainer);
            storyCommentContainer = itemView.findViewById(R.id.storyCommentContainer);
            storyCommentCountTextView = itemView.findViewById(R.id.storyCommentCountTextView);
            storyRecommendationCountTextView = itemView.findViewById(R.id.storyRecommendationCountTextView);
            storyImage = itemView.findViewById(R.id.storyImageView1);
            likeImageView = itemView.findViewById(R.id.likeImageView);
            facebookShareImageView = itemView.findViewById(R.id.facebookShareImageView);
            whatsappShareImageView = itemView.findViewById(R.id.whatsappShareImageView);
            instagramShareImageView = itemView.findViewById(R.id.instagramShareImageView);
            genericShareImageView = itemView.findViewById(R.id.genericShareImageView);
            rootview = itemView.findViewById(R.id.rootView);
            submitChallenegLayout = itemView.findViewById(R.id.submit_story_text);
            menuItem = itemView.findViewById(R.id.menuItem);
            followAuthorTextView = itemView.findViewById(R.id.followAuthorTextView);
            storyShareCardWidget = itemView.findViewById(R.id.storyShareCardWidget);
            trophyImageView = itemView.findViewById(R.id.trophyImageView);
            shareStoryImageView = storyShareCardWidget.findViewById(R.id.storyImageView);
            storyAuthorTextView = storyShareCardWidget.findViewById(R.id.storyAuthorTextView);
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
            recyclerViewClickListener.onClick(view, getAdapterPosition() - 1, challengeImageUrl);
        }
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position, String activeUrl);
    }
}
