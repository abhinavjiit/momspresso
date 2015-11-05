package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.mycity4kids.R;
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

        ((TextView) view.findViewById(R.id.not_now)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.rate_now)).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        RateVersion reteVersionModel = SharedPrefUtils.getRateVersion(getActivity());
        int currentRateVersion = reteVersionModel.getAppRateVersion();
        currentRateVersion++;
        boolean isCompleteRateProcess = reteVersionModel.isAppRateComplete();
        RateVersion rateModel = new RateVersion();
        rateModel.setAppRateComplete(isCompleteRateProcess);
        rateModel.setAppRateVersion(currentRateVersion);
        switch (view.getId()) {

            //                    case RATE_ME_OR_INSTALL:
//                        rateModel.setAppRateComplete(true);
//                        rateModel.setAppRateVersion(0);
//                        SharedPrefUtils.setAppRateVersion(this, rateModel);
//                        /**
//                         * a try/catch block here because an Exception will be thrown if the Play Store is not installed on the target device.
//                         */
//                        String appPackage = getPackageName();
//                        try {
//                            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
//                            startActivity(rateIntent);
//                        } catch (Exception e) {
//                            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
//                            startActivity(rateIntent);
//                        }
//                        break;
//
//                    case LATER:
//                        rateModel.setAppRateComplete(false);
//                        rateModel.setAppRateVersion(0);
//                        SharedPrefUtils.setAppRateVersion(this, rateModel);
//                        break;

            case R.id.not_now:
                rateModel.setAppRateComplete(false);
                rateModel.setAppRateVersion(-7);
                SharedPrefUtils.setAppRateVersion(getActivity(), rateModel);
                getDialog().dismiss();

                break;
            case R.id.rate_now:
                rateModel.setAppRateComplete(true);
                rateModel.setAppRateVersion(0);
                getDialog().dismiss();
                SharedPrefUtils.setAppRateVersion(getActivity(), rateModel);
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
