/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
//import com.google.android.exoplayer2.C;
//import com.google.android.exoplayer2.DefaultRenderersFactory;
//import com.google.android.exoplayer2.ExoPlaybackException;
//import com.google.android.exoplayer2.ExoPlayerFactory;
//import com.google.android.exoplayer2.PlaybackPreparer;
//import com.google.android.exoplayer2.Player;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
//import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
//import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
//import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
//import com.google.android.exoplayer2.drm.UnsupportedDrmException;
//import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException;
//import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
//import com.google.android.exoplayer2.offline.StreamKey;
//import com.google.android.exoplayer2.source.BehindLiveWindowException;
//import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
//import com.google.android.exoplayer2.source.ExtractorMediaSource;
//import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.source.TrackGroupArray;
//import com.google.android.exoplayer2.source.ads.AdsLoader;
//import com.google.android.exoplayer2.source.ads.AdsMediaSource;
//import com.google.android.exoplayer2.source.hls.HlsMediaSource;
//import com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistParserFactory;
//import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
//import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
//import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
//import com.google.android.exoplayer2.trackselection.TrackSelection;
//import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
//import com.google.android.exoplayer2.ui.PlayerControlView;
//import com.google.android.exoplayer2.ui.PlayerView;
//import com.google.android.exoplayer2.upstream.DataSource;
//import com.google.android.exoplayer2.upstream.HttpDataSource;
//import com.google.android.exoplayer2.util.ErrorMessageProvider;
//import com.google.android.exoplayer2.util.EventLogger;
//import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ViewCountResponse;
import com.mycity4kids.models.response.VlogsDetailResponse;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.observablescrollview.ObservableScrollView;
import com.mycity4kids.observablescrollview.ObservableScrollViewCallbacks;
import com.mycity4kids.observablescrollview.ScrollState;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.CustomFontTextView;
import com.mycity4kids.widget.RelatedArticlesView;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * An activity that plays media using {@link SimpleExoPlayer}.
 */
public class PlayerActivity /*extends Activity
        implements OnClickListener, PlaybackPreparer, PlayerControlView.VisibilityListener, ObservableScrollViewCallbacks */{
//    private static final int RECOVERY_REQUEST = 1;
//
//    private final static int REPLY_LEVEL_PARENT = 1;
//    private final static int REPLY_LEVEL_CHILD = 2;
//
//    private YouTubePlayerView youTubePlayerView;
//    private YouTubePlayer youTubePlayer;
//
//    private RelativeLayout mLodingView;
//    private Toast toast;
//    private LinearLayout commentLayout;
//    private ProgressDialog mProgressDialog;
//    private ObservableScrollView mScrollView;
//    //    private BubbleTextVew recommendSuggestion;
//    private TextView recentAuthorArticleHeading;
//    private RelatedArticlesView relatedArticles1, relatedArticles2, relatedArticles3;
//    private RelatedArticlesView trendingRelatedArticles1, trendingRelatedArticles2, trendingRelatedArticles3;
//    private LinearLayout trendingArticles;
//    private LinearLayout recentAuthorArticles;
//    private FlowLayout tagsLayout;
//    private TextView articleViewCountTextView;
//    private Toolbar mToolbar;
//    //    private FloatingActionButton commentFloatingActionButton;
//    private ImageView authorImageView;
//    private TextView followClick;
//    private LinearLayout commLayout;
//    private Rect scrollBounds;
//    private TextView authorTypeTextView, authorNameTextView;
//    private TextView article_title;
//    private TextView articleCreatedDateTextView;
//    //    private LinearLayout newCommentLayout;
//    private Menu menu;
//    //    private ImageView commentBtn;
//    private View commentEditView;
//    private LinearLayout addCommentLinearLayout;
//
//    private VlogsListingAndDetailResult detailData;
//    private String videoId;
//    private String youTubeId = null;
//    private boolean isLoading = false;
//    private String commentType = "db";
//    private String commentURL = "";
//    private String commentMainUrl;
//    private String pagination = "";
//    boolean isArticleDetailEndReached = false;
//    private boolean hasRecommendSuggestionAppeared = true;
//    private String authorId;
//    private String blogSlug;
//    private String titleSlug;
//    private String authorType, author;
//    private String shareUrl = "";
//    private String deepLinkURL;
//    private int recommendStatus;
//    private String recommendationFlag = "0";
//    private String screenTitle = "Video Blogs";
//    private String TAG;
//    private String bookmarkFlag = "0";
//    private int bookmarkStatus;
//    private String bookmarkId;
//    private Boolean isFollowing = false;
//
//    private Animation showRecommendAnim, hideRecommendAnim;
//
//    private VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI;
//    private GoogleApiClient mClient;
//
//    private CustomFontTextView facebookShareTextView, whatsappShareTextView, emailShareTextView, likeArticleTextView, bookmarkArticleTextView;
//    private String userDynamoId;
//    private ImageView backNavigationImageView;
//    private LinearLayout bottomToolbarLL;
//    private TextView viewCommentsTextView;
//
//    public static final String DRM_SCHEME_EXTRA = "drm_scheme";
//    public static final String DRM_LICENSE_URL_EXTRA = "drm_license_url";
//    public static final String DRM_KEY_REQUEST_PROPERTIES_EXTRA = "drm_key_request_properties";
//    public static final String DRM_MULTI_SESSION_EXTRA = "drm_multi_session";
//    public static final String PREFER_EXTENSION_DECODERS_EXTRA = "prefer_extension_decoders";
//
//    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
//    public static final String EXTENSION_EXTRA = "extension";
//
//    public static final String ACTION_VIEW_LIST =
//            "com.google.android.exoplayer.demo.action.VIEW_LIST";
//    public static final String URI_LIST_EXTRA = "uri_list";
//    public static final String EXTENSION_LIST_EXTRA = "extension_list";
//
//    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";
//
//    public static final String ABR_ALGORITHM_EXTRA = "abr_algorithm";
//    public static final String ABR_ALGORITHM_DEFAULT = "default";
//    public static final String ABR_ALGORITHM_RANDOM = "random";
//
//    public static final String SPHERICAL_STEREO_MODE_EXTRA = "spherical_stereo_mode";
//    public static final String SPHERICAL_STEREO_MODE_MONO = "mono";
//    public static final String SPHERICAL_STEREO_MODE_TOP_BOTTOM = "top_bottom";
//    public static final String SPHERICAL_STEREO_MODE_LEFT_RIGHT = "left_right";
//
//    // For backwards compatibility only.
//    private static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";
//
//    // Saved instance state keys.
//    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
//    private static final String KEY_WINDOW = "window";
//    private static final String KEY_POSITION = "position";
//    private static final String KEY_AUTO_PLAY = "auto_play";
//
//    private static final CookieManager DEFAULT_COOKIE_MANAGER;
//
//    static {
//        DEFAULT_COOKIE_MANAGER = new CookieManager();
//        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
//    }
//
//    private PlayerView playerView;
////    private LinearLayout debugRootView;
////    private TextView debugTextView;
//
//    private DataSource.Factory dataSourceFactory;
//    private SimpleExoPlayer player;
//    private FrameworkMediaDrm mediaDrm;
//    private MediaSource mediaSource;
//    private DefaultTrackSelector trackSelector;
//    private DefaultTrackSelector.Parameters trackSelectorParameters;
//    //    private DebugTextViewHelper debugViewHelper;
//    private TrackGroupArray lastSeenTrackGroupArray;
//
//    private boolean startAutoPlay;
//    private int startWindow;
//    private long startPosition;
//
//    // Fields used only for ad playback. The ads loader is loaded via reflection.
//
//    private AdsLoader adsLoader;
//    private Uri loadedAdTagUri;
//    private ViewGroup adUiViewGroup;
//
//    // Activity lifecycle
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        dataSourceFactory = buildDataSourceFactory();
//        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
//            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
//        }
//
//        setContentView(R.layout.activity_main_exo);
//        View rootView = findViewById(R.id.root);
//        rootView.setOnClickListener(this);
////        debugRootView = findViewById(R.id.controls_root);
////        debugTextView = findViewById(R.id.debug_text_view);
//
//        playerView = findViewById(R.id.player_view);
//        playerView.setControllerVisibilityListener(this);
//        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
//        playerView.requestFocus();
//
//        userDynamoId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
//        deepLinkURL = getIntent().getStringExtra(Constants.DEEPLINK_URL);
//
//        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
//        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
//        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
//        recentAuthorArticleHeading = (TextView) findViewById(R.id.recentAuthorArticleHeading);
//        trendingArticles = (LinearLayout) findViewById(R.id.trendingArticles);
//        recentAuthorArticles = (LinearLayout) findViewById(R.id.recentAuthorArticles);
//        relatedArticles1 = (RelatedArticlesView) findViewById(R.id.relatedArticles1);
//        relatedArticles2 = (RelatedArticlesView) findViewById(R.id.relatedArticles2);
//        relatedArticles3 = (RelatedArticlesView) findViewById(R.id.relatedArticles3);
//        trendingRelatedArticles1 = (RelatedArticlesView) findViewById(R.id.trendingRelatedArticles1);
//        trendingRelatedArticles2 = (RelatedArticlesView) findViewById(R.id.trendingRelatedArticles2);
//        trendingRelatedArticles3 = (RelatedArticlesView) findViewById(R.id.trendingRelatedArticles3);
//        bottomToolbarLL = (LinearLayout) findViewById(R.id.bottomToolbarLL);
//        facebookShareTextView = (CustomFontTextView) findViewById(R.id.facebookShareTextView);
//        whatsappShareTextView = (CustomFontTextView) findViewById(R.id.whatsappShareTextView);
//        emailShareTextView = (CustomFontTextView) findViewById(R.id.emailShareTextView);
//        likeArticleTextView = (CustomFontTextView) findViewById(R.id.likeTextView);
//        bookmarkArticleTextView = (CustomFontTextView) findViewById(R.id.bookmarkTextView);
//        tagsLayout = (FlowLayout) findViewById(R.id.tagsLayout);
//        articleViewCountTextView = (TextView) findViewById(R.id.articleViewCountTextView);
//        authorImageView = (ImageView) findViewById(R.id.user_image);
//        article_title = (TextView) findViewById(R.id.article_title);
//        authorTypeTextView = (TextView) findViewById(R.id.blogger_type);
//        authorNameTextView = (TextView) findViewById(R.id.user_name);
//        articleCreatedDateTextView = (TextView) findViewById(R.id.article_date);
//        followClick = (TextView) findViewById(R.id.follow_click);
//        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
//        backNavigationImageView = (ImageView) findViewById(R.id.backNavigationImageView);
//        commLayout = ((LinearLayout) findViewById(R.id.commnetLout));
//        commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));
//        viewCommentsTextView = ((TextView) findViewById(R.id.viewCommentsTextView));
//
//        viewCommentsTextView.setOnClickListener(this);
//        backNavigationImageView.setOnClickListener(this);
//        authorNameTextView.setOnClickListener(this);
//        authorImageView.setOnClickListener(this);
//        relatedArticles1.setOnClickListener(this);
//        relatedArticles2.setOnClickListener(this);
//        relatedArticles3.setOnClickListener(this);
//        trendingRelatedArticles1.setOnClickListener(this);
//        trendingRelatedArticles2.setOnClickListener(this);
//        trendingRelatedArticles3.setOnClickListener(this);
//        facebookShareTextView.setOnClickListener(this);
//        whatsappShareTextView.setOnClickListener(this);
//        emailShareTextView.setOnClickListener(this);
//        likeArticleTextView.setOnClickListener(this);
//        bookmarkArticleTextView.setOnClickListener(this);
//        followClick.setOnClickListener(this);
//
//        likeArticleTextView.setEnabled(false);
//        bookmarkArticleTextView.setEnabled(false);
//        likeArticleTextView.setTextColor(ContextCompat.getColor(this, R.color.grey));
//        bookmarkArticleTextView.setTextColor(ContextCompat.getColor(this, R.color.grey));
//        setTextViewDrawableColor(likeArticleTextView, R.color.grey);
//        setTextViewDrawableColor(bookmarkArticleTextView, R.color.grey);
//        followClick.setEnabled(false);
//
//        mScrollView.setScrollViewCallbacks(this);
//
//        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));
//
//        if (savedInstanceState != null) {
//            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
//            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
//            startWindow = savedInstanceState.getInt(KEY_WINDOW);
//            startPosition = savedInstanceState.getLong(KEY_POSITION);
//        } else {
//            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
//            clearStartPosition();
//        }
//
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            videoId = bundle.getString(Constants.VIDEO_ID);//"videos-67496bfa-b77d-466f-9d18-94e0f98f17c6"
//            authorId = bundle.getString(Constants.AUTHOR_ID, "");
//            if (bundle.getBoolean("fromNotification")) {
//                Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "video_details");
//            } else {
//                String listingType = bundle.getString(Constants.ARTICLE_OPENED_FROM);
//                String index = bundle.getString(Constants.ARTICLE_INDEX);
//                String screen = bundle.getString(Constants.FROM_SCREEN);
//                Utils.pushViewArticleEvent(this, screen, userDynamoId + "", videoId, listingType, index + "", author);
//            }
//
//            if (!ConnectivityUtils.isNetworkEnabled(this)) {
//                showToast(getString(R.string.error_network));
//                return;
//            }
////            showProgressDialog(getString(R.string.fetching_data));
//            Retrofit retro = BaseApplication.getInstance().getRetrofit();
//            vlogsListingAndDetailsAPI = retro.create(VlogsListingAndDetailsAPI.class);
//            hitArticleDetailsS3API();
//            getViewCountAPI();
////            hitRecommendedStatusAPI();
//        }
//
//        scrollBounds = new Rect();
//        mScrollView.getHitRect(scrollBounds);
//    }
//
//    private void hitArticleDetailsS3API() {
//        Call<VlogsDetailResponse> call = vlogsListingAndDetailsAPI.getVlogDetail(videoId);
//        call.enqueue(vlogDetailResponseCallback);
//    }
//
//    private void getViewCountAPI() {
//        Call<ViewCountResponse> call = vlogsListingAndDetailsAPI.getViewCount(videoId);
//        call.enqueue(getViewCountResponseCallback);
//    }
//
//    private Callback<VlogsDetailResponse> vlogDetailResponseCallback = new Callback<VlogsDetailResponse>() {
//        @Override
//        public void onResponse(Call<VlogsDetailResponse> call, retrofit2.Response<VlogsDetailResponse> response) {
//            removeProgressDialog();
//            if (response == null || response.body() == null) {
//                NetworkErrorException nee = new NetworkErrorException("Vlog API failure");
//                Crashlytics.logException(nee);
//                return;
//            }
//            try {
//                VlogsDetailResponse responseData = response.body();
////                newCommentLayout.setVisibility(View.VISIBLE);
//                updateUIfromResponse(responseData.getData().getResult());
//                if (StringUtils.isNullOrEmpty(authorId)) {
//                    authorId = responseData.getData().getResult().getAuthor().getId();
//                    hitBookmarkFollowingStatusAPI();
//                }
//                hitRelatedArticleAPI();
//                commentURL = responseData.getData().getResult().getCommentUri();
//                commentMainUrl = responseData.getData().getResult().getCommentUri();
//                if (!StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http")) {
////                    getMoreComments();
//                } else {
//                    commentType = "fb";
//                    commentURL = "http";
////                    getMoreComments();
//                }
//            } catch (Exception e) {
//                removeProgressDialog();
//                Crashlytics.logException(e);
//                Log.d("MC4kException", Log.getStackTraceString(e));
//            }
//
//        }
//
//        @Override
//        public void onFailure(Call<VlogsDetailResponse> call, Throwable t) {
//            removeProgressDialog();
//            handleExceptions(t);
////            getArticleDetailsWebserviceAPI();
//        }
//    };
//
//    private void hitRelatedArticleAPI() {
//        Call<VlogsListingResponse> callAuthorRecentcall = vlogsListingAndDetailsAPI.getPublishedVlogs(authorId, 0, 3, 0);
//        callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
//
//        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogsList(0, 3, 1, 3);
//        callRecentVideoArticles.enqueue(recentArticleResponseCallback);
//    }
//
//    private void hitBookmarkFollowingStatusAPI() {
//        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
//        articleDetailRequest.setArticleId(videoId);
//        Retrofit retro = BaseApplication.getInstance().getRetrofit();
//        VlogsListingAndDetailsAPI bookmarFollowingStatusAPI = retro.create(VlogsListingAndDetailsAPI.class);
//
//        Call<ArticleDetailResponse> callBookmark = bookmarFollowingStatusAPI.checkFollowingBookmarkStatus(videoId, authorId);
//        callBookmark.enqueue(isBookmarkedFollowedResponseCallback);
//    }
//
//    private Callback<VlogsListingResponse> recentArticleResponseCallback = new Callback<VlogsListingResponse>() {
//        @Override
//        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
//
//            if (mLodingView.getVisibility() == View.VISIBLE) {
//                mLodingView.setVisibility(View.GONE);
//            }
//            if (response == null || response.body() == null) {
//                showToast(getString(R.string.server_went_wrong));
//                return;
//            }
//
//            try {
//                VlogsListingResponse responseData = response.body();
//                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
//                    if (dataList == null) {
//                        return;
//                    }
//                    for (int i = 0; i < dataList.size(); i++) {
//                        if (dataList.get(i).getId().equals(videoId)) {
//                            dataList.remove(i);
//                            break;
//                        }
//                    }
//                    if (dataList == null || dataList.size() == 0) {
//
//                    } else {
//                        trendingArticles.setVisibility(View.VISIBLE);
//                        if (dataList.size() >= 3) {
//
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
//                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
//                            trendingRelatedArticles1.setTag(dataList.get(0));
//
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(1).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
//                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
//                            trendingRelatedArticles2.setTag(dataList.get(1));
//
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(2).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles3.getArticleImageView());
//                            trendingRelatedArticles3.setArticleTitle(dataList.get(2).getTitle());
//                            trendingRelatedArticles3.setTag(dataList.get(2));
//                        } else if (dataList.size() == 2) {
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
//                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
//                            trendingRelatedArticles1.setTag(dataList.get(0));
//
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(1).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
//                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
//                            trendingRelatedArticles2.setTag(dataList.get(1));
//
//                            trendingRelatedArticles3.setVisibility(View.GONE);
//                        } else if (dataList.size() == 1) {
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
//                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
//                            trendingRelatedArticles1.setTag(dataList.get(0));
//                            trendingRelatedArticles2.setVisibility(View.GONE);
//                            trendingRelatedArticles3.setVisibility(View.GONE);
//                        }
//                    }
//                } else {
//                    showToast(getString(R.string.server_went_wrong));
//                }
//            } catch (Exception e) {
//                Crashlytics.logException(e);
//                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
//            handleExceptions(t);
//        }
//    };
//
//    private Callback<VlogsListingResponse> bloggersArticleResponseCallback = new Callback<VlogsListingResponse>() {
//        @Override
//        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
//
//            if (mLodingView.getVisibility() == View.VISIBLE) {
//                mLodingView.setVisibility(View.GONE);
//            }
//            if (response == null || response.body() == null) {
//                showToast(getString(R.string.server_went_wrong));
//                return;
//            }
//
//            try {
//                VlogsListingResponse responseData = response.body();
//                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
//                    if (dataList == null) {
//                        return;
//                    }
//                    for (int i = 0; i < dataList.size(); i++) {
//                        if (dataList.get(i).getId().equals(videoId)) {
//                            dataList.remove(i);
//                            break;
//                        }
//                    }
//                    if (dataList.size() == 0) {
//
//                    } else {
//                        recentAuthorArticleHeading.setText(getString(R.string.vd_recent_videos_from_title) + " " + author);
//                        recentAuthorArticles.setVisibility(View.VISIBLE);
//
//                        if (dataList.size() >= 3) {
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
//                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
//                            relatedArticles1.setTag(dataList.get(0));
//
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(1).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
//                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
//                            relatedArticles2.setTag(dataList.get(1));
//
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(2).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(relatedArticles3.getArticleImageView());
//                            relatedArticles3.setArticleTitle(dataList.get(2).getTitle());
//                            relatedArticles3.setTag(dataList.get(2));
//                        } else if (dataList.size() == 2) {
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
//                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
//                            relatedArticles1.setTag(dataList.get(0));
//
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(1).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
//                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
//                            relatedArticles2.setTag(dataList.get(1));
//                            relatedArticles3.setVisibility(View.GONE);
//                        } else if (dataList.size() == 1) {
//                            Picasso.with(PlayerActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
//                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
//                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
//                            relatedArticles1.setTag(dataList.get(0));
//                            relatedArticles2.setVisibility(View.GONE);
//                            relatedArticles3.setVisibility(View.GONE);
//                        }
//                    }
//                } else {
//                    showToast(getString(R.string.server_went_wrong));
//                }
//            } catch (Exception e) {
//                Crashlytics.logException(e);
//                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
//            handleExceptions(t);
//        }
//    };
//
//    private Callback<ArticleDetailResponse> isBookmarkedFollowedResponseCallback = new Callback<ArticleDetailResponse>() {
//        @Override
//        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
//            if (response == null || null == response.body()) {
//                showToast(getString(R.string.server_went_wrong));
//                return;
//            }
//
//            ArticleDetailResponse responseData = response.body();
//            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                if (SharedPrefUtils.getUserDetailModel(PlayerActivity.this).getDynamoId().equals(authorId)) {
//                    followClick.setVisibility(View.INVISIBLE);
//                } else {
//                    if ("0".equals(responseData.getData().getResult().getIsFollowed())) {
//                        followClick.setEnabled(true);
//                        followClick.setText(getString(R.string.ad_follow_author));
//                        isFollowing = false;
//                    } else {
//                        followClick.setEnabled(true);
//                        followClick.setText(getString(R.string.ad_following_author));
//                        isFollowing = true;
//                    }
//                }
//            } else {
//                showToast(getString(R.string.server_went_wrong));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
//            handleExceptions(t);
//        }
//    };
//
//    private Callback<ViewCountResponse> getViewCountResponseCallback = new Callback<ViewCountResponse>() {
//        @Override
//        public void onResponse(Call<ViewCountResponse> call, Response<ViewCountResponse> response) {
//            if (response == null || response.body() == null) {
//                return;
//            }
//            try {
//                ViewCountResponse responseData = response.body();
//                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    articleViewCountTextView.setText(responseData.getData().get(0).getResult() + " Views");
//                } else {
//                    articleViewCountTextView.setText(responseData.getReason());
//                }
//            } catch (Exception e) {
//                Crashlytics.logException(e);
//                Log.d("MC4kException", Log.getStackTraceString(e));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ViewCountResponse> call, Throwable t) {
//            Crashlytics.logException(t);
//            Log.d("MC4kException", Log.getStackTraceString(t));
//        }
//    };
//
//    private void updateUIfromResponse(VlogsListingAndDetailResult responseData) {
//        detailData = responseData;
//        authorType = responseData.getAuthor().getUserType();
//        author = responseData.getAuthor().getFirstName() + " " + responseData.getAuthor().getLastName();
//        article_title.setText(responseData.getTitle());
//        blogSlug = responseData.getAuthor().getBlogTitleSlug();
//        titleSlug = responseData.getTitleSlug();
//        youTubeId = AppUtils.extractYoutubeId(detailData.getUrl());
//        if (youTubePlayer != null)
//            youTubePlayer.cueVideo(youTubeId); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
//
//        try {
//            if (!StringUtils.isNullOrEmpty(authorType)) {
//
//                if (AppConstants.USER_TYPE_BLOGGER.equals(authorType)) {
//                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
//                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_blogger));
//                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
//                        String bSlug = detailData.getAuthor().getBlogTitleSlug();
//                        if (StringUtils.isNullOrEmpty(bSlug)) {
//                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
//                        } else {
//                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + bSlug + "/video/" + detailData.getTitleSlug();
//                        }
//                    } else {
//                        shareUrl = deepLinkURL;
//                    }
//                } else if (AppConstants.USER_TYPE_EXPERT.equals(authorType)) {
//                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EXPERT.toUpperCase());
//                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_expert));
//                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
//                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
//                    } else {
//                        shareUrl = deepLinkURL;
//                    }
//                } else if (AppConstants.USER_TYPE_EDITOR.equals(authorType)) {
//                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITOR.toUpperCase());
//                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_editor));
//                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
//                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
//                    } else {
//                        shareUrl = deepLinkURL;
//                    }
//                } else if (AppConstants.USER_TYPE_EDITORIAL.equals(authorType)) {
//                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITORIAL.toUpperCase());
//                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_editorial));
//                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
//                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
//                    } else {
//                        shareUrl = deepLinkURL;
//                    }
//                } else if (AppConstants.USER_TYPE_FEATURED.equals(authorType)) {
//                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_FEATURED.toUpperCase());
//                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_featured));
//                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
//                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
//                    } else {
//                        shareUrl = deepLinkURL;
//                    }
//                } else if (AppConstants.USER_TYPE_USER.equals(authorType)) {
//                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_USER.toUpperCase());
//                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_blogger));
//                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
//                        String bSlug = detailData.getAuthor().getBlogTitleSlug();
//                        if (StringUtils.isNullOrEmpty(bSlug)) {
//                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
//                        } else {
//                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + bSlug + "/video/" + detailData.getTitleSlug();
//                        }
//                    } else {
//                        shareUrl = deepLinkURL;
//                    }
//                }
//            } else {
//                // Default Author type set to Blogger
//                authorTypeTextView.setText("Blogger".toUpperCase());
//                authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_blogger));
//                if (StringUtils.isNullOrEmpty(deepLinkURL)) {
//                    shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + detailData.getAuthor().getBlogTitleSlug() + "/video/" + titleSlug;
//                } else {
//                    shareUrl = deepLinkURL;
//                }
//            }
//        } catch (Exception e) {
//            Crashlytics.logException(e);
//            Log.d("MC4kException", Log.getStackTraceString(e));
//        }
//        hitUpdateViewCountAPI(detailData.getAuthor().getId());
////        createSelectedTagsView();
//        authorNameTextView.setText(author);
//        articleCreatedDateTextView.setText(DateTimeUtils.getDateFromTimestamp(Long.parseLong(responseData.getPublished_time())));
//
//        if (!StringUtils.isNullOrEmpty(responseData.getAuthor().getProfilePic().getClientApp())) {
//            Picasso.with(this).load(responseData.getAuthor().getProfilePic().getClientApp()).into(authorImageView);
//        }
////        Picasso.with(this).load(responseData.getAuthor().getProfilePic().getClientApp()).transform(new CircleTransformation()).into(target);
//
//    }
//
//    private void hitUpdateViewCountAPI(String userId) {
//        Call<ResponseBody> callUpdateViewCount = vlogsListingAndDetailsAPI.updateViewCount(videoId);
//        callUpdateViewCount.enqueue(updateViewCountResponseCallback);
//    }
//
//    private Callback<ResponseBody> updateViewCountResponseCallback = new Callback<ResponseBody>() {
//        @Override
//        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//            Log.d("View Count", "Updated Successfully");
//            if (response == null || null == response.body()) {
//                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
//                Crashlytics.logException(nee);
//                return;
//            }
//            try {
//                String resData = new String(response.body().bytes());
//                Log.d("View Count", "Updated Successfully" + resData);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ResponseBody> call, Throwable t) {
//            Crashlytics.logException(t);
//            Log.d("MC4kException", Log.getStackTraceString(t));
//        }
//    };
//
//    @Override
//    public void onNewIntent(Intent intent) {
//        releasePlayer();
//        releaseAdsLoader();
//        clearStartPosition();
//        setIntent(intent);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (Util.SDK_INT > 23) {
//            initializePlayer();
//            if (playerView != null) {
//                playerView.onResume();
//            }
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (Util.SDK_INT <= 23 || player == null) {
//            initializePlayer();
//            if (playerView != null) {
//                playerView.onResume();
//            }
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (Util.SDK_INT <= 23) {
//            if (playerView != null) {
//                playerView.onPause();
//            }
//            releasePlayer();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (Util.SDK_INT > 23) {
//            if (playerView != null) {
//                playerView.onPause();
//            }
//            releasePlayer();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        releaseAdsLoader();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (grantResults.length == 0) {
//            // Empty results are triggered if a permission is requested while another request was already
//            // pending and can be safely ignored in this case.
//            return;
//        }
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            initializePlayer();
//        } else {
//            showToast(R.string.storage_permission_denied);
//            finish();
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        updateTrackSelectorParameters();
//        updateStartPosition();
//        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
//        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
//        outState.putInt(KEY_WINDOW, startWindow);
//        outState.putLong(KEY_POSITION, startPosition);
//    }
//
//    // Activity input
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        // See whether the player view wants to handle media or DPAD keys events.
//        return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
//    }
//
//    // OnClickListener methods
//
//    @Override
//    public void onClick(View view) {
////        if (view.getParent() == debugRootView) {
////            MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
////            if (mappedTrackInfo != null) {
////                CharSequence title = ((Button) view).getText();
////                int rendererIndex = (int) view.getTag();
////                int rendererType = mappedTrackInfo.getRendererType(rendererIndex);
////                boolean allowAdaptiveSelections =
////                        rendererType == C.TRACK_TYPE_VIDEO
////                                || (rendererType == C.TRACK_TYPE_AUDIO
////                                && mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
////                                == MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS);
////                Pair<AlertDialog, TrackSelectionView> dialogPair =
////                        TrackSelectionView.getDialog(this, title, trackSelector, rendererIndex);
////                dialogPair.second.setShowDisableOption(true);
////                dialogPair.second.setAllowAdaptiveSelections(allowAdaptiveSelections);
////                dialogPair.first.show();
////            }
////        }
//    }
//
//    // PlaybackControlView.PlaybackPreparer implementation
//
//    @Override
//    public void preparePlayback() {
//        initializePlayer();
//    }
//
//    // PlaybackControlView.VisibilityListener implementation
//
//    @Override
//    public void onVisibilityChange(int visibility) {
////        debugRootView.setVisibility(visibility);
//    }
//
//    // Internal methods
//
//    private void initializePlayer() {
//        if (player == null) {
//            Intent intent = getIntent();
//            String action = intent.getAction();
//            Uri[] uris;
//            String[] extensions;
//            Uri mp4VideoUri = Uri.parse("https://www.momspresso.com/new-videos/v1/test1/playlist.m3u8");
////            if (ACTION_VIEW.equals(action)) {
//            uris = new Uri[]{mp4VideoUri};
//            extensions = new String[]{null};
////            } else if (ACTION_VIEW_LIST.equals(action)) {
////                String[] uriStrings = intent.getStringArrayExtra(URI_LIST_EXTRA);
////                uris = new Uri[uriStrings.length];
////                for (int i = 0; i < uriStrings.length; i++) {
////                    uris[i] = Uri.parse(uriStrings[i]);
////                }
////                extensions = intent.getStringArrayExtra(EXTENSION_LIST_EXTRA);
////                if (extensions == null) {
////                    extensions = new String[uriStrings.length];
////                }
////            } else {
////                showToast(getString(R.string.unexpected_intent_action, action));
////                finish();
////                return;
////            }
//            if (!Util.checkCleartextTrafficPermitted(uris)) {
//                showToast(R.string.error_cleartext_not_permitted);
//                return;
//            }
//            if (Util.maybeRequestReadExternalStoragePermission(/* activity= */ this, uris)) {
//                // The player will be reinitialized if the permission is granted.
//                return;
//            }
//
//            DefaultDrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
//            if (intent.hasExtra(DRM_SCHEME_EXTRA) || intent.hasExtra(DRM_SCHEME_UUID_EXTRA)) {
//                String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL_EXTRA);
//                String[] keyRequestPropertiesArray =
//                        intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES_EXTRA);
//                boolean multiSession = intent.getBooleanExtra(DRM_MULTI_SESSION_EXTRA, false);
//                int errorStringId = R.string.error_drm_unknown;
//                if (Util.SDK_INT < 18) {
//                    errorStringId = R.string.error_drm_not_supported;
//                } else {
//                    try {
//                        String drmSchemeExtra = intent.hasExtra(DRM_SCHEME_EXTRA) ? DRM_SCHEME_EXTRA
//                                : DRM_SCHEME_UUID_EXTRA;
//                        UUID drmSchemeUuid = Util.getDrmUuid(intent.getStringExtra(drmSchemeExtra));
//                        if (drmSchemeUuid == null) {
//                            errorStringId = R.string.error_drm_unsupported_scheme;
//                        } else {
//                            drmSessionManager =
//                                    buildDrmSessionManagerV18(
//                                            drmSchemeUuid, drmLicenseUrl, keyRequestPropertiesArray, multiSession);
//                        }
//                    } catch (UnsupportedDrmException e) {
//                        errorStringId = e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
//                                ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown;
//                    }
//                }
//                if (drmSessionManager == null) {
//                    showToast(errorStringId);
//                    finish();
//                    return;
//                }
//            }
//
//            TrackSelection.Factory trackSelectionFactory;
//            String abrAlgorithm = intent.getStringExtra(ABR_ALGORITHM_EXTRA);
//            if (abrAlgorithm == null || ABR_ALGORITHM_DEFAULT.equals(abrAlgorithm)) {
//                trackSelectionFactory = new AdaptiveTrackSelection.Factory();
//            } else if (ABR_ALGORITHM_RANDOM.equals(abrAlgorithm)) {
//                trackSelectionFactory = new RandomTrackSelection.Factory();
//            } else {
//                showToast(R.string.error_unrecognized_abr_algorithm);
//                finish();
//                return;
//            }
//
//            boolean preferExtensionDecoders =
//                    intent.getBooleanExtra(PREFER_EXTENSION_DECODERS_EXTRA, false);
//            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
//                    ((BaseApplication) getApplication()).useExtensionRenderers()
//                            ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
//                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
//                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
//            DefaultRenderersFactory renderersFactory =
//                    new DefaultRenderersFactory(this, extensionRendererMode);
//
//            trackSelector = new DefaultTrackSelector(trackSelectionFactory);
//            trackSelector.setParameters(trackSelectorParameters);
//            lastSeenTrackGroupArray = null;
//
//            player =
//                    ExoPlayerFactory.newSimpleInstance(
//              /* context= */ this, renderersFactory, trackSelector, drmSessionManager);
//            player.addListener(new PlayerEventListener());
//            player.setPlayWhenReady(startAutoPlay);
//            player.addAnalyticsListener(new EventLogger(trackSelector));
//            playerView.setPlayer(player);
//            playerView.setPlaybackPreparer(this);
////            debugViewHelper = new DebugTextViewHelper(player, debugTextView);
////            debugViewHelper.start();
//
//            MediaSource[] mediaSources = new MediaSource[uris.length];
//            for (int i = 0; i < uris.length; i++) {
//                mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
//            }
//            mediaSource =
//                    mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);
//            String adTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA);
//            if (adTagUriString != null) {
//                Uri adTagUri = Uri.parse(adTagUriString);
//                if (!adTagUri.equals(loadedAdTagUri)) {
//                    releaseAdsLoader();
//                    loadedAdTagUri = adTagUri;
//                }
//                MediaSource adsMediaSource = createAdsMediaSource(mediaSource, Uri.parse(adTagUriString));
//                if (adsMediaSource != null) {
//                    mediaSource = adsMediaSource;
//                } else {
//                    showToast(R.string.ima_not_loaded);
//                }
//            } else {
//                releaseAdsLoader();
//            }
//        }
//        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
//        if (haveStartPosition) {
//            player.seekTo(startWindow, startPosition);
//        }
//        player.prepare(mediaSource, !haveStartPosition, false);
//        updateButtonVisibilities();
//    }
//
//    private MediaSource buildMediaSource(Uri uri) {
//        return buildMediaSource(uri, null);
//    }
//
//    @SuppressWarnings("unchecked")
//    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
//        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
//        switch (type) {
//            case C.TYPE_HLS:
//                return new HlsMediaSource.Factory(dataSourceFactory)
//                        .setPlaylistParserFactory(
//                                new DefaultHlsPlaylistParserFactory(getOfflineStreamKeys(uri)))
//                        .createMediaSource(uri);
//            case C.TYPE_OTHER:
//                return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
//            default: {
//                throw new IllegalStateException("Unsupported type: " + type);
//            }
//        }
//    }
//
//    private List<StreamKey> getOfflineStreamKeys(Uri uri) {
//        return ((BaseApplication) getApplication()).getDownloadTracker().getOfflineStreamKeys(uri);
//    }
//
//    private DefaultDrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(
//            UUID uuid, String licenseUrl, String[] keyRequestPropertiesArray, boolean multiSession)
//            throws UnsupportedDrmException {
//        HttpDataSource.Factory licenseDataSourceFactory =
//                ((BaseApplication) getApplication()).buildHttpDataSourceFactory();
//        HttpMediaDrmCallback drmCallback =
//                new HttpMediaDrmCallback(licenseUrl, licenseDataSourceFactory);
//        if (keyRequestPropertiesArray != null) {
//            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
//                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
//                        keyRequestPropertiesArray[i + 1]);
//            }
//        }
//        releaseMediaDrm();
//        mediaDrm = FrameworkMediaDrm.newInstance(uuid);
//        return new DefaultDrmSessionManager<>(uuid, mediaDrm, drmCallback, null, multiSession);
//    }
//
//    private void releasePlayer() {
//        if (player != null) {
//            updateTrackSelectorParameters();
//            updateStartPosition();
////            debugViewHelper.stop();
////            debugViewHelper = null;
//            player.release();
//            player = null;
//            mediaSource = null;
//            trackSelector = null;
//        }
//        releaseMediaDrm();
//    }
//
//    private void releaseMediaDrm() {
//        if (mediaDrm != null) {
//            mediaDrm.release();
//            mediaDrm = null;
//        }
//    }
//
//    private void releaseAdsLoader() {
//        if (adsLoader != null) {
//            adsLoader.release();
//            adsLoader = null;
//            loadedAdTagUri = null;
//            playerView.getOverlayFrameLayout().removeAllViews();
//        }
//    }
//
//    private void updateTrackSelectorParameters() {
//        if (trackSelector != null) {
//            trackSelectorParameters = trackSelector.getParameters();
//        }
//    }
//
//    private void updateStartPosition() {
//        if (player != null) {
//            startAutoPlay = player.getPlayWhenReady();
//            startWindow = player.getCurrentWindowIndex();
//            startPosition = Math.max(0, player.getContentPosition());
//        }
//    }
//
//    private void clearStartPosition() {
//        startAutoPlay = true;
//        startWindow = C.INDEX_UNSET;
//        startPosition = C.TIME_UNSET;
//    }
//
//    /**
//     * Returns a new DataSource factory.
//     */
//    private DataSource.Factory buildDataSourceFactory() {
//        return ((BaseApplication) getApplication()).buildDataSourceFactory();
//    }
//
//    /**
//     * Returns an ads media source, reusing the ads loader if one exists.
//     */
//    private @Nullable
//    MediaSource createAdsMediaSource(MediaSource mediaSource, Uri adTagUri) {
//        // Load the extension source using reflection so the demo app doesn't have to depend on it.
//        // The ads loader is reused for multiple playbacks, so that ad playback can resume.
//        try {
//            Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
//            if (adsLoader == null) {
//                // Full class names used so the LINT.IfChange rule triggers should any of the classes move.
//                // LINT.IfChange
//                Constructor<? extends AdsLoader> loaderConstructor =
//                        loaderClass
//                                .asSubclass(AdsLoader.class)
//                                .getConstructor(android.content.Context.class, android.net.Uri.class);
//                // LINT.ThenChange(../../../../../../../../proguard-rules.txt)
//                adsLoader = loaderConstructor.newInstance(this, adTagUri);
//                adUiViewGroup = new FrameLayout(this);
//                // The demo app has a non-null overlay frame layout.
//                playerView.getOverlayFrameLayout().addView(adUiViewGroup);
//            }
//            AdsMediaSource.MediaSourceFactory adMediaSourceFactory =
//                    new AdsMediaSource.MediaSourceFactory() {
//                        @Override
//                        public MediaSource createMediaSource(Uri uri) {
//                            return PlayerActivity.this.buildMediaSource(uri);
//                        }
//
//                        @Override
//                        public int[] getSupportedTypes() {
//                            return new int[]{C.TYPE_DASH, C.TYPE_SS, C.TYPE_HLS, C.TYPE_OTHER};
//                        }
//                    };
//            return new AdsMediaSource(mediaSource, adMediaSourceFactory, adsLoader, adUiViewGroup);
//        } catch (ClassNotFoundException e) {
//            // IMA extension not loaded.
//            return null;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    // User controls
//
//    private void updateButtonVisibilities() {
////        debugRootView.removeAllViews();
//        if (player == null) {
//            return;
//        }
//
//        MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
//        if (mappedTrackInfo == null) {
//            return;
//        }
//
//        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
//            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
//            if (trackGroups.length != 0) {
//                Button button = new Button(this);
//                int label;
//                switch (player.getRendererType(i)) {
//                    case C.TRACK_TYPE_AUDIO:
//                        label = R.string.exo_track_selection_title_audio;
//                        break;
//                    case C.TRACK_TYPE_VIDEO:
//                        label = R.string.exo_track_selection_title_video;
//                        break;
//                    case C.TRACK_TYPE_TEXT:
//                        label = R.string.exo_track_selection_title_text;
//                        break;
//                    default:
//                        continue;
//                }
//                button.setText(label);
//                button.setTag(i);
//                button.setOnClickListener(this);
////                debugRootView.addView(button);
//            }
//        }
//    }
//
//    private void showControls() {
////        debugRootView.setVisibility(View.VISIBLE);
//    }
//
//    private void showToast(int messageId) {
//        showToast(getString(messageId));
//    }
//
//    private void showToast(String message) {
//        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//    }
//
//    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
//        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
//            return false;
//        }
//        Throwable cause = e.getSourceException();
//        while (cause != null) {
//            if (cause instanceof BehindLiveWindowException) {
//                return true;
//            }
//            cause = cause.getCause();
//        }
//        return false;
//    }
//
//    @Override
//    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//
//    }
//
//    @Override
//    public void onDownMotionEvent() {
//
//    }
//
//    @Override
//    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
//
//    }
//
//    private class PlayerEventListener implements Player.EventListener {
//
//        @Override
//        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            if (playbackState == Player.STATE_ENDED) {
//                showControls();
//            }
//            updateButtonVisibilities();
//        }
//
//        @Override
//        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
//            if (player.getPlaybackError() != null) {
//                // The user has performed a seek whilst in the error state. Update the resume position so
//                // that if the user then retries, playback resumes from the position to which they seeked.
//                updateStartPosition();
//            }
//        }
//
//        @Override
//        public void onPlayerError(ExoPlaybackException e) {
//            if (isBehindLiveWindow(e)) {
//                clearStartPosition();
//                initializePlayer();
//            } else {
//                updateStartPosition();
//                updateButtonVisibilities();
//                showControls();
//            }
//        }
//
//        @Override
//        @SuppressWarnings("ReferenceEquality")
//        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//            updateButtonVisibilities();
//            if (trackGroups != lastSeenTrackGroupArray) {
//                MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
//                if (mappedTrackInfo != null) {
//                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
//                            == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
//                        showToast(R.string.error_unsupported_video);
//                    }
//                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
//                            == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
//                        showToast(R.string.error_unsupported_audio);
//                    }
//                }
//                lastSeenTrackGroupArray = trackGroups;
//            }
//        }
//    }
//
//    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {
//
//        @Override
//        public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
//            String errorString = getString(R.string.error_generic);
//            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
//                Exception cause = e.getRendererException();
//                if (cause instanceof DecoderInitializationException) {
//                    // Special case for decoder initialization failures.
//                    DecoderInitializationException decoderInitializationException =
//                            (DecoderInitializationException) cause;
//                    if (decoderInitializationException.decoderName == null) {
//                        if (decoderInitializationException.getCause() instanceof DecoderQueryException) {
//                            errorString = getString(R.string.error_querying_decoders);
//                        } else if (decoderInitializationException.secureDecoderRequired) {
//                            errorString =
//                                    getString(
//                                            R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
//                        } else {
//                            errorString =
//                                    getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
//                        }
//                    } else {
//                        errorString =
//                                getString(
//                                        R.string.error_instantiating_decoder,
//                                        decoderInitializationException.decoderName);
//                    }
//                }
//            }
//            return Pair.create(0, errorString);
//        }
//    }
//
//    public void showProgressDialog(String bodyText) {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setOnKeyListener(new Dialog.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    return keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_SEARCH;
//                }
//            });
//        }
//
//        mProgressDialog.setMessage(bodyText);
//
//        if (!mProgressDialog.isShowing()) {
//            try {
//                mProgressDialog.show();
//            } catch (Exception e) {
//                Crashlytics.logException(e);
//                Log.d("MC4kException", Log.getStackTraceString(e));
//            }
//
//        }
//    }
//
//    public void removeProgressDialog() {
//        try {
//            if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setTextViewDrawableColor(TextView textView, int color) {
//        for (Drawable drawable : textView.getCompoundDrawables()) {
//            if (drawable != null) {
//                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_IN));
//            }
//        }
//    }
//
//    public void handleExceptions(Throwable t) {
//        if (t instanceof UnknownHostException) {
//            showToast(getString(R.string.error_network));
//        } else if (t instanceof SocketTimeoutException) {
//            showToast(getString(R.string.connection_timeout));
//        } else {
//            showToast(getString(R.string.server_went_wrong));
//        }
//        Crashlytics.logException(t);
//        Log.d("MC4kException", Log.getStackTraceString(t));
//    }
}
