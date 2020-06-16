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
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.ConfigResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by anshul on 7/12/16.
 */
public class CategorySyncService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public int version;
    public String location;

    public int popularVersion;
    public String popularLocation;

    public CategorySyncService(String name) {
        super(name);
    }

    public CategorySyncService() {
        super("CategorySyncService");
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
        final Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ConfigAPIs configApi = retrofit.create(ConfigAPIs.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            return;
        }

        Call<ConfigResponse> call = configApi.getConfig();
        call.enqueue(
                new Callback<ConfigResponse>() {
                    @Override
                    public void onResponse(Call<ConfigResponse> call, retrofit2.Response<ConfigResponse> response) {
                        final ConfigResponse responseModel = response.body();
                        try {
                            if (responseModel != null) {
                                if (responseModel.getCode() == 200) {
                                    if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                        SharedPrefUtils.setHomeAdSlotUrl(BaseApplication.getAppContext(),
                                                responseModel.getData().getResult().getHomeCarouselUrl());

                                        for (Map.Entry<String, String> entry : responseModel.getData().getResult()
                                                .getNotificationSettings().entrySet()) {
                                            SharedPrefUtils
                                                    .setNotificationConfig(BaseApplication.getAppContext(),
                                                            entry.getKey(),
                                                            entry.getValue());
                                        }

                                        AppUtils.writeJsonStringToFile(CategorySyncService.this,
                                                new Gson().toJson(responseModel.getData().getResult().getLanguages()),
                                                AppConstants.LANGUAGES_JSON_FILE);
                                        version = SharedPrefUtils
                                                .getConfigCategoryVersion(BaseApplication.getAppContext());
                                        if (version == 0 || version != responseModel.getData().getResult().getCategory()
                                                .getVersion()) {
                                            location = responseModel.getData().getResult().getCategory().getLocation();
                                            TopicsCategoryAPI categoryApi = retrofit.create(TopicsCategoryAPI.class);
                                            if (!ConnectivityUtils.isNetworkEnabled(CategorySyncService.this)) {
                                                return;
                                            }

                                            Call<ResponseBody> caller = categoryApi.downloadTopicsJSON();
                                            caller.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call,
                                                        retrofit2.Response<ResponseBody> response) {
                                                    AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(),
                                                            AppConstants.CATEGORIES_JSON_FILE, response.body());
                                                    SharedPrefUtils
                                                            .setConfigCategoryVersion(BaseApplication.getAppContext(),
                                                                    responseModel.getData().getResult().getCategory()
                                                                            .getVersion());
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    FirebaseCrashlytics.getInstance().recordException(t);
                                                    Log.d("MC4kException", Log.getStackTraceString(t));
                                                }
                                            });
                                        }

                                        popularVersion = SharedPrefUtils
                                                .getConfigPopularCategoryVersion(BaseApplication.getAppContext());
                                        if (popularVersion == 0 || popularVersion != responseModel.getData().getResult()
                                                .getCategory().getPopularVersion()) {
                                            popularLocation = responseModel.getData().getResult().getCategory()
                                                    .getPopularLocation();
                                            TopicsCategoryAPI categoryApi = retrofit.create(TopicsCategoryAPI.class);
                                            if (!ConnectivityUtils.isNetworkEnabled(CategorySyncService.this)) {
                                                return;
                                            }

                                            Call<ResponseBody> caller = categoryApi
                                                    .downloadTopicsListForFollowUnfollow(popularLocation);

                                            caller.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call,
                                                        retrofit2.Response<ResponseBody> response) {
                                                    AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(),
                                                            AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE,
                                                            response.body());
                                                    SharedPrefUtils
                                                            .setConfigPopularCategoryVersion(
                                                                    BaseApplication.getAppContext(),
                                                                    responseModel.getData().getResult().getCategory()
                                                                            .getPopularVersion());
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Log.e("TAGA", "error");
                                                    FirebaseCrashlytics.getInstance().recordException(t);
                                                    Log.d("MC4kException", Log.getStackTraceString(t));
                                                }
                                            });
                                        }

                                    }
                                }
                            }
                        } catch (Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            Log.d("MC4kException", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onFailure(Call<ConfigResponse> call, Throwable t) {
                        FirebaseCrashlytics.getInstance().recordException(t);
                        Log.d("MC4kException", Log.getStackTraceString(t));
                    }
                }
        );
    }
}
