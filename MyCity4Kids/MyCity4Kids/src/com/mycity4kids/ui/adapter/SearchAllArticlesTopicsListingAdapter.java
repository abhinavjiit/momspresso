package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.ui.fragment.SearchAllArticlesAndTopicsTabFragment;
import com.mycity4kids.utils.AppUtils;

import java.util.ArrayList;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchAllArticlesTopicsListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<SearchAllArticlesAndTopicsTabFragment.SearchArticleTopicResult> articleDataModelsNew;
    private final float density;
    private RecyclerViewClickListener mListener;

    public SearchAllArticlesTopicsListingAdapter(Context pContext, RecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        mListener = listener;
    }

    public void setListData(ArrayList<SearchAllArticlesAndTopicsTabFragment.SearchArticleTopicResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public int getItemViewType(int position) {

        switch (articleDataModelsNew.get(position).getListType()) {
            case AppConstants.SEARCH_ITEM_TYPE_ARTICLE_HEADER:
            case AppConstants.SEARCH_ITEM_TYPE_TOPIC_HEADER:
                return 0;
            case AppConstants.SEARCH_ITEM_TYPE_ARTICLE_SHOW_MORE:
            case AppConstants.SEARCH_ITEM_TYPE_TOPIC_SHOW_MORE:
                return 1;
            case AppConstants.SEARCH_ITEM_TYPE_ARTICLE:
                return 2;
            case AppConstants.SEARCH_ITEM_TYPE_TOPIC:
                return 3;

        }
        return 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case 0:
                View v0 = mInflator.inflate(R.layout.search_header_label_item, parent, false);

                viewHolder = new HeaderViewHolder(v0, mListener);
                break;
            case 1:
                View v1 = mInflator.inflate(R.layout.search_show_more_item, parent, false);
                viewHolder = new ShowMoreViewHolder(v1, mListener);
                break;
            case 2:
                View v2 = mInflator.inflate(R.layout.search_all_article_item, parent, false);
                viewHolder = new SearchArticleViewHolder(v2, mListener);
                break;
            case 3:
                View v3 = mInflator.inflate(R.layout.search_all_topic_item, parent, false);
                viewHolder = new SearchTopicViewHolder(v3, mListener);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        SearchAllArticlesAndTopicsTabFragment.SearchArticleTopicResult obj = articleDataModelsNew.get(position);
//        holder.textView.setText(obj.getTitle());
        switch (getItemViewType(position)) {
            case 0:
                HeaderViewHolder vh1 = (HeaderViewHolder) holder;
                configureHeaderViewHolder(vh1, position);
                break;
            case 1:
                ShowMoreViewHolder vh2 = (ShowMoreViewHolder) holder;
                configureShowMoreViewHolder(vh2, position);
                break;
            case 2:
                SearchArticleViewHolder vh3 = (SearchArticleViewHolder) holder;
                configureSearchArticleViewHolder(vh3, position);
                break;
            case 3:
                SearchTopicViewHolder vh4 = (SearchTopicViewHolder) holder;
                configureSearchTopicViewHolder(vh4, position);
                break;
        }
    }

    private void configureHeaderViewHolder(HeaderViewHolder vh1, int position) {
//        vh1.textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
        vh1.headerLabelTextView.setText(articleDataModelsNew.get(position).getTitle());
    }

    private void configureShowMoreViewHolder(ShowMoreViewHolder vh2, int position) {
//        vh2.textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_bg));
        vh2.showMoreTextView.setText(articleDataModelsNew.get(position).getTitle());
    }

    private void configureSearchArticleViewHolder(SearchArticleViewHolder vh3, int position) {
//        vh3.textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.home_green));
        vh3.articleTitleTextView.setText(AppUtils.fromHtml(articleDataModelsNew.get(position).getTitle()));
        vh3.articleDescTextView.setText(AppUtils.fromHtml(articleDataModelsNew.get(position).getBody()));
    }

    private void configureSearchTopicViewHolder(SearchTopicViewHolder vh4, int position) {
//        vh4.textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_grey));
        vh4.searchTopicTextView.setText(AppUtils.fromHtml(articleDataModelsNew.get(position).getTitle()));
    }


    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

//    public void refreshArticleList(ArrayList<SearchArticleResult> newList) {
//        this.articleDataModelsNew = newList;
//    }

    public class SearchArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView articleTitleTextView;
        private ImageView articleImageView;
        private TextView articleDescTextView;

        public SearchArticleViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            articleTitleTextView = (TextView) itemView.findViewById(R.id.articleTitleTextView);
            articleDescTextView = (TextView) itemView.findViewById(R.id.articleDescTextView);
            articleImageView = (ImageView) itemView.findViewById(R.id.articleImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public class SearchTopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView searchTopicTextView;

        public SearchTopicViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            searchTopicTextView = (TextView) itemView.findViewById(R.id.searchTopicTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView headerLabelTextView;

        public HeaderViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            headerLabelTextView = (TextView) itemView.findViewById(R.id.headerLabelTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public class ShowMoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView showMoreTextView;

        public ShowMoreViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            showMoreTextView = (TextView) itemView.findViewById(R.id.showMoreTextView);
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