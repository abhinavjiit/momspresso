package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.ui.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hemant on 13/4/16.
 */
public class CommentsReplyAdapter extends ArrayAdapter<CommentsData> {

    private LayoutInflater mInflater;
    private List<CommentsData> replyList;
    private Context mContext;

    public CommentsReplyAdapter(Context context, int resource, List<CommentsData> replyList) {
        super(context, resource, replyList);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.replyList = replyList;
        mContext = context;
    }

    static class ViewHolder {
        ImageView replierImageView;
        TextView replierNameTextView;
        TextView replyDateTextView;
        TextView replyDescTextView;
        TextView replyBtnTextView;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.custom_comment_cell, null);
            holder = new ViewHolder();
            holder.replierNameTextView = (TextView) view.findViewById(R.id.txvCommentTitle);
            holder.replyDateTextView = (TextView) view.findViewById(R.id.txvDate);
            holder.replyDescTextView = (TextView) view.findViewById(R.id.txvCommentDescription);
            holder.replierImageView = (ImageView) view.findViewById(R.id.network_img);
            holder.replyBtnTextView = (TextView) view.findViewById(R.id.txvReply);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (position == 0) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_color));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blog_comments_reply_bg));
        }
        holder.replierNameTextView.setText(replyList.get(position).getName());
        holder.replyDateTextView.setText(DateTimeUtils.getSeperateDate(replyList.get(position).getCreate()));
        holder.replyDescTextView.setText(replyList.get(position).getBody());
        holder.replyBtnTextView.setVisibility(View.INVISIBLE);
        if (!StringUtils.isNullOrEmpty(replyList.get(position).getProfile_image())) {
            try {
                Picasso.with(mContext).load(replyList.get(position).getProfile_image()).placeholder(R.drawable.default_commentor_img)
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
}
