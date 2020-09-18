package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.newmodels.ForceUpdateModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ForceUpdateAPI;
import com.mycity4kids.sync.CategorySyncService;
import com.mycity4kids.sync.FetchAdvertisementInfoService;
import com.mycity4kids.sync.FetchPublicIpAddressService;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.sync.SyncUserFollowingList;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.WebViewLocaleHelper;
import com.smartlook.sdk.smartlook.Smartlook;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import java.util.Locale;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SplashActivity extends BaseActivity {

    private String deepLinkUrl;
    Bundle extras;
    FirebaseAnalytics firebaseAnalytics;
    MixpanelAPI mixpanel;
    private String branchData;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }
        Utils.pushAppOpenEvent(this, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        //AppUtils.printHashKey(this);
        extras = getIntent().getExtras();
        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        //Android bug -- application locale resets to the device default workaround
        WebViewLocaleHelper helper = new WebViewLocaleHelper(this);
        helper.implementWorkaround();

        if (getIntent().getBooleanExtra("fromNotification", false)) {
            Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT,
                    SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "default");
        }

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        try {
            setContentView(R.layout.splash_activity);
            View mlayout = findViewById(R.id.rootLayout);
            ((BaseApplication) getApplication()).setView(mlayout);
            ((BaseApplication) getApplication()).setActivity(this);
            ImageView spin = findViewById(R.id.spin);
            spin.startAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.rotate_indefinitely));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        String action = getIntent().getAction();
        String data = getIntent().getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            deepLinkUrl = data;
        }
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener)
                .withData(getIntent() != null ? getIntent().getData() : null).init();
    }

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            if (error == null) {
                Log.e("BRANCH_SDK", linkProperties.toString());
                branchData = linkProperties.toString();
                try {
                    if (linkProperties.getBoolean("+clicked_branch_link")) {
                        if (!StringUtils.isNullOrEmpty(linkProperties.getString("type"))) {
                            BaseApplication.getInstance().setBranchData(branchData);
                            BaseApplication.getInstance().setBranchLink("true");
                        }
                    }
                    resumeSplash();
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    resumeSplash();
                }
            } else {
                Log.e("BRANCH SDK", error.getMessage());
                resumeSplash();
            }
        }
    };

    private void resumeSplash() {
        String version = AppUtils.getAppVersion(this);
        Intent serviceIntent = new Intent(SplashActivity.this, CategorySyncService.class);
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        if (ConnectivityUtils.isNetworkEnabled(SplashActivity.this)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            ForceUpdateAPI forceUpdateApi = retrofit.create(ForceUpdateAPI.class);
            Call<ForceUpdateModel> call = forceUpdateApi.checkForceUpdateRequired(version, "android");
            call.enqueue(checkForceUpdateResponseCallback);
        } else {
            if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                String message = SharedPrefUtils.getAppUgradeMessage(BaseApplication.getAppContext());
                showUpgradeAppAlertDialog("Momspresso", message);
                return;
            }
            navigateToNextScreen();
        }
    }

    private void navigateToNextScreen() {
        UserInfo userInfo = SharedPrefUtils.getUserDetailModel(this);
        if (null != userInfo && !StringUtils.isNullOrEmpty(userInfo.getMc4kToken())
                && AppConstants.VALIDATED_USER.equals(userInfo.getIsValidated())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(this, PushTokenService.class);
                startForegroundService(intent);
                Intent adIntent = new Intent(this, FetchAdvertisementInfoService.class);
                startForegroundService(adIntent);
                Intent ipIntent = new Intent(this, FetchPublicIpAddressService.class);
                startForegroundService(ipIntent);
                Intent followIntent = new Intent(this, SyncUserFollowingList.class);
                startForegroundService(followIntent);
            } else {
                Intent intent = new Intent(this, PushTokenService.class);
                startService(intent);
                Intent adIntent = new Intent(this, FetchAdvertisementInfoService.class);
                startService(adIntent);
                Intent ipIntent = new Intent(this, FetchPublicIpAddressService.class);
                startService(ipIntent);
                Intent followIntent = new Intent(this, SyncUserFollowingList.class);
                startService(followIntent);
            }
            startSyncingUserInfo();
            try {
                JSONObject prop = new JSONObject();
                prop.put("userId", SharedPrefUtils.getUserDetailModel(this).getDynamoId());
                prop.put("Language", Locale.getDefault().getLanguage());
                mixpanel.registerSuperProperties(prop);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
            gotoDashboard();
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
                Intent intent = new Intent(SplashActivity.this, LanguageSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
        Log.d("GCM Token ", SharedPrefUtils.getDeviceToken(BaseApplication.getAppContext()));
    }

    private void gotoDashboard() {
        if (!StringUtils.isNullOrEmpty(deepLinkUrl) && (deepLinkUrl.contains(AppConstants.BRANCH_DEEPLINK)
                || deepLinkUrl.contains(AppConstants.BRANCH_DEEPLINK_URL))) {
            Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
            intent.putExtra("branchLink", deepLinkUrl);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
            if (!StringUtils.isNullOrEmpty(deepLinkUrl)) {
                intent.putExtra(AppConstants.DEEP_LINK_URL, deepLinkUrl);
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
                        showUpgradeAppAlertDialog("Momspresso",
                                SharedPrefUtils.getAppUgradeMessage(BaseApplication.getAppContext())
                        );
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
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                SharedPrefUtils.setAppUgrade(BaseApplication.getAppContext(), false);
                navigateToNextScreen();
            }
        }

        @Override
        public void onFailure(Call<ForceUpdateModel> call, Throwable t) {
            showToast(getString(R.string.went_wrong));
            apiExceptions(t);
        }
    };
}
