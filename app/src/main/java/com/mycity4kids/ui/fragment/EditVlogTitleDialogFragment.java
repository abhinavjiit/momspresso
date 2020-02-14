package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.request.UpdateVlogTitleRequest;
import com.mycity4kids.models.response.VlogsDetailResponse;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditVlogTitleDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText vlogTitleEditView;
    private TextView confirmTextView, cancelTextView;
    private String vlogTitle, videoId;
    private int position;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            vlogTitle = getArguments().getString("vlogTitle");
            videoId = getArguments().getString("videoId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.edit_vlog_title_dialog_fragment, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (isAdded()) {
//            userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        }
        vlogTitleEditView = (EditText) rootView.findViewById(R.id.vlogTitleEditView);
        confirmTextView = (TextView) rootView.findViewById(R.id.confirmTextView);
        cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);

        vlogTitleEditView.setText(vlogTitle);

        confirmTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirmTextView:
                if (vlogTitleEditView.getText() != null && !StringUtils.isNullOrEmpty(vlogTitleEditView.getText().toString().trim())) {
                    updateVlogTitle(vlogTitleEditView.getText().toString());
                }
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
        }
    }

    private void updateVlogTitle(String title) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);

        UpdateVlogTitleRequest vlogTitleRequest = new UpdateVlogTitleRequest();
        vlogTitleRequest.setTitle(title);

        Call<VlogsDetailResponse> call = vlogsAPI.updateVideoTitle(videoId, vlogTitleRequest);
        call.enqueue(vlogTitleUpdateCallback);
    }

    private Callback<VlogsDetailResponse> vlogTitleUpdateCallback = new Callback<VlogsDetailResponse>() {
        @Override
        public void onResponse(Call<VlogsDetailResponse> call, Response<VlogsDetailResponse> response) {
            if (response.body() == null) {
                if (response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    VlogsDetailResponse vlogsDetailResponse = response.body();
                    ((UserFunnyVideosTabFragment) getParentFragment()).updateTitleInList(position, vlogsDetailResponse.getData().getResult().getTitle());
                    dismiss();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<VlogsDetailResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

}