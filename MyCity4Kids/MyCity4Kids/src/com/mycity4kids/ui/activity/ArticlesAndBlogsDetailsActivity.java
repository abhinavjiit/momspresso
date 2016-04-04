package com.mycity4kids.ui.activity;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.UiLifecycleHelper;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.kelltontech.utils.facebook.listener.FacebookLoginListener;
import com.kelltontech.utils.facebook.listener.FacebookPostListener;
import com.kelltontech.utils.facebook.model.FacebookUtils;
import com.kelltontech.utils.facebook.model.UserInfo;
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
import com.mycity4kids.fragmentdialog.LoginFragmentDialog;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.bookmark.BookmarkModel;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingdetails.CommentRequest;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.parentingdetails.ImageData;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.parentingdetails.ParentingDetailsData;
import com.mycity4kids.models.parentingstop.ArticleBlogFollowRequest;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.BlogShareSpouseModel;
import com.mycity4kids.newmodels.GetCommentsRequestModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.CircleTransformation;
import com.mycity4kids.ui.fragment.WhoToRemindDialogFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author deepanker.chaudhary
 */
public class ArticlesAndBlogsDetailsActivity extends BaseActivity implements
        OnClickListener, ImageGetter {
    //    private ImageLoader.ImageCache imageCache;
//    private ImageLoader imageLoader;
    ParentingDetailsData detailData;
    private UiLifecycleHelper mUiHelper;
    private int leftMargin = 0;
    private boolean isCommingFromCommentAPI;
    private int isComingAfterReply = -1;
    private int ADD_COMMENT_OR_REPLY = 0;
    private String articleId;
    private Spinner fontPicker;
    private int scrollX = 0;
    private int scrollY = -1;
    private ScrollView mScrollView;
    private Spanned spannedValue;
    private WebView webView;
    private ArrayList<ImageData> imageList;
    Boolean isFollowing = false;


    LinearLayout newCommentLayout;
    EditText commentText;
    ImageView commentBtn;
    CardView commentCountView;
    RelativeLayout followClick;
    RelativeLayout mainLayout;
    ImageView bookmarkImageView;

    Toolbar mToolbar;
    private float density;

    private Drawable mActionBarBackgroundDrawable;
    private int mLastDampedScroll;
    //    private int mInitialStatusBarColor;
//    private int mFinalStatusBarColor;
    private ImageView cover_image;
    private TextView article_title;
    private TextView titleMain, author_type;
    private String followAuthorId;
    float defaultTextSize = Float.valueOf(16);
    String authorType;
    private String blogName;
    private int bookmarkStatus;

    private String deepLinkURL;
    private GoogleApiClient mClient;
    private String TAG;
    private String screenTitle = "Parenting Blogs";
    Button share_spouse;
    public ArrayList<AppoitmentDataModel.Attendee> attendeeDataList;
    public ArrayList<BlogShareSpouseModel> whoToShareList;

    //Commments Lazy loading
//    private ProgressBar progressBar;
    private String commentTypeToFetch;
    Boolean isLoading = false;
    private int offset = 0;
    private int totalComments = 0;
    private int pageCount = 0;
    private RelativeLayout mLodingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(ArticlesAndBlogsDetailsActivity.this, "Blog Details", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        deepLinkURL = getIntent().getStringExtra(Constants.DEEPLINK_URL);
        TAG = ArticlesAndBlogsDetailsActivity.this.getClass().getSimpleName();
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.APP_INDEX_API).build();
        commentTypeToFetch = AppConstants.COMMENT_TYPE_DB;
        try {

            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(getIntent().getIntExtra(AppConstants.NOTIFICATION_ID, 0));

            setContentView(R.layout.aa_detail_article_new);
            ((TextView) findViewById(R.id.add_comment)).setOnClickListener(this);
            ((TextView) findViewById(R.id.user_name)).setOnClickListener(this);
            ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(this);
            ((ImageView) findViewById(R.id.img_follow)).setOnClickListener(this);
            ((ImageView) findViewById(R.id.img_share)).setOnClickListener(this);
            ((ImageView) findViewById(R.id.img_fb)).setOnClickListener(this);
            ((ImageView) findViewById(R.id.img_twitter)).setOnClickListener(this);
            ((ImageView) findViewById(R.id.user_image)).setOnClickListener(this);
            bookmarkImageView = (ImageView) findViewById(R.id.bookmarkBlogImageView);
            bookmarkImageView.setOnClickListener(this);
            share_spouse = (Button) findViewById(R.id.share_spouse);
            share_spouse.setOnClickListener(this);
            author_type = (TextView) findViewById(R.id.blogger_type);
            mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
            followClick = (RelativeLayout) findViewById(R.id.follow_click);
            followClick.setOnClickListener(this);
            article_title = (TextView) findViewById(R.id.article_title);
            titleMain = (TextView) findViewById(R.id.titleMain);
            titleMain.setTextColor(Color.argb(0, 255, 255, 255));
//            progressBar = (ProgressBar) findViewById(R.id.secondBar);
            String coverImageUrl = getIntent().getStringExtra(Constants.ARTICLE_COVER_IMAGE);

            cover_image = (ImageView) findViewById(R.id.cover_image);
            density = getResources().getDisplayMetrics().density;
            int width = getResources().getDisplayMetrics().widthPixels;
            if (!StringUtils.isNullOrEmpty(coverImageUrl)) {
                Picasso.with(this).load(coverImageUrl).placeholder(R.drawable.blog_bgnew).resize(width, (int) (220 * density)).centerCrop().into(cover_image);
            }

            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                // Set the status bar to dark-semi-transparentish
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                // Set paddingTop of toolbar to height of status bar.
                // Fixes statusbar covers toolbar issue
                mToolbar.setPadding(0, getStatusBarHeight(), 0, 0);
            }
            newCommentLayout = (LinearLayout) findViewById(R.id.comment_layout);
//            newCommentLayout.setVisibility(View.GONE);
            commentBtn = (ImageView) findViewById(R.id.add_comment_btn);
            commentBtn.setOnClickListener(this);
            commentText = (EditText) findViewById(R.id.editCommentTxt);
            commentCountView = (CardView) findViewById(R.id.comment_text_layout);
            commentCountView.setVisibility(View.GONE);

            mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
            findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

            mScrollView = (ScrollView) findViewById(R.id.scroll_view);
            TextView headerTxt = (TextView) findViewById(R.id.header_txt);
            mUiHelper = new UiLifecycleHelper(this, FacebookUtils.callback);
            mUiHelper.onCreate(savedInstanceState);

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                articleId = bundle.getString(Constants.ARTICLE_ID);
                ParentingFilterType parentingType = (ParentingFilterType) bundle.getSerializable(Constants.PARENTING_TYPE);
                switch (parentingType) {
                    case ARTICLES:
                        headerTxt.setText("Articles");
                        break;
                    case BLOGS:
                        headerTxt.setText("Blogs");
                        break;
                    case TOP_PICS:
                        headerTxt.setText("Top Picks");
                        break;
                }
                ArticleBlogDetailsController _controller = new ArticleBlogDetailsController(this, this);
                showProgressDialog(getString(R.string.fetching_data));
                _controller.getData(AppConstants.ARTICLES_DETAILS_REQUEST, articleId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActionBarBackgroundDrawable = findViewById(R.id.viewColor).getBackground();
        share_spouse.setVisibility(View.INVISIBLE);


        final int titleScrollHeight = (int) (50 * density);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {

                int scrollPosition = mScrollView.getScrollY(); //for verticalScrollView
//                Log.d("Scroll Position", String.valueOf(scrollPosition));
//                Log.d("Height Scroll", String.valueOf(mScrollView.getHeight()));
//                Log.d("Child Count", String.valueOf(mScrollView.getChildCount()));
//                Log.d("ScrollY", String.valueOf(mScrollView.getScrollY()));
//                int scrollBottom = mScrollView.getBottom();
                View view = (View) mScrollView.getChildAt(mScrollView.getChildCount() - 1);

                Rect scrollBounds = new Rect();
                mScrollView.getHitRect(scrollBounds);
                if (commentCountView.getLocalVisibleRect(scrollBounds)) {
                    // Any portion of the imageView, even a single pixel, is within the visible window
                    share_spouse.setAnimation((AnimationUtils.loadAnimation(ArticlesAndBlogsDetailsActivity.this, R.anim.right_to_left)));
                    share_spouse.setVisibility(View.VISIBLE);
                } else {
                    // NONE of the imageView is within the visible window
                    share_spouse.setVisibility(View.INVISIBLE);
                }

//                Log.d("View is", String.valueOf(view));

                int headerHeight = cover_image.getHeight() - mToolbar.getHeight();
                float ratio = 0;
                float ratioForTitle = 0;
                if (scrollPosition > 0 && headerHeight > 0) {
                    ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;
                    ratioForTitle = (float) Math.min(Math.max(scrollPosition, 0), titleScrollHeight) / titleScrollHeight;
                }
                updateActionBarTransparency(ratio, ratioForTitle);
                updateStatusBarColor(ratio);
                updateParallaxEffect(scrollPosition);

                int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));

                if (diff <= 10 && !isLoading && !StringUtils.isNullOrEmpty(commentTypeToFetch)) {
                    getMoreComments();
                }
            }
        });

    }

    private void getMoreComments() {
        isLoading = true;
        mLodingView.setVisibility(View.VISIBLE);
        GetCommentsRequestModel getCommentsRequestModel = new GetCommentsRequestModel();
        getCommentsRequestModel.setArticleId(articleId);
        getCommentsRequestModel.setLimit(AppConstants.COMMENT_LIMIT);
        getCommentsRequestModel.setOffset(offset);
        getCommentsRequestModel.setCommentType(commentTypeToFetch);
        ArticleBlogDetailsController _controller = new ArticleBlogDetailsController(this, this);
        _controller.getData(AppConstants.GET_MORE_COMMENTS, getCommentsRequestModel);
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

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void updateActionBarTransparency(float scrollRatio, float ratioForTitle) {
        int newAlpha = (int) (scrollRatio * 255);
        titleMain.setTextColor(Color.argb(newAlpha, 255, 255, 255));
        article_title.setTextColor(Color.argb(255 - (int) (ratioForTitle * 255), 255, 255, 255));
        mActionBarBackgroundDrawable.setAlpha(newAlpha);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mToolbar.setBackground(mActionBarBackgroundDrawable);
        } else {
            mToolbar.setBackgroundDrawable(mActionBarBackgroundDrawable);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor(float scrollRatio) {

        int newAlpha = (int) (scrollRatio * 255);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(newAlpha, 23, 68, 195));
        } else {
//            mToolbar.setBackgroundDrawable(mActionBarBackgroundDrawable);
        }

    }

    private void updateParallaxEffect(int scrollPosition) {
        float damping = 0.5f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        cover_image.offsetTopAndBottom(-offset);

        mLastDampedScroll = dampedScroll;
    }

    private int interpolate(int from, int to, float param) {
        return (int) (from * param + to * (1 - param));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        getMenuInflater().inflate(R.menu.aa_new_show_article, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActionBarBackgroundDrawable.setAlpha(255);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            case R.id.share:
                Utils.pushEvent(ArticlesAndBlogsDetailsActivity.this, GTMEventType.SHARE_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "");

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
                    Log.d("dadadaddadadadawdawdwdwadwadwda", "dwdwdwdwdwdw");
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
                        Log.d("COMMENTS ++++ ", " --- " + commentsJson.get(i).toString());
                        CommentsData cData = new Gson().fromJson(commentsJson.get(i).toString(), CommentsData.class);
                        leftMargin = 0;

                        ++isComingAfterReply;
                        setCommentData(viewHolder, cData, commentLayout);
                    }
                    break;
                case AppConstants.ARTICLES_DETAILS_REQUEST:
                case AppConstants.BLOGS_DETAILS_REQUEST:
                    ParentingDetailResponse responseData = (ParentingDetailResponse) response.getResponseObject();
                    if (responseData.getResponseCode() == 200) {

                        newCommentLayout.setVisibility(View.VISIBLE);
                        commentCountView.setVisibility(View.VISIBLE);
                        commentText.setText("");

                        getResponseUpdateUi(responseData);
                        if (isCommingFromCommentAPI) {
                            if (StringUtils.isNullOrEmpty(commentMessage)) {
                                showToast("Your comment has been added!");
                            } else {
                                Toast.makeText(ArticlesAndBlogsDetailsActivity.this, commentMessage, Toast.LENGTH_SHORT).show();
                            }
                            sendScrollDown();
                            isCommingFromCommentAPI = false;
                        }

                        removeProgressDialog();
                    } else if (responseData.getResponseCode() == 400) {
                        isCommingFromCommentAPI = false;
                        removeProgressDialog();
                        finish();
                        String message = responseData.getResult().getMessage();
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
                            ((ImageView) findViewById(R.id.follow_article)).setBackgroundResource(R.drawable.follow_blog);
                        } else {
                            isFollowing = true;
                            ((ImageView) findViewById(R.id.follow_article)).setBackgroundResource(R.drawable.un_follow_icon);
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
                            bookmarkImageView.setImageResource(R.drawable.ic_favorite_border_white_48dp_fill);
                        } else {
                            bookmarkStatus = 0;
                            bookmarkImageView.setImageResource(R.drawable.ic_favorite_border_white_48dp);
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

    private void changeFollowUnfollowIcon(String followOrUnfollow) {
        if (!StringUtils.isNullOrEmpty(followOrUnfollow)) {
            //	showToast("You are "+followOrUnfollow+" "+detailData.getAuthor_name());
            showToast(followOrUnfollow);
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


//                    Log.i("position", scrollX + " " + scrollY);
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
                    ((ImageView) findViewById(R.id.follow_article)).setBackgroundResource(R.drawable.follow_blog);
                    isFollowing = false;

                } else {
                    ((ImageView) findViewById(R.id.follow_article)).setBackgroundResource(R.drawable.un_follow_icon);
                    isFollowing = true;
                }
            }


            Log.i("resultCount", String.valueOf(resultCode));
        } catch (Exception e) {
            Log.i("OnActivityResult", e.getMessage());
        }
    }

    /**
     * It will give all the details & these will update all information on your screen;
     *
     * @param detailsResponse
     */

    private void getResponseUpdateUi(ParentingDetailResponse detailsResponse) {
        detailData = detailsResponse.getResult().getData();
        imageList = detailData.getBody().getImage();
        blogName = detailData.getBlog_title();
        authorType = detailData.getAuthor_type();
        if (StringUtils.isNullOrEmpty(blogName)) {
            blogName = "mycity4kids team";
        }
        if ("0".equals(detailData.getBookmarkStatus())) {
            bookmarkImageView.setImageResource(R.drawable.ic_favorite_border_white_48dp);
            bookmarkStatus = 0;
        } else {
            bookmarkImageView.setImageResource(R.drawable.ic_favorite_border_white_48dp_fill);
            bookmarkStatus = 1;
        }
        if (!StringUtils.isNullOrEmpty(detailsResponse.getResult().getData().getUser_following_status())) {
            if (detailsResponse.getResult().getData().getUser_following_status().equalsIgnoreCase("0")) {
                ((ImageView) findViewById(R.id.follow_article)).setBackgroundResource(R.drawable.follow_blog);
                isFollowing = false;
            } else {
                ((ImageView) findViewById(R.id.follow_article)).setBackgroundResource(R.drawable.un_follow_icon);
                isFollowing = true;
            }
        }

        if (!StringUtils.isNullOrEmpty(detailData.getTitle())) {
            article_title.setText(detailData.getTitle());
            titleMain.setText(detailData.getTitle());
        }

        try {
            if (!StringUtils.isNullOrEmpty(detailData.getAuthor_type())) {
                author_type.setText(detailData.getAuthor_type().toUpperCase());
            } else {
                author_type.setText(authorType.toUpperCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!StringUtils.isNullOrEmpty(detailData.getAuthor_name())) {
            ((TextView) findViewById(R.id.user_name)).setText(detailData.getAuthor_name());
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

            String bodyImgTxt = "<html><head></head><body>" + bodyDesc + "</body></html>";
            WebView view = (WebView) findViewById(R.id.articleWebView);
            view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            view.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");

        } else {
            bodyDesc = bodyDesc.replaceAll("\n", "<br/>");
            String bodyImgTxt = "<html><head></head><body>" + bodyDesc + "</body></html>";
            WebView view = (WebView) findViewById(R.id.articleWebView);
            view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            view.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");

        }


        if (!StringUtils.isNullOrEmpty(detailData.getAuthor_image())) {
            Picasso.with(this).load(detailData.getAuthor_image()).resize((int) (60 * density), (int) (65 * density)).centerCrop().into((ImageView) findViewById(R.id.user_image));
        } else {
            ((ImageView) findViewById(R.id.user_image)).setImageResource(R.drawable.default_img);
        }

        if (!StringUtils.isNullOrEmpty(detailData.getThumbnail_image())) {
            int width = getResources().getDisplayMetrics().widthPixels;
            Picasso.with(this).load(detailData.getThumbnail_image()).placeholder(R.drawable.blog_bgnew).resize(width, (int) (220 * density)).centerCrop().into(cover_image);
        }
        ViewHolder holder = null;
        if (detailData.getComments() != null) {
            holder = new ViewHolder();
            ((TextView) findViewById(R.id.txvComment)).setText("Comments " + "(" + detailData.getComments().size() + ")");
            LinearLayout commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));
            if (commentLayout.getChildCount() > 0) {
                commentLayout.removeAllViews();
            }
            //	LayoutInflater inflater=LayoutInflater.from(this);
            for (CommentsData commentList : detailData.getComments()) {
                leftMargin = 0;

                ++isComingAfterReply;
                //isInsertWithReply=false;


                setCommentData(holder, commentList, commentLayout);
            }
        }

        followAuthorId = detailData.getAuthor_id();

    }

    /**
     * This is using for  adding comments dynamically.
     *
     * @param holder
     * @param commentList
     * @param commentLayout
     */

    private void setCommentData(ViewHolder holder, CommentsData commentList, LinearLayout commentLayout) {

        if (holder != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.custom_comment_cell, null);
            holder.networkImg = (ImageView) view.findViewById(R.id.network_img);
//            holder.localImg = (ImageView) view.findViewById(R.id.local_img);
            holder.commentName = (TextView) view.findViewById(R.id.txvCommentTitle);
            holder.commentDescription = (TextView) view.findViewById(R.id.txvCommentDescription);
            holder.mRelativeContainer = (RelativeLayout) view.findViewById(R.id.relativeMainContainer);
            holder.dateTxt = (TextView) view.findViewById(R.id.txvDate);
            holder.replyTxt = (TextView) view.findViewById(R.id.txvReply);
            holder.dividerLine = view.findViewById(R.id.dividerLine);
            holder.innerCommentView = (CardView) view.findViewById(R.id.inner_comment_view);
            holder.replyTxt.setOnClickListener(this);
            holder.replyTxt.setTag(commentList);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(leftMargin, 0, 0, 0);
            leftMargin = leftMargin + 17;
            holder.mRelativeContainer.setLayoutParams(params);

            if (!StringUtils.isNullOrEmpty(commentList.getComment_type()) &&
                    (commentList.getComment_type().equals("fan") || commentList.getComment_type().equals("fb"))) {
                holder.replyTxt.setVisibility(View.GONE);
            } else {
                holder.replyTxt.setVisibility(View.VISIBLE);
            }

            if (!StringUtils.isNullOrEmpty(commentList.getName())) {
                holder.commentName.setText(commentList.getName());
            } else {
                holder.commentName.setText("User!");
            }
            if (!StringUtils.isNullOrEmpty(commentList.getBody())) {
                holder.commentDescription.setText(commentList.getBody());
            }
            if (!StringUtils.isNullOrEmpty(DateTimeUtils.getSeperateDate(commentList.getCreate()))) {
                holder.dateTxt.setText(DateTimeUtils.getSeperateDate(commentList.getCreate()));
            } else
                holder.dateTxt.setText(DateTimeUtils.getSeperateDate(commentList.getCreate()));

            if (!StringUtils.isNullOrEmpty(commentList.getProfile_image())) {
                try {
                    Picasso.with(this).load(commentList.getProfile_image()).transform(new CircleTransformation()).into(holder.networkImg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Picasso.with(this).load(R.drawable.default_img).transform(new CircleTransformation()).into(holder.networkImg);
                }
            } else {
                Picasso.with(this).load(R.drawable.default_img).transform(new CircleTransformation()).into(holder.networkImg);
            }

            /**
             * this will add seperator afer complete every comment with reply
             */

            if (isComingAfterReply == 1) {
                --isComingAfterReply;
                View dividerView = new View(this);
                dividerView.setBackgroundColor(Color.TRANSPARENT);
                commentLayout.addView(dividerView, LinearLayout.LayoutParams.MATCH_PARENT, 10);
            }

            commentLayout.addView(view);

            if (commentList.getReplies() != null && commentList.getReplies().size() > 0) {

                for (CommentsData replyList : commentList.getReplies()) {
                    setCommentData(holder, replyList, commentLayout);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.imgBack:
                    if (webView != null) {
                        webView.loadUrl("");
                        webView.clearHistory();
                        webView.loadDataWithBaseURL("", "", "", "", "");
                        //	webView=null;
                    }
                    finish();

                    break;
                case R.id.add_comment:
                    break;


                case R.id.txvReply:
                    UserTable _tableUser = new UserTable((BaseApplication) getApplication());
                    int countUser = _tableUser.getCount();
                    if (countUser <= 0) {

                        LoginFragmentDialog fragmentDialog = new LoginFragmentDialog();
                        fragmentDialog.show(ArticlesAndBlogsDetailsActivity.this.getSupportFragmentManager(), "");
                        return;
                    }
                    scrollX = mScrollView.getScrollX();
                    scrollY = mScrollView.getScrollY();
                    CommentsData commentData = (CommentsData) v.getTag();
                    String parentId = commentData.getId();

                    Intent intent = new Intent(this, AddCommentReplyActivity.class);
                    intent.putExtra(Constants.IS_COMMENT, false);
                    //intent.putExtra(Constants.ARTICLE_ID, articleId);
                    intent.putExtra(Constants.PARENT_ID, parentId);
                    startActivityForResult(intent, ADD_COMMENT_OR_REPLY);
                    //	((EditText)findViewById(R.id.editCommentTxt)).setVisibility(View.VISIBLE);
                    break;
                case R.id.img_follow:
                    ArticleBlogFollowRequest _followRequest = new ArticleBlogFollowRequest();
                    UserTable userTable = new UserTable((BaseApplication) getApplication());
                    int countFollow = userTable.getCount();
                    if (countFollow <= 0) {
                        removeProgressDialog();

                        LoginFragmentDialog fragmentDialog = new LoginFragmentDialog();
                        fragmentDialog.show(ArticlesAndBlogsDetailsActivity.this.getSupportFragmentManager(), "");
                        return;
                        //showToast(getResources().getString(R.string.user_login));
                        //return;
                    } else {
//                        UserModel userModel = userTable.getAllUserData();
//                        _followRequest.setSessionId("" + userModel.getUser().getSessionId());
//                        _followRequest.setUserId("" + userModel.getUser().getId());
                        _followRequest.setSessionId("" + SharedPrefUtils.getUserDetailModel(this).getId());
                        _followRequest.setUserId("" + SharedPrefUtils.getUserDetailModel(this).getSessionId());
                        _followRequest.setAuthorId("" + detailData.getId());
                        ArticleBlogFollowController _followController = new ArticleBlogFollowController(this, this);
                        showProgressDialog(getString(R.string.please_wait));
                        _followController.getData(AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST, _followRequest);
                    }
                    break;
                case R.id.img_fb:
                    showProgressDialog(getString(R.string.loading_));
                    FacebookUtils.loginFacebook(ArticlesAndBlogsDetailsActivity.this, new FacebookLoginListener() {

                        @Override
                        public void doAfterLogin(UserInfo userInfo) {
                            removeProgressDialog();
                            //System.out.println("Test");
                            Log.d("facebook", "Login Completed.");
                            String shareUrl = "";
                            if (StringUtils.isNullOrEmpty(detailData.getUrl())) {
                                shareUrl = "";
                            } else {
                                shareUrl = detailData.getUrl();
                            }
                            FacebookUtils.postOnWall(ArticlesAndBlogsDetailsActivity.this,
                                    new FacebookPostListener() {
                                        @Override
                                        public void doAfterPostOnWall(boolean status) {
                                            removeProgressDialog();
                                            //								showToast(getString(R.string.successfully_posted_post));
                                            showToast("Message has been successfully posted on your wall.");
                                            Log.d("facebook", "Post On Wall  Completed.");
                                            System.out.println(status);
                                        }
                                    }, shareUrl, detailData.getTitle());
                        }
                    });
                    break;
                case R.id.share_spouse:
                    Utils.pushEvent(ArticlesAndBlogsDetailsActivity.this, GTMEventType.SHARE_SPOUCE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "");

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


                case R.id.img_twitter:
                    break;

                case R.id.add_comment_btn:

                    if (commentText.getText().toString().trim().equalsIgnoreCase("")) {
                        ToastUtils.showToast(getApplicationContext(), "Please write to comment...");
                    } else {
                        String contentData = commentText.getText().toString();
                        String parentId1 = "";
                        UserTable _table1 = new UserTable((BaseApplication) getApplication());
                        int count1 = _table1.getCount();

                        if (count1 > 0) {
                            UserModel userData = _table1.getAllUserData();
                            CommentRequest _commentRequest = new CommentRequest();
                            _commentRequest.setArticleId(articleId);
                            /**
                             * in case of comment parentId will be empty.
                             *
                             */
                            _commentRequest.setParentId(parentId1);
                            _commentRequest.setContent(contentData);
                            _commentRequest.setUserId("" + userData.getUser().getId());
                            _commentRequest.setSessionId(userData.getUser().getSessionId());
                            CommentController _controller = new CommentController(this, this);
                            showProgressDialog("Adding a comment...");
                            _controller.getData(AppConstants.COMMENT_REPLY_REQUEST, _commentRequest);
                        }
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
                    if (!StringUtils.isNullOrEmpty(authorType)) {
                        if (authorType.trim().equalsIgnoreCase("Blogger")) {
                            intentn.putExtra(Constants.ARTICLE_NAME, blogName);
                            intentn.putExtra(Constants.FILTER_TYPE, "blogs");
                        } else {
                            intentn.putExtra(Constants.ARTICLE_NAME, detailData.getAuthor_name());
                            intentn.putExtra(Constants.FILTER_TYPE, "authors");
                        }
                    }
                    startActivityForResult(intentn, Constants.BLOG_FOLLOW_STATUS);
                    break;

                case R.id.user_name:
                    Intent intentnn = new Intent(this, BlogDetailActivity.class);
                    intentnn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentnn.putExtra(Constants.IS_COMMING_FROM_LISTING, false);
                    if (!StringUtils.isNullOrEmpty(authorType)) {
                        if (authorType.trim().equalsIgnoreCase("Blogger")) {
                            intentnn.putExtra(Constants.ARTICLE_NAME, blogName);
                            intentnn.putExtra(Constants.FILTER_TYPE, "blogs");
                        } else {
                            intentnn.putExtra(Constants.ARTICLE_NAME, detailData.getAuthor_name());
                            intentnn.putExtra(Constants.FILTER_TYPE, "authors");
                        }
                    }
                    startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                    break;
                case R.id.bookmarkBlogImageView:
                    Utils.pushEvent(ArticlesAndBlogsDetailsActivity.this, GTMEventType.FAVOURITE_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "");

                    addRemoveBookmark();
                    break;

            }
        } catch (Exception e) {
            Log.i("onClick", e.getMessage());
        }
    }


    private class ViewHolder {
        private ImageView networkImg;
        private ImageView localImg;
        private TextView commentName;
        private TextView commentDescription;
        private TextView dateTxt;
        private RelativeLayout mRelativeContainer;
        private TextView replyTxt;
        private View dividerLine;
        private CardView innerCommentView;

    }


    @Override
    public Drawable getDrawable(String source) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        LevelListDrawable d = new LevelListDrawable();

        new LoadImage().execute(source, d);
        return d;
    }

    private class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            //  Log.d(TAG, "doInBackground " + source);
            try {

                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // Log.d(TAG, "onPostExecute drawable " + mDrawable);
            //  Log.d(TAG, "onPostExecute bitmap " + bitmap);
            if (bitmap != null) {
                //   BitmapDrawable d = new BitmapDrawable(getResources(),bitmap);
                //  Bitmap b = ((BitmapDrawable)d).getBitmap();
                float imgWidth = bitmap.getWidth();
                float imgHeight = bitmap.getHeight();
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                float newWidth = dm.widthPixels;
                newWidth = newWidth - 50.0f;
                if (imgWidth < newWidth) {
                    newWidth = imgWidth;
                }

                float newHeight = (newWidth * imgHeight) / imgWidth;
                final Bitmap scaledBitmap = Bitmap.createBitmap((int) newWidth, (int) newHeight, Config.ARGB_8888);

                //   final Bitmap scaledBitmap =  BitmapUtils.getScaledBitmap(bitmap,(int)newWidth, (int)newHeight, true);

                float scaleX = (int) newWidth / (float) bitmap.getWidth();
                float scaleY = (int) newHeight / (float) bitmap.getHeight();
                float pivotX = 0;
                float pivotY = 0;

                Matrix scaleMatrix = new Matrix();
                scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

                Canvas canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);
                canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

                final BitmapDrawable resizeD = new BitmapDrawable(getResources(), scaledBitmap);

                mDrawable.addLevel(1, 1, resizeD);
                mDrawable.setBounds(0, 0, resizeD.getIntrinsicWidth(), resizeD.getIntrinsicHeight());
                mDrawable.setLevel(1);

                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                ((TextView) findViewById(R.id.txvDescription)).invalidate();
                CharSequence t = ((TextView) findViewById(R.id.txvDescription)).getText();
                ((TextView) findViewById(R.id.txvDescription)).setText(t);
                ((TextView) findViewById(R.id.txvDescription)).invalidate();
                //		}
                //	}, 100);

            }
        }
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
            bookmarkImageView.setImageResource(R.drawable.ic_favorite_border_white_48dp_fill);
            bookmarkRequest.setAction("add");
        } else {
            bookmarkStatus = 0;
            bookmarkImageView.setImageResource(R.drawable.ic_favorite_border_white_48dp);
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

}
