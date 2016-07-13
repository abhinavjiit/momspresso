package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * @author ArshVardhan
 * @email ArshVardhan.Atreya@kelltontech.com
 * @createdDate 25-03-2014
 * @modifiedDate 28-03-2014
 * @description The TopPicksActivity screen lists all the reviews for the
 * current/selected location/city
 * CR Done By- deepanker-chaudhary
 */

public class FilteredTopicsArticleListingActivity extends BaseActivity implements OnClickListener {

    ArticlesListingAdapter articlesListingAdapter;
    ListView listView;

    ArrayList<CommonParentingList> articleDataModelsNew;
    String sortType;
    private RelativeLayout mLodingView;

    private int nextPageNumber = 2;
    private boolean isReuqestRunning = false;
    private float density;
    private ProgressBar progressBar;
    private String authorId = "";
    boolean isLastPageReached = true;
    private TextView noBlogsTextView;
    private String selectedTopics;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filtered_topics_articles_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Topics");


        listView = (ListView) findViewById(R.id.scroll);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

        selectedTopics = getIntent().getStringExtra("selectedTopics");
        progressBar.setVisibility(View.VISIBLE);

        articleDataModelsNew = new ArrayList<CommonParentingList>();
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(nextPageNumber);

        articlesListingAdapter = new ArticlesListingAdapter(this, true);
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
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    hitFilteredTopicsArticleListingApi(nextPageNumber);
                    isReuqestRunning = true;
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(FilteredTopicsArticleListingActivity.this, ArticlesAndBlogsDetailsActivity.class);
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
    }

    private void hitFilteredTopicsArticleListingApi(int pPageCount) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, getString(R.string.error_network));
            return;
        }
        if (nextPageNumber == 1) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
        Call<CommonParentingResponse> filterCall = topicsAPI.filterCategories(selectedTopics, "category", pPageCount);
        filterCall.enqueue(articleListingResponseCallback);

    }

    private Callback<CommonParentingResponse> articleListingResponseCallback = new Callback<CommonParentingResponse>() {
        @Override
        public void onResponse(Call<CommonParentingResponse> call, retrofit2.Response<CommonParentingResponse> response) {
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
                CommonParentingResponse responseData = (CommonParentingResponse) response.body();
                if (responseData.getResponseCode() == 200) {
                    updateBlogArticleListingPG(responseData);
                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        showToast(message);
                    } else {
                        showToast(getString(R.string.went_wrong));
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<CommonParentingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private void updateBlogArticleListingPG(CommonParentingResponse responseData) {

        ArrayList<CommonParentingList> dataList = responseData.getResult().getData().getData();

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
    public void onClick(View v) {

    }

    @Override
    protected void updateUi(Response response) {

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