package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.GroupPostCommentResult;

import java.util.ArrayList;

/**
 * Created by hemant on 23/5/18.
 */

public class GroupPostCommentRepliesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int RESPONSE_TYPE_COMMENT = 0;
    private static final int RESPONSE_TYPE_REPLY = 1;
    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<GroupPostCommentResult> repliesList;
    private RecyclerViewClickListener mListener;

    public GroupPostCommentRepliesRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }

    public void setData(ArrayList<GroupPostCommentResult> repliesList) {
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
            View v0 = mInflator.inflate(R.layout.group_post_replies_cell, parent, false);
            return new RepliesViewHolder(v0);
        } else {
            View v0 = mInflator.inflate(R.layout.group_post_comment_cell_test, parent, false);
            return new CommentsViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CommentsViewHolder) {
            CommentsViewHolder commentsViewHolder = (CommentsViewHolder) holder;
            commentsViewHolder.commentorUsernameTextView.setText(repliesList.get(position).getUserId());
            commentsViewHolder.commentDataTextView.setText(repliesList.get(position).getContent());
            commentsViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(repliesList.get(position).getCreatedAt()));
        } else {
            RepliesViewHolder repliesViewHolder = (RepliesViewHolder) holder;
            repliesViewHolder.commentorUsernameTextView.setText(repliesList.get(position).getUserId());
            repliesViewHolder.commentDataTextView.setText(repliesList.get(position).getContent());
            repliesViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(repliesList.get(position).getCreatedAt()));
        }
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class RepliesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);
    }
}