package com.mycity4kids.reminders;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.sync.RemainderEmailService;
import com.mycity4kids.ui.activity.ActivityEditAppointment;
import com.mycity4kids.ui.activity.ActivityEditTask;
import com.mycity4kids.ui.activity.ActivityShowAppointment;
import com.mycity4kids.ui.activity.ActivityShowTask;
import com.mycity4kids.ui.activity.DashboardActivity;

import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * Created by kapil.vij on 08-07-2015.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    //public static final int APPOINTMENT_NOTIFICATION_ID = 11235;
   // public static final int TASK_NOTIFICATION_ID = 11234;

    @Override
    public void onReceive(Context context, Intent intent) {

        long startTimeMillis = intent.getLongExtra(Constants.EXTRA_ALARM_START_MILLIS, 0);
        String reminderBefore = intent.getStringExtra(Constants.EXTRA_ALARM_REMIND_BEFORE);
        String recurring = intent.getStringExtra(Constants.EXTRA_ALARM_RECURRING);
        String repeat = intent.getStringExtra(Constants.EXTRA_ALARM_REPEAT);
        String repeatFrequency = intent.getStringExtra(Constants.EXTRA_ALARM_REPEAT_FREQ);
        String repeatNum = intent.getStringExtra(Constants.EXTRA_ALARM_REPEAT_NUM);
        String repeatUntill = intent.getStringExtra(Constants.EXTRA_ALARM_REPEAT_UNTILL);
        int reminderId = intent.getIntExtra(Constants.EXTRA_ALARM_ID, 0);
        String appointmentName = intent.getStringExtra(Constants.EXTRA_ALARM_DESC);
        int reminderType = intent.getIntExtra(Constants.EXTRA_ALARM_TYPE, Constants.REMINDER_TYPE_APPOINTMENT);

        Reminder.with(context).info(reminderType, appointmentName)
                .startTime(startTimeMillis)
                .setRepeatBehavior(repeat, repeatUntill, repeatFrequency, repeatNum)
                .remindBefore(reminderBefore)
                .setRecurring(recurring)
                .create(reminderId);


        String notificationPrefs = SharedPrefUtils.getNotificationPrefrence(context, reminderType == Constants.REMINDER_TYPE_APPOINTMENT);
        if (notificationPrefs == AppConstants.NOTIFICATION_PREF_NONE) {
            return;
        } else {
            if (notificationPrefs == AppConstants.NOTIFICATION_PREF_BOTH || notificationPrefs == AppConstants.NOTIFICATION_PREF_EMAIL) {
                Intent intentS = new Intent(context, RemainderEmailService.class);
                intentS.putExtra(Constants.REMAINDER_TYPE, reminderType);
                intentS.putExtra(Constants.REMAINDER_ID, reminderId);
                context.startService(intentS);
            }

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Class showActvity;
            Class editActivity;
            String keyForId;
            String reminderHeader;
            if (reminderType == Constants.REMINDER_TYPE_APPOINTMENT) {
                showActvity = ActivityShowAppointment.class;
                editActivity = ActivityEditAppointment.class;
                keyForId = AppConstants.EXTRA_APPOINTMENT_ID;
                reminderHeader = "Appointment";

                Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_launcher);

                int requestID = (int) System.currentTimeMillis();

                Intent actionIntent = new Intent(context, showActvity);
                actionIntent.putExtra(keyForId, reminderId);
                actionIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent contentIntent = PendingIntent.getActivity(context, getUniqueRequestCode(), actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setLargeIcon(icon).setSmallIcon(R.drawable.iconnotify).setContentTitle(reminderHeader + " at " + new SimpleDateFormat("h:mm aa").format(startTimeMillis)).setStyle(new NotificationCompat.BigTextStyle().bigText(appointmentName)).setContentText(appointmentName);
                mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
                mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

                Intent editActionIntent = new Intent(context, editActivity);
                editActionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                editActionIntent.putExtra(keyForId, reminderId);
                editActionIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                PendingIntent editIntent = PendingIntent.getActivity(context, getUniqueRequestCode(), editActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Intent shareIntent = shareIntent(reminderId);
                //PendingIntent sharePendingIntent = PendingIntent.getActivity(context, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent shareIntent = new Intent(context, ShareNotificationReceiver.class);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                shareIntent.putExtra(AppConstants.EXTRA_ID, reminderId);
                shareIntent.putExtra(AppConstants.IS_APPOINTMENT, true);
                PendingIntent sharePendingIntent = PendingIntent.getBroadcast(context, getUniqueRequestCode(), shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                mBuilder.addAction(R.drawable.edit_xxhdpi, "Edit", editIntent);
                mBuilder.addAction(R.drawable.share, "Share", sharePendingIntent);
                mBuilder.setAutoCancel(true);
                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(requestID, mBuilder.build());


                /////////////////////////////////////


            } else {
                showActvity = ActivityShowTask.class;
                editActivity = ActivityEditTask.class;
                keyForId = AppConstants.EXTRA_TASK_ID;
                reminderHeader = "Task";

                Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_launcher);


                int requestID = (int) System.currentTimeMillis();

                Intent actionIntent = new Intent(context, showActvity);
                actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                actionIntent.putExtra(keyForId, reminderId);
                actionIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);

                PendingIntent contentIntent = PendingIntent.getActivity(context, getUniqueRequestCode(), actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setLargeIcon(icon).setSmallIcon(R.drawable.iconnotify).setContentTitle(reminderHeader + " at " + new SimpleDateFormat("h:mm aa").format(startTimeMillis)).setStyle(new NotificationCompat.BigTextStyle().bigText(appointmentName)).setContentText(appointmentName);
                mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
                mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

                Intent editActionIntent = new Intent(context, editActivity);
                editActionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                editActionIntent.putExtra(keyForId, reminderId);
                editActionIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                PendingIntent editIntent = PendingIntent.getActivity(context, getUniqueRequestCode(), editActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent taskcompleteIntent = new Intent(context, DashboardActivity.class);
                taskcompleteIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                taskcompleteIntent.putExtra(AppConstants.EXTRA_TASK_ID, reminderId);
                taskcompleteIntent.putExtra(AppConstants.IS_RECURRING, recurring);

                PendingIntent finishPendingIntent = PendingIntent.getActivity(context, getUniqueRequestCode(), taskcompleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.addAction(R.drawable.edit_xxhdpi, "Edit", editIntent);
                mBuilder.addAction(R.drawable.check, "Finish", finishPendingIntent);
                mBuilder.setAutoCancel(true);
                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(requestID, mBuilder.build());


            }
        }
    }

    private int getUniqueRequestCode(){
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(1000);
    }

    }
