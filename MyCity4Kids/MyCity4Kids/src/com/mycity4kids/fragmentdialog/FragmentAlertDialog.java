package com.mycity4kids.fragmentdialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.dbtable.CityTable;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.LandingLoginActivity;
import com.mycity4kids.ui.activity.SelectLocationActivity;
import com.mycity4kids.utils.NearMyCity;
import com.mycity4kids.utils.NearMyCity.FetchCity;
import com.mycity4kids.utils.location.GPSTracker;
import com.mycity4kids.widget.StaticCityList;

import java.util.ArrayList;

public class FragmentAlertDialog extends DialogFragment {
    private TextView mInputText;

    public FragmentAlertDialog newInstance(Context pContext, String str) {
        FragmentAlertDialog frag = new FragmentAlertDialog();

        return frag;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        OnClickListener okClick = new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        getActivity().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                       // doFirstTimeConfigWork();

                        getDialog().cancel();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        showSelectCityLocation();
                        break;

                    default:
                        break;
                }


            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mInputText = new TextView(getActivity());
        mInputText.setTextColor(Color.BLACK);
        mInputText.setText(getString(R.string.gps_enabled_alert));
        mInputText.setPadding(30, 30, 30, 30);
        mInputText.setTextSize(20);
        builder.setPositiveButton("OK", okClick);
        // commented by khushboo
        //builder.setNegativeButton("Select City", okClick);
        //builder.setCancelable(false);

        builder.setView(mInputText);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;

    }


    private void doFirstTimeConfigWork() {
        GPSTracker getCurrentLocation = new GPSTracker(getActivity());
        double _latitude = getCurrentLocation.getLatitude();
        double _longitude = getCurrentLocation.getLongitude();

        /**
         * configuration Controller for fetching category,locality,city
         * according to api versions:
         */
        final ConfigurationController _controller = new ConfigurationController(getActivity(), (IScreen) getActivity());
        final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(getActivity());


        /**
         * this method will give current city model & we get city id according
         * to current City:- CityId will pass in configuration controller &
         * according to city id we will get latest locality & category :)
         */

        if (versionApiModel.getCategoryVersion() == 0.0 && versionApiModel.getCityVersion() == 0.0 && versionApiModel.getLocalityVersion() == 0.0) {
            new NearMyCity(getActivity(), _latitude, _longitude, new FetchCity() {

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
                        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                            ToastUtils.showToast(getActivity(), getString(R.string.error_network));
                            return;

                        }
                        _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);


                    }

                }
            });
        } else {
            /**
             * this will call every time on splash:
             */
            // commented by khushboo

            versionApiModel.setCityId(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
            if (ConnectivityUtils.isNetworkEnabled(getActivity())) {

                _controller.getData(AppConstants.CONFIGURATION_REQUEST,
                        versionApiModel);
                //ToastUtils.showToast(getActivity(), getString(R.string.error_network));
                //return;
            } else {

                TableAdult _table=new TableAdult((BaseApplication)getActivity().getApplicationContext());
                if(_table.getRowsCount()>0){ // if he signup
                    // chnaged this earlier homecategory
                    Intent intent=new Intent(getActivity(),DashboardActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

                else{
                    Intent intent = new Intent(getActivity(),LandingLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                   getActivity().finish();
                }
            }


        }
    }

    private void showSelectCityLocation() {
        CityTable mCityTable = new CityTable((BaseApplication) getActivity().getApplicationContext());
        if (mCityTable.getTotalCount() == 0) {


            ArrayList<MetroCity> _city = StaticCityList._hardCodedCity;
            try {
                mCityTable.beginTransaction();
                for (MetroCity cityModel : _city) {

                    mCityTable.insertData(cityModel);
                }
                mCityTable.setTransactionSuccessful();
            } finally {
                mCityTable.endTransaction();
            }
        }

        Intent intent = new Intent(getActivity(), SelectLocationActivity.class);
        intent.putExtra("isFromSplash", true);
        getActivity().startActivity(intent);
        getActivity().finish();

    }




}
