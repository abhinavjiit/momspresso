package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.NotificationController;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.bloggermodel.ParentingBlogResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;

/**
 * Created by manish.soni on 30-07-2015.
 */
public class NotificationFragment extends BaseFragment implements View.OnClickListener {

    ImageView emailCal, emailTodo, phoneCal, phoneTodo;
    Boolean isCommingFromSetting = false;
    String appointmentCode = "3";
    String taskCode = "3";
    String oldAppointmentCode = "0";
    String oldTaskCode = "0";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.pushOpenScreenEvent(getActivity(), "Notification Settings", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        View view = inflater.inflate(R.layout.aa_notification, container, false);

        if (getArguments() != null) {
            isCommingFromSetting = getArguments().getBoolean(Constants.IS_COMMING_FROM_SETTING);
            if (isCommingFromSetting) {
                ((DashboardActivity) getActivity()).setTitle("Notifications");
            } else {
            }
        }

        emailCal = (ImageView) view.findViewById(R.id.cal_email);
        emailTodo = (ImageView) view.findViewById(R.id.todo_email);
        phoneCal = (ImageView) view.findViewById(R.id.cal_phone);
        phoneTodo = (ImageView) view.findViewById(R.id.todo_phone);

        emailCal.setOnClickListener(this);
        emailTodo.setOnClickListener(this);
        phoneCal.setOnClickListener(this);
        phoneTodo.setOnClickListener(this);

        oldAppointmentCode = SharedPrefUtils.getNotificationPrefrence(getActivity(), true);
        oldTaskCode = SharedPrefUtils.getNotificationPrefrence(getActivity(), false);

        setNotificationState(SharedPrefUtils.getNotificationPrefrence(getActivity(), true), SharedPrefUtils.getNotificationPrefrence(getActivity(), false));

        return view;
    }

    @Override
    protected void updateUi(Response response) {

        ParentingBlogResponse responseData;

        if (response == null) {
            ToastUtils.showToast(getActivity(), "Something went wrong from server", Toast.LENGTH_SHORT);
            removeProgressDialog();
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.NOTIFICATION_REQUEST:
                responseData = (ParentingBlogResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {

                    removeProgressDialog();
                    getActivity().getSupportFragmentManager().popBackStack();
                    ToastUtils.showToast(getActivity(), responseData.getResult().getMessage(), Toast.LENGTH_SHORT);

                } else if (responseData.getResponseCode() == 400) {
                    SharedPrefUtils.setNotificationPrefrence(getActivity(), oldAppointmentCode, true);
                    SharedPrefUtils.setNotificationPrefrence(getActivity(), oldTaskCode, false);
                    ToastUtils.showToast(getActivity(), "Something went wrong from server", Toast.LENGTH_SHORT);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.cal_email:
                if (emailCal.getTag().equals(0)) {
                    emailCal.setTag(1);
                    emailCal.setImageResource(R.drawable.checked_sel);
                } else {
                    emailCal.setTag(0);
                    emailCal.setImageResource(R.drawable.checked_unsel);
                }

                break;
            case R.id.cal_phone:
                if (phoneCal.getTag().equals(0)) {
                    phoneCal.setTag(1);
                    phoneCal.setImageResource(R.drawable.checked_sel);
                } else {
                    phoneCal.setTag(0);
                    phoneCal.setImageResource(R.drawable.checked_unsel);
                }
                break;
            case R.id.todo_email:
                if (emailTodo.getTag().equals(0)) {
                    emailTodo.setTag(1);
                    emailTodo.setImageResource(R.drawable.checked_sel);
                } else {
                    emailTodo.setTag(0);
                    emailTodo.setImageResource(R.drawable.checked_unsel);
                }
                break;
            case R.id.todo_phone:
                if (phoneTodo.getTag().equals(0)) {
                    phoneTodo.setTag(1);
                    phoneTodo.setImageResource(R.drawable.checked_sel);
                } else {
                    phoneTodo.setTag(0);
                    phoneTodo.setImageResource(R.drawable.checked_unsel);
                }
                break;

        }
    }

    public void saveNotificationSetting() {

//        set new appointmant notification Code..

        if (emailCal.getTag().equals(0) && phoneCal.getTag().equals(0)) {
            appointmentCode = "0";
        } else if (emailCal.getTag().equals(1) && phoneCal.getTag().equals(0)) {
            appointmentCode = "1";
        } else if (emailCal.getTag().equals(0) && phoneCal.getTag().equals(1)) {
            appointmentCode = "2";
        } else if (emailCal.getTag().equals(1) && phoneCal.getTag().equals(1)) {
            appointmentCode = "3";
        }

//        set new task notification Code..

        if (emailTodo.getTag().equals(0) && phoneTodo.getTag().equals(0)) {
            taskCode = "0";
        } else if (emailTodo.getTag().equals(1) && phoneTodo.getTag().equals(0)) {
            taskCode = "1";
        } else if (emailTodo.getTag().equals(0) && phoneTodo.getTag().equals(1)) {
            taskCode = "2";
        } else if (emailTodo.getTag().equals(1) && phoneTodo.getTag().equals(1)) {
            taskCode = "3";
        }

        SharedPrefUtils.setNotificationPrefrence(getActivity(), appointmentCode, true);
        SharedPrefUtils.setNotificationPrefrence(getActivity(), taskCode, false);

        if (isCommingFromSetting) {
            hitNotificationApi();
        } else {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    public void setNotificationState(String appCode, String taskCode) {

        switch (appCode) {

            case "0":
                emailCal.setTag(0);
                emailCal.setImageResource(R.drawable.checked_unsel);
                phoneCal.setTag(0);
                phoneCal.setImageResource(R.drawable.checked_unsel);
                break;
            case "1":
                emailCal.setTag(1);
                emailCal.setImageResource(R.drawable.checked_sel);
                phoneCal.setTag(0);
                phoneCal.setImageResource(R.drawable.checked_unsel);
                break;
            case "2":
                emailCal.setTag(0);
                emailCal.setImageResource(R.drawable.checked_unsel);
                phoneCal.setTag(1);
                phoneCal.setImageResource(R.drawable.checked_sel);
                break;
            case "3":
                emailCal.setTag(1);
                emailCal.setImageResource(R.drawable.checked_sel);
                phoneCal.setTag(1);
                phoneCal.setImageResource(R.drawable.checked_sel);
                break;

        }
        switch (taskCode) {

            case "0":
                emailTodo.setTag(0);
                emailTodo.setImageResource(R.drawable.checked_unsel);
                phoneTodo.setTag(0);
                phoneTodo.setImageResource(R.drawable.checked_unsel);
                break;
            case "1":
                emailTodo.setTag(1);
                emailTodo.setImageResource(R.drawable.checked_sel);
                phoneTodo.setTag(0);
                phoneTodo.setImageResource(R.drawable.checked_unsel);
                break;
            case "2":
                emailTodo.setTag(0);
                emailTodo.setImageResource(R.drawable.checked_unsel);
                phoneTodo.setTag(1);
                phoneTodo.setImageResource(R.drawable.checked_sel);
                break;
            case "3":
                emailTodo.setTag(1);
                emailTodo.setImageResource(R.drawable.checked_sel);
                phoneTodo.setTag(1);
                phoneTodo.setImageResource(R.drawable.checked_sel);
                break;

        }

    }

    public void hitNotificationApi() {
        showProgressDialog(getString(R.string.please_wait));
        ParentingRequest _parentingModel = new ParentingRequest();

        NotificationController notificationController = new NotificationController(getActivity(), this);
        notificationController.getData(AppConstants.NOTIFICATION_REQUEST, _parentingModel);
    }

}
