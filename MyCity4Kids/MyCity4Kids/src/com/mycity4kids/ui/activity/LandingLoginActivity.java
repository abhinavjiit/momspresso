package com.mycity4kids.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
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

import java.util.ArrayList;

/**
 * @author "Deepanker Chaudhary"
 */

public class LandingLoginActivity extends BaseActivity implements OnClickListener, IPlusClient, IFacebookUser {
    private GooglePlusUtils mGooglePlusUtils;
    private boolean isCommingFromInside;
    private int mCategoryId;
    private int mBusinessOrEventType;
    private String mBusinessOrEventId;
    private String mDistance;
    private String googleEmailId, userId, currentPersonName, personPhotoUrl;
    private boolean isFacebook = false;
    private GraphUser fbUser;
    private String accessToken = "";
    private String googleToken = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.login_with_fb_google);
        setContentView(R.layout.aa_splashactivity);
        try {
            //((Button)findViewById(R.id.skip_btn_login)).setOnClickListener(this);
            //((Button)findViewById(R.id.email_login_btn)).setOnClickListener(this);
            //((Button)findViewById(R.id.sign_up_btn)).setOnClickListener(this);
            //((LinearLayout)findViewById(R.id.fb_lout_btn)).setOnClickListener(this);
            //((LinearLayout)findViewById(R.id.google_lout_btn)).setOnClickListener(this);

            TextView login = (TextView) findViewById(R.id.email_login_btn);

            ((TextView) findViewById(R.id.email_login_btn)).setOnClickListener(this);
            ((ImageView) findViewById(R.id.sign_up_btn)).setOnClickListener(this);
            ((ImageView) findViewById(R.id.fb_lout_btn)).setOnClickListener(this);
            ((ImageView) findViewById(R.id.google_lout_btn)).setOnClickListener(this);

            login.setPaintFlags(login.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {

                Constants.IS_COMING_FROM_INSIDE = bundle.getBoolean(Constants.LOGIN_REQUIRED, false);
                mCategoryId = bundle.getInt(Constants.CATEGORY_ID, 0);
                mBusinessOrEventId = bundle.getString(Constants.BUSINESS_OR_EVENT_ID);
                mBusinessOrEventType = bundle.getInt(Constants.PAGE_TYPE, 0);
                mDistance = bundle.getString(Constants.DISTANCE);
            } else {
                Constants.IS_COMING_FROM_INSIDE = false;
            }

            /**
             * this is a google plus utility class which will register from here for google plus login
             */
            mGooglePlusUtils = new GooglePlusUtils(this, this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGooglePlusUtils != null)
            mGooglePlusUtils.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeProgressDialog();
    }


    @Override
    protected void onStop() {
        super.onStop();
        //removeProgressDialog();
        if (mGooglePlusUtils != null)
            mGooglePlusUtils.onStop();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    public void loginFacebook(View v) {
        /**
         * this function would be required for google plus  login
         */
        mGooglePlusUtils.googlePlusLogin();
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);//64206,0  -1
        if (_requestCode == GooglePlusUtils.REQUEST_CODE_SIGN_IN) {
            mGooglePlusUtils.onActivityResult(this, _requestCode, _resultCode, _data);
            if (_resultCode != -1) {
                removeProgressDialog();
            }
            //
        } else {
            if (_resultCode == 0) {
                removeProgressDialog();
            }
            FacebookUtils.onActivityResult(this, _requestCode, _resultCode, _data);
        }


    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
        /*case R.id.facebook_btn:
            if(ConnectivityUtils.isNetworkEnabled(this)){
				showProgressDialog(getString(R.string.please_wait));

				FacebookUtils.facebookLogin(this, this);
			}else{
				showToast(getString(R.string.error_network));
			}

			break;*/
            case R.id.fb_lout_btn:
                if (ConnectivityUtils.isNetworkEnabled(this)) {
                    showProgressDialog(getString(R.string.please_wait));

                    FacebookUtils.facebookLogin(this, this);
                } else {
                    showToast(getString(R.string.error_network));
                }

                break;

            case R.id.email_login_btn:


//                commented by manish.
                startActivity(new Intent(this, ActivityLogin.class));

                break;
            case R.id.sign_up_btn:

                Intent intent1 = new Intent(LandingLoginActivity.this, ActivitySignUp.class);
                intent1.putExtra(Constants.SIGNUP_FLAG, true);
                startActivity(intent1);

                break;
            case R.id.skip_btn_login:
                //if(!isCommingFromInside)
                sendToHomeScreen();

                //	finish();
                break;

            case R.id.google_lout_btn:
                if (ConnectivityUtils.isNetworkEnabled(this)) {
                    showProgressDialog("Please Wait");
                    mGooglePlusUtils.googlePlusLogin();
                } else {
                    showToast(getString(R.string.error_network));
                }
                break;


            default:
                break;
        }
    }


    public void onBackPressed() {
        finish();
        // added by khushboo
//		if(!Constants.IS_COMING_FROM_INSIDE){
//			sendToHomeScreen();
//		}else{
//			finish();
//		}

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
                e.printStackTrace();
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

            SharedPrefUtils.setpinCode(LandingLoginActivity.this, model.getResult().getData().getUser().getPincode());
            familyTable.insertData(model.getResult().getData().getFamily());

        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void showLoginDialog(String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage(message).setNegativeButton(R.string.ok
                , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

                startActivity(new Intent(LandingLoginActivity.this, ActivityLogin.class));
                finish();


            }
        }).setPositiveButton(R.string.new_cancel, new DialogInterface.OnClickListener() {
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


    @Override
    protected void updateUi(com.kelltontech.network.Response response) {
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

                        // prompt formove to login activity
//
                        removeProgressDialog();
                        if (mGooglePlusUtils != null)
                            mGooglePlusUtils.onStop();

                        // save in prefrences
                        UserInfo model = new UserInfo();
                        model.setId(responseData.getResult().getData().getUser().getId());
                        model.setEmail(responseData.getResult().getData().getUser().getEmail());
                        model.setMobile_number(responseData.getResult().getData().getUser().getMobile_number());
                        model.setFamily_id(responseData.getResult().getData().getUser().getFamily_id());
                        model.setColor_code(responseData.getResult().getData().getUser().getColor_code());
                        model.setSessionId(responseData.getResult().getData().getUser().getSessionId());
                        model.setFirst_name(responseData.getResult().getData().getUser().getFirst_name() + " " + responseData.getResult().getData().getUser().getLast_name());
                        SharedPrefUtils.setUserDetailModel(LandingLoginActivity.this, model);

                        if (StringUtils.isNullOrEmpty(responseData.getResult().getData().getUser().getMobile_number())) {
                            //existing user with no mobile number logging in using social media.
                            removeProgressDialog();
                            Intent updateMobileIntent = new Intent(this, UpdateMobileNumberActivity.class);
//                            updateMobileIntent.putExtra("activity", "landingLoginActivity");
                            updateMobileIntent.putExtra("isExistingUser", "1");
                            updateMobileIntent.putExtra("name", responseData.getResult().getData().getUser().getFirst_name());
                            updateMobileIntent.putExtra("colorCode", responseData.getResult().getData().getUser().getColor_code());
                            startActivity(updateMobileIntent);
                            return;
                        }

                        Toast.makeText(LandingLoginActivity.this, responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();

                        // if db not exists first save in db
                        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
                        ArrayList<KidsInfo> kidList = kidsTable.getAllKids();
                        if (kidList.isEmpty()) {
                            // db not exists
                            saveDatainDB(responseData);
                        }

                        // set city also

                        // then call getappoitmnt service
                        startSyncing();

                        Intent intent1 = new Intent(this, LoadingActivity.class);
                        startActivity(intent1);
                    } else if (responseData.getResponseCode() == 400) {
                        // now move to sign up screen
                        removeProgressDialog();

//                        if (responseData.getResult().getMessage().toString().trim().equalsIgnoreCase("family_linked")) {
//                            if (mGooglePlusUtils != null)
//                                mGooglePlusUtils.onStop();
//                            showLoginDialog(getResources().getString(R.string.do_login));
//
//
//                        } else {

                        if (isFacebook) {

                            if (fbUser != null) {
                                //new facebook user
                                Intent i = new Intent(this, SocialSignUpActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.USER_ID, fbUser.getId());
                                bundle.putString(Constants.USER_NAME, fbUser.getFirstName() + " " + fbUser.getLastName());
                                bundle.putString(Constants.USER_EMAIL, fbUser.asMap().get("email").toString());
                                bundle.putString(Constants.ACCESS_TOKEN, accessToken);
                                bundle.putString(Constants.MODE, "fb");
                                bundle.putString(Constants.PROFILE_IMAGE, personPhotoUrl);
                                i.putExtra("userbundle", bundle);
                                startActivity(i);
                            }

                        } else {
                            //new google plus user
                            Intent i = new Intent(LandingLoginActivity.this, SocialSignUpActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.USER_ID, userId);
                            bundle.putString(Constants.USER_NAME, currentPersonName);
                            bundle.putString(Constants.USER_EMAIL, googleEmailId);
                            bundle.putString(Constants.ACCESS_TOKEN, googleToken);
                            bundle.putString(Constants.MODE, "gp");
                            bundle.putString(Constants.PROFILE_IMAGE, personPhotoUrl);
                            i.putExtra("userbundle", bundle);
                            startActivity(i);

                        }
//                        }

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
                    userInviteModel.setUserId("" + rData.getResult().getData().getUserInfo().getId());
                    userInviteModel.setEmail(rData.getResult().getData().getUserInfo().getEmail());
                    userInviteModel.setMobile(rData.getResult().getData().getUserInfo().getMobile_number());
                    userInviteModel.setFamilyInvites(rData.getResult().getData().getFamilyInvites());
                    SharedPrefUtils.setUserFamilyInvites(this, new Gson().toJson(userInviteModel).toString());
                    if (null != rData.getResult().getData().getFamilyInvites() && !rData.getResult().getData().getFamilyInvites().isEmpty()) {
                        //new FB/G+ user with invites who has not accepted any invites or created a family
                        // but created his account and verified his mobile.
                        Intent intent = new Intent(this, ListFamilyInvitesActivity.class);
                        intent.putExtra("userInviteData", userInviteModel);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        //new FB/G+ user without invites who has not craeted a family but created his account and verified his mobile.
                        Intent intent = new Intent(this, CreateFamilyActivity.class);
                        intent.putExtra("userInviteData", userInviteModel);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                } else if (rData.getResponseCode() == 400) {
                    Toast.makeText(LandingLoginActivity.this, rData.getResult().getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;


        }

//        UserResponse responseData = (UserResponse) response.getResponseObject();
//        if (responseData.getResponseCode() == 200) {
//            Toast.makeText(LandingLoginActivity.this, "Login has been successful.", Toast.LENGTH_SHORT).show();
//            if (!Constants.IS_COMING_FROM_INSIDE) {
//                sendToHomeScreen();
//            } else {
//                finish();
//            }
//        } else if (responseData.getResponseCode() == 400) {
//            Toast.makeText(LandingLoginActivity.this, R.string.login_failed_please_try_again_, Toast.LENGTH_SHORT).show();
//
//        }
//        removeProgressDialog();


    }

    /**
     * this is a call back method which will give google plus details:
     */
    @Override
    public void getGooglePlusInfo(GoogleApiClient plusClient) {

        try {
            if (Plus.PeopleApi.getCurrentPerson(plusClient) != null) {

                isFacebook = false;
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
//            String personPhoto = currentPerson.getImage().getUrl();
//            String personGooglePlusProfile = currentPerson.getUrl();


                new GetGoogleToken().execute();
            } else {
                removeProgressDialog();
            }
        } catch (Exception e) {
            Log.i("googlePlust", e.getMessage());
            showToast("Try again later.");
            removeProgressDialog();
        }

    }

    @Override
    public void onGooglePlusLoginFailed() {
        removeProgressDialog();
    }

    // background tasks


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

                googleToken = GoogleAuthUtil.getToken(LandingLoginActivity.this, googleEmailId, "oauth2:" + GooglePlusUtils.SCOPES);

                System.out.println("token " + googleToken);
                return googleToken;

            } catch (UserRecoverableAuthException userAuthEx) {
                userAuthEx.printStackTrace();
                Log.d("UserRecoverableAuthException", userAuthEx.toString());
                startActivityForResult(userAuthEx.getIntent(), 98);
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
            final LoginController _controller = new LoginController(LandingLoginActivity.this, LandingLoginActivity.this);
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


    @Override
    public void getFacebookUser(GraphUser user) {

        try {
            //showProgressDialog(getString(R.string.please_wait));
            if (user != null) {
                fbUser = user;
                isFacebook = true;

                Session session = Session.getActiveSession();
                if (session.isOpened())
                    accessToken = session.getAccessToken();

                personPhotoUrl = "https://graph.facebook.com/" + user.getId() + "/picture?type=large";

                // send this details to activity signup form
                // first check whether his family created


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


//                Intent i = new Intent(this, ActivitySignUp.class);
//                Bundle bundle = new Bundle();
//                bundle.putString(Constants.USER_ID, user.getId());
//                bundle.putString(Constants.USER_NAME, user.getFirstName() + " " + user.getLastName());
//                bundle.putString(Constants.USER_EMAIL, user.asMap().get("email").toString());
//                bundle.putString(Constants.ACCESS_TOKEN, accessToken);
//                bundle.putString(Constants.PROFILE_IMAGE, personPhotoUrl);
//                i.putExtra("userbundle", bundle);
//                startActivity(i);
//                Log.i("fbUsernameUserId", user.getId() + " " + user.getUsername() + " " + user.asMap().get("email"));
                // removeProgressDialog();
            }
        } catch (Exception e) {
            removeProgressDialog();
            Log.d("Facebook Error", "" + e.getMessage());
            showToast("Try again later.");
            //e.printStackTrace();
        }


    }


}
