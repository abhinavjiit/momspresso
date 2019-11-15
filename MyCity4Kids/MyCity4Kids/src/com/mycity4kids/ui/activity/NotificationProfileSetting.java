package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.NotificationSettingsModel;
import com.mycity4kids.models.SubscriptionAndLanguageSettingsModel;
import com.mycity4kids.models.request.SubscriptionUpdateRequest;
import com.mycity4kids.models.response.NotificationSettingsResponse;
import com.mycity4kids.models.response.SubscriptionSettingsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.SubscriptionsAPI;
import com.mycity4kids.ui.adapter.EmailSubscriptionAdapter;
import com.mycity4kids.ui.adapter.NotificationSubscriptionAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class NotificationProfileSetting extends BaseActivity implements View.OnClickListener {

    private ArrayList<SubscriptionAndLanguageSettingsModel> subscriptionSettingsList;
    private ArrayList<NotificationSettingsModel> notificationSettingsList;
    private RecyclerView notificationSettingRecyclerView;
    private RecyclerView subscriptionSettingRecyclerView;
    private EmailSubscriptionAdapter subscriptionSettingsListAdapter;
    private NotificationSubscriptionAdapter notificationSettingsListAdapter;
    private TextView saveTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_profile_setting);
        subscriptionSettingsList = new ArrayList<>();
        notificationSettingsList = new ArrayList<>();
        subscriptionSettingRecyclerView = (RecyclerView) findViewById(R.id.subscriptionSettingRecyclerView);
        notificationSettingRecyclerView = (RecyclerView) findViewById(R.id.notificationSettingRecyclerView);
        saveTextView = (TextView) findViewById(R.id.saveTextView);
        final LinearLayoutManager emailLayoutManager = new LinearLayoutManager(this);
        emailLayoutManager.setOrientation(RecyclerView.VERTICAL);
        subscriptionSettingsListAdapter = new EmailSubscriptionAdapter(this, subscriptionSettingsList);
        subscriptionSettingRecyclerView.setLayoutManager(emailLayoutManager);
        subscriptionSettingRecyclerView.setAdapter(subscriptionSettingsListAdapter);

        final LinearLayoutManager notificationLayoutManager = new LinearLayoutManager(this);
        notificationLayoutManager.setOrientation(RecyclerView.VERTICAL);
        notificationSettingsListAdapter = new NotificationSubscriptionAdapter(this, notificationSettingsList);
        notificationSettingRecyclerView.setLayoutManager(notificationLayoutManager);
        notificationSettingRecyclerView.setAdapter(notificationSettingsListAdapter);


        checkNotificationsStatus();
        checkSubscriptionStatus();
        saveTextView.setOnClickListener(this);
    }

    private void checkNotificationsStatus() {
        showProgressDialog("Please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);
        Call<NotificationSettingsResponse> call = notificationsAPI.getNotificationsStatus();
        call.enqueue(notificationSettingsResponseCallback);
    }

    private void checkSubscriptionStatus() {
        showProgressDialog("Please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        SubscriptionsAPI subscriptionsAPI = retrofit.create(SubscriptionsAPI.class);
        Call<SubscriptionSettingsResponse> call = subscriptionsAPI.getSubscriptionList(SharedPrefUtils.getUserDetailModel(this).getEmail());
        call.enqueue(subscriptionSettingsResponseCallback);
    }

    private Callback<SubscriptionSettingsResponse> subscriptionSettingsResponseCallback = new Callback<SubscriptionSettingsResponse>() {
        @Override
        public void onResponse(Call<SubscriptionSettingsResponse> call, retrofit2.Response<SubscriptionSettingsResponse> response) {

            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }
            try {
                SubscriptionSettingsResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    for (Map.Entry<String, Object> entry : responseData.getData().getResult().entrySet()) {
                        if (entry.getValue() instanceof String) {
                            Log.d("Notification Items = ", entry.getKey() + "/" + entry.getValue());

                            SubscriptionAndLanguageSettingsModel subscriptionAndLanguageSettingsModel = new SubscriptionAndLanguageSettingsModel();
                            subscriptionAndLanguageSettingsModel.setStatus((String) entry.getValue());
                            subscriptionAndLanguageSettingsModel.setOriginalStatus((String) entry.getValue());
                            subscriptionAndLanguageSettingsModel.setName(entry.getKey());
                            switch (entry.getKey()) {
                                case "newsletters":
                                    subscriptionAndLanguageSettingsModel.setDisplayName(getString(R.string.app_settings_edit_prefs_subscribe_top_reads));
                                    break;
                                case "trending":
                                    subscriptionAndLanguageSettingsModel.setDisplayName(getString(R.string.app_settings_edit_prefs_subscribe_trending_blog));
                                    break;
                                case "momspresso":
                                    subscriptionAndLanguageSettingsModel.setDisplayName(getString(R.string.app_settings_edit_prefs_subscribe_momspresso));
                                    break;
                                case "editorial":
                                    subscriptionAndLanguageSettingsModel.setDisplayName(getString(R.string.app_settings_edit_prefs_subscribe_city_best));
                                    break;
                                default:
                                    subscriptionAndLanguageSettingsModel.setDisplayName(entry.getKey());
                            }
                            subscriptionSettingsList.add(subscriptionAndLanguageSettingsModel);
                        } else if (entry.getValue() instanceof Map) {
                            Map<String, String> retMap = new Gson().fromJson(entry.getValue().toString(), new TypeToken<HashMap<String, String>>() {
                            }.getType());

                            for (Map.Entry<String, String> langEntry : retMap.entrySet()) {
                                SubscriptionAndLanguageSettingsModel subscriptionAndLanguageSettingsModel = new SubscriptionAndLanguageSettingsModel();
                                subscriptionAndLanguageSettingsModel.setStatus(langEntry.getValue());
                                subscriptionAndLanguageSettingsModel.setOriginalStatus(langEntry.getValue());
                                subscriptionAndLanguageSettingsModel.setName(langEntry.getKey());
                                subscriptionAndLanguageSettingsModel.setDisplayName(langEntry.getKey());
                                subscriptionSettingsList.add(subscriptionAndLanguageSettingsModel);
                            }
                        }
                    }
                    subscriptionSettingsListAdapter.notifyDataSetChanged();
                } else {

                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<SubscriptionSettingsResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<NotificationSettingsResponse> notificationSettingsResponseCallback = new Callback<NotificationSettingsResponse>() {
        @Override
        public void onResponse(Call<NotificationSettingsResponse> call, retrofit2.Response<NotificationSettingsResponse> response) {

            removeProgressDialog();
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            try {
                NotificationSettingsResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    for (Map.Entry<String, String> entry : responseData.getData().getResult().entrySet()) {
                        NotificationSettingsModel notificationSettingsModel = new NotificationSettingsModel();
                        notificationSettingsModel.setId(entry.getKey());
                        notificationSettingsModel.setName(SharedPrefUtils.getNotificationConfig(BaseApplication.getAppContext(), entry.getKey()));
                        notificationSettingsModel.setStatus(entry.getValue());
                        notificationSettingsList.add(notificationSettingsModel);
                        Log.d("Notification Items = ", entry.getKey() + "/" + entry.getValue());
                    }
                    notificationSettingsListAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveTextView:
                updateNotificationSettings();
                updateSubscriptions();
                break;
        }
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
                    NotificationSettingsResponse responseData = response.body();
                    if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                        Toast.makeText(NotificationProfileSetting.this, "Settings updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NotificationProfileSetting.this, "Error while updating notification settings", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(NotificationProfileSetting.this, "Error while updating notification settings", Toast.LENGTH_SHORT).show();
                    removeProgressDialog();
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<NotificationSettingsResponse> call, Throwable t) {
                Toast.makeText(NotificationProfileSetting.this, "Error while updating notification settings", Toast.LENGTH_SHORT).show();
                Crashlytics.logException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

    public void updateSubscriptions() {
        if (!ConnectivityUtils.isNetworkEnabled(NotificationProfileSetting.this)) {
            Toast.makeText(NotificationProfileSetting.this, R.string.error_network, Toast.LENGTH_SHORT).show();
            return;
        }
        final Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        SubscriptionsAPI subscriptionsAPI = retrofit.create(SubscriptionsAPI.class);
        SubscriptionUpdateRequest subscriptionUpdateRequest = new SubscriptionUpdateRequest();

        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < subscriptionSettingsList.size(); i++) {
            map.put(subscriptionSettingsList.get(i).getName(), subscriptionSettingsList.get(i).getStatus());
        }

        subscriptionUpdateRequest.setEmail(SharedPrefUtils.getUserDetailModel(NotificationProfileSetting.this).getEmail());
        subscriptionUpdateRequest.setSubscribe(map);

        Call<SubscriptionSettingsResponse> call = subscriptionsAPI.updateSubscriptions(subscriptionUpdateRequest);
        call.enqueue(updateSubscriptionsReponseListener);
        Log.d("GTM Subscription", ":" + map.values());
        for (int i = 0; i < subscriptionSettingsList.size(); i++) {
            if (!subscriptionSettingsList.get(i).getStatus().equals(subscriptionSettingsList.get(i).getOriginalStatus())) {
                if ("1".equals(subscriptionSettingsList.get(i).getStatus())) {
                    Utils.pushEnableSubscriptionEvent(NotificationProfileSetting.this, "SettingScreen", SharedPrefUtils.getUserDetailModel(NotificationProfileSetting.this).getDynamoId(), subscriptionSettingsList.get(i).getName());
                } else {
                    Utils.pushDisableSubscriptionEvent(NotificationProfileSetting.this, "SettingScreen", SharedPrefUtils.getUserDetailModel(NotificationProfileSetting.this).getDynamoId(), subscriptionSettingsList.get(i).getName());
                }
            }
            subscriptionSettingsList.get(i).setOriginalStatus(subscriptionSettingsList.get(i).getStatus());
        }

    }

    private Callback<SubscriptionSettingsResponse> updateSubscriptionsReponseListener = new Callback<SubscriptionSettingsResponse>() {
        @Override
        public void onResponse(Call<SubscriptionSettingsResponse> call, retrofit2.Response<SubscriptionSettingsResponse> response) {
            if (response == null || response.body() == null) {
                return;
            }
            try {
                SubscriptionSettingsResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                        Toast.makeText(NotificationProfileSetting.this, "Subscription settings updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotificationProfileSetting.this, "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(NotificationProfileSetting.this, "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<SubscriptionSettingsResponse> call, Throwable t) {
            Toast.makeText(NotificationProfileSetting.this, "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

}
