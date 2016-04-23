package com.mycity4kids.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.newmodels.PushNotificationModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.ShareArticleReceiver;
import com.mycity4kids.sync.SyncService;
import com.mycity4kids.sync.SyncUserInfoService;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.NewsLetterWebviewActivity;
import com.mycity4kids.ui.activity.PlanYourWeekActivity;
import com.mycity4kids.ui.activity.SplashActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class
        GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 11232;
    private static final String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null) {
            if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                    Log.i(TAG, "Send error: " + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                    Log.i(TAG, "Deleted messages on server: " + extras.toString());
                    // If it's a regular GCM message, do some work.
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    // This loop represents the service doing some work.
                    // Post notification of received message.
                    sendNotification(extras.getString("message"));
                    Log.i(TAG, "Received: " + extras.toString());
                }
            }
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        if (StringUtils.isNullOrEmpty(msg)) {
            return;
        }

        try {
            PushNotificationModel pushNotificationModel = new Gson().fromJson(msg, PushNotificationModel.class);
            if (pushNotificationModel != null) {

                String type = pushNotificationModel.getType();
                if (type.equalsIgnoreCase("Appointment")) {

                    if (!(pushNotificationModel.getUser_id() == SharedPrefUtils.getUserDetailModel(this).getId())) {
                        Utils.pushEvent(getApplicationContext(), GTMEventType.APPOINTMENT_NOTIFICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId() + "", "");
                        Intent intent = new Intent(this, SyncService.class);
                        intent.putExtra(Constants.PUSH_MODEL, pushNotificationModel);
                        intent.putExtra("isAppointmentFlag", true);
                        intent.putExtra("callTask", false);
                        startService(intent);
                    }
                } else if (type.equalsIgnoreCase("task")) {
                    if (!(pushNotificationModel.getUser_id() == SharedPrefUtils.getUserDetailModel(this).getId())) {
                        Utils.pushEvent(getApplicationContext(), GTMEventType.TASK_NOTIFICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId() + "", "");
                        Intent intent = new Intent(this, SyncService.class);
                        intent.putExtra(Constants.PUSH_MODEL, pushNotificationModel);
                        intent.putExtra("isAppointmentFlag", false);
                        intent.putExtra("callTask", false);
                        startService(intent);
                    }
                } else if (type.equalsIgnoreCase("family")) {
                    // update family too
                    if (!(pushNotificationModel.getUser_id() == SharedPrefUtils.getUserDetailModel(this).getId())) {
                        Utils.pushEvent(getApplicationContext(), GTMEventType.FAMILY_NOTICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId() + "", "");

                        Intent intent = new Intent(this, SyncUserInfoService.class);
                        intent.putExtra(Constants.PUSH_MODEL, pushNotificationModel);
                        startService(intent);
                    }
                } else if (type.equalsIgnoreCase("Article")) {
                    // generate notifications
                    Utils.pushEvent(getApplicationContext(), GTMEventType.BLOG_NOTIFICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId() + "", "");

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

                    NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent intent = new Intent(getApplicationContext(), ArticlesAndBlogsDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.ARTICLE_ID, "" + pushNotificationModel.getId());
                    intent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    intent.putExtra(Constants.BLOG_NAME, pushNotificationModel.getBlog_name());
                    if (pushNotificationModel.getFilter_type().trim().equals("Blogger")) {
                        intent.putExtra(Constants.FILTER_TYPE, "blogs");
                    } else {
                        intent.putExtra(Constants.FILTER_TYPE, "authors");
                    }

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    // Adds the back stack
                    stackBuilder.addParentStack(ArticlesAndBlogsDetailsActivity.class);
                    // Adds the Intent to the top of the stack
                    stackBuilder.addNextIntent(intent);

                    //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.BigPictureStyle notiStyle = new
                            NotificationCompat.BigPictureStyle();
                    notiStyle.setBigContentTitle(title);
                    notiStyle.setSummaryText(message);
                    notiStyle.bigPicture(bitmap);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setLargeIcon(icon)
                                    .setSmallIcon(R.drawable.iconnotify)
                                    .setContentTitle(title)
                                    .setContentIntent(contentIntent)
                                    .setContentText(message).setStyle(notiStyle);
                    ;
                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(requestID, mBuilder.build());

                } else if (type.equalsIgnoreCase("Newsletter")) {
                    // generate notifications
                    Utils.pushEvent(getApplicationContext(), GTMEventType.NEWSLETTER_NOTICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId() + "", "");

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
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setLargeIcon(icon).setSmallIcon(R.drawable.iconnotify).setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message);
                    mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setContentIntent(contentIntent);
                    mBuilder.addAction(R.drawable.share, "Share", sharePendingIntent);
                    mNotificationManager.notify(requestID, mBuilder.build());
                } else if (type.equalsIgnoreCase("weekly_calendar_todo")) {
                    int requestID = 2;
                    Utils.pushEvent(getApplicationContext(), GTMEventType.WEEKLY_CALENDAR_NOTIFICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId() + "", "");

                    pushNotificationModel.getTodo_items();
                    String calendarBtnText, todoBtnText;
                    int plus_calendar_image;

                    if (Integer.parseInt(pushNotificationModel.getCalendar_items()) < 1) {
                        calendarBtnText = "Appointment";
                        plus_calendar_image = R.drawable.attachment;
                    } else {
                        calendarBtnText = "Calendar(" + pushNotificationModel.getCalendar_items() + ")";
                        plus_calendar_image = R.drawable.calendar_new;
                    }
                    int plus_todo_image;

                    if (Integer.parseInt(pushNotificationModel.getTodo_items()) < 1) {
                        todoBtnText = "Tasks";
                        plus_todo_image = R.drawable.attachment;
                    } else {
                        todoBtnText = "Tasks(" + pushNotificationModel.getTodo_items() + ")";
                        plus_todo_image = R.drawable.todo;
                    }

                    NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent actionIntent = new Intent(this, DashboardActivity.class);
                    actionIntent.setAction("Show");
                    actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    actionIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Intent to_dointent = new Intent(this, DashboardActivity.class);
                    to_dointent.putExtra("load_fragment", "fragment_todo");
                    to_dointent.setAction("todo");
                    to_dointent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent todoIntent = PendingIntent.getActivity(this, 0, to_dointent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Intent calintent = new Intent(this, DashboardActivity.class);
                    calintent.setAction("Cal");
                    calintent.putExtra("load_fragment", "fragment_calendar");
                    calintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent calendarIntent = PendingIntent.getActivity(this, 0, calintent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(pushNotificationModel.getTitle())
                            .setContentText(pushNotificationModel.getMessage_id());

                    mBuilder.addAction(plus_calendar_image, calendarBtnText, calendarIntent);
                    mBuilder.addAction(plus_todo_image, todoBtnText, todoIntent);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setContentIntent(contentIntent);

                    mNotificationManager.notify(requestID, mBuilder.build());

                } else if (type.equalsIgnoreCase("event_detail")) {
                    // generate notifications
                    Utils.pushEvent(getApplicationContext(), GTMEventType.EVENT_DETAIL_NOTIFICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId() + "", "");

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

                    // Creates an explicit intent for an ResultActivity to receive.
                    Intent resultIntent = new Intent(getApplicationContext(), BusinessDetailsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    resultIntent.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getApplication()));
                    resultIntent.putExtra(Constants.BUSINESS_OR_EVENT_ID, pushNotificationModel.getId());
                    resultIntent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                    resultIntent.putExtra(Constants.DISTANCE, "0");
                    resultIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);

                    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
                    Utils.pushEvent(getApplicationContext(), GTMEventType.UPCOMING_EVENTS_NOTIFICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId() + "", "");

                    Log.i(TAG, " INSIDE EVENTS LIST: " + msg);
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);

                    int requestID = (int) System.currentTimeMillis();
                    String message = pushNotificationModel.getMessage_id();
                    String title = pushNotificationModel.getTitle();

                    NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                    // Creates an explicit intent for an ResultActivity to receive.
                    Intent resultIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    resultIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                    resultIntent.putExtra(Constants.LOAD_FRAGMENT, Constants.BUSINESS_EVENTLIST_FRAGMENT);

                    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setLargeIcon(icon).setSmallIcon(R.drawable.iconnotify)
                            .setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message);

                    mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setContentIntent(contentIntent);
                    mNotificationManager.notify(requestID, mBuilder.build());

                } else if (type.equalsIgnoreCase("plan_week")) {
                    Utils.pushEvent(getApplicationContext(), GTMEventType.PLAN_WEEK_NOTIFICATION_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId() + "", "");

                    // generate notifications
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);

                    int requestID = (int) System.currentTimeMillis();
                    String message = pushNotificationModel.getMessage_id();
                    String title = pushNotificationModel.getTitle();
                    Intent cIntent = null;
                    PendingIntent contentIntent;
                    NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    TableAdult _table = new TableAdult(BaseApplication.getInstance());
                    if (_table.getAdultCount() > 0) { // if he signup
                        cIntent = new Intent(getApplicationContext(), PlanYourWeekActivity.class);
                        cIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack
                        stackBuilder.addParentStack(PlanYourWeekActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(cIntent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        cIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        contentIntent = PendingIntent.getActivity(this, 0, cIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
//                    message = "Plan you week.";
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setLargeIcon(icon).setSmallIcon(R.drawable.iconnotify)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setContentText(message);

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
