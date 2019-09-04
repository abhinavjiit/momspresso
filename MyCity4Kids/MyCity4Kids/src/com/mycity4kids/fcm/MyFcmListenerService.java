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

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
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
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.PublicProfileActivity;
import com.mycity4kids.ui.activity.SplashActivity;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by anshul on 5/26/16.
 */
public class MyFcmListenerService extends FirebaseMessagingService {
    private static final String TAG = MyFcmListenerService.class.getSimpleName();
    Bitmap bitmap;

    @Override
    public void onNewToken(@NonNull String fcmToken) {
        super.onNewToken(fcmToken);
        SharedPrefUtils.setDeviceToken(this, fcmToken);
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

    /*this will prepare the notification for every type*/
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
        PendingIntent launchIntent = getLaunchIntent(1, getBaseContext());
        NotificationCompat.Builder mBuilder;
        if (!imageUrl.isEmpty()) {
            if (sound.isEmpty()) {
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
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                        .setContentIntent(pendingIntent);
                //.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.coin));
            }
        } else {
            if (sound.isEmpty()) {
                mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                        .setSmallIcon(R.drawable.icon_notify) // notification icon
                        .setContentTitle(title) // title for notification
                        .setContentText(message)// message for notification
                        .setAutoCancel(true) // clear notification after click
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                        .setContentIntent(pendingIntent);
            } else {
                mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                        .setSmallIcon(R.drawable.icon_notify) // notification icon
                        .setContentTitle(title) // title for notification
                        .setContentText(message)// message for notification
                        .setAutoCancel(true) // clear notification after click
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                        .setContentIntent(pendingIntent);
                //.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.coin));
            }
        }


        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public PendingIntent getLaunchIntent(int notificationId, Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("notificationId", notificationId);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
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
            pushNotificationModel = new Gson().fromJson(new Gson().toJson(remoteMessage.getData()), PushNotificationModel.class);
        }
        try {
            if (pushNotificationModel != null) {
                String type = pushNotificationModel.getType();
                Log.d("NOTI_TYPE", "type ===== " + type);
                if (type.equalsIgnoreCase("upcoming_event_list")) {
                    Log.i(TAG, " INSIDE EVENTS LIST: " + msg);
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher
                    );
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        resultIntent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        // Creates an explicit intent for an ResultActivity to receive.
                        resultIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                        resultIntent.putExtra(Constants.LOAD_FRAGMENT, Constants.BUSINESS_EVENTLIST_FRAGMENT);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(ParallelFeedActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());

                } else if (type.equalsIgnoreCase("article_details")) {
                    Intent intent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
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
                        // Adds the back stack
                        stackBuilder.addParentStack(ArticleDetailsContainerActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                } else if (type.equalsIgnoreCase("video_details")) {
                    Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "video_details");
                    int requestID = (int) System.currentTimeMillis();
                    Intent intent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
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
                        // Adds the back stack
                        stackBuilder.addParentStack(ParallelFeedActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    }
                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();

                    prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());

                } else if (type.equalsIgnoreCase("event_details")) {
//                    Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "event_details");
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        resultIntent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), BusinessDetailsActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getApplication()));
                        resultIntent.putExtra(Constants.BUSINESS_OR_EVENT_ID, pushNotificationModel.getId() + "");
                        resultIntent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                        resultIntent.putExtra(Constants.DISTANCE, "0");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(BusinessDetailsActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    }
                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();

                    prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                } else if (type.equalsIgnoreCase("webView")) {

                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        resultIntent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), LoadWebViewActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra(Constants.WEB_VIEW_URL, pushNotificationModel.getUrl());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(LoadWebViewActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    }
                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());

                } else if (type.equalsIgnoreCase("profile")) {

                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
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
                        // Adds the back stack
                        stackBuilder.addParentStack(PublicProfileActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());

                } else if (type.equalsIgnoreCase("app_settings")) {
//                    Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "app_settings");
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        resultIntent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), AppSettingsActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra("load_fragment", Constants.SETTINGS_FRAGMENT);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());

                } else if (type.equalsIgnoreCase("momsights_screen")) {
//                    Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "app_settings");
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("upupgrade true", "it's true");
                    } else {
                        resultIntent = new Intent(getApplicationContext(), RewardsContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("opening rewards contai", "it's true");
                    }
                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());

                } else if (type.equalsIgnoreCase("campaign_listing")) {
//                    Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "app_settings");
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("upupgrade true", "it's true");
                    } else {
                        resultIntent = new Intent(getApplicationContext(), CampaignContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("opening rewards contai", "it's true");
                    }
                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setLargeIcon(icon)
                                    .setSmallIcon(R.drawable.icon_notify)
                                    .setContentTitle(title)
                                    .setContentIntent(contentIntent)
                                    .setContentText(body)
                                    .setAutoCancel(true);

                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(requestID, mBuilder.build());

                } else if (type.equalsIgnoreCase("mymoney_pancard")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("upupgrade true", "it's true");
                    } else {
                        resultIntent = new Intent(getApplicationContext(), RewardsContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("isComingFromRewards", true);
                        resultIntent.putExtra("pageLimit", 5);
                        resultIntent.putExtra("pageNumber", 5);
                        resultIntent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("opening mymoney_pancard", "it's true" + pushNotificationModel.getCampaign_id());

                    }

                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setLargeIcon(icon)
                                    .setSmallIcon(R.drawable.icon_notify)
                                    .setContentTitle(title)
                                    .setContentIntent(contentIntent)
                                    .setContentText(body)
                                    .setAutoCancel(true);

                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(requestID, mBuilder.build());
                } else if (type.equalsIgnoreCase("category_listing")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("upupgrade true", "it's true");
                    } else {
                        resultIntent = new Intent(getApplicationContext(), RewardsContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("isComingFromRewards", true);
                        resultIntent.putExtra("pageLimit", 5);
                        resultIntent.putExtra("pageNumber", 5);
                        resultIntent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("opening mymoney_pancard", "it's true" + pushNotificationModel.getCampaign_id());

                    }

                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setLargeIcon(icon)
                                    .setSmallIcon(R.drawable.icon_notify)
                                    .setContentTitle(title)
                                    .setContentIntent(contentIntent)
                                    .setContentText(body)
                                    .setAutoCancel(true);

                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(requestID, mBuilder.build());
                } else if (type.equalsIgnoreCase("campaign_submit_proof")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("upupgrade true", "it's true");
                    } else {
                        resultIntent = new Intent(getApplicationContext(), CampaignContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra("campaign_id", pushNotificationModel.getCampaign_id());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("opening campaign_detail", "it's true" + pushNotificationModel.getCampaign_id());

                    }

                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setLargeIcon(icon)
                                    .setSmallIcon(R.drawable.icon_notify)
                                    .setContentTitle(title)
                                    .setContentIntent(contentIntent)
                                    .setContentText(body)
                                    .setAutoCancel(true);

                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(requestID, mBuilder.build());

                } else if (type.equalsIgnoreCase("mymoney_bankdetails")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("upupgrade true", "it's true");
                    } else {
                        resultIntent = new Intent(getApplicationContext(), RewardsContainerActivity.class);
                        resultIntent.putExtra("isComingfromCampaign", true);
                        resultIntent.putExtra("pageLimit", 4);
                        resultIntent.putExtra("pageNumber", 4);
                        //    resultIntent = new Intent(getApplicationContext(), CampaignContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra("campaign_id", pushNotificationModel.getCampaign_id());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("mymoney_bankdetails", "it's true" + pushNotificationModel.getCampaign_id());

                    }

                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setLargeIcon(icon)
                                    .setSmallIcon(R.drawable.icon_notify)
                                    .setContentTitle(title)
                                    .setContentIntent(contentIntent)
                                    .setContentText(body)
                                    .setAutoCancel(true);

                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(requestID, mBuilder.build());

                } else if (type.equalsIgnoreCase("campaign_detail")) {
//                    Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "app_settings");
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("upupgrade true", "it's true");
                    } else {
                        resultIntent = new Intent(getApplicationContext(), CampaignContainerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra("campaign_id", pushNotificationModel.getCampaign_id());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.e("opening campaign_detail", "it's true" + pushNotificationModel.getCampaign_id());
                    }
                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setLargeIcon(icon)
                                    .setSmallIcon(R.drawable.icon_notify)
                                    .setContentTitle(title)
                                    .setContentIntent(contentIntent)
                                    .setContentText(body)
                                    .setAutoCancel(true);

                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(requestID, mBuilder.build());

                } else if (type.equalsIgnoreCase("group_membership") || type.equalsIgnoreCase("group_new_post")
                        || type.equalsIgnoreCase("group_admin_group_edit") || type.equalsIgnoreCase("group_admin")
                        || type.equalsIgnoreCase("group_new_response") || type.equalsIgnoreCase("group_new_reply")
                        || type.equalsIgnoreCase("group_admin_membership") || type.equalsIgnoreCase("group_admin_reported")) {


                } else if (type.equals("remote_config_silent_update")) {
                    SharedPrefUtils.setFirebaseRemoteConfigUpdateFlag(this, true);
                } else {
                    Log.i(TAG, " Default : " + msg);
                    Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "default");
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);

                    int requestID = (int) System.currentTimeMillis();
                    String message = pushNotificationModel.getMessage_id();
                    String title = pushNotificationModel.getTitle();

                    NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent resultIntent;
                    PendingIntent contentIntent;
                    resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                    resultIntent.putExtra("fromNotification", true);
                    contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    prepareNotification(title, message, pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     *To get a Bitmap image from the URL received
     * */
    /*
     *To get a Bitmap image from the URL received
     * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

    private Bitmap getScaledBitmap(Bitmap bitmap) {
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int height = 180;


        float scaleFactor = (float) displaymetrics.heightPixels / (float) displaymetrics.widthPixels;
        int newWidth = (int) (bitmap.getWidth() / scaleFactor);

        return Bitmap.createScaledBitmap(bitmap, 200, height, true);
    }

}
