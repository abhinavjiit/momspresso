package com.mycity4kids.ui.adapter;

import android.content.Context;
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
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
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
                view = mInflator.inflate(R.layout.video_listing_item, null);
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

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getTitle())) {
                holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            } else {
                holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitleSlug());
            }

            holder.viewCountTextView.setVisibility(View.GONE);
            holder.commentCountTextView.setVisibility(View.GONE);
            holder.recommendCountTextView.setVisibility(View.GONE);

//            switch (articleDataModelsNew.get(position).getPublication_status()) {
//                case AppConstants.VIDEO_STATUS_DRAFT: {
//                    holder.authorTypeTextView.setText(AppConstants.VIDEO_TYPE_DRAFT.toUpperCase());
//                }
//                case AppConstants.VIDEO_STATUS_APPROVAL_PENDING: {
//                    holder.authorTypeTextView.setText(AppConstants.VIDEO_TYPE_APPROVAL_PENDING.toUpperCase());
//                }
//                case AppConstants.VIDEO_STATUS_APPROVAL_CANCELLED: {
//                    holder.authorTypeTextView.setText(AppConstants.VIDEO_TYPE_APPROVAL_CANCELLED.toUpperCase());
//                }
//                case AppConstants.VIDEO_STATUS_PUBLISHED: {
//                    holder.authorTypeTextView.setText(AppConstants.VIDEO_TYPE_PUBLISHED.toUpperCase());
//                }
//                case AppConstants.VIDEO_STATUS_UNPUBLISHED: {
//                    holder.authorTypeTextView.setText(AppConstants.VIDEO_TYPE_UNPUBLISHED.toUpperCase());
//                }
//            }
//            holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.blue_color));
//            holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
//            holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_blogger));

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

//            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getAuthor().getProfilePic().getClientAppMin())) {
//                Picasso.with(mContext).load(articleDataModelsNew.get(position).getAuthor().getProfilePic().getClientAppMin())
//                        .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(holder.authorImageView);
//            } else {
//                holder.authorImageView.setBackgroundResource(R.drawable.default_commentor_img);
//            }
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
