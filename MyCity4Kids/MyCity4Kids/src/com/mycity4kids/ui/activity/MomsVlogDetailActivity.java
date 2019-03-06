package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
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
import com.mycity4kids.ui.fragment.ViewAllCommentsDialogFragment;
import com.mycity4kids.utils.AppUtils;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MomsVlogDetailActivity extends BaseActivity implements View.OnClickListener, ObservableScrollViewCallbacks {

    private static final int RECOVERY_REQUEST = 1;

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
    private String authorId;
    private String titleSlug;
    private String authorType, author;
    private String shareUrl = "";
    private String deepLinkURL;
    private Boolean isFollowing = false;


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
    private boolean mExoPlayerFullscreen = false;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;

    private int mResumeWindow;
    private long mResumePosition;
    String streamUrl = "https://www.momspresso.com/new-videos/v1/test1/playlist.m3u8";
    private String taggedCategories;
    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_exo);
        userDynamoId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        Utils.pushOpenScreenEvent(this, "DetailVideoScreen", userDynamoId + "");

        deepLinkURL = getIntent().getStringExtra(Constants.DEEPLINK_URL);

        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        trendingArticles = (LinearLayout) findViewById(R.id.trendingArticles);
        recentAuthorArticles = (LinearLayout) findViewById(R.id.recentAuthorArticles);
        relatedArticles1 = (RelatedArticlesView) findViewById(R.id.relatedArticles1);
        relatedArticles2 = (RelatedArticlesView) findViewById(R.id.relatedArticles2);
        relatedArticles3 = (RelatedArticlesView) findViewById(R.id.relatedArticles3);
        trendingRelatedArticles1 = (RelatedArticlesView) findViewById(R.id.trendingRelatedArticles1);
        trendingRelatedArticles2 = (RelatedArticlesView) findViewById(R.id.trendingRelatedArticles2);
        trendingRelatedArticles3 = (RelatedArticlesView) findViewById(R.id.trendingRelatedArticles3);
        bottomToolbarLL = (LinearLayout) findViewById(R.id.bottomToolbarLL);
        facebookShareTextView = (CustomFontTextView) findViewById(R.id.facebookShareTextView);
        whatsappShareTextView = (CustomFontTextView) findViewById(R.id.whatsappShareTextView);
        emailShareTextView = (CustomFontTextView) findViewById(R.id.emailShareTextView);
        likeArticleTextView = (CustomFontTextView) findViewById(R.id.likeTextView);
        bookmarkArticleTextView = (CustomFontTextView) findViewById(R.id.bookmarkTextView);
        tagsLayout = (FlowLayout) findViewById(R.id.tagsLayout);
        articleViewCountTextView = (TextView) findViewById(R.id.articleViewCountTextView);
        authorImageView = (ImageView) findViewById(R.id.user_image);
        article_title = (TextView) findViewById(R.id.article_title);
        authorTypeTextView = (TextView) findViewById(R.id.blogger_type);
        authorNameTextView = (TextView) findViewById(R.id.user_name);
        articleCreatedDateTextView = (TextView) findViewById(R.id.article_date);
        followClick = (TextView) findViewById(R.id.follow_click);
        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        backNavigationImageView = (ImageView) findViewById(R.id.backNavigationImageView);
        viewCommentsTextView = ((TextView) findViewById(R.id.viewCommentsTextView));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.app_logo);

        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

        followClick.setOnClickListener(this);
        viewCommentsTextView.setOnClickListener(this);
        backNavigationImageView.setOnClickListener(this);
        authorNameTextView.setOnClickListener(this);
        authorImageView.setOnClickListener(this);
        relatedArticles1.setOnClickListener(this);
        relatedArticles2.setOnClickListener(this);
        relatedArticles3.setOnClickListener(this);
        trendingRelatedArticles1.setOnClickListener(this);
        trendingRelatedArticles2.setOnClickListener(this);
        trendingRelatedArticles3.setOnClickListener(this);
        facebookShareTextView.setOnClickListener(this);
        whatsappShareTextView.setOnClickListener(this);
        emailShareTextView.setOnClickListener(this);
        likeArticleTextView.setOnClickListener(this);
        bookmarkArticleTextView.setOnClickListener(this);

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        likeArticleTextView.setEnabled(false);
        bookmarkArticleTextView.setEnabled(false);
        likeArticleTextView.setTextColor(ContextCompat.getColor(this, R.color.grey));
        bookmarkArticleTextView.setTextColor(ContextCompat.getColor(this, R.color.grey));
        setTextViewDrawableColor(likeArticleTextView, R.color.grey);
        setTextViewDrawableColor(bookmarkArticleTextView, R.color.grey);
        mScrollView.setScrollViewCallbacks(this);
        followClick.setEnabled(false);

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            streamUrl = bundle.getString(Constants.STREAM_URL);
            videoId = bundle.getString(Constants.VIDEO_ID);//"videos-67496bfa-b77d-466f-9d18-94e0f98f17c6"
            authorId = bundle.getString(Constants.AUTHOR_ID, "");
            if (bundle.getBoolean("fromNotification")) {
                Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "video_details");
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

        scrollBounds = new Rect();
        mScrollView.getHitRect(scrollBounds);
        mixpanel.timeEvent("Player_Start");
    }

    private void hitArticleDetailsS3API() {
        Call<VlogsDetailResponse> call = vlogsListingAndDetailsAPI.getVlogDetail(videoId);
        call.enqueue(vlogDetailResponseCallback);
    }

    private void hitBookmarkFollowingStatusAPI() {
        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
        articleDetailRequest.setArticleId(videoId);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI bookmarFollowingStatusAPI = retro.create(VlogsListingAndDetailsAPI.class);

        Call<ArticleDetailResponse> callBookmark = bookmarFollowingStatusAPI.checkFollowingBookmarkStatus(videoId, authorId);
        callBookmark.enqueue(isBookmarkedFollowedResponseCallback);
    }

    private void hitRelatedArticleAPI() {

        if (detailData.getCategory_id() != null && !detailData.getCategory_id().isEmpty()) {
            taggedCategories = detailData.getCategory_id().get(0);
        }

        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogsList(0, 4, 0, 3, taggedCategories);
        callRecentVideoArticles.enqueue(recentArticleResponseCallback);

        Call<VlogsListingResponse> callAuthorRecentcall = vlogsListingAndDetailsAPI.getVlogsList(0, 4, 1, 3, null);
        callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
    }

    private void hitUpdateViewCountAPI(String userId) {
        Call<ResponseBody> callUpdateViewCount = vlogsListingAndDetailsAPI.updateViewCount(videoId);
        callUpdateViewCount.enqueue(updateViewCountResponseCallback);
    }

    public void followAPICall(String id) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);

        if (isFollowing) {
            isFollowing = false;
            followClick.setText(getString(R.string.ad_follow_author));
            Utils.pushFollowAuthorEvent(this, "DetailVideoScreen", userDynamoId, authorId + "~" + author);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            followClick.setText(getString(R.string.ad_following_author));
            Utils.pushUnfollowAuthorEvent(this, "DetailVideoScreen", userDynamoId, authorId + "~" + author);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.followUser(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }

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
                hitBookmarkFollowingStatusAPI();
                hitRelatedArticleAPI();
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

    private void updateUIfromResponse(VlogsListingAndDetailResult responseData) {
        detailData = responseData;

        if (StringUtils.isNullOrEmpty(streamUrl)) {
            streamUrl = responseData.getUrl();
            if (mExoPlayerView == null) {

                mExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer);
                initFullscreenDialog();
                initFullscreenButton();

                String userAgent = Util.getUserAgent(MomsVlogDetailActivity.this, getApplicationContext().getApplicationInfo().packageName);
                DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(MomsVlogDetailActivity.this, null, httpDataSourceFactory);
                Uri daUri = Uri.parse(streamUrl);

                mVideoSource = new HlsMediaSource(daUri, dataSourceFactory, 1, null, null);
            }

            initExoPlayer();

            if (mExoPlayerFullscreen) {
                ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
                mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(MomsVlogDetailActivity.this, R.drawable.ic_fullscreen_skrink));
                mFullScreenDialog.show();
            }
        }
        authorType = responseData.getAuthor().getUserType();
        author = responseData.getAuthor().getFirstName() + " " + responseData.getAuthor().getLastName();
        article_title.setText(responseData.getTitle());
        titleSlug = responseData.getTitleSlug();
        articleViewCountTextView.setText(responseData.getView_count() + " Views");
        try {
            if (!StringUtils.isNullOrEmpty(authorType)) {

                if (AppConstants.USER_TYPE_BLOGGER.equals(authorType)) {
                    authorTypeTextView.setText(getString(R.string.author_type_blogger));
                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_blogger));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        String bSlug = detailData.getAuthor().getBlogTitleSlug();
                        if (StringUtils.isNullOrEmpty(bSlug)) {
                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
                        } else {
                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + bSlug + "/video/" + detailData.getTitleSlug();
                        }
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EXPERT.equals(authorType)) {
                    authorTypeTextView.setText(getString(R.string.author_type_expert));
                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_expert));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITOR.equals(authorType)) {
                    authorTypeTextView.setText(getString(R.string.author_type_editor));
                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_editor));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITORIAL.equals(authorType)) {
                    authorTypeTextView.setText(getString(R.string.author_type_editorial));
                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_editorial));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_FEATURED.equals(authorType)) {
                    authorTypeTextView.setText(getString(R.string.author_type_featured));
                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_featured));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_USER.equals(authorType)) {
                    authorTypeTextView.setText(getString(R.string.author_type_user));
                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_blogger));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        String bSlug = detailData.getAuthor().getBlogTitleSlug();
                        if (StringUtils.isNullOrEmpty(bSlug)) {
                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
                        } else {
                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + bSlug + "/video/" + detailData.getTitleSlug();
                        }
                    } else {
                        shareUrl = deepLinkURL;
                    }
                }
            } else {
                // Default Author type set to Blogger
                authorTypeTextView.setText(getString(R.string.author_type_user));
                authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_blogger));
                if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                    shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + detailData.getAuthor().getBlogTitleSlug() + "/video/" + titleSlug;
                } else {
                    shareUrl = deepLinkURL;
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        hitUpdateViewCountAPI(detailData.getAuthor().getId());
        authorNameTextView.setText(author);

        try {
            Picasso.with(this).load(responseData.getAuthor().getProfilePic().getClientApp()).into(authorImageView);
        } catch (Exception e) {
            authorImageView.setImageResource(R.drawable.default_blogger_profile_img);
        }
        try {
            articleCreatedDateTextView.setText(DateTimeUtils.getDateFromTimestamp(Long.parseLong(responseData.getPublished_time())));
        } catch (Exception e) {
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
                    followClick.setText(getString(R.string.ad_follow_author));
                    isFollowing = false;
                }
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
                    followClick.setText(getString(R.string.ad_following_author));
                    isFollowing = true;
                }
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

    private Callback<ArticleDetailResponse> isBookmarkedFollowedResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            if (response == null || null == response.body()) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }

            ArticleDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (SharedPrefUtils.getUserDetailModel(MomsVlogDetailActivity.this).getDynamoId().equals(authorId)) {
                    followClick.setVisibility(View.INVISIBLE);
                } else {
                    if ("0".equals(responseData.getData().getResult().getIsFollowed())) {
                        followClick.setEnabled(true);
                        followClick.setText(getString(R.string.ad_follow_author));
                        isFollowing = false;
                    } else {
                        followClick.setEnabled(true);
                        followClick.setText(getString(R.string.ad_following_author));
                        isFollowing = true;
                    }
                }
            } else {
                showToast(getString(R.string.server_went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<VlogsListingResponse> bloggersArticleResponseCallback = new Callback<VlogsListingResponse>() {
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
                    if (dataList.size() == 0) {
                    } else {
                        recentAuthorArticles.setVisibility(View.VISIBLE);

                        if (dataList.size() >= 3) {
                            try {
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(0).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            } catch (Exception e) {
                                relatedArticles1.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList.get(0));

                            try {
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(1).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            } catch (Exception e) {
                                relatedArticles2.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList.get(1));

                            try {
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(2).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(relatedArticles3.getArticleImageView());
                            } catch (Exception e) {
                                relatedArticles3.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            relatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            relatedArticles3.setTag(dataList.get(2));
                        } else if (dataList.size() == 2) {
                            try {
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(0).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            } catch (Exception e) {
                                relatedArticles1.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList.get(0));

                            try {
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(1).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            } catch (Exception e) {
                                relatedArticles2.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList.get(1));
                            relatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {

                            try {
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(0).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            } catch (Exception e) {
                                relatedArticles1.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList.get(0));
                            relatedArticles2.setVisibility(View.GONE);
                            relatedArticles3.setVisibility(View.GONE);
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
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(0).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            } catch (Exception e) {
                                trendingRelatedArticles1.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList.get(0));

                            try {
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(1).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
                            } catch (Exception e) {
                                trendingRelatedArticles2.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            trendingRelatedArticles2.setTag(dataList.get(1));

                            try {
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(2).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles3.getArticleImageView());
                            } catch (Exception e) {
                                trendingRelatedArticles3.getArticleImageView().setImageResource(R.drawable.default_article);
                            }

                            trendingRelatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            trendingRelatedArticles3.setTag(dataList.get(2));
                        } else if (dataList.size() == 2) {
                            try {
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(0).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            } catch (Exception e) {
                                trendingRelatedArticles1.getArticleImageView().setImageResource(R.drawable.default_article);
                            }
                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList.get(0));
                            try {
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(1).getThumbnail()).
                                        placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
                            } catch (Exception e) {
                                trendingRelatedArticles2.getArticleImageView().setImageResource(R.drawable.default_article);
                            }
                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            trendingRelatedArticles2.setTag(dataList.get(1));

                            trendingRelatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            try {
                                Picasso.with(MomsVlogDetailActivity.this).load(dataList.get(0).getThumbnail()).
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
                    LayoutInflater mInflater = LayoutInflater.from(MomsVlogDetailActivity.this);

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
                                ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(MomsVlogDetailActivity.this, R.drawable.tick));
                                topicView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("TOPICS ----- ", "UNFOLLOW");
                                        followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 0);
                                    }
                                });
                            } else {
                                ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(MomsVlogDetailActivity.this, R.drawable.follow_plus));
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
                                    Intent intent = new Intent(MomsVlogDetailActivity.this, FilteredTopicsArticleListingActivity.class);
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
            ((ImageView) tagView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(MomsVlogDetailActivity.this, R.drawable.follow_plus));
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
            ((ImageView) tagView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(MomsVlogDetailActivity.this, R.drawable.tick));
            tagView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 0);
                }
            });
        }
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.followCategories(SharedPrefUtils.getUserDetailModel(this).getDynamoId(), followUnfollowCategoriesRequest);
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
        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(MomsVlogDetailActivity.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }


    private void closeFullscreenDialog() {
        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(mExoPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(MomsVlogDetailActivity.this, R.drawable.ic_fullscreen_expand));
    }


    private void initFullscreenButton() {
        PlaybackControlView controlView = mExoPlayerView.findViewById(R.id.exo_controller);
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
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
        mExoPlayerView.setPlayer(player);

        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            mExoPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
        }

        mExoPlayerView.getPlayer().prepare(mVideoSource);
        mExoPlayerView.getPlayer().setPlayWhenReady(true);
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
            String userAgent = Util.getUserAgent(MomsVlogDetailActivity.this, getApplicationContext().getApplicationInfo().packageName);
            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(MomsVlogDetailActivity.this, null, httpDataSourceFactory);
            Uri daUri = Uri.parse(streamUrl);
            mVideoSource = new HlsMediaSource(daUri, dataSourceFactory, 1, null, null);
        }

        initExoPlayer();

        if (mExoPlayerFullscreen) {
            ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
            mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(MomsVlogDetailActivity.this, R.drawable.ic_fullscreen_skrink));
            mFullScreenDialog.show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mExoPlayerView != null && mExoPlayerView.getPlayer() != null) {

            mResumeWindow = mExoPlayerView.getPlayer().getCurrentWindowIndex();
            mResumePosition = Math.max(0, mExoPlayerView.getPlayer().getContentPosition());
            mExoPlayerView.getPlayer().release();
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.backNavigationImageView:
                    finish();
                    break;
                case R.id.txvCommentTitle:
                case R.id.commentorImageView: {
                    CommentsData commentData = (CommentsData) ((View) view.getParent().getParent()).getTag();
                    if (!"fb".equals(commentData.getComment_type())) {
                        if (userDynamoId.equals(commentData.getUserId())) {
                            Intent profileIntent = new Intent(this, DashboardActivity.class);
                            profileIntent.putExtra("TabType", "profile");
                            startActivity(profileIntent);
                        } else {
                            Intent profileIntent = new Intent(this, PublicProfileActivity.class);
                            profileIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, commentData.getUserId());
                            profileIntent.putExtra(AppConstants.AUTHOR_NAME, commentData.getName());
                            profileIntent.putExtra(Constants.FROM_SCREEN, "Article Detail Comments");
                            startActivity(profileIntent);
                        }

                    }
                }
                break;
                case R.id.txvReplyTitle:
                case R.id.replierImageView: {
                    CommentsData commentData = (CommentsData) ((View) view.getParent()).getTag();
                    if (!"fb".equals(commentData.getComment_type())) {
                        if (userDynamoId.equals(commentData.getUserId())) {
                            Intent profileIntent = new Intent(this, DashboardActivity.class);
                            profileIntent.putExtra("TabType", "profile");
                            startActivity(profileIntent);
                        } else {
                            Intent profileIntent = new Intent(this, PublicProfileActivity.class);
                            profileIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, commentData.getUserId());
                            profileIntent.putExtra(AppConstants.AUTHOR_NAME, commentData.getName());
                            profileIntent.putExtra(Constants.FROM_SCREEN, "Article Detail Comments");
                            startActivity(profileIntent);
                        }
                    }
                }
                break;
                case R.id.follow_click:
                    followAPICall(authorId);
                    break;

                case R.id.user_image:
                case R.id.user_name:
                    if (AppConstants.USER_TYPE_USER.equals(authorType)) {
                        return;
                    }
                    if (userDynamoId.equals(authorId)) {
                        Intent profileIntent = new Intent(this, DashboardActivity.class);
                        profileIntent.putExtra("TabType", "profile");
                        startActivity(profileIntent);
                    } else {
                        Intent intentnn = new Intent(this, PublicProfileActivity.class);
                        intentnn.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, authorId);
                        intentnn.putExtra(AppConstants.AUTHOR_NAME, "" + author);
                        intentnn.putExtra(Constants.FROM_SCREEN, "Video Details");
                        startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                    }
                    break;
                case R.id.relatedArticles1: {
                    launchRelatedTrendingArticle(view, "videoDetailsPopular", 1);
                    break;
                }
                case R.id.relatedArticles2: {
                    launchRelatedTrendingArticle(view, "videoDetailsPopular", 2);
                    break;
                }
                case R.id.relatedArticles3: {
                    launchRelatedTrendingArticle(view, "videoDetailsPopular", 3);
                    break;
                }
                case R.id.trendingRelatedArticles1: {
                    launchRelatedTrendingArticle(view, "videoDetailsRelated", 1);
                    break;
                }
                case R.id.trendingRelatedArticles2: {
                    launchRelatedTrendingArticle(view, "videoDetailsRelated", 2);
                    break;
                }
                case R.id.trendingRelatedArticles3: {
                    launchRelatedTrendingArticle(view, "videoDetailsRelated", 3);
                    break;
                }
                case R.layout.related_tags_view: {
                    String categoryId = (String) view.getTag();
                    Intent intent = new Intent(MomsVlogDetailActivity.this, FilteredTopicsArticleListingActivity.class);
                    intent.putExtra("selectedTopics", categoryId);
                    intent.putExtra("displayName", ((TextView) ((LinearLayout) view).getChildAt(0)).getText());
                    startActivity(intent);
                    break;
                }
                case R.id.txvCommentCellEdit: {
                    CommentsData cData = (CommentsData) ((View) view.getParent().getParent().getParent()).getTag();
                }
                break;
                case R.id.txvReplyCellEdit: {
                    CommentsData cData = (CommentsData) ((View) view.getParent().getParent()).getTag();
                }
                break;
                case R.id.likeTextView: {

                }
                case R.id.viewCommentsTextView:
                    openViewCommentDialog();
                    break;
                case R.id.facebookShareTextView: {
                    Utils.pushShareArticleEvent(this, "DetailVideoScreen", userDynamoId + "", videoId, authorId + "~" + author, "Facebook");
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(shareUrl))
                                .build();
                        new ShareDialog(this).show(content);
                    }
                    break;
                }
                case R.id.whatsappShareTextView: {
                    if (StringUtils.isNullOrEmpty(shareUrl)) {
                        Toast.makeText(MomsVlogDetailActivity.this, getString(R.string.moderation_or_share_whatsapp_fail), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_out_momvlog) + shareUrl);
                        try {
                            startActivity(whatsappIntent);
                            Utils.pushShareArticleEvent(this, "DetailVideoScreen", userDynamoId + "", videoId, authorId + "~" + author, "Whatsapp");
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MomsVlogDetailActivity.this, getString(R.string.moderation_or_share_whatsapp_not_installed), Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                case R.id.emailShareTextView: {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/html");
                    final PackageManager pm = getPackageManager();
                    final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
                    ResolveInfo best = null;
                    for (final ResolveInfo info : matches) {
                        if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")) {
                            best = info;
                            break;
                        }
                    }
                    if (best != null) {
                        intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                    }
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Momspresso");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, AppUtils.fromHtml(getString(R.string.check_out_momvlog) + shareUrl));

                    try {
                        startActivity(intent);
                        Utils.pushShareArticleEvent(this, "DetailVideoScreen", userDynamoId + "", videoId, authorId + "~" + author, "Email");
                    } catch (Exception e) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("plain/text");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{});
                        i.putExtra(Intent.EXTRA_SUBJECT, "");
                        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_out_momvlog) + shareUrl);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                            Utils.pushShareArticleEvent(this, "DetailVideoScreen", userDynamoId + "", videoId, authorId + "~" + author, "Email");
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MomsVlogDetailActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void openViewCommentDialog() {
        try {

            ViewAllCommentsDialogFragment commentFrag = new ViewAllCommentsDialogFragment();
            Bundle _args = new Bundle();
            _args.putString("mycityCommentURL", commentMainUrl);
            _args.putString("fbCommentURL", shareUrl);
            _args.putString(Constants.ARTICLE_ID, videoId);
            _args.putString(Constants.AUTHOR, authorId + "~" + author);
            commentFrag.setArguments(_args);
            FragmentManager fm = getSupportFragmentManager();
            commentFrag.show(fm, "ViewAllComments");
           mExoPlayerView.getPlayer().setPlayWhenReady(false);             //bug fixed
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void launchRelatedTrendingArticle(View v, String listingType, int index) {
        MixPanelUtils.pushMomVlogClickEvent(mixpanel, index, listingType);
        Intent intent = new Intent(this, MomsVlogDetailActivity.class);
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
        View tagsView = mScrollView.findViewById(R.id.recentAuthorArticles);
        Rect scrollBounds = new Rect();
        mScrollView.getHitRect(scrollBounds);
        int permanentDiff = (tagsView.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));
        if (permanentDiff <= 0) {
            isArticleDetailEndReached = true;
        } else {
            isArticleDetailEndReached = false;
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                hideMainToolbar();
            }
            if (bottomToolbarLL.getVisibility() == View.VISIBLE) {
                hideToolbar();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                showMainToolbar();
            }
            if (bottomToolbarLL.getVisibility() != View.VISIBLE) {
                showToolbar();
            }
        }
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


}

