package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.CityConfigResponse;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.ui.activity.SettingsActivity;
import com.mycity4kids.ui.adapter.ChangeCityAdapter;
import com.mycity4kids.utils.NearMyCity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by anshul on 2/4/16.
 */
public class ChangeCityFragment extends BaseFragment implements ChangeCityAdapter.IOtherCity {

    private ListView cityListView;
    RadioGroup radioGroup;
    Double _latitude;
    Double _longitude;
    int cityId;
    FirebaseAnalytics mFirebaseAnalytics;
    ArrayList<CityInfoItem> mDatalist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_city, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "City Change", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        ((SettingsActivity) getActivity()).setTitle("Change City");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        setHasOptionsMenu(true);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        cityId = SharedPrefUtils.getCurrentCityModel(getActivity()).getId();
        cityListView = (ListView) view.findViewById(R.id.cityListView);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ConfigAPIs cityConfigAPI = retrofit.create(ConfigAPIs.class);
        Call<CityConfigResponse> call = cityConfigAPI.getCityConfig();
        call.enqueue(cityConfigResponseCallback);

        return view;

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
                CityConfigResponse responseData = (CityConfigResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    mDatalist = new ArrayList<>();
                    if (mDatalist == null) {
                        return;
                    }
                    for (int i = 0; i < responseData.getData().getResult().getCityData().size(); i++) {
                        if (!AppConstants.ALL_CITY_NEW_ID.equals(responseData.getData().getResult().getCityData().get(i).getId())) {
                            mDatalist.add(responseData.getData().getResult().getCityData().get(i));
                        }
                    }
                    int currentCityId = SharedPrefUtils.getCurrentCityModel(getActivity()).getId();
                    for (int i = 0; i < mDatalist.size(); i++) {
                        int cId = Integer.parseInt(mDatalist.get(i).getId().replace("city-", ""));
                        if (currentCityId == cId) {
                            mDatalist.get(i).setSelected(true);
                        } else {
                            mDatalist.get(i).setSelected(false);
                        }
                    }

                    final ChangeCityAdapter adapter = new ChangeCityAdapter(getActivity(), mDatalist, ChangeCityFragment.this);
                    cityListView.setAdapter(adapter);
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }

//            mFetchCity.nearCity(getNearMe(mLattitude, mLongitude));
        }

        @Override
        public void onFailure(Call<CityConfigResponse> call, Throwable t) {

        }
    };


    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
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
                    BaseApplication.setBusinessREsponse(null);
                    HeavyDbTask _heavyDbTask = new HeavyDbTask(getActivity(),
                            _configurationResponse, new OnUIView() {

                        @Override
                        public void comeBackOnUI() {
                            if (getActivity() != null) {
                                ((SettingsActivity) getActivity()).replaceFragment(new FragmentSetting(), null, true);
                            }
                            removeProgressDialog();
                            Log.e("comeBackUi", "hey");
                        }
                    });
                    _heavyDbTask.execute();
                }
                break;

            default:
                break;
        }

    }

    public void changeCity() {

        final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(getActivity());
        final ConfigurationController _controller = new ConfigurationController(getActivity(), this);
        if (null == mDatalist || mDatalist.isEmpty()) {
            ToastUtils.showToast(getActivity(), getString(R.string.change_city_fetch_available_cities));
            return;
        }
        showProgressDialog(getString(R.string.please_wait));
        for (int i = 0; i < mDatalist.size(); i++) {
            if (mDatalist.get(i).isSelected()) {
                _latitude = mDatalist.get(i).getLat();
                _longitude = mDatalist.get(i).getLon();
            }
        }
        new NearMyCity(getActivity(), _latitude, _longitude, new NearMyCity.FetchCity() {

            @Override
            public void nearCity(City cityModel) {
                int cityId = cityModel.getCityId();

                if (getActivity() != null) {
                    /**
                     * save current city in shared preference
                     */
                    MetroCity model = new MetroCity();
                    model.setId(cityModel.getCityId());
                    model.setName(cityModel.getCityName());
                    model.setNewCityId(cityModel.getNewCityId());

                    SharedPrefUtils.setCurrentCityModel(getActivity(), model);
                    SharedPrefUtils.setChangeCityFlag(getActivity(), true);

                    if (cityId > 0) {
                        versionApiModel.setCityId(cityId);
                        mFirebaseAnalytics.setUserProperty("CityId", cityId + "");
                        /**
                         * get current version code ::
                         */
                        PackageInfo pInfo = null;
                        try {
                            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        String version = pInfo.versionName;
                        if (!StringUtils.isNullOrEmpty(version)) {
                            versionApiModel.setAppUpdateVersion(version);
                        }

                        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                            ToastUtils.showToast(getActivity(), getString(R.string.error_network));
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
                                    Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                UserDetailResponse responseData = (UserDetailResponse) response.body();
                                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                                    if (getActivity() != null) {
                                        Toast.makeText(getActivity(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getActivity(), PushTokenService.class);
                                        getActivity().startService(intent);
                                    }
                                } else {
                                    Toast.makeText(getActivity(), responseData.getReason(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                                removeProgressDialog();
                                Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                                Crashlytics.logException(t);
                                Log.d("MC4kException", Log.getStackTraceString(t));
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onOtherCityAdd(final String cityName) {
        final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(getActivity());
        final ConfigurationController _controller = new ConfigurationController(getActivity(), this);
        if (null == mDatalist || mDatalist.isEmpty()) {
            ToastUtils.showToast(getActivity(), getString(R.string.change_city_fetch_available_cities));
            return;
        }
        showProgressDialog(getString(R.string.please_wait));
        for (int i = 0; i < mDatalist.size(); i++) {
            if (mDatalist.get(i).isSelected()) {
                _latitude = mDatalist.get(i).getLat();
                _longitude = mDatalist.get(i).getLon();
            }
        }
        new NearMyCity(getActivity(), _latitude, _longitude, new NearMyCity.FetchCity() {

            @Override
            public void nearCity(City cityModel) {
                int cityId = cityModel.getCityId();

                /**
                 * save current city in shared preference
                 */
                MetroCity model = new MetroCity();
                model.setId(cityModel.getCityId());
                model.setName(cityName);
                model.setNewCityId(cityModel.getNewCityId());

                SharedPrefUtils.setCurrentCityModel(getActivity(), model);
                SharedPrefUtils.setChangeCityFlag(getActivity(), true);

                if (cityId > 0) {
                    versionApiModel.setCityId(cityId);
                    mFirebaseAnalytics.setUserProperty("CityId", cityId + "");
                    /**
                     * get current version code ::
                     */
                    PackageInfo pInfo = null;
                    try {
                        pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    String version = pInfo.versionName;
                    if (!StringUtils.isNullOrEmpty(version)) {
                        versionApiModel.setAppUpdateVersion(version);
                    }

                    if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                        ToastUtils.showToast(getActivity(), getString(R.string.error_network));
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
                                Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            UserDetailResponse responseData = (UserDetailResponse) response.body();
                            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                                Toast.makeText(getActivity(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), PushTokenService.class);
                                getActivity().startService(intent);
                            } else {
                                Toast.makeText(getActivity(), responseData.getReason(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                            removeProgressDialog();
                            Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                            Crashlytics.logException(t);
                            Log.d("MC4kException", Log.getStackTraceString(t));
                        }
                    });

                    UpdateUserDetailsRequest addOtherCityNameRequest = new UpdateUserDetailsRequest();
                    addOtherCityNameRequest.setCityId("" + cityModel.getCityId());
                    addOtherCityNameRequest.setCityName(cityName);
                    Call<UserDetailResponse> callNew = userAttributeUpdateAPI.updateCityAndKids(addOtherCityNameRequest);
                    callNew.enqueue(addOtherCityNameResponseCallback);
                }
            }
        });
    }

    Callback<UserDetailResponse> addOtherCityNameResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            Log.d("SUCCESS", "" + response);
            if (response == null || response.body() == null) {
                return;
            }
            try {

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
