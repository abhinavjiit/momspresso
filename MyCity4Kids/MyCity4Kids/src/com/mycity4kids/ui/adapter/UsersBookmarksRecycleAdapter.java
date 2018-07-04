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
public class UsersBookmarksRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CONTENT_TYPE_SHORT_STORY = 0;
    private static final int CONTENT_TYPE_ARTICLE = 1;
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    private final float density;
    private RecyclerViewClickListener mListener;

    public UsersBookmarksRecycleAdapter(Context pContext, RecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public int getItemViewType(int position) {
        if ("1".equals(articleDataModelsNew.get(position).getContentType())) {
            return CONTENT_TYPE_SHORT_STORY;
        } else {
            return CONTENT_TYPE_ARTICLE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CONTENT_TYPE_ARTICLE) {
            UserBookmarksViewHolder viewHolder = null;
            View v0 = mInflator.inflate(R.layout.users_bookmark_recycle_item, parent, false);
            viewHolder = new UserBookmarksViewHolder(v0, mListener);
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
        if (holder instanceof UserBookmarksViewHolder) {
            UserBookmarksViewHolder articleViewHolder = (UserBookmarksViewHolder) holder;
            articleViewHolder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            articleViewHolder.authorTextView.setText(mContext.getString(R.string.user_activities_bookmarks_by) + " " + articleDataModelsNew.get(position).getUserName());

            if (null == articleDataModelsNew.get(position).getArticleCount() || "0".equals(articleDataModelsNew.get(position).getArticleCount())) {
                articleViewHolder.viewCountTextView.setVisibility(View.GONE);
            } else {
                articleViewHolder.viewCountTextView.setVisibility(View.VISIBLE);
                articleViewHolder.viewCountTextView.setText(articleDataModelsNew.get(position).getArticleCount());
            }

            if (null == articleDataModelsNew.get(position).getCommentCount() || "0".equals(articleDataModelsNew.get(position).getCommentCount())) {
                articleViewHolder.commentCountTextView.setVisibility(View.GONE);
            } else {
                articleViewHolder.commentCountTextView.setVisibility(View.VISIBLE);
                articleViewHolder.commentCountTextView.setText(articleDataModelsNew.get(position).getCommentCount());
            }

            if (null == articleDataModelsNew.get(position).getLikesCount() || "0".equals(articleDataModelsNew.get(position).getLikesCount())) {
                articleViewHolder.recommendCountTextView.setVisibility(View.GONE);
            } else {
                articleViewHolder.recommendCountTextView.setVisibility(View.VISIBLE);
                articleViewHolder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLikesCount());
            }
            try {
                if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())
                        && (articleDataModelsNew.get(position).getImageUrl().getThumbMax() == null || articleDataModelsNew.get(position).getImageUrl().getThumbMax().endsWith("default.jpg"))) {
                    Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURLMomspresso(articleDataModelsNew.get(position).getVideoUrl())).placeholder(R.drawable.default_article).into(articleViewHolder.articleImageView);
                } else {
                    if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getThumbMax())) {
                        Picasso.with(mContext).load(articleDataModelsNew.get(position).getImageUrl().getThumbMax())
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
            shortStoryViewHolder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());

            shortStoryViewHolder.commentCountTextView.setText(articleDataModelsNew.get(position).getCommentsCount());
            if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getCommentsCount()) || "0".equals(articleDataModelsNew.get(position).getCommentsCount())) {
                shortStoryViewHolder.commentCountTextView.setVisibility(View.GONE);
            } else {
                shortStoryViewHolder.commentCountTextView.setVisibility(View.VISIBLE);
            }
            shortStoryViewHolder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLikesCount());
            if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getLikesCount()) || "0".equals(articleDataModelsNew.get(position).getLikesCount())) {
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
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class UserBookmarksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView authorTextView;
        ImageView articleImageView;
        TextView txvArticleTitle;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        TextView removeBookmarkTextView;
        ImageView shareImageView;

        public UserBookmarksViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            txvArticleTitle = (TextView) itemView.findViewById(R.id.articleTitleTextView);
            articleImageView = (ImageView) itemView.findViewById(R.id.articleImageView);
            authorTextView = (TextView) itemView.findViewById(R.id.authorTextView);
            viewCountTextView = (TextView) itemView.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) itemView.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) itemView.findViewById(R.id.recommendCountTextView);
            removeBookmarkTextView = (TextView) itemView.findViewById(R.id.removeBookmarkTextView);
            shareImageView = (ImageView) itemView.findViewById(R.id.shareImageView);
            shareImageView.setOnClickListener(this);
            removeBookmarkTextView.setOnClickListener(this);
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

        public UserRecommendedSSViewHolder(View itemView, RecyclerViewClickListener listener) {
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
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

}