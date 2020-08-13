package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.mycity4kids.R;
import com.mycity4kids.models.response.SearchArticleResult;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchArticlesListingAdapter extends RecyclerView.Adapter<SearchArticlesListingAdapter.ArticleViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<SearchArticleResult> articleDataModelsNew;
    private final float density;
    private RecyclerViewClickListener mListener;

    public SearchArticlesListingAdapter(Context pContext, RecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    public void setListData(ArrayList<SearchArticleResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ArticleViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.search_all_article_item, parent, false);
        viewHolder = new ArticleViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        if (articleDataModelsNew.get(position).getItemType().equals("4")) {
            holder.card.setVisibility(View.VISIBLE);
            holder.articleCard.setVisibility(View.GONE);
            holder.collectionTitleTextView.setText(AppUtils.fromHtml(articleDataModelsNew.get(position).getName()));
            holder.collectionAuthorName.setText(AppUtils.fromHtml(articleDataModelsNew.get(position).getUserName()));
            try {
                Picasso.get().load(articleDataModelsNew.get(position).getImageUrl().getThumbMin()).
                        placeholder(R.drawable.default_article).error(R.drawable.default_article)
                        .into(holder.collectionImageView);
            } catch (Exception e) {
                holder.collectionImageView.setBackgroundResource(R.drawable.article_default);
            }
        } else {
            holder.card.setVisibility(View.GONE);
            holder.articleCard.setVisibility(View.VISIBLE);
            holder.titleTextView.setText(AppUtils.fromHtml(articleDataModelsNew.get(position).getTitle()));
            if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getBody())) {
                holder.bodyTextView.setText("");
            } else {
                holder.bodyTextView.setText(AppUtils.fromHtml(articleDataModelsNew.get(position).getBody()));
            }

            try {
                Picasso.get().load(articleDataModelsNew.get(position).getImageUrl().getThumbMin()).
                        placeholder(R.drawable.default_article).error(R.drawable.default_article)
                        .into(holder.articleImageView);
            } catch (Exception e) {
                holder.articleImageView.setBackgroundResource(R.drawable.article_default);
            }
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleTextView, collectionTitleTextView,collectionAuthorName;
        TextView bodyTextView;
        ImageView articleImageView, collectionImageView;
        CardView card;
        RelativeLayout articleCard;

        public ArticleViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.articleTitleTextView);
            bodyTextView = (TextView) itemView.findViewById(R.id.articleDescTextView);
            articleImageView = (ImageView) itemView.findViewById(R.id.articleImageView);
            collectionTitleTextView = itemView.findViewById(R.id.collectionTitleTextView);
            collectionAuthorName = itemView.findViewById(R.id.collectionAuthorName);
            collectionImageView = itemView.findViewById(R.id.collectionImageView);
            card = itemView.findViewById(R.id.card);
            articleCard = itemView.findViewById(R.id.article_card);
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
