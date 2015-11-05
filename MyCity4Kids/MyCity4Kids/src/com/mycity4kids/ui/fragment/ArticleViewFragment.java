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
import com.mycity4kids.newmodels.parentingmodel.ArticleModelNew;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;

import java.util.ArrayList;

/**
 * Created by manish.soni on 21-07-2015.
 */
public class ArticleViewFragment extends BaseFragment {

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
//    private ArrayList<CommonParentingList> mDataList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.new_article_layout, container, false);
        listView = (ListView) view.findViewById(R.id.scroll);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        if (getArguments() != null) {
            articleDataModelsNew = getArguments().getParcelableArrayList(Constants.ARTICLES_LIST);
            sortType = getArguments().getString(Constants.SORT_TYPE);
            isSearchActive = getArguments().getBoolean(Constants.IS_SEARCH_ACTIVE);
        }
        if ("bookmark".equals(sortType)) {
            articleDataModelsNew = new ArrayList<CommonParentingList>();
            nextPageNumber = 1;
            hitBookmarkedArticleListingAPI(nextPageNumber, "bookmark");
        }


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
                    if (!"bookmark".equals(sortType)) {
                        if (isSearchActive == false) {
                            mLodingView.setVisibility(View.VISIBLE);
                            hitArticleListingApi(nextPageNumber, sortType);
                        } else if (isSearchActive) {
                            mLodingView.setVisibility(View.VISIBLE);
                            newSearchTopicArticleListingApi(searchName, sortType, nextPageNumber);
                        }
                    } else {
                        mLodingView.setVisibility(View.VISIBLE);
                        hitBookmarkedArticleListingAPI(nextPageNumber, "bookmark");
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
    protected void updateUi(Response response) {

        String temp = "";

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

    private void hitArticleListingApi(int pPageCount, String SortKey) {
        ParentingRequest _parentingModel = new ParentingRequest();
        /**
         * this case will case in pagination case: for sorting
         */
        if (SortKey != null) {
            _parentingModel.setSoty_by(SortKey);
        }

        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + pPageCount);
        ParentingStopController _controller = new ParentingStopController(getActivity(), this);
//        mIsRequestRunning = true;
        _controller.getData(AppConstants.PARENTING_STOP_ARTICLES_REQUEST, _parentingModel);

    }

    private void hitBookmarkedArticleListingAPI(int pPageCount, String SortKey) {
        ParentingRequest _parentingModel = new ParentingRequest();
        /**
         * this case will case in pagination case: for sorting
         */
        if (SortKey != null) {
            _parentingModel.setSoty_by(SortKey);
        }

        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + pPageCount);
        ParentingStopController _controller = new ParentingStopController(getActivity(), this);
//        mIsRequestRunning = true;
        _controller.getData(AppConstants.BOOKMARKED_ARTICLE_LIST_REQUEST, _parentingModel);

    }

    public void refreshSubList(ArrayList<CommonParentingList> newList) {
        articleDataModelsNew = newList;
        articlesListingAdapter.setNewListData(newList);
        articlesListingAdapter.notifyDataSetChanged();
    }

    public void refreshSubListBySearch(Boolean isSearchActive, String search_name, int pos, ArticleModelNew.AllArticles tempList) {

        this.isSearchActive = isSearchActive;
        searchName = search_name;
//        tabPosition = pos;
        ArrayList<CommonParentingList> newList = null;
        switch (pos) {

            case 0:

                articleDataModelsNew = tempList.getRecent();
                articlesListingAdapter.setNewListData(tempList.getRecent());

                break;
            case 1:

                articleDataModelsNew = tempList.getPopular();
                articlesListingAdapter.setNewListData(tempList.getPopular());

                break;
            case 2:

                articleDataModelsNew = tempList.getTrending();
                articlesListingAdapter.setNewListData(tempList.getTrending());
                break;

            case 3:

                articleDataModelsNew = tempList.getBookmark();
                articlesListingAdapter.setNewListData(tempList.getBookmark());
                break;

        }
        articlesListingAdapter.notifyDataSetChanged();

    }


    private void getArticleResponse(CommonParentingResponse responseData) {
        //	parentingResponse = responseData ;
        ArrayList<CommonParentingList> dataList = responseData.getResult().getData().getData();

        if (dataList.size() == 0) {
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

    public void setSearchFilterString(String newSearchString, int pos) {
        if (!newSearchString.equalsIgnoreCase(searchName)) {
            searchName = newSearchString;

            // hit api
//            isFirstTimeSearch = true;
            nextPageNumber = 1;
            isSearchActive = true;

            newSearchTopicArticleListingApi(searchName, sortType, nextPageNumber);

        } else {
            if (articleDataModelsNew == null) {
                return;
            }
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            articlesListingAdapter.notifyDataSetChanged();

        }
    }

    private void newSearchTopicArticleListingApi(String searchName, String sortby, int pageCount) {

        if (nextPageNumber == 1) {
            showProgressDialog(getString(R.string.please_wait));
        }

        ParentingRequest _parentingModel = new ParentingRequest();
        /**
         * this case will case in pagination case: for sorting
         */
//        if (mCurrentSortByModel != null) {
//            _parentingModel.setSoty_by(mCurrentSortByModel.getKey());
//        }
        _parentingModel.setSearchName(searchName);
        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());

//        if (isFirstTimeSearch) {
//            _parentingModel.setPage("" + 1);
//        } else {
//            _parentingModel.setPage("" + pageCount);
//        }
        _parentingModel.setPage("" + nextPageNumber);

        _parentingModel.setSoty_by(sortby);
        ParentingStopController _controller = new ParentingStopController(getActivity(), this);
//        mIsRequestRunning = true;
        _controller.getData(AppConstants.TOP_PICKS_REQUEST, _parentingModel);
    }

}
