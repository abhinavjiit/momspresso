package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.GroupsCategoryMappingResult;
import com.mycity4kids.newmodels.SelectTopic;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.AddArticleTopicsPagerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 17/7/17.
 */
public class GroupCategoriesSelectionActivity extends BaseActivity implements View.OnClickListener {

    private String userId;
    private ArrayList<SelectTopic> selectTopic;
    private ArrayList<String> previouslyFollowedTopics = new ArrayList<>();
    private ArrayList<Topics> chosenTopicsList = new ArrayList<>();
    int tabPos;
    private HashMap<String, Topics> selectedTopicsMap;
    private ArrayList<String> updateTopicList;
    private HashMap<Topics, List<Topics>> topicsMap;
    private ArrayList<Topics> topicList;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private TextView applyTextView;
    private ArrayList<GroupsCategoryMappingResult> groupCategoriesList;
    private AddArticleTopicsPagerAdapter adapter;
    private ViewPager viewPager;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_article_topics_activity_new);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        Utils.pushOpenScreenEvent(this, "FollowTopicScreen", userId + "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        applyTextView = (TextView) findViewById(R.id.applyTextView);
//        cancelTextView = (TextView) findViewById(R.id.cancelTextView);
        viewPager = (ViewPager) findViewById(R.id.pager);

        applyTextView.setOnClickListener(this);
//        cancelTextView.setOnClickListener(this);

        tabPos = getIntent().getIntExtra("tabPos", 0);
        groupCategoriesList = getIntent().getParcelableArrayListExtra("groupTaggedCategories");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectedTopicsMap = new HashMap<>();

//        showProgressDialog("Getting selected topics");
        populateTopicsList();
    }

    private void populateTopicsList() {
        try {
            FileInputStream fileInputStream = this.openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
//            FollowTopics[] res = new Gson().fromJson(fileContent, FollowTopics[].class);
            TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
            createTopicsData(res);
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

//            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
//            call.enqueue(downloadCategoriesJSONCallback);
            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();

            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(GroupCategoriesSelectionActivity.this, AppConstants.CATEGORIES_JSON_FILE, response.body());

                    try {
                        FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                        createTopicsData(res);
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
        }
    }

    private void createTopicsData(TopicsResponse responseData) {
        try {
//            progressBar.setVisibility(View.GONE);
            topicsMap = new HashMap<Topics, List<Topics>>();
            topicList = new ArrayList<>();

            for (int i = 0; i < responseData.getData().size(); i++) {
                ArrayList<Topics> secondLevelLeafNodeList = new ArrayList<>();

                for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                    ArrayList<Topics> thirdLevelLeafNodeList = new ArrayList<>();

                    for (int k = 0; k < responseData.getData().get(i).getChild().get(j).getChild().size(); k++) {

                        if ("1".equals(responseData.getData().get(i).getChild().get(j).getChild().get(k).getPublicVisibility())) {
                            //Adding All sub-subcategories
                            responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentId(responseData.getData().get(i).getId());
                            responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentName(responseData.getData().get(i).getTitle());
                            thirdLevelLeafNodeList.add(responseData.getData().get(i).getChild().get(j).getChild().get(k));
                        }
                    }

                    responseData.getData().get(i).getChild().get(j).setChild(thirdLevelLeafNodeList);
                }

                for (int k = 0; k < responseData.getData().get(i).getChild().size(); k++) {
                    if ("1".equals(responseData.getData().get(i).getChild().get(k).getPublicVisibility())) {
                        //Adding All subcategories
                        responseData.getData().get(i).getChild().get(k)
                                .setParentId(responseData.getData().get(i).getId());
                        responseData.getData().get(i).getChild().get(k)
                                .setParentName(responseData.getData().get(i).getTitle());
                        secondLevelLeafNodeList.add(responseData.getData().get(i).getChild().get(k));
                    }
                }

                if ("1".equals(responseData.getData().get(i).getPublicVisibility()) && !AppConstants.SHORT_STORY_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                    responseData.getData().get(i).setChild(secondLevelLeafNodeList);
                    if (!secondLevelLeafNodeList.isEmpty()) {
                        topicList.add(responseData.getData().get(i));
                        topicsMap.put(responseData.getData().get(i), secondLevelLeafNodeList);
                    }
                }

            }

            if (null != groupCategoriesList && !groupCategoriesList.isEmpty()) {
                retainItemsFromReminaingList(groupCategoriesList);
            }
            createTopicsTabPages();
        } catch (Exception e) {
//            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            showToast(getString(R.string.went_wrong));
        }
    }

    private int retainItemsFromReminaingList(ArrayList<GroupsCategoryMappingResult> list) {
        int totalSelectedItems = 0;
        Iterator it = topicsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<Topics> tList = ((ArrayList) pair.getValue());

            for (int j = 0; j < tList.size(); j++) {

                //subcategories with no child
                if (tList.get(j).getChild().size() == 0) {
                    System.out.println(tList.get(j).getTitle() + " = ");
                    tList.get(j).setIsSelected(false);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getCategoryId().equals(tList.get(j).getId())) {
                            tList.get(j).setIsSelected(true);
                            totalSelectedItems++;
                        }
                    }
                    continue;
                }

                //subcategories children
                for (int k = 0; k < tList.get(j).getChild().size(); k++) {
                    System.out.println(tList.get(j).getChild().get(k).getTitle() + " = ");
                    tList.get(j).getChild().get(k).setIsSelected(false);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getCategoryId().equals(tList.get(j).getChild().get(k).getId())) {
                            tList.get(j).getChild().get(k).setIsSelected(true);
                            totalSelectedItems++;
                        }
                    }
                }
            }
        }
        return totalSelectedItems;
    }

    private void createTopicsTabPages() {
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        for (int i = 0; i < topicList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(topicList.get(i).getDisplay_name()));
        }
        AppUtils.changeTabsFont(tabLayout);
        adapter = new AddArticleTopicsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), topicList);
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

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.applyTextView:
                getSelectedTopicsFromList();
                if (chosenTopicsList.size() == 0) {
                    showToast(getString(R.string.add_article_topics_min_topics));
                    return;
                } else if (chosenTopicsList.size() > 8) {
                    showToast(getString(R.string.add_article_topics_max_topics));
                    return;
                }
                Intent intent = getIntent();
                intent.putParcelableArrayListExtra("updatedTopicList", chosenTopicsList);
                setResult(RESULT_OK, intent);
//                createTagObjectFromList();
            case R.id.cancelTextView:
                finish();
                break;
        }
    }

    private void getSelectedTopicsFromList() {
        chosenTopicsList.clear();
        for (int i = 0; i < topicList.size(); i++) {
            for (int j = 0; j < topicList.get(i).getChild().size(); j++) {
                for (int k = 0; k < topicList.get(i).getChild().get(j).getChild().size(); k++) {
                    if (topicList.get(i).getChild().get(j).getChild().get(k).isSelected()) {
                        chosenTopicsList.add(topicList.get(i).getChild().get(j).getChild().get(k));
                    }
                }
            }
        }
    }

    private Callback<FollowUnfollowCategoriesResponse> followUnfollowCategoriesResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    SharedPrefUtils.setFollowedTopicsCount(BaseApplication.getAppContext(), responseData.getData().size());
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
