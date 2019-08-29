package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.ChangePasswordRequest;
import com.mycity4kids.models.response.ChangePasswordResponse;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 19/7/17.
 */
public class ChangePasswordTabFragment extends BaseFragment implements View.OnClickListener {

    private View view;

    private EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private TextView saveTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.change_password_tab_fragment, container, false);

        currentPasswordEditText = (EditText) view.findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = (EditText) view.findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.confirmPasswordEditText);
        saveTextView = (TextView) view.findViewById(R.id.saveTextView);

        saveTextView.setOnClickListener(this);

        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveTextView:
                if (validateFields()) {
                    updatePassword();
                }
                break;
        }
    }

    private void updatePassword() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(currentPasswordEditText.getText().toString());
        changePasswordRequest.setNewPassword(newPasswordEditText.getText().toString());

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<ChangePasswordResponse> call = loginRegistrationAPI.changePassword(changePasswordRequest);
        call.enqueue(passwordChangeResponseListener);
    }

    private Callback<ChangePasswordResponse> passwordChangeResponseListener = new Callback<ChangePasswordResponse>() {
        @Override
        public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
            if (response == null || response.body() == null) {
//                showToast(getString(R.string.went_wrong));
                return;
            }
            ChangePasswordResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                Toast.makeText(getActivity(), getString(R.string.app_settings_change_pass_pass_update_success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.app_settings_change_pass_pass_update_fail), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private boolean validateFields() {
        if (StringUtils.isNullOrEmpty(currentPasswordEditText.getText().toString().trim())) {
            Toast.makeText(getActivity(), getString(R.string.app_settings_change_pass_toast_current_pass_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (StringUtils.isNullOrEmpty(newPasswordEditText.getText().toString().trim())) {
            Toast.makeText(getActivity(), getString(R.string.app_settings_change_pass_toast_new_pass_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.app_settings_change_pass_toast_confirm_pass_match), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
