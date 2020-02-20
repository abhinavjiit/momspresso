package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.SubscriptionAndLanguageSettingsModel;
import com.mycity4kids.models.response.ConfigResponse;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.LanguageSettingsResponse;
import com.mycity4kids.models.response.PreferredLanguageUpdateRequest;
import com.mycity4kids.models.response.UpdateLanguageSettingsResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.LanguageSettingsAPI;
import com.mycity4kids.ui.adapter.PreferredLanguagesAdapter;
import com.mycity4kids.utils.AppUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 19/7/17.
 */
public class EditLangPrefsTabFragment extends BaseFragment implements View.OnClickListener {

    private ArrayList<SubscriptionAndLanguageSettingsModel> languagesList;

    private View view;
    private TextView saveTextView;
    private RecyclerView preferredLanguageRecyclerView;
    private PreferredLanguagesAdapter preferredLanguagesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_lang_prefs_tab_fragment, container, false);

        languagesList = new ArrayList<>();

        saveTextView = (TextView) view.findViewById(R.id.saveTextView);
        preferredLanguageRecyclerView = (RecyclerView) view.findViewById(R.id.prefLanguagesRecyclerView);

        saveTextView.setOnClickListener(this);

        final LinearLayoutManager languageLayoutManager = new LinearLayoutManager(getActivity());
        languageLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        preferredLanguagesAdapter = new PreferredLanguagesAdapter(getActivity(), languagesList);
        preferredLanguageRecyclerView.setLayoutManager(languageLayoutManager);
        preferredLanguageRecyclerView.setAdapter(preferredLanguagesAdapter);

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
                LanguageSettingsResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    SubscriptionAndLanguageSettingsModel englishDefault = new SubscriptionAndLanguageSettingsModel();
                    for (Map<String, String> map : responseData.getData().getResult()) {
                        Log.d("MAP", map.toString());
                        SubscriptionAndLanguageSettingsModel subscriptionAndLanguageSettingsModel = new SubscriptionAndLanguageSettingsModel();
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            if ("stories".equals(entry.getKey())) {
                                subscriptionAndLanguageSettingsModel.setStories(Integer.parseInt(entry.getValue()));
                            } else {
                                subscriptionAndLanguageSettingsModel.setName(entry.getKey());
                                subscriptionAndLanguageSettingsModel.setOriginalStatus(entry.getValue());
                                subscriptionAndLanguageSettingsModel.setStatus(entry.getValue());
                            }
                        }
                        languagesList.add(subscriptionAndLanguageSettingsModel);
                    }
                    preferredLanguagesAdapter.notifyDataSetChanged();
                    Log.d("dwaddawad", "" + languagesList);
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


    public void updateLanguageSubscription() {

        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            Toast.makeText(getActivity(), R.string.error_network, Toast.LENGTH_SHORT).show();
            return;
        }
        final Retrofit retrofit = BaseApplication.getInstance().getRetrofit();

        LanguageSettingsAPI languageSettingsAPI = retrofit.create(LanguageSettingsAPI.class);
        PreferredLanguageUpdateRequest languageUpdateRequest = new PreferredLanguageUpdateRequest();

        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < languagesList.size(); i++) {
            map.put(languagesList.get(i).getName().toLowerCase(), languagesList.get(i).getStatus());
        }

        languageUpdateRequest.setLangSubscription(map);

        Call<UpdateLanguageSettingsResponse> call = languageSettingsAPI.updatePreferredLanguages(languageUpdateRequest);
        call.enqueue(updateLanguageSettingsCallback);

        for (int i = 0; i < languagesList.size(); i++) {
            if (!languagesList.get(i).getStatus().equals(languagesList.get(i).getOriginalStatus())) {
                if ("1".equals(languagesList.get(i).getStatus())) {
                    Log.d("GTM Launguage Added", ":" + languagesList.get(i).getName());
                    Utils.pushEnableLanguageEvent(getActivity(), "SettingScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(), languagesList.get(i).getName());
//                    Utils.pushEventFeedLanguage(getActivity(), GTMEventType.FEED_LANGUAGE_ADD_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
//                            "Launguage Settings", languagesList.get(i).getName());
                } else {
                    Log.d("GTM Launguage Remove", ":" + languagesList.get(i).getName());
                    Utils.pushDisableLanguageEvent(getActivity(), "SettingScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(), languagesList.get(i).getName());
//                    Utils.pushEventFeedLanguage(getActivity(), GTMEventType.FEED_LANGUAGE_REMOVE_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
//                            "Launguage Settings", languagesList.get(i).getName());
                }
            }
            languagesList.get(i).setOriginalStatus(languagesList.get(i).getStatus());
        }
    }

    Callback<UpdateLanguageSettingsResponse> updateLanguageSettingsCallback = new Callback<UpdateLanguageSettingsResponse>() {
        @Override
        public void onResponse(Call<UpdateLanguageSettingsResponse> call, retrofit2.Response<UpdateLanguageSettingsResponse> response) {
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            try {
                UpdateLanguageSettingsResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (null != getActivity()) {
//                        Toast.makeText(getActivity(), "Language content settings updated successfully", Toast.LENGTH_SHORT).show();
                        UserInfo userInfo = SharedPrefUtils.getUserDetailModel(getActivity());
                        userInfo.setIsLangSelection("1");
                        SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), userInfo);

                        Map<String, String> subscribedContentLanguages = responseData.getData();
                        String filter = "";


                        FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.LANGUAGES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                                fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                                }.getType()
                        );

                        for (Map.Entry<String, String> entry : subscribedContentLanguages.entrySet()) {
                            if ("1".equals(entry.getValue())) {

                                for (Map.Entry<String, LanguageConfigModel> langEntry : retMap.entrySet()) {
                                    if (entry.getKey().equals("english")) {
                                        if (StringUtils.isNullOrEmpty(filter)) {
                                            filter = "0";
                                            break;
                                        } else {
                                            filter = filter + "," + "0";
                                            break;
                                        }
                                    } else {
                                        if (entry.getKey().equals(langEntry.getValue().getName().toLowerCase())) {
                                            if (StringUtils.isNullOrEmpty(filter)) {
                                                filter = langEntry.getKey();
                                            } else {
                                                filter = filter + "," + langEntry.getKey();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        SharedPrefUtils.setLanguageFilters(BaseApplication.getAppContext(), filter);
                        Toast.makeText(getActivity(), "Preferred languages updated", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (FileNotFoundException fnfe) {
                Crashlytics.logException(fnfe);
                Log.d("MC4kException", Log.getStackTraceString(fnfe));
                updateConfigSettings();
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
            Toast.makeText(getActivity(), R.string.error_network, Toast.LENGTH_SHORT).show();
            return;
        }
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
                ConfigResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.saveTextView:
                updateLanguageSubscription();
                break;
        }
    }
}
