package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
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
import com.mycity4kids.ui.adapter.MainArticleListingAdapter;
import com.mycity4kids.ui.fragment.FilterTopicsDialogFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
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

public class FilteredTopicsArticleListingActivity extends BaseActivity implements OnClickListener, SwipeRefreshLayout.OnRefreshListener, FilterTopicsDialogFragment.OnTopicsSelectionComplete {

    private MainArticleListingAdapter articlesListingAdapter;
    private ListView listView;
    private Menu menu;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private RelativeLayout mLodingView;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int nextPageNumber;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filtered_topics_articles_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Topics");
        Utils.pushOpenScreenEvent(FilteredTopicsArticleListingActivity.this, "Topic Articles List", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        listView = (ListView) findViewById(R.id.scroll);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        sortingLayout = (LinearLayout) findViewById(R.id.sortingLayout);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        sortTextView = (TextView) findViewById(R.id.sortTextView);
        filterTextView = (TextView) findViewById(R.id.filterTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        followUnfollowTextView = (TextView) findViewById(R.id.followUnfollowTextView);

        bottomOptionMenu = (RelativeLayout) findViewById(R.id.bottomOptionMenu);
        bottomMenuRecentSort = (TextView) findViewById(R.id.recentSort);
        bottomMenuPopularSort = (TextView) findViewById(R.id.popularSort);
//        bottomUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_from_bottom);
//        bottomDown = AnimationUtils.loadAnimation(this, R.anim.slide_down_to_bottom);
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
                TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
                createTopicsData(res);
            }

            getTopicLevelAndPrepareFilterData();

//            if (AppConstants.TOPIC_LEVEL_SUB_SUB_CATEGORY.equals(topicLevel) ||
//                    AppConstants.MOMSPRESSO_CATEGORYID.equals(selectedTopics) ||
//                    isLanguageListing) {
            sortBgLayout.setVisibility(View.GONE);
            bottomOptionMenu.setVisibility(View.GONE);
//            } else {
//                frameLayout.setVisibility(View.GONE);
//                fabMenu.setVisibility(View.GONE);
//                fabSort.setVisibility(View.GONE);
//                popularSortFAB.setVisibility(View.GONE);
//                recentSortFAB.setVisibility(View.GONE);
//            }
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
            call.enqueue(downloadCategoriesJSONCallback);
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
                getSupportActionBar().setTitle(displayName.toUpperCase());
            } else {
                getSupportActionBar().setTitle("");
            }
            titleTextView.setText(displayName);
        }
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) FilteredTopicsArticleListingActivity.this);
        progressBar.setVisibility(View.VISIBLE);

        articleDataModelsNew = new ArrayList<ArticleListingResult>();
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(sortType);

        articlesListingAdapter = new MainArticleListingAdapter(this);
        articlesListingAdapter.setNewListData(articleDataModelsNew);
        articlesListingAdapter.setListingType(listingType);
        listView.setAdapter(articlesListingAdapter);
        articlesListingAdapter.notifyDataSetChanged();

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

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;
            private boolean mIsScrollingUp;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final ListView lw = listView;

                if (view.getId() == lw.getId()) {
                    final int currentFirstVisibleItem = lw.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        mIsScrollingUp = false;
                        showBottomMenu();
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        hideBottomMenu();
                        mIsScrollingUp = true;
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    hitFilteredTopicsArticleListingApi(sortType);
                    isReuqestRunning = true;
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(FilteredTopicsArticleListingActivity.this, ArticleDetailsContainerActivity.class);
                if (adapterView.getAdapter() instanceof MainArticleListingAdapter) {
                    ArticleListingResult parentingListData = (ArticleListingResult) ((MainArticleListingAdapter) adapterView.getAdapter()).getItem(i);
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getImageUrl());
                    intent.putExtra(Constants.BLOG_SLUG, parentingListData.getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, parentingListData.getTitleSlug());
                    if (StringUtils.isNullOrEmpty(categoryName)) {
                        categoryName = displayName;
                    }
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, categoryName + "~" + selectedTopics);
                    intent.putExtra(Constants.FROM_SCREEN, "Topic Articles List");
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
                    intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
                    startActivity(intent);
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
                Utils.pushOpenArticleListingEvent(this, GTMEventType.ARTICLE_LISTING_CLICK_EVENT, fromScreen, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), displayName + "~" + selectedTopics, "" + nextPageNumber);
                filterCall = topicsAPI.getArticlesForCategory(selectedTopics, sortType, from, from + limit - 1, "");
            } else if (isLanguageListing) {
                Utils.pushOpenArticleListingEvent(this, GTMEventType.ARTICLE_LISTING_CLICK_EVENT, fromScreen, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), categoryName + "~" + selectedTopics, "" + nextPageNumber);
                filterCall = topicsAPI.getArticlesForCategory(selectedTopics, sortType, from, from + limit - 1, "");
            } else {
                Utils.pushOpenArticleListingEvent(this, GTMEventType.ARTICLE_LISTING_CLICK_EVENT, fromScreen, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), displayName + "~" + selectedTopics, "" + nextPageNumber);
                filterCall = topicsAPI.getArticlesForCategory(selectedTopics, sortType, from, from + limit - 1, SharedPrefUtils.getLanguageFilters(this));
            }
//            Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(selectedTopics, sortType, from, from + limit - 1, SharedPrefUtils.getLanguageFilters(this));
            filterCall.enqueue(articleListingResponseCallback);
        } else {
            Utils.pushOpenArticleListingEvent(this, GTMEventType.ARTICLE_LISTING_CLICK_EVENT, fromScreen, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), displayName + "~" + selectedTopics, "" + nextPageNumber);
            Call<ArticleListingResponse> filterCall = topicsAPI.getFilteredArticlesForCategories(filteredTopics, sortType, from, from + limit - 1, SharedPrefUtils.getLanguageFilters(this));
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
                showToast("Something went wrong from server");
                return;
            }
            swipeRefreshLayout.setRefreshing(false);
            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
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
                noBlogsTextView.setText("No articles found");
                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recentSort:
            case R.id.recentSortFAB:
                Utils.pushSortListingEvent(FilteredTopicsArticleListingActivity.this, GTMEventType.SORT_LISTING_EVENT, SharedPrefUtils.getUserDetailModel(FilteredTopicsArticleListingActivity.this).getDynamoId(),
                        listingType, "recent");
                sortBgLayout.setVisibility(View.GONE);
                fabMenu.collapse();
                articleDataModelsNew.clear();
                articlesListingAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(0);
                break;
            case R.id.popularSort:
            case R.id.popularSortFAB:
                Utils.pushSortListingEvent(FilteredTopicsArticleListingActivity.this, GTMEventType.SORT_LISTING_EVENT, SharedPrefUtils.getUserDetailModel(FilteredTopicsArticleListingActivity.this).getDynamoId(),
                        listingType, "popular");
                sortBgLayout.setVisibility(View.GONE);
                fabMenu.collapse();
                articleDataModelsNew.clear();
                articlesListingAdapter.notifyDataSetChanged();
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
//                try {
//                    allTopicsList = BaseApplication.getTopicList();
//                    allTopicsMap = BaseApplication.getTopicsMap();
//
//                    if (allTopicsList == null || allTopicsMap == null) {
//                        FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
//                        String fileContent = convertStreamToString(fileInputStream);
//                        TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
//                        createTopicsData(res);
//                    }
//
//                    getTopicLevelAndPrepareFilterData();
//                openFilterDialog();
//                } catch (FileNotFoundException e) {
//                    Crashlytics.logException(e);
//                    Log.d("FileNotFoundException", Log.getStackTraceString(e));
//                    Retrofit retro = BaseApplication.getInstance().getRetrofit();
//                    final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
//
//                    Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
//                    call.enqueue(downloadCategoriesJSONCallback);
//                }
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
    protected void updateUi(Response response) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        getMenuInflater().inflate(R.menu.menu_filter_article_by_topics, menu);
        this.menu = menu;
//        menu.getItem(0).setEnabled(false);
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
                if (AppConstants.TOPIC_LEVEL_SUB_CATEGORY.equals(topicLevel) || AppConstants.TOPIC_LEVEL_MAIN_CATEGORY.equals(topicLevel)) {
//                    titleTextView.setVisibility(View.VISIBLE);
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

        Call<TopicsFollowingStatusResponse> callBookmark = topicFollowingStatusAPI.checkTopicsFollowingStatus(SharedPrefUtils.getUserDetailModel(this).getDynamoId(),
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
                showToast("Something went wrong from server");
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);

                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

                Call<ResponseBody> caller = topicsAPI.downloadFileWithDynamicUrlSync(jsonObject.getJSONObject("data").getJSONObject("result").getJSONObject("category").getString("location"));

                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                        Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                        try {
//                            Topics t = new Topics();
//                            t.setId("");
//                            t.setDisplay_name("");
//                            SharedPrefUtils.setMomspressoCategory(FilteredTopicsArticleListingActivity.this, t);

                            FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                            String fileContent = convertStreamToString(fileInputStream);
                            TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
                            createTopicsData(res);
                            getTopicLevelAndPrepareFilterData();
//                            if (AppConstants.TOPIC_LEVEL_SUB_SUB_CATEGORY.equals(topicLevel)) {
                            sortBgLayout.setVisibility(View.GONE);
                            bottomOptionMenu.setVisibility(View.GONE);
//                            } else {
//                                frameLayout.setVisibility(View.GONE);
//                                fabMenu.setVisibility(View.GONE);
//                                fabSort.setVisibility(View.GONE);
//                                popularSortFAB.setVisibility(View.GONE);
//                                recentSortFAB.setVisibility(View.GONE);
//                            }
//                            openFilterDialog();
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
                showToast("Something went wrong from server");
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
                        boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                        Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                            String fileContent = convertStreamToString(fileInputStream);
                            FollowTopics[] res = new Gson().fromJson(fileContent, FollowTopics[].class);
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

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = openFileOutput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, Context.MODE_PRIVATE);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("TopicsFilterActivity", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private Callback<TopicsFollowingStatusResponse> isTopicFollowedResponseCallback = new Callback<TopicsFollowingStatusResponse>() {
        @Override
        public void onResponse(Call<TopicsFollowingStatusResponse> call, retrofit2.Response<TopicsFollowingStatusResponse> response) {
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }

            TopicsFollowingStatusResponse responseData = (TopicsFollowingStatusResponse) response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                followingTopicStatus = responseData.getData().getStatus();
                if ("0".equals(followingTopicStatus)) {
//                    menu.getItem(0).setEnabled(true);
//                    menu.getItem(0).setTitle("FOLLOW");
                    followUnfollowTextView.setText("FOLLOW");
                    isTopicFollowed = 0;
                } else {
//                    menu.getItem(0).setEnabled(true);
//                    menu.getItem(0).setTitle("FOLLOWING");
                    followUnfollowTextView.setText("FOLLOWING");
                    isTopicFollowed = 1;
                }
            } else {
                showToast(getString(R.string.server_went_wrong));
            }
        }

        @Override
        public void onFailure(Call<TopicsFollowingStatusResponse> call, Throwable t) {

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
        Intent intent = new Intent(this, ContextualSearchActivity.class);
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
//            Utils.pushEventFollowUnfollowTopic(this, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "CategoriesArticleList", "follow", displayName + ":" + selectedTopics);
            Utils.pushTopicFollowUnfollowEvent(this, GTMEventType.FOLLOW_TOPIC_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Topic Articles List", displayName + "~" + selectedTopics);
//            menu.getItem(0).setTitle("FOLLOWING");
            followUnfollowTextView.setText("FOLLOWING");
            isTopicFollowed = 1;
        } else {
            Log.d("GTM UNFOLLOW", displayName + ":" + selectedTopics);
//            Utils.pushEventFollowUnfollowTopic(this, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "CategoriesArticleList", "follow", displayName + ":" + selectedTopics);
            Utils.pushTopicFollowUnfollowEvent(this, GTMEventType.UNFOLLOW_TOPIC_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Topic Articles List", displayName + "~" + selectedTopics);
//            menu.getItem(0).setTitle("FOLLOW");
            followUnfollowTextView.setText("FOLLOW");
            isTopicFollowed = 0;
        }
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.followCategories(SharedPrefUtils.getUserDetailModel(this).getDynamoId(), followUnfollowCategoriesRequest);
        call.enqueue(followUnfollowCategoriesResponseCallback);
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
                FollowUnfollowCategoriesResponse responseData = (FollowUnfollowCategoriesResponse) response.body();
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

        isLastPageReached = false;
        nextPageNumber = 1;
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

    private void hideBottomMenu() {
        bottomOptionMenu.animate()
                .translationY(bottomOptionMenu.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        getSupportActionBar().hide();
                    }
                });
    }

    private void showBottomMenu() {
        bottomOptionMenu.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
//                        getSupportActionBar().show();
                    }
                });
    }

    private void createTopicsData(TopicsResponse responseData) {
        try {
            progressBar.setVisibility(View.GONE);
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

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

                    if ("1".equals(responseData.getData().get(i).getShowInMenu())) {
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

//                    if ("1".equals(responseData.getData().get(i).getPublicVisibility())) {
                    allTopicsList.add(responseData.getData().get(i));
                    allTopicsMap.put(responseData.getData().get(i), tempUpList);
//                    }
                }
                BaseApplication.setTopicList(allTopicsList);
                BaseApplication.setTopicsMap(allTopicsMap);
            } else {
                showToast(getString(R.string.server_error));
            }
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

}