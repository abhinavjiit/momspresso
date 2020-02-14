package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.utils.StringUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.google.GooglePlusUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IFacebookUser;
import com.mycity4kids.models.request.LoginRegistrationRequest;
import com.mycity4kids.models.request.PhoneLoginRequest;
import com.mycity4kids.models.request.SocialConnectRequest;
import com.mycity4kids.models.response.BaseResponse;
import com.mycity4kids.models.response.FBPhoneLoginResponse;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.sync.CategorySyncService;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.ui.fragment.ChooseLoginAccountDialogFragment;
import com.mycity4kids.ui.fragment.FacebookAddEmailDialogFragment;
import com.mycity4kids.ui.fragment.SignInFragment;
import com.mycity4kids.ui.fragment.SignUpFragment;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by Hemant Parmar on 19-06-2016.
 */
public class ActivityLogin extends BaseActivity implements IFacebookUser {

    public static final int RECOVERABLE_REQUEST_CODE = 98;
    private static final int RC_SIGN_IN = 9001;
    public ChooseLoginAccountDialogFragment chooseLoginAccountFragment;
    private GoogleSignInClient mSignInClient;
    private String googleEmailId;
    private String accessToken = "";
    private String googleToken = "";
    FacebookAddEmailDialogFragment dialogFragment;

    private String loginMode = "";
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_loginform);
        View root = findViewById(R.id.content_frame);
        ((BaseApplication) getApplication()).setActivity(this);
        ((BaseApplication) getApplication()).setView(root);
        Utils.pushOpenScreenEvent(this, "LoginSignUpScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestScopes(new Scope(Scopes.PLUS_ME))
                .build();

        GoogleSignInOptions options =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(new Scope(Scopes.PLUS_ME))
                        .build();

        mSignInClient = GoogleSignIn.getClient(this, options);
        callbackManager = CallbackManager.Factory.create();

        String fragmentToLaunch = getIntent().getStringExtra(AppConstants.LAUNCH_FRAGMENT);
        if (AppConstants.FRAGMENT_SIGNIN.equals(fragmentToLaunch)) {
            SignInFragment fragment = new SignInFragment();
            replaceFragment(fragment, null, true);
        } else if (AppConstants.FRAGMENT_SIGNUP.equals(fragmentToLaunch)) {
            SignUpFragment fragment = new SignUpFragment();
            replaceFragment(fragment, null, true);
        } else {
            SignInFragment fragment = new SignInFragment();
            replaceFragment(fragment, null, true);
        }
    }

    public void loginWithFacebook() {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            showProgressDialog(getString(R.string.please_wait));
            FacebookUtils.facebookLogin(this, this);
        } else {
            showToast(getString(R.string.error_network));
        }
    }

    public void loginWithGplus() {
        Intent intent = mSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    public void phoneLogin(String authCode) {
        loginMode = "phone";
        showProgressDialog(getString(R.string.please_wait));
        Log.d("PhoneToken", authCode);
        PhoneLoginRequest phoneLoginRequest = new PhoneLoginRequest();
        phoneLoginRequest.setAuth_token(authCode);
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<FBPhoneLoginResponse> call = loginRegistrationAPI.loginWithPhoneToken(phoneLoginRequest);
        call.enqueue(fbPhoneLoginResponseCallback);
    }

    private Callback<FBPhoneLoginResponse> fbPhoneLoginResponseCallback = new Callback<FBPhoneLoginResponse>() {
        @Override
        public void onResponse(Call<FBPhoneLoginResponse> call, retrofit2.Response<FBPhoneLoginResponse> response) {
            Log.d("SUCCESS", "" + response);
            removeProgressDialog();
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FBPhoneLoginResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (responseData.getData().get(0).getResult().size() > 1) {
                        chooseLoginAccountFragment = new ChooseLoginAccountDialogFragment();
                        Bundle _args = new Bundle();
                        _args.putParcelableArrayList("accountList", (ArrayList<? extends Parcelable>) responseData.getData().get(0).getResult());
                        chooseLoginAccountFragment.setArguments(_args);
                        FragmentManager fm = getSupportFragmentManager();
                        chooseLoginAccountFragment.setCancelable(false);
                        chooseLoginAccountFragment.show(fm, "Accounts");
                    } else {
                        loginWithAccount(responseData.getData().get(0).getResult().get(0));
                    }
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
        public void onFailure(Call<FBPhoneLoginResponse> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    public void loginRequest(String email_id, String password) {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            showProgressDialog(getString(R.string.please_wait));
            loginMode = "email";
            LoginRegistrationRequest lr = new LoginRegistrationRequest();
            lr.setEmail(email_id);
            lr.setPassword(password);
            lr.setRequestMedium("custom");

            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
            Call<UserDetailResponse> call = loginRegistrationAPI.login(lr);
            call.enqueue(onLoginResponseReceivedListener);
        } else {
            showToast(getString(R.string.error_network));
        }
    }

    @Override
    public void getFacebookUser(JSONObject jObject, String user) {
        try {
            if (user != null) {
                loginMode = "fb";
                LoginRegistrationRequest lr = new LoginRegistrationRequest();
                lr.setCityId("" + SharedPrefUtils.getCurrentCityModel(this).getId());
                lr.setRequestMedium("fb");
                lr.setSocialToken(user);

                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
                Call<UserDetailResponse> call = loginRegistrationAPI.login(lr);
                call.enqueue(onLoginResponseReceivedListener);
            }
        } catch (Exception e) {
            removeProgressDialog();
            showToast(getString(R.string.toast_response_error));
        }
    }

    public void getGooglePlusInfo(GoogleSignInAccount result) {
        try {
            if (isFinishing())
                return;

            loginMode = "gp";
            googleEmailId = result.getEmail();
            if (StringUtils.isNullOrEmpty(googleEmailId)) {
                googleEmailId = "Email not fetch from google.";
            }
            new GetGoogleToken().execute();
        } catch (Exception e) {
            removeProgressDialog();
            showToast(getString(R.string.toast_response_error));
            e.printStackTrace();
        }
    }

    public void loginWithAccount(UserDetailResult userDetailResult) {
        UserInfo model = new UserInfo();
        model.setId(userDetailResult.getId());
        model.setDynamoId(userDetailResult.getDynamoId());
        model.setEmail(userDetailResult.getEmail());
        model.setMc4kToken(userDetailResult.getMc4kToken());
        model.setIsValidated(userDetailResult.getIsValidated());
        model.setFirst_name(userDetailResult.getFirstName());
        model.setLast_name(userDetailResult.getLastName());
        model.setProfilePicUrl(userDetailResult.getProfilePicUrl().getClientApp());
        model.setSessionId(userDetailResult.getSessionId());
        model.setIsLangSelection(userDetailResult.getIsLangSelection());
        model.setUserType(userDetailResult.getUserType());
        model.setBlogTitle(userDetailResult.getBlogTitle());
        model.setIsNewUser(userDetailResult.getIsNewUser());

        int cityIdFromLocation = SharedPrefUtils.getCurrentCityModel(ActivityLogin.this).getId();
        if (cityIdFromLocation == AppConstants.OTHERS_CITY_ID) {
            model.setCityId(userDetailResult.getCityId());
        }
        model.setSessionId(userDetailResult.getSessionId());
        model.setLoginMode(loginMode);
        SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), model);
        SharedPrefUtils.setProfileImgUrl(BaseApplication.getAppContext(), userDetailResult.getProfilePicUrl().getClientApp());
        SharedPrefUtils.setLastLoginTimestamp(BaseApplication.getAppContext(), System.currentTimeMillis());

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            jsonObject.put("loginFrom", loginMode);
            mixpanel.track("UserLogin", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == userDetailResult.getSocialTokens()) {
            //token already expired or not yet connected with facebook
            SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(), "1");
        } else {
            SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(),
                    userDetailResult.getSocialTokens().getFb().getIsExpired());
        }

        if ("fb".equals(loginMode)) {
            SocialConnectRequest socialConnectRequest = new SocialConnectRequest();
            socialConnectRequest.setToken(accessToken);
            socialConnectRequest.setReferer("fb");

            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            LoginRegistrationAPI socialConnectAPI = retrofit.create(LoginRegistrationAPI.class);
            Call<BaseResponse> socialConnectCall = socialConnectAPI.socialConnect(socialConnectRequest);
            socialConnectCall.enqueue(socialConnectResponseListener);
        }

        //facebook login with an account without email
        if (!AppConstants.VALIDATED_USER.equals(model.getIsValidated()) && "fb".equals(loginMode)) {
            dialogFragment = new FacebookAddEmailDialogFragment();
            dialogFragment.setTargetFragment(dialogFragment, 2);
            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.FROM_ACTIVITY, AppConstants.ACTIVITY_LOGIN);
            dialogFragment.setArguments(bundle);
            dialogFragment.show(getFragmentManager(), "verify email");
        } else {
            if ("phone".equals(loginMode) &&
                    ((StringUtils.isNullOrEmpty(userDetailResult.getFirstName()) && StringUtils.isNullOrEmpty(userDetailResult.getLastName()))
                            || userDetailResult.getFirstName().toUpperCase().contains("XXX"))) {
                Intent intent = new Intent(ActivityLogin.this, PushTokenService.class);
                startService(intent);
                Intent mServiceIntent = new Intent(ActivityLogin.this, CategorySyncService.class);
                startService(mServiceIntent);
                Intent intent1 = new Intent(ActivityLogin.this, PhoneLoginUserDetailActivity.class);
                startActivity(intent1);
                startSyncingUserInfo();
            } else {
                Intent intent = new Intent(ActivityLogin.this, PushTokenService.class);
                startService(intent);
                Intent mServiceIntent = new Intent(ActivityLogin.this, CategorySyncService.class);
                startService(mServiceIntent);
                Intent intent1 = new Intent(ActivityLogin.this, LoadingActivity.class);
                startActivity(intent1);
            }
        }
    }

    public class GetGoogleToken extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                googleToken = GoogleAuthUtil.getToken(ActivityLogin.this, googleEmailId, "oauth2:" + GooglePlusUtils.SCOPES);
                return googleToken;
            } catch (UserRecoverableAuthException userAuthEx) {
                userAuthEx.printStackTrace();
                Log.d("UserRecoverAuthExceptin", userAuthEx.toString());
                // Start the user recoverable action using the intent returned
                startActivityForResult(userAuthEx.getIntent(), RECOVERABLE_REQUEST_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (StringUtils.isNullOrEmpty(result))
                return;

            LoginRegistrationRequest lr = new LoginRegistrationRequest();
            lr.setCityId("" + SharedPrefUtils.getCurrentCityModel(ActivityLogin.this).getId());
            lr.setRequestMedium("gp");
            lr.setSocialToken(googleToken);

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
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    UserInfo model = new UserInfo();
                    model.setId(responseData.getData().get(0).getResult().getId());
                    model.setDynamoId(responseData.getData().get(0).getResult().getDynamoId());
                    model.setEmail(responseData.getData().get(0).getResult().getEmail());
                    model.setMc4kToken(responseData.getData().get(0).getResult().getMc4kToken());
                    model.setIsValidated(responseData.getData().get(0).getResult().getIsValidated());
                    model.setFirst_name(responseData.getData().get(0).getResult().getFirstName());
                    model.setLast_name(responseData.getData().get(0).getResult().getLastName());
                    model.setProfilePicUrl(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    model.setSessionId(responseData.getData().get(0).getResult().getSessionId());
                    model.setIsLangSelection(responseData.getData().get(0).getResult().getIsLangSelection());
                    model.setUserType(responseData.getData().get(0).getResult().getUserType());
                    model.setGender("" + responseData.getData().get(0).getResult().getGender());
                    model.setBlogTitle(responseData.getData().get(0).getResult().getBlogTitle());
                    model.setIsNewUser(responseData.getData().get(0).getResult().getIsNewUser());
                    int cityIdFromLocation = SharedPrefUtils.getCurrentCityModel(ActivityLogin.this).getId();
                    if (cityIdFromLocation == AppConstants.OTHERS_CITY_ID) {
                        model.setCityId(responseData.getData().get(0).getResult().getCityId());
                    }
                    model.setSessionId(responseData.getData().get(0).getResult().getSessionId());
                    model.setLoginMode(loginMode);
                    SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), model);
                    SharedPrefUtils.setProfileImgUrl(BaseApplication.getAppContext(), responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    SharedPrefUtils.setLastLoginTimestamp(BaseApplication.getAppContext(), System.currentTimeMillis());

                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("loginFrom", loginMode);
                        mixpanel.track("UserLogin", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                        //token already expired or not yet connected with facebook
                        SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(), "1");
                    } else {
                        SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(),
                                responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                    }

                    if ("fb".equals(loginMode)) {
                        SocialConnectRequest socialConnectRequest = new SocialConnectRequest();
                        socialConnectRequest.setToken(accessToken);
                        socialConnectRequest.setReferer("fb");

                        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                        LoginRegistrationAPI socialConnectAPI = retrofit.create(LoginRegistrationAPI.class);
                        Call<BaseResponse> socialConnectCall = socialConnectAPI.socialConnect(socialConnectRequest);
                        socialConnectCall.enqueue(socialConnectResponseListener);
                    }

                    //facebook login with an account without email
                    if (!AppConstants.VALIDATED_USER.equals(model.getIsValidated()) && "fb".equals(loginMode)) {
                        dialogFragment = new FacebookAddEmailDialogFragment();
                        dialogFragment.setTargetFragment(dialogFragment, 2);
                        Bundle bundle = new Bundle();
                        bundle.putString(AppConstants.FROM_ACTIVITY, AppConstants.ACTIVITY_LOGIN);
                        dialogFragment.setArguments(bundle);
                        dialogFragment.show(getFragmentManager(), "verify email");
                    }
                    //Custom sign up user but email is not yet verfifed.
                    else if (!AppConstants.VALIDATED_USER.equals(model.getIsValidated())) {
                        showVerifyEmailDialog("Error", "Please verify your account to login");
                    }
                    //Verified User
                    else {
                        Intent intent = new Intent(ActivityLogin.this, PushTokenService.class);
                        startService(intent);
                        Intent mServiceIntent = new Intent(ActivityLogin.this, CategorySyncService.class);
                        startService(mServiceIntent);
                        Intent intent1 = new Intent(ActivityLogin.this, LoadingActivity.class);
                        startActivity(intent1);
                        startSyncingUserInfo();
                    }
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

    private Callback<BaseResponse> socialConnectResponseListener = new Callback<BaseResponse>() {
        @Override
        public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
            Log.d("FacebookConnect", "SUCCESS");
        }

        @Override
        public void onFailure(Call<BaseResponse> call, Throwable t) {
            Log.d("FacebookConnect", "FAILURE");
        }
    };

    public void showVerifyEmailDialog(String title, String message) {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                .setNegativeButton(R.string.resend_email, (dialog, which) -> {
                    // resend verification link
                    LoginRegistrationRequest lr = new LoginRegistrationRequest();
                    lr.setEmail(SharedPrefUtils.getUserDetailModel(ActivityLogin.this).getEmail());

                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
                    Call<UserDetailResponse> call = loginRegistrationAPI.resendVerificationLink(lr);
                    call.enqueue(onVerifyEmailLinkResendResponseReceived);
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    Callback<UserDetailResponse> onVerifyEmailLinkResendResponseReceived = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }

            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
            showToast(getString(R.string.went_wrong));
        }
    };

    public void addEmail(String email) {
        showProgressDialog(getString(R.string.please_wait));
        String emailId = email;
        LoginRegistrationRequest lr = new LoginRegistrationRequest();
        lr.setEmail(emailId);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = loginRegistrationAPI.addFacebookEmail(lr);
        call.enqueue(onAddFacebookEmailResponseReceived);
    }

    public void cancelAddEmail() {
        removeProgressDialog();
        dialogFragment.dismiss();
    }

    Callback<UserDetailResponse> onAddFacebookEmailResponseReceived = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            Log.d("SUCCESS", "" + response);
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    dialogFragment.dismiss();
                    showToast(getString(R.string.verify_email));
                    Intent intent = new Intent(ActivityLogin.this, PushTokenService.class);
                    startService(intent);
                    Intent intent1 = new Intent(ActivityLogin.this, LoadingActivity.class);
                    startActivity(intent1);
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("Exception", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
            showToast(getString(R.string.went_wrong));
        }
    };

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);//64206,0  -1
        if (_requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(_data);
            if (task.isSuccessful()) {
                GoogleSignInAccount acct = task.getResult();
                getGooglePlusInfo(acct);
            } else {
                showToast(getString(R.string.toast_response_error));
            }
        } else if (_requestCode == RECOVERABLE_REQUEST_CODE) {
            removeProgressDialog();
            Bundle extra = _data.getExtras();
            googleToken = extra.getString("authtoken");
            if (StringUtils.isNullOrEmpty(googleToken))
                return;

            LoginRegistrationRequest lr = new LoginRegistrationRequest();
            lr.setCityId("" + SharedPrefUtils.getCurrentCityModel(this).getId());
            lr.setRequestMedium("gp");
            lr.setSocialToken(googleToken);
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
            Call<UserDetailResponse> call = loginRegistrationAPI.login(lr);
            call.enqueue(onLoginResponseReceivedListener);
        } else {
            if (_resultCode == 0) {
                removeProgressDialog();
            }
            callbackManager.onActivityResult(_requestCode, _resultCode, _data);
            FacebookUtils.onActivityResult(this, _requestCode, _resultCode, _data);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}