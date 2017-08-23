package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;

/**
 * Created by manish.soni on 30-07-2015.
 */
public class SyncSettingFragment extends BaseFragment implements View.OnClickListener {

    ImageView automatic, onlyWifi;
    private boolean isCommingFromSetting = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.pushOpenScreenEvent(getActivity(), "Sync Settings", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        View view = inflater.inflate(R.layout.aa_sync_settings, container, false);

        if (getArguments() != null) {
            isCommingFromSetting = getArguments().getBoolean(Constants.IS_COMMING_FROM_SETTING);
            if (isCommingFromSetting) {
                getActivity().setTitle("Sync Settings");
            } else {
            }
        }

        automatic = (ImageView) view.findViewById(R.id.automatic);
        onlyWifi = (ImageView) view.findViewById(R.id.only_wifi);
        automatic.setOnClickListener(this);
        automatic.setTag(1);
        automatic.setImageResource(R.drawable.checked_sel);
        onlyWifi.setOnClickListener(this);
        onlyWifi.setTag(0);
        onlyWifi.setImageResource(R.drawable.checked_unsel);

        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.automatic:

                if (automatic.getTag().equals(1)) {
                    automatic.setTag(0);
                    automatic.setImageResource(R.drawable.checked_unsel);
                    onlyWifi.setTag(1);
                    onlyWifi.setImageResource(R.drawable.checked_sel);
                } else {
                    automatic.setTag(1);
                    automatic.setImageResource(R.drawable.checked_sel);
                    onlyWifi.setTag(0);
                    onlyWifi.setImageResource(R.drawable.checked_unsel);
                }

                break;
            case R.id.only_wifi:

                if (onlyWifi.getTag().equals(0)) {
                    onlyWifi.setTag(1);
                    onlyWifi.setImageResource(R.drawable.checked_sel);
                    automatic.setTag(0);
                    automatic.setImageResource(R.drawable.checked_unsel);
                } else {
                    onlyWifi.setTag(0);
                    onlyWifi.setImageResource(R.drawable.checked_unsel);
                    automatic.setTag(1);
                    automatic.setImageResource(R.drawable.checked_sel);
                }

                break;

        }

    }
}