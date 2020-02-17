package com.mycity4kids.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.ConfigResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.utils.AppUtils;

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
    protected void onHandleIntent(Intent intent) {

        final Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ConfigAPIs configAPIs = retrofit.create(ConfigAPIs.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            return;
        }

        Call<ConfigResponse> call = configAPIs.getConfig();
        //asynchronous call
        call.enqueue(new Callback<ConfigResponse>() {
                         @Override
                         public void onResponse(Call<ConfigResponse> call, retrofit2.Response<ConfigResponse> response) {
                             final ConfigResponse responseModel = response.body();
                             try {
                                 if (responseModel != null) {
                                     if (responseModel.getCode() != 200) {
                                     } else {
                                         if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                             SharedPrefUtils.setHomeAdSlotUrl(BaseApplication.getAppContext(), responseModel.getData().getResult().getHomeCarouselUrl());

                                             for (Map.Entry<String, String> entry : responseModel.getData().getResult().getNotificationSettings().entrySet()) {
                                                 SharedPrefUtils.setNotificationConfig(BaseApplication.getAppContext(), entry.getKey(), entry.getValue());
                                             }

                                             AppUtils.writeJsonStringToFile(CategorySyncService.this, new Gson().toJson(responseModel.getData().getResult().getLanguages()), AppConstants.LANGUAGES_JSON_FILE);
                                             version = SharedPrefUtils.getConfigCategoryVersion(BaseApplication.getAppContext());
                                             if (version == 0 || version != responseModel.getData().getResult().getCategory().getVersion()) {
                                                 location = responseModel.getData().getResult().getCategory().getLocation();
                                                 TopicsCategoryAPI categoryAPI = retrofit.create(TopicsCategoryAPI.class);
                                                 if (!ConnectivityUtils.isNetworkEnabled(CategorySyncService.this)) {
                                                     return;
                                                 }

                                                 Call<ResponseBody> caller = categoryAPI.downloadTopicsJSON();
                                                 caller.enqueue(new Callback<ResponseBody>() {
                                                     @Override
                                                     public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                                         AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());
                                                         SharedPrefUtils.setConfigCategoryVersion(BaseApplication.getAppContext(), responseModel.getData().getResult().getCategory().getVersion());
                                                     }

                                                     @Override
                                                     public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                         Crashlytics.logException(t);
                                                         Log.d("MC4kException", Log.getStackTraceString(t));
                                                     }
                                                 });
                                             }

                                             popularVersion = SharedPrefUtils.getConfigPopularCategoryVersion(BaseApplication.getAppContext());
                                             if (popularVersion == 0 || popularVersion != responseModel.getData().getResult().getCategory().getPopularVersion()) {
                                                 popularLocation = responseModel.getData().getResult().getCategory().getPopularLocation();
                                                 TopicsCategoryAPI categoryAPI = retrofit.create(TopicsCategoryAPI.class);
                                                 if (!ConnectivityUtils.isNetworkEnabled(CategorySyncService.this)) {
                                                     return;
                                                 }

                                                 Call<ResponseBody> caller = categoryAPI.downloadTopicsListForFollowUnfollow(popularLocation);

                                                 caller.enqueue(new Callback<ResponseBody>() {
                                                     @Override
                                                     public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                                         AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, response.body());
                                                         SharedPrefUtils.setConfigPopularCategoryVersion(BaseApplication.getAppContext(),
                                                                 responseModel.getData().getResult().getCategory().getPopularVersion());
                                                     }

                                                     @Override
                                                     public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                         Log.e("TAGA", "error");
                                                         Crashlytics.logException(t);
                                                         Log.d("MC4kException", Log.getStackTraceString(t));
                                                     }
                                                 });
                                             }

                                         }
                                     }
                                 }
                             } catch (Exception e) {
                                 Crashlytics.logException(e);
                                 Log.d("MC4kException", Log.getStackTraceString(e));
                             }
                         }

                         @Override
                         public void onFailure(Call<ConfigResponse> call, Throwable t) {
                             Crashlytics.logException(t);
                             Log.d("MC4kException", Log.getStackTraceString(t));
                         }
                     }
        );
    }
}
