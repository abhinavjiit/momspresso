package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.ui.fragment.ShortStoryFragment;
import com.mycity4kids.utils.AppUtils;
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
    private final float density;
    private RecyclerViewClickListener mListener;

    public ShortStoriesDetailRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
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

//                ssViewHolder.titleTextView.setText(datalist.get(position).getSsResult().getTitle());
//                ssViewHolder.bodyTextView.setText(datalist.get(position).getSsResult().getBody());
//                ssViewHolder.authorTextView.setText(datalist.get(position).getSsResult().getUserName());

                if (null == datalist.get(position).getSsResult().getCommentCount() || "0".equals(datalist.get(position).getSsResult().getCommentCount())) {
                    ssViewHolder.storyCommentCountTextView.setVisibility(View.GONE);
                } else {
                    ssViewHolder.storyCommentCountTextView.setVisibility(View.VISIBLE);
                    ssViewHolder.storyCommentCountTextView.setText(datalist.get(position).getSsResult().getCommentCount());
                }

                if (null == datalist.get(position).getSsResult().getLikeCount() || "0".equals(datalist.get(position).getSsResult().getLikeCount())) {
                    ssViewHolder.storyRecommendationCountTextView.setVisibility(View.GONE);
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
                    ssCommentViewHolder.replyCountTextView.setText("View Replies (" + datalist.get(position).getSsComment().getReplies_count() + ") replies");
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
        TextView storyCommentCountTextView;
        TextView storyRecommendationCountTextView;
        ImageView storyOptionImageView;
        ImageView facebookShareImageView, whatsappShareImageView, genericShareImageView;

        public ShortStoriesViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            storyTitleTextView = (TextView) itemView.findViewById(R.id.storyTitleTextView);
//            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            storyBodyTextView = (TextView) itemView.findViewById(R.id.storyBodyTextView);
//            bodyTextView = (TextView) itemView.findViewById(R.id.bodyTextView);
            authorNameTextView = (TextView) itemView.findViewById(R.id.authorNameTextView);
//            authorTextView = (TextView) itemView.findViewById(R.id.authorTextView);
            storyCommentCountTextView = (TextView) itemView.findViewById(R.id.storyCommentCountTextView);
            storyRecommendationCountTextView = (TextView) itemView.findViewById(R.id.storyRecommendationCountTextView);
            storyOptionImageView = (ImageView) itemView.findViewById(R.id.storyOptionImageView);
            facebookShareImageView = (ImageView) itemView.findViewById(R.id.facebookShareImageView);
            whatsappShareImageView = (ImageView) itemView.findViewById(R.id.whatsappShareImageView);
            genericShareImageView = (ImageView) itemView.findViewById(R.id.genericShareImageView);

            facebookShareImageView.setOnClickListener(this);
            whatsappShareImageView.setOnClickListener(this);
            genericShareImageView.setOnClickListener(this);
            authorNameTextView.setOnClickListener(this);
            storyOptionImageView.setOnClickListener(this);
            storyRecommendationCountTextView.setOnClickListener(this);
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