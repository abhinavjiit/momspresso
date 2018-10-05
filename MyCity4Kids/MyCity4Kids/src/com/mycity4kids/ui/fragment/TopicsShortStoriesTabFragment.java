package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.AddShortStoryActivity;
import com.mycity4kids.ui.activity.PrivateProfileActivity;
import com.mycity4kids.ui.activity.PublicProfileActivity;
import com.mycity4kids.ui.activity.ShortStoriesListingContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.TopicsListingFragment;
import com.mycity4kids.ui.adapter.ShortStoriesRecyclerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.FeedNativeAd;
import com.mycity4kids.widget.TrackingData;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class TopicsShortStoriesTabFragment extends BaseFragment implements View.OnClickListener, ShortStoriesRecyclerAdapter.RecyclerViewClickListener {

    private static final long MIN_TIME_VIEW = 3;

    private int nextPageNumber = 1;
    private int limit = 15;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int sortType = 0;
    private ArrayList<ArticleListingResult> mDatalist;
    private Topics currentSubTopic;
    private Topics selectedTopic;
    private boolean isHeaderVisible = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private ShortStoriesRecyclerAdapter recyclerAdapter;
//    private MainArticleListingAdapter adapter;

    private LinearLayoutManager llm;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private FlowLayout flowLayout;
    private RelativeLayout headerRL;
    private ImageView expandImageView;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton popularSortFAB;
    private FloatingActionButton recentSortFAB;
    private FloatingActionButton fabSort;
    private RecyclerView recyclerView;
    private FeedNativeAd feedNativeAd;
    private RelativeLayout guideOverlay;
    private RelativeLayout writeArticleCell;
    private boolean showGuide = false;
    private String userDynamoId;
    private View shareSSView;
    private TextView titleTextView, bodyTextView, authorTextView;
    private ShortStoryAPI shortStoryAPI;
    Set<Integer> viewedStoriesSet = new HashSet<>();
    private boolean isRecommendRequestRunning;
    private String likeStatus;
    private int currentShortStoryPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.topics_short_stories_tab_fragment, container, false);

        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        guideOverlay = (RelativeLayout) view.findViewById(R.id.guideOverlay);
        writeArticleCell = (RelativeLayout) view.findViewById(R.id.writeArticleCell);
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
        shareSSView = view.findViewById(R.id.shareSSView);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        bodyTextView = (TextView) view.findViewById(R.id.bodyTextView);
        authorTextView = (TextView) view.findViewById(R.id.authorTextView);

        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        popularSortFAB = (FloatingActionButton) view.findViewById(R.id.popularSortFAB);
        recentSortFAB = (FloatingActionButton) view.findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) view.findViewById(R.id.fabSort);

        guideOverlay.setOnClickListener(this);
        writeArticleCell.setOnClickListener(this);
        frameLayout.setVisibility(View.VISIBLE);
        fabSort.setVisibility(View.VISIBLE);
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

        mDatalist = new ArrayList<>();
        recyclerAdapter = new ShortStoriesRecyclerAdapter(getActivity(), this);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setListData(mDatalist);
        recyclerView.setAdapter(recyclerAdapter);

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        shortStoryAPI = retro.create(ShortStoryAPI.class);

        if (getArguments() != null) {
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }


        hitFilteredTopicsArticleListingApi(sortType);
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
                            hitFilteredTopicsArticleListingApi(sortType);
                        }
                    }
                }
            }
        });

        //Just For First Time -- Since complete text is not rendered in the bitmap
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.error_network));
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(selectedTopic.getId(), sortType, from, from + limit - 1, "0");

        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != mDatalist && !mDatalist.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
//                noBlogsTextView.setVisibility(View.VISIBLE);
//                noBlogsTextView.setText(getString(R.string.no_articles_found));
                writeArticleCell.setVisibility(View.VISIBLE);
                mDatalist = dataList;
                recyclerAdapter.setListData(mDatalist);
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
//            noBlogsTextView.setVisibility(View.GONE);
            writeArticleCell.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                mDatalist = dataList;

            } else {
                mDatalist.addAll(dataList);
            }
            recyclerAdapter.setListData(mDatalist);
            nextPageNumber = nextPageNumber + 1;
            recyclerAdapter.notifyDataSetChanged();
            if (nextPageNumber == 2) {
                startTracking();
            }
        }

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.writeArticleCell:
                if (isAdded()) {
                    Intent intent = new Intent(getActivity(), AddShortStoryActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.guideOverlay:
                guideOverlay.setVisibility(View.GONE);
                TopicsListingFragment frag = ((TopicsListingFragment) this.getParentFragment());
                frag.hideTabLayer();
                if (isAdded()) {
//                    ((ShortStoriesListingContainerActivity) getActivity()).hideToolbarAndNavigationLayer();
                    SharedPrefUtils.setCoachmarksShownFlag(getActivity(), "topics_article", true);
                }
                break;
            case R.id.recentSortFAB:
                fabMenu.collapse();
                mDatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(0);
                break;
            case R.id.popularSortFAB:
                fabMenu.collapse();
                mDatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(1);
                break;
        }
    }

    @Override
    public void onClick(View view, final int position) {
        switch (view.getId()) {
            case R.id.storyOptionImageView: {
                ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("postId", mDatalist.get(position).getId());
                _args.putInt("type", AppConstants.REPORT_TYPE_STORY);
                reportContentDialogFragment.setArguments(_args);
                reportContentDialogFragment.setCancelable(true);
                reportContentDialogFragment.setTargetFragment(this, 0);
                reportContentDialogFragment.show(fm, "Report Content");
            }
            break;
            case R.id.rootView:
                Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, mDatalist.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, mDatalist.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, mDatalist.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, mDatalist.get(position).getTitleSlug());
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + currentSubTopic.getParentName());
                intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                intent.putParcelableArrayListExtra("pagerListData", mDatalist);
                intent.putExtra(Constants.AUTHOR, mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName());
                startActivity(intent);
                break;
            case R.id.storyRecommendationContainer:
                if (!isRecommendRequestRunning) {
                    if (mDatalist.get(position).isLiked()) {
                        likeStatus = "0";
                        currentShortStoryPosition = position;
                        recommendUnrecommentArticleAPI("0", mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                    } else {
                        likeStatus = "1";
                        currentShortStoryPosition = position;
                        recommendUnrecommentArticleAPI("1", mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                    }
                }
                break;
            case R.id.facebookShareImageView: {
                if (isAdded()) {
                    AppUtils.shareStoryWithFB(this, mDatalist.get(position).getUserType(), mDatalist.get(position).getBlogPageSlug(), mDatalist.get(position).getTitleSlug(),
                            "ShortStoryListingScreen", userDynamoId, mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                }
            }
            break;
            case R.id.whatsappShareImageView: {
                try {
                    AppUtils.drawMultilineTextToBitmap(mDatalist.get(position).getTitle().trim(), mDatalist.get(position).getBody().trim(), mDatalist.get(position).getUserName());
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    return;
                }
                if (isAdded()) {
                    AppUtils.shareStoryWithWhatsApp(getActivity(), mDatalist.get(position).getUserType(), mDatalist.get(position).getBlogPageSlug(), mDatalist.get(position).getTitleSlug(),
                            "ShortStoryListingScreen", userDynamoId, mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                }
            }
            break;
            case R.id.instagramShareImageView: {
                try {
                    AppUtils.drawMultilineTextToBitmap(mDatalist.get(position).getTitle().trim(), mDatalist.get(position).getBody().trim(), mDatalist.get(position).getUserName());
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }

                if (isAdded()) {
                    AppUtils.shareStoryWithInstagram(getActivity(), "ShortStoryListingScreen", userDynamoId, mDatalist.get(position).getId(),
                            mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                }
            }
            break;
            case R.id.genericShareImageView: {

                if (isAdded()) {
                    AppUtils.shareStoryGeneric(getActivity(), mDatalist.get(position).getUserType(), mDatalist.get(position).getBlogPageSlug(), mDatalist.get(position).getTitleSlug(),
                            "ShortStoryListingScreen", userDynamoId, mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                }
            }
            break;
            case R.id.authorNameTextView:
                if (userDynamoId.equals(mDatalist.get(position).getUserId())) {
//                    MyAccountProfileFragment fragment0 = new MyAccountProfileFragment();
//                    Bundle mBundle0 = new Bundle();
//                    fragment0.setArguments(mBundle0);
//                    if (isAdded())
//                        ((ShortStoriesListingContainerActivity) getActivity()).addFragment(fragment0, mBundle0, true);
                    Intent pIntent = new Intent(getActivity(), PrivateProfileActivity.class);
                    startActivity(pIntent);
                } else {
                    Intent intentnn = new Intent(getActivity(), PublicProfileActivity.class);
                    intentnn.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, mDatalist.get(position).getUserId());
                    intentnn.putExtra(AppConstants.AUTHOR_NAME, mDatalist.get(position).getUserName());
                    intentnn.putExtra(Constants.FROM_SCREEN, "ShortStoryScreen");
                    startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                }
                break;
        }
    }

    private void recommendUnrecommentArticleAPI(String status, String articleId, String authorId, String author) {
        Utils.pushLikeStoryEvent(getActivity(), "ShortStoryListingScreen", userDynamoId + "", articleId, authorId + "~" + author);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);

        isRecommendRequestRunning = true;
        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest = new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(articleId);
        recommendUnrecommendArticleRequest.setStatus(likeStatus);
        Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle = articleDetailsAPI.recommendUnrecommendArticle(recommendUnrecommendArticleRequest);
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback);
    }

    private Callback<RecommendUnrecommendArticleResponse> recommendUnrecommendArticleResponseCallback = new Callback<RecommendUnrecommendArticleResponse>() {
        @Override
        public void onResponse(Call<RecommendUnrecommendArticleResponse> call, retrofit2.Response<RecommendUnrecommendArticleResponse> response) {
            isRecommendRequestRunning = false;
            if (response == null || null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((ShortStoriesListingContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }

            try {
                RecommendUnrecommendArticleResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    if (!responseData.getData().isEmpty()) {
//                        for (int i = 0; i < mDatalist.size(); i++) {
//                            if (responseData.getData().get(0).equals(mDatalist.get(i).getId())) {
//                                mDatalist.get(i).setLikesCount("" + (Integer.parseInt(mDatalist.get(i).getLikesCount()) + 1));
//                                mDatalist.get(i).setLiked(true);
//                                recyclerAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
                    if (likeStatus.equals("1")) {
                        if (!responseData.getData().isEmpty()) {
                            mDatalist.get(currentShortStoryPosition).setLikesCount("" + (Integer.parseInt(mDatalist.get(currentShortStoryPosition).getLikesCount()) + 1));
                        }
                        mDatalist.get(currentShortStoryPosition).setLiked(true);
                    } else {
                        if (!responseData.getData().isEmpty()) {
                            mDatalist.get(currentShortStoryPosition).setLikesCount("" + (Integer.parseInt(mDatalist.get(currentShortStoryPosition).getLikesCount()) - 1));
                        }
                        mDatalist.get(currentShortStoryPosition).setLiked(false);
                    }
                    recyclerAdapter.notifyDataSetChanged();
                    if (isAdded()) {
                        ((ShortStoriesListingContainerActivity) getActivity()).showToast("" + responseData.getReason());
                    }

                } else {
                    if (isAdded())
                        ((ShortStoriesListingContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((ShortStoriesListingContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<RecommendUnrecommendArticleResponse> call, Throwable t) {
            isRecommendRequestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    // Time from which a particular view has been started viewing.
    private long startTime = 0;

    // Time at which a particular view has been stopped viewing.
    private long endTime = 0;

    // Flag is required because 'addOnGlobalLayoutListener'
    // is called multiple times.
    // The flag limits the action inside 'onGlobalLayout' to only once.
    private boolean firstTrackFlag = false;

    // ArrayList of view ids that are being considered for tracking.
    private ArrayList<Integer> viewsViewed = new ArrayList<>();

    // ArrayList of TrackingData class instances.
    private ArrayList<TrackingData> trackingData = new ArrayList<>();

    // The minimum amount of area of the list item that should be on
    // the screen for the tracking to start.
    private double minimumVisibleHeightThreshold = 60;

    // Start the tracking process.
    public void startTracking() {

        // Track the views when the data is loaded into
        // recycler view for the first time.
        recyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver
                        .OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (!firstTrackFlag) {

                            startTime = System.currentTimeMillis();

                            int firstVisibleItemPosition = ((LinearLayoutManager)
                                    recyclerView.getLayoutManager())
                                    .findFirstVisibleItemPosition();

                            int lastVisibleItemPosition = ((LinearLayoutManager)
                                    recyclerView.getLayoutManager())
                                    .findLastVisibleItemPosition();

                            analyzeAndAddViewData(firstVisibleItemPosition,
                                    lastVisibleItemPosition);

                            firstTrackFlag = true;
                        }
                    }
                });

        // Track the views when user scrolls through the recyclerview.
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // User is scrolling, calculate and store the tracking
                // data of the views that were being viewed
                // before the scroll.
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    endTime = System.currentTimeMillis();

                    for (int trackedViewsCount = 0;
                         trackedViewsCount < viewsViewed.size();
                         trackedViewsCount++) {

//                        trackingData.add(prepareTrackingData(String
//                                        .valueOf(viewsViewed
//                                                .get(trackedViewsCount)),
//                                (endTime - startTime) / 1000));
                        if (((endTime - startTime) / 1000) >= MIN_TIME_VIEW) {
                            if (!viewedStoriesSet.contains(viewsViewed.get(trackedViewsCount))) {
                                updateViewCount(viewsViewed.get(trackedViewsCount));
                                viewedStoriesSet.add(viewsViewed.get(trackedViewsCount));
                            }
                        }
                    }

                    // We clear the list of current item positions.
                    // If we don't do this, the items will be tracked
                    // every time the new items are added.
                    viewsViewed.clear();
                }

                // Scrolling has ended, start the tracking
                // process by assigning a start time
                // and maintaining a list of views being viewed.
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    startTime = System.currentTimeMillis();

                    int firstVisibleItemPosition = ((LinearLayoutManager)
                            recyclerView.getLayoutManager())
                            .findFirstVisibleItemPosition();

                    int lastVisibleItemPosition = ((LinearLayoutManager)
                            recyclerView.getLayoutManager())
                            .findLastVisibleItemPosition();

                    analyzeAndAddViewData(firstVisibleItemPosition,
                            lastVisibleItemPosition);
                }
            }
        });
    }

    // Track the items currently visible and then stop the tracking process.
    public void stopTracking() {

        endTime = System.currentTimeMillis();

        int firstVisibleItemPosition = ((LinearLayoutManager)
                recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        int lastVisibleItemPosition = ((LinearLayoutManager)
                recyclerView.getLayoutManager()).findLastVisibleItemPosition();

        analyzeAndAddViewData(firstVisibleItemPosition,
                lastVisibleItemPosition);

        for (int trackedViewsCount = 0; trackedViewsCount < viewsViewed.size();
             trackedViewsCount++) {

//            trackingData.add(prepareTrackingData(String.valueOf(viewsViewed
//                            .get(trackedViewsCount)),
//                    (endTime - startTime) / 1000));
            if (((endTime - startTime) / 1000) >= MIN_TIME_VIEW) {
                if (!viewedStoriesSet.contains(viewsViewed.get(trackedViewsCount))) {
                    updateViewCount(viewsViewed.get(trackedViewsCount));
                    viewedStoriesSet.add(viewsViewed.get(trackedViewsCount));
                }
            }
            viewsViewed.clear();
        }
    }

    private void analyzeAndAddViewData(int firstVisibleItemPosition,
                                       int lastVisibleItemPosition) {

        // Analyze all the views
        for (int viewPosition = firstVisibleItemPosition;
             viewPosition <= lastVisibleItemPosition; viewPosition++) {

            Log.i("View being considered", String.valueOf(viewPosition));

            // Get the view from its position.
            View itemView = recyclerView.getLayoutManager()
                    .findViewByPosition(viewPosition);

            // Check if the visibility of the view is more than or equal
            // to the threshold provided. If it falls under the desired limit,
            // add it to the tracking data.
            if (viewPosition >= 0) {
                if (getVisibleHeightPercentage(itemView) >= minimumVisibleHeightThreshold) {
                    viewsViewed.add(viewPosition);
                }
            }
        }
    }

    // Method to calculate how much of the view is visible
    // (i.e. within the screen) wrt the view height.
    // @param view
    // @return Percentage of the height visible.
    private double getVisibleHeightPercentage(View view) {

        Rect itemRect = new Rect();
        view.getLocalVisibleRect(itemRect);

        // Find the height of the item.
        double visibleHeight = itemRect.height();
        double height = view.getMeasuredHeight();

        Log.i("Visible Height", String.valueOf(visibleHeight));
        Log.i("Measured Height", String.valueOf(height));

        double viewVisibleHeightPercentage = ((visibleHeight / height) * 100);

        Log.i("Percentage visible", String.valueOf(viewVisibleHeightPercentage));

        Log.i("___", "___");

        return viewVisibleHeightPercentage;
    }

    // Method to store the tracking data in an instance of "TrackingData" and
    // then returning that instance.
    // @param viewId
    // @param viewDuration in seconds.
    private TrackingData prepareTrackingData(String viewId, long viewDuration) {

        TrackingData trackingData = new TrackingData();

        trackingData.setViewId(viewId);
        trackingData.setViewDuration(viewDuration);

        return trackingData;
    }

    private void updateViewCount(int position) {
        Log.d("VIEW INFO", "updateViewCount -- " + mDatalist.get(position).getTitle());
        UpdateViewCountRequest updateViewCountRequest = new UpdateViewCountRequest();
        updateViewCountRequest.setUserId(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        ArrayList<Map<String, String>> tagData = mDatalist.get(position).getTags();
        Map<String, String> tagMap = new HashMap<>();
        for (Map.Entry<String, String> entry : tagData.get(0).entrySet()) {
            if (entry.getKey().contains("category-")) {
                tagMap.put(entry.getKey(), entry.getValue());
                break;
            }
        }
        List<Map<String, String>> requestTagList = new ArrayList<>();
        requestTagList.add(tagMap);
        updateViewCountRequest.setTags(requestTagList);
        updateViewCountRequest.setContentType("1");
        Call<ResponseBody> callUpdateViewCount = shortStoryAPI.updateViewCount(mDatalist.get(position).getId(), updateViewCountRequest);
        callUpdateViewCount.enqueue(updateViewCountResponseCallback);
    }

    private Callback<ResponseBody> updateViewCountResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    };
}
