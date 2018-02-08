package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
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
import com.mycity4kids.models.response.ArticleRecommendationStatusResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.ViewCountResponse;
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
import com.mycity4kids.ui.fragment.AddEditCommentReplyDialogFragment;
import com.mycity4kids.ui.fragment.AddEditCommentReplyFragment;
import com.mycity4kids.ui.fragment.ViewAllCommentsDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.CustomFontTextView;
import com.mycity4kids.widget.RelatedArticlesView;
import com.mycity4kids.youtube.DeveloperKey;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 3/1/17.
 */
public class VlogsDetailActivity extends BaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener, ObservableScrollViewCallbacks, AddEditCommentReplyFragment.IAddCommentReply {

    private static final int RECOVERY_REQUEST = 1;

    private final static int REPLY_LEVEL_PARENT = 1;
    private final static int REPLY_LEVEL_CHILD = 2;

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;

    private RelativeLayout mLodingView;
    private Toast toast;
    private LinearLayout commentLayout;
    private ProgressDialog mProgressDialog;
    private ObservableScrollView mScrollView;
    //    private BubbleTextVew recommendSuggestion;
    private TextView recentAuthorArticleHeading;
    private RelatedArticlesView relatedArticles1, relatedArticles2, relatedArticles3;
    private RelatedArticlesView trendingRelatedArticles1, trendingRelatedArticles2, trendingRelatedArticles3;
    private LinearLayout trendingArticles;
    private LinearLayout recentAuthorArticles;
    private FlowLayout tagsLayout;
    private TextView articleViewCountTextView;
    private Toolbar mToolbar;
    //    private FloatingActionButton commentFloatingActionButton;
    private ImageView authorImageView;
    private TextView followClick;
    private LinearLayout commLayout;
    private Rect scrollBounds;
    private TextView authorTypeTextView, authorNameTextView;
    private TextView article_title;
    private TextView articleCreatedDateTextView;
    //    private LinearLayout newCommentLayout;
    private Menu menu;
    //    private ImageView commentBtn;
    private View commentEditView;
    private LinearLayout addCommentLinearLayout;

    private VlogsListingAndDetailResult detailData;
    private String videoId;
    private String youTubeId = null;
    private boolean isLoading = false;
    private String commentType = "db";
    private String commentURL = "";
    private String commentMainUrl;
    private String pagination = "";
    boolean isArticleDetailEndReached = false;
    private boolean hasRecommendSuggestionAppeared = true;
    private String authorId;
    private String blogSlug;
    private String titleSlug;
    private String authorType, author;
    private String shareUrl = "";
    private String deepLinkURL;
    private int recommendStatus;
    private String recommendationFlag = "0";
    private String screenTitle = "Video Blogs";
    private String TAG;
    private String bookmarkFlag = "0";
    private int bookmarkStatus;
    private String bookmarkId;
    private Boolean isFollowing = false;

    private Animation showRecommendAnim, hideRecommendAnim;

    private VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI;
    private GoogleApiClient mClient;

    private CustomFontTextView facebookShareTextView, whatsappShareTextView, emailShareTextView, likeArticleTextView, bookmarkArticleTextView;
    private String userDynamoId;
    private ImageView backNavigationImageView;
    private LinearLayout bottomToolbarLL;
    private TextView viewCommentsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vlogs_detail_activity);
        userDynamoId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        Utils.pushOpenScreenEvent(VlogsDetailActivity.this, "DetailVideoScreen", userDynamoId + "");

        deepLinkURL = getIntent().getStringExtra(Constants.DEEPLINK_URL);
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.APP_INDEX_API).build();
        TAG = VlogsDetailActivity.this.getClass().getSimpleName();

        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
//        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        YouTubePlayerFragment youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);

//        youTubePlayerView.initialize(DeveloperKey.DEVELOPER_KEY, this);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

        recentAuthorArticleHeading = (TextView) findViewById(R.id.recentAuthorArticleHeading);
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

//        recommendSuggestion = (BubbleTextVew) findViewById(R.id.recommendSuggestion);
        tagsLayout = (FlowLayout) findViewById(R.id.tagsLayout);
        articleViewCountTextView = (TextView) findViewById(R.id.articleViewCountTextView);

        authorImageView = (ImageView) findViewById(R.id.user_image);
        authorImageView.setOnClickListener(this);
//        commentFloatingActionButton = (FloatingActionButton) findViewById(R.id.commentFloatingActionButton);
//        commentFloatingActionButton.setOnClickListener(this);

        article_title = (TextView) findViewById(R.id.article_title);
        authorTypeTextView = (TextView) findViewById(R.id.blogger_type);
        authorNameTextView = (TextView) findViewById(R.id.user_name);
        articleCreatedDateTextView = (TextView) findViewById(R.id.article_date);
        followClick = (TextView) findViewById(R.id.follow_click);
        authorNameTextView.setOnClickListener(this);

//        initializeAnimations();
        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        backNavigationImageView = (ImageView) findViewById(R.id.backNavigationImageView);
        backNavigationImageView.setOnClickListener(this);

        commLayout = ((LinearLayout) findViewById(R.id.commnetLout));
        commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));
        viewCommentsTextView = ((TextView) findViewById(R.id.viewCommentsTextView));
        viewCommentsTextView.setOnClickListener(this);

        likeArticleTextView.setEnabled(false);
        bookmarkArticleTextView.setEnabled(false);
        likeArticleTextView.setTextColor(ContextCompat.getColor(this, R.color.grey));
        bookmarkArticleTextView.setTextColor(ContextCompat.getColor(this, R.color.grey));
        setTextViewDrawableColor(likeArticleTextView, R.color.grey);
        setTextViewDrawableColor(bookmarkArticleTextView, R.color.grey);

        mScrollView.setScrollViewCallbacks(this);
        followClick.setOnClickListener(this);
        followClick.setEnabled(false);

        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.app_logo);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
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
            getViewCountAPI();
//            hitRecommendedStatusAPI();
        }

        scrollBounds = new Rect();
        mScrollView.getHitRect(scrollBounds);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("https://api.momspresso.com/")) {
            // Connect client
            mClient.connect();
            final String TITLE = screenTitle;
            final Uri APP_URI = AppConstants.APP_BASE_URI.buildUpon().appendPath(deepLinkURL).build();
            final Uri WEB_URL = AppConstants.WEB_BASE_URL.buildUpon().appendPath(deepLinkURL).build();
            Action viewAction = Action.newAction(Action.TYPE_VIEW, TITLE, WEB_URL, APP_URI);
            PendingResult<Status> result = AppIndex.AppIndexApi.start(mClient, viewAction);

            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        Log.d(TAG, APP_URI.toString() + " App Indexing API: The screen view started" +
                                " successfully.");
                    } else {
                        Log.e(TAG, APP_URI.toString() + " App Indexing API: There was an error " +
                                "recording the screen ." + status.toString());
                    }
                }
            });
        }
    }

    @Override
    protected void onStop() {
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("https://api.momspresso.com/")) {
            final String TITLE = screenTitle;
            final Uri APP_URI = AppConstants.APP_BASE_URI.buildUpon().appendPath(deepLinkURL).build();
            final Uri WEB_URL = AppConstants.WEB_BASE_URL.buildUpon().appendPath(deepLinkURL).build();
            Action viewAction = Action.newAction(Action.TYPE_VIEW, TITLE, WEB_URL, APP_URI);
            PendingResult<Status> result = AppIndex.AppIndexApi.end(mClient, viewAction);

            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        Log.d(TAG, APP_URI.toString() + " App Indexing API:  The screen view end " +
                                "successfully.");
                    } else {
                        Log.e(TAG, APP_URI.toString() + " App Indexing API: There was an error " +
                                "recording the screen." + status.toString());
                    }
                }
            });
            // Disconnecting the client
            mClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void updateUi(com.kelltontech.network.Response response) {

    }

    private void initializeAnimations() {
        showRecommendAnim = AnimationUtils.loadAnimation(this, R.anim.recommend_anim_show);
        showRecommendAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                recommendSuggestion.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        recommendSuggestion.setVisibility(View.INVISIBLE);
//                        recommendSuggestion.startAnimation(hideRecommendAnim);
                    }
                }, 5000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hideRecommendAnim = AnimationUtils.loadAnimation(this, R.anim.recommend_anim_hide);
        hideRecommendAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                recommendSuggestion.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void hitArticleDetailsS3API() {
        Call<VlogsDetailResponse> call = vlogsListingAndDetailsAPI.getVlogDetail(videoId);
        call.enqueue(vlogDetailResponseCallback);
    }

    private void getViewCountAPI() {
        Call<ViewCountResponse> call = vlogsListingAndDetailsAPI.getViewCount(videoId);
        call.enqueue(getViewCountResponseCallback);
    }

    private void hitRecommendedStatusAPI() {
        Call<ArticleRecommendationStatusResponse> checkArticleRecommendStaus = vlogsListingAndDetailsAPI.getArticleRecommendedStatus(videoId);
        checkArticleRecommendStaus.enqueue(recommendStatusResponseCallback);
    }

//    private void getMoreComments() {
//        isLoading = true;
//        if (!ConnectivityUtils.isNetworkEnabled(this)) {
//            showToast(getString(R.string.error_network));
//            return;
//        }
//        mLodingView.setVisibility(View.VISIBLE);
//        Retrofit retro = BaseApplication.getInstance().getRetrofit();
//        if ("db".equals(commentType)) {
//            ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
//            Call<ResponseBody> call = articleDetailsAPI.getComments(commentURL);
//            call.enqueue(commentsCallback);
//        } else {
//            ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
//            Call<FBCommentResponse> call = articleDetailsAPI.getFBComments(videoId, pagination);
//            call.enqueue(fbCommentsCallback);
//        }
//    }

    private void hitBookmarkFollowingStatusAPI() {
        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
        articleDetailRequest.setArticleId(videoId);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI bookmarFollowingStatusAPI = retro.create(VlogsListingAndDetailsAPI.class);

        Call<ArticleDetailResponse> callBookmark = bookmarFollowingStatusAPI.checkFollowingBookmarkStatus(videoId, authorId);
        callBookmark.enqueue(isBookmarkedFollowedResponseCallback);
    }

    private void hitRelatedArticleAPI() {
//        String url = AppConstants.LIVE_URL + AppConstants.SERVICE_TYPE_ARTICLE + "trending" +
//                AppConstants.SEPARATOR_BACKSLASH + "1" + AppConstants.SEPARATOR_BACKSLASH + "3";
//        HttpVolleyRequest.getStringResponse(this, url, null, mGetArticleListingListener, Request.Method.GET, true);

//        Call<ArticleListingResponse> categoryRelatedArticlesCall = vlogsListingAndDetailsAPI.getCategoryRelatedArticles(videoId);
//        categoryRelatedArticlesCall.enqueue(categoryArticleResponseCallback);

        Call<VlogsListingResponse> callAuthorRecentcall = vlogsListingAndDetailsAPI.getPublishedVlogs(authorId, 0, 3, 0);
        callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);

        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogsList(0, 3, 1, 3);
        callRecentVideoArticles.enqueue(recentArticleResponseCallback);
    }

    private void getFollowedTopicsList() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.getFollowedCategories(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(getFollowedTopicsResponseCallback);
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

//    @Override
//    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//        View view = (View) mScrollView.getChildAt(mScrollView.getChildCount() - 1);
//        View tagsView = (View) mScrollView.findViewById(R.id.tagsLayout);
//        Rect scrollBounds = new Rect();
//        mScrollView.getHitRect(scrollBounds);
//        int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));
//        if (diff <= 10 && !isLoading && !StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http") && !AppConstants.PAGINATION_END_VALUE.equals(pagination)) {
//            getMoreComments();
//        }
//    }


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
//                newCommentLayout.setVisibility(View.VISIBLE);
                updateUIfromResponse(responseData.getData().getResult());
                if (StringUtils.isNullOrEmpty(authorId)) {
                    authorId = responseData.getData().getResult().getAuthor().getId();
                    hitBookmarkFollowingStatusAPI();
                }
                hitRelatedArticleAPI();
                commentURL = responseData.getData().getResult().getCommentUri();
                commentMainUrl = responseData.getData().getResult().getCommentUri();
                if (!StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http")) {
//                    getMoreComments();
                } else {
                    commentType = "fb";
                    commentURL = "http";
//                    getMoreComments();
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
//            getArticleDetailsWebserviceAPI();
        }
    };

    private void updateUIfromResponse(VlogsListingAndDetailResult responseData) {
        detailData = responseData;
        authorType = responseData.getAuthor().getUserType();
        author = responseData.getAuthor().getFirstName() + " " + responseData.getAuthor().getLastName();
        article_title.setText(responseData.getTitle());
        blogSlug = responseData.getAuthor().getBlogTitleSlug();
        titleSlug = responseData.getTitleSlug();
        youTubeId = AppUtils.extractYoutubeId(detailData.getUrl());
        if (youTubePlayer != null)
            youTubePlayer.cueVideo(youTubeId); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo

        try {
            if (!StringUtils.isNullOrEmpty(authorType)) {

                if (AppConstants.USER_TYPE_BLOGGER.equals(authorType)) {
                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
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
                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EXPERT.toUpperCase());
                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_expert));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITOR.equals(authorType)) {
                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITOR.toUpperCase());
                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_editor));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITORIAL.equals(authorType)) {
                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITORIAL.toUpperCase());
                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_editorial));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_FEATURED.equals(authorType)) {
                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_FEATURED.toUpperCase());
                    authorTypeTextView.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_featured));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_USER.equals(authorType)) {
                    authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_USER.toUpperCase());
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
                authorTypeTextView.setText("Blogger".toUpperCase());
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
//        createSelectedTagsView();
        authorNameTextView.setText(author);
        articleCreatedDateTextView.setText(DateTimeUtils.getDateFromTimestamp(Long.parseLong(responseData.getPublished_time())));

        if (!StringUtils.isNullOrEmpty(responseData.getAuthor().getProfilePic().getClientApp())) {
            Picasso.with(this).load(responseData.getAuthor().getProfilePic().getClientApp()).into(authorImageView);
        }
//        Picasso.with(this).load(responseData.getAuthor().getProfilePic().getClientApp()).transform(new CircleTransformation()).into(target);

    }

    private void createSelectedTagsView() {
//        selectedTopics
        getFollowedTopicsList();
    }

    private Callback<ViewCountResponse> getViewCountResponseCallback = new Callback<ViewCountResponse>() {
        @Override
        public void onResponse(Call<ViewCountResponse> call, Response<ViewCountResponse> response) {
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ViewCountResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    articleViewCountTextView.setText(responseData.getData().get(0).getResult() + " Views");
                } else {
                    articleViewCountTextView.setText(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ViewCountResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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
                Drawable top = ContextCompat.getDrawable(VlogsDetailActivity.this, R.drawable.ic_recommend);
                likeArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            } else {
                recommendStatus = 1;
                Drawable top = ContextCompat.getDrawable(VlogsDetailActivity.this, R.drawable.ic_recommended);
                likeArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            }
        }

        @Override
        public void onFailure(Call<ArticleRecommendationStatusResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

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
//                bookmarkFlag = responseData.getData().getResult().getBookmarkStatus();
//                if ("0".equals(bookmarkFlag)) {
//                    menu.getItem(0).setEnabled(true);
//                    menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp);
//                    bookmarkStatus = 0;
//                } else {
//                    menu.getItem(0).setEnabled(true);
//                    menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp_fill);
//                    bookmarkStatus = 1;
//                }
//                bookmarkId = responseData.getData().getResult().getBookmarkId();
                if (SharedPrefUtils.getUserDetailModel(VlogsDetailActivity.this).getDynamoId().equals(authorId)) {
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
                        recentAuthorArticleHeading.setText(getString(R.string.vd_recent_videos_from_title) + " " + author);
                        recentAuthorArticles.setVisibility(View.VISIBLE);

                        if (dataList.size() >= 3) {
                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList.get(0));

                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(1).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList.get(1));

                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(2).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles3.getArticleImageView());
                            relatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            relatedArticles3.setTag(dataList.get(2));
                        } else if (dataList.size() == 2) {
                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList.get(0));

                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(1).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList.get(1));
                            relatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
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

                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList.get(0));

                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(1).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            trendingRelatedArticles2.setTag(dataList.get(1));

                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(2).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles3.getArticleImageView());
                            trendingRelatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            trendingRelatedArticles3.setTag(dataList.get(2));
                        } else if (dataList.size() == 2) {
                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList.get(0));

                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(1).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            trendingRelatedArticles2.setTag(dataList.get(1));

                            trendingRelatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            Picasso.with(VlogsDetailActivity.this).load(AppUtils.getYoutubeThumbnailURL(dataList.get(0).getUrl())).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
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
                    LayoutInflater mInflater = LayoutInflater.from(VlogsDetailActivity.this);

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
//                                ((TextView) topicView.getChildAt(0)).setBackgroundColor(ContextCompat.getColor(ArticlesAndBlogsDetailsActivity.this, R.color.home_green));
                                ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(VlogsDetailActivity.this, R.drawable.tick));
                                topicView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("TOPICS ----- ", "UNFOLLOW");
                                        followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 0);
                                    }
                                });
                            } else {
                                ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(VlogsDetailActivity.this, R.drawable.follow_plus));
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
//                                    trackArticleReadTime.updateTimeAtBackendAndGA(shareUrl, articleId, estimatedReadTime);
//                                    trackArticleReadTime.resetTimer();
                                    String categoryId = (String) v.getTag();
                                    Intent intent = new Intent(VlogsDetailActivity.this, FilteredTopicsArticleListingActivity.class);
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
            Log.d("GTM FOLLOW", "displayName" + selectedTopic);
            Utils.pushFollowTopicEvent(this, "DetailVideoScreen", userDynamoId, selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
            tagView.getChildAt(0).setTag(selectedTopic);
            tagView.getChildAt(2).setTag(selectedTopic);
            ((ImageView) tagView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(VlogsDetailActivity.this, R.drawable.follow_plus));
            tagView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TOPICS ----- ", "FOLLOW");
                    followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 1);
                }
            });
        } else {
            Log.d("GTM UNFOLLOW", "displayName" + selectedTopic);
            Utils.pushUnfollowTopicEvent(this, "DetailVideoScreen", userDynamoId, selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
            tagView.getChildAt(0).setTag(selectedTopic);
            tagView.getChildAt(2).setTag(selectedTopic);
            ((ImageView) tagView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(VlogsDetailActivity.this, R.drawable.tick));
            tagView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TOPICS ----- ", "UNFOLLOW");
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
            Log.d("View Count", "Updated Successfully");
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                Log.d("View Count", "Updated Successfully" + resData);
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
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            this.youTubePlayer = youTubePlayer;
            youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION | YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
            if (null != youTubeId) {
                youTubePlayer.cueVideo(youTubeId);
            }// Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = "ERROR";//String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(DeveloperKey.DEVELOPER_KEY, this);
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

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubePlayerView;
    }

    private class ViewHolder {
        private ImageView commentorsImage;
        private TextView commentName;
        private TextView commentDescription;
        private TextView dateTxt;
        private TextView commentCellReplyTxt;
        private TextView replyCellReplyTxt;
        private TextView commentCellEditTxt;
        private TextView replyCellEditTxt;
        private LinearLayout replyCommentView;
        public ImageView replyIndicatorImageView;
    }

    /**
     * @param bodyText
     */
    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_SEARCH;
                }
            });
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToast(String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(this, "" + message, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.backNavigationImageView:
                    finish();
                    break;
                case R.id.txvCommentTitle:
                case R.id.commentorImageView: {
                    CommentsData commentData = (CommentsData) ((View) v.getParent().getParent()).getTag();
                    if (!"fb".equals(commentData.getComment_type())) {
//                        trackArticleReadTime.updateTimeAtBackendAndGA(shareUrl, articleId, estimatedReadTime);
//                        trackArticleReadTime.resetTimer();
                        if (userDynamoId.equals(commentData.getUserId())) {
                            Intent profileIntent = new Intent(this, DashboardActivity.class);
                            profileIntent.putExtra("TabType", "profile");
                            startActivity(profileIntent);
                        } else {
                            Intent profileIntent = new Intent(this, BloggerProfileActivity.class);
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
                    CommentsData commentData = (CommentsData) ((View) v.getParent()).getTag();
                    if (!"fb".equals(commentData.getComment_type())) {
//                        trackArticleReadTime.updateTimeAtBackendAndGA(shareUrl, articleId, estimatedReadTime);
//                        trackArticleReadTime.resetTimer();
                        if (userDynamoId.equals(commentData.getUserId())) {
                            Intent profileIntent = new Intent(this, DashboardActivity.class);
                            profileIntent.putExtra("TabType", "profile");
                            startActivity(profileIntent);
                        } else {
                            Intent profileIntent = new Intent(this, BloggerProfileActivity.class);
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
//                    if (null != trackArticleReadTime) {
//                        trackArticleReadTime.updateTimeAtBackendAndGA(shareUrl, articleId, estimatedReadTime);
//                        trackArticleReadTime.resetTimer();
//                    }
                    if (AppConstants.USER_TYPE_USER.equals(authorType)) {
                        return;
                    }
                    if (userDynamoId.equals(authorId)) {
                        Intent profileIntent = new Intent(this, DashboardActivity.class);
                        profileIntent.putExtra("TabType", "profile");
                        startActivity(profileIntent);
                    } else {
                        Intent intentnn = new Intent(this, BloggerProfileActivity.class);
                        intentnn.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, authorId);
                        intentnn.putExtra(AppConstants.AUTHOR_NAME, "" + author);
                        intentnn.putExtra(Constants.FROM_SCREEN, "Video Details");
                        startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                    }
                    break;
                case R.id.relatedArticles1: {
//                    Utils.pushEventRelatedArticle(VlogsDetailActivity.this, GTMEventType.RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Video Detail", ((VlogsListingAndDetailResult) v.getTag()).getTitleSlug(), 1);
                    launchRelatedTrendingArticle(v, "videoDetailsRelated", 1);
                    break;
                }
                case R.id.relatedArticles2: {
//                    Utils.pushEventRelatedArticle(VlogsDetailActivity.this, GTMEventType.RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Video Detail", ((VlogsListingAndDetailResult) v.getTag()).getTitleSlug(), 2);
                    launchRelatedTrendingArticle(v, "videoDetailsRelated", 2);
                    break;
                }
                case R.id.relatedArticles3: {
//                    Utils.pushEventRelatedArticle(VlogsDetailActivity.this, GTMEventType.RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Video Detail", ((VlogsListingAndDetailResult) v.getTag()).getTitleSlug(), 3);
                    launchRelatedTrendingArticle(v, "videoDetailsRelated", 3);
                    break;
                }
                case R.id.trendingRelatedArticles1: {
//                    Utils.pushEventRelatedArticle(VlogsDetailActivity.this, GTMEventType.TRENDING_RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Video Detail", ((VlogsListingAndDetailResult) v.getTag()).getTitleSlug(), 1);
                    launchRelatedTrendingArticle(v, "videoDetailsTrending", 1);
                    break;
                }
                case R.id.trendingRelatedArticles2: {
//                    Utils.pushEventRelatedArticle(VlogsDetailActivity.this, GTMEventType.TRENDING_RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Video Detail", ((VlogsListingAndDetailResult) v.getTag()).getTitleSlug(), 2);
                    launchRelatedTrendingArticle(v, "videoDetailsTrending", 2);
                    break;
                }
                case R.id.trendingRelatedArticles3: {
//                    Utils.pushEventRelatedArticle(VlogsDetailActivity.this, GTMEventType.TRENDING_RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Video Detail", ((VlogsListingAndDetailResult) v.getTag()).getTitleSlug(), 3);
                    launchRelatedTrendingArticle(v, "videoDetailsTrending", 3);
                    break;
                }
                case R.layout.related_tags_view: {
//                    trackArticleReadTime.updateTimeAtBackendAndGA(shareUrl, articleId, estimatedReadTime);
//                    trackArticleReadTime.resetTimer();
                    String categoryId = (String) v.getTag();
                    Intent intent = new Intent(VlogsDetailActivity.this, FilteredTopicsArticleListingActivity.class);
                    intent.putExtra("selectedTopics", categoryId);
                    intent.putExtra("displayName", ((TextView) ((LinearLayout) v).getChildAt(0)).getText());
                    startActivity(intent);
                    break;
                }
//                case R.id.commentFloatingActionButton:
//                    openCommentDialog(null, "ADD");
//                    break;
//                case R.id.txvCommentCellReply:
//                    openCommentDialog((CommentsData) ((View) v.getParent().getParent().getParent()).getTag(), "ADD");
//                    break;
//                case R.id.txvReplyCellReply:
//                    openCommentDialog((CommentsData) ((View) v.getParent().getParent()).getTag(), "ADD");
//                    break;
                case R.id.txvCommentCellEdit: {
                    CommentsData cData = (CommentsData) ((View) v.getParent().getParent().getParent()).getTag();
                    openCommentDialog(cData, "EDIT");
                }
                break;
                case R.id.txvReplyCellEdit: {
                    CommentsData cData = (CommentsData) ((View) v.getParent().getParent()).getTag();
                    openCommentDialog(cData, "EDIT");
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
                        Toast.makeText(VlogsDetailActivity.this, getString(R.string.moderation_or_share_whatsapp_fail), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_out_blog) + shareUrl);
                        try {
                            startActivity(whatsappIntent);
                            Utils.pushShareArticleEvent(this, "DetailVideoScreen", userDynamoId + "", videoId, authorId + "~" + author, "Whatsapp");
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(VlogsDetailActivity.this, getString(R.string.moderation_or_share_whatsapp_not_installed), Toast.LENGTH_SHORT).show();
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
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, AppUtils.fromHtml(getString(R.string.check_out_blog) + shareUrl));

                    try {
                        startActivity(intent);
                        Utils.pushShareArticleEvent(this, "DetailVideoScreen", userDynamoId + "", videoId, authorId + "~" + author, "Email");
                    } catch (Exception e) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("plain/text");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{});
                        i.putExtra(Intent.EXTRA_SUBJECT, "");
                        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_out_blog) + shareUrl);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                            Utils.pushShareArticleEvent(this, "DetailVideoScreen", userDynamoId + "", videoId, authorId + "~" + author, "Email");
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(VlogsDetailActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
//            commentFrag.setTargetFragment(this, 0);
            Bundle _args = new Bundle();
            _args.putString("mycityCommentURL", commentMainUrl);
            _args.putString("fbCommentURL", shareUrl);
            _args.putString(Constants.ARTICLE_ID, videoId);
            _args.putString(Constants.AUTHOR, authorId + "~" + author);
            commentFrag.setArguments(_args);
            hideToolbarPerm();
            FragmentManager fm = getSupportFragmentManager();
            commentFrag.show(fm, "ViewAllComments");
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void openCommentDialog(CommentsData comData, String opType) {
        try {
            AddEditCommentReplyDialogFragment commentFrag = new AddEditCommentReplyDialogFragment();
            Bundle _args = new Bundle();
            _args.putString(Constants.ARTICLE_ID, videoId);
            _args.putString(Constants.AUTHOR, authorId + "~" + author);
            _args.putString("opType", opType);
            if (comData != null) {
                _args.putParcelable("commentData", comData);
                _args.putString("type", "video");
            }
            commentFrag.setArguments(_args);
            FragmentManager fm = getSupportFragmentManager();
            commentFrag.show(fm, "Replies");
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    public void hideToolbarPerm() {
        mToolbar.setVisibility(View.GONE);
    }

    private void launchRelatedTrendingArticle(View v, String listingType, int index) {
//        trackArticleReadTime.updateTimeAtBackendAndGA(shareUrl, articleId, estimatedReadTime);
//        trackArticleReadTime.resetTimer();
        Intent intent = new Intent(this, VlogsDetailActivity.class);
        VlogsListingAndDetailResult parentingListData = (VlogsListingAndDetailResult) v.getTag();
        intent.putExtra(Constants.VIDEO_ID, parentingListData.getId());
        intent.putExtra(Constants.AUTHOR_ID, parentingListData.getAuthor().getId());
        intent.putExtra(Constants.FROM_SCREEN, "Video Details");
        intent.putExtra(Constants.ARTICLE_OPENED_FROM, listingType);
        intent.putExtra(Constants.ARTICLE_INDEX, "" + index);
        startActivity(intent);
    }

    /*
    * Called to update the article details screen with number of replies to a comment.
    * Reply of reply does not change anything on the article details screen hence no
    * hadling for reply_level == 2 condition. Leaving it here for future use only.
    * */
    public void onReplyOrNestedReplyAddition(CommentsData updatedComment, int reply_level) {
        int replyReplyIndex = 0;
        for (int i = 0; i < commentLayout.getChildCount(); i++) {
            CommentsData cdata = (CommentsData) commentLayout.getChildAt(i).getTag();
            if (updatedComment.getParent_id().equals(cdata.getId())) {
                Log.d("Comment ", "comment mil gaya");
                replyReplyIndex++;
                if (reply_level == 2) {
                    Log.d("cdata", " " + i + " " + cdata);
                    Log.d("updatedComment", "" + updatedComment);

                } else {
                    cdata.getReplies().add(updatedComment);
                }
            }
        }
    }

//    public void hideFloatingActionButton() {
//        commentFloatingActionButton.animate()
//                .alpha(0)
//                .setInterpolator(new LinearInterpolator())
//                .setDuration(180)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        commentFloatingActionButton.setVisibility(View.INVISIBLE);
//                    }
//                });
//    }
//
//    public void showFloatingActionButton() {
//        commentFloatingActionButton.animate()
//                .alpha(1)
//                .setInterpolator(new LinearInterpolator())
//                .setDuration(180)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        commentFloatingActionButton.setVisibility(View.VISIBLE);
//                    }
//                });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vlogs_menu, menu);
        this.menu = menu;
//        menu.getItem(0).setEnabled(false);
        if (!StringUtils.isNullOrEmpty(authorId)) {
            hitBookmarkFollowingStatusAPI();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                if (null != trackArticleReadTime) {
//                    trackArticleReadTime.updateTimeAtBackendAndGA(shareUrl, articleId, estimatedReadTime);
//                }
                finish();
                return true;
//            case R.id.bookmark:
//                addRemoveBookmark();
//                return true;
            case R.id.share:
                if (!ConnectivityUtils.isNetworkEnabled(this)) {
                    showToast(getString(R.string.error_network));
                    return true;
                }
                if (StringUtils.isNullOrEmpty(shareUrl) && StringUtils.isNullOrEmpty(blogSlug) && StringUtils.isNullOrEmpty(titleSlug)) {
                    showToast("Unable to share the article currently!");
                    return true;
                }
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String author = authorNameTextView.getText().toString();
                String shareMessage;
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    shareMessage = getString(R.string.check_out_blog) + " " + "\"" + detailData.getTitle() + "\" by " + author + ".";
                } else {
                    shareMessage = getString(R.string.check_out_blog) + " " + "\"" + detailData.getTitle() + "\" by " + author + ".\nRead Here: " + shareUrl;
                }
//                Utils.pushEventShareURL(VlogsDetailActivity.this, GTMEventType.SHARE_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Video Detail", shareUrl);
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCommentAddition(CommentsData cd) {
//        displayComments(new ViewHolder(), cd, false);
    }

    @Override
    public void onCommentReplyEditSuccess(CommentsData cd) {
//        for (int i = 0; i < commentLayout.getChildCount(); i++) {
//            CommentsData cdata = (CommentsData) commentLayout.getChildAt(i).getTag();
//            CommentsData searchedData = recursiveSearch(cdata, cd);
//            if (searchedData != null) {
//                ViewHolder viewHolder = new ViewHolder();
//                displayCommentsAtPosition(viewHolder, cdata, false, i);
//                break;
//            } else {
//                Log.d("Nothing in comment ", cdata.getBody());
//            }
//        }
    }

    @Override
    public void onReplyAddition(CommentsData updatedComment) {
//        for (int i = 0; i < commentLayout.getChildCount(); i++) {
//            CommentsData cdata = (CommentsData) commentLayout.getChildAt(i).getTag();
//            CommentsData searchedData = recursiveSearch(cdata, updatedComment);
//            if (searchedData != null) {
//                searchedData.getReplies().add(updatedComment);
//                ViewHolder viewHolder = new ViewHolder();
//                displayCommentsAtPosition(viewHolder, cdata, false, i);
//                break;
//            } else {
//                Log.d("Nothing in comment ", cdata.getBody());
//            }
//        }
    }

    private CommentsData recursiveSearch(CommentsData cd1, CommentsData upComment) {
        if (cd1.getId().equals(upComment.getId()) || cd1.getId().equals(upComment.getParent_id())) {
            return cd1;
        }
        ArrayList<CommentsData> children = cd1.getReplies();
        CommentsData res = null;
        for (int i = 0; res == null && i < children.size(); i++) {
            res = recursiveSearch(children.get(i), upComment);
        }
        return res;
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);
        View tagsView = mScrollView.findViewById(R.id.recentAuthorArticles);
        Rect scrollBounds = new Rect();
        mScrollView.getHitRect(scrollBounds);
        int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));
//        if (diff <= 10 && !isLoading && !StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http") && !AppConstants.PAGINATION_END_VALUE.equals(pagination)) {
//            getMoreComments();
//        }

        int permanentDiff = (tagsView.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));
        if (permanentDiff <= 0) {
            isArticleDetailEndReached = true;

//            if (commentFloatingActionButton.getVisibility() == View.INVISIBLE) {
//                showFloatingActionButton();
//            }
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
//            if (!isArticleDetailEndReached && commentFloatingActionButton.getVisibility() == View.VISIBLE) {
//                hideFloatingActionButton();
//            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                showMainToolbar();
            }
            if (bottomToolbarLL.getVisibility() != View.VISIBLE) {
                showToolbar();
            }
//            if (commentFloatingActionButton.getVisibility() == View.INVISIBLE) {
//                showFloatingActionButton();
//            }
        }
    }

    public void hideMainToolbar() {
        mToolbar.animate()
                .translationY(-mToolbar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getSupportActionBar().hide();
                    }
                });
        backNavigationImageView.animate()
                .alpha(1)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        backNavigationImageView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void hideToolbar() {
        bottomToolbarLL.animate()
                .translationY(bottomToolbarLL.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        getSupportActionBar().hide();
                        bottomToolbarLL.setVisibility(View.GONE);
                    }
                });
    }

    private void showToolbar() {
        bottomToolbarLL.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
//                        getSupportActionBar().show();
                        bottomToolbarLL.setVisibility(View.VISIBLE);
                    }

                });
    }

    public void showMainToolbar() {
        mToolbar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        getSupportActionBar().show();
                    }
                });
        backNavigationImageView.animate()
                .alpha(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        backNavigationImageView.setVisibility(View.GONE);
                    }
                });
    }
}
