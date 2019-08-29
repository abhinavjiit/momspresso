package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.NotificationReadRequest;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.NotificationCenterListResponse;
import com.mycity4kids.models.response.NotificationCenterResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ArticleListingActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.PrivateProfileActivity;
import com.mycity4kids.ui.activity.PublicProfileActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.SuggestedTopicsActivity;
import com.mycity4kids.ui.activity.TopicsListingActivity;
import com.mycity4kids.ui.activity.TopicsShortStoriesContainerFragment;
import com.mycity4kids.ui.activity.ViewGroupPostCommentsRepliesActivity;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;
import com.mycity4kids.ui.fragment.GroupsFragment;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 21/12/16.
 */
public class NotificationCenterListAdapter extends BaseAdapter implements GroupMembershipStatus.IMembershipStatus {

    private MixpanelAPI mixpanel;
    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<NotificationCenterResult> notificationList;

    public NotificationCenterListAdapter(Context pContext, ArrayList<NotificationCenterResult> notificationList) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.notificationList = notificationList;
        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
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

        holder.notificationTitleTextView.setText(("" + notificationList.get(position).getTitle()).toUpperCase());
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
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "webView");
                    Intent intent1 = new Intent(mContext, LoadWebViewActivity.class);
                    intent1.putExtra(Constants.WEB_VIEW_URL, notificationList.get(position).getUrl());
                    mContext.startActivity(intent1);

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "0");
                        //Log.d("NotificationCenterClick", jsonObject.toString());
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "1".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_ARTICLE_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "article_details");
                    Intent intent = new Intent(mContext, ArticleDetailsContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, notificationList.get(position).getArticleId());
                    intent.putExtra(Constants.AUTHOR_ID, notificationList.get(position).getAuthorId());
                    intent.putExtra(Constants.BLOG_SLUG, notificationList.get(position).getBlogTitleSlug());
                    intent.putExtra(Constants.TITLE_SLUG, notificationList.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "NotificationsScreen");
                    intent.putExtra(Constants.FROM_SCREEN, "NotificationsScreen");
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                    intent.putExtra(Constants.AUTHOR, notificationList.get(position).getUserId() + "~");
                    mContext.startActivity(intent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "1");
                        //Log.d("NotificationCenterClick", jsonObject.toString());
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "4".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_VIDEO_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "video_details");
                    Intent intent = new Intent(mContext, ParallelFeedActivity.class);
                    intent.putExtra(Constants.VIDEO_ID, notificationList.get(position).getArticleId());
                    //intent.putExtra(Constants.STREAM_URL, notificationList.get(position).getUrl());
                    intent.putExtra(Constants.AUTHOR_ID, notificationList.get(position).getAuthorId());
                    intent.putExtra(Constants.FROM_SCREEN, "Home Screen");
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Funny Videos");
                    mContext.startActivity(intent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "4");
                        //Log.d("NotificationCenterClick", jsonObject.toString());
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "3".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_UPCOMING_EVENTS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "upcoming_event_list");
                    FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                    bundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(mContext));
                    bundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
                    fragment.setArguments(bundle);
                    ((DashboardActivity) mContext).addFragment(fragment, bundle, true);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "3");
                        //Log.d("NotificationCenterClick", jsonObject.toString());
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "2".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_PROFILE.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "profile");
                    if (notificationList.get(position).getAuthorId().equals(notificationList.get(position).getUserId())) {
                        Intent pIntent = new Intent(mContext, PrivateProfileActivity.class);
                        mContext.startActivity(pIntent);
                    } else {
                        Intent intent1 = new Intent(mContext, PublicProfileActivity.class);
                        intent1.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, notificationList.get(position).getAuthorId());
                        intent1.putExtra(AppConstants.AUTHOR_NAME, "");
                        intent1.putExtra(Constants.FROM_SCREEN, "Notification Center List");
                        mContext.startActivity(intent1);
                    }
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "2");
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "-1".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_APP_SETTINGS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "-1");
                        //Log.d("NotificationCenterClick", jsonObject.toString());
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "5".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_EDITOR.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "write_blog");
                    launchEditor();
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "5");
                        //Log.d("NotificationCenterClick", jsonObject.toString());
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "6".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_SUGGESTED_TOPICS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "suggested_topics");
                    Intent intent = new Intent(mContext, SuggestedTopicsActivity.class);
                    mContext.startActivity(intent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "6");
                        //Log.d("NotificationCenterClick", jsonObject.toString());
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "7".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_TODAYS_BEST.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "todaysBest");
                    Intent intent1 = new Intent(mContext, ArticleListingActivity.class);
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_TODAYS_BEST);
                    mContext.startActivity(intent1);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "7");
                        //Log.d("NotificationCenterClick", jsonObject.toString());
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "8".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_SHORT_STORY_LIST.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "shortStoryListing");
                    TopicsShortStoriesContainerFragment fragment1 = new TopicsShortStoriesContainerFragment();
                    Bundle mBundle1 = new Bundle();
                    mBundle1.putString("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
                    fragment1.setArguments(mBundle1);
                    ((DashboardActivity) mContext).addFragment(fragment1, mBundle1, true);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "8");
                        //Log.d("NotificationCenterClick", jsonObject.toString());
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "9".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_SHORT_STORY_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "shortStoryDetails");
                    Intent intent = new Intent(mContext, ShortStoryContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, notificationList.get(position).getArticleId());
                    intent.putExtra(Constants.AUTHOR_ID, notificationList.get(position).getAuthorId());
                    intent.putExtra(Constants.BLOG_SLUG, notificationList.get(position).getBlogTitleSlug());
                    intent.putExtra(Constants.TITLE_SLUG, notificationList.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "NotificationsScreen");
                    intent.putExtra(Constants.FROM_SCREEN, "NotificationsScreen");
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                    intent.putExtra(Constants.AUTHOR, notificationList.get(position).getUserId() + "~");
                    mContext.startActivity(intent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "9");
                        //Log.d("NotificationCenterClick", jsonObject.toString());
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "10".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_GROUP_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "groupDetails");
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "10");
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "11".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_POST_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "groupDetails");
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "11");
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "12".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_POST_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "postDetails");
                    Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                    intent.putExtra("postId", notificationList.get(position).getPostId());
                    intent.putExtra("groupId", notificationList.get(position).getGroupId());
                    mContext.startActivity(intent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "12");
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "13".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_POST_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "postDetails");
                    Intent intent = new Intent(mContext, ViewGroupPostCommentsRepliesActivity.class);
                    intent.putExtra("postId", notificationList.get(position).getPostId());
                    intent.putExtra("groupId", notificationList.get(position).getGroupId());
                    intent.putExtra("responseId", notificationList.get(position).getResponseId());
                    intent.putExtra("action", "commentReply");
                    mContext.startActivity(intent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "13");
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "14".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_POST_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "groupDetails");
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "14");
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "15".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_POST_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "groupDetails");
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "15");
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "16".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_POST_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "groupDetails");
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "16");
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "17".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_POST_DETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "groupDetails");
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "17");
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && "18".equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_GROUP_LISTING.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", "groupListing");
                    GroupsFragment fragment0 = new GroupsFragment();
                    Bundle mBundle0 = new Bundle();
                    fragment0.setArguments(mBundle0);
                    ((DashboardActivity) mContext).addFragment(fragment0, mBundle0, true);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", "18");
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && AppConstants.NOTIFICATION_NOTIFY_TYPE_MOMSIGHT_REWARDS.equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_MOMSIGHT_REWARD_LISTING.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", AppConstants.NOTIFICATION_TYPE_MOMSIGHT_REWARD_LISTING);
                    Intent intent = new Intent(mContext, RewardsContainerActivity.class);
                    mContext.startActivity(intent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", AppConstants.NOTIFICATION_NOTIFY_TYPE_MOMSIGHT_REWARDS);
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && AppConstants.NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_LISTING.equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_CAMPAIGN_LISING.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", AppConstants.NOTIFICATION_TYPE_CAMPAIGN_LISING);
                    Intent campaignIntent = new Intent(mContext, CampaignContainerActivity.class);
                    campaignIntent.putExtra("campaign_listing", "campaign_listing");
                    mContext.startActivity(campaignIntent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", AppConstants.NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_LISTING);
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && AppConstants.NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_DETAIL.equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_CAMPAIGN_DETAIL.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", AppConstants.NOTIFICATION_TYPE_CAMPAIGN_DETAIL);
                    Intent campaignIntent = new Intent(mContext, CampaignContainerActivity.class);
                    campaignIntent.putExtra("campaign_id", notificationList.get(position).getCampaign_id() + "");
                    campaignIntent.putExtra("campaign_detail", "campaign_detail");
                    campaignIntent.putExtra("fromNotification", true);
                    mContext.startActivity(campaignIntent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", AppConstants.NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_DETAIL);
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && AppConstants.NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_SUBMIT_PROOF.equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_CAMPAIGN_SUBMIT_PROOF.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", AppConstants.NOTIFICATION_TYPE_CAMPAIGN_SUBMIT_PROOF);
                    Intent campaignIntent = new Intent(mContext, CampaignContainerActivity.class);
                    campaignIntent.putExtra("campaign_Id", notificationList.get(position).getCampaign_id() + "");
                    campaignIntent.putExtra("campaign_submit_proof", "campaign_submit_proof");
                    mContext.startActivity(campaignIntent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", AppConstants.NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_SUBMIT_PROOF);
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && AppConstants.NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_PANCARD.equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_CAMPAIGN_PANCARD.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", AppConstants.NOTIFICATION_TYPE_CAMPAIGN_PANCARD);
                    Intent campaignIntent = new Intent(mContext, RewardsContainerActivity.class);
                    campaignIntent.putExtra("isComingFromRewards", true);
                    campaignIntent.putExtra("pageLimit", 5);
                    campaignIntent.putExtra("pageNumber", 5);
                    campaignIntent.putExtra("panCardFormNotification", "mymoney_pancard");
                    campaignIntent.putExtra("mymoney_pancard", "mymoney_pancard");
                    mContext.startActivity(campaignIntent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", AppConstants.NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_PANCARD);
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && AppConstants.NOTIFICATION_NOTIFY_TYPE_TOPIC.equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_CAMPAIGN_CATEGORY_LISTING.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", AppConstants.NOTIFICATION_TYPE_CAMPAIGN_CATEGORY_LISTING);
                    Intent intent = new Intent(mContext, TopicsListingActivity.class);
                    intent.putExtra("parentTopicId", notificationList.get(position).getCategoryId());
                    mContext.startActivity(intent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", AppConstants.NOTIFICATION_NOTIFY_TYPE_TOPIC);
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if ((StringUtils.isNullOrEmpty(nType) && AppConstants.NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_BANKDETAIL.equals(notificationList.get(position).getNotifType())) || AppConstants.NOTIFICATION_TYPE_CAMPAIGN_BANKDETAILS.equals(nType)) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
                    hitNotificationReadAPI(notificationList.get(position).getId());
                    notifyDataSetChanged();
                    Utils.pushEventNotificationClick(mContext, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "Notification Centre", AppConstants.NOTIFICATION_TYPE_CAMPAIGN_BANKDETAILS);
                    Intent campaignIntent = new Intent(mContext, RewardsContainerActivity.class);
                    campaignIntent.putExtra("isComingfromCampaign", true);
                    campaignIntent.putExtra("pageLimit", 4);
                    campaignIntent.putExtra("pageNumber", 4);
                    campaignIntent.putExtra("campaign_Id", notificationList.get(position).getCampaign_id() + "");
                    campaignIntent.putExtra("mymoney_bankdetails", "mymoney_bankdetails");
                    mContext.startActivity(campaignIntent);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("type", AppConstants.NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_BANKDETAIL);
                        mixpanel.track("NotificationCenterClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return view;
    }

    private void launchEditor() {
        Intent intent1 = new Intent(mContext, EditorPostActivity.class);
        Bundle bundle5 = new Bundle();
        bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
        bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
        bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                mContext.getString(R.string.example_post_title_placeholder));
        bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                mContext.getString(R.string.example_post_content_placeholder));
        bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
        bundle5.putString("from", "dashboard");
        intent1.putExtras(bundle5);
        mContext.startActivity(intent1);
    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        String userType = null;
        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {

        } else {
            if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
            } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
            }
        }

        if (!AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(userType) && !AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(userType)) {
            if ("male".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender()) ||
                    "m".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())) {
                Toast.makeText(mContext, mContext.getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                if (BuildConfig.DEBUG || AppConstants.DEBUGGING_USER_ID.contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {

                } else {
                    return;
                }
            } else {

            }
        }

        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
            Intent intent = new Intent(mContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mContext.startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            Toast.makeText(mContext, mContext.getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT).show();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(mContext, GroupDetailsActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mContext.startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(mContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            intent.putExtra("pendingMembershipFlag", true);
            mContext.startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

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
                NotificationCenterListResponse responseData = response.body();
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




