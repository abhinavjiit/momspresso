package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
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

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ParentingStopController;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.SearchArticlesAndAuthorsActivity;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;

import java.util.ArrayList;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class SearchArticlesTabFragment extends BaseFragment {

    ArticlesListingAdapter articlesListingAdapter;
    ArrayList<CommonParentingList> articleDataModelsNew;
    ListView listView;
    TextView noBlogsTextView;
    String sortType;
    String searchName = "";
    private RelativeLayout mLodingView;
    private int totalPageCount = 3;
    private int nextPageNumber = 2;
    private boolean isReuqestRunning = false;
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;
    private boolean isDataLoadedOnce = false;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.new_article_layout, container, false);
        listView = (ListView) view.findViewById(R.id.scroll);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        articlesListingAdapter = new ArticlesListingAdapter(getActivity(), true);
        listView.setAdapter(articlesListingAdapter);
        if (getArguments() != null) {
            sortType = getArguments().getString(Constants.SORT_TYPE);
            searchName = getArguments().getString(Constants.SEARCH_PARAM);
        }
        articleDataModelsNew = new ArrayList<CommonParentingList>();

        if (StringUtils.isNullOrEmpty(searchName)) {

        } else if (!fragmentResume && fragmentVisible) {
            //only when first time fragment is created
            nextPageNumber = 1;
            newSearchTopicArticleListingApi(searchName, sortType);
            isDataLoadedOnce = true;
        }

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && (nextPageNumber < 2 || nextPageNumber <= totalPageCount)) {
                    if (!"bookmark".equals(sortType)) {
                        mLodingView.setVisibility(View.VISIBLE);
                        newSearchTopicArticleListingApi(searchName, sortType);
                    } else {
                        mLodingView.setVisibility(View.VISIBLE);
                        newSearchTopicArticleListingApi(searchName, sortType);
                    }
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
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
            nextPageNumber = 1;
            if (!isDataLoadedOnce && !StringUtils.isNullOrEmpty(searchName)) {
                newSearchTopicArticleListingApi(searchName, sortType);
                isDataLoadedOnce = true;
            }
        } else if (visible) {        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        } else if (!visible && fragmentOnCreated) {// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
        }
    }

    @Override
    protected void updateUi(Response response) {

        String temp = "";
        progressBar.setVisibility(View.INVISIBLE);
        CommonParentingResponse responseData;
        if (response == null) {
            ((SearchArticlesAndAuthorsActivity) getActivity()).showToast("Something went wrong from server");
            isReuqestRunning = false;
            mLodingView.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.PARENTING_STOP_ARTICLES_REQUEST:
                responseData = (CommonParentingResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    getArticleResponse(responseData);
                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    totalPageCount = 0;
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(message);
                    } else {
                        ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    }
                }

                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);
                break;

            case AppConstants.TOP_PICKS_REQUEST:
                responseData = (CommonParentingResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    getArticleResponse(responseData);
                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(message);
                    } else {
                        ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    }
                }

                Constants.IS_REQUEST_RUNNING = false;
                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);
                break;

            case AppConstants.BOOKMARKED_ARTICLE_LIST_REQUEST:
                responseData = (CommonParentingResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    getArticleResponse(responseData);
                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    totalPageCount = 0;
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(message);
                    } else {
                        ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    }
                }

                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);
                break;

            default:
                break;
        }

    }

    private void getArticleResponse(CommonParentingResponse responseData) {
        //	parentingResponse = responseData ;
        ArrayList<CommonParentingList> dataList = responseData.getResult().getData().getData();

        if (dataList.size() == 0) {
            articleDataModelsNew = dataList;
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            articlesListingAdapter.notifyDataSetChanged();
            noBlogsTextView.setVisibility(View.VISIBLE);
            noBlogsTextView.setText("No articles found");
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

    private void newSearchTopicArticleListingApi(String searchName, String sortby) {
        if (nextPageNumber == 1) {
            progressBar.setVisibility(View.VISIBLE);
        }
        ParentingRequest _parentingModel = new ParentingRequest();
        _parentingModel.setSearchName(searchName);
        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + nextPageNumber);

        _parentingModel.setSoty_by(sortby);
        ParentingStopController _controller = new ParentingStopController(getActivity(), this);
        _controller.getData(AppConstants.TOP_PICKS_REQUEST, _parentingModel);
    }

    public void refreshAllArticles(String searchText, String sortType) {
        if (null != articleDataModelsNew) {
            articleDataModelsNew.clear();
        }
        nextPageNumber = 1;
        newSearchTopicArticleListingApi(searchText, sortType);
    }

    public void resetOnceLoadedFlag(String searchTxt) {
        if (null != articleDataModelsNew) {
            articleDataModelsNew.clear();
        }
        isDataLoadedOnce = false;
        searchName = searchTxt;
    }
}
