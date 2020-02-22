package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.SearchVideoResult;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchVideosListingAdapter extends RecyclerView.Adapter<SearchVideosListingAdapter.ArticleViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<SearchVideoResult> articleDataModelsNew;
    private final float density;
    private RecyclerViewClickListener mListener;

    public SearchVideosListingAdapter(Context pContext, RecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    public void setListData(ArrayList<SearchVideoResult> mParentingLists) {
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
        holder.titleTextView.setText(AppUtils.fromHtml(articleDataModelsNew.get(position).getTitle()));
        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getTitle_slug())) {
            holder.bodyTextView.setText("");
        } else {
            holder.bodyTextView.setText(AppUtils.fromHtml(articleDataModelsNew.get(position).getTitle_slug()));
        }

        try {
            Picasso.get().load(articleDataModelsNew.get(position).getImageUrl()).
                    placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
        } catch (Exception e) {
            holder.articleImageView.setBackgroundResource(R.drawable.article_default);
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        TextView bodyTextView;
        ImageView articleImageView;

        public ArticleViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.articleTitleTextView);
            bodyTextView = (TextView) itemView.findViewById(R.id.articleDescTextView);
            articleImageView = (ImageView) itemView.findViewById(R.id.articleImageView);
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
