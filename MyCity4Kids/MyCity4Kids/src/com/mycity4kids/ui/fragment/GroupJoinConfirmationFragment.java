package com.mycity4kids.ui.fragment;

import android.content.Intent;
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
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.utils.LocaleManager;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by user on 08-06-2015.
 */
public class GroupJoinConfirmationFragment extends DialogFragment implements OnClickListener {

    private GroupResult selectedGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.group_join_confirmation_fragment, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectedGroup = getArguments().getParcelable("groupItem");

        TextView okayTextView = (TextView) rootView.findViewById(R.id.okayTextView);
        ImageView closeImageView = (ImageView) rootView.findViewById(R.id.closeImageView);

        okayTextView.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.okayTextView:
            case R.id.closeImageView:
                if (isAdded())
                    getActivity().finish();
                break;
        }
    }

}