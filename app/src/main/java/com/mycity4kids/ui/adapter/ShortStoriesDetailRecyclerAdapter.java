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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.ui.fragment.ShortStoryFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.StoryShareCardWidget;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by hemant on 30/5/18.
 */

public class ShortStoriesDetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER = 0;
    private static final int COMMENT_LEVEL_ROOT = 1;
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ShortStoryFragment.ShortStoryDetailAndCommentModel> datalist;
    private RecyclerViewClickListener mListener;
    private int colorPosition;
    private String followingStatus = "";

    public ShortStoriesDetailRecyclerAdapter(Context pContext, RecyclerViewClickListener listener, int colorPosition) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
        this.colorPosition = colorPosition;
    }

    public void setListData(ArrayList<ShortStoryFragment.ShortStoryDetailAndCommentModel> datalist) {
        this.datalist = datalist;
    }

    public void setAuthorFollowingStatus(String followingStatus) {
        this.followingStatus = followingStatus;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return COMMENT_LEVEL_ROOT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View v0 = mInflator.inflate(R.layout.short_story_listing_item, parent, false);
            ShortStoriesViewHolder viewHolder = new ShortStoriesViewHolder(v0, mListener);
            return viewHolder;
        } else {
            View v0 = mInflator.inflate(R.layout.ss_comment_item, parent, false);
            SSCommentViewHolder ssCommentViewHolder = new SSCommentViewHolder(v0, mListener);
            return ssCommentViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ShortStoriesViewHolder) {
                ShortStoriesViewHolder ssViewHolder = (ShortStoriesViewHolder) holder;
                try {
                    Picasso.get().load(datalist.get(position).getSsResult().getStoryImage().trim())
                            .placeholder(R.drawable.default_article).into(ssViewHolder.storyImage);
                } catch (Exception e) {
                    ssViewHolder.storyImage.setImageResource(R.drawable.default_article);
                }
                try {
                    Picasso.get().load(datalist.get(position).getSsResult().getStoryImage())
                            .into(ssViewHolder.shareStoryImageView);
                    ssViewHolder.storyAuthorTextView.setText(datalist.get(position).getSsResult().getUserName());
                    AppUtils.populateLogoImageLanguageWise(holder.itemView.getContext(), ssViewHolder.logoImageView,
                            datalist.get(position).getSsResult().getLang());
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
                ssViewHolder.authorNameTextView.setText(datalist.get(position).getSsResult().getUserName());
                if (StringUtils.isNullOrEmpty(followingStatus)) {
                    ssViewHolder.followAuthorTextView.setVisibility(View.GONE);
                } else if (AppConstants.STATUS_FOLLOWING.equals(followingStatus)) {
                    ssViewHolder.followAuthorTextView.setVisibility(View.VISIBLE);
                    ssViewHolder.followAuthorTextView
                            .setText(WordUtils.capitalizeFully(mContext.getString(R.string.ad_following_author)));
                } else {
                    ssViewHolder.followAuthorTextView.setVisibility(View.VISIBLE);
                    ssViewHolder.followAuthorTextView
                            .setText(WordUtils.capitalizeFully(mContext.getString(R.string.ad_follow_author)));
                }
                if (null == datalist.get(position).getSsResult().getCommentCount()) {
                    ssViewHolder.storyCommentCountTextView.setText("0");
                } else {
                    ssViewHolder.storyCommentCountTextView.setVisibility(View.VISIBLE);
                    ssViewHolder.storyCommentCountTextView
                            .setText(datalist.get(position).getSsResult().getCommentCount());
                }

                if (null == datalist.get(position).getSsResult().getLikeCount()) {
                    ssViewHolder.storyRecommendationCountTextView.setText("0");
                } else {
                    ssViewHolder.storyRecommendationCountTextView.setVisibility(View.VISIBLE);
                    ssViewHolder.storyRecommendationCountTextView
                            .setText(datalist.get(position).getSsResult().getLikeCount());
                }

                if (datalist.get(position).getSsResult().isLiked()) {
                    ssViewHolder.likeImageView
                            .setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_recommended));
                } else {
                    ssViewHolder.likeImageView
                            .setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_ss_like));
                }
            } else {
                SSCommentViewHolder ssCommentViewHolder = (SSCommentViewHolder) holder;
                ssCommentViewHolder.commentorUsernameTextView
                        .setText(datalist.get(position).getSsComment().getUserName());
                ssCommentViewHolder.commentDataTextView.setText(datalist.get(position).getSsComment().getMessage());
                ssCommentViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(
                        Long.parseLong(datalist.get(position).getSsComment().getCreatedTime())));
                if (datalist.get(position).getSsComment().getReplies() == null || datalist.get(position).getSsComment()
                        .getReplies().isEmpty()) {
                    ssCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
                } else {
                    ssCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                    ssCommentViewHolder.replyCountTextView.setText(mContext.getString(R.string.short_s_view_replies) + "(" + datalist.get(position).getSsComment().getRepliesCount() + ")");
                }
                try {
                    Picasso.get().load(datalist.get(position).getSsComment().getUserPic().getClientAppMin())
                            .placeholder(R.drawable.default_commentor_img)
                            .into((ssCommentViewHolder.commentorImageView));
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    Picasso.get().load(R.drawable.default_commentor_img).into(ssCommentViewHolder.commentorImageView);
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public int getItemCount() {
        return datalist == null ? 0 : datalist.size();
    }

    public class ShortStoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView authorNameTextView;
        TextView followAuthorTextView;
        LinearLayout storyRecommendationContainer, storyCommentContainer;
        TextView storyCommentCountTextView;
        TextView storyRecommendationCountTextView;
        ImageView storyImage, likeImageView, menuItem;
        ImageView facebookShareImageView, whatsappShareImageView, instagramShareImageView, genericShareImageView;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView;
        TextView storyAuthorTextView;
        ImageView logoImageView;

        ShortStoriesViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            authorNameTextView = (TextView) itemView.findViewById(R.id.authorNameTextView);
            followAuthorTextView = (TextView) itemView.findViewById(R.id.followAuthorTextView);
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
            menuItem = (ImageView) itemView.findViewById(R.id.menuItem);
            storyShareCardWidget = (StoryShareCardWidget) itemView.findViewById(R.id.storyShareCardWidget);
            shareStoryImageView = (ImageView) storyShareCardWidget.findViewById(R.id.storyImageView);
            storyAuthorTextView = (TextView) storyShareCardWidget.findViewById(R.id.storyAuthorTextView);
            logoImageView = (ImageView) storyShareCardWidget.findViewById(R.id.logoImageView);

            facebookShareImageView.setOnClickListener(this);
            whatsappShareImageView.setOnClickListener(this);
            instagramShareImageView.setOnClickListener(this);
            genericShareImageView.setOnClickListener(this);
            authorNameTextView.setOnClickListener(this);
            storyImage.setOnClickListener(this);
            storyRecommendationContainer.setOnClickListener(this);
            followAuthorTextView.setOnClickListener(this);
            itemView.setOnClickListener(this);
            menuItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition(), whatsappShareImageView);
        }
    }

    public class SSCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        ImageView commentorImageView, menuItem;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView replyCommentTextView;
        TextView commentDateTextView;
        TextView replyCountTextView;
        View underlineView;

        SSCommentViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            replyCommentTextView = (TextView) view.findViewById(R.id.replyCommentTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);

            view.setOnLongClickListener(this);
            replyCommentTextView.setOnClickListener(this);
            replyCountTextView.setOnClickListener(this);

            underlineView = view.findViewById(R.id.underlineView);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition(), v);
        }


        @Override
        public boolean onLongClick(View v) {
            mListener.onClick(v, getAdapterPosition(), v);
            return true;
        }
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position, View whatsappShare);
    }

}
