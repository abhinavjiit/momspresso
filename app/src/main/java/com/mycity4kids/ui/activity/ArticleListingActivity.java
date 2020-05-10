package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.RecommendationAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 4/8/16.
 */
public class ArticleListingActivity extends BaseActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,  /*FeedNativeAd.AdLoadingListener,*/
        MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    private MainArticleRecyclerViewAdapter recyclerAdapter;
    private ArrayList<ArticleListingResult> articleDataModelsNew;

    private String sortType;
    private int nextPageNumber;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private ProgressBar progressBar;
    private int limit = 15;
    private String chunks = "";
    private String fromScreen;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private Toolbar mToolbar;
    private TextView toolbarTitleTextView;
    private RecyclerView recyclerView;
    private LinearLayout addTopicsLayout;
    private FrameLayout headerArticleCardLayout;
    ShimmerFrameLayout ashimmerFrameLayout;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_listing_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ashimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmer_article_listing);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitleTextView = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
        addTopicsLayout = (LinearLayout) findViewById(R.id.addTopicsLayout);
        headerArticleCardLayout = (FrameLayout) findViewById(R.id.headerArticleView);

        addTopicsLayout.setOnClickListener(this);

        mToolbar.setVisibility(View.VISIBLE);

        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

        sortType = getIntent().getStringExtra(Constants.SORT_TYPE);
        fromScreen = getIntent().getStringExtra(Constants.FROM_SCREEN);

        if (sortType.equals(Constants.KEY_RECENT)) {
            toolbarTitleTextView.setText(getString(R.string.article_listing_toolbar_title_recent));
        } else if (sortType.equals(Constants.KEY_POPULAR)) {
            toolbarTitleTextView.setText(getString(R.string.article_listing_toolbar_title_popular));
        } else if (sortType.equals(Constants.KEY_TRENDING)) {
            toolbarTitleTextView.setText(getString(R.string.article_listing_toolbar_title_trending));
        } else if (sortType.equals(Constants.KEY_FOR_YOU)) {
            Utils.timeSpending(this, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            toolbarTitleTextView.setText(getString(R.string.article_listing_toolbar_title_for_you));
        } else if (sortType.equals(Constants.KEY_TODAYS_BEST)) {
            toolbarTitleTextView.setText(getString(R.string.article_listing_toolbar_title_todays_best));
        } else {
            toolbarTitleTextView.setText(getString(R.string.article_listing_toolbar_title_editor_picks));
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        articleDataModelsNew = new ArrayList<ArticleListingResult>();
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, sortType, false);

        recyclerAdapter = new MainArticleRecyclerViewAdapter(this, this, false, sortType, false);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setNewListData(articleDataModelsNew);
        recyclerView.setAdapter(recyclerAdapter);

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
                            hitArticleListingApi(nextPageNumber, sortType, false);
                        }
                    }
                }
            }
        });

    }

    private void hitArticleListingApi(int pPageCount, String sortKey, boolean isCacheRequired) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        if (Constants.KEY_FOR_YOU.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            RecommendationAPI recommendationAPI = retrofit.create(RecommendationAPI.class);
            progressBar.setVisibility(View.VISIBLE);
            Call<ArticleListingResponse> call = recommendationAPI
                    .getRecommendedArticlesList(SharedPrefUtils.getUserDetailModel(this).getDynamoId(), 10, chunks,
                            SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            call.enqueue(recommendedArticlesResponseCallback);
        } else if (Constants.KEY_EDITOR_PICKS.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

            int from = (nextPageNumber - 1) * limit + 1;
            Call<ArticleListingResponse> filterCall = topicsAPI
                    .getArticlesForCategory(AppConstants.EDITOR_PICKS_CATEGORY_ID, 0, from, from + limit - 1,
                            SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
        } else if (Constants.KEY_TODAYS_BEST.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

            int from = (nextPageNumber - 1) * limit + 1;
            Call<ArticleListingResponse> filterCall = topicsAPI
                    .getTodaysBestArticles(DateTimeUtils.getKidsDOBNanoMilliTimestamp("" + System.currentTimeMillis()),
                            from, from + limit - 1,
                            SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
        } else {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

            int from = (nextPageNumber - 1) * limit + 1;
            Call<ArticleListingResponse> filterCall = topicsAPI.getRecentArticles(from, from + limit - 1,
                    SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
        }
    }

    private Callback<ArticleListingResponse> recommendedArticlesResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processForYouResponse(responseData);
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
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private void processForYouResponse(ArticleListingResponse responseData) {
        try {
            if (responseData.getData().get(0).getResult() == null && (articleDataModelsNew == null
                    || articleDataModelsNew.isEmpty())) {
                addTopicsLayout.setVisibility(View.VISIBLE);
                headerArticleCardLayout.setVisibility(View.GONE);
                return;
            }

            ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
            if (dataList.size() == 0) {
                isLastPageReached = true;
                if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                    //No more next results from pagination
                } else {
                    // No results
                    articleDataModelsNew = dataList;
                    recyclerAdapter.setNewListData(dataList);
                    recyclerAdapter.notifyDataSetChanged();
                    noBlogsTextView.setVisibility(View.VISIBLE);
                    noBlogsTextView.setText(getString(R.string.no_articles_found));
                }
            } else {
                noBlogsTextView.setVisibility(View.GONE);
                if ("".equals(chunks)) {
                    articleDataModelsNew.clear();
//                    articleDataModelsNew.addAll(dataList);
                    for (int j = 0; j < dataList.size(); j++) {
                        if (!StringUtils.isNullOrEmpty(dataList.get(j).getId())) {
                            articleDataModelsNew.add(dataList.get(j));
                        }
                    }
                } else {
                    for (int j = 0; j < dataList.size(); j++) {
                        if (!StringUtils.isNullOrEmpty(dataList.get(j).getId())) {
                            articleDataModelsNew.add(dataList.get(j));
                        }
                    }
                }
                nextPageNumber++;
                if (chunks.equals(responseData.getData().get(0).getChunks())) {
                    isLastPageReached = true;
                } else {
                    chunks = responseData.getData().get(0).getChunks();
                }

                recyclerAdapter.setNewListData(articleDataModelsNew);
                recyclerAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            mLodingView.setVisibility(View.GONE);
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            progressBar.setVisibility(View.GONE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
                    ashimmerFrameLayout.stopShimmerAnimation();
                    ashimmerFrameLayout.setVisibility(View.GONE);
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            isReuqestRunning = false;
            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
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
//                writeArticleCell.setVisibility(View.VISIBLE);
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
    public void onRefresh() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        isLastPageReached = false;
        chunks = "";
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, sortType, false);
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

    @Override
    public void onRecyclerItemClick(View view, int position) {
        Intent intent = new Intent(ArticleListingActivity.this, ArticleDetailsContainerActivity.class);
        if (Constants.KEY_FOR_YOU.equalsIgnoreCase(sortType)) {
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "ForYouScreen");
            intent.putExtra(Constants.FROM_SCREEN, "ForYouScreen");
        } else if (Constants.KEY_EDITOR_PICKS.equalsIgnoreCase(sortType)) {
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "EditorsPickScreen");
            intent.putExtra(Constants.FROM_SCREEN, "EditorsPickScreen");
        } else if (Constants.KEY_RECENT.equalsIgnoreCase(sortType)) {
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "RecentScreen");
            intent.putExtra(Constants.FROM_SCREEN, "RecentScreen");
        } else if (Constants.KEY_TODAYS_BEST.equalsIgnoreCase(sortType)) {
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "TodaysBestScreen");
            intent.putExtra(Constants.FROM_SCREEN, "TodaysBestScreen");
        }
        intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
        intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
        intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
        intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
        intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
        intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
        intent.putExtra(Constants.AUTHOR,
                articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position)
                        .getUserName());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addTopicsLayout:
                Intent intent1 = new Intent(this, ExploreArticleListingTypeActivity.class);
                intent1.putExtra("fragType", "search");
                intent1.putExtra("source", "foryou");
                startActivity(intent1);
                break;
        }
    }

    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
        ashimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ashimmerFrameLayout.stopShimmerAnimation();
    }
}
