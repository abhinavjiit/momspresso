package com.mycity4kids.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.models.NotificationSettingsModel;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.response.NotificationSettingsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.ui.activity.SettingsActivity;
import com.mycity4kids.ui.adapter.NotificationSettingsListAdapter;
import com.mycity4kids.utils.NearMyCity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 2/4/16.
 */
public class NotificationSettingsFragment extends BaseFragment {

    ListView notificationListView;
    ArrayList<NotificationSettingsModel> notificationSettingsList;
    NotificationSettingsListAdapter notificationSettingsListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_settings, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Notification Settings", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        setHasOptionsMenu(true);
        notificationSettingsList = new ArrayList<>();
        notificationListView = (ListView) view.findViewById(R.id.notificationSettingListView);
        checkNotificationsStatus();
        return view;
    }

    private void checkNotificationsStatus() {

        showProgressDialog("Please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);
        Call<NotificationSettingsResponse> call = notificationsAPI.getNotificationsStatus();
        call.enqueue(notificationSettingsResponseCallback);
    }

    private Callback<NotificationSettingsResponse> notificationSettingsResponseCallback = new Callback<NotificationSettingsResponse>() {
        @Override
        public void onResponse(Call<NotificationSettingsResponse> call, retrofit2.Response<NotificationSettingsResponse> response) {

            removeProgressDialog();
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            try {
                NotificationSettingsResponse responseData = (NotificationSettingsResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    for (Map.Entry<String, String> entry : responseData.getData().getResult().entrySet()) {
                        NotificationSettingsModel notificationSettingsModel = new NotificationSettingsModel();
                        notificationSettingsModel.setId(entry.getKey());
                        notificationSettingsModel.setName(SharedPrefUtils.getNotificationConfig(getActivity(), entry.getKey()));
                        notificationSettingsModel.setStatus(entry.getValue());
                        notificationSettingsList.add(notificationSettingsModel);
                        Log.d("Notification Items = ", entry.getKey() + "/" + entry.getValue());
                    }
                    notificationSettingsListAdapter = new NotificationSettingsListAdapter(getActivity(), notificationSettingsList);
                    notificationListView.setAdapter(notificationSettingsListAdapter);
                } else {

                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<NotificationSettingsResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    protected void updateUi(Response response) {
    }

    public void updateNotificationSettings() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < notificationSettingsList.size(); i++) {
            map.put(notificationSettingsList.get(i).getId(), notificationSettingsList.get(i).getStatus());
        }
        Call<NotificationSettingsResponse> call = notificationsAPI.updateNotificationSettings(map);
        call.enqueue(new Callback<NotificationSettingsResponse>() {
            @Override
            public void onResponse(Call<NotificationSettingsResponse> call, retrofit2.Response<NotificationSettingsResponse> response) {
                if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                    return;
                }
                try {
                    NotificationSettingsResponse responseData = (NotificationSettingsResponse) response.body();
                    if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                        if (null != getActivity()) {
                            Toast.makeText(getActivity(), "Notification settings updated successfully", Toast.LENGTH_SHORT);
                        }
                    } else {
                        if (null != getActivity()) {
                            Toast.makeText(getActivity(), "Error while updating notification settings", Toast.LENGTH_SHORT);
                        }
                    }
                } catch (Exception e) {
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "Error while updating notification settings", Toast.LENGTH_SHORT);
                    }
                    removeProgressDialog();
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<NotificationSettingsResponse> call, Throwable t) {
                if (null != getActivity()) {
                    Toast.makeText(getActivity(), "Error while updating notification settings", Toast.LENGTH_SHORT);
                }
                Crashlytics.logException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

}
