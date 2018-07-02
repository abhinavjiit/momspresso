package com.mycity4kids.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mycity4kids.R;

/**
 * Created by user on 08-06-2015.
 */
public class GroupPostReportDialogFragment extends DialogFragment implements OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.report_post_dialog_fragment, container,
                false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getDialog().setCanceledOnTouchOutside(false);

        RadioGroup reportReasonRadioGroup = (RadioGroup) rootView.findViewById(R.id.reportReasonRadioGroup);
        final AppCompatRadioButton reason1RadioButton = (AppCompatRadioButton) rootView.findViewById(R.id.reason1RadioButton);
        final AppCompatRadioButton reason2RadioButton = (AppCompatRadioButton) rootView.findViewById(R.id.reason2RadioButton);
        final AppCompatRadioButton reason3RadioButton = (AppCompatRadioButton) rootView.findViewById(R.id.reason3RadioButton);
//        final AppCompatRadioButton reason4RadioButton = (AppCompatRadioButton) rootView.findViewById(R.id.reason4RadioButton);

        reportReasonRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (reason1RadioButton.isChecked()) {
                    Log.d("RadioGroup", "option1");
                }
                if (reason2RadioButton.isChecked()) {
                    Log.d("RadioGroup", "option1");
                }
                if (reason3RadioButton.isChecked()) {
                    Log.d("RadioGroup", "option1");
                }
//                if (reason4RadioButton.isChecked()) {
//                    Log.d("RadioGroup", "option1");
//                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 1000);
            }
        });
        return rootView;
    }

    @Override
    public void onClick(View view) {

    }

}