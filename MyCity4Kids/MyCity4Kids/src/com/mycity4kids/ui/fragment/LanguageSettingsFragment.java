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
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.SubscriptionAndLanguageSettingsModel;
import com.mycity4kids.models.response.ConfigResponse;
import com.mycity4kids.models.response.LanguageSettingsResponse;
import com.mycity4kids.models.response.PreferredLanguageUpdateRequest;
import com.mycity4kids.models.response.UpdateLanguageSettingsResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.LanguageSettingsAPI;
import com.mycity4kids.sync.CategorySyncService;
import com.mycity4kids.ui.adapter.LanguageSettingsListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 2/4/16.
 */
public class LanguageSettingsFragment extends BaseFragment {

    private boolean isSubsequentCall = false;

    private ListView languageListView;
    private ArrayList<SubscriptionAndLanguageSettingsModel> languageSettingsList;
    private LanguageSettingsListAdapter languageSettingsListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_settings, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Language Settings", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        setHasOptionsMenu(true);
        languageSettingsList = new ArrayList<>();
        languageListView = (ListView) view.findViewById(R.id.languageSettingListView);
        checkLanguageSubscriptionStatus();
        return view;
    }

    private void checkLanguageSubscriptionStatus() {

        showProgressDialog("Please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LanguageSettingsAPI languageSettingsAPI = retrofit.create(LanguageSettingsAPI.class);
        Call<LanguageSettingsResponse> call = languageSettingsAPI.getLanguagesList();
        call.enqueue(languageSettingsResponseCallback);
    }

    private Callback<LanguageSettingsResponse> languageSettingsResponseCallback = new Callback<LanguageSettingsResponse>() {
        @Override
        public void onResponse(Call<LanguageSettingsResponse> call, retrofit2.Response<LanguageSettingsResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }
            try {
                LanguageSettingsResponse responseData = (LanguageSettingsResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    responseData.getData().getResult()
                    for (Map.Entry<String, String> entry : responseData.getData().getResult().entrySet()) {
                        Log.d("Notification Items = ", entry.getKey() + "/" + entry.getValue());
                        SubscriptionAndLanguageSettingsModel subscriptionAndLanguageSettingsModel = new SubscriptionAndLanguageSettingsModel();
                        subscriptionAndLanguageSettingsModel.setStatus(entry.getValue());
                        subscriptionAndLanguageSettingsModel.setName(entry.getKey());
                        languageSettingsList.add(subscriptionAndLanguageSettingsModel);
                    }
                    languageSettingsListAdapter = new LanguageSettingsListAdapter(getActivity(), languageSettingsList);
                    languageListView.setAdapter(languageSettingsListAdapter);
                } else {

                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<LanguageSettingsResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    protected void updateUi(Response response) {
    }

    public void updateLanguageSubscription() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LanguageSettingsAPI languageSettingsAPI = retrofit.create(LanguageSettingsAPI.class);
        PreferredLanguageUpdateRequest languageUpdateRequest = new PreferredLanguageUpdateRequest();

        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < languageSettingsList.size(); i++) {
            map.put(languageSettingsList.get(i).getName(), languageSettingsList.get(i).getStatus());
        }

        languageUpdateRequest.setLangSubscription(map);

        Call<UpdateLanguageSettingsResponse> call = languageSettingsAPI.updatePreferredLanguages(languageUpdateRequest);
        call.enqueue(updateLanguageSettingsCallback);
    }

    Callback<UpdateLanguageSettingsResponse> updateLanguageSettingsCallback = new Callback<UpdateLanguageSettingsResponse>() {
        @Override
        public void onResponse(Call<UpdateLanguageSettingsResponse> call, retrofit2.Response<UpdateLanguageSettingsResponse> response) {
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            try {
                UpdateLanguageSettingsResponse responseData = (UpdateLanguageSettingsResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "Language content settings updated successfully", Toast.LENGTH_SHORT).show();
                        UserInfo userInfo = SharedPrefUtils.getUserDetailModel(getActivity());
                        userInfo.setIsLangSelection("1");
                        SharedPrefUtils.setUserDetailModel(getActivity(), userInfo);

                        Map<String, String> subscribedContentLanguages = responseData.getData();
                        String filter = "0";
                        for (Map.Entry<String, String> entry : subscribedContentLanguages.entrySet()) {
                            if ("1".equals(entry.getValue())) {
                                String langKey = SharedPrefUtils.getLanguageConfig(getActivity(), entry.getKey());
                                if (!StringUtils.isNullOrEmpty(langKey)) {
                                    filter = filter + "," + SharedPrefUtils.getLanguageConfig(getActivity(), entry.getKey());
                                } else {
                                    if (!isSubsequentCall) {
                                        updateConfigSettings();
                                        return;
                                    }

                                }
                            }
                        }
                        SharedPrefUtils.setLanguageFilters(getActivity(), filter);
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
        public void onFailure(Call<UpdateLanguageSettingsResponse> call, Throwable t) {
            if (null != getActivity()) {
                Toast.makeText(getActivity(), "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void updateConfigSettings() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            return;
        }
        isSubsequentCall = true;
        final Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ConfigAPIs configAPIs = retrofit.create(ConfigAPIs.class);
        Call<ConfigResponse> call = configAPIs.getConfig();
        call.enqueue(configSettingResponseListener);
    }

    private Callback<ConfigResponse> configSettingResponseListener = new Callback<ConfigResponse>() {
        @Override
        public void onResponse(Call<ConfigResponse> call, retrofit2.Response<ConfigResponse> response) {
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ConfigResponse responseData = (ConfigResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    for (Map.Entry<String, String> entry : responseData.getData().getResult().getLanguage().entrySet()) {
                        SharedPrefUtils.setLanguageConfig(getActivity(), entry.getKey(), entry.getValue());
                    }
                    updateLanguageSubscription();
                } else {
                    Toast.makeText(getActivity(), "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }


        }

        @Override
        public void onFailure(Call<ConfigResponse> call, Throwable t) {
            Toast.makeText(getActivity(), "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
