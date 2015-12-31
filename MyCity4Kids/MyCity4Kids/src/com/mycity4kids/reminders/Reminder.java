package com.mycity4kids.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.Constants;

/**
 * Created by kapil.vij on 09-07-2015.
 */
public class Reminder {

    private static Context mContext;
    private static Reminder reminder;
    private int reminderType;
    private String reminderDesc;
    private int remindBeforeMins;
    private boolean isRecurring = false;
    private String repeatFrequency;
    private String repeatNum;
    private String repeatUntill;
    private String repeat;
    private long startTimeMillis;
    private String remindBefore;

    private Reminder() {
        // do nothing
        reminderType = Constants.REMINDER_TYPE_APPOINTMENT;
        reminderDesc = "";
        remindBeforeMins = 0;
        isRecurring = false;
        repeatFrequency = "";
        repeatNum = "";
        repeatUntill = "";
        repeat = "";
        startTimeMillis = 0;
        remindBefore = "";
    }

    public static Reminder with(Context context) {
        mContext = context;
        reminder = new Reminder();
        return reminder;
    }

    public Reminder info(int reminderType, String reminderDesc) {
        this.reminderType = reminderType;
        this.reminderDesc = reminderDesc;
        return reminder;
    }

    public Reminder setRepeatBehavior(String repeat, String repeatUntill, String repeatFrequency, String repeatNum) {
        this.repeat = repeat;
        this.repeatUntill = repeatUntill;
        this.repeatFrequency = repeatFrequency;
        this.repeatNum = repeatNum;
        return reminder;
    }

    public Reminder remindBefore(String reminBefore) {
        this.remindBefore = reminBefore;
        if (!StringUtils.isNullOrEmpty(reminBefore)) {
            remindBeforeMins = Integer.parseInt(reminBefore);
        }
        return reminder;
    }

    public Reminder setRecurring(String recurring) {
        isRecurring = false;
        if (!StringUtils.isNullOrEmpty(recurring) && recurring.equalsIgnoreCase("yes")) {
            isRecurring = true;
        }
        return reminder;
    }

    public void create(int reminderId) {
//        long reminderTriggerMilles = ReminderUtils.calculateTimeDifference(startTimeMillis, remindBefore);
        long reminderTriggerMilles = startTimeMillis;
        if (!StringUtils.isNullOrEmpty(remindBefore)) {
            reminderTriggerMilles = ReminderUtils.calculateTimeDifference(startTimeMillis, remindBefore);
        }

        if (System.currentTimeMillis() > reminderTriggerMilles || (isRecurring && repeat.equalsIgnoreCase("Days"))) {
            if (isRecurring) {
                reminderTriggerMilles = ReminderUtils.calculateNextReminderTime(reminderType, startTimeMillis, remindBefore, repeat, repeatFrequency, repeatNum, repeatUntill);
            } else {
                reminderTriggerMilles = 0;
            }
        }
        Log.i("Reminder", "reminderTriggerMilles -------------- " + reminderTriggerMilles);
        if (reminderTriggerMilles == 0) {
            return;
        }

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(mContext, AlarmReceiver.class);
        i.putExtra(Constants.EXTRA_ALARM_TYPE, reminderType);
        i.putExtra(Constants.EXTRA_ALARM_DESC, reminderDesc);
        i.putExtra(Constants.EXTRA_ALARM_RECURRING, isRecurring ? "yes" : "no");
        i.putExtra(Constants.EXTRA_ALARM_START_MILLIS, startTimeMillis);

        if (!StringUtils.isNullOrEmpty(repeat)) {
            i.putExtra(Constants.EXTRA_ALARM_REPEAT, repeat);
        }
        if (!StringUtils.isNullOrEmpty(repeatFrequency)) {
            i.putExtra(Constants.EXTRA_ALARM_REPEAT_FREQ, repeatFrequency);
        }
        if (!StringUtils.isNullOrEmpty(repeatNum)) {
            i.putExtra(Constants.EXTRA_ALARM_REPEAT_NUM, repeatNum);
        }
        if (!StringUtils.isNullOrEmpty(repeatUntill)) {
            i.putExtra(Constants.EXTRA_ALARM_REPEAT_UNTILL, repeatUntill);
        }
        i.putExtra(Constants.EXTRA_ALARM_ID, reminderId);


        PendingIntent pi = PendingIntent.getBroadcast(mContext, reminderId, i, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, reminderTriggerMilles, pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, reminderTriggerMilles, pi);
        }
    }

    public void cancel(int reminderId) {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, reminderId, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
    }

    public Reminder startTime(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
        return reminder;
    }
}