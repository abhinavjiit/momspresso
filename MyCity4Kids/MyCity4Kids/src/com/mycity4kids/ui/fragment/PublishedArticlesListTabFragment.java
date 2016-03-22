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
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.BloggerDashboardAndPublishedArticlesController;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.newmodels.PublishedArticlesModel;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleModel;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.mycity4kids.ui.adapter.BloggerListingAdapter;
import com.mycity4kids.ui.adapter.PublishedArticlesListAdapter;

import java.util.ArrayList;

/**
 * Created by hemant on 18/3/16.
 */
public class PublishedArticlesListTabFragment extends BaseFragment {
    PublishedArticlesListAdapter articlesListingAdapter;
    ArrayList<PublishedArticlesModel.PublishedArticleData> articleDataModelsNew;
    ListView listView;

    private RelativeLayout mLodingView;

    private int nextPageNumber = 1;
    private boolean isReuqestRunning = false;
    int publishedArticleCount;
    int totalPageCount = 0;
    TextView noBlogsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_published_articles_tab, container, false);

        publishedArticleCount = getArguments().getInt("publishedArticleCount");
        if (publishedArticleCount % 15 == 0) {
            totalPageCount = publishedArticleCount / 15;
        } else {
            totalPageCount = publishedArticleCount / 15 + 1;
        }

        listView = (ListView) view.findViewById(R.id.publishedBlogsListView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);

        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        getAllPublishedArticles(nextPageNumber);

        articlesListingAdapter =
                new PublishedArticlesListAdapter(getActivity());
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

                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && (nextPageNumber < 2 || nextPageNumber < totalPageCount)) {

                    mLodingView.setVisibility(View.VISIBLE);
                    getAllPublishedArticles(nextPageNumber);
                    isReuqestRunning = true;

                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ArticlesAndBlogsDetailsActivity.class);
                if (adapterView.getAdapter() instanceof PublishedArticlesListAdapter) {
                    PublishedArticlesModel.PublishedArticleData parentingListData = (PublishedArticlesModel.PublishedArticleData) ((PublishedArticlesListAdapter) adapterView.getAdapter()).getItem(i);
                    intent.putExtra(Constants.ARTICLE_ID, String.valueOf(parentingListData.getId()));
                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getThumbnail_image());
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    startActivity(intent);

                }
            }
        });

        return view;
    }

    @Override
    protected void updateUi(Response response) {

        PublishedArticlesModel responseData;
        if (response == null) {
            ((BloggerDashboardActivity) getActivity()).showToast("Something went wrong from server");
            removeProgressDialog();
            isReuqestRunning = false;
            mLodingView.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.GET_BLOGGER_PUBLISHED_ARTICLES_REQUEST:
                responseData = (PublishedArticlesModel) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    processPublishedArticleResponse(responseData);
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

    private void processPublishedArticleResponse(PublishedArticlesModel responseData) {

        ArrayList<PublishedArticlesModel.PublishedArticleData> dataList = responseData.getResult().getData();

        if (dataList.size() == 0) {
            articleDataModelsNew = dataList;
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            articlesListingAdapter.notifyDataSetChanged();
            noBlogsTextView.setVisibility(View.VISIBLE);
            //((DashboardActivity) getActivity()).showToast(responseData.getResult().getMessage());
        } else {
            noBlogsTextView.setVisibility(View.GONE);
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

    private void getAllPublishedArticles(int pPageCount) {
        BloggerDashboardAndPublishedArticlesController _controller =
                new BloggerDashboardAndPublishedArticlesController(getActivity(), this);
        _controller.getData(AppConstants.GET_BLOGGER_PUBLISHED_ARTICLES_REQUEST, pPageCount);

    }
}
