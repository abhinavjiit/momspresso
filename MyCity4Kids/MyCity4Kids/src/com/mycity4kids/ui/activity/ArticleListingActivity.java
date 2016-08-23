package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.interfaces.OnWebServiceCompleteListener;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.newmodels.VolleyBaseResponse;
import com.mycity4kids.ui.adapter.NewArticlesListingAdapter;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.volley.HttpVolleyRequest;

import java.util.ArrayList;

/**
 * Created by hemant on 4/8/16.
 */
public class ArticleListingActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    NewArticlesListingAdapter articlesListingAdapter;
    ArrayList<ArticleListingResult> articleDataModelsNew;

    ListView listView;
    private RelativeLayout mLodingView;
    TextView noBlogsTextView;
    Toolbar mToolbar;

    String sortType;
    private int nextPageNumber;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private int from = 1;
    private int to = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_article_layout);
        listView = (ListView) findViewById(R.id.scroll);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setVisibility(View.VISIBLE);

        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

        progressBar.setVisibility(View.VISIBLE);
        sortType = getIntent().getStringExtra(Constants.SORT_TYPE);
        if (sortType.equals(Constants.KEY_RECENT)) {
            getSupportActionBar().setTitle("Recent");
        } else if (sortType.equals(Constants.KEY_POPULAR)) {
            getSupportActionBar().setTitle("Popular");
        } else {
            getSupportActionBar().setTitle("Trending");
        }


        articleDataModelsNew = new ArrayList<ArticleListingResult>();
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, sortType, true);

        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);

        articlesListingAdapter = new NewArticlesListingAdapter(this, true);
        articlesListingAdapter.setNewListData(articleDataModelsNew);
        listView.setAdapter(articlesListingAdapter);
        articlesListingAdapter.notifyDataSetChanged();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    //caching enabled only for page 1. so disabling it here for all other pages by passing false.
                    hitArticleListingApi(nextPageNumber, sortType, false);
                    isReuqestRunning = true;
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(ArticleListingActivity.this, ArticlesAndBlogsDetailsActivity.class);
                if (adapterView.getAdapter() instanceof NewArticlesListingAdapter) {
                    ArticleListingResult parentingListData = (ArticleListingResult) ((NewArticlesListingAdapter) adapterView.getAdapter()).getItem(i);
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getImageUrl());
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    intent.putExtra(Constants.FILTER_TYPE, parentingListData.getUserType());
                    intent.putExtra(Constants.BLOG_NAME, parentingListData.getBlogPageSlug());
                    startActivity(intent);

                }
            }
        });
    }


    @Override
    protected void updateUi(Response response) {

    }

    private void hitArticleListingApi(int pPageCount, String sortKey, boolean isCacheRequired) {

        String url = AppConstants.LIVE_URL + AppConstants.SERVICE_TYPE_ARTICLE + sortKey +
                AppConstants.SEPARATOR_BACKSLASH + from + AppConstants.SEPARATOR_BACKSLASH + to;
        HttpVolleyRequest.getStringResponse(this, url, null, mGetArticleListingListener, Request.Method.GET, isCacheRequired);


    }

    private OnWebServiceCompleteListener mGetArticleListingListener = new OnWebServiceCompleteListener() {
        @Override
        public void onWebServiceComplete(VolleyBaseResponse response, boolean isError) {
            progressBar.setVisibility(View.GONE);
            if (isError) {
                if (response.getResponseCode() != 999)
                    showToast("Something went wrong from server");
            } else {

                if (response == null) {
                    showToast("Something went wrong from server");
                    isReuqestRunning = false;
                    mLodingView.setVisibility(View.GONE);
                    return;
                }
                Log.d("Response back =", " " + response.getResponseBody());
                ArticleListingResponse responseData;
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                    responseData = gson.fromJson(response.getResponseBody(), ArticleListingResponse.class);
                } catch (JsonSyntaxException jse) {
                    Crashlytics.logException(jse);
                    Log.d("JsonSyntaxException", Log.getStackTraceString(jse));
                    showToast(getString(R.string.server_error));
                    return;
                }

                swipeRefreshLayout.setRefreshing(false);
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processResponse(responseData);
                } else if (responseData.getCode() == 400) {
                    String message = responseData.getReason();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        showToast(message);
                    } else {
                        showToast(getString(R.string.went_wrong));
                    }
                }

                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);
            }

        }
    };

    private void processResponse(ArticleListingResponse responseData) {
        try {
            ArrayList<ArticleListingResult> dataList = responseData.getData().getResult();

            if (dataList.size() == 0) {

                isLastPageReached = true;
                if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                    //No more next results for search from pagination

                } else {
                    // No results for search
                    articleDataModelsNew = dataList;
                    articlesListingAdapter.setNewListData(dataList);
                    articlesListingAdapter.notifyDataSetChanged();
                    noBlogsTextView.setVisibility(View.VISIBLE);
                    noBlogsTextView.setText("No articles found");
                }

            } else {
                noBlogsTextView.setVisibility(View.GONE);
                if (from == 1) {
                    articleDataModelsNew = dataList;
                } else {
                    int prevFrom = Integer.parseInt(responseData.getData().getChunks().split("-")[0]);
                    if (prevFrom < from) {
                        //Response from cache refresh request. Update the dataset and refresh list
                        //cache refresh request response and response from pagination may overlap causing duplication
                        // to prevent check the page number in response
                        //-- open article listing and immediately scroll to next page to reproduce.
                        int articleNumber = prevFrom - 1;
                        for (int i = 0; i < dataList.size(); i++) {
                            articleDataModelsNew.set(articleNumber + i, dataList.get(i));
                        }
                        articlesListingAdapter.setNewListData(articleDataModelsNew);
                        articlesListingAdapter.notifyDataSetChanged();
                        return;
                    } else {
                        articleDataModelsNew.addAll(dataList);
                    }

                }
                from = from + 15;
                to = to + 15;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            mLodingView.setVisibility(View.GONE);
            removeVolleyCache(sortType);
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

    }

    @Override
    public void onRefresh() {
        isLastPageReached = false;
        removeVolleyCache(sortType);
        from = 1;
        to = 15;
        hitArticleListingApi(nextPageNumber, sortType, false);
    }

    private void removeVolleyCache(String sortType) {
        if (AppConstants.SORT_TYPE_BOOKMARK.equals(sortType))
            return;

        int cacheFrom = 1;
        int cacheTo = 15;

        String baseCacheKey = Request.Method.GET + ":" + AppConstants.LIVE_URL + AppConstants.SERVICE_TYPE_ARTICLE + sortType +
                AppConstants.SEPARATOR_BACKSLASH;
        String cachedPage = cacheFrom + AppConstants.SEPARATOR_BACKSLASH + cacheTo;
        while (null != BaseApplication.getInstance().getRequestQueue().getCache().get(baseCacheKey + cachedPage)) {
            BaseApplication.getInstance().getRequestQueue().getCache().remove(baseCacheKey + cachedPage);
            cacheFrom = cacheFrom + 15;
            cacheTo = cacheTo + 15;
            cachedPage = cacheFrom + AppConstants.SEPARATOR_BACKSLASH + cacheTo;
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
                Intent intent = new Intent(getApplicationContext(), SearchArticlesAndAuthorsActivity.class);
                intent.putExtra(Constants.FILTER_NAME, "");
                intent.putExtra(Constants.TAB_POSITION, 0);
                startActivity(intent);
        }
        return true;
    }
}
