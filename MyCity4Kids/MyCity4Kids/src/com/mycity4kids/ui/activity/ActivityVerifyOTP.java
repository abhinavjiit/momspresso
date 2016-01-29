package com.mycity4kids.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.VerifyOTPController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by hemant on 20/1/16.
 */
public class ActivityVerifyOTP extends BaseActivity {

    private static final String TAG = ActivityVerifyOTP.class.getSimpleName();
    private Toolbar mToolbar;
    private EditText otpSMSText;
    private TextView verifyOTPTextView;

    private String email, mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_otp_activity);

        email = getIntent().getExtras().getString("email");
        mobileNumber = getIntent().getExtras().getString("mobile");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        otpSMSText = (EditText) findViewById(R.id.otpSMSText);
        verifyOTPTextView = (TextView) findViewById(R.id.verifyOTPTextView);
        setSupportActionBar(mToolbar);
        verifyOTPTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityVerifyOTP.this, ListFamilyInvitesActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Verify Mobile");
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
                        if ("9899741739".equals(senderAddress)) {
                            verificationCode = message;
                        } else {
                            if (!senderAddress.toLowerCase().contains(AppConstants.SMS_ORIGIN.toLowerCase())) {
                                return;
                            }
                            // verification code from sms
                            verificationCode = getVerificationCode(message);
                        }
                        Log.e(TAG, "OTP received: " + verificationCode);
                        otpSMSText.setText(verificationCode);
                        showProgressDialog(getString(R.string.please_wait));
                        VerifyOTPController _controller = new VerifyOTPController(ActivityVerifyOTP.this, ActivityVerifyOTP.this);
                        _controller.getData(AppConstants.VERIFY_OTP_REQUEST, mobileNumber, email, verificationCode);
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
            return msgArray[5].substring(0, 6);
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
                UserResponse responseData = (UserResponse) response.getResponseObject();
                String message = responseData.getResult().getMessage();
                if (responseData.getResponseCode() == 200) {

                    //  showSnackbar(rootLayout, message);

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                    // save in db
                    saveDatainDB(responseData);

                    // store userdetail in prefrences

                    UserInfo model = new UserInfo();
                    model.setId(responseData.getResult().getData().getUser().getId());
                    model.setFamily_id(responseData.getResult().getData().getUser().getFamily_id());
                    model.setColor_code(responseData.getResult().getData().getUser().getColor_code());
                    model.setSessionId(responseData.getResult().getData().getUser().getSessionId());
                    model.setFirst_name(responseData.getResult().getData().getUser().getFirst_name() + " " + responseData.getResult().getData().getUser().getLast_name());

                    SharedPrefUtils.setUserDetailModel(ActivityVerifyOTP.this, model);
                    // shift to my city for kids
                    removeProgressDialog();

                    break;
                }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forgot_password, menu);
        return true;
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
