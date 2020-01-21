package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.snackbar.Snackbar;
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
import com.mycity4kids.models.campaignmodels.AllCampaignDataResponse;
import com.mycity4kids.models.campaignmodels.CampaignDataListResult;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.RecommendationAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ExploreArticleListingTypeActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.GroupIdCategoryMap;
import com.mycity4kids.utils.MixPanelUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.SharingUtils;
import com.mycity4kids.widget.StoryShareCardWidget;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ArticleListingFragment extends BaseFragment implements GroupIdCategoryMap.GroupCategoryInterface, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    private final static int LIMIT = 15;
    private final static int FORYOU_LIMIT = 10;
    private static final int REQUEST_INIT_PERMISSION = 2;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private MainArticleRecyclerViewAdapter recyclerAdapter;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private ArrayList<CampaignDataListResult> campaignListDataModels;
    private String sortType;
    private int nextPageNumber;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private ProgressBar progressBar;
    private String chunks = "";
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isHeaderVisible = false;
    private boolean mIsVisibleToUser;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private RecyclerView recyclerView;
    private LinearLayout addTopicsLayout;
    private FrameLayout headerArticleCardLayout;
    private ShimmerFrameLayout ashimmerFrameLayout;
    private SwipeRefreshLayout pullToRefresh;
    private boolean fromPullToRefresh;
    private Context mContext;
    private int tabPosition;
    private MixpanelAPI mixpanel;
    private Tracker tracker;
    private String userDynamoId;
    private int position;
    private String likeStatus;
    private int currentShortStoryPosition;
    private boolean isRecommendRequestRunning;
    private RelativeLayout rootLayout;
    private StoryShareCardWidget storyShareCardWidget;
    private ImageView shareStoryImageView;
    private ArticleListingResult sharedStoryItem;
    private String shareMedium;


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
        rootLayout = rootView.findViewById(R.id.rootLayout);

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        addTopicsLayout.setOnClickListener(this);

        rootView.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_indefinitely));
        if (getArguments() != null) {
            sortType = getArguments().getString(Constants.SORT_TYPE);
            tabPosition = getArguments().getInt(Constants.TAB_POSITION);
        }

        articleDataModelsNew = new ArrayList<>();
        campaignListDataModels = new ArrayList<>();
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

        recyclerAdapter = new MainArticleRecyclerViewAdapter(mContext, this, isHeaderVisible, sortType, tabPosition == 0);
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
                } /*else if (Constants.KEY_EDITOR_PICKS.equalsIgnoreCase(sortType)) {
                    tracker.setScreenName("EditorsPickScreen");
                    Utils.pushOpenScreenEvent(getActivity(), "EditorsPickScreen", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "");
                }*/ else if (Constants.KEY_RECENT.equalsIgnoreCase(sortType)) {
                    tracker.setScreenName("RecentScreen");
                    Utils.pushOpenScreenEvent(getActivity(), "RecentScreen", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "");
                } else if (Constants.KEY_TODAYS_BEST.equalsIgnoreCase(sortType)) {
                    tracker.setScreenName("TodaysBestScreen");
                    Utils.pushOpenScreenEvent(getActivity(), "TodaysBestScreen", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "");
                } else if (Constants.KEY_TRENDING.equalsIgnoreCase(sortType)) {
                    tracker.setScreenName("AllTrendingScreen");
                    Utils.pushOpenScreenEvent(getActivity(), "AllTrending", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "");
                } else if (Constants.KEY_FOLLOWING.equalsIgnoreCase(sortType)) {
                    tracker.setScreenName("FollowingContentScreen");
                    Utils.pushOpenScreenEvent(getActivity(), "FollowingContentScreen", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "");
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
            Call<ArticleListingResponse> call = recommendationAPI.getRecommendedArticlesList(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), FORYOU_LIMIT, chunks, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            progressBar.setVisibility(View.VISIBLE);
            call.enqueue(recommendedArticlesResponseCallback);
        } else if (Constants.KEY_FOLLOWING.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            RecommendationAPI recommendationAPI = retrofit.create(RecommendationAPI.class);
            if (fromPullToRefresh) {
                fromPullToRefresh = false;
                chunks = "";
            }
            Call<ArticleListingResponse> call = recommendationAPI.getFollowingArticlesList(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), LIMIT, chunks, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            progressBar.setVisibility(View.VISIBLE);
            call.enqueue(recommendedArticlesResponseCallback);
        } /*else if (Constants.KEY_EDITOR_PICKS.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
            int from = (nextPageNumber - 1) * LIMIT + 1;
            Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(AppConstants.EDITOR_PICKS_CATEGORY_ID, 0, from, from + LIMIT - 1,
                    SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
        }*/ else if (Constants.KEY_TODAYS_BEST.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
            CampaignAPI campaignAPI = retrofit.create(CampaignAPI.class);
            int from = (nextPageNumber - 1) * LIMIT + 1;
            Call<ArticleListingResponse> filterCall = topicsAPI.getTodaysBestArticles(DateTimeUtils.getKidsDOBNanoMilliTimestamp("" + System.currentTimeMillis()), from, from + LIMIT - 1,
                    SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
            if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getHomeAdSlotUrl(BaseApplication.getAppContext()))) {
                Call<ResponseBody> adSlotCall = campaignAPI.getAdSlotData(SharedPrefUtils.getHomeAdSlotUrl(BaseApplication.getAppContext()));
                adSlotCall.enqueue(adSlotResponseCallback);
            } else {
                Call<AllCampaignDataResponse> campaignListCall = campaignAPI.getCampaignList(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), 0, 5, 3.0);
                campaignListCall.enqueue(getCampaignList);
            }
        } else if (Constants.KEY_TRENDING.equals(sortKey)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
            int from = (nextPageNumber - 1) * LIMIT + 1;
            Call<ArticleListingResponse> filterCall = topicsAPI.getTrendingArticles(from, from + LIMIT - 1, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
        } else {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
            int from = (nextPageNumber - 1) * LIMIT + 1;
            Call<ArticleListingResponse> filterCall = topicsAPI.getRecentArticles(from, from + LIMIT - 1, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
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
                    ashimmerFrameLayout.stopShimmerAnimation();
                    ashimmerFrameLayout.setVisibility(View.GONE);
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
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

    }

    private Callback<AllCampaignDataResponse> getCampaignList = new Callback<AllCampaignDataResponse>() {
        @Override
        public void onResponse(Call<AllCampaignDataResponse> call, retrofit2.Response<AllCampaignDataResponse> response) {
            if (response.body() == null) {
                return;
            }
            try {
                AllCampaignDataResponse allCampaignDataResponse = response.body();
                if (allCampaignDataResponse.getCode() == 200 && Constants.SUCCESS.equals(allCampaignDataResponse.getStatus())) {
                    processCampaignListingResponse(allCampaignDataResponse);
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<AllCampaignDataResponse> call, Throwable e) {
            Crashlytics.logException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }
    };

    private Callback<ResponseBody> adSlotResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            try {
                if (response.body() == null) {
                    return;
                }
                String resData = new String(response.body().bytes());
                JSONObject jObject = new JSONObject(resData);
                String htmlContent = jObject.getJSONObject("revive-0-0").getString("html");
                recyclerAdapter.setCampaignOrAdSlotData("adslot", null, htmlContent);
                recyclerAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                recyclerAdapter.setCampaignOrAdSlotData("", null, "");
                recyclerAdapter.notifyDataSetChanged();
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable e) {
            recyclerAdapter.setCampaignOrAdSlotData("", null, "");
            recyclerAdapter.notifyDataSetChanged();
            Crashlytics.logException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }
    };

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
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
            mLodingView.setVisibility(View.GONE);
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

    private void processCampaignListingResponse(AllCampaignDataResponse responseData) {
        ArrayList<CampaignDataListResult> dataList = responseData.getData().getResult();
        campaignListDataModels.addAll(dataList);
        recyclerAdapter.setCampaignOrAdSlotData("campaign", campaignListDataModels, "");
        recyclerAdapter.notifyDataSetChanged();
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
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, sortType);
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
    public void onRecyclerItemClick(View view, int position) {
        try {
            switch (view.getId()) {
                case R.id.videoContainerFL1:
                    launchVideoDetailsActivity(position, 0);
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
                case R.id.cardView1:
                    try {
                        Utils.campaignEvent(getActivity(), "HomeScreen", "HomeScreenCarousel", "CTA_Campaign_Carousel",
                                "" + campaignListDataModels.get(0).getName(), "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "CTA_Campaign_Carousel");
                    } catch (Exception e) {

                    }
                    Intent campaignIntent = new Intent(getActivity(), CampaignContainerActivity.class);
                    campaignIntent.putExtra("campaign_id", campaignListDataModels.get(0).getId() + "");
                    campaignIntent.putExtra("campaign_detail", "campaign_detail");
                    startActivity(campaignIntent);
                    break;
                case R.id.cardView2:
                    try {
                        Utils.campaignEvent(getActivity(), "HomeScreen", "HomeScreenCarousel", "CTA_Campaign_Carousel",
                                "" + campaignListDataModels.get(1).getName(), "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "CTA_Campaign_Carousel");
                    } catch (Exception e) {

                    }
                    Intent campaignIntent2 = new Intent(getActivity(), CampaignContainerActivity.class);
                    campaignIntent2.putExtra("campaign_id", campaignListDataModels.get(1).getId() + "");
                    campaignIntent2.putExtra("campaign_detail", "campaign_detail");
                    startActivity(campaignIntent2);
                    break;
                case R.id.cardView3:
                    try {
                        Utils.campaignEvent(getActivity(), "HomeScreen", "HomeScreenCarousel", "CTA_Campaign_Carousel",
                                "" + campaignListDataModels.get(2).getName(), "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "CTA_Campaign_Carousel");
                    } catch (Exception e) {

                    }
                    Intent campaignIntent3 = new Intent(getActivity(), CampaignContainerActivity.class);
                    campaignIntent3.putExtra("campaign_id", campaignListDataModels.get(2).getId() + "");
                    campaignIntent3.putExtra("campaign_detail", "campaign_detail");
                    startActivity(campaignIntent3);
                    break;
                case R.id.cardView4:
                    try {
                        Utils.campaignEvent(getActivity(), "HomeScreen", "HomeScreenCarousel", "CTA_Campaign_Carousel",
                                "" + campaignListDataModels.get(3).getName(), "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "CTA_Campaign_Carousel");
                    } catch (Exception e) {

                    }
                    Intent campaignIntent4 = new Intent(getActivity(), CampaignContainerActivity.class);
                    campaignIntent4.putExtra("campaign_id", campaignListDataModels.get(3).getId() + "");
                    campaignIntent4.putExtra("campaign_detail", "campaign_detail");
                    startActivity(campaignIntent4);
                    break;
                case R.id.cardView5:
                    try {
                        Utils.campaignEvent(getActivity(), "HomeScreen", "HomeScreenCarousel", "CTA_Campaign_Carousel",
                                "" + campaignListDataModels.get(4).getName(), "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "CTA_Campaign_Carousel");
                    } catch (Exception e) {

                    }
                    Intent campaignIntent5 = new Intent(getActivity(), CampaignContainerActivity.class);
                    campaignIntent5.putExtra("campaign_id", campaignListDataModels.get(4).getId() + "");
                    campaignIntent5.putExtra("campaign_detail", "campaign_detail");
                    startActivity(campaignIntent5);
                    break;
                case R.id.authorNameTextView:
                    int limitShortStory;
                    if (Constants.KEY_FOR_YOU.equals(sortType)) {
                        limitShortStory = FORYOU_LIMIT;
                    } else {
                        limitShortStory = LIMIT;
                    }
                    int pageShortStory = (position / limitShortStory);
                    int posSubListShortStory = position % limitShortStory;
                    int startIndexShortStory = pageShortStory * limitShortStory;
                    int endIndexShortStory = startIndexShortStory + limitShortStory;
                    ArrayList<ArticleListingResult> articleDataModelsSubList1 = new ArrayList<>(articleDataModelsNew.subList(startIndexShortStory, endIndexShortStory));
                    if ("1".equals(articleDataModelsNew.get(position).getContentType())) {
                        Intent pIntent = new Intent(getActivity(), UserProfileActivity.class);
                        pIntent.putExtra(Constants.USER_ID, articleDataModelsSubList1.get(posSubListShortStory).getUserId());
                        startActivity(pIntent);
                    }
                    break;
                case R.id.headerArticleView:
                case R.id.fbAdArticleView:
                case R.id.storyHeaderView:
                case R.id.storyImageView1:
                default:
                    int limit;
                    if (Constants.KEY_FOR_YOU.equals(sortType)) {
                        limit = FORYOU_LIMIT;
                    } else {
                        limit = LIMIT;
                    }
                    int page = (position / limit);
                    int posSubList = position % limit;
                    int startIndex = page * limit;
                    int endIndex = startIndex + limit;
                    ArrayList<ArticleListingResult> articleDataModelsSubList = new ArrayList<>(articleDataModelsNew.subList(startIndex, endIndex));
                    if ("1".equals(articleDataModelsNew.get(position).getContentType())) {
                        Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                        if (Constants.KEY_FOR_YOU.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "ForYouScreen");
                        } /*else if (Constants.KEY_EDITOR_PICKS.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "EditorsPickScreen");
                        }*/ else if (Constants.KEY_RECENT.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "RecentScreen");
                        } else if (Constants.KEY_TODAYS_BEST.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "TodaysBestScreen");
                        } else if (Constants.KEY_TRENDING.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "AllTrending");
                        } else if (Constants.KEY_FOLLOWING.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "FollowingContentScreen");
                        }
                        intent.putExtra(Constants.ARTICLE_ID, articleDataModelsSubList.get(posSubList).getId());
                        intent.putExtra(Constants.AUTHOR_ID, articleDataModelsSubList.get(posSubList).getUserId());
                        intent.putExtra(Constants.BLOG_SLUG, articleDataModelsSubList.get(posSubList).getBlogPageSlug());
                        intent.putExtra(Constants.TITLE_SLUG, articleDataModelsSubList.get(posSubList).getTitleSlug());
                        intent.putExtra(Constants.FROM_SCREEN, "HomeScreen");
                        intent.putExtra(Constants.AUTHOR, articleDataModelsSubList.get(posSubList).getUserId() + "~" + articleDataModelsSubList.get(posSubList).getUserName());
                        ArrayList<ArticleListingResult> filteredResult = AppUtils.getFilteredContentList(articleDataModelsSubList, AppConstants.CONTENT_TYPE_SHORT_STORY);
                        intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                        intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(posSubList, articleDataModelsSubList, AppConstants.CONTENT_TYPE_SHORT_STORY));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, ArticleDetailsContainerActivity.class);
                        if (Constants.KEY_FOR_YOU.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "ForYouScreen");
                        } /*else if (Constants.KEY_EDITOR_PICKS.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "EditorsPickScreen");
                        }*/ else if (Constants.KEY_RECENT.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "RecentScreen");
                        } else if (Constants.KEY_TODAYS_BEST.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "TodaysBestScreen");
                        } else if (Constants.KEY_TRENDING.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "AllTrending");
                        } else if (Constants.KEY_FOLLOWING.equalsIgnoreCase(sortType)) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "FollowingContentScreen");
                        }
                        intent.putExtra(Constants.ARTICLE_ID, articleDataModelsSubList.get(posSubList).getId());
                        intent.putExtra(Constants.AUTHOR_ID, articleDataModelsSubList.get(posSubList).getUserId());
                        intent.putExtra(Constants.BLOG_SLUG, articleDataModelsSubList.get(posSubList).getBlogPageSlug());
                        intent.putExtra(Constants.TITLE_SLUG, articleDataModelsSubList.get(posSubList).getTitleSlug());
                        intent.putExtra(Constants.FROM_SCREEN, "HomeScreen");
                        intent.putExtra(Constants.AUTHOR, articleDataModelsSubList.get(posSubList).getUserId() + "~" + articleDataModelsSubList.get(posSubList).getUserName());
                        ArrayList<ArticleListingResult> filteredResult = AppUtils.getFilteredContentList(articleDataModelsSubList, AppConstants.CONTENT_TYPE_ARTICLE);
                        intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                        intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(posSubList, articleDataModelsSubList, AppConstants.CONTENT_TYPE_ARTICLE));
                        startActivity(intent);
                    }
                    break;
                case R.id.menuItem: {
                    chooseMenuOptionsItem(view, position);
                }
                break;
                case R.id.followAuthorTextView:
                    followAPICall(articleDataModelsNew.get(position).getUserId(), position);
                    break;
                case R.id.whatsappShareImageView:
                    getSharableViewForPosition(position, AppConstants.MEDIUM_WHATSAPP);
                    break;
                case R.id.facebookShareImageView:
                    getSharableViewForPosition(position, AppConstants.MEDIUM_FACEBOOK);
                    break;
                case R.id.instagramShareImageView:
                    try {
                        filterTags(articleDataModelsNew.get(position).getTags());
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                    getSharableViewForPosition(position, AppConstants.MEDIUM_INSTAGRAM);
                    break;
                case R.id.genericShareImageView: {
                    try {
                        AddCollectionAndCollectionItemDialogFragment addCollectionAndCollectionitemDialogFragment = new AddCollectionAndCollectionItemDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("articleId", articleDataModelsNew.get(position).getId());
                        bundle.putString("type", AppConstants.SHORT_STORY_COLLECTION_TYPE);
                        addCollectionAndCollectionitemDialogFragment.setArguments(bundle);
                        FragmentManager fm = getFragmentManager();
                        addCollectionAndCollectionitemDialogFragment.setTargetFragment(this, 0);
                        addCollectionAndCollectionitemDialogFragment.show(fm, "collectionAdd");
                        Utils.pushProfileEvents(getActivity(), "CTA_100WS_Add_To_Collection",
                                "ArticleListingFragment", "Add to Collection", "-");
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                    break;
                }
                case R.id.storyRecommendationContainer:
                    if (!isRecommendRequestRunning) {
                        if (articleDataModelsNew.get(position).isLiked()) {
                        } else {
                            likeStatus = "1";
                            currentShortStoryPosition = position;
                            recommendUnrecommentArticleAPI("1", articleDataModelsNew.get(position).getId(), articleDataModelsNew.get(position).getUserId(), articleDataModelsNew.get(position).getUserName());
                        }
                    }
                    break;

            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void filterTags(ArrayList<Map<String, String>> tagObjectList) {
        ArrayList<String> tagList = new ArrayList<>();
        for (int i = 0; i < tagObjectList.size(); i++) {
            for (Map.Entry<String, String> mapEntry : tagObjectList.get(i).entrySet()) {
                if (mapEntry.getKey().startsWith("category-")) {
                    tagList.add(mapEntry.getKey());
                }
            }
        }

        String hashtags = AppUtils.getHasTagFromCategoryList(tagList);
        AppUtils.copyToClipboard(hashtags);
        if (isAdded())
            ToastUtils.showToast(getActivity(), getActivity().getString(R.string.all_insta_share_clipboard_msg));
    }

    private void recommendUnrecommentArticleAPI(String status, String articleId, String authorId, String author) {
        Utils.pushLikeStoryEvent(getActivity(), "ArticleListingFragment", userDynamoId + "", articleId, authorId + "~" + author);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);

        isRecommendRequestRunning = true;
        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest = new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(articleId);
        recommendUnrecommendArticleRequest.setStatus(likeStatus);
        Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle = articleDetailsAPI.recommendUnrecommendArticle(recommendUnrecommendArticleRequest);
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback);
    }

    private Callback<RecommendUnrecommendArticleResponse> recommendUnrecommendArticleResponseCallback = new Callback<RecommendUnrecommendArticleResponse>() {
        @Override
        public void onResponse(Call<RecommendUnrecommendArticleResponse> call, retrofit2.Response<RecommendUnrecommendArticleResponse> response) {
            isRecommendRequestRunning = false;
            if (response == null || null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ToastUtils.showToast(getActivity(), getString(R.string.server_went_wrong));
                return;
            }
            try {
                RecommendUnrecommendArticleResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (likeStatus.equals("1")) {
                        if (!responseData.getData().isEmpty()) {
                            articleDataModelsNew.get(currentShortStoryPosition).setLikesCount("" + (Integer.parseInt(articleDataModelsNew.get(currentShortStoryPosition).getLikesCount()) + 1));
                        }
                        articleDataModelsNew.get(currentShortStoryPosition).setLiked(true);
                    } else {
                        if (!responseData.getData().isEmpty()) {
                            articleDataModelsNew.get(currentShortStoryPosition).setLikesCount("" + (Integer.parseInt(articleDataModelsNew.get(currentShortStoryPosition).getLikesCount()) - 1));
                        }
                        articleDataModelsNew.get(currentShortStoryPosition).setLiked(false);
                    }
                    recyclerAdapter.notifyDataSetChanged();
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), responseData.getReason());
                    }
                } else {
                    if (isAdded())
                        ToastUtils.showToast(getActivity(), getString(R.string.server_went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ToastUtils.showToast(getActivity(), getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<RecommendUnrecommendArticleResponse> call, Throwable t) {
            isRecommendRequestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


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

    @SuppressLint("RestrictedApi")
    private void chooseMenuOptionsItem(View view, int position) {
        final androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.choose_short_story_menu, popupMenu.getMenu());
        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            Drawable drawable = popupMenu.getMenu().getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.app_red), PorterDuff.Mode.SRC_ATOP);
            }
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.shareShortStory) {
                if (isAdded()) {
                    getSharableViewForPosition(position, AppConstants.MEDIUM_GENERIC);
                }


                return true;
            } else if (item.getItemId() == R.id.bookmarkShortStory) {


                return true;
            } else if (item.getItemId() == R.id.reportContentShortStory) {
                ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("postId", articleDataModelsNew.get(position).getId());
                _args.putInt("type", AppConstants.REPORT_TYPE_STORY);
                reportContentDialogFragment.setArguments(_args);
                reportContentDialogFragment.setCancelable(true);
                reportContentDialogFragment.show(fm, "Report Content");
                return true;
            }


            return false;
        });

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(view.getContext(), (MenuBuilder) popupMenu.getMenu(), view);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.show();
    }


    private void followAPICall(String authorId, int position) {
        this.position = position;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);
        if (articleDataModelsNew.get(position).getIsfollowing().equals("1")) {
            Utils.pushGenericEvent(getActivity(), "CTA_Unfollow_100WS_Detail", userDynamoId, "TopicsShortStoryTabFragment");
            Call<ResponseBody> followUnfollowUserResponseCall = followAPI.unfollowUserInShortStoryListing(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            Utils.pushGenericEvent(getActivity(), "CTA_Follow_100WS_Detail", userDynamoId, "TopicsShortStoryTabFragment");
            Call<ResponseBody> followUnfollowUserResponseCall = followAPI.followUserInShortStoryListing(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }
    }

    private Callback<ResponseBody> unfollowUserResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {
                if (isAdded())
                    ToastUtils.showToast(getActivity(), "some thing went wrong ");
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jObject = new JSONObject(resData);
                int code = jObject.getInt("code");
                String status = jObject.getString("status");
                String reason = jObject.getString("reason");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    articleDataModelsNew.get(position).setIsfollowing("0");
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast(getActivity(), reason);
                }
            } catch (Exception e) {
                if (isAdded())
                    ToastUtils.showToast(getActivity(), "some thing went wrong at the server ");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            if (isAdded())
                ToastUtils.showToast(getActivity(), "some thing went wrong at the server ");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<ResponseBody> followUserResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {
                if (isAdded())
                    ToastUtils.showToast(getActivity(), "some thing went wrong ");
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jObject = new JSONObject(resData);
                int code = jObject.getInt("code");
                String status = jObject.getString("status");
                String reason = jObject.getString("reason");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    articleDataModelsNew.get(position).setIsfollowing("1");
                    recyclerAdapter.notifyDataSetChanged();
                } else if (code == 200 && "failure".equals(status) && "Already following!".equals(reason)) {
                    articleDataModelsNew.get(position).setIsfollowing("1");
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast(getActivity(), reason);
                }
            } catch (Exception e) {
                if (isAdded())
                    ToastUtils.showToast(getActivity(), "some thing went wrong at the server ");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            if (isAdded())
                ToastUtils.showToast(getActivity(), "some thing went wrong at the server ");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    }).show();
        } else {
            requestUngrantedPermissions();
        }
    }

    private void requestUngrantedPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (String s : PERMISSIONS_INIT) {
            if (ActivityCompat.checkSelfPermission(getActivity(), s)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(s);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        requestPermissions(requiredPermission, REQUEST_INIT_PERMISSION);
    }


    private void checkPermissionAndCreateShareableImage() {
        if (Build.VERSION.SDK_INT >= 23 && isAdded()) {
            if (ActivityCompat
                    .checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat
                    .checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
            } else {
                try {
                    createBitmapForSharingStory();
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }
        } else {
            try {
                createBitmapForSharingStory();
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INIT_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                createBitmapForSharingStory();
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getSharableViewForPosition(int position, String medium) {
        storyShareCardWidget = recyclerView.getLayoutManager().findViewByPosition(position).findViewById(R.id.storyShareCardWidget);
        shareStoryImageView = storyShareCardWidget.findViewById(R.id.storyImageView);
        shareMedium = medium;
        sharedStoryItem = articleDataModelsNew.get(position);
        checkPermissionAndCreateShareableImage();
    }

    private void createBitmapForSharingStory() {
        if (isAdded()) {
            Bitmap bitmap1 = ((BitmapDrawable) shareStoryImageView.getDrawable()).getBitmap();
            shareStoryImageView.setImageBitmap(SharingUtils.getRoundCornerBitmap(bitmap1, AppUtils.dpTopx(4.0f)));
            AppUtils.getBitmapFromView(storyShareCardWidget, AppConstants.STORY_SHARE_IMAGE_NAME);
            shareStory();
        }
    }

    private void shareStory() {
        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() +
                "/MyCity4Kids/videos/" + AppConstants.STORY_SHARE_IMAGE_NAME + ".jpg");
        if (isAdded()) {
            switch (shareMedium) {
                case AppConstants.MEDIUM_FACEBOOK: {
                    SharingUtils.shareViaFacebook(this);
                    Utils.pushShareStoryEvent(getActivity(), "ArticleListingFragment",
                            userDynamoId + "", sharedStoryItem.getId(),
                            sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Facebook");
                }
                break;
                case AppConstants.MEDIUM_WHATSAPP: {
                    if (AppUtils.shareImageWithWhatsApp(getActivity(), uri, getString(R.string.ss_follow_author,
                            sharedStoryItem.getUserName(), AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId()))) {
                        Utils.pushShareStoryEvent(getActivity(), "ArticleListingFragment",
                                userDynamoId + "", sharedStoryItem.getId(),
                                sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Whatsapp");
                    }
                }
                break;
                case AppConstants.MEDIUM_INSTAGRAM: {
                    if (AppUtils.shareImageWithInstagram(getActivity(), uri)) {
                        Utils.pushShareStoryEvent(getActivity(), "ArticleListingFragment",
                                userDynamoId + "", sharedStoryItem.getId(),
                                sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Instagram");
                    }
                }
                break;
                case AppConstants.MEDIUM_GENERIC: {
                    if (AppUtils.shareGenericImageAndOrLink(getActivity(), uri, getString(R.string.ss_follow_author,
                            sharedStoryItem.getUserName(), AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId()))) {
                        Utils.pushShareStoryEvent(getActivity(), "ArticleListingFragment",
                                userDynamoId + "", sharedStoryItem.getId(),
                                sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Generic");
                    }
                }
                break;
            }
        }
    }
}