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
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author deepanker.chaudhary
 */
public class NewArticlesListingAdapter extends BaseAdapter {

    private ArrayList<ArticleListingResult> mArticleListData;
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    Boolean newChanges = false;

    private final float density;

    public NewArticlesListingAdapter(Context pContext, Boolean newChanges) {

        density = pContext.getResources().getDisplayMetrics().density;

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.newChanges = newChanges;
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
        mArticleListData = mParentingLists;
    }

    public void setNewListData(ArrayList<ArticleListingResult> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    public void addNewListData(ArrayList<ArticleListingResult> mParentingLists_new) {
        articleDataModelsNew.addAll(mParentingLists_new);
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
                view = mInflator.inflate(R.layout.list_item_articles, null);
                holder = new ViewHolder();
                holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                holder.txvPublishDate = (TextView) view.findViewById(R.id.txvPublishDate);
                holder.imvAuthorThumb = (ImageView) view.findViewById(R.id.imvAuthorThumb);
//            holder.authorPic = (ImageView) view.findViewById(R.id.author_pic);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (newChanges) {

                holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());

                if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getUserName()) || articleDataModelsNew.get(position).getUserName().toString().trim().equalsIgnoreCase("")) {
                    holder.txvAuthorName.setText("NA");
                } else {
                    holder.txvAuthorName.setText(articleDataModelsNew.get(position).getUserName());
                }

//            holder.txvAuthorName.setTextColor(Color.parseColor(articleDataModelsNew.get(position).getAuthor_color_code()));
                holder.txvPublishDate.setText(DateTimeUtils.getDateFromTimestamp(articleDataModelsNew.get(position).getCreatedTime()));
                if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getMobileWebThumbnail())) {
                    Picasso.with(mContext).load(articleDataModelsNew.get(position).getImageUrl().getMobileWebThumbnail()).placeholder(R.drawable.default_article).resize((int) (101 * density), (int) (67 * density)).centerCrop().into(holder.imvAuthorThumb);
                } else {
                    holder.imvAuthorThumb.setBackgroundResource(R.drawable.article_default);
                }

                holder.txvAuthorName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, BloggerDashboardActivity.class);
                        intent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, articleDataModelsNew.get(position).getUserId());
                        mContext.startActivity(intent);
                    }
                });
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

        return view;
    }

    class ViewHolder {
        TextView txvArticleTitle;
        TextView txvAuthorName;
        TextView txvPublishDate;
        ImageView imvAuthorThumb;
//        ImageView authorPic;
    }

    public void refreshArticleList(ArrayList<ArticleListingResult> newList) {
        this.articleDataModelsNew = newList;
    }

}
