package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
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
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.ReportStoryOrCommentRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ArticleDetailResult;
import com.mycity4kids.models.response.ArticleDetailWebserviceResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.ArticleRecommendationStatusResponse;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.models.response.CrownDataResponse;
import com.mycity4kids.models.response.DeepLinkingResposnse;
import com.mycity4kids.models.response.FBCommentResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.LikeReactionModel;
import com.mycity4kids.models.response.MixFeedResponse;
import com.mycity4kids.models.response.MixFeedResult;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.ReportStoryOrCommentResponse;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.response.ViewCountResponse;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;
import com.mycity4kids.observablescrollview.ObservableScrollView;
import com.mycity4kids.observablescrollview.ObservableScrollViewCallbacks;
import com.mycity4kids.observablescrollview.ScrollState;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.DeepLinkingAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TorcaiAdsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.BadgeActivity;
import com.mycity4kids.ui.activity.CategoryVideosListingActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.FilteredTopicsArticleListingActivity;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.activity.ShortStoriesListingContainerActivity;
import com.mycity4kids.ui.activity.collection.CollectionsActivity;
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.ui.fragment.CommentOptionsDialogFragment.ICommentOptionAction;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.GroupIdCategoryMap;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.widget.CustomFontTextView;
import com.mycity4kids.widget.MomspressoButtonWidget;
import com.mycity4kids.widget.RelatedArticlesView;
import com.mycity4kids.widget.ShareButtonWidget;
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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        GroupMembershipStatus.IMembershipStatus, ICommentOptionAction {

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

    private ImageView facebookShareTextView;
    private ImageView whatsappShareTextView;
    private CustomFontTextView emailShareTextView;
    private ImageView likeArticleTextView;
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
    private TextView authorName;
    private ImageView authorImageViewFollowContainer;
    private TextView authorNameTextViewFollowContainer;
    private MomspressoButtonWidget followTextViewFollowContainer;
    private TextView postsCountTextView;
    private TextView likeCountTextView;
    private RelativeLayout userFollowViewContainer;
    private ImageView dot;
    private ShareButtonWidget writeCommentTextView;
    private TextView viewMoreTextView;
    private TextView commentsHeaderTextView;
    private RelativeLayout commentContainer;
    private RelativeLayout beTheFirstOneCommentContainer;
    private RelativeLayout commentContainer1;
    private RelativeLayout commentContainer2;
    private ImageView commentatorImageView1;
    private TextView commentatorNameAndCommentTextView1;
    private TextView commentDateTextView1;
    private TextView likeCount1;
    private TextView replyCount1;
    private ImageView commentatorImageView2;
    private TextView commentatorNameAndCommentTextView2;
    private TextView commentDateTextView2;
    private TextView likeCount2;
    private TextView replyCount2;
    private ArrayList<CommentListData> commentsList;
    private ArrayList<CommentsData> fbCommentsList;
    private RelativeLayout bookmarkOrMenuBarContainer;
    private ImageView bookmarkImageViewNew;
    private ImageView menuItemImageView;
    private CardView moreFromAuthorContainer1;
    private CardView moreFromAuthorContainer2;
    private CardView moreFromAuthorContainer3;
    private CardView moreFromAuthorContainer4;
    private CardView moreFromAuthorContainer5;
    private ImageView articleImageView1;
    private ImageView articleImageView2;
    private ImageView articleImageView3;
    private ImageView articleImageView4;
    private ImageView articleImageView5;
    private TextView txvArticleTitle1;
    private TextView txvArticleTitle2;
    private TextView txvArticleTitle3;
    private TextView txvArticleTitle4;
    private TextView txvArticleTitle5;
    private TextView viewCountTextView1;
    private TextView viewCountTextView2;
    private TextView viewCountTextView3;
    private TextView viewCountTextView4;
    private TextView viewCountTextView5;
    private TextView commentCountTextView1;
    private TextView commentCountTextView2;
    private TextView commentCountTextView3;
    private TextView commentCountTextView4;
    private TextView commentCountTextView5;
    private TextView recommendCountTextView1;
    private TextView recommendCountTextView2;
    private TextView recommendCountTextView3;
    private TextView recommendCountTextView4;
    private TextView recommendCountTextView5;
    private TextView moreFromAuthorTextView;
    private HorizontalScrollView relatedArticleHorizontalScrollView;
    private ArrayList<ArticleListingResult> relatedOrBloggerArticle;
    private ArrayList<MixFeedResult> todaysBestListData;
    private ScrollView todaysBestContainerLayout;
    private CardView todaysBestContainer1;
    private CardView todaysBestContainer2;
    private CardView todaysBestContainer3;
    private CardView todaysBestContainer4;
    private CardView todaysBestContainer5;
    private ImageView todaysBestImageView1;
    private ImageView todaysBestImageView2;
    private ImageView todaysBestImageView3;
    private ImageView todaysBestImageView4;
    private ImageView todaysBestImageView5;
    private TextView todaysBestArticleTextView1;
    private TextView todaysBestArticleTextView2;
    private TextView todaysBestArticleTextView3;
    private TextView todaysBestArticleTextView4;
    private TextView todaysBestArticleTextView5;
    private TextView authorName1;
    private TextView authorName2;
    private TextView authorName3;
    private TextView authorName4;
    private TextView authorName5;
    private TextView articleViewCountTextView1;
    private TextView articleViewCountTextView2;
    private TextView articleViewCountTextView3;
    private TextView articleViewCountTextView4;
    private TextView articleViewCountTextView5;
    private TextView articleCommentCountTextView1;
    private TextView articleCommentCountTextView2;
    private TextView articleCommentCountTextView3;
    private TextView articleCommentCountTextView4;
    private TextView articleCommentCountTextView5;
    private TextView articleRecommendationCountTextView1;
    private TextView articleRecommendationCountTextView2;
    private TextView articleRecommendationCountTextView3;
    private TextView articleRecommendationCountTextView4;
    private TextView articleRecommendationCountTextView5;
    private TextView bodyTextView1;
    private TextView bodyTextView2;
    private TextView bodyTextView3;
    private TextView bodyTextView4;
    private TextView bodyTextView5;
    private ImageView shareTodayBestArticle5;
    private ImageView shareTodayBestArticle4;
    private ImageView shareTodayBestArticle3;
    private ImageView shareTodayBestArticle2;
    private ImageView shareTodayBestArticle1;
    private ImageView bookmarkTodaysBestArticle1;
    private ImageView bookmarkTodaysBestArticle2;
    private ImageView bookmarkTodaysBestArticle3;
    private ImageView bookmarkTodaysBestArticle4;
    private ImageView bookmarkTodaysBestArticle5;
    private int todaysBestBookmarkIdIndex;
    private RelativeLayout userFollowView;
    private ImageView reportCommentContent2;
    private ImageView reportCommentContent1;
    private TextView publishedDateTextView;
    private MomspressoButtonWidget moreArticlesTextView;
    private MomspressoButtonWidget userTypeBadgeTextView;
    private int deleteCommentPosition;
    private int editCommentPosition;
    private String editContent;
    private ArticleCommentRepliesDialogFragment articleCommentRepliesDialogFragment;
    private String editReplyParentCommentId;
    private String replyId;


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

            moreArticlesTextView = fragmentView.findViewById(R.id.moreArticlesTextView);
            publishedDateTextView = fragmentView.findViewById(R.id.publishedDateTextView);
            reportCommentContent1 = fragmentView.findViewById(R.id.reportCommentContent1);
            reportCommentContent2 = fragmentView.findViewById(R.id.reportCommentContent2);
            moreFromAuthorTextView = fragmentView.findViewById(R.id.moreFromAuthorTextView);
            todaysBestContainerLayout = fragmentView.findViewById(R.id.todaysBestContainerLayout);
            userFollowView = fragmentView.findViewById(R.id.userFollowView);
            todaysBestContainer1 = fragmentView.findViewById(R.id.todaysBestContainer1);
            todaysBestContainer2 = fragmentView.findViewById(R.id.todaysBestContainer2);
            todaysBestContainer3 = fragmentView.findViewById(R.id.todaysBestContainer3);
            todaysBestContainer4 = fragmentView.findViewById(R.id.todaysBestContainer4);
            todaysBestContainer5 = fragmentView.findViewById(R.id.todaysBestContainer5);

            todaysBestImageView1 = fragmentView.findViewById(R.id.todaysBestImageView1);
            todaysBestImageView2 = fragmentView.findViewById(R.id.todaysBestImageView2);
            todaysBestImageView3 = fragmentView.findViewById(R.id.todaysBestImageView3);
            todaysBestImageView4 = fragmentView.findViewById(R.id.todaysBestImageView4);
            todaysBestImageView5 = fragmentView.findViewById(R.id.todaysBestImageView5);

            todaysBestArticleTextView1 = fragmentView.findViewById(R.id.todaysBestArticleTextView1);
            todaysBestArticleTextView2 = fragmentView.findViewById(R.id.todaysBestArticleTextView2);
            todaysBestArticleTextView3 = fragmentView.findViewById(R.id.todaysBestArticleTextView3);
            todaysBestArticleTextView4 = fragmentView.findViewById(R.id.todaysBestArticleTextView4);
            todaysBestArticleTextView5 = fragmentView.findViewById(R.id.todaysBestArticleTextView5);

            authorName1 = fragmentView.findViewById(R.id.authorName1);
            authorName2 = fragmentView.findViewById(R.id.authorName2);
            authorName3 = fragmentView.findViewById(R.id.authorName3);
            authorName4 = fragmentView.findViewById(R.id.authorName4);
            authorName5 = fragmentView.findViewById(R.id.authorName5);

            articleViewCountTextView1 = fragmentView.findViewById(R.id.articleViewCountTextView1);
            articleViewCountTextView2 = fragmentView.findViewById(R.id.articleViewCountTextView2);
            articleViewCountTextView3 = fragmentView.findViewById(R.id.articleViewCountTextView3);
            articleViewCountTextView4 = fragmentView.findViewById(R.id.articleViewCountTextView4);
            articleViewCountTextView5 = fragmentView.findViewById(R.id.articleViewCountTextView5);

            articleCommentCountTextView1 = fragmentView.findViewById(R.id.articleCommentCountTextView1);
            articleCommentCountTextView2 = fragmentView.findViewById(R.id.articleCommentCountTextView2);
            articleCommentCountTextView3 = fragmentView.findViewById(R.id.articleCommentCountTextView3);
            articleCommentCountTextView4 = fragmentView.findViewById(R.id.articleCommentCountTextView4);
            articleCommentCountTextView5 = fragmentView.findViewById(R.id.articleCommentCountTextView5);

            articleRecommendationCountTextView1 = fragmentView.findViewById(R.id.articleRecommendationCountTextView1);
            articleRecommendationCountTextView2 = fragmentView.findViewById(R.id.articleRecommendationCountTextView2);
            articleRecommendationCountTextView3 = fragmentView.findViewById(R.id.articleRecommendationCountTextView3);
            articleRecommendationCountTextView4 = fragmentView.findViewById(R.id.articleRecommendationCountTextView4);
            articleRecommendationCountTextView5 = fragmentView.findViewById(R.id.articleRecommendationCountTextView5);

            bodyTextView1 = fragmentView.findViewById(R.id.bodyTextView1);
            bodyTextView2 = fragmentView.findViewById(R.id.bodyTextView2);
            bodyTextView3 = fragmentView.findViewById(R.id.bodyTextView3);
            bodyTextView4 = fragmentView.findViewById(R.id.bodyTextView4);
            bodyTextView5 = fragmentView.findViewById(R.id.bodyTextView5);

            shareTodayBestArticle1 = fragmentView.findViewById(R.id.shareTodayBestArticle1);
            shareTodayBestArticle2 = fragmentView.findViewById(R.id.shareTodayBestArticle2);
            shareTodayBestArticle3 = fragmentView.findViewById(R.id.shareTodayBestArticle3);
            shareTodayBestArticle4 = fragmentView.findViewById(R.id.shareTodayBestArticle4);
            shareTodayBestArticle5 = fragmentView.findViewById(R.id.shareTodayBestArticle5);

            bookmarkTodaysBestArticle1 = fragmentView.findViewById(R.id.bookmarkTodaysBestArticle1);
            bookmarkTodaysBestArticle2 = fragmentView.findViewById(R.id.bookmarkTodaysBestArticle2);
            bookmarkTodaysBestArticle3 = fragmentView.findViewById(R.id.bookmarkTodaysBestArticle3);
            bookmarkTodaysBestArticle4 = fragmentView.findViewById(R.id.bookmarkTodaysBestArticle4);
            bookmarkTodaysBestArticle5 = fragmentView.findViewById(R.id.bookmarkTodaysBestArticle5);

            shareTodayBestArticle1 = fragmentView.findViewById(R.id.shareTodayBestArticle1);
            shareTodayBestArticle2 = fragmentView.findViewById(R.id.shareTodayBestArticle2);
            shareTodayBestArticle3 = fragmentView.findViewById(R.id.shareTodayBestArticle3);
            shareTodayBestArticle4 = fragmentView.findViewById(R.id.shareTodayBestArticle4);
            shareTodayBestArticle5 = fragmentView.findViewById(R.id.shareTodayBestArticle5);

            relatedArticleHorizontalScrollView = fragmentView.findViewById(R.id.relatedArticleHorizontalScrollView);
            txvArticleTitle1 = fragmentView.findViewById(R.id.txvArticleTitle1);
            txvArticleTitle2 = fragmentView.findViewById(R.id.txvArticleTitle2);
            txvArticleTitle3 = fragmentView.findViewById(R.id.txvArticleTitle3);
            txvArticleTitle4 = fragmentView.findViewById(R.id.txvArticleTitle4);
            txvArticleTitle5 = fragmentView.findViewById(R.id.txvArticleTitle5);
            viewCountTextView1 = fragmentView.findViewById(R.id.viewCountTextView1);
            viewCountTextView2 = fragmentView.findViewById(R.id.viewCountTextView2);
            viewCountTextView3 = fragmentView.findViewById(R.id.viewCountTextView3);
            viewCountTextView4 = fragmentView.findViewById(R.id.viewCountTextView4);
            viewCountTextView5 = fragmentView.findViewById(R.id.viewCountTextView5);
            recommendCountTextView1 = fragmentView.findViewById(R.id.recommendCountTextView1);
            recommendCountTextView2 = fragmentView.findViewById(R.id.recommendCountTextView2);
            recommendCountTextView3 = fragmentView.findViewById(R.id.recommendCountTextView3);
            recommendCountTextView4 = fragmentView.findViewById(R.id.recommendCountTextView4);
            recommendCountTextView5 = fragmentView.findViewById(R.id.recommendCountTextView5);
            commentCountTextView1 = fragmentView.findViewById(R.id.commentCountTextView1);
            commentCountTextView2 = fragmentView.findViewById(R.id.commentCountTextView2);
            commentCountTextView3 = fragmentView.findViewById(R.id.commentCountTextView3);
            commentCountTextView4 = fragmentView.findViewById(R.id.commentCountTextView4);
            commentCountTextView5 = fragmentView.findViewById(R.id.commentCountTextView5);
            articleImageView1 = fragmentView.findViewById(R.id.articleImageView1);
            articleImageView2 = fragmentView.findViewById(R.id.articleImageView2);
            articleImageView4 = fragmentView.findViewById(R.id.articleImageView4);
            articleImageView3 = fragmentView.findViewById(R.id.articleImageView3);
            articleImageView5 = fragmentView.findViewById(R.id.articleImageView5);
            moreFromAuthorContainer1 = fragmentView.findViewById(R.id.moreFromAuthorContainer1);
            moreFromAuthorContainer2 = fragmentView.findViewById(R.id.moreFromAuthorContainer2);
            moreFromAuthorContainer3 = fragmentView.findViewById(R.id.moreFromAuthorContainer3);
            moreFromAuthorContainer4 = fragmentView.findViewById(R.id.moreFromAuthorContainer4);
            moreFromAuthorContainer5 = fragmentView.findViewById(R.id.moreFromAuthorContainer5);
            menuItemImageView = fragmentView.findViewById(R.id.menuItemImageView);
            bookmarkImageViewNew = fragmentView.findViewById(R.id.bookmarkImageViewNew);
            bookmarkOrMenuBarContainer = fragmentView.findViewById(R.id.bookmarkOrMenuBarContainer);
            commentContainer1 = fragmentView.findViewById(R.id.commentContainer1);
            commentContainer2 = fragmentView.findViewById(R.id.commentContainer2);
            commentatorImageView1 = fragmentView.findViewById(R.id.commentatorImageView);
            commentatorNameAndCommentTextView1 = fragmentView.findViewById(R.id.commentatorNameAndCommentTextView);
            commentDateTextView1 = fragmentView.findViewById(R.id.commentDateTextView);
            likeCount1 = fragmentView.findViewById(R.id.likeCount);
            replyCount1 = fragmentView.findViewById(R.id.replyCount);
            commentatorImageView2 = fragmentView.findViewById(R.id.commentatorImageView2);
            commentatorNameAndCommentTextView2 = fragmentView.findViewById(R.id.commentatorNameAndCommentTextView2);
            commentDateTextView2 = fragmentView.findViewById(R.id.commentDateTextView2);
            likeCount2 = fragmentView.findViewById(R.id.likeCount2);
            replyCount2 = fragmentView.findViewById(R.id.replyCount2);

            beTheFirstOneCommentContainer = fragmentView.findViewById(R.id.beTheFirstOneCommentContainer);
            commentContainer = fragmentView.findViewById(R.id.commentContainer);
            commentsHeaderTextView = fragmentView.findViewById(R.id.commentsHeaderTextView);
            viewMoreTextView = fragmentView.findViewById(R.id.viewMoreTextView);
            writeCommentTextView = fragmentView.findViewById(R.id.writeCommentTextView);
            dot = fragmentView.findViewById(R.id.dot);
            authorImageViewFollowContainer = fragmentView.findViewById(R.id.authorImageViewFollowContainer);
            authorNameTextViewFollowContainer = fragmentView.findViewById(R.id.authorNameTextViewFollowContainer);
            followTextViewFollowContainer = fragmentView.findViewById(R.id.followTextViewFollowContainer);
            postsCountTextView = fragmentView.findViewById(R.id.postsCountTextView);
            likeCountTextView = fragmentView.findViewById(R.id.likeCountTextView);
            followPopUpBottomContainer = (RelativeLayout) fragmentView.findViewById(R.id.followPopUpBottomContainer);
            cancelFollowPopUp = (ImageView) fragmentView.findViewById(R.id.cancelFollowPopUp);
            authorNameFollowPopUp = (TextView) fragmentView.findViewById(R.id.authorNameFollowPopUp);
            authorImageViewFollowPopUp = (ImageView) fragmentView.findViewById(R.id.authorImageViewFollowPopUp);
            followText = (TextView) fragmentView.findViewById(R.id.followText);
            floatingActionButton = (ImageView) fragmentView.findViewById(R.id.user_image);
            mainWebView = (WebView) fragmentView.findViewById(R.id.articleWebView);
            viewAllTagsTextView = (TextView) fragmentView.findViewById(R.id.viewAllTagsTextView);
            bottomToolbarLL = (LinearLayout) fragmentView.findViewById(R.id.bottomToolbarLL);
            facebookShareTextView = (ImageView) fragmentView.findViewById(R.id.facebookShareTextView);
            whatsappShareTextView = (ImageView) fragmentView.findViewById(R.id.whatsappShareTextView);
            emailShareTextView = (CustomFontTextView) fragmentView.findViewById(R.id.emailShareTextView);
            likeArticleTextView = (ImageView) fragmentView.findViewById(R.id.likeTextView);
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
            authorName = fragmentView.findViewById(R.id.authorName);
            userTypeBadgeTextView = fragmentView.findViewById(R.id.userTypeBadgeTextView);

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
            authorName.setOnClickListener(this);
            writeCommentTextView.setOnClickListener(this);
            viewMoreTextView.setOnClickListener(this);
            beTheFirstOneCommentContainer.setOnClickListener(this);
            replyCount1.setOnClickListener(this);
            replyCount2.setOnClickListener(this);
            likeCount1.setOnClickListener(this);
            likeCount2.setOnClickListener(this);
            bookmarkImageViewNew.setOnClickListener(this);
            menuItemImageView.setOnClickListener(this);
            moreFromAuthorContainer1.setOnClickListener(this);
            moreFromAuthorContainer2.setOnClickListener(this);
            moreFromAuthorContainer3.setOnClickListener(this);
            moreFromAuthorContainer4.setOnClickListener(this);
            moreFromAuthorContainer5.setOnClickListener(this);
            todaysBestContainer1.setOnClickListener(this);
            todaysBestContainer2.setOnClickListener(this);
            todaysBestContainer3.setOnClickListener(this);
            todaysBestContainer4.setOnClickListener(this);
            todaysBestContainer5.setOnClickListener(this);
            bookmarkTodaysBestArticle1.setOnClickListener(this);
            bookmarkTodaysBestArticle2.setOnClickListener(this);
            bookmarkTodaysBestArticle3.setOnClickListener(this);
            bookmarkTodaysBestArticle4.setOnClickListener(this);
            bookmarkTodaysBestArticle5.setOnClickListener(this);//shareTodayBestArticle1
            shareTodayBestArticle1.setOnClickListener(this);
            shareTodayBestArticle2.setOnClickListener(this);
            shareTodayBestArticle3.setOnClickListener(this);
            shareTodayBestArticle4.setOnClickListener(this);
            shareTodayBestArticle5.setOnClickListener(this);
            followTextViewFollowContainer.setOnClickListener(this);
            userFollowView.setOnClickListener(this);
            reportCommentContent1.setOnClickListener(this);
            reportCommentContent2.setOnClickListener(this);
            moreArticlesTextView.setOnClickListener(this);

            mainWebChromeClient = new MyWebChromeClient();
            mainWebView.setWebChromeClient(mainWebChromeClient);
            mainWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    try {
                        if (request.getUrl() == null || request.getUrl().toString().isEmpty()) {
                            return true;
                        }
                        if (AppUtils.isMomspressoDomain(request.getUrl().toString())) {
                            handleDeeplinks(request.getUrl().toString());
                        } else {
                            if (getActivity() != null) {
                                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                CustomTabsIntent customTabsIntent = builder.build();
                                customTabsIntent.launchUrl(getActivity(), request.getUrl());
                            }
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4KException", Log.getStackTraceString(e));
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
                FirebaseCrashlytics.getInstance().recordException(e);
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
                            FirebaseCrashlytics.getInstance().recordException(e);
                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        } catch (Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            Log.d("MC4KException", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        FirebaseCrashlytics.getInstance().recordException(t);
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
                getTodaysBestArticles();
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
            getComments();
            scrollBounds = new Rect();
            observableScrollView.getHitRect(scrollBounds);
        } catch (Exception e) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        return fragmentView;
    }


    private void handleDeeplinks(String tempDeepLinkUrl) {
        String tempDeepLinkUrlWithoutSlash = "";
        if ((tempDeepLinkUrl.endsWith("/"))) {
            tempDeepLinkUrlWithoutSlash = tempDeepLinkUrl.substring(0, tempDeepLinkUrl.lastIndexOf("/"));
        }
        if (matchRegex(tempDeepLinkUrl)) {
            // need to optimize this code
        } else if (tempDeepLinkUrl.equals(AppConstants.MOM_VLOG_LISTING_SCREEN) || tempDeepLinkUrlWithoutSlash
                .equals(AppConstants.MOM_VLOG_LISTING_SCREEN)) {
            Intent intent = new Intent(getActivity(), CategoryVideosListingActivity.class);
            startActivity(intent);
        } else if (tempDeepLinkUrl.equals(AppConstants.SHORT_STORY_LISTING_SCREEN)
                || tempDeepLinkUrlWithoutSlash
                .equals(AppConstants.SHORT_STORY_LISTING_SCREEN)) {
            Intent intent = new Intent(getActivity(), ShortStoriesListingContainerActivity.class);
            startActivity(intent);
        } else if (tempDeepLinkUrl.equals(AppConstants.VLOG_CHALLENGES_BASE_SHARE_URL) || tempDeepLinkUrl
                .equals(AppConstants.VLOG_CHALLENGES_BASE_SHARE_URL
                        .substring(0, AppConstants.VLOG_CHALLENGES_BASE_SHARE_URL.length() - 1))) {
            Intent intent = new Intent(getActivity(), CategoryVideosListingActivity.class);
            intent.putExtra("categoryId", AppConstants.VIDEO_CHALLENGE_ID);
            startActivity(intent);
        } else if (tempDeepLinkUrl.startsWith(AppConstants.VLOG_CHALLENGES_BASE_SHARE_URL)) {
            String challengeId = tempDeepLinkUrl.replace(AppConstants.VLOG_CHALLENGES_BASE_SHARE_URL, "")
                    .replace("/", "");
            Intent intent = new Intent(getActivity(), NewVideoChallengeActivity.class);
            intent.putExtra(Constants.CHALLENGE_ID, challengeId);
            intent.putExtra("comingFrom", "deeplink");
            startActivity(intent);
        } else if (tempDeepLinkUrl.endsWith(AppConstants.DEEPLINK_SELF_PROFILE_URL_1)
                || tempDeepLinkUrl.endsWith(AppConstants.DEEPLINK_SELF_PROFILE_URL_2)) {
            Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
            startActivity(profileIntent);
        }
    }

    private Boolean matchRegex(String tempDeepLinkUrl) {
        try {
            String urlWithNoParams = tempDeepLinkUrl.split("\\?")[0];
            if (urlWithNoParams.endsWith("/")) {
                urlWithNoParams = urlWithNoParams.substring(0, urlWithNoParams.length() - 1);
            }
            Pattern pattern = Pattern.compile(AppConstants.COLLECTION_LIST_REGEX);
            Matcher matcher = pattern.matcher(urlWithNoParams);
            if (matcher.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(getActivity(), CollectionsActivity.class);
                intent.putExtra("userId", separated[separated.length - 2]);
                startActivity(intent);
                return true;
            }

            Pattern pattern1 = Pattern.compile(AppConstants.COLLECTION_DETAIL_REGEX);
            Matcher matcher1 = pattern1.matcher(urlWithNoParams);
            if (matcher1.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(getActivity(), UserCollectionItemListActivity.class);
                intent.putExtra("id", separated[separated.length - 1]);
                startActivity(intent);
                return true;
            }

            Pattern pattern2 = Pattern.compile(AppConstants.BADGES_LISTING_REGEX);
            Matcher matcher2 = pattern2.matcher(urlWithNoParams);
            if (matcher2.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(getActivity(), BadgeActivity.class);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 2]);
                startActivity(intent);
                return true;
            }

            Pattern pattern3 = Pattern.compile(AppConstants.BADGES_DETAIL_REGEX);
            Matcher matcher3 = pattern3.matcher(urlWithNoParams);
            if (matcher3.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra(AppConstants.BADGE_ID, separated[separated.length - 1]);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 3]);
                startActivity(intent);
                return true;
            }

            Pattern pattern4 = Pattern.compile(AppConstants.MILESTONE_DETAIL_REGEX);
            Matcher matcher4 = pattern4.matcher(urlWithNoParams);
            if (matcher4.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra(AppConstants.MILESTONE_ID, separated[separated.length - 1]);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 3]);
                startActivity(intent);
                return true;
            }

            Pattern pattern5 = Pattern.compile(AppConstants.USER_PROFILE_REGEX);
            Matcher matcher5 = pattern5.matcher(urlWithNoParams);
            if (matcher5.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 1]);
                startActivity(intent);
                return true;
            }

            Pattern pattern6 = Pattern.compile(AppConstants.USER_ANALYTICS_REGEX);
            Matcher matcher6 = pattern6.matcher(urlWithNoParams);
            if (matcher6.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra("detail", "rank");
                intent.putExtra(Constants.USER_ID, separated[separated.length - 2]);
                startActivity(intent);
                return true;
            }

            Pattern pattern7 = Pattern.compile(AppConstants.ARTICLE_DETAIL_REGEX);
            Pattern pattern8 = Pattern.compile(AppConstants.EDITORIAL_ARTICLE_DETAIL_REGEX);
            Matcher matcher7 = pattern7.matcher(urlWithNoParams);
            Matcher matcher8 = pattern8.matcher(urlWithNoParams);
            if (matcher7.matches() || matcher8.matches()) {
                getDeepLinkData(urlWithNoParams);
                return true;
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        return false;
    }

    private void getTodaysBestArticles() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryApi = retrofit.create(TopicsCategoryAPI.class);
        Call<MixFeedResponse> todaysBestCall = topicsCategoryApi
                .getTodaysBestMixedFeed(DateTimeUtils.getKidsDOBNanoMilliTimestamp("" + System.currentTimeMillis()), 1,
                        6,
                        SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()), "0");
        todaysBestCall.enqueue(new Callback<MixFeedResponse>() {
            @Override
            public void onResponse(Call<MixFeedResponse> call, Response<MixFeedResponse> response) {
                if (null == response.body()) {
                    return;
                }
                try {
                    MixFeedResponse responseData = response.body();
                    if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                        setDataIntoUi(responseData);
                    }
                } catch (Exception t) {
                    FirebaseCrashlytics.getInstance()
                            .recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            }

            @Override
            public void onFailure(Call<MixFeedResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });

    }

    private void setDataIntoUi(MixFeedResponse todaysBestData) {
        todaysBestListData = new ArrayList<>();
        if (null != todaysBestData.getData() && null != todaysBestData.getData().getResult()) {
            for (int i = 0; i < todaysBestData.getData().getResult().size(); i++) {
                if (!articleId.equals(todaysBestData.getData().getResult().get(i).getId())) {
                    todaysBestListData.add(todaysBestData.getData().getResult().get(i));
                }
            }

            if (todaysBestListData.size() >= 1) {
                todaysBestContainer1.setVisibility(View.VISIBLE);
                setDataIntoTodayBestContainer(todaysBestListData.get(0), todaysBestImageView1,
                        todaysBestArticleTextView1, authorName1, articleViewCountTextView1,
                        articleCommentCountTextView1, articleRecommendationCountTextView1, bodyTextView1,
                        bookmarkTodaysBestArticle1);
            }
            if (todaysBestListData.size() >= 2) {
                todaysBestContainer2.setVisibility(View.VISIBLE);
                setDataIntoTodayBestContainer(todaysBestListData.get(1), todaysBestImageView2,
                        todaysBestArticleTextView2, authorName2, articleViewCountTextView2,
                        articleCommentCountTextView2, articleRecommendationCountTextView2, bodyTextView2,
                        bookmarkTodaysBestArticle2);
            }

            if (todaysBestListData.size() >= 3) {
                todaysBestContainer3.setVisibility(View.VISIBLE);
                setDataIntoTodayBestContainer(todaysBestListData.get(2), todaysBestImageView3,
                        todaysBestArticleTextView3, authorName3, articleViewCountTextView3,
                        articleCommentCountTextView3, articleRecommendationCountTextView3, bodyTextView3,
                        bookmarkTodaysBestArticle3);
            }

            if (todaysBestListData.size() >= 4) {
                todaysBestContainer4.setVisibility(View.VISIBLE);
                setDataIntoTodayBestContainer(todaysBestListData.get(3), todaysBestImageView4,
                        todaysBestArticleTextView4, authorName4, articleViewCountTextView4,
                        articleCommentCountTextView4, articleRecommendationCountTextView4, bodyTextView4,
                        bookmarkTodaysBestArticle4);
            }
            if (todaysBestListData.size() >= 5) {
                todaysBestContainer5.setVisibility(View.VISIBLE);
                setDataIntoTodayBestContainer(todaysBestListData.get(4), todaysBestImageView5,
                        todaysBestArticleTextView5, authorName5, articleViewCountTextView5,
                        articleCommentCountTextView5, articleRecommendationCountTextView5, bodyTextView5,
                        bookmarkTodaysBestArticle5);
            }

        }


    }

    private void setDataIntoTodayBestContainer(MixFeedResult dataList, ImageView coverImageView, TextView title,
            TextView authorName, TextView viewCount, TextView commentCount, TextView likeCount, TextView body,
            ImageView bookmarkImageView) {

        Picasso.get().load(dataList.getImageUrl().getThumbMax())
                .error(R.drawable.default_article).into(coverImageView);
        title.setText(dataList.getTitle());
        authorName.setText(dataList.getUserName());
        viewCount
                .setText(AppUtils.withSuffix(Long.parseLong(dataList.getArticleCount() + "")));
        commentCount.setText(
                AppUtils.withSuffix(Long.parseLong(dataList.getCommentsCount() + "")));
        likeCount
                .setText(AppUtils.withSuffix(Long.parseLong(dataList.getLikesCount() + "")));
        body.setText(dataList.getExcerpt());
        if (dataList.getIsbookmark() == 1) {
            Drawable top = ContextCompat.getDrawable(bookmarkImageView.getContext(), R.drawable.ic_bookmarked);
            top.setColorFilter(ContextCompat.getColor(bookmarkImageView.getContext(), R.color.app_red),
                    PorterDuff.Mode.SRC_IN);
            bookmarkImageView.setImageDrawable(top);
        } else {
            Drawable top = ContextCompat.getDrawable(bookmarkImageView.getContext(), R.drawable.ic_bookmark);
            top.setColorFilter(ContextCompat.getColor(bookmarkImageView.getContext(), R.color.grey),
                    PorterDuff.Mode.SRC_IN);
            bookmarkImageView.setImageDrawable(top);
        }

    }

    private void getUsersData() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardApi = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardApi.getBloggerData(authorId);
        call.enqueue(userDetailsResponseListener);

    }

    private Callback<UserDetailResponse> userDetailsResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call,
                retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (null == response.body()) {
                return;
            }
            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS
                        .equals(responseData.getStatus())) {
                    if (responseData.getData() != null && responseData.getData().get(0) != null
                            && responseData.getData().get(0).getResult() != null) {
                        if ("0".equals(responseData.getData().get(0).getResult().getTotalArticles())) {
                            postsCountTextView.setVisibility(View.GONE);
                            dot.setVisibility(View.GONE);
                        } else {
                            postsCountTextView
                                    .setText(responseData.getData().get(0).getResult().getTotalArticles() + " "
                                            + "Posts");
                        }
                        if ("0".equals(responseData.getData().get(0).getResult().getTotalArticlesViews())) {
                            likeCountTextView.setVisibility(View.GONE);
                            dot.setVisibility(View.GONE);

                        } else if ("1".equals(responseData.getData().get(0).getResult().getTotalArticlesViews())) {
                            likeCountTextView
                                    .setText(responseData.getData().get(0).getResult().getTotalArticlesViews()
                                            + " " + "View");
                        } else {
                            likeCountTextView
                                    .setText(AppUtils.withSuffix(Long.parseLong(
                                            responseData.getData().get(0).getResult().getTotalArticlesViews()))
                                            + " " + "Views");
                        }
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance()
                    .recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void getComments() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
        Call<CommentListResponse> call = articleDetailsApi.getArticleComments(articleId, null, null);
        call.enqueue(ssCommentsResponseCallback);
    }

    private Callback<CommentListResponse> ssCommentsResponseCallback = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            if (response == null || response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("New comments API failure");
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }

            try {
                CommentListResponse commentListResponse = response.body();
                commentsList = new ArrayList<>(commentListResponse.getData());
                if (commentListResponse.getCount() != 0) {
                    showComments(commentListResponse.getData());
                    reportCommentContent1.setVisibility(View.VISIBLE);
                    reportCommentContent2.setVisibility(View.VISIBLE);
                    commentContainer.setVisibility(View.VISIBLE);
                } else {
                    reportCommentContent1.setVisibility(View.GONE);
                    reportCommentContent2.setVisibility(View.GONE);
                    getFbCommentsApi();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void getFbCommentsApi() {
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsApi = retro.create(ArticleDetailsAPI.class);
        Call<FBCommentResponse> call = articleDetailsApi.getFBComments(articleId, null);
        call.enqueue(fbCommentsCallback);
    }

    Callback<FBCommentResponse> fbCommentsCallback = new Callback<FBCommentResponse>() {
        @Override
        public void onResponse(Call<FBCommentResponse> call, retrofit2.Response<FBCommentResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                if (isAdded()) {
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
                ;
                return;
            }

            try {
                FBCommentResponse responseData = response.body();
                fbCommentsList = new ArrayList<>();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (null != responseData.getData().getResult() && !responseData.getData().getResult().isEmpty()) {
                        fbCommentsList = responseData.getData().getResult();
                        showFbComments(fbCommentsList);
                        commentContainer.setVisibility(View.VISIBLE);
                    } else {
                        commentContainer.setVisibility(View.GONE);
                        viewMoreTextView.setVisibility(View.GONE);
                        beTheFirstOneCommentContainer.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (isAdded()) {
                        ((ArticleDetailsContainerActivity) getActivity())
                                .showToast(getString(R.string.server_went_wrong));
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded()) {
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            }
        }

        @Override
        public void onFailure(Call<FBCommentResponse> call, Throwable e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    };

    private void showComments(List<CommentListData> commentsList) {
        if (commentsList.size() == 1) {
            commentContainer2.setVisibility(View.GONE);
            viewMoreTextView.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(commentsList.get(0).getUserPic().getClientApp())
                        .error(R.drawable.default_commentor_img).into(commentatorImageView1);
            } catch (Exception e) {
                commentatorImageView1.setImageResource(R.drawable.default_commentor_img);
            }
            commentatorNameAndCommentTextView1.setText((Html
                    .fromHtml(
                            "<b>" + "<font color=\"#D54058\">" + commentsList.get(0).getUserName() + "</font>" + "</b>"
                                    + " "
                                    + "<font color=\"#4A4A4A\">" + commentsList.get(0).getMessage() + "</font>")));

            commentDateTextView1.setText(
                    DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(commentsList.get(0).getCreatedTime())));
            if (commentsList.get(0).getLiked()) {
                Drawable myDrawable = ContextCompat
                        .getDrawable(likeCount1.getContext(), R.drawable.ic_recommended);
                myDrawable
                        .setColorFilter(ContextCompat.getColor(likeCount1.getContext(), R.color.app_red),
                                PorterDuff.Mode.SRC_IN);
                likeCount1.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            } else {
                Drawable myDrawable = ContextCompat
                        .getDrawable(likeCount1.getContext(), R.drawable.ic_recommend);
                myDrawable.setColorFilter(ContextCompat.getColor(likeCount1.getContext(), R.color.grey),
                        PorterDuff.Mode.SRC_IN);
                likeCount1.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            }
            if (commentsList.get(0).getLikeCount() == 0) {
                likeCount1.setText("");

            } else {
                likeCount1.setText(commentsList.get(0).getLikeCount() + "");
            }
            if (commentsList.get(0).getRepliesCount() == 0) {
                replyCount1.setText("Reply");
            } else {
                replyCount1.setText("Reply(" + commentsList.get(0).getRepliesCount() + ")");
            }

        } else if (commentsList.size() == 2) {
            viewMoreTextView.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(commentsList.get(0).getUserPic().getClientApp())
                        .error(R.drawable.default_commentor_img).into(commentatorImageView1);
            } catch (Exception e) {
                commentatorImageView1.setImageResource(R.drawable.default_commentor_img);
            }
            try {
                Picasso.get().load(commentsList.get(1).getUserPic().getClientApp())
                        .error(R.drawable.default_commentor_img).into(commentatorImageView2);
            } catch (Exception e) {
                commentatorImageView1.setImageResource(R.drawable.default_commentor_img);
            }
            commentatorNameAndCommentTextView1.setText((Html
                    .fromHtml(
                            "<b>" + "<font color=\"#D54058\">" + commentsList.get(0).getUserName() + "</font>" + "</b>"
                                    + " "
                                    + "<font color=\"#4A4A4A\">" + commentsList.get(0).getMessage() + "</font>")));
            commentDateTextView1.setText(
                    DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(commentsList.get(0).getCreatedTime())));

            if (commentsList.get(0).getLiked()) {
                Drawable myDrawable = ContextCompat
                        .getDrawable(likeCount1.getContext(), R.drawable.ic_recommended).mutate();
                myDrawable.setColorFilter(getResources().getColor(R.color.app_red), PorterDuff.Mode.SRC_IN);
                likeCount1.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            } else {

                Drawable myDrawable = ContextCompat
                        .getDrawable(likeCount1.getContext(), R.drawable.ic_recommend).mutate();
                myDrawable.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
                likeCount1.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            }
            if (commentsList.get(0).getLikeCount() == 0) {
                likeCount1.setText("");

            } else {
                likeCount1.setText(commentsList.get(0).getLikeCount() + "");
            }
            if (commentsList.get(0).getRepliesCount() == 0) {
                replyCount1.setText("Reply");
            } else {
                replyCount1.setText("Reply(" + commentsList.get(0).getRepliesCount() + ")");
            }

            commentatorNameAndCommentTextView2.setText((Html
                    .fromHtml(
                            "<b>" + "<font color=\"#D54058\">" + commentsList.get(1).getUserName() + "</font>" + "</b>"
                                    + " "
                                    + "<font color=\"#4A4A4A\">" + commentsList.get(1).getMessage() + "</font>")));
            commentDateTextView2.setText(
                    DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(commentsList.get(1).getCreatedTime())));

            if (commentsList.get(1).getLiked()) {
                Drawable myDrawable = ContextCompat
                        .getDrawable(getActivity(), R.drawable.ic_recommended);
                myDrawable.setColorFilter(getResources().getColor(R.color.app_red), PorterDuff.Mode.SRC_IN);
                likeCount2.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            } else {
                Drawable myDrawable = ContextCompat
                        .getDrawable(getActivity(), R.drawable.ic_recommend);
                myDrawable.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
                likeCount2.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            }
            if (commentsList.get(1).getLikeCount() == 0) {
                likeCount2.setText("");
            } else {
                likeCount2.setText(commentsList.get(0).getLikeCount() + "");
            }
            if (commentsList.get(1).getRepliesCount() == 0) {
                replyCount2.setText("Reply");
            } else {
                replyCount2.setText("Reply(" + commentsList.get(1).getRepliesCount() + ")");
            }

        } else {
            viewMoreTextView.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(commentsList.get(0).getUserPic().getClientApp())
                        .error(R.drawable.default_commentor_img).into(commentatorImageView1);
            } catch (Exception e) {
                commentatorImageView1.setImageResource(R.drawable.default_commentor_img);
            }
            try {
                Picasso.get().load(commentsList.get(1).getUserPic().getClientApp())
                        .error(R.drawable.default_commentor_img).into(commentatorImageView2);
            } catch (Exception e) {
                commentatorImageView2.setImageResource(R.drawable.default_commentor_img);
            }
            commentatorNameAndCommentTextView1.setText((Html
                    .fromHtml("<b>" + "<font color=\"#D54058\">" + commentsList.get(0).getUserName() + "</font>"
                            + "</b>"
                            + " "
                            + "<font color=\"#4A4A4A\">" + commentsList.get(0).getMessage() + "</font>")));
            commentDateTextView1.setText(
                    DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(commentsList.get(0).getCreatedTime())));
            if (commentsList.get(0).getLiked()) {
                Drawable myDrawable = ContextCompat
                        .getDrawable(likeCount1.getContext(), R.drawable.ic_recommended);
                myDrawable.setColorFilter(getResources().getColor(R.color.app_red), PorterDuff.Mode.SRC_IN);
                likeCount1.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            } else {
                Drawable myDrawable = ContextCompat
                        .getDrawable(likeCount1.getContext(), R.drawable.ic_recommend);
                myDrawable.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
                likeCount1.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            }
            if (commentsList.get(0).getLikeCount() == 0) {
                likeCount1.setText("");

            } else {
                likeCount1.setText(commentsList.get(0).getLikeCount() + "");

            }
            if (commentsList.get(0).getRepliesCount() == 0) {
                replyCount1.setText("Reply");
            } else {
                replyCount1.setText("Reply(" + commentsList.get(0).getRepliesCount() + ")");
            }
            commentatorNameAndCommentTextView2.setText((Html
                    .fromHtml("<b>" + "<font color=\"#D54058\"></bold>" + commentsList.get(1).getUserName() + "</font>"
                            + "</b>"
                            + " "
                            + "<font color=\"#4A4A4A\">" + commentsList.get(1).getMessage() + "</font>")));
            commentDateTextView2.setText(
                    DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(commentsList.get(1).getCreatedTime())));
            if (commentsList.get(1).getLiked()) {
                Drawable myDrawable = ContextCompat
                        .getDrawable(likeCount2.getContext(), R.drawable.ic_recommended);
                myDrawable.setColorFilter(getResources().getColor(R.color.app_red), PorterDuff.Mode.SRC_IN);
                likeCount2.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            } else {

                Drawable myDrawable = ContextCompat
                        .getDrawable(likeCount2.getContext(), R.drawable.ic_recommend);
                myDrawable.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
                likeCount2.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
            }
            if (commentsList.get(1).getLikeCount() == 0) {
                likeCount2.setText("");

            } else {
                likeCount2.setText(commentsList.get(0).getLikeCount() + "");

            }
            if (commentsList.get(1).getRepliesCount() == 0) {
                replyCount2.setText("Reply");
            } else {
                replyCount2.setText("Reply(" + commentsList.get(1).getRepliesCount() + ")");
            }
        }
    }

    private void showFbComments(List<CommentsData> fbCommentsList) {
        likeCount1.setVisibility(View.GONE);
        likeCount2.setVisibility(View.GONE);
        if (fbCommentsList.size() == 1) {
            commentContainer2.setVisibility(View.GONE);
            viewMoreTextView.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(fbCommentsList.get(0).getProfile_image().getClientApp())
                        .error(R.drawable.default_commentor_img).into(commentatorImageView1);
            } catch (Exception e) {
                commentatorImageView1.setImageResource(R.drawable.default_commentor_img);
            }
            commentatorNameAndCommentTextView1.setText((Html
                    .fromHtml(
                            "<b>" + "<font color=\"#D54058\">" + fbCommentsList.get(0).getName() + "</font>" + "</b>"
                                    + " "
                                    + "<font color=\"#4A4A4A\">" + fbCommentsList.get(0).getBody() + "</font>")));

            commentDateTextView1.setText(
                    DateTimeUtils
                            .getDateFromNanoMilliTimestamp(Long.parseLong(fbCommentsList.get(0).getCreate())));
            if (null == fbCommentsList.get(0).getReplies() || fbCommentsList.get(0).getReplies().size() == 0) {
                replyCount1.setVisibility(View.GONE);
            } else {
                replyCount1.setText("Reply(" + fbCommentsList.get(0).getReplies().size() + ")");
            }

        } else if (fbCommentsList.size() == 2) {
            viewMoreTextView.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(fbCommentsList.get(0).getProfile_image().getClientApp())
                        .error(R.drawable.default_commentor_img).into(commentatorImageView1);
            } catch (Exception e) {
                commentatorImageView1.setImageResource(R.drawable.default_commentor_img);
            }
            try {
                Picasso.get().load(fbCommentsList.get(1).getProfile_image().getClientApp())
                        .error(R.drawable.default_commentor_img).into(commentatorImageView2);
            } catch (Exception e) {
                commentatorImageView1.setImageResource(R.drawable.default_commentor_img);
            }
            commentatorNameAndCommentTextView1.setText((Html
                    .fromHtml(
                            "<b>" + "<font color=\"#D54058\">" + fbCommentsList.get(0).getName() + "</font>" + "</b>"
                                    + " "
                                    + "<font color=\"#4A4A4A\">" + fbCommentsList.get(0).getBody() + "</font>")));
            commentDateTextView1.setText(
                    DateTimeUtils
                            .getDateFromNanoMilliTimestamp(Long.parseLong(fbCommentsList.get(0).getCreate())));
            if (null == fbCommentsList.get(0).getReplies() || fbCommentsList.get(0).getReplies().size() == 0) {
                replyCount1.setVisibility(View.GONE);
            } else {
                replyCount1.setText("Reply(" + fbCommentsList.get(0).getReplies().size() + ")");
            }

            commentatorNameAndCommentTextView2.setText((Html
                    .fromHtml(
                            "<b>" + "<font color=\"#D54058\">" + fbCommentsList.get(1).getName() + "</font>" + "</b>"
                                    + " "
                                    + "<font color=\"#4A4A4A\">" + fbCommentsList.get(1).getBody() + "</font>")));
            commentDateTextView2.setText(
                    DateTimeUtils
                            .getDateFromNanoMilliTimestamp(Long.parseLong(fbCommentsList.get(1).getCreate())));

            if (null == fbCommentsList.get(1).getReplies() || fbCommentsList.get(1).getReplies().size() == 0) {
                replyCount2.setVisibility(View.GONE);
            } else {
                replyCount2.setText("Reply(" + fbCommentsList.get(1).getReplies().size() + ")");
            }

        } else {
            viewMoreTextView.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(fbCommentsList.get(0).getProfile_image().getClientApp())
                        .error(R.drawable.default_commentor_img).into(commentatorImageView1);
            } catch (Exception e) {
                commentatorImageView1.setImageResource(R.drawable.default_commentor_img);
            }
            try {
                Picasso.get().load(fbCommentsList.get(1).getProfile_image().getClientApp())
                        .error(R.drawable.default_commentor_img).into(commentatorImageView2);
            } catch (Exception e) {
                commentatorImageView1.setImageResource(R.drawable.default_commentor_img);
            }
            commentatorNameAndCommentTextView1.setText((Html
                    .fromHtml("<b>" + "<font color=\"#D54058\">" + fbCommentsList.get(0).getName() + "</font>"
                            + "</b>"
                            + " "
                            + "<font color=\"#4A4A4A\">" + fbCommentsList.get(0).getBody() + "</font>")));
            commentDateTextView1.setText(
                    DateTimeUtils
                            .getDateFromNanoMilliTimestamp(Long.parseLong(fbCommentsList.get(0).getCreate())));
            if (null == fbCommentsList.get(0).getReplies() || fbCommentsList.get(0).getReplies().size() == 0) {
                replyCount1.setVisibility(View.GONE);
            } else {
                replyCount1.setText("Reply(" + fbCommentsList.get(0).getReplies().size() + ")");
            }
            commentatorNameAndCommentTextView2.setText((Html
                    .fromHtml("<b>" + "<font color=\"#D54058\"></bold>" + fbCommentsList.get(1).getName() + "</font>"
                            + "</b>"
                            + " "
                            + "<font color=\"#4A4A4A\">" + fbCommentsList.get(1).getBody() + "</font>")));
            commentDateTextView2.setText(
                    DateTimeUtils
                            .getDateFromNanoMilliTimestamp(Long.parseLong(fbCommentsList.get(1).getCreate())));
            if (null == fbCommentsList.get(0).getReplies() || fbCommentsList.get(1).getReplies().size() == 0) {
                replyCount2.setVisibility(View.GONE);
            } else {
                replyCount2.setText("Reply(" + fbCommentsList.get(1).getReplies().size() + ")");
            }
        }
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
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("FileNotFoundException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            topAdSlotWebView.setVisibility(View.GONE);
            FirebaseCrashlytics.getInstance().recordException(t);
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
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("FileNotFoundException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            bottomAdSlotWebView.setVisibility(View.GONE);
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("FileNotFoundException", Log.getStackTraceString(t));
        }
    };

    private void getDeepLinkData(String url) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        showProgressDialog("");
        DeepLinkingAPI deepLinkingApi = retrofit.create(DeepLinkingAPI.class);
        Call<DeepLinkingResposnse> call = deepLinkingApi.getUrlDetails(url);
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
                        }
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<DeepLinkingResposnse> call, Throwable t) {
                removeProgressDialog();
                FirebaseCrashlytics.getInstance().recordException(t);
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
        Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsApi
                .getPublishedArticles(authorId, 0, 1, 6);
        callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
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
                    userTypeBadgeTextView.setVisibility(View.VISIBLE);
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
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

        if (!StringUtils.isNullOrEmpty(detailData.getUserName())) {
            ((TextView) fragmentView.findViewById(R.id.user_name))
                    .setText(detailData.getUserName());
            authorName.setText(detailData.getUserName());
            authorNameTextViewFollowContainer.setText(detailData.getUserName());
        }

        if (!StringUtils.isNullOrEmpty(detailData.getCreated())) {
            ((TextView) fragmentView.findViewById(R.id.article_date)).setText(
                    DateTimeUtils.getDateFromTimestamp(Long.parseLong(detailData.getCreated())));
            publishedDateTextView
                    .setText("Published : " + DateTimeUtils
                            .getDateFromTimestamp(Long.parseLong(detailData.getCreated())));
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
        if (!StringUtils.isNullOrEmpty(detailData.getProfilePic().getClientApp())) {
            Picasso.get().load(detailData.getProfilePic().getClientApp()).into(authorImageViewFollowContainer);
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
            FirebaseCrashlytics.getInstance().recordException(e);
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
                case R.id.moreArticlesTextView:
                    Intent intent1 = new Intent(getActivity(), DashboardActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent1);
                    break;
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
                case R.id.bookmarkImageViewNew:
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
                case R.id.followTextViewFollowContainer:
                case R.id.follow_click:
                case R.id.followText:
                    followApiCall();
                    break;
                case R.id.user_image:
                case R.id.user_name:
                case R.id.authorName:
                case R.id.userFollowView:
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
                case R.id.writeCommentTextView:
                case R.id.viewMoreTextView:
                case R.id.beTheFirstOneCommentContainer:
                    openViewCommentDialog();
                    ((ArticleDetailsContainerActivity) getActivity()).checkAudioPlaying();

                    break;
                case R.id.likeTextView: {
                    if (recommendStatus == 0) {
                        recommendStatus = 1;
                        if (!isFollowing && !userDynamoId.equals(articleId)) {
                            setValuesInFollowPopUp();
                        }
                        Drawable drawable = ContextCompat
                                .getDrawable(likeArticleTextView.getContext(), R.drawable.ic_recommended);
                        drawable.setColorFilter(
                                ContextCompat.getColor(likeArticleTextView.getContext(), R.color.app_red),
                                PorterDuff.Mode.SRC_IN);
                        likeArticleTextView.setImageDrawable(drawable);
                        recommendUnrecommendArticleApi("1");
                        Utils.pushLikeArticleEvent(getActivity(), "DetailArticleScreen",
                                userDynamoId + "", articleId, authorId + "~" + author);
                    } else {
                        Drawable drawable = ContextCompat
                                .getDrawable(likeArticleTextView.getContext(), R.drawable.ic_recommend);
                        drawable.setColorFilter(
                                ContextCompat.getColor(likeArticleTextView.getContext(), R.color.app_red),
                                PorterDuff.Mode.SRC_IN);
                        likeArticleTextView.setImageDrawable(drawable);
                        recommendStatus = 0;
                        recommendUnrecommendArticleApi("0");
                        Utils.pushUnlikeArticleEvent(getActivity(), "DetailArticleScreen",
                                userDynamoId + "", articleId, authorId + "~" + author);
                    }
                    break;
                }
                case R.id.facebookShareTextView:
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(Uri.parse(
                                AppUtils.getUtmParamsAppendedShareUrl(shareUrl, "AD_Facebook_Share", "Share_Android")))
                                .build();
                        if (getActivity() != null) {
                            new ShareDialog(getActivity()).show(content);
                        }
                    }
                    Utils.shareEventTracking(getActivity(), "Article Detail", "Share_Android", "AD_Facebook_Share");
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
                                AppUtils.stripHtml("" + detailData.getExcerpt()) + "\n\n" + BaseApplication
                                        .getAppContext().getString(R.string.ad_share_follow_author, author) + "\n"
                                        + AppUtils
                                        .getUtmParamsAppendedShareUrl(shareUrl, "AD_Whatsapp_Share", "Share_Android"));
                        try {
                            startActivity(whatsappIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getActivity(), "Whatsapp have not been installed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        Utils.shareEventTracking(getActivity(), "Article Detail", "Share_Android", "AD_Whatsapp_Share");
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
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                    break;
                case R.id.cancelFollowPopUp:
                    followPopUpBottomContainer.setVisibility(View.GONE);
                    break;
                case R.id.replyCount2: {
                    Bundle args = new Bundle();
                    args.putParcelable("commentReplies", commentsList.get(1));
                    args.putInt("totalRepliesCount", commentsList.get(1).getRepliesCount());
                    args.putInt("position", 1);
                    articleCommentRepliesDialogFragment =
                            new ArticleCommentRepliesDialogFragment();
                    articleCommentRepliesDialogFragment.setArguments(args);
                    articleCommentRepliesDialogFragment.setCancelable(true);
                    FragmentManager fm = getChildFragmentManager();
                    articleCommentRepliesDialogFragment.show(fm, "View Replies");

                    break;
                }
                case R.id.replyCount: {
                    if (commentsList.get(0).getRepliesCount() == 0) {
                        openAddCommentReplyDialog(commentsList.get(0));
                    } else {
                        Bundle args = new Bundle();
                        args.putParcelable("commentReplies", commentsList.get(0));
                        args.putInt("totalRepliesCount", commentsList.get(0).getRepliesCount());
                        args.putInt("position", 0);
                        articleCommentRepliesDialogFragment
                                = new ArticleCommentRepliesDialogFragment();
                        articleCommentRepliesDialogFragment.setArguments(args);
                        articleCommentRepliesDialogFragment.setCancelable(true);
                        FragmentManager fm = getChildFragmentManager();
                        articleCommentRepliesDialogFragment.show(fm, "View Replies");
                    }
                    break;
                }
                case R.id.menuItemImageView:
                    chooseOptionMenuItem(menuItemImageView);
                    break;
                case R.id.moreFromAuthorContainer1:
                    launchRelatedOrBloggersArticle(relatedOrBloggerArticle, "articleDetailsRelatedList", 1);
                    break;
                case R.id.moreFromAuthorContainer2:
                    launchRelatedOrBloggersArticle(relatedOrBloggerArticle, "articleDetailsRelatedList", 2);
                    break;
                case R.id.moreFromAuthorContainer3:
                    launchRelatedOrBloggersArticle(relatedOrBloggerArticle, "articleDetailsRelatedList", 3);
                    break;
                case R.id.moreFromAuthorContainer4:
                    launchRelatedOrBloggersArticle(relatedOrBloggerArticle, "articleDetailsRelatedList", 4);
                    break;
                case R.id.moreFromAuthorContainer5:
                    launchRelatedOrBloggersArticle(relatedOrBloggerArticle, "articleDetailsRelatedList", 5);
                    break;
                case R.id.todaysBestContainer1:
                    launchTodaysBestArticle(todaysBestListData, "todaysBestArticle", 1);
                    break;
                case R.id.todaysBestContainer2:
                    launchTodaysBestArticle(todaysBestListData, "todaysBestArticle", 2);
                    break;
                case R.id.todaysBestContainer3:
                    launchTodaysBestArticle(todaysBestListData, "todaysBestArticle", 3);
                    break;
                case R.id.todaysBestContainer4:
                    launchTodaysBestArticle(todaysBestListData, "todaysBestArticle", 4);
                    break;
                case R.id.todaysBestContainer5:
                    launchTodaysBestArticle(todaysBestListData, "todaysBestArticle", 5);
                    break;
                case R.id.bookmarkTodaysBestArticle1:
                    addRemoveTodatsBestArticleBookmark(todaysBestListData.get(0), 1, bookmarkTodaysBestArticle1);
                    break;
                case R.id.bookmarkTodaysBestArticle2:
                    addRemoveTodatsBestArticleBookmark(todaysBestListData.get(1), 2, bookmarkTodaysBestArticle2);
                    break;
                case R.id.bookmarkTodaysBestArticle3:
                    addRemoveTodatsBestArticleBookmark(todaysBestListData.get(2), 3, bookmarkTodaysBestArticle3);
                    break;
                case R.id.bookmarkTodaysBestArticle4:
                    addRemoveTodatsBestArticleBookmark(todaysBestListData.get(3), 4, bookmarkTodaysBestArticle4);
                    break;
                case R.id.bookmarkTodaysBestArticle5:
                    addRemoveTodatsBestArticleBookmark(todaysBestListData.get(4), 5, bookmarkTodaysBestArticle5);
                    break;
                case R.id.shareTodayBestArticle1:
                    shareTodaysBestArticles(todaysBestListData.get(0));
                    break;
                case R.id.shareTodayBestArticle2:
                    shareTodaysBestArticles(todaysBestListData.get(1));
                    break;
                case R.id.shareTodayBestArticle3:
                    shareTodaysBestArticles(todaysBestListData.get(2));
                    break;
                case R.id.shareTodayBestArticle4:
                    shareTodaysBestArticles(todaysBestListData.get(3));
                    break;
                case R.id.shareTodayBestArticle5:
                    shareTodaysBestArticles(todaysBestListData.get(4));
                    break;
                case R.id.likeCount:
                    likeDislikeComment(0);
                    break;
                case R.id.likeCount2:
                    likeDislikeComment(1);
                    break;
                case R.id.reportCommentContent1:
                    reportComment(0);
                    break;
                case R.id.reportCommentContent2:
                    reportComment(1);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void reportComment(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("authorId", commentsList.get(position).getUserId());
        args.putString("responseType", "COMMENT");
        CommentOptionsDialogFragment commentOptionsDialogFragment = new CommentOptionsDialogFragment();
        commentOptionsDialogFragment.setArguments(args);
        commentOptionsDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        commentOptionsDialogFragment.show(fm, "Comment Options");
    }

    private void likeDislikeComment(int index) {
        if (!commentsList.get(index).getLiked()) {
            if (index == 0) {
                try {
                    Drawable myDrawable = ContextCompat
                            .getDrawable(likeCount1.getContext(), R.drawable.ic_recommended);
                    myDrawable
                            .setColorFilter(ContextCompat.getColor(getActivity(), R.color.app_red),
                                    PorterDuff.Mode.SRC_IN);
                    likeCount1
                            .setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
                    likeCount1.setText(commentsList.get(0).getLikeCount() + 1 + "");
                    commentsList.get(0).setLikeCount(commentsList.get(0).getLikeCount() + 1);
                } catch (NullPointerException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }
            } else {
                try {
                    Drawable myDrawable = ContextCompat
                            .getDrawable(likeCount2.getContext(), R.drawable.ic_recommended);
                    myDrawable
                            .setColorFilter(ContextCompat.getColor(getActivity(), R.color.app_red),
                                    PorterDuff.Mode.SRC_IN);
                    likeCount2
                            .setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
                    likeCount2.setText(commentsList.get(1).getLikeCount() + 1 + "");
                    commentsList.get(1).setLikeCount(commentsList.get(1).getLikeCount() + 1);
                } catch (NullPointerException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }
            }
            commentsList.get(index).setLiked(true);
            LikeReactionModel commentListData = new LikeReactionModel();
            commentListData.setReaction("like");
            commentListData.setStatus("1");
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
            Call<ResponseBody> call = articleDetailsApi
                    .likeDislikeComment(commentsList.get(index).getId(), commentListData);
            call.enqueue(likeDisLikeCommentCallback);
        } else if (commentsList.get(index).getLiked()) {

            if (index == 0) {
                try {
                    Drawable myDrawable = ContextCompat
                            .getDrawable(likeCount1.getContext(), R.drawable.ic_recommend);
                    myDrawable
                            .setColorFilter(ContextCompat.getColor(getActivity(), R.color.grey),
                                    PorterDuff.Mode.SRC_IN);
                    likeCount1
                            .setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
                    if (commentsList.get(0).getLikeCount() - 1 != 0) {
                        likeCount1.setText(commentsList.get(0).getLikeCount() - 1 + "");
                    } else {
                        likeCount1.setText("");
                    }
                    commentsList.get(0).setLikeCount(commentsList.get(0).getLikeCount() - 1);
                } catch (NullPointerException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }
            } else {

                try {
                    Drawable myDrawable = ContextCompat
                            .getDrawable(likeCount2.getContext(), R.drawable.ic_recommend);
                    myDrawable
                            .setColorFilter(ContextCompat.getColor(getActivity(), R.color.grey),
                                    PorterDuff.Mode.SRC_IN);
                    likeCount2
                            .setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
                    if (commentsList.get(1).getLikeCount() - 1 != 0) {
                        likeCount2.setText(commentsList.get(1).getLikeCount() - 1 + "");
                    } else {
                        likeCount2.setText("");
                    }
                    commentsList.get(1).setLikeCount(commentsList.get(1).getLikeCount() - 1);

                } catch (NullPointerException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("NullPointerException", Log.getStackTraceString(e));
                }
            }
            commentsList.get(index).setLiked(false);
            LikeReactionModel commentListData = new LikeReactionModel();
            commentListData.setReaction("like");
            commentListData.setStatus("0");
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
            Call<ResponseBody> call = articleDetailsApi
                    .likeDislikeComment(commentsList.get(index).getId(), commentListData);
            call.enqueue(likeDisLikeCommentCallback);
        }
    }


    private Callback<ResponseBody> likeDisLikeCommentCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    };

    private void shareTodaysBestArticles(MixFeedResult dataList) {
        String shareUrl = AppUtils.getShareUrl(dataList.getUserType(), dataList.getBlogTitleSlug(),
                dataList.getTitleSlug());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
        startActivity(Intent.createChooser(shareIntent, "Momspresso"));
    }


    private void addRemoveTodatsBestArticleBookmark(MixFeedResult dataList, int index, ImageView bookmarkImageViewNew) {
        todaysBestBookmarkIdIndex = index - 1;
        if (todaysBestListData.get(index - 1).getIsbookmark() == 0) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(todaysBestListData.get(index - 1).getId());
            todaysBestListData.get(index - 1).setIsbookmark(1);
            Drawable top = ContextCompat.getDrawable(bookmarkImageViewNew.getContext(), R.drawable.ic_bookmarked);
            top.setColorFilter(ContextCompat.getColor(bookmarkImageViewNew.getContext(), R.color.app_red),
                    PorterDuff.Mode.SRC_IN);
            bookmarkImageViewNew.setImageDrawable(top);
            if ("1".equals(todaysBestListData.get(index - 1).isMomspresso())) {
                Call<AddBookmarkResponse> call = articleDetailsApi
                        .addVideoWatchLater(articleDetailRequest);
                call.enqueue(addTodaysBestBookmarkCallback);
            } else {
                Call<AddBookmarkResponse> call = articleDetailsApi
                        .addBookmark(articleDetailRequest);
                call.enqueue(addTodaysBestBookmarkCallback);
            }
        } else {
            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
            deleteBookmarkRequest.setId(todaysBestListData.get(index - 1).getBookmarkId());
            todaysBestListData.get(index - 1).setIsbookmark(0);

            Drawable top = ContextCompat.getDrawable(bookmarkImageViewNew.getContext(), R.drawable.ic_bookmark);
            top.setColorFilter(ContextCompat.getColor(bookmarkImageViewNew.getContext(), R.color.grey),
                    PorterDuff.Mode.SRC_IN);
            bookmarkImageViewNew.setImageDrawable(top);

            if ("1".equals(todaysBestListData.get(index - 1).isMomspresso())) {
                Call<AddBookmarkResponse> call = articleDetailsApi
                        .deleteVideoWatchLater(deleteBookmarkRequest);
                call.enqueue(addTodaysBestBookmarkCallback);

            } else {
                Call<AddBookmarkResponse> call = articleDetailsApi
                        .deleteBookmark(deleteBookmarkRequest);
                call.enqueue(addTodaysBestBookmarkCallback);
            }
        }
    }


    private void chooseOptionMenuItem(View chooseOptionMenuItem) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), chooseOptionMenuItem);
        popupMenu.getMenuInflater().inflate(R.menu.article_detail_items_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.copyLink) {
                AppUtils.copyToClipboard(
                        AppUtils.getShareUrl(detailData.getUserType(), detailData.getBlogTitleSlug(),
                                detailData.getTitleSlug())
                );
                ToastUtils.showToast(getActivity(), "Link Copied");
                return true;
            } else if (menuItem.getItemId() == R.id.addCollection) {
                try {
                    AddCollectionAndCollectionItemDialogFragment addCollectionAndCollectionitemDialogFragment =
                            new AddCollectionAndCollectionItemDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("articleId", articleId);
                    bundle.putString("type", AppConstants.ARTICLE_COLLECTION_TYPE);
                    addCollectionAndCollectionitemDialogFragment.setArguments(bundle);
                    FragmentManager fm = getFragmentManager();
                    addCollectionAndCollectionitemDialogFragment.setTargetFragment(ArticleDetailsFragment.this, 0);
                    addCollectionAndCollectionitemDialogFragment.show(fm, "collectionAdd");
                    Utils.pushProfileEvents(getActivity(), "CTA_Article_Add_To_Collection",
                            "ArticleDetailsFragment", "Add to Collection", "-");
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
                return true;
            } else if (menuItem.getItemId() == R.id.share) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        AppUtils.getUtmParamsAppendedShareUrl(shareUrl, "AD_Generic_Share", "Share_Android"));
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                Utils.shareEventTracking(getActivity(), "Article Detail", "Share_Android", "AD_Generic_Share");
                return true;
            } else if (menuItem.getItemId() == R.id.reportLang) {
                final ReportStoryOrCommentRequest reportStoryOrCommentRequest = new ReportStoryOrCommentRequest();
                reportStoryOrCommentRequest.setId(articleId);
                reportStoryOrCommentRequest.setType(AppConstants.REPORT_TYPE_ARTICLE);
                reportStoryOrCommentRequest.setReason("Incorrect Language");
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                final ShortStoryAPI shortStoryApi = retrofit.create(ShortStoryAPI.class);
                Call<ReportStoryOrCommentResponse> call = shortStoryApi
                        .reportStoryOrComment(reportStoryOrCommentRequest);
                call.enqueue(new Callback<ReportStoryOrCommentResponse>() {
                    @Override
                    public void onResponse(Call<ReportStoryOrCommentResponse> call,
                            Response<ReportStoryOrCommentResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<ReportStoryOrCommentResponse> call, Throwable t) {

                    }
                });
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    void openAddCommentReplyDialog(CommentListData commentListData) {
        Bundle args = new Bundle();
        args.putParcelable("parentCommentData", commentListData);
        AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment =
                new AddArticleCommentReplyDialogFragment();
        addArticleCommentReplyDialogFragment.setArguments(args);
        addArticleCommentReplyDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        addArticleCommentReplyDialogFragment.show(fm, "Add Replies");
    }

    public void addReply(String content, String commentId) {
        showProgressDialog("Adding Reply");
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        addEditCommentOrReplyRequest.setParent_id(commentId);
        addEditCommentOrReplyRequest.setType("article");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
        Call<CommentListResponse> call = articleDetailsApi.addCommentOrReply(addEditCommentOrReplyRequest);
        call.enqueue(addReplyResponseListener);
    }

    private Callback<CommentListResponse> addReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, Response<CommentListResponse> response) {
            removeProgressDialog();
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ((ArticleDetailsContainerActivity) getActivity())
                            .showToast(getString(R.string.server_went_wrong));
                }
                return;
            }
            try {

                CommentListResponse res = response.body();
                if (null != res && 200 == res.getCode() && Constants.SUCCESS.equals(res.getStatus())) {
                    CommentListData commentListData = new CommentListData();
                    commentListData.setId(res.getData().get(0).getId());
                    commentListData.setMessage(res.getData().get(0).getMessage());
                    commentListData.setCreatedTime(res.getData().get(0).getCreatedTime());
                    commentListData.setPostId(res.getData().get(0).getPostId());
                    commentListData.setParentCommentId(res.getData().get(0).getParentCommentId());
                    commentListData.setUserPic(res.getData().get(0).getUserPic());
                    commentListData.setUserName(res.getData().get(0).getUserName());
                    commentListData.setUserId(res.getData().get(0).getUserId());

                    for (int i = 0; i < commentsList.size(); i++) {
                        if (commentsList.get(i).getId().equals(res.getData().get(0).getParentCommentId())) {
                            commentsList.get(i).getReplies().add(0, commentListData);
                            commentsList.get(i).setRepliesCount(commentsList.get(i).getRepliesCount() + 1);
                            if (i == 0) {
                                replyCount1.setText("Reply(" + commentsList.get(i).getRepliesCount() + ")");
                            } else {
                                replyCount2.setText("Reply(" + commentsList.get(i).getRepliesCount() + ")");
                            }
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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
            FirebaseCrashlytics.getInstance().recordException(e);
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
            args.putString("contentType", AppConstants.CONTENT_TYPE_ARTICLE);
            commentFrag.setArguments(args);
            ((ArticleDetailsContainerActivity) getActivity()).hideToolbarPerm();
            ((ArticleDetailsContainerActivity) getActivity())
                    .addFragment(commentFrag, null, "topToBottom");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
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

    private void launchRelatedOrBloggersArticle(ArrayList<ArticleListingResult> dataList, String listingType,
            int index) {
        Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
        intent.putExtra(Constants.ARTICLE_ID, dataList.get(index - 1).getId());
        intent.putExtra(Constants.AUTHOR_ID, dataList.get(index - 1).getUserId());
        intent.putExtra(Constants.FROM_SCREEN, "Article Details");
        intent.putExtra(Constants.ARTICLE_OPENED_FROM, listingType);
        intent.putExtra(Constants.ARTICLE_INDEX, "" + (index - 1));
        intent.putParcelableArrayListExtra("pagerListData", dataList);
        startActivity(intent);
    }

    private void launchTodaysBestArticle(ArrayList<MixFeedResult> dataList, String listingType,
            int index) {
        Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
        intent.putExtra(Constants.ARTICLE_ID, dataList.get(index - 1).getId());
        intent.putExtra(Constants.AUTHOR_ID, dataList.get(index - 1).getUserId());
        intent.putExtra(Constants.FROM_SCREEN, "Article Details");
        intent.putExtra(Constants.ARTICLE_OPENED_FROM, listingType);
        intent.putExtra(Constants.ARTICLE_INDEX, "" + (index - 1));
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
                bookmarkOrMenuBarContainer.setVisibility(View.VISIBLE);

            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ((ArticleDetailsContainerActivity) getActivity()).showMainToolbar();
                bookmarkOrMenuBarContainer.setVisibility(View.GONE);

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
            followTextViewFollowContainer.setText(getString(R.string.ad_follow_author));
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
            followTextViewFollowContainer.setText(getString(R.string.ad_following_author));
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
            Drawable top = ContextCompat.getDrawable(bookmarkImageViewNew.getContext(), R.drawable.ic_bookmarked);
            top.setColorFilter(ContextCompat.getColor(bookmarkImageViewNew.getContext(), R.color.app_red),
                    PorterDuff.Mode.SRC_IN);
            bookmarkImageViewNew.setImageDrawable(top);

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

            Drawable top = ContextCompat.getDrawable(bookmarkImageViewNew.getContext(), R.drawable.ic_bookmark);
            top.setColorFilter(ContextCompat.getColor(bookmarkImageViewNew.getContext(), R.color.grey),
                    PorterDuff.Mode.SRC_IN);
            bookmarkImageViewNew.setImageDrawable(top);

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
                getUsersData();
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
                FirebaseCrashlytics.getInstance().recordException(e);
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
                    try {
                        articleViewCountTextView
                                .setText(AppUtils.withSuffix(Long.parseLong(responseData.getData().get(0).getCount())));
                    } catch (Exception e) {
                        articleViewCountTextView.setText(responseData.getData().get(0).getCount());
                    }
                    try {
                        articleCommentCountTextView
                                .setText(AppUtils.withSuffix(
                                        Long.parseLong(responseData.getData().get(0).getCommentCount())));
                    } catch (Exception e) {
                        articleCommentCountTextView
                                .setText(responseData.getData().get(0).getCommentCount());
                    }
                    try {
                        articleRecommendationCountTextView
                                .setText(AppUtils.withSuffix(
                                        Long.parseLong(responseData.getData().get(0).getLikeCount())));

                    } catch (Exception e) {
                        articleRecommendationCountTextView
                                .setText(responseData.getData().get(0).getLikeCount());
                    }

                    if ("0".equals(responseData.getData().get(0).getCommentCount())) {
                        articleCommentCountTextView.setVisibility(View.GONE);
                        commentsHeaderTextView.setVisibility(View.GONE);
                    } else {
                        commentsHeaderTextView.setVisibility(View.VISIBLE);
                        commentsHeaderTextView
                                .setText("Comments(" + responseData.getData().get(0).getCommentCount() + ")");
                    }
                    if ("0".equals(responseData.getData().get(0).getLikeCount())) {
                        articleRecommendationCountTextView.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ViewCountResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
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
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CrownDataResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
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
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ArticleDetailWebserviceResponse> call, Throwable t) {
                    removeProgressDialog();
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
                NetworkErrorException nee = new NetworkErrorException(
                        "Category related Article API failure");
                FirebaseCrashlytics.getInstance().recordException(nee);
                Call<ArticleListingResponse> categoryRelatedArticlesCall = articleDetailsApi
                        .getCategoryRelatedArticles(articleId, 0, 4,
                                SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
                categoryRelatedArticlesCall.enqueue(categoryArticleResponseCallback);
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
                        Call<ArticleListingResponse> categoryRelatedArticlesCall = articleDetailsApi
                                .getCategoryRelatedArticles(articleId, 0, 4,
                                        SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
                        categoryRelatedArticlesCall.enqueue(categoryArticleResponseCallback);
                    } else {
                        recentAuthorArticleHeading.setText(getString(R.string.recent_article));
                        recentAuthorArticles.setVisibility(View.GONE);
                        Collections.shuffle(dataList);
                        impressionList.addAll(dataList);
                        swipeRelated.onRelatedSwipe(dataList);
                        moreFromAuthorTextView.setText("MORE FROM AUTHOR");
                        relatedOrBloggerArticle = new ArrayList<>();
                        relatedOrBloggerArticle = dataList;
                        if (dataList.size() >= 1) {
                            moreFromAuthorContainer1.setVisibility(View.VISIBLE);
                            Picasso.get().load(dataList.get(0).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(articleImageView1);
                            txvArticleTitle1.setText(dataList.get(0).getTitle());
                            recommendCountTextView1.setText(dataList.get(0).getLikesCount());
                            commentCountTextView1.setText(dataList.get(0).getCommentsCount());
                            viewCountTextView1.setText(dataList.get(0).getArticleCount());
                        }

                        if (dataList.size() >= 2) {
                            moreFromAuthorContainer2.setVisibility(View.VISIBLE);
                            Picasso.get().load(dataList.get(1).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(articleImageView2);
                            txvArticleTitle2.setText(dataList.get(1).getTitle());
                            recommendCountTextView2.setText(dataList.get(1).getLikesCount());
                            commentCountTextView2.setText(dataList.get(1).getCommentsCount());
                            viewCountTextView2.setText(dataList.get(1).getArticleCount());
                        }

                        if (dataList.size() >= 3) {
                            moreFromAuthorContainer3.setVisibility(View.VISIBLE);

                            Picasso.get().load(dataList.get(2).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(articleImageView3);
                            txvArticleTitle3.setText(dataList.get(2).getTitle());
                            recommendCountTextView3.setText(dataList.get(2).getLikesCount());
                            commentCountTextView3.setText(dataList.get(2).getCommentsCount());
                            viewCountTextView3.setText(dataList.get(2).getArticleCount());
                        }

                        if (dataList.size() >= 4) {
                            moreFromAuthorContainer4.setVisibility(View.VISIBLE);

                            Picasso.get().load(dataList.get(3).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(articleImageView4);
                            txvArticleTitle4.setText(dataList.get(3).getTitle());
                            recommendCountTextView4.setText(dataList.get(3).getLikesCount());
                            commentCountTextView4.setText(dataList.get(3).getCommentsCount());
                            viewCountTextView4.setText(dataList.get(3).getArticleCount());


                        }

                        if (dataList.size() >= 5) {
                            moreFromAuthorContainer5.setVisibility(View.VISIBLE);

                            Picasso.get().load(dataList.get(4).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(articleImageView5);
                            txvArticleTitle5.setText(dataList.get(4).getTitle());
                            recommendCountTextView5.setText(dataList.get(4).getLikesCount());
                            commentCountTextView5.setText(dataList.get(4).getCommentsCount());
                            viewCountTextView5.setText(dataList.get(4).getArticleCount());

                        }
                    }
                } else {
                    NetworkErrorException nee = new NetworkErrorException(
                            "Category related Article Error Response");
                    FirebaseCrashlytics.getInstance().recordException(nee);
                    Call<ArticleListingResponse> categoryRelatedArticlesCall = articleDetailsApi
                            .getCategoryRelatedArticles(articleId, 0, 4,
                                    SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
                    categoryRelatedArticlesCall.enqueue(categoryArticleResponseCallback);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Call<ArticleListingResponse> categoryRelatedArticlesCall = articleDetailsApi
                        .getCategoryRelatedArticles(articleId, 0, 4,
                                SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
                categoryRelatedArticlesCall.enqueue(categoryArticleResponseCallback);
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
                        relatedArticleHorizontalScrollView.setVisibility(View.GONE);
                        moreFromAuthorTextView.setVisibility(View.GONE);

                    } else {
                        impressionList.addAll(dataList);
                        Collections.shuffle(dataList);
                        moreFromAuthorTextView.setText("RELATED ARTICLES");
                        relatedOrBloggerArticle = new ArrayList<>();
                        relatedOrBloggerArticle = dataList;
                        if (dataList.size() >= 1) {
                            moreFromAuthorContainer1.setVisibility(View.VISIBLE);
                            Picasso.get().load(dataList.get(0).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(articleImageView1);
                            txvArticleTitle1.setText(dataList.get(0).getTitle());
                            recommendCountTextView1.setText(dataList.get(0).getLikesCount());
                            commentCountTextView1.setText(dataList.get(0).getCommentsCount());
                            viewCountTextView1.setText(dataList.get(0).getArticleCount());
                        }

                        if (dataList.size() >= 2) {
                            moreFromAuthorContainer2.setVisibility(View.VISIBLE);

                            Picasso.get().load(dataList.get(1).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(articleImageView2);
                            txvArticleTitle2.setText(dataList.get(1).getTitle());
                            recommendCountTextView2.setText(dataList.get(1).getLikesCount());
                            commentCountTextView2.setText(dataList.get(1).getCommentsCount());
                            viewCountTextView2.setText(dataList.get(1).getArticleCount());
                        }

                        if (dataList.size() >= 3) {
                            moreFromAuthorContainer3.setVisibility(View.VISIBLE);

                            Picasso.get().load(dataList.get(2).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(articleImageView3);
                            txvArticleTitle3.setText(dataList.get(2).getTitle());
                            recommendCountTextView3.setText(dataList.get(2).getLikesCount());
                            commentCountTextView3.setText(dataList.get(2).getCommentsCount());
                            viewCountTextView3.setText(dataList.get(2).getArticleCount());
                        }

                        if (dataList.size() >= 4) {
                            moreFromAuthorContainer4.setVisibility(View.VISIBLE);

                            Picasso.get().load(dataList.get(3).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(articleImageView4);
                            txvArticleTitle4.setText(dataList.get(3).getTitle());
                            recommendCountTextView4.setText(dataList.get(3).getLikesCount());
                            commentCountTextView4.setText(dataList.get(3).getCommentsCount());
                            viewCountTextView4.setText(dataList.get(3).getArticleCount());


                        }

                        if (dataList.size() >= 5) {
                            moreFromAuthorContainer5.setVisibility(View.VISIBLE);

                            Picasso.get().load(dataList.get(4).getImageUrl().getThumbMin())
                                    .placeholder(R.drawable.default_article).fit()
                                    .into(articleImageView5);
                            txvArticleTitle5.setText(dataList.get(4).getTitle());
                            recommendCountTextView5.setText(dataList.get(4).getLikesCount());
                            commentCountTextView5.setText(dataList.get(4).getCommentsCount());
                            viewCountTextView5.setText(dataList.get(4).getArticleCount());
                        }
                    }
                } else {
                    if (isAdded()) {
                        ((ArticleDetailsContainerActivity) getActivity())
                                .showToast(getString(R.string.server_went_wrong));
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
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
                        FirebaseCrashlytics.getInstance().recordException(nee);
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
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                                        } catch (Exception e) {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                            Log.d("MC4KException", Log.getStackTraceString(e));
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        FirebaseCrashlytics.getInstance().recordException(t);
                                        Log.d("MC4KException", Log.getStackTraceString(t));
                                    }
                                });
                            } catch (Exception e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                Log.d("MC4KException", Log.getStackTraceString(e));
                            }
                        } else {
                            if (isAdded()) {
                                ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
                            }
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
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

                            - relatedImageWidth - topicView.getPaddingStart() - topicView
                            .getPaddingEnd();

                    if (width < 0) {
                        viewAllTagsTextView.setVisibility(View.VISIBLE);
                    }
                    if (sponsoredList.contains(key)) {
                        ((ImageView) topicView.getChildAt(2)).setVisibility(View.GONE);
                        ((View) topicView.getChildAt(1)).setVisibility(View.GONE);
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
                FirebaseCrashlytics.getInstance().recordException(e);
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
                        FirebaseCrashlytics.getInstance().recordException(nee);
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
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                        }
                    }
                }

                @Override
                public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
                    removeProgressDialog();
                    FirebaseCrashlytics.getInstance().recordException(t);
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
                                if (getActivity() != null) {
                                    Drawable top = ContextCompat
                                            .getDrawable(getActivity(), R.drawable.ic_bookmark);
                                    top.setColorFilter(ContextCompat.getColor(getActivity(), R.color.grey),
                                            PorterDuff.Mode.SRC_IN);
                                    bookmarkImageViewNew.setImageDrawable(top);

                                }
                            } else {
                                if (getActivity() != null) {
                                    Drawable top = ContextCompat
                                            .getDrawable(getActivity(), R.drawable.ic_bookmarked);
                                    top.setColorFilter(ContextCompat.getColor(getActivity(), R.color.app_red),
                                            PorterDuff.Mode.SRC_IN);
                                    bookmarkImageViewNew.setImageDrawable(top);

                                }
                                bookmarkStatus = 1;
                            }
                            bookmarkId = responseData.getData().getResult().getBookmarkId();
                        }
                        if (userDynamoId.equals(authorId)) {
                            followClick.setVisibility(View.INVISIBLE);
                            userFollowView.setVisibility(View.GONE);
                        } else {
                            if (!responseData.getData().getResult().getIsFollowed()) {
                                followTextViewFollowContainer.setEnabled(true);
                                followTextViewFollowContainer.setText(
                                        AppUtils.getString(getActivity(), R.string.ad_follow_author));
                                isFollowing = false;
                            } else {
                                followTextViewFollowContainer.setEnabled(true);
                                followTextViewFollowContainer.setText(
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
                    if (getActivity() != null) {
                        Drawable top = ContextCompat
                                .getDrawable(getActivity(), R.drawable.ic_bookmark);
                        top.setColorFilter(ContextCompat.getColor(getActivity(), R.color.grey), PorterDuff.Mode.SRC_IN);
                        bookmarkImageViewNew.setImageDrawable(top);

                    }
                } else {
                    if (getActivity() != null) {
                        Drawable top = ContextCompat
                                .getDrawable(getActivity(), R.drawable.ic_bookmarked);
                        top.setColorFilter(ContextCompat.getColor(getActivity(), R.color.app_red),
                                PorterDuff.Mode.SRC_IN);
                        bookmarkImageViewNew.setImageDrawable(top);

                    }
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
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<AddBookmarkResponse> addTodaysBestBookmarkCallback = new Callback<AddBookmarkResponse>() {
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
                            ((ArticleDetailsContainerActivity) getActivity()).showToast(responseData.getReason());
                        } else {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
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
        try {
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (status == ADD_BOOKMARK) {
                    todaysBestListData.get(todaysBestBookmarkIdIndex)
                            .setBookmarkId(responseData.getData().getResult().getBookmarkId());
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
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
        FirebaseCrashlytics.getInstance().recordException(t);
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
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
                    if (isAdded()) {
                        ((ArticleDetailsContainerActivity) getActivity())
                                .showToast(getString(R.string.server_went_wrong));
                    }
                    FirebaseCrashlytics.getInstance().recordException(t);
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
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
                    if (isAdded()) {
                        ((ArticleDetailsContainerActivity) getActivity())
                                .showToast(getString(R.string.server_went_wrong));
                    }
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    @Override
    public void onResponseDelete(int position, String responseType) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
        Call<CommentListResponse> call = articleDetailsApi.deleteCommentOrReply(commentsList.get(position).getId());
        call.enqueue(deleteCommentResponseListener);
        deleteCommentPosition = position;
    }

    @Override
    public void onResponseEdit(int position, String responseType) {
        Bundle args = new Bundle();
        args.putString("action", "EDIT_COMMENT");
        args.putParcelable("parentCommentData", commentsList.get(position));
        args.putInt("position", position);
        AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment =
                new AddArticleCommentReplyDialogFragment();
        addArticleCommentReplyDialogFragment.setArguments(args);
        addArticleCommentReplyDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        addArticleCommentReplyDialogFragment.show(fm, "Add Comment");
    }

    @Override
    public void onResponseReport(int position, String responseType) {
        Bundle args = new Bundle();
        args.putString("postId", commentsList.get(position).getId());
        args.putInt("type", AppConstants.REPORT_TYPE_COMMENT);
        ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
        reportContentDialogFragment.setArguments(args);
        reportContentDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        reportContentDialogFragment.show(fm, "Report Content");
    }

    public void editComment(String content, String responseId, int position) {
        showProgressDialog("Editing your response");
        editCommentPosition = position;
        editContent = content;
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        Call<CommentListResponse> call = articleDetailsApi.editCommentOrReply(responseId, addEditCommentOrReplyRequest);
        call.enqueue(editCommentResponseListener);
    }

    private Callback<CommentListResponse> editCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (editCommentPosition == 0) {
                        commentatorNameAndCommentTextView1.setText((Html
                                .fromHtml(
                                        "<b>" + "<font color=\"#D54058\">" + commentsList.get(0).getUserName()
                                                + "</font>" + "</b>"
                                                + " "
                                                + "<font color=\"#4A4A4A\">" + editContent + "</font>")));
                    } else {
                        commentatorNameAndCommentTextView1.setText((Html
                                .fromHtml(
                                        "<b>" + "<font color=\"#D54058\">" + commentsList.get(1).getUserName()
                                                + "</font>" + "</b>"
                                                + " "
                                                + "<font color=\"#4A4A4A\">" + editContent + "</font>")));
                    }
                } else {
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    private Callback<CommentListResponse> deleteCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to delete comment. Please try again");
                }
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    commentsList.remove(deleteCommentPosition);
                    if (deleteCommentPosition == 0) {
                        commentContainer1.setVisibility(View.GONE);
                    } else {
                        commentContainer2.setVisibility(View.GONE);
                    }

                    if (isAdded()) {
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId,
                                articleId, "delete", "comment");
                    }
                } else {
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), "Failed to delete comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to delete comment. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
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
            FirebaseCrashlytics.getInstance().recordException(e);
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
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
        }
        authorNameFollowPopUp.setText(detailData.getUserName());
        followPopUpBottomContainer.setVisibility(View.VISIBLE);
    }

    void deleteReply(int commentPos, int replyPos) {
        deleteCommentPosition = commentPos;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
        Call<CommentListResponse> call = articleDetailsApi
                .deleteCommentOrReply(commentsList.get(commentPos).getReplies().get(replyPos).getId());
        call.enqueue(deleteReplyResponseListener);
    }

    private Callback<CommentListResponse> deleteReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (deleteCommentPosition == 0) {
                        commentsList.get(0).setRepliesCount(commentsList.get(0).getRepliesCount() - 1);
                        if (commentsList.get(0).getRepliesCount() == 0) {
                            replyCount1.setText("Reply");
                        } else {
                            replyCount1.setText("Reply(" + commentsList.get(0).getRepliesCount() + ")");
                        }
                    } else {
                        commentsList.get(1).setRepliesCount(commentsList.get(1).getRepliesCount() - 1);
                        if (commentsList.get(1).getRepliesCount() == 0) {
                            replyCount2.setText("Reply");
                        } else {
                            replyCount2.setText("Reply(" + commentsList.get(1).getRepliesCount() + ")");
                        }
                    }
                    commentsList.get(deleteCommentPosition).getReplies().remove(deleteCommentPosition);
                    if (articleCommentRepliesDialogFragment != null) {
                        articleCommentRepliesDialogFragment.updateRepliesList(commentsList.get(deleteCommentPosition));
                        if (commentsList.get(deleteCommentPosition).getRepliesCount() == 0) {
                            articleCommentRepliesDialogFragment.dismiss();
                        }
                    }
                } else {
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    void editReply(String content, String parentCommentId, String replyId) {
        showProgressDialog("Editing Reply");
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        Call<CommentListResponse> call = articleDetailsApi.editCommentOrReply(replyId, addEditCommentOrReplyRequest);
        call.enqueue(editReplyResponseListener);
        this.replyId = replyId;
        editReplyParentCommentId = parentCommentId;
        editContent = content;
    }

    private Callback<CommentListResponse> editReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    boolean isReplyUpdated = false;
                    for (int i = 0; i < commentsList.size(); i++) {
                        if (commentsList.get(i).getId().equals(editReplyParentCommentId)) {
                            for (int j = 0; j < commentsList.get(i).getReplies().size(); j++) {
                                if (commentsList.get(i).getReplies().get(j).getId().equals(replyId)) {
                                    commentsList.get(i).getReplies().get(j).setMessage(editContent);
                                    if (articleCommentRepliesDialogFragment != null) {
                                        articleCommentRepliesDialogFragment.updateRepliesList(commentsList.get(i));
                                    }
                                    isReplyUpdated = true;
                                    break;
                                }
                            }
                        }
                        if (isReplyUpdated) {
                            break;
                        }
                    }

                    if (isAdded()) {
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId,
                                articleId, "edit", "reply");
                    }
                } else {
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

}
