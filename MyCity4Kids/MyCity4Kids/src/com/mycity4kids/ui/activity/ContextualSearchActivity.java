package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.SearchArticleResult;
import com.mycity4kids.models.response.SearchResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI;
import com.mycity4kids.ui.adapter.SearchArticlesListingAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 22/3/17.
 */
public class ContextualSearchActivity extends BaseActivity implements View.OnClickListener {

    private ListView listView;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private ProgressBar progressBar;
    private EditText searchEditText;
    private ImageView searchImageView;

    private SearchArticlesListingAdapter articlesListingAdapter;

    private String categoryId;
    private int nextPageNumber = 2;
    private int limit = 15;
    private ArrayList<SearchArticleResult> articleDataModelsNew;
    private boolean isReuqestRunning;
    private boolean isLastPageReached;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contextual_search_activity);
        Utils.pushOpenScreenEvent(ContextualSearchActivity.this, "Contextual Search Screen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        categoryId = getIntent().getStringExtra(Constants.CATEGORY_ID);

        listView = (ListView) findViewById(R.id.scroll);
        listView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color));
        ColorDrawable sage = new ColorDrawable(ContextCompat.getColor(this, R.color.gray2));
        listView.setDivider(sage);
        listView.setDividerHeight(1);

        searchImageView = (ImageView) findViewById(R.id.searchImageView);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

        searchImageView.setOnClickListener(this);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    resetExisitingSearchResult();
                    searchArticleWithinCategoryOrCity();
                }
                return false;
            }
        });

        articlesListingAdapter = new SearchArticlesListingAdapter(this);
        listView.setAdapter(articlesListingAdapter);

        articleDataModelsNew = new ArrayList<SearchArticleResult>();

        nextPageNumber = 1;
//        searchArticleWithinCategoryOrCity();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    searchArticleWithinCategoryOrCity();
                    isReuqestRunning = true;
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(ContextualSearchActivity.this, ArticleDetailsContainerActivity.class);
                SearchArticleResult parentingListData = (SearchArticleResult) ((SearchArticlesListingAdapter) adapterView.getAdapter()).getItem(i);
                intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getImage());
                intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                intent.putExtra(Constants.BLOG_SLUG, parentingListData.getBlogSlug());
                intent.putExtra(Constants.TITLE_SLUG, parentingListData.getTitleSlug());
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "ContextualSearch");
                intent.putExtra(Constants.FROM_SCREEN, "Contextual Search Screen");
                intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchImageView:
                resetExisitingSearchResult();
                searchArticleWithinCategoryOrCity();
                break;
        }
    }

    private void resetExisitingSearchResult() {
        if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
            articleDataModelsNew.clear();
        }
        nextPageNumber = 1;
        isLastPageReached = false;

    }

    private void searchArticleWithinCategoryOrCity() {
        if (StringUtils.isNullOrEmpty(searchEditText.getText().toString())) {
            showToast("Please enter a valid search parameter");
            return;
        }
        if (nextPageNumber == 1 && null != progressBar) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            showToast("No connectivity available");
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        SearchArticlesAuthorsAPI searchArticlesAuthorsAPI = retro.create(SearchArticlesAuthorsAPI.class);
        int from = (nextPageNumber - 1) * limit + 1;
        if (StringUtils.isNullOrEmpty(categoryId)) {
            //Search article in Best in your City.
            Call<SearchResponse> call = searchArticlesAuthorsAPI.getContextualSearchResultForCity(searchEditText.getText().toString(), "article", from, from + limit - 1, SharedPrefUtils.getCurrentCityModel(this).getNewCityId());
            call.enqueue(searchArticlesResponseCallback);
        } else {
            //Search article in categories/topics
            Call<SearchResponse> call = searchArticlesAuthorsAPI.getContextualSearchResult(searchEditText.getText().toString(), "article", from, from + limit - 1, categoryId);
            call.enqueue(searchArticlesResponseCallback);
        }
    }

    Callback<SearchResponse> searchArticlesResponseCallback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
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
                SearchResponse responseData = (SearchResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    getArticleResponse(responseData);
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void getArticleResponse(SearchResponse responseData) {
        ArrayList<SearchArticleResult> dataList = responseData.getData().getResult().getArticle();

        if (dataList.size() == 0) {

            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                articleDataModelsNew = dataList;
                articlesListingAdapter.setListData(dataList);
                articlesListingAdapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText("No articles found");
            }

        } else {
            noBlogsTextView.setVisibility(View.GONE);
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

    private void newSearchTopicArticleListingApi(String searchName, String sortby) {
    }

    @Override
    protected void updateUi(Response response) {

    }
}
