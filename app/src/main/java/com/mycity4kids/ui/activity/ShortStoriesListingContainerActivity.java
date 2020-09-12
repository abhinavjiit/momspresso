package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.TopicsShortStoriesPagerAdapter;
import com.mycity4kids.ui.fragment.ShortStoryChallengeListingTabFragment;
import com.mycity4kids.ui.fragment.TopicsShortStoriesTabFragment;
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
public class ShortStoriesListingContainerActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TopicsShortStoriesPagerAdapter pagerAdapter;
    private ArrayList<Topics> shortStoriesTopicList;
    private String parentTopicId;
    private ArrayList<Topics> subTopicsList;
    private Toolbar toolbar;
    private TextView toolbarTitleTextView;
    private LinearLayout layoutBottomSheet;
    private LinearLayout bottomSheet;
    private ImageView imageSortBy;
    private FloatingActionButton fabAddShortStory;
    private CoordinatorLayout root;
    private String selectedTabCategoryId;
    private int selectedTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_listing_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        layoutBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheet = layoutBottomSheet.findViewById(R.id.bottom_sheet);
        fabAddShortStory = findViewById(R.id.fabAddShortStory);
        bottomSheet.setVisibility(View.GONE);
        fabAddShortStory.setVisibility(View.VISIBLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        toolbarTitleTextView = (TextView) findViewById(R.id.toolbarTitleTextView);
        imageSortBy = (ImageView) findViewById(R.id.imageSortBy);

        imageSortBy.setOnClickListener(view -> {
            Fragment fragment = pagerAdapter.getCurrentFragment();
            if (fragment != null && fragment instanceof TopicsShortStoriesTabFragment) {
                ((TopicsShortStoriesTabFragment) fragment).showSortedByDialog();
            }
        });

        parentTopicId = getIntent().getStringExtra("parentTopicId");
        selectedTabCategoryId = getIntent().getStringExtra("selectedTabCategoryId");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fabAddShortStory.setOnClickListener(view -> {
            Intent intent = new Intent(ShortStoriesListingContainerActivity.this,
                    ChooseShortStoryCategoryActivity.class);
            intent.putExtra("source", "storyListingCard");
            startActivity(intent);
        });

        toolbarTitleTextView.setText(getString(R.string.article_listing_type_short_story_label));
        try {
            shortStoriesTopicList = BaseApplication.getShortStoryTopicList();

            if (shortStoriesTopicList == null) {
                FileInputStream fileInputStream = BaseApplication.getAppContext()
                        .openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                createTopicsData(res);
            }
            getCurrentParentTopicCategoriesAndSubCategories();
            initializeTabsAndPager();
        } catch (FileNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsApi = retro.create(TopicsCategoryAPI.class);
            Call<ResponseBody> caller = topicsApi.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE,
                            response.body());
                    try {
                        FileInputStream fileInputStream = BaseApplication.getAppContext()
                                .openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                        createTopicsData(res);
                        getCurrentParentTopicCategoriesAndSubCategories();
                        initializeTabsAndPager();
                    } catch (FileNotFoundException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4KException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4KException", Log.getStackTraceString(t));
                }
            });
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }
        AppUtils.deleteDirectoryContent();
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
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(subTopicsList.get(i).getDisplay_name());
            tab.setTag(subTopicsList.get(i).getId());
            tabLayout.addTab(tab);
            if (subTopicsList.get(i).getId().equals(selectedTabCategoryId)) {
                selectedTab = i;
            }
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        AppUtils.changeTabsFont(tabLayout);

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new TopicsShortStoriesPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),
                subTopicsList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    if (tab.getTag().toString().equals(AppConstants.SHORT_STORY_CHALLENGE_ID)) {
                        Utils.shareEventTracking(ShortStoriesListingContainerActivity.this, "100WS listing",
                                "Story_Challenges_Android", "SS_Show_SCListing_Challenge");
                    }
                } catch (Exception e) {

                }
                viewPager.setCurrentItem(tab.getPosition());
                Fragment fragment = pagerAdapter.getItem(tab.getPosition());
                if (fragment != null) {
                    if (fragment instanceof ShortStoryChallengeListingTabFragment) {
                        imageSortBy.setVisibility(View.GONE);
                        fabAddShortStory.setVisibility(View.GONE);
                    } else {

                        imageSortBy.setVisibility(View.VISIBLE);
                        fabAddShortStory.setVisibility(View.VISIBLE);

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
        viewPager.setCurrentItem(selectedTab);
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
                        if ("1".equals(responseData.getData().get(i).getChild().get(k).getShowInMenu())
                                || AppConstants.SHORT_STORY_CHALLENGE_ID
                                .equals(responseData.getData().get(i).getChild().get(k).getId())) {
                            responseData.getData().get(i).getChild().get(k)
                                    .setParentId(responseData.getData().get(i).getId());
                            responseData.getData().get(i).getChild().get(k)
                                    .setParentName(responseData.getData().get(i).getTitle());
                            if (responseData.getData().get(i).getChild().get(k).getChild().isEmpty()) {
                                Topics dupChildTopic = new Topics();
                                dupChildTopic.setChild(new ArrayList<Topics>());
                                dupChildTopic.setId(responseData.getData().get(i).getChild().get(k).getId());
                                dupChildTopic
                                        .setIsSelected(responseData.getData().get(i).getChild().get(k).isSelected());
                                dupChildTopic
                                        .setParentId(responseData.getData().get(i).getChild().get(k).getParentId());
                                dupChildTopic.setDisplay_name(
                                        responseData.getData().get(i).getChild().get(k).getDisplay_name());
                                dupChildTopic
                                        .setParentName(responseData.getData().get(i).getChild().get(k).getParentName());
                                dupChildTopic.setPublicVisibility(
                                        responseData.getData().get(i).getChild().get(k).getPublicVisibility());
                                dupChildTopic
                                        .setShowInMenu(responseData.getData().get(i).getChild().get(k).getShowInMenu());
                                dupChildTopic.setSlug(responseData.getData().get(i).getChild().get(k).getSlug());
                                dupChildTopic.setTitle(responseData.getData().get(i).getChild().get(k).getTitle());
                                ArrayList<Topics> duplicateEntry = new ArrayList<Topics>();
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
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void getCurrentParentTopicCategoriesAndSubCategories() {
        for (int i = 0; i < shortStoriesTopicList.size(); i++) {
            subTopicsList = new ArrayList<>();
            //Selected topic is Main Category
            if (parentTopicId.equals(shortStoriesTopicList.get(i).getId())) {
                subTopicsList.addAll(shortStoriesTopicList.get(i).getChild());
                Utils.pushViewTopicArticlesEvent(this, "TopicShortStoryListingScreen",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "",
                        shortStoriesTopicList.get(i).getId() + "~" + shortStoriesTopicList.get(i).getDisplay_name());
                return;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TopicListingFragment", "onStop");
        try {
            TopicsShortStoriesTabFragment topicsShortStoriesTabFragment = ((TopicsShortStoriesTabFragment) pagerAdapter
                    .instantiateItem(viewPager, viewPager.getCurrentItem()));
            topicsShortStoriesTabFragment.stopTracking();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
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
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

    }
}
