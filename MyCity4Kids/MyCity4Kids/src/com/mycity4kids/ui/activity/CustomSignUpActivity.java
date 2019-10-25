package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.LoginRegistrationRequest;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class CustomSignUpActivity extends BaseActivity implements View.OnClickListener {

    private UserInfo model;

    private EditText emailEditText, passwordEditText;
    private TextView continueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_signup_activity);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        continueTextView = findViewById(R.id.continueTextView);

        continueTextView.setOnClickListener(this);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        if (isDataValid()) {
            showProgressDialog(getString(R.string.please_wait));

            LoginRegistrationRequest lr = new LoginRegistrationRequest();
            lr.setEmail("nananaa@gmail.com");
            lr.setPassword(passwordEditText.getText().toString());
            lr.setRequestMedium("custom");

            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
            Call<UserDetailResponse> call = loginRegistrationAPI.login(lr);
            call.enqueue(onLoginResponseReceivedListener);
        }
    }

    Callback<UserDetailResponse> onLoginResponseReceivedListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            Log.d("SUCCESS", "" + response);
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    createUser();
                } else {
                    removeProgressDialog();
                    showToast("Incorrect Credentials");
                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
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

    private void createUser() {
        RegistrationRequest lr = new RegistrationRequest();
        lr.setEmail(emailEditText.getText().toString());
        lr.setPassword("password");

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = loginRegistrationAPI.customRegistration(lr);
        call.enqueue(onRegistrationResponseListener);
    }

    private Callback<UserDetailResponse> onRegistrationResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            Log.d("SUCCESS", "" + response);
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    model = new UserInfo();
                    model.setId(responseData.getData().get(0).getResult().getId());
                    model.setDynamoId(responseData.getData().get(0).getResult().getDynamoId());
                    model.setEmail(responseData.getData().get(0).getResult().getEmail());
                    model.setMc4kToken(responseData.getData().get(0).getResult().getMc4kToken());
                    model.setIsValidated(responseData.getData().get(0).getResult().getIsValidated());
                    model.setFirst_name(responseData.getData().get(0).getResult().getFirstName());
                    model.setLast_name(responseData.getData().get(0).getResult().getLastName());
                    model.setUserType(responseData.getData().get(0).getResult().getUserType());
                    model.setProfilePicUrl(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    model.setSessionId(responseData.getData().get(0).getResult().getSessionId());

                    model.setBlogTitle(responseData.getData().get(0).getResult().getBlogTitle());

                    SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), model);
                    SharedPrefUtils.setProfileImgUrl(BaseApplication.getAppContext(), responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());

                    if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                        //token already expired or not yet connected with facebook
                        SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(), "1");
                    } else {
                        SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(),
                                responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                    }

                    validateUser();
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
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

    private void validateUser() {
        UpdateUserDetailsRequest validateUserRequest = new UpdateUserDetailsRequest();
        validateUserRequest.setIsValidated("1");

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateProfile(validateUserRequest);
        call.enqueue(userDetailsUpdateResponseListener);
    }

    private Callback<UserDetailResponse> userDetailsUpdateResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            Log.d("SUCCESS", "" + response);
            removeProgressDialog();
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    model.setIsValidated("1");
                    SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), model);
                    Intent intent1 = new Intent(CustomSignUpActivity.this, LoadingActivity.class);
                    startActivity(intent1);
                }
            } catch (Exception e) {
                Log.d("MC4kException", Log.getStackTraceString(e));
                Crashlytics.logException(e);
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

    private boolean isDataValid() {
        boolean isLoginOk = true;
        String email_id = emailEditText.getText().toString().trim();
        if (email_id.trim().length() == 0 || !StringUtils.isValidEmail(email_id) || !email_id.endsWith("mc4k.com")) {
            emailEditText.setFocusableInTouchMode(true);
            emailEditText.setError(getString(R.string.enter_valid_email));
            emailEditText.requestFocus();
            isLoginOk = false;
        } else if (passwordEditText.getText().toString().length() == 0) {
            passwordEditText.setFocusableInTouchMode(true);
            passwordEditText.requestFocus();
            passwordEditText.setError("Password can't be left blank");
            //passwordEditText.requestFocus();
            isLoginOk = false;
        } else if (passwordEditText.getText().toString().length() < 1) {
            passwordEditText.setFocusableInTouchMode(true);
            passwordEditText.requestFocus();
            passwordEditText.setError("Password should not less than 5 character.");
            isLoginOk = false;
        }
        return isLoginOk;
    }


    public class RegistrationRequest {

        private String firstName = "fn", lastName = "ln";
        private String cityId = "1";
        private String email;
        private String password;
        private String requestMedium = "custom";

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRequestMedium() {
            return requestMedium;
        }

        public void setRequestMedium(String requestMedium) {
            this.requestMedium = requestMedium;
        }
    }
}