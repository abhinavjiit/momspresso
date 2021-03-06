package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.ReportStoryOrCommentRequest;
import com.mycity4kids.models.response.ReportStoryOrCommentResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReportContentDialogFragment extends DialogFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.report_post_dialog_fragment, container,
                false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final String postId = getArguments().getString("postId");
        final int type = getArguments().getInt("type");

        RadioGroup reportReasonRadioGroup = rootView.findViewById(R.id.reportReasonRadioGroup);
        final AppCompatRadioButton reason1RadioButton = rootView.findViewById(R.id.reason1RadioButton);
        final AppCompatRadioButton reason2RadioButton = rootView.findViewById(R.id.reason2RadioButton);
        final AppCompatRadioButton reason3RadioButton = rootView.findViewById(R.id.reason3RadioButton);
        final AppCompatRadioButton reason4RadioButton = rootView.findViewById(R.id.reason4RadioButton);
        final AppCompatRadioButton reason5RadioButton = rootView.findViewById(R.id.reason5RadioButton);
        final AppCompatRadioButton reason6RadioButton = rootView.findViewById(R.id.reason6RadioButton);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        final ShortStoryAPI shortStoryApi = retrofit.create(ShortStoryAPI.class);
        final ReportStoryOrCommentRequest reportStoryOrCommentRequest = new ReportStoryOrCommentRequest();
        reportStoryOrCommentRequest.setId(postId);
        reportStoryOrCommentRequest.setType(type);
        reportReasonRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (reason1RadioButton.isChecked()) {
                reportStoryOrCommentRequest.setReason(reason1RadioButton.getText().toString());
                Call<ReportStoryOrCommentResponse> call = shortStoryApi
                        .reportStoryOrComment(reportStoryOrCommentRequest);
                call.enqueue(reportCallback);
                if (isAdded()) {
                    Utils.pushReportShortStoryEvent(getActivity(), "ReportDialog",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            postId, reason1RadioButton.getText().toString(), "" + type);
                }
            }
            if (reason2RadioButton.isChecked()) {
                reportStoryOrCommentRequest.setReason(reason2RadioButton.getText().toString());
                Call<ReportStoryOrCommentResponse> call = shortStoryApi
                        .reportStoryOrComment(reportStoryOrCommentRequest);
                call.enqueue(reportCallback);
                if (isAdded()) {
                    Utils.pushReportShortStoryEvent(getActivity(), "ReportDialog",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            postId, reason2RadioButton.getText().toString(), "" + type);
                }
            }
            if (reason3RadioButton.isChecked()) {
                reportStoryOrCommentRequest.setReason(reason3RadioButton.getText().toString());
                Call<ReportStoryOrCommentResponse> call = shortStoryApi
                        .reportStoryOrComment(reportStoryOrCommentRequest);
                call.enqueue(reportCallback);
                if (isAdded()) {
                    Utils.pushReportShortStoryEvent(getActivity(), "ReportDialog",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            postId, reason3RadioButton.getText().toString(), "" + type);
                }
            }
            if (reason4RadioButton.isChecked()) {
                reportStoryOrCommentRequest.setReason(reason4RadioButton.getText().toString());
                Call<ReportStoryOrCommentResponse> call = shortStoryApi
                        .reportStoryOrComment(reportStoryOrCommentRequest);
                call.enqueue(reportCallback);
                if (isAdded()) {
                    Utils.pushReportShortStoryEvent(getActivity(), "ReportDialog",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            postId, reason4RadioButton.getText().toString(), "" + type);
                }
            }
            if (reason5RadioButton.isChecked()) {
                reportStoryOrCommentRequest.setReason(reason5RadioButton.getText().toString());
                Call<ReportStoryOrCommentResponse> call = shortStoryApi
                        .reportStoryOrComment(reportStoryOrCommentRequest);
                call.enqueue(reportCallback);
                if (isAdded()) {
                    Utils.pushReportShortStoryEvent(getActivity(), "ReportDialog",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            postId, reason5RadioButton.getText().toString(), "" + type);
                }
            }
            if (reason6RadioButton.isChecked()) {
                reportStoryOrCommentRequest.setReason(reason6RadioButton.getText().toString());
                Call<ReportStoryOrCommentResponse> call = shortStoryApi
                        .reportStoryOrComment(reportStoryOrCommentRequest);
                call.enqueue(reportCallback);
                if (isAdded()) {
                    Utils.pushReportShortStoryEvent(getActivity(), "ReportDialog",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            postId, reason6RadioButton.getText().toString(), "" + type);
                }
            }
        });
        return rootView;
    }

    private Callback<ReportStoryOrCommentResponse> reportCallback = new Callback<ReportStoryOrCommentResponse>() {
        @Override
        public void onResponse(Call<ReportStoryOrCommentResponse> call,
                Response<ReportStoryOrCommentResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    Toast.makeText(getActivity(), "Failed to report. Please try again", Toast.LENGTH_SHORT).show();
                }
                dismiss();
                return;
            }
            try {
                ReportStoryOrCommentResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    dismiss();
                } else {
                    if (isAdded()) {
                        Toast.makeText(getActivity(), "Failed to report. Please try again", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
            } catch (Exception e) {
                if (isAdded()) {
                    Toast.makeText(getActivity(), "Failed to report. Please try again", Toast.LENGTH_SHORT).show();
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                dismiss();
            }
        }

        @Override
        public void onFailure(Call<ReportStoryOrCommentResponse> call, Throwable t) {
            if (isAdded()) {
                Toast.makeText(getActivity(), "Failed to report. Please try again", Toast.LENGTH_SHORT).show();
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            dismiss();
        }
    };

    @Override
    public void onClick(View view) {
    }
}
