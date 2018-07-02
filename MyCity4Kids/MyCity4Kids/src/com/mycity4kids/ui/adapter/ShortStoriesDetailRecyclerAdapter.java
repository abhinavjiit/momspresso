package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.ui.fragment.ShortStoryFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 30/5/18.
 */

public class ShortStoriesDetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0;
    public static final int COMMENT_LEVEL_ROOT = 1;
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ShortStoryFragment.ShortStoryDetailAndCommentModel> datalist;
    private RecyclerViewClickListener mListener;
    private int colorPosition;

    public ShortStoriesDetailRecyclerAdapter(Context pContext, RecyclerViewClickListener listener, int colorPosition) {

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
        this.colorPosition = colorPosition;
    }

    public void setListData(ArrayList<ShortStoryFragment.ShortStoryDetailAndCommentModel> datalist) {
        this.datalist = datalist;
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
                ssViewHolder.storyTitleTextView.setText(datalist.get(position).getSsResult().getTitle());
                ssViewHolder.storyBodyTextView.setText(datalist.get(position).getSsResult().getBody());
                ssViewHolder.authorNameTextView.setText(datalist.get(position).getSsResult().getUserName());
                switch (colorPosition % 6) {
                    case 0:
                        ssViewHolder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_1));
                        break;
                    case 1:
                        ssViewHolder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_2));
                        break;
                    case 2:
                        ssViewHolder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_3));
                        break;
                    case 3:
                        ssViewHolder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_4));
                        break;
                    case 4:
                        ssViewHolder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_5));
                        break;
                    case 5:
                        ssViewHolder.mainView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.short_story_card_bg_6));
                        break;
                }

                if (null == datalist.get(position).getSsResult().getCommentCount()) {
                    ssViewHolder.storyCommentCountTextView.setText("0");
                } else {
                    ssViewHolder.storyCommentCountTextView.setVisibility(View.VISIBLE);
                    ssViewHolder.storyCommentCountTextView.setText(datalist.get(position).getSsResult().getCommentCount());
                }

                if (null == datalist.get(position).getSsResult().getLikeCount()) {
                    ssViewHolder.storyRecommendationCountTextView.setText("0");
                } else {
                    ssViewHolder.storyRecommendationCountTextView.setVisibility(View.VISIBLE);
                    ssViewHolder.storyRecommendationCountTextView.setText(datalist.get(position).getSsResult().getLikeCount());
                }
            } else {
                SSCommentViewHolder ssCommentViewHolder = (SSCommentViewHolder) holder;
                ssCommentViewHolder.commentorUsernameTextView.setText(datalist.get(position).getSsComment().getUserName());
                ssCommentViewHolder.commentDataTextView.setText(datalist.get(position).getSsComment().getMessage());
                ssCommentViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(datalist.get(position).getSsComment().getCreatedTime())));
                if (datalist.get(position).getSsComment().getReplies() == null || datalist.get(position).getSsComment().getReplies().isEmpty()) {
                    ssCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
                } else {
                    ssCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                    ssCommentViewHolder.replyCountTextView.setText(mContext.getString(R.string.short_s_view_replies) + "(" + datalist.get(position).getSsComment().getReplies_count() + ")");
                }
                try {
                    Picasso.with(mContext).load(datalist.get(position).getSsComment().getUserPic().getClientAppMin())
                            .placeholder(R.drawable.default_commentor_img).into((ssCommentViewHolder.commentorImageView));
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    Picasso.with(mContext).load(R.drawable.default_commentor_img).into(ssCommentViewHolder.commentorImageView);
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public int getItemCount() {
        return datalist == null ? 0 : datalist.size();
    }

    public class ShortStoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView storyTitleTextView;
        TextView storyBodyTextView;
        TextView authorNameTextView;
        LinearLayout storyRecommendationContainer, storyCommentContainer;
        TextView storyCommentCountTextView;
        TextView storyRecommendationCountTextView;
        ImageView storyOptionImageView;
        ImageView facebookShareImageView, whatsappShareImageView, instagramShareImageView, genericShareImageView;
        RelativeLayout mainView;

        public ShortStoriesViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            storyTitleTextView = (TextView) itemView.findViewById(R.id.storyTitleTextView);
            storyBodyTextView = (TextView) itemView.findViewById(R.id.storyBodyTextView);
            authorNameTextView = (TextView) itemView.findViewById(R.id.authorNameTextView);
            storyRecommendationContainer = (LinearLayout) itemView.findViewById(R.id.storyRecommendationContainer);
            storyCommentContainer = (LinearLayout) itemView.findViewById(R.id.storyCommentContainer);
            storyCommentCountTextView = (TextView) itemView.findViewById(R.id.storyCommentCountTextView);
            storyRecommendationCountTextView = (TextView) itemView.findViewById(R.id.storyRecommendationCountTextView);
            storyOptionImageView = (ImageView) itemView.findViewById(R.id.storyOptionImageView);
            facebookShareImageView = (ImageView) itemView.findViewById(R.id.facebookShareImageView);
            whatsappShareImageView = (ImageView) itemView.findViewById(R.id.whatsappShareImageView);
            instagramShareImageView = (ImageView) itemView.findViewById(R.id.instagramShareImageView);
            genericShareImageView = (ImageView) itemView.findViewById(R.id.genericShareImageView);
            mainView = (RelativeLayout) itemView.findViewById(R.id.mainView);

            facebookShareImageView.setOnClickListener(this);
            whatsappShareImageView.setOnClickListener(this);
            instagramShareImageView.setOnClickListener(this);
            genericShareImageView.setOnClickListener(this);
            authorNameTextView.setOnClickListener(this);
            storyOptionImageView.setOnClickListener(this);
            storyRecommendationContainer.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public class SSCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView commentorImageView;
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
            mListener.onClick(v, getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            mListener.onClick(v, getAdapterPosition());
            return true;
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

}