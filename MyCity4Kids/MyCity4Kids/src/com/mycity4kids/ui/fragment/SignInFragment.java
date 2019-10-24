package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ActivityLogin;
import com.mycity4kids.ui.activity.CustomSignUpActivity;
import com.mycity4kids.widget.CustomFontTextView;

/**
 * Created by hemant on 5/6/17.
 */
public class SignInFragment extends BaseFragment implements View.OnClickListener {

    private int count = 0;

    private View view;
    private RelativeLayout lnrRoot;
    private LayoutInflater mInflator;
    private CustomFontTextView facebookLoginTextView;
    private CustomFontTextView googleLoginTextView;
    private CustomFontTextView loginEmailTextView;
    private CustomFontTextView signupTextView;
    private CustomFontTextView phoneLoginTextView;
    private CustomFontTextView welcomeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signin_fragment, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "SignInScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        lnrRoot = (RelativeLayout) view.findViewById(R.id.rootLayout);
        welcomeTextView = (CustomFontTextView) view.findViewById(R.id.welcomeTextView);
        facebookLoginTextView = (CustomFontTextView) view.findViewById(R.id.connect_facebook);
        googleLoginTextView = (CustomFontTextView) view.findViewById(R.id.connect_googleplus);
        loginEmailTextView = (CustomFontTextView) view.findViewById(R.id.loginEmailTextView);
        phoneLoginTextView = (CustomFontTextView) view.findViewById(R.id.connect_phone);
        signupTextView = (CustomFontTextView) view.findViewById(R.id.signupTextView);

        facebookLoginTextView.setOnClickListener(this);
        googleLoginTextView.setOnClickListener(this);
        loginEmailTextView.setOnClickListener(this);
        phoneLoginTextView.setOnClickListener(this);
        signupTextView.setOnClickListener(this);
        welcomeTextView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcomeTextView:
                if (count == 7) {
                    count = 0;
                    Intent signUpSIntent = new Intent(getActivity(), CustomSignUpActivity.class);
                    startActivity(signUpSIntent);
                } else {
                    count++;
                }
                break;
            case R.id.connect_phone:
                Utils.pushGenericEvent(getActivity(), "SignIn_phone_click_event", "NA", "SignInFragment");
                ((ActivityLogin) getActivity()).fbAccountKitVerification();
                break;
            case R.id.connect_facebook:
                Utils.pushGenericEvent(getActivity(), "SignIn_facebook_click_event", "NA", "SignInFragment");
                ((ActivityLogin) getActivity()).loginWithFacebook();
                break;
            case R.id.connect_googleplus:
                Utils.pushGenericEvent(getActivity(), "SignIn_google_click_event", "NA", "SignInFragment");
                ((ActivityLogin) getActivity()).loginWithGplus();
                break;
            case R.id.loginEmailTextView:
                Utils.pushGenericEvent(getActivity(), "Launch_email_sign_in_event", "NA", "SignInFragment");
                EmailLoginFragment emailLoginFragment = new EmailLoginFragment();
                Bundle mBundle = new Bundle();
                emailLoginFragment.setArguments(mBundle);
                ((ActivityLogin) getActivity()).replaceFragmentWithAnimation(emailLoginFragment, mBundle, true);
                break;
            case R.id.signupTextView:
                Utils.pushGenericEvent(getActivity(), "Launch_sign_up_from_sign_in_event", "NA", "SignInFragment");
                SignUpFragment signUpFragment = new SignUpFragment();
                Bundle bundle = new Bundle();
                signUpFragment.setArguments(bundle);
                ((ActivityLogin) getActivity()).replaceFragmentWithAnimation(signUpFragment, bundle, true);
                break;
            default:
                break;

        }
    }

    @Override
    protected void updateUi(Response response) {

    }
}
