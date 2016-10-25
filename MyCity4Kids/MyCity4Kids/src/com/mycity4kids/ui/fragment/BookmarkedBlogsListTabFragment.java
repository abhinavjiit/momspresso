package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;
import com.mycity4kids.ui.adapter.NewArticlesListingAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 18/3/16.
 */
public class BookmarkedBlogsListTabFragment extends BaseFragment {
    NewArticlesListingAdapter articlesListingAdapter;
    ListView listView;
    ArrayList<ArticleListingResult> articleDataModelsNew;

    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;

    //    private int nextPageNumber = 1;
    private boolean isLastPageReached = true;
    private boolean isReuqestRunning = false;
    private int from = 1;
    private int to = 15;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_bookmarked_blogs_tab, container, false);

        listView = (ListView) view.findViewById(R.id.bookmarkedBlogsListView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        from = 1;
        to = 15;
//        nextPageNumber=1;
        hitBookmarkedArticleListingAPI();

        articlesListingAdapter = new NewArticlesListingAdapter(getActivity(), true);
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
                    hitBookmarkedArticleListingAPI();
                    isReuqestRunning = true;
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ArticlesAndBlogsDetailsActivity.class);
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

    @Override
    protected void updateUi(Response response) {
    }

    private void processBookmarkResponse(ArticleListingResponse responseData) {

        //	parentingResponse = responseData ;
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {

            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination

            } else {
                // No results for search
                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(dataList);
                articlesListingAdapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText("No articles found");
            }

//            articleDataModelsNew = dataList;
//            articlesListingAdapter.setNewListData(articleDataModelsNew);
//            articlesListingAdapter.notifyDataSetChanged();
//            noBlogsTextView.setVisibility(View.VISIBLE);
//            noBlogsTextView.setText("No articles found");
        } else {
            noBlogsTextView.setVisibility(View.GONE);
//            totalPageCount = responseData.getResult().getData().getPage_count();
            if (from == 1) {
                articleDataModelsNew = dataList;
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            from = from + 15;
            to = to + 15;
            articlesListingAdapter.notifyDataSetChanged();
        }

    }

    private void hitBookmarkedArticleListingAPI() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bookmarkedList = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            ToastUtils.showToast(getActivity(), "");
            return;
        }
        Call<ArticleListingResponse> call = bookmarkedList.getBookmarkedList(from, to);
        //asynchronous call
        call.enqueue(mBookmarkListingResponseReceiver);

    }

    Callback<ArticleListingResponse> mBookmarkListingResponseReceiver = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processBookmarkResponse(responseData);
                } else {
                    String message = responseData.getReason();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ToastUtils.showToast(getActivity(), message);
                    } else {
                        ToastUtils.showToast(getActivity(), getString(R.string.went_wrong));
                    }
                }
                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);
            } catch (Exception ex) {
                Crashlytics.logException(ex);
                Log.d("MC4kException", Log.getStackTraceString(ex));
                ((BloggerDashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
            ((BloggerDashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
        }
    };

}
