package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
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
import com.mycity4kids.ui.adapter.TopicsPagerAdapter;
import com.mycity4kids.ui.fragment.TopicsArticlesTabFragment;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/5/17.
 */
public class TopicsListingActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FrameLayout topLayerGuideLayout;
    private TopicsPagerAdapter pagerAdapter;
    private HashMap<Topics, List<Topics>> allTopicsMap;
    private ArrayList<Topics> allTopicsList;
    private String parentTopicId;
    private ArrayList<Topics> subTopicsList;
    private Toolbar toolbar;
    private TextView toolbarTitleTextView;
    private LinearLayout layoutBottomSheet, bottom_sheet;
    private BottomSheetBehavior sheetBehavior;
    private TextView textHeaderUpdate, textUpdate;
    public ImageView imageSortBy;
    private CoordinatorLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_listing_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        layoutBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        textHeaderUpdate = layoutBottomSheet.findViewById(R.id.textHeaderUpdate);
        textUpdate = layoutBottomSheet.findViewById(R.id.textUpdate);
        bottom_sheet = layoutBottomSheet.findViewById(R.id.bottom_sheet);

        String isRewardsAdded = SharedPrefUtils.getIsRewardsAdded(TopicsListingActivity.this);
        if (!isRewardsAdded.isEmpty() && isRewardsAdded.equalsIgnoreCase("0")) {
            bottom_sheet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            });

            textUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.campaignEvent(TopicsListingActivity.this, "Rewards 1st screen", "Bottom sheet", "Update", "", "android", SharedPrefUtils.getAppLocale(TopicsListingActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Rewards_Detail");

                    startActivity(new Intent(TopicsListingActivity.this, RewardsContainerActivity.class));
                }
            });

            textHeaderUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.campaignEvent(TopicsListingActivity.this, "Rewards 1st screen", "Bottom sheet", "Update", "", "android", SharedPrefUtils.getAppLocale(TopicsListingActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Rewards_Detail");

                    startActivity(new Intent(TopicsListingActivity.this, RewardsContainerActivity.class));
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottom_sheet.setVisibility(View.GONE);
                }
            }, 10000);
        } else {
            bottom_sheet.setVisibility(View.GONE);
        }

        imageSortBy = (ImageView) findViewById(R.id.imageSortBy);

        imageSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = pagerAdapter.getCurrentFragment();//.get(viewPager.getCurrentItem());
                if (fragment != null && fragment instanceof TopicsArticlesTabFragment) {
                    ((TopicsArticlesTabFragment) fragment).showSortedByDialog();
                }
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        topLayerGuideLayout = (FrameLayout) findViewById(R.id.topLayerGuideLayout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        toolbarTitleTextView = (TextView) findViewById(R.id.toolbarTitleTextView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        parentTopicId = getIntent().getStringExtra("parentTopicId");

        Utils.pushOpenScreenEvent(this, "TopicArticlesListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        try {
            allTopicsList = BaseApplication.getTopicList();
            allTopicsMap = BaseApplication.getTopicsMap();
            if (allTopicsList == null || allTopicsMap == null) {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                createTopicsData(res);
            }
            getCurrentParentTopicCategoriesAndSubCategories();
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
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                // don't forget to add Tab first before measuring..
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int widthS = displayMetrics.widthPixels;
                tabLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int widthT = tabLayout.getMeasuredWidth();

                if (widthS > widthT) {
                    tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    tabLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        });
        AppUtils.changeTabsFont(this, tabLayout);

        pagerAdapter = new TopicsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), subTopicsList);
        viewPager.setAdapter(pagerAdapter);
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

        if (!SharedPrefUtils.isCoachmarksShownFlag(this, "topics_article")) {
            showGuideView();
        }
    }

    private void createTopicsData(TopicsResponse responseData) {
        try {
            allTopicsMap = new HashMap<Topics, List<Topics>>();
            allTopicsList = new ArrayList<>();

            //Prepare structure for multi-expandable listview.
            for (int i = 0; i < responseData.getData().size(); i++) {
                ArrayList<Topics> tempUpList = new ArrayList<>();

                for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                    ArrayList<Topics> tempList = new ArrayList<>();
                    for (int k = 0; k < responseData.getData().get(i).getChild().get(j).getChild().size(); k++) {
                        if ("1".equals(responseData.getData().get(i).getChild().get(j).getChild().get(k).getShowInMenu())) {
                            //Adding All sub-subcategories
                            responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentId(responseData.getData().get(i).getId());
                            responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentName(responseData.getData().get(i).getTitle());
                            tempList.add(responseData.getData().get(i).getChild().get(j).getChild().get(k));
                        }
                    }
                    responseData.getData().get(i).getChild().get(j).setChild(tempList);
                }

                if ("1".equals(responseData.getData().get(i).getShowInMenu()) && !AppConstants.SHORT_STORY_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                    for (int k = 0; k < responseData.getData().get(i).getChild().size(); k++) {
                        if ("1".equals(responseData.getData().get(i).getChild().get(k).getShowInMenu())) {
                            //Adding All subcategories
                            responseData.getData().get(i).getChild().get(k)
                                    .setParentId(responseData.getData().get(i).getId());
                            responseData.getData().get(i).getChild().get(k)
                                    .setParentName(responseData.getData().get(i).getTitle());

                            // create duplicate entry for subcategories with no child
                            if (responseData.getData().get(i).getChild().get(k).getChild().isEmpty()) {
                                ArrayList<Topics> duplicateEntry = new ArrayList<Topics>();
                                //adding exact same object adds the object recursively producing stackoverflow exception when writing for Parcel.
                                //So need to create different object with same params
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

                allTopicsList.add(responseData.getData().get(i));
                allTopicsMap.put(responseData.getData().get(i), tempUpList);
            }
            BaseApplication.setTopicList(allTopicsList);
            BaseApplication.setTopicsMap(allTopicsMap);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void getCurrentParentTopicCategoriesAndSubCategories() {
        for (int i = 0; i < allTopicsList.size(); i++) {
            subTopicsList = new ArrayList<>();
            //Selected topic is Main Category
            if (parentTopicId.equals(allTopicsList.get(i).getId())) {
                subTopicsList.addAll(allTopicsList.get(i).getChild());
                toolbarTitleTextView.setText(allTopicsList.get(i).getDisplay_name());
//                ((DashboardActivity) getActivity()).setDynamicToolbarTitle(allTopicsList.get(i).getDisplay_name());
                Utils.pushViewTopicArticlesEvent(this, "TopicArticlesListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "",
                        allTopicsList.get(i).getId() + "~" + allTopicsList.get(i).getDisplay_name());
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TopicListingFragment", "onDestroy");
    }

    public void showGuideView() {
        TopicsArticlesTabFragment topicsArticlesTabFragment = ((TopicsArticlesTabFragment) pagerAdapter.instantiateItem(viewPager, viewPager.getCurrentItem()));
        topicsArticlesTabFragment.showGuideView();
    }

    public void showGuideTopLayer() {
        topLayerGuideLayout.setVisibility(View.VISIBLE);
    }

    public void hideGuideTopLayer() {
        topLayerGuideLayout.setVisibility(View.GONE);
    }

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
