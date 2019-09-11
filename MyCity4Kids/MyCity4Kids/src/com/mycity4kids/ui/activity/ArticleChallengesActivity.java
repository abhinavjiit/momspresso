package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.retrofitAPIsInterfaces.ArticlePublishAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ChallengeListingRecycleAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ArticleChallengesActivity extends BaseActivity implements ChallengeListingRecycleAdapter.RecyclerViewClickListener {


    private ArrayList<String> activeArticleChallengeList;
    private Topics currentActiveChallenge;
    private int nextPageNumber = 1;
    private int limit = 15;
    private int sortType = 0;
    private boolean isRequestRunning = false;
    private boolean isLastPageReached = false;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private ArrayList<ArticleListingResult> mDatalist;

    private ProgressBar progressBar;
    private RecyclerView challengeArticleRecyclerView;
    private Toolbar mToolbar;
    private ChallengeListingRecycleAdapter articleChallengesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_challenges_activity);

        progressBar = findViewById(R.id.progressBar);
        challengeArticleRecyclerView = findViewById(R.id.challengeArticleRecyclerView);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getActiveChallenge(AppConstants.ARTICLE_CHALLENGES_ID);
        progressBar.setVisibility(View.VISIBLE);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        challengeArticleRecyclerView.setLayoutManager(llm);

        challengeArticleRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisibleItems = llm.findFirstVisibleItemPosition();

                    if (!isRequestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            isRequestRunning = true;
                            getArticleForChallenge();
                        }
                    }
                }
            }
        });

    }

    private void getActiveChallenge(String categoryId) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticlePublishAPI articlePublishAPI = retrofit.create(ArticlePublishAPI.class);
        Call<Topics> call = articlePublishAPI.getArticleChallenges(categoryId);
        call.enqueue(articleChallengeResponseCallback);

    }

    private Callback<Topics> articleChallengeResponseCallback = new Callback<Topics>() {
        @Override
        public void onResponse(Call<Topics> call, retrofit2.Response<Topics> response) {
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            if (response.isSuccessful()) {
                try {
                    Topics topic = response.body();
//                    ArrayList<Topics> articleChallengesList = new ArrayList<>();
//                    articleChallengesList.addAll(topic.getChild());
                    for (int i = topic.getChild().size() - 1; i >= 0; i--) {
                        if ("1".equals(topic.getChild().get(i).getPublicVisibility())
                                && topic.getChild().get(i).getExtraData() != null && !topic.getChild().get(i).getExtraData().isEmpty()
                                && "1".equals(topic.getChild().get(i).getExtraData().get(0).getChallenge().getActive())) {
                            currentActiveChallenge = topic.getChild().get(i);
                            mDatalist = new ArrayList<>();
                            articleChallengesRecyclerAdapter = new ChallengeListingRecycleAdapter(ArticleChallengesActivity.this
                                    , ArticleChallengesActivity.this, 0, currentActiveChallenge.getDisplay_name(),
                                    currentActiveChallenge.getExtraData().get(0).getChallenge().getImageUrl());
                            challengeArticleRecyclerView.setAdapter(articleChallengesRecyclerAdapter);
                            articleChallengesRecyclerAdapter.setListData(mDatalist);
                            articleChallengesRecyclerAdapter.notifyDataSetChanged();
                            getArticleForChallenge();
                            break;
                        }
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("FileNotFoundException", Log.getStackTraceString(e));
                }
            }
        }

        @Override
        public void onFailure(Call<Topics> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("FileNotFoundException", Log.getStackTraceString(t));
        }
    };

    private void getArticleForChallenge() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, getString(R.string.error_network));
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(currentActiveChallenge.getId(), sortType, from, from + limit - 1, "0");
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isRequestRunning = false;
            progressBar.setVisibility(View.GONE);
            if (response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            isRequestRunning = false;
            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != mDatalist && !mDatalist.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                mDatalist = dataList;
                articleChallengesRecyclerAdapter.setListData(mDatalist);
                articleChallengesRecyclerAdapter.notifyDataSetChanged();
            }
        } else {
//            writeArticleCell.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                mDatalist = dataList;

            } else {
                mDatalist.addAll(dataList);
            }
            articleChallengesRecyclerAdapter.setListData(mDatalist);
            nextPageNumber = nextPageNumber + 1;
            articleChallengesRecyclerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view, int position, String activeUrl) {

    }
}
