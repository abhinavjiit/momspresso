package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.MissingPlaceActivity;
import com.mycity4kids.ui.activity.ReportAnErrorActivity;

/**
 * Created by manish.soni on 31-07-2015.
 */
public class SendFeedbackFragment extends BaseFragment implements View.OnClickListener {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.pushOpenScreenEvent(getActivity(), "Feedback", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        View view = inflater.inflate(R.layout.aa_feedback, null, false);

        view.findViewById(R.id.missing_place).setOnClickListener(this);
        view.findViewById(R.id.report_incorrect).setOnClickListener(this);
        view.findViewById(R.id.feed_back).setOnClickListener(this);


        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.missing_place:
                intent = new Intent(getActivity(), MissingPlaceActivity.class);
                startActivity(intent);

                break;

            case R.id.report_incorrect:
                intent = new Intent(getActivity(), ReportAnErrorActivity.class);
                startActivity(intent);

                break;

            case R.id.feed_back:

                Intent intentEmail = new Intent(Intent.ACTION_SEND);
                String[] recipients = {"feedback@momspresso.com"};
                intentEmail.putExtra(Intent.EXTRA_EMAIL, recipients);
                intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Momspresso mobile app");
                //intentEmail.putExtra(Intent.EXTRA_TEXT,"I just downloaded the amazing mycity4kids mobile app. Check it out @: http://www.mycity4kids.com/mobile ");
                //intentEmail.putExtra(Intent.EXTRA_CC,"ghi");
                intentEmail.setType("text/html");
                startActivity(Intent.createChooser(intentEmail, "Send mail").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                break;

        }

    }
}
