package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.CircleTransformation;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.fragment.CommentRepliesDialogFragment;
import com.mycity4kids.ui.fragment.EditCommentsRepliesFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hemant on 13/4/16.
 */
public class CommentsReplyAdapter extends ArrayAdapter<CommentsData> {

    private LayoutInflater mInflater;
    private List<CommentsData> replyList;
    private Context mContext;
    private static final int TYPE_REPLY_LEVEL_ONE = 0;
    private static final int TYPE_REPLY_LEVEL_TWO = 1;
    private static final int TYPE_MAX_COUNT = 2;
    private int fragmentReplyLevel;
    private ReplyCommentInterface replyCommentInterface;
    public CommentsReplyAdapter(Context context, int resource, List<CommentsData> replyList, ReplyCommentInterface replyCommentInterface) {
        super(context, resource, replyList);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.replyList = replyList;
        mContext = context;
        this.replyCommentInterface = replyCommentInterface;
    }

    static class ViewHolder {
        ImageView replierImageView;
        TextView replierNameTextView;
        TextView replyDateTextView;
        TextView replyDescTextView;
        TextView replyBtnTextView;
        TextView editBtnTextView;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            if (getItemViewType(position) == TYPE_REPLY_LEVEL_ONE) {
                view = mInflater.inflate(R.layout.custom_comment_cell, null);
            } else {
                view = mInflater.inflate(R.layout.reply_reply_cell, null);
            }

            holder.replierNameTextView = (TextView) view.findViewById(R.id.txvCommentTitle);
            holder.replyDateTextView = (TextView) view.findViewById(R.id.txvDate);
            holder.replyDescTextView = (TextView) view.findViewById(R.id.txvCommentDescription);
            holder.replierImageView = (ImageView) view.findViewById(R.id.network_img);
            holder.replyBtnTextView = (TextView) view.findViewById(R.id.txvReply);
            holder.editBtnTextView = (TextView) view.findViewById(R.id.txvEdit);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        if (SharedPrefUtils.getUserDetailModel(mContext).getDynamoId().equals(replyList.get(position).getUserId())) {
            holder.editBtnTextView.setVisibility(View.VISIBLE);
            holder.editBtnTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    try {
//                        EditCommentsRepliesFragment editCommentsRepliesFragment = new EditCommentsRepliesFragment();
//
//                        CommentsData cData = (CommentsData) v.getTag();
////                        commentEditView = (View) v.getParent().getParent();
//                        Bundle _args = new Bundle();
//                        _args.putParcelable("commentData", cData);
//                        _args.putString("articleId", articleId);
//                        editCommentsRepliesFragment.setArguments(_args);
//                        FragmentManager fm = ((ArticlesAndBlogsDetailsActivity) mContext).getSupportFragmentManager();
//                        editCommentsRepliesFragment.show(fm, "Replies");
//                    } catch (Exception e) {
//                        Crashlytics.logException(e);
//                        Log.d("MC4kException", Log.getStackTraceString(e));
//                    }
                }
            });
        } else {
            holder.editBtnTextView.setVisibility(View.INVISIBLE);
        }

        if (position == 0) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_color));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blog_comments_reply_bg));
            if (getItemViewType(position) == TYPE_REPLY_LEVEL_ONE && fragmentReplyLevel == 0) {
                holder.replyBtnTextView.setVisibility(View.VISIBLE);
            } else {
                holder.replyBtnTextView.setVisibility(View.INVISIBLE);
            }
        }
        holder.replyBtnTextView.setTag(replyList.get(position).getParent_id());

        holder.replierNameTextView.setText(replyList.get(position).getName());
        holder.replyDateTextView.setText(DateTimeUtils.getSeperateDate(replyList.get(position).getCreate()));
        holder.replyDescTextView.setText(replyList.get(position).getBody());
        holder.replyBtnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyCommentInterface.onReplyButtonClicked(position);
            }
        });
        if (replyList.get(position).getProfile_image() != null && !StringUtils.isNullOrEmpty(replyList.get(position).getProfile_image().getClientAppMin())) {
            try {
                Picasso.with(mContext).load(replyList.get(position).getProfile_image().getClientAppMin()).placeholder(R.drawable.default_commentor_img)
                        .transform(new CircleTransformation()).into(holder.replierImageView);
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(mContext).load(R.drawable.default_commentor_img).transform(new CircleTransformation()).into(holder.replierImageView);
            }
        } else {
            Picasso.with(mContext).load(R.drawable.default_commentor_img).transform(new CircleTransformation()).into(holder.replierImageView);
        }

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return replyList.get(position).getCommentLevel();
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    public interface ReplyCommentInterface{
        void onReplyButtonClicked(int posit);
    }
}
