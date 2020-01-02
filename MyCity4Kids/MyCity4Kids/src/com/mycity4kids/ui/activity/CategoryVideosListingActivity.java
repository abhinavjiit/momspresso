package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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
import com.mycity4kids.models.request.VlogsEventRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.adapter.VideoTopicsPagerAdapter;
import com.mycity4kids.ui.fragment.CategoryVideosTabFragment;
import com.mycity4kids.ui.fragment.ChallengeCategoryVideoTabFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/5/17.
 */
public class CategoryVideosListingActivity extends BaseActivity implements View.OnClickListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    FrameLayout topLayerGuideLayout;
    private VideoTopicsPagerAdapter pagerAdapter;
    HashMap<Topics, List<Topics>> allTopicsMap;
    private ArrayList<Topics> allTopicsList;
    private String parentTopicId;
    private ArrayList<Topics> subTopicsList;
    Toolbar toolbar;
    TextView toolbarTitleTextView;
    public ImageView imageSortBy;
    private FloatingActionButton fabAdd;
    private CoordinatorLayout root;
    private RelativeLayout momVlogCoachMark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_listing_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        momVlogCoachMark = (RelativeLayout) findViewById(R.id.momVlogCoachMark);
        momVlogCoachMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                momVlogCoachMark.setVisibility(View.GONE);
                fabAdd.setVisibility(View.VISIBLE);
                SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "Mom_vlog", true);
            }
        });

        if (!SharedPrefUtils.isCoachmarksShownFlag(CategoryVideosListingActivity.this, "Mom_vlog")) {
            momVlogCoachMark.setVisibility(View.VISIBLE);


        } else {
            fabAdd.setVisibility(View.VISIBLE);
            momVlogCoachMark.setVisibility(View.GONE);

        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        topLayerGuideLayout = (FrameLayout) findViewById(R.id.topLayerGuideLayout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        toolbarTitleTextView = (TextView) findViewById(R.id.toolbarTitleTextView);
        imageSortBy = (ImageView) findViewById(R.id.imageSortBy);
        imageSortBy.setVisibility(View.GONE);

        imageSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = pagerAdapter.getCurrentFragment();//.get(viewPager.getCurrentItem());
                if (fragment != null && fragment instanceof CategoryVideosTabFragment) {
                    ((CategoryVideosTabFragment) fragment).showSortedByDialog();
                }
            }
        });
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cityIntent = new Intent(CategoryVideosListingActivity.this, ChooseVideoCategoryActivity.class);
                cityIntent.putExtra("comingFrom", "createDashboardIcon");

                startActivity(cityIntent);
                Utils.momVlogEvent(CategoryVideosListingActivity.this, "Video Listing", "FAB_create", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_video_creation_categories", "", "");
                fireEventForVideoCreationIntent();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        parentTopicId = getIntent().getStringExtra("parentTopicId");

        toolbarTitleTextView.setText(getString(R.string.myprofile_section_videos_label));
        Utils.pushOpenScreenEvent(this, "TopicArticlesListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        try {

            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
            TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
            createTopicsData(res);
            getCurrentParentTopicCategoriesAndSubCategories();
            initializeUI();
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
                        initializeUI();
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

    private void fireEventForVideoCreationIntent() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        VlogsEventRequest vlogsEventRequest = new VlogsEventRequest();
        vlogsEventRequest.setCreatedTime(System.currentTimeMillis());
        vlogsEventRequest.setKey(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        vlogsEventRequest.setTopic("create_video_fab");
        vlogsEventRequest.setPayload(vlogsEventRequest.getPayload());
        Call<ResponseBody> call = vlogsListingAndDetailsAPI.addVlogsCreateIntentEvent(vlogsEventRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
    }

    private void initializeUI() {
        Topics mainTopic = new Topics();
        mainTopic.setId(null);
        String allCategoryLabel = "";
        allCategoryLabel = getString(R.string.article_listing_type_trending_label);
        mainTopic.setDisplay_name(allCategoryLabel);
        mainTopic.setTitle(allCategoryLabel);

        subTopicsList.add(0, mainTopic);
        for (int i = 0; i < subTopicsList.size(); i++) {
            Log.d("VIDEO TOPIC", subTopicsList.get(i).getDisplay_name() + "  --  " + subTopicsList.get(i).getId());
            tabLayout.addTab(tabLayout.newTab().setText(subTopicsList.get(i).getDisplay_name()));
        }


        AppUtils.changeTabsFont(tabLayout);

        pagerAdapter = new VideoTopicsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), subTopicsList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Utils.momVlogEvent(CategoryVideosListingActivity.this, "Video Listing", "Vlogs_Tab", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_vlogs_tab", subTopicsList.get(tab.getPosition()).getId(), "");

                Fragment fragment = pagerAdapter.getItem(tab.getPosition());
                if (fragment != null) {
                    if (fragment instanceof ChallengeCategoryVideoTabFragment) {
                        fabAdd.setVisibility(View.GONE);
                        imageSortBy.setVisibility(View.GONE);
                    } else {
                        fabAdd.setVisibility(View.VISIBLE);
                        imageSortBy.setVisibility(View.GONE);
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
            allTopicsMap = new HashMap<Topics, List<Topics>>();
            allTopicsList = new ArrayList<>();

            //Prepare structure for multi-expandable listview.
            for (int i = 0; i < responseData.getData().size(); i++) {
                ArrayList<Topics> tempUpList = new ArrayList<>();
                if (AppConstants.HOME_VIDEOS_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                    for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                        ArrayList<Topics> tempList = new ArrayList<>();
                        for (int k = 0; k < responseData.getData().get(i).getChild().get(j).getChild().size(); k++) {
                            //Adding All sub-subcategories
                            responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentId(responseData.getData().get(i).getId());
                            responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentName(responseData.getData().get(i).getTitle());
                            tempList.add(responseData.getData().get(i).getChild().get(j).getChild().get(k));
                        }
                        responseData.getData().get(i).getChild().get(j).setChild(tempList);
                    }

                    for (int k = 0; k < responseData.getData().get(i).getChild().size(); k++) {
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
                responseData.getData().get(i).setChild(tempUpList);

                allTopicsList.add(responseData.getData().get(i));
                allTopicsMap.put(responseData.getData().get(i), tempUpList);
            }
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
//                subTopicsList.addAll(allTopicsList.get(i).getChild());
                for (int j = 0; j < allTopicsList.get(i).getChild().size(); j++) {
                    if ("1".equals(allTopicsList.get(i).getChild().get(j).getShowInMenu())) {
                        subTopicsList.add(allTopicsList.get(i).getChild().get(j));
                    }
                }
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
    public void onClick(View view) {
        switch (view.getId()) {

        }

    }


}
