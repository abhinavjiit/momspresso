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
import com.mycity4kids.models.response.ArticleListingResult;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hemant on 19/7/17.
 */
public class UserPublishedArticleAdapter extends RecyclerView.Adapter<UserPublishedArticleAdapter.UserPublishedArticleViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    private RecyclerViewClickListener mListener;
    private boolean isPrivateProfile;

    public UserPublishedArticleAdapter(Context pContext, RecyclerViewClickListener listener, boolean isPrivateProfile) {

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
        this.isPrivateProfile = isPrivateProfile;
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public UserPublishedArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserPublishedArticleViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.user_published_article_list_item, parent, false);
        viewHolder = new UserPublishedArticleViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserPublishedArticleViewHolder holder, int position) {

        if (null != articleDataModelsNew.get(position).getImageUrl()) {
            Picasso.with(mContext).load(articleDataModelsNew.get(position).getImageUrl().getThumbMin()).
                    placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
        } else {
            holder.articleImageView.setBackgroundResource(R.drawable.article_default);
        }

        holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
        holder.viewCountTextView.setText(articleDataModelsNew.get(position).getArticleCount());
        holder.commentCountTextView.setText(articleDataModelsNew.get(position).getCommentsCount());
        holder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLikesCount());
        if (isPrivateProfile) {
            holder.editPublishedTextView.setVisibility(View.VISIBLE);
        } else {
            holder.editPublishedTextView.setVisibility(View.GONE);
        }

        try {
            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.US);
            calendar1.setTimeInMillis(articleDataModelsNew.get(position).getCreatedTime() * 1000);

            Long diff = System.currentTimeMillis() - articleDataModelsNew.get(position).getCreatedTime() * 1000;
            if (diff / (1000 * 60 * 60) > 24 && !sdf.format(System.currentTimeMillis()).equals(sdf.format((articleDataModelsNew.get(position).getCreatedTime() * 1000)))) {
                holder.txvPublishDate.setText(DateTimeUtils.getDateFromTimestamp(articleDataModelsNew.get(position).getCreatedTime()));
            } else {
                holder.txvPublishDate.setText(sdf1.format(calendar1.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

//    public void refreshArticleList(ArrayList<SearchArticleResult> newList) {
//        this.articleDataModelsNew = newList;
//    }

    public class UserPublishedArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView articleImageView;
        TextView txvArticleTitle;
        TextView txvPublishDate;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        ImageView shareArticleImageView;
        TextView editPublishedTextView;

        public UserPublishedArticleViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            txvArticleTitle = (TextView) itemView.findViewById(R.id.txvArticleTitle);
            txvPublishDate = (TextView) itemView.findViewById(R.id.txvPublishDate);
            viewCountTextView = (TextView) itemView.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) itemView.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) itemView.findViewById(R.id.recommendCountTextView);
            articleImageView = (ImageView) itemView.findViewById(R.id.articleImageView);
            shareArticleImageView = (ImageView) itemView.findViewById(R.id.shareArticleImageView);
            editPublishedTextView = (TextView) itemView.findViewById(R.id.editPublishedTextView);
            shareArticleImageView.setOnClickListener(this);
            editPublishedTextView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

}