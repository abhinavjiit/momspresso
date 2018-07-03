package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticleListingActivity;
import com.mycity4kids.ui.fragment.ForYouInfoDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 4/12/17.
 */

public class GroupBlogsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0;
    public static final int ARTICLE = 1;
    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<ArticleListingResult> articleDataList;
    private RecyclerViewClickListener mListener;
    private int selectedPosition;

    public GroupBlogsRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }

    public void setData(ArrayList<ArticleListingResult> articleDataList) {
        this.articleDataList = articleDataList;
    }

    @Override
    public int getItemCount() {
        return articleDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return ARTICLE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View v0 = mInflator.inflate(R.layout.groups_blog_header_item, parent, false);
            return new HeaderViewHolder(v0);
        } else {
            View v0 = mInflator.inflate(R.layout.article_listing_item, parent, false);
            return new FeedViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
        } else {
            addArticleItem((GroupBlogsRecyclerAdapter.FeedViewHolder) holder, position);
        }
    }

    private void addArticleItem(final GroupBlogsRecyclerAdapter.FeedViewHolder holder, final int position) {
        holder.txvArticleTitle.setText(articleDataList.get(position).getTitle());

        if (StringUtils.isNullOrEmpty(articleDataList.get(position).getReason())) {
            holder.forYouInfoLL.setVisibility(View.GONE);
        } else {
            holder.forYouInfoLL.setVisibility(View.VISIBLE);
            holder.forYouInfoLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("For You", "for you article -- " + articleDataList.get(position).getTitle());
                    ForYouInfoDialogFragment forYouInfoDialogFragment = new ForYouInfoDialogFragment();
                    FragmentManager fm = ((ArticleListingActivity) mContext).getSupportFragmentManager();
                    Bundle _args = new Bundle();
                    _args.putString("reason", articleDataList.get(position).getReason());
                    _args.putString("articleId", articleDataList.get(position).getId());
                    _args.putInt("position", position);
                    forYouInfoDialogFragment.setArguments(_args);
                    forYouInfoDialogFragment.setCancelable(true);
                    forYouInfoDialogFragment.setListener((ForYouInfoDialogFragment.IForYourArticleRemove) mContext);
                    forYouInfoDialogFragment.show(fm, "For You");
                }
            });
        }

        if (null == articleDataList.get(position).getArticleCount() || "0".equals(articleDataList.get(position).getArticleCount())) {
            holder.viewCountTextView.setVisibility(View.GONE);
        } else {
            holder.viewCountTextView.setVisibility(View.VISIBLE);
            holder.viewCountTextView.setText(articleDataList.get(position).getArticleCount());
        }

        if (null == articleDataList.get(position).getCommentsCount() || "0".equals(articleDataList.get(position).getCommentsCount())) {
            holder.commentCountTextView.setVisibility(View.GONE);
        } else {
            holder.commentCountTextView.setVisibility(View.VISIBLE);
            holder.commentCountTextView.setText(articleDataList.get(position).getCommentsCount());
        }

        if (null == articleDataList.get(position).getLikesCount() || "0".equals(articleDataList.get(position).getLikesCount())) {
            holder.recommendCountTextView.setVisibility(View.GONE);
        } else {
            holder.recommendCountTextView.setVisibility(View.VISIBLE);
            holder.recommendCountTextView.setText(articleDataList.get(position).getLikesCount());
        }

        if (StringUtils.isNullOrEmpty(articleDataList.get(position).getUserName()) || articleDataList.get(position).getUserName().toString().trim().equalsIgnoreCase("")) {
            holder.txvAuthorName.setText("NA");
        } else {
            holder.txvAuthorName.setText(articleDataList.get(position).getUserName());
        }

        try {
            if (!StringUtils.isNullOrEmpty(articleDataList.get(position).getVideoUrl())
                    && (articleDataList.get(position).getImageUrl().getThumbMax() == null || articleDataList.get(position).getImageUrl().getThumbMax().contains("default.jp"))) {
                Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURLMomspresso(articleDataList.get(position).getVideoUrl())).placeholder(R.drawable.default_article).into(holder.articleImageView);
            } else {
                if (!StringUtils.isNullOrEmpty(articleDataList.get(position).getImageUrl().getThumbMax())) {
                    Picasso.with(mContext).load(articleDataList.get(position).getImageUrl().getThumbMax())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
                } else {
                    holder.articleImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
        } catch (Exception e) {
            holder.articleImageView.setBackgroundResource(R.drawable.default_article);
        }


        if (!StringUtils.isNullOrEmpty(articleDataList.get(position).getVideoUrl())) {
            holder.videoIndicatorImageView.setVisibility(View.VISIBLE);
        } else {
            holder.videoIndicatorImageView.setVisibility(View.INVISIBLE);
        }

        if ("1".equals(articleDataList.get(position).getIsMomspresso())) {
            holder.bookmarkArticleImageView.setVisibility(View.INVISIBLE);
            holder.watchLaterImageView.setVisibility(View.VISIBLE);

            if (articleDataList.get(position).getListingWatchLaterStatus() == 0) {
                holder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
            } else {
                holder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
            }
        } else {
            holder.bookmarkArticleImageView.setVisibility(View.VISIBLE);
            holder.watchLaterImageView.setVisibility(View.INVISIBLE);

            if (articleDataList.get(position).getListingBookmarkStatus() == 0) {
                holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
            } else {
                holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
            }
        }

        holder.watchLaterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addRemoveWatchLater(position, holder);
                Utils.pushWatchLaterArticleEvent(mContext, "ArticleListing", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId() + "",
                        articleDataList.get(position).getId(), articleDataList.get(position).getUserId() + "~" + articleDataList.get(position).getUserName());
            }
        });

        holder.bookmarkArticleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addRemoveBookmark(position, holder);
                Utils.pushBookmarkArticleEvent(mContext, "ArticleListing", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId() + "",
                        articleDataList.get(position).getId(), articleDataList.get(position).getUserId() + "~" + articleDataList.get(position).getUserName());
            }
        });
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView videoIndicatorImageView;
        LinearLayout forYouInfoLL;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        TextView authorTypeTextView;
        ImageView bookmarkArticleImageView;
        ImageView watchLaterImageView;

        FeedViewHolder(View view) {
            super(view);
            txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
            articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
            videoIndicatorImageView = (ImageView) view.findViewById(R.id.videoIndicatorImageView);
            forYouInfoLL = (LinearLayout) view.findViewById(R.id.forYouInfoLL);
            viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
            authorTypeTextView = (TextView) view.findViewById(R.id.authorTypeTextView);
            bookmarkArticleImageView = (ImageView) view.findViewById(R.id.bookmarkArticleImageView);
            watchLaterImageView = (ImageView) view.findViewById(R.id.watchLaterImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView groupDescTextView;
        TextView groupAdminTextView;

        HeaderViewHolder(View view) {
            super(view);
//            groupDescTextView = (TextView) view.findViewById(R.id.groupDescTextView);
//            groupAdminTextView = (TextView) view.findViewById(R.id.groupAdminTextView);
//            groupDescTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("GpSumPstRecyclerAdapter", "groupDescTextView");
//                }
//            });
//            groupAdminTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("GpSumPstRecyclerAdapter", "groupAdminTextView");
//                }
//            });
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);
    }

}