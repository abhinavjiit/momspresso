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
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.CircleTransformation;
import com.mycity4kids.ui.fragment.WhoToRemindDialogFragment;
import com.squareup.picasso.Picasso;

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


//    private SystemBarTintManager mStatusBarManager;

//    public static void navigate(AppCompatActivity activity, View transitionImage, ViewModel viewModel) {
//        Intent intent = new Intent(activity, DetailActivity.class);
//        intent.putExtra(EXTRA_IMAGE, viewModel.getImage());
//        intent.putExtra(EXTRA_TITLE, viewModel.getText());
//
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_IMAGE);
//        ActivityCompat.startActivity(activity, intent, options.toBundle());
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(ArticlesAndBlogsDetailsActivity.this, "Blog Details", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        deepLinkURL = getIntent().getStringExtra(Constants.DEEPLINK_URL);
        TAG = ArticlesAndBlogsDetailsActivity.this.getClass().getSimpleName();
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.APP_INDEX_API).build();

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
            String coverImageUrl = getIntent().getStringExtra(Constants.ARTICLE_COVER_IMAGE);

//            authorType = getIntent().getStringExtra(Constants.FILTER_TYPE);
//            bookmarkStatus = getIntent().getIntExtra(Constants.BOOKMARK_STATUS, 0);

//            if (bookmarkStatus == 0)
//                bookmarkImageView.setImageResource(R.drawable.ic_favorite_border_white_48dp);
//            else
//                bookmarkImageView.setImageResource(R.drawable.ic_favorite_border_white_48dp_fill);

//            if (!StringUtils.isNullOrEmpty(getIntent().getStringExtra(Constants.BLOG_NAME))) {
//                blogName = getIntent().getStringExtra(Constants.BLOG_NAME);
//            } else {
//                blogName = "mycity4kids team";
//            }


            cover_image = (ImageView) findViewById(R.id.cover_image);
            density = getResources().getDisplayMetrics().density;
            int width = getResources().getDisplayMetrics().widthPixels;
            if (!StringUtils.isNullOrEmpty(coverImageUrl)) {
                Picasso.with(this).load(coverImageUrl).placeholder(R.drawable.blog_bgnew).resize(width, (int) (220 * density)).centerCrop().into(cover_image);
            }

//            new changes @manish


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
            /*if(webView!=null){
                webView.loadUrl("");
				webView.clearHistory();
				webView.destroy();
				webView.loadDataWithBaseURL("", "", "", "", "");
				webView=null;
			}
			webView=(WebView)findViewById(R.id.WebView);
			webView.clearHistory();
			webView.setPadding(0, 0, 0, 0);
			webView.getSettings().setUseWideViewPort(true);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setHorizontalScrollBarEnabled(false);
			webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
			webView.getSettings().setLoadWithOverviewMode(true);*/
            //webView.invalidate();

            mScrollView = (ScrollView) findViewById(R.id.scroll_view);
            TextView headerTxt = (TextView) findViewById(R.id.header_txt);
//            imageCache = new BitmapLruCache();
//            imageLoader = new ImageLoader(Volley.newRequestQueue(this), imageCache);
            mUiHelper = new UiLifecycleHelper(this, FacebookUtils.callback);
            mUiHelper.onCreate(savedInstanceState);
//            fontPicker = (Spinner) findViewById(R.id.fontSizePicker_new);
//            ArrayList<String> fontList = new ArrayList<String>();
//
//            for (int font = 40; font < 61; font += 2) {
//                String fontSize = "" + Integer.valueOf(font);
//                fontList.add(fontSize);
//            }
//            ArrayAdapter<String> fontAdapter = new ArrayAdapter<String>(this, R.layout.text_for_write_review, fontList);
//            fontPicker.setAdapter(fontAdapter);
//
//            fontPicker.setOnItemSelectedListener(new OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                    float textSize = Float.valueOf(fontPicker.getAdapter().getItem(pos).toString());
//
//                    //((TextView) findViewById(R.id.txvDescription)).setTextSize(textSize);
//                    WebView webView = (WebView) findViewById(R.id.articleWebView);
//                    webView.getSettings().setTextZoom((int) (textSize));
//                    webView.getSettings().setDefaultFontSize((int) (textSize));
//                    webView.invalidate();
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> arg0) {
//                }
//            });

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

//        mStatusBarManager = new SystemBarTintManager(this);
//        mStatusBarManager.setStatusBarTintEnabled(true);
//        mInitialStatusBarColor = getResources().getColor(R.color.transparent);
//        mFinalStatusBarColor = getResources().getColor(R.color.primary_dark);
        mActionBarBackgroundDrawable = findViewById(R.id.viewColor).getBackground();
        share_spouse.setVisibility(View.INVISIBLE);


        final int titleScrollHeight = (int) (50 * density);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {

                int scrollPosition = mScrollView.getScrollY(); //for verticalScrollView
                Log.d("Scroll Position", String.valueOf(scrollPosition));
                Log.d("Height Scroll", String.valueOf(mScrollView.getHeight()));
                Log.d("Child Count", String.valueOf(mScrollView.getChildCount()));
                Log.d("ScrollY", String.valueOf(mScrollView.getScrollY()));
                int scrollBottom = mScrollView.getBottom();
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

                //View view2 = (View) ((RelativeLayout)mScrollView.getChildAt(0)).getChildAt(2);

                Log.d("View is", String.valueOf(view));
                //int diff = (view2.getTop() - (mScrollView.getHeight() + mScrollView.getScrollY()));
                //Log.d("diff", String.valueOf(diff));
                //if(diff==0)
                //{

                //share_spouse.setVisibility(View.VISIBLE);
                //}

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
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//        }
//        new code @manish
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


//    private void setTranslucentStatus(boolean on) {
//        Window win = getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }

    private void updateActionBarTransparency(float scrollRatio, float ratioForTitle) {
        int newAlpha = (int) (scrollRatio * 255);
//        article_title.setAlpha(255 - ratioForTitle * 255);
//        ViewHelper.setAlpha(titleMain, scrollRatio * 255);
        titleMain.setTextColor(Color.argb(newAlpha, 255, 255, 255));
        article_title.setTextColor(Color.argb(255 - (int) (ratioForTitle * 255), 255, 255, 255));
//        titleMain.setAlpha(newAlpha);
        mActionBarBackgroundDrawable.setAlpha(newAlpha);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mToolbar.setBackground(mActionBarBackgroundDrawable);
        } else {
            mToolbar.setBackgroundDrawable(mActionBarBackgroundDrawable);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor(float scrollRatio) {
//        int r = interpolate(Color.red(mInitialStatusBarColor), Color.red(mFinalStatusBarColor), 1 - scrollRatio);
//        int g = interpolate(Color.green(mInitialStatusBarColor), Color.green(mFinalStatusBarColor), 1 - scrollRatio);
//        int b = interpolate(Color.blue(mInitialStatusBarColor), Color.blue(mFinalStatusBarColor), 1 - scrollRatio);
//        mStatusBarManager.setTintColor(Color.rgb(r, g, b));

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
                Utils.pushEvent(ArticlesAndBlogsDetailsActivity.this, GTMEventType.SHARE_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId()+"", "");

                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
//                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, detailData.getTitle());
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

                // String shareMessage = "mycity4kids \n \n Check out this interesting blog post on " + detailData.getTitle() + " by " + author + " \n Read Here: " + shareUrl;
                // String shareMessage = "I just discovered something interesting on " + detailData.getTitle() + " - Check it out here " + shareUrl;
                /*+mBusinessInfoModel.getName()==null?"":mBusinessInfoModel.getName()+" in mycity4kids app. Check it out "+mBusinessInfoModel.getWeb_url()==null?"":mBusinessInfoModel.getWeb_url();*/
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
//                case AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST:
//                    CommonResponse followData = (CommonResponse) response.getResponseObject();
//                    removeProgressDialog();
//                    if (followData.getResponseCode() == 200) {
//                        changeFollowUnfollowIcon(followData.getResult().getMessage());
//                    } else if (followData.getResponseCode() == 400) {
//                        String message = followData.getResult().getMessage();
//                        if (!StringUtils.isNullOrEmpty(message)) {
//                            showToast(message);
//                        } else {
//                            showToast(getString(R.string.went_wrong));
//                        }
//                    }
//                    break;
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


                    Log.i("position", scrollX + " " + scrollY);
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
                    //bodyDesc = bodyDesc.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\n", "<br/>");
                    //bodyDescription.replaceAll("\\]", "");
                    //String imagekey = images.getKey().replaceAll("\\[", "").replaceAll("\\]", "");//<img src=\http://www.mycity4kids.com/parentingstop/uploads/737x164_Metro%20Museum.jpg\>
                    //	imagekey=images.getKey().replaceAll("\\]", "");
                    //	bodyDesc=bodyDesc.replaceAll(imagekey, "<img src=\\"+images.getValue()+"\\>");
                    bodyDesc = bodyDesc.replace(images.getKey(), "<p style='text-align:center'><img src=" + images.getValue() + " style=\"width: 100%;\"+></p>");
                }
            }
//            spannedValue = Html.fromHtml(bodyDesc, this, null);
//            ((TextView) findViewById(R.id.txvDescription)).setText(spannedValue);
//            ((TextView) findViewById(R.id.txvDescription)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);


            String bodyImgTxt = "<html><head></head><body>" + bodyDesc + "</body></html>";
            WebView view = (WebView) findViewById(R.id.articleWebView);
//            view.getSettings().setLoadWithOverviewMode(true);
//			view.getSettings().setUseWideViewPort(true);
            view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            view.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");

        } else {
            bodyDesc = bodyDesc.replaceAll("\n", "<br/>");
            //bodyDesc = "Music, maths, wildlife, art and dance - these are just a few elements in the week that lies ahead for children in Delhi NCR. Join Happy Feet’s music and movement programme for young children or sign up for some hip hop, Bollywood Jazz and contemporary dance in Shiamak’s Winter Funk. Students can gain from Amend Academy’s session on short cut tricks in Maths or take advantage of Kumon’s two week free trial to get a sense of the programme. Celebrate Wildlife Week at Asola Bhatti Sanctuary with a host of interesting activities. Let your children explore space through art in a painting competition organised by SPACE.</i></p><p><i><br></i></p>  <p><b><u>Creative Learning through Dance, Music and Movement</u></b></p>  <p>Locality:&nbsp;<a href=\"http://www.mycity4kids.com/Delhi-NCR/Events_Sheikh-Sarai-I_South-Delhi_el\" title=\"Events in Sheikh Sarai I\">Sheikh Sarai I</a></p>  <p>Age Group: 2 to 8 years</p>  <p>Date: 30<sup>th</sup> September to 31<sup>st</sup> December 2015</p>  <p>Children are naturally interested in music and dance and both these creative media are naturally good for children in their growth and development. Happy Feet is a music, dance and dance programme designed to build coordination, enhance creativity, confidence and core strength in young children. The next batch of the 3 month programme starts this week.</p><p>For more details, please <a href=\"http://www.mycity4kids.com/Delhi-NCR/Events/Dance-Music-and-Movement-Creative-Learning_Sheikh-Sarai-I/53802_ed\" target=\"_blank\" style=\"font-weight: bold;\">click here</a>.</p><p><br></p>  <p><b><u>Pick up some Short-cut Tricks of Maths</u></b></p>  <p>Locality:&nbsp;<a href=\"http://www.mycity4kids.com/Delhi-NCR/Events_Rohini-Sector-8_North-Delhi_el\" title=\"Events in Rohini Sector 8\">Rohini Sector 8</a></p>  <p>Age Group: 11 to 17 years</p>  <p>Date: 1<sup>st</sup> October 2015&nbsp;</p>  <p>Exams, Olympiads, competitive exams . . . it just gets tougher and more competitive for students these days. Since Maths is an integral part of their curriculum and out of school academics, it would help children to learn some easy short cuts for calculations. Amend Education Academy has organised a one day camp to teach students some quick tips and tricks of mathematics to help them get\uFEFF&nbsp;faster in calculations.</p><p>For more details, please <a href=\"http://www.mycity4kids.com/Delhi-NCR/Enhanced-Learning/Amend-Education-Academy_Rohini-Sector-8/48076_bd\" target=\"_blank\" style=\"font-weight: bold;\">click here</a>.</p><p><br></p>  <p><b><u>Wildlife Week 2015 at Asola Bhatti Wildlife Sanctuary</u></b></p>  <p>&nbsp;Locality: <a href=\"http://www.mycity4kids.com/Delhi-NCR/Events_Tughlakabad_South-Delhi_el\" title=\"Events in Tughlakabad\">Tughlakabad</a></p>  <p>Age Group: 10 and above</p>  <p>Date: 1<sup>st</sup> to 11<sup>th</sup> October 2015</p>  <p>Starting on 1<sup>st</sup> October is the Wildlife Week celebration at Asola Bhatti Wildlife Sanctuary.&nbsp;There are a host of activities being planned for the next 10 days, all free of cost and suitable for children of 10 years and above. You can join any of the walks and excursions around the venue that include a Migratory Bird Walk, Asola Lake Excursion, Leopard Trail and Blackbuck Trail and also a Wetland Birds Excursion to the Okhla Bird Sanctuary. In addition to the native tree plantation, kids will enjoy the more physical activities like tree climbing and mountain biking while yoga sessions will find takers from all age groups. There are several creative sessions such as making DIY bird feeder/nest, art from waste and competitions in wildlife quiz, face painting, wildlife sketching and online slogan writing as well as photo exhibitions and acoustic music sessions.</p>  <p>Those interested in learning more about wildlife can attend the workshop on wildlife gardening and study of aquatic lifeforms or even volunteer for habitat restoration or butterfly gardening. There will also be interactions with the Forest Department and several other expert talks on green living and related topics.</p><p>For more details, please <a href=\"http://www.mycity4kids.com/Delhi-NCR/Events/Wildlife-Week-2015-at-Asola-Bhatti-Wildlife-Sanctuary_Tughlakabad/53211_ed\" target=\"_blank\" style=\"font-weight: bold;\">click here</a>.</p><p><br> <br> <b><u>Don’t know what Kumon is? Try it out for free now.</u></b></p>  <p>Locality:&nbsp;Various centres in Delhi-NCR</p>  <p>Age Group: 5 and above</p>  <p>Date: 3<sup>rd</sup> to 16<sup>th</sup> October 2015</p>  <p>Kumon after-school&nbsp;math&nbsp;and&nbsp;reading&nbsp;programmes is offering free trial of two weeks&nbsp;for students new to the concept or current students who would like to try out a second subject. The Kumon method is an individualised learning method based on worksheets designed in a way that allows students to figure out how to solve problems on their own. This would be a good opportunity for parents who have been meaning to try out the Kumon Method for their children, however, have been hesitant due to lack of information or guidance.</p><p>For m ore information. please <a href=\"http://www.mycity4kids.com/Delhi-NCR/kumon-classes\" target=\"_blank\" style=\"font-weight: bold;\">click here</a>.</p>  <p>&nbsp;</p>  <p><b><u>World Space Week Interschool Painting Competition</u></b></p>  <p>Locality:&nbsp;<a href=\"http://www.mycity4kids.com/Delhi-NCR/Events_Others_Others_el\" title=\"Events in Others\">Online</a></p>  <p>Age Group: 7 to 17 years</p>  <p>Date: 5<sup>th</sup> to 28<sup>th</sup> October 2015</p>  <p>SPACE is an organisation working towards educating the masses through its programmes in astronomy and space science through tutorials, modules, curriculum for education requirements of schools &amp; students in India. As part of the celebrations of the World Space Week, SPACE is organising an Interschool Painting competition for students at primary and middle level from their associated schools only. The theme is ‘Discovering space’ and participants are encouraged to let their imagination soar - new planets, asteroids and blackholes; settlements and colonies in space; aliens and space creatures - anything goes. Entries should be submitted as scans of the paintings or as clicked images of the paintings. Painting submission starts from 5th October 2015 and can be submitted as part of 4 categories according to age (Group I: I to III; Group II: IV and V; Group III: VI to VIII and Group IV: IX to XII).</p>  <p>For more details, please <a href=\"http://www.mycity4kids.com/Delhi-NCR/Events/World-Space-Week-Interschool-Painting-Competition_Others/54379_ed\" target=\"_blank\" style=\"font-weight: bold;\">click here.</a></p>  <p>&nbsp;</p>  <br>  <p><b><u>Shiamak Winter Funk 2015 in Delhi</u></b></p>  <p>Locality:&nbsp;<a href=\"http://www.mycity4kids.com/Delhi-NCR/Events_Sector-57-Gurgaon_Gurgaon_el\" title=\"Events in Sector 57 Gurgaon\">Sector 57 Gurgaon</a>, East of Kailash, Jankapuri, Noida Punjabi Bagh, Vasantkunj</p>  <p>Age Group: 4 and above</p>  <p>Date: 7<sup>th</sup> to 25<sup>th</sup> October 2015</p>  <p>With Delhi winters approaching another winter staple starts soon - \u202ASHIAMAK Winter Funk. The next edition of this popular dance workshops will be held for three categories, Children (4-6 yrs);&nbsp;Junior (7-11 yrs) and Adults (12 yrs &amp; above). You can learn Hip Hop, Contemporary and Bollywood Jazz and even get to perform on stage. This time there is a special opportunity for the participants to win a chance to perform at a Bollywood award show. For early enrolments, there is a special early bird offer on 1st, 3rd and 4th October 2015.</p><p>For more details, please <a href=\"http://www.mycity4kids.com/Delhi-NCR/shiamak-winter-funk-2015\" target=\"_blank\" style=\"font-weight: bold;\">click here</a>.</p><p><br></p><p><i>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <span style=\"font-weight: bold;\">Submit An Event</span><br></i><i>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Do you know of an event we should add? Please send a mail to&nbsp;<a href=\"mailto:parul.ohri@mycity4kids.com\" target=\"_blank\">parul.ohri@mycity4kids.com</a>.<br></i><i>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Are you hosting an event we should know about? Please use the form at&nbsp;<a href=\"http://www.mycity4kids.com/Delhi-NCR/event/createevent\" target=\"_blank\">Delhi-NCR Events</a>.";
            String bodyImgTxt = "<html><head></head><body>" + bodyDesc + "</body></html>";
            WebView view = (WebView) findViewById(R.id.articleWebView);
//            view.getSettings().setLoadWithOverviewMode(true);
//            view.getSettings().setUseWideViewPort(true);
            view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            view.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");
//            if (!StringUtils.isNullOrEmpty(detailData.getBody().getText())) {
//                ((TextView) findViewById(R.id.txvDescription)).setText(detailData.getBody().getText());
//                ((TextView) findViewById(R.id.txvDescription)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//            }

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

			/*if(isInsertWithReply){
                holder.dividerLine.setVisibility(View.VISIBLE);
				isInsertWithReply=false;
			}else{
				holder.dividerLine.setVisibility(View.GONE);
			}*/
            /*
            if(leftMargin==0){
				holder.dividerLine.setVisibility(View.VISIBLE);
			}else{
				holder.dividerLine.setVisibility(View.GONE);
			}*/

            //	RelativeLayout.LayoutParams head_params = (RelativeLayout.LayoutParams)view.getLayoutParams();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(leftMargin, 0, 0, 0);
            leftMargin = leftMargin + 17;
//            view.setLayoutParams(params);
            holder.mRelativeContainer.setLayoutParams(params);

//            CardView.LayoutParams cardViewParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
//
//            cardViewParams.setMargins(leftMargin, 0, 0, 5);
//            leftMargin = leftMargin + 17;
//
//            holder.innerCommentView.setLayoutParams(cardViewParams);
            if (!StringUtils.isNullOrEmpty(commentList.getComment_type()) && commentList.getComment_type().equals("fb")) {
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

            /*if(isInsertWithReply){
            //	holder.dividerLine.setVisibility(View.VISIBLE);
				isInsertWithReply=false;
			}else{
			//	holder.dividerLine.setVisibility(View.GONE);
			}*/

            /**
             * this will add seperator afer complete every comment with reply
             */

            if (isComingAfterReply == 1) {
                --isComingAfterReply;
                View dividerView = new View(this);
                dividerView.setBackgroundColor(Color.TRANSPARENT);
//                dividerView.setBackgroundColor(Color.parseColor("#e3eaff"));
                commentLayout.addView(dividerView, LinearLayout.LayoutParams.MATCH_PARENT, 10);
            }

            commentLayout.addView(view);

            if (commentList.getReplies() != null && commentList.getReplies().size() > 0) {

                for (CommentsData replyList : commentList.getReplies()) {
                    /*	i++;
                    if(i==commentList.getReplies().size()){
						isInsertWithReply=true;
					}else{
						isInsertWithReply=false;
					}*/
                    setCommentData(holder, replyList, commentLayout);
                }
            }
        }
    }
    /*@Override
    protected void onStop() {
		super.onStop();
			webView.clearHistory();
			webView.clearCache(true);
		webView.loadUrl("");
		webView.loadDataWithBaseURL("", "", "", "", "");

	}*/
    /*@Override
    protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		webView.destroy();
		webView=null;
	}*/


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

//                    commented by @manish

//                    UserTable _table = new UserTable((BaseApplication) getApplication());
//                    int count = _table.getCount();
//                    if (count <= 0) {
//
//                        LoginFragmentDialog fragmentDialog = new LoginFragmentDialog();
//                        fragmentDialog.show(ArticlesAndBlogsDetailsActivity.this.getSupportFragmentManager(), "");
//                        return;
//                    }
//                    scrollX = mScrollView.getScrollX();
//                    scrollY = mScrollView.getScrollY();
//                    Intent intentAddComment = new Intent(this, AddCommentReplyActivity.class);
//                    intentAddComment.putExtra(Constants.IS_COMMENT, true);
//                    intentAddComment.putExtra(Constants.PARENT_ID, "");
//                    startActivityForResult(intentAddComment, ADD_COMMENT_OR_REPLY);

//                    new changes @manish

//                    ((TextView) findViewById(R.id.add_comment)).setVisibility(View.GONE);
//                    newCommentLayout.setVisibility(View.VISIBLE);
                    //	((EditText)findViewById(R.id.editCommentTxt)).setVisibility(View.VISIBLE);
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
//                case R.id.img_share:
//                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
//                    shareIntent.setType("text/plain");
//                    String shareUrl = "";
//                    if (StringUtils.isNullOrEmpty(detailData.getUrl())) {
//                        shareUrl = "";
//                    } else {
//                        shareUrl = detailData.getUrl();
//                    }
//                    String shareMessage="";
//                    String author = ((TextView) findViewById(R.id.user_name)).getText().toString();
//                    if (StringUtils.isNullOrEmpty(shareUrl)) {
//                         shareMessage = "mycity4kids\n\nCheck out this interesting blog post " +"\""+ detailData.getTitle() + "\" by " + author+".";
//                    } else {
//                         shareMessage = "mycity4kids\n\nCheck out this interesting blog post "+"\""+ detailData.getTitle() + "\" by " + author + ".\nRead Here: " + shareUrl;
//                    }
//
//                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
//                    startActivity(Intent.createChooser(shareIntent, "mycity4kids"));
//                    break;
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
                    Utils.pushEvent(ArticlesAndBlogsDetailsActivity.this, GTMEventType.SHARE_SPOUCE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId()+"", "");

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
//				Twitter.getInstance(Constants.TWITTER_OAUTH_KEY,
//                        Constants.TWITTER_OAUTH_SECRET, Constants.CALLBACK_URL,
//                        ArticlesAndBlogsDetailsActivity.this).doLogin(new ITLogin() {
//							@Override
//							public void success(TwitterUser user) {
//								showToast("Tweet has been successfully posted.");
//							}
//						},detailData.getTitle()+ " on mycity4kids. Check it out here " +detailData.getUrl());
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
                    Utils.pushEvent(ArticlesAndBlogsDetailsActivity.this, GTMEventType.FAVOURITE_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId()+"", "");

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
        /*try {

			Bitmap x;

			HttpURLConnection connection = (HttpURLConnection) new URL(source).openConnection();
			connection.connect();
			InputStream input = connection.getInputStream();

			x = BitmapFactory.decodeStream(input);
			final Drawable drawable=new BitmapDrawable(null,x);
			 Handler handler=new Handler();
			 handler.postDelayed(new Runnable() {

				@Override
				public void run() {



				//	drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
					 ((TextView) findViewById(R.id.txvDescription)).invalidate();
			            CharSequence t = ((TextView) findViewById(R.id.txvDescription)).getText();
			            ((TextView) findViewById(R.id.txvDescription)).setText(t);
				}
			}, 500);

			return d;
		} catch(IOException exception) {
			Log.v("IOException",exception.getMessage());
			return null;
		}*/
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

                //     Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 650, 350, false);
                final BitmapDrawable resizeD = new BitmapDrawable(getResources(), scaledBitmap);

                //   Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 650, 350, false);
                //   BitmapDrawable resizeD=  new BitmapDrawable(getResources(), bitmapResized);

                //		Handler handler=new Handler();
                //		handler.postDelayed(new Runnable() {

                //		@Override
                //		public void run() {
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
