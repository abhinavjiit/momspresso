package com.mycity4kids.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.newmodels.PushNotificationModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.ui.activity.AppSettingsActivity;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.ChooseVideoCategoryActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.PublicProfileActivity;
import com.mycity4kids.ui.activity.SplashActivity;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by anshul on 5/26/16.
 */
public class MyFcmListenerService extends FirebaseMessagingService {
    private static final String TAG = MyFcmListenerService.class.getSimpleName();
    Bitmap bitmap;

    @Override
    public void onNewToken(@NonNull String fcmToken) {
        super.onNewToken(fcmToken);
        SharedPrefUtils.setDeviceToken(BaseApplication.getAppContext(), fcmToken);
        Intent intent = new Intent(this, PushTokenService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                startForegroundService(intent);
            } catch (IllegalArgumentException e) {
                startService(intent);
            }
        } else {
            startService(intent);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String msg = remoteMessage.getData().get("message");
        if (msg == null) {
            msg = remoteMessage.getData().toString();
        }

        if (StringUtils.isNullOrEmpty(msg)) {
            return;
        }
        PushNotificationModel pushNotificationModel;
        try {
            pushNotificationModel = new Gson().fromJson(msg, PushNotificationModel.class);
        } catch (JsonSyntaxException jse) {
            try {
                pushNotificationModel = new Gson().fromJson(new Gson().toJson(remoteMessage.getData()), PushNotificationModel.class);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                return;
            }
        }
        try {
            if (pushNotificationModel != null) {
                String type = pushNotificationModel.getType();
                Log.d("NOTI_TYPE", "CONTENT ===== " + msg);
                Log.d("NOTI_TYPE", "type ===== " + type);

                if (type.equalsIgnoreCase("upcoming_event_list")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        resultIntent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                        resultIntent.putExtra(Constants.LOAD_FRAGMENT, Constants.BUSINESS_EVENTLIST_FRAGMENT);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(ParallelFeedActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "upcoming_event_list ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "upcoming_event_list ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
                } else if (type.equalsIgnoreCase("video_challenge_details")) {
                    Intent intent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        intent = new Intent(getApplicationContext(), SplashActivity.class);
                        intent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        intent = new Intent(getApplicationContext(), NewVideoChallengeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(Constants.CHALLENGE_ID, "" + pushNotificationModel.getChallengeId());
                        intent.putExtra("comingFrom", "notification");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        stackBuilder.editIntentAt(1).putExtra("parentTopicId", AppConstants.HOME_VIDEOS_CATEGORYID);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "momvlog_challenge_details ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "momvlog_challenge_details ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
                } else if (type.equalsIgnoreCase("choose_video_category")) {
                    Intent intent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        intent = new Intent(getApplicationContext(), SplashActivity.class);
                        intent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        intent = new Intent(getApplicationContext(), ChooseVideoCategoryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra("comingFrom", "notification");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "momvlog_challenge_details ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "momvlog_challenge_details ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
                } else if (type.equalsIgnoreCase("article_details")) {
                    Intent intent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        intent = new Intent(getApplicationContext(), SplashActivity.class);
                        intent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        intent = new Intent(getApplicationContext(), ArticleDetailsContainerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(Constants.ARTICLE_ID, "" + pushNotificationModel.getId());
                        intent.putExtra(Constants.AUTHOR_ID, "" + pushNotificationModel.getUser_id());
                        intent.putExtra(Constants.BLOG_SLUG, pushNotificationModel.getBlogPageSlug());
                        intent.putExtra(Constants.TITLE_SLUG, pushNotificationModel.getTitleSlug());
                        intent.putExtra(Constants.FROM_SCREEN, "Notification");
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                        intent.putExtra(Constants.ARTICLE_INDEX, "-1");
                        intent.putExtra(Constants.AUTHOR, pushNotificationModel.getUser_id() + "~");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(ArticleDetailsContainerActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "article_details ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "article_details ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
                } else if (type.equalsIgnoreCase("video_details")) {
                    Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "video_details");
                    Intent intent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        intent = new Intent(getApplicationContext(), SplashActivity.class);
                        intent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        intent = new Intent(getApplicationContext(), ParallelFeedActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(Constants.VIDEO_ID, "" + pushNotificationModel.getId());
                        intent.putExtra(Constants.STREAM_URL, pushNotificationModel.getUrl());
                        intent.putExtra(Constants.AUTHOR_ID, "" + pushNotificationModel.getUser_id());
                        intent.putExtra(Constants.FROM_SCREEN, "Notification");
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                        intent.putExtra(Constants.ARTICLE_INDEX, "-1");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(ParallelFeedActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "video_details ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "video_details ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
                } else if (type.equalsIgnoreCase("event_details")) {
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        resultIntent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), BusinessDetailsActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(BaseApplication.getAppContext()));
                        resultIntent.putExtra(Constants.BUSINESS_OR_EVENT_ID, pushNotificationModel.getId() + "");
                        resultIntent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                        resultIntent.putExtra(Constants.DISTANCE, "0");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(BusinessDetailsActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "event_details ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "event_details ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
                } else if (type.equalsIgnoreCase("webView")) {
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        resultIntent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), LoadWebViewActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra(Constants.WEB_VIEW_URL, pushNotificationModel.getUrl());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(LoadWebViewActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "webView ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "webView ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
                } else if (type.equalsIgnoreCase("profile")) {
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        resultIntent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), PublicProfileActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, pushNotificationModel.getUser_id());
                        resultIntent.putExtra(AppConstants.AUTHOR_NAME, "");
                        resultIntent.putExtra(Constants.FROM_SCREEN, "Notification");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(PublicProfileActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "profile ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "profile ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
                } else if (type.equalsIgnoreCase("app_settings")) {
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        resultIntent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), AppSettingsActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra("load_fragment", Constants.SETTINGS_FRAGMENT);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "app_settings ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "app_settings ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
                } else if (type.equalsIgnoreCase("momsights_screen")) {
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), RewardsContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "momsights_screen ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "momsights_screen ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
                } else if (type.equalsIgnoreCase("campaign_listing")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), CampaignContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "campaign_listing ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "campaign_listing ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
//                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                            R.drawable.ic_launcher);
//                    NotificationCompat.Builder mBuilder =
//                            new NotificationCompat.Builder(this)
//                                    .setLargeIcon(icon)
//                                    .setSmallIcon(R.drawable.icon_notify)
//                                    .setContentTitle(title)
//                                    .setContentIntent(contentIntent)
//                                    .setContentText(body)
//                                    .setAutoCancel(true);
//                    NotificationManager mNotifyMgr =
//                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    mNotifyMgr.notify(requestID, mBuilder.build());
                } else if (type.equalsIgnoreCase("mymoney_pancard")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), RewardsContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("isComingFromRewards", true);
                        resultIntent.putExtra("pageLimit", 5);
                        resultIntent.putExtra("pageNumber", 5);
                        resultIntent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "mymoney_pancard ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "mymoney_pancard ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
//                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                            R.drawable.ic_launcher);
//                    NotificationCompat.Builder mBuilder =
//                            new NotificationCompat.Builder(this)
//                                    .setLargeIcon(icon)
//                                    .setSmallIcon(R.drawable.icon_notify)
//                                    .setContentTitle(title)
//                                    .setContentIntent(contentIntent)
//                                    .setContentText(body)
//                                    .setAutoCancel(true);
//                    NotificationManager mNotifyMgr =
//                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    mNotifyMgr.notify(requestID, mBuilder.build());
                } else if (type.equalsIgnoreCase("category_listing")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), RewardsContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("isComingFromRewards", true);
                        resultIntent.putExtra("pageLimit", 5);
                        resultIntent.putExtra("pageNumber", 5);
                        resultIntent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "category_listing ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "category_listing ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
//                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                            R.drawable.ic_launcher);
//                    NotificationCompat.Builder mBuilder =
//                            new NotificationCompat.Builder(this)
//                                    .setLargeIcon(icon)
//                                    .setSmallIcon(R.drawable.icon_notify)
//                                    .setContentTitle(title)
//                                    .setContentIntent(contentIntent)
//                                    .setContentText(body)
//                                    .setAutoCancel(true);
//                    NotificationManager mNotifyMgr =
//                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    mNotifyMgr.notify(requestID, mBuilder.build());
                } else if (type.equalsIgnoreCase("campaign_submit_proof")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), CampaignContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra("campaign_id", pushNotificationModel.getCampaign_id());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "campaign_submit_proof ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "campaign_submit_proof ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
//                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                            R.drawable.ic_launcher);
//                    NotificationCompat.Builder mBuilder =
//                            new NotificationCompat.Builder(this)
//                                    .setLargeIcon(icon)
//                                    .setSmallIcon(R.drawable.icon_notify)
//                                    .setContentTitle(title)
//                                    .setContentIntent(contentIntent)
//                                    .setContentText(body)
//                                    .setAutoCancel(true);
//                    NotificationManager mNotifyMgr =
//                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    mNotifyMgr.notify(requestID, mBuilder.build());
                } else if (type.equalsIgnoreCase("mymoney_bankdetails")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), RewardsContainerActivity.class);
                        resultIntent.putExtra("isComingfromCampaign", true);
                        resultIntent.putExtra("pageLimit", 4);
                        resultIntent.putExtra("pageNumber", 4);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra("campaign_id", pushNotificationModel.getCampaign_id());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "mymoney_bankdetails ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "mymoney_bankdetails ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
//                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                            R.drawable.ic_launcher);
//                    NotificationCompat.Builder mBuilder =
//                            new NotificationCompat.Builder(this)
//                                    .setLargeIcon(icon)
//                                    .setSmallIcon(R.drawable.icon_notify)
//                                    .setContentTitle(title)
//                                    .setContentIntent(contentIntent)
//                                    .setContentText(body)
//                                    .setAutoCancel(true);
//                    NotificationManager mNotifyMgr =
//                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    mNotifyMgr.notify(requestID, mBuilder.build());
                } else if (type.equalsIgnoreCase("campaign_detail")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), CampaignContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra("campaign_id", pushNotificationModel.getCampaign_id());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if (remoteMessage.getNotification() != null) {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();
                        Log.e("NOTIFICATION_TYPE", "campaign_detail ----- Notification Message");
                        prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                    } else {
                        Log.e("NOTIFICATION_TYPE", "campaign_detail ----- Notification Data");
                        prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(), pushNotificationModel.getRich_image_url()
                                , contentIntent, pushNotificationModel.getSound());
                    }
                } else if (type.equalsIgnoreCase("group_membership") || type.equalsIgnoreCase("group_new_post")
                        || type.equalsIgnoreCase("group_admin_group_edit") || type.equalsIgnoreCase("group_admin")
                        || type.equalsIgnoreCase("group_new_response") || type.equalsIgnoreCase("group_new_reply")
                        || type.equalsIgnoreCase("group_admin_membership") || type.equalsIgnoreCase("group_admin_reported")) {

                } else if (type.equals("remote_config_silent_update")) {
                    SharedPrefUtils.setFirebaseRemoteConfigUpdateFlag(BaseApplication.getAppContext(), true);
                } else {
                    Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "default");
                    String message = pushNotificationModel.getMessage_id();
                    String title = pushNotificationModel.getTitle();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                    resultIntent.putExtra("fromNotification", true);
                    contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Log.e("NOTIFICATION_TYPE", "upcoming_event_list ----- Notification Message");
                    prepareNotification(title, message, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void prepareNotification(String title, String message, String imageUrl, PendingIntent pendingIntent, String sound) {
        try {
            URL url = new URL(imageUrl);
            bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
        } catch (Exception ex) {

        }

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.coin);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                    .setSmallIcon(R.drawable.icon_notify) // notification icon
                    .setContentTitle(title) // title for notification
                    .setContentText(message)// message for notification
                    .setAutoCancel(true) // clear notification after click
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setContentIntent(pendingIntent);
        } else {
            mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                    .setSmallIcon(R.drawable.icon_notify) // notification icon
                    .setContentTitle(title) // title for notification
                    .setContentText(message)// message for notification
                    .setAutoCancel(true) // clear notification after click
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setContentIntent(pendingIntent);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, mBuilder.build());
    }
}
