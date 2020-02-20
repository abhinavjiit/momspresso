package com.mycity4kids.ui.activity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.ChangePasswordRequest;
import com.mycity4kids.models.response.ChangePasswordResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 19/7/17.
 */
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private TextView emailTextView, saveTextView;
    private Toolbar toolbar;
    private RelativeLayout root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_tab_fragment);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        currentPasswordEditText = (EditText) findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = (EditText) findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        saveTextView = (TextView) findViewById(R.id.saveTextView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        emailTextView.setText(SharedPrefUtils.getUserDetailModel(this).getEmail());

        saveTextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveTextView:
                if (validateFields()) {
                    updatePassword();
                }
                break;
        }
    }

    private void updatePassword() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(currentPasswordEditText.getText().toString());
        changePasswordRequest.setNewPassword(newPasswordEditText.getText().toString());

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<ChangePasswordResponse> call = loginRegistrationAPI.changePassword(changePasswordRequest);
        call.enqueue(passwordChangeResponseListener);
    }

    private Callback<ChangePasswordResponse> passwordChangeResponseListener = new Callback<ChangePasswordResponse>() {
        @Override
        public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
            if (response == null || response.body() == null) {
//                showToast(getString(R.string.went_wrong));
                return;
            }
            ChangePasswordResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.app_settings_change_pass_pass_update_success), Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.app_settings_change_pass_pass_update_fail), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private boolean validateFields() {
        if (StringUtils.isNullOrEmpty(currentPasswordEditText.getText().toString().trim())) {
            Toast.makeText(ChangePasswordActivity.this, getString(R.string.app_settings_change_pass_toast_current_pass_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (StringUtils.isNullOrEmpty(newPasswordEditText.getText().toString().trim())) {
            Toast.makeText(ChangePasswordActivity.this, getString(R.string.app_settings_change_pass_toast_new_pass_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
            Toast.makeText(ChangePasswordActivity.this, getString(R.string.app_settings_change_pass_toast_confirm_pass_match), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
