package com.mycity4kids.ui.fragment;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
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
import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ChooseVideoCategoryActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.MomsVlogDetailActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.GroupIdCategoryMap;
import com.mycity4kids.utils.MixPanelUtils;
import com.mycity4kids.widget.FeedNativeAd;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class TrendingTopicsTabFragment extends BaseFragment implements GroupIdCategoryMap.GroupCategoryInterface,View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, /*FeedNativeAd.AdLoadingListener,*/ MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    private int nextPageNumber = 2;
    private int limit = 10;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private TrendingListingResult trendingTopicData;

    private MainArticleRecyclerViewAdapter recyclerAdapter;

    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private RecyclerView recyclerView;
    private FeedNativeAd feedNativeAd;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private MixpanelAPI mixpanel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_article_layout, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);

        if (getArguments() != null) {
            trendingTopicData = getArguments().getParcelable("trendingTopicsData");
        }

        getGroupIdForCurrentCategory();

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

//        feedNativeAd = new FeedNativeAd(getActivity(), this, AppConstants.FB_AD_PLACEMENT_ARTICLE_LISTING);
//        feedNativeAd.loadAds();
        recyclerAdapter = new MainArticleRecyclerViewAdapter(getActivity(), feedNativeAd, this, false, "Trending-" + trendingTopicData.getDisplay_name(), true);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setNewListData(trendingTopicData.getArticleList());
        recyclerView.setAdapter(recyclerAdapter);


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

        return view;
    }

    private void getGroupIdForCurrentCategory() {
                GroupIdCategoryMap groupIdCategoryMap = new GroupIdCategoryMap(trendingTopicData.getId(), this, "listing");
                groupIdCategoryMap.getGroupIdForCurrentCategory();
            }


    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.error_network));
            return;
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;

        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(trendingTopicData.getId(), sortType, from, from + limit - 1, SharedPrefUtils.getLanguageFilters(getActivity()));
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
//            swipeRefreshLayout.setRefreshing(false);
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
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != trendingTopicData.getArticleList() && !trendingTopicData.getArticleList().isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText(getString(R.string.no_articles_found));
//                articleDataModelsNew = dataList;
                trendingTopicData.setArticleList(dataList);
                recyclerAdapter.setNewListData(trendingTopicData.getArticleList());
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1 && (trendingTopicData.getArticleList() == null || trendingTopicData.getArticleList().isEmpty())) {
                trendingTopicData.setArticleList(dataList);
            } else {
                trendingTopicData.getArticleList().addAll(dataList);
            }
            recyclerAdapter.setNewListData(trendingTopicData.getArticleList());
            nextPageNumber = nextPageNumber + 1;
            recyclerAdapter.notifyDataSetChanged();
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
//            swipeRefreshLayout.setRefreshing(false);
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
            case R.id.addMomVlogTextView:
            case R.id.addVideoContainer: {
                MixPanelUtils.pushAddMomVlogClickEvent(mixpanel, "Trending-" + trendingTopicData.getDisplay_name());
                Utils.momVlogEvent(getActivity(), "Home Screen", "Carousel_banner_add_vlog", "", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_video_creation_categories", "", "");

                Intent intent = new Intent(getActivity(), ChooseVideoCategoryActivity.class);
                startActivity(intent);
            }
            break;
            default:
                if ("1".equals(trendingTopicData.getArticleList().get(position).getContentType())) {
                    Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, trendingTopicData.getArticleList().get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, trendingTopicData.getArticleList().get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, trendingTopicData.getArticleList().get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, trendingTopicData.getArticleList().get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "AllTrending");
                    intent.putExtra(Constants.FROM_SCREEN, "HomeScreen");
                    intent.putExtra(Constants.AUTHOR, trendingTopicData.getArticleList().get(position).getUserId() + "~" + trendingTopicData.getArticleList().get(position).getUserName());

                    ArrayList<ArticleListingResult> filteredResult = AppUtils.getFilteredContentList(trendingTopicData.getArticleList(), AppConstants.CONTENT_TYPE_SHORT_STORY);
                    intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(position, trendingTopicData.getArticleList(), AppConstants.CONTENT_TYPE_SHORT_STORY));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, trendingTopicData.getArticleList().get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, trendingTopicData.getArticleList().get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, trendingTopicData.getArticleList().get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, trendingTopicData.getArticleList().get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "AllTrending");
                    intent.putExtra(Constants.FROM_SCREEN, "HomeScreen");
                    intent.putExtra(Constants.AUTHOR, trendingTopicData.getArticleList().get(position).getUserId() + "~" + trendingTopicData.getArticleList().get(position).getUserName());

                    ArrayList<ArticleListingResult> filteredResult = AppUtils.getFilteredContentList(trendingTopicData.getArticleList(), AppConstants.CONTENT_TYPE_ARTICLE);
                    intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(position, trendingTopicData.getArticleList(), AppConstants.CONTENT_TYPE_ARTICLE));
                    startActivity(intent);
                }
                break;
        }
    }

    private void launchVideoDetailsActivity(int position, int videoIndex) {
        MixPanelUtils.pushMomVlogClickEvent(mixpanel, videoIndex, "Trending-" + trendingTopicData.getDisplay_name());
        if (trendingTopicData.getArticleList().get(position).getCarouselVideoList() != null && !trendingTopicData.getArticleList().get(position).getCarouselVideoList().isEmpty()) {
            VlogsListingAndDetailResult result = trendingTopicData.getArticleList().get(position).getCarouselVideoList().get(videoIndex);
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

    @Override
    public void onGroupMappingResult(int groupId, String gpHeading, String gpSubHeading, String gpImageUrl) {
        recyclerAdapter.setGroupInfo(groupId, gpHeading, gpSubHeading, gpImageUrl);
                recyclerAdapter.notifyDataSetChanged();
    }
}
