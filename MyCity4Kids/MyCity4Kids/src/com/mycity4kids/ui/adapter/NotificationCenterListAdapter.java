package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.interfaces.ITopicSelectionEvent;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.NotificationCenterResult;
import com.mycity4kids.newmodels.SelectTopic;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.widget.MyBounceInterpolator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hemant on 21/12/16.
 */
public class NotificationCenterListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<NotificationCenterResult> notificationList;

    public NotificationCenterListAdapter(Context pContext, ArrayList<NotificationCenterResult> notificationList) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.notificationList = notificationList;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.notification_center_list_item, null);
            holder = new ViewHolder();
            holder.rootView = (RelativeLayout) view.findViewById(R.id.rootView);
            holder.notificationTitleTextView = (TextView) view.findViewById(R.id.notificationTitleTextView);
            holder.notificationBodyTextView = (TextView) view.findViewById(R.id.notificationBodyTextView);
            holder.notificationDateTextView = (TextView) view.findViewById(R.id.notificationDateTextView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.notificationTitleTextView.setText("" + notificationList.get(position).getTitle());
        holder.notificationBodyTextView.setText("" + notificationList.get(position).getBody());
        holder.notificationDateTextView.setText("" + DateTimeUtils.getMMMDDFormatDate(notificationList.get(position).getCreatedTime()));

        String nType = SharedPrefUtils.getNotificationType(mContext, notificationList.get(position).getNotifType());
        if (AppConstants.NOTIFICATION_TYPE_WEBVIEW.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(mContext, LoadWebViewActivity.class);
                    intent1.putExtra(Constants.WEB_VIEW_URL, notificationList.get(position).getUrl());
                    mContext.startActivity(intent1);
                }
            });
        } else if (AppConstants.NOTIFICATION_TYPE_ARTICLE_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ArticlesAndBlogsDetailsActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, notificationList.get(position).getArticleId());
                    intent.putExtra(Constants.AUTHOR_ID, notificationList.get(position).getAuthorId());
                    intent.putExtra(Constants.BLOG_SLUG, notificationList.get(position).getBlogTitleSlug());
                    intent.putExtra(Constants.TITLE_SLUG, notificationList.get(position).getTitleSlug());
                    mContext.startActivity(intent);
                }
            });
        } else if (AppConstants.NOTIFICATION_TYPE_VIDEO_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ArticlesAndBlogsDetailsActivity.class);
                    intent.putExtra(Constants.VIDEO_ID, notificationList.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, notificationList.get(position).getAuthorId());
                    mContext.startActivity(intent);
                }
            });
        } else if (AppConstants.NOTIFICATION_TYPE_UPCOMING_EVENTS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(mContext, DashboardActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "upcoming_event_list");
                    intent1.putExtra("notificationExtras", bundle);
                    mContext.startActivity(intent1);
                }
            });
        } else if (AppConstants.NOTIFICATION_TYPE_PROFILE.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(mContext, BloggerDashboardActivity.class);
                    intent1.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, notificationList.get(position).getUserId());
                    mContext.startActivity(intent1);
                }
            });
        }

        return view;
    }

    class ViewHolder {
        RelativeLayout rootView;
        TextView notificationTitleTextView;
        TextView notificationBodyTextView;
        TextView notificationDateTextView;
    }
}
