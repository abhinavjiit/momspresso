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
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ActivityLogin;
import com.mycity4kids.widget.CustomFontTextView;

/**
 * Created by hemant on 5/6/17.
 */
public class SignInFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private RelativeLayout lnrRoot;
    private LayoutInflater mInflator;
    private CustomFontTextView facebookLoginTextView;
    private CustomFontTextView googleLoginTextView;
    private CustomFontTextView loginEmailTextView;
    private CustomFontTextView signupTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signin_fragment, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "SignInScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        lnrRoot = (RelativeLayout) view.findViewById(R.id.rootLayout);
        facebookLoginTextView = (CustomFontTextView) view.findViewById(R.id.connect_facebook);
        googleLoginTextView = (CustomFontTextView) view.findViewById(R.id.connect_googleplus);
        loginEmailTextView = (CustomFontTextView) view.findViewById(R.id.loginEmailTextView);
        signupTextView = (CustomFontTextView) view.findViewById(R.id.signupTextView);

        facebookLoginTextView.setOnClickListener(this);
        googleLoginTextView.setOnClickListener(this);
        loginEmailTextView.setOnClickListener(this);
        signupTextView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.connect_facebook:
                ((ActivityLogin) getActivity()).loginWithFacebook();
                break;
            case R.id.connect_googleplus:
                ((ActivityLogin) getActivity()).loginWithGplus();
                break;
            case R.id.loginEmailTextView:
                EmailLoginFragment emailLoginFragment = new EmailLoginFragment();
                Bundle mBundle = new Bundle();
                emailLoginFragment.setArguments(mBundle);
                ((ActivityLogin) getActivity()).replaceFragmentWithAnimation(emailLoginFragment, mBundle, true);

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

    @Override
    protected void updateUi(Response response) {

    }
}
