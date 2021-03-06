package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ForgotPasswordActivity;
import com.mycity4kids.ui.login.LoginActivity;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.CustomFontEditText;
import com.mycity4kids.widget.CustomFontTextView;

/**
 * Created by hemant on 5/6/17.
 */
public class EmailLoginFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private CustomFontEditText mEmailId, mPassword;
    private CustomFontTextView loginEmailTextView;
    private CustomFontTextView forgotPasswordTextView;
    private CustomFontTextView signupTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.email_login_fragment, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "EmailLoginScreen",
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "");
        mEmailId = view.findViewById(R.id.emailEditText);
        mPassword = view.findViewById(R.id.passwordEditText);
        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView);
        loginEmailTextView = view.findViewById(R.id.loginEmailTextView);
        signupTextView = view.findViewById(R.id.signupTextView);

        if (BuildConfig.DEBUG) {
            mEmailId.setText("nananaa@gmail.com");
        }
        mEmailId.addTextChangedListener(mTextWatcher);
        mPassword.addTextChangedListener(mTextWatcher);
        mPassword.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId
                    == EditorInfo.IME_ACTION_DONE)) {
                loginWithEmail();
            }
            return false;
        });

        loginEmailTextView.setEnabled(false);
        loginEmailTextView.setOnClickListener(this);
        forgotPasswordTextView.setOnClickListener(this);
        signupTextView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signupTextView:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                break;
            case R.id.loginEmailTextView:
                Utils.pushGenericEvent(getActivity(), "Login_email_click_event", "NA", "EmailLoginFragment");
                loginWithEmail();
                break;
            case R.id.forgotPasswordTextView:
                Utils.pushGenericEvent(getActivity(), "Launch_forgot_password_event", "NA", "EmailLoginFragment");
                Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            default:
                break;

        }
    }

    private void loginWithEmail() {
        if (isDataValid()) {
            String emailId = mEmailId.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            if (BuildConfig.DEBUG && emailId.equals("nananaa@gmail.com")) {
                password = "rikkichu";
            }
            ((LoginActivity) getActivity()).loginRequest(emailId, password);
        }
    }

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
            loginEmailTextView.setEnabled(false);
        } else {
            loginEmailTextView.setEnabled(true);
        }
    }

    private boolean isDataValid() {
        boolean isLoginOk = true;
        String email_id = mEmailId.getText().toString().trim();
        if (email_id.trim().length() == 0 || ((!StringUtils.isValidEmail(email_id)) && (!StringUtils
                .checkMobileNumber(email_id)))) {
            mEmailId.setFocusableInTouchMode(true);
            mEmailId.setError(getString(R.string.enter_valid_email));
            mEmailId.requestFocus();
            isLoginOk = false;
        } else if (mPassword.getText().toString().length() == 0) {
            mPassword.setFocusableInTouchMode(true);
            mPassword.requestFocus();
            mPassword.setError("Password can't be left blank");
            isLoginOk = false;
        } else if (mPassword.getText().toString().length() < 1) {
            mPassword.setFocusableInTouchMode(true);
            mPassword.requestFocus();
            mPassword.setError("Password should not less than 5 character.");
            isLoginOk = false;
        }
        return isLoginOk;
    }
}
