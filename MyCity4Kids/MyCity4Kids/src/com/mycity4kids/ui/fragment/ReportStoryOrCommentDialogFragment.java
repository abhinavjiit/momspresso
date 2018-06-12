package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.ReportStoryOrCommentRequest;
import com.mycity4kids.models.response.ReportStoryOrCommentResponse;
import com.mycity4kids.models.response.ShortStoryCommentListResponse;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReportStoryOrCommentDialogFragment extends DialogFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.report_post_dialog_fragment, container,
                false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getDialog().setCanceledOnTouchOutside(false);
        String postId = getArguments().getString("postId");
        String type = getArguments().getString("type");

        RadioGroup reportReasonRadioGroup = (RadioGroup) rootView.findViewById(R.id.reportReasonRadioGroup);
        final AppCompatRadioButton reason1RadioButton = (AppCompatRadioButton) rootView.findViewById(R.id.reason1RadioButton);
        final AppCompatRadioButton reason2RadioButton = (AppCompatRadioButton) rootView.findViewById(R.id.reason2RadioButton);
        final AppCompatRadioButton reason3RadioButton = (AppCompatRadioButton) rootView.findViewById(R.id.reason3RadioButton);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        final ShortStoryAPI shortStoryAPI = retrofit.create(ShortStoryAPI.class);
        final ReportStoryOrCommentRequest reportStoryOrCommentRequest = new ReportStoryOrCommentRequest();
        reportStoryOrCommentRequest.setId(postId);
        reportStoryOrCommentRequest.setType(type);
        reportReasonRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (reason1RadioButton.isChecked()) {
                    Log.d("RadioGroup", "option1");
                    reportStoryOrCommentRequest.setReason(reason1RadioButton.getText().toString());
                    Call<ReportStoryOrCommentResponse> call = shortStoryAPI.reportStoryOrComment(reportStoryOrCommentRequest);
                    call.enqueue(reportCallback);
                }
                if (reason2RadioButton.isChecked()) {
                    Log.d("RadioGroup", "option2");
                    reportStoryOrCommentRequest.setReason(reason2RadioButton.getText().toString());
                    Call<ReportStoryOrCommentResponse> call = shortStoryAPI.reportStoryOrComment(reportStoryOrCommentRequest);
                    call.enqueue(reportCallback);
                }
                if (reason3RadioButton.isChecked()) {
                    Log.d("RadioGroup", "option3");
                    reportStoryOrCommentRequest.setReason(reason3RadioButton.getText().toString());
                    Call<ReportStoryOrCommentResponse> call = shortStoryAPI.reportStoryOrComment(reportStoryOrCommentRequest);
                    call.enqueue(reportCallback);
                }
            }
        });
        return rootView;
    }

    private Callback<ReportStoryOrCommentResponse> reportCallback = new Callback<ReportStoryOrCommentResponse>() {
        @Override
        public void onResponse(Call<ReportStoryOrCommentResponse> call, Response<ReportStoryOrCommentResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    Toast.makeText(getActivity(), "Failed to report. Please try again", Toast.LENGTH_SHORT).show();

                dismiss();
                return;
            }
            try {
                ReportStoryOrCommentResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    dismiss();
                } else {
                    if (isAdded())
                        Toast.makeText(getActivity(), "Failed to report. Please try again", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            } catch (Exception e) {
                if (isAdded())
                    Toast.makeText(getActivity(), "Failed to report. Please try again", Toast.LENGTH_SHORT).show();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                dismiss();
            }
        }

        @Override
        public void onFailure(Call<ReportStoryOrCommentResponse> call, Throwable t) {
            if (isAdded())
                Toast.makeText(getActivity(), "Failed to report. Please try again", Toast.LENGTH_SHORT).show();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            dismiss();
        }
    };

    @Override
    public void onClick(View view) {

    }

}