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
public class SignUpFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private RelativeLayout lnrRoot;
    private LayoutInflater mInflator;
    private CustomFontTextView facebookLoginTextView;
    private CustomFontTextView googleLoginTextView;
    private CustomFontTextView phoneLoginTextView;
    private CustomFontTextView signinTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_fragment, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "SignUpScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lnrRoot = (RelativeLayout) view.findViewById(R.id.rootLayout);
        facebookLoginTextView = (CustomFontTextView) view.findViewById(R.id.connect_facebook);
        googleLoginTextView = (CustomFontTextView) view.findViewById(R.id.connect_googleplus);
        phoneLoginTextView = (CustomFontTextView) view.findViewById(R.id.connect_phone);
        signinTextView = (CustomFontTextView) view.findViewById(R.id.signinTextView);

        facebookLoginTextView.setOnClickListener(this);
        googleLoginTextView.setOnClickListener(this);
        phoneLoginTextView.setOnClickListener(this);
        signinTextView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.connect_phone:
                ((ActivityLogin) getActivity()).fbAccountKitVerification();
                break;
            case R.id.connect_facebook:
                ((ActivityLogin) getActivity()).loginWithFacebook();
                break;

            case R.id.connect_googleplus:
                ((ActivityLogin) getActivity()).loginWithGplus();
                break;

            case R.id.signinTextView:
                SignInFragment signInFragment = new SignInFragment();
                Bundle bundle = new Bundle();
                signInFragment.setArguments(bundle);
                ((ActivityLogin) getActivity()).replaceFragmentWithAnimation(signInFragment, bundle, true);
                break;

            default:
                break;

        }
    }

    @Override
    protected void updateUi(Response response) {

    }
}
