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
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.ParentingFilterType;
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
 * Created by manish.soni on 21-07-2015.
 */
public class ArticleViewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    ArticlesListingAdapter articlesListingAdapter;
    ArrayList<CommonParentingList> articleDataModelsNew;
    ListView listView;
    TextView noBlogsTextView;
    String sortType;
    String searchName = "";
    Boolean isSearchActive = false;
    private RelativeLayout mLodingView;
    private int totalPageCount = 3;
    private int nextPageNumber = 2;
    private boolean isReuqestRunning = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
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
        articleDataModelsNew = new ArrayList<CommonParentingList>();
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, sortType, false);
//        }
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) ArticleViewFragment.this);

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
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && (nextPageNumber < 2 || nextPageNumber <= totalPageCount)) {
//                    if (!"bookmark".equals(sortType)) {
                    mLodingView.setVisibility(View.VISIBLE);
                    hitArticleListingApi(nextPageNumber, sortType, false);
//                    } else {
//                        mLodingView.setVisibility(View.VISIBLE);
//                        hitBookmarkedArticleListingAPI(nextPageNumber, "bookmark");
//                    }
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

    private void hitArticleListingApi(int pPageCount, String SortKey, boolean shouldRefreshCache) {

        String url;
        StringBuilder builder = new StringBuilder();
        if (!"bookmark".equals(sortType)) {
            builder.append("city_id=").append(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
            builder.append("&page=").append(pPageCount);
            builder.append("&sort=").append(SortKey);
            url = AppConstants.PARENTING_STOP_ARTICLE_URL + builder.toString().replace(" ", "%20");
            HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, shouldRefreshCache);
        } else {
            builder.append("user_id=").append(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
            builder.append("&page=").append(pPageCount);
            url = AppConstants.FETCH_BOOKMARK_URL + builder.toString().replace(" ", "%20");
            HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, shouldRefreshCache);
        }

    }

    private OnWebServiceCompleteListener mGetArticleListingListener = new OnWebServiceCompleteListener() {
        @Override
        public void onWebServiceComplete(VolleyBaseResponse response, boolean isError) {
            progressBar.setVisibility(View.GONE);
            Log.d("Response back =", " " + response.getResponseBody());
            if (isError) {
                if (null != getActivity())
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
            } else {
                Log.d("Response = ", response.getResponseBody());
                String temp = "";
//                progressBar.setVisibility(View.INVISIBLE);
                if (response == null) {
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                    removeProgressDialog();
                    isReuqestRunning = false;
                    mLodingView.setVisibility(View.GONE);
                    return;
                }
                CommonParentingResponse responseData = new Gson().fromJson(response.getResponseBody(), CommonParentingResponse.class);

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

    public void refreshSubList(ArrayList<CommonParentingList> newList) {
        articleDataModelsNew = newList;
        articlesListingAdapter.setNewListData(newList);
        articlesListingAdapter.notifyDataSetChanged();
    }

    private void getArticleResponse(CommonParentingResponse responseData) {
        //	parentingResponse = responseData ;
        ArrayList<CommonParentingList> dataList = responseData.getResult().getData().getData();

        if (dataList.size() == 0) {
            articleDataModelsNew = dataList;
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            articlesListingAdapter.notifyDataSetChanged();
            noBlogsTextView.setVisibility(View.VISIBLE);
            //((DashboardActivity) getActivity()).showToast(responseData.getResult().getMessage());
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            totalPageCount = responseData.getResult().getData().getPage_count();
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }
    }

    public void refreshBookmarkList() {
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, "bookmark", true);
    }

    @Override
    public void onRefresh() {
        nextPageNumber = 1;
        hitArticleListingApi(nextPageNumber, sortType, true);
    }
}
