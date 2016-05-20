package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by manish.soni on 27-07-2015.
 */
public class BloggerListingAdapter extends BaseAdapter {

    private final float density;
    private Context mContext;
    ArrayList<BlogArticleModel> articleDataModelsNew;

    public BloggerListingAdapter(Context pContext, ArrayList<BlogArticleModel> mParentingLists_new) {
        mContext = pContext;
        articleDataModelsNew = mParentingLists_new;
        density = mContext.getResources().getDisplayMetrics().density;
    }

    public void setNewListData(ArrayList<BlogArticleModel> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    public ArrayList<BlogArticleModel> getArticleList() {
        return articleDataModelsNew;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(R.layout.new_blog_list_item, null);
            holder = new ViewHolder();
            holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            holder.totalViews = (TextView) view.findViewById(R.id.total_views);
            holder.txvPublishDate = (TextView) view.findViewById(R.id.txvPublishDate);
            holder.imvAuthorThumb = (ImageView) view.findViewById(R.id.imvAuthorThumb);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
        holder.totalViews.setText(articleDataModelsNew.get(position).getCurrent_views() + " Views");

        holder.txvPublishDate.setText(articleDataModelsNew.get(position).getCreated());
        if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getThumbnail_image())) {
            Picasso.with(mContext).load(articleDataModelsNew.get(position).getThumbnail_image())
                    .resize((int) (100 * density), (int) (66 * density)).centerCrop().placeholder(R.drawable.default_article)
                    .into(holder.imvAuthorThumb);
        } else {
            Picasso.with(mContext).load(R.drawable.article_default).resize((int) (100 * density), (int) (66 * density))
                    .centerCrop().into(holder.imvAuthorThumb);
        }
        return view;
    }

    class ViewHolder {
        TextView txvArticleTitle;
        TextView totalViews;
        TextView txvPublishDate;
        ImageView imvAuthorThumb;
    }

}
