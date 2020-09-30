package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.ui.login.LoginActivity;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;

/**
 * Created by manish.soni on 08-09-2015.
 */
public class FacebookAddEmailDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextView ok;
    private TextView cancel;
    private EditText emailEditText;
    private String fromActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.verify_email_dialog_fragment, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        fromActivity = getArguments().getString(AppConstants.FROM_ACTIVITY);
        ok = rootView.findViewById(R.id.ok);
        cancel = rootView.findViewById(R.id.cancel);
        emailEditText = rootView.findViewById(R.id.emailTextView);
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
                    if (getActivity() instanceof LoginActivity) {
                        ((LoginActivity) getActivity()).addEmail(emailEditText.getText().toString());
                    }
                }
                break;
            case R.id.cancel:
                if (getActivity() instanceof LoginActivity) {
                    ((LoginActivity) getActivity()).cancelAddEmail();
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
