package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by hemant on 24/5/17.
 */
public class UserProfileFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_INIT_PERMISSION = 1;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};

    private LayoutInflater mInflator;
    private View view;

    private String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_mc4k_home, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Dashboard Fragment", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();

        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {

    }
}