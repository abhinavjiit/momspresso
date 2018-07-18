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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.request.GroupReportContentRequest;
import com.mycity4kids.models.response.GroupsReportContentResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class GroupPostReportDialogFragment extends DialogFragment implements OnClickListener {

    private int groupId, postId, responseId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.report_post_dialog_fragment, container,
                false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getDialog().setCanceledOnTouchOutside(false);

        groupId = getArguments().getInt("groupId");
        postId = getArguments().getInt("postId");
        responseId = getArguments().getInt("responseId", -1);

        RadioGroup reportReasonRadioGroup = (RadioGroup) rootView.findViewById(R.id.reportReasonRadioGroup);
        final AppCompatRadioButton reason1RadioButton = (AppCompatRadioButton) rootView.findViewById(R.id.reason1RadioButton);
        final AppCompatRadioButton reason2RadioButton = (AppCompatRadioButton) rootView.findViewById(R.id.reason2RadioButton);
        final AppCompatRadioButton reason3RadioButton = (AppCompatRadioButton) rootView.findViewById(R.id.reason3RadioButton);
//        final AppCompatRadioButton reason4RadioButton = (AppCompatRadioButton) rootView.findViewById(R.id.reason4RadioButton);

        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        final GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        final GroupReportContentRequest groupReportContentRequest = new GroupReportContentRequest();
        groupReportContentRequest.setGroupId(groupId);
        groupReportContentRequest.setPostId(postId);
        if (responseId != -1) {
            groupReportContentRequest.setResponseId(responseId);
        }

        groupReportContentRequest.setReportedBy(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());

        reportReasonRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (reason1RadioButton.isChecked()) {
                    Log.d("RadioGroup", "option1");
                    groupReportContentRequest.setReason("0");
                }
                if (reason2RadioButton.isChecked()) {
                    Log.d("RadioGroup", "option1");
                    groupReportContentRequest.setReason("1");
                }
                if (reason3RadioButton.isChecked()) {
                    Log.d("RadioGroup", "option1");
                    groupReportContentRequest.setReason("2");
                }
                Call<GroupsReportContentResponse> call = groupsAPI.reportContent(groupReportContentRequest);
                call.enqueue(repostContentResponseCallback);
            }
        });
        return rootView;
    }

    public Callback<GroupsReportContentResponse> repostContentResponseCallback = new Callback<GroupsReportContentResponse>() {
        @Override
        public void onResponse(Call<GroupsReportContentResponse> call, Response<GroupsReportContentResponse> response) {
            dismiss();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupsReportContentResponse> call, Throwable t) {
            dismiss();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onClick(View view) {

    }

}