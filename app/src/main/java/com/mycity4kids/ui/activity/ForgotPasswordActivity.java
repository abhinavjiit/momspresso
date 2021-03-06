package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonSyntaxException;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.LoginRegistrationRequest;
import com.mycity4kids.models.response.ForgotPasswordResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ForgotPasswordActivity extends BaseActivity {

    private EditText mEmailId;
    private Toolbar mToolbar;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_forgotpswd);
        root = findViewById(R.id.rootLayout);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(ForgotPasswordActivity.this, "ForgotPasswordScreen",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmailId = (EditText) findViewById(R.id.editEmail);

        getSupportActionBar().setTitle("Forgot Password");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        getMenuInflater().inflate(R.menu.forgot_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.save:

                if (isDataValid()) {
                    if (!ConnectivityUtils
                            .isNetworkEnabled(ForgotPasswordActivity.this)) {
                        ToastUtils.showToast(ForgotPasswordActivity.this,
                                getString(R.string.error_network));
                    } else {
                        showProgressDialog(getString(R.string.please_wait));

                        LoginRegistrationRequest lr = new LoginRegistrationRequest();
                        lr.setEmail(mEmailId.getText().toString().trim());

                        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
                        Call<ForgotPasswordResponse> call = loginRegistrationAPI.resetPassword(lr);
                        call.enqueue(onForgotPasswordResponseReceived);
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Callback<ForgotPasswordResponse> onForgotPasswordResponseReceived = new Callback<ForgotPasswordResponse>() {
        @Override
        public void onResponse(Call<ForgotPasswordResponse> call, retrofit2.Response<ForgotPasswordResponse> response) {
            Log.d("SUCCESS", "" + response);
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            try {
                ForgotPasswordResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    showToast(responseData.getData().get(0).getMsg());
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
            removeProgressDialog();
            if (t instanceof JsonSyntaxException) {
                Log.d("dawd", "dwad");
            }
            Log.d("MC4kException", Log.getStackTraceString(t));
            FirebaseCrashlytics.getInstance().recordException(t);
            showToast(getString(R.string.went_wrong));
        }
    };

    private boolean isDataValid() {
        boolean isForgotOk = true;
        String email_id = mEmailId.getText().toString().trim();

        if (email_id.trim().length() == 0
                || (!StringUtils.isValidEmail(email_id))) {
            mEmailId.setError("Please enter a valid email address");
            mEmailId.requestFocus();
            isForgotOk = false;
        }
        return isForgotOk;
    }


}
