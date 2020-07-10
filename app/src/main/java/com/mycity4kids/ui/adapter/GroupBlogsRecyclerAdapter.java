package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by hemant on 4/12/17.
 */

public class GroupBlogsRecyclerAdapter extends RecyclerView.Adapter<GroupBlogsRecyclerAdapter.FeedViewHolder> {

    private final Context context;
    private final LayoutInflater layoutInflater;
    private ArrayList<ArticleListingResult> articleDataList;
    private RecyclerViewClickListener recyclerViewClickListener;

    public GroupBlogsRecyclerAdapter(Context context, RecyclerViewClickListener listener) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recyclerViewClickListener = listener;
    }

    public void setData(ArrayList<ArticleListingResult> articleDataList) {
        this.articleDataList = articleDataList;
    }

    @Override
    public int getItemCount() {
        return articleDataList.size();
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = layoutInflater.inflate(R.layout.article_listing_item, parent, false);
        return new FeedViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder holder, final int position) {
        addArticleItem(holder, position);
    }

    private void addArticleItem(final GroupBlogsRecyclerAdapter.FeedViewHolder holder, final int position) {
        holder.txvArticleTitle.setText(articleDataList.get(position).getTitle());
        if (null == articleDataList.get(position).getArticleCount() || "0"
                .equals(articleDataList.get(position).getArticleCount())) {
            holder.viewCountTextView.setVisibility(View.GONE);
        } else {
            holder.viewCountTextView.setVisibility(View.VISIBLE);
            holder.viewCountTextView.setText(articleDataList.get(position).getArticleCount());
        }

        if (null == articleDataList.get(position).getCommentsCount() || "0"
                .equals(articleDataList.get(position).getCommentsCount())) {
            holder.commentCountTextView.setVisibility(View.GONE);
        } else {
            holder.commentCountTextView.setVisibility(View.VISIBLE);
            holder.commentCountTextView.setText(articleDataList.get(position).getCommentsCount());
        }

        if (null == articleDataList.get(position).getLikesCount() || "0"
                .equals(articleDataList.get(position).getLikesCount())) {
            holder.recommendCountTextView.setVisibility(View.GONE);
        } else {
            holder.recommendCountTextView.setVisibility(View.VISIBLE);
            holder.recommendCountTextView.setText(articleDataList.get(position).getLikesCount());
        }

        if (StringUtils.isNullOrEmpty(articleDataList.get(position).getUserName()) || articleDataList.get(position)
                .getUserName().toString().trim().equalsIgnoreCase("")) {
            holder.txvAuthorName.setText("NA");
        } else {
            holder.txvAuthorName.setText(articleDataList.get(position).getUserName());
        }

        try {
            if (!StringUtils.isNullOrEmpty(articleDataList.get(position).getVideoUrl())
                    && (articleDataList.get(position).getImageUrl().getThumbMax() == null || articleDataList
                    .get(position).getImageUrl().getThumbMax().contains("default.jp"))) {
                Picasso.get()
                        .load(AppUtils.getYoutubeThumbnailURLMomspresso(articleDataList.get(position).getVideoUrl()))
                        .placeholder(R.drawable.default_article).into(holder.articleImageView);
            } else {
                if (!StringUtils.isNullOrEmpty(articleDataList.get(position).getImageUrl().getThumbMax())) {
                    Picasso.get().load(articleDataList.get(position).getImageUrl().getThumbMax())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(holder.articleImageView);
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
                holder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_watch));
            } else {
                holder.watchLaterImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_watch_added));
            }
        } else {
            holder.bookmarkArticleImageView.setVisibility(View.INVISIBLE);
            holder.watchLaterImageView.setVisibility(View.INVISIBLE);

            if (articleDataList.get(position).getListingBookmarkStatus() == 0) {
                holder.bookmarkArticleImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bookmark));
            } else {
                holder.bookmarkArticleImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bookmarked));
            }
        }
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
            bookmarkArticleImageView = (ImageView) view.findViewById(R.id.bookmarkArticleImageView);
            watchLaterImageView = (ImageView) view.findViewById(R.id.watchLaterImageView);

            bookmarkArticleImageView.setVisibility(View.GONE);
            watchLaterImageView.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
            forYouInfoLL.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {

        void onRecyclerItemClick(View view, int position);
    }

}
