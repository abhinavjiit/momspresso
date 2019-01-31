package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.adapter.VideoChallengeDetailListingAdapter;
import com.mycity4kids.utils.MixPanelUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class VideoChallengeDetailListingActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private VideoChallengeDetailListingAdapter articlesListingAdapter;
    private String selectedId;
    private String selected_Name;
    private String selectedActiveUrl;
    private String selectedStreamUrl;
    private int pos;
    private Topics topic;
    private String parentName, parentId;
    private String ActiveUrl;
    private ArrayList<String> challengeId = new ArrayList<>();
    private ArrayList<String> activeUrl = new ArrayList<>();
    private ArrayList<String> activeStreamUrl = new ArrayList<>();
    private ArrayList<String> Display_Name = new ArrayList<>();
    private ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;
    FloatingActionsMenu fabMenu;
    ListView listView;

    private RelativeLayout mLodingView;
    TextView noBlogsTextView;
    FloatingActionButton popularSortFAB, recentSortFAB, fabSort;
    FrameLayout frameLayout;
    private RelativeLayout rootLayout, writeAtricleCell;
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
    private Toolbar mToolbar;
    private TextView toolbarTitleText;
    private String Topics;
    private String jsonMyObject;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_challenge_detail_listing);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        /* writeAtricleCell = (RelativeLayout) findViewById(R.id.writeArticleCell);*/
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.vlogsListView);
        toolbarTitleText = (TextView) findViewById(R.id.toolbarTitleTextView);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        popularSortFAB = (FloatingActionButton) findViewById(R.id.popularSortFAB);
        recentSortFAB = (FloatingActionButton) findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) findViewById(R.id.fabSort);
        funnyvideosshimmer = (ShimmerFrameLayout) findViewById(R.id.shimmer_funny_videos_article);
        frameLayout.getBackground().setAlpha(0);
        Intent intent = getIntent();

        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("Topic");
        }
        topic = new Gson().fromJson(jsonMyObject, Topics.class);


        pos = intent.getIntExtra("position", 0);
        challengeId = intent.getStringArrayListExtra("challenge");
        Display_Name = intent.getStringArrayListExtra("Display_Name");
        activeUrl = intent.getStringArrayListExtra("StringUrl");
        parentId = intent.getStringExtra("parentId");
        parentName = intent.getStringExtra("topics");
        activeStreamUrl = intent.getStringArrayListExtra("StreamUrl");

        if (challengeId != null && challengeId.size() != 0) {
            selectedId = challengeId.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (activeUrl != null && activeUrl.size() != 0) {
            selectedActiveUrl = activeUrl.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (activeStreamUrl != null && activeStreamUrl.size() != 0) {
            selectedStreamUrl = activeStreamUrl.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (Display_Name != null && Display_Name.size() != 0) {
            selected_Name = Display_Name.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        popularSortFAB.setOnClickListener(this);
        recentSortFAB.setOnClickListener(this);
        /*   writeAtricleCell.setOnClickListener(this);*/
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

        articleDataModelsNew = new ArrayList<VlogsListingAndDetailResult>();
        nextPageNumber = 1;
        hitArticleListingApi();

        articlesListingAdapter = new VideoChallengeDetailListingAdapter(this, selected_Name, selectedActiveUrl, selectedId, topic, selectedStreamUrl);
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
                if (i != 0) {
                    Intent intent = new Intent(VideoChallengeDetailListingActivity.this, MomsVlogDetailActivity.class);
                    if (adapterView.getAdapter() instanceof VideoChallengeDetailListingAdapter) {
                        MixPanelUtils.pushMomVlogClickEvent(mixpanel, i - 1, "" + videoCategory);
                        VlogsListingAndDetailResult parentingListData = (VlogsListingAndDetailResult) adapterView.getAdapter().getItem(i - 1);
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
                if (i == 0) {


                }

            }
        });

    }


    void hitArticleListingApi() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            return;
        }

        int from = (nextPageNumber - 1) * limit;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Log.d("VIDEO CATEGORY", "--" + videoCategory);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogsList(from, from + limit - 1, sortType, 3, selectedId);
        callRecentVideoArticles.enqueue(recentArticleResponseCallback);
    }

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
                noBlogsTextView.setText(getString(R.string.all_videos_funny_videos_no_videos));
                articleDataModelsNew = new ArrayList<>();
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.INVISIBLE);
            funnyvideosshimmer.stopShimmerAnimation();
            funnyvideosshimmer.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
//            showToast(getString(R.string.went_wrong));
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
    protected void updateUi(Response response) {

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
            /*case R.id.writeArticleCell:
                Intent intent = new Intent(this, ChooseVideoCategoryActivity.class);
                if (selected_Name != null && !selected_Name.isEmpty() && selectedId != null && !selectedId.isEmpty()) {
                    intent.putExtra("selectedId", selectedId);
                    intent.putExtra("selectedName", selected_Name);
                    intent.putExtra("comingFrom", "Challenge");
                    startActivity(intent);
                }*/


        }

    }

    @Override
    public void onRefresh() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
//            showToast(getString(R.string.error_network));
            return;
        }

        isLastPageReached = false;
        nextPageNumber = 1;
        hitArticleListingApi();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
