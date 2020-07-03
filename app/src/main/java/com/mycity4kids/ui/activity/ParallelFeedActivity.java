package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.collectionsModels.TutorialCollectionsListModel;
import com.mycity4kids.models.collectionsModels.TutorialCollectionsModel;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.BaseResponseGeneric;
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
import com.mycity4kids.ui.adapter.VideoRecyclerViewAdapter.VideoFeedRecyclerViewClick;
import com.mycity4kids.ui.fragment.AddCollectionAndCollectionItemDialogFragment;
import com.mycity4kids.ui.fragment.ReportContentDialogFragment;
import com.mycity4kids.ui.fragment.ViewAllCommentsFragment;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.DividerItemDecoration;
import com.mycity4kids.utils.EndlessScrollListener;
import com.mycity4kids.utils.MixPanelUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.vlogs.VlogsCategoryWiseChallengesResponse;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ParallelFeedActivity extends BaseActivity implements View.OnClickListener, ObservableScrollViewCallbacks,
        VideoFeedRecyclerViewClick {

    private static final String STATE_RESUME_WINDOW = "resumeWindow";
    private static final String STATE_RESUME_POSITION = "resumePosition";
    private static final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private static final int PAGINATION_SIZE = 10;

    private VlogsListingAndDetailResult detailData;
    private String videoId;
    private String commentUrl = "";
    private String authorId;
    private String bookmarkStatus;
    private int updateFollowPos;
    private VlogsListingAndDetailsAPI vlogsListingAndDetailsApi;
    private String userDynamoId;

    private PlayerView exoPlayerView;
    public boolean exoPlayerFullscreen = false;
    private FrameLayout fullScreenButton;
    private ImageView fullScreenIcon;
    private Dialog fullScreenDialog;

    private int resumeWindow;
    private long resumePosition;
    String streamUrl = "https://www.momspresso.com/new-videos/v1/test1/playlist.m3u8";
    private String taggedCategories;
    private MixpanelAPI mixpanel;
    ArrayList<VlogsListingAndDetailResult> dataList = new ArrayList<>();
    ArrayList<VlogsListingAndDetailResult> finalList = new ArrayList<>();
    ArrayList<VlogsListingAndDetailResult> dataListHeader = new ArrayList<>();
    ExoPlayerRecyclerView recyclerViewFeed;

    private VideoRecyclerViewAdapter videoRecyclerViewAdapter;
    private boolean firstTime = true;
    private boolean fromLoadMore = false;
    private int endIndex;
    private String bookmarkId;
    private boolean isFromPause = false;
    private ConstraintLayout root;
    private int bookMarkPosition;
    private String collectionId;
    private boolean isLastPageReached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parallel_feed_activity);
        root = findViewById(R.id.content_frame);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        Utils.pushOpenScreenEvent(this, "DetailVideoScreen", userDynamoId + "");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recyclerViewFeed = (ExoPlayerRecyclerView) findViewById(R.id.recyclerViewFeed);
        recyclerViewFeed.setRecyclerView(recyclerViewFeed);
        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            streamUrl = bundle.getString(Constants.STREAM_URL);
            collectionId = bundle.getString(AppConstants.COLLECTION_ID);
            videoId = bundle.getString(Constants.VIDEO_ID);//"videos-67496bfa-b77d-466f-9d18-94e0f98f17c6"
            authorId = bundle.getString(Constants.AUTHOR_ID, "");
            if (bundle.getBoolean("fromNotification")) {
                Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT,
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        "Notification Popup", "video_details");
            } else {
                String listingType = bundle.getString(Constants.ARTICLE_OPENED_FROM);
                String index = bundle.getString(Constants.ARTICLE_INDEX);
                String screen = bundle.getString(Constants.FROM_SCREEN);
                Utils.pushViewArticleEvent(this, screen, userDynamoId + "", videoId, listingType, index + "", authorId);
            }

            if (!ConnectivityUtils.isNetworkEnabled(this)) {
                showToast(getString(R.string.error_network));
                return;
            }
            showProgressDialog(getString(R.string.fetching_data));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            vlogsListingAndDetailsApi = retro.create(VlogsListingAndDetailsAPI.class);
            hitArticleDetailsS3Api();
        }
        videoRecyclerViewAdapter = new VideoRecyclerViewAdapter(this, ParallelFeedActivity.this,
                getSupportFragmentManager(), collectionId);
        mixpanel.timeEvent("Player_Start");
        recyclerViewFeed.scrollToPosition(0);
    }

    private void hitArticleDetailsS3Api() {
        Call<VlogsDetailResponse> call = vlogsListingAndDetailsApi.getVlogDetail(videoId);
        call.enqueue(vlogDetailResponseCallback);
    }

    private Callback<VlogsDetailResponse> vlogDetailResponseCallback = new Callback<VlogsDetailResponse>() {
        @Override
        public void onResponse(Call<VlogsDetailResponse> call, retrofit2.Response<VlogsDetailResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("Vlog API failure");
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                VlogsDetailResponse responseData = response.body();
                updateUIfromResponse(responseData.getData().getResult());
                authorId = responseData.getData().getResult().getAuthor().getId();
                hitRelatedArticleApi(0);
                commentUrl = responseData.getData().getResult().getCommentUri();
                if (StringUtils.isNullOrEmpty(commentUrl) || !commentUrl.contains("http")) {
                    commentUrl = "http";
                }
            } catch (Exception e) {
                removeProgressDialog();
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<VlogsDetailResponse> call, Throwable t) {
            removeProgressDialog();
            handleExceptions(t);
        }
    };

    public void hitBookmarkFollowingStatusApi(String vidId) {
        try {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(vidId);
            articleDetailRequest.setContentType("vlogs");
            articleDetailRequest.setType("video");
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            VlogsListingAndDetailsAPI bookmarFollowingStatusApi = retro.create(VlogsListingAndDetailsAPI.class);

            Call<AddBookmarkResponse> callBookmark = bookmarFollowingStatusApi
                    .checkFollowingBookmarkStatus(articleDetailRequest);
            callBookmark.enqueue(isBookmarkedFollowedResponseCallback);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void hitRelatedArticleApi(int startIndex) {
        if (detailData.getCategory_id() != null && !detailData.getCategory_id().isEmpty()) {
            taggedCategories = detailData.getCategory_id().get(0);
        }
        if (StringUtils.isNullOrEmpty(collectionId)) {
            endIndex = startIndex + 10;
            Call<VlogsListingResponse> callAuthorRecentcall = vlogsListingAndDetailsApi
                    .getVlogsList(startIndex, endIndex, 0, 3, taggedCategories);
            callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
        } else {
            if (startIndex != 0) {
                startIndex = startIndex + PAGINATION_SIZE - 1;
            }
            Call<BaseResponseGeneric<TutorialCollectionsListModel>> callAuthorRecentcall = vlogsListingAndDetailsApi
                    .getTutorialCollectionItems(collectionId, startIndex, PAGINATION_SIZE);
            callAuthorRecentcall.enqueue(collectionVlogsResponseCallback);
        }
    }

    public void hitUpdateViewCountApi(String videoId) {
        Call<ResponseBody> callUpdateViewCount = vlogsListingAndDetailsApi.updateViewCount(videoId);
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
                        finalList.add(new VlogsListingAndDetailResult(1));
                        getChallenges();
                    } else {
                        finalList.addAll(dataList);
                        if (dataList.size() > 10) {
                            finalList.add(new VlogsListingAndDetailResult(1));
                        }
                        recyclerViewFeed.setVideoInfoList(ParallelFeedActivity.this, finalList);
                        videoRecyclerViewAdapter.updateList(finalList);
                    }

                } else {
                    showToast(getString(R.string.server_went_wrong));
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<BaseResponseGeneric<TutorialCollectionsListModel>> collectionVlogsResponseCallback =
            new Callback<BaseResponseGeneric<TutorialCollectionsListModel>>() {
                @Override
                public void onResponse(Call<BaseResponseGeneric<TutorialCollectionsListModel>> call,
                        retrofit2.Response<BaseResponseGeneric<TutorialCollectionsListModel>> response) {
                    try {
                        BaseResponseGeneric<TutorialCollectionsListModel> responseData = response.body();
                        if (response.isSuccessful()) {
                            ArrayList<VlogsListingAndDetailResult> vlogList = convertCollectionModelToVideoModel(
                                    responseData.getData().getResult().getCollectionItems());
                            if (vlogList == null || vlogList.size() == 0 || vlogList.size() < PAGINATION_SIZE) {
                                isLastPageReached = true;
                            }
                            dataList = vlogList;

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
                            } else {
                                finalList.addAll(dataList);
                                recyclerViewFeed.setVideoInfoList(ParallelFeedActivity.this, finalList);
                                videoRecyclerViewAdapter.updateList(finalList);
                            }

                        } else {
                            showToast(getString(R.string.server_went_wrong));
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        showToast(getString(R.string.went_wrong));
                    }
                }

                @Override
                public void onFailure(Call<BaseResponseGeneric<TutorialCollectionsListModel>> call, Throwable t) {
                    handleExceptions(t);
                }
            };

    private ArrayList<VlogsListingAndDetailResult> convertCollectionModelToVideoModel(
            ArrayList<TutorialCollectionsModel> collectionItems) {
        ArrayList<VlogsListingAndDetailResult> arrayList = new ArrayList<>();
        for (int i = 0; i < collectionItems.size(); i++) {
            arrayList.add(collectionItems.get(i).getItem_info());
        }
        return arrayList;
    }

    private void setRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ParallelFeedActivity.this,
                RecyclerView.VERTICAL, false);
        recyclerViewFeed.setLayoutManager(linearLayoutManager);
        Drawable dividerDrawable = ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.divider_drawable);
        recyclerViewFeed.setOnScrollListener(new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fromLoadMore = true;
                if (!isLastPageReached) {
                    hitRelatedArticleApi(endIndex + 1);
                }
            }
        });
        recyclerViewFeed.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        recyclerViewFeed.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFeed.setAdapter(videoRecyclerViewAdapter);
        if (firstTime) {
            new Handler(Looper.getMainLooper()).post(() -> recyclerViewFeed.playVideo(false));
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
        FirebaseCrashlytics.getInstance().recordException(t);
        Log.d("MC4kException", Log.getStackTraceString(t));
    }

    private void updateUIfromResponse(VlogsListingAndDetailResult responseData) {
        detailData = responseData;
        dataListHeader.add(detailData);
        if (StringUtils.isNullOrEmpty(streamUrl)) {
            streamUrl = responseData.getUrl();
            if (exoPlayerView == null) {
                exoPlayerView = (PlayerView) findViewById(R.id.exoplayer);
                initFullscreenDialog();
                initFullscreenButton();
            }
            if (exoPlayerFullscreen) {
                ((ViewGroup) recyclerViewFeed.getParent()).removeView(recyclerViewFeed);
                fullScreenDialog.addContentView(recyclerViewFeed,
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                fullScreenIcon.setImageDrawable(
                        ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.ic_fullscreen_skrink));
                fullScreenDialog.show();
            }
        }
    }

    public void followApiCall(String id, int pos) {
        authorId = id;
        updateFollowPos = pos;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followApi = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowee_id(authorId);
        if (finalList.get(updateFollowPos).isFollowed()) {
            finalList.get(updateFollowPos).setFollowed(false);
            Utils.pushGenericEvent(this, "CTA_Unfollow_Vlog", userDynamoId, "ParallelFeedActivity");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followApi.unfollowUserV2(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
            videoRecyclerViewAdapter.setListUpdate(updateFollowPos, finalList);
        } else {
            finalList.get(updateFollowPos).setFollowed(true);
            Utils.shareEventTracking(this, "Video Detail", "Follow_Android", "VlogDetail_Follow");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followApi.followUserV2(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
            videoRecyclerViewAdapter.setListUpdate(updateFollowPos, finalList);
        }
    }

    Callback<FollowUnfollowUserResponse> followUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call,
                retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response.body() == null) {
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() != 200 || !Constants.SUCCESS.equals(responseData.getStatus())) {
                    finalList.get(updateFollowPos).setFollowed(false);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    Callback<FollowUnfollowUserResponse> unfollowUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call,
                retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response.body() == null) {
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() != 200 || !Constants.SUCCESS.equals(responseData.getStatus())) {
                    finalList.get(updateFollowPos).setFollowed(true);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<AddBookmarkResponse> isBookmarkedFollowedResponseCallback = new Callback<AddBookmarkResponse>() {
        @Override
        public void onResponse(Call<AddBookmarkResponse> call, retrofit2.Response<AddBookmarkResponse> response) {
            if (null == response.body()) {
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

    private Callback<ResponseBody> updateViewCountResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_RESUME_WINDOW, resumeWindow);
        outState.putLong(STATE_RESUME_POSITION, resumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, exoPlayerFullscreen);
        super.onSaveInstanceState(outState);
    }

    private void initFullscreenDialog() {
        fullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (exoPlayerFullscreen) {
                    closeFullscreenDialog();
                }
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {
        ((ViewGroup) recyclerViewFeed.getSimpleExo().getParent()).removeView(recyclerViewFeed.getSimpleExo());
        fullScreenDialog.addContentView(recyclerViewFeed.getSimpleExo(),
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fullScreenIcon.setImageDrawable(
                ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.ic_fullscreen_skrink));
        exoPlayerFullscreen = true;
        fullScreenDialog.show();
    }

    public void closeFullscreenDialog() {
        if ((ViewGroup) recyclerViewFeed.getSimpleExo().getParent() != null) {
            ((ViewGroup) recyclerViewFeed.getSimpleExo().getParent()).removeView(recyclerViewFeed.getSimpleExo());
            (recyclerViewFeed.frameLayout).addView(recyclerViewFeed.getSimpleExo());
        }
        exoPlayerFullscreen = false;
        fullScreenDialog.dismiss();
        fullScreenIcon.setImageDrawable(
                ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.ic_fullscreen_expand));
    }

    public void initFullscreenButton() {
        PlaybackControlView controlView = recyclerViewFeed.getSimpleExo().findViewById(R.id.exo_controller);
        fullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        fullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!exoPlayerFullscreen) {
                    openFullscreenDialog();
                } else {
                    closeFullscreenDialog();
                }
            }
        });
    }

    private void initExoPlayer() {
        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        recyclerViewFeed.restart(haveResumePosition, resumeWindow, resumePosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (streamUrl == null) {
            return;
        }

        if (exoPlayerView == null) {
            exoPlayerView = (PlayerView) findViewById(R.id.exoplayer);
            initFullscreenDialog();
            initFullscreenButton();
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
            resumeWindow = recyclerViewFeed.getSimpleExo().getPlayer().getCurrentWindowIndex();
            resumePosition = Math.max(0, recyclerViewFeed.getSimpleExo().getPlayer().getContentPosition());
            recyclerViewFeed.getSimpleExo().getPlayer().release();
        }
        if (fullScreenDialog != null) {
            closeFullscreenDialog();
        }
    }

    public void recommendUnrecommentArticleApi(String vidId, String likeStatus, int pos) {
        Utils.pushLikeStoryEvent(this, "ShortStoryDetailsScreen", userDynamoId + "", vidId, authorId + "~" + authorId);
        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest =
                new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(vidId);
        recommendUnrecommendArticleRequest.setStatus(likeStatus);
        recommendUnrecommendArticleRequest.setType("video");
        Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle = vlogsListingAndDetailsApi
                .recommendUnrecommendArticle(recommendUnrecommendArticleRequest);
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback);
    }

    private Callback<RecommendUnrecommendArticleResponse> recommendUnrecommendArticleResponseCallback =
            new Callback<RecommendUnrecommendArticleResponse>() {
                @Override
                public void onResponse(Call<RecommendUnrecommendArticleResponse> call,
                        retrofit2.Response<RecommendUnrecommendArticleResponse> response) {
                    if (null == response.body()) {
                        showToast(getString(R.string.server_went_wrong));
                        return;
                    }

                    try {
                        RecommendUnrecommendArticleResponse responseData = response.body();
                        if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                            showToast("" + responseData.getReason());
                        } else {
                            showToast(getString(R.string.server_went_wrong));
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        showToast(getString(R.string.went_wrong));
                    }
                }

                @Override
                public void onFailure(Call<RecommendUnrecommendArticleResponse> call, Throwable t) {
                    handleExceptions(t);
                }
            };

    public void openViewCommentDialog(String commentMainUrl, String shareUrl, String authorId, String author,
            String vidId, String titleSlug) {
        try {
            Bundle args = new Bundle();
            args.putString("mycityCommentURL", commentMainUrl);
            args.putString("fbCommentURL", shareUrl);
            args.putString(Constants.ARTICLE_ID, vidId);
            args.putString(Constants.AUTHOR_ID, authorId);
            args.putString(Constants.TITLE_SLUG, titleSlug);
            args.putString("contentType", AppConstants.CONTENT_TYPE_VIDEO);
            ViewAllCommentsFragment commentFrag = new ViewAllCommentsFragment();
            this.addFragment(commentFrag, args);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
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

    private Callback<AddBookmarkResponse> addBookmarkResponseCallback = new Callback<AddBookmarkResponse>() {
        @Override
        public void onResponse(Call<AddBookmarkResponse> call, retrofit2.Response<AddBookmarkResponse> response) {
            if (response == null || null == response.body()) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            AddBookmarkResponse responseData = response.body();
            if (bookmarkStatus.equals("0")) {
                finalList.get(bookMarkPosition).setBookmark_id(responseData.getData().getArticleId());
                finalList.get(bookMarkPosition).setIs_bookmark("1");
            } else {
                finalList.get(bookMarkPosition).setIs_bookmark("0");
            }
        }

        @Override
        public void onFailure(Call<AddBookmarkResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private void delete(String bookmarkId) {
        if (!StringUtils.isNullOrEmpty(bookmarkId)) {
            ArticleDetailRequest deleteBookmarkRequest = new ArticleDetailRequest();
            deleteBookmarkRequest.setId(bookmarkId);
            deleteBookmarkRequest.setContentType("vlogs");
            deleteBookmarkRequest.setType("video");
            Call<AddBookmarkResponse> call = vlogsListingAndDetailsApi.deleteBookmark(deleteBookmarkRequest);
            call.enqueue(addBookmarkResponseCallback);
        }
        Utils.pushUnbookmarkArticleEvent(this, "DetailArticleScreen", userDynamoId + "", bookmarkStatus,
                authorId + "~" + authorId);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onClick(int position, View view) {
        this.bookMarkPosition = position;
        if (view.getId() == R.id.moreOptionImageView) {
            final PopupMenu popupMenu = new PopupMenu(this,
                    view);
            popupMenu.getMenuInflater().inflate(R.menu.choose_short_story_menu, popupMenu.getMenu());

            for (int i = 0; i < popupMenu.getMenu().size(); i++) {
                if (popupMenu.getMenu().getItem(i).getItemId() == R.id.copyLink) {
                    popupMenu.getMenu().getItem(i).setVisible(false);
                } else if (popupMenu.getMenu().getItem(i).getItemId() == R.id.bookmarkShortStory) {
                    popupMenu.getMenu().getItem(i).setVisible(true);
                    if (finalList.get(position).getIs_bookmark() != null && finalList.get(position).getIs_bookmark()
                            .equals("1")) {
                        popupMenu.getMenu().getItem(i).setIcon(R.drawable.ic_bookmarked);
                    } else {
                        popupMenu.getMenu().getItem(i).setIcon(R.drawable.ic_profile_bookmark);
                    }
                }
                Drawable drawable = popupMenu.getMenu().getItem(i).getIcon();
                if (drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(getResources().getColor(R.color.app_red), PorterDuff.Mode.SRC_ATOP);
                }
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.addCollection) {
                    try {
                        AddCollectionAndCollectionItemDialogFragment addCollectionAndCollectionitemDialogFragment =
                                new AddCollectionAndCollectionItemDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("articleId", finalList.get(position).getId());
                        bundle.putString("type", AppConstants.VIDEO_COLLECTION_TYPE);
                        addCollectionAndCollectionitemDialogFragment.setArguments(bundle);
                        FragmentManager fm = getSupportFragmentManager();
                        addCollectionAndCollectionitemDialogFragment.show(fm, "collectionAdd");
                        Utils.momVlogEvent(
                                this,
                                "Video Detail",
                                "Add To Collection",
                                "",
                                "android",
                                SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                                "" + System.currentTimeMillis(),
                                "Show_Video_Detail",
                                "",
                                ""
                        );
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                    return true;
                } else if (item.getItemId() == R.id.bookmarkShortStory) {
                    if (finalList.get(position).getIs_bookmark() != null && finalList.get(position).getIs_bookmark()
                            .equals("0")) {
                        bookmarkStatus = "0";
                        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
                        articleDetailRequest.setArticleId(finalList.get(position).getId());
                        articleDetailRequest.setContentType("vlogs");
                        Call<AddBookmarkResponse> call = vlogsListingAndDetailsApi.addBookmark(articleDetailRequest);
                        call.enqueue(addBookmarkResponseCallback);
                        Utils.momVlogEvent(
                                this,
                                "Video Detail",
                                "Bookmark",
                                "",
                                "android",
                                SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                                "" + System.currentTimeMillis(),
                                "Show_Video_Detail",
                                "",
                                ""
                        );
                    } else {
                        bookmarkStatus = "1";
                        if (StringUtils.isNullOrEmpty(finalList.get(position).getBookmark_id())) {
                            hitBookmarkFollowingStatusApi(finalList.get(position).getId());
                        } else {
                            delete(finalList.get(position).getBookmark_id());
                        }
                    }
                    return true;
                } else if (item.getItemId() == R.id.reportContentShortStory) {
                    ReportContentDialogFragment reportContentDialogFragment = new
                            ReportContentDialogFragment();
                    Bundle args = new Bundle();
                    args.putString("postId", finalList.get(position).getId());
                    args.putInt("type", AppConstants.REPORT_TYPE_VIDEO);
                    reportContentDialogFragment.setArguments(args);
                    reportContentDialogFragment.setCancelable(true);
                    FragmentManager fm = getSupportFragmentManager();
                    reportContentDialogFragment.show(fm, "Report Content");
                    return true;
                }
                return false;
            });
            MenuPopupHelper menuPopupHelper = new MenuPopupHelper(view.getContext(), (MenuBuilder) popupMenu.getMenu(),
                    view);
            menuPopupHelper.setForceShowIcon(true);
            menuPopupHelper.show();
        }
    }

    private void getChallenges() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            return;
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsApi = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsCategoryWiseChallengesResponse> callRecentVideoArticles = vlogsListingAndDetailsApi
                .getSingleChallenge(detailData.getCategory_id());
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
                        }
                    }
                }

                @Override
                public void onFailure(Call<VlogsCategoryWiseChallengesResponse> call, Throwable t) {

                }
            };

    private void processChallengesData(ArrayList<Topics> catWiseChallengeList) {
        if (finalList.size() > 5) {
            if (!StringUtils
                    .isNullOrEmpty(catWiseChallengeList.get(0).getExtraData().get(0).getChallenge().getVideoUrl())) {
                VlogsListingAndDetailResult item = new VlogsListingAndDetailResult(2);
                item.setChallengeInfo(catWiseChallengeList.get(0));
                finalList.add(5, item);
            }
        }
        setRecycler();
        recyclerViewFeed.setVideoInfoList(ParallelFeedActivity.this, finalList);
        videoRecyclerViewAdapter.updateList(finalList);
    }
}
