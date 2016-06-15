package com.mycity4kids.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
/*import com.google.android.gms.gcm.GoogleCloudMessaging;*/
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.listener.OnGcmTokenReceived;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.sync.PushTokenService;

import java.io.IOException;

/**
 * Created by kapil.vij on 16-07-2015.
 */

public class GCMUtil {

//    private static final String TAG = GCMUtil.class.getSimpleName();
//
//    public static final String EXTRA_MESSAGE = "message";
//    public static final String PROPERTY_REG_ID = "registration_id";
//    private static final String PROPERTY_APP_VERSION = "appVersion";
//    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    private final static String SENDER_ID = "3577377239";
//    private static GoogleCloudMessaging gcm;
//    private static String regid;
//    private static OnGcmTokenReceived onGcmListener;
//
//    public static void initializeGCM(final Activity context) {
//        // onGcmListener = onGcmTokenReceiveListener;
//        if (checkPlayServices(context)) {
//            gcm = GoogleCloudMessaging.getInstance(context);
//            regid = getRegistrationId(context);
//
//            if (regid.isEmpty()) {
////                new Thread(new Runnable() {
////                    @Override
////                    public void run() {
////                        if (StringUtils.isNullOrEmpty(SharedPrefUtils.getDeviceToken(context)))
////                            registerInBackground(context);
////                    }
////                }).start();
//                if (StringUtils.isNullOrEmpty(SharedPrefUtils.getDeviceToken(context)))
//                    registerInBackground(context);
//            } else {
//                //onGcmListener.onGcmTokenReceive(regid);
//                // call broadcast here too
//                //Intent intent = new Intent(Constants.LOCAL_BROADCAST_GCM);
//                //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//
//                Intent intent = new Intent(context, PushTokenService.class);
//                context.startService(intent);
//
//            }
//        } else {
//            Log.i(TAG, "No valid Google Play Services APK found.");
//        }
//    }
//
//    private static void registerInBackground(final Context context) {
//
//        try {
//            new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground(Void... params) {
//                    String msg = "";
//                    try {
//                        if (gcm == null) {
//                            gcm = GoogleCloudMessaging.getInstance(context);
//                        }
//                        regid = gcm.register(SENDER_ID);
//
//                        if (StringUtils.isNullOrEmpty(regid)) {
//                            while (StringUtils.isNullOrEmpty(regid)) {
//                                regid = gcm.register(SENDER_ID);
//                            }
//                        }
//
//                        msg = "Device registered, registration ID=" + regid;
//
//                        SharedPrefUtils.setDeviceToken(context, regid);
//
//                        // call broadcast
//                        Intent intent = new Intent(context, PushTokenService.class);
//                        context.startService(intent);
//
//                        //Intent intent = new Intent(Constants.LOCAL_BROADCAST_GCM);
//                        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//
//                    } catch (IOException ex) {
//                        msg = "Error :" + ex.getMessage();
//                    }
//                    return msg;
//                }
//
//                @Override
//                protected void onPostExecute(String msg) {
//                    // mDisplay.append(msg + "\n");
//                    System.out.println("msg>>>>" + msg);
//                }
//            }.execute(null, null, null);
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//
////        try {
////            if (gcm == null) {
////                gcm = GoogleCloudMessaging.getInstance(context);
////            }
////            regid = gcm.register(SENDER_ID);
////            msg = "Device registered, registration ID=" + regid;
////            SharedPrefUtils.setDeviceToken(context, regid);
////            onGcmListener.onGcmTokenReceive(regid);
////        } catch (IOException ex) {
////            msg = "Error :" + ex.getMessage();
////            // If there is an error, don't just keep trying to register.
////            // Require the user to click a button again, or perform
////            // exponential back-off.
////        }
////        Log.d(TAG, msg);
////        System.out.println(msg);
//    }
//
//    private static String getRegistrationId(Context context) {
//        String registrationId = SharedPrefUtils.getDeviceToken(context);
//        if (registrationId.isEmpty()) {
//            Log.i(TAG, "Registration not found.");
//            return "";
//        }
//        // Check if app was updated; if so, it must clear the registration ID
//        // since the existing regID is not guaranteed to work with the new
//        // app version.
////        int registeredVersion = SharedPrefUtils.getAppVersion(context);
////        int currentVersion = getAppVersion(context);
////        if (registeredVersion != currentVersion) {
////            Log.i(TAG, "App version changed.");
////            return "";
////        }
//        return registrationId;
//    }
//
//    private static int getAppVersion(Context context) {
//        try {
//            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.mycity4kids", PackageManager.GET_META_DATA);
//            return packageInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//
//    /**
//     * Check the device to make sure it has the Google Play Services APK. If it
//     * doesn't, display a dialog that allows users to download the APK from the
//     * Google Play Store or enable it in the device's system settings.
//     */
//    private static boolean checkPlayServices(Activity context) {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Log.i(TAG, "This device is not supported.");
////                finish();
//            }
//            return false;
//        }
//        return true;
//    }
}
