package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.StringUtils;

/**
 * Created by kapil.vij on 13-08-2015.
 */
public class LoadingActivity extends BaseActivity {

    private RelativeLayout root;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushGenericEvent(this, "Post_signup_singin_loading_event",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "LoadingActivity");
        setContentView(R.layout.fetch_pincode_config);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        FirebaseCrashlytics.getInstance().setUserId("" + SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        FirebaseCrashlytics.getInstance()
                .setCustomKey("email", "" + SharedPrefUtils.getUserDetailModel(this).getEmail());
        type = BaseApplication.getInstance().getBranchLink();
        navigateToDashboard();
    }

    public void navigateToDashboard() {
        Intent intent = new Intent(LoadingActivity.this, DashboardActivity.class);
        if (!StringUtils.isNullOrEmpty(type) && type.equals("true")) {
            intent.putExtra("branchLink", AppConstants.BRANCH_DEEPLINK);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
