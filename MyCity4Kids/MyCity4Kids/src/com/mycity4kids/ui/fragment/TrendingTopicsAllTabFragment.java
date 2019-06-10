package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
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
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ChooseVideoCategoryActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.ExploreArticleListingTypeActivity;
import com.mycity4kids.ui.activity.MomsVlogDetailActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.GroupIdCategoryMap;
import com.mycity4kids.utils.MixPanelUtils;
import com.mycity4kids.widget.FeedNativeAd;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class TrendingTopicsAllTabFragment extends BaseFragment implements GroupIdCategoryMap.GroupCategoryInterface, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, /*FeedNativeAd.AdLoadingListener,*/ MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    private int nextPageNumber = 1;
    private int limit = 10;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private ArrayList<VlogsListingAndDetailResult> carouselVideoList;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private MainArticleRecyclerViewAdapter recyclerAdapter;

    private ProgressBar progressBar;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private boolean isHeaderVisible = false;
    private RecyclerView recyclerView;
    private FeedNativeAd feedNativeAd;
    ShimmerFrameLayout mshimmerFrameLayout;
    //    private SwipeRefreshLayout swipe_refresh_layout;
    private MixpanelAPI mixpanel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_article_layout, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mshimmerFrameLayout = (ShimmerFrameLayout) view.findViewById(R.id.shimmer1);

        String gpHeading = getArguments().getString("gpHeading");
        String gpSubHeading = getArguments().getString("gpSubHeading");
        String gpImageUrl = getArguments().getString("gpImageUrl");
        int groupId = getArguments().getInt("groupId");
        // progressBar.setVisibility(View.VISIBLE);
        Utils.pushOpenScreenEvent(getActivity(), "TrendingAllTabFragment", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        progressBar.setVisibility(View.VISIBLE);
        articleDataModelsNew = new ArrayList<ArticleListingResult>();

        long timeDiff = System.currentTimeMillis() - SharedPrefUtils.getLastLoginTimestamp(BaseApplication.getAppContext()) - AppConstants.HOURS_24_TIMESTAMP;
        Log.d("Login Time Diff", "" + timeDiff);
        if (SharedPrefUtils.getFollowTopicApproachChangeFlag(BaseApplication.getAppContext())) {
            if (SharedPrefUtils.getFollowedTopicsCount(getActivity()) < 1 && timeDiff < 0 &&
                    !SharedPrefUtils.isTopicSelectionChanged(BaseApplication.getAppContext()) &&
                    !SharedPrefUtils.getUserSkippedFollowTopicFlag(BaseApplication.getAppContext())) {
                isHeaderVisible = true;
            } else {
                isHeaderVisible = false;
            }
        } else {
            if (SharedPrefUtils.getFollowedTopicsCount(getActivity()) < AppConstants.MINIMUM_TOPICS_FOLLOW_REQUIREMENT) {
                isHeaderVisible = true;
            } else {
                isHeaderVisible = false;
            }
        }
//        if (SharedPrefUtils.getFollowedTopicsCount(getActivity()) < AppConstants.MINIMUM_TOPICS_FOLLOW_REQUIREMENT) {
//            isHeaderVisible = true;
//        } else {
//            isHeaderVisible = false;
//        }
//        feedNativeAd = new FeedNativeAd(getActivity(), this, AppConstants.FB_AD_PLACEMENT_ARTICLE_LISTING);
//        feedNativeAd.loadAds();
        recyclerAdapter = new MainArticleRecyclerViewAdapter(getActivity(), feedNativeAd, this, isHeaderVisible, "TrendingAll", true);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setNewListData(articleDataModelsNew);
//        recyclerAdapter.setGroupInfo(groupId, gpHeading, gpSubHeading, gpImageUrl);
        recyclerView.setAdapter(recyclerAdapter);

        hitFilteredTopicsArticleListingApi(0);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private static final int HIDE_THRESHOLD = 20;
            private int scrolledDistance = 0;
            private boolean controlsVisible = true;

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
                            hitFilteredTopicsArticleListingApi(0);
                        }
                    }
                }
                int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                //show views if first item is first visible position and views are hidden
                if (firstVisibleItem == 0) {
                    if (!controlsVisible) {
                        if (isAdded())
                            ((DashboardActivity) getActivity()).showViews();
                        controlsVisible = true;
                    }
                } else {
                    if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                        if (isAdded())
                            ((DashboardActivity) getActivity()).hideViews();
                        controlsVisible = false;
                        scrolledDistance = 0;
                    } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                        if (isAdded())
                            ((DashboardActivity) getActivity()).showViews();
                        controlsVisible = true;
                        scrolledDistance = 0;
                    }
                }

                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrolledDistance += dy;
                }
            }


        });

        getGroupIdForCurrentCategory();

        return view;
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

    private void getCarouselVideos() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogsList(0, 5, 0, 3, null);
        callRecentVideoArticles.enqueue(carouselVideosResponseCallback);
    }

    private Callback<VlogsListingResponse> carouselVideosResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
//                showToast("Something went wrong from server");
                return;
            }
            try {
                VlogsListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processCarouselResponse(responseData);
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void processCarouselResponse(VlogsListingResponse responseData) {
        ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
        if (dataList == null || dataList.size() == 0) {
            isLastPageReached = true;
            if (null != carouselVideoList && !carouselVideoList.isEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results for search
                carouselVideoList = dataList;
                recyclerAdapter.setCarouselVideos(carouselVideoList);
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            if (nextPageNumber == 1) {
                carouselVideoList = dataList;
            } else {
                carouselVideoList.addAll(dataList);
            }
            recyclerAdapter.setCarouselVideos(carouselVideoList);
            nextPageNumber = nextPageNumber + 1;
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.error_network));
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getTrendingArticles(from, from + limit - 1, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
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
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
                    mshimmerFrameLayout.stopShimmerAnimation();
                    mshimmerFrameLayout.setVisibility(View.GONE);
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                progressBar.setVisibility(View.INVISIBLE);
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
            recyclerAdapter.notifyDataSetChanged();
            nextPageNumber = nextPageNumber + 1;
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    @Override
    public void onRefresh() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            return;
        }
        isLastPageReached = false;
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(0);
    }

//    @Override
//    public void onFinishToLoadAds() {
//
//    }
//
//    @Override
//    public void onErrorToLoadAd() {
//
//    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.videoContainerFL1:
                launchVideoDetailsActivity(position, 0);
                break;
            case R.id.videoContainerFL2:
                launchVideoDetailsActivity(position, 1);
                break;
            case R.id.videoContainerFL3:
                launchVideoDetailsActivity(position, 2);
                break;
            case R.id.videoContainerFL4:
                launchVideoDetailsActivity(position, 3);
                break;
            case R.id.videoContainerFL5:
                launchVideoDetailsActivity(position, 4);
                break;
            case R.id.addVideoContainer:
            case R.id.addMomVlogTextView: {
                MixPanelUtils.pushAddMomVlogClickEvent(mixpanel, "TrendingAll");
                Intent intent = new Intent(getActivity(), ChooseVideoCategoryActivity.class);
                startActivity(intent);
            }
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
//            case R.id.groupHeaderView:
//                GroupsFragment groupsFragment = new GroupsFragment();
//                Bundle bundle = new Bundle();
//                groupsFragment.setArguments(bundle);
//                ((DashboardActivity) getActivity()).addFragment(groupsFragment, bundle, true);
//                break;
            case R.id.headerArticleView:
            case R.id.fbAdArticleView:
            case R.id.storyHeaderView:
            default:
                if ("1".equals(articleDataModelsNew.get(position).getContentType())) {
                    Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "AllTrending");
                    intent.putExtra(Constants.FROM_SCREEN, "HomeScreen");
                    intent.putExtra(Constants.AUTHOR, articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());

                    ArrayList<ArticleListingResult> filteredResult = AppUtils.getFilteredContentList(articleDataModelsNew, AppConstants.CONTENT_TYPE_SHORT_STORY);
                    intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(position, articleDataModelsNew, AppConstants.CONTENT_TYPE_SHORT_STORY));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "AllTrending");
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

    private void launchVideoDetailsActivity(int position, int videoIndex) {
        MixPanelUtils.pushMomVlogClickEvent(mixpanel, videoIndex, "TrendingAll");
        if (articleDataModelsNew.get(position).getCarouselVideoList() != null && !articleDataModelsNew.get(position).getCarouselVideoList().isEmpty()) {
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

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void hideFollowTopicHeader() {
        isHeaderVisible = false;
        recyclerAdapter.hideFollowTopicHeader();
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        mshimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mshimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
    }
}
