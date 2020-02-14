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

import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.utils.AppUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by kapil.vij on 17-07-2015.
 */
public class PushTokenService extends IntentService {

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
            if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getDeviceToken(BaseApplication.getAppContext()))) {
                // hit api
                if (!SharedPrefUtils.getUserDetailModel(this).getId().equals("0")) {
                    hitApiRequest();
                }
            }
        }
    }

    private void hitApiRequest() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<ResponseBody> call = loginRegistrationAPI.updatePushToken(
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getId(),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                AppUtils.getAppVersion(BaseApplication.getAppContext()), "android",
                SharedPrefUtils.getCurrentCityModel(BaseApplication.getAppContext()).getId(),
                SharedPrefUtils.getDeviceToken(BaseApplication.getAppContext()),
                SharedPrefUtils.getDeviceToken(BaseApplication.getAppContext()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("push", "token updated");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("push", "token failed");
            }
        });
    }
}
