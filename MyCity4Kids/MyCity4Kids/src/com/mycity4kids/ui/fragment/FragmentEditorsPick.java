package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnWebServiceCompleteListener;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.newmodels.VolleyBaseResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;
import com.mycity4kids.volley.HttpVolleyRequest;

import java.util.ArrayList;

/**
 * Created by hemant on 19/5/16.
 */
public class FragmentEditorsPick extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    ArticlesListingAdapter articlesListingAdapter;
    ArrayList<CommonParentingList> articleDataModelsNew;
    ListView listView;
    TextView noBlogsTextView;
    private RelativeLayout mLodingView;
    private int totalPageCount = 3;
    private int nextPageNumber = 2;
    private boolean isReuqestRunning = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    boolean isLastPageReached = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_editors_pick, container, false);
        listView = (ListView) view.findViewById(R.id.scroll);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        progressBar.setVisibility(View.VISIBLE);

        articleDataModelsNew = new ArrayList<CommonParentingList>();
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, true);
//        }
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) FragmentEditorsPick.this);

        articlesListingAdapter = new ArticlesListingAdapter(getActivity(), true);
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
                    hitArticleListingApi(nextPageNumber, false);
                    isReuqestRunning = true;
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ArticlesAndBlogsDetailsActivity.class);
                if (adapterView.getAdapter() instanceof ArticlesListingAdapter) {
                    CommonParentingList parentingListData = (CommonParentingList) ((ArticlesListingAdapter) adapterView.getAdapter()).getItem(i);
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getThumbnail_image());
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    intent.putExtra(Constants.FILTER_TYPE, parentingListData.getAuthor_type());
                    intent.putExtra(Constants.BLOG_NAME, parentingListData.getBlog_name());
                    startActivity(intent);

                }
            }
        });

        return view;
    }

    @Override
    protected void updateUi(Response response) {

        String temp = "";
        progressBar.setVisibility(View.INVISIBLE);
        CommonParentingResponse responseData;
        if (response == null) {
            ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
            removeProgressDialog();
            isReuqestRunning = false;
            mLodingView.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.PARENTING_STOP_ARTICLES_REQUEST:
                responseData = (CommonParentingResponse) response.getResponseObject();
                swipeRefreshLayout.setRefreshing(false);
                if (responseData.getResponseCode() == 200) {
                    getArticleResponse(responseData);
                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    totalPageCount = 0;
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ((DashboardActivity) getActivity()).showToast(message);
                    } else {
                        ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    }
                }

                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);
                break;

            case AppConstants.TOP_PICKS_REQUEST:
                responseData = (CommonParentingResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    getArticleResponse(responseData);
                    removeProgressDialog();
                } else if (responseData.getResponseCode() == 400) {
                    removeProgressDialog();
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ((DashboardActivity) getActivity()).showToast(message);
                    } else {
                        ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    }
                }

                Constants.IS_REQUEST_RUNNING = false;
                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);
                break;

            case AppConstants.BOOKMARKED_ARTICLE_LIST_REQUEST:
                responseData = (CommonParentingResponse) response.getResponseObject();
                swipeRefreshLayout.setRefreshing(false);
                if (responseData.getResponseCode() == 200) {
                    getArticleResponse(responseData);
                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    totalPageCount = 0;
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ((DashboardActivity) getActivity()).showToast(message);
                    } else {
                        ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    }
                }

                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);
                break;

            default:
                break;
        }

    }

    private void hitArticleListingApi(int pPageCount, boolean isCacheRequired) {

        String url;
        StringBuilder builder = new StringBuilder();
        builder.append("cityId=").append(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        builder.append("&page=").append(pPageCount);
        url = AppConstants.EDITOR_PICKS_ARTICLES + builder.toString().replace(" ", "%20");
        HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, isCacheRequired);


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
                CommonParentingResponse responseData;
                try {
                    responseData = new Gson().fromJson(response.getResponseBody(), CommonParentingResponse.class);
                } catch (JsonSyntaxException jse) {
                    Crashlytics.logException(jse);
                    Log.d("JsonSyntaxException", Log.getStackTraceString(jse));
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                    return;
                }

                swipeRefreshLayout.setRefreshing(false);
                if (responseData.getResponseCode() == 200) {
                    getArticleResponse(responseData);
                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    totalPageCount = 0;
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

    private void getArticleResponse(CommonParentingResponse responseData) {
        //	parentingResponse = responseData ;
        ArrayList<CommonParentingList> dataList = responseData.getResult().getData().getData();

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
            //((DashboardActivity) getActivity()).showToast(responseData.getResult().getMessage());
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
            } else {

                //cache refresh request response and response from pagination may overlap causing duplication
                // to prevent check the page number in response
                //-- open article listing and immediately scroll to next page to reproduce.
                if (responseData.getResult().getData().getPageNumber() < nextPageNumber) {
                    //Response from cache refresh request. Update the dataset and refresh list
                    int articleNumber = (responseData.getResult().getData().getPageNumber() - 1) * 15;
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
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        removeVolleyCache();
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, false);
    }

    private void removeVolleyCache() {

        StringBuilder builder = new StringBuilder();
        int cachePageNumber = 1;
        builder.append("cityId=").append(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        builder.append("&page=").append(cachePageNumber);
        builder.append("&user_id=").append(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
        String cacheKey = Request.Method.GET + ":" + AppConstants.EDITOR_PICKS_ARTICLES + builder.toString().replace(" ", "%20");

        while (null != BaseApplication.getInstance().getRequestQueue().getCache().get(cacheKey)) {
            BaseApplication.getInstance().getRequestQueue().getCache().remove(cacheKey);
            cacheKey = cacheKey.replace("&page=" + cachePageNumber, "&page=" + (cachePageNumber + 1));
            cachePageNumber++;
        }

    }
}