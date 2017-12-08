package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnWebServiceCompleteListener;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.newmodels.VolleyBaseResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.RecommendationAPI;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.ui.fragment.ForYouInfoDialogFragment;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.volley.HttpVolleyRequest;
import com.mycity4kids.widget.FeedNativeAd;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 4/8/16.
 */
public class ArticleListingActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, ForYouInfoDialogFragment.IForYourArticleRemove, FeedNativeAd.AdLoadingListener, MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    //    private MainArticleListingAdapter articlesListingAdapter;
    private MainArticleRecyclerViewAdapter recyclerAdapter;
    private ArrayList<ArticleListingResult> articleDataModelsNew;

    private String sortType;
    private int nextPageNumber;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
//    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private int from = 1;
    private int to = 15;
    private String chunks = "";
    private String fromScreen;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    //    private ListView listView;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private Toolbar mToolbar;
    private TextView toolbarTitleTextView;
    private ImageView downArrowImageView;
    private RecyclerView recyclerView;
    private FeedNativeAd feedNativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_listing_activity);
//        listView = (ListView) findViewById(R.id.scroll);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        downArrowImageView = (ImageView) mToolbar.findViewById(R.id.downArrowImageView);
        toolbarTitleTextView = (TextView) mToolbar.findViewById(R.id.toolbarTitle);

        mToolbar.setVisibility(View.VISIBLE);

        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

        progressBar.setVisibility(View.VISIBLE);

        sortType = getIntent().getStringExtra(Constants.SORT_TYPE);
        fromScreen = getIntent().getStringExtra(Constants.FROM_SCREEN);

        if (sortType.equals(Constants.KEY_RECENT)) {
            toolbarTitleTextView.setText(getString(R.string.article_listing_toolbar_title_recent));
        } else if (sortType.equals(Constants.KEY_POPULAR)) {
            toolbarTitleTextView.setText(getString(R.string.article_listing_toolbar_title_popular));
        } else if (sortType.equals(Constants.KEY_TRENDING)) {
            toolbarTitleTextView.setText(getString(R.string.article_listing_toolbar_title_trending));
        } else if (sortType.equals(Constants.KEY_FOR_YOU)) {
            toolbarTitleTextView.setText(getString(R.string.article_listing_toolbar_title_for_you));
        } else {
            toolbarTitleTextView.setText(getString(R.string.article_listing_toolbar_title_editor_picks));
        }
        setSupportActionBar(mToolbar);

        articleDataModelsNew = new ArrayList<ArticleListingResult>();
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, sortType, false);

//        swipeRefreshLayout.setOnRefreshListener(this);

        feedNativeAd = new FeedNativeAd(this, this, AppConstants.FB_AD_PLACEMENT_ARTICLE_LISTING);
        feedNativeAd.loadAds();
        recyclerAdapter = new MainArticleRecyclerViewAdapter(this, feedNativeAd, this, false);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setNewListData(articleDataModelsNew);
        recyclerView.setAdapter(recyclerAdapter);

//        articlesListingAdapter = new MainArticleListingAdapter(this);
//        articlesListingAdapter.setListingType(sortType);
//        articlesListingAdapter.setNewListData(articleDataModelsNew);
//        listView.setAdapter(articlesListingAdapter);
//        articlesListingAdapter.notifyDataSetChanged();

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
//                    //caching enabled only for page 1. so disabling it here for all other pages by passing false.
//                    hitArticleListingApi(nextPageNumber, sortType, false);
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
                            hitArticleListingApi(nextPageNumber, sortType, false);
                        }
                    }
                }
            }
        });


//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(ArticleListingActivity.this, ArticleDetailsContainerActivity.class);
//                if (adapterView.getAdapter() instanceof MainArticleListingAdapter) {
//                    ArticleListingResult parentingListData = (ArticleListingResult) adapterView.getAdapter().getItem(i);
//                    if (Constants.KEY_FOR_YOU.equalsIgnoreCase(sortType)) {
//                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "ForYoucreen");
//                        intent.putExtra(Constants.FROM_SCREEN, "ForYouScreen");
//                    } else if (Constants.KEY_EDITOR_PICKS.equalsIgnoreCase(sortType)) {
//                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "EditorsPickScreen");
//                        intent.putExtra(Constants.FROM_SCREEN, "EditorsPickScreen");
//                    } else if (Constants.KEY_RECENT.equalsIgnoreCase(sortType)) {
//                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "RecentScreen");
//                        intent.putExtra(Constants.FROM_SCREEN, "RecentScreen");
//                    }
//
//                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
//                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
//                    intent.putExtra(Constants.BLOG_SLUG, parentingListData.getBlogPageSlug());
//                    intent.putExtra(Constants.TITLE_SLUG, parentingListData.getTitleSlug());
//                    intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
//                    intent.putExtra(Constants.AUTHOR, parentingListData.getUserId() + "~" + parentingListData.getUserName());
//                    startActivity(intent);
//                }
//            }
//        });

        downArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.TAB_FOR_YOU.equalsIgnoreCase(sortType)) {
                    Utils.pushTopMenuClickEvent(ArticleListingActivity.this, "ForYouScreen", SharedPrefUtils.getUserDetailModel(ArticleListingActivity.this).getDynamoId() + "");
                } else if (Constants.TAB_EDITOR_PICKS.equalsIgnoreCase(sortType)) {
                    Utils.pushTopMenuClickEvent(ArticleListingActivity.this, "EditorsPickScreen", SharedPrefUtils.getUserDetailModel(ArticleListingActivity.this).getDynamoId() + "");
                } else if (Constants.TAB_RECENT.equalsIgnoreCase(sortType)) {
                    Utils.pushTopMenuClickEvent(ArticleListingActivity.this, "RecentScreen", SharedPrefUtils.getUserDetailModel(ArticleListingActivity.this).getDynamoId() + "");
                }
                onBackPressed();
            }
        });
        toolbarTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.TAB_FOR_YOU.equalsIgnoreCase(sortType)) {
                    Utils.pushTopMenuClickEvent(ArticleListingActivity.this, "ForYouScreen", SharedPrefUtils.getUserDetailModel(ArticleListingActivity.this).getDynamoId() + "");
                } else if (Constants.TAB_EDITOR_PICKS.equalsIgnoreCase(sortType)) {
                    Utils.pushTopMenuClickEvent(ArticleListingActivity.this, "EditorsPickScreen", SharedPrefUtils.getUserDetailModel(ArticleListingActivity.this).getDynamoId() + "");
                } else if (Constants.TAB_RECENT.equalsIgnoreCase(sortType)) {
                    Utils.pushTopMenuClickEvent(ArticleListingActivity.this, "RecentScreen", SharedPrefUtils.getUserDetailModel(ArticleListingActivity.this).getDynamoId() + "");
                }
                onBackPressed();
            }
        });
    }

    @Override
    protected void updateUi(Response response) {

    }

    private void hitArticleListingApi(int pPageCount, String sortKey, boolean isCacheRequired) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            //   swipeRefreshLayout.setRefreshing(false);
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        String url = "";
        if (Constants.KEY_FOR_YOU.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            RecommendationAPI recommendationAPI = retrofit.create(RecommendationAPI.class);
            Call<ArticleListingResponse> call = recommendationAPI.getRecommendedArticlesList(SharedPrefUtils.getUserDetailModel(this).getDynamoId(), 10, chunks, SharedPrefUtils.getLanguageFilters(this));
            progressBar.setVisibility(View.VISIBLE);
            call.enqueue(recommendedArticlesResponseCallback);
        } else if (Constants.KEY_EDITOR_PICKS.equals(sortKey)) {
            url = AppConstants.LIVE_URL + AppConstants.SERVICE_TYPE__EDITORS_PICKS + AppConstants.EDITOR_PICKS_CATEGORY_ID + "?sort=0&sponsored=0&start=" + from +
                    "&end=" + to + "&lang=" + SharedPrefUtils.getLanguageFilters(this);
            HttpVolleyRequest.getStringResponse(this, url, null, mGetArticleListingListener, Request.Method.GET, isCacheRequired);
        } else {
            url = AppConstants.LIVE_URL + AppConstants.SERVICE_TYPE_ARTICLE + sortKey +
                    AppConstants.SEPARATOR_BACKSLASH + from + AppConstants.SEPARATOR_BACKSLASH + to + "?lang=" + SharedPrefUtils.getLanguageFilters(this);
            HttpVolleyRequest.getStringResponse(this, url, null, mGetArticleListingListener, Request.Method.GET, isCacheRequired);
        }
    }

    private Callback<ArticleListingResponse> recommendedArticlesResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
//            swipeRefreshLayout.setRefreshing(false);
            isReuqestRunning = false;
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast("Something went wrong from server");
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processForYouResponse(responseData);
//                    notificationCenterResultArrayList.addAll(responseData.getData().getResult());
//                    notificationCenterListAdapter.notifyDataSetChanged();
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
//            swipeRefreshLayout.setRefreshing(false);
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private void processForYouResponse(ArticleListingResponse responseData) {
        try {
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
                    noBlogsTextView.setText("No articles found");
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
            removeVolleyCache(sortType);
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

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

//                swipeRefreshLayout.setRefreshing(false);
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
            ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

            if (dataList.size() == 0) {

                isLastPageReached = true;
                if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                    //No more next results for search from pagination

                } else {
                    // No results for search
                    articleDataModelsNew = dataList;
                    recyclerAdapter.setNewListData(dataList);
                    recyclerAdapter.notifyDataSetChanged();
                    noBlogsTextView.setVisibility(View.VISIBLE);
                    noBlogsTextView.setText("No articles found");
                }

            } else {
                noBlogsTextView.setVisibility(View.GONE);
                if (from == 1) {
                    articleDataModelsNew = dataList;
                } else {
                    int prevFrom = Integer.parseInt(responseData.getData().get(0).getChunks().split("-")[0]);
                    if (prevFrom < from) {
                        //Response from cache refresh request. Update the dataset and refresh list
                        //cache refresh request response and response from pagination may overlap causing duplication
                        // to prevent check the page number in response
                        //-- open article listing and immediately scroll to next page to reproduce.
                        int articleNumber = prevFrom - 1;
                        for (int i = 0; i < dataList.size(); i++) {
                            articleDataModelsNew.set(articleNumber + i, dataList.get(i));
                        }
                        recyclerAdapter.setNewListData(articleDataModelsNew);
                        recyclerAdapter.notifyDataSetChanged();
                        return;
                    } else {
                        articleDataModelsNew.addAll(dataList);
                    }

                }
                from = from + 15;
                to = to + 15;
                nextPageNumber++;
                recyclerAdapter.setNewListData(articleDataModelsNew);
                recyclerAdapter.notifyDataSetChanged();
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
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
//            swipeRefreshLayout.setRefreshing(false);
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        isLastPageReached = false;
        chunks = "";
        removeVolleyCache(sortType);
        from = 1;
        to = 15;
        nextPageNumber = 1;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onForYouArticleRemoved(int position) {
        Log.d("Remove For YOu", "position = " + position);
        if (articleDataModelsNew != null && articleDataModelsNew.size() > position) {
            articleDataModelsNew.remove(position);
            recyclerAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent(ArticleListingActivity.this, ArticleDetailsContainerActivity.class);
        if (Constants.KEY_FOR_YOU.equalsIgnoreCase(sortType)) {
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "ForYoucreen");
            intent.putExtra(Constants.FROM_SCREEN, "ForYouScreen");
        } else if (Constants.KEY_EDITOR_PICKS.equalsIgnoreCase(sortType)) {
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "EditorsPickScreen");
            intent.putExtra(Constants.FROM_SCREEN, "EditorsPickScreen");
        } else if (Constants.KEY_RECENT.equalsIgnoreCase(sortType)) {
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "RecentScreen");
            intent.putExtra(Constants.FROM_SCREEN, "RecentScreen");
        }

        intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
        intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
        intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
        intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
        intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
        intent.putExtra(Constants.AUTHOR, articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
        startActivity(intent);
    }
}
