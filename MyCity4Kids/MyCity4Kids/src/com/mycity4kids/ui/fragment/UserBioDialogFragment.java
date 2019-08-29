package com.mycity4kids.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by user on 08-06-2015.
 */
public class UserBioDialogFragment extends DialogFragment {

    private String userBio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.user_bio_dialog, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Utils.pushOpenScreenEvent(getActivity(), "PickVideoScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        Bundle extras = getArguments();
        if (extras != null) {
            userBio = extras.getString("userBio");
        }

        TextView userbioTextView = (TextView) rootView.findViewById(R.id.userBioTextView);
        userbioTextView.setText(userBio);
        return rootView;
    }
}