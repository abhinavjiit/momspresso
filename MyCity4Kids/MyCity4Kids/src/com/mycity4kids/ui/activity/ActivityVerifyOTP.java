package com.mycity4kids.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ControllerSignUp;
import com.mycity4kids.controller.UpdateMobileController;
import com.mycity4kids.controller.VerifyOTPController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserRequest;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.FamilyInvites;
import com.mycity4kids.newmodels.NewSignUpModel;
import com.mycity4kids.newmodels.UserInviteModel;
import com.mycity4kids.newmodels.UserInviteResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hemant on 20/1/16.
 */
public class ActivityVerifyOTP extends BaseActivity {

    private static final String TAG = ActivityVerifyOTP.class.getSimpleName();
    private Toolbar mToolbar;
    private EditText otpSMSText;
    private TextView verifyOTPTextView, resendOtpTextView,textViewSendingOtp,textViewTimer,textMobileNumber;
    private NewSignUpModel newSignupModel;
    Animation animationBlinking;
    private String email, mobileNumber, profileImageUrl, colorCode, isExistingUser;
    private RelativeLayout layoutSendingOtp;
    private LinearLayout verifyOTPLayout;
    private static int resendCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_otp_activity);
        Utils.pushOpenScreenEvent(ActivityVerifyOTP.this, "OTP Verification", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        email = getIntent().getExtras().getString("email");
        mobileNumber = getIntent().getExtras().getString("mobile");
        profileImageUrl = getIntent().getExtras().getString("profileUrl");
        colorCode = getIntent().getExtras().getString("colorCode");
        isExistingUser = getIntent().getExtras().getString("isExistingUser", "");
        newSignupModel = getIntent().getExtras().getParcelable("signUpData");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        otpSMSText = (EditText) findViewById(R.id.otpSMSText);
        verifyOTPTextView = (TextView) findViewById(R.id.verifyOTPTextView);
        resendOtpTextView = (TextView) findViewById(R.id.resendOtpTextView);
        textViewSendingOtp=(TextView) findViewById(R.id.textViewSendingOtp);
        textViewTimer=(TextView) findViewById(R.id.textViewTimer);
        layoutSendingOtp=(RelativeLayout)findViewById(R.id.layoutSendingOtp);
        verifyOTPLayout=(LinearLayout) findViewById(R.id.verifyOTPLayout);
        textMobileNumber=(TextView) findViewById(R.id.textMobileNumber);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Verify Mobile");
        textMobileNumber.setText(mobileNumber);
        Log.e("mobileNo",mobileNumber);
        animationBlinking = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        textViewSendingOtp.setAnimation(animationBlinking);
        setAnimantionTimer(20000);
        verifyOTPTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyOTPController _controller = new VerifyOTPController(ActivityVerifyOTP.this, ActivityVerifyOTP.this);
                if ("1".equals(isExistingUser)) {
//                    existing users no need to create family or accept invite.
                    _controller.getData(AppConstants.CONFIRM_MOBILE_OTP_FOR_EXISTING_USERS_REQUEST,
                            mobileNumber, email, otpSMSText.getText().toString());
                } else {
                    _controller.getData(AppConstants.VERIFY_OTP_REQUEST, mobileNumber, email, otpSMSText.getText().toString());
                }
            }
        });


    resendOtpTextView.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 if (resendCount < 5) {
                                                     resendCount++;
                                                     if ("1".equals(isExistingUser)) {
                                                         UserRequest _requestModel = new UserRequest();
                                                         _requestModel.setUserId("" + SharedPrefUtils.getUserDetailModel(ActivityVerifyOTP.this).getId());
                                                         _requestModel.setMobileNumber(mobileNumber);
                                                         UpdateMobileController _controller = new UpdateMobileController(ActivityVerifyOTP.this, ActivityVerifyOTP.this);
                                                         _controller.getData(AppConstants.UPDATE_MOBILE_FOR_EXISTING_USER_REQUEST, _requestModel);
                                                     } else {
                                                         ControllerSignUp _controller = new ControllerSignUp(ActivityVerifyOTP.this, ActivityVerifyOTP.this);
                                                         _controller.getData(AppConstants.NEW_SIGNUP_REQUEST, newSignupModel);
                                                     }
                                                     setResetOTPTimeLimit(60000);
                                                 } else {
                                                     Toast.makeText(ActivityVerifyOTP.this, "Requests exhausted, Please come back after some time", Toast.LENGTH_LONG).show();
                                                 }
                                             }


                                         } );

            setResetOTPTimeLimit(60000);
    }

    private void setResetOTPTimeLimit(int timeInMillis) {
        resendOtpTextView.setEnabled(false);
        new CountDownTimer(timeInMillis, 1000) {

            public void onTick(long millisUntilFinished) {
                resendOtpTextView.setText("Resend OTP in " + millisUntilFinished / 1000 + " seconds: ");
            }

            public void onFinish() {
                resendOtpTextView.setText("Resend OTP");
                resendOtpTextView.setEnabled(true);
            }
        }.start();
    }
    private void setAnimantionTimer(int timeInMillis)
    { verifyOTPLayout.setVisibility(View.GONE);
        verifyOTPTextView.setVisibility(View.GONE);
        resendOtpTextView.setVisibility(View.GONE);
        layoutSendingOtp.setVisibility(View.VISIBLE);
         new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTimer.setText("Wait for "+millisUntilFinished / 1000 + " " + "seconds");


            }

            @Override
            public void onFinish() {
                //layoutContainerForMobileNoWhenAuthenticating.setVisibility(View.GONE);
                layoutSendingOtp.setVisibility(View.GONE);
                verifyOTPLayout.setVisibility(View.VISIBLE);
                verifyOTPTextView.setVisibility(View.VISIBLE);
                resendOtpTextView.setVisibility(View.VISIBLE);
              //  layoutContainerForOtpOptions.setVisibility(View.VISIBLE);
                otpSMSText.setFocusable(true);

                otpSMSText.requestFocus();
                otpSMSText.setFocusableInTouchMode(true);
                otpSMSText.setCursorVisible(true);
                otpSMSText.invalidate();

            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSmsReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSmsReceiver);
    }

    private BroadcastReceiver mSmsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (Object aPdusObj : pdusObj) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                        String senderAddress = currentMessage.getDisplayOriginatingAddress();
                        String message = currentMessage.getDisplayMessageBody();

                        Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);
                        String verificationCode;
                        // if the SMS is not from our gateway, ignore the message

                            if (!senderAddress.toLowerCase().contains(AppConstants.SMS_ORIGIN.toLowerCase())) {
                                return;
                            }
                            // verification code from sms
                            verificationCode = getVerificationCode(message);

                        Log.e(TAG, "OTP received: " + verificationCode);
                        otpSMSText.setText(verificationCode);
                        showProgressDialog(getString(R.string.please_wait));

                        VerifyOTPController _controller = new VerifyOTPController(ActivityVerifyOTP.this, ActivityVerifyOTP.this);
                        if ("1".equals(isExistingUser)) {
                            _controller.getData(AppConstants.CONFIRM_MOBILE_OTP_FOR_EXISTING_USERS_REQUEST,
                                    mobileNumber, email, otpSMSText.getText().toString());
                        } else {
                            _controller.getData(AppConstants.VERIFY_OTP_REQUEST, mobileNumber, email, otpSMSText.getText().toString());
                        }

//                    Intent hhtpIntent = new Intent(context, HttpService.class);
//                    hhtpIntent.putExtra("otp", verificationCode);
//                    context.startService(hhtpIntent);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        /**
         * Getting the OTP from sms message body
         * ':' is the separator of OTP from the message
         *
         * @param message
         * @return
         */
        private String getVerificationCode(String message) {
//            String[] msgArray = message.split("\\r?\\n");
            String[] msgArray = message.split("\\s+");
            return msgArray[6].substring(0, 6);
        }
    };

    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            removeProgressDialog();
            Toast.makeText(this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            // showSnackbar(rootLayout, getResources().getString(R.string.server_error));
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.VERIFY_OTP_REQUEST:
                // save in
                UserInviteResponse responseData = (UserInviteResponse) response.getResponseObject();
                String message = responseData.getResult().getMessage();
                removeProgressDialog();
                if (responseData.getResponseCode() == 200) {
                    //  showSnackbar(rootLayout, message);
//                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    UserInviteModel userInviteModel = new UserInviteModel();
                    userInviteModel.setUserId(responseData.getResult().getData().getUserId());
                    userInviteModel.setEmail(email);
                    userInviteModel.setMobile(mobileNumber);
                    userInviteModel.setProfileImgUrl(profileImageUrl);
                    userInviteModel.setColorCode(colorCode);
                    userInviteModel.setFamilyInvites(responseData.getResult().getData().getFamilyInvites());
                    SharedPrefUtils.setUserFamilyInvites(this, new Gson().toJson(userInviteModel).toString());

                    ArrayList<FamilyInvites> familyInviteList = userInviteModel.getFamilyInvites();
                    if (null != familyInviteList && !familyInviteList.isEmpty()) {
                        Intent intent = new Intent(getApplicationContext(), ListFamilyInvitesActivity.class);
                        intent.putExtra("userInviteData", userInviteModel);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    } else {
                        Intent intent = new Intent(getApplicationContext(), CreateFamilyActivity.class);
                        intent.putExtra("userInviteData", userInviteModel);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    }

                }
            case AppConstants.CONFIRM_MOBILE_OTP_FOR_EXISTING_USERS_REQUEST:
                removeProgressDialog();
                UserInviteResponse resData = (UserInviteResponse) response.getResponseObject();
                String msg = resData.getResult().getMessage();
                if (resData.getResponseCode() == 200) {
                    Log.e("Mobile Verification", "Existing User Mobile");
//                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    UserInfo userInfo = SharedPrefUtils.getUserDetailModel(this);
                    userInfo.setMobile_number(mobileNumber);
                    SharedPrefUtils.setUserDetailModel(this, userInfo);
                    startSyncing();
                    startSyncingUserInfo();

                    Intent intent1 = new Intent(this, LoadingActivity.class);
                    // intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                } else if (resData.getResponseCode() == 400) {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }

                break;
            case AppConstants.NEW_SIGNUP_REQUEST:

                // Resend OTP for Sign Up.
                removeProgressDialog();
                UserResponse otpResponse = (UserResponse) response.getResponseObject();
                String otpMessage = otpResponse.getResult().getMessage();

                if (otpResponse.getResponseCode() == 200) {
                    Log.e("Mobile Verification", "New User");
//                    Toast.makeText(this, otpMessage, Toast.LENGTH_SHORT).show();
                } else if (otpResponse.getResponseCode() == 400) {
                    Toast.makeText(this, otpMessage, Toast.LENGTH_SHORT).show();
                }

                break;
            case AppConstants.UPDATE_MOBILE_FOR_EXISTING_USER_REQUEST:
                // Resend OTP for adding mobile for existing User.
                removeProgressDialog();
                UserResponse existingOTPResponse = (UserResponse) response.getResponseObject();
                String otpMsg = existingOTPResponse.getResult().getMessage();
                if (existingOTPResponse.getResponseCode() == 200) {
                    Log.e("Mobile Verification", "Existing User Mobile");
//                    Toast.makeText(this, otpMsg, Toast.LENGTH_SHORT).show();
                } else if (existingOTPResponse.getResponseCode() == 400) {
                    Toast.makeText(this, otpMsg, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void saveDatainDB(UserResponse model) {

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
//        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
//        kidsTable.deleteAll();
//        try {
//            kidsTable.beginTransaction();
//            for (KidsInfo kids : model.getResult().getData().getKidsInformation()) {
//
//                kidsTable.insertData(kids);
//
//            }
//            kidsTable.setTransactionSuccessful();
//        } finally {
//            kidsTable.endTransaction();
//        }

        // saving family

//        TableFamily familyTable = new TableFamily((BaseApplication) getApplicationContext());
//        familyTable.deleteAll();
//        try {
//            SharedPrefUtils.setpinCode(ActivityVerifyOTP.this, model.getResult().getData().getUser().getPincode());
//            familyTable.insertData(model.getResult().getData().getFamily());
//
//        } catch (Exception e) {
//            e.getMessage();
//        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
