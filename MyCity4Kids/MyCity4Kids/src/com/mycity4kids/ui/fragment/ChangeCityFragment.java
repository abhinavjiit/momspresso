package com.mycity4kids.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

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
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.utils.NearMyCity;

/**
 * Created by anshul on 2/4/16.
 */
public class ChangeCityFragment extends BaseFragment {
    RadioGroup radioGroup;
    RadioButton radioButton;
    Double _latitude;
    Double _longitude;
    int cityId;
    FirebaseAnalytics mFirebaseAnalytics;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_city, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "City Change", SharedPrefUtils.getUserDetailModel(getActivity()).getId() + "");

        ((DashboardActivity) getActivity()).setTitle("Change City");
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getActivity());
        setHasOptionsMenu(true);
        radioGroup=(RadioGroup)view.findViewById(R.id.radioGroup);
        cityId=SharedPrefUtils.getCurrentCityModel(getActivity()).getId();
        Log.e("cityId",SharedPrefUtils.getCurrentCityModel(getActivity()).getId()+"");
        switch (cityId)
        {
            case 1:
                radioGroup.check(R.id.delhiNcr);
                break;
            case 2:
                radioGroup.check(R.id.Bangalore);
                break;
            case 3:
                radioGroup.check(R.id.Mumbai);
                break;
            case 4:
                radioGroup.check(R.id.Pune);
                break;
            case 5:
                radioGroup.check(R.id.Hyderabad);
                break;
            case 6:
                radioGroup.check(R.id.Chennai);
                break;
            case 7:
                radioGroup.check(R.id.Kolkata);
                break;
            case 8:
                radioGroup.check(R.id.Jaipur);
                break;
            case 9:
                radioGroup.check(R.id.Ahmedabad);
                break;
           /* case 100:
                radioGroup.check(R.id.Others);
                break;*/
        }
        return view;

    }

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

                    if (_configurationResponse.getResult().getData().getIsAppUpdateRequired() == 1) {
                     /*   showUpgradeAppAlertDialog("Error", "Please upgrade your app to continue", new OnButtonClicked() {
                            @Override
                            public void onButtonCLick(int buttonId) {
                            }
                        });
                        return;*/
                    }
                    /**
                     * Save data into tables :-
                     */
                    BaseApplication.setBusinessREsponse(null);
                    HeavyDbTask _heavyDbTask = new HeavyDbTask(getActivity(),
                            _configurationResponse, new OnUIView() {

                        @Override
                        public void comeBackOnUI() {
                         //   navigateToNextScreen(true);
                            ((DashboardActivity) getActivity()).replaceFragment(new FragmentSetting(), null, true);
                            removeProgressDialog();
                          //  getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                            Log.e("comeBackUi","hey");
                        }
                    });

                    _heavyDbTask.execute();

                }
                break;

            default:
                break;
        }

    }
    public void changeCity()
    {  final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(getActivity());
        final ConfigurationController _controller = new ConfigurationController(getActivity(), this);
        int selectedId=radioGroup.getCheckedRadioButtonId();
        radioButton=(RadioButton) getView().findViewById(selectedId);
        showProgressDialog(getString(R.string.please_wait));
      //  Toast.makeText(getActivity(),radioButton.getText(),Toast.LENGTH_SHORT).show();
        switch(radioButton.getText().toString())
        {
            case "Delhi-NCR":
                _latitude=28.6100;
                _longitude=77.2300;
                break;
            case "Bangalore":
                _latitude=12.9667;
                _longitude=77.5667;
                break;
            case "Mumbai":
                _latitude=18.9750;
                _longitude=72.8258;
                break;
            case "Pune":
                _latitude=18.5203;
                _longitude=73.8567;
                break;
            case "Hyderabad":
                _latitude=17.3660;
                _longitude=78.4760;
                break;
            case "Chennai":
                _latitude=13.0474097;
                _longitude=79.9288085;
                break;
            case "Kolkata":
                _latitude=22.5667;
                _longitude=88.3667;
                break;
            case "Jaipur":
                _latitude=26.9000;
                _longitude=75.8000;
                break;
            case "Ahmedabad":
                _latitude=23.0300;
                _longitude=72.5800;
                break;
            /*case "Others":
                _latitude=0.0;
                _longitude=0.0;
                break;*/
                default:
                    _latitude=28.6100;
                    _longitude=77.2300;
                    break;

        }
         new NearMyCity(getActivity(), _latitude, _longitude, new NearMyCity.FetchCity() {

            @Override
            public void nearCity(City cityModel) {


                int cityId = cityModel.getCityId();

                /**
                 * save current city id in shared preference
                 */
                MetroCity model = new MetroCity();
                model.setId(cityModel.getCityId());
                model.setName(cityModel.getCityName());
                /**
                 * this city model will be save only one time on splash:
                 */
                SharedPrefUtils.setCurrentCityModel(getActivity(), model);


                if (cityId > 0) {
                    versionApiModel.setCityId(cityId);
                    mFirebaseAnalytics.setUserProperty("CityId",cityId+"");
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


                }

            }
        });
    }
}
