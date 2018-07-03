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
import com.mycity4kids.models.response.CommentListData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentRepliesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int RESPONSE_TYPE_COMMENT = 0;
    private static final int RESPONSE_TYPE_REPLY = 1;
    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<CommentListData> repliesList;
    private RecyclerViewClickListener mListener;

    public CommentRepliesRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }

    public void setData(ArrayList<CommentListData> repliesList) {
        this.repliesList = repliesList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return RESPONSE_TYPE_COMMENT;
        } else {
            return RESPONSE_TYPE_REPLY;
        }
    }

    @Override
    public int getItemCount() {
        return repliesList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == RESPONSE_TYPE_REPLY) {
            View v0 = mInflator.inflate(R.layout.ss_comment_replies_item, parent, false);
            return new RepliesViewHolder(v0);
        } else {
            View v0 = mInflator.inflate(R.layout.ss_comment_item, parent, false);
            return new CommentsViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CommentsViewHolder) {
            CommentsViewHolder commentsViewHolder = (CommentsViewHolder) holder;
            commentsViewHolder.commentorUsernameTextView.setText(repliesList.get(position).getUserName());
            commentsViewHolder.commentDataTextView.setText(repliesList.get(position).getMessage());
            commentsViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(repliesList.get(position).getCreatedTime())));
            try {
                Picasso.with(mContext).load(repliesList.get(position).getUserPic().getClientAppMin())
                        .placeholder(R.drawable.default_commentor_img).into((commentsViewHolder.commentorImageView));
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Picasso.with(mContext).load(R.drawable.default_commentor_img).into(commentsViewHolder.commentorImageView);
            }
        } else {
            RepliesViewHolder repliesViewHolder = (RepliesViewHolder) holder;
            repliesViewHolder.commentorUsernameTextView.setText(repliesList.get(position).getUserName());
            repliesViewHolder.commentDataTextView.setText(repliesList.get(position).getMessage());
            repliesViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(repliesList.get(position).getCreatedTime())));
            try {
                Picasso.with(mContext).load(repliesList.get(position).getUserPic().getClientAppMin())
                        .placeholder(R.drawable.default_commentor_img).into((repliesViewHolder.commentorImageView));
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Picasso.with(mContext).load(R.drawable.default_commentor_img).into(repliesViewHolder.commentorImageView);
            }
        }
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView commentorImageView;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView replyCommentTextView;
        TextView commentDateTextView;
        TextView replyCountTextView;

        CommentsViewHolder(View view) {
            super(view);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            replyCommentTextView = (TextView) view.findViewById(R.id.replyCommentTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);
            replyCommentTextView.setVisibility(View.GONE);

            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onRecyclerItemLongClick(v, getAdapterPosition());
            return true;
        }
    }

    public class RepliesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView commentorImageView;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView commentDateTextView;

        RepliesViewHolder(View view) {
            super(view);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);

            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onRecyclerItemLongClick(v, getAdapterPosition());
            return true;
        }
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);

        void onRecyclerItemLongClick(View view, int position);
    }
}