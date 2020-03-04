package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.FollowTopics;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.TopicsFollowingStatusResponse;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.ui.fragment.FilterTopicsDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * @author Hemant Parmar
 */

/*
 * Used for Listing of Topics Article Listing, Language Specific Article Listing, Momspresso Article Listing
 *
 * */

public class FilteredTopicsArticleListingActivity extends BaseActivity implements OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        FilterTopicsDialogFragment.OnTopicsSelectionComplete, /*FeedNativeAd.AdLoadingListener,*/
        MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    private MainArticleRecyclerViewAdapter recyclerAdapter;

    private Menu menu;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private RelativeLayout mLodingView;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int nextPageNumber;
    private Animation slideDownAnim;
    private RelativeLayout bookmarkInfoView;

    private boolean isReuqestRunning = false;
    private ProgressBar progressBar;
    private boolean isLastPageReached = false;
    private TextView noBlogsTextView;
    private TextView sortTextView;
    private TextView filterTextView;

    private Toolbar mToolbar;
    private FrameLayout sortBgLayout;
    private int limit = 15;
    private FloatingActionButton popularSortFAB, recentSortFAB, fabSort;
    private TextView bottomMenuRecentSort, bottomMenuPopularSort;
    private RelativeLayout bottomOptionMenu;
    private TextView titleTextView;
    private TextView followUnfollowTextView;
    private LinearLayout sortingLayout;

    private int sortType = 0;
    private String displayName;
    private String listingType = "";
    private String selectedTopics;
    private String followingTopicStatus = "0";
    private int isTopicFollowed;
    private ArrayList<Topics> allTopicsList;
    private HashMap<Topics, List<Topics>> allTopicsMap;
    private ArrayList<Topics> subAndSubSubTopicsList;

    //    private Animation bottomUp, bottomDown;
    private String filteredTopics;
    private String topicLevel;
    private boolean isLanguageListing;
    private String categoryName;
    private String fromScreen;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RecyclerView recyclerView;
    private TextView toolbarTitle;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filtered_topics_articles_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Topics");
        Utils.pushOpenScreenEvent(FilteredTopicsArticleListingActivity.this, "TopicArticlesListingScreen",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        sortingLayout = (LinearLayout) findViewById(R.id.sortingLayout);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        sortTextView = (TextView) findViewById(R.id.sortTextView);
        filterTextView = (TextView) findViewById(R.id.filterTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        bookmarkInfoView = (RelativeLayout) findViewById(R.id.bookmarkInfoView);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        followUnfollowTextView = (TextView) findViewById(R.id.followUnfollowTextView);

        bottomOptionMenu = (RelativeLayout) findViewById(R.id.bottomOptionMenu);
        bottomMenuRecentSort = (TextView) findViewById(R.id.recentSort);
        bottomMenuPopularSort = (TextView) findViewById(R.id.popularSort);
        sortBgLayout = (FrameLayout) findViewById(R.id.sortBgLayout);
        sortTextView.setOnClickListener(this);
        filterTextView.setOnClickListener(this);
        sortBgLayout.setOnClickListener(this);
        bottomMenuRecentSort.setOnClickListener(this);
        bottomMenuPopularSort.setOnClickListener(this);
        followUnfollowTextView.setOnClickListener(this);
        sortBgLayout.setVisibility(View.GONE);

        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        popularSortFAB = (FloatingActionButton) findViewById(R.id.popularSortFAB);
        recentSortFAB = (FloatingActionButton) findViewById(R.id.recentSortFAB);
        popularSortFAB.setOnClickListener(this);
        recentSortFAB.setOnClickListener(this);
        fabSort = (FloatingActionButton) findViewById(R.id.fabSort);
        fabSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabMenu.isExpanded()) {
                    fabMenu.collapse();
                } else {
                    fabMenu.expand();
                }
            }
        });
        slideDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_down_from_top);
        slideDownAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bookmarkInfoView.setVisibility(View.GONE);
                    }
                }, 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

        selectedTopics = getIntent().getStringExtra("selectedTopics");
        displayName = getIntent().getStringExtra("displayName");
        categoryName = getIntent().getStringExtra("categoryName");
        isLanguageListing = getIntent().getBooleanExtra("isLanguage", false);
        fromScreen = getIntent().getStringExtra(Constants.FROM_SCREEN);

        try {
            allTopicsList = BaseApplication.getTopicList();
            allTopicsMap = BaseApplication.getTopicsMap();

            if (allTopicsList == null || allTopicsMap == null) {
                FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                createTopicsData(res);
            }

            getTopicLevelAndPrepareFilterData();

            sortBgLayout.setVisibility(View.GONE);
            bottomOptionMenu.setVisibility(View.GONE);
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(FilteredTopicsArticleListingActivity.this,
                            AppConstants.CATEGORIES_JSON_FILE, response.body());
                    Log.d("FilteredTopicsArticle", "file download was a success? " + writtenToDisk);

                    try {
                        FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                        createTopicsData(res);
                        getTopicLevelAndPrepareFilterData();
                        sortBgLayout.setVisibility(View.GONE);
                        bottomOptionMenu.setVisibility(View.GONE);
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

        if (AppConstants.MOMSPRESSO_CATEGORYID.equals(selectedTopics)) {
            listingType = Constants.KEY_MOMSPRESSO;
        } else if (isLanguageListing) {
            listingType = categoryName;
        } else {
            listingType = "Topic Articles List";
        }

        if (null != displayName) {
            if (AppConstants.MOMSPRESSO_CATEGORYID.equals(selectedTopics) ||
                    isLanguageListing) {
                toolbarTitle.setText(displayName.toUpperCase());
            } else {
                toolbarTitle.setText("");
            }
            titleTextView.setText(displayName);
        }

        swipeRefreshLayout.setOnRefreshListener(FilteredTopicsArticleListingActivity.this);
        progressBar.setVisibility(View.VISIBLE);

        articleDataModelsNew = new ArrayList<>();
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(sortType);

        recyclerAdapter = new MainArticleRecyclerViewAdapter(this, this, false, selectedTopics + "~" + displayName,
                false);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setNewListData(articleDataModelsNew);
        recyclerView.setAdapter(recyclerAdapter);

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            hitFilteredTopicsArticleListingApi(sortType);
                        }
                    }
                }
            }
        });

    }

    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, getString(R.string.error_network));
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        if (nextPageNumber == 1) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        if (StringUtils.isNullOrEmpty(filteredTopics)) {
            Call<ArticleListingResponse> filterCall;
            if (AppConstants.MOMSPRESSO_CATEGORYID.equals(selectedTopics)) {
                filterCall = topicsAPI.getArticlesForCategory(selectedTopics, sortType, from, from + limit - 1,
                        SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            } else if (isLanguageListing) {
                filterCall = topicsAPI.getArticlesForCategory(selectedTopics, sortType, from, from + limit - 1, "");
            } else {
                filterCall = topicsAPI.getArticlesForCategory(selectedTopics, sortType, from, from + limit - 1,
                        SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            }
            filterCall.enqueue(articleListingResponseCallback);
        } else {
            Call<ArticleListingResponse> filterCall = topicsAPI
                    .getArticlesForCategory(filteredTopics, sortType, from, from + limit - 1,
                            SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
        }
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            progressBar.setVisibility(View.INVISIBLE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            swipeRefreshLayout.setRefreshing(false);
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
                } else {
                    showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            progressBar.setVisibility(View.INVISIBLE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText(getString(R.string.no_articles_found));
                articleDataModelsNew = dataList;
                recyclerAdapter.setNewListData(articleDataModelsNew);
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            recyclerAdapter.setNewListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            recyclerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recentSort:
            case R.id.recentSortFAB:
                Utils.pushSortListingEvent(FilteredTopicsArticleListingActivity.this, GTMEventType.SORT_LISTING_EVENT,
                        SharedPrefUtils.getUserDetailModel(FilteredTopicsArticleListingActivity.this).getDynamoId(),
                        listingType, "recent");
                sortBgLayout.setVisibility(View.GONE);
                fabMenu.collapse();
                articleDataModelsNew.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(0);
                break;
            case R.id.popularSort:
            case R.id.popularSortFAB:
                Utils.pushSortListingEvent(FilteredTopicsArticleListingActivity.this, GTMEventType.SORT_LISTING_EVENT,
                        SharedPrefUtils.getUserDetailModel(FilteredTopicsArticleListingActivity.this).getDynamoId(),
                        listingType, "popular");
                sortBgLayout.setVisibility(View.GONE);
                fabMenu.collapse();
                articleDataModelsNew.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(1);
                break;
            case R.id.sortBgLayout:
                sortBgLayout.setVisibility(View.GONE);
                break;
            case R.id.followUnfollowTextView:
                followUnfollowTopics();
                sortBgLayout.setVisibility(View.GONE);
                break;
            case R.id.sortTextView:
//                sortingLayout.setAnimation(bottomUp);
//                sortingLayout.startAnimation(bottomUp);
                sortBgLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.filterTextView:
                openFilterDialog();
                break;
        }
    }

    public static Animation inFromBottomAnimation(long durationMillis) {
        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        inFromBottom.setDuration(durationMillis);
        return inFromBottom;
    }

    private void openFilterDialog() {
        FilterTopicsDialogFragment filterTopicsDialogFragment = new FilterTopicsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("topicsList", subAndSubSubTopicsList);
        args.putString("topicsLevel", topicLevel);
        filterTopicsDialogFragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        filterTopicsDialogFragment.show(fm, "Filter");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter_article_by_topics, menu);
        this.menu = menu;
        if (!StringUtils.isNullOrEmpty(selectedTopics)) {
            checkFollowingTopicStatus();
        }
        return true;
    }

    private void checkFollowingTopicStatus() {
        try {
            FileInputStream fileInputStream = openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
            String fileContent = convertStreamToString(fileInputStream);
            FollowTopics[] res = new Gson().fromJson(fileContent, FollowTopics[].class);
            if (!checkCurrentCategoryExists(res)) {
                if (AppConstants.TOPIC_LEVEL_SUB_CATEGORY.equals(topicLevel) || AppConstants.TOPIC_LEVEL_MAIN_CATEGORY
                        .equals(topicLevel)) {
                } else {
                    titleTextView.setVisibility(View.GONE);
                }
                followUnfollowTextView.setVisibility(View.GONE);
                return;
            }
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
            call.enqueue(downloadFollowTopicsJSONCallback);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
            call.enqueue(downloadFollowTopicsJSONCallback);
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicFollowingStatusAPI = retro.create(TopicsCategoryAPI.class);

        Call<TopicsFollowingStatusResponse> callBookmark = topicFollowingStatusAPI
                .checkTopicsFollowingStatus(SharedPrefUtils.getUserDetailModel(this).getDynamoId(),
                        selectedTopics);
        callBookmark.enqueue(isTopicFollowedResponseCallback);
    }

    private boolean checkCurrentCategoryExists(FollowTopics[] res) {
        Log.d("cttbhtbtbtb", "btrdsefafs");
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].getChild().size(); j++) {
                if (selectedTopics.equals(res[i].getChild().get(j).getId())) {
                    Log.d("lkkmk", "iuink");
                    return true;
                }
            }
        }
        return false;
    }

    Callback<ResponseBody> downloadCategoriesJSONCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            progressBar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        boolean writtenToDisk = AppUtils
                                .writeResponseBodyToDisk(FilteredTopicsArticleListingActivity.this,
                                        AppConstants.CATEGORIES_JSON_FILE, response.body());
                        Log.d("FilteredTopicsArticle", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                            String fileContent = convertStreamToString(fileInputStream);
                            TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
                            createTopicsData(res);
                            getTopicLevelAndPrepareFilterData();
                            sortBgLayout.setVisibility(View.GONE);
                            bottomOptionMenu.setVisibility(View.GONE);
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
                progressBar.setVisibility(View.GONE);
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void getTopicLevelAndPrepareFilterData() {
        for (int i = 0; i < allTopicsList.size(); i++) {
            subAndSubSubTopicsList = new ArrayList<>();

            //Selected topic is Main Category
            if (selectedTopics.equals(allTopicsList.get(i).getId())) {
                subAndSubSubTopicsList.addAll(allTopicsList.get(i).getChild());
                topicLevel = AppConstants.TOPIC_LEVEL_MAIN_CATEGORY;
                return;
            }

            for (int j = 0; j < allTopicsList.get(i).getChild().size(); j++) {
                if (selectedTopics.equals(allTopicsList.get(i).getChild().get(j).getId())) {
                    //selected topic is Subcategory with no subsubcategories
                    subAndSubSubTopicsList.addAll(allTopicsList.get(i).getChild().get(j).getChild());
                    if (subAndSubSubTopicsList.isEmpty()) {
                        topicLevel = AppConstants.TOPIC_LEVEL_SUB_SUB_CATEGORY;
                    } else {
                        topicLevel = AppConstants.TOPIC_LEVEL_SUB_CATEGORY;
                    }
                    return;
                }
                for (int k = 0; k < allTopicsList.get(i).getChild().get(j).getChild().size(); k++) {
                    if (selectedTopics.equals(allTopicsList.get(i).getChild().get(j).getChild().get(k).getId())) {
                        //selected topic is SubSubcategory
                        subAndSubSubTopicsList.addAll(allTopicsList.get(i).getChild());
                        topicLevel = AppConstants.TOPIC_LEVEL_SUB_SUB_CATEGORY;
                        return;
                    }
                }
            }
        }

    }

    Callback<ResponseBody> downloadFollowTopicsJSONCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            progressBar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);

                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                String popularURL = jsonObject.getJSONObject("data").getJSONObject("result").getJSONObject("category")
                        .getString("popularLocation");
                Call<ResponseBody> caller = topicsAPI.downloadTopicsListForFollowUnfollow(popularURL);

                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        boolean writtenToDisk = AppUtils
                                .writeResponseBodyToDisk(FilteredTopicsArticleListingActivity.this,
                                        AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, response.body());
                        Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = openFileInput(
                                    AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                            String fileContent = convertStreamToString(fileInputStream);
                            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory())
                                    .create();
                            FollowTopics[] res = gson.fromJson(fileContent, FollowTopics[].class);
                            checkCurrentCategoryExists(res);
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
                progressBar.setVisibility(View.GONE);
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private Callback<TopicsFollowingStatusResponse> isTopicFollowedResponseCallback = new Callback<TopicsFollowingStatusResponse>() {
        @Override
        public void onResponse(Call<TopicsFollowingStatusResponse> call,
                retrofit2.Response<TopicsFollowingStatusResponse> response) {
            if (response == null || null == response.body()) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }

            TopicsFollowingStatusResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                followingTopicStatus = responseData.getData().getStatus();
                if ("0".equals(followingTopicStatus)) {
                    followUnfollowTextView.setText(getString(R.string.ad_follow_author));
                    isTopicFollowed = 0;
                } else {
                    followUnfollowTextView.setText(getString(R.string.ad_following_author));
                    isTopicFollowed = 1;
                }
            } else {
                showToast(getString(R.string.server_went_wrong));
            }
        }

        @Override
        public void onFailure(Call<TopicsFollowingStatusResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.followUnfollowButton:
                followUnfollowTopics();
                break;
            case R.id.searchButton:
                startContextualSearch();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void startContextualSearch() {
        Intent intent = new Intent(this, SearchAllActivity.class);
        intent.putExtra(Constants.CATEGORY_ID, selectedTopics);
        startActivity(intent);
    }

    private void followUnfollowTopics() {
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retro.create(TopicsCategoryAPI.class);
        FollowUnfollowCategoriesRequest followUnfollowCategoriesRequest = new FollowUnfollowCategoriesRequest();
        ArrayList<String> topicIdLList = new ArrayList<>();
        topicIdLList.add(selectedTopics);
        followUnfollowCategoriesRequest.setCategories(topicIdLList);
        if (isTopicFollowed == 0) {
            Log.d("GTM FOLLOW", displayName + ":" + selectedTopics);
            Utils.pushTopicFollowUnfollowEvent(this, GTMEventType.FOLLOW_TOPIC_CLICK_EVENT,
                    SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Topic Articles List",
                    displayName + "~" + selectedTopics);
            followUnfollowTextView.setText(getString(R.string.ad_following_author));
            isTopicFollowed = 1;
        } else {
            Log.d("GTM UNFOLLOW", displayName + ":" + selectedTopics);
            Utils.pushTopicFollowUnfollowEvent(this, GTMEventType.UNFOLLOW_TOPIC_CLICK_EVENT,
                    SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Topic Articles List",
                    displayName + "~" + selectedTopics);
            followUnfollowTextView.setText(getString(R.string.ad_follow_author));
            isTopicFollowed = 0;
        }
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI
                .followCategories(SharedPrefUtils.getUserDetailModel(this).getDynamoId(),
                        followUnfollowCategoriesRequest);
        call.enqueue(followUnfollowCategoriesResponseCallback);
    }

    private Callback<FollowUnfollowCategoriesResponse> followUnfollowCategoriesResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call,
                retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

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
    public void onRefresh() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            swipeRefreshLayout.setRefreshing(false);
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        articleDataModelsNew.clear();
        recyclerAdapter.notifyDataSetChanged();
        isLastPageReached = false;
        nextPageNumber = 1;
        progressBar.setVisibility(View.VISIBLE);
        limit = 15;
        hitFilteredTopicsArticleListingApi(sortType);

    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            Crashlytics.logException(e);
            Log.d("IOException", Log.getStackTraceString(e));
        }
        return sb.toString();
    }

    private void createTopicsData(TopicsResponse responseData) {
        try {
            progressBar.setVisibility(View.GONE);
            allTopicsMap = new HashMap<Topics, List<Topics>>();
            allTopicsList = new ArrayList<>();

            //Prepare structure for multi-expandable listview.
            for (int i = 0; i < responseData.getData().size(); i++) {
                ArrayList<Topics> tempUpList = new ArrayList<>();

                for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                    ArrayList<Topics> tempList = new ArrayList<>();
                    for (int k = 0; k < responseData.getData().get(i).getChild().get(j).getChild().size(); k++) {
                        if ("1".equals(
                                responseData.getData().get(i).getChild().get(j).getChild().get(k).getShowInMenu())) {
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

                if ("1".equals(responseData.getData().get(i).getShowInMenu()) && !AppConstants.SHORT_STORY_CATEGORYID
                        .equals(responseData.getData().get(i).getId())) {
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
            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            showToast(getString(R.string.went_wrong));
        }
    }


    @Override
    public void onsSelectionComplete(List<String> topics) {
        nextPageNumber = 1;
        if (null == topics || topics.isEmpty()) {
            filteredTopics = null;
        } else {
            filteredTopics = TextUtils.join(",", topics);
        }
        hitFilteredTopicsArticleListingApi(0);
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            default:
                Intent intent = new Intent(FilteredTopicsArticleListingActivity.this,
                        ArticleDetailsContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
                intent.putExtra(Constants.ARTICLE_COVER_IMAGE, articleDataModelsNew.get(position).getImageUrl());
                intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
                if (StringUtils.isNullOrEmpty(categoryName)) {
                    categoryName = displayName;
                }
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, categoryName + "~" + selectedTopics);
                intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
                intent.putExtra(Constants.AUTHOR,
                        articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position)
                                .getUserName());
                startActivity(intent);
                break;
        }
    }

    public void showBookmarkConfirmationTooltip() {
        bookmarkInfoView.setVisibility(View.VISIBLE);
        bookmarkInfoView.startAnimation(slideDownAnim);
    }
}
