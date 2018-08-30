package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.response.CityConfigResponse;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by kapil.vij on 13-08-2015.
 */
public class LoadingActivity extends BaseActivity {

    private int cityIdFromLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fetch_pincode_config);

        if (!ConnectivityUtils.isNetworkEnabled(LoadingActivity.this)) {
            navigateToDashboard();
            return;
        }
        try {
            cityIdFromLocation = Integer.parseInt(SharedPrefUtils.getUserDetailModel(LoadingActivity.this).getCityId());
//        SharedPrefUtils.getCurrentCityModel(LoadingActivity.this).getId();
            if (SharedPrefUtils.getCurrentCityModel(LoadingActivity.this).getId() == AppConstants.OTHERS_CITY_ID) {
                fetchingLocation();
            } else {
                navigateToDashboard();
            }
        } catch (NumberFormatException e) {
            navigateToDashboard();
        }
    }

    private void fetchingLocation() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ConfigAPIs cityConfigAPI = retrofit.create(ConfigAPIs.class);
        Call<CityConfigResponse> call = cityConfigAPI.getCityConfig();
        call.enqueue(cityConfigResponseCallback);
    }

    private Callback<CityConfigResponse> cityConfigResponseCallback = new Callback<CityConfigResponse>() {
        @Override
        public void onResponse(Call<CityConfigResponse> call, retrofit2.Response<CityConfigResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                CityConfigResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<CityInfoItem> mDatalist = responseData.getData().getResult().getCityData();
                    for (int i = 0; i < mDatalist.size(); i++) {
                        CityInfoItem cii = mDatalist.get(i);
                        int cId = Integer.parseInt(cii.getId().replace("city-", ""));
                        if (cId == cityIdFromLocation) {
                            MetroCity model = new MetroCity();
                            model.setId(cId);
                            model.setName(cii.getCityName());
                            model.setNewCityId(cii.getId());

                            SharedPrefUtils.setCurrentCityModel(LoadingActivity.this, model);
                            sendConfigurationRequest();
                        }
                    }
                } else {
                    navigateToDashboard();
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                navigateToDashboard();
            }
        }

        @Override
        public void onFailure(Call<CityConfigResponse> call, Throwable t) {
            navigateToDashboard();
        }
    };

    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.CONFIGURATION_REQUEST:
                Object responseObject = response.getResponseObject();
                if (responseObject instanceof ConfigurationApiModel) {
                    ConfigurationApiModel _configurationResponse = (ConfigurationApiModel) responseObject;

                    /**
                     * Save data into tables :-
                     */
                    HeavyDbTask _heavyDbTask = new HeavyDbTask(this,
                            _configurationResponse, new OnUIView() {

                        @Override
                        public void comeBackOnUI() {
                            navigateToDashboard();
                            Log.i("Dashboard", "Configuration Data Updated");
                        }
                    });
                    _heavyDbTask.execute();

                }

            default:
                break;
        }
    }

    private void sendConfigurationRequest() {
        VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(this);
        versionApiModel.setCategoryVersion(0.0f);
        versionApiModel.setCityVersion(0.0f);
        versionApiModel.setLocalityVersion(0.0f);
        versionApiModel.setCityId(SharedPrefUtils.getCurrentCityModel(this).getId());

        /**
         * get current version code ::
         */
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String version = pInfo.versionName;
        if (!StringUtils.isNullOrEmpty(version)) {
            versionApiModel.setAppUpdateVersion(version);
        }

        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, getString(R.string.error_network));
            return;

        }
        ConfigurationController _controller = new ConfigurationController(this, this);
        _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);
    }

    public void navigateToDashboard() {

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.getFollowedCategories(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(getFollowedTopicsResponseCallback);

    }

    private Callback<FollowUnfollowCategoriesResponse> getFollowedTopicsResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<String> mDatalist = (ArrayList<String>) responseData.getData();
                    if (mDatalist != null) {
                        ArrayList<String> topicList = (ArrayList<String>) responseData.getData();
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        int version = pInfo.versionCode;
                        if (version > 100 && (topicList == null || topicList.size() < 1)) {
                            SharedPrefUtils.setFollowTopicApproachChangeFlag(LoadingActivity.this, true);
                        }
                        SharedPrefUtils.setFollowedTopicsCount(LoadingActivity.this, topicList.size());
                    }
                } else {
                    showToast(responseData.getReason());
                }
                Intent intent = new Intent(LoadingActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Intent intent = new Intent(LoadingActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            Intent intent = new Intent(LoadingActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    };

}
