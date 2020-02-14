package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.adapter.UsersBookmarksRecycleAdapter;
import com.mycity4kids.utils.AppUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 4/10/18.
 */

public class UsersBookmarkListActivity extends BaseActivity implements UsersBookmarksRecycleAdapter.RecyclerViewClickListener {

    private ArrayList<ArticleListingResult> bookmarksList;
    private String userId;
    private String paginationValue = "";
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private int limit = 15;
    private int bookmarkDeletePos;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private UsersBookmarksRecycleAdapter adapter;

    private RecyclerView recyclerView;
    private TextView noBlogsTextView;
    private RelativeLayout mLodingView;
    private Toolbar toolbar;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_bookmark_list_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();

        adapter = new UsersBookmarksRecycleAdapter(this, this);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        bookmarksList = new ArrayList<ArticleListingResult>();

        //only when first time fragment is created
        paginationValue = "";
        getUsersBookmarks();

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
                            getUsersBookmarks();
                        }
                    }
                }
            }
        });
    }

    private void getUsersBookmarks() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            showToast(getString(R.string.connectivity_unavailable));
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retro.create(BloggerDashboardAPI.class);
        final Call<ArticleListingResponse> call = bloggerDashboardAPI.getBookmarkedList(limit, paginationValue);
        call.enqueue(usersBookmarksResponseListener);
    }

    private Callback<ArticleListingResponse> usersBookmarksResponseListener = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
//                showToast("Something went wrong from server");
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processResponse(responseData);
                } else {
//                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            mLodingView.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processResponse(ArticleListingResponse responseData) {
        //	parentingResponse = responseData ;
        try {
            ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

            if (dataList.size() == 0) {
                isLastPageReached = true;
                if (null != bookmarksList && !bookmarksList.isEmpty()) {
                    //No more next results for search from pagination
                } else {
                    // No results for search
                    bookmarksList.addAll(dataList);
                    adapter.setListData(bookmarksList);
                    adapter.notifyDataSetChanged();
                    noBlogsTextView.setVisibility(View.VISIBLE);
                }
            } else {
                noBlogsTextView.setVisibility(View.GONE);
                bookmarksList.addAll(dataList);
                paginationValue = responseData.getData().get(0).getPagination();
                if (AppConstants.PAGINATION_END_VALUE.equals(paginationValue)) {
                    isLastPageReached = true;
                }
                adapter.setListData(bookmarksList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }
    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.shareImageView:
                if ("1".equals(bookmarksList.get(position).getContentType())) {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");

                    String shareUrl = AppUtils.getShortStoryShareUrl(bookmarksList.get(position).getUserType(),
                            bookmarksList.get(position).getBlogPageSlug(), bookmarksList.get(position).getTitleSlug());
                    String shareMessage;
                    if (StringUtils.isNullOrEmpty(shareUrl)) {
                        shareMessage = getString(R.string.check_out_short_story) + "\"" +
                                bookmarksList.get(position).getTitle() + "\" by " + bookmarksList.get(position).getUserName() + ".";
                    } else {
                        shareMessage = getString(R.string.check_out_short_story) + "\"" +
                                bookmarksList.get(position).getTitle() + "\" by " + bookmarksList.get(position).getUserName() + ".\nRead Here: " + shareUrl;
                    }
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Momspresso"));
//                    if (authorId.equals(SharedPrefUtils.getUserDetailModel(this).getDynamoId())) {
//                        Utils.pushShareArticleEvent(this, "PrivateLikedScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", recommendationsList.get(position).getId(),
//                                recommendationsList.get(position).getUserId() + "~" + recommendationsList.get(position).getUserName(), "-");
//                    } else {
//                        Utils.pushShareArticleEvent(this, "PublicLikedScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", recommendationsList.get(position).getId(),
//                                recommendationsList.get(position).getUserId() + "~" + recommendationsList.get(position).getUserName(), "-");
//                    }
                } else {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareUrl = AppUtils.getShareUrl(bookmarksList.get(position).getUserType(),
                            bookmarksList.get(position).getBlogPageSlug(), bookmarksList.get(position).getTitleSlug());
                    String shareMessage;
                    if (StringUtils.isNullOrEmpty(shareUrl)) {
                        shareMessage = getString(R.string.check_out_blog) + "\"" +
                                bookmarksList.get(position).getTitle() + "\" by " + bookmarksList.get(position).getUserName() + ".";
                    } else {
                        shareMessage = getString(R.string.check_out_blog) + "\"" +
                                bookmarksList.get(position).getTitle() + "\" by " + bookmarksList.get(position).getUserName() + ".\nRead Here: " + shareUrl;
                    }
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                    Utils.pushShareArticleEvent(this, "BookmarkedScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", bookmarksList.get(position).getId(),
                            bookmarksList.get(position).getUserId() + "~" + bookmarksList.get(position).getUserName(), "-");
                }
                break;
            case R.id.removeBookmarkTextView:
                bookmarkDeletePos = position;
                hitDeleteBookmarkAPI(bookmarksList.get(position));
                break;
            case R.id.rootView:
                if ("1".equals(bookmarksList.get(position).getContentType())) {
                    Intent intent = new Intent(this, ShortStoryContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, bookmarksList.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, bookmarksList.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, bookmarksList.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, bookmarksList.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "BookmarkList");
                    intent.putExtra(Constants.FROM_SCREEN, "PrivateActivityScreen");
                    ArrayList<ArticleListingResult> filteredResult = AppUtils.getFilteredContentList(bookmarksList, AppConstants.CONTENT_TYPE_SHORT_STORY);
                    intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(position, bookmarksList, AppConstants.CONTENT_TYPE_SHORT_STORY));
                    intent.putExtra(Constants.AUTHOR, bookmarksList.get(position).getUserId() + "~" + bookmarksList.get(position).getUserName());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, ArticleDetailsContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, bookmarksList.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, bookmarksList.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, bookmarksList.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, bookmarksList.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "BookmarkList");
                    intent.putExtra(Constants.FROM_SCREEN, "PrivateActivityScreen");
                    ArrayList<ArticleListingResult> filteredResult = AppUtils.getFilteredContentList(bookmarksList, AppConstants.CONTENT_TYPE_ARTICLE);
                    intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(position, bookmarksList, AppConstants.CONTENT_TYPE_ARTICLE));
                    intent.putExtra(Constants.AUTHOR, bookmarksList.get(position).getUserId() + "~" + bookmarksList.get(position).getUserName());
                    startActivity(intent);
                }
                break;
        }
    }

    private void hitDeleteBookmarkAPI(ArticleListingResult bookmarkArticle) {
        DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
        deleteBookmarkRequest.setId(bookmarkArticle.getBookmarkId());
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
        Call<AddBookmarkResponse> call = articleDetailsAPI.deleteBookmark(deleteBookmarkRequest);
        call.enqueue(removeBookmarkResponseCallback);
    }

    private Callback<AddBookmarkResponse> removeBookmarkResponseCallback = new Callback<AddBookmarkResponse>() {
        @Override
        public void onResponse(Call<AddBookmarkResponse> call, retrofit2.Response<AddBookmarkResponse> response) {
            if (response == null || null == response.body()) {
//                showToast("Something went wrong from server");
                return;
            }
            AddBookmarkResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                bookmarksList.remove(bookmarkDeletePos);
                adapter.notifyDataSetChanged();
            } else {

            }
        }

        @Override
        public void onFailure(Call<AddBookmarkResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("RemoveBookmarkException", Log.getStackTraceString(t));
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
