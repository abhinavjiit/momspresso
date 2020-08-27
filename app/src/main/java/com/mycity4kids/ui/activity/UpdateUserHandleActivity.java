package com.mycity4kids.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.campaignmodels.UserHandleAvailabilityResponse;
import com.mycity4kids.models.response.BaseResponseGeneric;
import com.mycity4kids.models.response.UserHandleResult;
import com.mycity4kids.models.response.UserHandleSuggestionResponse;
import com.mycity4kids.models.rewardsmodels.RewardsPersonalResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserHandleSuggestionAPI;
import com.mycity4kids.utils.StringUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class UpdateUserHandleActivity extends BaseActivity {

    private String loginMode = "";
    private String userHandle = "";
    private String email = "";
    private String emailValidated = "";
    private String fName = "";
    private String lName = "";
    private TextView fNameTextView, lNameTextView, userHandleTextView, suggestions, emailTextView, phoneTextView, checkTextView, userAvailabilityResultTextView, phoneEditView, textSaveAndContinue;
    private EditText fNameEditView, lNameEditView, userHandleEditView, emailEditView;
    private RadioGroup suggestionRadioGroup;
    private RadioButton suggestion_1, suggestion_2;
    private boolean isHandleChecked = false;
    private static final int VERIFY_NUMBER_ACCOUNTKIT_REQUEST_CODE = 1000;
    private String accountKitAuthCode = "", mobile;
    private UserHandleResult userDetailResult;
    private LinearLayout phoneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_handle_suggestion_flow);

        fNameTextView = findViewById(R.id.fNameTextView);
        lNameTextView = findViewById(R.id.lNameTextView);
        userHandleTextView = findViewById(R.id.userHandleTextView);
        suggestions = findViewById(R.id.suggestions);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        checkTextView = findViewById(R.id.checkTextView);
        userAvailabilityResultTextView = findViewById(R.id.userAvailabilityResultTextView);
        textSaveAndContinue = findViewById(R.id.textSaveAndContinue);

        fNameEditView = findViewById(R.id.fNameEditView);
        lNameEditView = findViewById(R.id.lNameEditView);
        userHandleEditView = findViewById(R.id.userHandleEditView);
        emailEditView = findViewById(R.id.emailEditView);
        phoneEditView = findViewById(R.id.phoneEditView);

        suggestionRadioGroup = findViewById(R.id.suggestionRadioGroup);

        suggestion_1 = findViewById(R.id.suggestion_1);
        suggestion_2 = findViewById(R.id.suggestion_2);

        phoneLayout = findViewById(R.id.phone_layout);

        fName = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getFirst_name();
        lName = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getLast_name();
        userHandle = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getUserHandle();
        if (getIntent().hasExtra("loginMode")) {
            loginMode = getIntent().getStringExtra("loginMode");
        }
        email = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getEmail();
        emailValidated = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getEmailValidated();

        if (!StringUtils.isNullOrEmpty(userHandle)) {
            userHandleEditView.setText(userHandle);
        }

        if (!StringUtils.isNullOrEmpty(email)) {
            emailEditView.setText(email);
            if (emailValidated.equals("1")) {
                emailEditView.setEnabled(false);
                emailEditView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
            }
        }

        if (loginMode.equals("phone")) {
            fNameEditView.setText(fName);
            lNameEditView.setText(lName);
        }

        getHandleSuggestion();

        if (userHandleEditView.getText().toString().equals(userHandle)) {
            checkTextView.setVisibility(View.GONE);
            isHandleChecked = true;
        }

        phoneEditView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateUserHandleActivity.this, OTPActivity.class);
                startActivityForResult(intent, VERIFY_NUMBER_ACCOUNTKIT_REQUEST_CODE);
            }
        });

        userHandleEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isHandleChecked = false;
                userAvailabilityResultTextView.setVisibility(View.GONE);
                if (userHandleEditView.getText().length() >= 7 && !userHandleEditView.getText().toString().equals(
                        userHandle
                )) {
                    checkTextView.setVisibility(View.VISIBLE);
                } else {
                    checkTextView.setVisibility(View.GONE);
                }
            }
        });

        checkTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userHandleEditView.getText().length() >= 7) {
                    if (userHandleEditView.getText().charAt(userHandleEditView.getText().length() - 1) == '.') {
                        Toast.makeText(
                                UpdateUserHandleActivity.this,
                                "User handle cannot ends with dot(.)",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        checkUserHandleAvailability();
                    }
                } else {
                    Toast.makeText(
                            UpdateUserHandleActivity.this,
                            "Handle should be of minimum 7 characters",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        textSaveAndContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isHandleChecked) {
                    if (prepareDataForPosting()) {
                        postData();
                    }
                } else {
                    if (userHandleEditView.getText().length() < 7) {
                        Toast.makeText(
                                UpdateUserHandleActivity.this,
                                "Handle should be of minimum 7 characters",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                                UpdateUserHandleActivity.this,
                                "Please check handle availability",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }
        });

        if (loginMode.equals("fb")) {
            fNameTextView.setVisibility(View.GONE);
            fNameEditView.setVisibility(View.GONE);
            lNameTextView.setVisibility(View.GONE);
            lNameEditView.setVisibility(View.GONE);
        } else if (loginMode.equals("phone")) {
            phoneLayout.setVisibility(View.GONE);
            phoneTextView.setVisibility(View.GONE);
        } else if (loginMode.equals("gp") || loginMode.equals("email")) {
            fNameTextView.setVisibility(View.GONE);
            fNameEditView.setVisibility(View.GONE);
            lNameTextView.setVisibility(View.GONE);
            lNameEditView.setVisibility(View.GONE);
            emailTextView.setVisibility(View.GONE);
            emailEditView.setVisibility(View.GONE);
        }

        suggestionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                userHandleEditView.setText(radioButton.getText());
                checkTextView.setVisibility(View.GONE);
                isHandleChecked = true;
            }
        });

    }

    Callback<ResponseBody> triggerSMSResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call,
                retrofit2.Response<ResponseBody> response) {
            removeProgressDialog();
            Log.d("SUCCESS", "" + response);
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            if (response.isSuccessful()) {
                try {
                    String resData = new String(response.body().bytes());
                    JSONObject jObject = new JSONObject(resData);
                    String sms_token = jObject.getJSONObject("data").getJSONObject("result").getString("sms_token");
//                    launchVerifySMSFragment(sms_token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            FirebaseCrashlytics.getInstance().recordException(t);
            apiExceptions(t);
//            showToast(getString(R.string.went_wrong));
        }
    };


    private void checkUserHandleAvailability() {
        BaseApplication.getInstance().getRetrofit().create(RewardsAPI.class)
                .checkUserHandleAvailability(userHandleEditView.getText().toString())
                .subscribeOn(
                        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<BaseResponseGeneric<UserHandleAvailabilityResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseResponseGeneric<UserHandleAvailabilityResponse> response) {
                        removeProgressDialog();
                        if (response.getCode() == 200 && Constants.SUCCESS.equals(response.getStatus())) {
                            userAvailabilityResultTextView.setVisibility(View.VISIBLE);
                            if (StringUtils.isNullOrEmpty(response.getData().getResult().getUserId())) {
                                userAvailabilityResultTextView.setText("User handle available");
                                userAvailabilityResultTextView
                                        .setTextColor(getResources().getColor(R.color.green_dark));
                                isHandleChecked = true;
                            } else {
                                isHandleChecked = false;
                                userAvailabilityResultTextView.setText("Sorry, this handle is not available.");
                                userAvailabilityResultTextView.setTextColor(getResources().getColor(R.color.app_red));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void getHandleSuggestion() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        UserHandleSuggestionAPI userHandleSuggestionAPI = retrofit.create(UserHandleSuggestionAPI.class);
        Call<UserHandleSuggestionResponse> call = userHandleSuggestionAPI
                .getSuggestion(fName, lName);
        call.enqueue(getHandleSuggestionResponseReceived);
    }


    Callback<UserHandleSuggestionResponse> getHandleSuggestionResponseReceived = new Callback<UserHandleSuggestionResponse>() {
        @Override
        public void onResponse(Call<UserHandleSuggestionResponse> call,
                retrofit2.Response<UserHandleSuggestionResponse> response) {
            removeProgressDialog();
            Log.d("SUCCESS", "" + response);
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            UserHandleSuggestionResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                suggestion_1.setText(responseData.getData().getResult().suggestionData.get(0));
                suggestion_2.setText(responseData.getData().getResult().suggestionData.get(1));
            }
        }

        @Override
        public void onFailure(Call<UserHandleSuggestionResponse> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            FirebaseCrashlytics.getInstance().recordException(t);
            apiExceptions(t);
//            showToast(getString(R.string.went_wrong));
        }
    };


    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);//64206,0  -1
        if (_requestCode == VERIFY_NUMBER_ACCOUNTKIT_REQUEST_CODE) {
            if (_data != null && _resultCode == Activity.RESULT_OK) {
                accountKitAuthCode = _data.getStringExtra("auth_token");
                phoneEditView.setText(_data.getStringExtra("phone"));
//                mobile = _data.getStringExtra("phone");
                if (isHandleChecked) {
                    if (prepareDataForPosting()) {
                        postData();
                    }
                } else {
                    Toast.makeText(
                            this,
                            "Please check handle availability",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }

    }

    private void postData() {
        String userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        if (!StringUtils.isNullOrEmpty(userId)) {
            showProgressDialog(getResources().getString(R.string.please_wait));
            BaseApplication.getInstance().getRetrofit().create(RewardsAPI.class)
                    .sendUserHandleFlowData(userId, userDetailResult)
                    .subscribeOn(
                            Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Observer<RewardsPersonalResponse>() {

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(RewardsPersonalResponse response) {
                            if (response.getCode() == 200) {
                                if (Constants.SUCCESS.equals(response.getStatus())) {
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else if (Constants.FAILURE.equals(response.getStatus())) {
                                    Toast.makeText(UpdateUserHandleActivity.this, response.getReason(),
                                            Toast.LENGTH_LONG).show();
                                    if (response.getReason().equals("Mobile Number is already registered with us")) {
                                        phoneEditView.setText("");
                                        accountKitAuthCode = null;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            removeProgressDialog();
                            Log.e("exception in error", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            removeProgressDialog();
                        }

                    });
        }
    }

    private boolean prepareDataForPosting() {
        userDetailResult = new UserHandleResult();
        if (!StringUtils.isNullOrEmpty(accountKitAuthCode) && !StringUtils.isNullOrEmpty(accountKitAuthCode.trim())) {
            userDetailResult.setMobileToken(accountKitAuthCode);
        }
        userDetailResult.setUserHandle(userHandleEditView.getText().toString());

        if (loginMode.equals("phone") || loginMode.equals("custom") || loginMode.equals("mobile number")) {
            if (StringUtils.isNullOrEmpty(fNameEditView.getText().toString())) {
                Toast.makeText(this, "First name cannot be blank", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                userDetailResult.setFirstName(fNameEditView.getText().toString());
            }
            if (StringUtils.isNullOrEmpty(lNameEditView.getText().toString())) {
                Toast.makeText(this, "Last name cannot be blank", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                userDetailResult.setLastName(lNameEditView.getText().toString());
            }
        }
        if (StringUtils.isNullOrEmpty(emailEditView.getText().toString())) {
            if (!loginMode.equals("phone")) {
                Toast.makeText(this, "EmailId cannot be blank", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (!StringUtils.isValidEmail(emailEditView.getText().toString())) {
            emailEditView.setFocusableInTouchMode(true);
            emailEditView.setError(getString(R.string.enter_valid_email));
            emailEditView.requestFocus();
            return false;
        } else {
            userDetailResult.setEmail(emailEditView.getText().toString());
        }
        return true;
    }


    @Override
    public void onBackPressed() {

    }
}
