package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.SearchArticleResult;

import java.util.ArrayList;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchArticlesListingAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<SearchArticleResult> articleDataModelsNew;
    private final float density;

    public SearchArticlesListingAdapter(Context pContext) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
    }

    public void setListData(ArrayList<SearchArticleResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public int getCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    @Override
    public Object getItem(int position) {
        return articleDataModelsNew.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.search_topics_item_layout, null);
            holder = new ViewHolder();
            holder.tagTitleTextView = (TextView) view.findViewById(R.id.tagTitleTextView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tagTitleTextView.setText(Html.fromHtml(articleDataModelsNew.get(position).getTitle()));
        return view;
    }

    class ViewHolder {
        TextView tagTitleTextView;
    }

    public void refreshArticleList(ArrayList<SearchArticleResult> newList) {
        this.articleDataModelsNew = newList;
    }

}