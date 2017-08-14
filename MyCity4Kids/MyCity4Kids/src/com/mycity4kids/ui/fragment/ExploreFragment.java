package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
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
import com.mycity4kids.ui.activity.CityBestArticleListingActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.utils.NearMyCity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 7/8/17.
 */
public class ExploreFragment extends BaseFragment implements View.OnClickListener, CityListingDialogFragment.IChangeCity {

    public ArrayList<CityInfoItem> mDatalist;

    private TextView cityNameTextView;
    private CityListingDialogFragment cityFragment;
    private int selectedCityId;
    private String newSelectedCityId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.pushOpenScreenEvent(getActivity(), "Notification Settings", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        View view = inflater.inflate(R.layout.explore_fragment, container, false);

        cityNameTextView = (TextView) view.findViewById(R.id.cityNameTextView);
        ImageView eventsImageView = (ImageView) view.findViewById(R.id.eventsImageView);
        ImageView resImageView = (ImageView) view.findViewById(R.id.resImageView);
        ImageView thingToDoImageView = (ImageView) view.findViewById(R.id.thingToDoImageView);

        cityNameTextView.setOnClickListener(this);
        eventsImageView.setOnClickListener(this);
        resImageView.setOnClickListener(this);
        thingToDoImageView.setOnClickListener(this);

        cityNameTextView.setText("" + SharedPrefUtils.getCurrentCityModel(getActivity()).getName());

        if (SharedPrefUtils.getCurrentCityModel(getActivity()).getId() == AppConstants.OTHERS_CITY_ID) {
            eventsImageView.setVisibility(View.GONE);
            resImageView.setVisibility(View.GONE);
            thingToDoImageView.setVisibility(View.GONE);
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ConfigAPIs cityConfigAPI = retrofit.create(ConfigAPIs.class);
        Call<CityConfigResponse> cityCall = cityConfigAPI.getCityConfig();
        cityCall.enqueue(cityConfigResponseCallback);

        return view;
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
                    MetroCity currentCity = SharedPrefUtils.getCurrentCityModel(getActivity());
                    for (int i = 0; i < responseData.getData().getResult().getCityData().size(); i++) {
                        if (!AppConstants.ALL_CITY_NEW_ID.equals(responseData.getData().getResult().getCityData().get(i).getId())
                                && !AppConstants.OTHERS_NEW_CITY_ID.equals(responseData.getData().getResult().getCityData().get(i).getId())) {
                            mDatalist.add(responseData.getData().getResult().getCityData().get(i));
                        }
                    }

                    for (int i = 0; i < mDatalist.size(); i++) {
                        int cId = Integer.parseInt(mDatalist.get(i).getId().replace("city-", ""));
                        if (currentCity.getId() == cId) {
                            mDatalist.get(i).setSelected(true);
                            cityNameTextView.setText(mDatalist.get(i).getCityName());
                        } else {
                            mDatalist.get(i).setSelected(false);
                        }
                    }
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
        if (response == null) {
//            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.CONFIGURATION_REQUEST:
                Object responseObject = response.getResponseObject();
                if (responseObject instanceof ConfigurationApiModel) {
                    ConfigurationApiModel _configurationResponse = (ConfigurationApiModel) responseObject;
                    BaseApplication.setBusinessREsponse(null);
                    HeavyDbTask _heavyDbTask = new HeavyDbTask(getActivity(),
                            _configurationResponse, new OnUIView() {
                        @Override
                        public void comeBackOnUI() {
//                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    _heavyDbTask.execute();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.eventsImageView:
                FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                bundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getActivity()));
                bundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
                fragment.setArguments(bundle);
                ((DashboardActivity) getActivity()).addFragment(fragment, bundle, true);
                break;
            case R.id.resImageView:
                Constants.IS_SEARCH_LISTING = false;
                ((DashboardActivity) getActivity()).addFragment(new FragmentHomeCategory(), null, true);
                break;
            case R.id.thingToDoImageView:
                Intent cityIntent = new Intent(getActivity(), CityBestArticleListingActivity.class);
                startActivity(cityIntent);
                break;
            case R.id.cityNameTextView:
                cityFragment = new CityListingDialogFragment();
                cityFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putParcelableArrayList("cityList", mDatalist);
                _args.putString("fromScreen", "explore");
                cityFragment.setArguments(_args);
                FragmentManager fm = getChildFragmentManager();
                cityFragment.show(fm, "Replies");
                break;
        }
    }

    @Override
    public void onCitySelect(CityInfoItem cityItem) {
        cityNameTextView.setText(cityItem.getCityName());
        selectedCityId = Integer.parseInt(cityItem.getId().replace("city-", ""));
        newSelectedCityId = cityItem.getId();
        saveCityData();
    }

    public void saveCityData() {

        final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(getActivity());
        final ConfigurationController _controller = new ConfigurationController(getActivity(), this);
        if (null == mDatalist || mDatalist.isEmpty()) {
            ToastUtils.showToast(getActivity(), getString(R.string.change_city_fetch_available_cities));
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
        new NearMyCity(getActivity(), _latitude, _longitude, new NearMyCity.FetchCity() {

            @Override
            public void nearCity(City cityModel) {
                int cityId = cityModel.getCityId();

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
//                    mFirebaseAnalytics.setUserProperty("CityId", cityId + "");
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
                                return;
                            }

                            UserDetailResponse responseData = (UserDetailResponse) response.body();
                            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                                Toast.makeText(BlogSetupActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), PushTokenService.class);
                                getActivity().startService(intent);
//                                finish();
                            } else {
//                                Toast.makeText(BlogSetupActivity.this, responseData.getReason(), Toast.LENGTH_SHORT).show();
//                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                            removeProgressDialog();
//                            Toast.makeText(BlogSetupActivity.this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                            Crashlytics.logException(t);
                            Log.d("MC4kException", Log.getStackTraceString(t));
//                            finish();
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onOtherCitySelect(int pos, String cityName) {

    }
}
