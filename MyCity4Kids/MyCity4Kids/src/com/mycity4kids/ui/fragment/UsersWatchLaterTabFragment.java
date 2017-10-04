package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.UserPublishedAndDraftsActivity;
import com.mycity4kids.ui.adapter.UsersBookmarksRecycleAdapter;
import com.mycity4kids.utils.AppUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 3/8/17.
 */
public class UsersWatchLaterTabFragment extends BaseFragment implements UsersBookmarksRecycleAdapter.RecyclerViewClickListener {

    private ArrayList<ArticleListingResult> watchLaterList;
    private String userId;
    private String paginationValue = "";
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private int limit = 15;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int bookmarkDeletePos;

    private UsersBookmarksRecycleAdapter adapter;

    private RecyclerView recyclerView;
    private TextView noBlogsTextView;
    private RelativeLayout mLodingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.users_watch_later_tab_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);

        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();

        adapter = new UsersBookmarksRecycleAdapter(getActivity(), this);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        watchLaterList = new ArrayList<ArticleListingResult>();

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


        return view;
    }

    private void getUsersBookmarks() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((UserPublishedAndDraftsActivity) getActivity()).showToast("No connectivity available");
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retro.create(BloggerDashboardAPI.class);
        final Call<ArticleListingResponse> call = bloggerDashboardAPI.getUsersWatchLaterVideos(limit, paginationValue);
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
                if (null != watchLaterList && !watchLaterList.isEmpty()) {
                    //No more next results for search from pagination
                } else {
                    // No results for search
                    watchLaterList.addAll(dataList);
                    adapter.setListData(watchLaterList);
                    adapter.notifyDataSetChanged();
                    noBlogsTextView.setVisibility(View.VISIBLE);
                }
            } else {
                noBlogsTextView.setVisibility(View.GONE);
                watchLaterList.addAll(dataList);
                paginationValue = responseData.getData().get(0).getPagination();
                if (AppConstants.PAGINATION_END_VALUE.equals(paginationValue)) {
                    isLastPageReached = true;
                }
                adapter.setListData(watchLaterList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.shareImageView:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareUrl = AppUtils.getShareUrl(watchLaterList.get(position).getUserType(),
                        watchLaterList.get(position).getBlogPageSlug(), watchLaterList.get(position).getTitleSlug());
                String shareMessage;
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    shareMessage = "mycity4kids\n\nCheck out this interesting blog post " + "\"" +
                            watchLaterList.get(position).getTitle() + "\" by " + watchLaterList.get(position).getUserName() + ".";
                } else {
                    shareMessage = "mycity4kids\n\nCheck out this interesting blog post " + "\"" +
                            watchLaterList.get(position).getTitle() + "\" by " + watchLaterList.get(position).getUserName() + ".\nRead Here: " + shareUrl;
                }
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "mycity4kids"));
                Utils.pushShareArticleEvent(getActivity(), "WatchLaterScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "", watchLaterList.get(position).getId(),
                        watchLaterList.get(position).getUserId() + "~" + watchLaterList.get(position).getUserName(), "");
                break;
            case R.id.removeBookmarkTextView:
                bookmarkDeletePos = position;
                hitDeleteBookmarkAPI(watchLaterList.get(position));
                adapter.notifyDataSetChanged();
                break;
            case R.id.rootView:
                Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, watchLaterList.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, watchLaterList.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, watchLaterList.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, watchLaterList.get(position).getTitleSlug());
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "WatchLaterList");
                intent.putExtra(Constants.FROM_SCREEN, "PrivateActivityScreen");
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                intent.putExtra(Constants.AUTHOR, watchLaterList.get(position).getUserId() + "~" + watchLaterList.get(position).getUserName());
                startActivity(intent);
                break;
        }
    }

    private void hitDeleteBookmarkAPI(ArticleListingResult bookmarkArticle) {
        DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
        deleteBookmarkRequest.setId(bookmarkArticle.getBookmarkId());
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
        Call<AddBookmarkResponse> call = articleDetailsAPI.deleteVideoWatchLater(deleteBookmarkRequest);
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
                watchLaterList.remove(bookmarkDeletePos);
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
}
