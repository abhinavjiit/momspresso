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

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.NewParentingBlogController;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleListResponse;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleModel;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.NewArticleListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.ParentingArticlesActivity;
import com.mycity4kids.ui.adapter.BloggerListingAdapter;

import java.util.ArrayList;

/**
 * Created by manish.soni on 27-07-2015.
 */
public class BlogListingViewFragment extends BaseFragment {
    BloggerListingAdapter articlesListingAdapter;
    BlogArticleListResponse.SortArticleBlogList articleDataModelsNew;
    ListView listView;

    String sortType;
    private RelativeLayout mLodingView;

    private int totalPageCount = 3;
    private int nextPageNumber = 2;
    private boolean isReuqestRunning = false;
    private String blogTitle = "";
    private float density;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;

        view = getActivity().getLayoutInflater().inflate(R.layout.new_article_layout, container, false);

        density = getResources().getDisplayMetrics().density;
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        listView = (ListView) view.findViewById(R.id.scroll);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listView.getLayoutParams();
//        params.setMargins(0, 0, 0, (int) (80 * density));
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);

        progressBar.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));


        if (getArguments() != null) {
            articleDataModelsNew = getArguments().getParcelable(Constants.BLOG_ARTICLES_LIST);
            sortType = getArguments().getString(Constants.SORT_TYPE);
            blogTitle = getArguments().getString(Constants.BLOG_TITLE);
            if (articleDataModelsNew != null) {
                totalPageCount = Integer.parseInt(articleDataModelsNew.getPage_count());
            }
        }

        if (articleDataModelsNew != null) {
            articlesListingAdapter = new BloggerListingAdapter(getActivity(), articleDataModelsNew.getData());
        } else {
            articlesListingAdapter = new BloggerListingAdapter(getActivity(), null);
        }
        listView.setAdapter(articlesListingAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && (nextPageNumber < 2 || nextPageNumber < totalPageCount)) {

                    mLodingView.setVisibility(View.VISIBLE);
                    hitArticleListingApi(nextPageNumber, sortType);
                    isReuqestRunning = true;

                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ArticlesAndBlogsDetailsActivity.class);
                if (adapterView.getAdapter() instanceof BloggerListingAdapter) {
                    BlogArticleModel parentingListData = (BlogArticleModel) ((BloggerListingAdapter) adapterView.getAdapter()).getItem(i);
                    intent.putExtra(Constants.ARTICLE_ID, String.valueOf(parentingListData.getId()));
                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getCover_image());
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    startActivity(intent);

                }
            }
        });

        return view;
    }

    @Override
    protected void updateUi(Response response) {

        NewArticleListingResponse responseData;
        if (response == null) {
            ((ParentingArticlesActivity) getActivity()).showToast("Something went wrong from server");
            removeProgressDialog();
            isReuqestRunning = false;
            mLodingView.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.PARRENTING_BLOG_ARTICLE_LISTING_PAGINATION:
                responseData = (NewArticleListingResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {

                    updateBlogArticleListingPG(responseData);

                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ToastUtils.showToast(getActivity(), message);
                    } else {
                        ToastUtils.showToast(getActivity(), getString(R.string.went_wrong));
                    }
                }

                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    private void updateBlogArticleListingPG(NewArticleListingResponse responseData) {

        ArrayList<BlogArticleModel> dataList = responseData.getResult().getData().getData();
        ArrayList<BlogArticleModel> tempDataList = new ArrayList<>();

        if (dataList.size() == 0) {
//            ((DashboardActivity) getActivity()).showToast(responseData.getResult().getMessage());
            ((DashboardActivity) getActivity()).showToast("No more articles..");
        } else {
            totalPageCount = Integer.parseInt(responseData.getResult().getData().getPage_count());
            if (nextPageNumber == 1) {
                articlesListingAdapter.setNewListData(dataList);
            } else {
                tempDataList = articlesListingAdapter.getArticleList();
                tempDataList.addAll(dataList);
                articlesListingAdapter.setNewListData(tempDataList);
            }
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }

    }

    private void hitArticleListingApi(int pPageCount, String SortKey) {

        ParentingRequest _parentingModel = new ParentingRequest();
        _parentingModel.setSoty_by(SortKey);
        _parentingModel.setSearchName(blogTitle);
        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + pPageCount);
        NewParentingBlogController _controller = new NewParentingBlogController(getActivity(), this);
        _controller.getData(AppConstants.PARRENTING_BLOG_ARTICLE_LISTING_PAGINATION, _parentingModel);

    }

}