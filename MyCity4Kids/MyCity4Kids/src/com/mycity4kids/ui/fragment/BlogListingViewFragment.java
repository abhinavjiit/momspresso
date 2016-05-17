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
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleModel;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.NewArticleListingResponse;
import com.mycity4kids.retrofitAPIsInterfaces.AuthorDetailsAPI;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.BlogDetailActivity;
import com.mycity4kids.ui.adapter.BloggerListingAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by manish.soni on 27-07-2015.
 */
public class BlogListingViewFragment extends BaseFragment {
    BloggerListingAdapter articlesListingAdapter;
    ListView listView;

    ArrayList<BlogArticleModel> bloggersArticleList;
    String sortType;
    private RelativeLayout mLodingView;

    private int nextPageNumber = 2;
    private boolean isReuqestRunning = false;
    private float density;
    private ProgressBar progressBar;
    private String authorId = "";
    boolean isLastPageReached = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;

        view = getActivity().getLayoutInflater().inflate(R.layout.bloggers_article_list_layout, container, false);

        density = getResources().getDisplayMetrics().density;
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        listView = (ListView) view.findViewById(R.id.scroll);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listView.getLayoutParams();
        params.setMargins(0, 0, 0, (int) (80 * density));
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        progressBar.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));
        if (getArguments() != null) {
            authorId = getArguments().getString(Constants.AUTHOR_ID);
            sortType = getArguments().getString(Constants.SORT_TYPE);
        }
        nextPageNumber = 1;
        articlesListingAdapter = new BloggerListingAdapter(getActivity(), null);
        listView.setAdapter(articlesListingAdapter);
        getBloggersArticle(authorId);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && isLastPageReached) {

                    mLodingView.setVisibility(View.VISIBLE);
                    getBloggersArticle(authorId);
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

    private void getBloggersArticle(String authorId) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.error_network));
            return;
        }
        if (nextPageNumber == 1) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        AuthorDetailsAPI authorDetailsAPI = retrofit.create(AuthorDetailsAPI.class);

        Call<NewArticleListingResponse> call;

        if ("recent".equals(sortType)) {
            call = authorDetailsAPI.getBloggersRecentArticle(authorId, nextPageNumber);
        } else {
            call = authorDetailsAPI.getBloggersPopularArticle(authorId, nextPageNumber);
        }

        call.enqueue(bloggersArticleResponseCallback);
    }

    @Override
    protected void updateUi(Response response) {

    }

    private void updateBlogArticleListingPG(NewArticleListingResponse responseData) {

        ArrayList<BlogArticleModel> dataList = responseData.getResult().getData().getData();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != bloggersArticleList && !bloggersArticleList.isEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results for search
                bloggersArticleList = dataList;
                articlesListingAdapter.setNewListData(bloggersArticleList);
                articlesListingAdapter.notifyDataSetChanged();
            }
        } else {
            if (nextPageNumber == 1) {
                bloggersArticleList = dataList;
            } else {
                bloggersArticleList.addAll(dataList);
            }
            articlesListingAdapter.setNewListData(bloggersArticleList);
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }

    }

    private Callback<NewArticleListingResponse> bloggersArticleResponseCallback = new Callback<NewArticleListingResponse>() {
        @Override
        public void onResponse(Call<NewArticleListingResponse> call, retrofit2.Response<NewArticleListingResponse> response) {
            isReuqestRunning = false;
            progressBar.setVisibility(View.INVISIBLE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                ((BlogDetailActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                NewArticleListingResponse responseData = (NewArticleListingResponse) response.body();
                if (responseData.getResponseCode() == 200) {
                    updateBlogArticleListingPG(responseData);
                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ((BlogDetailActivity) getActivity()).showToast(message);
                    } else {
                        ((BlogDetailActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((BlogDetailActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<NewArticleListingResponse> call, Throwable t) {
            t.printStackTrace();
            ((BlogDetailActivity) getActivity()).showToast(getString(R.string.went_wrong));
        }
    };

}