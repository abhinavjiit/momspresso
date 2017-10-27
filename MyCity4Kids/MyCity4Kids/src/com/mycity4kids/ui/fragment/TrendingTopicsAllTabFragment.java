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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.ExploreArticleListingTypeFragment;
import com.mycity4kids.ui.adapter.MainArticleListingAdapter;
import com.mycity4kids.volley.HttpVolleyRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class TrendingTopicsAllTabFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private int nextPageNumber = 1;
    private int limit = 10;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private ArrayList<ArticleListingResult> articleDataModelsNew;

    private MainArticleListingAdapter adapter;

    private ProgressBar progressBar;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isHeaderVisible = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_article_layout, container, false);

        final ListView listView = (ListView) view.findViewById(R.id.scroll);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setOnRefreshListener(this);
        articleDataModelsNew = new ArrayList<ArticleListingResult>();

        adapter = new MainArticleListingAdapter(getActivity());
        adapter.setNewListData(articleDataModelsNew);

        if (SharedPrefUtils.getFollowedTopicsCount(getActivity()) < AppConstants.MINIMUM_TOPICS_FOLLOW_REQUIREMENT) {
            View headerView = inflater.inflate(R.layout.trending_list_header_item, null, false);
            listView.addHeaderView(headerView);
            isHeaderVisible = true;
        } else {
            isHeaderVisible = false;
        }
        listView.setAdapter(adapter);

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
                ArticleListingResult parentingListData = (ArticleListingResult) adapterView.getItemAtPosition(i);

                if (null == parentingListData) {
                    ExploreArticleListingTypeFragment searchTopicFrag = new ExploreArticleListingTypeFragment();
                    Bundle searchBundle = new Bundle();
                    searchBundle.putString("fragType", "search");
                    searchTopicFrag.setArguments(searchBundle);
                    ((DashboardActivity) getActivity()).addFragment(searchTopicFrag, searchBundle, true);
                } else {
                    Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, parentingListData.getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, parentingListData.getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "AllTrending");
                    intent.putExtra(Constants.FROM_SCREEN, "HomeScreen");
                    if (isHeaderVisible == true) {
                        intent.putExtra(Constants.ARTICLE_INDEX, "" + (i - 1));
                    } else {
                        intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
                    }

                    intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
                    intent.putExtra(Constants.AUTHOR, parentingListData.getUserId() + "~" + parentingListData.getUserName());
                    startActivity(intent);
                }

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
//        String url = AppConstants.LIVE_URL + AppConstants.SERVICE_TYPE_ARTICLE + "trending" +
//                AppConstants.SEPARATOR_BACKSLASH + "1" + AppConstants.SEPARATOR_BACKSLASH + "3" + "?lang=" + SharedPrefUtils.getLanguageFilters(getActivity());
//        HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, true);
//        Utils.pushOpenArticleListingEvent(this, GTMEventType.ARTICLE_LISTING_CLICK_EVENT, "fromScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(), displayName + "~" + selectedTopics, "" + nextPageNumber);
        Call<ArticleListingResponse> filterCall = topicsAPI.getTrendingArticles(from, from + limit - 1);
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.INVISIBLE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
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
                noBlogsTextView.setText("No articles found");
                articleDataModelsNew = dataList;
                adapter.setNewListData(articleDataModelsNew);
                adapter.notifyDataSetChanged();
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            adapter.setNewListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            adapter.notifyDataSetChanged();
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
            swipeRefreshLayout.setRefreshing(false);
            removeProgressDialog();
            return;
        }
        isLastPageReached = false;
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(0);
    }
}
