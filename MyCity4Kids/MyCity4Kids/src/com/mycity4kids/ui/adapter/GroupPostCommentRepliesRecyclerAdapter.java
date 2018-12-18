package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.ui.activity.NewsLetterWebviewActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

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
    private final String localizedNotHelpful, localizedHelpful;

    public GroupPostCommentRepliesRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
        localizedHelpful = mContext.getString(R.string.groups_post_helpful);
        localizedNotHelpful = mContext.getString(R.string.groups_post_nothelpful);
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
            if (repliesList.get(position).getIsAnnon() == 1) {
                commentsViewHolder.commentorUsernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                commentsViewHolder.commentorImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) repliesList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    commentsViewHolder.commentDateTextView.setVisibility(View.GONE);
                    commentsViewHolder.media.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mediaList.get(0)).error(R.drawable.default_article).into(commentsViewHolder.media);
                } else {
                    commentsViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    commentsViewHolder.media.setVisibility(View.GONE);
                }
            } else {
                commentsViewHolder.commentorUsernameTextView.setText(repliesList.get(position).getUserInfo().getFirstName()
                        + " " + repliesList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(repliesList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(commentsViewHolder.commentorImageView);
                } catch (Exception e) {
                    commentsViewHolder.commentorImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) repliesList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    commentsViewHolder.commentDateTextView.setVisibility(View.GONE);
                    commentsViewHolder.media.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mediaList.get(0)).error(R.drawable.default_article).into(commentsViewHolder.media);
                } else {
                    commentsViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    commentsViewHolder.media.setVisibility(View.GONE);
                }
            }
            commentsViewHolder.upvoteCommentCountTextView.setText(repliesList.get(position).getHelpfullCount() + " " + localizedHelpful);
            commentsViewHolder.downvoteCommentCountTextView.setText(repliesList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            commentsViewHolder.commentDataTextView.setText(repliesList.get(position).getContent());
            Linkify.addLinks(commentsViewHolder.commentDataTextView, Linkify.WEB_URLS);
            commentsViewHolder.commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            commentsViewHolder.commentDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(commentsViewHolder.commentDataTextView);
            commentsViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(repliesList.get(position).getCreatedAt()));
        } else {
            RepliesViewHolder repliesViewHolder = (RepliesViewHolder) holder;
            if (repliesList.get(position).getIsAnnon() == 1) {
                repliesViewHolder.commentorUsernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                repliesViewHolder.commentorImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) repliesList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    repliesViewHolder.commentDateTextView.setVisibility(View.GONE);
                    repliesViewHolder.mediaview.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mediaList.get(0)).error(R.drawable.default_article).into(repliesViewHolder.mediaview);
                } else {
                    repliesViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    repliesViewHolder.mediaview.setVisibility(View.GONE);
                }
            } else {
                repliesViewHolder.commentorUsernameTextView.setText(repliesList.get(position).getUserInfo().getFirstName()
                        + " " + repliesList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(repliesList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(repliesViewHolder.commentorImageView);
                } catch (Exception e) {
                    repliesViewHolder.commentorImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) repliesList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    repliesViewHolder.commentDateTextView.setVisibility(View.GONE);
                    repliesViewHolder.mediaview.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mediaList.get(0)).error(R.drawable.default_article).into(repliesViewHolder.mediaview);
                } else {
                    repliesViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    repliesViewHolder.mediaview.setVisibility(View.GONE);
                }
            }
            repliesViewHolder.upvoteReplyCountTextView.setText(repliesList.get(position).getHelpfullCount() + " " + localizedHelpful);
            repliesViewHolder.downvoteReplyCountTextView.setText(repliesList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            repliesViewHolder.commentDataTextView.setText(repliesList.get(position).getContent());
            Linkify.addLinks(repliesViewHolder.commentDataTextView, Linkify.WEB_URLS);
            repliesViewHolder.commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            repliesViewHolder.commentDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(repliesViewHolder.commentDataTextView);
            repliesViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(repliesList.get(position).getCreatedAt()));
        }
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView commentorImageView;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView replyCommentTextView;
        TextView commentDateTextView;
        TextView replyCountTextView;
        ImageView media;
        TextView upvoteCommentCountTextView, downvoteCommentCountTextView;
        LinearLayout upvoteCommentContainer, downvoteCommentContainer;

        CommentsViewHolder(View view) {
            super(view);
            media = (ImageView) view.findViewById(R.id.media);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            replyCommentTextView = (TextView) view.findViewById(R.id.replyCommentTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);
            upvoteCommentCountTextView = (TextView) view.findViewById(R.id.upvoteCommentTextView);
            downvoteCommentCountTextView = (TextView) view.findViewById(R.id.downvoteCommentTextView);
            upvoteCommentContainer = (LinearLayout) view.findViewById(R.id.upvoteCommentContainer);
            downvoteCommentContainer = (LinearLayout) view.findViewById(R.id.downvoteCommentContainer);
            replyCommentTextView.setVisibility(View.GONE);

            downvoteCommentContainer.setOnClickListener(this);
            upvoteCommentContainer.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onRecyclerItemClick(view, getAdapterPosition());
            return true;
        }
    }

    public class RepliesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView commentorImageView;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView commentDateTextView;
        ImageView mediaview;
        TextView upvoteReplyCountTextView, downvoteReplyCountTextView;
        LinearLayout upvoteReplyContainer, downvoteReplyContainer;

        RepliesViewHolder(View view) {
            super(view);
            mediaview = (ImageView) view.findViewById(R.id.media);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            upvoteReplyCountTextView = (TextView) view.findViewById(R.id.upvoteReplyTextView);
            downvoteReplyCountTextView = (TextView) view.findViewById(R.id.downvoteReplyTextView);
            upvoteReplyContainer = (LinearLayout) view.findViewById(R.id.upvoteReplyContainer);
            downvoteReplyContainer = (LinearLayout) view.findViewById(R.id.downvoteReplyContainer);

            downvoteReplyContainer.setOnClickListener(this);
            upvoteReplyContainer.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onRecyclerItemClick(view, getAdapterPosition());
            return true;
        }
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);
    }

    private void addLinkHandler(TextView textView) {
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) textView.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();//should clear old spans
            for (URLSpan url : urls) {
                CustomerTextClick click = new CustomerTextClick(url.getURL());
                style.setSpan(click, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(style);
        }
    }

    private class CustomerTextClick extends ClickableSpan {

        private String mUrl;

        CustomerTextClick(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(mContext, NewsLetterWebviewActivity.class);
            intent.putExtra(Constants.URL, mUrl);
            mContext.startActivity(intent);
        }
    }
}