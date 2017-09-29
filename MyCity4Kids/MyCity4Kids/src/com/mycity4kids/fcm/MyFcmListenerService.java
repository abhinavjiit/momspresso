package com.mycity4kids.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.util.Log;

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
import com.mycity4kids.ui.activity.AppSettingsActivity;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.BloggerProfileActivity;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.ui.activity.SplashActivity;
import com.mycity4kids.ui.activity.VlogsDetailActivity;

import java.util.Map;

/**
 * Created by anshul on 5/26/16.
 */
public class MyFcmListenerService extends FirebaseMessagingService {
    private static final String TAG = MyFcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();
        Map data = remoteMessage.getData();
        Log.e("from", from);
        Log.e("data", data.toString());
//        Log.e("Notification Body", remoteMessage.getNotification().getBody());
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
            pushNotificationModel = new Gson().fromJson(new Gson().toJson(remoteMessage.getData()), PushNotificationModel.class);
        }
        try {
            if (pushNotificationModel != null) {

                String type = pushNotificationModel.getType();
                if (type.equalsIgnoreCase("upcoming_event_list")) {
//                    Utils.pushEvent(getApplicationContext(), GTMEventType.UPCOMING_EVENTS_NOTIFICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId() + "", "");
//                    Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "upcoming_event_list");
                    Log.i(TAG, " INSIDE EVENTS LIST: " + msg);
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);

                    int requestID = (int) System.currentTimeMillis();
                    String title = remoteMessage.getNotification().getTitle();
                    String body = remoteMessage.getNotification().getBody();

                    NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        resultIntent.putExtra("fromNotification", true);
                    } else {
                        // Creates an explicit intent for an ResultActivity to receive.
                        resultIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                        resultIntent.putExtra(Constants.LOAD_FRAGMENT, Constants.BUSINESS_EVENTLIST_FRAGMENT);
                    }
                    contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Creates an explicit intent for an ResultActivity to receive.

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setLargeIcon(icon)
                                    .setSmallIcon(R.drawable.icon_notify)
                                    .setContentTitle(title)
                                    .setContentIntent(contentIntent)
                                    .setContentText(body)
                                    .setAutoCancel(true);

                    mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setContentIntent(contentIntent);
                    mNotificationManager.notify(requestID, mBuilder.build());

                } else if (type.equalsIgnoreCase("article_details")) {

                    int requestID = (int) System.currentTimeMillis();
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
                        intent = new Intent(getApplicationContext(), VlogsDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(Constants.VIDEO_ID, "" + pushNotificationModel.getId());
                        intent.putExtra(Constants.AUTHOR_ID, "" + pushNotificationModel.getUser_id());
                        intent.putExtra(Constants.FROM_SCREEN, "Notification");
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                        intent.putExtra(Constants.ARTICLE_INDEX, "-1");

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(VlogsDetailActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

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

                } else if (type.equalsIgnoreCase("profile")) {

                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        resultIntent.putExtra("fromNotification", true);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), BloggerProfileActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra("fromNotification", true);
                        resultIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, pushNotificationModel.getUser_id());
                        resultIntent.putExtra(AppConstants.AUTHOR_NAME, "");
                        resultIntent.putExtra(Constants.FROM_SCREEN, "Notification");

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(BloggerProfileActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
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

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setLargeIcon(icon).setSmallIcon(R.drawable.icon_notify)
                            .setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message);

                    mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setContentIntent(contentIntent);
                    mNotificationManager.notify(requestID, mBuilder.build());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
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
