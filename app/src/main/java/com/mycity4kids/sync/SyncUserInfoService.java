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
import com.google.gson.reflect.TypeToken;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by manish.soni on 04-08-2015.
 */
public class SyncUserInfoService extends IntentService {

    private static final String TAG = SyncUserInfoService.class.getSimpleName();

    public SyncUserInfoService() {
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
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            hitApiRequest();
        }
    }

    private void hitApiRequest() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI usersApi = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = usersApi.getUserDetails(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(onLoginResponseReceivedListener);
    }

    Callback<UserDetailResponse> onLoginResponseReceivedListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            if (response.body() == null) {
                return;
            }

            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    SharedPrefUtils.setProfileImgUrl(BaseApplication.getAppContext(),
                            responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    UserInfo userInfo = SharedPrefUtils.getUserDetailModel(SyncUserInfoService.this);
                    userInfo.setIsLangSelection(responseData.getData().get(0).getResult().getIsLangSelection());
                    userInfo.setFirst_name(responseData.getData().get(0).getResult().getFirstName());
                    userInfo.setLast_name(responseData.getData().get(0).getResult().getLastName());
                    userInfo.setProfilePicUrl(
                            responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    userInfo.setSubscriptionEmail(responseData.getData().get(0).getResult().getSubscriptionEmail());
                    userInfo.setVideoPreferredLanguages(
                            responseData.getData().get(0).getResult().getVideoPreferredLanguages());
                    SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), userInfo);
                    processFollowingTopicListResponse(responseData.getData().get(0).getResult().getFollowCategories());

                    FileInputStream fileInputStream = openFileInput(AppConstants.LANGUAGES_JSON_FILE);
                    String fileContent = AppUtils.convertStreamToString(fileInputStream);
                    LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                            fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                            }.getType()
                    );
                    Map<String, String> subscribedContentLanguages = responseData.getData().get(0).getResult()
                            .getLangSubscription();
                    String filter = "";
                    for (Map.Entry<String, String> entry : subscribedContentLanguages.entrySet()) {
                        if ("1".equals(entry.getValue())) {
                            for (Map.Entry<String, LanguageConfigModel> langEntry : retMap.entrySet()) {
                                if (entry.getKey().equals("english")) {
                                    if (StringUtils.isNullOrEmpty(filter)) {
                                        filter = "0";
                                        break;
                                    } else {
                                        filter = filter + "," + "0";
                                        break;
                                    }
                                } else {
                                    if (entry.getKey().equals(langEntry.getValue().getName().toLowerCase())) {
                                        if (StringUtils.isNullOrEmpty(filter)) {
                                            filter = langEntry.getKey();
                                        } else {
                                            filter = filter + "," + langEntry.getKey();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    SharedPrefUtils.setLanguageFilters(BaseApplication.getAppContext(), filter);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            FirebaseCrashlytics.getInstance().recordException(t);
        }
    };

    private void processFollowingTopicListResponse(ArrayList<String> responseData) {
        HashMap<String, String> map = new HashMap<>();
        try {
            if (responseData != null) {
                for (int i = 0; i < responseData.size(); i++) {
                    if (responseData.get(i) != null && !responseData.get(i).trim().isEmpty()) {
                        map.put(responseData.get(i), "");
                    }
                }
                Gson gsonObj = new Gson();
                String followingJson = gsonObj.toJson(map);
                Log.e("followedTopics", followingJson);
                SharedPrefUtils.setFollowingTopicsJson(this, followingJson);
            } else {
                Gson gsonObj = new Gson();
                String followingJson = gsonObj.toJson(map);
                Log.e("followedTopics", followingJson);
                SharedPrefUtils.setFollowingTopicsJson(this, followingJson);
            }
        } catch (Exception e) {
            Gson gsonObj = new Gson();
            String followingJson = gsonObj.toJson(map);
            Log.e("followedTopics", followingJson);
            SharedPrefUtils.setFollowingTopicsJson(this, followingJson);
        }
    }
}
