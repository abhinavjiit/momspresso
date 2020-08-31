package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.request.VlogsEventRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.adapter.VideoTopicsPagerAdapter;
import com.mycity4kids.ui.fragment.CategoryVideosTabFragment;
import com.mycity4kids.ui.fragment.ChallengeCategoryVideoTabFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.vlogs.VideoCategoryAndChallengeSelectionActivity;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/5/17.
 */
public class CategoryVideosListingActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    FrameLayout topLayerGuideLayout;
    private VideoTopicsPagerAdapter pagerAdapter;
    private ArrayList<Topics> subTopicsList;
    Toolbar toolbar;
    TextView toolbarTitleTextView;
    public ImageView imageSortBy;
    private FloatingActionButton fabAdd;
    private CoordinatorLayout root;
    private ArrayList<Topics> categoriesList;
    private String selectedTabCategoryId;
    private int selectedTabIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_listing_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);
        fabAdd = findViewById(R.id.fabAdd);
        subTopicsList = new ArrayList<>();
        fabAdd.setVisibility(View.VISIBLE);
        getAllMomVlogCategories();
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        topLayerGuideLayout = findViewById(R.id.topLayerGuideLayout);
        viewPager = findViewById(R.id.pager);
        toolbarTitleTextView = findViewById(R.id.toolbarTitleTextView);
        imageSortBy = findViewById(R.id.imageSortBy);
        imageSortBy.setVisibility(View.GONE);

        imageSortBy.setOnClickListener(view -> {
            Fragment fragment = pagerAdapter.getCurrentFragment();
            if (fragment != null && fragment instanceof CategoryVideosTabFragment) {
                ((CategoryVideosTabFragment) fragment).showSortedByDialog();
            }
        });
        fabAdd.setOnClickListener(view -> {
            Intent cityIntent = new Intent(CategoryVideosListingActivity.this,
                    VideoCategoryAndChallengeSelectionActivity.class);
            cityIntent.putExtra("comingFrom", "createDashboardIcon");

            startActivity(cityIntent);
            Utils.momVlogEvent(CategoryVideosListingActivity.this, "Video Listing", "FAB_create", "", "android",
                    SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                    String.valueOf(System.currentTimeMillis()), "Show_video_creation_categories", "", "");
            fireEventForVideoCreationIntent();
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        selectedTabCategoryId = getIntent().getStringExtra("categoryId");
        if (StringUtils.isNullOrEmpty(selectedTabCategoryId)) {
            selectedTabCategoryId = AppConstants.HOME_VIDEOS_CATEGORYID;
        }

        toolbarTitleTextView.setText(getString(R.string.myprofile_section_videos_label));
        Utils.pushOpenScreenEvent(this, "TopicArticlesListingScreen",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
    }

    private void getAllMomVlogCategories() {
        showProgressDialog("Please wait");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryApi = retrofit.create(TopicsCategoryAPI.class);
        Call<Topics> call = topicsCategoryApi
                .momVlogTopics(AppConstants.HOME_VIDEOS_CATEGORYID);
        call.enqueue(new Callback<Topics>() {

            @Override
            public void onResponse(@NonNull Call<Topics> call, @NonNull Response<Topics> response) {
                removeProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    categoriesList = response.body().getChild();
                    Topics topics = new Topics();
                    topics.setId(response.body().getId());
                    topics.setDisplay_name(getString(R.string.all_categories_label));
                    topics.setChild(new ArrayList<>());
                    subTopicsList.add(topics);

                    for (int i = 0; i < categoriesList.size(); i++) {
                        if (("category-eed5fd2777a24bd48ba9a7e1e4dd4b47").equals(categoriesList.get(i).getId())
                                || ("category-958b29175e174f578c2d92a925451d4f").equals(categoriesList.get(i).getId())
                                || ("category-2ce9257cbf4c4794acacacb173feda13").equals(categoriesList.get(i).getId())
                                || ("category-ee7ea82543bd4bc0a8dad288561f2beb")
                                .equals(categoriesList.get(i).getId())) {
                            subTopicsList.add(categoriesList.get(i));
                        }
                    }
                    moveChallengeTabToLast();
                    initializeUI();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Topics> call, @NonNull Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4KException", Log.getStackTraceString(t));
            }
        });
    }

    private void moveChallengeTabToLast() {
        Topics challengeTopic = null;
        int challengeIndex = -1;
        for (int i = 0; i < subTopicsList.size(); i++) {
            if (AppConstants.VIDEO_CHALLENGE_ID.equals(subTopicsList.get(i).getId())) {
                challengeIndex = i;
            }
        }
        if (challengeIndex != -1) {
            challengeTopic = subTopicsList.get(challengeIndex);
            subTopicsList.remove(challengeIndex);
            if (challengeTopic != null) {
                subTopicsList.add(challengeTopic);
            }
        }
    }

    private void fireEventForVideoCreationIntent() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsApi = retrofit.create(VlogsListingAndDetailsAPI.class);
        VlogsEventRequest vlogsEventRequest = new VlogsEventRequest();
        vlogsEventRequest.setCreatedTime(System.currentTimeMillis());
        vlogsEventRequest.setKey(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        vlogsEventRequest.setTopic("create_video_fab");
        vlogsEventRequest.setPayload(vlogsEventRequest.getPayload());
        Call<ResponseBody> call = vlogsListingAndDetailsApi.addVlogsCreateIntentEvent(vlogsEventRequest);
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
        Topics following = new Topics();
        following.setId(null);
        String followingLabel = "";
        followingLabel = getString(R.string.all_following).toUpperCase();
        following.setDisplay_name(followingLabel);
        following.setTitle(followingLabel);
        subTopicsList.add(0, following);

        for (int i = 0; i < subTopicsList.size(); i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(subTopicsList.get(i).getDisplay_name());
            tab.setTag(subTopicsList.get(i).getId());
            tabLayout.addTab(tab);
            if (!StringUtils.isNullOrEmpty(subTopicsList.get(i).getId()) && subTopicsList.get(i).getId()
                    .equals(selectedTabCategoryId)) {
                selectedTabIndex = i;
            }
        }

        AppUtils.changeTabsFontInMomVlog(tabLayout);
        pagerAdapter = new VideoTopicsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), subTopicsList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Utils.momVlogEvent(CategoryVideosListingActivity.this, "Video Listing", "Vlogs_Tab", "", "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Show_vlogs_tab",
                        subTopicsList.get(tab.getPosition()).getId(), "");
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
        viewPager.setCurrentItem(selectedTabIndex);
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
    }
}
