package com.mycity4kids.reminders;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import androidx.legacy.content.WakefulBroadcastReceiver;

/**
 * Created by kapil.vij on 29-07-2015.
 */
public class CancelNotificationReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(intent.getIntExtra("notificationId", 0));
    }
}
