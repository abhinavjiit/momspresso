package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ChooseVideoCategoryActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.MixPanelUtils;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.net.URL;
import java.util.ArrayList;

/**
 * @author deepanker.chaudhary
 */
public class VlogsListingAdapter extends BaseAdapter {

    private ArrayList<VlogsListingAndDetailResult> mArticleListData;
    private Context mContext;
    private Topics topic;
    private LayoutInflater mInflator;
    ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;

    private final float density;

    public VlogsListingAdapter(Context pContext, Topics topic) {
        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.topic = topic;
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
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0 && position % 5 == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (getItemViewType(position) == 0) {
            AddVlogViewHolder addVlogViewHolder;
            if (view == null) {
                addVlogViewHolder = new AddVlogViewHolder();
                view = mInflator.inflate(R.layout.add_momvlog_list_item, null);
                addVlogViewHolder.goldLogo = (ImageView) view.findViewById(R.id.goldLogo);
                addVlogViewHolder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                addVlogViewHolder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                addVlogViewHolder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                addVlogViewHolder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
                addVlogViewHolder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                addVlogViewHolder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                addVlogViewHolder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
                addVlogViewHolder.addMomVlogImageView = (ImageView) view.findViewById(R.id.addMomVlogImageView);
                if (AppConstants.LOCALE_HINDI.equals(SharedPrefUtils.getAppLocale(mContext))) {
                    addVlogViewHolder.addMomVlogImageView.setImageResource(R.drawable.add_mom_vlog_hi);
                } else {
                    addVlogViewHolder.addMomVlogImageView.setImageResource(R.drawable.add_mom_vlog_en);
                }
                view.setTag(addVlogViewHolder);
            } else {
                addVlogViewHolder = (AddVlogViewHolder) view.getTag();
            }

            addVlogViewHolder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            addVlogViewHolder.viewCountTextView.setVisibility(View.GONE);
            addVlogViewHolder.commentCountTextView.setVisibility(View.GONE);
            addVlogViewHolder.recommendCountTextView.setVisibility(View.GONE);

            try {
                String userName = articleDataModelsNew.get(position).getAuthor().getFirstName() + " " + articleDataModelsNew.get(position).getAuthor().getLastName();
                if (StringUtils.isNullOrEmpty(userName) || userName.trim().equalsIgnoreCase("")) {
                    addVlogViewHolder.txvAuthorName.setText("NA");
                } else {
                    addVlogViewHolder.txvAuthorName.setText(userName);
                }
            } catch (Exception e) {
                addVlogViewHolder.txvAuthorName.setText("NA");
            }
            try {
                Picasso.with(mContext).load(articleDataModelsNew.get(position).getThumbnail())
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(addVlogViewHolder.articleImageView);
            } catch (Exception e) {
                addVlogViewHolder.articleImageView.setImageResource(R.drawable.default_article);
            }
            if (articleDataModelsNew.get(position).isIs_gold()) {
                addVlogViewHolder.goldLogo.setVisibility(View.VISIBLE);
            } else {
                addVlogViewHolder.goldLogo.setVisibility(View.GONE);


            }

            addVlogViewHolder.addMomVlogImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                    MixPanelUtils.pushAddMomVlogClickEvent(mixpanel, topic.getDisplay_name());
                    Intent intent = new Intent(mContext, ChooseVideoCategoryActivity.class);
                    mContext.startActivity(intent);
                }
            });
            return view;
        } else {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = mInflator.inflate(R.layout.video_listing_item, null);
                holder.goldLogo = (ImageView) view.findViewById(R.id.goldLogo);
                holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                holder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                holder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
                holder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                holder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                holder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
                Log.d("SetTag", "VLOGSetTag = " + position);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            holder.viewCountTextView.setVisibility(View.GONE);
            holder.commentCountTextView.setVisibility(View.GONE);
            holder.recommendCountTextView.setVisibility(View.GONE);

            try {
                String userName = articleDataModelsNew.get(position).getAuthor().getFirstName() + " " + articleDataModelsNew.get(position).getAuthor().getLastName();
                if (StringUtils.isNullOrEmpty(userName) || userName.trim().equalsIgnoreCase("")) {
                    holder.txvAuthorName.setText("NA");
                } else {
                    holder.txvAuthorName.setText(userName);
                }
            } catch (Exception e) {
                holder.txvAuthorName.setText("NA");
            }
            try {
                Picasso.with(mContext).load(articleDataModelsNew.get(position).getThumbnail())
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
            } catch (Exception e) {
                holder.articleImageView.setImageResource(R.drawable.default_article);
            }
            if (articleDataModelsNew.get(position).isIs_gold()) {
                holder.goldLogo.setVisibility(View.VISIBLE);
            } else {
                holder.goldLogo.setVisibility(View.GONE);

            }
            return view;
        }
    }

    class ViewHolder {
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView authorImageView;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        ImageView goldLogo;
    }

    class AddVlogViewHolder {
        ImageView addMomVlogImageView;
        ImageView goldLogo;
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView authorImageView;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
    }
}
