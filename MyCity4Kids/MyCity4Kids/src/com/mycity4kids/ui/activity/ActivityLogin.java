package com.mycity4kids.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.mycity4kids.controller.LoginController;
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
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.ui.fragment.FacebookAddEmailDialogFragment;
import com.mycity4kids.ui.fragment.SignInFragment;
import com.mycity4kids.ui.fragment.SignUpFragment;
import com.mycity4kids.utils.PermissionUtil;

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

    private static final int REQUEST_INIT_PERMISSION = 1;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.GET_ACCOUNTS};

    private GooglePlusUtils mGooglePlusUtils;

    private String googleEmailId, userId, currentPersonName, personPhotoUrl;

    private String accessToken = "";
    private String googleToken = "";
    private int changeBaseURL = 0;
    private GraphUser fbUser;
    FacebookAddEmailDialogFragment dialogFragment;

    private String loginMode = "";
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(ActivityLogin.this, "Login Screen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        setContentView(R.layout.aa_loginform);

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

        mLayout = findViewById(R.id.rootLayout);

        mGooglePlusUtils = new GooglePlusUtils(this, this);

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


                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                                != PackageManager.PERMISSION_GRANTED) {
                            Log.i("PERMISSIONS", "Get accounts permission has NOT been granted. Requesting permissions.");
                            requestGetAccountsPermissions();
                        } else {
                            showProgressDialog("Please Wait");
                            mGooglePlusUtils.googlePlusLogin();
                        }
                    } else {
                        showProgressDialog("Please Wait");
                        mGooglePlusUtils.googlePlusLogin();
                    }
                } else {
                    showToast(getString(R.string.error_network));
                }
                break;
            default:
                break;

        }
    }

    private void requestGetAccountsPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.GET_ACCOUNTS)) {
            Log.i("Permissions",
                    "Displaying get accounts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_get_account_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    })
                    .show();
        } else {
            requestUngrantedPermissions();
        }
    }

    private void requestUngrantedPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_INIT.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_INIT[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_INIT[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_INIT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_INIT_PERMISSION) {
            Log.i("Permissions", "Received response for storage permissions request.");

            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(mLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                showProgressDialog("Please Wait");
                mGooglePlusUtils.googlePlusLogin();
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void loginRequest(String email_id, String password) {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            showProgressDialog(getString(R.string.please_wait));
            //mProgressDialog=ProgressDialog.show(this, "", "Please Wait...",true,false);
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
                    int cityIdFromLocation = SharedPrefUtils.getCurrentCityModel(ActivityLogin.this).getId();
                    if (cityIdFromLocation == AppConstants.OTHERS_CITY_ID) {
                        model.setCityId(responseData.getData().get(0).getResult().getCityId());
                    }
                    model.setSessionId(responseData.getData().get(0).getResult().getSessionId());
                    model.setLoginMode(loginMode);
                    SharedPrefUtils.setUserDetailModel(ActivityLogin.this, model);
                    SharedPrefUtils.setProfileImgUrl(ActivityLogin.this, responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());

                    if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                        //token already expired or not yet connected with facebook
                        SharedPrefUtils.setFacebookConnectedFlag(ActivityLogin.this, "1");
                    } else {
                        SharedPrefUtils.setFacebookConnectedFlag(ActivityLogin.this,
                                responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                    }

                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String version = pInfo.versionName;
                    if (version.equals(AppConstants.PHOENIX_RELEASE_VERSION)) {
                        SharedPrefUtils.setPhoenixFirstLaunch(ActivityLogin.this, false);
                    }
                    if (version.equals(AppConstants.FACEBOOK_CONNECT_RELEASE_VERSION)) {
                        SharedPrefUtils.setFBConnectFirstLaunch(ActivityLogin.this, false);
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
                        if (null != responseData.getData().get(0).getResult().getKids()) {
                            saveKidsInformation(responseData.getData().get(0).getResult().getKids());
                        }
                        Intent intent = new Intent(ActivityLogin.this, PushTokenService.class);
                        startService(intent);
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

    private void saveKidsInformation(ArrayList<KidsModel> kidsList) {

        ArrayList<KidsInfo> kidsInfoArrayList = new ArrayList<>();

        if (kidsList.size() == 1 && StringUtils.isNullOrEmpty(kidsList.get(0).getName())) {
            return;
        }
        for (KidsModel kid : kidsList) {
            KidsInfo kidsInfo = new KidsInfo();
            kidsInfo.setName(kid.getName());
            kidsInfo.setDate_of_birth(convertTime(""+kid.getBirthDay()));
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
//                showToast(getString(R.string.went_wrong));
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
//                showToast(getString(R.string.went_wrong));
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

    @Override
    public void onBackPressed() {
        try {
            Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (topFragment instanceof ExploreArticleListingTypeFragment) {
            }
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
