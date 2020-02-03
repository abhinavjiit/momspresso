package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
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
import androidx.fragment.app.FragmentTransaction;

import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
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
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.FilteredTopicsArticleListingActivity;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.GroupIdCategoryMap;
import com.mycity4kids.widget.CustomFontTextView;
import com.mycity4kids.widget.RelatedArticlesView;
import com.mycity4kids.youtube.DeveloperKey;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import okhttp3.ResponseBody;
import q.rorbin.badgeview.QBadgeView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static com.mycity4kids.ui.activity.ArticleDetailsContainerActivity.NEW_ARTICLE_DETAIL_FLAG;

/**
 * Created by hemant on 6/6/17.
 */
public class ArticleDetailsFragment extends BaseFragment implements View.OnClickListener, ObservableScrollViewCallbacks, AddEditCommentReplyFragment.IAddCommentReply, GroupIdCategoryMap.GroupCategoryInterface, GroupMembershipStatus.IMembershipStatus, YouTubePlayer.OnInitializedListener {

    private final static int ADD_BOOKMARK = 1;
    private static final int RECOVERY_REQUEST = 1;
    private SimpleTooltip simpleTooltip, collectionTooltip;
    private int TOOLTIP_SHOW_TIMES = 0;
    private final static int REPLY_LEVEL_PARENT = 1;
    private final static int REPLY_LEVEL_CHILD = 2;
    private MixpanelAPI mixpanel;
    private Handler handler, startCollectionHandler, stopCollectionHandler;
    private ISwipeRelated iSwipeRelated;
    private ArticleDetailResult detailData;
    private Bitmap defaultBloggerBitmap;
    private Bitmap resized;
    private ArticleDetailsAPI articleDetailsAPI;
    private ArrayList<ImageData> imageList;
    private ArrayList<VideoData> videoList;
    private ArrayList<ArticleListingResult> impressionList;
    private int width;
    private int bookmarkStatus;
    private int recommendStatus;
    private int estimatedReadTime = 0;
    private float density;
    private boolean isFollowing = false;
    private boolean isLoading = false;
    private boolean isArticleDetailEndReached = false;
    private boolean isArticleDetailLoaded = false;
    private boolean isSwipeNextAvailable;
    private boolean isFbCommentHeadingAdded = false;
    private String bookmarkFlag = "0";
    private String recommendationFlag = "0";
    private String commentURL = "";
    private String shareUrl = "";
    private String bookmarkId;
    private String authorId;
    private String author;
    private String parentId;
    private String articleId;
    private String deepLinkURL;
    private String blogSlug;
    private String titleSlug;
    private String commentType = "db";
    private String commentMainUrl;
    private String pagination = "";
    private String isMomspresso;
    private String userDynamoId;
    private String articleLanguageCategoryId;
    private String from;
    private String gtmLanguage;
    private int followTopicChangeNewUser = 0;

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
    private TextView writeArticleTextView;
    private ImageView cover_image;
    private ImageView floatingActionButton;
    private ImageView writeArticleImageView;
    private RelativeLayout mLodingView;
    private FlowLayout tagsLayout;
    private Rect scrollBounds;
    private View fragmentView;
    private LinearLayout bottomToolbarLL;
    private TextView commentHeading;
    private View relatedTrendingSeparator;
    //    private AdView mAdView;
    private TextView viewCommentsTextView;
    private LayoutInflater mInflater;
    private LinearLayout adView;
    private RelativeLayout groupHeaderView;
    private ImageView groupHeaderImageView;
    private TextView groupHeadingTextView, groupSubHeadingTextView;
    private int groupId;
    private String youTubeId;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayerSupportFragment mYouTubePlayerSupportFragment;
    private FrameLayout youtubeContainer;
    private ImageView sponsoredImage;
    private TextView sponsoredTextView;
    private RelativeLayout sponsoredViewContainer;
    private ArrayList<Topics> shortStoriesTopicList = new ArrayList<>();
    private ArrayList<Topics> shortStoriesTopicList1 = new ArrayList<>();
    private ArticleDetailResult responseData;
    private ImageView badge;
    private LinearLayout progressBarContainer;
    private boolean newArticleDetailFlag;
    private String webViewURL;
    private ImageView crownImageView;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        fragmentView = inflater.inflate(R.layout.article_details_fragment, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "articleDetailFragment", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId());
        userDynamoId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        deepLinkURL = "";// getIntent().getStringExtra(Constants.DEEPLINK_URL);
        try {
            mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

            floatingActionButton = (ImageView) fragmentView.findViewById(R.id.user_image);
            mWebView = (WebView) fragmentView.findViewById(R.id.articleWebView);
            youtubeContainer = (FrameLayout) fragmentView.findViewById(R.id.youtube_fragment);
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
            author_type = (TextView) fragmentView.findViewById(R.id.blogger_type);
            followClick = (TextView) fragmentView.findViewById(R.id.follow_click);
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
            swipeNextTextView = (TextView) fragmentView.findViewById(R.id.swipeNextTextView);
            viewCommentsTextView = ((TextView) fragmentView.findViewById(R.id.viewCommentsTextView));
            writeArticleTextView = ((TextView) fragmentView.findViewById(R.id.writeArticleTextView));
            writeArticleImageView = ((ImageView) fragmentView.findViewById(R.id.writeArticleImageView));
            groupHeaderView = (RelativeLayout) fragmentView.findViewById(R.id.groupHeaderView);
            groupHeaderImageView = (ImageView) fragmentView.findViewById(R.id.groupHeaderImageView);
            groupHeadingTextView = (TextView) fragmentView.findViewById(R.id.groupHeadingTextView);
            groupSubHeadingTextView = (TextView) fragmentView.findViewById(R.id.groupSubHeadingTextView);
            mScrollView = (ObservableScrollView) fragmentView.findViewById(R.id.scroll_view);
            mLodingView = (RelativeLayout) fragmentView.findViewById(R.id.relativeLoadingView);
            crownImageView = fragmentView.findViewById(R.id.crownImageView);

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
            writeArticleImageView.setOnClickListener(this);
            writeArticleTextView.setOnClickListener(this);
            groupHeaderView.setOnClickListener(this);
            followClick.setOnClickListener(this);
            followClick.setEnabled(false);

            startCollectionHandler = new Handler();
            startCollectionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null)
                        tooltipForCollection();
                }
            }, 60000);

            mWebChromeClient = new MyWebChromeClient();
            mWebView.setWebChromeClient(mWebChromeClient);
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW, request.getUrl());
                        startActivity(i);
                    } catch (ActivityNotFoundException anfe) {
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
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    Log.d("onReceivedError", "----- " + error.toString() + " -----");
                }

                @Override
                public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                    super.onReceivedHttpError(view, request, errorResponse);
                    Log.d("onReceivedHttpError", "--    --- " + errorResponse.getReasonPhrase() + " -----");
                }
            });

            if ((AppConstants.LOCALE_ENGLISH.equals(SharedPrefUtils.getAppLocale(getActivity())))) {
                mWebView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return true;
                    }
                });
            } else {
                mWebView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return true;
                    }
                });
                mWebView.setLongClickable(false);
            }

            facebookShareTextView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_facebook_svg), null, null);
            if (Build.VERSION.SDK_INT > 23) {
                try {
                    Drawable myDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_whats_app);
                    myDrawable.setTint(getResources().getColor(R.color.app_red));
                    whatsappShareTextView.setCompoundDrawablesWithIntrinsicBounds(null, myDrawable, null, null);
                } catch (NullPointerException e) {
                    Crashlytics.logException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }
                try {
                    Drawable myDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_collection_add);
                    myDrawable.setTint(getResources().getColor(R.color.app_red));
                    emailShareTextView.setCompoundDrawablesWithIntrinsicBounds(null, myDrawable, null, null);
                } catch (NullPointerException e) {
                    Crashlytics.logException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }

                try {
                    Drawable myDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark);
                    myDrawable.setTint(getResources().getColor(R.color.app_red));
                    bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, myDrawable, null, null);
                } catch (NullPointerException e) {
                    Crashlytics.logException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }
                try {
                    Drawable myDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_recommend);
                    myDrawable.setTint(getResources().getColor(R.color.app_red));
                    likeArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, myDrawable, null, null);
                } catch (NullPointerException e) {
                    Crashlytics.logException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }
            }


            density = getResources().getDisplayMetrics().density;
            width = getResources().getDisplayMetrics().widthPixels;
            defaultBloggerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_blogger_profile_img);
            //resizing image because of crash .image size is large.
            resized = Bitmap.createScaledBitmap(defaultBloggerBitmap, 96, 96, true);
            floatingActionButton.setImageDrawable(new BitmapDrawable(getResources(), resized));

            try {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                for (int i = 0; i < res.getData().size(); i++) {
                    if (AppConstants.SPONSORED_CATEGORYID.equals(res.getData().get(i).getId())) {
                        shortStoriesTopicList.add(res.getData().get(i));
                        shortStoriesTopicList1 = shortStoriesTopicList;
                    }
                }
            } catch (FileNotFoundException e) {
                Crashlytics.logException(e);
                Log.d("FileNotFoundException", Log.getStackTraceString(e));


                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());
                        Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                            TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                            for (int i = 0; i < res.getData().size(); i++) {
                                if (AppConstants.SPONSORED_CATEGORYID.equals(res.getData().get(i).getId())) {
                                    shortStoriesTopicList.add(res.getData().get(i));
                                    shortStoriesTopicList1 = shortStoriesTopicList;
                                }
                            }

                        } catch (FileNotFoundException e) {
                            Crashlytics.logException(e);
                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Crashlytics.logException(t);
                        Log.d("MC4KException", Log.getStackTraceString(t));
                    }
                });
            }


            mYouTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
            if (getUserVisibleHint()) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.youtube_fragment, mYouTubePlayerSupportFragment).commit();
                mYouTubePlayerSupportFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);
            }
            mLodingView = (RelativeLayout) fragmentView.findViewById(R.id.relativeLoadingView);

            mScrollView = (ObservableScrollView) fragmentView.findViewById(R.id.scroll_view);
            mScrollView.setScrollViewCallbacks(this);
            impressionList = new ArrayList<>();

            Bundle bundle = getArguments();
            if (bundle != null) {
                parentId = bundle.getString("parentId");
                articleId = bundle.getString(Constants.ARTICLE_ID);
                authorId = bundle.getString(Constants.AUTHOR_ID, "");
                isSwipeNextAvailable = bundle.getBoolean("swipeNext", false);
                newArticleDetailFlag = bundle.getBoolean(NEW_ARTICLE_DETAIL_FLAG);

                if (isSwipeNextAvailable) {
                    isSwipeNextAvailable = BaseApplication.isFirstSwipe();
                }

                if (bundle.getBoolean("fromNotification")) {
                } else {
                    from = bundle.getString(Constants.ARTICLE_OPENED_FROM);
                    String index = bundle.getString(Constants.ARTICLE_INDEX);
                    String screen = bundle.getString(Constants.FROM_SCREEN);
                }

                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
                hitArticleDetailsRedisAPI();
                //bindSponsored();

                getViewCountAPI();
                hitRecommendedStatusAPI();
            }

            scrollBounds = new Rect();
            mScrollView.getHitRect(scrollBounds);
//            showNativeAd();
//            showTopBannerAd();
        } catch (Exception e) {
            removeProgressDialog();
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        new QBadgeView(getActivity())
                .setBadgeText(" " + getString(R.string.new_label) + " ")
                .setBadgeBackgroundColor(getResources().getColor(R.color.orange_new))
                .setBadgeTextSize(7, true)
                .setBadgePadding(3, true)
                .setBadgeGravity(Gravity.TOP | Gravity.END)
                .setGravityOffset(4, -2, true)
                .bindTarget(emailShareTextView);

        return fragmentView;
    }

    private void tooltipForCollection() {
        collectionTooltip = new SimpleTooltip.Builder(getActivity())
                .anchorView(emailShareTextView)
                .backgroundColor(getResources().getColor(R.color.app_red))
                .text(getResources().getString(R.string.add_to_collection))
                .textColor(getResources().getColor(R.color.white))
                .arrowColor(getResources().getColor(R.color.app_red))
                .gravity(Gravity.TOP)
                .arrowWidth(40)
                .animated(true)
                .transparentOverlay(true)
                .dismissOnInsideTouch(false)
                .dismissOnOutsideTouch(false)
                .build();
        collectionTooltip.show();
        stopCollectionHandler = new Handler();
        stopCollectionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (collectionTooltip.isShowing()) {
                    try {
                        collectionTooltip.dismiss();
                    } catch (Exception e) {

                    }
                }
            }
        }, 5000);

    }

    private void bindSponsored() {
        for (int i = 0; i < shortStoriesTopicList.get(0).getChild().size(); i++) {
            if (parentId.equals(shortStoriesTopicList.get(0).getChild().get(i).getId())) {
                if (shortStoriesTopicList.get(0).getChild().get(i).getExtraData().get(0).getCategoryTag().getCategoryImage() != null &&
                        !shortStoriesTopicList.get(0).getChild().get(i).getExtraData().get(0).getCategoryTag().getCategoryImage().isEmpty()) {
                    sponsoredViewContainer.setVisibility(View.VISIBLE);
                    Picasso.with(getActivity()).load(shortStoriesTopicList.get(0).getChild().get(i).getExtraData().get(0).getCategoryTag().getCategoryImage()).placeholder(R.drawable.default_article).into(sponsoredImage);
                    sponsoredTextView.setText("this story is sponsored by ");
                } else {
                    sponsoredViewContainer.setVisibility(View.GONE);
                }
                if (shortStoriesTopicList.get(0).getChild().get(i).getExtraData().get(0).getCategoryTag().getCategoryBadge() != null && !shortStoriesTopicList.get(0).getChild().get(i).getExtraData().get(0).getCategoryTag().getCategoryBadge().isEmpty()) {
                    badge.setVisibility(View.VISIBLE);
                    Picasso.with(getActivity()).load(shortStoriesTopicList.get(0).getChild().get(i).getExtraData().get(0).getCategoryTag().getCategoryBadge()).placeholder(R.drawable.default_article).into(badge);
                } else {
                    badge.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && youTubePlayer != null) {
//            Log.v (TAG, "Releasing youtube player, URL : " + getArguments().getString(KeyConstant.KEY_VIDEO_URL));
            youTubePlayer.release();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(mYouTubePlayerSupportFragment).commitAllowingStateLoss();
        }
        if (isVisibleToUser && mYouTubePlayerSupportFragment != null) {
//            Log.v (TAG, "Initializing youtube player, URL : " + getArguments().getString(KeyConstant.KEY_VIDEO_URL));
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.youtube_fragment, mYouTubePlayerSupportFragment).commitAllowingStateLoss();
            mYouTubePlayerSupportFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    private void hitArticleDetailsRedisAPI() {
        Call<ArticleDetailResult> call = articleDetailsAPI.getArticleDetailsFromRedis(articleId, "articleId");
        call.enqueue(articleDetailResponseCallbackRedis);
    }

    private void getViewCountAPI() {
        Call<ViewCountResponse> call = articleDetailsAPI.getViewCount(articleId);
        call.enqueue(getViewCountResponseCallback);
    }

    private void getCrownData() {
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
        Call<CrownDataResponse> call = articleDetailsAPI.getCrownData(authorId);
        call.enqueue(getCrownDataResponse);
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
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
        Call<ArticleListingResponse> vernacularTrendingCall = topicsCategoryAPI.getTrendingArticles(1, 4, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
        vernacularTrendingCall.enqueue(vernacularTrendingResponseCallback);

        Call<ArticleListingResponse> categoryRelatedArticlesCall = articleDetailsAPI.getCategoryRelatedArticles(articleId, 0, 3, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
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
            Picasso.with(getActivity()).load(detailData.getImageUrl().getThumbMax()).placeholder(R.drawable.default_article).resize(width, (int) (220 * density)).centerCrop().into(cover_image);
        }

        if (!StringUtils.isNullOrEmpty(detailData.getTitle())) {
            article_title.setText(detailData.getTitle());
        }

        try {
            if (!StringUtils.isNullOrEmpty(detailData.getUserType())) {
                if (AppConstants.USER_TYPE_BLOGGER.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        author_type.setText(AppUtils.getString(getActivity(), R.string.author_type_blogger));
                    } else {
                        author_type.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + detailData.getBlogTitleSlug().trim() + "/article/" + detailData.getTitleSlug();
                        webViewURL = AppConstants.ARTICLE_WEBVIEW_URL + detailData.getBlogTitleSlug().trim() + "/article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EXPERT.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        author_type.setText(AppUtils.getString(getActivity(), R.string.author_type_expert));
                    } else {
                        author_type.setText(AppConstants.AUTHOR_TYPE_EXPERT.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData.getTitleSlug();
                        webViewURL = AppConstants.ARTICLE_WEBVIEW_URL + "article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITOR.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        author_type.setText(AppUtils.getString(getActivity(), R.string.author_type_editor));
                    } else {
                        author_type.setText(AppConstants.AUTHOR_TYPE_EDITOR.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData.getTitleSlug();
                        webViewURL = AppConstants.ARTICLE_WEBVIEW_URL + "article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITORIAL.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        author_type.setText(AppUtils.getString(getActivity(), R.string.author_type_editorial));
                    } else {
                        author_type.setText(AppConstants.AUTHOR_TYPE_EDITORIAL.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData.getTitleSlug();
                        webViewURL = AppConstants.ARTICLE_WEBVIEW_URL + "article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_FEATURED.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        author_type.setText(AppUtils.getString(getActivity(), R.string.author_type_featured));
                    } else {
                        author_type.setText(AppConstants.AUTHOR_TYPE_FEATURED.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData.getTitleSlug();
                        webViewURL = AppConstants.ARTICLE_WEBVIEW_URL + "article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_COLLABORATION.equals(detailData.getUserType())) {
                    if (isAdded()) {
                        author_type.setText(AppUtils.getString(getActivity(), R.string.author_type_collaboration));
                    } else {
                        author_type.setText(AppConstants.AUTHOR_TYPE_COLLABORATION.toUpperCase());
                    }

                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + detailData.getTitleSlug();
                        webViewURL = AppConstants.ARTICLE_WEBVIEW_URL + "article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else {
                    if (isAdded()) {
                        author_type.setText(AppUtils.getString(getActivity(), R.string.author_type_user));
                    } else {
                        author_type.setText(AppConstants.AUTHOR_TYPE_USER.toUpperCase());
                    }
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + detailData.getBlogTitleSlug().trim() + "/article/" + detailData.getTitleSlug();
                        webViewURL = AppConstants.ARTICLE_WEBVIEW_URL + detailData.getBlogTitleSlug().trim() + "/article/" + detailData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                }
            } else {
                // Default Author type set to Blogger
                if (isAdded()) {
                    author_type.setText(AppUtils.getString(getActivity(), R.string.author_type_blogger));
                } else {
                    author_type.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
                }
                if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                    shareUrl = AppConstants.ARTICLE_SHARE_URL + detailData.getBlogTitleSlug().trim() + "/article/" + detailData.getTitleSlug();
                    webViewURL = AppConstants.ARTICLE_WEBVIEW_URL + detailData.getBlogTitleSlug().trim() + "/article/" + detailData.getTitleSlug();
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

        if (!newArticleDetailFlag) {
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
                            youtubeContainer.setVisibility(View.VISIBLE);
                            youTubeId = AppUtils.extractYoutubeIdForMomspresso(video.getVideoUrl());
                            if (youTubePlayer != null)
                                youTubePlayer.cueVideo(youTubeId);
                            cover_image.setVisibility(View.INVISIBLE);
                            bodyDesc = bodyDesc.replace(video.getKey(), "");
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
            } else {
                if (null != videoList && !videoList.isEmpty()) {
                    for (VideoData video : videoList) {
                        if ("1".equals(isMomspresso)) {
                            youtubeContainer.setVisibility(View.VISIBLE);
                            youTubeId = AppUtils.extractYoutubeIdForMomspresso(video.getVideoUrl());
                            if (youTubePlayer != null)
                                youTubePlayer.cueVideo(youTubeId);
                            cover_image.setVisibility(View.INVISIBLE);
                            bodyDesc = bodyDesc.replace(video.getKey(), "");
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
            }
        } else {
            Log.d("WEB VIEW URL", "------ " + webViewURL + " -----");
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mWebView.loadUrl(webViewURL);
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
    }

    private void setArticleLanguageCategoryId() {
        ArrayList<Map<String, String>> tagsList = detailData.getTags();
        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.LANGUAGES_JSON_FILE);
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
                                case AppConstants.TAMIL_CATEGORYID:
                                    articleLanguageCategoryId = AppConstants.TAMIL_CATEGORYID;
                                    ((ArticleDetailsContainerActivity) getActivity()).showPlayArticleAudioButton();
                                    return;
                                case AppConstants.TELUGU_CATEGORYID:
                                    articleLanguageCategoryId = AppConstants.TELUGU_CATEGORYID;
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

    private void createSelectedTagsView() {
        getFollowedTopicsList();
    }

    private void getFollowedTopicsList() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
        Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.getFollowedCategories(userDynamoId);
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
                        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
                        groupMembershipStatus.checkMembershipStatus(groupId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    }
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
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
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(AppUtils.dpTopx(10), 0, AppUtils.dpTopx(10), 0);
                    tagsLayout.setLayoutParams(params);
                    viewAllTagsTextView.setVisibility(View.GONE);
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
                        Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
                        profileIntent.putExtra(Constants.USER_ID, commentData.getUserId());
                        startActivity(profileIntent);
                    }
                }
                break;
                case R.id.txvReplyTitle:
                case R.id.replierImageView: {
                    CommentsData commentData = (CommentsData) ((View) v.getParent()).getTag();
                    if (!"fb".equals(commentData.getComment_type())) {
                        Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
                        profileIntent.putExtra(Constants.USER_ID, commentData.getUserId());
                        startActivity(profileIntent);
                    }
                }
                break;
                case R.id.follow_click:
                    followAPICall();
                    break;
                case R.id.user_image:
                case R.id.user_name:
                    Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
                    profileIntent.putExtra(Constants.USER_ID, detailData.getUserId());
                    startActivity(profileIntent);
                    break;
                case R.id.relatedArticles1: {
                    if (recentAuthorArticleHeading.getText() != null && recentAuthorArticleHeading.getText().toString().contains("RELATED")) {
                        launchRelatedTrendingArticle(v, "articleDetailsRelatedList", 1);
                    } else {
                        launchRelatedTrendingArticle(v, "articleDetailsAuthorsRecentList", 1);
                    }
                    break;
                }
                case R.id.relatedArticles2: {
                    if (recentAuthorArticleHeading.getText() != null && recentAuthorArticleHeading.getText().toString().contains("RELATED")) {
                        launchRelatedTrendingArticle(v, "articleDetailsRelatedList", 2);
                    } else {
                        launchRelatedTrendingArticle(v, "articleDetailsAuthorsRecentList", 2);
                    }
                    break;
                }
                case R.id.relatedArticles3: {
                    if (recentAuthorArticleHeading.getText() != null && recentAuthorArticleHeading.getText().toString().contains("RELATED")) {
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
                    Intent intent = new Intent(getActivity(), FilteredTopicsArticleListingActivity.class);
                    intent.putExtra("selectedTopics", categoryId);
                    intent.putExtra("displayName", ((TextView) ((LinearLayout) v).getChildAt(0)).getText());
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
                        tooltipForShare();
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
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, AppUtils.stripHtml("" + detailData.getExcerpt()) + "\n\n" + BaseApplication.getAppContext().getString(R.string.ad_share_follow_author, author) + "\n" + shareUrl);
                        try {
                            startActivity(whatsappIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getActivity(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                        }
                        Utils.pushShareArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author, "Whatsapp");
                    }
                    break;
                case R.id.emailShareTextView:
                    try {
                        AddCollectionAndCollectionItemDialogFragment addCollectionAndCollectionitemDialogFragment = new AddCollectionAndCollectionItemDialogFragment();
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

    private void openViewCommentDialog() {
        try {
            long createdTime = Long.parseLong(detailData.getCreated());
            ViewAllCommentsFragment commentFrag = new ViewAllCommentsFragment();
            commentFrag.setTargetFragment(this, 0);
            Bundle _args = new Bundle();
            _args.putString("mycityCommentURL", commentMainUrl);
            if (createdTime < AppConstants.MYCITY_TO_MOMSPRESSO_SWITCH_TIME) {
                _args.putString("fbCommentURL", shareUrl.replace("www.momspresso.com", "www.mycity4kids.com"));
            } else {
                _args.putString("fbCommentURL", shareUrl);
            }
            _args.putString(Constants.ARTICLE_ID, articleId);
            _args.putString(Constants.AUTHOR, authorId + "~" + author);
            _args.putString(Constants.BLOG_SLUG, detailData.getBlogTitleSlug());
            _args.putString(Constants.TITLE_SLUG, detailData.getTitleSlug());
            _args.putString("userType", detailData.getUserType());
            commentFrag.setArguments(_args);
            ((ArticleDetailsContainerActivity) getActivity()).hideToolbarPerm();
            ((ArticleDetailsContainerActivity) getActivity()).addFragment(commentFrag, null, true, "topToBottom");
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            if (isAdded())
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.unable_to_load_comment));
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
        View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);
        View tagsView = mScrollView.findViewById(R.id.tagsLayoutContainer);
        Rect scrollBounds = new Rect();
        mScrollView.getHitRect(scrollBounds);
        int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));
        if (diff <= 10 && !isLoading && !StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http") && !AppConstants.PAGINATION_END_VALUE.equals(pagination)) {
//            getMoreComments();
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
            if (isArticleDetailLoaded) {
                ((ArticleDetailsContainerActivity) getActivity()).addArticleForImpression(articleId);
            }
            if (impressionList != null && !impressionList.isEmpty()) {
                for (ArticleListingResult result : impressionList) {
                    ((ArticleDetailsContainerActivity) getActivity()).addArticleForImpression(result.getTitle());
                }
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
                        if (collectionTooltip != null && collectionTooltip.isShowing()) {
                            startCollectionHandler.postAtTime(new Runnable() {
                                @Override
                                public void run() {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                collectionTooltip.dismiss();
                                                stopCollectionHandler.removeMessages(0);
                                            }
                                        });
                                    }
                                }
                            }, 100);

                        }
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

    public String getGTMArticleId() {
        return articleId;
    }

    public String getGTMAuthor() {
        return authorId + "~" + author;
    }

    public String getGTMLanguage() {
        return gtmLanguage;
    }

    @Override
    public void onCommentAddition(CommentsData cd) {

    }

    @Override
    public void onCommentReplyEditSuccess(CommentsData cd) {

    }

    @Override
    public void onReplyAddition(CommentsData cd) {

    }

    @Override
    public void onGroupMappingResult(int groupId, String gpHeading, String gpSubHeading, String gpImageUrl) {
        this.groupId = groupId;
        try {
            Picasso.with(getActivity()).load(gpImageUrl).placeholder(R.drawable.groups_generic)
                    .error(R.drawable.groups_generic).into(groupHeaderImageView);
        } catch (Exception e) {
            groupHeaderImageView.setImageResource(R.drawable.groups_generic);
        }
        if (StringUtils.isNullOrEmpty(gpHeading)) {
            groupHeadingTextView.setText(BaseApplication.getAppContext().getString(R.string.groups_join_support_gp));
        } else {
            groupHeadingTextView.setText(gpHeading);
        }
        if (StringUtils.isNullOrEmpty(gpSubHeading)) {
            groupSubHeadingTextView.setText(BaseApplication.getAppContext().getString(R.string.groups_not_alone));
        } else {
            groupSubHeadingTextView.setText(gpSubHeading);
        }
    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        String userType = null;
        if (isAdded()) {
            if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {

            } else {
                if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                    userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
                } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                    userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
                }
            }

            if (!AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(userType) && !AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(userType)) {
                if ("male".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender()) ||
                        "m".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())) {
                    if (isAdded()) {
                        Toast.makeText(getActivity(), getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                    }
                    if (BuildConfig.DEBUG || AppConstants.DEBUGGING_USER_ID.contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {

                    } else {
                        return;
                    }
                } else {

                }
            }

            if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
                Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
                startActivity(intent);
            } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
                Toast.makeText(getActivity(), getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT).show();
            } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
                Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
                startActivity(intent);
            } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
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

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            this.youTubePlayer = youTubePlayer;
            youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
            if (null != youTubeId) {
                youTubePlayer.cueVideo(youTubeId);
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), RECOVERY_REQUEST).show();
        }
    }

    private void followAPICall() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);

        if (isFollowing) {
            isFollowing = false;
            followClick.setText(getString(R.string.ad_follow_author));
            Utils.pushGenericEvent(getActivity(), "CTA_Unfollow_Article_Detail", userDynamoId, "ArticleDetailsFragment");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            followClick.setText(getString(R.string.ad_following_author));
            Utils.pushGenericEvent(getActivity(), "CTA_Follow_Article_Detail", userDynamoId, "ArticleDetailsFragment");
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

    private void checkingForSponsored() {
        if (responseData.getIsSponsored().equalsIgnoreCase("1")) {
            boolean flag = false;
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                for (int i = 0; i < res.getData().size(); i++) {
                    shortStoriesTopicList.add(res.getData().get(i));
                }

                ArrayList<Topics> topicLocalList = new ArrayList<>();
                for (Topics topic : shortStoriesTopicList) {
                    if (topic.getSlug() != null && topic.getSlug().equalsIgnoreCase("sponsored-stories")) {
                        topicLocalList = topic.getChild();
                        break;
                    }
                }

                for (Topics topic : topicLocalList) {
                    for (Map<String, String> var : responseData.getTags()) {
                        if (var.size() > 0) {
                            for (Map.Entry<String, String> entry : var.entrySet()) {
                                if (topic.getId().equalsIgnoreCase(entry.getKey())) {
                                    if (topic.getExtraData() != null && topic.getExtraData().size() != 0 &&
                                            topic.getExtraData().get(0).getCategoryTag() != null) {
                                        if (topic.getExtraData().get(0).getCategoryTag().getCategoryImage() != null &&
                                                !topic.getExtraData().get(0).getCategoryTag().getCategoryImage().isEmpty()) {
                                            sponsoredViewContainer.setVisibility(View.VISIBLE);
                                            Picasso.with(getActivity()).load(topic.getExtraData().get(0).getCategoryTag().getCategoryImage()).into(sponsoredImage);
                                            sponsoredTextView.setText("this story is sponsored by ");
                                        } else {
                                            sponsoredViewContainer.setVisibility(View.GONE);
                                        }

                                        if (topic.getExtraData().get(0).getCategoryTag().getCategoryBadge() != null && !topic.getExtraData().get(0).getCategoryTag().getCategoryBadge().isEmpty()) {
                                            badge.setVisibility(View.VISIBLE);
                                            Picasso.with(getActivity()).load(topic.getExtraData().get(0).getCategoryTag().getCategoryBadge()).into(badge);
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

    Callback<ArticleDetailResult> articleDetailResponseCallbackRedis = new Callback<ArticleDetailResult>() {
        @Override
        public void onResponse(Call<ArticleDetailResult> call, retrofit2.Response<ArticleDetailResult> response) {
            removeProgressDialog();
            if (response.body() == null) {
                getArticleDetailsWebserviceAPI();
                return;
            }
            try {
                removeProgressDialog();


                responseData = response.body();
                getResponseUpdateUi(responseData);
                checkingForSponsored();

                authorId = detailData.getUserId();
                isArticleDetailLoaded = true;
                hitBookmarkFollowingStatusAPI();
                if ("1".equals(isMomspresso)) {
                    hitBookmarkVideoStatusAPI();
                }
                hitRelatedArticleAPI();
                commentURL = responseData.getCommentsUri();
                commentMainUrl = responseData.getCommentsUri();

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
            if (response.body() == null) {
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

    private Callback<CrownDataResponse> getCrownDataResponse = new Callback<CrownDataResponse>() {
        @Override
        public void onResponse(Call<CrownDataResponse> call, retrofit2.Response<CrownDataResponse> response) {
            if (response.body() == null) {
                return;
            }
            try {
                CrownDataResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    setCrownData(responseData.getData().getResult().getImage_url());
                } else {

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

    private void setCrownData(String image_url) {
        if (!StringUtils.isNullOrEmpty(image_url)) {
            crownImageView.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(image_url).
                    placeholder(R.drawable.default_article).fit().into(crownImageView);
        }
    }

    private void getArticleDetailsWebserviceAPI() {
        Call<ArticleDetailWebserviceResponse> call = articleDetailsAPI.getArticleDetailsFromWebservice(articleId);
        call.enqueue(articleDetailResponseCallbackWebservice);
    }

    private Callback<ArticleDetailWebserviceResponse> articleDetailResponseCallbackWebservice = new Callback<ArticleDetailWebserviceResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailWebserviceResponse> call, retrofit2.Response<ArticleDetailWebserviceResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
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
//                        getMoreComments();
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

    private Callback<ArticleListingResponse> vernacularTrendingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("Trending Article API failure");
                Crashlytics.logException(nee);
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
                    if (dataList == null || dataList.size() == 0) {

                    } else {
                        trendingArticles.setVisibility(View.VISIBLE);
                        impressionList.addAll(dataList);
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
                } else {
                    NetworkErrorException nee = new NetworkErrorException("Trending Article Error Response");
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
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response.body() == null) {
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
                        recentAuthorArticleHeading.setText(getString(R.string.recent_article));
                        recentAuthorArticles.setVisibility(View.VISIBLE);

                        Collections.shuffle(dataList);
                        impressionList.addAll(dataList);
                        iSwipeRelated.onRelatedSwipe(dataList);
                        if (dataList.size() >= 3) {
                            Picasso.with(getActivity()).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(new ArrayList<ArticleListingResult>(dataList.subList(0, 3)));

                            Picasso.with(getActivity()).load(dataList.get(1).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(new ArrayList<ArticleListingResult>(dataList.subList(0, 3)));

                            Picasso.with(getActivity()).load(dataList.get(2).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles3.getArticleImageView());
                            relatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            relatedArticles3.setTag(new ArrayList<ArticleListingResult>(dataList.subList(0, 3)));
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
            if (response.body() == null) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
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
                        impressionList.addAll(dataList);
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
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    final ArrayList<String> previouslyFollowedTopics = (ArrayList<String>) responseData.getData();
                    final ArrayList<Map<String, String>> tagsList = detailData.getTags();
                    final ArrayList<String> sponsoredList = new ArrayList<>();
                    try {
                        createSponsporedTagsList(sponsoredList);
                        createArticleTags(previouslyFollowedTopics, tagsList, sponsoredList);
                    } catch (FileNotFoundException ffe) {
                        Retrofit retro = BaseApplication.getInstance().getRetrofit();
                        final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                        Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
                        caller.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());

                                try {
                                    createSponsporedTagsList(sponsoredList);
                                    createArticleTags(previouslyFollowedTopics, tagsList, sponsoredList);
                                } catch (FileNotFoundException e) {
                                    Crashlytics.logException(e);
                                    Log.d("FileNotFoundException", Log.getStackTraceString(e));
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Crashlytics.logException(t);
                                Log.d("MC4KException", Log.getStackTraceString(t));
                            }
                        });
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

    private void createSponsporedTagsList(ArrayList<String> sponsoredList) throws
            FileNotFoundException {
        FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
        String fileContent = AppUtils.convertStreamToString(fileInputStream);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
        TopicsResponse tRes = gson.fromJson(fileContent, TopicsResponse.class);
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
    }

    private void createArticleTags
            (ArrayList<String> previouslyFollowedTopics, ArrayList<Map<String, String>> tagsList, ArrayList<String> sponsoredList) {
        int relatedImageWidth = (int) BaseApplication.getAppContext().getResources().getDimension(R.dimen.related_article_article_image_width);
        viewAllTagsTextView.setVisibility(View.GONE);
        width = width - ((RelativeLayout.LayoutParams) tagsLayout.getLayoutParams()).leftMargin - ((RelativeLayout.LayoutParams) tagsLayout.getLayoutParams()).rightMargin;

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
                    ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.tick));
                    topicView.getChildAt(2).setBackgroundColor(ContextCompat.getColor(BaseApplication.getAppContext(), R.color.app_red));
                    ((ImageView) topicView.getChildAt(2)).setColorFilter(ContextCompat.getColor(BaseApplication.getAppContext(), R.color.white_color));
                    topicView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 0);
                        }
                    });
                } else {
                    ((ImageView) topicView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.follow_plus));
                    topicView.getChildAt(2).setBackgroundColor(ContextCompat.getColor(BaseApplication.getAppContext(), R.color.ad_tags_follow_bg));
                    ((ImageView) topicView.getChildAt(2)).setColorFilter(ContextCompat.getColor(BaseApplication.getAppContext(), R.color.app_red));
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
        loadGroupDataForCategories(tagsList);
    }

    private void loadGroupDataForCategories(ArrayList<Map<String, String>> tagsList) {
        ArrayList<String> categoriesList = new ArrayList<>();
        for (int i = 0; i < tagsList.size(); i++) {
            categoriesList.addAll(tagsList.get(i).keySet());
        }
        if (categoriesList.size() == 1) {
            GroupIdCategoryMap groupIdCategoryMap = new GroupIdCategoryMap(categoriesList.get(0), this, "details");
            groupIdCategoryMap.getGroupIdForCurrentCategory();
        } else {
            GroupIdCategoryMap groupIdCategoryMap = new GroupIdCategoryMap(categoriesList, this, "details");
            groupIdCategoryMap.getGroupIdForMultipleCategories();
        }
    }

    private void followUnfollowTopics(String selectedTopic, RelativeLayout tagView, int action) {
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryAPI = retro.create(TopicsCategoryAPI.class);
        FollowUnfollowCategoriesRequest followUnfollowCategoriesRequest = new FollowUnfollowCategoriesRequest();
        ArrayList<String> topicIdLList = new ArrayList<>();
        topicIdLList.add(selectedTopic);
        followUnfollowCategoriesRequest.setCategories(topicIdLList);
        if (action == 0) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("Topic", "" + selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
                jsonObject.put("ScreenName", "DetailArticleScreen");
                jsonObject.put("isFirstTimeUser", followTopicChangeNewUser);
                Log.d("UnfollowTopics", jsonObject.toString());
                mixpanel.track("UnfollowTopic", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Utils.pushUnfollowTopicEvent(getActivity(), "DetailArticleScreen", userDynamoId, selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
            tagView.getChildAt(0).setTag(selectedTopic);
            tagView.getChildAt(2).setTag(selectedTopic);
            ((ImageView) tagView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.follow_plus));
            tagView.getChildAt(2).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ad_tags_follow_bg));
            ((ImageView) tagView.getChildAt(2)).setColorFilter(ContextCompat.getColor(getActivity(), R.color.app_red));
            tagView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followUnfollowTopics((String) v.getTag(), (RelativeLayout) v.getParent(), 1);
                }
            });
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("Topic", "" + selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
                jsonObject.put("ScreenName", "DetailArticleScreen");
                jsonObject.put("isFirstTimeUser", followTopicChangeNewUser);
                Log.d("FollowTopics", jsonObject.toString());
                mixpanel.track("FollowTopic", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Utils.pushFollowTopicEvent(getActivity(), "DetailArticleScreen", userDynamoId, selectedTopic + "~" + ((TextView) tagView.getChildAt(0)).getText().toString());
            tagView.getChildAt(0).setTag(selectedTopic);
            tagView.getChildAt(2).setTag(selectedTopic);
            ((ImageView) tagView.getChildAt(2)).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.tick));
            tagView.getChildAt(2).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_red));
            ((ImageView) tagView.getChildAt(2)).setColorFilter(ContextCompat.getColor(getActivity(), R.color.white_color));
            tagView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
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

    private Callback<ArticleDetailResponse> isBookmarkedFollowedResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            if (null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
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
                        followClick.setText(AppUtils.getString(getActivity(), R.string.ad_follow_author));
                        isFollowing = false;
                    } else {
                        followClick.setEnabled(true);
                        followClick.setText(AppUtils.getString(getActivity(), R.string.ad_following_author));
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
            if (null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
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
            if (null == response.body()) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
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
            if (null == response.body()) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
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
            if (null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                RecommendUnrecommendArticleResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (!isAdded()) {
                        return;
                    }
                    if (null == responseData.getData() || responseData.getData().isEmpty()) {
                        ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
                    } else {

                        // ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
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
            } else {
            }
        }
    }

    public void handleExceptions(Throwable t) {
        if (isAdded()) {
            if (t instanceof UnknownHostException) {
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.error_network));
            } else if (t instanceof SocketTimeoutException) {
                ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.connection_timeout));
            } else {
            }
        }
        Crashlytics.logException(t);
        Log.d("MC4kException", Log.getStackTraceString(t));
    }

    private Callback<FollowUnfollowUserResponse> followUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response.body() == null) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followClick.setText(BaseApplication.getAppContext().getString(R.string.ad_follow_author));
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

    private Callback<FollowUnfollowUserResponse> unfollowUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response.body() == null) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followClick.setText(BaseApplication.getAppContext().getString(R.string.ad_following_author));
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            iSwipeRelated = (ISwipeRelated) activity;
        } catch (ClassCastException e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    public interface ISwipeRelated {
        void onRelatedSwipe(ArrayList<ArticleListingResult> articleList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(DeveloperKey.DEVELOPER_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubePlayerView;
    }

    private void tooltipForShare() {
        simpleTooltip = new SimpleTooltip.Builder(getContext())
                .anchorView(whatsappShareTextView)
                .backgroundColor(getResources().getColor(R.color.app_blue))
                .text(getResources().getString(R.string.ad_bottom_bar_generic_share))
                .textColor(getResources().getColor(R.color.white))
                .arrowColor(getResources().getColor(R.color.app_blue))
                .gravity(Gravity.TOP)
                .arrowWidth(60)
                .arrowHeight(20)
                .animated(false)
                .transparentOverlay(true)
                .build();
        simpleTooltip.show();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (simpleTooltip.isShowing())
                    simpleTooltip.dismiss();
            }
        }, 3000);

    }


}
