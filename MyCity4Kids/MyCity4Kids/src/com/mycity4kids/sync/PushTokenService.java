package com.mycity4kids.sync;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;

/**
 * Created by kapil.vij on 17-07-2015.
 */
public class PushTokenService extends IntentService implements UpdateListener {

    private final static String TAG = PushTokenService.class.getSimpleName();

    public PushTokenService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("my_service", "My Background Service");
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        Notification notification = builder.setOngoing(true)
                .setSmallIcon(R.drawable.icon_notify)
                .setPriority(NotificationManagerCompat.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE).build();
        startForeground(1, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        return channelId;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getDeviceToken(this))) {
                // hit api
                if (!SharedPrefUtils.getUserDetailModel(this).getId().equals("0")) {
                    hitApiRequest(AppConstants.PUSH_TOKEN_REQUEST);
                }
            }
        }
    }

    private void hitApiRequest(int requestType) {
        ApiHandler handler = new ApiHandler(this, this, requestType);
        handler.execute(getApiUrl(requestType));
    }

    @Override
    public void updateView(String jsonString, int requestType) {
        switch (requestType) {
            case AppConstants.PUSH_TOKEN_REQUEST:
                try {
                    CommonResponse responseData = new Gson().fromJson(jsonString, CommonResponse.class);
                    if (null != responseData && responseData.getResponseCode() == 200) {
                        Log.e("push", "token updated");
                    } else {
                        Log.e("push", "token failed");
                    }
                } catch (JsonSyntaxException jse) {
                    Crashlytics.logException(jse);
                    Log.d("JsonSyntaxException", Log.getStackTraceString(jse));
                }
                break;
        }
    }

    private String getApiUrl(int requestType) {
        StringBuilder builder = new StringBuilder();
        switch (requestType) {
            case AppConstants.PUSH_TOKEN_REQUEST:
                builder.append(AppConstants.UPDATE_PUSH_TOKEN_URL);
                builder.append("userId=").append(SharedPrefUtils.getUserDetailModel(this).getId());
                builder.append("&dynamoId=").append(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
                builder.append("&app_version=").append(AppUtils.getAppVersion(this));
                builder.append("&deviceType=").append("android");
                builder.append("&cityId=").append(SharedPrefUtils.getCurrentCityModel(this).getId());
                builder.append("&pushToken=").append(SharedPrefUtils.getDeviceToken(this));
                builder.append("&fcmToken=").append(SharedPrefUtils.getDeviceToken(this));
                return builder.toString().replace(" ", "%20");
        }
        return builder.toString();
    }
}
