package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.JsonSyntaxException;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.interfaces.OnWebServiceCompleteListener;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.response.ArticleListingData;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.newmodels.VolleyBaseResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;
import com.mycity4kids.ui.adapter.NewArticlesListingAdapter;
import com.mycity4kids.volley.HttpVolleyRequest;

import java.util.ArrayList;

/**
 * Created by manish.soni on 21-07-2015.
 */
public class ArticleViewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    NewArticlesListingAdapter articlesListingAdapter;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    ListView listView;
    TextView noBlogsTextView;
    String sortType;
    String searchName = "";
    Boolean isSearchActive = false;
    private RelativeLayout mLodingView;
    private int nextPageNumber = 2;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private int from = 1;
    private int to = 15;
    private int limit = 6;
    private String paginationValue = "";
//    private ArrayList<CommonParentingList> mDataList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.new_article_layout, container, false);
        listView = (ListView) view.findViewById(R.id.scroll);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        progressBar.setVisibility(View.VISIBLE);
        if (getArguments() != null) {
            articleDataModelsNew = getArguments().getParcelableArrayList(Constants.ARTICLES_LIST);
            sortType = getArguments().getString(Constants.SORT_TYPE);
        }
//        if ("bookmark".equals(sortType)) {
//            articleDataModelsNew = new ArrayList<CommonParentingList>();
//            nextPageNumber = 1;
//            hitBookmarkedArticleListingAPI(nextPageNumber, "bookmark");
//        } else {
        articleDataModelsNew = new ArrayList<ArticleListingResult>();
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, sortType, true);
//        }
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) ArticleViewFragment.this);

        articlesListingAdapter = new NewArticlesListingAdapter(getActivity(), true);
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

                Intent intent = new Intent(getActivity(), ArticlesAndBlogsDetailsActivity.class);
                if (adapterView.getAdapter() instanceof NewArticlesListingAdapter) {
                    ArticleListingResult parentingListData = (ArticleListingResult) ((NewArticlesListingAdapter) adapterView.getAdapter()).getItem(i);
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getImageUrl());
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    intent.putExtra(Constants.FILTER_TYPE, parentingListData.getUserType());
                    intent.putExtra(Constants.BLOG_NAME, parentingListData.getBlogPageSlug());
                    startActivity(intent);

                }
            }
        });

        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    private void hitArticleListingApi(int pPageCount, String sortKey, boolean isCacheRequired) {

        String url;
        if (!AppConstants.SORT_TYPE_BOOKMARK.equals(sortType)) {
            url = AppConstants.LIVE_URL + AppConstants.SERVICE_TYPE_ARTICLE + sortKey +
                    AppConstants.SEPARATOR_BACKSLASH + from + AppConstants.SEPARATOR_BACKSLASH + to;
            HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, isCacheRequired);
        } else {
            // caching disabled for bookmarked articles as we need to refresh it everytime an article is bookmarked/unbookmarked.
            url = AppConstants.LIVE_URL + AppConstants.SERVICE_TYPE_USER + sortKey + "/?limit=" + limit + "&pagination=" + paginationValue;
            HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, false);
        }

    }

    private OnWebServiceCompleteListener mGetArticleListingListener = new OnWebServiceCompleteListener() {
        @Override
        public void onWebServiceComplete(VolleyBaseResponse response, boolean isError) {
            progressBar.setVisibility(View.GONE);
            if (isError) {
                if (null != getActivity() && response.getResponseCode() != 999)
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
            } else {

                if (response == null) {
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                    isReuqestRunning = false;
                    mLodingView.setVisibility(View.GONE);
                    return;
                }
                Log.d("Response back =", " " + response.getResponseBody());
                ArticleListingResponse responseData;
                try {
                    responseData = new Gson().fromJson(response.getResponseBody(), ArticleListingResponse.class);
                } catch (JsonSyntaxException jse) {
                    Crashlytics.logException(jse);
                    Log.d("JsonSyntaxException", Log.getStackTraceString(jse));
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.server_error));
                    return;
                }

                swipeRefreshLayout.setRefreshing(false);
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processResponse(responseData);
                } else if (responseData.getCode() == 400) {
                    String message = responseData.getReason();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ((DashboardActivity) getActivity()).showToast(message);
                    } else {
                        ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    }
                }

                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);
            }

        }
    };

    public void refreshSubList(ArrayList<ArticleListingResult> newList) {
        articleDataModelsNew = newList;
        articlesListingAdapter.setNewListData(newList);
        articlesListingAdapter.notifyDataSetChanged();
    }

    private void processResponse(ArticleListingResponse responseData) {
        //	parentingResponse = responseData ;
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

//            articleDataModelsNew = dataList;
//            articlesListingAdapter.setNewListData(articleDataModelsNew);
//            articlesListingAdapter.notifyDataSetChanged();
//            noBlogsTextView.setVisibility(View.VISIBLE);
//            noBlogsTextView.setText("No articles found");
            } else {
                noBlogsTextView.setVisibility(View.GONE);
//            totalPageCount = responseData.getResult().getData().getPage_count();

                if (AppConstants.SORT_TYPE_BOOKMARK.equals(sortType)) {
                    if (StringUtils.isNullOrEmpty(paginationValue)) {
                        articleDataModelsNew = dataList;
                    } else {
                        articleDataModelsNew.addAll(dataList);
                    }
                    paginationValue = responseData.getData().getPagination();
                    if (AppConstants.PAGINATION_END_VALUE.equals(paginationValue)) {
                        isLastPageReached = true;
                    }

                } else {
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
                }
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            removeVolleyCache(sortType);
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

    }

//    private void getArticleResponse(ArticleListingResponse responseData) {
//        //	parentingResponse = responseData ;
//        ArrayList<ArticleListingResult> dataList = responseData.getData().getResult();
//
//        if (dataList.size() == 0) {
//            articleDataModelsNew = dataList;
//            articlesListingAdapter.setNewListData(articleDataModelsNew);
//            articlesListingAdapter.notifyDataSetChanged();
//            noBlogsTextView.setVisibility(View.VISIBLE);
//            //((DashboardActivity) getActivity()).showToast(responseData.getResult().getMessage());
//        } else {
//            noBlogsTextView.setVisibility(View.GONE);
//            totalPageCount = 5;//responseData.getResult().getData().getPage_count();
//            if (nextPageNumber == 1) {
//                articleDataModelsNew = dataList;
//            } else {
//
//                //cache refresh request response and response from pagination may overlap causing duplication
//                // to prevent check the page number in response
//                //-- open article listing and immediately scroll to next page to reproduce.
//                if (!"bookmark".equals(sortType) && responseData.getData().getPageNumber() < nextPageNumber) {
//                    //Response from cache refresh request. Update the dataset and refresh list
//                    int articleNumber = (responseData.getResult().getData().getPageNumber() - 1) * 15;
//                    for (int i = 0; i < dataList.size(); i++) {
//                        articleDataModelsNew.set(articleNumber + i, dataList.get(i));
//                    }
//                    articlesListingAdapter.setNewListData(articleDataModelsNew);
//                    articlesListingAdapter.notifyDataSetChanged();
//                    return;
//                } else {
//                    articleDataModelsNew.addAll(dataList);
//                }
//            }
//            articlesListingAdapter.setNewListData(articleDataModelsNew);
//            nextPageNumber = nextPageNumber + 1;
//            articlesListingAdapter.notifyDataSetChanged();
//        }
//    }

    public void refreshBookmarkList() {
        nextPageNumber = 1;
        isLastPageReached = false;
        paginationValue = "";
        hitArticleListingApi(nextPageNumber, AppConstants.SORT_TYPE_BOOKMARK, false);
    }

    @Override
    public void onRefresh() {
        isLastPageReached = false;
        removeVolleyCache(sortType);
        paginationValue = "";
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
}
