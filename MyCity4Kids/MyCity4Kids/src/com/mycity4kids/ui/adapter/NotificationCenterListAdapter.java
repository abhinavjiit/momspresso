package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.NotificationReadRequest;
import com.mycity4kids.models.response.NotificationCenterListResponse;
import com.mycity4kids.models.response.NotificationCenterResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.ui.CircleTransformation;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.ui.activity.VlogsDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

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
            holder.notificationImageView = (ImageView) view.findViewById(R.id.notificationImageView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.notificationTitleTextView.setText("" + notificationList.get(position).getTitle());
        holder.notificationBodyTextView.setText("" + notificationList.get(position).getBody());
        holder.notificationDateTextView.setText("" + DateTimeUtils.getMMMDDFormatDate(notificationList.get(position).getCreatedTime()));

        if (!StringUtils.isNullOrEmpty(notificationList.get(position).getThumbNail())) {
            Picasso.with(mContext).load(notificationList.get(position).getThumbNail())
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.notificationImageView);
        } else {
            Picasso.with(mContext).load(R.drawable.default_article).into(holder.notificationImageView);
        }

        if (AppConstants.NOTIFICATION_STATUS_UNREAD.equals(notificationList.get(position).getIsRead())) {
            holder.rootView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.notification_center_unread_bg));
        } else {
            holder.rootView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.notification_center_read_bg));
        }

        String nType = SharedPrefUtils.getNotificationType(mContext, notificationList.get(position).getNotifType());
        if ((StringUtils.isNullOrEmpty(nType) && "0".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_WEBVIEW.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Intent intent1 = new Intent(mContext, LoadWebViewActivity.class);
                    intent1.putExtra(Constants.WEB_VIEW_URL, notificationList.get(position).getUrl());
                    mContext.startActivity(intent1);
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "1".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_ARTICLE_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Intent intent = new Intent(mContext, ArticlesAndBlogsDetailsActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, notificationList.get(position).getArticleId());
                    intent.putExtra(Constants.AUTHOR_ID, notificationList.get(position).getAuthorId());
                    intent.putExtra(Constants.BLOG_SLUG, notificationList.get(position).getBlogTitleSlug());
                    intent.putExtra(Constants.TITLE_SLUG, notificationList.get(position).getTitleSlug());

//                    if (AppConstants.NOTIFICATION_STATUS_UNREAD.equals(notificationList.get(position).getIsRead())) {
//                        intent.putExtra(Constants.NOTIFICATION_CENTER_ID, notificationList.get(position).getId());
//                    } else {
//                        intent.putExtra(Constants.NOTIFICATION_CENTER_ID, "");
//                    }
                    mContext.startActivity(intent);
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "1".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_VIDEO_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Intent intent = new Intent(mContext, VlogsDetailActivity.class);
                    intent.putExtra(Constants.VIDEO_ID, notificationList.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, notificationList.get(position).getAuthorId());
                    mContext.startActivity(intent);
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "3".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_UPCOMING_EVENTS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Intent intent1 = new Intent(mContext, DashboardActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "upcoming_event_list");
                    if (AppConstants.NOTIFICATION_STATUS_UNREAD.equals(notificationList.get(position).getIsRead())) {
                        bundle.putString(Constants.NOTIFICATION_CENTER_ID, notificationList.get(position).getId());
                    } else {
                        bundle.putString(Constants.NOTIFICATION_CENTER_ID, "");
                    }
                    intent1.putExtra("notificationExtras", bundle);
                    mContext.startActivity(intent1);
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "2".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_PROFILE.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Intent intent1 = new Intent(mContext, BloggerDashboardActivity.class);
                    intent1.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, notificationList.get(position).getUserId());
                    if (AppConstants.NOTIFICATION_STATUS_UNREAD.equals(notificationList.get(position).getIsRead())) {
                        intent1.putExtra(Constants.NOTIFICATION_CENTER_ID, notificationList.get(position).getId());
                    } else {
                        intent1.putExtra(Constants.NOTIFICATION_CENTER_ID, "");
                    }
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
        ImageView notificationImageView;
    }

    private void hitNotificationReadAPI(String notificationCenterId) {
        NotificationReadRequest notificationReadRequest = new NotificationReadRequest();
        notificationReadRequest.setNotifId(notificationCenterId);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);
        Call<NotificationCenterListResponse> call = notificationsAPI.markNotificationAsRead(notificationReadRequest);
        call.enqueue(markNotificationReadResponseCallback);
    }

    private Callback<NotificationCenterListResponse> markNotificationReadResponseCallback = new Callback<NotificationCenterListResponse>() {
        @Override
        public void onResponse(Call<NotificationCenterListResponse> call, retrofit2.Response<NotificationCenterListResponse> response) {
            if (response == null || response.body() == null) {
                return;
            }
            try {
                NotificationCenterListResponse responseData = (NotificationCenterListResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<NotificationCenterListResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
