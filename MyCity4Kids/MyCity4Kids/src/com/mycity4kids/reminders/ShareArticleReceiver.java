package com.mycity4kids.reminders;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.mycity4kids.constants.AppConstants;


public class ShareArticleReceiver extends WakefulBroadcastReceiver {

    private Uri bitmapUri;
    private int id;
    private boolean isAppoitment;

    @Override
    public void onReceive(final Context context, Intent intent) {

        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(intent.getIntExtra(AppConstants.NOTIFICATION_ID, 0));
        closeStatusBar(context);

        String shareContent = intent.getStringExtra(AppConstants.SHARE_CONTENT);
        String shareUrl = intent.getStringExtra(AppConstants.SHARE_URL);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String type = "text/plain";

        sendIntent.setType(type);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "mycity4kids \n" + shareContent + "\nRead Here: " + shareUrl);
        Intent byintent = Intent.createChooser(sendIntent, "Share");
        byintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(byintent);


    }

    private void closeStatusBar(Context context) {
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }
}
