package com.mycity4kids.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ForgotPasswordController;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.request.LoginRegistrationRequest;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.ui.fragment.FacebookAddEmailDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ForgotPasswordActivity extends BaseActivity {
    private EditText mEmailId;
    private ForgotPasswordController _controller;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_forgotpswd);
        Utils.pushOpenScreenEvent(ForgotPasswordActivity.this, "Forgot Password", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmailId = (EditText) findViewById(R.id.editEmail);
        _controller = new ForgotPasswordController(this, this);

        getSupportActionBar().setTitle("Forgot Family Password");

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
                        Call<UserDetailResponse> call = loginRegistrationAPI.resetPassword(lr);
                        call.enqueue(onForgotPasswordResponseReceived);
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Callback<UserDetailResponse> onForgotPasswordResponseReceived = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            Log.d("SUCCESS", "" + response);
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            try {
                UserDetailResponse responseData = (UserDetailResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
            showToast(getString(R.string.went_wrong));
        }
    };

    @Override
    protected void updateUi(Response response) {

    }

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
