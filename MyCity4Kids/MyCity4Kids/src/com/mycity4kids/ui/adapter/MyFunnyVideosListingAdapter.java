package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author deepanker.chaudhary
 */
public class MyFunnyVideosListingAdapter extends BaseAdapter {

    private final static String VIDEO_PUBLISHED_STATUS = "3";
    private ArrayList<VlogsListingAndDetailResult> mArticleListData;
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;

    private final float density;

    public MyFunnyVideosListingAdapter(Context pContext) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
    }

    public void setListData(ArrayList<VlogsListingAndDetailResult> mParentingLists) {
        mArticleListData = mParentingLists;
    }

    public void setNewListData(ArrayList<VlogsListingAndDetailResult> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
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

        try {
            final ViewHolder holder;
            if (view == null) {
                view = mInflator.inflate(R.layout.users_funny_video_item, null);
                holder = new ViewHolder();
                holder.txvArticleTitle = (TextView) view.findViewById(R.id.articleTitleTextView);
                holder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                holder.shareImageView = (ImageView) view.findViewById(R.id.shareImageView);
                holder.dateTextView = (TextView) view.findViewById(R.id.dateTextView);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getTitle())) {
                holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            } else {
                holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitleSlug());
            }

            if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getUrl())) {
                Picasso.with(mContext).load(R.drawable.default_article)
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
            } else {
                Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURL(articleDataModelsNew.get(position).getUrl()))
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
            }

            holder.dateTextView.setText(mContext.getString(R.string.user_funny_video_published_on, DateTimeUtils.getDateFromTimestamp(Long.parseLong(articleDataModelsNew.get(position).getPublished_time()))));

            if (VIDEO_PUBLISHED_STATUS.equals(articleDataModelsNew.get(position).getPublication_status())) {
                holder.shareImageView.setVisibility(View.VISIBLE);
            } else {
                holder.shareImageView.setVisibility(View.GONE);
            }

            holder.shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareUrl = articleDataModelsNew.get(position).getUrl();
                    String shareMessage;
                    if (StringUtils.isNullOrEmpty(shareUrl)) {
                        shareMessage = "Momspresso\n\nCheck out this interesting blog post " + "\"" +
                                articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getAuthor().getFirstName() + ".";
                    } else {
                        shareMessage = "Momspresso\n\nCheck out this interesting blog post " + "\"" +
                                articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getAuthor().getFirstName() + ".\nRead Here: " + shareUrl;
                    }
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    mContext.startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                }
            });
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

        return view;
    }

    class ViewHolder {
        TextView txvArticleTitle;
        ImageView articleImageView;
        TextView dateTextView;
        ImageView shareImageView;
    }

}
