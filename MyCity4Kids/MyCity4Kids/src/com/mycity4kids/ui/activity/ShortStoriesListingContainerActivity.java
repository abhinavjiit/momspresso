package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.TopicsShortStoriesPagerAdapter;
import com.mycity4kids.ui.fragment.TopicChallengeTabFragment;
import com.mycity4kids.ui.fragment.TopicsShortStoriesTabFragment;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/5/17.
 */
public class ShortStoriesListingContainerActivity extends BaseActivity {

    //    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FrameLayout tablayoutLayer;

    private TopicsShortStoriesPagerAdapter pagerAdapter;

    private ArrayList<Topics> shortStoriesTopicList;
    private String parentTopicId;
    private ArrayList<Topics> subTopicsList;
    private Toolbar toolbar;
    private TextView toolbarTitleTextView;
    private LinearLayout layoutBottomSheet, bottom_sheet;
    private BottomSheetBehavior sheetBehavior;
    private TextView textHeaderUpdate, textUpdate;
    private ImageView imageSortBy;
    private android.support.design.widget.FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_listing_activity);

        layoutBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        textHeaderUpdate = layoutBottomSheet.findViewById(R.id.textHeaderUpdate);
        textUpdate = layoutBottomSheet.findViewById(R.id.textUpdate);
        bottom_sheet = layoutBottomSheet.findViewById(R.id.bottom_sheet);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);

        String isRewardsAdded = SharedPrefUtils.getIsRewardsAdded(ShortStoriesListingContainerActivity.this);
        if (!isRewardsAdded.isEmpty() && isRewardsAdded.equalsIgnoreCase("0")) {
            bottom_sheet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            });

            textUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ShortStoriesListingContainerActivity.this, RewardsContainerActivity.class));
                }
            });

            textHeaderUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ShortStoriesListingContainerActivity.this, RewardsContainerActivity.class));
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottom_sheet.setVisibility(View.GONE);
                    fabAdd.setVisibility(View.VISIBLE);
                }
            }, 10000);
        } else {
            bottom_sheet.setVisibility(View.GONE);
            fabAdd.setVisibility(View.VISIBLE);

        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tablayoutLayer = (FrameLayout) findViewById(R.id.topLayerGuideLayout);
        toolbarTitleTextView = (TextView) findViewById(R.id.toolbarTitleTextView);

        imageSortBy = (ImageView) findViewById(R.id.imageSortBy);

        imageSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = pagerAdapter.getCurrentFragment();//.get(viewPager.getCurrentItem());
                if (fragment != null && fragment instanceof TopicsShortStoriesTabFragment) {
                    ((TopicsShortStoriesTabFragment) fragment).showSortedByDialog();
                }
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShortStoriesListingContainerActivity.this, ChooseVideoCategoryActivity.class);
                startActivity(intent);
            }
        });

        parentTopicId = getIntent().getStringExtra("parentTopicId");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarTitleTextView.setText(getString(R.string.article_listing_type_short_story_label));
        try {
            shortStoriesTopicList = BaseApplication.getShortStoryTopicList();

            if (shortStoriesTopicList == null) {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                createTopicsData(res);
            }
            getCurrentParentTopicCategoriesAndSubCategories();
            initializeTabsAndPager();
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());
                    Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                    try {
                        FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                        createTopicsData(res);
                        getCurrentParentTopicCategoriesAndSubCategories();
                        initializeTabsAndPager();
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

    private void initializeTabsAndPager() {
        if (subTopicsList.size() == 0) {
            Topics mainTopic = new Topics();
            mainTopic.setId(parentTopicId);
            String allCategoryLabel = "";
            allCategoryLabel = getString(R.string.all_categories_label);

            mainTopic.setDisplay_name(allCategoryLabel);
            mainTopic.setTitle(allCategoryLabel);

            Topics childTopic = new Topics();
            childTopic.setId(parentTopicId);
            childTopic.setDisplay_name(allCategoryLabel);
            childTopic.setTitle(allCategoryLabel);

            ArrayList<Topics> aa = new ArrayList<Topics>();
            aa.add(childTopic);
            mainTopic.setChild(aa);
            subTopicsList.add(mainTopic);
        }
        for (int i = 0; i < subTopicsList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(subTopicsList.get(i).getDisplay_name()));
        }
        //  tabLayout.addTab(tabLayout.newTab().setText("Challenges"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        AppUtils.changeTabsFont(this, tabLayout);

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new TopicsShortStoriesPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), subTopicsList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Fragment fragment = pagerAdapter.getItem(tab.getPosition());
                if (fragment != null) {
                    if (fragment instanceof TopicChallengeTabFragment) {
                        fabAdd.setVisibility(View.GONE);
                        imageSortBy.setVisibility(View.GONE);
                    } else {
                        fabAdd.setVisibility(View.VISIBLE);
                        imageSortBy.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void createTopicsData(TopicsResponse responseData) {
        try {
            shortStoriesTopicList = new ArrayList<>();

            //Prepare structure for multi-expandable listview.
            for (int i = 0; i < responseData.getData().size(); i++) {
                ArrayList<Topics> tempUpList = new ArrayList<>();

                if (AppConstants.SHORT_STORY_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                    Topics allStoryModel = new Topics();
                    allStoryModel.setId(responseData.getData().get(i).getId());
                    allStoryModel.setChild(new ArrayList<Topics>());
                    allStoryModel.setIsSelected(responseData.getData().get(i).isSelected());
                    allStoryModel.setParentId(responseData.getData().get(i).getId());
                    allStoryModel.setParentName(responseData.getData().get(i).getDisplay_name());
                    allStoryModel.setPublicVisibility(responseData.getData().get(i).getPublicVisibility());
                    allStoryModel.setSlug(responseData.getData().get(i).getSlug());
                    allStoryModel.setTitle(responseData.getData().get(i).getTitle());
                    String allCategoryLabel;
                    allCategoryLabel = getString(R.string.all_categories_label);
                    allStoryModel.setDisplay_name(allCategoryLabel);
                    allStoryModel.setShowInMenu("1");
                    responseData.getData().get(i).getChild().add(0, allStoryModel);


                    for (int k = 0; k < responseData.getData().get(i).getChild().size(); k++) {

                        if ("1".equals(responseData.getData().get(i).getChild().get(k).getShowInMenu()) || AppConstants.SHORT_STORY_CHALLENGE_ID.equals(responseData.getData().get(i).getChild().get(k).getId())) {
                            responseData.getData().get(i).getChild().get(k)
                                    .setParentId(responseData.getData().get(i).getId());
                            responseData.getData().get(i).getChild().get(k)
                                    .setParentName(responseData.getData().get(i).getTitle());
                            if (responseData.getData().get(i).getChild().get(k).getChild().isEmpty()) {
                                ArrayList<Topics> duplicateEntry = new ArrayList<Topics>();
                                Topics dupChildTopic = new Topics();
                                dupChildTopic.setChild(new ArrayList<Topics>());
                                dupChildTopic.setId(responseData.getData().get(i).getChild().get(k).getId());
                                dupChildTopic.setIsSelected(responseData.getData().get(i).getChild().get(k).isSelected());
                                dupChildTopic.setParentId(responseData.getData().get(i).getChild().get(k).getParentId());
                                dupChildTopic.setDisplay_name(responseData.getData().get(i).getChild().get(k).getDisplay_name());
                                dupChildTopic.setParentName(responseData.getData().get(i).getChild().get(k).getParentName());
                                dupChildTopic.setPublicVisibility(responseData.getData().get(i).getChild().get(k).getPublicVisibility());
                                dupChildTopic.setShowInMenu(responseData.getData().get(i).getChild().get(k).getShowInMenu());
                                dupChildTopic.setSlug(responseData.getData().get(i).getChild().get(k).getSlug());
                                dupChildTopic.setTitle(responseData.getData().get(i).getChild().get(k).getTitle());
                                duplicateEntry.add(dupChildTopic);
                                responseData.getData().get(i).getChild().get(k).setChild(duplicateEntry);
                            }
                            tempUpList.add(responseData.getData().get(i).getChild().get(k));
                        }
                    }
                }
                responseData.getData().get(i).setChild(tempUpList);
                shortStoriesTopicList.add(responseData.getData().get(i));
            }
            BaseApplication.setShortStoryTopicList(shortStoriesTopicList);
        } catch (Exception e) {
//            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
//            showToast(getString(R.string.went_wrong));
        }
    }

    private void getCurrentParentTopicCategoriesAndSubCategories() {
        for (int i = 0; i < shortStoriesTopicList.size(); i++) {
            subTopicsList = new ArrayList<>();
            //Selected topic is Main Category
            if (parentTopicId.equals(shortStoriesTopicList.get(i).getId())) {
                subTopicsList.addAll(shortStoriesTopicList.get(i).getChild());
//                ((DashboardActivity) getActivity()).setDynamicToolbarTitle(shortStoriesTopicList.get(i).getDisplay_name());
                Utils.pushViewTopicArticlesEvent(this, "TopicShortStoryListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "",
                        shortStoriesTopicList.get(i).getId() + "~" + shortStoriesTopicList.get(i).getDisplay_name());
                return;
            }
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TopicListingFragment", "onStop");
        try {
            TopicsShortStoriesTabFragment topicsShortStoriesTabFragment = ((TopicsShortStoriesTabFragment) pagerAdapter.instantiateItem(viewPager, viewPager.getCurrentItem()));
            topicsShortStoriesTabFragment.stopTracking();
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TopicListingFragment", "onDestroy");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}
