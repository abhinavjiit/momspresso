package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.facebook.applinks.AppLinkData;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.newmodels.ForceUpdateModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ForceUpdateAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.sync.CategorySyncService;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.utils.AppUtils;
import com.smartlook.sdk.smartlook.Smartlook;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import io.branch.referral.Branch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SplashActivity extends BaseActivity {

    private String _deepLinkURL;
    Bundle extras;
    FirebaseAnalytics mFirebaseAnalytics;
    MixpanelAPI mixpanel;
    private String branchData;
    private Handler handler, handler1;

    // The onNewIntent() is overridden to get and resolve the data for deep linking
    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            _deepLinkURL = data;

            try {
                mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("_deeplinkurl", _deepLinkURL);
                jsonObject.put("manufacturer", Build.MANUFACTURER);
                jsonObject.put("model", Build.MODEL);
                mixpanel.track("DeepLink", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i("deepLinkUrl", _deepLinkURL);
            if (_deepLinkURL.contains(AppConstants.BRANCH_DEEPLINK) || _deepLinkURL.contains(AppConstants.BRANCH_DEEPLINK_URL)) {
                BaseApplication.getInstance().setBranchLink("true");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushAppOpenEvent(this, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        onNewIntent(getIntent());
//        AppUtils.printHashKey(this);
        extras = getIntent().getExtras();
        MobileAds.initialize(this, initializationStatus -> Log.d("Admob", "Initialized"));
        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        if (getIntent().getBooleanExtra("fromNotification", false)) {
            Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "default");
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        try {

            setContentView(R.layout.splash_activity);
            View mLayout = findViewById(R.id.rootLayout);
            ((BaseApplication) getApplication()).setView(mLayout);
            ((BaseApplication) getApplication()).setActivity(this);

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            Log.e("version number ", version);

            ImageView _spin = (ImageView) findViewById(R.id.spin);
            _spin.startAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.rotate_indefinitely));

            resumeSplash();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void resumeSplash() {
        String version = AppUtils.getAppVersion(this);
        Intent mServiceIntent = new Intent(SplashActivity.this, CategorySyncService.class);
        startService(mServiceIntent);
        if (ConnectivityUtils.isNetworkEnabled(SplashActivity.this)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            ForceUpdateAPI forceUpdateAPI = retrofit.create(ForceUpdateAPI.class);
            Call<ForceUpdateModel> call = forceUpdateAPI.checkForceUpdateRequired(version, "android");
            call.enqueue(checkForceUpdateResponseCallback);
        } else {
            if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                String message = SharedPrefUtils.getAppUgradeMessage(BaseApplication.getAppContext());
                showUpgradeAppAlertDialog("Momspresso", message, buttonId -> {
                });
                return;
            }
            navigateToNextScreen();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        handler = new Handler();
        handler.postDelayed(() -> Branch.getInstance().initSession((referringParams, error) -> {
            if (error == null) {
                String type = "";
                Log.i("BRANCH_SDK", referringParams.toString());
                branchData = referringParams.toString();
                try {
                    if (!StringUtils.isNullOrEmpty(referringParams.getString("type"))) {
                        BaseApplication.getInstance().setBranchData(branchData);
                        BaseApplication.getInstance().setBranchLink("true");
                    }
                } catch (Exception e) {
                    Log.e("Branch_Tag", e.getMessage());
                }
            } else {
                Log.i("BRANCH SDK", error.getMessage());
            }
        }, SplashActivity.this.getIntent().getData(), SplashActivity.this), 1000);

        AppLinkData.fetchDeferredAppLinkData(this, new AppLinkData.CompletionHandler() {
            @Override
            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                Log.e("FBDeferredDeepLink", "" + appLinkData);
                if (appLinkData != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("type", "personal_info");
                        BaseApplication.getInstance().setBranchData(jsonObject.toString());
                        BaseApplication.getInstance().setBranchLink("true");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void navigateToNextScreen() {
        UserInfo userInfo = SharedPrefUtils.getUserDetailModel(this);
        if (null != userInfo && !StringUtils.isNullOrEmpty(userInfo.getMc4kToken()) &&
                AppConstants.VALIDATED_USER.equals(userInfo.getIsValidated())) { // if he signup
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
            Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.getFollowedCategories(
                    SharedPrefUtils.getUserDetailModel(this).getDynamoId());
            call.enqueue(getFollowedTopicsResponseCallback);
        } else {
            Log.e("MYCITY4KIDS", "USER logged Out");
            if (SharedPrefUtils.getLogoutFlag(BaseApplication.getAppContext())) {
                Intent intent = new Intent(SplashActivity.this, ActivityLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Smartlook.setupAndStartRecording(getString(R.string.smart_look_key));
                Smartlook.enableCrashlytics(true);
                handler1 = new Handler();
                handler1.postDelayed(() -> runOnUiThread(() -> {
                    Intent intent = new Intent(SplashActivity.this, LanguageSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }), 1000);
            }
        }
        Log.d("GCM Token ", SharedPrefUtils.getDeviceToken(BaseApplication.getAppContext()));
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
                    SharedPrefUtils.setFollowedTopicsCount(BaseApplication.getAppContext(), mDatalist.size());
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
        if (!StringUtils.isNullOrEmpty(_deepLinkURL) && (_deepLinkURL.contains(AppConstants.BRANCH_DEEPLINK) || _deepLinkURL.contains(AppConstants.BRANCH_DEEPLINK_URL))) {
            handler1 = new Handler();
            handler1.postDelayed(() -> runOnUiThread(() -> {
                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                intent.putExtra("branchLink", _deepLinkURL);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }), 1000);
        } else {
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
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    Callback<ForceUpdateModel> checkForceUpdateResponseCallback = new Callback<ForceUpdateModel>() {
        @Override
        public void onResponse(Call<ForceUpdateModel> call, retrofit2.Response<ForceUpdateModel> response) {
            if (response.body() == null) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                ForceUpdateModel responseData = response.body();
                if (responseData.getResponseCode() == 200) {
                    if (responseData.getResult().getData().getIsAppUpdateRequired() == 1) {
                        SharedPrefUtils.setAppUgrade(BaseApplication.getAppContext(), true);
                        String message = responseData.getResult().getData().getMessage();
                        SharedPrefUtils.setAppUgradeMessage(BaseApplication.getAppContext(), message);
                        showUpgradeAppAlertDialog("Momspresso", SharedPrefUtils.getAppUgradeMessage(BaseApplication.getAppContext()), new OnButtonClicked() {
                            @Override
                            public void onButtonCLick(int buttonId) {
                            }
                        });
                    } else {
                        SharedPrefUtils.setAppUgrade(BaseApplication.getAppContext(), false);
                        navigateToNextScreen();
                    }
                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        showToast(message);
                    } else {
                        showToast(getString(R.string.went_wrong));
                    }
                } else {
                    SharedPrefUtils.setAppUgrade(BaseApplication.getAppContext(), false);
                    navigateToNextScreen();
                }
            } catch (Exception e) {
                showToast(getString(R.string.went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                //Uncomment to run on phoenix
                SharedPrefUtils.setAppUgrade(BaseApplication.getAppContext(), false);
                navigateToNextScreen();
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
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (handler1 != null) {
            handler1.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}