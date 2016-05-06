package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ParentingStopController;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.BloggerDashboardModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;
import com.mycity4kids.ui.adapter.BloggerDashboardPagerAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 18/3/16.
 */
public class BookmarkedBlogsListTabFragment extends BaseFragment {
    ArticlesListingAdapter articlesListingAdapter;
    ListView listView;
    ArrayList<CommonParentingList> articleDataModelsNew;

    String sortType;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;

    private int totalPageCount = 3;
    private int nextPageNumber = 1;
    private boolean isReuqestRunning = false;


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

        nextPageNumber=1;
        hitBookmarkedArticleListingAPI(nextPageNumber);

        articlesListingAdapter = new ArticlesListingAdapter(getActivity(), true);
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
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && (nextPageNumber < 2 || nextPageNumber <= totalPageCount)) {
                    mLodingView.setVisibility(View.VISIBLE);
                    hitBookmarkedArticleListingAPI(nextPageNumber);
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

        CommonParentingResponse responseData;
        if (response == null) {
            ((BloggerDashboardActivity) getActivity()).showToast("Something went wrong from server");
            removeProgressDialog();
            mLodingView.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.BOOKMARKED_ARTICLE_LIST_REQUEST:
        /*        responseData = (CommonParentingResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {

                    processBookmarkResponse(responseData);

                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ToastUtils.showToast(getActivity(), message);
                    } else {
                        ToastUtils.showToast(getActivity(), getString(R.string.went_wrong));
                    }
                }

                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);*/
                break;

            default:
                break;
        }
    }

    private void processBookmarkResponse(CommonParentingResponse responseData) {

        ArrayList<CommonParentingList> dataList = responseData.getResult().getData().getData();

        if (dataList.size() == 0) {
            articleDataModelsNew = dataList;
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            articlesListingAdapter.notifyDataSetChanged();
            noBlogsTextView.setVisibility(View.VISIBLE);
            //((DashboardActivity) getActivity()).showToast(responseData.getResult().getMessage());
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            totalPageCount = responseData.getResult().getData().getPage_count();
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

    private void hitBookmarkedArticleListingAPI(int page) {
        /*ParentingRequest _parentingModel = new ParentingRequest();
        _parentingModel.setSoty_by("bookmark");

        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + page);
        ParentingStopController _controller = new ParentingStopController(getActivity(), this);
//        mIsRequestRunning = true;
        _controller.getData(AppConstants.BOOKMARKED_ARTICLE_LIST_REQUEST, _parentingModel);*/
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bookmarkedList = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), "");
            return;
        }
        Call<CommonParentingResponse> call = bookmarkedList.getBookmarkedList("" + SharedPrefUtils.getUserDetailModel(getActivity()).getId(),
                ""+page );


        //asynchronous call
        call.enqueue(new Callback<CommonParentingResponse>() {
                         @Override
                         public void onResponse(Call<CommonParentingResponse> call, retrofit2.Response<CommonParentingResponse> response) {
                             int statusCode = response.code();

                             CommonParentingResponse responseData = (CommonParentingResponse) response.body();



                             if (responseData.getResponseCode() == 200) {

                                 processBookmarkResponse(responseData);

                             } else if (responseData.getResponseCode() == 400) {
                                 String message = responseData.getResult().getMessage();
                                 if (!StringUtils.isNullOrEmpty(message)) {
                                     ToastUtils.showToast(getActivity(), message);
                                 } else {
                                     ToastUtils.showToast(getActivity(), getString(R.string.went_wrong));
                                 }
                             }

                             isReuqestRunning = false;
                             mLodingView.setVisibility(View.GONE);

                         }


                         @Override
                         public void onFailure(Call<CommonParentingResponse> call, Throwable t) {

                         }
                     }
        );

    }

}
