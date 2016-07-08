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
    private int totalPageCount = 3;
    private int nextPageNumber = 2;
    private boolean isLastPageReached = true;
    private boolean isReuqestRunning = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private int from = 1;
    private int to = 15;
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
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
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

    private void hitArticleListingApi(int pPageCount, String SortKey, boolean isCacheRequired) {

        String url;
        StringBuilder builder = new StringBuilder();
        if (!"bookmark".equals(sortType)) {
            builder.append("city_id=").append(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
            builder.append("&page=").append(pPageCount);
            builder.append("&sort=").append(SortKey);
//            to = pPageCount * 10;
//            from = to - 9;
//            url = AppConstants.PHOENIX_ARTICLE_STAGING_URL + "v1/articles/recent/" + from + "/" + to;
            url = AppConstants.PHOENIX_ARTICLE_STAGING_URL + "v1/articles/" + SortKey + "/" + from + "/" + to;
//            url = AppConstants.PHOENIX_ARTICLE_STAGING_URL + builder.toString().replace(" ", "%20");
            HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, isCacheRequired);
        } else {
            builder.append("user_id=").append(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
            builder.append("&page=").append(pPageCount);
//            url = AppConstants.FETCH_BOOKMARK_URL + builder.toString().replace(" ", "%20");
            to = pPageCount * 10;
            from = to - 9;
            url = AppConstants.PHOENIX_ARTICLE_STAGING_URL + "v1/articles/recent/" + from + "/" + to;
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
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                    return;
                }

                swipeRefreshLayout.setRefreshing(false);
                if (responseData.getCode() == 200) {
                    processResponse(responseData);
                } else if (responseData.getCode() == 400) {
//                    String message = responseData.getResult().getMessage();
                    totalPageCount = 0;
                    if (!StringUtils.isNullOrEmpty("dwadw")) {
                        ((DashboardActivity) getActivity()).showToast("dwadwdawdawd");
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
        ArrayList<ArticleListingResult> dataList = responseData.getData().getResult();

        if (dataList.size() == 0) {

            isLastPageReached = false;
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
            if (from == 1) {
                articleDataModelsNew = dataList;
            } else {

                int prevFrom = Integer.parseInt(responseData.getData().getChunks().split("-")[0]);
                //cache refresh request response and response from pagination may overlap causing duplication
                // to prevent check the page number in response
                //-- open article listing and immediately scroll to next page to reproduce.
                if (!"bookmark".equals(sortType) && prevFrom < from) {
                    //Response from cache refresh request. Update the dataset and refresh list
//                    int articleNumber = (responseData.getResult().getData().getPageNumber() - 1) * 15;
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
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            from = from + 15;
            to = to + 15;
            articlesListingAdapter.notifyDataSetChanged();
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
        hitArticleListingApi(nextPageNumber, "bookmark", false);
    }

    @Override
    public void onRefresh() {
        removeVolleyCache(sortType);
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, sortType, false);
    }

    private void removeVolleyCache(String sortType) {
        if ("bookmark".equals(sortType))
            return;

        StringBuilder builder = new StringBuilder();
        int cachePageNumber = 1;
        builder.append("city_id=").append(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        builder.append("&page=").append(cachePageNumber);
        builder.append("&sort=").append(sortType);
        builder.append("&user_id=").append(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
        String cacheKey = Request.Method.GET + ":" + AppConstants.PARENTING_STOP_ARTICLE_URL + builder.toString().replace(" ", "%20");

        while (null != BaseApplication.getInstance().getRequestQueue().getCache().get(cacheKey)) {
            BaseApplication.getInstance().getRequestQueue().getCache().remove(cacheKey);
            cacheKey = cacheKey.replace("&page=" + cachePageNumber, "&page=" + (cachePageNumber + 1));
            cachePageNumber++;
        }

    }
}
