package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.NewArticlesListingAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 4/8/16.
 */
public class CityBestArticleListingActivity extends BaseActivity {

    NewArticlesListingAdapter articlesListingAdapter;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    ListView listView;
    private Toolbar mToolbar;
    TextView noBlogsTextView;
    int sortType = 0;
    private RelativeLayout mLodingView;
    private int nextPageNumber;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private ProgressBar progressBar;
    private int limit = 15;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filtered_topics_articles_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setCityNameAsTitle();


        listView = (ListView) findViewById(R.id.scroll);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.setVisibility(View.GONE);


        progressBar.setVisibility(View.VISIBLE);

        articleDataModelsNew = new ArrayList<ArticleListingResult>();
        nextPageNumber = 1;
        hitBestofCityArticleListingApi(sortType);

        articlesListingAdapter = new NewArticlesListingAdapter(this, true);
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
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    hitBestofCityArticleListingApi(sortType);
                    isReuqestRunning = true;
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(CityBestArticleListingActivity.this, ArticlesAndBlogsDetailsActivity.class);
                if (adapterView.getAdapter() instanceof NewArticlesListingAdapter) {
                    ArticleListingResult parentingListData = (ArticleListingResult) ((NewArticlesListingAdapter) adapterView.getAdapter()).getItem(i);
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getImageUrl());
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    intent.putExtra(Constants.FILTER_TYPE, parentingListData.getUserType());
                    intent.putExtra(Constants.BLOG_NAME, parentingListData.getBlogPageSlug());
                    startActivity(intent);

                }
            }
        });
    }

    private void setCityNameAsTitle() {
        if (SharedPrefUtils.getCurrentCityModel(this).getName().isEmpty()) {
            switch (SharedPrefUtils.getCurrentCityModel(this).getId()) {
                case 1:
                    setTitle("Best of " + "Delhi-NCR");
                    break;
                case 2:
                    setTitle("Best of " + "Bangalore");
                    break;
                case 3:
                    setTitle("Best of " + "Mumbai");
                    break;
                case 4:
                    setTitle("Best of " + "Pune");
                    break;
                case 5:
                    setTitle("Best of " + "Hyderabad");
                    break;
                case 6:
                    setTitle("Best of " + "Chennai");
                    break;
                case 7:
                    setTitle("Best of " + "Kolkata");
                    break;
                case 8:
                    setTitle("Best of " + "Jaipur");
                    break;
                case 9:
                    setTitle("Best of " + "Ahmedabad");
                    break;
                default:
                    setTitle("Best of " + "Delhi-NCR");
                    break;
            }

        } else {
            if (SharedPrefUtils.getCurrentCityModel(this).getName().equals("Delhi-Ncr")) {
                SharedPrefUtils.getCurrentCityModel(this).setName("Delhi-NCR");
            }
            getSupportActionBar().setTitle("Best of " + SharedPrefUtils.getCurrentCityModel(this).getName());
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    private void hitBestofCityArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, getString(R.string.error_network));
            return;
        }
        if (nextPageNumber == 1) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getBestArticlesForCity("" + SharedPrefUtils.getCurrentCityModel(this).getId(), sortType, from, from + limit - 1);
        filterCall.enqueue(articleListingResponseCallback);

    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            progressBar.setVisibility(View.INVISIBLE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }

            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
                } else {
                    showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().getResult();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results for search
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText("No articles found");
                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

}
