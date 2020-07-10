package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.adapter.MomVlogHorizontalRecyclerAdapter;
import com.mycity4kids.ui.adapter.MomVlogListingAdapter;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.vlogs.VlogsCategoryWiseChallengesResponse;
import java.util.ArrayList;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * git Created by hemant on 29/5/17.
 */

public class CategoryVideosTabFragment extends BaseFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, MomVlogHorizontalRecyclerAdapter.ClickListener {

    private MomVlogListingAdapter articlesListingAdapter;
    private ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;
    private FloatingActionsMenu fabMenu;
    private RecyclerView listView;
    private RelativeLayout lodingView;
    TextView noBlogsTextView;
    private FloatingActionButton popularSortFab;
    private FloatingActionButton recentSortFab;
    private FloatingActionButton fabSort;
    private FrameLayout frameLayout;
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
    private RecyclerView scrollView;
    private MomVlogHorizontalRecyclerAdapter momVlogHorizontalRecyclerAdapter;
    private ArrayList<Topics> subCategoriesTopicList;
    private ArrayList<Topics> categoriesList;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private GridLayoutManager gridLayoutManager;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.funny_videos_tab_fragment, container, false);
        rootLayout = view.findViewById(R.id.rootLayout);
        listView = (RecyclerView) view.findViewById(R.id.vlogsListView);
        lodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
        fabMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        popularSortFab = (FloatingActionButton) view.findViewById(R.id.popularSortFAB);
        recentSortFab = (FloatingActionButton) view.findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) view.findViewById(R.id.fabSort);
        funnyvideosshimmer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_funny_videos_article);
        scrollView = view.findViewById(R.id.scrollView);
        frameLayout.getBackground().setAlpha(0);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setEnabled(false);

        if (getArguments() != null) {
            videoCategory = getArguments().getString("video_category_id");
            topic = getArguments().getParcelable("currentSubTopic");
        }

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        popularSortFab.setOnClickListener(this);
        recentSortFab.setOnClickListener(this);
        subCategoriesTopicList = new ArrayList<>();
        categoriesList = new ArrayList<>();
        Topics momVlogsSubCategoryModel = new Topics();
        momVlogsSubCategoryModel.setId(videoCategory);
        momVlogsSubCategoryModel.setDisplay_name(rootLayout.getContext().getString(R.string.all_categories_label));
        subCategoriesTopicList.add(0, momVlogsSubCategoryModel);
        for (int i = 0; i < topic.getChild().size(); i++) {
            if ("1".equals(topic.getChild().get(i).getShowInMenu())) {
                subCategoriesTopicList.add(topic.getChild().get(i));
            }
        }
        for (int i = 0; i < subCategoriesTopicList.size(); i++) {
            subCategoriesTopicList.get(i).setSelectedSubCategory(false);
        }
        subCategoriesTopicList.get(0).setSelectedSubCategory(true);
        if (subCategoriesTopicList.size() > 1) {
            momVlogHorizontalRecyclerAdapter = new MomVlogHorizontalRecyclerAdapter(this, getActivity());
            scrollView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
            scrollView.setAdapter(momVlogHorizontalRecyclerAdapter);
            momVlogHorizontalRecyclerAdapter.setListData(subCategoriesTopicList);
            momVlogHorizontalRecyclerAdapter.notifyDataSetChanged();
        }

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

        view.findViewById(R.id.imgLoader)
                .startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        articleDataModelsNew = new ArrayList<>();
        nextPageNumber = 1;
        hitArticleListingApi();

        articlesListingAdapter = new MomVlogListingAdapter(getActivity());
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        listView.setLayoutManager(gridLayoutManager);
        articlesListingAdapter.setNewListData(articleDataModelsNew);
        listView.setAdapter(articlesListingAdapter);
        articlesListingAdapter.notifyDataSetChanged();
        listView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent,
                    @NotNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position
                if (articlesListingAdapter.getItemViewType(position) == 1
                        || articlesListingAdapter.getItemViewType(position) == 2) {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                } else {
                    if (((GridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex() == 0) {
                        // int column = position % spanCount; // item column
                        ;
                        Log.d("Position____item",
                                ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex() + "");
                        outRect.right = 4;
                    } else if (((GridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex() == 1) {
                        Log.d("Position____item1",
                                ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex() + "");
                        outRect.left = 4;
                    } else {
                        outRect.left = 0;
                        outRect.right = 0;
                        outRect.top = 0;
                        outRect.bottom = 0;
                    }
                }
            }
        });
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (articlesListingAdapter.getItemViewType(position) == 1) {
                    return 2;
                }
                return 1;
            }
        });
        listView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        if (dy > 0) {
                            firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                            visibleItemCount = gridLayoutManager.getChildCount();
                            totalItemCount = gridLayoutManager.getItemCount();
                            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                            if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning
                                    && !isLastPageReached) {
                                lodingView.setVisibility(View.VISIBLE);
                                hitArticleListingApi();
                                isReuqestRunning = true;
                            }
                        }
                    }
                });

        return view;
    }

    private void hitArticleListingApi() {
        int from;
        int end;
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            return;
        }
        from = (nextPageNumber - 1) * limit;
        if (nextPageNumber == 1) {
            end = from + limit - 2;
        } else {
            limit = 10;
            end = from + limit - 1;
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsApi = retrofit.create(VlogsListingAndDetailsAPI.class);
        Log.d("VIDEO CATEGORY", "--" + videoCategory);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsApi
                .getVlogsList(from, end, sortType, 3, videoCategory);
        callRecentVideoArticles.enqueue(recentArticleResponseCallback);
    }

    private Callback<VlogsListingResponse> recentArticleResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            progressBar.setVisibility(View.GONE);
            lodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (response == null || null == response.body()) {
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
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            isReuqestRunning = false;
            isLastPageReached = true;
            if (lodingView.getVisibility() == View.VISIBLE) {
                lodingView.setVisibility(View.GONE);
            }
            if (articleDataModelsNew == null || articleDataModelsNew.isEmpty()) {
                fabSort.setVisibility(View.GONE);
                fabMenu.setVisibility(View.GONE);
                popularSortFab.setVisibility(View.GONE);
                recentSortFab.setVisibility(View.GONE);
                noBlogsTextView.setVisibility(View.VISIBLE);
                if (isAdded()) {
                    noBlogsTextView.setText(getString(R.string.all_videos_funny_videos_no_videos));
                }
                articleDataModelsNew = new ArrayList<>();
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.INVISIBLE);
            funnyvideosshimmer.stopShimmerAnimation();
            funnyvideosshimmer.setVisibility(View.GONE);
            FirebaseCrashlytics.getInstance().recordException(t);
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
                popularSortFab.setVisibility(View.GONE);
                recentSortFab.setVisibility(View.GONE);
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText(getString(R.string.all_videos_funny_videos_no_videos));
                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
            }
        } else {
            // ArrayList<VlogsListingAndDetailResult> forFollow = new ArrayList<>();
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
                // forFollow = dataList;
                getSingleChallenge();
            } else {
                articleDataModelsNew.addAll(dataList);
                if (dataList.size() >= 10) {
                    // forFollow = dataList;
                    // forFollow.add(new VlogsListingAndDetailResult(1));
                    articleDataModelsNew.add(new VlogsListingAndDetailResult(1));
                    articlesListingAdapter.setNewListData(articleDataModelsNew);
                    nextPageNumber = nextPageNumber + 1;
                    articlesListingAdapter.notifyDataSetChanged();
                }
            }
            // articlesListingAdapter.setTestData(forFollow);
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
                Utils.momVlogEvent(getActivity(), "Video Listing", "Sort_recent", "", "android",
                        SharedPrefUtils.getAppLocale(getActivity()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Show_Video_Listing", "", "");

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
                Utils.momVlogEvent(getActivity(), "Video Listing", "Sort_popular", "", "android",
                        SharedPrefUtils.getAppLocale(getActivity()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Show_Video_Listing", "", "");

            default:
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
        Utils.momVlogEvent(
                getActivity(),
                "Video Listing",
                topic.getDisplay_name(),
                "",
                "android",
                SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                "" + System.currentTimeMillis(),
                "Show_Video_Listing",
                topic.getId(),
                ""
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        funnyvideosshimmer.stopShimmerAnimation();
    }

    @Override
    public void onRecyclerClick(int position) {
        for (int i = 0; i < subCategoriesTopicList.size(); i++) {
            if (position == i) {
                subCategoriesTopicList.get(i).setSelectedSubCategory(true);
                videoCategory = subCategoriesTopicList.get(i).getId();
                nextPageNumber = 1;
                limit = 10;
                isLastPageReached = false;
                isReuqestRunning = false;
                articleDataModelsNew.clear();
                articlesListingAdapter.notifyDataSetChanged();
                funnyvideosshimmer.setVisibility(View.VISIBLE);
                funnyvideosshimmer.startShimmerAnimation();
                hitArticleListingApi();
                Utils.momVlogEvent(
                        getActivity(),
                        "Video Listing",
                        subCategoriesTopicList.get(i).getDisplay_name(),
                        "",
                        "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        "" + System.currentTimeMillis(),
                        "Show_Video_Listing",
                        videoCategory,
                        ""
                );
            } else {
                subCategoriesTopicList.get(i).setSelectedSubCategory(false);
            }
        }

        momVlogHorizontalRecyclerAdapter.setListData(subCategoriesTopicList);
        momVlogHorizontalRecyclerAdapter.notifyDataSetChanged();
    }


    private void getSingleChallenge() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            return;
        }
        ArrayList<String> singleChallengeId = new ArrayList<String>();
        singleChallengeId.add(videoCategory);
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsApi = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsCategoryWiseChallengesResponse> callRecentVideoArticles = vlogsListingAndDetailsApi
                .getSingleChallenge(singleChallengeId);
        callRecentVideoArticles.enqueue(vlogChallengeResponseCallBack);
    }


    private Callback<VlogsCategoryWiseChallengesResponse> vlogChallengeResponseCallBack =
            new Callback<VlogsCategoryWiseChallengesResponse>() {
                @Override
                public void onResponse(Call<VlogsCategoryWiseChallengesResponse> call,
                        retrofit2.Response<VlogsCategoryWiseChallengesResponse> response) {
                    if (null == response.body()) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        return;
                    }
                    if (response.isSuccessful()) {
                        try {
                            VlogsCategoryWiseChallengesResponse responseData = response.body();
                            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                                processChallengesData(responseData.getData().getResult());
                            }
                        } catch (Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            Log.d("MC4kException", Log.getStackTraceString(e));
                            articleDataModelsNew.add(new VlogsListingAndDetailResult(1));
                            articlesListingAdapter.setNewListData(articleDataModelsNew);
                            nextPageNumber = nextPageNumber + 1;
                            articlesListingAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<VlogsCategoryWiseChallengesResponse> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                    articleDataModelsNew.add(new VlogsListingAndDetailResult(1));
                    articlesListingAdapter.setNewListData(articleDataModelsNew);
                    nextPageNumber = nextPageNumber + 1;
                    articlesListingAdapter.notifyDataSetChanged();
                }
            };

    private void processChallengesData(ArrayList<Topics> catWiseChallengeList) {
        if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
            if (articleDataModelsNew.size() >= 5 && null != catWiseChallengeList && !catWiseChallengeList.isEmpty()) {
                VlogsListingAndDetailResult item = new VlogsListingAndDetailResult(2);
                for (int i = 0; i <= catWiseChallengeList.size(); i++) {
                    if (catWiseChallengeList.get(i).getPublicVisibility().equals("1")) {
                        item.setChallengeInfo(catWiseChallengeList.get(i));
                        break;
                    }
                }
                articleDataModelsNew.add(item);
                Collections.swap(articleDataModelsNew, 4, articleDataModelsNew.size() - 1);
            }
            articleDataModelsNew.add(new VlogsListingAndDetailResult(1));
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }
    }
}
