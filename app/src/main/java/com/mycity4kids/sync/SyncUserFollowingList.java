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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.FollowersFollowingResponse;
import com.mycity4kids.models.response.FollowersFollowingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by kapil.vij on 17-07-2015.
 */
public class SyncUserFollowingList extends IntentService {

    private static final String TAG = SyncUserFollowingList.class.getSimpleName();

    public SyncUserFollowingList() {
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
                .setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT)
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
        hitApiRequest();
    }

    private void hitApiRequest() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followListApi = retrofit.create(FollowAPI.class);
        Call<ResponseBody> callFollowingList = followListApi
                .getAllFollowingList(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        callFollowingList.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                    Response<ResponseBody> response) {
                if (response.body() == null) {
                    return;
                }
                try {
                    String resData = new String(response.body().bytes());
                    processFollowersListResponse(resData);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

    private void processFollowersListResponse(String responseData) {
        HashMap<String, String> map = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            int code = jsonObject.getInt("code");
            String status = jsonObject.getString("status");

            if (code == 200 && Constants.SUCCESS.equals(status)) {
                JSONArray datalist = jsonObject.getJSONObject("data").getJSONObject("result").getJSONArray("following");
                for (int i = 0; i < datalist.length(); i++) {
                    map.put(datalist.getString(i), "");
                }
                Gson gsonObj = new Gson();
                String followingJson = gsonObj.toJson(map);
                SharedPrefUtils.setFollowingJson(this, followingJson);
            } else {
                Gson gsonObj = new Gson();
                String followingJson = gsonObj.toJson(map);
                SharedPrefUtils.setFollowingJson(this, followingJson);
            }
        } catch (Exception e) {
            Gson gsonObj = new Gson();
            String followingJson = gsonObj.toJson(map);
            SharedPrefUtils.setFollowingJson(this, followingJson);
        }
    }
}
