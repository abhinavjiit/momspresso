package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.VlogsDetailResponse;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.observablescrollview.ObservableScrollViewCallbacks;
import com.mycity4kids.observablescrollview.ScrollState;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.ExoPlayerRecyclerView;
import com.mycity4kids.ui.adapter.VideoRecyclerViewAdapter;
import com.mycity4kids.ui.fragment.ViewAllCommentsDialogFragment;
import com.mycity4kids.utils.DividerItemDecoration;
import com.mycity4kids.utils.EndlessScrollListener;
import com.mycity4kids.utils.MixPanelUtils;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ParallelFeedActivity extends BaseActivity implements View.OnClickListener, ObservableScrollViewCallbacks {
    private VlogsListingAndDetailResult detailData;
    private String videoId;
    private String commentURL = "";
    private String authorId;
    private String author;
    private Boolean isFollowing = false;
    private String likeStatus, bookmarkStatus;
    private int updateLikePos, updateBookmarkPos;
    private int updateFollowPos;
    private VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI;
    private String userDynamoId;

    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    private SimpleExoPlayerView mExoPlayerView;
    private MediaSource mVideoSource;
    public boolean mExoPlayerFullscreen = false;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;

    private int mResumeWindow;
    private long mResumePosition;
    String streamUrl = "https://www.momspresso.com/new-videos/v1/test1/playlist.m3u8";
    private String taggedCategories;
    private MixpanelAPI mixpanel;
    ArrayList<VlogsListingAndDetailResult> dataList = new ArrayList<>();
    ArrayList<VlogsListingAndDetailResult> finalList = new ArrayList<>();
    ArrayList<VlogsListingAndDetailResult> dataListHeader = new ArrayList<>();
    ExoPlayerRecyclerView recyclerViewFeed;

    private VideoRecyclerViewAdapter mAdapter;
    private boolean firstTime = true;
    private PlaybackControlView controlView;
    private boolean fromLoadMore = false;
    private int endIndex;
    private String bookmarkId;
    private boolean isFromPause = false;
    private ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parallel_feed_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        Utils.pushOpenScreenEvent(this, "DetailVideoScreen", userDynamoId + "");
        recyclerViewFeed = (ExoPlayerRecyclerView) findViewById(R.id.recyclerViewFeed);
        recyclerViewFeed.setRecyclerView(recyclerViewFeed);
        controlView = recyclerViewFeed.findViewById(R.id.exo_controller);
        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            streamUrl = bundle.getString(Constants.STREAM_URL);
            videoId = bundle.getString(Constants.VIDEO_ID);//"videos-67496bfa-b77d-466f-9d18-94e0f98f17c6"
            authorId = bundle.getString(Constants.AUTHOR_ID, "");
            if (bundle.getBoolean("fromNotification")) {
                Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), "Notification Popup", "video_details");
            } else {
                String listingType = bundle.getString(Constants.ARTICLE_OPENED_FROM);
                String index = bundle.getString(Constants.ARTICLE_INDEX);
                String screen = bundle.getString(Constants.FROM_SCREEN);
                Utils.pushViewArticleEvent(this, screen, userDynamoId + "", videoId, listingType, index + "", author);
            }

            if (!ConnectivityUtils.isNetworkEnabled(this)) {
                showToast(getString(R.string.error_network));
                return;
            }
            showProgressDialog(getString(R.string.fetching_data));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            vlogsListingAndDetailsAPI = retro.create(VlogsListingAndDetailsAPI.class);
            hitArticleDetailsS3API();
        }
        mAdapter = new VideoRecyclerViewAdapter(ParallelFeedActivity.this, getSupportFragmentManager());
        mixpanel.timeEvent("Player_Start");
        recyclerViewFeed.scrollToPosition(0);
    }

    private void hitArticleDetailsS3API() {
        Call<VlogsDetailResponse> call = vlogsListingAndDetailsAPI.getVlogDetail(videoId);
        call.enqueue(vlogDetailResponseCallback);
    }

    private Callback<VlogsDetailResponse> vlogDetailResponseCallback = new Callback<VlogsDetailResponse>() {
        @Override
        public void onResponse(Call<VlogsDetailResponse> call, retrofit2.Response<VlogsDetailResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("Vlog API failure");
                Crashlytics.logException(nee);
                return;
            }
            try {
                VlogsDetailResponse responseData = response.body();
                updateUIfromResponse(responseData.getData().getResult());
                authorId = responseData.getData().getResult().getAuthor().getId();
                hitRelatedArticleAPI(0);
                commentURL = responseData.getData().getResult().getCommentUri();
                if (!StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http")) {
                } else {
                    commentURL = "http";
                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<VlogsDetailResponse> call, Throwable t) {
            removeProgressDialog();
            handleExceptions(t);
        }
    };

    public void hitBookmarkFollowingStatusAPI(String vidId) {
        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
        articleDetailRequest.setArticleId(vidId);
        articleDetailRequest.setContentType("vlogs");
        articleDetailRequest.setType("video");
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI bookmarFollowingStatusAPI = retro.create(VlogsListingAndDetailsAPI.class);

        Call<AddBookmarkResponse> callBookmark = bookmarFollowingStatusAPI.checkFollowingBookmarkStatus(articleDetailRequest);
        callBookmark.enqueue(isBookmarkedFollowedResponseCallback);
    }

    private void hitRelatedArticleAPI(int startIndex) {
        if (detailData.getCategory_id() != null && !detailData.getCategory_id().isEmpty()) {
            taggedCategories = detailData.getCategory_id().get(0);
        }
        endIndex = startIndex + 10;
        Call<VlogsListingResponse> callAuthorRecentcall = vlogsListingAndDetailsAPI.getVlogsList(startIndex, endIndex, 0, 3, taggedCategories);
        callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
    }

    public void hitUpdateViewCountAPI(String videoId) {
        Call<ResponseBody> callUpdateViewCount = vlogsListingAndDetailsAPI.updateViewCount(videoId);
        callUpdateViewCount.enqueue(updateViewCountResponseCallback);
    }

    private Callback<VlogsListingResponse> bloggersArticleResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            try {
                VlogsListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    dataList = responseData.getData().get(0).getResult();
                    if (dataList == null) {
                        return;
                    }
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getId().equals(videoId)) {
                            dataList.remove(i);
                            break;
                        }
                    }

                    if (!fromLoadMore) {
                        dataList.addAll(0, dataListHeader);
                        finalList = dataList;
                        setRecycler();
                    } else {
                        finalList.addAll(dataList);
                    }
                    recyclerViewFeed.setVideoInfoList(ParallelFeedActivity.this, finalList);
                    mAdapter.updateList(finalList);
                } else {
                    showToast(getString(R.string.server_went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private void setRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ParallelFeedActivity.this, RecyclerView.VERTICAL, false);
        recyclerViewFeed.setLayoutManager(linearLayoutManager);
        Drawable dividerDrawable = ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.divider_drawable);
        recyclerViewFeed.setOnScrollListener(new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fromLoadMore = true;
                hitRelatedArticleAPI(endIndex + 1);
            }
        });
        recyclerViewFeed.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        recyclerViewFeed.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFeed.setAdapter(mAdapter);
        if (firstTime) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    recyclerViewFeed.playVideo(false);
                }
            });
            firstTime = false;
        }
    }

    public void handleExceptions(Throwable t) {
        if (t instanceof UnknownHostException) {
            showToast(getString(R.string.error_network));
        } else if (t instanceof SocketTimeoutException) {
            showToast(getString(R.string.connection_timeout));
        } else {
            showToast(getString(R.string.server_went_wrong));
        }
        Crashlytics.logException(t);
        Log.d("MC4kException", Log.getStackTraceString(t));
    }

    private void updateUIfromResponse(VlogsListingAndDetailResult responseData) {
        detailData = responseData;
        dataListHeader.add(detailData);
        if (StringUtils.isNullOrEmpty(streamUrl)) {
            streamUrl = responseData.getUrl();
            if (mExoPlayerView == null) {
                mExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer);
                initFullscreenDialog();
                initFullscreenButton();

                String userAgent = Util.getUserAgent(ParallelFeedActivity.this, getApplicationContext().getApplicationInfo().packageName);
                DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(ParallelFeedActivity.this, null, httpDataSourceFactory);
                Uri daUri = Uri.parse(streamUrl);

                mVideoSource = new HlsMediaSource(daUri, dataSourceFactory, 1, null, null);
            }
            if (mExoPlayerFullscreen) {
                ((ViewGroup) recyclerViewFeed.getParent()).removeView(recyclerViewFeed);
                mFullScreenDialog.addContentView(recyclerViewFeed, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.ic_fullscreen_skrink));
                mFullScreenDialog.show();
            }
        }
    }

    public void followAPICall(String id, int pos) {
        authorId = id;
        updateFollowPos = pos;
//        changeFollowUnfollowTextPos = pos;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);

        if (isFollowing) {
            isFollowing = false;
            finalList.get(updateFollowPos).setFollowed(false);
            Utils.pushGenericEvent(this, "CTA_Unfollow_Vlog", userDynamoId, "ParallelFeedActivity");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            finalList.get(updateFollowPos).setFollowed(true);
            Utils.pushGenericEvent(this, "CTA_Follow_Vlog", userDynamoId, "ParallelFeedActivity");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.followUser(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }
    }

    Callback<FollowUnfollowUserResponse> followUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    finalList.get(updateFollowPos).setFollowed(false);
                    isFollowing = false;
                }
                mAdapter.setListUpdate(updateFollowPos, finalList);
            } catch (Exception e) {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    Callback<FollowUnfollowUserResponse> unfollowUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    finalList.get(updateFollowPos).setFollowed(true);
                    isFollowing = true;
                }
                mAdapter.setListUpdate(updateFollowPos, finalList);
            } catch (Exception e) {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<AddBookmarkResponse> isBookmarkedFollowedResponseCallback = new Callback<AddBookmarkResponse>() {
        @Override
        public void onResponse(Call<AddBookmarkResponse> call, retrofit2.Response<AddBookmarkResponse> response) {
            if (response == null || null == response.body()) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            AddBookmarkResponse responseData = response.body();//AddBookmarkResponse
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                bookmarkId = responseData.getData().getArticleId();
                delete(bookmarkId);
            } else {
                showToast(getString(R.string.server_went_wrong));
            }

        }

        @Override
        public void onFailure(Call<AddBookmarkResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<FollowUnfollowCategoriesResponse> followUnfollowCategoriesResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    showToast(responseData.getReason());
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private Callback<ResponseBody> updateViewCountResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
        super.onSaveInstanceState(outState);
    }

    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {
        ((ViewGroup) recyclerViewFeed.getSimpleExo().getParent()).removeView(recyclerViewFeed.getSimpleExo());
        mFullScreenDialog.addContentView(recyclerViewFeed.getSimpleExo(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    public void closeFullscreenDialog() {
        if ((ViewGroup) recyclerViewFeed.getSimpleExo().getParent() != null) {
            ((ViewGroup) recyclerViewFeed.getSimpleExo().getParent()).removeView(recyclerViewFeed.getSimpleExo());
            (recyclerViewFeed.frameLayout).addView(recyclerViewFeed.getSimpleExo());
        }
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.ic_fullscreen_expand));
    }

    public void initFullscreenButton() {
        PlaybackControlView controlView = recyclerViewFeed.getSimpleExo().findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }

    private void initExoPlayer() {
        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;
        recyclerViewFeed.restart(haveResumePosition, mResumeWindow, mResumePosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (streamUrl == null) {
            return;
        }

        if (mExoPlayerView == null) {
            mExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer);
            initFullscreenDialog();
            initFullscreenButton();
            String userAgent = Util.getUserAgent(ParallelFeedActivity.this, getApplicationContext().getApplicationInfo().packageName);
            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(ParallelFeedActivity.this, null, httpDataSourceFactory);
            Uri daUri = Uri.parse(streamUrl);
            mVideoSource = new HlsMediaSource(daUri, dataSourceFactory, 1, null, null);

        }
        if (isFromPause) {
            initExoPlayer();
            isFromPause = false;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        isFromPause = true;

        if ((recyclerViewFeed.getSimpleExo().getPlayer()) != null) {
            mResumeWindow = recyclerViewFeed.getSimpleExo().getPlayer().getCurrentWindowIndex();
            mResumePosition = Math.max(0, recyclerViewFeed.getSimpleExo().getPlayer().getContentPosition());
            recyclerViewFeed.getSimpleExo().getPlayer().release();
        }

        if (mFullScreenDialog != null)
            closeFullscreenDialog();
    }

    @Override
    public void onClick(View view) {
    }

    public void recommendUnrecommentArticleAPI(String vidId, String likeStatus, int pos) {
        updateLikePos = pos;
        this.likeStatus = likeStatus;
        Utils.pushLikeStoryEvent(this, "ShortStoryDetailsScreen", userDynamoId + "", vidId, authorId + "~" + author);
        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest = new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(vidId);
        recommendUnrecommendArticleRequest.setStatus(likeStatus);
        recommendUnrecommendArticleRequest.setType("video");
        Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle = vlogsListingAndDetailsAPI.recommendUnrecommendArticle(recommendUnrecommendArticleRequest);
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback);
    }

    private Callback<RecommendUnrecommendArticleResponse> recommendUnrecommendArticleResponseCallback = new Callback<RecommendUnrecommendArticleResponse>() {
        @Override
        public void onResponse(Call<RecommendUnrecommendArticleResponse> call, retrofit2.Response<RecommendUnrecommendArticleResponse> response) {
            if (response == null || null == response.body()) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }

            try {
                RecommendUnrecommendArticleResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    if (likeStatus.equals("1")) {
                        finalList.get(updateLikePos).setIs_liked("1");
                    } else {
                        finalList.get(updateLikePos).setIs_liked("0");
                    }

                    mAdapter.setList(updateLikePos, finalList);
                    showToast("" + responseData.getReason());
                } else {
                    showToast(getString(R.string.server_went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<RecommendUnrecommendArticleResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    public void openViewCommentDialog(String commentMainUrl, String shareUrl, String authorId, String author, String vidId) {
        try {
            ViewAllCommentsDialogFragment commentFrag = new ViewAllCommentsDialogFragment();
            Bundle _args = new Bundle();
            _args.putString("mycityCommentURL", commentMainUrl);
            _args.putString("fbCommentURL", shareUrl);
            _args.putString(Constants.ARTICLE_ID, vidId);
            _args.putString(Constants.AUTHOR, authorId + "~" + author);
            commentFrag.setArguments(_args);
            FragmentManager fm = getSupportFragmentManager();
            commentFrag.show(fm, "ViewAllComments");
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    public void pushMomVlogViewEvent() {
        MixPanelUtils.pushMomVlogViewEvent(mixpanel, "ParallelFeed");
    }

    public void openPublicProfile(String authorType, String authorId, String author) {
        Intent intentnn = new Intent(this, UserProfileActivity.class);
        intentnn.putExtra(Constants.USER_ID, authorId);
        intentnn.putExtra(AppConstants.AUTHOR_NAME, "" + author);
        intentnn.putExtra(Constants.FROM_SCREEN, "Video Details");
        startActivity(intentnn);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            jsonObject.put("videoId", videoId);
            if (detailData != null && detailData.getTitle() != null) {
                jsonObject.put("videoTitle", detailData.getTitle());
            } else {
                jsonObject.put("videoTitle", "videoTitle");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mixpanel.track("Player_Start", jsonObject);
    }

    public void addRemoveBookmark(String bookmarkStatus, int pos, String authorId, String videoId) {
        updateBookmarkPos = pos;
//        bookmarkAuthorId = authorId;
        this.bookmarkStatus = bookmarkStatus;
        if (bookmarkStatus.equals("1")) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(videoId);
            articleDetailRequest.setContentType("vlogs");
            Call<AddBookmarkResponse> call = vlogsListingAndDetailsAPI.addBookmark(articleDetailRequest);
            call.enqueue(addBookmarkResponseCallback);
            Utils.pushBookmarkArticleEvent(this, "DetailArticleScreen", userDynamoId + "", bookmarkStatus, authorId + "~" + author);
        } else {
            if (StringUtils.isNullOrEmpty(finalList.get(updateBookmarkPos).getBookmark_id())) {
                hitBookmarkFollowingStatusAPI(videoId);
            } else {
                delete(finalList.get(updateBookmarkPos).getBookmark_id());
            }
        }
    }


    private Callback<AddBookmarkResponse> addBookmarkResponseCallback = new Callback<AddBookmarkResponse>() {
        @Override
        public void onResponse(Call<AddBookmarkResponse> call, retrofit2.Response<AddBookmarkResponse> response) {
            if (response == null || null == response.body()) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            AddBookmarkResponse responseData = response.body();
            //  updateBookmarkStatus(ADD_BOOKMARK, responseData);
            if (bookmarkStatus.equals("1")) {
                finalList.get(updateBookmarkPos).setBookmark_id(responseData.getData().getArticleId());
                finalList.get(updateBookmarkPos).setIs_bookmark("1");
            } else {
                finalList.get(updateBookmarkPos).setIs_bookmark("0");
            }
            mAdapter.setBookmark(updateBookmarkPos, finalList);
        }

        @Override
        public void onFailure(Call<AddBookmarkResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };


    private void delete(String bookmarkId) {
        if (StringUtils.isNullOrEmpty(bookmarkId)) {
        } else {
            ArticleDetailRequest deleteBookmarkRequest = new ArticleDetailRequest();
            deleteBookmarkRequest.setId(bookmarkId);
            deleteBookmarkRequest.setContentType("vlogs");
            deleteBookmarkRequest.setType("video");
            Call<AddBookmarkResponse> call = vlogsListingAndDetailsAPI.deleteBookmark(deleteBookmarkRequest);
            call.enqueue(addBookmarkResponseCallback);
        }
        Utils.pushUnbookmarkArticleEvent(this, "DetailArticleScreen", userDynamoId + "", bookmarkStatus, authorId + "~" + author);
    }
}
