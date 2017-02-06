package com.mycity4kids.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.google.GooglePlusUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IFacebookUser;
import com.mycity4kids.interfaces.IPlusClient;
import com.mycity4kids.models.request.LoginRegistrationRequest;
import com.mycity4kids.models.request.SocialConnectRequest;
import com.mycity4kids.models.response.BaseResponse;
import com.mycity4kids.models.response.KidsModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.ui.adapter.TutorialAdapter;
import com.mycity4kids.ui.fragment.FacebookAddEmailDialogFragment;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;


public class TutorialActivity extends BaseActivity implements View.OnClickListener, IPlusClient, IFacebookUser {

    public static final int RECOVERABLE_REQUEST_CODE = 98;
    private ViewPager mViewPager;
    private TutorialAdapter mViewPagerAdapter;

    FacebookAddEmailDialogFragment dialogFragment;

    private GooglePlusUtils mGooglePlusUtils;
    private String loginMode = "";
    private String accessToken = "";
    private String googleToken = "";
    private String googleEmailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        Utils.pushOpenScreenEvent(TutorialActivity.this, "Tutorial Screen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mViewPagerAdapter = new TutorialAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mViewPagerAdapter);
        mGooglePlusUtils = new GooglePlusUtils(this, this);

    }

    private void navigateTologin() {
        Intent intent = new Intent(TutorialActivity.this, ActivityLogin.class);
        startActivity(intent);
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {

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
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            showProgressDialog("Please Wait");
            mGooglePlusUtils.googlePlusLogin();
        } else {
            showToast(getString(R.string.error_network));
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
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void getGooglePlusInfo(GoogleApiClient plusClient) {
        try {
            removeProgressDialog();
            if (isFinishing())
                return;

            showProgressDialog(getString(R.string.please_wait));
            if (Plus.PeopleApi.getCurrentPerson(plusClient) != null) {
                loginMode = "gp";
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(plusClient);
                googleEmailId = Plus.AccountApi.getAccountName(plusClient);

                if (StringUtils.isNullOrEmpty(googleEmailId)) {
                    googleEmailId = "Email not fetch from google.";
                }

                new GetGoogleToken().execute();

            } else {
                removeProgressDialog();
                Log.i("ActivityLogin", "GoogleApiClient persona information is null");
            }

        } catch (Exception e) {
            removeProgressDialog();
            showToast("Try again later.");
            e.printStackTrace();
        }
    }

    @Override
    public void onGooglePlusLoginFailed() {
        removeProgressDialog();
    }

    public class GetGoogleToken extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {

                googleToken = GoogleAuthUtil.getToken(TutorialActivity.this, googleEmailId, "oauth2:" + GooglePlusUtils.SCOPES);

//                System.out.println("token " + googleToken);
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
            lr.setCityId("" + SharedPrefUtils.getCurrentCityModel(TutorialActivity.this).getId());
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
                    model.setId(responseData.getData().get(0).getResult().getId());
                    model.setDynamoId(responseData.getData().get(0).getResult().getDynamoId());
                    model.setEmail(responseData.getData().get(0).getResult().getEmail());
                    model.setMc4kToken(responseData.getData().get(0).getResult().getMc4kToken());
                    model.setIsValidated(responseData.getData().get(0).getResult().getIsValidated());
                    model.setFirst_name(responseData.getData().get(0).getResult().getFirstName());
                    model.setLast_name(responseData.getData().get(0).getResult().getLastName());
                    model.setProfilePicUrl(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    model.setSessionId(responseData.getData().get(0).getResult().getSessionId());
                    model.setLoginMode(loginMode);
                    SharedPrefUtils.setUserDetailModel(TutorialActivity.this, model);
                    SharedPrefUtils.setProfileImgUrl(TutorialActivity.this, responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());

                    if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                        //token already expired or not yet connected with facebook
                        SharedPrefUtils.setFacebookConnectedFlag(TutorialActivity.this, "1");
                    } else {
                        SharedPrefUtils.setFacebookConnectedFlag(TutorialActivity.this,
                                responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                    }

                    PackageInfo pInfo = getPackageManager().getPackageInfo(TutorialActivity.this.getPackageName(), 0);
                    String version = pInfo.versionName;
                    if (version.equals(AppConstants.PHOENIX_RELEASE_VERSION)) {
                        SharedPrefUtils.setPhoenixFirstLaunch(TutorialActivity.this, false);
                    }
                    if (version.equals(AppConstants.FACEBOOK_CONNECT_RELEASE_VERSION)) {
                        SharedPrefUtils.setFBConnectFirstLaunch(TutorialActivity.this, false);
                    }

                    //Facebook connect request if user logged in through facebook
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
                        bundle.putString(AppConstants.FROM_ACTIVITY, AppConstants.ACTIVITY_TUTORIAL);
                        dialogFragment.setArguments(bundle);
                        dialogFragment.show(getFragmentManager(), "verify email");
                    }
                    //Custom sign up user but email is not yet verfifed.
                    else if (!AppConstants.VALIDATED_USER.equals(model.getIsValidated())) {
                        showVerifyEmailDialog("Error", "Please verify your account to login");
                    }
                    //Verified User
                    else {
                        if (null != responseData.getData().get(0).getResult().getKids()) {
                            saveKidsInformation(responseData.getData().get(0).getResult().getKids());
                        }
                        Intent intent = new Intent(TutorialActivity.this, PushTokenService.class);
                        startService(intent);
                        Intent intent1 = new Intent(TutorialActivity.this, LoadingActivity.class);
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
                        lr.setEmail(SharedPrefUtils.getUserDetailModel(TutorialActivity.this).getEmail());

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
            Date date = new Date(Long.parseLong(time) * 1000);
            Format format = new SimpleDateFormat("dd-MM-yyyy");
            return format.format(date);
        } catch (NumberFormatException nfe) {
            return "";
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
                UserDetailResponse responseData = (UserDetailResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    dialogFragment.dismiss();
                    showToast("Mail sent to email address Please click and verify");

                    Intent intent = new Intent(TutorialActivity.this, PushTokenService.class);
                    startService(intent);
                    Intent intent1 = new Intent(TutorialActivity.this, LoadingActivity.class);
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

}
