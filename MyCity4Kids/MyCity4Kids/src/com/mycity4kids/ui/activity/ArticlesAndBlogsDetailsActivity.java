package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.kelltontech.utils.facebook.model.FacebookUtils;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnWebServiceCompleteListener;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.parentingdetails.ImageData;
import com.mycity4kids.models.parentingdetails.VideoData;
import com.mycity4kids.models.request.AddCommentRequest;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ArticleDetailResult;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.FBCommentResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.ProfilePic;
import com.mycity4kids.models.response.ViewCountResponse;
import com.mycity4kids.newmodels.VolleyBaseResponse;
import com.mycity4kids.observablescrollview.ObservableScrollView;
import com.mycity4kids.observablescrollview.ObservableScrollViewCallbacks;
import com.mycity4kids.observablescrollview.ScrollState;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.ui.CircleTransformation;
import com.mycity4kids.ui.fragment.CommentRepliesDialogFragment;
import com.mycity4kids.ui.fragment.EditCommentsRepliesFragment;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.volley.HttpVolleyRequest;
import com.mycity4kids.widget.RelatedArticlesView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * @author Hemant.parmar
 */
public class ArticlesAndBlogsDetailsActivity extends BaseActivity implements OnClickListener, ObservableScrollViewCallbacks {

    private final static int ADD_BOOKMARK = 1;

    ArticleDetailResult detailData;
    private UiLifecycleHelper mUiHelper;
    private String articleId;
    int width;

    private ObservableScrollView mScrollView;
    private ArrayList<ImageData> imageList;
    private ArrayList<VideoData> videoList;

    Boolean isFollowing = false;

    LinearLayout newCommentLayout;
    LinearLayout commentLayout;
    EditText commentText;
    ImageView commentBtn;
    TextView followClick;
    TextView recentAuthorArticleHeading, recentAuthorArticle1, recentAuthorArticle2, recentAuthorArticle3;
    LinearLayout trendingArticles, recentAuthorArticles;
    TextView trendingArticle1, trendingArticle2, trendingArticle3;
    Toolbar mToolbar;
    View commentEditView;
    AppBarLayout appBarLayout;
    WebView mWebView;
    RelatedArticlesView relatedArticles1, relatedArticles2, relatedArticles3;
    RelatedArticlesView trendingRelatedArticles1, trendingRelatedArticles2, trendingRelatedArticles3;

    private MyWebChromeClient mWebChromeClient = null;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private float density;

    private ImageView cover_image;
    private TextView article_title;
    private TextView author_type;
    private TextView articleViewCountTextView;
    private String authorId;
    String authorType, author;
    private int bookmarkStatus;

    private String deepLinkURL;
    private GoogleApiClient mClient;
    private String TAG;
    private String screenTitle = "Parenting Blogs";

    Boolean isLoading = false;
    private RelativeLayout mLodingView;

    //New UI changes
    private CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView floatingActionButton;
    private Menu menu;
    LinearLayout commLayout;
    RelativeLayout coordinatorLayout;
    FlowLayout tagsLayout;
    ArrayList<Topics> selectedTopics;
    Bitmap defaultBloggerBitmap, defaultCommentorBitmap;
    private String bookmarkFlag = "0";

    private String commentURL = "";
    String shareUrl = "";
    ArticleDetailsAPI articleDetailsAPI;
    private String bookmarkId;

    EditCommentsRepliesFragment editCommentsRepliesFragment;
    CommentRepliesDialogFragment commentFragment;
    private String blogSlug;
    private String titleSlug;
    private String commentType = "db";
    private String pagination = "";
    private float mToolbarBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(ArticlesAndBlogsDetailsActivity.this, "Article Details", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        deepLinkURL = getIntent().getStringExtra(Constants.DEEPLINK_URL);
        TAG = ArticlesAndBlogsDetailsActivity.this.getClass().getSimpleName();
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.APP_INDEX_API).build();
        try {

            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(getIntent().getIntExtra(AppConstants.NOTIFICATION_ID, 0));

            setContentView(R.layout.article_details_activity);
            coordinatorLayout = (RelativeLayout) findViewById(R.id.coordinatorLayout);
            ((TextView) findViewById(R.id.add_comment)).setOnClickListener(this);
            ((TextView) findViewById(R.id.user_name)).setOnClickListener(this);

            floatingActionButton = (ImageView) findViewById(R.id.user_image);
            floatingActionButton.setOnClickListener(this);

            mWebView = (WebView) findViewById(R.id.articleWebView);
            mWebChromeClient = new MyWebChromeClient();
            mWebView.setWebChromeClient(mWebChromeClient);

            author_type = (TextView) findViewById(R.id.blogger_type);

            followClick = (TextView) findViewById(R.id.follow_click);
            followClick.setOnClickListener(this);
            followClick.setEnabled(false);

            article_title = (TextView) findViewById(R.id.article_title);
            String coverImageUrl = getIntent().getStringExtra(Constants.ARTICLE_COVER_IMAGE);
            recentAuthorArticleHeading = (TextView) findViewById(R.id.recentAuthorArticleHeading);
            relatedArticles1 = (RelatedArticlesView) findViewById(R.id.relatedArticles1);
            relatedArticles2 = (RelatedArticlesView) findViewById(R.id.relatedArticles2);
            relatedArticles3 = (RelatedArticlesView) findViewById(R.id.relatedArticles3);

            trendingRelatedArticles1 = (RelatedArticlesView) findViewById(R.id.trendingRelatedArticles1);
            trendingRelatedArticles2 = (RelatedArticlesView) findViewById(R.id.trendingRelatedArticles2);
            trendingRelatedArticles3 = (RelatedArticlesView) findViewById(R.id.trendingRelatedArticles3);
            trendingArticles = (LinearLayout) findViewById(R.id.trendingArticles);
            recentAuthorArticles = (LinearLayout) findViewById(R.id.recentAuthorArticles);

            tagsLayout = (FlowLayout) findViewById(R.id.tagsLayout);
            articleViewCountTextView = (TextView) findViewById(R.id.articleViewCountTextView);
            cover_image = (ImageView) findViewById(R.id.cover_image);
            density = getResources().getDisplayMetrics().density;
            width = getResources().getDisplayMetrics().widthPixels;
            if (!StringUtils.isNullOrEmpty(coverImageUrl)) {
                Picasso.with(this).load(coverImageUrl).placeholder(R.drawable.default_article).resize(width, (int) (220 * density)).centerCrop().into(cover_image);
            }

            mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setIcon(R.drawable.app_logo);

//            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//            collapsingToolbarLayout.setExpandedTitleGravity(Gravity.CENTER);
//            collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

            defaultBloggerBitmap = (new CircleTransformation()).transform(BitmapFactory.decodeResource(getResources(),
                    R.drawable.default_blogger_profile_img));
            defaultCommentorBitmap = (new CircleTransformation()).transform(BitmapFactory.decodeResource(getResources(),
                    R.drawable.default_commentor_img));
            floatingActionButton.setImageDrawable(new BitmapDrawable(getResources(), defaultBloggerBitmap));

            commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));
            newCommentLayout = (LinearLayout) findViewById(R.id.comment_layout);
            commentBtn = (ImageView) findViewById(R.id.add_comment_btn);
            commentBtn.setOnClickListener(this);
            relatedArticles1.setOnClickListener(this);
            relatedArticles2.setOnClickListener(this);
            relatedArticles3.setOnClickListener(this);
            trendingRelatedArticles1.setOnClickListener(this);
            trendingRelatedArticles2.setOnClickListener(this);
            trendingRelatedArticles3.setOnClickListener(this);


            commentText = (EditText) findViewById(R.id.editCommentTxt);

            mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
            findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

            mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
            mUiHelper = new UiLifecycleHelper(this, FacebookUtils.callback);
            mUiHelper.onCreate(savedInstanceState);
            commLayout = ((LinearLayout) findViewById(R.id.commnetLout));

            mScrollView.setScrollViewCallbacks(this);

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                articleId = bundle.getString(Constants.ARTICLE_ID);
                authorId = bundle.getString(Constants.AUTHOR_ID, "");
                blogSlug = bundle.getString(Constants.BLOG_SLUG);
                titleSlug = bundle.getString(Constants.TITLE_SLUG);

                if (!ConnectivityUtils.isNetworkEnabled(this)) {
                    showToast(getString(R.string.error_network));
                    return;
                }
                showProgressDialog(getString(R.string.fetching_data));
                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
                hitArticleDetailsS3API();
                getViewCountAPI();
            }
        } catch (Exception e) {
            removeProgressDialog();
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    private void hitArticleDetailsS3API() {
        Call<ArticleDetailResult> call = articleDetailsAPI.getArticleDetailsFromS3(articleId);
        call.enqueue(articleDetailResponseCallbackS3);
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

    private void hitRelatedArticleAPI() {
        String url = AppConstants.LIVE_URL + AppConstants.SERVICE_TYPE_ARTICLE + "trending" +
                AppConstants.SEPARATOR_BACKSLASH + "1" + AppConstants.SEPARATOR_BACKSLASH + "3";
        HttpVolleyRequest.getStringResponse(this, url, null, mGetArticleListingListener, Request.Method.GET, true);

        Call<ArticleListingResponse> categoryRelatedArticlesCall = articleDetailsAPI.getCategoryRelatedArticles(articleId);
        categoryRelatedArticlesCall.enqueue(categoryArticleResponseCallback);

//        Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsAPI.getPublishedArticles(authorId, 0, 1, 4);
//        callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);

    }


    private void hitUpdateViewCountAPI(String userId, ArrayList<Map<String, String>> tagsList, ArrayList<Map<String, String>> cityList) {
        UpdateViewCountRequest updateViewCountRequest = new UpdateViewCountRequest();
        updateViewCountRequest.setUserId(userId);
        updateViewCountRequest.setTags(tagsList);
        updateViewCountRequest.setCities(cityList);
        Call<ResponseBody> callUpdateViewCount = articleDetailsAPI.updateViewCount(articleId, updateViewCountRequest);
        callUpdateViewCount.enqueue(updateViewCountResponseCallback);
    }

    private void getMoreComments() {
        isLoading = true;
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            showToast(getString(R.string.error_network));
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

    @Override
    protected void onStart() {
        super.onStart();
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("https://api.mycity4kids.com/")) {
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
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("https://api.mycity4kids.com/")) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        getMenuInflater().inflate(R.menu.aa_new_show_article, menu);
        this.menu = menu;
        menu.getItem(0).setEnabled(false);
        if (!StringUtils.isNullOrEmpty(authorId)) {
            hitBookmarkFollowingStatusAPI();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.bookmark:
                addRemoveBookmark();
                return true;
            case R.id.share:
                if (StringUtils.isNullOrEmpty(shareUrl) && StringUtils.isNullOrEmpty(blogSlug) && StringUtils.isNullOrEmpty(titleSlug)) {
                    showToast("Unable to share the article currently!");
                    return true;
                }
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String author = ((TextView) findViewById(R.id.user_name)).getText().toString();
                String shareMessage;
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    shareMessage = "mycity4kids\n\nCheck out this interesting blog post " + "\"" + detailData.getTitle() + "\" by " + author + ".";
                } else {
                    shareMessage = "mycity4kids\n\nCheck out this interesting blog post " + "\"" + detailData.getTitle() + "\" by " + author + ".\nRead Here: " + shareUrl;
                }
                Utils.pushEventShareURL(ArticlesAndBlogsDetailsActivity.this, GTMEventType.SHARE_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Blog Detail", shareUrl);
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "mycity4kids"));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {

            if (resultCode != RESULT_OK) {
                return;
            }

            if (mUiHelper != null) {

                if (resultCode == 0) {
                    removeProgressDialog();
                } else {
                    mUiHelper.onActivityResult(requestCode, resultCode, intent, FacebookUtils.dialogCallback);
                }
            }

            if (requestCode == Constants.BLOG_FOLLOW_STATUS) {

                if (intent.getStringExtra(Constants.BLOG_ISFOLLOWING).equalsIgnoreCase("0")) {
                    followClick.setText("FOLLOW");
                    isFollowing = false;

                } else {
                    followClick.setText("FOLLOWING");
                    isFollowing = true;
                }
            }

            Log.i("resultCount", String.valueOf(resultCode));
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    /**
     * It will give all the details & these will update all information on your screen;
     *
     * @param detailsResponse
     */

    private void getResponseUpdateUi(ArticleDetailResult detailsResponse) {
        detailData = detailsResponse;
        imageList = detailData.getBody().getImage();
        videoList = detailData.getBody().getVideo();
        authorType = detailData.getUserType();
        author = detailData.getUserName();

        if (!StringUtils.isNullOrEmpty(detailData.getImageUrl().getClientApp())) {
            Picasso.with(this).load(detailData.getImageUrl().getClientApp()).placeholder(R.drawable.default_article).resize(width, (int) (220 * density)).centerCrop().into(cover_image);
        }

        if (!StringUtils.isNullOrEmpty(detailData.getTitle())) {
            article_title.setText(detailData.getTitle());
//            collapsingToolbarLayout.setTitle(detailData.getTitle());
        }

        try {
            if (!StringUtils.isNullOrEmpty(detailData.getUserType())) {

                if (AppConstants.USER_TYPE_BLOGGER.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_BLOGGER);
                    author_type.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_blogger));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + blogSlug + "/article/" + titleSlug;
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EXPERT.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_EXPERT);
                    author_type.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_expert));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + titleSlug;
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITOR.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_EDITOR);
                    author_type.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_editor));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + titleSlug;
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITORIAL.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_EDITORIAL);
                    author_type.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_editorial));
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + titleSlug;
                    } else {
                        shareUrl = deepLinkURL;
                    }
                }
            } else {
                // Default Author type set to Blogger
                author_type.setText("Blogger");
                author_type.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_blogger));
                if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                    shareUrl = AppConstants.ARTICLE_SHARE_URL + blogSlug + "/article/" + titleSlug;
                } else {
                    shareUrl = deepLinkURL;
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }


        if (!StringUtils.isNullOrEmpty(detailData.getUserName())) {
            ((TextView) findViewById(R.id.user_name)).setText(detailData.getUserName());
        }

        if (!StringUtils.isNullOrEmpty(detailData.getCreated())) {
            ((TextView) findViewById(R.id.article_date)).setText(DateTimeUtils.getDateFromTimestamp(Long.parseLong(detailData.getCreated())));
        }

        String bodyDescription = detailData.getBody().getText();
        String bodyDesc = bodyDescription;
        if (imageList.size() > 0) {
            for (ImageData images : imageList) {
                if (bodyDescription.contains(images.getKey())) {
                    bodyDesc = bodyDesc.replace(images.getKey(), "<p style='text-align:center'><img src=" + images.getValue() + " style=\"width: 100%;\"+></p>");
                }
            }
            if (null != videoList && !videoList.isEmpty()) {
                for (VideoData video : videoList) {
                    if (bodyDescription.contains(video.getKey())) {
                        bodyDesc = bodyDesc.replace(video.getKey(), "<p style='text-align:center'><iframe allowfullscreen src=http:" + video.getVideoUrl() + "?modestbranding=1&amp;rel=0&amp;showinfo=0\" style=\"width: 100%;\"></iframe></p>");
                    }
                }
            }

//            <iframe allowfullscreen=\"true\" allowtransparency=\"true\" frameborder=\"0\" src=\"//www.youtube.com/embed/TqQ5xM2x1Gs\" width=\"640\" height=\"360\" class=\"note-video-clip\"></iframe>
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
                    if (bodyDescription.contains(video.getKey())) {
                        bodyDesc = bodyDesc.replace(video.getKey(), "<p style='text-align:center'><iframe allowfullscreen src=http:" + video.getVideoUrl() + "?modestbranding=1&amp;rel=0&amp;showinfo=0\" style=\"width: 100%;\" ></iframe></p>");
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
                    "    line-height: " + getResources().getInteger(R.integer.article_details_line_height) + "%;\n" +
                    "    text-align: left;\n" +
                    "}\n" +
                    "</style>" +
                    "</head><body>" + bodyDesc + "</body></html>";

            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mWebView.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");
            mWebView.getSettings().setJavaScriptEnabled(true);

        }

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                floatingActionButton.setPadding(-1, -1, -1, -1);
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
            Picasso.with(this).load(detailData.getProfilePic().getClientApp()).transform(new CircleTransformation()).into(target);
        }

        if (!StringUtils.isNullOrEmpty(detailData.getImageUrl().getClientApp())) {
            Picasso.with(this).load(detailData.getImageUrl().getClientApp()).placeholder(R.drawable.default_article).fit().into(cover_image);
        }

        hitUpdateViewCountAPI(detailData.getUserId(), detailData.getTags(), detailData.getCities());
        createSelectedTagsView();
    }

    /*
    * creates selected tags views from the list of selected topics.
    * */
    private void createSelectedTagsView() {
//        selectedTopics
        ArrayList<Map<String, String>> tagsList = detailData.getTags();
        LayoutInflater mInflater = LayoutInflater.from(this);

        for (int i = 0; i < tagsList.size(); i++) {

            for (Map.Entry<String, String> entry : tagsList.get(i).entrySet()) {
                String key = entry.getKey();
                final String value = entry.getValue();
                if (AppConstants.IGNORE_TAG.equals(key)) {
                    continue;
                }

                LinearLayout topicView = (LinearLayout) mInflater.inflate(R.layout.related_tags_view, null, false);
                topicView.setClickable(true);
                ((TextView) topicView.getChildAt(0)).setTag(key);
                ((TextView) topicView.getChildAt(0)).setText(value.toUpperCase());
                ((TextView) topicView.getChildAt(0)).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String categoryId = (String) v.getTag();
                        Intent intent = new Intent(ArticlesAndBlogsDetailsActivity.this, FilteredTopicsArticleListingActivity.class);
                        intent.putExtra("selectedTopics", categoryId);
                        intent.putExtra("displayName", value);
                        startActivity(intent);
                    }
                });
                tagsLayout.addView(topicView);
            }
        }
    }

    private void displayComments(ViewHolder holder, CommentsData commentList,
                                 boolean isNewComment) {
        if (holder != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.custom_comment_cell, null);
            holder.commentorsImage = (CircularImageView) view.findViewById(R.id.network_img);
            holder.commentName = (TextView) view.findViewById(R.id.txvCommentTitle);
            holder.commentDescription = (TextView) view.findViewById(R.id.txvCommentDescription);
            holder.dateTxt = (TextView) view.findViewById(R.id.txvDate);
            holder.replyTxt = (TextView) view.findViewById(R.id.txvReply);
            holder.editTxt = (TextView) view.findViewById(R.id.txvEdit);
            holder.replierImageView = (CircularImageView) view.findViewById(R.id.replyUserImageView);
            holder.replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);
            holder.replierUsernameTextView = (TextView) view.findViewById(R.id.replyUserNameTextView);
            holder.replyCommentView = (RelativeLayout) view.findViewById(R.id.replyRelativeLayout);

            holder.commentorsImage.setOnClickListener(this);
            holder.commentName.setOnClickListener(this);
            holder.replyCommentView.setOnClickListener(this);
            holder.replyCommentView.setTag(commentList);
            holder.replyTxt.setOnClickListener(this);
            holder.editTxt.setOnClickListener(this);

            view.setTag(commentList);

            if (!"fb".equals(commentList.getComment_type()) && SharedPrefUtils.getUserDetailModel(this).getDynamoId().equals(commentList.getUserId())) {
                holder.editTxt.setVisibility(View.VISIBLE);
            } else {
                holder.editTxt.setVisibility(View.INVISIBLE);
            }

            if ("fb".equals(commentList.getComment_type())) {
                holder.replyTxt.setVisibility(View.GONE);
            } else {
                holder.replyTxt.setVisibility(View.VISIBLE);
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
                    Picasso.with(this).load(commentList.getProfile_image().getClientAppMin()).placeholder(R.drawable.default_commentor_img).into(holder.commentorsImage);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    Picasso.with(this).load(R.drawable.default_commentor_img).into(holder.commentorsImage);
                }
            } else {
                Picasso.with(this).load(R.drawable.default_commentor_img).into(holder.commentorsImage);
            }

            if (isNewComment) {
                commentLayout.addView(view, 0);
            } else {
                commentLayout.addView(view);
            }
            if (commentList.getReplies() != null && commentList.getReplies().size() > 0) {

                holder.replyCommentView.setVisibility(View.VISIBLE);
                if (commentList.getReplies().size() > 1) {
                    holder.replyCountTextView.setText(commentList.getReplies().size() + " replies");
                } else {
                    holder.replyCountTextView.setText(commentList.getReplies().size() + " reply");
                }
                holder.replierUsernameTextView.setText("" + commentList.getReplies().get(0).getName());

                if (commentList.getProfile_image() != null && !StringUtils.isNullOrEmpty(commentList.getProfile_image().getClientAppMin())) {
                    try {
                        Picasso.with(this).load(commentList.getReplies().get(0).getProfile_image().getClientAppMin())
                                .placeholder(R.drawable.default_commentor_img).into(holder.replierImageView);
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        Picasso.with(this).load(R.drawable.default_commentor_img).into(holder.replierImageView);
                    }
                } else {
                    Picasso.with(this).load(R.drawable.default_commentor_img).into(holder.replierImageView);
                }
            } else {
                holder.replyCommentView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.add_comment:
                    break;
                case R.id.txvReply:
                    try {
                        commentFragment = new CommentRepliesDialogFragment();
                        Bundle _args = new Bundle();
                        _args.putParcelable("commentData", (CommentsData) ((View) v.getParent().getParent()).getTag());
                        _args.putString("articleId", articleId);
                        commentFragment.setArguments(_args);
                        FragmentManager fm = getSupportFragmentManager();
                        commentFragment.show(fm, "Replies");
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                    break;
                case R.id.replyRelativeLayout:
                    try {
                        commentFragment = new CommentRepliesDialogFragment();
                        Bundle commentArgs = new Bundle();
                        commentArgs.putParcelable("commentData", (CommentsData) ((View) v.getParent()).getTag());
                        commentArgs.putString("articleId", articleId);
                        commentFragment.setArguments(commentArgs);
                        FragmentManager fm = getSupportFragmentManager();
                        commentFragment.show(fm, "Replies");
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                    break;
                case R.id.txvEdit:
                    try {
                        editCommentsRepliesFragment = new EditCommentsRepliesFragment();
                        CommentsData cData = (CommentsData) ((View) v.getParent().getParent()).getTag();
                        commentEditView = (View) v.getParent().getParent();
                        Bundle _args = new Bundle();
                        _args.putParcelable("commentData", cData);
                        _args.putString("articleId", articleId);
                        _args.putInt(AppConstants.COMMENT_OR_REPLY_OR_NESTED_REPLY, 0);
                        editCommentsRepliesFragment.setArguments(_args);
                        FragmentManager fm = getSupportFragmentManager();
                        editCommentsRepliesFragment.show(fm, "Replies");
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                    break;
                case R.id.add_comment_btn:

                    if (StringUtils.isNullOrEmpty(commentText.getText().toString())) {
                        ToastUtils.showToast(getApplicationContext(), "Please write to comment...", Toast.LENGTH_SHORT);
                    } else {
                        String contentData = commentText.getText().toString();
                        Retrofit retro = BaseApplication.getInstance().getRetrofit();
                        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
                        AddCommentRequest addCommentRequest = new AddCommentRequest();
                        addCommentRequest.setArticleId(articleId);
                        addCommentRequest.setUserComment(contentData);
                        Call<AddCommentResponse> callBookmark = articleDetailsAPI.addComment(addCommentRequest);
                        callBookmark.enqueue(addCommentsResponseCallback);
                        showProgressDialog("Please wait ...");
                    }
                    break;
                case R.id.network_img:
                    CommentsData commentData = (CommentsData) ((View) v.getParent().getParent()).getTag();
                    if (!"fb".equals(commentData.getComment_type())) {
                        Intent profileIntent = new Intent(this, BloggerDashboardActivity.class);
                        profileIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, commentData.getUserId());
                        startActivity(profileIntent);
                    }
                    break;
                case R.id.txvCommentTitle:
                    CommentsData cData = (CommentsData) ((View) v.getParent().getParent()).getTag();
                    if (!"fb".equals(cData.getComment_type())) {
                        Intent userProfileIntent = new Intent(this, BloggerDashboardActivity.class);
                        userProfileIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, cData.getUserId());
                        startActivity(userProfileIntent);
                    }
                    break;
                case R.id.follow_click:
                    followAPICall(authorId);
                    break;

                case R.id.user_image:
                case R.id.user_name:
                    Intent intentnn = new Intent(this, BloggerDashboardActivity.class);
                    intentnn.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, detailData.getUserId());
                    startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                    break;
                case R.id.relatedArticles1: {
                    Utils.pushEventRelatedArticle(ArticlesAndBlogsDetailsActivity.this, GTMEventType.RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 1);
                    Intent intent = new Intent(this, ArticlesAndBlogsDetailsActivity.class);
                    ArticleListingResult parentingListData = (ArticleListingResult) v.getTag();
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId() + "");
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    startActivity(intent);
                    break;
                }
                case R.id.relatedArticles2: {
                    Utils.pushEventRelatedArticle(ArticlesAndBlogsDetailsActivity.this, GTMEventType.RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 2);
                    Intent intent = new Intent(this, ArticlesAndBlogsDetailsActivity.class);
                    ArticleListingResult parentingListData = (ArticleListingResult) v.getTag();
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId() + "");
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    startActivity(intent);
                    break;
                }
                case R.id.relatedArticles3: {
                    Utils.pushEventRelatedArticle(ArticlesAndBlogsDetailsActivity.this, GTMEventType.RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 3);
                    Intent intent = new Intent(this, ArticlesAndBlogsDetailsActivity.class);
                    ArticleListingResult parentingListData = (ArticleListingResult) v.getTag();
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId() + "");
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    startActivity(intent);
                    break;
                }
                case R.id.trendingRelatedArticles1: {
                    Utils.pushEventRelatedArticle(ArticlesAndBlogsDetailsActivity.this, GTMEventType.TRENDING_RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 1);
                    Intent intent = new Intent(this, ArticlesAndBlogsDetailsActivity.class);
                    ArticleListingResult parentingListData = (ArticleListingResult) v.getTag();
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    startActivity(intent);
                    break;
                }
                case R.id.trendingRelatedArticles2: {
                    Utils.pushEventRelatedArticle(ArticlesAndBlogsDetailsActivity.this, GTMEventType.TRENDING_RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 2);
                    Intent intent = new Intent(this, ArticlesAndBlogsDetailsActivity.class);
                    ArticleListingResult parentingListData = (ArticleListingResult) v.getTag();
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    startActivity(intent);
                    break;
                }
                case R.id.trendingRelatedArticles3: {
                    Utils.pushEventRelatedArticle(ArticlesAndBlogsDetailsActivity.this, GTMEventType.TRENDING_RELATED_ARTICLE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Blog Detail", ((ArticleListingResult) v.getTag()).getTitleSlug(), 3);
                    Intent intent = new Intent(this, ArticlesAndBlogsDetailsActivity.class);
                    ArticleListingResult parentingListData = (ArticleListingResult) v.getTag();
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    startActivity(intent);
                    break;
                }
                case R.layout.related_tags_view: {
                    String categoryId = (String) v.getTag();
                    Intent intent = new Intent(ArticlesAndBlogsDetailsActivity.this, FilteredTopicsArticleListingActivity.class);
                    intent.putExtra("selectedTopics", categoryId);
                    intent.putExtra("displayName", ((TextView) ((LinearLayout) v).getChildAt(0)).getText());
                    startActivity(intent);
                    break;
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        View view = (View) mScrollView.getChildAt(mScrollView.getChildCount() - 1);
        Rect scrollBounds = new Rect();
        mScrollView.getHitRect(scrollBounds);
        int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));
        if (diff <= 10 && !isLoading && !StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http") && !AppConstants.PAGINATION_END_VALUE.equals(pagination)) {
            getMoreComments();
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
                hideToolbar();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                showToolbar();
            }
        }
    }

    private void hideToolbar() {
//        mToolbar.animate().translationY(-mToolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();

        mToolbar.animate()
                .translationY(-mToolbar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        toolbarSetElevation(0);
                        getSupportActionBar().hide();
                    }
                });
    }

    private void showToolbar() {
//        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
        mToolbar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        getSupportActionBar().show();
//                        toolbarSetElevation(verticalOffset == 0 ? 0 : TOOLBAR_ELEVATION);
                    }
                });
    }

    private class ViewHolder {
        private CircularImageView commentorsImage;
        private TextView commentName;
        private TextView commentDescription;
        private TextView dateTxt;
        private TextView replyTxt;
        private TextView editTxt;
        private CircularImageView replierImageView;
        private TextView replierUsernameTextView;
        private TextView replyCountTextView;
        private RelativeLayout replyCommentView;
    }

    public void followAPICall(String id) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);

        if (isFollowing) {
            isFollowing = false;
            followClick.setText("FOLLOW");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            followClick.setText("FOLLOWING");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.followUser(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }

    }

    private void addRemoveBookmark() {

        if (bookmarkStatus == 0) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(articleId);
            bookmarkStatus = 1;
            menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp_fill);
            Call<AddBookmarkResponse> call = articleDetailsAPI.addBookmark(articleDetailRequest);
            call.enqueue(addBookmarkResponseCallback);
        } else {
            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
            deleteBookmarkRequest.setId(bookmarkId);
            bookmarkStatus = 0;
            menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp);
            Call<AddBookmarkResponse> call = articleDetailsAPI.deleteBookmark(deleteBookmarkRequest);
            call.enqueue(addBookmarkResponseCallback);
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
                ArticleDetailResult responseData = (ArticleDetailResult) response.body();
                newCommentLayout.setVisibility(View.VISIBLE);
                getResponseUpdateUi(responseData);
                if (StringUtils.isNullOrEmpty(authorId)) {
                    authorId = detailData.getUserId();
                    hitBookmarkFollowingStatusAPI();
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
                getArticleDetailsWebserviceAPI();
                return;
            }
            try {
                ViewCountResponse responseData = (ViewCountResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    articleViewCountTextView.setText(responseData.getData().get(0).getCount() + " Views");
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
        Call<ArticleDetailResponse> call = articleDetailsAPI.getArticleDetailsFromWebservice(articleId);
        call.enqueue(articleDetailResponseCallbackWebservice);
    }

    Callback<ArticleDetailResponse> articleDetailResponseCallbackWebservice = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }
            try {
                ArticleDetailResponse responseData = (ArticleDetailResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    newCommentLayout.setVisibility(View.VISIBLE);
                    getResponseUpdateUi(responseData.getData().getResult());
                    if (StringUtils.isNullOrEmpty(authorId)) {
                        authorId = detailData.getUserId();
                        hitBookmarkFollowingStatusAPI();
                    }
                    hitRelatedArticleAPI();
                    commentURL = responseData.getData().getResult().getCommentsUri();

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
        public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
            removeProgressDialog();
            handleExceptions(t);
        }
    };

    private OnWebServiceCompleteListener mGetArticleListingListener = new OnWebServiceCompleteListener() {
        @Override
        public void onWebServiceComplete(VolleyBaseResponse response, boolean isError) {
            Log.d("Response back =", " " + response.getResponseBody());
            if (isError) {
                if (null != this && response.getResponseCode() != 999)
                    showToast("Something went wrong from server");
            } else {
                Log.d("Response = ", response.getResponseBody());
                if (response == null) {
                    showToast("Something went wrong from server");
                    removeProgressDialog();
                    return;
                }

                ArticleListingResponse responseBlogData;
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                    responseBlogData = gson.fromJson(response.getResponseBody(), ArticleListingResponse.class);
//                    responseBlogData = new Gson().fromJson(response.getResponseBody(), ArticleListingResponse.class);
                } catch (JsonSyntaxException jse) {
                    Crashlytics.logException(jse);
                    Log.d("JsonSyntaxException", Log.getStackTraceString(jse));
                    showToast("Something went wrong from server");
                    removeProgressDialog();
                    return;
                }

                if (responseBlogData.getCode() == 200 && Constants.SUCCESS.equals(responseBlogData.getStatus())) {
                    ArrayList<ArticleListingResult> dataList = responseBlogData.getData().get(0).getResult();
                    if (dataList == null || dataList.size() == 0) {

                    } else {
                        trendingArticles.setVisibility(View.VISIBLE);
                        if (dataList.size() >= 3) {
                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(0).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList.get(0));

                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(1).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            trendingRelatedArticles2.setTag(dataList.get(1));

                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(2).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles3.getArticleImageView());
                            trendingRelatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            trendingRelatedArticles3.setTag(dataList.get(2));
                        } else if (dataList.size() == 2) {
                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(0).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList.get(0));

                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(1).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles2.getArticleImageView());
                            trendingRelatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            trendingRelatedArticles2.setTag(dataList.get(1));

                            trendingRelatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(0).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(trendingRelatedArticles1.getArticleImageView());
                            trendingRelatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            trendingRelatedArticles1.setTag(dataList.get(0));
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
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getId().equals(articleId)) {
                            dataList.remove(i);
                            break;
                        }
                    }
                    if (dataList.size() == 0) {
                        Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsAPI.getPublishedArticles(authorId, 0, 1, 4);
                        callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
                    } else {
                        recentAuthorArticleHeading.setText("RELATED ARTICLES");
                        recentAuthorArticles.setVisibility(View.VISIBLE);

                        if (dataList.size() >= 3) {
                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(0).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList.get(0));

                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(1).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList.get(1));

                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(2).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles3.getArticleImageView());
                            relatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            relatedArticles3.setTag(dataList.get(2));
                        } else if (dataList.size() == 2) {
                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(0).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList.get(0));

                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(1).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList.get(1));
                            relatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(0).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList.get(0));
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
                showToast("Something went wrong from server");
                return;
            }

            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getId().equals(articleId)) {
                            dataList.remove(i);
                            break;
                        }
                    }
                    if (dataList.size() == 0) {

                    } else {
                        recentAuthorArticleHeading.setText("RECENT BLOGS FROM " + author);
                        recentAuthorArticles.setVisibility(View.VISIBLE);

                        if (dataList.size() >= 3) {
                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(0).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList.get(0));

                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(1).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList.get(1));

                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(2).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles3.getArticleImageView());
                            relatedArticles3.setArticleTitle(dataList.get(2).getTitle());
                            relatedArticles3.setTag(dataList.get(2));
                        } else if (dataList.size() == 2) {
                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(0).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles1.getArticleImageView());
                            relatedArticles1.setArticleTitle(dataList.get(0).getTitle());
                            relatedArticles1.setTag(dataList.get(0));

                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(1).getImageUrl().getClientAppThumbnail()).
                                    placeholder(R.drawable.default_article).fit().into(relatedArticles2.getArticleImageView());
                            relatedArticles2.setArticleTitle(dataList.get(1).getTitle());
                            relatedArticles2.setTag(dataList.get(1));
                            relatedArticles3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            Picasso.with(ArticlesAndBlogsDetailsActivity.this).load(dataList.get(0).getImageUrl().getClientAppThumbnail()).
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
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            handleExceptions(t);
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
                showToast("Something went wrong from server");
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
                commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));
                ViewHolder viewHolder = null;
                viewHolder = new ViewHolder();
                for (int i = 0; i < arrayList.size(); i++) {
                    displayComments(viewHolder, arrayList.get(i), false);
                }
            } catch (JSONException jsonexception) {
                Crashlytics.logException(jsonexception);
                Log.d("JSONException", Log.getStackTraceString(jsonexception));
                showToast("Something went wrong while parsing response from server");
            } catch (Exception ex) {
                Crashlytics.logException(ex);
                Log.d("MC4kException", Log.getStackTraceString(ex));
                showToast("Something went wrong from server");
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
                showToast("Something went wrong from server");
                return;
            }

            try {
                isLoading = false;
                FBCommentResponse responseData = (FBCommentResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<CommentsData> dataList = responseData.getData().getResult();
                    pagination = responseData.getData().getPagination();
                    if (dataList.size() == 0) {
                        pagination = AppConstants.PAGINATION_END_VALUE;
                    } else {
                        commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));
                        ViewHolder viewHolder = null;
                        viewHolder = new ViewHolder();
                        for (int i = 0; i < dataList.size(); i++) {
                            CommentsData fbCommentData = dataList.get(i);
                            fbCommentData.setComment_type("fb");
                            displayComments(viewHolder, fbCommentData, false);
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
        public void onFailure(Call<FBCommentResponse> call, Throwable t) {
            mLodingView.setVisibility(View.GONE);
            handleExceptions(t);
        }
    };


    private Callback<ArticleDetailResponse> isBookmarkedFollowedResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }

            ArticleDetailResponse responseData = (ArticleDetailResponse) response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                bookmarkFlag = responseData.getData().getResult().getBookmarkStatus();
                if ("0".equals(bookmarkFlag)) {
                    menu.getItem(0).setEnabled(true);
                    menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp);
                    bookmarkStatus = 0;
                } else {
                    menu.getItem(0).setEnabled(true);
                    menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp_fill);
                    bookmarkStatus = 1;
                }
                bookmarkId = responseData.getData().getResult().getBookmarkId();
                if (SharedPrefUtils.getUserDetailModel(ArticlesAndBlogsDetailsActivity.this).getDynamoId().equals(authorId)) {
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
                showToast(getString(R.string.server_went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<AddCommentResponse> addCommentsResponseCallback = new Callback<AddCommentResponse>() {
        @Override
        public void onResponse(Call<AddCommentResponse> call, retrofit2.Response<AddCommentResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }

            AddCommentResponse responseData = (AddCommentResponse) response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                showToast("Comment added successfully!");
                ViewHolder vh = new ViewHolder();
                CommentsData cd = new CommentsData();
                cd.setId(responseData.getData().getId());
                cd.setBody(commentText.getText().toString().trim());
                cd.setUserId(SharedPrefUtils.getUserDetailModel(ArticlesAndBlogsDetailsActivity.this).getDynamoId());
                cd.setName(SharedPrefUtils.getUserDetailModel(ArticlesAndBlogsDetailsActivity.this).getFirst_name());
                cd.setReplies(new ArrayList<CommentsData>());

                ProfilePic profilePic = new ProfilePic();
                profilePic.setClientApp(SharedPrefUtils.getProfileImgUrl(ArticlesAndBlogsDetailsActivity.this));
                profilePic.setClientAppMin(SharedPrefUtils.getProfileImgUrl(ArticlesAndBlogsDetailsActivity.this));
                cd.setProfile_image(profilePic);
                cd.setCreate("" + System.currentTimeMillis() / 1000);
                commentText.setText("");
                displayComments(vh, cd, false);

            } else {
                showToast(responseData.getReason());
            }
        }

        @Override
        public void onFailure(Call<AddCommentResponse> call, Throwable t) {
            removeProgressDialog();
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
                showToast("Something went wrong from server");
                return;
            }
            AddBookmarkResponse responseData = (AddBookmarkResponse) response.body();
            updateBookmarkStatus(ADD_BOOKMARK, responseData);
        }

        @Override
        public void onFailure(Call<AddBookmarkResponse> call, Throwable t) {
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
                showToast(responseData.getReason());
            } else {
                showToast(getString(R.string.went_wrong));
            }
        }
    }

    public void handleExceptions(Throwable t) {
        if (t instanceof UnknownHostException) {
            showToast(getString(R.string.error_network));
        } else if (t instanceof SocketTimeoutException) {
            showToast("connection timed out");
        } else {
            showToast(getString(R.string.server_went_wrong));
        }
        Crashlytics.logException(t);
        Log.d("MC4kException", Log.getStackTraceString(t));
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

                    RelativeLayout replyCommentView = (RelativeLayout) commentLayout.getChildAt(i).findViewById(R.id.replyRelativeLayout);
                    ImageView replierImageView = (ImageView) replyCommentView.findViewById(R.id.replyUserImageView);
                    TextView replyCountTextView = (TextView) replyCommentView.findViewById(R.id.replyCountTextView);
                    TextView replierUsernameTextView = (TextView) replyCommentView.findViewById(R.id.replyUserNameTextView);

                    if (cdata.getReplies() != null && cdata.getReplies().size() > 0) {

                        replyCommentView.setVisibility(View.VISIBLE);
                        if (cdata.getReplies().size() > 1) {
                            replyCountTextView.setText(cdata.getReplies().size() + " replies");
                        } else {
                            replyCountTextView.setText(cdata.getReplies().size() + " reply");
                        }
                        replierUsernameTextView.setText("" + cdata.getReplies().get(0).getName());

                        if (cdata.getProfile_image() != null && !StringUtils.isNullOrEmpty(cdata.getProfile_image().getClientAppMin())) {
                            try {
                                Picasso.with(this).load(cdata.getReplies().get(0).getProfile_image().getClientAppMin())
                                        .placeholder(R.drawable.default_commentor_img).into(replierImageView);
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                                Log.d("MC4kException", Log.getStackTraceString(e));
                                Picasso.with(this).load(R.drawable.default_commentor_img).into(replierImageView);
                            }
                        } else {
                            Picasso.with(this).load(R.drawable.default_commentor_img).into(replierImageView);
                        }
                    } else {
                        replyCommentView.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    public void updateCommentReplyNestedReply(CommentsData updatedComment, int editType) {
        for (int i = 0; i < commentLayout.getChildCount(); i++) {
            CommentsData cdata = (CommentsData) commentLayout.getChildAt(i).getTag();
            if (editType == AppConstants.EDIT_COMMENT) {
                if (updatedComment.getId().equals(cdata.getId())) {
                    TextView bodyTextView = (TextView) commentLayout.getChildAt(i).findViewById(R.id.txvCommentDescription);
                    Log.d("Comment ", "comment mil gaya");
                    cdata = updatedComment;
                    bodyTextView.setText(cdata.getBody());
                }
            } else {
            }

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
                FollowUnfollowUserResponse responseData = (FollowUnfollowUserResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followClick.setText("FOLLOW");
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
                FollowUnfollowUserResponse responseData = (FollowUnfollowUserResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followClick.setText("FOLLOWING");
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
            mContentView = (RelativeLayout) findViewById(R.id.coordinatorLayout);
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(ArticlesAndBlogsDetailsActivity.this);
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            setContentView(mCustomViewContainer);
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
                setContentView(mContentView);
            }
        }
    }

}