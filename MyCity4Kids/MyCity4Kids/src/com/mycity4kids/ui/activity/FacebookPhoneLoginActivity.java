package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.ThemeUIManager;
import com.facebook.accountkit.ui.UIManager;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;

/**
 * Created by hemant on 20/9/18.
 */

public class FacebookPhoneLoginActivity extends BaseActivity {

    private static final String TAG = "FacebookPhoneLogin";
    public static int APP_REQUEST_CODE = 99;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_phone_login_activity);

        loginTextView = (TextView) findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneLogin(v);
            }
        });
        getCurrentAccount();
    }

    private void getCurrentAccount() {
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null) {
            //Handle Returning User
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {

                @Override
                public void onSuccess(final Account account) {

                    // Get Account Kit ID
                    String accountKitId = account.getId();
                    Log.e("Account Kit Id", accountKitId);

                    if (account.getPhoneNumber() != null) {
                        Log.e("CountryCode", "" + account.getPhoneNumber().getCountryCode());
                        Log.e("PhoneNumber", "" + account.getPhoneNumber().getPhoneNumber());

                        // Get phone number
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        String phoneNumberString = phoneNumber.toString();
//                        logout.setVisibility(View.VISIBLE);
//                        login.setVisibility(View.GONE);
                        Log.e("NumberString", phoneNumberString);


                    }

                    if (account.getEmail() != null)
                        Log.e("Email", account.getEmail());
                }

                @Override
                public void onError(final AccountKitError error) {
                    // Handle Error
                    Log.e(TAG, error.toString());
                }
            });

        } else {
            //Handle new or logged out user
            Log.e(TAG, "Logged Out");
            Toast.makeText(this, "Logged Out User", Toast.LENGTH_SHORT).show();
        }
    }

    public void phoneLogin(@Nullable View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.CODE
//        UIManager uiManager = new SkinManager(
//                SkinManager.Skin.CLASSIC,
//                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? getResources().getColor(R.color.app_red, null) : getResources().getColor(R.color.app_red)),
//                R.drawable._tab4,
//                SkinManager.Tint.WHITE,
//                1.0
//        );

        /*If you want default country code*/
        // configurationBuilder.setDefaultCountryCode("IN");
        int selectedThemeId = R.style.AppLoginTheme;
        UIManager themeManager = new ThemeUIManager(selectedThemeId);
        configurationBuilder.setUIManager(themeManager);
//        configurationBuilder.setUIManager(uiManager);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE && resultCode == RESULT_OK) {
            getCurrentAccount();
        }
    }

    public void logout(@Nullable View view) {
        AccountKit.logOut();
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null)
            Log.e(TAG, "Still Logged in...");
        else {

        }
//            logout.setVisibility(View.GONE);
//        login.setVisibility(View.VISIBLE);
    }

    @Override
    protected void updateUi(Response response) {

    }
}
