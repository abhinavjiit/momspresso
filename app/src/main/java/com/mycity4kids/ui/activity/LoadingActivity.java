package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by kapil.vij on 13-08-2015.
 */
public class LoadingActivity extends BaseActivity {

    private RelativeLayout root;
    String type = "";
    private static final int UPDATE_USER_HANDLE = 1001;

    private String loginMode = "";
    private String isUserHandleUpdated = "";
    private String userHandle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushGenericEvent(this, "Post_signup_singin_loading_event",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "LoadingActivity");
        setContentView(R.layout.fetch_pincode_config);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        isUserHandleUpdated = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                .getIsUserHandleUpdated();

        if ((StringUtils.isNullOrEmpty(isUserHandleUpdated) || isUserHandleUpdated.equals("0")) && !loginMode
                .equals("email")) {
            Intent intent = new Intent(LoadingActivity.this, UpdateUserHandleActivity.class);
            startActivityForResult(intent, UPDATE_USER_HANDLE);
        } else {
            if (!ConnectivityUtils.isNetworkEnabled(LoadingActivity.this)) {
                navigateToDashboard();
                return;
            }
            type = BaseApplication.getInstance().getBranchLink();
            navigateToDashboard();
        }
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);//64206,0  -1
        if (_resultCode == RESULT_OK) {
            type = BaseApplication.getInstance().getBranchLink();
            navigateToDashboard();
        }
    }

    public void navigateToDashboard() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.getFollowedCategories(
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        call.enqueue(getFollowedTopicsResponseCallback);
    }

    private Callback<FollowUnfollowCategoriesResponse> getFollowedTopicsResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call,
                retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<String> mDatalist = (ArrayList<String>) responseData.getData();
                    if (mDatalist != null) {
                        ArrayList<String> topicList = (ArrayList<String>) responseData.getData();
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        int version = pInfo.versionCode;
                        if (version > 100 && (topicList == null || topicList.size() < 1)) {
                            SharedPrefUtils.setFollowTopicApproachChangeFlag(BaseApplication.getAppContext(), true);
                        }
                        SharedPrefUtils.setFollowedTopicsCount(BaseApplication.getAppContext(), topicList.size());
                    }
                } else {
                    showToast(responseData.getReason());
                }

                Intent intent = new Intent(LoadingActivity.this, DashboardActivity.class);
                if (!StringUtils.isNullOrEmpty(type) && type.equals("true")) {
                    intent.putExtra("branchLink", AppConstants.BRANCH_DEEPLINK);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Intent intent = new Intent(LoadingActivity.this, DashboardActivity.class);
                if (!StringUtils.isNullOrEmpty(type) && type.equals("true")) {
                    intent.putExtra("branchLink", AppConstants.BRANCH_DEEPLINK);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            Intent intent = new Intent(LoadingActivity.this, DashboardActivity.class);
            if (!StringUtils.isNullOrEmpty(type) && type.equals("true")) {
                intent.putExtra("branchLink", AppConstants.BRANCH_DEEPLINK);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    };

}
