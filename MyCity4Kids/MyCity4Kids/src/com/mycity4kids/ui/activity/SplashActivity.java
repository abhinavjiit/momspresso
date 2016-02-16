package com.mycity4kids.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.fragmentdialog.FragmentAlertDialog;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.newmodels.UserInviteModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AnalyticsHelper;
import com.mycity4kids.utils.NearMyCity;
import com.mycity4kids.utils.location.GPSTracker;

public class SplashActivity extends BaseActivity {
    private boolean isLocationScreen = false;
    private String _deepLinkURL;
    private int isFirstLaunch = 0;
    private String mobileNumberForVerification = "";
//    private DeepLinkData _deepLinkData;
//    private GetAppointmentController _appointmentcontroller;

    // The onNewIntent() is overridden to get and resolve the data for deep linking
    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            _deepLinkURL = data;
//            DeepLinkingController _deepLinkingController = new DeepLinkingController(this, this);
//            _deepLinkingController.getData(AppConstants.DEEP_LINK_RESOLVER_REQUEST, data);
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onNewIntent(getIntent());

        try {

            setContentView(R.layout.splash_activity);

            AnalyticsHelper.logEvent("Application Launch...");

            ImageView _spin = (ImageView) findViewById(R.id.spin);
            _spin.startAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.rotate_indefinitely));
            final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(SplashActivity.this);

            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            /**
             * for first time we will check that gps is enabled or not:
             */
            if (versionApiModel.getCategoryVersion() == 0.0 && versionApiModel.getCityVersion() == 0.0 && versionApiModel.getLocalityVersion() == 0.0) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {

                    buildAlertMessageNoGps();
                    return;
                }
            }

            GPSTracker getCurrentLocation = new GPSTracker(this);
            double _latitude = getCurrentLocation.getLatitude();
            double _longitude = getCurrentLocation.getLongitude();

            /**
             * configuration Controller for fetching category,locality,city
             * according to api versions:
             */
            final ConfigurationController _controller = new ConfigurationController(this, this);


            /**
             * this method will give current city model & we get city id according
             * to current City:- CityId will pass in configuration controller &
             * according to city id we will get latest locality & category :)
             */

            if (versionApiModel.getCategoryVersion() == 0.0 && versionApiModel.getCityVersion() == 0.0 && versionApiModel.getLocalityVersion() == 0.0) {
                new NearMyCity(this, _latitude, _longitude, new NearMyCity.FetchCity() {

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
                        SharedPrefUtils.setCurrentCityModel(SplashActivity.this, model);


                        if (cityId > 0) {
                            versionApiModel.setCityId(cityId);

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

                            if (!ConnectivityUtils.isNetworkEnabled(SplashActivity.this)) {
                                ToastUtils.showToast(SplashActivity.this, getString(R.string.error_network));
                                return;

                            }
                            isFirstLaunch = 1;
                            _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);


                        }

                    }
                });
            } else {
                /**
                 * this will call every time on splash:
                 */

                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;

                versionApiModel.setCityId(SharedPrefUtils.getCurrentCityModel(this).getId());
                versionApiModel.setAppUpdateVersion(version);
                if (ConnectivityUtils.isNetworkEnabled(SplashActivity.this)) {

                    _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isFirstLaunch = 0;
                            navigateToNextScreen(true);
                        }
                    }, 500);

                    //ToastUtils.showToast(SplashActivity.this, getString(R.string.error_network));
                    //return;
                } else {
                    isFirstLaunch = 0;
                    navigateToNextScreen(true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//
//        GCMUtil.initializeGCM(this, new OnGcmTokenReceived() {
//            @Override
//            public void onGcmTokenReceive(String deviceToken) {
//                Log.e("SplashActivity", "deviceToken ------------- " + deviceToken);
//            }
//        });
//        GCMUtil.initializeGCM(this);

    }


    private void navigateToNextScreen(boolean isConfigurationAvailable) {
        TableAdult _table = new TableAdult(BaseApplication.getInstance());
        if (_table.getAdultCount() > 0) { // if he signup
            Log.e("MYCITY4KIDS", "USER logged In");
            String mobileNumb = SharedPrefUtils.getUserDetailModel(this).getMobile_number();
            if (StringUtils.isNullOrEmpty(mobileNumb)) {
                Intent updateMobileIntent = new Intent(this, UpdateMobileNumberActivity.class);
//                            updateMobileIntent.putExtra("activity", "landingLoginActivity");
                updateMobileIntent.putExtra("isExistingUser", "1");
                updateMobileIntent.putExtra("name", SharedPrefUtils.getUserDetailModel(this).getFirst_name());
                updateMobileIntent.putExtra("colorCode", SharedPrefUtils.getUserDetailModel(this).getColor_code());
                startActivity(updateMobileIntent);
            } else {
                startSyncing();
                startSyncingUserInfo();
                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                if (!StringUtils.isNullOrEmpty(_deepLinkURL)) {
                    intent.putExtra(AppConstants.DEEP_LINK_URL, _deepLinkURL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP/*|Intent.FLAG_ACTIVITY_NEW_TASK*/);
                }

                startActivity(intent);
                finish();
            }


        } else {
            Log.e("MYCITY4KIDS", "USER logged Out");
            if (!isConfigurationAvailable) {
                showAlertDialog("Error", "Something went wrong from server side!", new OnButtonClicked() {
                    @Override
                    public void onButtonCLick(int buttonId) {
                        finish();
                    }
                });
                return;
            } else {
                String userFamilyInvites = SharedPrefUtils.getUserFamilyInvites(this);
                if (null != userFamilyInvites && !userFamilyInvites.isEmpty()) {
                    //User Signed in but has not created family.
                    UserInviteModel userInviteModel = new Gson().fromJson(userFamilyInvites, UserInviteModel.class);
                    if (null != userInviteModel.getFamilyInvites() && !userInviteModel.getFamilyInvites().isEmpty()) {
                        Intent intent = new Intent(SplashActivity.this, ListFamilyInvitesActivity.class);
                        intent.putExtra("userInviteData", userInviteModel);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, CreateFamilyActivity.class);
                        intent.putExtra("userInviteData", userInviteModel);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                } else {

                    Intent intent;
                    if (SharedPrefUtils.getLogoutFlag(this))
                        intent = new Intent(SplashActivity.this, JoinFamilyActivity.class);
                    else
                        intent = new Intent(SplashActivity.this, TutorialActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }
        Log.d("GCM Token ", SharedPrefUtils.getDeviceToken(this));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // again check location

        if (isLocationScreen) {

            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {

            } else
                navigateToNextScreen(true);
        }
        // move to screen


        /*if(isSplash)
        {
		 moveTaskToBack(true);
		}*/
    }

    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            navigateToNextScreen(false);

        }
        switch (response.getDataType()) {

            case AppConstants.CONFIGURATION_REQUEST:
                Object responseObject = response.getResponseObject();
                if (responseObject instanceof ConfigurationApiModel) {
                    ConfigurationApiModel _configurationResponse = (ConfigurationApiModel) responseObject;

                    if (_configurationResponse.getResult().getData().getIsAppUpdateRequired() == 1) {
                        showUpgradeAppAlertDialog("Error", "Please upgrade your app to continue", new OnButtonClicked() {
                            @Override
                            public void onButtonCLick(int buttonId) {
                            }
                        });
                        return;
                    }
                    /**
                     * Save data into tables :-
                     */
                    HeavyDbTask _heavyDbTask = new HeavyDbTask(this,
                            _configurationResponse, new OnUIView() {

                        @Override
                        public void comeBackOnUI() {
                            if (isFirstLaunch == 1)
                                navigateToNextScreen(true);
                        }
                    });
                    _heavyDbTask.execute();

                }
                break;
            default:
                break;
        }

    }

//    public void saveData(AppoitmentDataModel model) {
//
//        TableAppointmentData apponntmentTable = new TableAppointmentData((BaseApplication) getApplicationContext());
//        TableFile FileTable = new TableFile((BaseApplication) getApplicationContext());
//        TableNotes NoteTable = new TableNotes((BaseApplication) getApplicationContext());
//        TableWhoToRemind WhoToRemindTable = new TableWhoToRemind((BaseApplication) getApplicationContext());
//        TableAttendee attendeeTable = new TableAttendee((BaseApplication) getApplicationContext());
//
////        apponntmentTable.deleteAll();
////        FileTable.deleteAll();
////        NoteTable.deleteAll();
////        WhoToRemindTable.deleteAll();
////        attendeeTable.deleteAll();
//
//
//        try {
//
//            for (AppoitmentDataModel.AppointmentData data : model.getAppointment()) { // appoitment array loop
//
//
//                int appointmentid = data.getAppointment().getId();
//
//                // first  delete
//
//                apponntmentTable.deleteAppointment(appointmentid);
//                FileTable.deleteAppointment(appointmentid);
//                attendeeTable.deleteAppointment(appointmentid);
//                NoteTable.deleteAppointment(appointmentid);
//                WhoToRemindTable.deleteAppointment(appointmentid);
//
//                // data is saving here
//                apponntmentTable.insertData(data.getAppointment());
//
//
//                for (AppoitmentDataModel.Files filesList : data.getAppointmentFile()) {
//
//                    FileTable.insertData(filesList);
//
//                }
//
//
//                for (AppoitmentDataModel.Attendee attendeeList : data.getAppointmentAttendee()) {
//
//                    attendeeTable.insertData(attendeeList);
//
//                }
//                for (AppoitmentDataModel.Notes notesList : data.getAppointmentNote()) {
//
//                    NoteTable.insertData(notesList);
//
//                }
//                for (AppoitmentDataModel.WhoToRemind whotoRemind : data.getAppointmentWhomRemind()) {
//
//                    WhoToRemindTable.insertData(whotoRemind);
//
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            System.out.println("TOuch outside the dialog ******************** ");

        }
        return false;
    }

    private void buildAlertMessageNoGps() {

        isLocationScreen = true;
        FragmentAlertDialog _dialog = new FragmentAlertDialog();

        FragmentAlertDialog fragment = _dialog.newInstance(this, getString(R.string.gps_enabled_alert));

        getSupportFragmentManager().beginTransaction().add(fragment, "MAGIC_TAG").commit();
        return;
    }

}
