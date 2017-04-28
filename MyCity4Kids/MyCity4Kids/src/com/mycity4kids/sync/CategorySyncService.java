package com.mycity4kids.sync;

import android.accounts.NetworkErrorException;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ConfigResponse;
import com.mycity4kids.models.response.UserTypeResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.utils.AppUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    public int userTypeVersion;

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
            //showToast(getString(R.string.error_network));
            return;
        }

        Call<ConfigResponse> call = configAPIs.getConfig();


        //asynchronous call
        call.enqueue(new Callback<ConfigResponse>() {
                         @Override
                         public void onResponse(Call<ConfigResponse> call, retrofit2.Response<ConfigResponse> response) {
                             int statusCode = response.code();
                             final ConfigResponse responseModel = (ConfigResponse) response.body();
                             try {
                                 if (responseModel.getCode() != 200) {
                                     return;
                                 } else {
                                     if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                         for (Map.Entry<String, String> entry : responseModel.getData().getResult().getNotificationSettings().entrySet()) {
                                             SharedPrefUtils.setNotificationConfig(CategorySyncService.this, entry.getKey(), entry.getValue());
                                         }

                                         for (int i = 0; i < responseModel.getData().getResult().getNotificationType().size(); i++) {
                                             SharedPrefUtils.setNotificationType(CategorySyncService.this, "" + i, responseModel.getData().getResult().getNotificationType().get(i));
                                         }

                                         for (Map.Entry<String, String> entry : responseModel.getData().getResult().getLanguage().entrySet()) {
                                             SharedPrefUtils.setLanguageConfig(CategorySyncService.this, entry.getKey(), entry.getValue());
                                         }
                                         boolean status = AppUtils.writeJsonStringToFile(CategorySyncService.this, new Gson().toJson(responseModel.getData().getResult().getLanguages()), AppConstants.LANGUAGES_JSON_FILE);

                                         version = SharedPrefUtils.getConfigCategoryVersion(CategorySyncService.this);
                                         if (version == 0 || version != responseModel.getData().getResult().getCategory().getVersion()) {
                                             location = responseModel.getData().getResult().getCategory().getLocation();
                                             TopicsCategoryAPI categoryAPI = retrofit.create(TopicsCategoryAPI.class);
                                             if (!ConnectivityUtils.isNetworkEnabled(CategorySyncService.this)) {
                                                 //showToast(getString(R.string.error_network));
                                                 return;
                                             }

                                             Call<ResponseBody> caller = categoryAPI.downloadFileWithDynamicUrlSync(location);

                                             caller.enqueue(new Callback<ResponseBody>() {
                                                 @Override
                                                 public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                                     Log.d("TAGA", "server contacted and has file");
                                                     boolean writtenToDisk = writeResponseBodyToDisk(response.body(), AppConstants.CATEGORIES_JSON_FILE);

//                                                     Topics t = new Topics();
//                                                     t.setId("");
//                                                     t.setDisplay_name("");
//                                                     SharedPrefUtils.setMomspressoCategory(CategorySyncService.this, t);

                                                     SharedPrefUtils.setConfigCategoryVersion(CategorySyncService.this, responseModel.getData().getResult().getCategory().getVersion());
                                                     Log.d("TAGA", "file download was a success? " + writtenToDisk);
                                                 }

                                                 @Override
                                                 public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                     Log.e("TAGA", "error");
                                                     Crashlytics.logException(t);
                                                     Log.d("MC4kException", Log.getStackTraceString(t));
                                                 }
                                             });
                                         }

                                         popularVersion = SharedPrefUtils.getConfigPopularCategoryVersion(CategorySyncService.this);
                                         if (popularVersion == 0 || popularVersion != responseModel.getData().getResult().getCategory().getPopularVersion()) {
                                             popularLocation = responseModel.getData().getResult().getCategory().getPopularLocation();
                                             TopicsCategoryAPI categoryAPI = retrofit.create(TopicsCategoryAPI.class);
                                             if (!ConnectivityUtils.isNetworkEnabled(CategorySyncService.this)) {
                                                 //showToast(getString(R.string.error_network));
                                                 return;
                                             }

                                             Call<ResponseBody> caller = categoryAPI.downloadTopicsListForFollowUnfollow(popularLocation);

                                             caller.enqueue(new Callback<ResponseBody>() {
                                                 @Override
                                                 public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                                     Log.d("TAGA", "server contacted and has file");
                                                     boolean writtenToDisk = writeResponseBodyToDisk(response.body(), AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                                                     SharedPrefUtils.setConfigPopularCategoryVersion(CategorySyncService.this,
                                                             responseModel.getData().getResult().getCategory().getPopularVersion());
                                                     Log.d("TAGA", "file download was a success? " + writtenToDisk);
                                                 }

                                                 @Override
                                                 public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                     Log.e("TAGA", "error");
                                                     Crashlytics.logException(t);
                                                     Log.d("MC4kException", Log.getStackTraceString(t));
                                                 }
                                             });
                                         }

                                         userTypeVersion = SharedPrefUtils.getUserTypeVersion(CategorySyncService.this);
                                         if (userTypeVersion == 0 || userTypeVersion != responseModel.getData().getResult().getCategory().getUserTypeVersion()) {
//                                             updateUserTypeMap();
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

    private void updateUserTypeMap() {
        final Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ConfigAPIs configAPIs = retrofit.create(ConfigAPIs.class);
        Call<UserTypeResponse> userTypeCall = configAPIs.getAllUserType();
        userTypeCall.enqueue(userTypeResponseCallback);
    }

    private Callback<UserTypeResponse> userTypeResponseCallback = new Callback<UserTypeResponse>() {
        @Override
        public void onResponse(Call<UserTypeResponse> call, Response<UserTypeResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                UserTypeResponse responseData = (UserTypeResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    for (Map.Entry<String, String> entry : responseData.getData().getResult().entrySet()) {
                        SharedPrefUtils.setConfigUserType(CategorySyncService.this, entry.getValue(), entry.getKey());
                    }
                } else {
//                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserTypeResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private boolean writeResponseBodyToDisk(ResponseBody body, String filename) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("dAWDdawwdawd", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
