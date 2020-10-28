package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.FollowTopics;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;
import com.mycity4kids.newmodels.SelectTopic;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.SubscribeTopicsTabAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by' hemant on 17/7/17.
 */
public class SubscribeTopicsActivity extends BaseActivity implements View.OnClickListener {

    private String userId;
    private ArrayList<SelectTopic> selectTopic;
    private ArrayList<String> previouslyFollowedTopics = new ArrayList<>();
    private ArrayList<Topics> previouslyFollowedTopicsObjects = new ArrayList<>();
    private int tabPos;
    private int followTopicChangeNewUser = 0;
    private MixpanelAPI mixpanel;

    private HashMap<String, Topics> selectedTopicsMap;
    private HashMap<String, Topics> initSelectedTopicsMap;
    private ArrayList<String> updateTopicList;
    private ArrayList<String> followCategoriesArrayList;
    private ArrayList<String> unfollowCategoriesArrayList;

    private Toolbar toolbar;
    //    private TabLayout tabLayout;
    private TextView saveTextView, cancelTextView;

    private ArrayList<String> categoryIdList;
    private SubscribeTopicsTabAdapter searchTopicsSplashAdapter;
    private ListView popularTopicsListView;

    private String source;
    private String screen = "other";
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_topics_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        Utils.pushOpenScreenEvent(this, "FollowTopicScreen", userId + "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        saveTextView = (TextView) findViewById(R.id.saveTextView);
        cancelTextView = (TextView) findViewById(R.id.cancelTextView);
        popularTopicsListView = (ListView) findViewById(R.id.popularTopicsListView);

        saveTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        tabPos = getIntent().getIntExtra("tabPos", 0);

        categoryIdList = getIntent().getStringArrayListExtra("selectedTopicList");

        if (categoryIdList == null) {
            categoryIdList = new ArrayList<>();
        }
        source = getIntent().getStringExtra("source");

        if ("home".equals(source)) {
            screen = "FollowTopicScreen";
        } else if ("settings".equals(source)) {
            screen = "UserProfileSettingScreen";
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectedTopicsMap = new HashMap<>();
        initSelectedTopicsMap = new HashMap<>();

        showProgressDialog("Getting selected topics");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.getFollowedCategories(userId);
        call.enqueue(getFollowedTopicsResponseCallback);
    }

    private Callback<FollowUnfollowCategoriesResponse> getFollowedTopicsResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call,
                retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (responseData.getData() == null) {
                        previouslyFollowedTopics = new ArrayList<>();
                    } else {
                        previouslyFollowedTopics = (ArrayList<String>) responseData.getData();
                    }
                    populateTopicsList();
                } else {
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void populateTopicsList() {
        try {
            FileInputStream fileInputStream = this.openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
            FollowTopics[] res = gson.fromJson(fileContent, FollowTopics[].class);
            createTopicsData(res);
        } catch (FileNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));

            showProgressDialog("Please wait");
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
            call.enqueue(downloadCategoriesJSONCallback);
        }
    }

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

                Retrofit retro = BaseApplication.getInstance().getConfigurableTimeoutRetrofit(3);
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                String popularURL = jsonObject.getJSONObject("data").getJSONObject("result").getJSONObject("category")
                        .getString("popularLocation");
                Call<ResponseBody> caller = topicsAPI.downloadTopicsListForFollowUnfollow(popularURL);

                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        removeProgressDialog();
                        boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(SubscribeTopicsActivity.this,
                                AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, response.body());
                        Log.d("TopicsSplashActivity", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = openFileInput(
                                    AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory())
                                    .create();
                            FollowTopics[] res = gson.fromJson(fileContent, FollowTopics[].class);
                            createTopicsData(res);
                        } catch (FileNotFoundException e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        removeProgressDialog();
                        FirebaseCrashlytics.getInstance().recordException(t);
                        Log.d("MC4KException", Log.getStackTraceString(t));
                    }
                });
            } catch (Exception e) {
                removeProgressDialog();
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void createTopicsData(FollowTopics[] responseData) {
        try {
            selectTopic = new ArrayList<>();
//            topicsSplashAdapter = new TopicsSplashAdapter(this, selectedTopicsMap, selectTopic);
//            popularTopicsListView.setAdapter(topicsSplashAdapter);
            for (int i = 0; i < responseData.length; i++) {
                //Main Category Parent Details
                SelectTopic st = new SelectTopic();
                st.setId(responseData[i].getId());
                st.setDisplayName(responseData[i].getDisplay_name());
//                st.setBackgroundImageUrl(responseData[i].getExtraData().getCategoryBackImage().getApp());
                ArrayList<Topics> topicLL = new ArrayList<>();
                for (int j = 0; j < responseData[i].getChild().size(); j++) {
                    for (int k = 0; k < previouslyFollowedTopics.size(); k++) {
                        if (responseData[i].getChild().get(j).getId().equals(previouslyFollowedTopics.get(k))) {
                            //highlight previously selected topics in the current data.
                            selectedTopicsMap
                                    .put(responseData[i].getChild().get(j).getId(), responseData[i].getChild().get(j));
                            initSelectedTopicsMap
                                    .put(responseData[i].getChild().get(j).getId(), responseData[i].getChild().get(j));
                            responseData[i].getChild().get(j).setIsSelected(true);
                        }
                    }
                    ArrayList<Topics> tempList = new ArrayList<>();
                    topicLL.add(responseData[i].getChild().get(j));
                    responseData[i].getChild().get(j).setChild(tempList);
                }
                if (topicLL.isEmpty()) {
                    //do not add any category with empty childs
                    continue;
                }
                st.setChildTopics(topicLL);
                selectTopic.add(st);
            }
            BaseApplication.setSelectedTopicsMap(selectedTopicsMap);
//            processTrendingResponse();
            ArrayList<SelectTopic> filteredTopicList = new ArrayList<>();
            for (int i = 0; i < categoryIdList.size(); i++) {
                for (int j = 0; j < selectTopic.size(); j++) {
                    if (categoryIdList.get(i).equals(selectTopic.get(j).getId())) {
                        filteredTopicList.add(selectTopic.get(j));
                    }
                }
            }
            ArrayList<Topics> otherTopicsChildList = new ArrayList<>();
            SelectTopic selectTopicNew = new SelectTopic();
            selectTopicNew.setDisplayName("OTHERS");
            for (int i = 0; i < selectTopic.size(); i++) {
                if (categoryIdList.contains(selectTopic.get(i).getId())) {

                } else {
                    if (selectTopic.get(i).getChildTopics() != null) {
                        otherTopicsChildList.addAll(selectTopic.get(i).getChildTopics());
                    }
                }
            }
            selectTopicNew.setChildTopics(otherTopicsChildList);
            filteredTopicList.add(selectTopicNew);
            searchTopicsSplashAdapter = new SubscribeTopicsTabAdapter(this, filteredTopicList,
                    BaseApplication.getSelectedTopicsMap(), 0);
            popularTopicsListView.setAdapter(searchTopicsSplashAdapter);
            if (BaseApplication.getSelectedTopicsMap() == null || BaseApplication.getSelectedTopicsMap().isEmpty()) {
                saveTextView.setEnabled(false);
            }
//            processTrendingResponse();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
//            showToast(getString(R.string.went_wrong));
        }
    }

    private void processTrendingResponse() {
//        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
//        for (int i = 0; i < selectTopic.size(); i++) {
//            tabLayout.addTab(tabLayout.newTab().setText(selectTopic.get(i).getDisplayName()));
//        }
//        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
//        final SubscribeTopicsPagerAdapter adapter = new SubscribeTopicsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), selectTopic, previouslyFollowedTopics);
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//        viewPager.setCurrentItem(tabPos);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.saveTextView:
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    jsonObject.put("ScreenName", screen);
                    mixpanel.track("SaveTopicSelection", jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ArrayList<Topics> topicsList = new ArrayList<>();
                if (BaseApplication.getSelectedTopicsMap().values() != null
                        && BaseApplication.getSelectedTopicsMap().values().size() != 0) {
                    topicsList.addAll(BaseApplication.getSelectedTopicsMap().values());
                } else {
                    populateTopicsList();
                    topicsList.addAll(BaseApplication.getSelectedTopicsMap().values());
                }

                Set<String> updateSet = new HashSet<>();
                Set<String> followCategories = new HashSet<>();
                Set<String> unfollowCategories = new HashSet<>();

                //create datalist for updating the topics at backend
                //need to remove the previously selected topics (if they are not unselected)
                //as the API is toggle functionality and if the previously selected items are unselected add them to the dataset.
                for (int i = 0; i < topicsList.size(); i++) {
                    updateSet.add(topicsList.get(i).getId());
                    followCategories.add(topicsList.get(i).getId());
                }

                for (int i = 0; i < previouslyFollowedTopics.size(); i++) {
                    if (!updateSet.contains(previouslyFollowedTopics.get(i))) {
                        updateSet.add(previouslyFollowedTopics.get(i));
                        unfollowCategories.add(previouslyFollowedTopics.get(i));
                    } else {
                        updateSet.remove(previouslyFollowedTopics.get(i));
                        followCategories.remove(previouslyFollowedTopics.get(i));
                    }
                }
                updateTopicList = new ArrayList<>(updateSet);
                followCategoriesArrayList = new ArrayList<>(followCategories);
                unfollowCategoriesArrayList = new ArrayList<>(unfollowCategories);

                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
                FollowUnfollowCategoriesRequest followUnfollowCategoriesRequest = new FollowUnfollowCategoriesRequest();

                followUnfollowCategoriesRequest.setCategories(updateTopicList);
                Call<FollowUnfollowCategoriesResponse> categoriesResponseCall =
                        topicsCategoryAPI.followCategories(userId, followUnfollowCategoriesRequest);
                categoriesResponseCall.enqueue(followUnfollowCategoriesResponseCallback);

                break;
            case R.id.cancelTextView:
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    jsonObject.put("ScreenName", screen);
                    mixpanel.track("CancelTopicSelection", jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
                break;
        }
    }

    private Callback<FollowUnfollowCategoriesResponse> followUnfollowCategoriesResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call,
                retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                if (SharedPrefUtils.getFollowTopicApproachChangeFlag(BaseApplication.getAppContext())) {
                    followTopicChangeNewUser = 1;
                }
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    try {
                        for (int i = 0; i < followCategoriesArrayList.size(); i++) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("userId",
                                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                                                .getDynamoId());
                                jsonObject.put("Topic", followCategoriesArrayList.get(i) + "~" + selectedTopicsMap
                                        .get(followCategoriesArrayList.get(i)).getDisplay_name().toUpperCase());
                                jsonObject.put("ScreenName", screen);
                                jsonObject.put("isFirstTimeUser", followTopicChangeNewUser);
                                Log.d("FollowTopics", jsonObject.toString());
                                mixpanel.track("FollowTopic", jsonObject);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        for (int i = 0; i < unfollowCategoriesArrayList.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("userId",
                                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                            jsonObject.put("Topic", unfollowCategoriesArrayList.get(i) + "~" + initSelectedTopicsMap
                                    .get(unfollowCategoriesArrayList.get(i)).getDisplay_name().toUpperCase());
                            jsonObject.put("ScreenName", screen);
                            jsonObject.put("isFirstTimeUser", followTopicChangeNewUser);
                            Log.d("UnfollowTopics", jsonObject.toString());
                            mixpanel.track("UnfollowTopic", jsonObject);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SharedPrefUtils.setTopicSelectionChanged(BaseApplication.getAppContext(), true);
                    showToast(getString(R.string.subscribe_topics_toast_topic_updated));
                    Intent intent = getIntent();
                    intent.putStringArrayListExtra("updatedTopicList", updateTopicList);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            jsonObject.put("ScreenName", screen);
            Log.d("CancelTopicSelection", jsonObject.toString());
            mixpanel.track("CancelTopicSelection", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void topicSelectionChanged() {
        if (BaseApplication.getSelectedTopicsMap() == null || BaseApplication.getSelectedTopicsMap().isEmpty()) {
            saveTextView.setEnabled(false);
        } else {
            saveTextView.setEnabled(true);
        }
    }
}
