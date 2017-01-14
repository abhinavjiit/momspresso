package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * @author deepanker.chaudhary
 */
public class MyFunnyVideosListingAdapter extends BaseAdapter {

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
                view = mInflator.inflate(R.layout.vlogs_listing_item, null);
                holder = new ViewHolder();
                holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                holder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                holder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
                holder.forYouDescriptionTextView = (TextView) view.findViewById(R.id.forYouDescriptionTextView);

                holder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                holder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                holder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);

                holder.authorTypeTextView = (TextView) view.findViewById(R.id.authorTypeTextView);
                holder.rankTextView = (TextView) view.findViewById(R.id.rankTextView);

                holder.flowLayout = (FlowLayout) view.findViewById(R.id.flowLayout);
                holder.popularSubCatTextView1 = (TextView) view.findViewById(R.id.popularSubcatTextView_1);
                holder.popularSubCatTextView2 = (TextView) view.findViewById(R.id.popularSubcatTextView_2);
                holder.popularSubCatTextView3 = (TextView) view.findViewById(R.id.popularSubcatTextView_3);
                holder.popularSubCatTextView4 = (TextView) view.findViewById(R.id.popularSubcatTextView_4);

                holder.tvParentLL1 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_1);
                holder.tvParentLL2 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_2);
                holder.tvParentLL3 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_3);
                holder.tvParentLL4 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_4);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

//            ArrayList<Map<String, String>> topicsList = articleDataModelsNew.get(position).getTags();
//            if (topicsList.size() > 3) {
//                holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
//                holder.popularSubCatTextView1.setText((String) topicsList.get(0).values().toArray()[0]);
//                holder.popularSubCatTextView2.setVisibility(View.VISIBLE);
//                holder.popularSubCatTextView2.setText((String) topicsList.get(1).values().toArray()[0]);
//                holder.popularSubCatTextView3.setVisibility(View.VISIBLE);
//                holder.popularSubCatTextView3.setText((String) topicsList.get(2).values().toArray()[0]);
//                holder.popularSubCatTextView4.setVisibility(View.VISIBLE);
//                holder.popularSubCatTextView4.setText((String) topicsList.get(3).values().toArray()[0]);
//            } else if (topicsList.size() > 2) {
//                holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
//                holder.popularSubCatTextView1.setText((String) topicsList.get(0).values().toArray()[0]);
//                holder.popularSubCatTextView2.setVisibility(View.VISIBLE);
//                holder.popularSubCatTextView2.setText((String) topicsList.get(1).values().toArray()[0]);
//                holder.popularSubCatTextView3.setVisibility(View.VISIBLE);
//                holder.popularSubCatTextView3.setText((String) topicsList.get(2).values().toArray()[0]);
//                holder.tvParentLL4.setVisibility(View.GONE);
//            } else if (topicsList.size() > 1) {
//                holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
//                holder.popularSubCatTextView1.setText((String) topicsList.get(0).values().toArray()[0]);
//                holder.popularSubCatTextView2.setVisibility(View.VISIBLE);
//                holder.popularSubCatTextView2.setText((String) topicsList.get(1).values().toArray()[0]);
//                holder.tvParentLL3.setVisibility(View.GONE);
//                holder.tvParentLL4.setVisibility(View.GONE);
//            } else if (topicsList.size() > 0) {
//                holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
//                holder.popularSubCatTextView1.setText((String) topicsList.get(0).values().toArray()[0]);
//                holder.tvParentLL2.setVisibility(View.GONE);
//                holder.tvParentLL3.setVisibility(View.GONE);
//                holder.tvParentLL4.setVisibility(View.GONE);
//            } else {
//                holder.tvParentLL1.setVisibility(View.GONE);
//                holder.tvParentLL2.setVisibility(View.GONE);
//                holder.tvParentLL3.setVisibility(View.GONE);
//                holder.tvParentLL4.setVisibility(View.GONE);
//            }

            holder.tvParentLL1.setVisibility(View.GONE);
            holder.tvParentLL2.setVisibility(View.GONE);
            holder.tvParentLL3.setVisibility(View.GONE);
            holder.tvParentLL4.setVisibility(View.GONE);
            if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getTitle())) {
                holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            } else {
                holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitleSlug());
            }

//            if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getReason())) {
//                holder.forYouDescriptionTextView.setVisibility(View.GONE);
//            } else {
//                holder.forYouDescriptionTextView.setVisibility(View.VISIBLE);
//                holder.forYouDescriptionTextView.setText(Html.fromHtml(articleDataModelsNew.get(position).getReason()));
//            }

//            if (null == articleDataModelsNew.get(position).getArticleCount() || "0".equals(articleDataModelsNew.get(position).getArticleCount())) {
//                holder.viewCountTextView.setVisibility(View.GONE);
//            } else {
//                holder.viewCountTextView.setVisibility(View.VISIBLE);
//                holder.viewCountTextView.setText(articleDataModelsNew.get(position).getArticleCount());
//            }
//
//            if (null == articleDataModelsNew.get(position).getCommentsCount() || "0".equals(articleDataModelsNew.get(position).getCommentsCount())) {
//                holder.commentCountTextView.setVisibility(View.GONE);
//            } else {
//                holder.commentCountTextView.setVisibility(View.VISIBLE);
//                holder.commentCountTextView.setText(articleDataModelsNew.get(position).getCommentsCount());
//            }
//
//            if (null == articleDataModelsNew.get(position).getLikesCount() || "0".equals(articleDataModelsNew.get(position).getLikesCount())) {
//                holder.recommendCountTextView.setVisibility(View.GONE);
//            } else {
//                holder.recommendCountTextView.setVisibility(View.VISIBLE);
//                holder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLikesCount());
//            }
            holder.viewCountTextView.setVisibility(View.GONE);
            holder.commentCountTextView.setVisibility(View.GONE);
            holder.recommendCountTextView.setVisibility(View.GONE);

//            if (null == articleDataModelsNew.get(position).getTrendingCount()) {
//                holder.rankTextView.setVisibility(View.GONE);
//            } else {
//                holder.rankTextView.setVisibility(View.VISIBLE);
//                holder.rankTextView.setText(articleDataModelsNew.get(position).getTrendingCount());
//            }
            holder.rankTextView.setVisibility(View.GONE);
            switch (articleDataModelsNew.get(position).getPublication_status()) {
                case AppConstants.VIDEO_STATUS_DRAFT: {
                    holder.authorTypeTextView.setText(AppConstants.VIDEO_TYPE_DRAFT.toUpperCase());
                }
                case AppConstants.VIDEO_STATUS_APPROVAL_PENDING: {
                    holder.authorTypeTextView.setText(AppConstants.VIDEO_STATUS_APPROVAL_PENDING.toUpperCase());
                }
                case AppConstants.VIDEO_STATUS_APPROVAL_CANCELLED: {
                    holder.authorTypeTextView.setText(AppConstants.VIDEO_TYPE_APPROVAL_CANCELLED.toUpperCase());
                }
                case AppConstants.VIDEO_STATUS_PUBLISHED: {
                    holder.authorTypeTextView.setText(AppConstants.VIDEO_STATUS_PUBLISHED.toUpperCase());
                }
                case AppConstants.VIDEO_STATUS_UNPUBLISHED: {
                    holder.authorTypeTextView.setText(AppConstants.VIDEO_TYPE_UNPUBLISHED.toUpperCase());
                }
            }
            holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.blue_color));
            holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
            holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_blogger));

            String userName = articleDataModelsNew.get(position).getAuthor().getFirstName() + " " + articleDataModelsNew.get(position).getAuthor().getLastName();
            if (StringUtils.isNullOrEmpty(userName) || userName.trim().equalsIgnoreCase("")) {
                holder.txvAuthorName.setText("NA");
            } else {
                holder.txvAuthorName.setText(userName);
            }
            if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getUrl())) {
                Picasso.with(mContext).load(R.drawable.default_article)
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);

            } else {
                Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURL(articleDataModelsNew.get(position).getUrl()))
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
            }

            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getAuthor().getProfilePic().getClientAppMin())) {
                Picasso.with(mContext).load(articleDataModelsNew.get(position).getAuthor().getProfilePic().getClientAppMin())
                        .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(holder.authorImageView);
            } else {
                holder.authorImageView.setBackgroundResource(R.drawable.default_commentor_img);
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
        ImageView articleImageView;
        ImageView authorImageView;
        TextView forYouDescriptionTextView;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        TextView authorTypeTextView;
        TextView rankTextView;
        FlowLayout flowLayout;
        TextView popularSubCatTextView1;
        TextView popularSubCatTextView2;
        TextView popularSubCatTextView3;
        TextView popularSubCatTextView4;
        LinearLayout tvParentLL1;
        LinearLayout tvParentLL2;
        LinearLayout tvParentLL3;
        LinearLayout tvParentLL4;
    }

}
