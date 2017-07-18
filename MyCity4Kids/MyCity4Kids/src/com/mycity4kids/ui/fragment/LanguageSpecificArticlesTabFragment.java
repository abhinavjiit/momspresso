package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.adapter.MainArticleListingAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class LanguageSpecificArticlesTabFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private int nextPageNumber = 1;
    private int limit = 15;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private String chunks = "";
    private LanguageConfigModel trendingTopicData;

    private MainArticleListingAdapter articlesListingAdapter;
    private ArrayList<ArticleListingResult> articleDataModelsNew;

    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.pushOpenScreenEvent(getActivity(), "LanguageSpecificArticlesTabFragment", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        View view = inflater.inflate(R.layout.new_article_layout, container, false);

        ListView listView = (ListView) view.findViewById(R.id.scroll);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        if (getArguments() != null) {
            trendingTopicData = getArguments().getParcelable("languageData");
        }
        Log.d("searchName", "" + trendingTopicData.getDisplay_name());

        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);

        articlesListingAdapter = new MainArticleListingAdapter(getActivity());
//        adapter.setNewListData(trendingTopicData.getArticleList());
        listView.setAdapter(articlesListingAdapter);

        articleDataModelsNew = new ArrayList<ArticleListingResult>();
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(0);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    hitFilteredTopicsArticleListingApi(0);
                    isReuqestRunning = true;
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                if (adapterView.getAdapter() instanceof MainArticleListingAdapter) {
                    ArticleListingResult parentingListData = (ArticleListingResult) ((MainArticleListingAdapter) adapterView.getAdapter()).getItem(i);
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, parentingListData.getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, parentingListData.getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Trending");
                    intent.putExtra(Constants.FROM_SCREEN, "Article Listing Screen");
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
//                    intent.putParcelableArrayListExtra("pagerListData", trendingTopicData.getArticleList());
                    startActivity(intent);
                }
//                Intent intent = new Intent(getActivity(), ExampleActivity.class);
//                startActivity(intent);
            }
        });

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
        Call<ArticleListingResponse> filterCall = topicsAPI.getFilteredArticlesForCategories(trendingTopicData.getId(), sortType, from, from + limit - 1, SharedPrefUtils.getLanguageFilters(getActivity()));
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            swipeRefreshLayout.setRefreshing(false);
//            progressBar.setVisibility(View.INVISIBLE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
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
                    articlesListingAdapter.setNewListData(dataList);
                    articlesListingAdapter.notifyDataSetChanged();
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

                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            mLodingView.setVisibility(View.GONE);
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

    }

//    private void processArticleListingResponse(ArticleListingResponse responseData) {
//
//        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
//
//        if (dataList.size() == 0) {
//            isLastPageReached = false;
//            if (null != trendingTopicData.getArticleList() && !trendingTopicData.getArticleList().isEmpty()) {
//                //No more next results for search from pagination
//                isLastPageReached = true;
//            } else {
//                // No results for search
//                noBlogsTextView.setVisibility(View.VISIBLE);
//                noBlogsTextView.setText("No articles found");
////                articleDataModelsNew = dataList;
//                trendingTopicData.setArticleList(dataList);
//                adapter.setNewListData(trendingTopicData.getArticleList());
//                adapter.notifyDataSetChanged();
//            }
//        } else {
//            noBlogsTextView.setVisibility(View.GONE);
//            if (nextPageNumber == 1 && (trendingTopicData.getArticleList() == null || trendingTopicData.getArticleList().isEmpty())) {
////                articleDataModelsNew = dataList;
//                trendingTopicData.setArticleList(dataList);
//            } else {
////                articleDataModelsNew.addAll(dataList);
//                trendingTopicData.getArticleList().addAll(dataList);
//            }
//            adapter.setNewListData(trendingTopicData.getArticleList());
//            nextPageNumber = nextPageNumber + 1;
//            adapter.notifyDataSetChanged();
//        }
//
//    }

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
            swipeRefreshLayout.setRefreshing(false);
            removeProgressDialog();
            return;
        }
        isLastPageReached = false;
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(0);
    }
}
