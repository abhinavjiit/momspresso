package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.ui.activity.ActivityLogin;
import com.mycity4kids.ui.activity.SplashActivity;
import com.mycity4kids.ui.activity.TutorialActivity;

/**
 * Created by manish.soni on 08-09-2015.
 */
public class FacebookAddEmailDialogFragment extends android.app.DialogFragment implements View.OnClickListener {

    TextView ok, cancel;
    EditText emailEditText;

    String fromActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.verify_email_dialog_fragment, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        fromActivity = getArguments().getString(AppConstants.FROM_ACTIVITY);
        ok = (TextView) rootView.findViewById(R.id.ok);
        cancel = (TextView) rootView.findViewById(R.id.cancel);
        emailEditText = (EditText) rootView.findViewById(R.id.emailTextView);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ok:
                if (StringUtils.isNullOrEmpty((emailEditText.getText().toString()))) {
                    ToastUtils.showToast(getActivity(), "Please enter your email");
                } else {
                    if (!isValidEmail()) {
                        return;
                    }
                    if (AppConstants.ACTIVITY_LOGIN.equals(fromActivity)) {
                        ((ActivityLogin) getActivity()).addEmail(emailEditText.getText().toString());
                    }else if(AppConstants.ACTIVITY_TUTORIAL.equals(fromActivity)){
                        ((TutorialActivity) getActivity()).addEmail(emailEditText.getText().toString());
                    }
//                    getDialog().dismiss();
                }
                break;
            case R.id.cancel:
                if (AppConstants.ACTIVITY_LOGIN.equals(fromActivity)) {
                    ((ActivityLogin) getActivity()).cancelAddEmail();
                }else if(AppConstants.ACTIVITY_TUTORIAL.equals(fromActivity)){
                    ((TutorialActivity) getActivity()).cancelAddEmail();
                }
                break;
        }
    }

    private boolean isValidEmail() {
        boolean isLoginOk = true;
        String email_id = emailEditText.getText().toString().trim();

        if (email_id.trim().length() == 0 || ((!StringUtils.isValidEmail(email_id)))) {
            emailEditText.setFocusableInTouchMode(true);
            emailEditText.setError("Please enter valid email id");
            emailEditText.requestFocus();
            isLoginOk = false;
        }
        return isLoginOk;
    }
}