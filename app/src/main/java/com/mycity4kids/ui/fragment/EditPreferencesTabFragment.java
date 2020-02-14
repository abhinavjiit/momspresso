package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IFacebookUser;
import com.mycity4kids.models.FollowTopics;
import com.mycity4kids.models.NotificationSettingsModel;
import com.mycity4kids.models.SubscriptionAndLanguageSettingsModel;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.request.SocialConnectRequest;
import com.mycity4kids.models.request.SubscriptionUpdateRequest;
import com.mycity4kids.models.response.BaseResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.NotificationSettingsResponse;
import com.mycity4kids.models.response.SubscriptionSettingsResponse;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.SubscriptionsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.SubscribeTopicsActivity;
import com.mycity4kids.ui.adapter.EmailSubscriptionAdapter;
import com.mycity4kids.ui.adapter.NotificationSubscriptionAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 19/7/17.
 */
public class EditPreferencesTabFragment extends BaseFragment implements View.OnClickListener, IFacebookUser {

    private ArrayList<SubscriptionAndLanguageSettingsModel> subscriptionSettingsList;
    private ArrayList<NotificationSettingsModel> notificationSettingsList;
    private ArrayList<String> mDatalist;
    private ArrayList<Topics> followedSubSubTopicList = new ArrayList<>();
    private View view;
    private TextView facebookConnectTextView;
    private TextView showMoreFollowedTopicsTextView;
    private TextView addTopicsBtn;
    private TextView saveTextView;
    private ViewGroup flowLayout;
    private RecyclerView notificationSettingRecyclerView;
    private RecyclerView subscriptionSettingRecyclerView;
    private EmailSubscriptionAdapter subscriptionSettingsListAdapter;
    private NotificationSubscriptionAdapter notificationSettingsListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_preferences_tab_fragment, container, false);

        subscriptionSettingsList = new ArrayList<>();
        notificationSettingsList = new ArrayList<>();

        showMoreFollowedTopicsTextView = (TextView) view.findViewById(R.id.showMoreFollowedTopicsTextView);
        addTopicsBtn = (TextView) view.findViewById(R.id.addTopicsBtn);
        saveTextView = (TextView) view.findViewById(R.id.saveTextView);
        flowLayout = (FlowLayout) view.findViewById(R.id.flowLayout);
        subscriptionSettingRecyclerView = (RecyclerView) view.findViewById(R.id.subscriptionSettingRecyclerView);
        notificationSettingRecyclerView = (RecyclerView) view.findViewById(R.id.notificationSettingRecyclerView);
        facebookConnectTextView = (TextView) view.findViewById(R.id.fbConnectBtn);
        facebookConnectTextView.setOnClickListener(this);
        addTopicsBtn.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        showMoreFollowedTopicsTextView.setOnClickListener(this);

        if ("1".equals(SharedPrefUtils.getFacebookConnectedFlag(BaseApplication.getAppContext()))) {
            facebookConnectTextView.setText(getString(R.string.app_settings_edit_prefs_add));
        } else {
            facebookConnectTextView.setText(getString(R.string.app_settings_edit_prefs_connected));
        }

        final LinearLayoutManager emailLayoutManager = new LinearLayoutManager(getActivity());
        emailLayoutManager.setOrientation(RecyclerView.VERTICAL);
        subscriptionSettingsListAdapter = new EmailSubscriptionAdapter(getActivity(), subscriptionSettingsList);
        subscriptionSettingRecyclerView.setLayoutManager(emailLayoutManager);
        subscriptionSettingRecyclerView.setAdapter(subscriptionSettingsListAdapter);

        final LinearLayoutManager notificationLayoutManager = new LinearLayoutManager(getActivity());
        notificationLayoutManager.setOrientation(RecyclerView.VERTICAL);
        notificationSettingsListAdapter = new NotificationSubscriptionAdapter(getActivity(), notificationSettingsList);
        notificationSettingRecyclerView.setLayoutManager(notificationLayoutManager);
        notificationSettingRecyclerView.setAdapter(notificationSettingsListAdapter);


        checkNotificationsStatus();
        checkSubscriptionStatus();
        getFollowedTopics();
        return view;
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
        Call<SubscriptionSettingsResponse> call = subscriptionsAPI.getSubscriptionList(SharedPrefUtils.getUserDetailModel(getActivity()).getEmail());
        call.enqueue(subscriptionSettingsResponseCallback);
    }

    private void getFollowedTopics() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI followListAPI = retrofit.create(TopicsCategoryAPI.class);
        Call<FollowUnfollowCategoriesResponse> categoriesResponseCall = followListAPI.getFollowedCategories(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId());
        categoriesResponseCall.enqueue(getFollowedTopicsResponseCallback);
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

    private Callback<FollowUnfollowCategoriesResponse> getFollowedTopicsResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
//                showToast("Something went wrong from server");
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    mDatalist = (ArrayList<String>) responseData.getData();
                    try {
                        FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        FollowTopics[] res = gson.fromJson(fileContent, FollowTopics[].class);
                        checkCurrentCategoryExists(res, mDatalist);
                    } catch (FileNotFoundException e) {
                        Crashlytics.logException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        Retrofit retro = BaseApplication.getInstance().getRetrofit();
                        final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

                        Call<ResponseBody> callDownloadAPI = topicsAPI.downloadCategoriesJSON();
                        callDownloadAPI.enqueue(downloadCategoriesJSONCallback);
                    }
                    inflateFollowedTopics();
                } else {
//                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
//            showToast(getString(R.string.went_wrong));
        }
    };

    Callback<ResponseBody> downloadCategoriesJSONCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);

                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                String popularURL = jsonObject.getJSONObject("data").getJSONObject("result").getJSONObject("category").getString("popularLocation");
                Call<ResponseBody> caller = topicsAPI.downloadTopicsListForFollowUnfollow(popularURL);

                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, response.body());
                        Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                            FollowTopics[] res = gson.fromJson(fileContent, FollowTopics[].class);
                            checkCurrentCategoryExists(res, mDatalist);
                        } catch (FileNotFoundException e) {
                            Crashlytics.logException(e);
                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Crashlytics.logException(t);
                        Log.d("MC4KException", Log.getStackTraceString(t));
                    }
                });
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
//            showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private boolean checkCurrentCategoryExists(FollowTopics[] res, ArrayList<String> followedTopicsIdList) {
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].getChild().size(); j++) {
                for (int k = 0; k < followedTopicsIdList.size(); k++) {
                    if (followedTopicsIdList.get(k).equals(res[i].getChild().get(j).getId())) {
                        followedSubSubTopicList.add(res[i].getChild().get(j));
                    }
                }
            }
        }
        return false;
    }

    private void inflateFollowedTopics() {
        if (followedSubSubTopicList == null || followedSubSubTopicList.size() == 0) {
            showMoreFollowedTopicsTextView.setVisibility(View.GONE);
            return;
        }
        for (int i = 0; i < followedSubSubTopicList.size(); i++) {
            followedSubSubTopicList.get(i).setIsSelected(true);
            final LinearLayout subsubLL = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.topic_follow_unfollow_item, null);
            final TextView catTextView = ((TextView) subsubLL.getChildAt(0));
            catTextView.setText(followedSubSubTopicList.get(i).getDisplay_name());
            catTextView.setSelected(true);
            subsubLL.setTag(followedSubSubTopicList.get(i));
            flowLayout.addView(subsubLL);
//            subsubLL.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Topics top = (Topics) subsubLL.getTag();
//                    if (top.isSelected()) {
//                        top.setIsSelected(false);
//                        catTextView.setSelected(false);
//                        Utils.pushUnfollowTopicEvent(getActivity(), "DetailArticleScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
//                                top.getId() + "~" + top.getDisplay_name());
//                    } else {
//                        top.setIsSelected(true);
//                        catTextView.setSelected(true);
//                        Utils.pushFollowTopicEvent(getActivity(), "DetailArticleScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
//                                top.getId() + "~" + top.getDisplay_name());
//                    }
//                }
//            });
        }
    }

    private void updateFollowedUnfollowedTopics() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
        FollowUnfollowCategoriesRequest followUnfollowCategoriesRequest = new FollowUnfollowCategoriesRequest();

        ArrayList<String> updateTopicList = new ArrayList<>();
        for (int i = 0; i < followedSubSubTopicList.size(); i++) {
            if (!followedSubSubTopicList.get(i).isSelected()) {
                updateTopicList.add(followedSubSubTopicList.get(i).getId());
            }
        }
        if (!updateTopicList.isEmpty()) {
            followUnfollowCategoriesRequest.setCategories(updateTopicList);
            Call<FollowUnfollowCategoriesResponse> categoriesResponseCall =
                    topicsCategoryAPI.followCategories(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(), followUnfollowCategoriesRequest);
            categoriesResponseCall.enqueue(followUnfollowCategoriesResponseCallback);
        }
    }

    private Callback<FollowUnfollowCategoriesResponse> followUnfollowCategoriesResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    SharedPrefUtils.setFollowedTopicsCount(BaseApplication.getAppContext(), responseData.getData().size());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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
                        if (null != getActivity()) {
                            Toast.makeText(getActivity(), "Settings updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (null != getActivity()) {
                            Toast.makeText(getActivity(), "Error while updating notification settings", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "Error while updating notification settings", Toast.LENGTH_SHORT).show();
                    }
                    removeProgressDialog();
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<NotificationSettingsResponse> call, Throwable t) {
                if (null != getActivity()) {
                    Toast.makeText(getActivity(), "Error while updating notification settings", Toast.LENGTH_SHORT).show();
                }
                Crashlytics.logException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

    public void updateSubscriptions() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            Toast.makeText(getActivity(), R.string.error_network, Toast.LENGTH_SHORT).show();
            return;
        }
        final Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        SubscriptionsAPI subscriptionsAPI = retrofit.create(SubscriptionsAPI.class);
        SubscriptionUpdateRequest subscriptionUpdateRequest = new SubscriptionUpdateRequest();

        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < subscriptionSettingsList.size(); i++) {
            map.put(subscriptionSettingsList.get(i).getName(), subscriptionSettingsList.get(i).getStatus());
        }

        subscriptionUpdateRequest.setEmail(SharedPrefUtils.getUserDetailModel(getActivity()).getEmail());
        subscriptionUpdateRequest.setSubscribe(map);

        Call<SubscriptionSettingsResponse> call = subscriptionsAPI.updateSubscriptions(subscriptionUpdateRequest);
        call.enqueue(updateSubscriptionsReponseListener);
        Log.d("GTM Subscription", ":" + map.values());
        for (int i = 0; i < subscriptionSettingsList.size(); i++) {
            if (!subscriptionSettingsList.get(i).getStatus().equals(subscriptionSettingsList.get(i).getOriginalStatus())) {
                if ("1".equals(subscriptionSettingsList.get(i).getStatus())) {
                    Utils.pushEnableSubscriptionEvent(getActivity(), "SettingScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(), subscriptionSettingsList.get(i).getName());
                } else {
                    Utils.pushDisableSubscriptionEvent(getActivity(), "SettingScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(), subscriptionSettingsList.get(i).getName());
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
                    if (null != getActivity()) {
//                        Toast.makeText(getActivity(), "Subscription settings updated successfully", Toast.LENGTH_SHORT).show();
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
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showMoreFollowedTopicsTextView:
                flowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                showMoreFollowedTopicsTextView.setVisibility(View.GONE);
                break;
            case R.id.addTopicsBtn:

                Intent subscribeTopicIntent = new Intent(getActivity(), SubscribeTopicsActivity.class);
                subscribeTopicIntent.putExtra("source", "settings");
                startActivityForResult(subscribeTopicIntent, 1111);
                break;
            case R.id.saveTextView:
                updateNotificationSettings();
                updateSubscriptions();
                updateFollowedUnfollowedTopics();
                break;
            case R.id.fbConnectBtn:
                if (getString(R.string.app_settings_edit_prefs_add).equals(facebookConnectTextView.getText().toString())) {
                    FacebookUtils.facebookLogin(getActivity(), this);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1111 && resultCode == Activity.RESULT_OK) {
                flowLayout.removeAllViews();
                followedSubSubTopicList.clear();
                getFollowedTopics();
            }
            FacebookUtils.onActivityResult(getActivity(), requestCode, resultCode, data);
        } catch (Exception ex) {
        }

    }

    @Override
    public void getFacebookUser(JSONObject jObject, String user) {
        try {
            if (user != null) {
                SocialConnectRequest socialConnectRequest = new SocialConnectRequest();
                socialConnectRequest.setToken(user);
                socialConnectRequest.setReferer("fb");

                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                LoginRegistrationAPI socialConnectAPI = retrofit.create(LoginRegistrationAPI.class);
                Call<BaseResponse> call = socialConnectAPI.socialConnect(socialConnectRequest);
                call.enqueue(socialConnectResponseListener);

            }
        } catch (Exception e) {
            removeProgressDialog();
        }
    }

    private Callback<BaseResponse> socialConnectResponseListener = new Callback<BaseResponse>() {
        @Override
        public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
            if (response == null || response.body() == null) {
                return;
            }
            try {
                BaseResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    Log.d("socialConnectListen", "SUCCESS");
                    facebookConnectTextView.setText(getString(R.string.app_settings_edit_prefs_connected));
                } else {
                    Log.d("socialConnectListener", "FAILURE");
                    facebookConnectTextView.setText(getString(R.string.app_settings_edit_prefs_add));
                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<BaseResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
