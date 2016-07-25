package com.mycity4kids.ui.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.google.gson.JsonSyntaxException;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.kelltontech.utils.facebook.model.FacebookUtils;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ArticleBlogDetailsController;
import com.mycity4kids.controller.ArticleBlogFollowController;
import com.mycity4kids.controller.BlogShareSpouseController;
import com.mycity4kids.controller.BookmarkController;
import com.mycity4kids.controller.CommentController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnWebServiceCompleteListener;
import com.mycity4kids.models.bookmark.BookmarkModel;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingdetails.CommentRequest;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.parentingdetails.ImageData;
import com.mycity4kids.models.parentingstop.ArticleBlogFollowRequest;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ArticleDetailData;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.BlogShareSpouseModel;
import com.mycity4kids.newmodels.VolleyBaseResponse;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleModel;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.NewArticleListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.AuthorDetailsAPI;
import com.mycity4kids.ui.CircleTransformation;
import com.mycity4kids.ui.fragment.CommentRepliesDialogFragment;
import com.mycity4kids.ui.fragment.WhoToRemindDialogFragment;
import com.mycity4kids.volley.HttpVolleyRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * @author deepanker.chaudhary
 */
public class ArticlesAndBlogsDetailsActivity extends BaseActivity implements OnClickListener {
    //    private ImageLoader.ImageCache imageCache;
//    private ImageLoader imageLoader;
    ArticleDetailData detailData;
    private UiLifecycleHelper mUiHelper;
    private boolean isCommingFromCommentAPI;
    private int ADD_COMMENT_OR_REPLY = 0;
    private String articleId;

    private NestedScrollView mScrollView;
    private ArrayList<ImageData> imageList;
    Boolean isFollowing = false;


    LinearLayout newCommentLayout;
    EditText commentText;
    ImageView commentBtn;
    TextView followClick;
    TextView recentAuthorArticleHeading, recentAuthorArticle1, recentAuthorArticle2, recentAuthorArticle3;
    LinearLayout trendingArticles, recentAuthorArticles;
    TextView trendingArticle1, trendingArticle2, trendingArticle3;
    Toolbar mToolbar;
    private float density;

    private ImageView cover_image;
    private TextView article_title;
    private TextView author_type;
    private String followAuthorId;
    String authorType, author;
    private String blogName;
    private int bookmarkStatus;

    private String deepLinkURL;
    private GoogleApiClient mClient;
    private String TAG;
    private String screenTitle = "Parenting Blogs";
    Button share_spouse;
    public ArrayList<BlogShareSpouseModel> whoToShareList;

    //Commments Lazy loading
//    private ProgressBar progressBar;
    private String commentTypeToFetch;
    Boolean isLoading = false;
    private int offset = 0;
    private RelativeLayout mLodingView;
    private String versionName;

    //New UI changes
    private CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton floatingActionButton;
    private Menu menu;
    LinearLayout commLayout;
    CoordinatorLayout coordinatorLayout;
    Bitmap defaultBloggerBitmap, defaultCommentorBitmap;
    private String bookmarkFlag = "0";

    private String commentURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(ArticlesAndBlogsDetailsActivity.this, "Article Details", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        deepLinkURL = getIntent().getStringExtra(Constants.DEEPLINK_URL);
        TAG = ArticlesAndBlogsDetailsActivity.this.getClass().getSimpleName();
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.APP_INDEX_API).build();
        commentTypeToFetch = AppConstants.COMMENT_TYPE_DB;
        try {

            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(getIntent().getIntExtra(AppConstants.NOTIFICATION_ID, 0));

            setContentView(R.layout.new_article_details_activity);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            ((TextView) findViewById(R.id.add_comment)).setOnClickListener(this);
            ((TextView) findViewById(R.id.user_name)).setOnClickListener(this);
            floatingActionButton = (FloatingActionButton) findViewById(R.id.user_image);
            floatingActionButton.setOnClickListener(this);
            share_spouse = (Button) findViewById(R.id.share_spouse);
            share_spouse.setOnClickListener(this);
            author_type = (TextView) findViewById(R.id.blogger_type);
            followClick = (TextView) findViewById(R.id.follow_click);
            followClick.setOnClickListener(this);
            article_title = (TextView) findViewById(R.id.article_title);
            String coverImageUrl = getIntent().getStringExtra(Constants.ARTICLE_COVER_IMAGE);
            recentAuthorArticleHeading = (TextView) findViewById(R.id.recentAuthorArticleHeading);
            recentAuthorArticle1 = (TextView) findViewById(R.id.recentAuthorArticle1);
            recentAuthorArticle2 = (TextView) findViewById(R.id.recentAuthorArticle2);
            recentAuthorArticle3 = (TextView) findViewById(R.id.recentAuthorArticle3);
            trendingArticles = (LinearLayout) findViewById(R.id.trendingArticles);
            recentAuthorArticles = (LinearLayout) findViewById(R.id.recentAuthorArticles);
            trendingArticle1 = (TextView) findViewById(R.id.trendingArticle1);
            trendingArticle2 = (TextView) findViewById(R.id.trendingArticle2);
            trendingArticle3 = (TextView) findViewById(R.id.trendingArticle3);
            cover_image = (ImageView) findViewById(R.id.cover_image);
            density = getResources().getDisplayMetrics().density;
            int width = getResources().getDisplayMetrics().widthPixels;
            if (!StringUtils.isNullOrEmpty(coverImageUrl)) {
                Picasso.with(this).load(coverImageUrl).placeholder(R.drawable.blog_bgnew).resize(width, (int) (220 * density)).centerCrop().into(cover_image);
            }

            mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbarLayout.setExpandedTitleGravity(Gravity.CENTER);
            collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

            defaultBloggerBitmap = (new CircleTransformation()).transform(BitmapFactory.decodeResource(getResources(),
                    R.drawable.default_blogger_profile_img));
            defaultCommentorBitmap = (new CircleTransformation()).transform(BitmapFactory.decodeResource(getResources(),
                    R.drawable.default_commentor_img));
            floatingActionButton.setPadding(-1, -1, -1, -1);
            floatingActionButton.setImageDrawable(new BitmapDrawable(getResources(), defaultBloggerBitmap));

            newCommentLayout = (LinearLayout) findViewById(R.id.comment_layout);
            commentBtn = (ImageView) findViewById(R.id.add_comment_btn);
            commentBtn.setOnClickListener(this);
            recentAuthorArticle1.setOnClickListener(this);
            recentAuthorArticle2.setOnClickListener(this);
            recentAuthorArticle3.setOnClickListener(this);
            trendingArticle1.setOnClickListener(this);
            trendingArticle2.setOnClickListener(this);
            trendingArticle3.setOnClickListener(this);
            commentText = (EditText) findViewById(R.id.editCommentTxt);

            mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
            findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

            mScrollView = (NestedScrollView) findViewById(R.id.scroll_view);
            mUiHelper = new UiLifecycleHelper(this, FacebookUtils.callback);
            mUiHelper.onCreate(savedInstanceState);
            share_spouse.setVisibility(View.INVISIBLE);
            commLayout = ((LinearLayout) findViewById(R.id.commnetLout));
            mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                @Override
                public void onScrollChanged() {

                    View view = (View) mScrollView.getChildAt(mScrollView.getChildCount() - 1);

                    Rect scrollBounds = new Rect();
                    mScrollView.getHitRect(scrollBounds);
                    if (share_spouse.getVisibility() != View.VISIBLE) {
                        if (commLayout.getLocalVisibleRect(scrollBounds)) {
                            // Any portion of the imageView, even a single pixel, is within the visible window
                            share_spouse.setAnimation((AnimationUtils.loadAnimation(ArticlesAndBlogsDetailsActivity.this, R.anim.right_to_left)));
                            share_spouse.setVisibility(View.VISIBLE);
                        } else {
                            // NONE of the imageView is within the visible window
                            share_spouse.setVisibility(View.INVISIBLE);
                        }
                    }
                    int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));

                    if (diff <= 10 && !isLoading && !StringUtils.isNullOrEmpty(commentURL)) {
                        getMoreComments();
                    }
                }
            });

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                articleId = bundle.getString(Constants.ARTICLE_ID);
//                if (!ConnectivityUtils.isNetworkEnabled(this)) {
//                    showToast(getString(R.string.error_network));
//                    return;
//                }
                showProgressDialog(getString(R.string.fetching_data));
                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);

                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    Crashlytics.logException(e);
                    Log.d("NameNotFoundException", Log.getStackTraceString(e));
                }
                versionName = pInfo.versionName;
//                String test = "{\"id\":\"article-2535488b90f248eab3e6cabcbf9b4468\",\"userId\":\"7e9e7cfb2a904ce28e48dcdc6b1ba537\",\"title\":\"Life is the most uncertain of all!\",\"titleSlug\":\"life-is-the-most-uncertain-of-all\",\"body\":\"  <p>Firstly, I pay my condolences to the ones who lost their friends and dear ones in the high magnitude earthquake that hit Nepal last Saturday.</p>  <p>Such catastrophic incidents always leave me wondering how uncertain life is. We plan for the future keeping in mind our family and few moments like this bring everything, every dream, every aspiration just crumbling down to the ground.</p>  <p>So much of destruction and loss! Both of life, emotions and faith apart from material. Why does life hold so much uncertainty with it? There must be hundreds of people and kids gone out or maybe just enjoying at their warm and cozy abode in Nepal this morning, who would not be able to see and feel the sunshine today or ever.</p>  <p>We have no option but to bow down before god at such times and thank the almighty of being so kind enough on us that our families have made it to their homes safe and sound today. And also pray and ask him to have his mercy on us always.</p>  <p>My heart goes out to the ones who must have lost their loved ones. And again my curious heart wants to know why is life so uncertain...</p>  <p>Today my daughter held me so tightly while we in North India were feeling the tremors of what was devastating Nepal. I could sense her little heart fearing for life and safety and the worry to have her mother safe at that time. It makes me think time and again that we have progressed so much as mankind but life is one thing that still holds so much uncertainty...</p>  <p>Is this god's way of controlling the universe, is that why mother nature plays such tricks on us like tsunami, earthquake, flood, hurricane etc</p>  <p>I am not being a pessimistic but I still wonder what happens to the dream of a secure future and plannings when such times hit <i><u>:(</u></i></p>  <p>What happens to those unsaid words and expressions we never made that our dear ones never heard us say. What happens to the promises and certainty of our words held on any plan in the future.</p>  <p>Life is really short, you get out of the house and you don't know whether you will make it safe home or not, whether you leave for office or to complete some errands. With so much violence already encasing our lives in the form of terrorist attacks, thefts, road rages etc if the nature too starts playing tricks, what happens then....</p>  <p>I think we need to work towards our environment and its an awakening call to pull up all your relations and dreams. Don't think about saying or expressing something tomorrow. For tomorrow may never come!</p>  <p>Live today.. I'm not asking to spend each penny today and have nothing for tomorrow. Do remember even in catastrophes many lucky/ unlucky ones survive the worst even if it means being a sole survivor <i><u>:(</u></i></p>  <p>As I end this blog, I can still feel tremors of the aftershock shaking my home. I whole heartedly pray almighty to bless us all with the support and companionship of our family always and always. To bless us and keep us safe, give us wisdom to acknowledge all good relations and respect the ones who love and care for us. Amen!</p>  <p>Love &amp; Smiles Always,</p>  <p>&nbsp;</p><p>Subita Vikram Rooprai <em><u>:)</u></em></p>  <p>&nbsp;</p>  <br>\",\"excerpt\":\"Firstly, I pay my condolences to the ones who lost their friends and dear ones in the high magnitude earthquake that hit Nepal last Saturday. Such catastrophic incidents always leave me wondering how uncertain life is. We plan for the future keeping in mind our family and few moments like this bring everything, every... \",\"imageUrl\":\"/uploads/resized/2Rm8biAYF39MIGL2nofh0i2vUnV2kWchPWR-1Fi7sN_FOJ3Bc_SCpw1_9Shm3sSArAVgNszdcIp2iKVdv_bxOXmF_SZVhFgscg7c8WWpnqMV2jeRFokP_GSipzn8L6rIzDGD5nw2CkKeVqlQRIHFIqGj7GMc_-0PQRd723-bCA=w280-h385-nc.jpeg\",\"commentUri\":\"modif\"}";
//                ArticleDetailData add = new Gson().fromJson(test, ArticleDetailData.class);
                articleId = "article-7b83891e32ca41d08adbbea1e4efb29d";
                Call<ArticleDetailData> call = articleDetailsAPI.getDemoArticle(articleId);
                call.enqueue(articleDetailResponseCallback);

                ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
                articleDetailRequest.setArticleId(articleId);
                Call<ArticleDetailData> callBookmark = articleDetailsAPI.checkBookmarkStatus(articleDetailRequest);
                callBookmark.enqueue(isBookmarkedResponseCallback);
            }
        } catch (Exception e) {
            removeProgressDialog();
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

    }

    private void getMoreComments() {
        isLoading = true;
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            showToast(getString(R.string.error_network));
            return;
        }
        mLodingView.setVisibility(View.VISIBLE);

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);

//        Call<ResponseBody> call = articleDetailsAPI.getArticleComments(articleId,
//                "" + AppConstants.COMMENT_LIMIT, "" + offset, commentTypeToFetch,
//                "" + SharedPrefUtils.getUserDetailModel(this).getId(), versionName);
//
//        call.enqueue(articleCommentsResponseCallback);

        Call<ResponseBody> call = articleDetailsAPI.getComments(commentURL);
        call.enqueue(commentsCallback);

//        GetCommentsRequestModel getCommentsRequestModel = new GetCommentsRequestModel();
//        getCommentsRequestModel.setArticleId(articleId);
//        getCommentsRequestModel.setLimit(AppConstants.COMMENT_LIMIT);
//        getCommentsRequestModel.setOffset(offset);
//        getCommentsRequestModel.setCommentType(commentTypeToFetch);
//        ArticleBlogDetailsController _controller = new ArticleBlogDetailsController(this, this);
//        _controller.getData(AppConstants.GET_MORE_COMMENTS, getCommentsRequestModel);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("http://webserve.mycity4kids.com/")) {
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
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("http://webserve.mycity4kids.com/")) {
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
        if (null != bookmarkFlag) {
            if ("0".equals(bookmarkFlag)) {
                menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp);
                bookmarkStatus = 0;
            } else {
                menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp_fill);
                bookmarkStatus = 1;
            }
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
                Utils.pushEvent(ArticlesAndBlogsDetailsActivity.this, GTMEventType.SHARE_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Article Details");

                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareUrl = "";
                if (StringUtils.isNullOrEmpty(detailData.getUrl())) {
                    shareUrl = "";
                } else {
                    shareUrl = detailData.getUrl();
                }

                String author = ((TextView) findViewById(R.id.user_name)).getText().toString();
                String shareMessage;
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    shareMessage = "mycity4kids\n\nCheck out this interesting blog post " + "\"" + detailData.getTitle() + "\" by " + author + ".";
                } else {
                    shareMessage = "mycity4kids\n\nCheck out this interesting blog post " + "\"" + detailData.getTitle() + "\" by " + author + ".\nRead Here: " + shareUrl;
                }

                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "mycity4kids"));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void updateUi(Response response) {
        try {
            if (response == null) {
                removeProgressDialog();
                showToast("Something went wrong from server");
                return;
            }
            String commentMessage = "";
            switch (response.getDataType()) {
                case AppConstants.GET_MORE_COMMENTS:
                    mLodingView.setVisibility(View.GONE);
                    isLoading = false;
                    String resData = (String) response.getResponseObject();
                    JSONObject jsonObject = new JSONObject(resData);
                    JSONArray commentsJson = new JSONArray();
                    int commCount = 0;
                    if (!StringUtils.isNullOrEmpty(commentTypeToFetch) && AppConstants.COMMENT_TYPE_DB.equals(commentTypeToFetch)) {
                        commCount = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("db").getInt("count");
                        commentsJson = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("db").getJSONArray("comments");
                        if (commCount == 0) {
                            commentTypeToFetch = AppConstants.COMMENT_TYPE_FB_PLUGIN;
                            getMoreComments();

                        }
                        offset = offset + 1;
                        if (offset * AppConstants.COMMENT_LIMIT >= commCount) {
                            commentTypeToFetch = AppConstants.COMMENT_TYPE_FB_PLUGIN;
                            offset = 0;
                        }
                    } else if (!StringUtils.isNullOrEmpty(commentTypeToFetch) && AppConstants.COMMENT_TYPE_FB_PLUGIN.equals(commentTypeToFetch)) {
                        commCount = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("fb").getInt("count");
                        commentsJson = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("fb").getJSONArray("comments");
                        offset = offset + 1;
                        if (offset * AppConstants.COMMENT_LIMIT >= commCount) {
                            commentTypeToFetch = AppConstants.COMMENT_TYPE_FB_PAGE;
                            offset = 0;
                        }
                    } else if (!StringUtils.isNullOrEmpty(commentTypeToFetch) && AppConstants.COMMENT_TYPE_FB_PAGE.equals(commentTypeToFetch)) {
                        commCount = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("fan").getInt("count");
                        commentsJson = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("fan").getJSONArray("comments");
                        offset = offset + 1;
                        if (offset * AppConstants.COMMENT_LIMIT >= commCount) {
                            commentTypeToFetch = "";
                            offset = 0;
                        }
                    }

                    LinearLayout commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));
                    ViewHolder viewHolder = null;
                    viewHolder = new ViewHolder();
                    for (int i = 0; i < commentsJson.length(); i++) {
                        CommentsData cData = new Gson().fromJson(commentsJson.get(i).toString(), CommentsData.class);
                        displayComments(viewHolder, cData, commentLayout);
                    }
                    break;
                case AppConstants.ARTICLES_DETAILS_REQUEST:
                case AppConstants.BLOGS_DETAILS_REQUEST:
                    ArticleDetailResponse responseData = (ArticleDetailResponse) response.getResponseObject();
                    if (responseData.getCode() == 200) {

                        newCommentLayout.setVisibility(View.VISIBLE);

                        getResponseUpdateUi(responseData);
                        if (isCommingFromCommentAPI) {
                            if (StringUtils.isNullOrEmpty(commentMessage)) {
                                showToast("Your comment has been added!");
                                if (null != commentText) {
                                    commentText.setText("");
                                }
                            } else {
                                Toast.makeText(ArticlesAndBlogsDetailsActivity.this, commentMessage, Toast.LENGTH_SHORT).show();
                            }
                            sendScrollDown();
                            isCommingFromCommentAPI = false;
                        }

                        removeProgressDialog();
                    } else if (responseData.getCode() == 400) {
                        isCommingFromCommentAPI = false;
                        removeProgressDialog();
                        finish();
                        String message = responseData.getReason();
                        if (!StringUtils.isNullOrEmpty(message)) {
                            showToast(message);
                        } else {
                            showToast(getString(R.string.went_wrong));
                        }
                    }
                    break;
                case AppConstants.COMMENT_REPLY_REQUEST:
                    CommonResponse commonData = (CommonResponse) response.getResponseObject();
                    String messageComment = commonData.getResult().getMessage();
                    commentMessage = messageComment;
                    if (commonData.getResponseCode() == 200) {
                        isCommingFromCommentAPI = true;

                        //showProgressDialog(getString(R.string.fetching_data));
                        ArticleBlogDetailsController _controller = new ArticleBlogDetailsController(this, this);
                        _controller.getData(AppConstants.ARTICLES_DETAILS_REQUEST, articleId);

                    } else if (commonData.getResponseCode() == 400) {
                        isCommingFromCommentAPI = false;
                        removeProgressDialog();
                        String message = commonData.getResult().getMessage();
                        if (!StringUtils.isNullOrEmpty(message)) {
                            showToast(message);
                        } else {
                            showToast(getString(R.string.went_wrong));
                        }
                    }
//                    removeProgressDialog();
                    break;

                case AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST:
                    CommonResponse followData = (CommonResponse) response.getResponseObject();
                    removeProgressDialog();
                    if (followData.getResponseCode() == 200) {
                        ToastUtils.showToast(getApplicationContext(), followData.getResult().getMessage(), Toast.LENGTH_SHORT);
                        if (isFollowing) {
                            isFollowing = false;
                            ((TextView) findViewById(R.id.follow_click)).setText("FOLLOW");
                        } else {
                            isFollowing = true;
                            ((TextView) findViewById(R.id.follow_click)).setText("UNFOLLOW");
                        }
                        if (BuildConfig.DEBUG) {
                            Log.e("follow response", followData.getResult().getMessage());
                        }
                    } else if (followData.getResponseCode() == 400) {
                        String message = followData.getResult().getMessage();
                        if (BuildConfig.DEBUG) {
                            Log.e("follow response", followData.getResult().getMessage());
                        }
                        if (!StringUtils.isNullOrEmpty(message)) {
                            showToast(message);
                        } else {
                            showToast(getString(R.string.went_wrong));
                        }
                    }
                    break;
                case AppConstants.BOOKMARK_BLOG_REQUEST:
                    CommonResponse bookmarkResponse = (CommonResponse) response.getResponseObject();
                    if (bookmarkResponse.getResponseCode() == 200) {
                        ToastUtils.showToast(getApplicationContext(), bookmarkResponse.getResult().getMessage(), Toast.LENGTH_SHORT);
                        if (BuildConfig.DEBUG) {
                            Log.e("Bookmark response", bookmarkResponse.getResult().getMessage());
                        }
                    } else if (bookmarkResponse.getResponseCode() == 400) {
                        String message = bookmarkResponse.getResult().getMessage();
                        if (BuildConfig.DEBUG) {
                            Log.e("Bookmark response", bookmarkResponse.getResult().getMessage());
                        }
                        if (!StringUtils.isNullOrEmpty(message)) {
                            showToast(message);
                        } else {
                            showToast(getString(R.string.went_wrong));
                        }

                        if (bookmarkStatus == 0) {
                            bookmarkStatus = 1;
                            menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp_fill);
                        } else {
                            bookmarkStatus = 0;
                            menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp);
                        }
                    }
                    break;
                default:
            }
        } catch (Exception e) {
            removeProgressDialog();
            Log.i("details", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {

            if (resultCode != RESULT_OK) {
                return;
            }

            if (requestCode == ADD_COMMENT_OR_REPLY) {
                if (resultCode == RESULT_OK) {

                    mScrollView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            //	mScrollView.scrollTo(scrollX, scrollY+100);
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

                        }
                    }, 100);
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        String contentData = bundle.getString(Constants.ARTICLE_BLOG_CONTENT);
                        String parentId = bundle.getString(Constants.PARENT_ID);
                        UserTable _table = new UserTable((BaseApplication) getApplication());
                        int count = _table.getCount();

                        if (count > 0) {
                            UserModel userData = _table.getAllUserData();
                            CommentRequest _commentRequest = new CommentRequest();
                            _commentRequest.setArticleId(articleId);
                            /**
                             * in case of comment parentId will be empty.
                             *
                             */
                            _commentRequest.setParentId(parentId);
                            _commentRequest.setContent(contentData);
                            _commentRequest.setUserId("" + userData.getUser().getId());
                            _commentRequest.setSessionId(userData.getUser().getSessionId());
                            CommentController _controller = new CommentController(this, this);
                            showProgressDialog("Adding a comment...");
                            _controller.getData(AppConstants.COMMENT_REPLY_REQUEST, _commentRequest);
                        }
                    }
                }
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
//                    ((ImageView) findViewById(R.id.follow_article)).setBackgroundResource(R.drawable.follow_blog);
                    ((TextView) findViewById(R.id.follow_click)).setText("FOLLOW");
                    isFollowing = false;

                } else {
//                    ((ImageView) findViewById(R.id.follow_article)).setBackgroundResource(R.drawable.un_follow_icon);
                    ((TextView) findViewById(R.id.follow_click)).setText("UNFOLLOW");
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

    private void getResponseUpdateUi(ArticleDetailResponse detailsResponse) {
        detailData = detailsResponse.getData();
        imageList = detailData.getBody().getImage();
//        blogName = detailData.getBlog_title();
//        authorType = detailData.getAuthor_type();
//        author = detailData.getAuthor_name();
//        if (StringUtils.isNullOrEmpty(blogName)) {
//            blogName = "mycity4kids team";
//        }
//        if ("0".equals(detailData.getBookmarkStatus())) {
//            if (null != menu) {
//                menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp);
//            }
//            bookmarkStatus = 0;
//        } else {
//            if (null != menu) {
//                menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp_fill);
//            }
//            bookmarkStatus = 1;
//        }
//        if (!StringUtils.isNullOrEmpty(detailsResponse.getResult().getData().getUser_following_status())) {
//            if (detailsResponse.getResult().getData().getUser_following_status().equalsIgnoreCase("0")) {
////                ((ImageView) findViewById(R.id.follow_article)).setBackgroundResource(R.drawable.follow_blog);
//                ((TextView) findViewById(R.id.follow_click)).setText("FOLLOW");
//                isFollowing = false;
//            } else {
////                ((ImageView) findViewById(R.id.follow_article)).setBackgroundResource(R.drawable.un_follow_icon);
//                ((TextView) findViewById(R.id.follow_click)).setText("UNFOLLOW");
//                isFollowing = true;
//            }
//        }

        if (!StringUtils.isNullOrEmpty(detailData.getTitle())) {
            article_title.setText(detailData.getTitle());
            collapsingToolbarLayout.setTitle(detailData.getTitle());
        }

        try {
            if (!StringUtils.isNullOrEmpty(detailData.getUserType())) {

                if (AppConstants.USER_TYPE_BLOGGER.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_BLOGGER);
                    author_type.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_blogger));
                } else if (AppConstants.USER_TYPE_EXPERT.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_EXPERT);
                    author_type.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_expert));
                } else if (AppConstants.USER_TYPE_EDITOR.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_EDITOR);
                    author_type.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_editor));
                } else if (AppConstants.USER_TYPE_EDITORIAL.equals(detailData.getUserType())) {
                    author_type.setText(AppConstants.AUTHOR_TYPE_EDITORIAL);
                    author_type.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_editorial));
                }
            } else {
                // Default Author type set to Blogger
                author_type.setText("Blogger");
                author_type.setTextColor(ContextCompat.getColor(this, R.color.authortype_colorcode_blogger));
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }


        if (!StringUtils.isNullOrEmpty(detailData.getUserName())) {
            ((TextView) findViewById(R.id.user_name)).setText(detailData.getUserName());
        }

        if (!StringUtils.isNullOrEmpty(detailData.getCreated())) {
            ((TextView) findViewById(R.id.article_date)).setText(detailData.getCreated());
        }

        String bodyDescription = detailData.getBody().getText();
        String bodyDesc = bodyDescription;
        if (imageList.size() > 0) {
            for (ImageData images : imageList) {
                if (bodyDescription.contains(images.getKey())) {
                    bodyDesc = bodyDesc.replace(images.getKey(), "<p style='text-align:center'><img src=" + images.getValue() + " style=\"width: 100%;\"+></p>");
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
                    "    font-size: 16px;\n" +
                    "    text-align: left;\n" +
                    "}\n" +
                    "</style>" +
                    "</head><body>" + bodyDesc + "</body></html>";
            WebView view = (WebView) findViewById(R.id.articleWebView);
            view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            view.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");
            view.getSettings().setJavaScriptEnabled(true);
        } else {
            bodyDesc = bodyDesc.replaceAll("\n", "<br/>");
            String bodyImgTxt = "<html><head>" +
                    "" +
                    "<style type=\"text/css\">\n" +
                    "@font-face {\n" +
                    "    font-family: MyFont;\n" +
                    "    src: url(\"file:///android_asset/fonts/georgia.ttf\")\n" +
                    "}\n" +
                    "body {\n" +
                    "    font-family: MyFont;\n" +
                    "    font-size: 16px;\n" +
                    "    text-align: left;\n" +
                    "}\n" +
                    "</style>" +
                    "</head><body>" + bodyDesc + "</body></html>";
            WebView view = (WebView) findViewById(R.id.articleWebView);
            view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            view.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");
            view.getSettings().setJavaScriptEnabled(true);
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
        if (!StringUtils.isNullOrEmpty(detailData.getAuthor_image())) {
            Picasso.with(this).load(detailData.getAuthor_image()).transform(new CircleTransformation()).into(target);
        }

        if (!StringUtils.isNullOrEmpty(detailData.getImageUrl())) {
//            int width = getResources().getDisplayMetrics().widthPixels;
            Picasso.with(this).load(detailData.getImageUrl()).placeholder(R.drawable.blog_bgnew).fit().into(cover_image);
        }

        followAuthorId = detailData.getUserId();

    }


    private void displayComments(ViewHolder holder, CommentsData commentList, LinearLayout commentLayout) {
        if (holder != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.custom_comment_cell, null);
            holder.networkImg = (CircularImageView) view.findViewById(R.id.network_img);
            holder.commentName = (TextView) view.findViewById(R.id.txvCommentTitle);
            holder.commentDescription = (TextView) view.findViewById(R.id.txvCommentDescription);
            holder.dateTxt = (TextView) view.findViewById(R.id.txvDate);
            holder.replyTxt = (TextView) view.findViewById(R.id.txvReply);
            holder.replierImageView = (CircularImageView) view.findViewById(R.id.replyUserImageView);
            holder.replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);
            holder.replierUsernameTextView = (TextView) view.findViewById(R.id.replyUserNameTextView);

            holder.replyCommentView = (RelativeLayout) view.findViewById(R.id.replyRelativeLayout);
            holder.replyCommentView.setOnClickListener(this);
            holder.replyCommentView.setTag(commentList);
            holder.replyTxt.setOnClickListener(this);
            holder.replyTxt.setTag(commentList);

            if (!StringUtils.isNullOrEmpty(commentList.getComment_type()) &&
                    (commentList.getComment_type().equals("fan") || commentList.getComment_type().equals("fb"))) {
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
            if (!StringUtils.isNullOrEmpty(DateTimeUtils.getSeperateDate(commentList.getCreate()))) {
                holder.dateTxt.setText(DateTimeUtils.getSeperateDate(commentList.getCreate()));
            } else
                holder.dateTxt.setText(commentList.getCreate());

            if (!StringUtils.isNullOrEmpty(commentList.getProfile_image())) {
                try {
//                    holder.networkImg.setImageDrawable(new BitmapDrawable(getResources(),defaultCommentorBitmap));
                    Picasso.with(this).load(commentList.getProfile_image()).into(holder.networkImg);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    Picasso.with(this).load(R.drawable.default_commentor_img).into(holder.networkImg);
                }
            } else {
                Picasso.with(this).load(R.drawable.default_commentor_img).into(holder.networkImg);
            }

            commentLayout.addView(view);

            if (commentList.getReplies() != null && commentList.getReplies().size() > 0) {

                holder.replyCommentView.setVisibility(View.VISIBLE);
                if (commentList.getReplies().size() > 1) {
                    holder.replyCountTextView.setText(commentList.getReplies().size() + " replies");
                } else {
                    holder.replyCountTextView.setText(commentList.getReplies().size() + " reply");
                }
                holder.replierUsernameTextView.setText("" + commentList.getReplies().get(0).getName());

                if (!StringUtils.isNullOrEmpty(commentList.getProfile_image())) {
                    try {
                        Picasso.with(this).load(commentList.getReplies().get(0).getProfile_image())
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
                        CommentRepliesDialogFragment commentFragment = new CommentRepliesDialogFragment();
                        Bundle _args = new Bundle();
                        _args.putParcelable("commentData", (CommentsData) v.getTag());
                        _args.putString("articleId", articleId);
                        commentFragment.setArguments(_args);
                        FragmentManager fm = getSupportFragmentManager();
                        commentFragment.show(fm, "Replies");
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
//                    onShowPopup(coordinatorLayout);
                    break;

                case R.id.share_spouse:
                    Utils.pushEvent(ArticlesAndBlogsDetailsActivity.this, GTMEventType.SHARE_SPOUCE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Article Details");

                    ArrayList<Integer> idlist = new ArrayList<>();
                    idlist = new ArrayList<>();
                    WhoToRemindDialogFragment dialogFragment1 = new WhoToRemindDialogFragment();

                    Bundle args = new Bundle();
                    args.putString("dialogTitle", "Share with");
                    args.putIntegerArrayList("chkValues", idlist);
                    dialogFragment1.setArguments(args);


                    dialogFragment1.setTargetFragment(dialogFragment1, 2);
                    dialogFragment1.show(getFragmentManager(), "whotoremind");

                    break;

                case R.id.add_comment_btn:

                    if (commentText.getText().toString().trim().equalsIgnoreCase("")) {
                        ToastUtils.showToast(getApplicationContext(), "Please write to comment...");
                    } else {
                        String contentData = commentText.getText().toString();
                        String parentId1 = "";

                        Retrofit retro = BaseApplication.getInstance().getRetrofit();
                        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);

                        Call<AddCommentResponse> callBookmark = articleDetailsAPI.addComment(articleId);
                        callBookmark.enqueue(addCommentsResponseCallback);
//                        UserTable _table1 = new UserTable((BaseApplication) getApplication());
//                        int count1 = _table1.getCount();
//
//                        if (count1 > 0) {
//                            UserModel userData = _table1.getAllUserData();
//                            CommentRequest _commentRequest = new CommentRequest();
//                            _commentRequest.setArticleId(articleId);
//                            /**
//                             * in case of comment parentId will be empty.
//                             *
//                             */
//                            _commentRequest.setParentId(parentId1);
//                            _commentRequest.setContent(contentData);
//                            _commentRequest.setUserId("" + userData.getUser().getId());
//                            _commentRequest.setSessionId(userData.getUser().getSessionId());
//                            CommentController _controller = new CommentController(this, this);
//                            showProgressDialog("Adding a comment...");
//                            _controller.getData(AppConstants.COMMENT_REPLY_REQUEST, _commentRequest);
//                        }

                    }
                    break;

                case R.id.follow_article:
//                    followAPICall(articleId);

                    break;

                case R.id.follow_click:
                    followAPICall(followAuthorId);
                    break;

                case R.id.user_image:

                    Intent intentn = new Intent(this, BlogDetailActivity.class);
                    intentn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentn.putExtra(Constants.IS_COMMING_FROM_LISTING, false);
                    intentn.putExtra(Constants.AUTHOR_ID, detailData.getUserId());
                    if (!StringUtils.isNullOrEmpty(authorType)) {
                        if (authorType.trim().equalsIgnoreCase("Blogger")) {
                            intentn.putExtra(Constants.ARTICLE_NAME, blogName);
                            intentn.putExtra(Constants.FILTER_TYPE, "blogs");
                        } else {
                            intentn.putExtra(Constants.ARTICLE_NAME, detailData.getUserName());
                            intentn.putExtra(Constants.FILTER_TYPE, "authors");
                        }
                    }
                    startActivityForResult(intentn, Constants.BLOG_FOLLOW_STATUS);
                    break;

                case R.id.user_name:
                    Intent intentnn = new Intent(this, BlogDetailActivity.class);
                    intentnn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentnn.putExtra(Constants.IS_COMMING_FROM_LISTING, false);
                    intentnn.putExtra(Constants.AUTHOR_ID, detailData.getUserId());
                    if (!StringUtils.isNullOrEmpty(authorType)) {
                        if (authorType.trim().equalsIgnoreCase("Blogger")) {
                            intentnn.putExtra(Constants.ARTICLE_NAME, blogName);
                            intentnn.putExtra(Constants.FILTER_TYPE, "blogs");
                        } else {
                            intentnn.putExtra(Constants.ARTICLE_NAME, detailData.getUserName());
                            intentnn.putExtra(Constants.FILTER_TYPE, "authors");
                        }
                    }
                    startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                    break;
                case R.id.replyRelativeLayout:
                    try {
                        CommentRepliesDialogFragment commentFragment = new CommentRepliesDialogFragment();
                        Bundle commentArgs = new Bundle();
                        commentArgs.putParcelable("commentData", (CommentsData) v.getTag());
                        commentArgs.putString("articleId", articleId);
                        commentFragment.setArguments(commentArgs);
                        FragmentManager fm = getSupportFragmentManager();
                        commentFragment.show(fm, "Replies");
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
//                    onShowPopup(coordinatorLayout);
                    break;
                case R.id.recentAuthorArticle1:
                case R.id.recentAuthorArticle2:
                case R.id.recentAuthorArticle3: {
                    Intent intent = new Intent(this, ArticlesAndBlogsDetailsActivity.class);

                    BlogArticleModel parentingListData = (BlogArticleModel) v.getTag();
                    // int id=(int)  v.getTag();
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId() + "");
                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getThumbnail_image());
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    startActivity(intent);
                    finish();
                    break;
                }
                case R.id.trendingArticle1:
                case R.id.trendingArticle2:
                case R.id.trendingArticle3: {
                    Intent intent = new Intent(this, ArticlesAndBlogsDetailsActivity.class);

                    CommonParentingList parentingListData = (CommonParentingList) v.getTag();
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getThumbnail_image());
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    intent.putExtra(Constants.FILTER_TYPE, parentingListData.getAuthor_type());
                    intent.putExtra(Constants.BLOG_NAME, parentingListData.getBlog_name());
                    startActivity(intent);
                    finish();
                    break;
                }

            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }


    private class ViewHolder {
        private CircularImageView networkImg;
        private ImageView localImg;
        private TextView commentName;
        private TextView commentDescription;
        private TextView dateTxt;
        //        private RelativeLayout mRelativeContainer;
        private TextView replyTxt;
        private CircularImageView replierImageView;
        private TextView replierUsernameTextView;
        private TextView replyCountTextView;
        //        private View dividerLine;
//        private RelativeLayout innerCommentView;
        private RelativeLayout replyCommentView;
    }


    public void setShareWith(ArrayList<AttendeeModel> attendeeList) {

        whoToShareList = new ArrayList<>();
        ArrayList<String> userList = new ArrayList<>();
        BlogShareSpouseModel spouseModel = new BlogShareSpouseModel();
        for (int i = 0; i < attendeeList.size(); i++) {
            if (attendeeList.get(i).getCheck() == true) {
                if (attendeeList.get(i).getType().equalsIgnoreCase("user")) {
                    userList.add(String.valueOf(attendeeList.get(i).getId()));
                    Log.d("Attendees are", String.valueOf(attendeeList.get(i).getId()));
                }
            }
        }
        //whoToShareList.add(spouseModel);

        spouseModel.setSharedWithUserList(userList);
        spouseModel.setArticleId(articleId);
        BlogShareSpouseController blogShareSpouseContoller = new BlogShareSpouseController(this, this);
        blogShareSpouseContoller.getData(AppConstants.SHARE_SPOUSE_BLOG, spouseModel);
    }

    public void followAPICall(String id) {

        ArticleBlogFollowRequest _followRequest = new ArticleBlogFollowRequest();
        _followRequest.setSessionId("" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getSessionId());
        _followRequest.setUserId("" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId());
        _followRequest.setAuthorId("" + id);
        ArticleBlogFollowController _followController = new ArticleBlogFollowController(this, this);
//        showProgressDialog(getString(R.string.please_wait));
        _followController.getData(AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST, _followRequest);

    }

    private void addRemoveBookmark() {
        BookmarkModel bookmarkRequest = new BookmarkModel();
        bookmarkRequest.setId(articleId);
        bookmarkRequest.setCategory("blogs");
        if (bookmarkStatus == 0) {
            bookmarkStatus = 1;
            menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp_fill);
            bookmarkRequest.setAction("add");
        } else {
            bookmarkStatus = 0;
            menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp);
            bookmarkRequest.setAction("remove");
        }
//        followAPICall(followAuthorId);
        BookmarkController bookmarkController = new BookmarkController(this, this);
        bookmarkController.getData(AppConstants.BOOKMARK_BLOG_REQUEST, bookmarkRequest);
    }

    private void sendScrollDown() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }

    public void sendScrollUp() {
        LinearLayout commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));
        commentLayout.removeAllViews();
        commentTypeToFetch = AppConstants.COMMENT_TYPE_DB;
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.fullScroll(View.FOCUS_UP);
                    }
                });
            }
        }).start();
    }

    Callback<ArticleDetailData> articleDetailResponseCallback = new Callback<ArticleDetailData>() {
        @Override
        public void onResponse(Call<ArticleDetailData> call, retrofit2.Response<ArticleDetailData> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }
            String commentMessage = "";
            ArticleDetailData responseData = (ArticleDetailData) response.body();

            if (responseData != null) {
                newCommentLayout.setVisibility(View.VISIBLE);
//                getResponseUpdateUi(responseData);

                commentURL = responseData.getCommentsUri();
                getMoreComments();
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                AuthorDetailsAPI authorDetailsAPI = retrofit.create(AuthorDetailsAPI.class);
                Call<NewArticleListingResponse> call1 = authorDetailsAPI.getBloggersRecentArticle(followAuthorId, 1);
                call1.enqueue(bloggersArticleResponseCallback);
                hitBlogListingApi();
                if (isCommingFromCommentAPI) {
                    if (StringUtils.isNullOrEmpty(commentMessage)) {
                        showToast("Your comment has been added!");
                    } else {
                        Toast.makeText(ArticlesAndBlogsDetailsActivity.this, commentMessage, Toast.LENGTH_SHORT).show();
                    }
                    sendScrollDown();
                    isCommingFromCommentAPI = false;
                }
            } else {
                isCommingFromCommentAPI = false;
                finish();
                String message = "dddd";
                if (!StringUtils.isNullOrEmpty(message)) {
                    showToast(message);
                } else {
                    showToast(getString(R.string.went_wrong));
                }
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailData> call, Throwable t) {
            removeProgressDialog();
            if (t instanceof UnknownHostException) {
                showToast(getString(R.string.error_network));
            } else if (t instanceof SocketTimeoutException) {
                showToast("connection timed out");
            } else {
                showToast(getString(R.string.server_went_wrong));
            }
        }
    };

    public void hitBlogListingApi() {
//

        // blogProgessBar.setVisibility(View.VISIBLE);
        String url;
        StringBuilder builder = new StringBuilder();
        builder.append("city_id=").append(SharedPrefUtils.getCurrentCityModel(this).getId());
        builder.append("&page=").append(1);
        builder.append("&sort=").append("trending_today");
        url = AppConstants.NEW_ALL_ARTICLE_URL + builder.toString().replace(" ", "%20");
        HttpVolleyRequest.getStringResponse(this, url, null, mGetArticleListingListener, Request.Method.GET, true);

    }

    private OnWebServiceCompleteListener mGetArticleListingListener = new OnWebServiceCompleteListener() {
        @Override
        public void onWebServiceComplete(VolleyBaseResponse response, boolean isError) {
            //  progressBar.setVisibility(View.GONE);
            Log.d("Response back =", " " + response.getResponseBody());
            if (isError) {
                if (null != this && response.getResponseCode() != 999)
                    showToast("Something went wrong from server");
            } else {
                Log.d("Response = ", response.getResponseBody());
                String temp = "";
//                progressBar.setVisibility(View.INVISIBLE);
                if (response == null) {
                    showToast("Something went wrong from server");
                    removeProgressDialog();
                    return;
                }

                //   blogProgessBar.setVisibility(View.GONE);
                CommonParentingResponse responseBlogData;
                try {
                    responseBlogData = new Gson().fromJson(response.getResponseBody(), CommonParentingResponse.class);
                } catch (JsonSyntaxException jse) {
                    Crashlytics.logException(jse);
                    Log.d("JsonSyntaxException", Log.getStackTraceString(jse));
                    showToast("Something went wrong from server");
                    removeProgressDialog();
                    return;
                }

                if (responseBlogData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {
                    //clear list to avoid duplicates due to volley caching

                    //  articlesListingAdapter.setNewListData(mArticleDataListing);
                    // articlesListingAdapter.notifyDataSetChanged();
                    trendingArticles.setVisibility(View.VISIBLE);
                    trendingArticle1.setText(responseBlogData.getResult().getData().getData().get(0).getTitle());
                    trendingArticle1.setTag(responseBlogData.getResult().getData().getData().get(0));
                    trendingArticle2.setText(responseBlogData.getResult().getData().getData().get(1).getTitle());
                    trendingArticle2.setTag(responseBlogData.getResult().getData().getData().get(1));
                    trendingArticle3.setText(responseBlogData.getResult().getData().getData().get(2).getTitle());
                    trendingArticle3.setTag(responseBlogData.getResult().getData().getData().get(2));

                }

            }
        }
    };
    private Callback<NewArticleListingResponse> bloggersArticleResponseCallback = new Callback<NewArticleListingResponse>() {
        @Override
        public void onResponse(Call<NewArticleListingResponse> call, retrofit2.Response<NewArticleListingResponse> response) {

            //  progressBar.setVisibility(View.INVISIBLE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }

            try {
                NewArticleListingResponse responseData = (NewArticleListingResponse) response.body();
                if (responseData.getResponseCode() == 200) {
                    ArrayList<BlogArticleModel> dataList = responseData.getResult().getData().getData();
                    if (dataList.size() == 0) {

                    } else {
                        recentAuthorArticleHeading.setText("RECENT BLOGS FROM " + author);
                        // No results for search
                        if (dataList.size() >= 3) {
                            recentAuthorArticles.setVisibility(View.VISIBLE);
                            recentAuthorArticle1.setText(dataList.get(0).getTitle());
                            recentAuthorArticle1.setTag(dataList.get(0));
                            recentAuthorArticle2.setText(dataList.get(1).getTitle());
                            recentAuthorArticle2.setTag(dataList.get(1));
                            recentAuthorArticle3.setText(dataList.get(2).getTitle());
                            recentAuthorArticle3.setTag(dataList.get(2));
                        } else if (dataList.size() == 2) {
                            recentAuthorArticles.setVisibility(View.VISIBLE);
                            recentAuthorArticle1.setText(dataList.get(0).getTitle());
                            recentAuthorArticle1.setTag(dataList.get(0));
                            recentAuthorArticle2.setText(dataList.get(1).getTitle());
                            recentAuthorArticle2.setTag(dataList.get(1));
                            recentAuthorArticle3.setVisibility(View.GONE);
                        } else if (dataList.size() == 1) {
                            recentAuthorArticles.setVisibility(View.VISIBLE);
                            recentAuthorArticle1.setText(dataList.get(0).getTitle());
                            recentAuthorArticle1.setTag(dataList.get(0));
                            recentAuthorArticle2.setVisibility(View.GONE);
                            recentAuthorArticle3.setVisibility(View.GONE);
                        }
                    }

                } else if (responseData.getResponseCode() == 400) {
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        showToast(message);
                    } else {
                        showToast(getString(R.string.went_wrong));
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<NewArticleListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };


    Callback<ResponseBody> articleCommentsResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }
            try {
                mLodingView.setVisibility(View.GONE);
                isLoading = false;
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);
                JSONArray commentsJson = new JSONArray();
                int commCount = 0;
                if (!StringUtils.isNullOrEmpty(commentTypeToFetch) && AppConstants.COMMENT_TYPE_DB.equals(commentTypeToFetch)) {
                    commCount = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("db").getInt("count");
                    commentsJson = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("db").getJSONArray("comments");
                    if (commCount == 0) {
                        commentTypeToFetch = AppConstants.COMMENT_TYPE_FB_PLUGIN;
                        getMoreComments();

                    }
                    offset = offset + 1;
                    if (offset * AppConstants.COMMENT_LIMIT >= commCount) {
                        commentTypeToFetch = AppConstants.COMMENT_TYPE_FB_PLUGIN;
                        offset = 0;
                    }
                } else if (!StringUtils.isNullOrEmpty(commentTypeToFetch) && AppConstants.COMMENT_TYPE_FB_PLUGIN.equals(commentTypeToFetch)) {
                    commCount = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("fb").getInt("count");
                    commentsJson = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("fb").getJSONArray("comments");
                    offset = offset + 1;
                    if (offset * AppConstants.COMMENT_LIMIT >= commCount) {
                        commentTypeToFetch = AppConstants.COMMENT_TYPE_FB_PAGE;
                        offset = 0;
                    }
                } else if (!StringUtils.isNullOrEmpty(commentTypeToFetch) && AppConstants.COMMENT_TYPE_FB_PAGE.equals(commentTypeToFetch)) {
                    commCount = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("fan").getInt("count");
                    commentsJson = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("fan").getJSONArray("comments");
                    offset = offset + 1;
                    if (offset * AppConstants.COMMENT_LIMIT >= commCount) {
                        commentTypeToFetch = "";
                        offset = 0;
                    }
                }

                LinearLayout commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));
                ViewHolder viewHolder = null;
                viewHolder = new ViewHolder();
                for (int i = 0; i < commentsJson.length(); i++) {
                    CommentsData cData = new Gson().fromJson(commentsJson.get(i).toString(), CommentsData.class);
                    displayComments(viewHolder, cData, commentLayout);
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
            showToast(getString(R.string.went_wrong));
        }
    };


    Callback<ResponseBody> commentsCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }
            try {
                mLodingView.setVisibility(View.GONE);
                isLoading = false;
                String resData = new String(response.body().bytes());
//                JSONObject jsonObject = new JSONObject(resData);
                ArrayList<CommentsData> arrayList = new ArrayList<>();
                JSONArray commentsJson = new JSONArray(resData);
                commentURL = "";
                for (int i = 0; i < commentsJson.length(); i++) {
                    if (commentsJson.getJSONObject(i).has("next")) {
                        commentURL = commentsJson.getJSONObject(i).getString("next");
                    } else {
                        CommentsData cData = new Gson().fromJson(commentsJson.get(i).toString(), CommentsData.class);
//                        CommentsData commentsData = new CommentsData();
//                        commentsData.setId(commentsJson.getJSONObject(i).getString("id"));
//                        commentsData.setName(commentsJson.getJSONObject(i).getString("userName"));
//                        commentsData.setParent_id(commentsJson.getJSONObject(i).getString("parentId"));
//                        commentsData.setBody(commentsJson.getJSONObject(i).getString("userComment"));
//                        commentsData.setCreate(commentsJson.getJSONObject(i).getString("date"));
//                        commentsData.setProfile_image(commentsJson.getJSONObject(i).getString("userImage"));

                        arrayList.add(cData);
                    }

                }

                LinearLayout commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));
                ViewHolder viewHolder = null;
                viewHolder = new ViewHolder();
                for (int i = 0; i < arrayList.size(); i++) {
//                    CommentsData cData = new Gson().fromJson(commentsJson.get(i).toString(), CommentsData.class);
                    displayComments(viewHolder, arrayList.get(i), commentLayout);
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
            showToast(getString(R.string.went_wrong));
        }
    };

    private Callback<ArticleDetailData> isBookmarkedResponseCallback = new Callback<ArticleDetailData>() {
        @Override
        public void onResponse(Call<ArticleDetailData> call, retrofit2.Response<ArticleDetailData> response) {
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }

            ArticleDetailData responseData = (ArticleDetailData) response.body();
            bookmarkFlag = responseData.getBookmarkStatus();
            if ("0".equals(bookmarkFlag)) {
                menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp);
                bookmarkStatus = 0;
            } else {
                menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_48dp_fill);
                bookmarkStatus = 1;
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailData> call, Throwable t) {

        }
    };

    private Callback<AddCommentResponse> addCommentsResponseCallback = new Callback<AddCommentResponse>() {
        @Override
        public void onResponse(Call<AddCommentResponse> call, retrofit2.Response<AddCommentResponse> response) {
            AddCommentResponse baseResponse = (AddCommentResponse) response.body();
            if (response.isSuccessful()) {
                showToast("Comment added successfully!");
            } else {
                showToast(baseResponse.getReason());
            }
        }

        @Override
        public void onFailure(Call<AddCommentResponse> call, Throwable t) {
            removeProgressDialog();
            if (t instanceof UnknownHostException) {
                showToast(getString(R.string.error_network));
            } else if (t instanceof SocketTimeoutException) {
                showToast("connection timed out");
            } else {
                showToast(getString(R.string.server_went_wrong));
            }
        }
    };
}

