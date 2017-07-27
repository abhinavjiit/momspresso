package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.CityConfigResponse;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.ui.adapter.CitySpinnerAdapter;
import com.mycity4kids.ui.fragment.CityListingDialogFragment;
import com.mycity4kids.utils.NearMyCity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/7/17.
 */
public class BlogSetupActivity extends BaseActivity implements View.OnClickListener {

    public ArrayList<CityInfoItem> mDatalist;
    private String otherCityName;

    private LinearLayout introLinearLayout;
    private RelativeLayout detailsRelativeLayout;
    private TextView okayTextView;
    private TextView cityTextView;
    private TextView savePublishTextView;
    private CityListingDialogFragment cityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_setup_activity);

        introLinearLayout = (LinearLayout) findViewById(R.id.introLinearLayout);
        detailsRelativeLayout = (RelativeLayout) findViewById(R.id.detailsRelativeLayout);
        okayTextView = (TextView) findViewById(R.id.okayTextView);
        cityTextView = (TextView) findViewById(R.id.cityTextView);
        savePublishTextView = (TextView) findViewById(R.id.savePublishTextView);

//        introLinearLayout.setOnClickListener(this);
//        detailsRelativeLayout.setOnClickListener(this);
        okayTextView.setOnClickListener(this);
        cityTextView.setOnClickListener(this);
        savePublishTextView.setOnClickListener(this);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ConfigAPIs cityConfigAPI = retrofit.create(ConfigAPIs.class);
        Call<CityConfigResponse> cityCall = cityConfigAPI.getCityConfig();
        cityCall.enqueue(cityConfigResponseCallback);
    }

    private Callback<CityConfigResponse> cityConfigResponseCallback = new Callback<CityConfigResponse>() {

        @Override
        public void onResponse(Call<CityConfigResponse> call, retrofit2.Response<CityConfigResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
//                gotToProfile();
                return;
            }
            try {
                CityConfigResponse responseData = (CityConfigResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    mDatalist = responseData.getData().getResult().getCityData();
                    mDatalist = new ArrayList<>();
                    if (mDatalist == null) {
//                        gotToProfile();
                        return;
                    }
                    MetroCity currentCity = SharedPrefUtils.getCurrentCityModel(BlogSetupActivity.this);
                    for (int i = 0; i < responseData.getData().getResult().getCityData().size(); i++) {
                        if (!AppConstants.ALL_CITY_NEW_ID.equals(responseData.getData().getResult().getCityData().get(i).getId())) {
                            mDatalist.add(responseData.getData().getResult().getCityData().get(i));
                        }
                        if (AppConstants.OTHERS_NEW_CITY_ID.equals(responseData.getData().getResult().getCityData().get(i).getId())) {
                            if (currentCity.getName() != null && !"Others".equals(currentCity.getName()) && currentCity.getId() == AppConstants.OTHERS_CITY_ID) {
                                mDatalist.get(mDatalist.size() - 1).setCityName("Others(" + currentCity.getName() + ")");
                            }
                        }
                    }

                    for (int i = 0; i < mDatalist.size(); i++) {
                        int cId = Integer.parseInt(mDatalist.get(i).getId().replace("city-", ""));
                        if (currentCity.getId() == cId) {
                            mDatalist.get(i).setSelected(true);
                            cityTextView.setText(mDatalist.get(i).getCityName());
                        } else {
                            mDatalist.get(i).setSelected(false);
                        }
                    }
//                    CitySpinnerAdapter citySpinnerAdapter = new CitySpinnerAdapter(getActivity(), R.layout.text_current_locality, mDatalist);
//                    citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    // Apply the adapter to the spinner
//                    citySpinner.setAdapter(citySpinnerAdapter);
//                    int currentCityId = SharedPrefUtils.getCurrentCityModel(getActivity()).getId();
//                    for (int i = 0; i < mDatalist.size(); i++) {
//                        int cId = Integer.parseInt(mDatalist.get(i).getId().replace("city-", ""));
//                        if (currentCityId == cId) {
//                            citySpinner.setSelection(i, true);
//                        }
//                    }
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                gotToProfile();
            }
        }

        @Override
        public void onFailure(Call<CityConfigResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
//            gotToProfile();
        }
    };

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okayTextView:
                introLinearLayout.setVisibility(View.GONE);
                detailsRelativeLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.cityTextView:
                cityFragment = new CityListingDialogFragment();
                Bundle _args = new Bundle();
                _args.putParcelableArrayList("cityList", mDatalist);
                cityFragment.setArguments(_args);
                FragmentManager fm = getSupportFragmentManager();
                cityFragment.show(fm, "Replies");
                break;
            case R.id.savePublishTextView:
                saveCityData();
                break;
        }
    }

    public void changeCityText(CityInfoItem cityInfoItem) {
        cityTextView.setText(cityInfoItem.getCityName());
    }

    public void setOtherCityName(final int pos, final String cityName) {
        otherCityName = cityName;
        mDatalist.get(pos).setCityName("Others(" + cityName + ")");
        cityTextView.setText(mDatalist.get(pos).getCityName());
    }

    public void saveCityData() {

        final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(this);
        final ConfigurationController _controller = new ConfigurationController(this, this);
        if (null == mDatalist || mDatalist.isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.change_city_fetch_available_cities));
            return;
        }
        showProgressDialog(getString(R.string.please_wait));
        double _latitude = 0;
        double _longitude = 0;
        for (int i = 0; i < mDatalist.size(); i++) {
            if (mDatalist.get(i).isSelected()) {
                _latitude = mDatalist.get(i).getLat();
                _longitude = mDatalist.get(i).getLon();
            }
        }
        new NearMyCity(this, _latitude, _longitude, new NearMyCity.FetchCity() {

            @Override
            public void nearCity(City cityModel) {
                int cityId = cityModel.getCityId();

                /**
                 * save current city in shared preference
                 */
                MetroCity model = new MetroCity();
                model.setId(cityModel.getCityId());
                if (AppConstants.OTHERS_CITY_ID == cityModel.getCityId()) {
                    cityModel.setCityName(otherCityName);
                    model.setName(otherCityName);
                } else {
                    model.setName(cityModel.getCityName());
                }

                model.setNewCityId(cityModel.getNewCityId());

                SharedPrefUtils.setCurrentCityModel(BlogSetupActivity.this, model);
                SharedPrefUtils.setChangeCityFlag(BlogSetupActivity.this, true);

                if (cityId > 0) {
                    versionApiModel.setCityId(cityId);
//                    mFirebaseAnalytics.setUserProperty("CityId", cityId + "");
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

                    if (!ConnectivityUtils.isNetworkEnabled(BlogSetupActivity.this)) {
                        ToastUtils.showToast(BlogSetupActivity.this, getString(R.string.error_network));
                        return;

                    }
                    _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);

                    UpdateUserDetailsRequest updateUserDetail = new UpdateUserDetailsRequest();
                    updateUserDetail.setAttributeName("cityId");
                    updateUserDetail.setAttributeType("S");
                    updateUserDetail.setAttributeValue("" + cityModel.getCityId());
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
                    Call<UserDetailResponse> call = userAttributeUpdateAPI.updateCity(updateUserDetail);
                    call.enqueue(new Callback<UserDetailResponse>() {
                        @Override
                        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
                            removeProgressDialog();
                            if (response == null || response.body() == null) {
                                Toast.makeText(BlogSetupActivity.this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            UserDetailResponse responseData = (UserDetailResponse) response.body();
                            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                                Toast.makeText(BlogSetupActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(BlogSetupActivity.this, PushTokenService.class);
                                startService(intent);
                            } else {
                                Toast.makeText(BlogSetupActivity.this, responseData.getReason(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                            removeProgressDialog();
                            Toast.makeText(BlogSetupActivity.this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                            Crashlytics.logException(t);
                            Log.d("MC4kException", Log.getStackTraceString(t));
                        }
                    });

                    UpdateUserDetailsRequest addOtherCityNameRequest = new UpdateUserDetailsRequest();
                    addOtherCityNameRequest.setCityId("" + cityModel.getCityId());
                    addOtherCityNameRequest.setCityName(cityModel.getCityName());
                    Call<UserDetailResponse> callNew = userAttributeUpdateAPI.updateCityAndKids(addOtherCityNameRequest);
                    callNew.enqueue(addOtherCityNameResponseCallback);
                }
            }
        });
    }

    Callback<UserDetailResponse> addOtherCityNameResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            Log.d("SUCCESS", "" + response);
            if (response == null || response.body() == null) {
                return;
            }
            try {
                if (null != cityFragment) {
                    cityFragment.dismiss();
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
        }
    };
}
