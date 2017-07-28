package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.FollowTopics;
import com.mycity4kids.models.NotificationSettingsModel;
import com.mycity4kids.models.SubscriptionAndLanguageSettingsModel;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.request.SubscriptionUpdateRequest;
import com.mycity4kids.models.response.ConfigResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.LanguageSettingsResponse;
import com.mycity4kids.models.response.NotificationSettingsResponse;
import com.mycity4kids.models.response.PreferredLanguageUpdateRequest;
import com.mycity4kids.models.response.SubscriptionSettingsResponse;
import com.mycity4kids.models.response.UpdateLanguageSettingsResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.LanguageSettingsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.SubscriptionsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.EmailSubscriptionAdapter;
import com.mycity4kids.ui.adapter.NotificationSubscriptionAdapter;
import com.mycity4kids.ui.adapter.PreferredLanguagesAdapter;
import com.mycity4kids.utils.AppUtils;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 19/7/17.
 */
public class EditPreferencesTabFragment extends BaseFragment implements View.OnClickListener {

    private boolean isSubsequentCall = false;

    private ArrayList<SubscriptionAndLanguageSettingsModel> subscriptionSettingsList;
    private ArrayList<NotificationSettingsModel> notificationSettingsList;
    private ArrayList<SubscriptionAndLanguageSettingsModel> languagesList;
    private ArrayList<String> mDatalist;
    ArrayList<Topics> followedSubSubTopicList = new ArrayList<>();


    private View view;
    private TextView showMoreFollowedTopicsTextView;
    private TextView addTopicsBtn;
    private TextView saveTextView;
    private ViewGroup flowLayout;
    private RecyclerView notificationSettingRecyclerView;
    private RecyclerView subscriptionSettingRecyclerView;
    private RecyclerView preferredLanguageRecyclerView;

    private EmailSubscriptionAdapter subscriptionSettingsListAdapter;
    private NotificationSubscriptionAdapter notificationSettingsListAdapter;
    private PreferredLanguagesAdapter preferredLanguagesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_preferences_tab_fragment, container, false);

        subscriptionSettingsList = new ArrayList<>();
        notificationSettingsList = new ArrayList<>();
        languagesList = new ArrayList<>();

        showMoreFollowedTopicsTextView = (TextView) view.findViewById(R.id.showMoreFollowedTopicsTextView);
        addTopicsBtn = (TextView) view.findViewById(R.id.addTopicsBtn);
        saveTextView = (TextView) view.findViewById(R.id.saveTextView);
        flowLayout = (FlowLayout) view.findViewById(R.id.flowLayout);
        subscriptionSettingRecyclerView = (RecyclerView) view.findViewById(R.id.subscriptionSettingRecyclerView);
        notificationSettingRecyclerView = (RecyclerView) view.findViewById(R.id.notificationSettingRecyclerView);
        preferredLanguageRecyclerView = (RecyclerView) view.findViewById(R.id.prefLanguagesRecyclerView);

        addTopicsBtn.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        showMoreFollowedTopicsTextView.setOnClickListener(this);

        final LinearLayoutManager emailLayoutManager = new LinearLayoutManager(getActivity());
        emailLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        subscriptionSettingsListAdapter = new EmailSubscriptionAdapter(getActivity(), subscriptionSettingsList);
        subscriptionSettingRecyclerView.setLayoutManager(emailLayoutManager);
        subscriptionSettingRecyclerView.setAdapter(subscriptionSettingsListAdapter);

        final LinearLayoutManager notificationLayoutManager = new LinearLayoutManager(getActivity());
        notificationLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notificationSettingsListAdapter = new NotificationSubscriptionAdapter(getActivity(), notificationSettingsList);
        notificationSettingRecyclerView.setLayoutManager(notificationLayoutManager);
        notificationSettingRecyclerView.setAdapter(notificationSettingsListAdapter);

        final LinearLayoutManager languageLayoutManager = new LinearLayoutManager(getActivity());
        languageLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        preferredLanguagesAdapter = new PreferredLanguagesAdapter(getActivity(), languagesList);
        preferredLanguageRecyclerView.setLayoutManager(languageLayoutManager);
        preferredLanguageRecyclerView.setAdapter(preferredLanguagesAdapter);

        checkNotificationsStatus();
        checkSubscriptionStatus();
        checkLanguageSubscriptionStatus();
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

    private void checkLanguageSubscriptionStatus() {
        showProgressDialog("Please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LanguageSettingsAPI languageSettingsAPI = retrofit.create(LanguageSettingsAPI.class);
        Call<LanguageSettingsResponse> call = languageSettingsAPI.getLanguagesList();
        call.enqueue(languageSettingsResponseCallback);
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
                SubscriptionSettingsResponse responseData = (SubscriptionSettingsResponse) response.body();
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
                FollowUnfollowCategoriesResponse responseData = (FollowUnfollowCategoriesResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    mDatalist = (ArrayList<String>) responseData.getData();
                    try {
                        FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        FollowTopics[] res = new Gson().fromJson(fileContent, FollowTopics[].class);
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
//                    mDatalistAdapter mDatalistAdapter = new mDatalistAdapter(mDatalistingActivity.this);
//                    mDatalistView.setAdapter(mDatalistAdapter);
//                    mDatalistAdapter.setData(followedSubSubTopicList);
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
                        boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(getActivity(), AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, response.body());
                        Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            FollowTopics[] res = new Gson().fromJson(fileContent, FollowTopics[].class);
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
        Log.d("cttbhtbtbtb", "btrdsefafs");
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
        for (int i = 0; i < followedSubSubTopicList.size(); i++) {
            followedSubSubTopicList.get(i).setIsSelected(true);
            final LinearLayout subsubLL = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.topic_follow_unfollow_item, null);
            final TextView catTextView = ((TextView) subsubLL.getChildAt(0));
            catTextView.setText(followedSubSubTopicList.get(i).getDisplay_name());
            catTextView.setSelected(true);
            subsubLL.setTag(followedSubSubTopicList.get(i));

            flowLayout.addView(subsubLL);
            subsubLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Topics top = (Topics) subsubLL.getTag();
                    if (top.isSelected()) {
                        top.setIsSelected(false);
                        catTextView.setSelected(false);
//                        }
                    } else {
                        top.setIsSelected(true);
                        catTextView.setSelected(true);
                    }
                }
            });
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
                FollowUnfollowCategoriesResponse responseData = (FollowUnfollowCategoriesResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    SharedPrefUtils.setFollowedTopicsCount(getActivity(), responseData.getData().size());
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
                    NotificationSettingsResponse responseData = (NotificationSettingsResponse) response.body();
                    if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                        if (null != getActivity()) {
                            Toast.makeText(getActivity(), "Notification settings updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (null != getActivity()) {
                            Toast.makeText(getActivity(), "Error while updating notification settings", Toast.LENGTH_SHORT).show();
                            ;
                        }
                    }
                } catch (Exception e) {
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "Error while updating notification settings", Toast.LENGTH_SHORT).show();
                        ;
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
                    ;
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
                    Log.d("GTM Subscription Added", ":" + subscriptionSettingsList.get(i).getDisplayName());
                    Utils.pushEventSubscriptionSettings(getActivity(), GTMEventType.EMAIL_SUBSCRIBE_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
                            "Subscription Settings", subscriptionSettingsList.get(i).getName());
                } else {
                    Log.d("GTM Subscription Remove", ":" + subscriptionSettingsList.get(i).getDisplayName());
                    Utils.pushEventSubscriptionSettings(getActivity(), GTMEventType.EMAIL_UNSUBSCRIBE_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
                            "Subscription Settings", subscriptionSettingsList.get(i).getName());
                }
            }
            subscriptionSettingsList.get(i).setOriginalStatus(subscriptionSettingsList.get(i).getStatus());
        }

    }

    private Callback<SubscriptionSettingsResponse> updateSubscriptionsReponseListener = new Callback<SubscriptionSettingsResponse>() {
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
            map.put(languagesList.get(i).getName(), languagesList.get(i).getStatus());
        }

        languageUpdateRequest.setLangSubscription(map);

        Call<UpdateLanguageSettingsResponse> call = languageSettingsAPI.updatePreferredLanguages(languageUpdateRequest);
        call.enqueue(updateLanguageSettingsCallback);

        for (int i = 0; i < languagesList.size(); i++) {
            if (!languagesList.get(i).getStatus().equals(languagesList.get(i).getOriginalStatus())) {
                if ("1".equals(languagesList.get(i).getStatus())) {
                    Log.d("GTM Launguage Added", ":" + languagesList.get(i).getName());
                    Utils.pushEventFeedLanguage(getActivity(), GTMEventType.FEED_LANGUAGE_ADD_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
                            "Launguage Settings", languagesList.get(i).getName());
                } else {
                    Log.d("GTM Launguage Remove", ":" + languagesList.get(i).getName());
                    Utils.pushEventFeedLanguage(getActivity(), GTMEventType.FEED_LANGUAGE_REMOVE_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
                            "Launguage Settings", languagesList.get(i).getName());
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
                UpdateLanguageSettingsResponse responseData = (UpdateLanguageSettingsResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "Language content settings updated successfully", Toast.LENGTH_SHORT).show();
                        UserInfo userInfo = SharedPrefUtils.getUserDetailModel(getActivity());
                        userInfo.setIsLangSelection("1");
                        SharedPrefUtils.setUserDetailModel(getActivity(), userInfo);

                        Map<String, String> subscribedContentLanguages = responseData.getData();
                        String filter = "0";


                        FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.LANGUAGES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                                fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                                }.getType()
                        );

                        for (Map.Entry<String, String> entry : subscribedContentLanguages.entrySet()) {
                            if ("1".equals(entry.getValue())) {

                                for (Map.Entry<String, LanguageConfigModel> langEntry : retMap.entrySet()) {
                                    if (entry.getKey().equalsIgnoreCase(langEntry.getValue().getName())) {
                                        filter = filter + "," + langEntry.getKey();
                                    }
                                }
                            }
                        }
                        SharedPrefUtils.setLanguageFilters(getActivity(), filter);
                        BaseApplication.setHasLanguagePreferrenceChanged(true);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
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
        isSubsequentCall = true;
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

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showMoreFollowedTopicsTextView:
                flowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                showMoreFollowedTopicsTextView.setVisibility(View.GONE);
                break;
            case R.id.addTopicsBtn:
                break;
            case R.id.saveTextView:
                updateNotificationSettings();
                updateSubscriptions();
                updateLanguageSubscription();
                updateFollowedUnfollowedTopics();
                break;
        }
    }
}
