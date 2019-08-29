package com.mycity4kids.fcm;

/**
 * Created by anshul on 5/26/16.
 */
public class MyInstanceIDListenerService /*extends FirebaseInstanceIdService*/ {


//    @Override
//    public void onTokenRefresh() {
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        SharedPrefUtils.setDeviceToken(this, refreshedToken);
//        Log.e("token in preferences", SharedPrefUtils.getDeviceToken(this));
//        Intent intent = new Intent(this, PushTokenService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            try {
//                startForegroundService(intent);
//            } catch (IllegalArgumentException e) {
//                startService(intent);
//            }
//        } else {
//            startService(intent);
//        }
//    }
}