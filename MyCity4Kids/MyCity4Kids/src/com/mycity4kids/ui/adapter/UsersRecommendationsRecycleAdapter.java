package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/17.
 */
public class UsersRecommendationsRecycleAdapter extends RecyclerView.Adapter<UsersRecommendationsRecycleAdapter.UserRecommendationsViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    private final float density;
    private RecyclerViewClickListener mListener;

    public UsersRecommendationsRecycleAdapter(Context pContext, RecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public UserRecommendationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserRecommendationsViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.users_recommendation_recycle_item, parent, false);
        viewHolder = new UserRecommendationsViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserRecommendationsViewHolder holder, int position) {

        holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());

        if (null == articleDataModelsNew.get(position).getArticleCount() || "0".equals(articleDataModelsNew.get(position).getArticleCount())) {
            holder.viewCountTextView.setVisibility(View.GONE);
        } else {
            holder.viewCountTextView.setVisibility(View.VISIBLE);
            holder.viewCountTextView.setText(articleDataModelsNew.get(position).getArticleCount());
        }

        if (null == articleDataModelsNew.get(position).getCommentsCount() || "0".equals(articleDataModelsNew.get(position).getCommentsCount())) {
            holder.commentCountTextView.setVisibility(View.GONE);
        } else {
            holder.commentCountTextView.setVisibility(View.VISIBLE);
            holder.commentCountTextView.setText(articleDataModelsNew.get(position).getCommentsCount());
        }

        if (null == articleDataModelsNew.get(position).getLikesCount() || "0".equals(articleDataModelsNew.get(position).getLikesCount())) {
            holder.recommendCountTextView.setVisibility(View.GONE);
        } else {
            holder.recommendCountTextView.setVisibility(View.VISIBLE);
            holder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLikesCount());
        }

        if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())
                && (articleDataModelsNew.get(position).getImageUrl().getThumbMax() == null || articleDataModelsNew.get(position).getImageUrl().getThumbMax().endsWith("default.jpg"))) {
            Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURLMomspresso(articleDataModelsNew.get(position).getVideoUrl())).placeholder(R.drawable.default_article).into(holder.articleImageView);
        } else {
            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getThumbMax())) {
                Picasso.with(mContext).load(articleDataModelsNew.get(position).getImageUrl().getThumbMax())
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
            } else {
                holder.articleImageView.setBackgroundResource(R.drawable.default_article);
            }
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class UserRecommendationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView articleImageView;
        ImageView shareImageView;
        TextView txvArticleTitle;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;

        public UserRecommendationsViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            txvArticleTitle = (TextView) itemView.findViewById(R.id.articleTitleTextView);
            articleImageView = (ImageView) itemView.findViewById(R.id.articleImageView);
            viewCountTextView = (TextView) itemView.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) itemView.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) itemView.findViewById(R.id.recommendCountTextView);
            shareImageView = (ImageView) itemView.findViewById(R.id.shareImageView);
            shareImageView.setOnClickListener(this);
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