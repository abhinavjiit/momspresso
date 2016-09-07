package com.mycity4kids.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.LoginController;
import com.mycity4kids.controller.UpdateMobileController;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.google.GooglePlusUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IFacebookUser;
import com.mycity4kids.interfaces.IPlusClient;
import com.mycity4kids.models.request.LoginRegistrationRequest;
import com.mycity4kids.models.response.KidsModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserRequest;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.ui.fragment.FacebookAddEmailDialogFragment;
import com.mycity4kids.widget.CustomFontEditText;
import com.mycity4kids.widget.CustomFontTextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by khushboo.goyal on 19-06-2015.
 */
public class ActivityLogin extends BaseActivity implements View.OnClickListener, IPlusClient, IFacebookUser {

    public static final int RECOVERABLE_REQUEST_CODE = 98;

    private GooglePlusUtils mGooglePlusUtils;
    private CustomFontEditText mEmailId, mPassword;
    CustomFontTextView signinTextView;
    private Toolbar mToolbar;
    LinearLayout forgotView;
    private boolean filterchange;
    private String socialEmailid = "";
    private String mobileNumberForVerification = "";
    private String googleEmailId, userId, currentPersonName, personPhotoUrl;

    private String accessToken = "";
    private String googleToken = "";
    private int changeBaseURL = 0;
    private GraphUser fbUser;
    FacebookAddEmailDialogFragment dialogFragment;

    private String loginMode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(ActivityLogin.this, "Login Screen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        setContentView(R.layout.aa_loginform);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign In");
        try {
            mEmailId = (CustomFontEditText) findViewById(R.id.email_login);
            mPassword = (CustomFontEditText) findViewById(R.id.password_login);
            signinTextView = (CustomFontTextView) findViewById(R.id.signinTextView);
            mEmailId.addTextChangedListener(mTextWatcher);
            mPassword.addTextChangedListener(mTextWatcher);
            ((CustomFontTextView) findViewById(R.id.orTextView)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeBaseURL++;
                    if (changeBaseURL > 9) {
                        if (BuildConfig.DEBUG) {
                            BaseApplication.changeApiBaseUrl();
                            showToast("changed baseurl to " + BaseApplication.getInstance().getRetrofit().baseUrl());
                            changeBaseURL = 0;
                        }
                    }
                }
            });

            signinTextView.setEnabled(false);

            signinTextView.setOnClickListener(this);
            ((CustomFontTextView) findViewById(R.id.forgot_password)).setOnClickListener(this);
            ((CustomFontTextView) findViewById(R.id.connect_facebook)).setOnClickListener(this);
            ((CustomFontTextView) findViewById(R.id.connect_googleplus)).setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        mGooglePlusUtils = new GooglePlusUtils(this, this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.connect_facebook:
                if (ConnectivityUtils.isNetworkEnabled(this)) {
                    showProgressDialog(getString(R.string.please_wait));
                    FacebookUtils.facebookLogin(this, this);
                } else {
                    showToast(getString(R.string.error_network));
                }
                break;

            case R.id.connect_googleplus:
                if (ConnectivityUtils.isNetworkEnabled(this)) {
                    showProgressDialog("Please Wait");
                    mGooglePlusUtils.googlePlusLogin();
                } else {
                    showToast(getString(R.string.error_network));
                }
                break;

            case R.id.forgot_password:
                intent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.signinTextView:
//                try {
                if (isDataValid()) {
                    if (ConnectivityUtils.isNetworkEnabled(this)) {
                        showProgressDialog(getString(R.string.please_wait));
                        //mProgressDialog=ProgressDialog.show(this, "", "Please Wait...",true,false);
                        loginMode = "email";
                        String emailId_or_mobile = mEmailId.getText().toString().trim();
                        String password = mPassword.getText().toString().trim();

//                            byte[] bytesOfMessage = null;
//                            MessageDigest md = null;
//
//                            md = MessageDigest.getInstance("MD5");
//                            bytesOfMessage = password.getBytes("UTF-8");
//
//                            byte[] thedigest = md.digest(bytesOfMessage);

                        LoginRegistrationRequest lr = new LoginRegistrationRequest();
                        lr.setEmail(emailId_or_mobile);
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
//                } catch (NoSuchAlgorithmException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                break;
            default:
                break;

        }
    }

    /**
     * this is a call back method which will give facebook user details
     */
    @Override
    public void getFacebookUser(GraphUser user) {
        //showProgressDialog(getString(R.string.please_wait));
        try {
            if (user != null) {
                fbUser = user;
                loginMode = "fb";

                Session session = Session.getActiveSession();
                if (session.isOpened()) {
                    accessToken = session.getAccessToken();
                }

                LoginRegistrationRequest lr = new LoginRegistrationRequest();
                lr.setCityId("" + SharedPrefUtils.getCurrentCityModel(this).getId());
                lr.setRequestMedium("fb");
                lr.setSocialToken(accessToken);

                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
                Call<UserDetailResponse> call = loginRegistrationAPI.login(lr);
                call.enqueue(onLoginResponseReceivedListener);

            }
        } catch (Exception e) {
            // e.printStackTrace();
            removeProgressDialog();
            showToast("Try again later.");
        }
    }

    @Override
    public void getGooglePlusInfo(GoogleApiClient plusClient) {
        try {
            if (isFinishing())
                return;
            showProgressDialog(getString(R.string.please_wait));
            final LoginController _controller = new LoginController(this, this);
            if (Plus.PeopleApi.getCurrentPerson(plusClient) != null) {
                loginMode = "gp";
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(plusClient);
                currentPersonName = currentPerson.getDisplayName();
                userId = currentPerson.getId();
                googleEmailId = Plus.AccountApi.getAccountName(plusClient);
                personPhotoUrl = currentPerson.getImage().getUrl();

                personPhotoUrl = personPhotoUrl.substring(0, personPhotoUrl.lastIndexOf("?"));

                if (StringUtils.isNullOrEmpty(googleEmailId)) {
                    googleEmailId = "Email not fetch from google.";
                }
                if (StringUtils.isNullOrEmpty(currentPersonName)) {
                    currentPersonName = getString(R.string.unknown_person);
                }
                if (StringUtils.isNullOrEmpty(userId)) {
                    userId = getString(R.string.unknown_person);
                }

                new GetGoogleToken().execute();

            } else {
                removeProgressDialog();
                Log.i("ActivityLogin", "GoogleApiClient persona information is null");
            }
            removeProgressDialog();
        } catch (Exception e) {
            removeProgressDialog();
            showToast("Try again later.");
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        //removeProgressDialog();
        if (mGooglePlusUtils != null)
            mGooglePlusUtils.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGooglePlusUtils != null)
            mGooglePlusUtils.onStart();

    }

    @Override
    protected void updateUi(Response response) {
    }

    @Override
    public void onGooglePlusLoginFailed() {
        removeProgressDialog();
    }

    private boolean isDataValid() {
        boolean isLoginOk = true;
        String email_id = mEmailId.getText().toString().trim();

        if (email_id.trim().length() == 0 || ((!StringUtils.isValidEmail(email_id)) && (!StringUtils.checkMobileNumber(email_id)))) {
            mEmailId.setFocusableInTouchMode(true);
            mEmailId.setError("Please enter valid email id");
            mEmailId.requestFocus();
            isLoginOk = false;
        } else if (mPassword.getText().toString().length() == 0) {
            mPassword.setFocusableInTouchMode(true);
            mPassword.requestFocus();
            mPassword.setError("Password can't be left blank");
            //mPassword.requestFocus();
            isLoginOk = false;
        } else if (mPassword.getText().toString().length() < 1) {
            mPassword.setFocusableInTouchMode(true);
            mPassword.requestFocus();

            mPassword.setError("Password should not less than 5 character.");
            //mPassword.requestFocus();
            isLoginOk = false;
        }
        return isLoginOk;
    }

    public void showSignUpDialog(String message, final UserResponse response) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage(message).setNegativeButton(android.R.string.ok
                , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        String data = "";
                        Intent intent = new Intent(ActivityLogin.this, ActivitySignUp.class);
                        intent.putExtra(Constants.SIGNUP_DATA, new Gson().toJson(response));
//                intent.putExtra(Constants.SIGNUP_FLAG, true);
                        startActivity(intent);
                        finish();


                    }
                }).setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                dialog.cancel();


            }
        }).setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alert11 = dialog.create();
        alert11.show();

        alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
        alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));

    }

    public void getMobileFromExistingUsers(String mobileNum) {
        mobileNumberForVerification = mobileNum;
        showProgressDialog(getString(R.string.please_wait));
        UserRequest _requestModel = new UserRequest();
        _requestModel.setUserId("" + SharedPrefUtils.getUserDetailModel(this).getId());
        _requestModel.setMobileNumber(mobileNumberForVerification);
        UpdateMobileController _controller = new UpdateMobileController(this, this);
        _controller.getData(AppConstants.UPDATE_MOBILE_FOR_EXISTING_USER_REQUEST, _requestModel);
    }


    public class GetGoogleToken extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                googleToken = GoogleAuthUtil.getToken(ActivityLogin.this, googleEmailId, "oauth2:" + GooglePlusUtils.SCOPES);

                System.out.println("token " + googleToken);
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
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            // check login here
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
                UserDetailResponse responseData = (UserDetailResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    UserInfo model = new UserInfo();
                    model.setId(responseData.getData().getResult().getId());
                    model.setDynamoId(responseData.getData().getResult().getDynamoId());
                    model.setEmail(responseData.getData().getResult().getEmail());
                    model.setMc4kToken(responseData.getData().getResult().getMc4kToken());
                    model.setIsValidated(responseData.getData().getResult().getIsValidated());
                    model.setFirst_name(responseData.getData().getResult().getFirstName() + " " + responseData.getData().getResult().getLastName());
                    model.setProfilePicUrl(responseData.getData().getResult().getProfilePicUrl().getClientApp());
                    SharedPrefUtils.setUserDetailModel(ActivityLogin.this, model);
                    SharedPrefUtils.setProfileImgUrl(ActivityLogin.this, responseData.getData().getResult().getProfilePicUrl().getClientApp());

                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String version = pInfo.versionName;
                    if (version.equals(AppConstants.PHOENIX_RELEASE_VERSION)) {
                        SharedPrefUtils.setPhoenixFirstLaunch(ActivityLogin.this, false);
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
                        if (null != responseData.getData().getResult().getKids()) {
                            saveKidsInformation(responseData.getData().getResult().getKids());
                        }
                        Intent intent = new Intent(ActivityLogin.this, PushTokenService.class);
                        startService(intent);
                        Intent intent1 = new Intent(ActivityLogin.this, LoadingActivity.class);
                        startActivity(intent1);
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

    private void saveKidsInformation(ArrayList<KidsModel> kidsList) {

        ArrayList<KidsInfo> kidsInfoArrayList = new ArrayList<>();

        if (kidsList.size() == 1 && StringUtils.isNullOrEmpty(kidsList.get(0).getName())) {
            return;
        }
        for (KidsModel kid : kidsList) {
            KidsInfo kidsInfo = new KidsInfo();
            kidsInfo.setName(kid.getName());
            kidsInfo.setDate_of_birth(convertTime(kid.getBirthDay()));
            kidsInfo.setColor_code(kid.getColorCode());
            kidsInfo.setGender(kid.getGender());

            kidsInfoArrayList.add(kidsInfo);
        }

        // saving child data
        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
        kidsTable.deleteAll();
        try {
            kidsTable.beginTransaction();
            for (KidsInfo kids : kidsInfoArrayList) {

                kidsTable.insertData(kids);

            }
            kidsTable.setTransactionSuccessful();
        } finally {
            kidsTable.endTransaction();
        }
    }

    public String convertTime(String time) {
        try {
            Date date = new Date(Long.parseLong(time));
            Format format = new SimpleDateFormat("dd-MM-yyyy");
            return format.format(date);
        } catch (NumberFormatException nfe) {
            return "";
        }
    }

    public void saveDatainDB(UserResponse model) {

//        UserTable table = new UserTable(BaseApplication.getInstance());
//        if (table.getRowsCount() > 0) {
//
//            try {
//                String profileimg = table.getAllUserData().getProfile().getProfile_image();
//                if (!StringUtils.isNullOrEmpty(profileimg)) {
//                    SharedPrefUtils.setProfileImgUrl(this, profileimg);
//                }
//            } catch (Exception e) {
//            }
//
//        }


//        TableAdult adultTable = new TableAdult((BaseApplication) getApplicationContext());
//        adultTable.deleteAll();
//        try {
//
//            adultTable.beginTransaction();
//            for (UserModel.AdultsInfo user : model.getResult().getData().getAdult()) {
//
//                adultTable.insertData(user.getUser());
//            }
//            adultTable.setTransactionSuccessful();
//        } finally {
//            adultTable.endTransaction();
//        }

        // saving child data
        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
        kidsTable.deleteAll();
        try {
            kidsTable.beginTransaction();
            for (KidsInfo kids : model.getResult().getData().getKidsInformation()) {

                kidsTable.insertData(kids);

            }
            kidsTable.setTransactionSuccessful();
        } finally {
            kidsTable.endTransaction();
        }

        // saving family

//        TableFamily familyTable = new TableFamily((BaseApplication) getApplicationContext());
//        familyTable.deleteAll();
//        try {
//
//            SharedPrefUtils.setpinCode(ActivityLogin.this, model.getResult().getData().getUser().getPincode());
//            familyTable.insertData(model.getResult().getData().getFamily());
//
//        } catch (Exception e) {
//            e.getMessage();
//        }

    }

    public void showVerifyEmailDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.resend_email, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // resend verification link
                        LoginRegistrationRequest lr = new LoginRegistrationRequest();
                        lr.setEmail(SharedPrefUtils.getUserDetailModel(ActivityLogin.this).getEmail());

                        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
                        Call<UserDetailResponse> call = loginRegistrationAPI.resendVerificationLink(lr);
                        call.enqueue(onVerifyEmailLinkResendResponseReceived);
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    Callback<UserDetailResponse> onVerifyEmailLinkResendResponseReceived = new Callback<UserDetailResponse>() {
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
                Log.d("MC4kException", Log.getStackTraceString(e));
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

    public void addEmail(String email) {

        showProgressDialog(getString(R.string.please_wait));
        String emailId = email;
//        UpdateUserDetail _requestModel = new UpdateUserDetail();
//        _requestModel.setAttributeName("email");
//        _requestModel.setAttributeValue(email);
//        _requestModel.setAttributeType("S");

        LoginRegistrationRequest lr = new LoginRegistrationRequest();
        lr.setEmail(emailId);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = loginRegistrationAPI.addFacebookEmail(lr);
        call.enqueue(onAddFacebookEmailResponseReceived);

//        LoginController _controller = new LoginController(this, this);
//        _controller.getData(AppConstants.NEW_LOGIN_REQUEST, _requestModel);
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
                UserDetailResponse responseData = (UserDetailResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    dialogFragment.dismiss();
                    showToast("Mail sent to email address Please click and verify");

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


    //  create a textWatcher member
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues();
        }
    };

    void checkFieldsForEmptyValues() {

        String s1 = mEmailId.getText().toString();
        String s2 = mPassword.getText().toString();

        if (s1.equals("") || s2.equals("")) {
            signinTextView.setEnabled(false);
        } else {
            signinTextView.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);//64206,0  -1
        if (_requestCode == GooglePlusUtils.REQUEST_CODE_SIGN_IN) {
            mGooglePlusUtils.onActivityResult(this, _requestCode, _resultCode, _data);
            if (_resultCode != -1) {
                removeProgressDialog();
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
            FacebookUtils.onActivityResult(this, _requestCode, _resultCode, _data);
        }
    }

}
