package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatDelegate;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.adapter.VlogsListingAdapter;
import com.mycity4kids.utils.MixPanelUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */

public class CategoryVideosTabFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private VlogsListingAdapter articlesListingAdapter;
    private ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;
    FloatingActionsMenu fabMenu;
    ListView listView;
    private RelativeLayout mLodingView;
    TextView noBlogsTextView;
    FloatingActionButton popularSortFAB, recentSortFAB, fabSort;
    FrameLayout frameLayout;
    private View rootLayout;
    Topics topic;
    private int sortType = 0;
    private int nextPageNumber;
    private int limit = 10;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private ProgressBar progressBar;
    private String fromScreen;
    private ShimmerFrameLayout funnyvideosshimmer;
    private String videoCategory;
    private MixpanelAPI mixpanel;
    private SwipeRefreshLayout pullToRefresh;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.funny_videos_tab_fragment, container, false);
        rootLayout = view.findViewById(R.id.rootLayout);
        listView = (ListView) view.findViewById(R.id.vlogsListView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
        fabMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        popularSortFAB = (FloatingActionButton) view.findViewById(R.id.popularSortFAB);
        recentSortFAB = (FloatingActionButton) view.findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) view.findViewById(R.id.fabSort);
        funnyvideosshimmer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_funny_videos_article);
        frameLayout.getBackground().setAlpha(0);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);

        if (getArguments() != null) {
            videoCategory = getArguments().getString("video_category_id");
            topic = getArguments().getParcelable("currentSubTopic");
        }

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        popularSortFAB.setOnClickListener(this);
        recentSortFAB.setOnClickListener(this);
        fabSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabMenu.isExpanded()) {
                    fabMenu.collapse();
                } else {
                    fabMenu.expand();
                }
            }
        });
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });

        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

//        progressBar.setVisibility(View.VISIBLE);

        articleDataModelsNew = new ArrayList<VlogsListingAndDetailResult>();
        nextPageNumber = 1;
        hitRecommendedVideoAdApi();
        hitArticleListingApi();

        articlesListingAdapter = new VlogsListingAdapter(getActivity(), topic);
        articlesListingAdapter.setNewListData(articleDataModelsNew);
        listView.setAdapter(articlesListingAdapter);
        articlesListingAdapter.notifyDataSetChanged();


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                funnyvideosshimmer.setVisibility(View.VISIBLE);
                funnyvideosshimmer.startShimmerAnimation();
                articleDataModelsNew.clear();
                articlesListingAdapter.notifyDataSetChanged();
                nextPageNumber = 1;
                hitRecommendedVideoAdApi();
                hitArticleListingApi();
                pullToRefresh.setRefreshing(false);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    hitArticleListingApi();
                    isReuqestRunning = true;
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ParallelFeedActivity.class);
                if (adapterView.getAdapter() instanceof VlogsListingAdapter) {

                    MixPanelUtils.pushMomVlogClickEvent(mixpanel, i, "" + videoCategory);
                    VlogsListingAndDetailResult parentingListData = (VlogsListingAndDetailResult) adapterView.getAdapter().getItem(i);
                    Utils.momVlogEvent(getActivity(), "Video Listing", "Video_play_icon", parentingListData.getId(), "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Video_Detail", "", "");
                    intent.putExtra(Constants.VIDEO_ID, parentingListData.getId());
                    intent.putExtra(Constants.STREAM_URL, parentingListData.getUrl());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getAuthor().getId());
                    intent.putExtra(Constants.FROM_SCREEN, "Funny Videos Listing");
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Funny Videos");
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
                    intent.putExtra(Constants.AUTHOR, parentingListData.getAuthor().getId() + "~" + parentingListData.getAuthor().getFirstName() + " " + parentingListData.getAuthor().getLastName());
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    public void hitRecommendedVideoAdApi() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            return;
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<Topics> topicsCall = vlogsListingAndDetailsAPI.getRecommendedVideoAd();
        topicsCall.enqueue(topicsCallback);
    }

    public void hitArticleListingApi() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            return;
        }

        int from = (nextPageNumber - 1) * limit;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Log.d("VIDEO CATEGORY", "--" + videoCategory);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogsList(from, from + limit - 1, sortType, 3, videoCategory);
        callRecentVideoArticles.enqueue(recentArticleResponseCallback);
    }

    private Callback<Topics> topicsCallback = new Callback<Topics>() {
        @Override
        public void onResponse(Call<Topics> call, retrofit2.Response<Topics> response) {
            if (response == null || null == response.body()) {
                return;
            }
            try {
                articlesListingAdapter.setRecommendedVideoAd(response.body());
                articlesListingAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(Call<Topics> call, Throwable t) {
        }
    };

    private Callback<VlogsListingResponse> recentArticleResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
//                showToast("Something went wrong from server");
                return;
            }
            try {
                VlogsListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processResponse(responseData);
                    funnyvideosshimmer.stopShimmerAnimation();
                    funnyvideosshimmer.setVisibility(View.GONE);
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            isReuqestRunning = false;
            isLastPageReached = true;
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (articleDataModelsNew == null || articleDataModelsNew.isEmpty()) {
                fabSort.setVisibility(View.GONE);
                fabMenu.setVisibility(View.GONE);
                popularSortFAB.setVisibility(View.GONE);
                recentSortFAB.setVisibility(View.GONE);
                noBlogsTextView.setVisibility(View.VISIBLE);
                if (isAdded())
                    noBlogsTextView.setText(getString(R.string.all_videos_funny_videos_no_videos));
                articleDataModelsNew = new ArrayList<>();
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.INVISIBLE);
            funnyvideosshimmer.stopShimmerAnimation();
            funnyvideosshimmer.setVisibility(View.GONE);
            Crashlytics.logException(t);
        }
    };

    private void processResponse(VlogsListingResponse responseData) {
        ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
        if (dataList == null || dataList.size() == 0) {
            isLastPageReached = true;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
            } else {
                fabSort.setVisibility(View.GONE);
                fabMenu.setVisibility(View.GONE);
                popularSortFAB.setVisibility(View.GONE);
                recentSortFAB.setVisibility(View.GONE);
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText(getString(R.string.all_videos_funny_videos_no_videos));
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
    public void onRefresh() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
//            showToast(getString(R.string.error_network));
            return;
        }

        isLastPageReached = false;
        nextPageNumber = 1;
        hitArticleListingApi();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recentSortFAB:
                funnyvideosshimmer.startShimmerAnimation();
                funnyvideosshimmer.setVisibility(View.VISIBLE);
                fabMenu.collapse();
                isLastPageReached = false;
                articleDataModelsNew.clear();
                articlesListingAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitArticleListingApi();
                Utils.momVlogEvent(getActivity(), "Video Listing", "Sort_recent", "", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Video_Listing", "", "");

                break;
            case R.id.popularSortFAB:
                funnyvideosshimmer.startShimmerAnimation();
                funnyvideosshimmer.setVisibility(View.VISIBLE);
                fabMenu.collapse();
                isLastPageReached = false;
                articleDataModelsNew.clear();
                articlesListingAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitArticleListingApi();
                Utils.momVlogEvent(getActivity(), "Video Listing", "Sort_popular", "", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Video_Listing", "", "");

                break;
        }
    }

    public void hitArticleListingSortByRecent() {
        funnyvideosshimmer.startShimmerAnimation();
        funnyvideosshimmer.setVisibility(View.VISIBLE);
        isLastPageReached = false;
        articleDataModelsNew.clear();
        articlesListingAdapter.notifyDataSetChanged();
        sortType = 0;
        nextPageNumber = 1;
        hitArticleListingApi();
    }

    public void hitArticleListingSortByPopular() {
        funnyvideosshimmer.startShimmerAnimation();
        funnyvideosshimmer.setVisibility(View.VISIBLE);
        isLastPageReached = false;
        articleDataModelsNew.clear();
        articlesListingAdapter.notifyDataSetChanged();
        sortType = 1;
        nextPageNumber = 1;
        hitArticleListingApi();
    }

    public void showSortedByDialog() {
        if (getActivity() != null) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_sort_by);
            dialog.setCancelable(true);
            dialog.findViewById(R.id.linearSortByPopular).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hitArticleListingSortByPopular();
                    dialog.dismiss();
                }
            });

            dialog.findViewById(R.id.linearSortByRecent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hitArticleListingSortByRecent();
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.findViewById(R.id.textUpdate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        funnyvideosshimmer.startShimmerAnimation();

    }

    @Override
    public void onPause() {
        super.onPause();
        funnyvideosshimmer.stopShimmerAnimation();
    }
}
