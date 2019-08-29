package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mycity4kids.R;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResult;

import java.util.ArrayList;

public class ChallengeListingRecycleAdapter extends RecyclerView.Adapter<ChallengeListingRecycleAdapter.ChallengeListingViewHolder> {
    private Context mContext;
    private LayoutInflater mInflator;
    private final float density;
    private Topics datamodal;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    private RecyclerViewClickListener recyclerViewClickListener;
    private int pos;
    private String selected_Name;
    private String ActiveUrl;

    public ChallengeListingRecycleAdapter(RecyclerViewClickListener recyclerViewClickListener, Context mContext, int pos, String selected_Name, String ActiveUrl) {
        density = mContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.selected_Name = selected_Name;
        this.mContext = mContext;
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.pos = pos;
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
            holder.mainView.setVisibility(View.VISIBLE);
            viewListingResult(holder, position - 1);

        }


    }

    private void viewListingResult(ChallengeListingViewHolder holder, int position) {
        switch (((position) % 6)) {
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
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size() + 1;

    }

    public class ChallengeListingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout rootview;
        private RelativeLayout mainview;
        private RelativeLayout titlecontainer;
        private RelativeLayout challengeHeaderText;
        private TextView ChallengeNameText;
        TextView storyTitleTextView;
        TextView storyBodyTextView;
        TextView authorNameTextView;
        TextView storyCommentCountTextView;
        LinearLayout storyRecommendationContainer, storyCommentContainer;
        TextView storyRecommendationCountTextView;
        ImageView storyOptionImageView, likeImageView;
        ImageView facebookShareImageView, whatsappShareImageView, instagramShareImageView, genericShareImageView;
        RelativeLayout mainView;
        TextView submitChallenegLayout;
        ImageView challengeNameImage;


        public ChallengeListingViewHolder(View itemView, RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            challengeNameImage = (ImageView) itemView.findViewById(R.id.ChallengeNameImage);
            challengeHeaderText = (RelativeLayout) itemView.findViewById(R.id.challenge_header_text);
            ChallengeNameText = (TextView) itemView.findViewById(R.id.ChallengeNameText);
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
            rootview = (RelativeLayout) itemView.findViewById(R.id.rootView);
            submitChallenegLayout = (TextView) itemView.findViewById(R.id.submit_story_text);
            whatsappShareImageView.setTag(itemView);
            submitChallenegLayout.setOnClickListener(this);
            storyRecommendationContainer.setOnClickListener(this);
            facebookShareImageView.setOnClickListener(this);
            whatsappShareImageView.setOnClickListener(this);
            instagramShareImageView.setOnClickListener(this);
            genericShareImageView.setOnClickListener(this);
            authorNameTextView.setOnClickListener(this);
            storyOptionImageView.setOnClickListener(this);
            challengeHeaderText.setOnClickListener(this);
            itemView.setOnClickListener(this);
            rootview.setOnClickListener(this);
            mainView.setOnClickListener(this);


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
