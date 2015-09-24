package com.mycity4kids.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.mycity4kids.ui.activity.SplashActivity;

/**
 * Created by arsh.vardhan on 18-09-2015.
 */
public class InstallReferrerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Uri data = intent.getData();
        Log.d("INSTALL_REFERRER", "Received Data : "+data+"");
//        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            Intent _splashIntent = new Intent(context, SplashActivity.class);
                  _splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  _splashIntent.setAction(action);
                _splashIntent.setData(data);
                context.startActivity(_splashIntent);
//        }
    }
}
