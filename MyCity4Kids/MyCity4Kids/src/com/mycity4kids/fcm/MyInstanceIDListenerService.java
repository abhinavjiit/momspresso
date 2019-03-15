package com.mycity4kids.fcm;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.sync.PushTokenService;

/**
 * Created by anshul on 5/26/16.
 */
public class MyInstanceIDListenerService extends FirebaseInstanceIdService {


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.e("Refreshed token", "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        // sendRegistrationToServer(refreshedToken);
        SharedPrefUtils.setDeviceToken(this, refreshedToken);
        Log.e("token in preferences", SharedPrefUtils.getDeviceToken(this));
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
}