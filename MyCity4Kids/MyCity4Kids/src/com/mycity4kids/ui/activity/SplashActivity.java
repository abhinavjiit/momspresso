package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.fragmentdialog.FragmentAlertDialog;
import com.mycity4kids.gtmutils.ContainerHolderSingleton;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.newmodels.ForceUpdateModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ForceUpdateAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.sync.CategorySyncService;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.LocaleManager;
import com.mycity4kids.utils.NearMyCity;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.location.GPSTracker;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SplashActivity extends BaseActivity {

    private static final int REQUEST_INIT_PERMISSION = 1;

    private static String[] PERMISSIONS_INIT = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private boolean isLocationScreen = false;
    private String _deepLinkURL;
    private int isFirstLaunch = 0;
    private String mobileNumberForVerification = "";
    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;
    Bundle extras;
    FirebaseAnalytics mFirebaseAnalytics;
    private View mLayout;
    private boolean shouldResumeSplash = false;
    MixpanelAPI mixpanel;

    // The onNewIntent() is overridden to get and resolve the data for deep linking
    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            _deepLinkURL = data;
        }
    }

    /*don't delete this function we can use this function to generate facebook hashcode*/
//    private void generateFacebookHashcode() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    getPackageName(),
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (Exception e) {
//
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushAppOpenEvent(this, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        onNewIntent(getIntent());
        //generateFacebookHashcode();
        extras = getIntent().getExtras();
        setUpGTM();
        MobileAds.initialize(this, getString(R.string.admob_id));
        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        if (getIntent().getBooleanExtra("fromNotification", false)) {
            Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "default");
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        /* mFirebaseAnalytics.setUserProperty("CityId","1");*/
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // mUrl = Uri.parse("http://www.mycity4kids.com/parenting/kalpana---without-boundaries/article/From-the-Bicycle-to-the-Recycle-days...");
        mUrl = Uri.parse("android-app://com.mycity4kids/http/momspresso.com");
        mTitle = "Momspresso";
        mDescription = "Parenting made easy with Mommy Blogs, Kids Activities and Family Organizer";
        try {

            setContentView(R.layout.splash_activity);
            mLayout = findViewById(R.id.rootLayout);
            /* AnalyticsHelper.logEvent("Application Launch...");*/

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            Log.e("version number ", version);

            if (!"0".equals(SharedPrefUtils.getUserDetailModel(this).getId()) && version.equals(AppConstants.GROUPS_COACHMARK_VERSION) && SharedPrefUtils.isGroupTourFirstLaunch(this)) {
                SharedPrefUtils.setCoachmarksShownFlag(this, "groups", false);
                SharedPrefUtils.setGroupTourFirstLaunch(this, false);
            }

            if (!"0".equals(SharedPrefUtils.getUserDetailModel(this).getId()) && version.equals(AppConstants.LOCALIZATION_RELEASE_VERSION) && SharedPrefUtils.isLocalizationFirstLaunch(this)) {
                SharedPrefUtils.setCoachmarksShownFlag(this, "home", false);
                SharedPrefUtils.setCoachmarksShownFlag(this, "topics", false);
                SharedPrefUtils.setCoachmarksShownFlag(this, "topics_article", false);
                SharedPrefUtils.setCoachmarksShownFlag(this, "article_details", false);
                SharedPrefUtils.setLocalizationFirstLaunch(this, false);
            }

            if (!"0".equals(SharedPrefUtils.getUserDetailModel(this).getId()) && version.equals(AppConstants.FACEBOOK_CONNECT_RELEASE_VERSION) && SharedPrefUtils.isFBConnectFirstLaunch(this)) {
                SharedPrefUtils.clearPrefrence(this);
                SharedPrefUtils.setFBConnectFirstLaunch(this, false);
            }

            ImageView _spin = (ImageView) findViewById(R.id.spin);
            _spin.startAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.rotate_indefinitely));

            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.i("PERMISSIONS", "storage permissions has NOT been granted. Requesting permissions.");
                    requestLocationAndStoragePermissions();
                } else {
                    resumeSplash();
                }
            } else {
                resumeSplash();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldResumeSplash) {
            resumeSplash();
        }
    }

    private void resumeSplash() {

        String version = AppUtils.getAppVersion(this);

        final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(SplashActivity.this);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /**
         * for first time we will check that gps is enabled or not:
         */
        if (versionApiModel.getCategoryVersion() == 0.0 && versionApiModel.getCityVersion() == 0.0 && versionApiModel.getLocalityVersion() == 0.0) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {

                buildAlertMessageNoGps();
//                return;
            }
        }

        Intent mServiceIntent = new Intent(SplashActivity.this, CategorySyncService.class);
        //     mServiceIntent.setData(Uri.parse("test"));
        startService(mServiceIntent);

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
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    GPSTracker getCurrentLocation = new GPSTracker(this);
                    double _latitude = getCurrentLocation.getLatitude();
                    double _longitude = getCurrentLocation.getLongitude();

                    SharedPrefUtils.setUserLocationLatitude(this, _latitude);
                    SharedPrefUtils.setUserLocationLongitude(this, _longitude);
                    new NearMyCity(this, _latitude, _longitude, new NearMyCity.FetchCity() {

                        @Override
                        public void nearCity(City cityModel) {


                            int cityId = cityModel.getCityId();
                            // mFirebaseAnalytics = FirebaseAnalytics.getInstance(SplashActivity.this);
                            mFirebaseAnalytics.setUserProperty("CityId", cityId + "");
                            /**
                             * save current city id in shared preference
                             */
                            MetroCity model = new MetroCity();
                            model.setId(cityModel.getCityId());
                            model.setName(cityModel.getCityName());
                            model.setNewCityId(cityModel.getNewCityId());
                            /**
                             * this city model will be save only one time on splash:
                             */
                            SharedPrefUtils.setCurrentCityModel(SplashActivity.this, model);

                            if (cityId > 0) {
                                versionApiModel.setCityId(cityId);
                                mFirebaseAnalytics.setUserProperty("CityId", cityId + "");
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
                                Log.e("version number ", version);
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
                    versionApiModel.setCityId(SharedPrefUtils.getCurrentCityModel(this).getId());
                    mFirebaseAnalytics.setUserProperty("CityId", SharedPrefUtils.getCurrentCityModel(this).getId() + "");
                    versionApiModel.setAppUpdateVersion(version);
                    if (ConnectivityUtils.isNetworkEnabled(SplashActivity.this)) {
                        _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);
                        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                        ForceUpdateAPI forceUpdateAPI = retrofit.create(ForceUpdateAPI.class);
                        Call<ForceUpdateModel> call = forceUpdateAPI.checkForceUpdateRequired(version, "android");
                        call.enqueue(checkForceUpdateResponseCallback);
                    } else {
                        if (SharedPrefUtils.getAppUpgrade(SplashActivity.this)) {
                            String message = SharedPrefUtils.getAppUgradeMessage(SplashActivity.this);
                            showUpgradeAppAlertDialog("Momspresso", message, new OnButtonClicked() {
                                @Override
                                public void onButtonCLick(int buttonId) {
                                }
                            });
                            return;
                        }
                        isFirstLaunch = 0;
                        navigateToNextScreen(true);
                    }
                }
            } else {
                /**
                 * this will call every time on splash:
                 */
                versionApiModel.setCityId(SharedPrefUtils.getCurrentCityModel(this).getId());
                mFirebaseAnalytics.setUserProperty("CityId", SharedPrefUtils.getCurrentCityModel(this).getId() + "");
                versionApiModel.setAppUpdateVersion(version);
                if (ConnectivityUtils.isNetworkEnabled(SplashActivity.this)) {

                    _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ForceUpdateAPI forceUpdateAPI = retrofit.create(ForceUpdateAPI.class);
                    Call<ForceUpdateModel> call = forceUpdateAPI.checkForceUpdateRequired(version, "android");
                    call.enqueue(checkForceUpdateResponseCallback);
                } else {
                    if (SharedPrefUtils.getAppUpgrade(SplashActivity.this)) {
                        String message = SharedPrefUtils.getAppUgradeMessage(SplashActivity.this);
                        showUpgradeAppAlertDialog("Momspresso", message, new OnButtonClicked() {
                            @Override
                            public void onButtonCLick(int buttonId) {
                            }
                        });
                        return;
                    }
                    isFirstLaunch = 0;
                    navigateToNextScreen(true);
                }
            }
        }else{
            versionApiModel.setCityId(SharedPrefUtils.getCurrentCityModel(this).getId());
            mFirebaseAnalytics.setUserProperty("CityId", SharedPrefUtils.getCurrentCityModel(this).getId() + "");
            versionApiModel.setAppUpdateVersion(version);
            if (ConnectivityUtils.isNetworkEnabled(SplashActivity.this)) {
                _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                ForceUpdateAPI forceUpdateAPI = retrofit.create(ForceUpdateAPI.class);
                Call<ForceUpdateModel> call = forceUpdateAPI.checkForceUpdateRequired(version, "android");
                call.enqueue(checkForceUpdateResponseCallback);
            } else {
                if (SharedPrefUtils.getAppUpgrade(SplashActivity.this)) {
                    String message = SharedPrefUtils.getAppUgradeMessage(SplashActivity.this);
                    showUpgradeAppAlertDialog("Momspresso", message, new OnButtonClicked() {
                        @Override
                        public void onButtonCLick(int buttonId) {
                        }
                    });
                    return;
                }
                isFirstLaunch = 0;
                navigateToNextScreen(true);
            }
        }
    }

    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(mUrl)
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Branch init
        Branch.getInstance().initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    Log.i("BRANCH SDK", referringParams.toString());
                    // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                    // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
                } else {
                    Log.i("BRANCH SDK", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);

        mClient.connect();
        if (!BuildConfig.DEBUG)
            AppIndex.AppIndexApi.start(mClient, getAction());
    }

    @Override
    public void onStop() {
        if (!BuildConfig.DEBUG)
            AppIndex.AppIndexApi.end(mClient, getAction());
        mClient.disconnect();
        super.onStop();
    }

    private void navigateToNextScreen(boolean isConfigurationAvailable) {

        UserInfo userInfo = SharedPrefUtils.getUserDetailModel(this);
        TableAdult _table = new TableAdult(BaseApplication.getInstance());
        if (null != userInfo && !StringUtils.isNullOrEmpty(userInfo.getMc4kToken()) && AppConstants.VALIDATED_USER.equals(userInfo.getIsValidated())) { // if he signup

         /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ServedService.class));
            } else {
                context.startService(new Intent(context, ServedService.class));
            }*/


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Intent intent5 = new Intent(this, PushTokenService.class);
                startForegroundService(intent5);
            } else {
                Intent intent5 = new Intent(this, PushTokenService.class);
                startService(intent5);
            }
            startSyncingUserInfo();

            try {
                JSONObject prop = new JSONObject();
                prop.put("userId", SharedPrefUtils.getUserDetailModel(this).getDynamoId());
                prop.put("lang", Locale.getDefault().getLanguage());
                mixpanel.registerSuperProperties(prop);
            } catch (Exception e) {

            }

            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
            Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.getFollowedCategories(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
            call.enqueue(getFollowedTopicsResponseCallback);
        } else {
            Log.e("MYCITY4KIDS", "USER logged Out");
            if (!isConfigurationAvailable) {
                showAlertDialog(getString(R.string.error), getString(R.string.server_went_wrong), new OnButtonClicked() {
                    @Override
                    public void onButtonCLick(int buttonId) {
                        finish();
                    }
                });
                return;
            } else {
                Intent intent;
                if (SharedPrefUtils.getLogoutFlag(this))
                    intent = new Intent(SplashActivity.this, ActivityLogin.class);
                else
                    intent = new Intent(SplashActivity.this, LanguageSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
        Log.d("GCM Token ", SharedPrefUtils.getDeviceToken(this));
    }

    /**
     * Requests the Storage permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestLocationAndStoragePermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i("Permissions",
                    "Displaying storage permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    })
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_location_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    })
                    .show();
        } else {
            requestUngrantedPermissions();
        }
    }

    private void requestUngrantedPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_INIT.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_INIT[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_INIT[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_INIT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_INIT_PERMISSION) {
            Log.i("Permissions", "Received response for storage permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                shouldResumeSplash = true;
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
                shouldResumeSplash = true;
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private Callback<FollowUnfollowCategoriesResponse> getFollowedTopicsResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast(getString(R.string.server_went_wrong));
                gotoDashboard();
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<String> mDatalist = (ArrayList<String>) responseData.getData();
                    SharedPrefUtils.setFollowedTopicsCount(SplashActivity.this, mDatalist.size());
                    gotoDashboard();
                } else {
                    gotoDashboard();
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                gotoDashboard();
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            gotoDashboard();
        }

    };

    private void gotoDashboard() {
        Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
        if (!StringUtils.isNullOrEmpty(_deepLinkURL)) {
            intent.putExtra(AppConstants.DEEP_LINK_URL, _deepLinkURL);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else if (extras != null && extras.getString("type") != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("notificationExtras", extras);
        }
        startActivity(intent);
        finish();
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

                    /**
                     * Save data into tables :-
                     */
                    HeavyDbTask _heavyDbTask = new HeavyDbTask(this,
                            _configurationResponse, new OnUIView() {

                        @Override
                        public void comeBackOnUI() {
                            if (isFirstLaunch == 1) {
                                PackageInfo pInfo = null;
                                try {
                                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                                String version = pInfo.versionName;
                                Log.e("version number ", version);

                                //First launch or logout or Shared prefs cleared scenario.
                                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                                ForceUpdateAPI forceUpdateAPI = retrofit.create(ForceUpdateAPI.class);
                                Call<ForceUpdateModel> call = forceUpdateAPI.checkForceUpdateRequired(version, "android");
                                call.enqueue(checkForceUpdateResponseCallback);
                            }

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

    private void setUpGTM() {
        TagManager tagManager = TagManager.getInstance(this);

        // Modify the log level of the logger to print out not only
        // warning and error messages, but also verbose, debug, info messages.
        tagManager.setVerboseLoggingEnabled(true);

        PendingResult<ContainerHolder> pending =
                tagManager.loadContainerPreferNonDefault(AppConstants.CONTAINER_ID,
                        R.raw.gtmms864s_v4);
        // The onResult method will be called as soon as one of the following happens:
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the 2-second timeout occurs
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                Container container = containerHolder.getContainer();
                if (!containerHolder.getStatus().isSuccess()) {
//                    Log.e("CuteAnimals", "failure loading container");
                    displayErrorToUser(R.string.load_error);
                    return;
                }
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                ContainerLoadedCallback.registerCallbacksForContainer(container);
                containerHolder.setContainerAvailableListener(new ContainerLoadedCallback());
                //startMainActivity();
            }
        }, 2, TimeUnit.SECONDS);
    }

    private static class ContainerLoadedCallback implements ContainerHolder.ContainerAvailableListener {
        @Override
        public void onContainerAvailable(ContainerHolder containerHolder, String containerVersion) {
            // We load each container when it becomes available.
            Container container = containerHolder.getContainer();
            registerCallbacksForContainer(container);
        }

        public static void registerCallbacksForContainer(Container container) {
            // Register two custom function call macros to the container.
            //       container.registerFunctionCallMacroCallback("increment", new CustomMacroCallback());
            //       container.registerFunctionCallMacroCallback("mod", new CustomMacroCallback());
            //       // Register a custom function call tag to the container.
            //       container.registerFunctionCallTagCallback("custom_tag", new CustomTagCallback());

        }

    }

    private void displayErrorToUser(int stringKey) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(getResources().getString(stringKey));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }

    Callback<ForceUpdateModel> checkForceUpdateResponseCallback = new Callback<ForceUpdateModel>() {
        @Override
        public void onResponse(Call<ForceUpdateModel> call, retrofit2.Response<ForceUpdateModel> response) {
            if (response == null) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                ForceUpdateModel responseData = response.body();
                if (responseData.getResponseCode() == 200) {
                    if (responseData.getResult().getData().getIsAppUpdateRequired() == 1) {
                        SharedPrefUtils.setAppUgrade(SplashActivity.this, true);
                        String message = responseData.getResult().getData().getMessage();
                        SharedPrefUtils.setAppUgradeMessage(SplashActivity.this, message);
                        showUpgradeAppAlertDialog("Momspresso", SharedPrefUtils.getAppUgradeMessage(SplashActivity.this), new OnButtonClicked() {
                            @Override
                            public void onButtonCLick(int buttonId) {
                            }
                        });
                        return;
                    } else {
                        SharedPrefUtils.setAppUgrade(SplashActivity.this, false);
                        isFirstLaunch = 0;
                        navigateToNextScreen(true);
                    }
                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        showToast(message);
                    } else {
                        showToast(getString(R.string.went_wrong));
                    }
                }
                //TODO to be removed used only because force update API not available on Phoenix.
                else {
                    SharedPrefUtils.setAppUgrade(SplashActivity.this, false);
                    isFirstLaunch = 0;
                    navigateToNextScreen(true);
                }
            } catch (Exception e) {
                showToast(getString(R.string.went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                //Uncomment to run on phoenix
                SharedPrefUtils.setAppUgrade(SplashActivity.this, false);
                isFirstLaunch = 0;
                navigateToNextScreen(true);

            }

        }

        @Override
        public void onFailure(Call<ForceUpdateModel> call, Throwable t) {
            showToast(getString(R.string.went_wrong));
        }
    };

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }
}
