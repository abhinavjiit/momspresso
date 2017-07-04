package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.NotificationController;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.bloggermodel.ParentingBlogResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;

/**
 * Created by manish.soni on 30-07-2015.
 */
public class NotificationFragment extends BaseFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.pushOpenScreenEvent(getActivity(), "Notification Settings", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        View view = inflater.inflate(R.layout.aa_notification, container, false);
        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

        }
    }

}
