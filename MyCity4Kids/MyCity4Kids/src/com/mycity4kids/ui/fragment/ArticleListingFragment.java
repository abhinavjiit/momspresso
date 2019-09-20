package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.RecommendationAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ExploreArticleListingTypeActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.GroupIdCategoryMap;
import com.mycity4kids.utils.MixPanelUtils;
import com.mycity4kids.widget.FeedNativeAd;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ArticleListingFragment extends BaseFragment implements GroupIdCategoryMap.GroupCategoryInterface, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, ForYouInfoDialogFragment.IForYourArticleRemove, MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    private MainArticleRecyclerViewAdapter recyclerAdapter;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private String sortType;
    private int nextPageNumber;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private ProgressBar progressBar;
    private int from = 1;
    private int to = 15;
    private int limit = 15;
    private String chunks = "";
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isHeaderVisible = false;
    private boolean mIsVisibleToUser;
    private String[] feedOrderArray;

    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private RecyclerView recyclerView;
    private FeedNativeAd feedNativeAd;
    private LinearLayout addTopicsLayout;
    private FrameLayout headerArticleCardLayout;
    ShimmerFrameLayout ashimmerFrameLayout;
    private SwipeRefreshLayout pullToRefresh;
    private boolean fromPullToRefresh;
    Context mContext;
    private int tabPosition;
    private MixpanelAPI mixpanel;
    private Tracker tracker;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_article_layout, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        ashimmerFrameLayout = rootView.findViewById(R.id.shimmer1);
        mLodingView = rootView.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = rootView.findViewById(R.id.noBlogsTextView);
        progressBar = rootView.findViewById(R.id.progressBar);
        addTopicsLayout = rootView.findViewById(R.id.addTopicsLayout);
        headerArticleCardLayout = rootView.findViewById(R.id.headerArticleView);
        pullToRefresh = rootView.findViewById(R.id.pullToRefresh);

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        addTopicsLayout.setOnClickListener(this);

        rootView.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_indefinitely));
        if (getArguments() != null) {
            sortType = getArguments().getString(Constants.SORT_TYPE);
            tabPosition = getArguments().getInt(Constants.TAB_POSITION);
            feedOrderArray = getArguments().getStringArray("feedOrderArray");
        }

        articleDataModelsNew = new ArrayList<>();
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, sortType);

        if (tabPosition == 0) {
            long timeDiff = System.currentTimeMillis() - SharedPrefUtils.getLastLoginTimestamp(BaseApplication.getAppContext()) - AppConstants.HOURS_24_TIMESTAMP;
            if (SharedPrefUtils.getFollowTopicApproachChangeFlag(BaseApplication.getAppContext())) {
                if (SharedPrefUtils.getFollowedTopicsCount(BaseApplication.getAppContext()) < 1 && timeDiff < 0 &&
                        !SharedPrefUtils.isTopicSelectionChanged(BaseApplication.getAppContext()) &&
                        !SharedPrefUtils.getUserSkippedFollowTopicFlag(BaseApplication.getAppContext())) {
                    isHeaderVisible = true;
                } else {
                    isHeaderVisible = false;
                }
            } else {
                isHeaderVisible = SharedPrefUtils.getFollowedTopicsCount(BaseApplication.getAppContext()) < AppConstants.MINIMUM_TOPICS_FOLLOW_REQUIREMENT;
            }
        }

        recyclerAdapter = new MainArticleRecyclerViewAdapter(mContext, feedNativeAd, this, isHeaderVisible, sortType, tabPosition == 0);
        final LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setNewListData(articleDataModelsNew);
        recyclerView.setAdapter(recyclerAdapter);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                articleDataModelsNew.clear();
                recyclerAdapter.notifyDataSetChanged();
                ashimmerFrameLayout.setVisibility(View.VISIBLE);
                ashimmerFrameLayout.startShimmerAnimation();
                sortType = getArguments().getString(Constants.SORT_TYPE);
                nextPageNumber = 1;
                fromPullToRefresh = true;
                hitArticleListingApi(nextPageNumber, sortType);
                pullToRefresh.setRefreshing(false);
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
                            hitArticleListingApi(nextPageNumber, sortType);
                        }
                    }
                }
            }
        });

        if (tabPosition == 0) {
            getGroupIdForCurrentCategory();
        }
        tracker = BaseApplication.getInstance().getTracker(BaseApplication.TrackerName.APP_TRACKER);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mIsVisibleToUser) {
            onVisible();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIsVisibleToUser) {
            onInVisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
        if (isResumed()) { // fragment have created
            if (mIsVisibleToUser) {
                onVisible();
            } else {
                onInVisible();
            }
        }
    }

    private void onVisible() {
        Log.d("EditorPickFrag", "onVisible ---- " + sortType);
        try {
            if (!StringUtils.isNullOrEmpty(sortType)) {
                if (Constants.KEY_FOR_YOU.equalsIgnoreCase(sortType)) {
                    tracker.setScreenName("ForYouScreen");
                    Utils.pushOpenScreenEvent(getActivity(), "ForYouScreen", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "");
                } else if (Constants.KEY_EDITOR_PICKS.equalsIgnoreCase(sortType)) {
                    tracker.setScreenName("EditorsPickScreen");
                    Utils.pushOpenScreenEvent(getActivity(), "EditorsPickScreen", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "");
                } else if (Constants.KEY_RECENT.equalsIgnoreCase(sortType)) {
                    tracker.setScreenName("RecentScreen");
                    Utils.pushOpenScreenEvent(getActivity(), "RecentScreen", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "");
                } else if (Constants.KEY_TODAYS_BEST.equalsIgnoreCase(sortType)) {
                    tracker.setScreenName("TodaysBestScreen");
                    Utils.pushOpenScreenEvent(getActivity(), "TodaysBestScreen", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "");
                } else if (Constants.KEY_TRENDING.equalsIgnoreCase(sortType)) {
                    tracker.setScreenName("AllTrendingScreen");
                    Utils.pushOpenScreenEvent(getActivity(), "AllTrending", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "");
                }
                tracker.send(new HitBuilders.ScreenViewBuilder().build());
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

    }

    private void onInVisible() {
        Log.d("EditorPickFrag", "onInvisible");
    }

    private void getGroupIdForCurrentCategory() {
        GroupIdCategoryMap groupIdCategoryMap = new GroupIdCategoryMap("", this, "listing");
        groupIdCategoryMap.getGroupIdForCurrentCategory();
    }

    @Override
    public void onGroupMappingResult(int groupId, String gpHeading, String gpSubHeading, String gpImageUrl) {
        recyclerAdapter.setGroupInfo(groupId, gpHeading, gpSubHeading, gpImageUrl);
        recyclerAdapter.notifyDataSetChanged();
    }

    private void hitArticleListingApi(int pPageCount, String sortKey) {
        if (!ConnectivityUtils.isNetworkEnabled(mContext)) {
            removeProgressDialog();
            ToastUtils.showToast(mContext, getString(R.string.error_network));
            return;
        }
        if (Constants.KEY_FOR_YOU.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            RecommendationAPI recommendationAPI = retrofit.create(RecommendationAPI.class);
            if (fromPullToRefresh) {
                fromPullToRefresh = false;
                chunks = "";
            }
            Call<ArticleListingResponse> call = recommendationAPI.getRecommendedArticlesList(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), 10, chunks, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            progressBar.setVisibility(View.VISIBLE);
            call.enqueue(recommendedArticlesResponseCallback);
        } else if (Constants.KEY_EDITOR_PICKS.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
            int from = (nextPageNumber - 1) * limit + 1;
            Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(AppConstants.EDITOR_PICKS_CATEGORY_ID, 0, from, from + limit - 1,
                    SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
        } else if (Constants.KEY_TODAYS_BEST.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
            int from = (nextPageNumber - 1) * limit + 1;
            Call<ArticleListingResponse> filterCall = topicsAPI.getTodaysBestArticles(DateTimeUtils.getKidsDOBNanoMilliTimestamp("" + System.currentTimeMillis()), from, from + limit - 1,
                    SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
        } else if (Constants.KEY_TRENDING.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
            int from = (nextPageNumber - 1) * limit + 1;
            Call<ArticleListingResponse> filterCall = topicsAPI.getTrendingArticles(from, from + limit - 1, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
        } else {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
            int from = (nextPageNumber - 1) * limit + 1;
            Call<ArticleListingResponse> filterCall = topicsAPI.getRecentArticles(from, from + limit - 1, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
        }
    }

    private Callback<ArticleListingResponse> recommendedArticlesResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            if (!isAdded()) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                ToastUtils.showToast(mContext, getString(R.string.server_went_wrong));
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processForYouResponse(responseData);
                } else {
                    ToastUtils.showToast(mContext, responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ToastUtils.showToast(mContext, getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            if (isAdded())
                ToastUtils.showToast(mContext, getString(R.string.went_wrong));
        }
    };

    private void processForYouResponse(ArticleListingResponse responseData) {
        try {
            if (responseData.getData().get(0).getResult() == null && (articleDataModelsNew == null || articleDataModelsNew.isEmpty())) {
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

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            progressBar.setVisibility(View.GONE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response.body() == null) {
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
                recyclerView.setVisibility(View.GONE);
                noBlogsTextView.setText(getString(R.string.no_articles_found));
                articleDataModelsNew = dataList;
                recyclerAdapter.setNewListData(articleDataModelsNew);
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
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
        if (!ConnectivityUtils.isNetworkEnabled(mContext)) {
            removeProgressDialog();
            ToastUtils.showToast(mContext, getString(R.string.error_network));
            return;
        }
        isLastPageReached = false;
        chunks = "";
        removeVolleyCache(sortType);
        from = 1;
        to = 15;
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, sortType);
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
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.videoContainerFL1:
                break;
            case R.id.closeImageView:
                mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    mixpanel.track("FollowTopicCardClose", jsonObject);
                    Log.d("FollowUnfollowTopics", jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SharedPrefUtils.setUserSkippedFollowTopicFlag(BaseApplication.getAppContext(), true);
                hideFollowTopicHeader();
                break;
            case R.id.headerView:
                Intent intent1 = new Intent(getActivity(), ExploreArticleListingTypeActivity.class);
                intent1.putExtra("fragType", "search");
                startActivity(intent1);
                break;
            case R.id.headerArticleView:
            case R.id.fbAdArticleView:
            case R.id.storyHeaderView:
            default:
                if ("1".equals(articleDataModelsNew.get(position).getContentType())) {
                    Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                    if (Constants.KEY_FOR_YOU.equalsIgnoreCase(sortType)) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "ForYouScreen");
                    } else if (Constants.KEY_EDITOR_PICKS.equalsIgnoreCase(sortType)) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "EditorsPickScreen");
                    } else if (Constants.KEY_RECENT.equalsIgnoreCase(sortType)) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "RecentScreen");
                    } else if (Constants.KEY_TODAYS_BEST.equalsIgnoreCase(sortType)) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "TodaysBestScreen");
                    } else if (Constants.KEY_TRENDING.equalsIgnoreCase(sortType)) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "AllTrending");
                    }
                    intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
                    intent.putExtra(Constants.FROM_SCREEN, "HomeScreen");
                    intent.putExtra(Constants.AUTHOR, articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
                    ArrayList<ArticleListingResult> filteredResult = AppUtils.getFilteredContentList(articleDataModelsNew, AppConstants.CONTENT_TYPE_SHORT_STORY);
                    intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(position, articleDataModelsNew, AppConstants.CONTENT_TYPE_SHORT_STORY));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, ArticleDetailsContainerActivity.class);
                    if (Constants.KEY_FOR_YOU.equalsIgnoreCase(sortType)) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "ForYouScreen");
                    } else if (Constants.KEY_EDITOR_PICKS.equalsIgnoreCase(sortType)) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "EditorsPickScreen");
                    } else if (Constants.KEY_RECENT.equalsIgnoreCase(sortType)) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "RecentScreen");
                    } else if (Constants.KEY_TODAYS_BEST.equalsIgnoreCase(sortType)) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "TodaysBestScreen");
                    } else if (Constants.KEY_TRENDING.equalsIgnoreCase(sortType)) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "AllTrending");
                    }
                    intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
                    intent.putExtra(Constants.FROM_SCREEN, "HomeScreen");
                    intent.putExtra(Constants.AUTHOR, articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
                    ArrayList<ArticleListingResult> filteredResult = AppUtils.getFilteredContentList(articleDataModelsNew, AppConstants.CONTENT_TYPE_ARTICLE);
                    intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(position, articleDataModelsNew, AppConstants.CONTENT_TYPE_ARTICLE));
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addTopicsLayout:
                Intent intent1 = new Intent(mContext, ExploreArticleListingTypeActivity.class);
                intent1.putExtra("fragType", "search");
                intent1.putExtra("source", "foryou");
                startActivity(intent1);
                break;
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    public void onResume() {
        super.onResume();
        ashimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        ashimmerFrameLayout.stopShimmerAnimation();
    }

    private void launchVideoDetailsActivity(int position, int videoIndex) {
        MixPanelUtils.pushMomVlogClickEvent(mixpanel, videoIndex, "TrendingAll");
        if (articleDataModelsNew.get(position).getCarouselVideoList() != null && !articleDataModelsNew.get(position).getCarouselVideoList().isEmpty()) {
            if (isAdded()) {
                Utils.momVlogEvent(getActivity(), "Home Screen", "Vlog_card_home_feed",
                        "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Show_Video_Listing", "", "");
            }
            VlogsListingAndDetailResult result = articleDataModelsNew.get(position).getCarouselVideoList().get(videoIndex);
            Intent intent = new Intent(getActivity(), ParallelFeedActivity.class);
            intent.putExtra(Constants.VIDEO_ID, result.getId());
            intent.putExtra(Constants.STREAM_URL, result.getUrl());
            intent.putExtra(Constants.AUTHOR_ID, result.getAuthor().getId());
            intent.putExtra(Constants.FROM_SCREEN, "Home Screen");
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Funny Videos");
            intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
            intent.putExtra(Constants.AUTHOR, result.getAuthor().getId() + "~" + result.getAuthor().getFirstName() + " " + result.getAuthor().getLastName());
            startActivity(intent);
        }
    }

    public void hideFollowTopicHeader() {
        isHeaderVisible = false;
        recyclerAdapter.hideFollowTopicHeader();
        recyclerAdapter.notifyDataSetChanged();
    }
}