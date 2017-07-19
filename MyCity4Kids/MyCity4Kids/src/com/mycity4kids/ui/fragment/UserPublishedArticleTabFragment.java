package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.activity.UserPublishedAndDraftsActivity;
import com.mycity4kids.ui.adapter.UserPublishedArticleAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class UserPublishedArticleTabFragment extends BaseFragment implements View.OnClickListener {

    ArrayList<ArticleListingResult> articleDataModelsNew;
    RecyclerView recyclerView;
    private RelativeLayout mLodingView;
    private int nextPageNumber = 0;
    private boolean isReuqestRunning = false;
    boolean isLastPageReached = true;
    private UserPublishedArticleAdapter adapter;
    private String authorId;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.user_published_article_tab_fragment, container, false);

        Utils.pushOpenScreenEvent(getActivity(), "Search Articles Fragment Listing", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);

        adapter = new UserPublishedArticleAdapter(getActivity());
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        if (getArguments() != null) {
            authorId = getArguments().getString(Constants.AUTHOR_ID);
        }
        articleDataModelsNew = new ArrayList<ArticleListingResult>();

        //only when first time fragment is created
        nextPageNumber = 0;
        getUserPublishedArticles();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            getUserPublishedArticles();
                        }
                    }
                }
            }
        });
        return view;
    }

    private void getUserPublishedArticles() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((UserPublishedAndDraftsActivity) getActivity()).showToast("No connectivity available");
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI userpublishedArticlesAPI = retro.create(BloggerDashboardAPI.class);
        int from = 15 * nextPageNumber + 1;
        final Call<ArticleListingResponse> call = userpublishedArticlesAPI.getAuthorsPublishedArticles(authorId, 0, from, from + 14);
        call.enqueue(userPublishedArticleResponseListener);
    }

    @Override
    protected void updateUi(Response response) {

    }

    private Callback<ArticleListingResponse> userPublishedArticleResponseListener = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            removeProgressDialog();
            isReuqestRunning = false;
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processPublisedArticlesResponse(responseData);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processPublisedArticlesResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {

            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results for search
                articleDataModelsNew.addAll(dataList);
                adapter.setListData(articleDataModelsNew);
                adapter.notifyDataSetChanged();
                if (recyclerView.getVisibility() == View.VISIBLE) {
//                    noArticleTextView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (nextPageNumber == 1) {
                articleDataModelsNew.addAll(dataList);
                adapter.setListData(articleDataModelsNew);
                adapter.notifyDataSetChanged();
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            adapter.setListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.searchArticleShowMoreTextView:
//                if (!isReuqestRunning) {
//                    showMoreType = "article";
//                    isReuqestRunning = true;
//                    newSearchTopicArticleListingApi(searchName, "article");
//                }
//                break;
//            case R.id.searchTopicShowMoreTextView:
//                if (!isReuqestRunning) {
//                    showMoreType = "topic";
//                    isReuqestRunning = true;
//                    newSearchTopicArticleListingApi(searchName, "topic");
//                    break;
//                }
        }
    }

}
