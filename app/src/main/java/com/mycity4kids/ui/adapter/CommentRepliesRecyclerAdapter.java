package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.utils.DateTimeUtils;
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
            //  commentsViewHolder.commentDataTextView.setText(repliesList.get(position).getMessage());
            commentsViewHolder.commentDataTextView.setText((Html
                    .fromHtml(
                            "<b>" + "<font color=\"#D54058\">" + repliesList.get(position).getUserName() + "</font>"
                                    + "</b>"
                                    + " "
                                    + "<font color=\"#4A4A4A\">" + repliesList.get(position).getMessage()
                                    + "</font>")));
            commentsViewHolder.DateTextView.setText(DateTimeUtils
                    .getDateFromNanoMilliTimestamp(Long.parseLong(repliesList.get(position).getCreatedTime())));
            if (repliesList.get(position).getReplies() == null || repliesList.get(position).getReplies().isEmpty()
                    || repliesList.get(position).getRepliesCount() == 0) {
                commentsViewHolder.replyCommentTextView.setText(mContext.getString(R.string.reply));
            } else {
                commentsViewHolder.replyCommentTextView.setText(
                        mContext.getString(R.string.reply) + "(" + repliesList.get(position)
                                .getRepliesCount() + ")");
            }
            //  commentsViewHolder.commentDateTextView.setText(DateTimeUtils
            //   .getDateFromNanoMilliTimestamp(Long.parseLong(repliesList.get(position).getCreatedTime())));
            try {
                Picasso.get().load(repliesList.get(position).getUserPic().getClientAppMin())
                        .placeholder(R.drawable.default_commentor_img).into((commentsViewHolder.commentorImageView));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Picasso.get().load(R.drawable.default_commentor_img).into(commentsViewHolder.commentorImageView);
            }

            if (repliesList.get(position).getLiked()) {
                Drawable myDrawable = ContextCompat
                        .getDrawable(commentsViewHolder.likeTextView.getContext(), R.drawable.ic_like);
                commentsViewHolder.likeTextView.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            } else {
                Drawable myDrawable = ContextCompat
                        .getDrawable(commentsViewHolder.likeTextView.getContext(), R.drawable.ic_like_grey);
                commentsViewHolder.likeTextView.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            }
            if (repliesList.get(position).getLikeCount() <= 0) {
                commentsViewHolder.likeTextView.setText("");

            } else {
                commentsViewHolder.likeTextView.setText(repliesList.get(position).getLikeCount() + "");

            }
        } else {
            RepliesViewHolder repliesViewHolder = (RepliesViewHolder) holder;
            repliesViewHolder.commentDataTextView.setText((Html
                    .fromHtml(
                            "<b>" + "<font color=\"#D54058\">" + repliesList.get(position).getUserName() + "</font>"
                                    + "</b>"
                                    + " "
                                    + "<font color=\"#4A4A4A\">" + repliesList.get(position).getMessage()
                                    + "</font>")));
            repliesViewHolder.commentorUsernameTextView.setText(repliesList.get(position).getUserName());
            repliesViewHolder.commentDateTextView.setText(DateTimeUtils
                    .getDateFromNanoMilliTimestamp(Long.parseLong(repliesList.get(position).getCreatedTime())));
            try {
                Picasso.get().load(repliesList.get(position).getUserPic().getClientAppMin())
                        .placeholder(R.drawable.default_commentor_img).into((repliesViewHolder.commentorImageView));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Picasso.get().load(R.drawable.default_commentor_img).into(repliesViewHolder.commentorImageView);
            }
            if (repliesList.get(position).getLiked()) {
                Drawable myDrawable = ContextCompat
                        .getDrawable(repliesViewHolder.likeTextView.getContext(), R.drawable.ic_like);
                repliesViewHolder.likeTextView.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            } else {
                Drawable myDrawable = ContextCompat
                        .getDrawable(repliesViewHolder.likeTextView.getContext(), R.drawable.ic_like_grey);
                repliesViewHolder.likeTextView.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            }
            if (repliesList.get(position).getLikeCount() <= 0) {
                repliesViewHolder.likeTextView.setText("");

            } else {
                repliesViewHolder.likeTextView.setText(repliesList.get(position).getLikeCount() + "");

            }
        }
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        ImageView commentorImageView;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView replyCommentTextView;
        TextView commentDateTextView;
        TextView replyCountTextView;
        TextView DateTextView;
        TextView likeTextView;
        ImageView moreOptionImageView;

        CommentsViewHolder(View view) {
            super(view);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            replyCommentTextView = (TextView) view.findViewById(R.id.replyCommentTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            likeTextView = (TextView) view.findViewById(R.id.likeTextView);
            replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);
            DateTextView = (TextView) view.findViewById(R.id.DateTextView);
            moreOptionImageView = (ImageView) view.findViewById(R.id.moreOptionImageView);
            moreOptionImageView.setOnClickListener(this);
            replyCommentTextView.setVisibility(View.GONE);
            likeTextView.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.moreOptionImageView) {
                mListener.onRecyclerItemLongClick(v, getAdapterPosition());
            } else {
                mListener.onRecyclerItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onRecyclerItemLongClick(v, getAdapterPosition());
            return true;
        }
    }

    public class RepliesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        ImageView commentorImageView;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView commentDateTextView;
        TextView DateTextView;
        TextView likeTextView;
        TextView replyCommentTextView;
        ImageView moreOptionRepliesImageView;

        RepliesViewHolder(View view) {
            super(view);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            DateTextView = (TextView) view.findViewById(R.id.DateTextView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            likeTextView = (TextView) view.findViewById(R.id.likeTextView);
            replyCommentTextView = (TextView) view.findViewById(R.id.replyCommentTextView);
            moreOptionRepliesImageView = (ImageView) view.findViewById(R.id.moreOptionRepliesImageView);
            moreOptionRepliesImageView.setOnClickListener(this);
            likeTextView.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.moreOptionRepliesImageView) {
                mListener.onRecyclerItemLongClick(v, getAdapterPosition());
            } else {
                mListener.onRecyclerItemClick(v, getAdapterPosition());
            }
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
