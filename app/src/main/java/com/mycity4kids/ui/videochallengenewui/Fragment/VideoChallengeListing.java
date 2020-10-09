package com.mycity4kids.ui.videochallengenewui.Fragment;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.adapter.VideoChallengeDetailListingAdapter;
import com.mycity4kids.ui.adapter.VideoChallengeDetailListingAdapter.RecyclerViewClickListener;
import com.mycity4kids.utils.ConnectivityUtils;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class VideoChallengeListing extends BaseFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, RecyclerViewClickListener {

    private RelativeLayout loadingView;
    private VideoChallengeDetailListingAdapter articlesListingAdapter;
    private String selectedId;
    private ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;
    FloatingActionsMenu fabMenu;
    RecyclerView listView;
    TextView noBlogsTextView;
    FloatingActionButton popularSortFab;
    FloatingActionButton recentSortFab;
    FloatingActionButton fabSort;
    FrameLayout frameLayout;
    private int sortType = 0;
    private int nextPageNumber;
    private int limit = 10;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private ShimmerFrameLayout funnyvideosshimmer;
    private GridLayoutManager gridLayoutManager;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_challenge_detail_listing, container, false);

        listView = view.findViewById(R.id.vlogsListView);
        loadingView = view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = view.findViewById(R.id.noBlogsTextView);
        frameLayout = view.findViewById(R.id.frame_layout);
        fabMenu = view.findViewById(R.id.fab_menu);
        popularSortFab = view.findViewById(R.id.popularSortFAB);
        recentSortFab = view.findViewById(R.id.recentSortFAB);
        fabSort = view.findViewById(R.id.fabSort);
        funnyvideosshimmer = view.findViewById(R.id.shimmer_funny_videos_article);

        frameLayout.getBackground().setAlpha(0);

        if (getArguments() != null) {
            selectedId = getArguments().getString("selectedId");
        }

        popularSortFab.setOnClickListener(this);
        recentSortFab.setOnClickListener(this);
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
                frameLayout.setOnTouchListener((v, event) -> {
                    fabMenu.collapse();
                    return true;
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
                    default:
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            });
        }

        articleDataModelsNew = new ArrayList<>();
        showProgressDialog("Fetching Data");
        nextPageNumber = 1;
        hitArticleListingApi();
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        articlesListingAdapter = new VideoChallengeDetailListingAdapter(this, "challengeTab");
        listView.setLayoutManager(gridLayoutManager);
        articlesListingAdapter.setNewListData(articleDataModelsNew);
        listView.setAdapter(articlesListingAdapter);
        articlesListingAdapter.notifyDataSetChanged();
        listView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position
                int spanCount = 2;
                int spacing = 10;//spacing between views in grid
                if (position % 2 == 0) {
                    int column = position % spanCount;
                    outRect.right = (column + 1) * spacing / spanCount;
                } else if (position % 2 == 1) {
                    int column = position % spanCount;
                    outRect.left = spacing - column * spacing / spanCount;
                } else {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                }
            }
        });

        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();

                    boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                    if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning
                            && !isLastPageReached) {
                        loadingView.setVisibility(View.VISIBLE);
                        hitArticleListingApi();
                        isReuqestRunning = true;
                    }
                }
            }
        });
        return view;
    }


    private void hitArticleListingApi() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            return;
        }
        int from = (nextPageNumber - 1) * limit;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsApi = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsApi
                .getVlogsListForWinner(from, from + limit - 1, sortType, 3, selectedId, "-winner");
        callRecentVideoArticles.enqueue(recentArticleResponseCallback);
    }

    private Callback<VlogsListingResponse> recentArticleResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            removeProgressDialog();
            loadingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (nextPageNumber == 1) {
                EventBus.getDefault().post("showDialogBox");
            }
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                VlogsListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processResponse(responseData);
                    funnyvideosshimmer.stopShimmerAnimation();
                    funnyvideosshimmer.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                removeProgressDialog();
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };


    private void processResponse(VlogsListingResponse responseData) {
        ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
        if (dataList == null || dataList.size() == 0) {
            isLastPageReached = true;
            if (null == articleDataModelsNew || articleDataModelsNew.isEmpty()) {
                fabSort.setVisibility(View.GONE);
                fabMenu.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                popularSortFab.setVisibility(View.GONE);
                recentSortFab.setVisibility(View.GONE);
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText(getString(R.string.all_first_vlog_upload));
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
            default:
                break;
        }
    }

    @Override
    public void onRecyclerClick(View v, int adapterPosition) {
        Intent intent = new Intent(getActivity(), ParallelFeedActivity.class);
        intent.putExtra(Constants.VIDEO_ID, articleDataModelsNew.get(adapterPosition).getId());
        intent.putExtra(Constants.STREAM_URL, articleDataModelsNew.get(adapterPosition).getUrl());
        intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(adapterPosition).getAuthor().getId());
        intent.putExtra(Constants.FROM_SCREEN, "Funny Videos Listing");
        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Funny Videos");
        intent.putExtra(Constants.ARTICLE_INDEX, "" + adapterPosition);
        intent.putExtra(Constants.AUTHOR,
                articleDataModelsNew.get(adapterPosition).getAuthor().getId() + "~" + articleDataModelsNew
                        .get(adapterPosition).getAuthor().getFirstName() + " " + articleDataModelsNew
                        .get(adapterPosition).getAuthor().getLastName());
        startActivity(intent);
        Utils.momVlogEvent(getActivity(), "Challenge detail", "Video", "", "android",
                SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                String.valueOf(System.currentTimeMillis()), "Show_Video_Detail",
                articleDataModelsNew.get(adapterPosition).getId(), "");
    }
}
