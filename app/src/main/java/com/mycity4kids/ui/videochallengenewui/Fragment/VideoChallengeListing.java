package com.mycity4kids.ui.videochallengenewui.Fragment;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mycity4kids.base.BaseFragment;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.adapter.VideoChallengeDetailListingAdapter;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.MixPanelUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class VideoChallengeListing extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private RelativeLayout mLodingView;
    private ProgressDialog mProgressDialog;
    private MixpanelAPI mixpanel;
    private VideoChallengeDetailListingAdapter articlesListingAdapter;
    private String selectedId;
    private com.mycity4kids.models.Topics topic;
    private ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;
    FloatingActionsMenu fabMenu;
    ListView listView;
    TextView noBlogsTextView;
    FloatingActionButton popularSortFAB, recentSortFAB, fabSort;
    FrameLayout frameLayout;
    private int sortType = 0;
    private int nextPageNumber;
    private int limit = 10;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private ProgressBar progressBar;
    private ShimmerFrameLayout funnyvideosshimmer;
    private String videoCategory;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_challenge_detail_listing, container, false);
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

        if (getArguments() != null) {
            selectedId = getArguments().getString("selectedId");
            topic = getArguments().getParcelable("topics");
        }

        popularSortFAB.setOnClickListener(this);
        recentSortFAB.setOnClickListener(this);
        /*   writeAtricleCell.setOnClickListener(this);*/
        fabSort.setOnClickListener(v -> {
            if (fabMenu.isExpanded()) {
                fabMenu.collapse();
            } else {
                fabMenu.expand();
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

        if (Build.VERSION.SDK_INT >= 21) {
            ViewCompat.setNestedScrollingEnabled(listView, true);
        } else {
            listView.setOnTouchListener((v, event) -> {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            });
        }


        articleDataModelsNew = new ArrayList<VlogsListingAndDetailResult>();
        showProgressDialog("Fetching Data");
        nextPageNumber = 1;
        hitArticleListingApi();

        articlesListingAdapter = new VideoChallengeDetailListingAdapter(getActivity(), selectedId, topic);
        articlesListingAdapter.setNewListData(articleDataModelsNew);
        listView.setAdapter(articlesListingAdapter);
        articlesListingAdapter.notifyDataSetChanged();


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != 0) {

                  /*  articlesListingAdapter.player.setPlayWhenReady(false);
                    articlesListingAdapter.isPaused = true;*/
                }/* else {

                    articlesListingAdapter.player.setPlayWhenReady(true);
                    articlesListingAdapter.isPaused = false;
                }*/

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
                if (adapterView.getAdapter() instanceof VideoChallengeDetailListingAdapter) {
                    MixPanelUtils.pushMomVlogClickEvent(mixpanel, i, "" + videoCategory);
                    VlogsListingAndDetailResult parentingListData = (VlogsListingAndDetailResult) adapterView.getAdapter().getItem(i);
                    intent.putExtra(Constants.VIDEO_ID, parentingListData.getId());
                    intent.putExtra(Constants.STREAM_URL, parentingListData.getUrl());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getAuthor().getId());
                    intent.putExtra(Constants.FROM_SCREEN, "Funny Videos Listing");
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Funny Videos");
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
                    intent.putExtra(Constants.AUTHOR, parentingListData.getAuthor().getId() + "~" + parentingListData.getAuthor().getFirstName() + " " + parentingListData.getAuthor().getLastName());
                    startActivity(intent);
                    Utils.momVlogEvent(getActivity(), "Challenge detail", "Video", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Video_Detail", parentingListData.getId(), "");

                }
            }
        });
        return view;
    }


    void hitArticleListingApi() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            return;
        }
        int from = (nextPageNumber - 1) * limit;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Log.d("VIDEO CATEGORY", "--" + videoCategory);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogsListForWinner(from, from + limit - 1, sortType, 3, selectedId, "-winner");
        callRecentVideoArticles.enqueue(recentArticleResponseCallback);
    }

    private Callback<VlogsListingResponse> recentArticleResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            removeProgressDialog();
            if (nextPageNumber == 1) {
                EventBus.getDefault().post("showDialogBox");
            }
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
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
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
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
                break;


        }

    }
}
