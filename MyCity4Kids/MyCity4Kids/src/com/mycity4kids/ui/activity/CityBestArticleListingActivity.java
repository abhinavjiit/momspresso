package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.widget.FeedNativeAd;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 4/8/16.
 */
public class CityBestArticleListingActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, FeedNativeAd.AdLoadingListener, MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    //    MainArticleListingAdapter articlesListingAdapter;
    private MainArticleRecyclerViewAdapter recyclerAdapter;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
//    ListView listView;

    private int sortType = 0;
    private int nextPageNumber;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private int limit = 15;
    private String fromScreen;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private Toolbar mToolbar;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
//    private SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout sortBgLayout;
    private RelativeLayout bottomOptionMenu;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton popularSortFAB, recentSortFAB, fabSort;
    private RecyclerView recyclerView;
    private FeedNativeAd feedNativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.best_in_your_city_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setCityNameAsTitle();

        fromScreen = getIntent().getStringExtra(Constants.FROM_SCREEN);

//        listView = (ListView) findViewById(R.id.scroll);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));
//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        sortBgLayout = (FrameLayout) findViewById(R.id.sortBgLayout);
        bottomOptionMenu = (RelativeLayout) findViewById(R.id.bottomOptionMenu);

        sortBgLayout.setVisibility(View.GONE);
        bottomOptionMenu.setVisibility(View.GONE);

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

//        swipeRefreshLayout.setOnRefreshListener(CityBestArticleListingActivity.this);
        progressBar.setVisibility(View.VISIBLE);

        articleDataModelsNew = new ArrayList<ArticleListingResult>();
        nextPageNumber = 1;
        hitBestofCityArticleListingApi(sortType);

//        articlesListingAdapter = new MainArticleListingAdapter(this);
//        articlesListingAdapter.setListingType("Best of City Listing");
//        articlesListingAdapter.setNewListData(articleDataModelsNew);
//        listView.setAdapter(articlesListingAdapter);
//        articlesListingAdapter.notifyDataSetChanged();

        feedNativeAd = new FeedNativeAd(this, this, AppConstants.FB_AD_PLACEMENT_ARTICLE_LISTING);
        feedNativeAd.loadAds();
        recyclerAdapter = new MainArticleRecyclerViewAdapter(this, feedNativeAd, this, false, "City Best");
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setNewListData(articleDataModelsNew);
        recyclerView.setAdapter(recyclerAdapter);


//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
//                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
//                    mLodingView.setVisibility(View.VISIBLE);
//                    hitBestofCityArticleListingApi(sortType);
//                    isReuqestRunning = true;
//                }
//            }
//        });

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
                            hitBestofCityArticleListingApi(sortType);
                        }
                    }
                }
            }
        });


//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                Intent intent = new Intent(CityBestArticleListingActivity.this, ArticleDetailsContainerActivity.class);
//                if (adapterView.getAdapter() instanceof MainArticleListingAdapter) {
//                    ArticleListingResult parentingListData = (ArticleListingResult) adapterView.getAdapter().getItem(i);
//                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
//                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
//                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getImageUrl());
//                    intent.putExtra(Constants.BLOG_SLUG, parentingListData.getBlogPageSlug());
//                    intent.putExtra(Constants.TITLE_SLUG, parentingListData.getTitleSlug());
//                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, Constants.KEY_IN_YOUR_CITY + "~" + SharedPrefUtils.getCurrentCityModel(CityBestArticleListingActivity.this).getName());
//                    intent.putExtra(Constants.FROM_SCREEN, "BestOfCityScreen");
//                    intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
//                    intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
//                    intent.putExtra(Constants.AUTHOR, parentingListData.getUserId() + "~" + parentingListData.getUserName());
//                    startActivity(intent);
//
//                }
//            }
//        });
    }

    private void setCityNameAsTitle() {
        if (SharedPrefUtils.getCurrentCityModel(this).getName().isEmpty()) {
            switch (SharedPrefUtils.getCurrentCityModel(this).getId()) {
                case 1:
                    setTitle("Best of " + "Delhi-NCR");
                    break;
                case 2:
                    setTitle("Best of " + "Bangalore");
                    break;
                case 3:
                    setTitle("Best of " + "Mumbai");
                    break;
                case 4:
                    setTitle("Best of " + "Pune");
                    break;
                case 5:
                    setTitle("Best of " + "Hyderabad");
                    break;
                case 6:
                    setTitle("Best of " + "Chennai");
                    break;
                case 7:
                    setTitle("Best of " + "Kolkata");
                    break;
                case 8:
                    setTitle("Best of " + "Jaipur");
                    break;
                case 9:
                    setTitle("Best of " + "Ahmedabad");
                    break;
                default:
                    setTitle("Best of " + "Delhi-NCR");
                    break;
            }

        } else {
            if (SharedPrefUtils.getCurrentCityModel(this).getName().equals("Delhi-Ncr")) {
                SharedPrefUtils.getCurrentCityModel(this).setName("Delhi-NCR");
            }
            getSupportActionBar().setTitle("Best of " + SharedPrefUtils.getCurrentCityModel(this).getName());
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    private void hitBestofCityArticleListingApi(int sortType) {
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
        Call<ArticleListingResponse> filterCall = topicsAPI.getBestArticlesForCity("" + SharedPrefUtils.getCurrentCityModel(this).getId(), sortType, from, from + limit - 1, SharedPrefUtils.getLanguageFilters(this));
        filterCall.enqueue(articleListingResponseCallback);

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
//            swipeRefreshLayout.setRefreshing(false);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.search:
                startContextualSearch();
                break;
        }
        return true;
    }

    private void startContextualSearch() {
        Intent intent = new Intent(this, SearchAllActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
//            swipeRefreshLayout.setRefreshing(false);
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        nextPageNumber = 1;
        hitBestofCityArticleListingApi(sortType);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recentSortFAB:
                Utils.pushSortListingEvent(CityBestArticleListingActivity.this, GTMEventType.SORT_LISTING_EVENT, SharedPrefUtils.getUserDetailModel(CityBestArticleListingActivity.this).getDynamoId(),
                        "Best of City Listing", "recent");
                sortBgLayout.setVisibility(View.GONE);
                fabMenu.collapse();
                articleDataModelsNew.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitBestofCityArticleListingApi(0);
                break;
            case R.id.popularSortFAB:
                Utils.pushSortListingEvent(CityBestArticleListingActivity.this, GTMEventType.SORT_LISTING_EVENT, SharedPrefUtils.getUserDetailModel(CityBestArticleListingActivity.this).getDynamoId(),
                        "Best of City Listing", "popular");
                sortBgLayout.setVisibility(View.GONE);
                fabMenu.collapse();
                articleDataModelsNew.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitBestofCityArticleListingApi(1);
                break;
        }
    }

    @Override
    public void onFinishToLoadAds() {

    }

    @Override
    public void onErrorToLoadAd() {

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        Intent intent = new Intent(CityBestArticleListingActivity.this, ArticleDetailsContainerActivity.class);
        intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
        intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
        intent.putExtra(Constants.ARTICLE_COVER_IMAGE, articleDataModelsNew.get(position).getImageUrl());
        intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
        intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
        intent.putExtra(Constants.ARTICLE_OPENED_FROM, Constants.KEY_IN_YOUR_CITY + "~" + SharedPrefUtils.getCurrentCityModel(CityBestArticleListingActivity.this).getName());
        intent.putExtra(Constants.FROM_SCREEN, "BestOfCityScreen");
        intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
        intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
        intent.putExtra(Constants.AUTHOR, articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
        startActivity(intent);
    }
}
