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
import com.mycity4kids.controller.ControllerCityByPincode;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.newmodels.parentingmodel.CityByPinCodeModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by kapil.vij on 13-08-2015.
 */
public class LoadingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fetch_pincode_config);

        if (!ConnectivityUtils.isNetworkEnabled(LoadingActivity.this)) {
            navigateToDashboard();
//            ToastUtils.showToast(LoadingActivity.this, getString(R.string.error_network));
            return;
        }
//        showProgressDialog(getString(R.string.please_wait));

        // get cityid from pincode
        new ControllerCityByPincode(this, this).getData(AppConstants.CITY_BY_PINCODE_REQUEST, "" + SharedPrefUtils.getpinCode(this));
    }

    @Override
    protected void updateUi(Response response) {
//        removeProgressDialog();
        if (response == null) {
            SharedPrefUtils.setCityFetched(this, false);
//            showToast(getResources().getString(R.string.server_error));
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.CITY_BY_PINCODE_REQUEST:
                navigateToDashboard();
                CityByPinCodeModel cityByPinCodeModel = (CityByPinCodeModel) response.getResponseObject();
                if (cityByPinCodeModel.getResponseCode() == 200) {
                    MetroCity model = new MetroCity();
                    model.setId(cityByPinCodeModel.getResult().getData().getCity_id());
                    switch (cityByPinCodeModel.getResult().getData().getCity_id()) {
                        case 1:
                            model.setName("Delhi-NCR");
                            break;
                        case 2:
                            model.setName("Bangalore");
                            break;
                        case 3:
                            model.setName("Mumbai");
                            break;
                        case 4:
                            model.setName("Pune");
                            break;
                        case 5:
                            model.setName("Hyderabad");
                            break;
                        case 6:
                            model.setName("Chennai");
                            break;
                        case 7:
                            model.setName("Kolkata");
                            break;
                        case 8:
                            model.setName("Jaipur");
                            break;
                        case 9:
                            model.setName("Ahmedabad");
                            break;
                        default:
                            model.setName("Delhi-NCR");
                            break;
                    }

                    int lastCityIdUsed = SharedPrefUtils.getCurrentCityModel(this).getId();
                    SharedPrefUtils.setCurrentCityModel(this, model);
                    SharedPrefUtils.setCityFetched(this, true);
                    if (lastCityIdUsed != model.getId()) {
                        // hit configuration API
                        sendConfigurationRequest();
                    } else {
                        navigateToDashboard();
                    }

                } else {
                    SharedPrefUtils.setCityFetched(this, false);
                    navigateToDashboard();
                }
                break;
            case AppConstants.CONFIGURATION_REQUEST:
//                removeProgressDialog();
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
//                            navigateToNextScreen(true);
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
                showToast("Something went wrong from server");
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = (FollowUnfollowCategoriesResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<String> mDatalist = (ArrayList<String>) responseData.getData();
                    if (mDatalist.size() < AppConstants.MINIMUM_TOPICS_FOLLOW_REQUIREMENT) {
                        Intent intent = new Intent(LoadingActivity.this, TopicsSplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putStringArrayListExtra("followedTopics", mDatalist);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoadingActivity.this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
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
//            showToast(getString(R.string.went_wrong));
            Intent intent = new Intent(LoadingActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
