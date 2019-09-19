package com.mycity4kids.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

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
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;

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
    private String loginUser;
    private UserInfo uInfo = new UserInfo();

    private String[] userNameArray = {"poonam", "UserVideos", "Sangeetha", "AditiGupte", "Anirudh", "Amrita", "Adhunika", "Sunita", "Manisha", "ash", "antima", "kainaat", "vidhi", "blog@mc4k", "fff@mc4k", "Ms Voyager", "vania", "bbb", "Shavet", "Monika", "Priyanka", "Rakhi", "A.K Talwar", "Vinod Passi", "Pretty Arun", "Tina Sequeira", "Chiragi", "Monika Mahecha", "Prachi Mendiratta",
            "shweta shrivastva", "geetha", "rakheejain28@gmail.com", "prernawahi@yahoo.com", "Nivedita Khokhar", "Nitu Jhunjhunwala"};
    private String[] userIdArray = {"b8bf24150ac24ad4ab853cf099e6589e", "613b87c59d114c6fa37bcede79b38569", "648d3074e1f542179a54388ff3a27f59", "508244ccab684119b959e4bd7bc580d8", "3aa2436f16224f3dae5bb72610a907a6", "", "218f7fd8fe914c3887f508486fc9cf8e", "6051da4b0d83441e8133bbaf460dc5ad", "2db1f8d2fad94787846a31fe7135411d", "136a01652b444d3585e3955ba1ad7dff", "e9ccfd8e01234d458dd793e5692ded74", "a6620e449eaa438799b4f168d76dc563", "c56c3afb0a924a379fae787c17b91f02", "e21c692a6bc949a6b908ff2042214ac1", "ef20fd7764c14c728e90673a21f95790", "efdaaeb8e019478e8c835674a51dad6a", "1d6324b6114347c495394803d416b5fd", "6f57d7cb01fa46c89bf85e3d2ade7de3", "43f6a6e57f3d4ba0b41bab18509eae1f", "9b5032cd504b4c20a9f543059f18e2e6", "b1b10f47e32e4fdaa182e850f715414b", "61de7db0a6114272a906662f47af78b1",
            "92bf1a5a7dc2436dbca03c4f00e057a9", "e0e2fefd0eaf4f1996411c79454839f7", "382b2fe057d749ae8b1599b1dbbfba5f", "e9e055218682433faf10377c0266e412", "5cc5d90c19ab4d858a47d5ecf04a95c1",
            "f76d7bc8f996409696ed552a4e102c1b", "1676bec922db473fb0c708c984730f4a", "e576fb0752464a1898dc3673cf6584b4", "7d907fab39f942d884f2fe13fadb8c54", "058faef0c56541a9bbc027714ba31cc6", "a2cc05e5a20d4ee7ada030d471c5dcff",
            "8029226cf1e24f498f85cdc00b67b54e", "041ad9c7504e4f53b40b03783b2ed8c4"};
    private String[] mc4kArray = {"EAAC7f1na98IBAIuopvZBdJKkRlZAkLm8lG1rIOPwnxc4ffrmeTZAlDZBqjTXtX4ZBGcFwlqqearbFGQnAOiseTh3cZBUrBA4GpdZAMkWXgagvWDDi8f2o0pHcZC84CzGbSMZCflBY8NJJT6QfMkYfCtZAqQWayxJwnElwYcSsjZBBNZAKKUglsHEE0kYCarHZCign0WYZD", "EAAC7f1na98IBAKa5EHh9Ia6nmxCJI2Yregvqyxp4ZByVVuf68ABqjawBZC01NUkSoSFZBSYyPJpMnAzpO3iif7qz28IzuQCmA8ef1jqppkXgaYSt0EmvzeXx11ZBR8e7UQDJLNpwgCuuwoCBHma9HjqyF39nMJsqliFqOZC6I3eOM8Yt4ZBl0sVZC5Dp9A1YCNYCjkX44ZAZCuh4b71ogbqzz", "ya29.Ci_AA14SQ50z-NnaC6UrcMutRFKaFoaNutzfoAQbExGBpMkbcoi2LmV_pkvCeNw2ag", "c661177b9a941faec497d2d0a2fc5e93", "2f888da7f0dca54181832077d27aae8f", "", "", "", "", "ya29.GlsOBlh-u_gy9sxMTvT6CKSvMZRuQNHSf7b6Rmmyga7dvBd6kA168TBwXn6t_IRTvqyVidWzIAMxJ9pCZ1bTMzgI9TB5ulYMv1lczAFHO6DEy0uAhoMRjSVbdvyP", "EAAC7f1na98IBAIzI4UqlFRa9c8W9gIMxJ5l9J5bckDPho3lBKo8VBZBX9PK2Bha61OhR7lgr8l4LofkuCcXxzd0SqT1dGvHny8tkDCClBgEXIvogb45uJSUm69qnU4YkuD9PrSoY4TwjvAZBdAn8q2GxvwiSZAAwHTdccS4VjEp93pluwxHZBMpy4Ye5Jr2SWDxAkn37sAZDZD",
            "EAAC7f1na98IBAJoNJe361aqMSTZCc3rATu4Cas9ycooKw8cCQyobSZC19IZAEvtdVKZCM7Gg0u22v2RKzZBOBsTF1FTUQpfJZANl3wCGeWJPXQhtfuxIZCQRB8YmVZAthr0ZAaRxRfUZBbZCsVXpMcTZAzWcIY3Ph1lRjZAot60WCmXGVg04ubUneOo9fodlaFoZBLZCZBYZD", "47b6d2e8cf3b658c36051b8f49884f20", "eb89ee3d6ece42678086ddc5528f3cdb", "ac6f1b6ab5df7217b50d2207374855f6", "EAAC7f1na98IBAAj47OtcotNKz5zux0NpVdmHrqUP9rv0rXizMPD1QAcseJgY2w2ToRJThXiSlzaISWDUZAhtwOI7UTVYQc3ZCaLJocCx7JuMSVXO52CksTp4CwBcQ6hHYRpTxJ2CHNo7sYBqCavM46gnUx64gZD", "b43287acba314e5005558ed4f1b1cc8d", "acf825688fc4a97f3ddc046ec7ba9af6", "438d2aeb4d53b871e5204b5ee4b6c150", "ya29.Ci9rAwTDTDT7jAe0CfLZW5kgceRtPIPdOH0sirQpl0jKxRKSmsUvO4Py_P3kkcPnIQ", "f0eb6d95a80583fc4b98aabd820aa037",
            "c2cdb6540e8b3b694717ee0ca0f1047d", "ya29.GlvVBH8EGj-G3yQKgUuVZFy944ILJQLXEULCczUO6NCqrdQfoVKU-R9ezAD2z3PzniXzlojsxx8I51kOnD7KDM5W_eUFSxKpqWYNDTSPwbs01N34pirUz5bdmM0n",
            "EAAC7f1na98IBAEQabXwsYkXeCwx0ZApOZA4v0Bq2RSDqejXffIrg5HM5QvsTQqowzCVNKG6CjXev4Uc9a6WqPwxTT3abYqLMdEKQxkZBZCeEZCqttgGiGZCZC4b1HDNbODezV40RYuzhN81UZBG2yHh6yQ9P7mOs4b69hrvZAWZAJIj7kzPwhKTQJNTGWQoZCDY1hc4T7BqCOBuU7e3Fsi6l5K5F2kioPH0Q1bvd2qGZAhbGmWYVyFxxZCsN7",
            "ya29.Gls8BYKEi2wvLR-Zet7XKYc1zLAWdpVyO5hd371G6qbWY7K5qHqRr9uO9DzR2i3iAaPBfiLgoNFiDOssLpMsFkJhbGm0V_Ml676CsBKdoQrto_Iqqjv2ZlXAD7wZ",
            "ya29.Ci-lA5I6Px77LW78L-IOfRj6fF6OqCE8rfCnwqhvywZfsVK_ASLAp_B6_PZBCIwoKA",
            "EAAC7f1na98IBAMmZBIKXBiWBvVgVtrN6004aNpmZA4GJ1Fr9FdhVpZCZAmSkSdsZAEq6OTDuInnINxbc3s5LfWHHyA54rY32VusGg3HFBfQsD1k8V7QGZBZClMZCxeFIkey7AhcigEJJL0TiDCuDKjTulzmcBGkjuIVz6WQ84QRNuDcx2y1ffJaKhtXzzdqzlR3NssoPfIzhZB5NLsDjS2xITnP12fdDZBUoinA5qeTv26wAZDZD",
            "619ac30df4af5681a3d579ba9f07e65a78d1539b", "9eb5452942e9eb183cbe8c908bc3699b",
            "EAAC7f1na98IBAF9QLv1JrmkOkAcg1zReZA7VYHMmfqeFcp7eaZBMnCN6miZCt2YQwf6S2hXyXMrWsBJw6x8XI2at336lovzDtuxLWpVRjYjz0AFiW2z8vO0eWSIYK2LOYkmLtiKXhTbjnN4NpdclpZA9TGkdkhqIJU3rpCnidgHm6eqEdLcJOFC6tVzUSWNbdQP2BzJQpngawsTYiU7XDWPt7ZCZB06CJkhGyzxjZAzVPbdIOPayupZC",
            "ya29.GltMBSVMY9Q-63iHpx54s2GZjI4ys64BxqqMD6HPbDR8Q65DbURIk8V_VX1YcF7RTi3yZdm7NhUfGyAGzXng5KH6SD-cHVlzPBcbNyFFY0m_aTCoKXR-ZMutscGo", "ya29.GluuBNO7K1KftCDX1C24BUDJRGL9qOCkfPJ_LGurzInzS_yMKH6exrfsLwJOiIVGkpf-YpFgPS5uRO_HnylvVwJUIGpruK2xpzSENgqlupt1nOL1aHTqihmJPX-p",
            "EAAC7f1na98IBAOnkNnMZAlAJ3VQorGLq8RcJtQVRzE3bNuk73T3U0Cb4jfJctYn8xfiPNe4AL6EJZAR1LzD3UFmSAZBYvz23NeQbzHqpenoudbMes7DLA6g01WPKm3y1ec2J2q0HCKlNDp6FRyBD48MPU04ZBcKnRA7pmCr7LT3dVxcFkWYfNs5ZCTFZAe4GkZD",
            "ya29.GluBBZK84Jf2IzfuVTKifGUdqbF86DdkNS3VPcDSXXb6GvnnQ2aPvaz0aQtMy8xVsT2nET3DnubHwlVxD4wHOp79C8kyVRjc1doBLBoCe7O8TRnCC74rRRKAwdml",
            "ya29.GltQBQ24qjfOqnyfwYQt0LVmffDEHRZkkPG6LZ7da3vfUdhIQX4NlYmFrxuq9nrqoy9ugEioadIqbQ0nhufpbAuuAcsk4tVLzt_em3iRnAybywMqsj8ZCPXEDrJw"
    };
    private LinearLayout userContainer, root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_token_login_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        idEditText = (EditText) findViewById(R.id.idEditText);
        tokenEditText = (EditText) findViewById(R.id.tokenEditText);
        userContainer = (LinearLayout) findViewById(R.id.userContainer);
        loginTextView = (TextView) findViewById(R.id.loginTextView);

        for (int i = 0; i < userNameArray.length; i++) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.id_token_item_layout, null);
            ((TextView) view.getChildAt(0)).setText("Login with " + userNameArray[i]);

            userContainer.addView(view);
            view.setTag(userNameArray[i]);
            view.setOnClickListener(this);
        }

        idEditText.setOnClickListener(this);
        tokenEditText.setOnClickListener(this);
        loginTextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userItem:
                for (int i = 0; i < userNameArray.length; i++) {
                    if (((String) v.getTag()).equals(userNameArray[i])) {
                        uInfo.setDynamoId(userIdArray[i]);
                        uInfo.setMc4kToken(mc4kArray[i]);
                        break;
                    }
                }
                logoutCurrentUser();
                break;
            case R.id.loginTextView:
                loginUser = "";
                uInfo.setDynamoId(idEditText.getText().toString());
                uInfo.setMc4kToken(tokenEditText.getText().toString());
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
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    UserInfo model = new UserInfo();
                    model.setId(responseData.getData().get(0).getResult().getId());
                    model.setDynamoId(responseData.getData().get(0).getResult().getDynamoId());
                    model.setEmail(responseData.getData().get(0).getResult().getEmail());
                    model.setMc4kToken(uInfo.getMc4kToken());
                    model.setIsValidated(responseData.getData().get(0).getResult().getIsValidated());
                    model.setFirst_name(responseData.getData().get(0).getResult().getFirstName());
                    model.setLast_name(responseData.getData().get(0).getResult().getLastName());
                    model.setUserType(responseData.getData().get(0).getResult().getUserType());
                    model.setProfilePicUrl(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    model.setSessionId(responseData.getData().get(0).getResult().getSessionId());


                    model.setBlogTitle(responseData.getData().get(0).getResult().getBlogTitle());


                    //                    model.setLoginMode(loginMode);
                    SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), model);
                    SharedPrefUtils.setProfileImgUrl(BaseApplication.getAppContext(), responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());

                    if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                        //token already expired or not yet connected with facebook
                        SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(), "1");
                    } else {
                        SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(),
                                responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                    }

                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String version = pInfo.versionName;
                    if (version.equals(AppConstants.PHOENIX_RELEASE_VERSION)) {
                        SharedPrefUtils.setPhoenixFirstLaunch(BaseApplication.getAppContext(), false);
                    }
                    if (version.equals(AppConstants.FACEBOOK_CONNECT_RELEASE_VERSION)) {
                        SharedPrefUtils.setFBConnectFirstLaunch(BaseApplication.getAppContext(), false);
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
            String pushToken = SharedPrefUtils.getDeviceToken(BaseApplication.getAppContext());
            SharedPrefUtils.clearPrefrence(BaseApplication.getAppContext());
            SharedPrefUtils.setDeviceToken(BaseApplication.getAppContext(), pushToken);
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
            BaseApplication.setBlogResponse(null);
            BaseApplication.setBusinessREsponse(null);

            // clear all sessions

            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
            // set logout flag
            SharedPrefUtils.setLogoutFlag(BaseApplication.getAppContext(), true);
            loginWithIdToken(loginUser);

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

    private void loginWithIdToken(String loggedInUser) {
        BaseApplication.getInstance().destroyRetrofitInstance();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//        if ("shavet".equals(loggedInUser)) {
//            uInfo.setDynamoId("43f6a6e57f3d4ba0b41bab18509eae1f");
//            uInfo.setMc4kToken("438d2aeb4d53b871e5204b5ee4b6c150");
//        } else if ("monika".equals(loggedInUser)) {
//            uInfo.setDynamoId("9b5032cd504b4c20a9f543059f18e2e6");
//            uInfo.setMc4kToken("ya29.Ci9rAwTDTDT7jAe0CfLZW5kgceRtPIPdOH0sirQpl0jKxRKSmsUvO4Py_P3kkcPnIQ");
//        } else if ("priyanka".equals(loggedInUser)) {
//            uInfo.setDynamoId("b1b10f47e32e4fdaa182e850f715414b");
//            uInfo.setMc4kToken("f0eb6d95a80583fc4b98aabd820aa037");
//        } else if ("rakhi".equals(loggedInUser)) {
//            uInfo.setDynamoId("61de7db0a6114272a906662f47af78b1");
//            uInfo.setMc4kToken("c2cdb6540e8b3b694717ee0ca0f1047d");
//        } else {
//            uInfo.setDynamoId(idEditText.getText().toString());
//            uInfo.setMc4kToken(tokenEditText.getText().toString());
//        }


        SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), uInfo);

        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = loginRegistrationAPI.getUserDetails(uInfo.getDynamoId());
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
            kidsInfo.setDate_of_birth(convertTime("" + kid.getBirthDay()));
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
