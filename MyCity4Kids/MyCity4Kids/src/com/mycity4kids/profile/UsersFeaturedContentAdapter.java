package com.mycity4kids.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/17.
 */
public class UsersFeaturedContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CONTENT_TYPE_SHORT_STORY = 0;
    private static final int CONTENT_TYPE_ARTICLE = 1;
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<FeaturedItem> featuredItemsList;
    private RecyclerViewClickListener mListener;

    public UsersFeaturedContentAdapter(RecyclerViewClickListener listener) {
        this.mListener = listener;
    }

    public void setListData(ArrayList<FeaturedItem> featuredItemsList) {
        featuredItemsList = featuredItemsList;
    }

    @Override
    public int getItemViewType(int position) {
        if ("1".equals(featuredItemsList.get(position).getItemType())) {
            return CONTENT_TYPE_SHORT_STORY;
        } else {
            CONTENT_TYPE_ARTICLE
            return CONTENT_TYPE_ARTICLE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CONTENT_TYPE_ARTICLE) {
            UserFeaturedContentViewHolder viewHolder = null;
            View v0 = mInflator.inflate(R.layout.featured_item, parent, false);
            viewHolder = new UserFeaturedContentViewHolder(v0, mListener);
            return viewHolder;
        } else {
            UserRecommendedSSViewHolder viewHolder = null;
            View v0 = mInflator.inflate(R.layout.users_activity_short_stories_item, parent, false);
            viewHolder = new UserRecommendedSSViewHolder(v0, mListener);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserFeaturedContentViewHolder) {
            UserFeaturedContentViewHolder articleViewHolder = (UserFeaturedContentViewHolder) holder;
            articleViewHolder.txvArticleTitle.setText(featuredItemsList.get(position).getTitle());

            if (StringUtils.isNullOrEmpty(featuredItemsList.get(position).getArticleCount()) || "0".equals(featuredItemsList.get(position).getArticleCount())) {
                articleViewHolder.viewCountTextView.setVisibility(View.GONE);
            } else {
                articleViewHolder.viewCountTextView.setVisibility(View.VISIBLE);
                articleViewHolder.viewCountTextView.setText(featuredItemsList.get(position).getArticleCount());
            }

            articleViewHolder.commentCountTextView.setText(featuredItemsList.get(position).getCommentCount());
            if (StringUtils.isNullOrEmpty(featuredItemsList.get(position).getCommentCount()) || "0".equals(featuredItemsList.get(position).getCommentCount())) {
                articleViewHolder.commentCountTextView.setVisibility(View.GONE);
                articleViewHolder.separatorView1.setVisibility(View.GONE);
            } else {
                articleViewHolder.commentCountTextView.setVisibility(View.VISIBLE);
                articleViewHolder.separatorView1.setVisibility(View.VISIBLE);
            }

            articleViewHolder.recommendCountTextView.setText(featuredItemsList.get(position).getLikesCount());
            if (StringUtils.isNullOrEmpty(featuredItemsList.get(position).getLikesCount()) || "0".equals(featuredItemsList.get(position).getLikesCount())) {
                articleViewHolder.recommendCountTextView.setVisibility(View.GONE);
                articleViewHolder.separatorView2.setVisibility(View.GONE);
            } else {
                articleViewHolder.recommendCountTextView.setVisibility(View.VISIBLE);
                articleViewHolder.separatorView2.setVisibility(View.VISIBLE);
            }

            try {
                if (!StringUtils.isNullOrEmpty(featuredItemsList.get(position).getVideoUrl())
                        && (featuredItemsList.get(position).getImageUrl().getThumbMax() == null || featuredItemsList.get(position).getImageUrl().getThumbMax().endsWith("default.jpg"))) {
                    Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURLMomspresso(featuredItemsList.get(position).getVideoUrl())).placeholder(R.drawable.default_article).into(articleViewHolder.articleImageView);
                } else {
                    if (!StringUtils.isNullOrEmpty(featuredItemsList.get(position).getImageUrl().getThumbMax())) {
                        Picasso.with(mContext).load(featuredItemsList.get(position).getImageUrl().getThumbMax())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(articleViewHolder.articleImageView);
                    } else {
                        articleViewHolder.articleImageView.setBackgroundResource(R.drawable.default_article);
                    }
                }
            } catch (Exception e) {
                articleViewHolder.articleImageView.setBackgroundResource(R.drawable.default_article);
            }
        } else {
            UserRecommendedSSViewHolder shortStoryViewHolder = (UserRecommendedSSViewHolder) holder;
            shortStoryViewHolder.txvArticleTitle.setText(featuredItemsList.get(position).getTitle());

            shortStoryViewHolder.commentCountTextView.setText(featuredItemsList.get(position).getCommentCount());
            if (StringUtils.isNullOrEmpty(featuredItemsList.get(position).getCommentCount()) || "0".equals(featuredItemsList.get(position).getCommentCount())) {
                shortStoryViewHolder.commentCountTextView.setVisibility(View.GONE);
            } else {
                shortStoryViewHolder.commentCountTextView.setVisibility(View.VISIBLE);
            }
            shortStoryViewHolder.recommendCountTextView.setText(featuredItemsList.get(position).getLikesCount());
            if (StringUtils.isNullOrEmpty(featuredItemsList.get(position).getLikesCount()) || "0".equals(featuredItemsList.get(position).getLikesCount())) {
                shortStoryViewHolder.recommendCountTextView.setVisibility(View.GONE);
                shortStoryViewHolder.separatorView2.setVisibility(View.GONE);
            } else {
                shortStoryViewHolder.recommendCountTextView.setVisibility(View.VISIBLE);
                shortStoryViewHolder.separatorView2.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return featuredItemsList == null ? 0 : featuredItemsList.size();
    }

    public class UserRecommendationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView articleImageView;
        ImageView shareImageView;
        TextView txvArticleTitle;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        View separatorView1;
        View separatorView2;

        UserRecommendationsViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            txvArticleTitle = (TextView) itemView.findViewById(R.id.articleTitleTextView);
            articleImageView = (ImageView) itemView.findViewById(R.id.articleImageView);
            viewCountTextView = (TextView) itemView.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) itemView.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) itemView.findViewById(R.id.recommendCountTextView);
            shareImageView = (ImageView) itemView.findViewById(R.id.shareImageView);
            separatorView1 = itemView.findViewById(R.id.separatorView1);
            separatorView2 = itemView.findViewById(R.id.separatorView2);
            shareImageView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public class UserRecommendedSSViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txvArticleTitle;
        TextView txvPublishDate;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        ImageView shareArticleImageView;
        View separatorView2;

        UserRecommendedSSViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            txvArticleTitle = (TextView) itemView.findViewById(R.id.articleTitleTextView);
            txvPublishDate = (TextView) itemView.findViewById(R.id.txvPublishDate);
            commentCountTextView = (TextView) itemView.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) itemView.findViewById(R.id.recommendCountTextView);
            shareArticleImageView = (ImageView) itemView.findViewById(R.id.shareImageView);
            separatorView2 = itemView.findViewById(R.id.separatorView2);
            shareArticleImageView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onFeaturedItemClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onFeaturedItemClick(View view, int position);
    }

}