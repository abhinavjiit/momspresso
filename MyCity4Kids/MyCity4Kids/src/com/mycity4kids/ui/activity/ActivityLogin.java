package com.mycity4kids.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.google.GooglePlusUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IFacebookUser;
import com.mycity4kids.interfaces.IPlusClient;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserRequest;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.FamilyInvites;
import com.mycity4kids.newmodels.UserInviteModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.widget.CustomFontEditText;
import com.mycity4kids.widget.CustomFontTextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

    private String loginMode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(ActivityLogin.this, "Login Screen", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        setContentView(R.layout.aa_loginform);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign In");

//        forgotView = (LinearLayout) findViewById(R.id.forgot_view);
//
//        ((LinearLayout) findViewById(R.id.forgot_view)).setGravity(Gravity.CENTER);

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
//            forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        } catch (Exception e) {
            e.printStackTrace();
        }
        mGooglePlusUtils = new GooglePlusUtils(this, this);

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

            UserRequest _userModel = new UserRequest();
            _userModel.setEmailId(googleEmailId);
            _userModel.setProfileId(userId);
            _userModel.setNetworkName("google");
            _userModel.setFirstName(currentPersonName);
            _userModel.setLastName("");
            _userModel.setAccessToken(googleToken);

            Log.i("onActivityResult data", new Gson().toJson(_userModel).toString());
            final LoginController _controller = new LoginController(ActivityLogin.this, ActivityLogin.this);
            _controller.getData(AppConstants.NEW_LOGIN_REQUEST, _userModel);
        } else {
            if (_resultCode == 0) {
                removeProgressDialog();
            }
            FacebookUtils.onActivityResult(this, _requestCode, _resultCode, _data);
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

    public void saveDatainDB(UserResponse model) {

        UserTable table = new UserTable(BaseApplication.getInstance());
        if (table.getRowsCount() > 0) {

            try {
                String profileimg = table.getAllUserData().getProfile().getProfile_image();
                if (!StringUtils.isNullOrEmpty(profileimg)) {
                    SharedPrefUtils.setProfileImgUrl(this, profileimg);
                }
            } catch (Exception e) {
            }

        }


        TableAdult adultTable = new TableAdult((BaseApplication) getApplicationContext());
        adultTable.deleteAll();
        try {

            adultTable.beginTransaction();
            for (UserModel.AdultsInfo user : model.getResult().getData().getAdult()) {

                adultTable.insertData(user.getUser());
            }
            adultTable.setTransactionSuccessful();
        } finally {
            adultTable.endTransaction();
        }

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

        TableFamily familyTable = new TableFamily((BaseApplication) getApplicationContext());
        familyTable.deleteAll();
        try {

            SharedPrefUtils.setpinCode(ActivityLogin.this, model.getResult().getData().getUser().getPincode());
            familyTable.insertData(model.getResult().getData().getFamily());

        } catch (Exception e) {
            e.getMessage();
        }

    }

    public static String readFromFile(Context context, String fileName) {

        String ret = "";

        try {
            InputStream inputStream = context.getAssets().open("countries.json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("login activity", "File not found: " + e.toString());
        }

        return ret;
    }


    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            removeProgressDialog();
            showToast(getResources().getString(R.string.server_error));
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.NEW_LOGIN_REQUEST:
                try {
                    UserResponse responseData = (UserResponse) response.getResponseObject();
                    if (responseData.getResponseCode() == 200) {

                        // save in prefrences
                        UserInfo model = new UserInfo();
                        model.setId(responseData.getResult().getData().getUser().getId());
                        model.setEmail(responseData.getResult().getData().getUser().getEmail());
                        model.setMobile_number(responseData.getResult().getData().getUser().getMobile_number());
                        model.setFamily_id(responseData.getResult().getData().getUser().getFamily_id());
                        model.setColor_code(responseData.getResult().getData().getUser().getColor_code());
                        model.setSessionId(responseData.getResult().getData().getUser().getSessionId());
                        model.setFirst_name(responseData.getResult().getData().getUser().getFirst_name() + " " + responseData.getResult().getData().getUser().getLast_name());
                        SharedPrefUtils.setUserDetailModel(ActivityLogin.this, model);

                        ArrayList<FamilyInvites> familyInvitesList = responseData.getResult().getData().getFamilyInvites();

                        // Check User already linked with a family or not.
                        //then check for Invites if present or not
                        if ((StringUtils.isNullOrEmpty("" + responseData.getResult().getData().getUser().getFamily_id()) ||
                                responseData.getResult().getData().getUser().getFamily_id() == 0) && !familyInvitesList.isEmpty()) {
                            removeProgressDialog();

                            UserInviteModel userInviteModel = new UserInviteModel();
                            userInviteModel.setUserId("" + responseData.getResult().getData().getUser().getId());
                            userInviteModel.setEmail(responseData.getResult().getData().getUser().getEmail());
                            userInviteModel.setMobile(responseData.getResult().getData().getUser().getMobile_number());
                            userInviteModel.setFamilyInvites(responseData.getResult().getData().getFamilyInvites());
                            SharedPrefUtils.setUserFamilyInvites(this, new Gson().toJson(userInviteModel).toString());

                            Intent listFamilyInvitesIntent = new Intent(this, ListFamilyInvitesActivity.class);
                            listFamilyInvitesIntent.putExtra("userInviteData", userInviteModel);
                            listFamilyInvitesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(listFamilyInvitesIntent);
//                            Intent updateMobileIntent = new Intent(this, UpdateMobileNumberActivity.class);
//                            updateMobileIntent.putExtra("isExistingUser", "1");
//                            updateMobileIntent.putExtra("name", responseData.getResult().getData().getUser().getFirst_name());
//                            updateMobileIntent.putExtra("colorCode", responseData.getResult().getData().getUser().getColor_code());
//                            startActivity(updateMobileIntent);
                            return;
                        }
                        Toast.makeText(ActivityLogin.this, responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();

                        // if db not exists first save in db
                        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
                        ArrayList<KidsInfo> kidList = kidsTable.getAllKids();
                        if (kidList.isEmpty()) {
                            // db not exists
                            saveDatainDB(responseData);
                        }

                        // set city also

                        // then call getappoitmnt service
                        removeProgressDialog();
                        startSyncing();

                        Intent intent1 = new Intent(this, LoadingActivity.class);
                        // intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        // finish();
                    } else if (responseData.getResponseCode() == 400) {
                        removeProgressDialog();
                        Toast.makeText(this, responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    removeProgressDialog();
                    e.printStackTrace();
                }
                break;
        }


    }

    public void addPassword(String pswd) {

        showProgressDialog(getString(R.string.please_wait));
        String emailId = socialEmailid;
        String password = pswd;
        UserRequest _requestModel = new UserRequest();
        _requestModel.setEmailId(emailId);
        _requestModel.setPassword(password);
        _requestModel.setNetworkName("throughMail");
        _requestModel.setPush_token(SharedPrefUtils.getDeviceToken(this));
        _requestModel.setPlatform("android");
        _requestModel.setDevice_model(Build.MODEL + "");
        _requestModel.setDevice_os(Build.VERSION.SDK_INT + "");
        _requestModel.setImei_no(getImeiNumber() + "");
        _requestModel.setManufacturer(Build.MANUFACTURER + "");
        LoginController _controller = new LoginController(this, this);
        _controller.getData(AppConstants.NEW_LOGIN_REQUEST, _requestModel);
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

//                socialEmailid = googleEmailId;
//
//                System.out.println(currentPersonName + "user ID" + userId);
//                UserRequest _userModel = new UserRequest();
//                _userModel.setEmailId(googleEmailId);
//                _userModel.setProfileId(userId);
//                _userModel.setNetworkName("google");
//                _userModel.setFirstName(currentPersonName);
//                _userModel.setLastName("");
//
//                Log.i("login data", new Gson().toJson(_userModel).toString());
//                _controller.getData(AppConstants.NEW_LOGIN_REQUEST, _userModel);
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
    public void onGooglePlusLoginFailed() {
        removeProgressDialog();
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
                personPhotoUrl = "https://graph.facebook.com/" + user.getId() + "/picture?type=large";


                final LoginController _controller = new LoginController(this, this);
                String fbEmailId = user.asMap().get("email").toString();

                UserRequest _userModel = new UserRequest();
                _userModel.setEmailId(fbEmailId);
                _userModel.setProfileId(user.getId());
                _userModel.setNetworkName("facebook");
                _userModel.setFirstName(user.getFirstName());
                _userModel.setLastName(user.getLastName());
                _userModel.setAccessToken(accessToken);
                _controller.getData(AppConstants.NEW_LOGIN_REQUEST, _userModel);

//                socialEmailid = fbEmailId;
//                Log.i("fbUsernameUserId", user.getId() + " " + user.getUsername() + " " + user.asMap().get("email"));
            }
        } catch (Exception e) {
            // e.printStackTrace();
            removeProgressDialog();
            showToast("Try again later.");
        }
    }

    private boolean isDataValid() {
        boolean isLoginOk = true;
        String email_id = mEmailId.getText().toString().trim();

        if (email_id.trim().length() == 0 || ((!StringUtils.isValidEmail(email_id)) && (!StringUtils.checkMobileNumber(email_id)))) {
            mEmailId.setFocusableInTouchMode(true);
            mEmailId.setError("Please enter valid email id or mobile number");
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
                if (isDataValid()) {
                    if (ConnectivityUtils.isNetworkEnabled(this)) {
                        showProgressDialog(getString(R.string.please_wait));
                        //mProgressDialog=ProgressDialog.show(this, "", "Please Wait...",true,false);
                        loginMode = "email";
                        String emailId_or_mobile = mEmailId.getText().toString().trim();
                        String password = mPassword.getText().toString().trim();
                        UserRequest _requestModel = new UserRequest();
                        _requestModel.setEmailId(emailId_or_mobile);
                        _requestModel.setPassword(password);
//                        _requestModel.setNetworkName("throughMail");
//                        _requestModel.setPush_token(SharedPrefUtils.getDeviceToken(this));
//                        _requestModel.setPlatform("android");
//                        _requestModel.setDevice_model(Build.MODEL + "");
//                        _requestModel.setDevice_os(Build.VERSION.SDK_INT + "");
//                        _requestModel.setImei_no(getImeiNumber() + "");
//                        _requestModel.setManufacturer(Build.MANUFACTURER + "");
                        LoginController _controller = new LoginController(this, this);
                        _controller.getData(AppConstants.NEW_LOGIN_REQUEST, _requestModel);
                    } else {
                        showToast(getString(R.string.error_network));
                    }
                }
                break;
            default:
                break;

        }
    }

    public void showSignUpDialog(String message, final UserResponse response) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage(message).setNegativeButton(android.R.string.ok
                , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                String data = "";

//                if (response.getResponse().equalsIgnoreCase("failure")) {
//                    data = "";
//                } else {
//
//                    data = new Gson().toJson(response);
//                }

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
                Log.d("UserRecoverableAuthException", userAuthEx.toString());
                startActivityForResult(userAuthEx.getIntent(), RECOVERABLE_REQUEST_CODE);
                // Start the user recoverable action using the intent returned by
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

            UserRequest _userModel = new UserRequest();
            _userModel.setEmailId(googleEmailId);
            _userModel.setProfileId(userId);
            _userModel.setNetworkName("google");
            _userModel.setFirstName(currentPersonName);
            _userModel.setLastName("");
            _userModel.setAccessToken(googleToken);

            Log.i("login data", new Gson().toJson(_userModel).toString());
            final LoginController _controller = new LoginController(ActivityLogin.this, ActivityLogin.this);
            _controller.getData(AppConstants.NEW_LOGIN_REQUEST, _userModel);

//            Intent i = new Intent(LandingLoginActivity.this, ActivitySignUp.class);
//            Bundle bundle = new Bundle();
//            bundle.putString(Constants.USER_ID, userId);
//            bundle.putString(Constants.USER_NAME, currentPersonName);
//            bundle.putString(Constants.USER_EMAIL, googleEmailId);
//            bundle.putString(Constants.ACCESS_TOKEN, result);
//            bundle.putString(Constants.MODE, "google");
//            bundle.putString(Constants.PROFILE_IMAGE, personPhotoUrl);
//            i.putExtra("userbundle", bundle);
//            startActivity(i);

            //removeProgressDialog();
        }

    }

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

}
