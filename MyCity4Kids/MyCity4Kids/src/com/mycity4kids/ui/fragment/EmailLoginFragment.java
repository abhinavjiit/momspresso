package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ActivityLogin;
import com.mycity4kids.ui.activity.ForgotPasswordActivity;
import com.mycity4kids.widget.CustomFontEditText;
import com.mycity4kids.widget.CustomFontTextView;

/**
 * Created by hemant on 5/6/17.
 */
public class EmailLoginFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private LayoutInflater mInflator;
    private CustomFontEditText mEmailId, mPassword;
    private CustomFontTextView loginEmailTextView;
    private CustomFontTextView forgotPasswordTextView;
    private CustomFontTextView signupTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.email_login_fragment, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "EmailLoginScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mEmailId = (CustomFontEditText) view.findViewById(R.id.emailEditText);
        mPassword = (CustomFontEditText) view.findViewById(R.id.passwordEditText);
        forgotPasswordTextView = (CustomFontTextView) view.findViewById(R.id.forgotPasswordTextView);
        loginEmailTextView = (CustomFontTextView) view.findViewById(R.id.loginEmailTextView);
        signupTextView = (CustomFontTextView) view.findViewById(R.id.signupTextView);

        if (BuildConfig.DEBUG) {
            mEmailId.setText("android@mc4k.com");
        }
        mEmailId.addTextChangedListener(mTextWatcher);
        mPassword.addTextChangedListener(mTextWatcher);
        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    loginWithEmail();
                }
                return false;
            }
        });

        loginEmailTextView.setEnabled(false);
        loginEmailTextView.setOnClickListener(this);
        forgotPasswordTextView.setOnClickListener(this);
        signupTextView.setOnClickListener(this);
        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginEmailTextView:
                loginWithEmail();
                break;
            case R.id.forgotPasswordTextView:
                Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.signupTextView:
                SignUpFragment signUpFragment = new SignUpFragment();
                Bundle bundle = new Bundle();
                signUpFragment.setArguments(bundle);
                ((ActivityLogin) getActivity()).replaceFragmentWithAnimation(signUpFragment, bundle, true);
                break;
            default:
                break;

        }
    }

    private void loginWithEmail() {
        if (isDataValid()) {
            String emailId = mEmailId.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            if (BuildConfig.DEBUG && emailId.equals("android@mc4k.com")) {
                password = "android";
            }
            ((ActivityLogin) getActivity()).loginRequest(emailId, password);
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

        if (email_id.trim().length() == 0 || ((!StringUtils.isValidEmail(email_id)) && (!StringUtils.checkMobileNumber(email_id)))) {
            mEmailId.setFocusableInTouchMode(true);
            mEmailId.setError(getString(R.string.enter_valid_email));
            mEmailId.requestFocus();
            isLoginOk = false;
        } else if (mPassword.getText().toString().length() == 0) {
            mPassword.setFocusableInTouchMode(true);
            mPassword.requestFocus();
            mPassword.setError("Password can't be left blank");
            //mPassword.requestFocus();
            isLoginOk = false;
        } else if (mPassword.getText().toString().length() < 1) {
            mPassword.setFocusableInTouchMode(true);
            mPassword.requestFocus();

            mPassword.setError("Password should not less than 5 character.");
            //mPassword.requestFocus();
            isLoginOk = false;
        }
        return isLoginOk;
    }
}
