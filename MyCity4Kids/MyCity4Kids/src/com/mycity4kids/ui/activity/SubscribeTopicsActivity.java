package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
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
import com.mycity4kids.ui.adapter.SubscribeTopicsPagerAdapter;
import com.mycity4kids.utils.AppUtils;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 17/7/17.
 */
public class SubscribeTopicsActivity extends BaseActivity implements View.OnClickListener {

    private String userId;
    private ArrayList<SelectTopic> selectTopic;
    private ArrayList<String> previouslyFollowedTopics = new ArrayList<>();
    int tabPos;
    private HashMap<String, Topics> selectedTopicsMap;
    private ArrayList<String> updateTopicList;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private TextView saveTextView, cancelTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_topics_activity);

        userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        Utils.pushOpenScreenEvent(this, "Dashboard Fragment", userId + "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        saveTextView = (TextView) findViewById(R.id.saveTextView);
        cancelTextView = (TextView) findViewById(R.id.cancelTextView);

        saveTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        tabPos = getIntent().getIntExtra("tabPos", 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectedTopicsMap = new HashMap<>();

        showProgressDialog("Getting selected topics");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.getFollowedCategories(userId);
        call.enqueue(getFollowedTopicsResponseCallback);
    }

    private Callback<FollowUnfollowCategoriesResponse> getFollowedTopicsResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
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
                    if (responseData.getData() == null) {
                        previouslyFollowedTopics = new ArrayList<>();
                    } else {
                        previouslyFollowedTopics = (ArrayList<String>) responseData.getData();
                    }
                    populateTopicsList();
                } else {
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

    private void populateTopicsList() {
        try {
            FileInputStream fileInputStream = this.openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            FollowTopics[] res = new Gson().fromJson(fileContent, FollowTopics[].class);
            createTopicsData(res);
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
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
                String popularURL = jsonObject.getJSONObject("data").getJSONObject("result").getJSONObject("category").getString("popularLocation");
                Call<ResponseBody> caller = topicsAPI.downloadTopicsListForFollowUnfollow(popularURL);

                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        removeProgressDialog();
                        boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(SubscribeTopicsActivity.this, AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, response.body());
                        Log.d("TopicsSplashActivity", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            FollowTopics[] res = new Gson().fromJson(fileContent, FollowTopics[].class);
                            createTopicsData(res);
                        } catch (FileNotFoundException e) {
                            Crashlytics.logException(e);
                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        removeProgressDialog();
                        Crashlytics.logException(t);
                        Log.d("MC4KException", Log.getStackTraceString(t));
//                        showToast("Something went wrong while downloading topics");

//                        Intent intent = new Intent(TopicsSplashActivity.this, DashboardActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        startActivity(intent);
//                        finish();
                    }
                });
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            removeProgressDialog();
//            showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
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
                st.setBackgroundImageUrl(responseData[i].getExtraData().getCategoryBackImage().getApp());
                ArrayList<Topics> topicLL = new ArrayList<>();
                for (int j = 0; j < responseData[i].getChild().size(); j++) {
                    for (int k = 0; k < previouslyFollowedTopics.size(); k++) {
                        if (responseData[i].getChild().get(j).getId().equals(previouslyFollowedTopics.get(k))) {
                            //highlight previously selected topics in the current data.
                            selectedTopicsMap.put(responseData[i].getChild().get(j).getId(), responseData[i].getChild().get(j));
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
            processTrendingResponse();
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
//            showToast(getString(R.string.went_wrong));
        }
    }

    private void processTrendingResponse() {
//        trendingArraylist.addAll(responseData.getData().get(0).getResult());
//        Collections.shuffle(trendingArraylist);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        for (int i = 0; i < selectTopic.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(selectTopic.get(i).getDisplayName()));
        }
//        changeTabsFont();
//        wrapTabIndicatorToTitle(tabLayout, 25, 25);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final SubscribeTopicsPagerAdapter adapter = new SubscribeTopicsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), selectTopic, previouslyFollowedTopics);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setCurrentItem(tabPos);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.saveTextView:
                ArrayList<Topics> topicsList = new ArrayList<Topics>(BaseApplication.getSelectedTopicsMap().values());

                Set<String> updateSet = new HashSet<>();

                //create datalist for updating the topics at backend
                //need to remove the previously selected topics (if they are not unselected)
                //as the API is toggle functionality and if the previously selected items are unselected add them to the dataset.
                for (int i = 0; i < topicsList.size(); i++) {
                    updateSet.add(topicsList.get(i).getId());
                }
                for (int i = 0; i < previouslyFollowedTopics.size(); i++) {
                    if (!updateSet.contains(previouslyFollowedTopics.get(i))) {
                        updateSet.add(previouslyFollowedTopics.get(i));
                    } else {
                        updateSet.remove(previouslyFollowedTopics.get(i));
                    }
                }
                updateTopicList = new ArrayList<>(updateSet);

                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
                FollowUnfollowCategoriesRequest followUnfollowCategoriesRequest = new FollowUnfollowCategoriesRequest();

                followUnfollowCategoriesRequest.setCategories(updateTopicList);
                Call<FollowUnfollowCategoriesResponse> categoriesResponseCall =
                        topicsCategoryAPI.followCategories(userId, followUnfollowCategoriesRequest);
                categoriesResponseCall.enqueue(followUnfollowCategoriesResponseCallback);
                Log.d("dwad", "dwad");
                break;
            case R.id.cancelTextView:
                finish();
                break;
        }
    }

    private Callback<FollowUnfollowCategoriesResponse> followUnfollowCategoriesResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast("Something went wrong from server");
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    SharedPrefUtils.setFollowedTopicsCount(SubscribeTopicsActivity.this, responseData.getData().size());
                    showToast(getString(R.string.subscribe_topics_toast_topic_updated));
                    Intent intent = getIntent();
                    intent.putStringArrayListExtra("updatedTopicList", updateTopicList);
                    setResult(RESULT_OK, intent);
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
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
}
