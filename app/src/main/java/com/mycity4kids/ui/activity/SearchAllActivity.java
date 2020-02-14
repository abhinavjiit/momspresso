package com.mycity4kids.ui.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.tabs.TabLayout;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.SaveSearchQueryRequest;
import com.mycity4kids.models.response.SearchTrendsAndHistoryResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI;
import com.mycity4kids.ui.adapter.SearchAllPagerAdapter;
import com.mycity4kids.utils.AppUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 19/4/16.
 */
public class SearchAllActivity extends BaseActivity implements View.OnClickListener {

    Toolbar mToolbar;
    private TabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private SearchAllPagerAdapter tabsPagerAdapter;
    private ImageView searchImageView;
    private EditText searchEditText;
    String searchParam;
    int tabPosition;
    Handler handler = new Handler();
    private LinearLayout searchHistoryAndTrendContainer;
    private TextView userSearchHistory1TextView, userSearchHistory2TextView, trendingSearch1TextView, trendingSearch2TextView, trendingSearch3TextView;
    private View underline1, underline2, underline3, underline4, underline5;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(this, "SearchScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        setContentView(R.layout.search_articles_authors_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        searchParam = getIntent().getStringExtra(Constants.FILTER_NAME);
        tabPosition = getIntent().getIntExtra(Constants.TAB_POSITION, 0);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mSlidingTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        searchImageView = (ImageView) findViewById(R.id.searchImageView);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchHistoryAndTrendContainer = (LinearLayout) findViewById(R.id.searchHistoryAndTrendContainer);
        userSearchHistory1TextView = (TextView) findViewById(R.id.userSearchHistory1TextView);
        userSearchHistory2TextView = (TextView) findViewById(R.id.userSearchHistory2TextView);
        trendingSearch1TextView = (TextView) findViewById(R.id.trendingSearch1TextView);
        trendingSearch2TextView = (TextView) findViewById(R.id.trendingSearch2TextView);
        trendingSearch3TextView = (TextView) findViewById(R.id.trendingSearch3TextView);
        underline1 = findViewById(R.id.underline1);
        underline2 = findViewById(R.id.underline2);
        underline3 = findViewById(R.id.underline3);
        underline4 = findViewById(R.id.underline4);
        underline5 = findViewById(R.id.underline5);

        userSearchHistory1TextView.setOnClickListener(this);
        userSearchHistory2TextView.setOnClickListener(this);
        trendingSearch1TextView.setOnClickListener(this);
        trendingSearch2TextView.setOnClickListener(this);
        trendingSearch3TextView.setOnClickListener(this);

        tabsPagerAdapter = new SearchAllPagerAdapter(getSupportFragmentManager(), this, this, searchParam);

        setSupportActionBar(mToolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black_color), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mViewPager.setAdapter(tabsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mSlidingTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(tabPosition);

        searchEditText.setText(searchParam);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    requestSearch();
                }
                return false;
            }
        });

        //getUserHistoryAndTrendingSearchResult();

        AppUtils.changeTabsFont(mSlidingTabLayout);
        searchImageView.setOnClickListener(this);
    }

    private void getUserHistoryAndTrendingSearchResult() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        SearchArticlesAuthorsAPI searchArticlesAuthorsAPI = retrofit.create(SearchArticlesAuthorsAPI.class);
        Call<SearchTrendsAndHistoryResponse> call = searchArticlesAuthorsAPI.getSearchTrendAndHistory();
        call.enqueue(searchHistoryResponseCallback);
    }

    private Callback<SearchTrendsAndHistoryResponse> searchHistoryResponseCallback = new Callback<SearchTrendsAndHistoryResponse>() {
        @Override
        public void onResponse(Call<SearchTrendsAndHistoryResponse> call, retrofit2.Response<SearchTrendsAndHistoryResponse> response) {
            if (response == null || response.body() == null) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }

            try {
                SearchTrendsAndHistoryResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (responseData.getData().getResult().getUserResult().size() == 0) {
                        if (responseData.getData().getResult().getTrendingResult().size() > 2) {
                            trendingSearch1TextView.setVisibility(View.VISIBLE);
                            underline3.setVisibility(View.VISIBLE);
                            trendingSearch2TextView.setVisibility(View.VISIBLE);
                            underline4.setVisibility(View.VISIBLE);
                            trendingSearch3TextView.setVisibility(View.VISIBLE);
                            underline5.setVisibility(View.VISIBLE);
                            trendingSearch1TextView.setText(responseData.getData().getResult().getTrendingResult().get(0));
                            trendingSearch2TextView.setText(responseData.getData().getResult().getTrendingResult().get(1));
                            trendingSearch3TextView.setText(responseData.getData().getResult().getTrendingResult().get(2));
                        } else if (responseData.getData().getResult().getTrendingResult().size() == 2) {
                            trendingSearch1TextView.setVisibility(View.VISIBLE);
                            underline3.setVisibility(View.VISIBLE);
                            trendingSearch2TextView.setVisibility(View.VISIBLE);
                            underline4.setVisibility(View.VISIBLE);
                            trendingSearch1TextView.setText(responseData.getData().getResult().getTrendingResult().get(0));
                            trendingSearch2TextView.setText(responseData.getData().getResult().getTrendingResult().get(1));
                        } else if (responseData.getData().getResult().getTrendingResult().size() == 1) {
                            trendingSearch1TextView.setVisibility(View.VISIBLE);
                            underline3.setVisibility(View.VISIBLE);
                            trendingSearch1TextView.setText(responseData.getData().getResult().getTrendingResult().get(0));
                        } else {
                            searchHistoryAndTrendContainer.setVisibility(View.GONE);
                        }
                    } else if (responseData.getData().getResult().getUserResult().size() == 1) {
                        userSearchHistory1TextView.setVisibility(View.VISIBLE);
                        underline1.setVisibility(View.VISIBLE);
                        userSearchHistory1TextView.setText(responseData.getData().getResult().getUserResult().get(0));
                        if (responseData.getData().getResult().getTrendingResult().size() > 1) {
                            trendingSearch1TextView.setVisibility(View.VISIBLE);
                            underline3.setVisibility(View.VISIBLE);
                            trendingSearch2TextView.setVisibility(View.VISIBLE);
                            underline4.setVisibility(View.VISIBLE);
                            trendingSearch1TextView.setText(responseData.getData().getResult().getTrendingResult().get(0));
                            trendingSearch2TextView.setText(responseData.getData().getResult().getTrendingResult().get(1));
                        } else if (responseData.getData().getResult().getTrendingResult().size() == 1) {
                            trendingSearch1TextView.setVisibility(View.VISIBLE);
                            underline3.setVisibility(View.VISIBLE);
                            trendingSearch1TextView.setText(responseData.getData().getResult().getTrendingResult().get(0));
                        } else {

                        }
                    } else {
                        userSearchHistory1TextView.setVisibility(View.VISIBLE);
                        underline1.setVisibility(View.VISIBLE);
                        userSearchHistory2TextView.setVisibility(View.VISIBLE);
                        underline2.setVisibility(View.VISIBLE);
                        userSearchHistory1TextView.setText(responseData.getData().getResult().getUserResult().get(0));
                        userSearchHistory2TextView.setText(responseData.getData().getResult().getUserResult().get(1));
                        if (responseData.getData().getResult().getTrendingResult().size() >= 1) {
                            trendingSearch1TextView.setVisibility(View.VISIBLE);
                            underline3.setVisibility(View.VISIBLE);
                            trendingSearch1TextView.setText(responseData.getData().getResult().getTrendingResult().get(0));
                        } else {

                        }
                    }

                } else {

                }
            } catch (Exception e) {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<SearchTrendsAndHistoryResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchImageView:
                searchHistoryAndTrendContainer.setVisibility(View.GONE);
                mSlidingTabLayout.setVisibility(View.VISIBLE);
                requestSearch();
                break;
            case R.id.userSearchHistory1TextView:
                searchHistoryAndTrendContainer.setVisibility(View.GONE);
                mSlidingTabLayout.setVisibility(View.VISIBLE);
                searchEditText.setText(userSearchHistory1TextView.getText());
                requestSearch();
//                requestSearchWithParam(userSearchHistory1TextView.getText().toString());
                break;
            case R.id.userSearchHistory2TextView:
                searchHistoryAndTrendContainer.setVisibility(View.GONE);
                mSlidingTabLayout.setVisibility(View.VISIBLE);
                searchEditText.setText(userSearchHistory2TextView.getText());
                requestSearch();
//                requestSearchWithParam(userSearchHistory2TextView.getText().toString());
                break;
            case R.id.trendingSearch1TextView:
                searchHistoryAndTrendContainer.setVisibility(View.GONE);
                mSlidingTabLayout.setVisibility(View.VISIBLE);
                searchEditText.setText(trendingSearch1TextView.getText());
                requestSearch();
//                requestSearchWithParam(trendingSearch1TextView.getText().toString());
                break;
            case R.id.trendingSearch2TextView:
                searchHistoryAndTrendContainer.setVisibility(View.GONE);
                mSlidingTabLayout.setVisibility(View.VISIBLE);
                searchEditText.setText(trendingSearch2TextView.getText());
                requestSearch();
//                requestSearchWithParam(trendingSearch2TextView.getText().toString());
                break;
            case R.id.trendingSearch3TextView:
                searchHistoryAndTrendContainer.setVisibility(View.GONE);
                mSlidingTabLayout.setVisibility(View.VISIBLE);
                searchEditText.setText(trendingSearch3TextView.getText());
                requestSearch();
//                requestSearchWithParam(trendingSearch3TextView.getText().toString());
                break;
        }
    }

    private void storeSearchQuery(String query) {
        SaveSearchQueryRequest saveSearchQueryRequest = new SaveSearchQueryRequest();
        saveSearchQueryRequest.setQuery(query);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        SearchArticlesAuthorsAPI searchArticlesAuthorsAPI = retrofit.create(SearchArticlesAuthorsAPI.class);
        Call<ResponseBody> call = searchArticlesAuthorsAPI.saveSearchQuery(saveSearchQueryRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void requestSearch() {
        if (StringUtils.isNullOrEmpty(searchEditText.getText().toString())) {
//            showToast("Please enter a valid search parameter");
        } else {
            tabsPagerAdapter.refreshArticlesAuthors(searchEditText.getText().toString());
            storeSearchQuery(searchEditText.getText().toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
