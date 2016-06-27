package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.controller.ControllerCityByPincode;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.newmodels.parentingmodel.CityByPinCodeModel;
import com.mycity4kids.preference.SharedPrefUtils;

import java.util.ArrayList;

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
        Intent intent = new Intent(LoadingActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
