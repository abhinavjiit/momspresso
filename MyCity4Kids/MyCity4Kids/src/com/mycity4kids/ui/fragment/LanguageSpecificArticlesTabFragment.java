package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.widget.FeedNativeAd;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class LanguageSpecificArticlesTabFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, /*FeedNativeAd.AdLoadingListener,*/ MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    private int nextPageNumber = 1;
    private int limit = 15;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private String chunks = "";
    private LanguageConfigModel trendingTopicData;
    private int sortType = 0;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    //    private MainArticleListingAdapter articlesListingAdapter;
    private MainArticleRecyclerViewAdapter recyclerAdapter;
    private ArrayList<ArticleListingResult> articleDataModelsNew;

    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    //    private SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton popularSortFAB;
    private FloatingActionButton recentSortFAB;
    private FloatingActionButton fabSort;
    private RecyclerView recyclerView;
    private FeedNativeAd feedNativeAd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_article_layout, container, false);

//        ListView listView = (ListView) view.findViewById(R.id.scroll);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

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

        if (getArguments() != null) {
            trendingTopicData = getArguments().getParcelable("languageData");
        }
        Log.d("searchName", "" + trendingTopicData.getDisplay_name());

//        swipeRefreshLayout.setOnRefreshListener(this);

//        articlesListingAdapter = new MainArticleListingAdapter(getActivity());
//        listView.setAdapter(articlesListingAdapter);


        articleDataModelsNew = new ArrayList<ArticleListingResult>();
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(sortType);

//        feedNativeAd = new FeedNativeAd(getActivity(), this, AppConstants.FB_AD_PLACEMENT_ARTICLE_LISTING);
//        feedNativeAd.loadAds();
        recyclerAdapter = new MainArticleRecyclerViewAdapter(getActivity(), feedNativeAd, this, false, trendingTopicData.getDisplay_name(), false);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
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
                            hitFilteredTopicsArticleListingApi(sortType);
                        }
                    }
                }
            }
        });
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

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
//                if (adapterView.getAdapter() instanceof MainArticleListingAdapter) {
//                    ArticleListingResult parentingListData = (ArticleListingResult) adapterView.getAdapter().getItem(i);
//                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
//                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
//                    intent.putExtra(Constants.BLOG_SLUG, parentingListData.getBlogPageSlug());
//                    intent.putExtra(Constants.TITLE_SLUG, parentingListData.getTitleSlug());
//                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + trendingTopicData.getDisplay_name());
//                    intent.putExtra(Constants.FROM_SCREEN, "LanguageScreen");
//                    intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
//                    intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
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

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(trendingTopicData.getId(), sortType, from, from + limit - 1, "");
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
//            swipeRefreshLayout.setRefreshing(false);
//            progressBar.setVisibility(View.INVISIBLE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processForYouResponse(responseData);
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

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recentSortFAB:
                fabMenu.collapse();
                articleDataModelsNew.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                chunks = "";
                hitFilteredTopicsArticleListingApi(0);
                break;
            case R.id.popularSortFAB:
                fabMenu.collapse();
                articleDataModelsNew.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                chunks = "";
                hitFilteredTopicsArticleListingApi(1);
                break;
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
        Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
        intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
        intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
        intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
        intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + trendingTopicData.getDisplay_name());
        intent.putExtra(Constants.FROM_SCREEN, "LanguageScreen");
        intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
        intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
        intent.putExtra(Constants.AUTHOR, articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
        startActivity(intent);
    }
}
