package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.response.SearchBlogResult;
import com.mycity4kids.models.response.SearchResponse;
import com.mycity4kids.models.response.SearchTopicResult;
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.SearchArticlesAndAuthorsActivity;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;
import com.mycity4kids.ui.adapter.SearchBlogsListingAdapter;
import com.mycity4kids.ui.adapter.SearchTopicsListingAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class SearchBlogsTabFragment extends BaseFragment {

    SearchBlogsListingAdapter articlesListingAdapter;
    ArrayList<SearchBlogResult> articleDataModelsNew;
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
    boolean isLastPageReached = true;
    private SwipeRefreshLayout swipe_refresh_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.new_article_layout, container, false);
        listView = (ListView) view.findViewById(R.id.scroll);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        swipe_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setEnabled(false);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        articlesListingAdapter = new SearchBlogsListingAdapter(getActivity());
        listView.setAdapter(articlesListingAdapter);
        if (getArguments() != null) {
            searchName = getArguments().getString(Constants.SEARCH_PARAM);
        }
        articleDataModelsNew = new ArrayList<SearchBlogResult>();

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
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    newSearchTopicArticleListingApi(searchName, sortType);
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

    private void newSearchTopicArticleListingApi(String searchName, String sortby) {
        if (nextPageNumber == 1 && null != progressBar) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((SearchArticlesAndAuthorsActivity) getActivity()).showToast("No connectivity available");
            return;
        }
//        mLodingView.setVisibility(View.VISIBLE);

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        SearchArticlesAuthorsAPI searchAPI = retro.create(SearchArticlesAuthorsAPI.class);
        int from = (nextPageNumber - 1) * 15 + 1;
        Call<SearchResponse> call = searchAPI.getSearchBlogsResult(searchName,
                "blog", from, from + 15);

        call.enqueue(searchTopicsResponseCallback);


//        ParentingRequest _parentingModel = new ParentingRequest();
//        _parentingModel.setSearchName(searchName);
//        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
//        _parentingModel.setPage("" + nextPageNumber);
//
//        _parentingModel.setSoty_by(sortby);
//        ParentingStopController _controller = new ParentingStopController(getActivity(), this);
//        _controller.getData(AppConstants.TOP_PICKS_REQUEST, _parentingModel);
    }

//    @Override
//    public void setUserVisibleHint(boolean visible) {
//        super.setUserVisibleHint(visible);
//        if (visible && isResumed()) {   // only at fragment screen is resumed
//            fragmentResume = true;
//            fragmentVisible = false;
//            fragmentOnCreated = true;
//            if (!isDataLoadedOnce && !StringUtils.isNullOrEmpty(searchName)) {
//                nextPageNumber = 1;
//                newSearchTopicArticleListingApi(searchName, sortType);
//                isDataLoadedOnce = true;
//            }
//        } else if (visible) {        // only at fragment onCreated
//            fragmentResume = false;
//            fragmentVisible = true;
//            fragmentOnCreated = true;
//        } else if (!visible && fragmentOnCreated) {// only when you go out of fragment screen
//            fragmentVisible = false;
//            fragmentResume = false;
//        }
//    }

    @Override
    protected void updateUi(Response response) {

    }

    private void getArticleResponse(SearchResponse responseData) {
        //	parentingResponse = responseData ;
        ArrayList<SearchBlogResult> dataList = responseData.getData().getResult().getBlog();

        if (dataList.size() == 0) {

            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination

            } else {
                // No results for search
                articleDataModelsNew = dataList;
                articlesListingAdapter.setListData(dataList);
                articlesListingAdapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText("No blogs found");
            }

        } else {
            noBlogsTextView.setVisibility(View.GONE);
//            totalPageCount = responseData.getResult().getData().getPage_count();
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            articlesListingAdapter.setListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }
    }

    public void refreshAllArticles(String searchText, String sortType) {
        if (null != articleDataModelsNew) {
            articleDataModelsNew.clear();
        }
        nextPageNumber = 1;
        isLastPageReached = true;
        searchName = searchText;
        isDataLoadedOnce = true;
        newSearchTopicArticleListingApi(searchName, sortType);
    }

    public void resetOnceLoadedFlag(String searchTxt) {
        if (null != articleDataModelsNew) {
            articleDataModelsNew.clear();
        }
        isDataLoadedOnce = false;
        isLastPageReached = true;
        searchName = searchTxt;
    }

    Callback<SearchResponse> searchTopicsResponseCallback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
            isReuqestRunning = false;
            progressBar.setVisibility(View.INVISIBLE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null) {
                ((SearchArticlesAndAuthorsActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }
            try {
                SearchResponse responseData = (SearchResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    getArticleResponse(responseData);
                } else {
                    ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(responseData.getReason());
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }

        }

        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(getString(R.string.went_wrong));
        }
    };

}