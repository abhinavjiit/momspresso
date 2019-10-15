package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
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
import com.mycity4kids.models.response.ArticleRecommendationStatusResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.VlogsDetailResponse;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;
import com.mycity4kids.observablescrollview.ObservableScrollView;
import com.mycity4kids.observablescrollview.ObservableScrollViewCallbacks;
import com.mycity4kids.observablescrollview.ScrollState;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.ExoPlayerRecyclerView;
import com.mycity4kids.ui.adapter.VideoRecyclerViewAdapter;
import com.mycity4kids.ui.fragment.ViewAllCommentsDialogFragment;
import com.mycity4kids.utils.DividerItemDecoration;
import com.mycity4kids.utils.EndlessScrollListener;
import com.mycity4kids.utils.MixPanelUtils;
import com.mycity4kids.widget.CustomFontTextView;
import com.mycity4kids.widget.RelatedArticlesView;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ParallelFeedActivity extends BaseActivity implements View.OnClickListener, ObservableScrollViewCallbacks {


    private static final int RECOVERY_REQUEST = 1;
    private final static int ADD_BOOKMARK = 1;

    private RelativeLayout mLodingView;
    private ObservableScrollView mScrollView;
    private RelatedArticlesView relatedArticles1, relatedArticles2, relatedArticles3;
    private RelatedArticlesView trendingRelatedArticles1, trendingRelatedArticles2, trendingRelatedArticles3;
    private LinearLayout trendingArticles;
    private LinearLayout recentAuthorArticles;
    private FlowLayout tagsLayout;
    private TextView articleViewCountTextView;
    private Toolbar mToolbar;
    private ImageView authorImageView;
    private TextView followClick;
    private Rect scrollBounds;
    private TextView authorTypeTextView, authorNameTextView;
    private TextView article_title;
    private TextView articleCreatedDateTextView;

    private VlogsListingAndDetailResult detailData;
    private String videoId;
    private String commentURL = "";
    private String commentMainUrl;
    boolean isArticleDetailEndReached = false;
    private String authorId, bookmarkAuthorId;
    private String titleSlug;
    private String authorType, author;
    private String shareUrl = "";
    private String deepLinkURL;
    private Boolean isFollowing = false;
    private String likeStatus, bookmarkStatus;
    private boolean isRecommendRequestRunning;
    private String recommendationFlag = "0";
    private int recommendStatus;
    private int updateLikePos, updateBookmarkPos;
    private int updateFollowPos;
    private int changeFollowUnfollowTextPos;
//    private int nextPageNumber;


    private VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI;

    private CustomFontTextView facebookShareTextView, whatsappShareTextView, emailShareTextView, likeArticleTextView, bookmarkArticleTextView;
    private String userDynamoId;
    private ImageView backNavigationImageView;
    private LinearLayout bottomToolbarLL;
    private TextView viewCommentsTextView;

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
    ArrayList<VlogsListingAndDetailResult> finalDataList = new ArrayList<>();
    ExoPlayerRecyclerView recyclerViewFeed;
    private int followPos;

    //    private List<VideoInfo> videoInfoList = new ArrayList<>();
    private List<VlogsDetailResponse> videoInfoList = new ArrayList<>();
    private VideoRecyclerViewAdapter mAdapter;
    private boolean firstTime = true;
    private PlaybackControlView controlView;
    public String urlString;
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

        deepLinkURL = getIntent().getStringExtra(Constants.DEEPLINK_URL);

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
        mAdapter = new VideoRecyclerViewAdapter(ParallelFeedActivity.this);
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
            if (response == null || response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("Vlog API failure");
                Crashlytics.logException(nee);
                return;
            }
            try {
                VlogsDetailResponse responseData = response.body();
                updateUIfromResponse(responseData.getData().getResult());
                authorId = responseData.getData().getResult().getAuthor().getId();
//                hitBookmarkFollowingStatusAPI(videoId);
//                nextPageNumber = 1;
                hitRelatedArticleAPI(0);
                commentURL = responseData.getData().getResult().getCommentUri();
                commentMainUrl = responseData.getData().getResult().getCommentUri();
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
        Log.d("startIndex", "" + startIndex + "," + (startIndex + 10));
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
//                        recyclerViewFeed.setVideoInfoList(ParallelFeedActivity.this, finalList);
//                        mAdapter.updateList(finalList);
                    }
                    recyclerViewFeed.setVideoInfoList(ParallelFeedActivity.this, finalList);
                    mAdapter.updateList(finalList);
//                    mAdapter.notifyDataSetChanged();
//                    nextPageNumber = nextPageNumber + 1;

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

    private Callback<VlogsListingResponse> recentArticleResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {

            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }

            try {
                VlogsListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
                    if (dataList == null) {
                        return;
                    }
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getId().equals(videoId)) {
                            dataList.remove(i);
                            break;
                        }
                    }
                    if (dataList == null || dataList.size() == 0) {

                    } else {
                        trendingArticles.setVisibility(View.VISIBLE);
                        if (dataList.size() >= 3) {

                            try {
                                Picasso.with(ParallelFeedActivity.this).load(dataList.get(0).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            } catch (Exception e) {
                                trendingRelatedArticles1.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList.get(0));

                            try {
                                Picasso.with(ParallelFeedActivity.this).load(dataList.get(1).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
                            } catch (Exception e) {
                                trendingRelatedArticles2.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            trendingRelatedArticles2.setTag(dataList.get(1));

                            try {
                                Picasso.with(ParallelFeedActivity.this).load(dataList.get(2).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles3.getArticleImageView());
                            } catch (Exception e) {
                                trendingRelatedArticles3.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            trendingRelatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            trendingRelatedArticles3.setTag(dataList.get(2));
                        } else if (dataList.size() == 2) {
                            try {
                                Picasso.with(ParallelFeedActivity.this).load(dataList.get(0).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            } catch (Exception e) {
                                trendingRelatedArticles1.getArticleImageView().setImageResource(R.drawable.default_article);
                            }
                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList.get(0));
                            try {
                                Picasso.with(ParallelFeedActivity.this).load(dataList.get(1).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
                            } catch (Exception e) {
                                trendingRelatedArticles2.getArticleImageView().setImageResource(R.drawable.default_article);
                            }
                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            trendingRelatedArticles2.setTag(dataList.get(1));

                            trendingRelatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            try {
                                Picasso.with(ParallelFeedActivity.this).load(dataList.get(0).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            } catch (Exception e) {
                                trendingRelatedArticles1.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList.get(0));
                            trendingRelatedArticles2.setVisibility(View.GONE);
                            trendingRelatedArticles3.setVisibility(View.GONE);
                        }
                    }
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
//                recyclerViewFeed = (ExoPlayerRecyclerView) findViewById(R.id.exoplayer);
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
        changeFollowUnfollowTextPos = pos;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);

        if (isFollowing) {
            isFollowing = false;
            finalList.get(updateFollowPos).setFollowed(false);
            Utils.pushFollowAuthorEvent(this, "DetailVideoScreen", userDynamoId, authorId + "~" + author);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            finalList.get(updateFollowPos).setFollowed(true);
            Utils.pushUnfollowAuthorEvent(this, "DetailVideoScreen", userDynamoId, authorId + "~" + author);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.followUser(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }

    }

    Callback<FollowUnfollowUserResponse> followUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
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


    private Callback<FollowUnfollowCategoriesResponse> getFollowedTopicsResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    ArrayList<String> previouslyFollowedTopics = (ArrayList<String>) responseData.getData();
                    ArrayList<Map<String, String>> tagsList = new ArrayList<>();//detailData.getTags();
                    LayoutInflater mInflater = LayoutInflater.from(ParallelFeedActivity.this);

                    for (int i = 0; i < tagsList.size(); i++) {

                        for (Map.Entry<String, String> entry : tagsList.get(i).entrySet()) {
                            String key = entry.getKey();
                            final String value = entry.getValue();
                            if (AppConstants.IGNORE_TAG.equals(key)) {
                                continue;
                            }

                            RelativeLayout topicView = (RelativeLayout) mInflater.inflate(R.layout.related_tags_view, null, false);
                            topicView.setClickable(true);
                            topicView.getChildAt(0).setTag(key);
                            topicView.getChildAt(2).setTag(key);
                            ((TextView) topicView.getChildAt(0)).setText(value.toUpperCase());
                            if (null != previouslyFollowedTopics && previouslyFollowedTopics.contains(key)) {
                                ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.tick));
                                topicView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("TOPICS ----- ", "UNFOLLOW");
                                        followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 0);
                                    }
                                });
                            } else {
                                ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.follow_plus));
                                topicView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("TOPICS ----- ", "FOLLOW");
                                        followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 1);
                                    }
                                });
                            }

                            topicView.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String categoryId = (String) v.getTag();
                                    Intent intent = new Intent(ParallelFeedActivity.this, FilteredTopicsArticleListingActivity.class);
                                    intent.putExtra("selectedTopics", categoryId);
                                    intent.putExtra("displayName", value);
                                    startActivity(intent);
                                }
                            });
                            tagsLayout.addView(topicView);
                        }
                    }
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void followUnfollowTopics(String selectedTopic, RelativeLayout tagView, int action) {
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retro.create(TopicsCategoryAPI.class);
        FollowUnfollowCategoriesRequest followUnfollowCategoriesRequest = new FollowUnfollowCategoriesRequest();
        ArrayList<String> topicIdLList = new ArrayList<>();
        topicIdLList.add(selectedTopic);
        followUnfollowCategoriesRequest.setCategories(topicIdLList);
        if (action == 0) {
            Utils.pushFollowTopicEvent(this, "DetailVideoScreen", userDynamoId, selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
            tagView.getChildAt(0).setTag(selectedTopic);
            tagView.getChildAt(2).setTag(selectedTopic);
            ((ImageView) tagView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.follow_plus));
            tagView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 1);
                }
            });
        } else {
            Utils.pushUnfollowTopicEvent(this, "DetailVideoScreen", userDynamoId, selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
            tagView.getChildAt(0).setTag(selectedTopic);
            tagView.getChildAt(2).setTag(selectedTopic);
            ((ImageView) tagView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.tick));
            tagView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 0);
                }
            });
        }
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.followCategories(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), followUnfollowCategoriesRequest);
        call.enqueue(followUnfollowCategoriesResponseCallback);
    }

    private Callback<FollowUnfollowCategoriesResponse> followUnfollowCategoriesResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
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
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                String resData = new String(response.body().bytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
        }
        try {
            if (resultCode != RESULT_OK) {
                return;
            }
            if (requestCode == Constants.BLOG_FOLLOW_STATUS) {
                if (data.getStringExtra(Constants.BLOG_ISFOLLOWING).equalsIgnoreCase("0")) {
                    followClick.setText(getString(R.string.ad_follow_author));
                    isFollowing = false;
                } else {
                    followClick.setText(getString(R.string.ad_following_author));
                    isFollowing = true;
                }
            }
            Log.i("resultCount", String.valueOf(resultCode));
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
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
//        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
//        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//        LoadControl loadControl = new DefaultLoadControl();
//        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
//        mExoPlayerView.setPlayer(player);

        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;
        recyclerViewFeed.restart(haveResumePosition, mResumeWindow, mResumePosition);
/*
        if (haveResumePosition) {
            mExoPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
        }

        player.prepare(mVideoSource);
        mExoPlayerView.getPlayer().setPlayWhenReady(true);*/
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

//        if (mExoPlayerFullscreen) {
//            ((ViewGroup) recyclerViewFeed.getParent()).removeView(recyclerViewFeed);
//            mFullScreenDialog.addContentView(recyclerViewFeed, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(ParallelFeedActivity.this, R.drawable.ic_fullscreen_skrink));
//            mFullScreenDialog.show();
//        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        recyclerViewFeed.player.setVolume(0F);
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
    protected void onRestart() {
        super.onRestart();
//        recyclerViewFeed.player.setVolume(1F);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {

    }

    public void recommendUnrecommentArticleAPI(String vidId, String likeStatus, int pos) {
        updateLikePos = pos;
        this.likeStatus = likeStatus;
        Utils.pushLikeStoryEvent(this, "ShortStoryDetailsScreen", userDynamoId + "", vidId, authorId + "~" + author);
        isRecommendRequestRunning = true;
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
            isRecommendRequestRunning = false;
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
            isRecommendRequestRunning = false;
            handleExceptions(t);
        }
    };


    public void hitRecommendedStatusAPI(String vidId) {
        Call<ArticleRecommendationStatusResponse> checkArticleRecommendStaus = vlogsListingAndDetailsAPI.getArticleRecommendedStatus(vidId);
        checkArticleRecommendStaus.enqueue(recommendStatusResponseCallback);
    }

    private Callback<ArticleRecommendationStatusResponse> recommendStatusResponseCallback = new Callback<ArticleRecommendationStatusResponse>() {
        @Override
        public void onResponse(Call<ArticleRecommendationStatusResponse> call, retrofit2.Response<ArticleRecommendationStatusResponse> response) {
            if (response == null || null == response.body()) {
                showToast("Unable to fetch like status");
                return;
            }
            ArticleRecommendationStatusResponse responseData = response.body();
            recommendationFlag = responseData.getData().getStatus();

            if ("0".equals(recommendationFlag)) {
                recommendStatus = 0;
                dataList.get(followPos).setFollowed(false);
            } else {
                recommendStatus = 1;
                dataList.get(followPos).setFollowed(true);
            }
        }

        @Override
        public void onFailure(Call<ArticleRecommendationStatusResponse> call, Throwable t) {
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
//            mExoPlayerView.getPlayer().setPlayWhenReady(false);             //bug fixed
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    public void pushMomVlogViewEvent() {
        MixPanelUtils.pushMomVlogViewEvent(mixpanel, "ParallelFeed");
    }

    public void openPublicProfile(String authorType, String authorId, String author) {
        /*if (AppConstants.USER_TYPE_USER.equals(authorType)) {
            return;
        }*/
        if (userDynamoId.equals(authorId)) {
            Intent profileIntent = new Intent(this, DashboardActivity.class);
            profileIntent.putExtra("TabType", "profile");
            startActivity(profileIntent);
        } else {
            Intent intentnn = new Intent(this, PublicProfileActivity.class);
            intentnn.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, authorId);
            intentnn.putExtra(AppConstants.AUTHOR_NAME, "" + author);
            intentnn.putExtra(Constants.FROM_SCREEN, "Video Details");
            startActivity(intentnn);
        }
    }

    private void launchRelatedTrendingArticle(View v, String listingType, int index) {
        MixPanelUtils.pushMomVlogClickEvent(mixpanel, index, listingType);
        Intent intent = new Intent(this, ParallelFeedActivity.class);
        VlogsListingAndDetailResult parentingListData = (VlogsListingAndDetailResult) v.getTag();
        intent.putExtra(Constants.VIDEO_ID, parentingListData.getId());
        intent.putExtra(Constants.AUTHOR_ID, parentingListData.getAuthor().getId());
        intent.putExtra(Constants.FROM_SCREEN, "Video Details");
        intent.putExtra(Constants.ARTICLE_OPENED_FROM, listingType);
        intent.putExtra(Constants.ARTICLE_INDEX, "" + index);
        startActivity(intent);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        /*View tagsView = mScrollView.findViewById(R.id.recentAuthorArticles);
        Rect scrollBounds = new Rect();
        mScrollView.getHitRect(scrollBounds);
        int permanentDiff = (tagsView.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));
        if (permanentDiff <= 0) {
            isArticleDetailEndReached = true;
        } else {
            isArticleDetailEndReached = false;
        }*/
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    public void hideMainToolbar() {
    }

    private void hideToolbar() {
    }

    private void showToolbar() {
    }

    public void showMainToolbar() {
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_IN));
            }
        }
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
            jsonObject.put("videoTitle", article_title);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mixpanel.track("Player_Start", jsonObject);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void addRemoveBookmark(String bookmarkStatus, int pos, String authorId, String videoId) {
        updateBookmarkPos = pos;
        bookmarkAuthorId = authorId;
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

            //        hitBookmarkFollowingStatusAPI(videoId);
//            hitBookmarkVideoStatusAPI(videoId,bookmarkId);
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
