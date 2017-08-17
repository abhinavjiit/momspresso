package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.UserCommentsResult;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/17.
 */
public class UsersCommentsRecycleAdapter extends RecyclerView.Adapter<UsersCommentsRecycleAdapter.UserBookmarksViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<UserCommentsResult> articleDataModelsNew;
    private final float density;
    private RecyclerViewClickListener mListener;
    private boolean isPrivate = false;

    public UsersCommentsRecycleAdapter(Context pContext, RecyclerViewClickListener listener, boolean isPrivate) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
        this.isPrivate = isPrivate;
    }

    public void setListData(ArrayList<UserCommentsResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public UserBookmarksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserBookmarksViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.users_comment_recycle_item, parent, false);
        viewHolder = new UserBookmarksViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserBookmarksViewHolder holder, int position) {

        holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getArticleTitle());
        holder.commentBodyTextView.setText(articleDataModelsNew.get(position).getUserComment());
        holder.dateTextView.setText(mContext.getString(R.string.user_activities_comments_saved_on) + " " + DateTimeUtils.getDateFromTimestamp(articleDataModelsNew.get(position).getUpdatedTime()));

        if (isPrivate) {
            holder.editCommentTextView.setVisibility(View.VISIBLE);
        } else {
            holder.editCommentTextView.setVisibility(View.INVISIBLE);
        }
//        if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())
//                && (articleDataModelsNew.get(position).getImageUrl().getWebThumbnail() == null || articleDataModelsNew.get(position).getImageUrl().getWebThumbnail().endsWith("default.jpg"))) {
//            Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURLMomspresso(articleDataModelsNew.get(position).getVideoUrl())).placeholder(R.drawable.default_article).into(holder.articleImageView);
//        } else {
//            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getWebThumbnail())) {
//                Picasso.with(mContext).load(articleDataModelsNew.get(position).getImageUrl().getWebThumbnail())
//                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
//            } else if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getMobileWebThumbnail())) {
//                Picasso.with(mContext).load(articleDataModelsNew.get(position).getImageUrl().getMobileWebThumbnail())
//                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
//            } else {
//                holder.articleImageView.setBackgroundResource(R.drawable.default_article);
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class UserBookmarksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txvArticleTitle;
        TextView dateTextView;
        TextView commentBodyTextView;
        TextView editCommentTextView;

        public UserBookmarksViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            txvArticleTitle = (TextView) itemView.findViewById(R.id.articleTitleTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            commentBodyTextView = (TextView) itemView.findViewById(R.id.commentBodyTextView);
            editCommentTextView = (TextView) itemView.findViewById(R.id.editCommentTextView);
            editCommentTextView.setOnClickListener(this);
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