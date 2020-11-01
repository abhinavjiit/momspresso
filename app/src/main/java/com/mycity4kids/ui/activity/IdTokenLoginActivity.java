package com.mycity4kids.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.ToastUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 20/1/17.
 */
public class IdTokenLoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText idEditText, tokenEditText;
    private UserInfo uInfo = new UserInfo();

    private String[] userNameArray = {"Priya Rastogi", "Ren bisht", "iyer", "hemant.parmar", "mom1",
            "hemantparmar23 -- 9899741739",
            "poonam",
            "UserVideos",
            "Sangeetha",
            "AditiGupte",
            "Anirudh", "Amrita", "Adhunika", "Sunita", "Manisha", "ash", "antima", "kainaat", "vidhi", "blog@mc4k",
            "fff@mc4k", "Ms Voyager", "vania", "bbb", "Shavet", "Monika", "Priyanka", "Rakhi", "A.K Talwar",
            "Vinod Passi", "Pretty Arun", "Tina Sequeira", "Chiragi", "Monika Mahecha", "Prachi Mendiratta",
            "shweta shrivastva", "geetha", "rakheejain28@gmail.com", "prernawahi@yahoo.com", "Nivedita Khokhar",
            "Nitu Jhunjhunwala"};
    private String[] userIdArray = {"34c96ab668cf45159577c00a1be619b9", "a3c419173c4444fa968297b8a38bec98",
            "7ed26d1eba9f4faa9d3b8cc8b697384d",
            "6fbdd0c3b8284b17a8992f8f91d9fe62",
            "9634bd40ab694508b54a833912280ac2",
            "f675214502c04d61aa10823cad81e7c0",
            "b8bf24150ac24ad4ab853cf099e6589e",
            "613b87c59d114c6fa37bcede79b38569", "648d3074e1f542179a54388ff3a27f59", "508244ccab684119b959e4bd7bc580d8",
            "3aa2436f16224f3dae5bb72610a907a6", "", "218f7fd8fe914c3887f508486fc9cf8e",
            "6051da4b0d83441e8133bbaf460dc5ad", "2db1f8d2fad94787846a31fe7135411d", "136a01652b444d3585e3955ba1ad7dff",
            "e9ccfd8e01234d458dd793e5692ded74", "a6620e449eaa438799b4f168d76dc563", "c56c3afb0a924a379fae787c17b91f02",
            "e21c692a6bc949a6b908ff2042214ac1", "ef20fd7764c14c728e90673a21f95790", "efdaaeb8e019478e8c835674a51dad6a",
            "1d6324b6114347c495394803d416b5fd", "6f57d7cb01fa46c89bf85e3d2ade7de3", "43f6a6e57f3d4ba0b41bab18509eae1f",
            "9b5032cd504b4c20a9f543059f18e2e6", "b1b10f47e32e4fdaa182e850f715414b", "61de7db0a6114272a906662f47af78b1",
            "92bf1a5a7dc2436dbca03c4f00e057a9", "e0e2fefd0eaf4f1996411c79454839f7", "382b2fe057d749ae8b1599b1dbbfba5f",
            "e9e055218682433faf10377c0266e412", "5cc5d90c19ab4d858a47d5ecf04a95c1",
            "f76d7bc8f996409696ed552a4e102c1b", "1676bec922db473fb0c708c984730f4a", "e576fb0752464a1898dc3673cf6584b4",
            "7d907fab39f942d884f2fe13fadb8c54", "058faef0c56541a9bbc027714ba31cc6", "a2cc05e5a20d4ee7ada030d471c5dcff",
            "8029226cf1e24f498f85cdc00b67b54e", "041ad9c7504e4f53b40b03783b2ed8c4"};
    private String[] mc4kArray = {
            "ya29.Gls8B0hVlb7NnlmPfqONO5a2HNLv2kw8axNrMNJXSCKFVhfYHi9rPNHhFNTEMx9IogiJOZ6kFfno0RH8YyhjHMTnH31YhYEmFHLyqNNUSkd42qi_xK5PGTuqxIe7",
            "dc1c0bd3-65ea-4999-9f22-127168bc885c",
            "EAAC7f1na98IBAAh3hgOh3VYmS6QlzPeD4p2cNInVupGHPgkHPwTZCbH8ICMGIGZCFimzLBDlJw6FsSZCP7RBVuDvD1iPAmCwq0owpht5QLa3zdzPANnLZCWUUL4ySZCFIOjavHZAjhbh1eW9gR1G2TVquYfbeX69vMDcfK3F8WG3OGLF34OiCq4nPxZCDo5hUYf2hTekTTRpBcM3N14cGuKZCVJ41jvV8twZD",
            "ya29.Ci9rA0uT5Mu3We5hggYpjqJ_2GgjpEd1aIzj2xIeEy4MmGyAQ0hfQVdOVsUIkMCCog",
            "46e609f938fd428bbe09769c1b612fa7", "f24baa23e76537fe65e11b9ccef5a9bf",
            "EAAC7f1na98IBAIuopvZBdJKkRlZAkLm8lG1rIOPwnxc4ffrmeTZAlDZBqjTXtX4ZBGcFwlqqearbFGQnAOiseTh3cZBUrBA4GpdZAMkWXgagvWDDi8f2o0pHcZC84CzGbSMZCflBY8NJJT6QfMkYfCtZAqQWayxJwnElwYcSsjZBBNZAKKUglsHEE0kYCarHZCign0WYZD",
            "EAAC7f1na98IBAKa5EHh9Ia6nmxCJI2Yregvqyxp4ZByVVuf68ABqjawBZC01NUkSoSFZBSYyPJpMnAzpO3iif7qz28IzuQCmA8ef1jqppkXgaYSt0EmvzeXx11ZBR8e7UQDJLNpwgCuuwoCBHma9HjqyF39nMJsqliFqOZC6I3eOM8Yt4ZBl0sVZC5Dp9A1YCNYCjkX44ZAZCuh4b71ogbqzz",
            "ya29.Ci_AA14SQ50z-NnaC6UrcMutRFKaFoaNutzfoAQbExGBpMkbcoi2LmV_pkvCeNw2ag",
            "c661177b9a941faec497d2d0a2fc5e93", "2f888da7f0dca54181832077d27aae8f", "", "", "", "",
            "ya29.GlsOBlh-u_gy9sxMTvT6CKSvMZRuQNHSf7b6Rmmyga7dvBd6kA168TBwXn6t_IRTvqyVidWzIAMxJ9pCZ1bTMzgI9TB5ulYMv1lczAFHO6DEy0uAhoMRjSVbdvyP",
            "EAAC7f1na98IBAIzI4UqlFRa9c8W9gIMxJ5l9J5bckDPho3lBKo8VBZBX9PK2Bha61OhR7lgr8l4LofkuCcXxzd0SqT1dGvHny8tkDCClBgEXIvogb45uJSUm69qnU4YkuD9PrSoY4TwjvAZBdAn8q2GxvwiSZAAwHTdccS4VjEp93pluwxHZBMpy4Ye5Jr2SWDxAkn37sAZDZD",
            "EAAC7f1na98IBAJoNJe361aqMSTZCc3rATu4Cas9ycooKw8cCQyobSZC19IZAEvtdVKZCM7Gg0u22v2RKzZBOBsTF1FTUQpfJZANl3wCGeWJPXQhtfuxIZCQRB8YmVZAthr0ZAaRxRfUZBbZCsVXpMcTZAzWcIY3Ph1lRjZAot60WCmXGVg04ubUneOo9fodlaFoZBLZCZBYZD",
            "47b6d2e8cf3b658c36051b8f49884f20", "eb89ee3d6ece42678086ddc5528f3cdb", "ac6f1b6ab5df7217b50d2207374855f6",
            "EAAC7f1na98IBAAj47OtcotNKz5zux0NpVdmHrqUP9rv0rXizMPD1QAcseJgY2w2ToRJThXiSlzaISWDUZAhtwOI7UTVYQc3ZCaLJocCx7JuMSVXO52CksTp4CwBcQ6hHYRpTxJ2CHNo7sYBqCavM46gnUx64gZD",
            "b43287acba314e5005558ed4f1b1cc8d", "acf825688fc4a97f3ddc046ec7ba9af6", "438d2aeb4d53b871e5204b5ee4b6c150",
            "ya29.Ci9rAwTDTDT7jAe0CfLZW5kgceRtPIPdOH0sirQpl0jKxRKSmsUvO4Py_P3kkcPnIQ",
            "f0eb6d95a80583fc4b98aabd820aa037",
            "c2cdb6540e8b3b694717ee0ca0f1047d",
            "ya29.GlvVBH8EGj-G3yQKgUuVZFy944ILJQLXEULCczUO6NCqrdQfoVKU-R9ezAD2z3PzniXzlojsxx8I51kOnD7KDM5W_eUFSxKpqWYNDTSPwbs01N34pirUz5bdmM0n",
            "EAAC7f1na98IBAEQabXwsYkXeCwx0ZApOZA4v0Bq2RSDqejXffIrg5HM5QvsTQqowzCVNKG6CjXev4Uc9a6WqPwxTT3abYqLMdEKQxkZBZCeEZCqttgGiGZCZC4b1HDNbODezV40RYuzhN81UZBG2yHh6yQ9P7mOs4b69hrvZAWZAJIj7kzPwhKTQJNTGWQoZCDY1hc4T7BqCOBuU7e3Fsi6l5K5F2kioPH0Q1bvd2qGZAhbGmWYVyFxxZCsN7",
            "ya29.Gls8BYKEi2wvLR-Zet7XKYc1zLAWdpVyO5hd371G6qbWY7K5qHqRr9uO9DzR2i3iAaPBfiLgoNFiDOssLpMsFkJhbGm0V_Ml676CsBKdoQrto_Iqqjv2ZlXAD7wZ",
            "ya29.Ci-lA5I6Px77LW78L-IOfRj6fF6OqCE8rfCnwqhvywZfsVK_ASLAp_B6_PZBCIwoKA",
            "EAAC7f1na98IBAMmZBIKXBiWBvVgVtrN6004aNpmZA4GJ1Fr9FdhVpZCZAmSkSdsZAEq6OTDuInnINxbc3s5LfWHHyA54rY32VusGg3HFBfQsD1k8V7QGZBZClMZCxeFIkey7AhcigEJJL0TiDCuDKjTulzmcBGkjuIVz6WQ84QRNuDcx2y1ffJaKhtXzzdqzlR3NssoPfIzhZB5NLsDjS2xITnP12fdDZBUoinA5qeTv26wAZDZD",
            "619ac30df4af5681a3d579ba9f07e65a78d1539b", "9eb5452942e9eb183cbe8c908bc3699b",
            "EAAC7f1na98IBAF9QLv1JrmkOkAcg1zReZA7VYHMmfqeFcp7eaZBMnCN6miZCt2YQwf6S2hXyXMrWsBJw6x8XI2at336lovzDtuxLWpVRjYjz0AFiW2z8vO0eWSIYK2LOYkmLtiKXhTbjnN4NpdclpZA9TGkdkhqIJU3rpCnidgHm6eqEdLcJOFC6tVzUSWNbdQP2BzJQpngawsTYiU7XDWPt7ZCZB06CJkhGyzxjZAzVPbdIOPayupZC",
            "ya29.GltMBSVMY9Q-63iHpx54s2GZjI4ys64BxqqMD6HPbDR8Q65DbURIk8V_VX1YcF7RTi3yZdm7NhUfGyAGzXng5KH6SD-cHVlzPBcbNyFFY0m_aTCoKXR-ZMutscGo",
            "ya29.GluuBNO7K1KftCDX1C24BUDJRGL9qOCkfPJ_LGurzInzS_yMKH6exrfsLwJOiIVGkpf-YpFgPS5uRO_HnylvVwJUIGpruK2xpzSENgqlupt1nOL1aHTqihmJPX-p",
            "EAAC7f1na98IBAOnkNnMZAlAJ3VQorGLq8RcJtQVRzE3bNuk73T3U0Cb4jfJctYn8xfiPNe4AL6EJZAR1LzD3UFmSAZBYvz23NeQbzHqpenoudbMes7DLA6g01WPKm3y1ec2J2q0HCKlNDp6FRyBD48MPU04ZBcKnRA7pmCr7LT3dVxcFkWYfNs5ZCTFZAe4GkZD",
            "ya29.GluBBZK84Jf2IzfuVTKifGUdqbF86DdkNS3VPcDSXXb6GvnnQ2aPvaz0aQtMy8xVsT2nET3DnubHwlVxD4wHOp79C8kyVRjc1doBLBoCe7O8TRnCC74rRRKAwdml",
            "ya29.GltQBQ24qjfOqnyfwYQt0LVmffDEHRZkkPG6LZ7da3vfUdhIQX4NlYmFrxuq9nrqoy9ugEioadIqbQ0nhufpbAuuAcsk4tVLzt_em3iRnAybywMqsj8ZCPXEDrJw"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_token_login_activity);
        LinearLayout root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        idEditText = (EditText) findViewById(R.id.idEditText);
        tokenEditText = (EditText) findViewById(R.id.tokenEditText);
        LinearLayout userContainer = (LinearLayout) findViewById(R.id.userContainer);
        TextView loginTextView = (TextView) findViewById(R.id.loginTextView);

        for (String s : userNameArray) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.id_token_item_layout, null);
            ((TextView) view.getChildAt(0)).setText("Login with " + s);
            userContainer.addView(view);
            view.setTag(s);
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
                    model.setBlogTitle(responseData.getData().get(0).getResult().getBlogTitle());
                    model.setVideoPreferredLanguages(
                            responseData.getData().get(0).getResult().getVideoPreferredLanguages());
                    SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), model);
                    SharedPrefUtils.setProfileImgUrl(BaseApplication.getAppContext(),
                            responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());

                    if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                        //token already expired or not yet connected with facebook
                        SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(), "1");
                    } else {
                        SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(),
                                responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                    }
                    //Custom sign up user but email is not yet verfifed.
                    if (!AppConstants.VALIDATED_USER.equals(model.getIsValidated())) {
                        showToast("Please verify your account to login");
                    }
                    //Verified User
                    else {
                        Intent intent1 = new Intent(IdTokenLoginActivity.this, LoadingActivity.class);
                        startActivity(intent1);
                    }
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
            Log.d("MC4kException", Log.getStackTraceString(t));
            FirebaseCrashlytics.getInstance().recordException(t);
            showToast(getString(R.string.went_wrong));
        }
    };

    private void logoutCurrentUser() {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

            dialog.setMessage(getResources().getString(R.string.logout_msg)).setNegativeButton(R.string.new_yes
                    , (_dialog, which) -> {
                        _dialog.cancel();
                        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
                        Call<ResponseBody> call = loginRegistrationAPI.logout();
                        call.enqueue(logoutUserResponseListener);
                    }).setPositiveButton(R.string.new_cancel,
                    (dialog1, which) -> dialog1.cancel()).setIcon(android.R.drawable.ic_dialog_alert);
            AlertDialog _alert = dialog.create();
            _alert.show();
            _alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                    .setTextColor(getResources().getColor(R.color.home_light_blue));
            _alert.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(R.color.canceltxt_color));
        } else {
            ToastUtils.showToast(this, getString(R.string.error_network));
        }
    }

    private Callback<ResponseBody> logoutUserResponseListener = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            clearUserDataPostLogout();
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            clearUserDataPostLogout();
        }
    };

    private void clearUserDataPostLogout() {
        String pushToken = SharedPrefUtils.getDeviceToken(BaseApplication.getAppContext());
        SharedPrefUtils.clearPrefrence(BaseApplication.getAppContext());
        SharedPrefUtils.setDeviceToken(BaseApplication.getAppContext(), pushToken);
        // set logout flag
        SharedPrefUtils.setLogoutFlag(BaseApplication.getAppContext(), true);
        loginWithIdToken();
    }

    private void loginWithIdToken() {
        BaseApplication.getInstance().destroyRetrofitInstance();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();

        SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), uInfo);

        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = loginRegistrationAPI.getUserDetails(uInfo.getDynamoId());
        call.enqueue(onLoginResponseReceivedListener);
    }
}
