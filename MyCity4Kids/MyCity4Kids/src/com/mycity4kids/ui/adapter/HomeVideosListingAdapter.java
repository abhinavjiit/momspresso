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
import com.mycity4kids.models.response.HomeVideosListingResult;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * @author deepanker.chaudhary
 */
public class HomeVideosListingAdapter extends BaseAdapter {

    private ArrayList<HomeVideosListingResult> mArticleListData;
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<HomeVideosListingResult> articleDataModelsNew;

    private final float density;

    public HomeVideosListingAdapter(Context pContext) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
    }

    public void setListData(ArrayList<HomeVideosListingResult> mParentingLists) {
        mArticleListData = mParentingLists;
    }

    public void setNewListData(ArrayList<HomeVideosListingResult> mParentingLists_new) {
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

                holder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                holder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                holder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            holder.viewCountTextView.setVisibility(View.GONE);
            holder.commentCountTextView.setVisibility(View.GONE);
            holder.recommendCountTextView.setVisibility(View.GONE);

            String userName = articleDataModelsNew.get(position).getAuthor().getFirstName() + " " + articleDataModelsNew.get(position).getAuthor().getLastName();
            if (StringUtils.isNullOrEmpty(userName) || userName.trim().equalsIgnoreCase("")) {
                holder.txvAuthorName.setText("NA");
            } else {
                holder.txvAuthorName.setText(userName);
            }

            try {
                Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURL(articleDataModelsNew.get(position).getUploaded_url()))
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
            } catch (Exception e) {
                holder.articleImageView.setImageResource(R.drawable.default_article);
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