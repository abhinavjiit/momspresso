package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.widget.FeedNativeAd;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 8/8/17.
 */
public class MomspressoVideosTabFragment extends BaseFragment implements View.OnClickListener, FeedNativeAd.AdLoadingListener, MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    private int nextPageNumber = 1;
    private int limit = 15;
    private int sortType = 0;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private ArrayList<ArticleListingResult> mDatalist;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private MainArticleRecyclerViewAdapter recyclerAdapter;
//    private MainArticleListingAdapter adapter;

    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton popularSortFAB;
    private FloatingActionButton recentSortFAB;
    private FloatingActionButton fabSort;
    private RecyclerView recyclerView;
    private FeedNativeAd feedNativeAd;
    ShimmerFrameLayout momvideosshimmer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.momspresso_videos_tab_fragment, container, false);
        momvideosshimmer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_videos_article);
//        ListView listView = (ListView) view.findViewById(R.id.scroll);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        popularSortFAB = (FloatingActionButton) view.findViewById(R.id.popularSortFAB);
        recentSortFAB = (FloatingActionButton) view.findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) view.findViewById(R.id.fabSort);
        frameLayout.setVisibility(View.VISIBLE);
        fabSort.setVisibility(View.VISIBLE);
        popularSortFAB.setOnClickListener(this);
        recentSortFAB.setOnClickListener(this);
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

        mDatalist = new ArrayList<>();
//        adapter = new MainArticleListingAdapter(getActivity());
//        adapter.setNewListData(mDatalist);
//        listView.setAdapter(adapter);
        feedNativeAd = new FeedNativeAd(getActivity(), this, AppConstants.FB_AD_PLACEMENT_ARTICLE_LISTING);
        feedNativeAd.loadAds();
        recyclerAdapter = new MainArticleRecyclerViewAdapter(getActivity(), feedNativeAd, this, false, AppConstants.MOMSPRESSO_CATEGORYID, true);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setNewListData(mDatalist);
        recyclerView.setAdapter(recyclerAdapter);

        hitFilteredTopicsArticleListingApi(sortType);
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
//                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
//                    mLodingView.setVisibility(View.VISIBLE);
//                    hitFilteredTopicsArticleListingApi(sortType);
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
                            hitFilteredTopicsArticleListingApi(sortType);
                        }
                    }
                }
            }
        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
//                if (adapterView.getAdapter() instanceof MainArticleListingAdapter) {
//                    ArticleListingResult parentingListData = (ArticleListingResult) adapterView.getAdapter().getItem(i);
//                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
//                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
//                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getImageUrl());
//                    intent.putExtra(Constants.BLOG_SLUG, parentingListData.getBlogPageSlug());
//                    intent.putExtra(Constants.TITLE_SLUG, parentingListData.getTitleSlug());
//                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Momspresso Videos" + "~" + AppConstants.MOMSPRESSO_CATEGORYID);
//                    intent.putExtra(Constants.FROM_SCREEN, "VideosScreen");
//                    intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
//                    intent.putParcelableArrayListExtra("pagerListData", mDatalist);
//                    intent.putExtra(Constants.AUTHOR, parentingListData.getUserId() + "~" + parentingListData.getUserName());
//                    startActivity(intent);
//                }
//            }
//        });
        return view;

    }

    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.error_network));
            return;
        }
        if (nextPageNumber == 1) {
//            progressBar.setVisibility(View.VISIBLE);
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(AppConstants.MOMSPRESSO_CATEGORYID, sortType, from, from + limit - 1, SharedPrefUtils.getLanguageFilters(getActivity()));
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
//            progressBar.setVisibility(View.INVISIBLE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
//            swipeRefreshLayout.setRefreshing(false);
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
                    momvideosshimmer.stopShimmerAnimation();
                    momvideosshimmer.setVisibility(View.GONE);
                } else {
//                    showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
//            progressBar.setVisibility(View.INVISIBLE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
//            showToast(getString(R.string.went_wrong));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != mDatalist && !mDatalist.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText(getString(R.string.no_articles_found));
                mDatalist = dataList;
//                trendingTopicData.setArticleList(dataList);
                recyclerAdapter.setNewListData(mDatalist);
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                mDatalist = dataList;
//                trendingTopicData.setArticleList(dataList);
            } else {
                mDatalist.addAll(dataList);
//                trendingTopicData.getArticleList().addAll(dataList);
            }
            recyclerAdapter.setNewListData(mDatalist);
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
            case R.id.recentSortFAB:
                momvideosshimmer.startShimmerAnimation();
                momvideosshimmer.setVisibility(View.VISIBLE);
//                Utils.pushSortListingEvent(FilteredTopicsArticleListingActivity.this, GTMEventType.SORT_LISTING_EVENT, SharedPrefUtils.getUserDetailModel(FilteredTopicsArticleListingActivity.this).getDynamoId(),
//                        listingType, "recent");
                fabMenu.collapse();
                mDatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(0);
                break;
            case R.id.popularSortFAB:
                momvideosshimmer.startShimmerAnimation();
                momvideosshimmer.setVisibility(View.VISIBLE);
// momvideosshimmer.startShimmerAnimation();
//                momvideosshimmer.setVisibility(View.VISIBLE);
//  Utils.pushSortListingEvent(FilteredTopicsArticleListingActivity.this, GTMEventType.SORT_LISTING_EVENT, SharedPrefUtils.getUserDetailModel(FilteredTopicsArticleListingActivity.this).getDynamoId(),
//                        listingType, "popular");
                fabMenu.collapse();
                mDatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(1);
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
        Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
        intent.putExtra(Constants.ARTICLE_ID, mDatalist.get(position).getId());
        intent.putExtra(Constants.AUTHOR_ID, mDatalist.get(position).getUserId());
        intent.putExtra(Constants.ARTICLE_COVER_IMAGE, mDatalist.get(position).getImageUrl());
        intent.putExtra(Constants.BLOG_SLUG, mDatalist.get(position).getBlogPageSlug());
        intent.putExtra(Constants.TITLE_SLUG, mDatalist.get(position).getTitleSlug());
        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Momspresso Videos" + "~" + AppConstants.MOMSPRESSO_CATEGORYID);
        intent.putExtra(Constants.FROM_SCREEN, "VideosScreen");
        intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
        intent.putParcelableArrayListExtra("pagerListData", mDatalist);
        intent.putExtra(Constants.AUTHOR, mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        momvideosshimmer.startShimmerAnimation();

    }

    @Override
    public void onPause() {
        super.onPause();
        momvideosshimmer.stopShimmerAnimation();
    }
}
