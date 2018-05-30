package com.mycity4kids.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by user on 08-06-2015.
 */
public class ChooseAnonymousDialogFragment extends DialogFragment implements OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.choose_anonymous_option_dialog, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Utils.pushOpenScreenEvent(getActivity(), "AnonymousDialogScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        ImageView closeDialogImageView = (ImageView) rootView.findViewById(R.id.closeDialogImageView);
        TextView okTextView = (TextView) rootView.findViewById(R.id.okTextView);
        TextView cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);

        closeDialogImageView.setOnClickListener(this);
        okTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeDialogImageView:
            case R.id.cancelTextView:
                dismiss();
                break;
            case R.id.okTextView:
                SharedPrefUtils.setUserAnonymous(getActivity(), true);
                dismiss();
                break;
        }
    }

}