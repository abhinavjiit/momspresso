package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
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
import com.mycity4kids.models.response.CrownDataResponse;
import com.mycity4kids.models.response.DeepLinkingResposnse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.ViewCountResponse;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;
import com.mycity4kids.observablescrollview.ObservableScrollView;
import com.mycity4kids.observablescrollview.ObservableScrollViewCallbacks;
import com.mycity4kids.observablescrollview.ScrollState;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.DeepLinkingAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TorcaiAdsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.FilteredTopicsArticleListingActivity;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.GroupIdCategoryMap;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.CustomFontTextView;
import com.mycity4kids.widget.RelatedArticlesView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import okhttp3.ResponseBody;
import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 6/6/17.
 */
public class ArticleDetailsFragment extends BaseFragment implements View.OnClickListener,
        ObservableScrollViewCallbacks,
        GroupIdCategoryMap.GroupCategoryInterface,
        GroupMembershipStatus.IMembershipStatus {

    private static final int ADD_BOOKMARK = 1;
    private MixpanelAPI mixpanel;
    private ISwipeRelated swipeRelated;
    private ArticleDetailResult detailData;
    private Bitmap defaultBloggerBitmap;
    private Bitmap resized;
    private ArticleDetailsAPI articleDetailsApi;
    private ArrayList<ImageData> imageList;
    private ArrayList<VideoData> videoList;
    private ArrayList<ArticleListingResult> impressionList;
    private int width;
    private int bookmarkStatus;
    private int recommendStatus;
    private float density;
    private boolean isFollowing = false;
    private boolean isArticleDetailLoaded = false;
    private boolean isSwipeNextAvailable;
    private boolean bookmarkFlag = false;
    private String recommendationFlag = "0";
    private String commentUrl = "";
    private String shareUrl = "";
    private String bookmarkId;
    private String authorId;
    private String author;
    private String articleId;
    private String deepLinkUrl;
    private String commentMainUrl;
    private String isMomspresso;
    private String userDynamoId;
    private String articleLanguageCategoryId;
    private String gtmLanguage;
    private int followTopicChangeNewUser = 0;

    private ObservableScrollView observableScrollView;
    private TextView followClick;
    private TextView recentAuthorArticleHeading;
    private LinearLayout trendingArticles;
    private LinearLayout recentAuthorArticles;
    private WebView mainWebView;
    private RelatedArticlesView relatedArticles1;
    private RelatedArticlesView relatedArticles2;
    private RelatedArticlesView relatedArticles3;
    private RelatedArticlesView trendingRelatedArticles1;
    private RelatedArticlesView trendingRelatedArticles2;
    private RelatedArticlesView trendingRelatedArticles3;

    private MyWebChromeClient mainWebChromeClient = null;
    private View mainCustomView;
    private RelativeLayout mainContentView;
    private FrameLayout mainCustomViewContainer;
    private WebChromeClient.CustomViewCallback mainCustomViewCallback;

    private CustomFontTextView facebookShareTextView;
    private CustomFontTextView whatsappShareTextView;
    private CustomFontTextView emailShareTextView;
    private CustomFontTextView likeArticleTextView;
    private CustomFontTextView bookmarkArticleTextView;
    private TextView articleTitle;
    private TextView authorType;
    private TextView articleViewCountTextView;
    private TextView articleCommentCountTextView;
    private TextView articleRecommendationCountTextView;
    private TextView viewAllTagsTextView;
    private TextView swipeNextTextView;
    private ImageView coverImage;
    private ImageView floatingActionButton;
    private RelativeLayout loadingView;
    private FlowLayout tagsLayout;
    private Rect scrollBounds;
    private View fragmentView;
    private LinearLayout bottomToolbarLL;
    private View relatedTrendingSeparator;
    private TextView viewCommentsTextView;
    private LayoutInflater layoutInflater;
    private RelativeLayout groupHeaderView;
    private ImageView groupHeaderImageView;
    private TextView groupHeadingTextView;
    private TextView groupSubHeadingTextView;
    private int groupId;
    private ImageView sponsoredImage;
    private TextView sponsoredTextView;
    private RelativeLayout sponsoredViewContainer;
    private ArrayList<Topics> shortStoriesTopicList = new ArrayList<>();
    private ArticleDetailResult responseData;
    private ImageView badge;
    private LinearLayout progressBarContainer;
    private boolean newArticleDetailFlag;
    private String webViewUrl;
    private ImageView crownImageView;
    private YouTubePlayerView youTubePlayerView;
    private WebView bottomAdSlotWebView;
    private WebView topAdSlotWebView;
    private RelativeLayout followPopUpBottomContainer;
    private ImageView cancelFollowPopUp;
    private TextView authorNameFollowPopUp;
    private ImageView authorImageViewFollowPopUp;
    private TextView followText;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        layoutInflater = inflater;
        fragmentView = inflater.inflate(R.layout.article_details_fragment, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "articleDetailFragment",
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        userDynamoId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        deepLinkUrl = "";// getIntent().getStringExtra(Constants.DEEPLINK_URL);
        try {
            mixpanel = MixpanelAPI
                    .getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

            followPopUpBottomContainer = (RelativeLayout) fragmentView.findViewById(R.id.followPopUpBottomContainer);
            cancelFollowPopUp = (ImageView) fragmentView.findViewById(R.id.cancelFollowPopUp);
            authorNameFollowPopUp = (TextView) fragmentView.findViewById(R.id.authorNameFollowPopUp);
            authorImageViewFollowPopUp = (ImageView) fragmentView.findViewById(R.id.authorImageViewFollowPopUp);
            followText = (TextView) fragmentView.findViewById(R.id.followText);
            floatingActionButton = (ImageView) fragmentView.findViewById(R.id.user_image);
            mainWebView = (WebView) fragmentView.findViewById(R.id.articleWebView);
            viewAllTagsTextView = (TextView) fragmentView.findViewById(R.id.viewAllTagsTextView);
            bottomToolbarLL = (LinearLayout) fragmentView.findViewById(R.id.bottomToolbarLL);
            facebookShareTextView = (CustomFontTextView) fragmentView.findViewById(R.id.facebookShareTextView);
            whatsappShareTextView = (CustomFontTextView) fragmentView.findViewById(R.id.whatsappShareTextView);
            emailShareTextView = (CustomFontTextView) fragmentView.findViewById(R.id.emailShareTextView);
            likeArticleTextView = (CustomFontTextView) fragmentView.findViewById(R.id.likeTextView);
            bookmarkArticleTextView = (CustomFontTextView) fragmentView.findViewById(R.id.bookmarkTextView);
            sponsoredViewContainer = (RelativeLayout) fragmentView.findViewById(R.id.sponseredLayoutContainer);
            sponsoredImage = (ImageView) fragmentView.findViewById(R.id.sponseredImage);
            sponsoredTextView = (TextView) fragmentView.findViewById(R.id.sponseredText);
            badge = (ImageView) fragmentView.findViewById(R.id.badge);
            progressBarContainer = (LinearLayout) fragmentView.findViewById(R.id.progressBarContainer);
            authorType = (TextView) fragmentView.findViewById(R.id.blogger_type);
            followClick = (TextView) fragmentView.findViewById(R.id.follow_click);
            articleTitle = (TextView) fragmentView.findViewById(R.id.article_title);
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
            articleRecommendationCountTextView = (TextView) fragmentView
                    .findViewById(R.id.articleRecommendationCountTextView);
            coverImage = fragmentView.findViewById(R.id.cover_image);
            swipeNextTextView = fragmentView.findViewById(R.id.swipeNextTextView);
            viewCommentsTextView = fragmentView.findViewById(R.id.viewCommentsTextView);
            groupHeaderView = fragmentView.findViewById(R.id.groupHeaderView);
            groupHeaderImageView = fragmentView.findViewById(R.id.groupHeaderImageView);
            groupHeadingTextView = fragmentView.findViewById(R.id.groupHeadingTextView);
            groupSubHeadingTextView = fragmentView.findViewById(R.id.groupSubHeadingTextView);
            observableScrollView = fragmentView.findViewById(R.id.scroll_view);
            loadingView = fragmentView.findViewById(R.id.relativeLoadingView);
            crownImageView = fragmentView.findViewById(R.id.crownImageView);
            youTubePlayerView = fragmentView.findViewById(R.id.youtube_player_view);
            bottomAdSlotWebView = fragmentView.findViewById(R.id.bottomAdSlotWebView);
            topAdSlotWebView = fragmentView.findViewById(R.id.topAdSlotWebView);

            fragmentView.findViewById(R.id.user_name).setOnClickListener(this);
            floatingActionButton.setOnClickListener(this);
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
            viewCommentsTextView.setOnClickListener(this);
            groupHeaderView.setOnClickListener(this);
            followClick.setOnClickListener(this);
            followClick.setEnabled(false);
            cancelFollowPopUp.setOnClickListener(this);
            followText.setOnClickListener(this);

            mainWebChromeClient = new MyWebChromeClient();
            mainWebView.setWebChromeClient(mainWebChromeClient);
            mainWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    try {
                        getDeepLinkData(request.getUrl());
                    } catch (Exception anfe) {
                        Crashlytics.logException(anfe);
                        Log.d("FileNotFoundException", Log.getStackTraceString(anfe));
                    }
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBarContainer.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request,
                        WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    Log.d("onReceivedError", "----- " + error.toString() + " -----");
                }

                @Override
                public void onReceivedHttpError(WebView view, WebResourceRequest request,
                        WebResourceResponse errorResponse) {
                    super.onReceivedHttpError(view, request, errorResponse);
                    Log.d("onReceivedHttpError",
                            "--    --- " + errorResponse.getReasonPhrase() + " -----");
                }
            });

            if ((AppConstants.LOCALE_ENGLISH.equals(SharedPrefUtils.getAppLocale(getActivity())))) {
                mainWebView.setOnLongClickListener(v -> true);
            } else {
                mainWebView.setOnLongClickListener(v -> true);
                mainWebView.setLongClickable(false);
            }

            facebookShareTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.ic_facebook_svg), null, null);
            if (Build.VERSION.SDK_INT > 23) {
                try {
                    Drawable myDrawable = ContextCompat
                            .getDrawable(getActivity(), R.drawable.ic_whats_app);
                    myDrawable.setTint(getResources().getColor(R.color.app_red));
                    whatsappShareTextView
                            .setCompoundDrawablesWithIntrinsicBounds(null, myDrawable, null, null);
                } catch (NullPointerException e) {
                    Crashlytics.logException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }
                try {
                    Drawable myDrawable = ContextCompat
                            .getDrawable(getActivity(), R.drawable.ic_collection_add);
                    myDrawable.setTint(ContextCompat.getColor(emailShareTextView.getContext(), R.color.app_red));
                    emailShareTextView
                            .setCompoundDrawablesWithIntrinsicBounds(null, myDrawable, null, null);
                } catch (NullPointerException e) {
                    Crashlytics.logException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }

                try {
                    Drawable myDrawable = ContextCompat
                            .getDrawable(getActivity(), R.drawable.ic_bookmark);
                    myDrawable.setTint(getResources().getColor(R.color.app_red));
                    bookmarkArticleTextView
                            .setCompoundDrawablesWithIntrinsicBounds(null, myDrawable, null, null);
                } catch (NullPointerException e) {
                    Crashlytics.logException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }
                try {
                    Drawable myDrawable = ContextCompat
                            .getDrawable(getActivity(), R.drawable.ic_recommend);
                    myDrawable.setTint(getResources().getColor(R.color.app_red));
                    likeArticleTextView
                            .setCompoundDrawablesWithIntrinsicBounds(null, myDrawable, null, null);
                } catch (NullPointerException e) {
                    Crashlytics.logException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }
            }

            density = getResources().getDisplayMetrics().density;
            width = getResources().getDisplayMetrics().widthPixels;
            defaultBloggerBitmap = BitmapFactory
                    .decodeResource(getResources(), R.drawable.default_blogger_profile_img);
            //resizing image because of crash .image size is large.
            resized = Bitmap.createScaledBitmap(defaultBloggerBitmap, 96, 96, true);
            floatingActionButton.setImageDrawable(new BitmapDrawable(getResources(), resized));

            try {
                FileInputStream fileInputStream = BaseApplication.getAppContext()
                        .openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory())
                        .create();
                TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                for (int i = 0; i < res.getData().size(); i++) {
                    if (AppConstants.SPONSORED_CATEGORYID.equals(res.getData().get(i).getId())) {
                        shortStoriesTopicList.add(res.getData().get(i));
                    }
                }
            } catch (FileNotFoundException e) {
                Crashlytics.logException(e);
                Log.d("FileNotFoundException", Log.getStackTraceString(e));
                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                final TopicsCategoryAPI topicsApi = retro.create(TopicsCategoryAPI.class);
                Call<ResponseBody> caller = topicsApi.downloadTopicsJSON();
                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                            retrofit2.Response<ResponseBody> response) {
                        AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(),
                                AppConstants.CATEGORIES_JSON_FILE, response.body());
                        try {
                            FileInputStream fileInputStream = BaseApplication.getAppContext()
                                    .openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                            TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                            for (int i = 0; i < res.getData().size(); i++) {
                                if (AppConstants.SPONSORED_CATEGORYID
                                        .equals(res.getData().get(i).getId())) {
                                    shortStoriesTopicList.add(res.getData().get(i));
                                }
                            }

                        } catch (FileNotFoundException e) {
                            Crashlytics.logException(e);
                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            Log.d("MC4KException", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Crashlytics.logException(t);
                        Log.d("MC4KException", Log.getStackTraceString(t));
                    }
                });
            }

            loadingView = (RelativeLayout) fragmentView.findViewById(R.id.relativeLoadingView);
            observableScrollView = (ObservableScrollView) fragmentView.findViewById(R.id.scroll_view);
            observableScrollView.setScrollViewCallbacks(this);
            impressionList = new ArrayList<>();

            Bundle bundle = getArguments();
            if (bundle != null) {
                articleId = bundle.getString(Constants.ARTICLE_ID);
                authorId = bundle.getString(Constants.AUTHOR_ID, "");
                isSwipeNextAvailable = bundle.getBoolean("swipeNext", false);
                newArticleDetailFlag = bundle.getBoolean(AppConstants.NEW_ARTICLE_DETAIL_FLAG);

                if (isSwipeNextAvailable) {
                    isSwipeNextAvailable = BaseApplication.isFirstSwipe();
                }

                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                articleDetailsApi = retro.create(ArticleDetailsAPI.class);
                hitArticleDetailsRedisApi();
                getViewCountApi();
                hitRecommendedStatusApi();

                TorcaiAdsAPI torcaiAdsApi = retro.create(TorcaiAdsAPI.class);
                Call<ResponseBody> topAdsCall;
                Call<ResponseBody> endAdsCall;
                if (BuildConfig.DEBUG) {
                    topAdsCall = torcaiAdsApi.getTorcaiAd();
                    endAdsCall = torcaiAdsApi.getTorcaiAd();
                } else {
                    topAdsCall = torcaiAdsApi.getTorcaiAd(AppUtils.getAdSlotId("ART", "TOP"),
                            "www.momspresso.com",
                            AppUtils.getIpAddress(true),
                            "1",
                            "Momspresso",
                            AppUtils.getAppVersion(BaseApplication.getAppContext()),
                            "https://play.google.com/store/apps/details?id=com.mycity4kids&hl=en_IN",
                            "mobile",
                            SharedPrefUtils.getAdvertisementId(BaseApplication.getAppContext()),
                            "" + System.getProperty("http.agent"));

                    endAdsCall = torcaiAdsApi.getTorcaiAd(AppUtils.getAdSlotId("ART", "END"),
                            "www.momspresso.com",
                            AppUtils.getIpAddress(true),
                            "1",
                            "Momspresso",
                            AppUtils.getAppVersion(BaseApplication.getAppContext()),
                            "https://play.google.com/store/apps/details?id=com.mycity4kids&hl=en_IN", "mobile",
                            SharedPrefUtils.getAdvertisementId(BaseApplication.getAppContext()),
                            "" + System.getProperty("http.agent"));
                }
                topAdsCall.enqueue(torcaiTopAdResponseCallback);
                endAdsCall.enqueue(torcaiEndAdResponseCallback);
            }

            scrollBounds = new Rect();
            observableScrollView.getHitRect(scrollBounds);
        } catch (Exception e) {
            removeProgressDialog();
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        return fragmentView;
    }

    private Callback<ResponseBody> torcaiTopAdResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            String resData = null;
            try {
                if (response.body() != null) {
                    resData = new String(response.body().bytes());
                    JSONObject jsonObject = new JSONObject(resData);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    String html = jsonArray.getJSONObject(0).getJSONObject("response").getString("adm")
                            .replaceAll("\"//", "\"https://");
                    Log.e("HTML CONTENT", "html == " + html);
                    topAdSlotWebView
                            .loadDataWithBaseURL("", html, "text/html", "utf-8", "");
                } else {
                    topAdSlotWebView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                topAdSlotWebView.setVisibility(View.GONE);
                Crashlytics.logException(e);
                Log.d("FileNotFoundException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            topAdSlotWebView.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("FileNotFoundException", Log.getStackTraceString(t));
        }
    };

    private Callback<ResponseBody> torcaiEndAdResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            String resData = null;
            try {
                if (response.body() != null) {
                    resData = new String(response.body().bytes());
                    JSONObject jsonObject = new JSONObject(resData);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    String html = jsonArray.getJSONObject(0).getJSONObject("response").getString("adm")
                            .replaceAll("\"//", "\"https://");
                    Log.e("HTML CONTENT", "html == " + html);
                    bottomAdSlotWebView
                            .loadDataWithBaseURL("", html, "text/html", "utf-8", "");
                } else {
                    bottomAdSlotWebView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                bottomAdSlotWebView.setVisibility(View.GONE);
                Crashlytics.logException(e);
                Log.d("FileNotFoundException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            bottomAdSlotWebView.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("FileNotFoundException", Log.getStackTraceString(t));
        }
    };

    private void getDeepLinkData(Uri url) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        showProgressDialog("");
        DeepLinkingAPI deepLinkingApi = retrofit.create(DeepLinkingAPI.class);
        Call<DeepLinkingResposnse> call = deepLinkingApi.getUrlDetails(url.toString());
        call.enqueue(new Callback<DeepLinkingResposnse>() {
            @Override
            public void onResponse(Call<DeepLinkingResposnse> call,
                    retrofit2.Response<DeepLinkingResposnse> response) {
                removeProgressDialog();
                try {
                    DeepLinkingResposnse responseData = response.body();
                    if (responseData != null && responseData.getCode() == 200 && Constants.SUCCESS
                            .equals(responseData.getStatus())) {
                        if (AppConstants.DEEP_LINK_ARTICLE_DETAIL
                                .equals(responseData.getData().getResult().getType())) {
                            Intent intent = new Intent(getActivity(),
                                    ArticleDetailsContainerActivity.class);
                            intent.putExtra(Constants.AUTHOR_ID, responseData.getData().getResult().getAuthor_id());
                            intent.putExtra(Constants.ARTICLE_ID, responseData.getData().getResult().getArticle_id());
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "DeepLinking");
                            intent.putExtra(Constants.ARTICLE_INDEX, "-1");
                            intent.putExtra(Constants.FROM_SCREEN, "DeepLinking");
                            intent.putExtra(Constants.AUTHOR,
                                    responseData.getData().getResult().getAuthor_id() + "~" + responseData.getData()
                                            .getResult().getAuthor_name());
                            startActivity(intent);
                        } else {
                            Intent i = new Intent(Intent.ACTION_VIEW, url);
                            startActivity(i);
                        }
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<DeepLinkingResposnse> call, Throwable t) {
                removeProgressDialog();
                Crashlytics.logException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mainWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mainWebView.onPause();
    }

    @Override
    public void onDestroy() {
        if (youTubePlayerView != null) {
            youTubePlayerView.release();
        }
        super.onDestroy();
    }

    private void hitArticleDetailsRedisApi() {
        Call<ArticleDetailResult> call = articleDetailsApi
                .getArticleDetailsFromRedis(articleId, "articleId");
        call.enqueue(articleDetailResponseCallbackRedis);
    }

    private void getViewCountApi() {
        Call<ViewCountResponse> call = articleDetailsApi.getViewCount(articleId);
        call.enqueue(getViewCountResponseCallback);
    }

    private void getCrownData() {
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        articleDetailsApi = retro.create(ArticleDetailsAPI.class);
        Call<CrownDataResponse> call = articleDetailsApi.getCrownData(authorId);
        call.enqueue(getCrownDataResponse);
    }

    private void hitBookmarkFollowingStatusApi() {
        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
        articleDetailRequest.setArticleId(articleId);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI bookmarFollowingStatusApi = retro.create(ArticleDetailsAPI.class);

        Call<ArticleDetailResponse> callBookmark = bookmarFollowingStatusApi
                .checkFollowingBookmarkStatus(articleId, authorId);
        callBookmark.enqueue(isBookmarkedFollowedResponseCallback);
    }

    private void hitBookmarkVideoStatusApi() {
        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
        articleDetailRequest.setArticleId(articleId);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI bookmarkVideoStatusApi = retro.create(ArticleDetailsAPI.class);

        Call<ArticleDetailResponse> callBookmark = bookmarkVideoStatusApi
                .checkBookmarkVideoStatus(articleDetailRequest);
        callBookmark.enqueue(isBookmarkedVideoResponseCallback);
    }

    private void hitRelatedArticleApi() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryApi = retrofit.create(TopicsCategoryAPI.class);
        Call<ArticleListingResponse> vernacularTrendingCall = topicsCategoryApi
                .getTrendingArticles(1, 4,
                        SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
        vernacularTrendingCall.enqueue(vernacularTrendingResponseCallback);

        Call<ArticleListingResponse> categoryRelatedArticlesCall = articleDetailsApi
                .getCategoryRelatedArticles(articleId, 0, 3,
                        SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
        categoryRelatedArticlesCall.enqueue(categoryArticleResponseCallback);
    }

    private void hitUpdateViewCountApi(String userId, ArrayList<Map<String, String>> tagsList,
            ArrayList<Map<String, String>> cityList) {
        UpdateViewCountRequest updateViewCountRequest = new UpdateViewCountRequest();
        updateViewCountRequest.setUserId(userId);
        updateViewCountRequest.setTags(tagsList);
        updateViewCountRequest.setCities(cityList);
        Call<ResponseBody> callUpdateViewCount = articleDetailsApi
                .updateViewCount(articleId, updateViewCountRequest);
        callUpdateViewCount.enqueue(updateViewCountResponseCallback);
    }

    private void hitRecommendedStatusApi() {
        Call<ArticleRecommendationStatusResponse> checkArticleRecommendStaus = articleDetailsApi
                .getArticleRecommendedStatus(articleId);
        checkArticleRecommendStaus.enqueue(recommendStatusResponseCallback);
    }

    private void recommendUnrecommendArticleApi(String status) {
        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest =
                new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(articleId);
        recommendUnrecommendArticleRequest.setStatus(status);

        Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle = articleDetailsApi
                .recommendUnrecommendArticle(recommendUnrecommendArticleRequest);
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback);
    }

    private void getResponseUpdateUi(ArticleDetailResult detailsResponse) {
        if (SharedPrefUtils.getFollowTopicApproachChangeFlag(BaseApplication.getAppContext())) {
            followTopicChangeNewUser = 1;
        }

        detailData = detailsResponse;
        imageList = detailData.getBody().getImage();
        videoList = detailData.getBody().getVideo();
        author = detailData.getUserName();
        isMomspresso = detailData.getIsMomspresso();
        getCrownData();

        if (!StringUtils.isNullOrEmpty(detailData.getImageUrl().getThumbMax())) {
            Picasso.get().load(detailData.getImageUrl().getThumbMax())
                    .placeholder(R.drawable.default_article).resize(width, (int) (220 * density))
                    .centerCrop().into(coverImage);
        }

        if (!StringUtils.isNullOrEmpty(detailData.getTitle())) {
            articleTitle.setText(detailData.getTitle());
        }

        try {
            if (!StringUtils.isNullOrEmpty(detailData.getUserType())) {
                if (AppConstants.USER_TYPE_BLOGGER.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        authorType.setText(
                                AppUtils.getString(getActivity(), R.string.author_type_blogger));
                    } else {
                        authorType.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkUrl)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + detailData.getBlogTitleSlug()
                                .trim() + "/article/" + detailData.getTitleSlug();
                        webViewUrl =
                                AppConstants.ARTICLE_WEBVIEW_URL + detailData.getBlogTitleSlug()
                                        .trim() + "/article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkUrl;
                    }
                } else if (AppConstants.USER_TYPE_EXPERT.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        authorType.setText(
                                AppUtils.getString(getActivity(), R.string.author_type_expert));
                    } else {
                        authorType.setText(AppConstants.AUTHOR_TYPE_EXPERT.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkUrl)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData
                                .getTitleSlug();
                        webViewUrl = AppConstants.ARTICLE_WEBVIEW_URL + "article/" + detailData
                                .getTitleSlug();
                    } else {
                        shareUrl = deepLinkUrl;
                    }
                } else if (AppConstants.USER_TYPE_EDITOR.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        authorType.setText(
                                AppUtils.getString(getActivity(), R.string.author_type_editor));
                    } else {
                        authorType.setText(AppConstants.AUTHOR_TYPE_EDITOR.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkUrl)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData
                                .getTitleSlug();
                        webViewUrl = AppConstants.ARTICLE_WEBVIEW_URL + "article/" + detailData
                                .getTitleSlug();
                    } else {
                        shareUrl = deepLinkUrl;
                    }
                } else if (AppConstants.USER_TYPE_EDITORIAL.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        authorType.setText(
                                AppUtils.getString(getActivity(), R.string.author_type_editorial));
                    } else {
                        authorType.setText(AppConstants.AUTHOR_TYPE_EDITORIAL.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkUrl)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData
                                .getTitleSlug();
                        webViewUrl = AppConstants.ARTICLE_WEBVIEW_URL + "article/" + detailData
                                .getTitleSlug();
                    } else {
                        shareUrl = deepLinkUrl;
                    }
                } else if (AppConstants.USER_TYPE_FEATURED.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        authorType.setText(
                                AppUtils.getString(getActivity(), R.string.author_type_featured));
                    } else {
                        authorType.setText(AppConstants.AUTHOR_TYPE_FEATURED.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkUrl)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData
                                .getTitleSlug();
                        webViewUrl = AppConstants.ARTICLE_WEBVIEW_URL + "article/" + detailData
                                .getTitleSlug();
                    } else {
                        shareUrl = deepLinkUrl;
                    }
                } else if (AppConstants.USER_TYPE_COLLABORATION.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        authorType.setText(AppUtils.getString(getActivity(),
                                R.string.author_type_collaboration));
                    } else {
                        authorType.setText(AppConstants.AUTHOR_TYPE_COLLABORATION.toUpperCase());
                    }

                    if (StringUtils.isNullOrEmpty(deepLinkUrl)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData
                                .getTitleSlug();
                        webViewUrl = AppConstants.ARTICLE_WEBVIEW_URL + "article/" + detailData
                                .getTitleSlug();
                    } else {
                        shareUrl = deepLinkUrl;
                    }
                } else {
                    if (isAdded()) {
                        authorType.setText(
                                AppUtils.getString(getActivity(), R.string.author_type_user));
                    } else {
                        authorType.setText(AppConstants.AUTHOR_TYPE_USER.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkUrl)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + detailData.getBlogTitleSlug()
                                .trim() + "/article/" + detailData.getTitleSlug();
                        webViewUrl =
                                AppConstants.ARTICLE_WEBVIEW_URL + detailData.getBlogTitleSlug()
                                        .trim() + "/article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkUrl;
                    }
                }
            } else {
                // Default Author type set to Blogger
                if (isAdded()) {
                    authorType.setText(
                            AppUtils.getString(getActivity(), R.string.author_type_blogger));
                } else {
                    authorType.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
                }
                if (StringUtils.isNullOrEmpty(deepLinkUrl)) {
                    shareUrl = AppConstants.ARTICLE_SHARE_URL + detailData.getBlogTitleSlug().trim()
                            + "/article/" + detailData.getTitleSlug();
                    webViewUrl =
                            AppConstants.ARTICLE_WEBVIEW_URL + detailData.getBlogTitleSlug().trim()
                                    + "/article/" + detailData.getTitleSlug();
                } else {
                    shareUrl = deepLinkUrl;
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

        if (!StringUtils.isNullOrEmpty(detailData.getUserName())) {
            ((TextView) fragmentView.findViewById(R.id.user_name))
                    .setText(detailData.getUserName());
        }

        if (!StringUtils.isNullOrEmpty(detailData.getCreated())) {
            ((TextView) fragmentView.findViewById(R.id.article_date)).setText(
                    DateTimeUtils.getDateFromTimestamp(Long.parseLong(detailData.getCreated())));
        }

        if (!newArticleDetailFlag) {
            String bodyDescription = detailData.getBody().getText();
            String bodyDesc = bodyDescription;
            int imageIndex = 0;
            int imageReadTime = 0;
            if (imageList.size() > 0) {
                for (ImageData images : imageList) {
                    if (imageIndex <= AppConstants.MIN_ARTICLE_BODY_IMAGE_READ_TIME) {
                        imageReadTime =
                                imageReadTime + AppConstants.MAX_ARTICLE_BODY_IMAGE_READ_TIME
                                        - imageIndex;
                    } else {
                        imageReadTime =
                                imageReadTime + AppConstants.MIN_ARTICLE_BODY_IMAGE_READ_TIME;
                    }
                    imageIndex++;
                    if (bodyDescription.contains(images.getKey())) {
                        bodyDesc = bodyDesc.replace(images.getKey(),
                                "<p style='text-align:center'><img src=" + images.getValue()
                                        + " style=\"width: 100%;\"+></p>");
                    }
                }
                if (null != videoList && !videoList.isEmpty()) {
                    for (VideoData video : videoList) {
                        if ("1".equals(isMomspresso)) {
                            youTubePlayerView.setVisibility(View.VISIBLE);
                            String youTubeId = AppUtils
                                    .extractYoutubeIdForMomspresso(video.getVideoUrl());
                            youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
                                YouTubePlayerUtils.loadOrCueVideo(
                                        youTubePlayer,
                                        getLifecycle(),
                                        youTubeId,
                                        0
                                );
                            });
                            coverImage.setVisibility(View.INVISIBLE);
                            bodyDesc = bodyDesc.replace(video.getKey(), "");
                        } else if (bodyDescription.contains(video.getKey())) {
                            String videoUrl = video.getVideoUrl().replace("http:", "")
                                    .replace("https:", "");
                            bodyDesc = bodyDesc.replace(video.getKey(),
                                    "<p style='text-align:center'><iframe allowfullscreen src=http:" + videoUrl
                                            + "?modestbranding=1&amp;rel=0&amp;showinfo=0\" style=\"width: 100%;\">"
                                            + "</iframe></p>");
                        }
                    }
                }

                String bodyImgTxt = "<html><head>"
                        + ""
                        + "<style type=\"text/css\">\n"
                        + "@font-face {\n"
                        + "    font-family: MyFont;\n"
                        + "    src: url(\"file:///android_asset/fonts/georgia.ttf\")\n"
                        + "}\n"
                        + "body {\n"
                        + "    font-family: MyFont;\n"
                        + "    font-size: " + getResources()
                        .getDimension(R.dimen.article_details_text_size) + ";\n"
                        + "    color: #333333" + ";\n"
                        + "    line-height: " + getResources()
                        .getInteger(R.integer.article_details_line_height) + "%;\n"
                        + "    text-align: left;\n"
                        + "}\n"
                        + "</style>"
                        + "</head><body>" + bodyDesc + "</body></html>";

                mainWebView.getSettings()
                        .setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                mainWebView.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");
                mainWebView.getSettings().setJavaScriptEnabled(true);
            } else {
                if (null != videoList && !videoList.isEmpty()) {
                    for (VideoData video : videoList) {
                        if ("1".equals(isMomspresso)) {
                            youTubePlayerView.setVisibility(View.VISIBLE);
                            String youTubeId = AppUtils
                                    .extractYoutubeIdForMomspresso(video.getVideoUrl());
                            youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
                                YouTubePlayerUtils.loadOrCueVideo(
                                        youTubePlayer,
                                        getLifecycle(),
                                        youTubeId,
                                        0
                                );
                            });
                            coverImage.setVisibility(View.INVISIBLE);
                            bodyDesc = bodyDesc.replace(video.getKey(), "");
                        } else if (bodyDescription.contains(video.getKey())) {
                            String videoUrl = video.getVideoUrl().replace("http:", "")
                                    .replace("https:", "");
                            bodyDesc = bodyDesc.replace(video.getKey(),
                                    "<p style='text-align:center'><iframe allowfullscreen src=http:"
                                            + videoUrl
                                            + "?modestbranding=1&amp;rel=0&amp;showinfo=0\" style=\"width: 100%;\" >"
                                            + "</iframe></p>");
                        }
                    }
                }
                String bodyImgTxt = "<html><head>"
                        + ""
                        + "<style type=\"text/css\">\n"
                        + "@font-face {\n"
                        + "    font-family: MyFont;\n"
                        + "    src: url(\"file:///android_asset/fonts/georgia.ttf\")\n"
                        + "}\n"
                        + "body {\n"
                        + "    font-family: MyFont;\n"
                        + "    font-size: " + getResources()
                        .getDimension(R.dimen.article_details_text_size) + ";\n"
                        + "    color: #333333" + ";\n"
                        + "    line-height: " + getResources()
                        .getInteger(R.integer.article_details_line_height) + "%;\n"
                        + "    text-align: left;\n"
                        + "}\n"
                        + "</style>"
                        + "</head><body>" + bodyDesc + "</body></html>";
                mainWebView.getSettings()
                        .setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                mainWebView.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");
                mainWebView.getSettings().setJavaScriptEnabled(true);
            }
        } else {
            Log.d("WEB VIEW URL", "------ " + webViewUrl + " -----");
            mainWebView.getSettings().setJavaScriptEnabled(true);
            mainWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mainWebView.loadUrl(webViewUrl);
        }

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                floatingActionButton.setPadding(-1, -1, -1, -1);
                if (isAdded()) {
                    floatingActionButton
                            .setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        floatingActionButton.setTag(target);
        if (!StringUtils.isNullOrEmpty(detailData.getProfilePic().getClientApp())) {
            Picasso.get().load(detailData.getProfilePic().getClientApp()).into(target);
        }

        if (!StringUtils.isNullOrEmpty(detailData.getImageUrl().getThumbMax())) {
            Picasso.get().load(detailData.getImageUrl().getThumbMax())
                    .placeholder(R.drawable.default_article).fit().into(coverImage);
        }
        if (!userDynamoId.equals(detailData.getUserId())) {
            hitUpdateViewCountApi(detailData.getUserId(), detailData.getTags(),
                    detailData.getCities());
        }
        createSelectedTagsView();
        setArticleLanguageCategoryId();
    }

    private void setArticleLanguageCategoryId() {
        ArrayList<Map<String, String>> tagsList = detailData.getTags();
        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext()
                    .openFileInput(AppConstants.LANGUAGES_JSON_FILE);
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
                            gtmLanguage = tagEntry.getKey() + "~" + tagEntry.getValue();
                            Utils.pushArticleLoadedEvent(getActivity(), "DetailArticleScreen",
                                    userDynamoId + "", articleId, authorId + "~" + author,
                                    gtmLanguage);
                            //The current category is a language category.
                            // Display play button if hindi or bangla else hide button.
                            switch (tagEntry.getKey()) {
                                case AppConstants.HINDI_CATEGORYID:
                                    articleLanguageCategoryId = AppConstants.HINDI_CATEGORYID;
                                    ((ArticleDetailsContainerActivity) getActivity())
                                            .showPlayArticleAudioButton();
                                    return;
                                case AppConstants.BANGLA_CATEGORYID:
                                    articleLanguageCategoryId = AppConstants.BANGLA_CATEGORYID;
                                    ((ArticleDetailsContainerActivity) getActivity())
                                            .showPlayArticleAudioButton();
                                    return;
                                case AppConstants.TAMIL_CATEGORYID:
                                    articleLanguageCategoryId = AppConstants.TAMIL_CATEGORYID;
                                    ((ArticleDetailsContainerActivity) getActivity())
                                            .showPlayArticleAudioButton();
                                    return;
                                case AppConstants.TELUGU_CATEGORYID:
                                    articleLanguageCategoryId = AppConstants.TELUGU_CATEGORYID;
                                    ((ArticleDetailsContainerActivity) getActivity())
                                            .showPlayArticleAudioButton();
                                    return;
                                default:
                                    return;
                            }
                        }
                    }
                }
            }
            if ("English".equals(gtmLanguage)) {
                Utils.pushArticleLoadedEvent(getActivity(), "DetailArticleScreen",
                        userDynamoId + "", articleId, authorId + "~" + author, gtmLanguage);
            }
            if (!"1".equals(isMomspresso)) {
                ((ArticleDetailsContainerActivity) getActivity()).showPlayArticleAudioButton();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    public String getArticleLanguageCategoryId() {
        return articleLanguageCategoryId;
    }

    private void createSelectedTagsView() {
        getFollowedTopicsList();
    }

    private void getFollowedTopicsList() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryApi = retrofit.create(TopicsCategoryAPI.class);
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryApi
                .getFollowedCategories(userDynamoId);
        call.enqueue(getFollowedTopicsResponseCallback);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.groupHeaderView: {
                    if (groupId == 0) {
                        Intent groupIntent = new Intent(getActivity(), DashboardActivity.class);
                        groupIntent.putExtra("TabType", "group");
                        startActivity(groupIntent);
                    } else {
                        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(
                                this);
                        groupMembershipStatus.checkMembershipStatus(groupId,
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                                        .getDynamoId());
                    }
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                                        .getDynamoId());
                        jsonObject.put("screenName", "" + "DetailArticleScreen");
                        jsonObject.put("Topic", "" + "ArticleDetail");
                        mixpanel.track("JoinSupportGroupBannerClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case R.id.writeArticleTextView:
                case R.id.writeArticleImageView:
                    ((ArticleDetailsContainerActivity) getActivity()).checkAudioPlaying();
                    Intent intentt = new Intent(getActivity(), CampaignContainerActivity.class);
                    startActivity(intentt);
                    break;
                case R.id.viewAllTagsTextView:
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(AppUtils.dpTopx(10), 0, AppUtils.dpTopx(10), 0);
                    tagsLayout.setLayoutParams(params);
                    viewAllTagsTextView.setVisibility(View.GONE);
                    break;
                case R.id.txvCommentCellEdit: {
                    CommentsData commentsData = (CommentsData) ((View) v.getParent().getParent()
                            .getParent()).getTag();
                    openCommentDialog(commentsData, "EDIT");
                }
                break;
                case R.id.txvReplyCellEdit: {
                    CommentsData commentsData = (CommentsData) ((View) v.getParent().getParent()).getTag();
                    openCommentDialog(commentsData, "EDIT");
                }
                break;
                case R.id.bookmarkTextView:
                    addRemoveBookmark();
                    break;
                case R.id.txvCommentTitle:
                case R.id.commentorImageView:
                    CommentsData commentData = (CommentsData) ((View) v.getParent().getParent()).getTag();
                    if (!"fb".equals(commentData.getComment_type())) {
                        Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
                        profileIntent.putExtra(Constants.USER_ID, commentData.getUserId());
                        startActivity(profileIntent);
                    }
                    break;
                case R.id.txvReplyTitle:
                case R.id.replierImageView:
                    CommentsData replyCommentData = (CommentsData) ((View) v.getParent()).getTag();
                    if (!"fb".equals(replyCommentData.getComment_type())) {
                        Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
                        profileIntent.putExtra(Constants.USER_ID, replyCommentData.getUserId());
                        startActivity(profileIntent);
                    }
                    break;
                case R.id.follow_click:
                case R.id.followText:
                    followApiCall();
                    break;
                case R.id.user_image:
                case R.id.user_name:
                    Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
                    profileIntent.putExtra(Constants.USER_ID, detailData.getUserId());
                    startActivity(profileIntent);
                    break;
                case R.id.relatedArticles1: {
                    if (recentAuthorArticleHeading.getText() != null && recentAuthorArticleHeading
                            .getText().toString().contains("RELATED")) {
                        launchRelatedTrendingArticle(v, "articleDetailsRelatedList", 1);
                    } else {
                        launchRelatedTrendingArticle(v, "articleDetailsAuthorsRecentList", 1);
                    }
                    break;
                }
                case R.id.relatedArticles2: {
                    if (recentAuthorArticleHeading.getText() != null && recentAuthorArticleHeading
                            .getText().toString().contains("RELATED")) {
                        launchRelatedTrendingArticle(v, "articleDetailsRelatedList", 2);
                    } else {
                        launchRelatedTrendingArticle(v, "articleDetailsAuthorsRecentList", 2);
                    }
                    break;
                }
                case R.id.relatedArticles3: {
                    if (recentAuthorArticleHeading.getText() != null && recentAuthorArticleHeading
                            .getText().toString().contains("RELATED")) {
                        launchRelatedTrendingArticle(v, "articleDetailsRelatedList", 3);
                    } else {
                        launchRelatedTrendingArticle(v, "articleDetailsAuthorsRecentList", 3);
                    }
                    break;
                }
                case R.id.trendingRelatedArticles1: {
                    launchRelatedTrendingArticle(v, "articleDetailsTrendingList", 1);
                    break;
                }
                case R.id.trendingRelatedArticles2: {
                    launchRelatedTrendingArticle(v, "articleDetailsTrendingList", 2);
                    break;
                }
                case R.id.trendingRelatedArticles3: {
                    launchRelatedTrendingArticle(v, "articleDetailsTrendingList", 3);
                    break;
                }
                case R.layout.related_tags_view: {
                    String categoryId = (String) v.getTag();
                    Intent intent = new Intent(getActivity(),
                            FilteredTopicsArticleListingActivity.class);
                    intent.putExtra("selectedTopics", categoryId);
                    intent.putExtra("displayName",
                            ((TextView) ((LinearLayout) v).getChildAt(0)).getText());
                    startActivity(intent);
                    break;
                }
                case R.id.viewCommentsTextView:
                    openViewCommentDialog();
                    ((ArticleDetailsContainerActivity) getActivity()).checkAudioPlaying();

                    break;
                case R.id.likeTextView: {
                    if (recommendStatus == 0) {
                        recommendStatus = 1;
                        if (!isFollowing && !userDynamoId.equals(articleId)) {
                            setValuesInFollowPopUp();
                        }
                        Drawable top = ContextCompat
                                .getDrawable(getActivity(), R.drawable.ic_recommended);
                        likeArticleTextView
                                .setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                        recommendUnrecommendArticleApi("1");
                        Utils.pushLikeArticleEvent(getActivity(), "DetailArticleScreen",
                                userDynamoId + "", articleId, authorId + "~" + author);
                    } else {
                        recommendStatus = 0;
                        Drawable top = ContextCompat
                                .getDrawable(getActivity(), R.drawable.ic_recommend);
                        likeArticleTextView
                                .setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                        recommendUnrecommendArticleApi("0");
                        Utils.pushUnlikeArticleEvent(getActivity(), "DetailArticleScreen",
                                userDynamoId + "", articleId, authorId + "~" + author);
                    }
                    break;
                }
                case R.id.facebookShareTextView:
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(shareUrl))
                                .build();
                        if (getActivity() != null) {
                            new ShareDialog(getActivity()).show(content);
                        }
                    }
                    Utils.pushShareArticleEvent(getActivity(), "DetailArticleScreen",
                            userDynamoId + "", articleId, authorId + "~" + author, "Facebook");
                    break;
                case R.id.whatsappShareTextView:
                    if (StringUtils.isNullOrEmpty(shareUrl)) {
                        Toast.makeText(getActivity(), "Unable to share with whatsapp.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT,
                                AppUtils.stripHtml("" + detailData.getExcerpt()) + "\n\n"
                                        + BaseApplication.getAppContext()
                                        .getString(R.string.ad_share_follow_author, author) + "\n"
                                        + shareUrl);
                        try {
                            startActivity(whatsappIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getActivity(), "Whatsapp have not been installed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        Utils.pushShareArticleEvent(getActivity(), "DetailArticleScreen",
                                userDynamoId + "", articleId, authorId + "~" + author, "Whatsapp");
                    }
                    break;
                case R.id.emailShareTextView:
                    try {
                        AddCollectionAndCollectionItemDialogFragment addCollectionAndCollectionitemDialogFragment =
                                new AddCollectionAndCollectionItemDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("articleId", articleId);
                        bundle.putString("type", AppConstants.ARTICLE_COLLECTION_TYPE);
                        addCollectionAndCollectionitemDialogFragment.setArguments(bundle);
                        FragmentManager fm = getFragmentManager();
                        addCollectionAndCollectionitemDialogFragment.setTargetFragment(this, 0);
                        addCollectionAndCollectionitemDialogFragment.show(fm, "collectionAdd");
                        Utils.pushProfileEvents(getActivity(), "CTA_Article_Add_To_Collection",
                                "ArticleDetailsFragment", "Add to Collection", "-");
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                    /*  Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, AppUtils.stripHtml("" + detailData.getExcerpt()) + "\n\n"
                            + BaseApplication.getAppContext()
                            .getString(R.string.ad_share_follow_author, author) + "\n"
                            + shareUrl);
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);*/
                    break;
                case R.id.cancelFollowPopUp:
                    followPopUpBottomContainer.setVisibility(View.GONE);
                default:
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
            Bundle args = new Bundle();
            args.putString(Constants.ARTICLE_ID, articleId);
            args.putString(Constants.AUTHOR, authorId + "~" + author);
            args.putString("opType", opType);
            if (comData != null) {
                args.putParcelable("commentData", comData);
            }
            commentFrag.setArguments(args);
            ((ArticleDetailsContainerActivity) getActivity()).hideToolbarPerm();
            ((ArticleDetailsContainerActivity) getActivity())
                    .addFragment(commentFrag, null, "topToBottom");
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void openViewCommentDialog() {
        try {
            long createdTime = Long.parseLong(detailData.getCreated());
            ViewAllCommentsFragment commentFrag = new ViewAllCommentsFragment();
            commentFrag.setTargetFragment(this, 0);
            Bundle args = new Bundle();
            args.putString("mycityCommentURL", commentMainUrl);
            if (createdTime < AppConstants.MYCITY_TO_MOMSPRESSO_SWITCH_TIME) {
                args.putString("fbCommentURL",
                        shareUrl.replace("www.momspresso.com", "www.mycity4kids.com"));
            } else {
                args.putString("fbCommentURL", shareUrl);
            }
            args.putString(Constants.ARTICLE_ID, articleId);
            args.putString(Constants.AUTHOR, authorId + "~" + author);
            args.putString(Constants.BLOG_SLUG, detailData.getBlogTitleSlug());
            args.putString(Constants.TITLE_SLUG, detailData.getTitleSlug());
            args.putString("userType", detailData.getUserType());
            commentFrag.setArguments(args);
            ((ArticleDetailsContainerActivity) getActivity()).hideToolbarPerm();
            ((ArticleDetailsContainerActivity) getActivity())
                    .addFragment(commentFrag, null, "topToBottom");
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            if (isAdded()) {
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.unable_to_load_comment));
            }
        }
    }

    private void launchRelatedTrendingArticle(View v, String listingType, int index) {
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
        View view = observableScrollView.getChildAt(observableScrollView.getChildCount() - 1);
        View tagsView = observableScrollView.findViewById(R.id.tagsLayoutContainer);
        Rect scrollBounds = new Rect();
        observableScrollView.getHitRect(scrollBounds);
        int diff = (view.getBottom() - (observableScrollView.getHeight() + observableScrollView.getScrollY()));
        int permanentDiff = (tagsView.getBottom() - (observableScrollView.getHeight() + observableScrollView
                .getScrollY()));
        if (permanentDiff <= 0) {
            if (bottomToolbarLL.getVisibility() == View.VISIBLE) {
                hideBottomToolbar();
            }
            if (isSwipeNextAvailable) {
                swipeNextTextView.setVisibility(View.VISIBLE);
            }
            if (isArticleDetailLoaded) {
                ((ArticleDetailsContainerActivity) getActivity())
                        .addArticleForImpression(articleId);
            }
            if (impressionList != null && !impressionList.isEmpty()) {
                for (ArticleListingResult result : impressionList) {
                    ((ArticleDetailsContainerActivity) getActivity())
                            .addArticleForImpression(result.getTitle());
                }
            }
        } else {
            if (bottomToolbarLL.getVisibility() != View.VISIBLE) {
                showBottomToolbar();
            }
            if (isSwipeNextAvailable) {
                swipeNextTextView.setVisibility(View.GONE);
            }
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
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ((ArticleDetailsContainerActivity) getActivity()).showMainToolbar();
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

    public String getGtmArticleId() {
        return articleId;
    }

    public String getGtmAuthor() {
        return authorId + "~" + author;
    }

    public String getGtmLanguage() {
        return gtmLanguage;
    }

    @Override
    public void onGroupMappingResult(int groupId, String gpHeading, String gpSubHeading,
            String gpImageUrl) {
        this.groupId = groupId;
        try {
            Picasso.get().load(gpImageUrl).placeholder(R.drawable.groups_generic)
                    .error(R.drawable.groups_generic).into(groupHeaderImageView);
        } catch (Exception e) {
            groupHeaderImageView.setImageResource(R.drawable.groups_generic);
        }
        if (StringUtils.isNullOrEmpty(gpHeading)) {
            groupHeadingTextView.setText(
                    BaseApplication.getAppContext().getString(R.string.groups_join_support_gp));
        } else {
            groupHeadingTextView.setText(gpHeading);
        }
        if (StringUtils.isNullOrEmpty(gpSubHeading)) {
            groupSubHeadingTextView
                    .setText(BaseApplication.getAppContext().getString(R.string.groups_not_alone));
        } else {
            groupSubHeadingTextView.setText(gpSubHeading);
        }
    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        String userType = null;
        if (isAdded()) {
            if (body.getData().getResult() != null && !body.getData().getResult().isEmpty()) {
                if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                    userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
                } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                    userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
                }
            }

            if (!AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(userType)
                    && !AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(userType)) {
                if ("male".equalsIgnoreCase(
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                                .getGender())
                        || "m".equalsIgnoreCase(
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                                .getGender())) {
                    if (isAdded()) {
                        Toast.makeText(getActivity(), getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                    }
                    if (!BuildConfig.DEBUG && !AppConstants.DEBUGGING_USER_ID.contains(
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                                    .getDynamoId())) {
                        return;
                    }
                }
            }

            if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
                Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
                startActivity(intent);
            } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED
                    .equals(body.getData().getResult().get(0).getStatus())) {
                Toast.makeText(getActivity(), getString(R.string.groups_user_blocked_msg),
                        Toast.LENGTH_SHORT).show();
            } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER
                    .equals(body.getData().getResult().get(0).getStatus())) {
                Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
                startActivity(intent);
            } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION
                    .equals(body.getData().getResult().get(0).getStatus())) {
                Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("pendingMembershipFlag", true);
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

    }

    private void followApiCall() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followApi = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowee_id(authorId);
        if (isFollowing) {
            isFollowing = false;
            followClick.setText(getString(R.string.ad_follow_author));
            Utils.pushGenericEvent(getActivity(), "CTA_Unfollow_Article_Detail", userDynamoId,
                    "ArticleDetailsFragment");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followApi
                    .unfollowUserV2(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            if (followPopUpBottomContainer.getVisibility() == View.VISIBLE) {
                followPopUpBottomContainer.setVisibility(View.GONE);
            }
            isFollowing = true;
            followClick.setText(getString(R.string.ad_following_author));
            Utils.pushGenericEvent(getActivity(), "CTA_Follow_Article_Detail", userDynamoId,
                    "ArticleDetailsFragment");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followApi
                    .followUserV2(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }
    }

    private void addRemoveBookmark() {
        if (bookmarkStatus == 0) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(articleId);
            bookmarkStatus = 1;
            Drawable top = ContextCompat.getDrawable(bookmarkArticleTextView.getContext(), R.drawable.ic_bookmarked);
            bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            if ("1".equals(isMomspresso)) {
                Call<AddBookmarkResponse> call = articleDetailsApi
                        .addVideoWatchLater(articleDetailRequest);
                call.enqueue(addBookmarkResponseCallback);
                Utils.pushWatchLaterArticleEvent(getActivity(), "DetailArticleScreen",
                        userDynamoId + "", articleId, authorId + "~" + author);
            } else {
                Call<AddBookmarkResponse> call = articleDetailsApi
                        .addBookmark(articleDetailRequest);
                call.enqueue(addBookmarkResponseCallback);
                Utils.pushBookmarkArticleEvent(getActivity(), "DetailArticleScreen",
                        userDynamoId + "", articleId, authorId + "~" + author);
            }
        } else {
            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
            deleteBookmarkRequest.setId(bookmarkId);
            bookmarkStatus = 0;
            Drawable top = ContextCompat.getDrawable(bookmarkArticleTextView.getContext(), R.drawable.ic_bookmark);
            bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            if ("1".equals(isMomspresso)) {
                Call<AddBookmarkResponse> call = articleDetailsApi
                        .deleteVideoWatchLater(deleteBookmarkRequest);
                call.enqueue(addBookmarkResponseCallback);
                Utils.pushRemoveWatchLaterArticleEvent(getActivity(), "DetailArticleScreen",
                        userDynamoId + "", articleId, authorId + "~" + author);
            } else {
                Call<AddBookmarkResponse> call = articleDetailsApi
                        .deleteBookmark(deleteBookmarkRequest);
                call.enqueue(addBookmarkResponseCallback);
                Utils.pushUnbookmarkArticleEvent(getActivity(), "DetailArticleScreen",
                        userDynamoId + "", articleId, authorId + "~" + author);
            }
        }
    }

    private void checkingForSponsored() {
        if (responseData.getIsSponsored().equalsIgnoreCase("1")) {
            boolean flag = false;
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = BaseApplication.getAppContext()
                        .openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory())
                        .create();
                TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                for (int i = 0; i < res.getData().size(); i++) {
                    shortStoriesTopicList.add(res.getData().get(i));
                }

                ArrayList<Topics> topicLocalList = new ArrayList<>();
                for (Topics topic : shortStoriesTopicList) {
                    if (topic.getSlug() != null && topic.getSlug()
                            .equalsIgnoreCase("sponsored-stories")) {
                        topicLocalList = topic.getChild();
                        break;
                    }
                }

                for (Topics topic : topicLocalList) {
                    for (Map<String, String> var : responseData.getTags()) {
                        if (var.size() > 0) {
                            for (Map.Entry<String, String> entry : var.entrySet()) {
                                if (topic.getId().equalsIgnoreCase(entry.getKey())) {
                                    if (topic.getExtraData() != null
                                            && topic.getExtraData().size() != 0
                                            && topic.getExtraData().get(0).getCategoryTag() != null) {
                                        if (topic.getExtraData().get(0).getCategoryTag()
                                                .getCategoryImage() != null
                                                && !topic.getExtraData().get(0).getCategoryTag()
                                                .getCategoryImage().isEmpty()) {
                                            sponsoredViewContainer.setVisibility(View.VISIBLE);
                                            Picasso.get().load(topic.getExtraData().get(0)
                                                    .getCategoryTag().getCategoryImage())
                                                    .into(sponsoredImage);
                                            sponsoredTextView
                                                    .setText("this story is sponsored by ");
                                        } else {
                                            sponsoredViewContainer.setVisibility(View.GONE);
                                        }

                                        if (topic.getExtraData().get(0).getCategoryTag()
                                                .getCategoryBadge() != null && !topic.getExtraData()
                                                .get(0).getCategoryTag().getCategoryBadge()
                                                .isEmpty()) {
                                            badge.setVisibility(View.VISIBLE);
                                            Picasso.get().load(topic.getExtraData().get(0)
                                                    .getCategoryTag().getCategoryBadge())
                                                    .into(badge);
                                        } else {
                                            badge.setVisibility(View.GONE);
                                        }
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Callback<ArticleDetailResult> articleDetailResponseCallbackRedis = new Callback<ArticleDetailResult>() {
        @Override
        public void onResponse(Call<ArticleDetailResult> call,
                retrofit2.Response<ArticleDetailResult> response) {
            removeProgressDialog();
            if (response.body() == null) {
                getArticleDetailsWebserviceApi();
                return;
            }
            try {
                removeProgressDialog();
                responseData = response.body();
                getResponseUpdateUi(responseData);
                checkingForSponsored();
                authorId = detailData.getUserId();
                isArticleDetailLoaded = true;
                hitBookmarkFollowingStatusApi();
                if ("1".equals(isMomspresso)) {
                    hitBookmarkVideoStatusApi();
                }
                hitRelatedArticleApi();
                commentUrl = responseData.getCommentsUri();
                commentMainUrl = responseData.getCommentsUri();

                if (StringUtils.isNullOrEmpty(commentUrl) || !commentUrl.contains("http")) {
                    commentUrl = "http";
                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                getArticleDetailsWebserviceApi();
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailResult> call, Throwable t) {
            removeProgressDialog();
            handleExceptions(t);
            getArticleDetailsWebserviceApi();
        }
    };

    private Callback<ViewCountResponse> getViewCountResponseCallback = new Callback<ViewCountResponse>() {
        @Override
        public void onResponse(Call<ViewCountResponse> call,
                retrofit2.Response<ViewCountResponse> response) {
            if (response.body() == null) {
                return;
            }
            try {
                ViewCountResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS
                        .equals(responseData.getStatus())) {
                    articleViewCountTextView.setText(responseData.getData().get(0).getCount());
                    articleCommentCountTextView
                            .setText(responseData.getData().get(0).getCommentCount());
                    articleRecommendationCountTextView
                            .setText(responseData.getData().get(0).getLikeCount());
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

    private Callback<CrownDataResponse> getCrownDataResponse = new Callback<CrownDataResponse>() {
        @Override
        public void onResponse(Call<CrownDataResponse> call,
                retrofit2.Response<CrownDataResponse> response) {
            if (response.body() == null) {
                return;
            }
            try {
                CrownDataResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS
                        .equals(responseData.getStatus())) {
                    setCrownData(responseData.getData().getResult().getImage_url());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CrownDataResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void setCrownData(String imageUrl) {
        if (!StringUtils.isNullOrEmpty(imageUrl)) {
            crownImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUrl).placeholder(R.drawable.default_article).fit().into(crownImageView);
        }
    }

    private void getArticleDetailsWebserviceApi() {
        Call<ArticleDetailWebserviceResponse> call = articleDetailsApi
                .getArticleDetailsFromWebservice(articleId);
        call.enqueue(articleDetailResponseCallbackWebservice);
    }

    private Callback<ArticleDetailWebserviceResponse> articleDetailResponseCallbackWebservice =
            new Callback<ArticleDetailWebserviceResponse>() {
                @Override
                public void onResponse(Call<ArticleDetailWebserviceResponse> call,
                        retrofit2.Response<ArticleDetailWebserviceResponse> response) {
                    removeProgressDialog();
                    if (response.body() == null) {
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
                        return;
                    }
                    try {
                        ArticleDetailWebserviceResponse responseData = response.body();
                        if (responseData.getCode() == 200 && Constants.SUCCESS
                                .equals(responseData.getStatus())) {
                            getResponseUpdateUi(responseData.getData());
                            authorId = detailData.getUserId();
                            hitBookmarkFollowingStatusApi();
                            hitRelatedArticleApi();
                            commentUrl = responseData.getData().getCommentsUri();
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

    private Callback<ArticleListingResponse> vernacularTrendingResponseCallback =
            new Callback<ArticleListingResponse>() {
                @Override
                public void onResponse(Call<ArticleListingResponse> call,
                        retrofit2.Response<ArticleListingResponse> response) {
                    if (loadingView.getVisibility() == View.VISIBLE) {
                        loadingView.setVisibility(View.GONE);
                    }
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(
                                "Trending Article API failure");
                        Crashlytics.logException(nee);
                        return;
                    }

                    try {
                        ArticleListingResponse responseData = response.body();
                        if (responseData.getCode() == 200 && Constants.SUCCESS
                                .equals(responseData.getStatus())) {
                            ArrayList<ArticleListingResult> dataList = responseData.getData().get(0)
                                    .getResult();
                            if (dataList != null) {
                                for (int i = 0; i < dataList.size(); i++) {
                                    if (dataList.get(i).getId().equals(articleId)) {
                                        dataList.remove(i);
                                        break;
                                    }
                                }
                            }
                            if (dataList != null && dataList.size() != 0) {
                                trendingArticles.setVisibility(View.VISIBLE);
                                impressionList.addAll(dataList);
                                Collections.shuffle(dataList);
                                if (dataList.size() >= 3) {
                                    Picasso.get().load(dataList.get(0).getImageUrl().getThumbMin())
                                            .placeholder(R.drawable.default_article).fit()
                                            .into(trendingRelatedArticles1.getArticleImageView());
                                    trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                                    trendingRelatedArticles1.setTag(dataList);
                                    Picasso.get().load(dataList.get(1).getImageUrl().getThumbMin())
                                            .placeholder(R.drawable.default_article).fit()
                                            .into(trendingRelatedArticles2.getArticleImageView());
                                    trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                                    trendingRelatedArticles2.setTag(dataList);
                                    Picasso.get().load(dataList.get(2).getImageUrl().getThumbMin())
                                            .placeholder(R.drawable.default_article).fit()
                                            .into(trendingRelatedArticles3.getArticleImageView());
                                    trendingRelatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                                    trendingRelatedArticles3.setTag(dataList);
                                } else if (dataList.size() == 2) {
                                    Picasso.get().load(dataList.get(0).getImageUrl().getThumbMin())
                                            .placeholder(R.drawable.default_article).fit()
                                            .into(trendingRelatedArticles1.getArticleImageView());
                                    trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                                    trendingRelatedArticles1.setTag(dataList);
                                    Picasso.get().load(dataList.get(1).getImageUrl().getThumbMin())
                                            .placeholder(R.drawable.default_article).fit()
                                            .into(trendingRelatedArticles2.getArticleImageView());
                                    trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                                    trendingRelatedArticles2.setTag(dataList);
                                    trendingRelatedArticles3.setVisibility(View.GONE);
                                } else if (dataList.size() == 1) {
                                    Picasso.get().load(dataList.get(0).getImageUrl().getThumbMin())
                                            .placeholder(R.drawable.default_article).fit()
                                            .into(trendingRelatedArticles1.getArticleImageView());
                                    trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                                    trendingRelatedArticles1.setTag(dataList);
                                    trendingRelatedArticles2.setVisibility(View.GONE);
                                    trendingRelatedArticles3.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            NetworkErrorException nee = new NetworkErrorException(
                                    "Trending Article Error Response");
                            Crashlytics.logException(nee);
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
                    handleExceptions(t);
                }
            };

    private Callback<ArticleListingResponse> categoryArticleResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call,
                retrofit2.Response<ArticleListingResponse> response) {
            if (loadingView.getVisibility() == View.VISIBLE) {
                loadingView.setVisibility(View.GONE);
            }
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(
                        "Category related Article API failure");
                Crashlytics.logException(nee);
                Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsApi
                        .getPublishedArticles(authorId, 0, 1, 4);
                callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
                return;
            }

            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS
                        .equals(responseData.getStatus())) {
                    ArrayList<ArticleListingResult> dataList = responseData.getData().get(0)
                            .getResult();
                    if (dataList != null) {
                        for (int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).getId().equals(articleId)) {
                                dataList.remove(i);
                                break;
                            }
                        }
                    }
                    if (dataList.size() == 0) {
                        Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsApi
                                .getPublishedArticles(authorId, 0, 1, 4);
                        callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
                    } else {
                        recentAuthorArticleHeading.setText(getString(R.string.recent_article));
                        recentAuthorArticles.setVisibility(View.VISIBLE);
                        Collections.shuffle(dataList);
                        impressionList.addAll(dataList);
                        swipeRelated.onRelatedSwipe(dataList);
                        if (dataList.size() >= 3) {
                            Picasso.get().load(dataList.get(0).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(new ArrayList<>(
                                    dataList.subList(0, 3)));
                            Picasso.get().load(dataList.get(1).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(new ArrayList<>(
                                    dataList.subList(0, 3)));
                            Picasso.get().load(dataList.get(2).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles3.getArticleImageView());
                            relatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            relatedArticles3.setTag(new ArrayList<>(
                                    dataList.subList(0, 3)));
                        } else if (dataList.size() == 2) {
                            Picasso.get().load(dataList.get(0).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList);
                            Picasso.get().load(dataList.get(1).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList);
                            relatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            Picasso.get().load(dataList.get(0).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList);
                            relatedArticles2.setVisibility(View.GONE);
                            relatedArticles3.setVisibility(View.GONE);
                        }
                    }
                } else {
                    NetworkErrorException nee = new NetworkErrorException(
                            "Category related Article Error Response");
                    Crashlytics.logException(nee);
                    Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsApi
                            .getPublishedArticles(authorId, 0, 1, 4);
                    callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsApi
                        .getPublishedArticles(authorId, 0, 1, 4);
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
        public void onResponse(Call<ArticleListingResponse> call,
                retrofit2.Response<ArticleListingResponse> response) {
            if (loadingView.getVisibility() == View.VISIBLE) {
                loadingView.setVisibility(View.GONE);
            }
            if (response.body() == null) {
                if (isAdded()) {
                    ((ArticleDetailsContainerActivity) getActivity())
                            .showToast(getString(R.string.server_went_wrong));
                }
                return;
            }

            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS
                        .equals(responseData.getStatus())) {
                    ArrayList<ArticleListingResult> dataList = responseData.getData().get(0)
                            .getResult();
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getId().equals(articleId)) {
                            dataList.remove(i);
                            break;
                        }
                    }
                    if (dataList.size() == 0) {
                        relatedTrendingSeparator.setVisibility(View.GONE);
                    } else {
                        impressionList.addAll(dataList);
                        Collections.shuffle(dataList);
                        recentAuthorArticleHeading
                                .setText(getString(R.string.ad_recent_logs_from_title));
                        recentAuthorArticles.setVisibility(View.VISIBLE);
                        if (dataList.size() >= 3) {
                            Picasso.get().load(dataList.get(0).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList);
                            Picasso.get().load(dataList.get(1).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList);
                            Picasso.get().load(dataList.get(2).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles3.getArticleImageView());
                            relatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            relatedArticles3.setTag(dataList);
                        } else if (dataList.size() == 2) {
                            Picasso.get().load(dataList.get(0).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList);
                            Picasso.get().load(dataList.get(1).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList);
                            relatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            Picasso.get().load(dataList.get(0).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList);
                            relatedArticles2.setVisibility(View.GONE);
                            relatedArticles3.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (isAdded()) {
                        ((ArticleDetailsContainerActivity) getActivity())
                                .showToast(getString(R.string.server_went_wrong));
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded()) {
                    ((ArticleDetailsContainerActivity) getActivity())
                            .showToast(getString(R.string.went_wrong));
                }
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<FollowUnfollowCategoriesResponse> getFollowedTopicsResponseCallback =
            new Callback<FollowUnfollowCategoriesResponse>() {
                @Override
                public void onResponse(Call<FollowUnfollowCategoriesResponse> call,
                        retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
                    if (null == response.body()) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        Crashlytics.logException(nee);
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
                        return;
                    }
                    try {
                        FollowUnfollowCategoriesResponse responseData = response.body();
                        if (responseData.getCode() == 200 && Constants.SUCCESS
                                .equals(responseData.getStatus())) {
                            final ArrayList<String> previouslyFollowedTopics = (ArrayList<String>) responseData
                                    .getData();
                            final ArrayList<Map<String, String>> tagsList = detailData.getTags();
                            final ArrayList<String> sponsoredList = new ArrayList<>();
                            try {
                                createSponsporedTagsList(sponsoredList);
                                createArticleTags(previouslyFollowedTopics, tagsList, sponsoredList);
                            } catch (FileNotFoundException ffe) {
                                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                                final TopicsCategoryAPI topicsApi = retro.create(TopicsCategoryAPI.class);
                                Call<ResponseBody> caller = topicsApi.downloadTopicsJSON();
                                caller.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call,
                                            retrofit2.Response<ResponseBody> response) {
                                        AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(),
                                                AppConstants.CATEGORIES_JSON_FILE, response.body());
                                        try {
                                            createSponsporedTagsList(sponsoredList);
                                            createArticleTags(previouslyFollowedTopics, tagsList,
                                                    sponsoredList);
                                        } catch (FileNotFoundException e) {
                                            Crashlytics.logException(e);
                                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                                        } catch (Exception e) {
                                            Crashlytics.logException(e);
                                            Log.d("MC4KException", Log.getStackTraceString(e));
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Crashlytics.logException(t);
                                        Log.d("MC4KException", Log.getStackTraceString(t));
                                    }
                                });
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                                Log.d("MC4KException", Log.getStackTraceString(e));
                            }
                        } else {
                            if (isAdded()) {
                                ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
                            }
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

    private void createSponsporedTagsList(ArrayList<String> sponsoredList) throws
            FileNotFoundException {
        FileInputStream fileInputStream = BaseApplication.getAppContext()
                .openFileInput(AppConstants.CATEGORIES_JSON_FILE);
        String fileContent = AppUtils.convertStreamToString(fileInputStream);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory())
                .create();
        TopicsResponse topicsResponse = gson.fromJson(fileContent, TopicsResponse.class);
        for (int i = 0; i < topicsResponse.getData().size(); i++) {
            if (AppConstants.SPONSORED_CATEGORYID.equals(topicsResponse.getData().get(i).getId())) {
                for (int j = 0; j < topicsResponse.getData().get(i).getChild().size(); j++) {
                    for (int k = 0; k < topicsResponse.getData().get(i).getChild().get(j).getChild().size();
                            k++) {
                        sponsoredList.add(topicsResponse.getData().get(i).getChild().get(j).getChild().get(j)
                                .getId());
                    }
                    sponsoredList.add(topicsResponse.getData().get(i).getChild().get(j).getId());
                }
            }
        }
    }

    private void createArticleTags(ArrayList<String> previouslyFollowedTopics, ArrayList<Map<String, String>> tagsList,
            ArrayList<String> sponsoredList) {
        int relatedImageWidth = (int) BaseApplication.getAppContext().getResources()
                .getDimension(R.dimen.related_article_article_image_width);
        viewAllTagsTextView.setVisibility(View.GONE);
        width = width - ((RelativeLayout.LayoutParams) tagsLayout.getLayoutParams()).leftMargin
                - ((RelativeLayout.LayoutParams) tagsLayout.getLayoutParams()).rightMargin;

        for (int i = 0; i < tagsList.size(); i++) {
            try {
                for (Map.Entry<String, String> entry : tagsList.get(i).entrySet()) {
                    String key = entry.getKey();
                    final String value = entry.getValue();
                    if (!key.startsWith("category-") || StringUtils.isNullOrEmpty(value)) {
                        continue;
                    }

                    final RelativeLayout topicView = (RelativeLayout) layoutInflater
                            .inflate(R.layout.related_tags_view, null, false);
                    topicView.setClickable(true);
                    topicView.getChildAt(0).setTag(key);
                    topicView.getChildAt(2).setTag(key);
                    ((TextView) topicView.getChildAt(0)).setText(value.toUpperCase());
                    ((TextView) topicView.getChildAt(0)).measure(0, 0);
                    width = width - ((TextView) topicView.getChildAt(0)).getMeasuredWidth() - AppUtils
                            .dpTopx(1)
                            - relatedImageWidth - topicView.getPaddingLeft() - topicView
                            .getPaddingRight();
                    if (width < 0) {
                        viewAllTagsTextView.setVisibility(View.VISIBLE);
                    }
                    if (sponsoredList.contains(key)) {
                        ((ImageView) topicView.getChildAt(2)).setVisibility(View.GONE);
                        ((View) topicView.getChildAt(1)).setVisibility(View.GONE);
                    }
                    if (null != previouslyFollowedTopics && previouslyFollowedTopics.contains(key)) {
                        ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat
                                .getDrawable(BaseApplication.getAppContext(), R.drawable.tick));
                        topicView.getChildAt(2).setBackgroundColor(ContextCompat
                                .getColor(BaseApplication.getAppContext(), R.color.app_red));
                        ((ImageView) topicView.getChildAt(2)).setColorFilter(ContextCompat
                                .getColor(BaseApplication.getAppContext(), R.color.white_color));
                        topicView.getChildAt(2).setOnClickListener(v -> followUnfollowTopics((String) v.getTag(),
                                (RelativeLayout) v.getParent(), 0));
                    } else {
                        ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat
                                .getDrawable(BaseApplication.getAppContext(), R.drawable.follow_plus));
                        topicView.getChildAt(2).setBackgroundColor(ContextCompat
                                .getColor(BaseApplication.getAppContext(), R.color.ad_tags_follow_bg));
                        ((ImageView) topicView.getChildAt(2)).setColorFilter(ContextCompat
                                .getColor(BaseApplication.getAppContext(), R.color.app_red));
                        topicView.getChildAt(2).setOnClickListener(v -> followUnfollowTopics((String) v.getTag(),
                                (RelativeLayout) v.getParent(), 1));
                    }

                    topicView.getChildAt(0).setOnClickListener(v -> {
                        String categoryId = (String) v.getTag();
                        Intent intent = new Intent(getActivity(),
                                FilteredTopicsArticleListingActivity.class);
                        intent.putExtra("selectedTopics", categoryId);
                        intent.putExtra("displayName", value);
                        startActivity(intent);
                    });
                    tagsLayout.addView(topicView);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }
        loadGroupDataForCategories(tagsList);
    }

    private void loadGroupDataForCategories(ArrayList<Map<String, String>> tagsList) {
        ArrayList<String> categoriesList = new ArrayList<>();
        for (int i = 0; i < tagsList.size(); i++) {
            categoriesList.addAll(tagsList.get(i).keySet());
        }
        if (categoriesList.size() == 1) {
            GroupIdCategoryMap groupIdCategoryMap = new GroupIdCategoryMap(categoriesList.get(0),
                    this, "details");
            groupIdCategoryMap.getGroupIdForCurrentCategory();
        } else {
            GroupIdCategoryMap groupIdCategoryMap = new GroupIdCategoryMap(categoriesList, this,
                    "details");
            groupIdCategoryMap.getGroupIdForMultipleCategories();
        }
    }

    private void followUnfollowTopics(String selectedTopic, RelativeLayout tagView, int action) {
        FollowUnfollowCategoriesRequest followUnfollowCategoriesRequest = new FollowUnfollowCategoriesRequest();
        ArrayList<String> topicIdLList = new ArrayList<>();
        topicIdLList.add(selectedTopic);
        followUnfollowCategoriesRequest.setCategories(topicIdLList);
        if (action == 0) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                                .getDynamoId());
                jsonObject.put("Topic",
                        "" + selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText()
                                .toString());
                jsonObject.put("ScreenName", "DetailArticleScreen");
                jsonObject.put("isFirstTimeUser", followTopicChangeNewUser);
                Log.d("UnfollowTopics", jsonObject.toString());
                mixpanel.track("UnfollowTopic", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Utils.pushUnfollowTopicEvent(getActivity(), "DetailArticleScreen", userDynamoId,
                    selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
            tagView.getChildAt(0).setTag(selectedTopic);
            tagView.getChildAt(2).setTag(selectedTopic);
            ((ImageView) tagView.getChildAt(2)).setImageDrawable(
                    ContextCompat.getDrawable(getActivity(), R.drawable.follow_plus));
            tagView.getChildAt(2).setBackgroundColor(
                    ContextCompat.getColor(getActivity(), R.color.ad_tags_follow_bg));
            ((ImageView) tagView.getChildAt(2))
                    .setColorFilter(ContextCompat.getColor(getActivity(), R.color.app_red));
            tagView.getChildAt(2).setOnClickListener(
                    v -> followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 1));
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                                .getDynamoId());
                jsonObject.put("Topic",
                        "" + selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText()
                                .toString());
                jsonObject.put("ScreenName", "DetailArticleScreen");
                jsonObject.put("isFirstTimeUser", followTopicChangeNewUser);
                Log.d("FollowTopics", jsonObject.toString());
                mixpanel.track("FollowTopic", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Utils.pushFollowTopicEvent(getActivity(), "DetailArticleScreen", userDynamoId,
                    selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
            tagView.getChildAt(0).setTag(selectedTopic);
            tagView.getChildAt(2).setTag(selectedTopic);
            ((ImageView) tagView.getChildAt(2))
                    .setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.tick));
            tagView.getChildAt(2)
                    .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_red));
            ((ImageView) tagView.getChildAt(2))
                    .setColorFilter(ContextCompat.getColor(getActivity(), R.color.white_color));
            tagView.getChildAt(2).setOnClickListener(
                    v -> followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 0));
        }
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryApi = retro.create(TopicsCategoryAPI.class);
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryApi
                .followCategories(userDynamoId, followUnfollowCategoriesRequest);
        call.enqueue(followUnfollowCategoriesResponseCallback);
    }

    private Callback<FollowUnfollowCategoriesResponse> followUnfollowCategoriesResponseCallback =
            new Callback<FollowUnfollowCategoriesResponse>() {
                @Override
                public void onResponse(Call<FollowUnfollowCategoriesResponse> call,
                        retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
                    removeProgressDialog();
                    if (null == response.body()) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        Crashlytics.logException(nee);
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
                        return;
                    }
                    try {
                        FollowUnfollowCategoriesResponse responseData = response.body();
                        if (responseData.getCode() == 200 && Constants.SUCCESS
                                .equals(responseData.getStatus())) {
                            if (isAdded()) {
                                ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
                            }
                        } else {
                            if (isAdded()) {
                                ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
                            }
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                        }
                    }
                }

                @Override
                public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
                    removeProgressDialog();
                    Crashlytics.logException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                    if (isAdded()) {
                        ((ArticleDetailsContainerActivity) getActivity())
                                .showToast(getString(R.string.went_wrong));
                    }
                }
            };

    private Callback<ArticleDetailResponse> isBookmarkedFollowedResponseCallback =
            new Callback<ArticleDetailResponse>() {
                @Override
                public void onResponse(Call<ArticleDetailResponse> call,
                        retrofit2.Response<ArticleDetailResponse> response) {
                    if (null == response.body()) {
                        if (!isAdded()) {
                            return;
                        }
                        ((ArticleDetailsContainerActivity) getActivity())
                                .showToast(getString(R.string.server_went_wrong));
                        return;
                    }

                    ArticleDetailResponse responseData = response.body();
                    if (responseData.getCode() == 200 && Constants.SUCCESS
                            .equals(responseData.getStatus())) {
                        if (!"1".equals(isMomspresso)) {
                            bookmarkFlag = responseData.getData().getResult().getBookmarkStatus();
                            if (!isAdded()) {
                                return;
                            }
                            if (!bookmarkFlag) {
                                bookmarkStatus = 0;
                                Drawable top = ContextCompat
                                        .getDrawable(getActivity(), R.drawable.ic_bookmark);
                                bookmarkArticleTextView
                                        .setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                            } else {
                                Drawable top = ContextCompat
                                        .getDrawable(getActivity(), R.drawable.ic_bookmarked);
                                bookmarkArticleTextView
                                        .setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                                bookmarkStatus = 1;
                            }
                            bookmarkId = responseData.getData().getResult().getBookmarkId();
                        }
                        if (userDynamoId.equals(authorId)) {
                            followClick.setVisibility(View.INVISIBLE);
                        } else {
                            if (!responseData.getData().getResult().getIsFollowed()) {
                                followClick.setEnabled(true);
                                followClick.setText(
                                        AppUtils.getString(getActivity(), R.string.ad_follow_author));
                                isFollowing = false;
                            } else {
                                followClick.setEnabled(true);
                                followClick.setText(
                                        AppUtils.getString(getActivity(), R.string.ad_following_author));
                                isFollowing = true;
                            }
                        }
                    } else {
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
                    handleExceptions(t);
                }
            };

    private Callback<ArticleDetailResponse> isBookmarkedVideoResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call,
                retrofit2.Response<ArticleDetailResponse> response) {
            if (null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((ArticleDetailsContainerActivity) getActivity())
                        .showToast(getString(R.string.server_went_wrong));
                return;
            }

            ArticleDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS
                    .equals(responseData.getStatus())) {
                bookmarkFlag = responseData.getData().getResult().getBookmarkStatus();
                if (!isAdded()) {
                    return;
                }
                if (!bookmarkFlag) {
                    bookmarkStatus = 0;
                    Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark);
                    bookmarkArticleTextView
                            .setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                } else {
                    Drawable top = ContextCompat
                            .getDrawable(getActivity(), R.drawable.ic_bookmarked);
                    bookmarkArticleTextView
                            .setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                    bookmarkStatus = 1;
                }
                bookmarkId = responseData.getData().getResult().getBookmarkId();
            } else {
                if (isAdded()) {
                    ((ArticleDetailsContainerActivity) getActivity())
                            .showToast(getString(R.string.server_went_wrong));
                }
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
        public void onResponse(Call<AddBookmarkResponse> call,
                retrofit2.Response<AddBookmarkResponse> response) {
            if (null == response.body()) {
                if (isAdded()) {
                    ((ArticleDetailsContainerActivity) getActivity())
                            .showToast(getString(R.string.server_went_wrong));
                }
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

    private Callback<ArticleRecommendationStatusResponse> recommendStatusResponseCallback =
            new Callback<ArticleRecommendationStatusResponse>() {
                @Override
                public void onResponse(Call<ArticleRecommendationStatusResponse> call,
                        retrofit2.Response<ArticleRecommendationStatusResponse> response) {
                    if (null == response.body()) {
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
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

    private Callback<RecommendUnrecommendArticleResponse> recommendUnrecommendArticleResponseCallback =
            new Callback<RecommendUnrecommendArticleResponse>() {
                @Override
                public void onResponse(Call<RecommendUnrecommendArticleResponse> call,
                        retrofit2.Response<RecommendUnrecommendArticleResponse> response) {
                    if (null == response.body()) {
                        if (!isAdded()) {
                            return;
                        }
                        ((ArticleDetailsContainerActivity) getActivity())
                                .showToast(getString(R.string.server_went_wrong));
                        return;
                    }
                    try {
                        RecommendUnrecommendArticleResponse responseData = response.body();
                        if (responseData.getCode() == 200 && Constants.SUCCESS
                                .equals(responseData.getStatus())) {
                            if (!isAdded()) {
                                return;
                            }
                            if (null == responseData.getData() || responseData.getData().isEmpty()) {
                                ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
                            }
                        } else {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                        }
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
        }
    }

    private void handleExceptions(Throwable t) {
        if (isAdded()) {
            if (t instanceof UnknownHostException) {
                ((ArticleDetailsContainerActivity) getActivity())
                        .showToast(getString(R.string.error_network));
            } else if (t instanceof SocketTimeoutException) {
                ((ArticleDetailsContainerActivity) getActivity())
                        .showToast(getString(R.string.connection_timeout));
            }
        }
        Crashlytics.logException(t);
        Log.d("MC4kException", Log.getStackTraceString(t));
    }

    private Callback<FollowUnfollowUserResponse> followUserResponseCallback =
            new Callback<FollowUnfollowUserResponse>() {
                @Override
                public void onResponse(Call<FollowUnfollowUserResponse> call,
                        retrofit2.Response<FollowUnfollowUserResponse> response) {
                    if (response.body() == null) {
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.went_wrong));
                        }
                        return;
                    }
                    try {
                        FollowUnfollowUserResponse responseData = response.body();
                        if (responseData.getCode() != 200 || !Constants.SUCCESS
                                .equals(responseData.getStatus())) {
                            followClick.setText(
                                    BaseApplication.getAppContext().getString(R.string.ad_follow_author));
                            isFollowing = false;
                        }
                    } catch (Exception e) {
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
                    if (isAdded()) {
                        ((ArticleDetailsContainerActivity) getActivity())
                                .showToast(getString(R.string.server_went_wrong));
                    }
                    Crashlytics.logException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private Callback<FollowUnfollowUserResponse> unfollowUserResponseCallback =
            new Callback<FollowUnfollowUserResponse>() {
                @Override
                public void onResponse(Call<FollowUnfollowUserResponse> call,
                        retrofit2.Response<FollowUnfollowUserResponse> response) {
                    if (response.body() == null) {
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.went_wrong));
                        }
                        return;
                    }
                    try {
                        FollowUnfollowUserResponse responseData = response.body();
                        if (responseData.getCode() != 200 || !Constants.SUCCESS
                                .equals(responseData.getStatus())) {
                            followClick.setText(BaseApplication.getAppContext()
                                    .getString(R.string.ad_following_author));
                            isFollowing = true;
                        }
                    } catch (Exception e) {
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
                    if (isAdded()) {
                        ((ArticleDetailsContainerActivity) getActivity())
                                .showToast(getString(R.string.server_went_wrong));
                    }
                    Crashlytics.logException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private class MyWebChromeClient extends WebChromeClient {

        FrameLayout.LayoutParams layoutParameters = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mainCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mainContentView = (RelativeLayout) getActivity().findViewById(R.id.content_frame);
            mainContentView.setVisibility(View.GONE);
            mainCustomViewContainer = new FrameLayout(getActivity());
            mainCustomViewContainer.setLayoutParams(layoutParameters);
            mainCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(layoutParameters);
            mainCustomViewContainer.addView(view);
            mainCustomView = view;
            mainCustomViewCallback = callback;
            mainCustomViewContainer.setVisibility(View.VISIBLE);
            getActivity().setContentView(mainCustomViewContainer);
            mainCustomViewContainer.bringToFront();
        }

        @Override
        public void onHideCustomView() {
            if (mainCustomView != null) {
                // Hide the custom view.
                mainCustomView.setVisibility(View.GONE);
                // Remove the custom view from its container.
                mainCustomViewContainer.removeView(mainCustomView);
                mainCustomView = null;
                mainCustomViewContainer.setVisibility(View.GONE);
                mainCustomViewCallback.onCustomViewHidden();
                // Show the content view.
                mainContentView.setVisibility(View.VISIBLE);
                getActivity().setContentView(mainContentView);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            swipeRelated = (ISwipeRelated) activity;
        } catch (ClassCastException e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    public interface ISwipeRelated {

        void onRelatedSwipe(ArrayList<ArticleListingResult> articleList);
    }

    private void setValuesInFollowPopUp() {
        try {
            Picasso.get().load(detailData.getProfilePic().getClientApp()).into(authorImageViewFollowPopUp);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
        }
        authorNameFollowPopUp.setText(detailData.getUserName());
        followPopUpBottomContainer.setVisibility(View.VISIBLE);
    }
}
