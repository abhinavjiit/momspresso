package com.mycity4kids.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.LoginController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.google.GooglePlusUtils;
import com.mycity4kids.interfaces.IFacebookUser;
import com.mycity4kids.interfaces.IPlusClient;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserRequest;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.UserInviteModel;
import com.mycity4kids.newmodels.UserInviteResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.fragment.PasswordDialogFragment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by khushboo.goyal on 19-06-2015.
 */
public class ActivityLogin extends BaseActivity implements View.OnClickListener, IPlusClient, IFacebookUser {

    private GooglePlusUtils mGooglePlusUtils;
    private EditText mEmailId, mPassword;
    private Toolbar mToolbar;
    LinearLayout forgotView;
    private boolean filterchange;
    private String socialEmailid = "";
//    private GetAppointmentController _appointmentcontroller;
//    private GetTaskController _taskcontroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.aa_loginform);

        forgotView = (LinearLayout) findViewById(R.id.forgot_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign In");

        mToolbar.setClickable(true);

//        Field titleField = null;
//        try {
//            titleField = Toolbar.class.getDeclaredField("mTitleTextView");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//        titleField.setAccessible(true);
//        TextView barTitleView = null;
//        try {
//            barTitleView = (TextView) titleField.get(mToolbar);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        barTitleView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(ActivityLogin.this, "Hii ", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        barTitleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drop_downxxhdpi, 0);
//        barTitleView.setCompoundDrawablePadding(15);

        ((LinearLayout) findViewById(R.id.forgot_view)).setGravity(Gravity.CENTER);

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            boolean joinfamily = extras.getBoolean("frmJoinFamily");
//            if (joinfamily) {
//                ((ImageView) findViewById(R.id.connect_facebook)).setVisibility(View.GONE);
//                ((ImageView) findViewById(R.id.connect_googleplus)).setVisibility(View.GONE);
//                ((LinearLayout) findViewById(R.id.forgot_view)).setGravity(Gravity.LEFT);
//            }
//        }

        try {

            mEmailId = (EditText) findViewById(R.id.email_login);
            mPassword = (EditText) findViewById(R.id.password_login);
            TextView forgotPassword = (TextView) findViewById(R.id.forgot_password);

            ((TextView) findViewById(R.id.forgot_password)).setOnClickListener(this);
            ((ImageView) findViewById(R.id.connect_facebook)).setOnClickListener(this);
            ((ImageView) findViewById(R.id.connect_googleplus)).setOnClickListener(this);

            forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        } catch (Exception e) {
            e.printStackTrace();
        }
        mGooglePlusUtils = new GooglePlusUtils(this, this);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (filterchange) {
            menu.clear();
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.forgot_password, menu);
            MenuItem item = menu.findItem(R.id.save);
            MenuItemCompat.setActionView(item, R.layout.aa_filtericon);
            View view = MenuItemCompat.getActionView(item);
            ImageView img = (ImageView) view.findViewById(R.id.filter);
            img.setImageResource(R.drawable.filter_xxhdpi);
        }


        return super.onPrepareOptionsMenu(menu);
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
                    if (ConnectivityUtils.isNetworkEnabled(this)) {
                        showProgressDialog(getString(R.string.please_wait));
                        //mProgressDialog=ProgressDialog.show(this, "", "Please Wait...",true,false);

                        String emailId_or_mobile = mEmailId.getText().toString().trim();
                        String password = mPassword.getText().toString().trim();
                        UserRequest _requestModel = new UserRequest();
                        _requestModel.setEmailId(emailId_or_mobile);
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
                    } else {
                        showToast(getString(R.string.error_network));
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                        Toast.makeText(ActivityLogin.this, responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();

                        // if db not exists first save in db
                        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
                        ArrayList<KidsInfo> kidList = kidsTable.getAllKids();
                        if (kidList.isEmpty()) {
                            // db not exists
                            saveDatainDB(responseData);
                        }

                        // save in prefrences
                        UserInfo model = new UserInfo();
                        model.setId(responseData.getResult().getData().getUser().getId());
                        model.setFamily_id(responseData.getResult().getData().getUser().getFamily_id());
                        model.setColor_code(responseData.getResult().getData().getUser().getColor_code());
                        model.setSessionId(responseData.getResult().getData().getUser().getSessionId());
                        model.setFirst_name(responseData.getResult().getData().getUser().getFirst_name() + " " + responseData.getResult().getData().getUser().getLast_name());
                        SharedPrefUtils.setUserDetailModel(ActivityLogin.this, model);

                        // set city also

                        // then call getappoitmnt service
                        removeProgressDialog();
                        startSyncing();

                        Intent intent1 = new Intent(this, LoadingActivity.class);
                        // intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        // finish();
                    } else if (responseData.getResponseCode() == 400) {
                        if (!StringUtils.isNullOrEmpty(responseData.getResult().getData().getError())) {
                            if (responseData.getResult().getData().getError().toString().trim().equalsIgnoreCase("existing_user") || responseData.getResult().getData().getError().toString().trim().equalsIgnoreCase("sign_up")) {
                                showSignUpDialog(responseData.getResult().getData().getMessage(), responseData);
                            } else if (responseData.getResult().getData().getError().toString().trim().equalsIgnoreCase("family_linked")) {
                                PasswordDialogFragment dialogFragment = new PasswordDialogFragment();
                                dialogFragment.show(getFragmentManager(), "");
                            } else {
                                Toast.makeText(ActivityLogin.this, responseData.getResult().getData().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ActivityLogin.this, responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        removeProgressDialog();
                    }

                } catch (Exception e) {
                    removeProgressDialog();
                    e.printStackTrace();
                }
                break;
            case AppConstants.ACCEPT_OR_REJECT_INVITE_REQUEST:
                UserInviteResponse rData = (UserInviteResponse) response.getResponseObject();
                if (rData.getResponseCode() == 201) {
                    UserInviteModel userInviteModel = new UserInviteModel();
                    userInviteModel.setUserId(rData.getResult().getData().getUserId());
                    userInviteModel.setEmail(rData.getResult().getData().getEmail());
                    userInviteModel.setMobile(rData.getResult().getData().getMobile());
                    userInviteModel.setFamilyInvites(rData.getResult().getData().getFamilyInvites());
                    SharedPrefUtils.setUserFamilyInvites(this, new Gson().toJson(userInviteModel).toString());

                    Intent intent = new Intent(this, ListFamilyInvitesActivity.class);
                    intent.putExtra("userInviteData", userInviteModel);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (rData.getResponseCode() == 400) {
                    Toast.makeText(ActivityLogin.this, rData.getResult().getMessage(), Toast.LENGTH_SHORT).show();
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
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(plusClient);
                String currentPersonName = currentPerson.getDisplayName();
                String userId = currentPerson.getId();
                String googleEmailId = Plus.AccountApi.getAccountName(plusClient);
                if (StringUtils.isNullOrEmpty(googleEmailId)) {
                    googleEmailId = "Email not fetch from google.";
                }
                if (StringUtils.isNullOrEmpty(currentPersonName)) {
                    currentPersonName = getString(R.string.unknown_person);
                }
                if (StringUtils.isNullOrEmpty(userId)) {
                    userId = getString(R.string.unknown_person);
                }

                socialEmailid = googleEmailId;

                System.out.println(currentPersonName + "user ID" + userId);
                UserRequest _userModel = new UserRequest();
                _userModel.setEmailId(googleEmailId);
                _userModel.setProfileId(userId);
                _userModel.setNetworkName("google");
                _userModel.setFirstName(currentPersonName);
                _userModel.setLastName("");

                Log.i("login data", new Gson().toJson(_userModel).toString());
                //	showProgressDialog("PleaseWait");
                _controller.getData(AppConstants.NEW_LOGIN_REQUEST, _userModel);
            } else {
                Log.i("ActivityLogin", "GoogleApiClient persona information is null");
            }
            removeProgressDialog();
        } catch (Exception e) {
            removeProgressDialog();
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
                final LoginController _controller = new LoginController(this, this);
                //Toast.makeText(LoginActivity.this, user.asMap().get("email").toString(), Toast.LENGTH_LONG).show() ;
                String fbEmailId = user.asMap().get("email").toString();

                Session session = Session.getActiveSession();
                String accessToken;
                if (session.isOpened())
                    accessToken = session.getAccessToken();

                UserRequest _userModel = new UserRequest();
                _userModel.setEmailId(fbEmailId);
                _userModel.setProfileId(user.getId());
                _userModel.setNetworkName("facebook");
                _userModel.setFirstName(user.getFirstName());
                _userModel.setLastName(user.getLastName());

                socialEmailid = fbEmailId;
                _controller.getData(AppConstants.NEW_LOGIN_REQUEST, _userModel);
                Log.i("fbUsernameUserId", user.getId() + " " + user.getUsername() + " " + user.asMap().get("email"));
            }
        } catch (Exception e) {
            // e.printStackTrace();
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
        } else if (mPassword.getText().toString().length() < 5) {
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


//            case R.id.arrow:
//                showToast(getString(R.string.error_network));
//                filterchange = true;
//                invalidateOptionsMenu();
//                break;
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

}
