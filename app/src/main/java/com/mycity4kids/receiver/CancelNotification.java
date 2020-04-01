package com.mycity4kids.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationManagerCompat;

public class CancelNotification extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("notification_id")) {
            int id = intent.getIntExtra("notification_id", 1);
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            notificationManager.cancel(id);

        } else {
            Intent intent1 = new Intent();
            intent1.setAction("Cancel_Video_Uploading_Notification");
            context.sendBroadcast(intent1);
        }

    }
}
