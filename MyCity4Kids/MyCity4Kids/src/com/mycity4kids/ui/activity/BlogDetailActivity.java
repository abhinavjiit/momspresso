package com.mycity4kids.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.NewParentingBlogController;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleListResponse;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogDetailWithArticleModel;
import com.mycity4kids.newmodels.bloggermodel.BlogItemModel;
import com.mycity4kids.observablescrollview.ObservableScrollViewCallbacks;
import com.mycity4kids.observablescrollview.ScrollState;
import com.mycity4kids.observablescrollview.ScrollUtils;
import com.mycity4kids.observablescrollview.Scrollable;
import com.mycity4kids.observablescrollview.TouchInterceptionFrameLayout;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.AuthorDetailsAPI;
import com.mycity4kids.ui.adapter.BlogTapPagerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by manish.soni on 27-07-2015.
 */
public class BlogDetailActivity extends BaseActivity implements View.OnClickListener, ObservableScrollViewCallbacks {


    private TabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    BlogTapPagerAdapter blogTapPagerAdapter;
    BlogItemModel blogDetails;
    TextView bloggerName, authorType, description, moreDesc, bloggerTitle, authorRank, authorFollower;
    ImageView bloggerCover, bloggerImage;
    TextView bloggerFollow;
    RelativeLayout aboutLayout;
    ImageView profileImageView;
    //    ImageView backButton;
    private float density;
    private int screenWidth;

    // Observable Scroll
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final int INVALID_POINTER = -1;

    private View mImageView;
    private View mOverlayView;
    private TextView mTitleView;
    private TouchInterceptionFrameLayout mInterceptionLayout;
    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;
    private float mBaseTranslationY;
    private int mMaximumVelocity;
    private int mActivePointerId = INVALID_POINTER;
    private int mSlop;
    private int mFlexibleSpaceHeight;
    private int mTabHeight;
    private boolean mScrolled;
    private Toolbar mToolbar;
    private int mTopHeaderHeight;
    private String autFollow = "";
    private boolean isFollowing;
    private int listPosition;
    String lastFollowStatus;
    String authorId;

    Boolean isCommingFromListing = false;

    private String deepLinkURL;
    private GoogleApiClient mClient;
    private String TAG;
    private String screenTitle = "Parenting Blogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(BlogDetailActivity.this, "Blogger Description Screen", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        TAG = BlogDetailActivity.this.getClass().getSimpleName();
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.APP_INDEX_API).build();
        deepLinkURL = getIntent().getStringExtra(Constants.DEEPLINK_URL);

        setContentView(R.layout.blog_detail_layout);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mSlidingTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        bloggerName = (TextView) findViewById(R.id.blogger_name);
        bloggerTitle = (TextView) findViewById(R.id.blogger_title);
        authorType = (TextView) findViewById(R.id.author_type);
        bloggerImage = (ImageView) findViewById(R.id.blogger_profile);
        bloggerCover = (ImageView) findViewById(R.id.blogger_bg);
        bloggerFollow = (TextView) findViewById(R.id.blog_follow);
        description = (TextView) findViewById(R.id.blogger_desc);
        moreDesc = (TextView) findViewById(R.id.more_text);
        aboutLayout = (RelativeLayout) findViewById(R.id.about_desc_layout);
        authorRank = (TextView) findViewById(R.id.author_rank);
        authorFollower = (TextView) findViewById(R.id.author_follow);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        authorFollower.setVisibility(View.GONE);

        bloggerFollow.setOnClickListener(this);
        moreDesc.setOnClickListener(this);

        density = getResources().getDisplayMetrics().density;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        mTitleView = (TextView) findViewById(R.id.titleMain);
        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        mViewPager.setVisibility(View.VISIBLE);
        mSlidingTabLayout.setVisibility(View.VISIBLE);

        authorId = getIntent().getStringExtra(Constants.AUTHOR_ID);
        blogTapPagerAdapter = new BlogTapPagerAdapter(getSupportFragmentManager(), this, getApplicationContext(), authorId);

        getBlogDetails(authorId);

        // observable scrollview code
        mImageView = findViewById(R.id.blogger_bg);
        mOverlayView = findViewById(R.id.overlay);
        mOverlayView.setVisibility(View.VISIBLE);
        // Padding for ViewPager must be set outside the ViewPager itself
        // because with padding, EdgeEffect of ViewPager become strange.

        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_height_blogger);
        mTopHeaderHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_height_blogger_with_toolbar);
        mTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            // Set the status bar to dark-semi-transparentish
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            int statusBarHeight = getStatusBarHeight();
            authorRank.setPadding(0, statusBarHeight, 0, 0);
//            mFlexibleSpaceHeight = mFlexibleSpaceHeight + statusBarHeight;
            mTopHeaderHeight = mTopHeaderHeight + statusBarHeight;
            // Set paddingTop of toolbar to height of status bar.
            // Fixes statusbar covers toolbar issue
            mToolbar.setPadding(0, statusBarHeight, 0, 0);
        }

        findViewById(R.id.pager_wrapper).setPadding(0, mTopHeaderHeight, 0, 0);

//        mTitleView.setText(getTitle());
        setTitle(null);

        ((FrameLayout.LayoutParams) mSlidingTabLayout.getLayoutParams()).topMargin = mTopHeaderHeight - mTabHeight;

        ViewConfiguration vc = ViewConfiguration.get(this);
        mSlop = vc.getScaledTouchSlop();
        mMaximumVelocity = vc.getScaledMaximumFlingVelocity();
        mInterceptionLayout = (TouchInterceptionFrameLayout) findViewById(R.id.container);
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);
        mScroller = new OverScroller(getApplicationContext());
        ScrollUtils.addOnGlobalLayoutListener(mInterceptionLayout, new Runnable() {
            @Override
            public void run() {
                // Extra space is required to move mInterceptionLayout when it's scrolled.
                // It's better to adjust its height when it's laid out
                // than to adjust the height when scroll events (onMoveMotionEvent) occur
                // because it causes lagging.
                // See #87: https://github.com/ksoichiro/Android-ObservableScrollView/issues/87
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
                lp.height = getScreenHeight() + mFlexibleSpaceHeight;
                mInterceptionLayout.requestLayout();

                updateFlexibleSpace();
            }
        });

        moreDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BlogDetailActivity.this, ExpendedBlogDescriptionActivity.class);
                intent.putExtra(Constants.BLOG_DETAILS, blogDetails);
                intent.putExtra("AUTHOR_FOLLOW", autFollow);
                startActivityForResult(intent, Constants.BLOG_FOLLOW_STATUS);
                overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

            }
        });

        changeTabsFont();

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

    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) mSlidingTabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof AppCompatTextView) {
                    ((TextView) tabViewChild).setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
                }
            }
        }
    }

    public void run() {
        // TODO Auto-generated method stub
    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void updateUi(Response response) {

        BlogArticleListResponse responseData;

        if (response == null) {
            showToast("Something went wrong from server");
            removeProgressDialog();
            finish();
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.PARRENTING_BLOG_ARTICLE_LISTING:
                responseData = (BlogArticleListResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {

                    updateBlogArticleListing(responseData);

                    removeProgressDialog();
                } else if (responseData.getResponseCode() == 400) {
                    removeProgressDialog();
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        showToast(message);
                    } else {
                        showToast(getString(R.string.went_wrong));
                    }
                }
                break;

            case AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST:
                CommonResponse followData = (CommonResponse) response.getResponseObject();
                removeProgressDialog();
                if (followData.getResponseCode() == 200) {
                    ToastUtils.showToast(this, followData.getResult().getMessage(), Toast.LENGTH_SHORT);

                    if (isFollowing) {
                        isFollowing = false;
                        blogDetails.setUser_following_status("0");
//                        ((ImageView) findViewById(R.id.blog_follow)).setBackgroundResource((R.drawable.follow_blog));
                        ((TextView) findViewById(R.id.blog_follow)).setText("FOLLOW");
                    } else {
                        isFollowing = true;
                        blogDetails.setUser_following_status("1");
//                        ((ImageView) findViewById(R.id.blog_follow)).setBackgroundResource((R.drawable.un_follow_icon));
                        ((TextView) findViewById(R.id.blog_follow)).setText("UNFOLLOW");
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

            case AppConstants.PARRENTING_BLOG_ALL_DATA:
                BlogDetailWithArticleModel responseData_new = (BlogDetailWithArticleModel) response.getResponseObject();
                if (responseData_new.getResponseCode() == 200) {
                    try {
                        removeProgressDialog();
                        updateNewData(responseData_new);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast(getString(R.string.went_wrong));
                        finish();
                    }
                } else if (responseData_new.getResponseCode() == 400) {
                    removeProgressDialog();
                    String message = responseData_new.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        showToast(message);
                    } else {
                        showToast(getString(R.string.went_wrong));
                        finish();
                    }
                }
                break;

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent;
                if (blogDetails != null) {
                    if (!StringUtils.isNullOrEmpty(blogDetails.getUser_following_status())) {
                        if (!blogDetails.getUser_following_status().equalsIgnoreCase(lastFollowStatus)) {
                            if (isCommingFromListing) {
                                intent = new Intent(BlogDetailActivity.this, DashboardActivity.class);
                                intent.putExtra(Constants.BLOG_LIST_POSITION, listPosition);
                            } else {
                                intent = new Intent(BlogDetailActivity.this, ArticlesAndBlogsDetailsActivity.class);
                                intent.putExtra(Constants.BLOG_ISFOLLOWING, blogDetails.getUser_following_status());
                            }
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            finish();
                        }
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()) {

            case R.id.blog_follow:
                followAPICall(String.valueOf(blogDetails.getId()));
                break;
            case R.id.facebook_:
                if (!StringUtils.isNullOrEmpty(blogDetails.getFacebook_id())) {
                    intent = new Intent(this, LoadWebViewActivity.class);
                    intent.putExtra(Constants.WEB_VIEW_URL, blogDetails.getFacebook_id());
                    startActivity(intent);
                } else {
                    ToastUtils.showToast(this, "No link found..", Toast.LENGTH_SHORT);
                }

                break;

            case R.id.twitter_:
                if (!StringUtils.isNullOrEmpty(blogDetails.getTwitter_id())) {
                    intent = new Intent(this, LoadWebViewActivity.class);
                    intent.putExtra(Constants.WEB_VIEW_URL, blogDetails.getTwitter_id());
                    startActivity(intent);
                } else {
                    ToastUtils.showToast(this, "No link found..", Toast.LENGTH_SHORT);
                }
                break;
            case R.id.rss_:
                break;

        }

    }

    public void updateNewData(BlogDetailWithArticleModel data) {

        blogDetails = data.getResult().getData().getAuthor_details();
        lastFollowStatus = blogDetails.getUser_following_status();
        setBlogData();


        BlogArticleListResponse.BlogArticleListing articlelist = new BlogArticleListResponse().new BlogArticleListing();

        articlelist.setPopular(data.getResult().getData().getPopular());
        articlelist.setRecent(data.getResult().getData().getRecent());
        blogTapPagerAdapter = new BlogTapPagerAdapter(getSupportFragmentManager(), this, getApplicationContext(), authorId);
        mViewPager.setAdapter(blogTapPagerAdapter);
        mSlidingTabLayout.setupWithViewPager(mViewPager);
        mSlidingTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(1);
            }
        });
        changeTabsFont();

    }

    public void setBlogData() {

        bloggerName.setText(blogDetails.getFirst_name() + " " + blogDetails.getLast_name());
        bloggerName.setTextColor(Color.WHITE);
        bloggerTitle.setText(blogDetails.getBlog_title());
        mTitleView.setText(blogDetails.getBlog_title() + "");

        if (!StringUtils.isNullOrEmpty(blogDetails.getProfile_image())) {
            Picasso.with(this).load(blogDetails.getProfile_image()).resize((int) (90 * density), (int) (100 * density)).centerCrop().into(bloggerImage);
            Picasso.with(this).load(blogDetails.getProfile_image()).into(profileImageView);
        } else {
            Picasso.with(this).load(R.drawable.default_img).resize((int) (90 * density), (int) (100 * density)).centerCrop().into(bloggerImage);
            Picasso.with(this).load(R.drawable.default_img).into(profileImageView);
        }
        if (!StringUtils.isNullOrEmpty(blogDetails.getCover_image())) {
            Picasso.with(this).load(blogDetails.getCover_image()).resize(screenWidth, (int) (230 * density)).centerCrop().placeholder(R.drawable.blog_bgnew).into(bloggerCover);
        } else {
            Picasso.with(this).load(R.drawable.blog_bgnew).resize(screenWidth, (int) (230 * density)).centerCrop().into(bloggerCover);
        }

        authorRank.setText(String.valueOf(blogDetails.getAuthor_rank()));

        authorType.setText(blogDetails.getAuthor_type().toUpperCase());
        authorType.setTextColor(Color.parseColor(blogDetails.getAuthor_color_code()));

        if (StringUtils.isNullOrEmpty(blogDetails.getAbout_user())) {
            aboutLayout.setVisibility(View.GONE);
        } else {
            aboutLayout.setVisibility(View.VISIBLE);
            description.setText(blogDetails.getAbout_user());
            description.post(new Runnable() {

                @Override
                public void run() {
                    if (description.getLineCount() >= 4) {
                        moreDesc.setVisibility(View.VISIBLE);
                    } else {
                        moreDesc.setVisibility(View.GONE);
                    }
                }
            });
        }

        if (!StringUtils.isNullOrEmpty(blogDetails.getUser_following_status())) {
            if (blogDetails.getUser_following_status().equalsIgnoreCase("0")) {
//                ((ImageView) findViewById(R.id.blog_follow)).setBackgroundResource(R.drawable.follow_blog);
                ((TextView) findViewById(R.id.blog_follow)).setText("FOLLOW");
                isFollowing = false;
            } else {
//                ((ImageView) findViewById(R.id.blog_follow)).setBackgroundResource(R.drawable.un_follow_icon);
                ((TextView) findViewById(R.id.blog_follow)).setText("UNFOLLOW");
                isFollowing = true;
            }
        }

        if (!StringUtils.isNullOrEmpty(blogDetails.getAuthor_follwers_count())) {
            autFollow = blogDetails.getAuthor_follwers_count();
            authorFollower.setVisibility(View.VISIBLE);
            authorFollower.setText(blogDetails.getAuthor_follwers_count());
        }
    }

    public void hitDetailArticleListingapi() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, "Device is out of network coverage");
            return;
        }

        showProgressDialog(getString(R.string.please_wait));
        ParentingRequest _parentingModel = new ParentingRequest();
        _parentingModel.setPage(String.valueOf(0));
        _parentingModel.setSearchName(blogDetails.getBlog_title());
        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(this).getId());

        NewParentingBlogController newParentingBlogController = new NewParentingBlogController(this, this);
        newParentingBlogController.getData(AppConstants.PARRENTING_BLOG_ARTICLE_LISTING, _parentingModel);
    }

    private void getBlogDetails(String authorId) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, "Device is out of network coverage");
            return;
        }
        showProgressDialog(getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        AuthorDetailsAPI authorDetailsAPI = retrofit.create(AuthorDetailsAPI.class);

        Call<BlogDetailWithArticleModel> call = authorDetailsAPI.getAuthorDetails("" + SharedPrefUtils.getUserDetailModel(this).getId(),
                authorId);

        call.enqueue(authorDetailsResponseCallback);
    }

    Callback<BlogDetailWithArticleModel> authorDetailsResponseCallback = new Callback<BlogDetailWithArticleModel>() {
        @Override
        public void onResponse(Call<BlogDetailWithArticleModel> call, retrofit2.Response<BlogDetailWithArticleModel> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }

            BlogDetailWithArticleModel responseData_new = (BlogDetailWithArticleModel) response.body();
            if (responseData_new.getResponseCode() == 200) {
                try {
                    updateNewData(responseData_new);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("Exception", Log.getStackTraceString(e));
                    showToast(getString(R.string.went_wrong));
                    finish();
                }
            } else if (responseData_new.getResponseCode() == 400) {
                String message = responseData_new.getResult().getMessage();
                if (!StringUtils.isNullOrEmpty(message)) {
                    showToast(message);
                } else {
                    showToast(getString(R.string.went_wrong));
                    finish();
                }
            }
        }

        @Override
        public void onFailure(Call<BlogDetailWithArticleModel> call, Throwable t) {
            removeProgressDialog();
            showToast(getString(R.string.went_wrong));
        }
    };

    public void hitBlogDetail_with_article_listibg_API(String blog_name, String filtertype) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, "Device is out of network coverage");
            return;
        }
        showProgressDialog(getString(R.string.please_wait));
        ParentingRequest _parentingModel = new ParentingRequest();
        _parentingModel.setSearchName(blog_name);
        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(this).getId());
        _parentingModel.setSoty_by(filtertype);
        NewParentingBlogController newParentingBlogController = new NewParentingBlogController(this, this);
        newParentingBlogController.getData(AppConstants.PARRENTING_BLOG_ALL_DATA, _parentingModel);
    }


    private void updateBlogArticleListing(BlogArticleListResponse responseData) {


        if (!StringUtils.isNullOrEmpty(responseData.getResult().getData().getAuthor_follwers_count())) {
            autFollow = responseData.getResult().getData().getAuthor_follwers_count();
            authorFollower.setVisibility(View.VISIBLE);
            authorFollower.setText(responseData.getResult().getData().getAuthor_follwers_count());
        }

        blogTapPagerAdapter = new BlogTapPagerAdapter(getSupportFragmentManager(), this,
                getApplicationContext(), authorId);
        mViewPager.setAdapter(blogTapPagerAdapter);
        mSlidingTabLayout.setupWithViewPager(mViewPager);
        mSlidingTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(1);
            }
        });
        changeTabsFont();
    }


    // Observable ScrollView Code

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener = new TouchInterceptionFrameLayout.TouchInterceptionListener() {
        @Override
        public boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffX, float diffY) {
            if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
                // Horizontal scroll is maybe handled by ViewPager
                return false;
            }

            Scrollable scrollable = getCurrentScrollable();
            if (scrollable == null) {
                mScrolled = false;
                return false;
            }

            // If interceptionLayout can move, it should intercept.
            // And once it begins to move, horizontal scroll shouldn't work any longer.
            int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
            int translationY = (int) ViewHelper.getTranslationY(mInterceptionLayout);
            boolean scrollingUp = 0 < diffY;
            boolean scrollingDown = diffY < 0;
            if (scrollingUp) {
                if (translationY < 0) {
                    mScrolled = true;
                    return true;
                }
            } else if (scrollingDown) {
                if (-flexibleSpace < translationY) {
                    mScrolled = true;
                    return true;
                }
            }
            mScrolled = false;
            return false;
        }

        @Override
        public void onDownMotionEvent(MotionEvent ev) {
            mActivePointerId = ev.getPointerId(0);
            mScroller.forceFinished(true);
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            } else {
                mVelocityTracker.clear();
            }
            mBaseTranslationY = ViewHelper.getTranslationY(mInterceptionLayout);
            mVelocityTracker.addMovement(ev);
        }

        @Override
        public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
            int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
            float translationY = ScrollUtils.getFloat(ViewHelper.getTranslationY(mInterceptionLayout) + diffY, -flexibleSpace, 0);
            MotionEvent e = MotionEvent.obtainNoHistory(ev);
            e.offsetLocation(0, translationY - mBaseTranslationY);
            mVelocityTracker.addMovement(e);
            updateFlexibleSpace(translationY);
        }

        @Override
        public void onUpOrCancelMotionEvent(MotionEvent ev) {
            try {
                mScrolled = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity(mActivePointerId);
                mActivePointerId = INVALID_POINTER;
                mScroller.forceFinished(true);
                int baseTranslationY = (int) ViewHelper.getTranslationY(mInterceptionLayout);

                int minY = -(mTopHeaderHeight - mTabHeight);
                int maxY = 0;

                if (velocityY < -1500) {
                    velocityY = -500;
                }

                mScroller.fling(0, baseTranslationY, 0, velocityY, 0, 0, minY, maxY);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        updateLayout();
                    }
                });
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("Exception", Log.getStackTraceString(e));
            }

        }
    };

    private void updateLayout() {
        boolean needsUpdate = false;
        float translationY = 0;
        if (mScroller.computeScrollOffset()) {
            translationY = mScroller.getCurrY();
            int flexibleSpace = mTopHeaderHeight - mTabHeight;
            if (-flexibleSpace <= translationY && translationY <= 0) {
                needsUpdate = true;
            } else if (translationY < -flexibleSpace) {
                translationY = -flexibleSpace;
                needsUpdate = true;
            } else if (0 < translationY) {
                translationY = 0;
                needsUpdate = true;
            }
        }

        if (needsUpdate) {
            updateFlexibleSpace(translationY);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    updateLayout();
                }
            });
        }
    }

    private Scrollable getCurrentScrollable() {
        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return null;
        }
        View view = fragment.getView();
        if (view == null) {
            return null;
        }
        return (Scrollable) view.findViewById(R.id.scroll);
    }

    private void updateFlexibleSpace() {
        updateFlexibleSpace(ViewHelper.getTranslationY(mInterceptionLayout));
    }

    private void changeFollowUnfollowIcon(String followOrUnfollow) {
        if (!StringUtils.isNullOrEmpty(followOrUnfollow)) {
            showToast(followOrUnfollow);
        }
    }

    private void updateFlexibleSpace(float translationY) {
        ViewHelper.setTranslationY(mInterceptionLayout, translationY);
        int minOverlayTransitionY = getActionBarSize() - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-translationY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        float flexibleRange = mFlexibleSpaceHeight - getActionBarSize();
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat(-translationY / flexibleRange, 0, 1));
        float alpha = ScrollUtils.getFloat(-translationY / flexibleRange, 0, 1);

        ViewHelper.setAlpha(mTitleView, ScrollUtils.getFloat(-translationY / flexibleRange, 0, 1));
        ViewHelper.setAlpha(profileImageView, ScrollUtils.getFloat(-translationY / flexibleRange, 0, 1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb((int) alpha, 23, 68, 195));
        }

//        mTitleView.setTextColor(Color.argb((int)alpha, 255, 255, 255));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange + translationY - mTabHeight) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
//        setPivotXToTitle();
//        ViewHelper.setPivotY(mTitleView, 0);
//        ViewHelper.setScaleX(mTitleView, scale);
//        ViewHelper.setScaleY(mTitleView, scale);
    }

    private Fragment getCurrentFragment() {
        return blogTapPagerAdapter.getItemAt(mViewPager.getCurrentItem());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }
    }

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    private void updateBlogArticleListingNew(BlogArticleListResponse responseData) {


        if (!StringUtils.isNullOrEmpty(responseData.getResult().getData().getAuthor_follwers_count())) {
            autFollow = responseData.getResult().getData().getAuthor_follwers_count();
            authorFollower.setVisibility(View.VISIBLE);
            authorFollower.setText(responseData.getResult().getData().getAuthor_follwers_count());
        }

        blogTapPagerAdapter = new BlogTapPagerAdapter(getSupportFragmentManager(), this,
                getApplicationContext(), authorId);
        mViewPager.setAdapter(blogTapPagerAdapter);
        mSlidingTabLayout.setupWithViewPager(mViewPager);
        changeTabsFont();
    }

    @Override
    public void onBackPressed() {

        Intent intent;
        if (blogDetails != null) {
            if (!StringUtils.isNullOrEmpty(blogDetails.getUser_following_status())) {
                if (!blogDetails.getUser_following_status().equalsIgnoreCase(lastFollowStatus)) {

                    if (isCommingFromListing) {
                        intent = new Intent(BlogDetailActivity.this, DashboardActivity.class);
                        intent.putExtra(Constants.BLOG_LIST_POSITION, listPosition);
                    } else {
                        intent = new Intent(BlogDetailActivity.this, ArticlesAndBlogsDetailsActivity.class);
                        intent.putExtra(Constants.BLOG_ISFOLLOWING, blogDetails.getUser_following_status());
                    }

                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    finish();
                }
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == Constants.BLOG_FOLLOW_STATUS) {

            if (data.getStringExtra(Constants.BLOG_STATUS).equalsIgnoreCase("0")) {
                ((TextView) findViewById(R.id.blog_follow)).setText("FOLLOW");
                isFollowing = false;
                blogDetails.setUser_following_status("0");

            } else {
                ((TextView) findViewById(R.id.blog_follow)).setText("UNFOLLOW");
                isFollowing = true;
                blogDetails.setUser_following_status("1");
            }
        }

    }
}
