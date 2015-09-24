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
import com.mycity4kids.ui.activity.ActivityLogin;

/**
 * Created by manish.soni on 08-09-2015.
 */
public class PasswordDialogFragment extends android.app.DialogFragment implements View.OnClickListener {

    TextView ok, cancel;
    EditText mPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_get_password, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ok = (TextView) rootView.findViewById(R.id.ok);
        cancel = (TextView) rootView.findViewById(R.id.cancel);
        mPassword = (EditText) rootView.findViewById(R.id.m_password);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ok:

                if (StringUtils.isNullOrEmpty((mPassword.getText().toString()))) {

                    ToastUtils.showToast(getActivity(),"Please enter password");

                } else {
                    ((ActivityLogin) getActivity()).addPassword(mPassword.getText().toString());
                    getDialog().dismiss();
                }
                break;
            case R.id.cancel:

                getDialog().dismiss();
                break;
        }
    }
}