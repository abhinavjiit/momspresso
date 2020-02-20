package com.mycity4kids.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.utils.AppUtils;

import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by manish.soni on 04-08-2015.
 */
public class SyncUserInfoService extends IntentService {

    private final static String TAG = SyncUserInfoService.class.getSimpleName();

    public SyncUserInfoService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            hitApiRequest();
        }
    }

    private void hitApiRequest() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI usersAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = usersAPI.getUserDetails(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
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
                    SharedPrefUtils.setProfileImgUrl(BaseApplication.getAppContext(), responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    UserInfo userInfo = SharedPrefUtils.getUserDetailModel(SyncUserInfoService.this);
                    userInfo.setIsLangSelection(responseData.getData().get(0).getResult().getIsLangSelection());
                    userInfo.setFirst_name(responseData.getData().get(0).getResult().getFirstName());
                    userInfo.setLast_name(responseData.getData().get(0).getResult().getLastName());
                    userInfo.setProfilePicUrl(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    userInfo.setSessionId(responseData.getData().get(0).getResult().getSessionId());
                    userInfo.setSubscriptionEmail(responseData.getData().get(0).getResult().getSubscriptionEmail());

                    SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), userInfo);

                    FileInputStream fileInputStream = openFileInput(AppConstants.LANGUAGES_JSON_FILE);
                    String fileContent = AppUtils.convertStreamToString(fileInputStream);
                    LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                            fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                            }.getType()
                    );
                    Map<String, String> subscribedContentLanguages = responseData.getData().get(0).getResult().getLangSubscription();
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
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
        }
    };
}
