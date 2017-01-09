package com.mycity4kids.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.newmodels.PushNotificationModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.ShareArticleReceiver;
import com.mycity4kids.sync.SyncService;
import com.mycity4kids.sync.SyncUserInfoService;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.ui.activity.NewsLetterWebviewActivity;
import com.mycity4kids.ui.activity.PlanYourWeekActivity;
import com.mycity4kids.ui.activity.SplashActivity;
import com.mycity4kids.ui.activity.VlogsDetailActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
                if (type.equalsIgnoreCase("Article")) {
                    // generate notifications
                    Utils.pushEvent(getApplicationContext(), GTMEventType.BLOG_NOTIFICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId() + "", "");

                    Log.i(TAG, " BANNER TRENDING " + msg);
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);
                    Bitmap remote_picture = null;
                    Bitmap bitmap = null;
                    String url;
                    if (!StringUtils.isNullOrEmpty(pushNotificationModel.getArticle_cover_image_url())) {
                        url = pushNotificationModel.getArticle_cover_image_url();
                        try {
                            remote_picture = BitmapFactory.decodeStream(
                                    (InputStream) new URL(url).getContent());

                            bitmap = getScaledBitmap(remote_picture);
                        } catch (IOException e) {
                            e.printStackTrace();
                            remote_picture = BitmapFactory.decodeResource(getResources(), R.drawable.default_blogger);
                            bitmap = getScaledBitmap(remote_picture);
                        }
                    } else {
                        remote_picture = BitmapFactory.decodeResource(getResources(), R.drawable.default_blogger);
                        bitmap = getScaledBitmap(remote_picture);
                    }

                    int requestID = (int) System.currentTimeMillis();
                    String message = Html.fromHtml(pushNotificationModel.getMessage_id()).toString();
                    String title = Html.fromHtml(pushNotificationModel.getTitle()).toString();

                    Intent intent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        intent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        intent = new Intent(getApplicationContext(), ArticlesAndBlogsDetailsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constants.ARTICLE_ID, "" + pushNotificationModel.getId());
                        intent.putExtra(Constants.AUTHOR_ID, "" + pushNotificationModel.getUser_id());
                        intent.putExtra(Constants.BLOG_SLUG, pushNotificationModel.getBlogPageSlug());
                        intent.putExtra(Constants.TITLE_SLUG, pushNotificationModel.getTitleSlug());

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(ArticlesAndBlogsDetailsActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    NotificationCompat.BigPictureStyle notiStyle = new
                            NotificationCompat.BigPictureStyle();
                    notiStyle.setBigContentTitle(title);
                    notiStyle.setSummaryText(message);
                    notiStyle.bigPicture(bitmap);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setLargeIcon(icon)
                                    .setSmallIcon(R.drawable.icon_notify)
                                    .setContentTitle(title)
                                    .setContentIntent(contentIntent)
                                    .setContentText(message).setStyle(notiStyle);
                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(requestID, mBuilder.build());

                } else if (type.equalsIgnoreCase("Newsletter")) {
                    // generate notifications
                    Utils.pushEvent(getApplicationContext(), GTMEventType.NEWSLETTER_NOTICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId() + "", "");

                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);

                    int requestID = (int) System.currentTimeMillis();

                    String message = pushNotificationModel.getMessage_id();
                    String title = pushNotificationModel.getTitle();
                    NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


                    Intent intent = new Intent(getApplicationContext(), NewsLetterWebviewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                    intent.putExtra(Constants.URL, pushNotificationModel.getUrl());

                    Intent shareIntent = new Intent(this, ShareArticleReceiver.class);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                    shareIntent.putExtra(AppConstants.SHARE_CONTENT, pushNotificationModel.getShare_content());
                    shareIntent.putExtra(AppConstants.SHARE_URL, pushNotificationModel.getUrl());
                    PendingIntent sharePendingIntent = PendingIntent.getBroadcast((BaseApplication) getApplicationContext(), 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setLargeIcon(icon).setSmallIcon(R.drawable.icon_notify).setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message);
                    mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setContentIntent(contentIntent);
                    mBuilder.addAction(R.drawable.share, "Share", sharePendingIntent);
                    mNotificationManager.notify(requestID, mBuilder.build());
                } else if (type.equalsIgnoreCase("event_detail")) {
                    // generate notifications
                    Utils.pushEvent(getApplicationContext(), GTMEventType.EVENT_DETAIL_NOTIFICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId() + "", "");

                    Log.i(TAG, " INSIDE EVENTS DETAILS: " + msg);
                    Bitmap remote_picture = null;
                    Bitmap bitmap = null;
                    String url;
                    if (!StringUtils.isNullOrEmpty(pushNotificationModel.getUrl())) {
                        url = pushNotificationModel.getUrl();
                        try {
                            remote_picture = BitmapFactory.decodeStream(
                                    (InputStream) new URL(url).getContent());

                            bitmap = getScaledBitmap(remote_picture);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        remote_picture = BitmapFactory.decodeResource(getResources(), R.drawable.rateus_bg_);
                        bitmap = getScaledBitmap(remote_picture);
                    }


                    int requestID = (int) System.currentTimeMillis();

                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                    } else {
                        // Creates an explicit intent for an ResultActivity to receive.
                        resultIntent = new Intent(getApplicationContext(), BusinessDetailsActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getApplication()));
                        resultIntent.putExtra(Constants.BUSINESS_OR_EVENT_ID, "" + pushNotificationModel.getId());
                        resultIntent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                        resultIntent.putExtra(Constants.DISTANCE, "0");
                        resultIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                    }
                    contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                    NotificationCompat.BigPictureStyle notiStyle = new
                            NotificationCompat.BigPictureStyle();
                    notiStyle.setBigContentTitle(pushNotificationModel.getTitle());
                    notiStyle.setSummaryText(pushNotificationModel.getMessage_id());
                    notiStyle.bigPicture(bitmap);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle(pushNotificationModel.getTitle())
                                    .setContentIntent(contentIntent)
                                    .setContentText(pushNotificationModel.getMessage_id()).setStyle(notiStyle);
                    // Sets an ID for the notification
                    int mNotificationId = (int) System.currentTimeMillis();
                    ;
                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(requestID, mBuilder.build());

                } else if (type.equalsIgnoreCase("upcoming_event_list")) {
                    Utils.pushEvent(getApplicationContext(), GTMEventType.UPCOMING_EVENTS_NOTIFICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId() + "", "");

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
                    } else {
                        // Creates an explicit intent for an ResultActivity to receive.
                        resultIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                        contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        intent = new Intent(getApplicationContext(), ArticlesAndBlogsDetailsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constants.ARTICLE_ID, "" + pushNotificationModel.getId());
                        intent.putExtra(Constants.AUTHOR_ID, "" + pushNotificationModel.getUser_id());
                        intent.putExtra(Constants.BLOG_SLUG, pushNotificationModel.getBlogPageSlug());
                        intent.putExtra(Constants.TITLE_SLUG, pushNotificationModel.getTitleSlug());

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(ArticlesAndBlogsDetailsActivity.class);
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
                    ;
                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(requestID, mBuilder.build());
                } else if (type.equalsIgnoreCase("video_details")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent intent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        intent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        intent = new Intent(getApplicationContext(), VlogsDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constants.VIDEO_ID, "" + pushNotificationModel.getId());
                        intent.putExtra(Constants.AUTHOR_ID, "" + pushNotificationModel.getUser_id());

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
                    ;
                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(requestID, mBuilder.build());
                } else if (type.equalsIgnoreCase("event_details")) {
                    int requestID = (int) System.currentTimeMillis();
                    Intent resultIntent;
                    PendingIntent contentIntent;
                    if (SharedPrefUtils.getAppUpgrade(this)) {
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), BusinessDetailsActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getApplication()));
                        resultIntent.putExtra(Constants.BUSINESS_OR_EVENT_ID, pushNotificationModel.getId() + "");
                        resultIntent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                        resultIntent.putExtra(Constants.DISTANCE, "0");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(ArticlesAndBlogsDetailsActivity.class);
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
                    ;
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
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), LoadWebViewActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                        contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        resultIntent = new Intent(getApplicationContext(), BloggerDashboardActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, pushNotificationModel.getUser_id());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(BloggerDashboardActivity.class);
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
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);

                    int requestID = (int) System.currentTimeMillis();
                    String message = pushNotificationModel.getMessage_id();
                    String title = pushNotificationModel.getTitle();

                    NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent resultIntent;
                    PendingIntent contentIntent;
                    resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
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
