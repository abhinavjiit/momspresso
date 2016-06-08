package com.mycity4kids.ui.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.fragmentdialog.FragmentAlertDialog;
import com.mycity4kids.gtmutils.ContainerHolderSingleton;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.newmodels.ForceUpdateModel;
import com.mycity4kids.newmodels.UserInviteModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ForceUpdateAPI;
import com.mycity4kids.utils.NearMyCity;
import com.mycity4kids.utils.location.GPSTracker;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SplashActivity extends BaseActivity {
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
   /*     if (intent.getExtras()!=null&&intent.getExtras().getString("articleId")!=null)
        {
            if (intent.getExtras().getString("type").equalsIgnoreCase("article"))
            {  Log.e("extra New Intent ", intent.getExtras().toString());
            String articleId=intent.getExtras().getString("articleId");
//            Log.e("articleId",articleId);
            Intent intent1=new Intent(SplashActivity.this, ArticlesAndBlogsDetailsActivity.class);
            intent1.putExtra("article_id",articleId);
            intent1.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
            startActivity(intent1);
            finish();
                return;
        }}*/
      //  super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(SplashActivity.this, "Splash Screen", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        onNewIntent(getIntent());
        extras=getIntent().getExtras();
        setUpGTM();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setUserProperty("CityId","1");
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // mUrl = Uri.parse("http://www.mycity4kids.com/parenting/kalpana---without-boundaries/article/From-the-Bicycle-to-the-Recycle-days...");
        mUrl = Uri.parse("android-app://com.mycity4kids/http/mycity4kids.com");
        mTitle = "mycity4kids";
        mDescription = "Parenting made easy with Mommy Blogs, Kids Activities and Family Organizer";
        try {

            setContentView(R.layout.splash_activity);

           /* AnalyticsHelper.logEvent("Application Launch...");*/

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
                            mFirebaseAnalytics.setUserProperty("CityId",cityId+"");
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
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ForceUpdateAPI forceUpdateAPI = retrofit.create(ForceUpdateAPI.class);
                    Call<ForceUpdateModel> call = forceUpdateAPI.checkForceUpdateRequired(version, "android");
                    call.enqueue(checkForceUpdateResponseCallback);

//                    final Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            isFirstLaunch = 0;
//                            navigateToNextScreen(true);
//                        }
//                    }, 500);

                    //ToastUtils.showToast(SplashActivity.this, getString(R.string.error_network));
                    //return;
                } else {
                    if (SharedPrefUtils.getAppUpgrade(SplashActivity.this)) {
                        String message = SharedPrefUtils.getAppUgradeMessage(SplashActivity.this);
                        showUpgradeAppAlertDialog("mycity4kids", message, new OnButtonClicked() {
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
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getAction());
    }

    @Override
    public void onStop() {
        AppIndex.AppIndexApi.end(mClient, getAction());
        mClient.disconnect();
        super.onStop();
    }

    private void navigateToNextScreen(boolean isConfigurationAvailable) {

        UserInfo userInfo = SharedPrefUtils.getUserDetailModel(this);
        TableAdult _table = new TableAdult(BaseApplication.getInstance());
        if (null != userInfo && !StringUtils.isNullOrEmpty(userInfo.getEmail())) { // if he signup
            Log.e("MYCITY4KIDS", "USER logged In");

            startSyncing();
            startSyncingUserInfo();

            if (StringUtils.isNullOrEmpty("" + userInfo.getFamily_id()) ||
                    userInfo.getFamily_id() == 0) {
                String userFamilyInvites = SharedPrefUtils.getUserFamilyInvites(this);
                UserInviteModel userInviteModel = new Gson().fromJson(userFamilyInvites, UserInviteModel.class);
                if (null != userFamilyInvites && !userFamilyInvites.isEmpty()
                        && (null != userInviteModel.getFamilyInvites() && !userInviteModel.getFamilyInvites().isEmpty())) {
                    //User Signed in but has not created family.
                    Intent intent = new Intent(SplashActivity.this, ListFamilyInvitesActivity.class);
                    intent.putParcelableArrayListExtra("familyInvites", userInviteModel.getFamilyInvites());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                    if (!StringUtils.isNullOrEmpty(_deepLinkURL)) {
                        intent.putExtra(AppConstants.DEEP_LINK_URL, _deepLinkURL);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP/*|Intent.FLAG_ACTIVITY_NEW_TASK*/);
                    }
                    startActivity(intent);
                    finish();
                }

            } else {
                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                if (!StringUtils.isNullOrEmpty(_deepLinkURL)) {
                    intent.putExtra(AppConstants.DEEP_LINK_URL, _deepLinkURL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP/*|Intent.FLAG_ACTIVITY_NEW_TASK*/);
                } else if (extras!=null && extras.getString("type")!=null)
                {
                    //String articleId=extras.getString("articleId");
                   intent.putExtra("notificationExtras",extras);
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

                Intent intent;
                if (SharedPrefUtils.getLogoutFlag(this))
                    intent = new Intent(SplashActivity.this, ActivityLogin.class);
                else
                    intent = new Intent(SplashActivity.this, TutorialActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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
                showToast("Something went wrong from server");
                return;
            }
            try {
                ForceUpdateModel responseData = (ForceUpdateModel) response.body();
                if (responseData.getResponseCode() == 200) {
                    if (responseData.getResult().getData().getIsAppUpdateRequired() == 1) {
                        SharedPrefUtils.setAppUgrade(SplashActivity.this, true);
                        String message = responseData.getResult().getData().getMessage();
                        SharedPrefUtils.setAppUgradeMessage(SplashActivity.this, message);
                        showUpgradeAppAlertDialog("mycity4kids", SharedPrefUtils.getAppUgradeMessage(SplashActivity.this), new OnButtonClicked() {
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
            } catch (Exception e) {
                e.printStackTrace();
                showToast(getString(R.string.went_wrong));
            }

        }

        @Override
        public void onFailure(Call<ForceUpdateModel> call, Throwable t) {
            showToast(getString(R.string.went_wrong));
        }
    };
}
