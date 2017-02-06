package com.mycity4kids.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.LogoutController;
import com.mycity4kids.dbtable.ExternalCalendarTable;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableApiEvents;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableAttendee;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableFile;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableNotes;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.dbtable.TableWhoToRemind;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.logout.LogoutResponse;
import com.mycity4kids.models.response.KidsModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.AppointmentManager;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.sync.PushTokenService;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 20/1/17.
 */
public class IdTokenLoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText idEditText, tokenEditText;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_token_login_activity);
        idEditText = (EditText) findViewById(R.id.idEditText);
        tokenEditText = (EditText) findViewById(R.id.tokenEditText);
        loginTextView = (TextView) findViewById(R.id.loginTextView);

        idEditText.setOnClickListener(this);
        tokenEditText.setOnClickListener(this);
        loginTextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginTextView:
                logoutCurrentUser();
                break;
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
                    model.setMc4kToken(tokenEditText.getText().toString());
                    model.setIsValidated(responseData.getData().get(0).getResult().getIsValidated());
                    model.setFirst_name(responseData.getData().get(0).getResult().getFirstName());
                    model.setLast_name(responseData.getData().get(0).getResult().getLastName());
                    model.setProfilePicUrl(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    model.setSessionId(responseData.getData().get(0).getResult().getSessionId());
//                    model.setLoginMode(loginMode);
                    SharedPrefUtils.setUserDetailModel(IdTokenLoginActivity.this, model);
                    SharedPrefUtils.setProfileImgUrl(IdTokenLoginActivity.this, responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());

                    if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                        //token already expired or not yet connected with facebook
                        SharedPrefUtils.setFacebookConnectedFlag(IdTokenLoginActivity.this, "1");
                    } else {
                        SharedPrefUtils.setFacebookConnectedFlag(IdTokenLoginActivity.this,
                                responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                    }

                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String version = pInfo.versionName;
                    if (version.equals(AppConstants.PHOENIX_RELEASE_VERSION)) {
                        SharedPrefUtils.setPhoenixFirstLaunch(IdTokenLoginActivity.this, false);
                    }
                    if (version.equals(AppConstants.FACEBOOK_CONNECT_RELEASE_VERSION)) {
                        SharedPrefUtils.setFBConnectFirstLaunch(IdTokenLoginActivity.this, false);
                    }

                    //Custom sign up user but email is not yet verfifed.
                    else if (!AppConstants.VALIDATED_USER.equals(model.getIsValidated())) {
                        showToast("Please verify your account to login");
                    }
                    //Verified User
                    else {
                        if (null != responseData.getData().get(0).getResult().getKids()) {
                            saveKidsInformation(responseData.getData().get(0).getResult().getKids());
                        }
//                        Intent intent = new Intent(IdTokenLoginActivity.this, PushTokenService.class);
//                        startService(intent);
                        Intent intent1 = new Intent(IdTokenLoginActivity.this, LoadingActivity.class);
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

    private void logoutCurrentUser() {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            final LogoutController _controller = new LogoutController(this, this);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

            dialog.setMessage(getResources().getString(R.string.logout_msg)).setNegativeButton(R.string.new_yes
                    , new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            showProgressDialog(getResources().getString(R.string.please_wait));
                            _controller.getData(AppConstants.LOGOUT_REQUEST, "");
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
        } else {
            ToastUtils.showToast(this, getString(R.string.error_network));
        }
    }

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();
        if (response == null) {
            Toast.makeText(this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            return;
        }
        LogoutResponse responseData = (LogoutResponse) response.getResponseObject();
        String message = responseData.getResult().getMessage();
        if (responseData.getResponseCode() == 200) {
            String pushToken = SharedPrefUtils.getDeviceToken(this);
            SharedPrefUtils.clearPrefrence(this);
            SharedPrefUtils.setDeviceToken(this, pushToken);
            /**
             * delete table from local also;
             */
            UserTable _tables = new UserTable((BaseApplication) getApplicationContext());
            _tables.deleteAll();

            TableFamily _familytables = new TableFamily((BaseApplication) getApplicationContext());
            _familytables.deleteAll();

            TableAdult _adulttables = new TableAdult((BaseApplication) getApplicationContext());
            _adulttables.deleteAll();

            TableKids _kidtables = new TableKids((BaseApplication) getApplicationContext());
            _kidtables.deleteAll();

            new TableAppointmentData(BaseApplication.getInstance()).deleteAll();
            new TableNotes(BaseApplication.getInstance()).deleteAll();
            new TableFile(BaseApplication.getInstance()).deleteAll();
            new TableAttendee(BaseApplication.getInstance()).deleteAll();
            new TableWhoToRemind(BaseApplication.getInstance()).deleteAll();


            new TableTaskData(BaseApplication.getInstance()).deleteAll();
            new TableTaskList(BaseApplication.getInstance()).deleteAll();
            new TaskTableAttendee(BaseApplication.getInstance()).deleteAll();
            new TaskTableWhoToRemind(BaseApplication.getInstance()).deleteAll();
            new TaskTableFile(BaseApplication.getInstance()).deleteAll();
            new TaskTableNotes(BaseApplication.getInstance()).deleteAll();
            new TaskCompletedTable(BaseApplication.getInstance()).deleteAll();
            new TableApiEvents(BaseApplication.getInstance()).deleteAll();

            new ExternalCalendarTable(BaseApplication.getInstance()).deleteAll();

            // clear cachee
            AppointmentManager.getInstance(this).clearList();
            BaseApplication.setBlogResponse(null);
            BaseApplication.setBusinessREsponse(null);

            // clear all sessions

            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
            // set logout flag
            SharedPrefUtils.setLogoutFlag(this, true);
            loginWithIdToken();

//            Intent intent = new Intent(this, ActivityLogin.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//
//            finish();

        } else if (responseData.getResponseCode() == 400) {
            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void loginWithIdToken() {
        BaseApplication.getInstance().destroyRetrofitInstance();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();

        UserInfo uInfo = new UserInfo();
        uInfo.setDynamoId(idEditText.getText().toString());
        uInfo.setMc4kToken(tokenEditText.getText().toString());
        SharedPrefUtils.setUserDetailModel(this, uInfo);

        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = loginRegistrationAPI.getUserDetails(idEditText.getText().toString());
        call.enqueue(onLoginResponseReceivedListener);
    }

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

}
