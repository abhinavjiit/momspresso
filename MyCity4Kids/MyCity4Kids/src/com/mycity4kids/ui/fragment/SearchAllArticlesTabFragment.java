package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.SearchArticleResult;
import com.mycity4kids.models.response.SearchResponse;
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.SearchAllActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.adapter.SearchArticlesListingAdapter;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class SearchAllArticlesTabFragment extends BaseFragment implements SearchArticlesListingAdapter.RecyclerViewClickListener {

    private boolean isLastPageReached = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String searchName = "";
    private int nextPageNumber = 1;
    private boolean isReuqestRunning = true;

    private RelativeLayout mLodingView;
    SearchArticlesListingAdapter searchArticlesListingAdapter;
    ArrayList<SearchArticleResult> articleList;
    RecyclerView recyclerView;
    TextView noBlogsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_common_tab_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);

        noBlogsTextView.setText(BaseApplication.getAppContext().getString(R.string.no_articles_found));

        if (getArguments() != null) {
            searchName = getArguments().getString(Constants.SEARCH_PARAM);
        }

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        articleList = new ArrayList<>();

        searchArticlesListingAdapter = new SearchArticlesListingAdapter(getActivity(), this);
        searchArticlesListingAdapter.setListData(articleList);

        recyclerView.setAdapter(searchArticlesListingAdapter);
        if (StringUtils.isNullOrEmpty(searchName)) {

        } else {
            searchArticlesAPI();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            searchArticlesAPI();
                        }
                    }
                }
            }
        });


        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    private void updateTopicsListing(SearchResponse responseData) {

        ArrayList<SearchArticleResult> dataList = responseData.getData().getResult().getArticle();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != articleList && !articleList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                articleList = dataList;
                searchArticlesListingAdapter.setListData(dataList);
                searchArticlesListingAdapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);
            }

            //((SearchAllActivity) getActivity()).showToast(responseData.getResult().getMessage());
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleList = dataList;
            } else {
                articleList.addAll(dataList);
            }
            searchArticlesListingAdapter.setListData(articleList);
            nextPageNumber = nextPageNumber + 1;
            searchArticlesListingAdapter.notifyDataSetChanged();
        }
    }

    public void searchArticlesAPI() {

        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            if (isAdded())
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.connectivity_unavailable));
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        SearchArticlesAuthorsAPI searchArticlesAuthorsAPI = retro.create(SearchArticlesAuthorsAPI.class);
        int from = (nextPageNumber - 1) * 15 + 1;
        Call<SearchResponse> call = searchArticlesAuthorsAPI.getSearchTopicsResult(searchName,
                "article", from, from + 15);

        call.enqueue(searchTopicsResponseCallback);
    }

    public void refreshAllArticles(String searchTxt) {
        if (null != articleList) {
            articleList.clear();
        }
        nextPageNumber = 1;
        isLastPageReached = false;
        searchName = searchTxt;
        searchArticlesAPI();
    }

    public void resetOnceLoadedFlag(String searchTxt) {
        if (null != articleList) {
            articleList.clear();
        }
        isLastPageReached = false;
        searchName = searchTxt;
    }


    Callback<SearchResponse> searchTopicsResponseCallback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
            isReuqestRunning = false;
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (isAdded()) {
                if (response == null || response.body() == null) {
                    if (mLodingView.getVisibility() == View.VISIBLE) {
                        mLodingView.setVisibility(View.GONE);
                    }
                    ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));

                    return;
                }
            }
            try {
                SearchResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    updateTopicsListing(responseData);
                } else {
                    ((SearchAllActivity) getActivity()).showToast(responseData.getReason());
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (getActivity() != null) {
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onClick(View view, int position) {
        SearchArticleResult searchData = articleList.get(position);
        if ("1".equals(searchData.getContentType())) {
            Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
            intent.putExtra(Constants.ARTICLE_ID, searchData.getId());
            intent.putExtra(Constants.AUTHOR_ID, searchData.getUserId());
            intent.putExtra(Constants.BLOG_SLUG, searchData.getBlogSlug());
            intent.putExtra(Constants.TITLE_SLUG, searchData.getTitleSlug());
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "SearchScreen");
            intent.putExtra(Constants.FROM_SCREEN, "SearchScreen");
            intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
            intent.putExtra(Constants.AUTHOR, searchData.getUserId() + "~");
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
            intent.putExtra(Constants.ARTICLE_ID, searchData.getId());
            intent.putExtra(Constants.AUTHOR_ID, searchData.getUserId());
            intent.putExtra(Constants.BLOG_SLUG, searchData.getBlogSlug());
            intent.putExtra(Constants.TITLE_SLUG, searchData.getTitleSlug());
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "SearchScreen");
            intent.putExtra(Constants.FROM_SCREEN, "SearchScreen");
            intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
            intent.putExtra(Constants.AUTHOR, searchData.getUserId() + "~");
            startActivity(intent);
        }
    }
}
