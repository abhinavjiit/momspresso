package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.utils.PermissionUtil;

import java.util.ArrayList;

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