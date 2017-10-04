package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnWebServiceCompleteListener;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.parentingdetails.ImageData;
import com.mycity4kids.models.parentingdetails.VideoData;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ArticleDetailResult;
import com.mycity4kids.models.response.ArticleDetailWebserviceResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.ArticleRecommendationStatusResponse;
import com.mycity4kids.models.response.FBCommentResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.ViewCountResponse;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;
import com.mycity4kids.newmodels.VolleyBaseResponse;
import com.mycity4kids.observablescrollview.ObservableScrollView;
import com.mycity4kids.observablescrollview.ObservableScrollViewCallbacks;
import com.mycity4kids.observablescrollview.ScrollState;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.BloggerProfileActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.FilteredTopicsArticleListingActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.TrackArticleReadTime;
import com.mycity4kids.volley.HttpVolleyRequest;
import com.mycity4kids.widget.CustomFontTextView;
import com.mycity4kids.widget.RelatedArticlesView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 6/6/17.
 */
public class ArticleDetailsFragment extends BaseFragment implements View.OnClickListener, ObservableScrollViewCallbacks, AddEditCommentReplyFragment.IAddCommentReply {

    private final static int ADD_BOOKMARK = 1;

    private final static int REPLY_LEVEL_PARENT = 1;
    private final static int REPLY_LEVEL_CHILD = 2;

    private ArticleDetailResult detailData;
    private Bitmap defaultBloggerBitmap;
    private ArticleDetailsAPI articleDetailsAPI;
    private ArrayList<ImageData> imageList;
    private ArrayList<VideoData> videoList;
    private int width;
    private int bookmarkStatus;
    private int recommendStatus;
    private int estimatedReadTime = 0;
    private float density;
    private boolean isFollowing = false;
    private boolean isLoading = false;
    private boolean isArticleDetailEndReached = false;
    private boolean isSwipeNextAvailable;
    private boolean isFbCommentHeadingAdded = false;
    private String bookmarkFlag = "0";
    private String recommendationFlag = "0";
    private String commentURL = "";
    private String shareUrl = "";
    private String bookmarkId;
    private String authorId;
    private String author;
    private String articleId;
    private String deepLinkURL;
    private String blogSlug;
    private String titleSlug;
    private String commentType = "db";
    private String pagination = "";
    private String isMomspresso;
    private String userDynamoId;
    private String articleLanguageCategoryId;
    private String from;
    private String gtmLanguage;

    private ObservableScrollView mScrollView;
    private LinearLayout commentLayout;
    private TextView followClick;
    private TextView recentAuthorArticleHeading;
    private LinearLayout trendingArticles, recentAuthorArticles;
    private WebView mWebView;
    private RelatedArticlesView relatedArticles1, relatedArticles2, relatedArticles3;
    private RelatedArticlesView trendingRelatedArticles1, trendingRelatedArticles2, trendingRelatedArticles3;
    private FloatingActionButton commentFloatingActionButton;

    private MyWebChromeClient mWebChromeClient = null;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private CustomFontTextView facebookShareTextView, whatsappShareTextView, emailShareTextView, likeArticleTextView, bookmarkArticleTextView;
    private TextView article_title;
    private TextView author_type;
    private TextView articleViewCountTextView;
    private TextView articleCommentCountTextView;
    private TextView articleRecommendationCountTextView;
    private TextView viewAllTagsTextView;
    private TextView swipeNextTextView;
    private ImageView cover_image;
    private ImageView floatingActionButton;
    private RelativeLayout mLodingView;
    private FlowLayout tagsLayout;
    private Rect scrollBounds;
    //    private TrackArticleReadTime trackArticleReadTime;
    private WebView videoWebView;
    private View fragmentView;
    private LinearLayout bottomToolbarLL;
    private TextView commentHeading;
    private View relatedTrendingSeparator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.article_details_fragment, container, false);
        userDynamoId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();

        deepLinkURL = "";// getIntent().getStringExtra(Constants.DEEPLINK_URL);
        try {

            fragmentView.findViewById(R.id.user_name).setOnClickListener(this);

            floatingActionButton = (ImageView) fragmentView.findViewById(R.id.user_image);
            floatingActionButton.setOnClickListener(this);
            commentFloatingActionButton = (FloatingActionButton) fragmentView.findViewById(R.id.commentFloatingActionButton);
            commentFloatingActionButton.setOnClickListener(this);

            mWebView = (WebView) fragmentView.findViewById(R.id.articleWebView);
            videoWebView = (WebView) fragmentView.findViewById(R.id.videoWebView);
            viewAllTagsTextView = (TextView) fragmentView.findViewById(R.id.viewAllTagsTextView);
            bottomToolbarLL = (LinearLayout) fragmentView.findViewById(R.id.bottomToolbarLL);

            facebookShareTextView = (CustomFontTextView) fragmentView.findViewById(R.id.facebookShareTextView);
            whatsappShareTextView = (CustomFontTextView) fragmentView.findViewById(R.id.whatsappShareTextView);
            emailShareTextView = (CustomFontTextView) fragmentView.findViewById(R.id.emailShareTextView);
            likeArticleTextView = (CustomFontTextView) fragmentView.findViewById(R.id.likeTextView);
            bookmarkArticleTextView = (CustomFontTextView) fragmentView.findViewById(R.id.bookmarkTextView);

            mWebChromeClient = new MyWebChromeClient();
            mWebView.setWebChromeClient(mWebChromeClient);
            videoWebView.setWebChromeClient(mWebChromeClient);

            author_type = (TextView) fragmentView.findViewById(R.id.blogger_type);

            followClick = (TextView) fragmentView.findViewById(R.id.follow_click);
            followClick.setOnClickListener(this);
            followClick.setEnabled(false);

            article_title = (TextView) fragmentView.findViewById(R.id.article_title);
            recentAuthorArticleHeading = (TextView) fragmentView.findViewById(R.id.recentAuthorArticleHeading);
            relatedArticles1 = (RelatedArticlesView) fragmentView.findViewById(R.id.relatedArticles1);
            relatedArticles2 = (RelatedArticlesView) fragmentView.findViewById(R.id.relatedArticles2);
            relatedArticles3 = (RelatedArticlesView) fragmentView.findViewById(R.id.relatedArticles3);

            trendingRelatedArticles1 = (RelatedArticlesView) fragmentView.findViewById(R.id.trendingRelatedArticles1);
            trendingRelatedArticles2 = (RelatedArticlesView) fragmentView.findViewById(R.id.trendingRelatedArticles2);
            trendingRelatedArticles3 = (RelatedArticlesView) fragmentView.findViewById(R.id.trendingRelatedArticles3);
            trendingArticles = (LinearLayout) fragmentView.findViewById(R.id.trendingArticles);
            recentAuthorArticles = (LinearLayout) fragmentView.findViewById(R.id.recentAuthorArticles);
            relatedTrendingSeparator = (View) fragmentView.findViewById(R.id.relatedTrendingSeparator);

            tagsLayout = (FlowLayout) fragmentView.findViewById(R.id.tagsLayout);
            articleViewCountTextView = (TextView) fragmentView.findViewById(R.id.articleViewCountTextView);
            articleCommentCountTextView = (TextView) fragmentView.findViewById(R.id.articleCommentCountTextView);
            articleRecommendationCountTextView = (TextView) fragmentView.findViewById(R.id.articleRecommendationCountTextView);
            cover_image = (ImageView) fragmentView.findViewById(R.id.cover_image);

            density = getResources().getDisplayMetrics().density;
            width = getResources().getDisplayMetrics().widthPixels;

            defaultBloggerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_blogger_profile_img);
            floatingActionButton.setImageDrawable(new BitmapDrawable(getResources(), defaultBloggerBitmap));
            swipeNextTextView = (TextView) fragmentView.findViewById(R.id.swipeNextTextView);

            commentLayout = ((LinearLayout) fragmentView.findViewById(R.id.commnetLout));
            commentHeading = ((TextView) fragmentView.findViewById(R.id.commentsHeading));
            relatedArticles1.setOnClickListener(this);
            relatedArticles2.setOnClickListener(this);
            relatedArticles3.setOnClickListener(this);
            trendingRelatedArticles1.setOnClickListener(this);
            trendingRelatedArticles2.setOnClickListener(this);
            trendingRelatedArticles3.setOnClickListener(this);
            viewAllTagsTextView.setOnClickListener(this);
            facebookShareTextView.setOnClickListener(this);
            whatsappShareTextView.setOnClickListener(this);
            emailShareTextView.setOnClickListener(this);
            likeArticleTextView.setOnClickListener(this);
            bookmarkArticleTextView.setOnClickListener(this);

            mLodingView = (RelativeLayout) fragmentView.findViewById(R.id.relativeLoadingView);
            fragmentView.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

            mScrollView = (ObservableScrollView) fragmentView.findViewById(R.id.scroll_view);
            mScrollView.setScrollViewCallbacks(this);

            Bundle bundle = getArguments();
            if (bundle != null) {
                articleId = bundle.getString(Constants.ARTICLE_ID);
                authorId = bundle.getString(Constants.AUTHOR_ID, "");
                blogSlug = bundle.getString(Constants.BLOG_SLUG);
                titleSlug = bundle.getString(Constants.TITLE_SLUG);
                isSwipeNextAvailable = bundle.getBoolean("swipeNext", false);
                if (isSwipeNextAvailable) {
                    isSwipeNextAvailable = BaseApplication.isFirstSwipe();
                }

                if (bundle.getBoolean("fromNotification")) {
//                    Utils.pushEventNotificationClick(getActivity(), GTMEventType.NOTIFICATION_CLICK_EVENT, userDynamoId, "Notification Popup", "article_details");
//                    Utils.pushOpenArticleEvent(getActivity(), GTMEventType.ARTICLE_DETAILS_CLICK_EVENT, "Notification", userDynamoId + "", articleId, "-1" + "~Notification", "Notification");
                } else {
                    from = bundle.getString(Constants.ARTICLE_OPENED_FROM);
                    String index = bundle.getString(Constants.ARTICLE_INDEX);
                    String screen = bundle.getString(Constants.FROM_SCREEN);
//                    Utils.pushOpenArticleEvent(getActivity(), GTMEventType.ARTICLE_DETAILS_CLICK_EVENT, screen, userDynamoId + "", articleId, index + "~" + from, from);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mScrollView.fullScroll(View.FOCUS_DOWN);
//                        }
//                    }, 3000);
                }
                if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.error_network));
                }
                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                articleDetailsAPI = retro.create(ArticleDetailsAPI.class);

                hitArticleDetailsRedisAPI();
                getViewCountAPI();
                hitRecommendedStatusAPI();
            }

            scrollBounds = new Rect();
            mScrollView.getHitRect(scrollBounds);

        } catch (Exception e) {
            removeProgressDialog();
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        return fragmentView;

    }

    @Override
    public void onResume() {
        //coming back from another activity, restart the readtime
//        if (null != trackArticleReadTime && trackArticleReadTime.getActivityTimerStatus() == 1) {
//            trackArticleReadTime.startTimer();
//        }
        super.onResume();
        mWebView.onResume();
        videoWebView.onResume();
    }

    @Override
    public void onPause() {
//        if (null != trackArticleReadTime)
//            trackArticleReadTime.pauseTimer();
        super.onPause();
        mWebView.onPause();
        videoWebView.onPause();
    }

    private void hitArticleDetailsRedisAPI() {
        Call<ArticleDetailResult> call = articleDetailsAPI.getArticleDetailsFromRedis(articleId, "articleId");
        call.enqueue(articleDetailResponseCallbackS3);
    }

    private void hitArticleDetailsS3API() {
        Call<ArticleDetailResult> call = articleDetailsAPI.getArticleDetailsFromS3(articleId);
        call.enqueue(articleDetailResponseCallbackS3);

//        For Local JSON Testing
//        Call<ArticleDetailResult> call = articleDetailsAPI.getArticleDetailsFromLocal();

//        For Direct fetch from webservice testing
//        getArticleDetailsWebserviceAPI();
    }

    private void getViewCountAPI() {
        Call<ViewCountResponse> call = articleDetailsAPI.getViewCount(articleId);
        call.enqueue(getViewCountResponseCallback);
    }

    private void hitBookmarkFollowingStatusAPI() {
        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
        articleDetailRequest.setArticleId(articleId);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI bookmarFollowingStatusAPI = retro.create(ArticleDetailsAPI.class);

        Call<ArticleDetailResponse> callBookmark = bookmarFollowingStatusAPI.checkFollowingBookmarkStatus(articleId, authorId);
        callBookmark.enqueue(isBookmarkedFollowedResponseCallback);
    }

    private void hitBookmarkVideoStatusAPI() {
        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
        articleDetailRequest.setArticleId(articleId);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI bookmarkVideoStatusAPI = retro.create(ArticleDetailsAPI.class);

        Call<ArticleDetailResponse> callBookmark = bookmarkVideoStatusAPI.checkBookmarkVideoStatus(articleDetailRequest);
        callBookmark.enqueue(isBookmarkedVideoResponseCallback);
    }

    private void hitRelatedArticleAPI() {
        String url = AppConstants.LIVE_URL + AppConstants.SERVICE_TYPE_ARTICLE + "trending" +
                AppConstants.SEPARATOR_BACKSLASH + "1" + AppConstants.SEPARATOR_BACKSLASH + "3" + "?lang=" + SharedPrefUtils.getLanguageFilters(getActivity());
        HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, true);

        Call<ArticleListingResponse> categoryRelatedArticlesCall = articleDetailsAPI.getCategoryRelatedArticles(articleId, 0, 3, SharedPrefUtils.getLanguageFilters(getActivity()));
        categoryRelatedArticlesCall.enqueue(categoryArticleResponseCallback);
    }

    private void hitUpdateViewCountAPI(String userId, ArrayList<Map<String, String>> tagsList, ArrayList<Map<String, String>> cityList) {
        UpdateViewCountRequest updateViewCountRequest = new UpdateViewCountRequest();
        updateViewCountRequest.setUserId(userId);
        updateViewCountRequest.setTags(tagsList);
        updateViewCountRequest.setCities(cityList);
        Call<ResponseBody> callUpdateViewCount = articleDetailsAPI.updateViewCount(articleId, updateViewCountRequest);
        callUpdateViewCount.enqueue(updateViewCountResponseCallback);
    }

    private void hitRecommendedStatusAPI() {
        Call<ArticleRecommendationStatusResponse> checkArticleRecommendStaus = articleDetailsAPI.getArticleRecommendedStatus(articleId);
        checkArticleRecommendStaus.enqueue(recommendStatusResponseCallback);
    }

    private void recommendUnrecommentArticleAPI(String status) {
        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest = new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(articleId);
        recommendUnrecommendArticleRequest.setStatus(status);
        Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle = articleDetailsAPI.recommendUnrecommendArticle(recommendUnrecommendArticleRequest);
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback);
    }

    private void getMoreComments() {
        isLoading = true;
        if (isAdded() && !ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.error_network));
            return;
        }
        mLodingView.setVisibility(View.VISIBLE);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        if ("db".equals(commentType)) {
            ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
            Call<ResponseBody> call = articleDetailsAPI.getComments(commentURL);
            call.enqueue(commentsCallback);
        } else {
            ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
            Call<FBCommentResponse> call = articleDetailsAPI.getFBComments(articleId, pagination);
            call.enqueue(fbCommentsCallback);
        }

    }

    private void getResponseUpdateUi(ArticleDetailResult detailsResponse) {
        detailData = detailsResponse;
        imageList = detailData.getBody().getImage();
        videoList = detailData.getBody().getVideo();
        author = detailData.getUserName();
        isMomspresso = detailData.getIsMomspresso();

        if (!StringUtils.isNullOrEmpty(detailData.getImageUrl().getThumbMax())) {
            Picasso.with(getActivity()).load(detailData.getImageUrl().getThumbMax()).placeholder(R.drawable.default_article).resize(width, (int) (220 * density)).centerCrop().into(cover_image);
        }

        if (!StringUtils.isNullOrEmpty(detailData.getTitle())) {
            article_title.setText(detailData.getTitle());
        }

        try {
            if (!StringUtils.isNullOrEmpty(detailData.getUserType())) {

                if (AppConstants.USER_TYPE_BLOGGER.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + detailData.getBlogTitleSlug().trim() + "/article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EXPERT.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_EXPERT.toUpperCase());
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITOR.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_EDITOR.toUpperCase());
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITORIAL.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_EDITORIAL.toUpperCase());
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_FEATURED.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_FEATURED.toUpperCase());
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                }
            } else {
                // Default Author type set to Blogger
                author_type.setText("Blogger".toUpperCase());
                if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                    shareUrl = AppConstants.ARTICLE_SHARE_URL + detailData.getBlogTitleSlug().trim() + "/article/" + detailData.getTitleSlug();
                } else {
                    shareUrl = deepLinkURL;
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

        if (!StringUtils.isNullOrEmpty(detailData.getUserName())) {
            ((TextView) fragmentView.findViewById(R.id.user_name)).setText(detailData.getUserName());
        }

        if (!StringUtils.isNullOrEmpty(detailData.getCreated())) {
            ((TextView) fragmentView.findViewById(R.id.article_date)).setText(DateTimeUtils.getDateFromTimestamp(Long.parseLong(detailData.getCreated())));
        }

        String bodyDescription = detailData.getBody().getText();
        String bodyDesc = bodyDescription;
        int imageIndex = 0;
        int imageReadTime = 0;
        if (imageList.size() > 0) {
            for (ImageData images : imageList) {
                if (imageIndex <= AppConstants.MIN_ARTICLE_BODY_IMAGE_READ_TIME) {
                    imageReadTime = imageReadTime + AppConstants.MAX_ARTICLE_BODY_IMAGE_READ_TIME - imageIndex;
                } else {
                    imageReadTime = imageReadTime + AppConstants.MIN_ARTICLE_BODY_IMAGE_READ_TIME;
                }
                imageIndex++;
                if (bodyDescription.contains(images.getKey())) {
                    bodyDesc = bodyDesc.replace(images.getKey(), "<p style='text-align:center'><img src=" + images.getValue() + " style=\"width: 100%;\"+></p>");
                }
            }
            if (null != videoList && !videoList.isEmpty()) {
                for (VideoData video : videoList) {
                    if ("1".equals(isMomspresso)) {
                        videoWebView.setVisibility(View.VISIBLE);
                        cover_image.setVisibility(View.INVISIBLE);
                        bodyDesc = bodyDesc.replace(video.getKey(), "");
                        String vUrl = "<html><head></head><body><p style='text-align:center'><iframe allowfullscreen src=http:" + video.getVideoUrl() +
                                "?modestbranding=1&amp;rel=0&amp;showinfo=0\" style=\"width: 100%; height: 100%;\" ></iframe></p></body></html>";
                        videoWebView.loadDataWithBaseURL("", vUrl, "text/html", "utf-8", "");
                    } else if (bodyDescription.contains(video.getKey())) {
                        String vURL = video.getVideoUrl().replace("http:", "").replace("https:", "");
                        bodyDesc = bodyDesc.replace(video.getKey(), "<p style='text-align:center'><iframe allowfullscreen src=http:" + vURL + "?modestbranding=1&amp;rel=0&amp;showinfo=0\" style=\"width: 100%;\"></iframe></p>");
                    }
                }
            }

            String bodyImgTxt = "<html><head>" +
                    "" +
                    "<style type=\"text/css\">\n" +
                    "@font-face {\n" +
                    "    font-family: MyFont;\n" +
                    "    src: url(\"file:///android_asset/fonts/georgia.ttf\")\n" +
                    "}\n" +
                    "body {\n" +
                    "    font-family: MyFont;\n" +
                    "    font-size: " + getResources().getDimension(R.dimen.article_details_text_size) + ";\n" +
                    "    color: #333333" + ";\n" +
                    "    line-height: " + getResources().getInteger(R.integer.article_details_line_height) + "%;\n" +
                    "    text-align: left;\n" +
                    "}\n" +
                    "</style>" +
                    "</head><body>" + bodyDesc + "</body></html>";

            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mWebView.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");
            mWebView.getSettings().setJavaScriptEnabled(true);

            videoWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            videoWebView.getSettings().setJavaScriptEnabled(true);
        } else {
            if (null != videoList && !videoList.isEmpty()) {
                for (VideoData video : videoList) {
                    if ("1".equals(isMomspresso)) {
                        videoWebView.setVisibility(View.VISIBLE);
                        cover_image.setVisibility(View.INVISIBLE);
                        bodyDesc = bodyDesc.replace(video.getKey(), "");
                        String vUrl = "<html><head></head><body><p style='text-align:center'><iframe allowfullscreen src=http:" + video.getVideoUrl() +
                                "?modestbranding=1&amp;rel=0&amp;showinfo=0\" style=\"width: 100%; height: 100%;\" ></iframe></p></body></html>";
                        videoWebView.loadDataWithBaseURL("", vUrl, "text/html", "utf-8", "");
                    } else if (bodyDescription.contains(video.getKey())) {
                        String vURL = video.getVideoUrl().replace("http:", "").replace("https:", "");
                        bodyDesc = bodyDesc.replace(video.getKey(), "<p style='text-align:center'><iframe allowfullscreen src=http:" + vURL + "?modestbranding=1&amp;rel=0&amp;showinfo=0\" style=\"width: 100%;\" ></iframe></p>");
                    }
                }
            }
            String bodyImgTxt = "<html><head>" +
                    "" +
                    "<style type=\"text/css\">\n" +
                    "@font-face {\n" +
                    "    font-family: MyFont;\n" +
                    "    src: url(\"file:///android_asset/fonts/georgia.ttf\")\n" +
                    "}\n" +
                    "body {\n" +
                    "    font-family: MyFont;\n" +
                    "    font-size: " + getResources().getDimension(R.dimen.article_details_text_size) + ";\n" +
                    "    color: #333333" + ";\n" +
                    "    line-height: " + getResources().getInteger(R.integer.article_details_line_height) + "%;\n" +
                    "    text-align: left;\n" +
                    "}\n" +
                    "</style>" +
                    "</head><body>" + bodyDesc + "</body></html>";

            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mWebView.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");
            mWebView.getSettings().setJavaScriptEnabled(true);

            videoWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            videoWebView.getSettings().setJavaScriptEnabled(true);
        }

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                floatingActionButton.setPadding(-1, -1, -1, -1);
                if (isAdded())
                    floatingActionButton.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        floatingActionButton.setTag(target);
        if (!StringUtils.isNullOrEmpty(detailData.getProfilePic().getClientApp())) {
            Picasso.with(getActivity()).load(detailData.getProfilePic().getClientApp()).into(target);
        }

        if (!StringUtils.isNullOrEmpty(detailData.getImageUrl().getThumbMax())) {
            Picasso.with(getActivity()).load(detailData.getImageUrl().getThumbMax()).placeholder(R.drawable.default_article).fit().into(cover_image);
        }
        if (!userDynamoId.equals(detailData.getUserId())) {
            hitUpdateViewCountAPI(detailData.getUserId(), detailData.getTags(), detailData.getCities());
        }
        createSelectedTagsView();
        setArticleLanguageCategoryId();
        String[] wordsArray = bodyDescription.split(" ");
        int totalWords = wordsArray.length;
        int wordReadTime = (totalWords * 60) / AppConstants.WORDS_PER_MINUTE;
        Log.d("READ TIME", "total images = " + imageList.size() + " total words = " + wordsArray.length + " IMAGE_READ_TIME = " + imageReadTime + " words_read_time = " + wordReadTime);
        estimatedReadTime = wordReadTime + imageReadTime;

//        trackArticleReadTime = new TrackArticleReadTime(getActivity());
//        trackArticleReadTime.startTimer();
    }

    private void setArticleLanguageCategoryId() {
        ArrayList<Map<String, String>> tagsList = detailData.getTags();
        try {
            FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            gtmLanguage = "English";
            for (final Map.Entry<String, LanguageConfigModel> langEntry : retMap.entrySet()) {
                for (int i = 0; i < tagsList.size(); i++) {
                    for (Map.Entry<String, String> tagEntry : tagsList.get(i).entrySet()) {
                        if (tagEntry.getKey().equals(langEntry.getValue().getId())) {

//                            ((ArticleDetailsContainerActivity) getActivity()).gtmArticleLoadedEvent(articleId, authorId + "~" + author, tagEntry.getKey() + "~" + tagEntry.getValue(), fragPos);
                            gtmLanguage = tagEntry.getKey() + "~" + tagEntry.getValue();
                            Utils.pushArticleLoadedEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author, gtmLanguage);
                            //The current category is a language category. Display play button if hindi or bangla else hide button.
                            switch (tagEntry.getKey()) {
                                case AppConstants.HINDI_CATEGORYID:
                                    articleLanguageCategoryId = AppConstants.HINDI_CATEGORYID;
                                    ((ArticleDetailsContainerActivity) getActivity()).showPlayArticleAudioButton();
                                    return;
                                case AppConstants.BANGLA_CATEGORYID:
                                    articleLanguageCategoryId = AppConstants.BANGLA_CATEGORYID;
                                    ((ArticleDetailsContainerActivity) getActivity()).showPlayArticleAudioButton();
                                    return;
                                default:
                                    return;
                            }
                        }
                    }
                }
            }
            if ("English".equals(gtmLanguage)) {
                Utils.pushArticleLoadedEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author, gtmLanguage);
            }
            if (!"1".equals(isMomspresso))
                ((ArticleDetailsContainerActivity) getActivity()).showPlayArticleAudioButton();
            return;
        } catch (Exception e) {

        }

    }

    public String getArticleLanguageCategoryId() {
        return articleLanguageCategoryId;
    }

    /*
    * creates selected tags views from the list of selected topics.
    * */
    private void createSelectedTagsView() {
//        selectedTopics
        getFollowedTopicsList();

    }

    private void getFollowedTopicsList() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.getFollowedCategories(userDynamoId);
        call.enqueue(getFollowedTopicsResponseCallback);
    }

    private void displayComments(ViewHolder holder, CommentsData commentList,
                                 boolean isNewComment) {
        if (holder != null) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.custom_comment_cell, null);
            holder.commentorsImage = (ImageView) view.findViewById(R.id.commentorImageView);
            holder.commentName = (TextView) view.findViewById(R.id.txvCommentTitle);
            holder.commentDescription = (TextView) view.findViewById(R.id.txvCommentDescription);
            holder.dateTxt = (TextView) view.findViewById(R.id.txvDate);
            holder.commentCellReplyTxt = (TextView) view.findViewById(R.id.txvCommentCellReply);
            holder.separatorView = view.findViewById(R.id.separatorView);
            holder.commentCellEditTxt = (TextView) view.findViewById(R.id.txvCommentCellEdit);
            holder.replyCommentView = (LinearLayout) view.findViewById(R.id.replyRelativeLayout);

            holder.commentorsImage.setOnClickListener(this);
            holder.commentName.setOnClickListener(this);
            holder.replyCommentView.setOnClickListener(this);
            holder.replyCommentView.setTag(commentList);

            holder.commentCellReplyTxt.setOnClickListener(this);
            holder.commentCellEditTxt.setOnClickListener(this);

            view.setTag(commentList);

            if (!"fb".equals(commentList.getComment_type()) && userDynamoId.equals(commentList.getUserId())) {
                holder.commentCellEditTxt.setVisibility(View.VISIBLE);
                holder.commentCellReplyTxt.setVisibility(View.GONE);
            } else {
                holder.commentCellEditTxt.setVisibility(View.GONE);
                if ("fb".equals(commentList.getComment_type())) {
                    holder.commentCellReplyTxt.setVisibility(View.GONE);
                    holder.separatorView.setVisibility(View.GONE);
                } else {
                    holder.commentCellReplyTxt.setVisibility(View.VISIBLE);
                }
            }

            if (!StringUtils.isNullOrEmpty(commentList.getName())) {
                holder.commentName.setText(commentList.getName());
            } else {
                holder.commentName.setText("User");
            }
            if (!StringUtils.isNullOrEmpty(commentList.getBody())) {
                holder.commentDescription.setText(commentList.getBody());
            }
            if (!StringUtils.isNullOrEmpty(commentList.getCreate())) {
                holder.dateTxt.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(commentList.getCreate())));
            } else {
                holder.dateTxt.setText("NA");
            }

            if (commentList.getProfile_image() != null && !StringUtils.isNullOrEmpty(commentList.getProfile_image().getClientAppMin())) {
                try {
                    Picasso.with(getActivity()).load(commentList.getProfile_image().getClientAppMin()).placeholder(R.drawable.default_commentor_img).into(holder.commentorsImage);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(holder.commentorsImage);
                }
            } else {
                Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(holder.commentorsImage);
            }

            if (isNewComment) {
                commentLayout.addView(view, 0);
            } else {
                commentLayout.addView(view);
            }
            if (commentList.getReplies() != null && commentList.getReplies().size() > 0) {
                holder.replyCommentView.setVisibility(View.VISIBLE);
                ViewHolder replyViewholder = new ViewHolder();
                for (int j = 0; j < commentList.getReplies().size(); j++) {
                    if ("fb".equals(commentList.getComment_type())) {
                        commentList.getReplies().get(j).setComment_type("fb");
                    }
                    displayReplies(replyViewholder, commentList.getReplies().get(j), holder.replyCommentView, REPLY_LEVEL_PARENT, j);
                }
            } else {
                holder.replyCommentView.setVisibility(View.GONE);
            }
        }
    }

    private void displayReplies(ViewHolder replyViewholder, CommentsData replies, LinearLayout parentView, int replyLevel, int replyPos) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.custom_reply_cell, null);
        replyViewholder.replyIndicatorImageView = (ImageView) view.findViewById(R.id.replyIndicatorImageView);
        replyViewholder.commentorsImage = (ImageView) view.findViewById(R.id.replierImageView);
        replyViewholder.commentName = (TextView) view.findViewById(R.id.txvReplyTitle);
        replyViewholder.commentDescription = (TextView) view.findViewById(R.id.txvCommentDescription);
        replyViewholder.dateTxt = (TextView) view.findViewById(R.id.txvDate);
        replyViewholder.replyCellReplyTxt = (TextView) view.findViewById(R.id.txvReplyCellReply);
        replyViewholder.separatorView = view.findViewById(R.id.separatorView);
        replyViewholder.replyCellEditTxt = (TextView) view.findViewById(R.id.txvReplyCellEdit);
        replyViewholder.replyCommentView = (LinearLayout) view.findViewById(R.id.replyRelativeLayout);

        replyViewholder.commentorsImage.setOnClickListener(this);
        replyViewholder.commentName.setOnClickListener(this);

        replyViewholder.replyCellReplyTxt.setOnClickListener(this);
        replyViewholder.replyCellEditTxt.setOnClickListener(this);

        if (!"fb".equals(replies.getComment_type()) && userDynamoId.equals(replies.getUserId())) {
            replyViewholder.replyCellEditTxt.setVisibility(View.VISIBLE);
            replyViewholder.replyCellReplyTxt.setVisibility(View.GONE);
        } else {
            replyViewholder.replyCellEditTxt.setVisibility(View.GONE);
            if ("fb".equals(replies.getComment_type())) {
                replyViewholder.replyCellReplyTxt.setVisibility(View.GONE);
                replyViewholder.separatorView.setVisibility(View.GONE);
            } else {
                if (replyLevel == REPLY_LEVEL_CHILD) {
                    replyViewholder.replyCellReplyTxt.setVisibility(View.GONE);
                    replyViewholder.separatorView.setVisibility(View.GONE);
                } else {
                    replyViewholder.replyCellReplyTxt.setVisibility(View.VISIBLE);
                }
            }
        }

        if (replyLevel == REPLY_LEVEL_PARENT && replyPos == 0) {
            replyViewholder.replyIndicatorImageView.setVisibility(View.VISIBLE);
        } else {
            replyViewholder.replyIndicatorImageView.setVisibility(View.INVISIBLE);
        }


        view.setTag(replies);

        if (!StringUtils.isNullOrEmpty(replies.getName())) {
            replyViewholder.commentName.setText(replies.getName());
        } else {
            replyViewholder.commentName.setText("User");
        }
        if (!StringUtils.isNullOrEmpty(replies.getBody())) {
            replyViewholder.commentDescription.setText(replies.getBody());
        }
        if (!StringUtils.isNullOrEmpty(replies.getCreate())) {
            replyViewholder.dateTxt.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(replies.getCreate())));
        } else {
            replyViewholder.dateTxt.setText("NA");
        }

        if (replies.getProfile_image() != null && !StringUtils.isNullOrEmpty(replies.getProfile_image().getClientAppMin())) {
            try {
                Picasso.with(getActivity()).load(replies.getProfile_image().getClientAppMin()).placeholder(R.drawable.default_commentor_img).into(replyViewholder.commentorsImage);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(replyViewholder.commentorsImage);
            }
        } else {
            Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(replyViewholder.commentorsImage);
        }
        parentView.addView(view);

        if (replies.getReplies() != null && replies.getReplies().size() > 0) {
            replyViewholder.replyCommentView.setVisibility(View.VISIBLE);
            ViewHolder replyReplyViewholder = new ViewHolder();
            for (int j = 0; j < replies.getReplies().size(); j++) {
                if ("fb".equals(replies.getComment_type())) {
                    replies.getReplies().get(j).setComment_type("fb");
                }
                displayReplies(replyReplyViewholder, replies.getReplies().get(j), parentView, REPLY_LEVEL_CHILD, j);
            }
        } else {
            replyViewholder.replyCommentView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.viewAllTagsTextView:
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(AppUtils.dpTopx(10), 0, AppUtils.dpTopx(10), 0);
                    tagsLayout.setLayoutParams(params);
                    viewAllTagsTextView.setVisibility(View.GONE);
                    break;
                case R.id.txvCommentCellReply:
                    openCommentDialog((CommentsData) ((View) v.getParent().getParent().getParent()).getTag(), "ADD");
                    break;
                case R.id.txvReplyCellReply:
                    openCommentDialog((CommentsData) ((View) v.getParent().getParent()).getTag(), "ADD");
                    break;
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
                case R.id.bookmarkTextView:
                    addRemoveBookmark();
                    break;
                case R.id.txvCommentTitle:
                case R.id.commentorImageView: {
                    CommentsData commentData = (CommentsData) ((View) v.getParent().getParent()).getTag();
                    if (!"fb".equals(commentData.getComment_type())) {
//                        trackArticleReadTime.updateTimeAtBackendAndGA(shareUrl, articleId, estimatedReadTime);
//                        trackArticleReadTime.resetTimer();
                        if (userDynamoId.equals(commentData.getUserId())) {
                            Intent profileIntent = new Intent(getActivity(), DashboardActivity.class);
                            profileIntent.putExtra("TabType", "profile");
                            startActivity(profileIntent);
                        } else {
                            Intent profileIntent = new Intent(getActivity(), BloggerProfileActivity.class);
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
                            Intent profileIntent = new Intent(getActivity(), DashboardActivity.class);
                            profileIntent.putExtra("TabType", "profile");
                            startActivity(profileIntent);
                        } else {
                            Intent profileIntent = new Intent(getActivity(), BloggerProfileActivity.class);
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
                    if (userDynamoId.equals(detailData.getUserId())) {
                        Intent profileIntent = new Intent(getActivity(), DashboardActivity.class);
                        profileIntent.putExtra("TabType", "profile");
                        startActivity(profileIntent);
                    } else {
                        Intent intentnn = new Intent(getActivity(), BloggerProfileActivity.class);
                        intentnn.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, detailData.getUserId());
                        intentnn.putExtra(AppConstants.AUTHOR_NAME, detailData.getUserName());
                        intentnn.putExtra(Constants.FROM_SCREEN, "Article Details");
                        startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                    }
                    break;
                case R.id.relatedArticles1: {
//                    Utils.pushEventRelatedArticle(getActivity(), GTMEventType.RELATED_ARTICLE_CLICKED_EVENT, userDynamoId + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 1);
                    if (recentAuthorArticleHeading.getText() != null && recentAuthorArticleHeading.getText().toString().contains("RELATED")) {
                        launchRelatedTrendingArticle(v, "articleDetailsRelatedList", 1);
                    } else {
                        launchRelatedTrendingArticle(v, "articleDetailsAuthorsRecentList", 1);
                    }
                    break;
                }
                case R.id.relatedArticles2: {
//                    Utils.pushEventRelatedArticle(getActivity(), GTMEventType.RELATED_ARTICLE_CLICKED_EVENT, userDynamoId + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 2);
                    if (recentAuthorArticleHeading.getText() != null && recentAuthorArticleHeading.getText().toString().contains("RELATED")) {
                        launchRelatedTrendingArticle(v, "articleDetailsRelatedList", 2);
                    } else {
                        launchRelatedTrendingArticle(v, "articleDetailsAuthorsRecentList", 2);
                    }
                    break;
                }
                case R.id.relatedArticles3: {
//                    Utils.pushEventRelatedArticle(getActivity(), GTMEventType.RELATED_ARTICLE_CLICKED_EVENT, userDynamoId + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 3);
                    if (recentAuthorArticleHeading.getText() != null && recentAuthorArticleHeading.getText().toString().contains("RELATED")) {
                        launchRelatedTrendingArticle(v, "articleDetailsRelatedList", 3);
                    } else {
                        launchRelatedTrendingArticle(v, "articleDetailsAuthorsRecentList", 3);
                    }
                    break;
                }
                case R.id.trendingRelatedArticles1: {
//                    Utils.pushEventRelatedArticle(getActivity(), GTMEventType.TRENDING_RELATED_ARTICLE_CLICKED_EVENT, userDynamoId + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 1);
                    launchRelatedTrendingArticle(v, "articleDetailsTrendingList", 1);
                    break;
                }
                case R.id.trendingRelatedArticles2: {
//                    Utils.pushEventRelatedArticle(getActivity(), GTMEventType.TRENDING_RELATED_ARTICLE_CLICKED_EVENT, userDynamoId + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 2);
                    launchRelatedTrendingArticle(v, "articleDetailsTrendingList", 2);
                    break;
                }
                case R.id.trendingRelatedArticles3: {
//                    Utils.pushEventRelatedArticle(getActivity(), GTMEventType.TRENDING_RELATED_ARTICLE_CLICKED_EVENT, userDynamoId + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 3);
                    launchRelatedTrendingArticle(v, "articleDetailsTrendingList", 3);
                    break;
                }
                case R.layout.related_tags_view: {
//                    trackArticleReadTime.updateTimeAtBackendAndGA(shareUrl, articleId, estimatedReadTime);
//                    trackArticleReadTime.resetTimer();
                    String categoryId = (String) v.getTag();
                    Intent intent = new Intent(getActivity(), FilteredTopicsArticleListingActivity.class);
                    intent.putExtra("selectedTopics", categoryId);
                    intent.putExtra("displayName", ((TextView) ((LinearLayout) v).getChildAt(0)).getText());
                    startActivity(intent);
                    break;
                }
                case R.id.commentFloatingActionButton:
                    openCommentDialog(null, "ADD");
//                    String appLinkUrl, previewImageUrl;
//
//                    appLinkUrl = "https://www.mydomain.com/myapplink";
//                    previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";
//                    if (AppInviteDialog.canShow()) {
//                        AppInviteContent content = new AppInviteContent.Builder()
//                                .setApplinkUrl(appLinkUrl)
//                                .setPreviewImageUrl(previewImageUrl)
//                                .build();
//                        AppInviteDialog.show(this, content);
//                    }
                    break;
                case R.id.likeTextView: {
                    if (recommendStatus == 0) {
                        recommendStatus = 1;
                        Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_recommended);
                        likeArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                        recommendUnrecommentArticleAPI("1");
                        Utils.pushLikeArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author);
                    } else {
                        recommendStatus = 0;
                        Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_recommend);
                        likeArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                        recommendUnrecommentArticleAPI("0");
                        Utils.pushUnlikeArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author);
                    }
                    break;
                }
                case R.id.facebookShareTextView:
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(shareUrl))
                                .build();
                        new ShareDialog(this).show(content);
                    }
                    Utils.pushShareArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author, "Facebook");
                    break;
                case R.id.whatsappShareTextView:
                    if (StringUtils.isNullOrEmpty(shareUrl)) {
                        Toast.makeText(getActivity(), "Unable to share with whatsapp.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "mycity4kids\n\nCheck out this interesting blog post\n " + shareUrl);
                        try {
                            startActivity(whatsappIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getActivity(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                        }
                        Utils.pushShareArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author, "Whatsapp");
                    }
                    break;
                case R.id.emailShareTextView:
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/html");
                    final PackageManager pm = getActivity().getPackageManager();
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
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "mycity4kids");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, AppUtils.fromHtml("mycity4kids\n\nCheck out this interesting blog post\n " + shareUrl));

                    try {
                        startActivity(intent);
                        Utils.pushShareArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author, "Email");
                    } catch (Exception e) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("plain/text");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{});
                        i.putExtra(Intent.EXTRA_SUBJECT, "");
                        i.putExtra(Intent.EXTRA_TEXT, "mycity4kids\n\nCheck out this interesting blog post\n " + shareUrl);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                            Utils.pushShareArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author, "Email");
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void openCommentDialog(CommentsData comData, String opType) {
        try {
            AddEditCommentReplyFragment commentFrag = new AddEditCommentReplyFragment();
            commentFrag.setTargetFragment(this, 0);
            Bundle _args = new Bundle();
            _args.putString(Constants.ARTICLE_ID, articleId);
            _args.putString(Constants.AUTHOR, authorId + "~" + author);
            _args.putString("opType", opType);
            if (comData != null) {
                _args.putParcelable("commentData", comData);
            }
            commentFrag.setArguments(_args);
            ((ArticleDetailsContainerActivity) getActivity()).hideToolbarPerm();
            ((ArticleDetailsContainerActivity) getActivity()).addFragment(commentFrag, null, true, "topToBottom");
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onCommentAddition(CommentsData cd) {
        displayComments(new ViewHolder(), cd, false);
    }

    @Override
    public void onReplyAddition(CommentsData updatedComment) {
        for (int i = 0; i < commentLayout.getChildCount(); i++) {
            CommentsData cdata = (CommentsData) commentLayout.getChildAt(i).getTag();
            CommentsData searchedData = recursiveSearch(cdata, updatedComment);
            if (searchedData != null) {
                searchedData.getReplies().add(updatedComment);
                ViewHolder viewHolder = new ViewHolder();
                displayCommentsAtPosition(viewHolder, cdata, false, i);
                break;
            } else {
                Log.d("Nothing in comment ", cdata.getBody());
            }
        }
    }

    @Override
    public void onCommentReplyEditSuccess(CommentsData cd) {
        for (int i = 0; i < commentLayout.getChildCount(); i++) {
            CommentsData cdata = (CommentsData) commentLayout.getChildAt(i).getTag();
            CommentsData searchedData = recursiveSearch(cdata, cd);
            if (searchedData != null) {
                ViewHolder viewHolder = new ViewHolder();
                displayCommentsAtPosition(viewHolder, cdata, false, i);
                break;
            } else {
                Log.d("Nothing in comment ", cdata.getBody());
            }
        }
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

    private void displayCommentsAtPosition(ViewHolder holder, CommentsData commentList,
                                           boolean isNewComment, int position) {
        if (holder != null) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.custom_comment_cell, null);
            holder.commentorsImage = (ImageView) view.findViewById(R.id.commentorImageView);
            holder.commentName = (TextView) view.findViewById(R.id.txvCommentTitle);
            holder.commentDescription = (TextView) view.findViewById(R.id.txvCommentDescription);
            holder.dateTxt = (TextView) view.findViewById(R.id.txvDate);
            holder.commentCellReplyTxt = (TextView) view.findViewById(R.id.txvCommentCellReply);
            holder.commentCellEditTxt = (TextView) view.findViewById(R.id.txvCommentCellEdit);
            holder.replyCommentView = (LinearLayout) view.findViewById(R.id.replyRelativeLayout);

            holder.commentorsImage.setOnClickListener(this);
            holder.commentName.setOnClickListener(this);
            holder.replyCommentView.setOnClickListener(this);
            holder.replyCommentView.setTag(commentList);

            holder.commentCellReplyTxt.setOnClickListener(this);
            holder.commentCellEditTxt.setOnClickListener(this);

            view.setTag(commentList);

            if (!"fb".equals(commentList.getComment_type()) && userDynamoId.equals(commentList.getUserId())) {
                holder.commentCellEditTxt.setVisibility(View.VISIBLE);
            } else {
                holder.commentCellEditTxt.setVisibility(View.INVISIBLE);
            }

            if ("fb".equals(commentList.getComment_type())) {
                holder.commentCellReplyTxt.setVisibility(View.INVISIBLE);
            } else {
                holder.commentCellReplyTxt.setVisibility(View.VISIBLE);
            }

            if (holder.commentCellEditTxt.getVisibility() == View.VISIBLE) {
                holder.commentCellReplyTxt.setVisibility(View.INVISIBLE);
            } else {
                holder.commentCellReplyTxt.setVisibility(View.VISIBLE);
            }

            if (!StringUtils.isNullOrEmpty(commentList.getName())) {
                holder.commentName.setText(commentList.getName());
            } else {
                holder.commentName.setText("User");
            }
            if (!StringUtils.isNullOrEmpty(commentList.getBody())) {
                holder.commentDescription.setText(commentList.getBody());
            }
            if (!StringUtils.isNullOrEmpty(commentList.getCreate())) {
                holder.dateTxt.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(commentList.getCreate())));
            } else {
                holder.dateTxt.setText("NA");
            }

            if (commentList.getProfile_image() != null && !StringUtils.isNullOrEmpty(commentList.getProfile_image().getClientAppMin())) {
                try {
                    Picasso.with(getActivity()).load(commentList.getProfile_image().getClientAppMin()).placeholder(R.drawable.default_commentor_img).into(holder.commentorsImage);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(holder.commentorsImage);
                }
            } else {
                Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(holder.commentorsImage);
            }

            commentLayout.removeViewAt(position);
            commentLayout.addView(view, position);
            if (commentList.getReplies() != null && commentList.getReplies().size() > 0) {
                holder.replyCommentView.setVisibility(View.VISIBLE);
                ViewHolder replyViewholder = new ViewHolder();
                for (int j = 0; j < commentList.getReplies().size(); j++) {
                    displayReplies(replyViewholder, commentList.getReplies().get(j), holder.replyCommentView, REPLY_LEVEL_PARENT, j);
                }
            } else {
                holder.replyCommentView.setVisibility(View.GONE);
            }
        }
    }

    private void launchRelatedTrendingArticle(View v, String listingType, int index) {
//        trackArticleReadTime.updateTimeAtBackendAndGA(shareUrl, articleId, estimatedReadTime);
//        trackArticleReadTime.resetTimer();
        Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
        ArrayList<ArticleListingResult> parentingListData = (ArrayList<ArticleListingResult>) v.getTag();
        intent.putExtra(Constants.ARTICLE_ID, parentingListData.get(index - 1).getId());
        intent.putExtra(Constants.AUTHOR_ID, parentingListData.get(index - 1).getUserId());
        intent.putExtra(Constants.FROM_SCREEN, "Article Details");
        intent.putExtra(Constants.ARTICLE_OPENED_FROM, listingType);
        intent.putExtra(Constants.ARTICLE_INDEX, "" + (index - 1));
        intent.putParcelableArrayListExtra("pagerListData", parentingListData);
        startActivity(intent);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);
        View tagsView = mScrollView.findViewById(R.id.tagsLayoutContainer);
        Rect scrollBounds = new Rect();
        mScrollView.getHitRect(scrollBounds);
        int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));
        if (diff <= 10 && !isLoading && !StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http") && !AppConstants.PAGINATION_END_VALUE.equals(pagination)) {
            getMoreComments();
        }

        int permanentDiff = (tagsView.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));
        if (permanentDiff <= 0) {
            isArticleDetailEndReached = true;
            if (bottomToolbarLL.getVisibility() == View.VISIBLE) {
                hideBottomToolbar();
            }
            if (isSwipeNextAvailable) {
                swipeNextTextView.setVisibility(View.VISIBLE);
            }
            if (commentFloatingActionButton.getVisibility() == View.INVISIBLE) {
                showFloatingActionButton();
            }
        } else {
            if (bottomToolbarLL.getVisibility() != View.VISIBLE) {
                showBottomToolbar();
            }

            if (isSwipeNextAvailable) {
                swipeNextTextView.setVisibility(View.GONE);
            }
            isArticleDetailEndReached = false;
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = ((ArticleDetailsContainerActivity) getActivity()).getSupportActionBar();
        if (ab == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ((ArticleDetailsContainerActivity) getActivity()).hideMainToolbar();
            }
//            if (bottomToolbarLL.getVisibility() == View.VISIBLE) {
//                hideBottomToolbar();
//            }
            if (!isArticleDetailEndReached && commentFloatingActionButton.getVisibility() == View.VISIBLE) {
                hideFloatingActionButton();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ((ArticleDetailsContainerActivity) getActivity()).showMainToolbar();
            }
//            if (bottomToolbarLL.getVisibility() != View.VISIBLE) {
//                showBottomToolbar();
//            }
            if (commentFloatingActionButton.getVisibility() == View.INVISIBLE) {
                showFloatingActionButton();
            }
        }
    }

    private void hideBottomToolbar() {
        bottomToolbarLL.animate()
                .translationY(bottomToolbarLL.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        bottomToolbarLL.setVisibility(View.GONE);
                    }
                });
    }

    private void showBottomToolbar() {
        bottomToolbarLL.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        bottomToolbarLL.setVisibility(View.VISIBLE);
                    }

                });
    }

    public void hideFloatingActionButton() {
        commentFloatingActionButton.animate()
                .translationY(commentFloatingActionButton.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        commentFloatingActionButton.setVisibility(View.INVISIBLE);
                    }
                });
    }

    public void showFloatingActionButton() {
        commentFloatingActionButton.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        commentFloatingActionButton.setVisibility(View.VISIBLE);
                    }
                });
    }

    public String getArticleContent() {
        if (detailData == null || detailData.getBody() == null) {
            return "";
        }
        if (AppUtils.stripHtml(detailData.getBody().getText()).length() > 3999) {
            return AppUtils.stripHtml(detailData.getBody().getText()).substring(0, 3998);
        } else {
            return AppUtils.stripHtml(detailData.getBody().getText());
        }
    }

    public String getGTMArticleId() {
        return articleId;
    }

    public String getGTMAuthor() {
        return authorId + "~" + author;
    }

    public String getGTMLanguage() {
        return gtmLanguage;
    }


    private class ViewHolder {
        private ImageView commentorsImage;
        private TextView commentName;
        private TextView commentDescription;
        private TextView dateTxt;
        private TextView commentCellReplyTxt;
        private View separatorView;
        private TextView replyCellReplyTxt;
        private TextView commentCellEditTxt;
        private TextView replyCellEditTxt;
        private LinearLayout replyCommentView;
        public ImageView replyIndicatorImageView;
    }

    public void followAPICall(String id) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);

        if (isFollowing) {
            isFollowing = false;
            followClick.setText("FOLLOW");
            Utils.pushUnfollowAuthorEvent(getActivity(), "DetailArticleScreen", userDynamoId, authorId + "~" + author);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            followClick.setText("FOLLOWING");
            Utils.pushFollowAuthorEvent(getActivity(), "DetailArticleScreen", userDynamoId, authorId + "~" + author);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.followUser(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }

    }

    private void addRemoveBookmark() {

        if (bookmarkStatus == 0) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(articleId);
            bookmarkStatus = 1;
            Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmarked);
            bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            if ("1".equals(isMomspresso)) {
                Call<AddBookmarkResponse> call = articleDetailsAPI.addVideoWatchLater(articleDetailRequest);
                call.enqueue(addBookmarkResponseCallback);
                Utils.pushWatchLaterArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author);
            } else {
                Call<AddBookmarkResponse> call = articleDetailsAPI.addBookmark(articleDetailRequest);
                call.enqueue(addBookmarkResponseCallback);
                Utils.pushBookmarkArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author);
            }
        } else {
            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
            deleteBookmarkRequest.setId(bookmarkId);
            bookmarkStatus = 0;
            Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark);
            bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            if ("1".equals(isMomspresso)) {
                Call<AddBookmarkResponse> call = articleDetailsAPI.deleteVideoWatchLater(deleteBookmarkRequest);
                call.enqueue(addBookmarkResponseCallback);
                Utils.pushRemoveWatchLaterArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author);
            } else {
                Call<AddBookmarkResponse> call = articleDetailsAPI.deleteBookmark(deleteBookmarkRequest);
                call.enqueue(addBookmarkResponseCallback);
                Utils.pushUnbookmarkArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author);
            }
        }

    }

    Callback<ArticleDetailResult> articleDetailResponseCallbackS3 = new Callback<ArticleDetailResult>() {
        @Override
        public void onResponse(Call<ArticleDetailResult> call, retrofit2.Response<ArticleDetailResult> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                getArticleDetailsWebserviceAPI();
                return;
            }
            try {
                ArticleDetailResult responseData = response.body();
                getResponseUpdateUi(responseData);
                authorId = detailData.getUserId();
                hitBookmarkFollowingStatusAPI();
                if ("1".equals(isMomspresso)) {
                    hitBookmarkVideoStatusAPI();
                }
                hitRelatedArticleAPI();
                commentURL = responseData.getCommentsUri();

                if (!StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http")) {
                    getMoreComments();
                } else {
                    commentType = "fb";
                    commentURL = "http";
                    getMoreComments();
                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                getArticleDetailsWebserviceAPI();
            }

        }

        @Override
        public void onFailure(Call<ArticleDetailResult> call, Throwable t) {
            removeProgressDialog();
            handleExceptions(t);
            getArticleDetailsWebserviceAPI();
        }
    };

    private Callback<ViewCountResponse> getViewCountResponseCallback = new Callback<ViewCountResponse>() {
        @Override
        public void onResponse(Call<ViewCountResponse> call, retrofit2.Response<ViewCountResponse> response) {
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ViewCountResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    articleViewCountTextView.setText(responseData.getData().get(0).getCount());
                    articleCommentCountTextView.setText(responseData.getData().get(0).getCommentCount());
                    articleRecommendationCountTextView.setText(responseData.getData().get(0).getLikeCount());
                    if ("0".equals(responseData.getData().get(0).getCommentCount())) {
                        articleCommentCountTextView.setVisibility(View.GONE);
                    }
                    if ("0".equals(responseData.getData().get(0).getLikeCount())) {
                        articleRecommendationCountTextView.setVisibility(View.GONE);
                    }
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

    private void getArticleDetailsWebserviceAPI() {
        Call<ArticleDetailWebserviceResponse> call = articleDetailsAPI.getArticleDetailsFromWebservice(articleId);
        call.enqueue(articleDetailResponseCallbackWebservice);
    }

    Callback<ArticleDetailWebserviceResponse> articleDetailResponseCallbackWebservice = new Callback<ArticleDetailWebserviceResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailWebserviceResponse> call, retrofit2.Response<ArticleDetailWebserviceResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }
            try {
                ArticleDetailWebserviceResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    getResponseUpdateUi(responseData.getData());
                    authorId = detailData.getUserId();
                    hitBookmarkFollowingStatusAPI();
                    hitRelatedArticleAPI();
                    commentURL = responseData.getData().getCommentsUri();

                    if (!StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http")) {
                        getMoreComments();
                    }
                } else {

                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<ArticleDetailWebserviceResponse> call, Throwable t) {
            removeProgressDialog();
            handleExceptions(t);
        }
    };

    private OnWebServiceCompleteListener mGetArticleListingListener = new OnWebServiceCompleteListener() {
        @Override
        public void onWebServiceComplete(VolleyBaseResponse response, boolean isError) {
            if (!isAdded()) {
                return;
            }
            if (isError) {
                if (null != response && response.getResponseCode() != 999)
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
            } else {
                if (response == null) {
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                    removeProgressDialog();
                    return;
                }

                ArticleListingResponse responseBlogData;
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                    responseBlogData = gson.fromJson(response.getResponseBody(), ArticleListingResponse.class);
                } catch (JsonSyntaxException jse) {
                    Crashlytics.logException(jse);
                    Log.d("JsonSyntaxException", Log.getStackTraceString(jse));
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                    removeProgressDialog();
                    return;
                }

                if (responseBlogData.getCode() == 200 && Constants.SUCCESS.equals(responseBlogData.getStatus())) {
                    ArrayList<ArticleListingResult> dataList = responseBlogData.getData().get(0).getResult();
                    if (dataList == null || dataList.size() == 0) {

                    } else {
                        trendingArticles.setVisibility(View.VISIBLE);
                        Collections.shuffle(dataList);
                        if (dataList.size() >= 3) {
                            Picasso.with(getActivity()).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList);

                            Picasso.with(getActivity()).load(dataList.get(1).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            trendingRelatedArticles2.setTag(dataList);

                            Picasso.with(getActivity()).load(dataList.get(2).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles3.getArticleImageView());
                            trendingRelatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            trendingRelatedArticles3.setTag(dataList);
                        } else if (dataList.size() == 2) {
                            Picasso.with(getActivity()).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList);

                            Picasso.with(getActivity()).load(dataList.get(1).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            trendingRelatedArticles2.setTag(dataList);

                            trendingRelatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            Picasso.with(getActivity()).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList);
                            trendingRelatedArticles2.setVisibility(View.GONE);
                            trendingRelatedArticles3.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    };

    private Callback<ArticleListingResponse> categoryArticleResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {

            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                NetworkErrorException nee = new NetworkErrorException("Category related Article API failure");
                Crashlytics.logException(nee);
                Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsAPI.getPublishedArticles(authorId, 0, 1, 4);
                callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
                return;
            }

            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
                    if (dataList != null) {
                        for (int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).getId().equals(articleId)) {
                                dataList.remove(i);
                                break;
                            }
                        }
                    }
                    if (dataList.size() == 0) {
                        Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsAPI.getPublishedArticles(authorId, 0, 1, 4);
                        callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
                    } else {
                        recentAuthorArticleHeading.setText("RELATED ARTICLES");
                        recentAuthorArticles.setVisibility(View.VISIBLE);
                        Collections.shuffle(dataList);
                        iSwipeRelated.onRelatedSwipe(dataList);
                        if (dataList.size() >= 3) {
                            Picasso.with(getActivity()).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(new ArrayList<ArticleListingResult>(dataList.subList(0, 2)));

                            Picasso.with(getActivity()).load(dataList.get(1).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(new ArrayList<ArticleListingResult>(dataList.subList(0, 2)));

                            Picasso.with(getActivity()).load(dataList.get(2).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles3.getArticleImageView());
                            relatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            relatedArticles3.setTag(new ArrayList<ArticleListingResult>(dataList.subList(0, 2)));
                        } else if (dataList.size() == 2) {
                            Picasso.with(getActivity()).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList);

                            Picasso.with(getActivity()).load(dataList.get(1).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList);
                            relatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            Picasso.with(getActivity()).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList);
                            relatedArticles2.setVisibility(View.GONE);
                            relatedArticles3.setVisibility(View.GONE);
                        }
                    }
                } else {
                    NetworkErrorException nee = new NetworkErrorException("Category related Article Error Response");
                    Crashlytics.logException(nee);
                    Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsAPI.getPublishedArticles(authorId, 0, 1, 4);
                    callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsAPI.getPublishedArticles(authorId, 0, 1, 4);
                callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<ArticleListingResponse> bloggersArticleResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {

            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getId().equals(articleId)) {
                            dataList.remove(i);
                            break;
                        }
                    }
                    if (dataList.size() == 0) {
                        relatedTrendingSeparator.setVisibility(View.GONE);
                    } else {
                        Collections.shuffle(dataList);
                        recentAuthorArticleHeading.setText(getString(R.string.ad_recent_logs_from_title));
                        recentAuthorArticles.setVisibility(View.VISIBLE);

                        if (dataList.size() >= 3) {
                            Picasso.with(getActivity()).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList);

                            Picasso.with(getActivity()).load(dataList.get(1).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList);

                            Picasso.with(getActivity()).load(dataList.get(2).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles3.getArticleImageView());
                            relatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            relatedArticles3.setTag(dataList);
                        } else if (dataList.size() == 2) {
                            Picasso.with(getActivity()).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList);

                            Picasso.with(getActivity()).load(dataList.get(1).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList);
                            relatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            Picasso.with(getActivity()).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList);
                            relatedArticles2.setVisibility(View.GONE);
                            relatedArticles3.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<FollowUnfollowCategoriesResponse> getFollowedTopicsResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    ArrayList<String> previouslyFollowedTopics = (ArrayList<String>) responseData.getData();
                    ArrayList<Map<String, String>> tagsList = detailData.getTags();
                    LayoutInflater mInflater = LayoutInflater.from(getActivity());
                    int relatedImageWidth = (int) getResources().getDimension(R.dimen.related_article_article_image_width);
                    viewAllTagsTextView.setVisibility(View.GONE);
                    width = width - ((RelativeLayout.LayoutParams) tagsLayout.getLayoutParams()).leftMargin - ((RelativeLayout.LayoutParams) tagsLayout.getLayoutParams()).rightMargin;

                    ArrayList<String> sponsoredList = new ArrayList<>();
                    FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                    String fileContent = AppUtils.convertStreamToString(fileInputStream);
                    TopicsResponse tRes = new Gson().fromJson(fileContent, TopicsResponse.class);
                    for (int i = 0; i < tRes.getData().size(); i++) {
                        if (AppConstants.SPONSORED_CATEGORYID.equals(tRes.getData().get(i).getId())) {
                            for (int j = 0; j < tRes.getData().get(i).getChild().size(); j++) {
                                for (int k = 0; k < tRes.getData().get(i).getChild().get(j).getChild().size(); k++) {
                                    sponsoredList.add(tRes.getData().get(i).getChild().get(j).getChild().get(j).getId());
                                }
                                sponsoredList.add(tRes.getData().get(i).getChild().get(j).getId());
                            }
                        }
                    }
                    for (int i = 0; i < tagsList.size(); i++) {

                        for (Map.Entry<String, String> entry : tagsList.get(i).entrySet()) {
                            String key = entry.getKey();
                            final String value = entry.getValue();
                            if (AppConstants.IGNORE_TAG.equals(key)) {
                                continue;
                            }

                            final RelativeLayout topicView = (RelativeLayout) mInflater.inflate(R.layout.related_tags_view, null, false);
                            topicView.setClickable(true);
                            topicView.getChildAt(0).setTag(key);
                            topicView.getChildAt(2).setTag(key);
                            ((TextView) topicView.getChildAt(0)).setText(value.toUpperCase());
                            ((TextView) topicView.getChildAt(0)).measure(0, 0);
                            width = width - ((TextView) topicView.getChildAt(0)).getMeasuredWidth() - AppUtils.dpTopx(1)
                                    - relatedImageWidth - topicView.getPaddingLeft() - topicView.getPaddingRight();
                            if (width < 0) {
                                viewAllTagsTextView.setVisibility(View.VISIBLE);
                            }
                            if (sponsoredList.contains(key)) {
                                ((ImageView) topicView.getChildAt(2)).setVisibility(View.GONE);
                                ((View) topicView.getChildAt(1)).setVisibility(View.GONE);
                            }
                            if (null != previouslyFollowedTopics && previouslyFollowedTopics.contains(key)) {
                                ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.tick));
                                topicView.getChildAt(2).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ad_tags_following_bg));
                                ((ImageView) topicView.getChildAt(2)).setColorFilter(ContextCompat.getColor(getActivity(), R.color.white_color));
                                topicView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 0);
                                    }
                                });
                            } else {
                                ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.follow_plus));
                                topicView.getChildAt(2).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ad_tags_follow_bg));
                                ((ImageView) topicView.getChildAt(2)).setColorFilter(ContextCompat.getColor(getActivity(), R.color.ad_tags_following_bg));
                                topicView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
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
                                    Intent intent = new Intent(getActivity(), FilteredTopicsArticleListingActivity.class);
                                    intent.putExtra("selectedTopics", categoryId);
                                    intent.putExtra("displayName", value);
                                    startActivity(intent);
                                }
                            });
                            tagsLayout.addView(topicView);
                        }
                    }
                } else {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
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
            Utils.pushUnfollowTopicEvent(getActivity(), "DetailArticleScreen", userDynamoId, selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
            tagView.getChildAt(0).setTag(selectedTopic);
            tagView.getChildAt(2).setTag(selectedTopic);
            ((ImageView) tagView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.follow_plus));
            tagView.getChildAt(2).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ad_tags_follow_bg));
            ((ImageView) tagView.getChildAt(2)).setColorFilter(ContextCompat.getColor(getActivity(), R.color.ad_tags_following_bg));
            tagView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TOPICS ----- ", "FOLLOW");
                    followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 1);
                }
            });
        } else {
            Log.d("GTM UNFOLLOW", "displayName" + selectedTopic);
            Utils.pushFollowTopicEvent(getActivity(), "DetailArticleScreen", userDynamoId, selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
            tagView.getChildAt(0).setTag(selectedTopic);
            tagView.getChildAt(2).setTag(selectedTopic);
            ((ImageView) tagView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.tick));
            tagView.getChildAt(2).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ad_tags_following_bg));
            ((ImageView) tagView.getChildAt(2)).setColorFilter(ContextCompat.getColor(getActivity(), R.color.white_color));
            tagView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TOPICS ----- ", "UNFOLLOW");
                    followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 0);
                }
            });
        }
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.followCategories(userDynamoId, followUnfollowCategoriesRequest);
        call.enqueue(followUnfollowCategoriesResponseCallback);
    }

    private Callback<FollowUnfollowCategoriesResponse> followUnfollowCategoriesResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
                } else {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            if (isAdded())
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
        }
    };

    Callback<ResponseBody> commentsCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            removeProgressDialog();
            mLodingView.setVisibility(View.GONE);
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                isLoading = false;
                commentType = "fb";
                commentURL = "http";
                return;
            }
            try {
                isLoading = false;
                String resData = new String(response.body().bytes());
                ArrayList<CommentsData> arrayList = new ArrayList<>();
                JSONArray commentsJson = new JSONArray(resData);
                commentURL = "";
                if (commentsJson.length() > 0) {
                    commentHeading.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < commentsJson.length(); i++) {
                    if (commentsJson.getJSONObject(i).has("next")) {
                        commentURL = commentsJson.getJSONObject(i).getString("next");
                    } else {
                        CommentsData cData = new Gson().fromJson(commentsJson.get(i).toString(), CommentsData.class);
                        arrayList.add(cData);
                    }
                }
                if (StringUtils.isNullOrEmpty(commentURL)) {
                    commentType = "fb";
                    commentURL = "http";
                }
                commentLayout = ((LinearLayout) fragmentView.findViewById(R.id.commnetLout));
                ViewHolder viewHolder = new ViewHolder();
                for (int i = 0; i < arrayList.size(); i++) {
                    displayComments(viewHolder, arrayList.get(i), false);
                }
            } catch (JSONException jsonexception) {
                Crashlytics.logException(jsonexception);
                Log.d("JSONException", Log.getStackTraceString(jsonexception));
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong while parsing response from server");
            } catch (Exception ex) {
                Crashlytics.logException(ex);
                Log.d("MC4kException", Log.getStackTraceString(ex));
                if (isAdded()) {
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            mLodingView.setVisibility(View.GONE);
            handleExceptions(t);
        }
    };

    Callback<FBCommentResponse> fbCommentsCallback = new Callback<FBCommentResponse>() {
        @Override
        public void onResponse(Call<FBCommentResponse> call, retrofit2.Response<FBCommentResponse> response) {
            removeProgressDialog();
            mLodingView.setVisibility(View.GONE);
            if (response == null || null == response.body()) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                isLoading = false;
                FBCommentResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<CommentsData> dataList = responseData.getData().getResult();
                    pagination = responseData.getData().getPagination();
                    if (dataList.size() == 0) {
                        pagination = AppConstants.PAGINATION_END_VALUE;
                    } else {
                        commentLayout = ((LinearLayout) fragmentView.findViewById(R.id.commnetLout));
                        if (!isFbCommentHeadingAdded) {
                            TextView textView = new TextView(getActivity());
                            textView.setText(getString(R.string.ad_comments_fb_comment));
                            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.ad_comment_title));
                            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/oswald_regular.ttf");
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.ad_comments_heading));
                            textView.setTypeface(face);
                            int paddingVal = AppUtils.dpTopx(10);
                            textView.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
                            commentLayout.addView(textView);
                            isFbCommentHeadingAdded = true;
                        }

                        ViewHolder viewHolder = null;
                        viewHolder = new ViewHolder();
                        for (int i = 0; i < dataList.size(); i++) {
                            CommentsData fbCommentData = dataList.get(i);
                            fbCommentData.setComment_type("fb");
                            displayComments(viewHolder, fbCommentData, false);
                        }
                    }
                } else {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<FBCommentResponse> call, Throwable t) {
            mLodingView.setVisibility(View.GONE);
            handleExceptions(t);
        }
    };


    private Callback<ArticleDetailResponse> isBookmarkedFollowedResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            if (response == null || null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            ArticleDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (!"1".equals(isMomspresso)) {
                    bookmarkFlag = responseData.getData().getResult().getBookmarkStatus();
                    if (!isAdded()) {
                        return;
                    }
                    if ("0".equals(bookmarkFlag)) {
                        bookmarkStatus = 0;
                        Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark);
                        bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                    } else {
                        Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmarked);
                        bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                        bookmarkStatus = 1;
                    }
                    bookmarkId = responseData.getData().getResult().getBookmarkId();
                }
                if (userDynamoId.equals(authorId)) {
                    followClick.setVisibility(View.INVISIBLE);
                } else {
                    if ("0".equals(responseData.getData().getResult().getIsFollowed())) {
                        followClick.setEnabled(true);
                        followClick.setText("FOLLOW");
                        isFollowing = false;
                    } else {
                        followClick.setEnabled(true);
                        followClick.setText("FOLLOWING");
                        isFollowing = true;
                    }
                }
            } else {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<ArticleDetailResponse> isBookmarkedVideoResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            if (response == null || null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            ArticleDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                bookmarkFlag = responseData.getData().getResult().getBookmarkStatus();
                if (!isAdded()) {
                    return;
                }
                if ("0".equals(bookmarkFlag)) {
                    bookmarkStatus = 0;
                    Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark);
                    bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                } else {
                    Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmarked);
                    bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                    bookmarkStatus = 1;
                }
                bookmarkId = responseData.getData().getResult().getBookmarkId();
            } else {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
            handleExceptions(t);
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

    private Callback<AddBookmarkResponse> addBookmarkResponseCallback = new Callback<AddBookmarkResponse>() {
        @Override
        public void onResponse(Call<AddBookmarkResponse> call, retrofit2.Response<AddBookmarkResponse> response) {
            if (response == null || null == response.body()) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }
            AddBookmarkResponse responseData = response.body();
            updateBookmarkStatus(ADD_BOOKMARK, responseData);
        }

        @Override
        public void onFailure(Call<AddBookmarkResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<ArticleRecommendationStatusResponse> recommendStatusResponseCallback = new Callback<ArticleRecommendationStatusResponse>() {
        @Override
        public void onResponse(Call<ArticleRecommendationStatusResponse> call, retrofit2.Response<ArticleRecommendationStatusResponse> response) {
            if (response == null || null == response.body()) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }
            ArticleRecommendationStatusResponse responseData = response.body();
            recommendationFlag = responseData.getData().getStatus();
            if (!isAdded()) {
                return;
            }
            if ("0".equals(recommendationFlag)) {
                recommendStatus = 0;
                Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_recommend);
                likeArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            } else {
                recommendStatus = 1;
                Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_recommended);
                likeArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            }
        }

        @Override
        public void onFailure(Call<ArticleRecommendationStatusResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<RecommendUnrecommendArticleResponse> recommendUnrecommendArticleResponseCallback = new Callback<RecommendUnrecommendArticleResponse>() {
        @Override
        public void onResponse(Call<RecommendUnrecommendArticleResponse> call, retrofit2.Response<RecommendUnrecommendArticleResponse> response) {
            if (response == null || null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((ArticleDetailsContainerActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                RecommendUnrecommendArticleResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (!isAdded()) {
                        return;
                    }
                    if (null == responseData.getData() && responseData.getData().isEmpty()) {
                        ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
                    } else {
                        ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
                    }
                } else {
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<RecommendUnrecommendArticleResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private void updateBookmarkStatus(int status, AddBookmarkResponse responseData) {

        if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
            if (status == ADD_BOOKMARK) {
                bookmarkId = responseData.getData().getResult().getBookmarkId();
            } else {
                bookmarkId = null;
            }
        } else {
            if (StringUtils.isNullOrEmpty(responseData.getReason())) {
//                showToast(responseData.getReason());
            } else {
//                showToast(getString(R.string.went_wrong));
            }
        }
    }

    public void handleExceptions(Throwable t) {
        if (isAdded()) {
            if (t instanceof UnknownHostException) {
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.error_network));
            } else if (t instanceof SocketTimeoutException) {
                ((ArticleDetailsContainerActivity) getActivity()).showToast("connection timed out");
            } else {
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            }
        }
        Crashlytics.logException(t);
        Log.d("MC4kException", Log.getStackTraceString(t));
    }

    Callback<FollowUnfollowUserResponse> followUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response == null || response.body() == null) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followClick.setText("FOLLOW");
                    isFollowing = false;
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            if (isAdded())
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    Callback<FollowUnfollowUserResponse> unfollowUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response == null || response.body() == null) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followClick.setText("FOLLOWING");
                    isFollowing = true;
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            if (isAdded())
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private class MyWebChromeClient extends WebChromeClient {
        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mContentView = (RelativeLayout) getActivity().findViewById(R.id.content_frame);
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(getActivity());
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            getActivity().setContentView(mCustomViewContainer);
            mCustomViewContainer.bringToFront();
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            } else {
                // Hide the custom view.
                mCustomView.setVisibility(View.GONE);
                // Remove the custom view from its container.
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                // Show the content view.
                mContentView.setVisibility(View.VISIBLE);
                getActivity().setContentView(mContentView);
            }
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    ISwipeRelated iSwipeRelated;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            iSwipeRelated = (ISwipeRelated) activity;
        } catch (ClassCastException e) {

        }

    }

    public interface ISwipeRelated {
        void onRelatedSwipe(ArrayList<ArticleListingResult> articleList);
    }

}
