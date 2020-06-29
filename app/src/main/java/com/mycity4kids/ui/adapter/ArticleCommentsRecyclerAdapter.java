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

public class ArticleCommentsRecyclerAdapter extends
        RecyclerView.Adapter<ArticleCommentsRecyclerAdapter.CommentsViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<CommentListData> commentList;
    private RecyclerViewClickListener mListener;

    public ArticleCommentsRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }

    public void setData(ArrayList<CommentListData> commentList) {
        this.commentList = commentList;
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = mInflator.inflate(R.layout.ss_comment_item, parent, false);
        return new CommentsViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(final CommentsViewHolder commentsViewHolder, final int position) {
        commentsViewHolder.commentorUsernameTextView.setText(commentList.get(position).getUserName());
        commentsViewHolder.commentDataTextView.setText((Html
                .fromHtml(
                        "<b>" + "<font color=\"#D54058\">" + commentList.get(position).getUserName() + "</font>"
                                + "</b>"
                                + " "
                                + "<font color=\"#4A4A4A\">" + commentList.get(position).getMessage() + "</font>")));
        commentsViewHolder.DateTextView.setText(DateTimeUtils
                .getDateFromNanoMilliTimestamp(Long.parseLong(commentList.get(position).getCreatedTime())));
        if (commentList.get(position).getReplies() == null || commentList.get(position).getReplies().isEmpty()
                || commentList.get(position).getRepliesCount() == 0) {
            commentsViewHolder.replyCommentTextView.setText(mContext.getString(R.string.reply));
        } else {
            commentsViewHolder.replyCommentTextView.setText(
                    mContext.getString(R.string.reply) + "(" + commentList.get(position)
                            .getRepliesCount() + ")");
        }
        try {
            Picasso.get().load(commentList.get(position).getUserPic().getClientAppMin())
                    .placeholder(R.drawable.default_commentor_img).into((commentsViewHolder.commentorImageView));
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            Picasso.get().load(R.drawable.default_commentor_img).into(commentsViewHolder.commentorImageView);
        }

        if (commentList.get(position).getLiked()) {
            Drawable myDrawable = ContextCompat
                    .getDrawable(commentsViewHolder.likeTextView.getContext(), R.drawable.ic_like);
            commentsViewHolder.likeTextView.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
        } else {
            Drawable myDrawable = ContextCompat
                    .getDrawable(commentsViewHolder.likeTextView.getContext(), R.drawable.ic_like_grey);
            commentsViewHolder.likeTextView.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
        }
        if (commentList.get(position).getLikeCount() <= 0) {
            commentsViewHolder.likeTextView.setText("");

        } else {
            commentsViewHolder.likeTextView.setText(commentList.get(position).getLikeCount() + "");

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
            DateTextView = (TextView) view.findViewById(R.id.DateTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            replyCommentTextView = (TextView) view.findViewById(R.id.replyCommentTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);
            likeTextView = (TextView) view.findViewById(R.id.likeTextView);
            moreOptionImageView = (ImageView) view.findViewById(R.id.moreOptionImageView);
            view.setOnLongClickListener(this);
            replyCommentTextView.setOnClickListener(this);
            replyCountTextView.setOnClickListener(this);
            likeTextView.setOnClickListener(this);
            moreOptionImageView.setOnClickListener(this);
            commentorImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
            return true;
        }
    }

    public interface RecyclerViewClickListener {

        void onRecyclerItemClick(View view, int position);

    }
}
