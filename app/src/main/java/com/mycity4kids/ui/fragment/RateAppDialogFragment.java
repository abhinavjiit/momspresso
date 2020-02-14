package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.version.RateVersion;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by manish.soni on 31-07-2015.
 */
public class RateAppDialogFragment extends android.app.DialogFragment implements View.OnClickListener {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_rate_app, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        view.findViewById(R.id.not_now).setOnClickListener(this);
        view.findViewById(R.id.rate_now).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        RateVersion reteVersionModel = SharedPrefUtils.getRateVersion(BaseApplication.getAppContext());
        int currentRateVersion = reteVersionModel.getAppRateVersion();
        currentRateVersion++;
        boolean isCompleteRateProcess = reteVersionModel.isAppRateComplete();
        RateVersion rateModel = new RateVersion();
        rateModel.setAppRateComplete(isCompleteRateProcess);
        rateModel.setAppRateVersion(currentRateVersion);
        switch (view.getId()) {
            case R.id.not_now:
                rateModel.setAppRateComplete(false);
                rateModel.setAppRateVersion(-7);
                SharedPrefUtils.setAppRateVersion(BaseApplication.getAppContext(), rateModel);
                getDialog().dismiss();
                Utils.pushEvent(getActivity(), GTMEventType.NOT_RATE_EVENT_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "", "");

                break;
            case R.id.rate_now:
                rateModel.setAppRateComplete(true);
                rateModel.setAppRateVersion(0);
                getDialog().dismiss();
                SharedPrefUtils.setAppRateVersion(BaseApplication.getAppContext(), rateModel);
                Utils.pushEvent(getActivity(), GTMEventType.RATE_APP_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "", "");

                /**
                 * a try/catch block here because an Exception will be thrown if the Play Store is not installed on the target device.
                 */
                String appPackage = getActivity().getPackageName();
                try {
                    Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
                    startActivity(rateIntent);
                } catch (Exception e) {
                    Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
                    startActivity(rateIntent);
                }
                break;

        }

    }
}
