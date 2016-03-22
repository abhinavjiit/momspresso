package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.newmodels.PublishedArticlesModel;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleModel;
import com.mycity4kids.ui.activity.BlogDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 19/3/16.
 */
public class PublishedArticlesListAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<PublishedArticlesModel.PublishedArticleData> mPublishedArticlesList;
    private LayoutInflater mInflator;
    private final float density;

    class ViewHolder {
        TextView txvArticleTitle;
        TextView txvViewCount;
        TextView txvPublishDate;
        ImageView imvAuthorThumb;
    }

    public PublishedArticlesListAdapter(Context context) {
        mContext = context;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        density = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.published_articles_item, null);
            holder = new ViewHolder();
            holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            holder.txvPublishDate = (TextView) view.findViewById(R.id.txvPublishDate);
            holder.imvAuthorThumb = (ImageView) view.findViewById(R.id.imvAuthorThumb);
            holder.txvViewCount = (TextView) view.findViewById(R.id.txvViewCount);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.txvArticleTitle.setText(mPublishedArticlesList.get(position).getTitle());
        holder.txvViewCount.setText(mPublishedArticlesList.get(position).getCurrent_views() + " Views");
        holder.txvPublishDate.setText(mPublishedArticlesList.get(position).getCreated());
        if (!StringUtils.isNullOrEmpty(mPublishedArticlesList.get(position).getThumbnail_image())) {
            Picasso.with(mContext).load(mPublishedArticlesList.get(position).getThumbnail_image())
                    .placeholder(R.drawable.article_default).resize((int) (101 * density), (int) (67 * density))
                    .centerCrop().into(holder.imvAuthorThumb);
        } else {
            holder.imvAuthorThumb.setBackgroundResource(R.drawable.article_default);
        }

        return view;
    }

    public void setNewListData(ArrayList<PublishedArticlesModel.PublishedArticleData> mPublishedArticlesList) {
        this.mPublishedArticlesList = mPublishedArticlesList;
    }

    @Override
    public int getCount() {
        return mPublishedArticlesList == null ? 0 : mPublishedArticlesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPublishedArticlesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
