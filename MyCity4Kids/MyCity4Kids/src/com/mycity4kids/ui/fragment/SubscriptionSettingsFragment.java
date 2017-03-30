package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.SubscriptionAndLanguageSettingsModel;
import com.mycity4kids.models.request.SubscriptionUpdateRequest;
import com.mycity4kids.models.response.SubscriptionSettingsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.SubscriptionsAPI;
import com.mycity4kids.ui.adapter.SubscriptionSettingsListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 2/4/16.
 */
public class SubscriptionSettingsFragment extends BaseFragment {

    ListView subscriptionListView;
    ArrayList<SubscriptionAndLanguageSettingsModel> subscriptionSettingsList;
    SubscriptionSettingsListAdapter subscriptionSettingsListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subscription_settings, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Subscription Settings", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        setHasOptionsMenu(true);
        subscriptionSettingsList = new ArrayList<>();
        subscriptionListView = (ListView) view.findViewById(R.id.subscriptionSettingListView);
        checkSubscriptionStatus();
        return view;
    }

    private void checkSubscriptionStatus() {

        showProgressDialog("Please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        SubscriptionsAPI subscriptionsAPI = retrofit.create(SubscriptionsAPI.class);
        Call<SubscriptionSettingsResponse> call = subscriptionsAPI.getSubscriptionList(SharedPrefUtils.getUserDetailModel(getActivity()).getEmail());
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
                SubscriptionSettingsResponse responseData = (SubscriptionSettingsResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    for (Map.Entry<String, Object> entry : responseData.getData().getResult().entrySet()) {
                        if (entry.getValue() instanceof String) {
                            Log.d("Notification Items = ", entry.getKey() + "/" + entry.getValue());

                            SubscriptionAndLanguageSettingsModel subscriptionAndLanguageSettingsModel = new SubscriptionAndLanguageSettingsModel();
                            subscriptionAndLanguageSettingsModel.setStatus((String) entry.getValue());
                            subscriptionAndLanguageSettingsModel.setName(entry.getKey());
                            switch (entry.getKey()) {
                                case "newsletters":
                                    subscriptionAndLanguageSettingsModel.setDisplayName("Subscribe to the Top Reads Of The Month");
                                    break;
                                case "trending":
                                    subscriptionAndLanguageSettingsModel.setDisplayName("Subscribe to the Trending Blog Of The Day");
                                    break;
                                case "momspresso":
                                    subscriptionAndLanguageSettingsModel.setDisplayName("Subscribe to the Momspresso Videos");
                                    break;
                                case "editorial":
                                    subscriptionAndLanguageSettingsModel.setDisplayName("Subscribe to the Best In Your City");
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
                                subscriptionAndLanguageSettingsModel.setName(langEntry.getKey());
                                switch (langEntry.getKey()) {
                                    case "hindi":
                                        subscriptionAndLanguageSettingsModel.setDisplayName("Hindi");
                                        break;
                                    case "bengali":
                                        subscriptionAndLanguageSettingsModel.setDisplayName("Bangla");
                                        break;
                                    case "marathi":
                                        subscriptionAndLanguageSettingsModel.setDisplayName("Marathi");
                                        break;
                                    default:
                                        subscriptionAndLanguageSettingsModel.setDisplayName(entry.getKey());
                                }
                                subscriptionSettingsList.add(subscriptionAndLanguageSettingsModel);
                            }
                        }
                    }
                    subscriptionSettingsListAdapter = new SubscriptionSettingsListAdapter(getActivity(), subscriptionSettingsList);
                    subscriptionListView.setAdapter(subscriptionSettingsListAdapter);
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

    @Override
    protected void updateUi(Response response) {
    }

    public void updateLanguageSubscription() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        SubscriptionsAPI subscriptionsAPI = retrofit.create(SubscriptionsAPI.class);
        SubscriptionUpdateRequest subscriptionUpdateRequest = new SubscriptionUpdateRequest();

        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < subscriptionSettingsList.size(); i++) {
            map.put(subscriptionSettingsList.get(i).getName(), subscriptionSettingsList.get(i).getStatus());
        }

        subscriptionUpdateRequest.setEmail(SharedPrefUtils.getUserDetailModel(getActivity()).getEmail());
        subscriptionUpdateRequest.setSubscribe(map);

        Call<SubscriptionSettingsResponse> call = subscriptionsAPI.updateSubscriptions(subscriptionUpdateRequest);
        call.enqueue(new Callback<SubscriptionSettingsResponse>() {
            @Override
            public void onResponse(Call<SubscriptionSettingsResponse> call, retrofit2.Response<SubscriptionSettingsResponse> response) {
                if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                    return;
                }
                try {
                    SubscriptionSettingsResponse responseData = (SubscriptionSettingsResponse) response.body();
                    if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                        if (null != getActivity()) {
                            Toast.makeText(getActivity(), "Subscription settings updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (null != getActivity()) {
                            Toast.makeText(getActivity(), "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
                    }
                    removeProgressDialog();
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<SubscriptionSettingsResponse> call, Throwable t) {
                if (null != getActivity()) {
                    Toast.makeText(getActivity(), "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
                }
                Crashlytics.logException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

//    public void updateNotificationSettings() {
//        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);
//        HashMap<String, String> map = new HashMap<>();
//        for (int i = 0; i < subscriptionSettingsList.size(); i++) {
//            map.put(subscriptionSettingsList.get(i).getId(), subscriptionSettingsList.get(i).getStatus());
//        }
//        Call<NotificationSettingsResponse> call = notificationsAPI.updateNotificationSettings(map);
//        call.enqueue(new Callback<NotificationSettingsResponse>() {
//            @Override
//            public void onResponse(Call<NotificationSettingsResponse> call, retrofit2.Response<NotificationSettingsResponse> response) {
//                if (response == null || response.body() == null) {
////                showToast("Something went wrong from server");
//                    return;
//                }
//                try {
//                    NotificationSettingsResponse responseData = (NotificationSettingsResponse) response.body();
//                    if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                        if (null != getActivity()) {
//                            Toast.makeText(getActivity(), "Notification settings updated successfully", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        if (null != getActivity()) {
//                            Toast.makeText(getActivity(), "Error while updating notification settings", Toast.LENGTH_SHORT).show();
//                            ;
//                        }
//                    }
//                } catch (Exception e) {
//                    if (null != getActivity()) {
//                        Toast.makeText(getActivity(), "Error while updating notification settings", Toast.LENGTH_SHORT).show();
//                        ;
//                    }
//                    removeProgressDialog();
//                    Crashlytics.logException(e);
//                    Log.d("MC4kException", Log.getStackTraceString(e));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<NotificationSettingsResponse> call, Throwable t) {
//                if (null != getActivity()) {
//                    Toast.makeText(getActivity(), "Error while updating notification settings", Toast.LENGTH_SHORT).show();
//                    ;
//                }
//                Crashlytics.logException(t);
//                Log.d("MC4kException", Log.getStackTraceString(t));
//            }
//        });
//    }

}
