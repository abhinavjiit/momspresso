
package com.mycity4kids.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.DraftListAdapter;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.parentingdetails.ImageData;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ArticleDetailResult;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.DraftListData;
import com.mycity4kids.models.response.DraftListResponse;
import com.mycity4kids.models.response.DraftListResult;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.LanguageRanksModel;
import com.mycity4kids.models.response.ReviewListingResult;
import com.mycity4kids.models.response.ReviewResponse;
import com.mycity4kids.models.response.UserCommentsResponse;
import com.mycity4kids.models.response.UserCommentsResult;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.ui.adapter.PublishedArticleListingAdapter;
import com.mycity4kids.ui.adapter.ReviewsListAdapter;
import com.mycity4kids.ui.adapter.UserCommentsAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.RoundedTransformation;
import com.mycity4kids.widget.CustomViewFlipper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 16/3/16.
 */
public class BloggerDashboardActivity extends BaseActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final int REQUEST_CAMERA = 0;

    private static final int REQUEST_EDIT_PICTURE = 1;

    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static String[] PERMISSIONS_EDIT_PICTURE = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * Root of the layout of this Activity.
     */
    private View mLayout;
    private CustomViewFlipper rankViewFlipper;
    private Toolbar mToolbar;
    private ImageView bloggerImageView;
    ImageView addDraft;
    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE = 1113;
    String mCurrentPhotoPath, absoluteImagePath;
    File photoFile;

    Uri imageUri;
    File file;
    ListView draftListview, publishedArticleListView, commentsListView, reviewsListView;
    ArrayList<DraftListResult> draftList;
    ArrayList<ReviewListingResult> reviewList;
    ArrayList<UserCommentsResult> commentList;
    int position;
    DraftListAdapter adapter;
    PublishedArticleListingAdapter articlesListingAdapter;
    UserCommentsAdapter commentsListAdapter;
    ReviewsListAdapter reviewsListAdapter;
    //    private LinearLayout rankLinearLL, rankTwoLinearLL;
//    TextView rankingTextView, rankingTwoTextView, rankingLabelTextView, rankingTwoLabelTextView;
    TextView followersTextView, followingTextView, userBio, blogTitle, editProfileTextView, analyticsTextView;
    ImageView draftImageView, publishedImageView, commentsImageView, reviewImageView;
    LinearLayout draftItemLinearLayout, publishedItemLinearLayout, commentsItemLinearLayout, reviewItemLinearLayout;
    private String firstName, lastName, Bio, phoneNumber;
    private int nextPageNumber = 1;
    RelativeLayout mLodingView;
    private boolean isReuqestRunning = false;
    private boolean isReuqestCommentsRunning = false;
    private boolean isRequestReviewRunning = false;
    boolean isLastPageReached = true;
    private ProgressBar progressBar;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    TextView noDraftTextView, noArticleTextView, noReviewsTextView, noCommentsTextView;
    TextView moreTextView;
    public String userId;
    public boolean isPrivateProfile;
    TextView followButton, unfollowButton;
    LinearLayout FollowersLinearLL, FollowingLinearLL;
    View header;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
    private int limit = 10;
    private String paginationValue = "";
    boolean isLastPageCommentsReached = false;
    boolean isLastPageReviewsReached = false;
    Boolean isFollowing = false;
    boolean stackClearRequired = false;
    private int nextPageNumberReview = 1;
    private TranslateAnimation mInFromLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogger_dashboard);
        mLayout = findViewById(R.id.rootLayout);
        Utils.pushOpenScreenEvent(BloggerDashboardActivity.this, "User Profile", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        userId = getIntent().getStringExtra(AppConstants.PUBLIC_PROFILE_USER_ID);
        stackClearRequired = getIntent().getBooleanExtra(AppConstants.STACK_CLEAR_REQUIRED, false);

        if (getIntent().getBooleanExtra("fromNotification", false)) {
            Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "profile");
        }

        if (userId == null || userId.equals(SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getDynamoId())) {
            isPrivateProfile = true;
            userId = SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getDynamoId();
        } else {
            isPrivateProfile = false;
            String fromScreen = getIntent().getStringExtra(Constants.FROM_SCREEN);
            String authorName = getIntent().getStringExtra(AppConstants.AUTHOR_NAME);
            if (StringUtils.isNullOrEmpty(authorName)) {
                Utils.pushBloggerDetailsOpenedEvent(this, GTMEventType.BLOGGER_DETAILS_CLICKED, SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getDynamoId(), fromScreen, "" + userId);
            } else {
                Utils.pushBloggerDetailsOpenedEvent(this, GTMEventType.BLOGGER_DETAILS_CLICKED, SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getDynamoId(), fromScreen, authorName + "~" + userId);
            }

        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        draftListview = (ListView) findViewById(R.id.draftListView);
        publishedArticleListView = (ListView) findViewById(R.id.publishedArticleListView);
        commentsListView = (ListView) findViewById(R.id.commentsListView);
        reviewsListView = (ListView) findViewById(R.id.reviewsListView);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        addDraft = (ImageView) findViewById(R.id.addDraft);
        if (isPrivateProfile) {
            header = getLayoutInflater().inflate(R.layout.header_blogger_dashboard, null);
        } else {
            header = getLayoutInflater().inflate(R.layout.header_blogger_dashboard_public, null);
            addDraft.setVisibility(View.GONE);
        }
        header.setClickable(false);

        AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
        mInFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        mInFromLeft.setDuration(500);
        mInFromLeft.setInterpolator(accelerateInterpolator);

        rankViewFlipper = (CustomViewFlipper) header.findViewById(R.id.rankViewFlipper);
        rankViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        rankViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        rankViewFlipper.setAutoStart(true);
        rankViewFlipper.setFlipInterval(3000);
        rankViewFlipper.startFlipping();

//        rankTwoLinearLL = (LinearLayout) header.findViewById(R.id.rankTwoLinearLL);
//        rankingTextView = (TextView) header.findViewById(R.id.rankingTextView);
//        rankingTwoTextView = (TextView) header.findViewById(R.id.rankingTwoTextView);
//        rankingLabelTextView = (TextView) header.findViewById(R.id.rankingLabelTextView);
//        rankingTwoLabelTextView = (TextView) header.findViewById(R.id.otherRankedLanguageLabel);
        followersTextView = (TextView) header.findViewById(R.id.followersTextView);
        followingTextView = (TextView) header.findViewById(R.id.followingTextView);
        analyticsTextView = (TextView) header.findViewById(R.id.analyticsTextView);
        userBio = (TextView) header.findViewById(R.id.userBio);
        blogTitle = (TextView) header.findViewById(R.id.blogName);
        draftItemLinearLayout = (LinearLayout) header.findViewById(R.id.draftItemLinearlayout);
        publishedItemLinearLayout = (LinearLayout) header.findViewById(R.id.publishedItemLinearlayout);
        commentsItemLinearLayout = (LinearLayout) header.findViewById(R.id.commentsItemLinearlayout);
        reviewItemLinearLayout = (LinearLayout) header.findViewById(R.id.reviewItemLinearLayout);
        draftImageView = (ImageView) header.findViewById(R.id.draftImageView);
        publishedImageView = (ImageView) header.findViewById(R.id.publishedImageView);
        commentsImageView = (ImageView) header.findViewById(R.id.commentsImageView);
        reviewImageView = (ImageView) header.findViewById(R.id.reviewImageView);
        editProfileTextView = (TextView) header.findViewById(R.id.editProfileTextView);
        noDraftTextView = (TextView) header.findViewById(R.id.noDraftsTextView);
        noArticleTextView = (TextView) header.findViewById(R.id.noArticlesTextView);
        noCommentsTextView = (TextView) header.findViewById(R.id.noCommentsTextView);
        noReviewsTextView = (TextView) header.findViewById(R.id.noReviewTextView);
        moreTextView = (TextView) header.findViewById(R.id.more_text);
        followButton = (TextView) header.findViewById(R.id.followTextView);
        unfollowButton = (TextView) header.findViewById(R.id.unfollowTextView);
        FollowersLinearLL = (LinearLayout) header.findViewById(R.id.FollowersLinearLL);
        FollowingLinearLL = (LinearLayout) header.findViewById(R.id.FollowingLinearLL);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        draftList = new ArrayList<>();
        reviewList = new ArrayList<>();
        commentList = new ArrayList<>();
        articleDataModelsNew = new ArrayList<>();
        adapter = new DraftListAdapter(this, draftList);
        reviewsListAdapter = new ReviewsListAdapter(this, reviewList);
        commentsListAdapter = new UserCommentsAdapter(this, commentList);
        articlesListingAdapter = new PublishedArticleListingAdapter(this, new PublishedArticleListingAdapter.BtnClickListener() {
            @Override
            public void onBtnClick(int position) {
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                ArticleDetailsAPI articleDetailsAPI = retrofit.create(ArticleDetailsAPI.class);
                Call<ArticleDetailResult> call = articleDetailsAPI.getArticleDetailsFromS3(articleDataModelsNew.get(position).getId());
                call.enqueue(articleDetailResponseCallback);

            }
        }, true);

        bloggerImageView = (ImageView) header.findViewById(R.id.bloggerImageView);
        int imagesToShow[] = {R.drawable.default_article, R.drawable.default_blogger, R.drawable.default_commentor_img};

        if (isPrivateProfile) {
            editProfileTextView.setVisibility(View.VISIBLE);
            draftItemLinearLayout.setVisibility(View.VISIBLE);
            draftListview.addHeaderView(header, null, false);
            if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
                Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi)
                        .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(bloggerImageView);
            }
            bloggerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ActivityCompat.checkSelfPermission(BloggerDashboardActivity.this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(BloggerDashboardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(BloggerDashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            Log.i("PERMISSIONS", "storage permissions has NOT been granted. Requesting permissions.");
                            requestCameraAndStoragePermissions();
                        } else if (ActivityCompat.checkSelfPermission(BloggerDashboardActivity.this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(BloggerDashboardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(BloggerDashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {
                            Log.i("PERMISSIONS", "storage permissions has NOT been granted. Requesting permissions.");
                            requestCameraPermission();
                        } else {
                            chooseImageOptionPopUp(bloggerImageView);
                        }
                    } else {
                        chooseImageOptionPopUp(bloggerImageView);
                    }

                }
            });
        } else {
            followButton.setVisibility(View.VISIBLE);
            followButton.setEnabled(false);
            unfollowButton.setVisibility(View.INVISIBLE);
            unfollowButton.setEnabled(false);
            followButton.setOnClickListener(this);
            unfollowButton.setOnClickListener(this);

            editProfileTextView.setVisibility(View.GONE);
            draftListview.setVisibility(View.GONE);
            publishedArticleListView.addHeaderView(header, null, false);
            publishedArticleListView.setVisibility(View.VISIBLE);
            draftListview.setVisibility(View.GONE);
            publishedImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_publish_selected));
        }

        commentsListView.setAdapter(commentsListAdapter);
        reviewsListView.setAdapter(reviewsListAdapter);
        draftListview.setAdapter(adapter);
        draftItemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draftImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_draft_selected));
                publishedImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_publish));
                commentsImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_comments));
                reviewImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_review));

                draftListview.setVisibility(View.VISIBLE);
                if (draftList.size() == 0) {
                    noDraftTextView.setVisibility(View.VISIBLE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                } else {
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                }
                publishedArticleListView.setVisibility(View.GONE);

                commentsListView.setVisibility(View.GONE);
                reviewsListView.setVisibility(View.GONE);
            }
        });
        publishedItemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draftImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_draft));
                publishedImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_publish_selected));
                commentsImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_comments));
                reviewImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_review));

                draftListview.setVisibility(View.GONE);
                if (publishedArticleListView.getHeaderViewsCount() == 0) {
                    publishedArticleListView.setAdapter(null);
                    publishedArticleListView.addHeaderView(header, null, false);
                    publishedArticleListView.setAdapter(articlesListingAdapter);
                }
                if (articleDataModelsNew.size() == 0) {
                    noArticleTextView.setVisibility(View.VISIBLE);
                    noDraftTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                } else {
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                }
                header.setClickable(false);
                publishedArticleListView.setVisibility(View.VISIBLE);
                commentsListView.setVisibility(View.GONE);
                reviewsListView.setVisibility(View.GONE);
            }
        });
        commentsItemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draftImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_draft));
                publishedImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_publish));
                commentsImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_comments_selected));
                reviewImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_review));

                draftListview.setVisibility(View.GONE);
                publishedArticleListView.setVisibility(View.GONE);
                if (commentsListView.getHeaderViewsCount() == 0) {
                    commentsListView.setAdapter(null);
                    commentsListView.addHeaderView(header, null, false);
                    commentsListView.setAdapter(commentsListAdapter);
                }
                if (commentList.size() == 0) {
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.VISIBLE);
                    noReviewsTextView.setVisibility(View.GONE);
                } else {
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                }
                header.setClickable(false);
                commentsListView.setVisibility(View.VISIBLE);
                reviewsListView.setVisibility(View.GONE);
            }
        });
        reviewItemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draftImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_draft));
                publishedImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_publish));
                commentsImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_comments));
                reviewImageView.setImageDrawable(ContextCompat.getDrawable(BloggerDashboardActivity.this, R.drawable.icon_review_selected));

                draftListview.setVisibility(View.GONE);
                publishedArticleListView.setVisibility(View.GONE);
                commentsListView.setVisibility(View.GONE);
                if (reviewsListView.getHeaderViewsCount() == 0) {
                    reviewsListView.setAdapter(null);
                    reviewsListView.addHeaderView(header, null, false);
                    reviewsListView.setAdapter(reviewsListAdapter);
                }
                header.setClickable(false);
                if (reviewList.size() == 0) {
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.VISIBLE);
                } else {
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                }
                reviewsListView.setVisibility(View.VISIBLE);
            }
        });
        editProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BloggerDashboardActivity.this, EditProfieActivity.class);
                startActivity(intent);
            }
        });
        publishedArticleListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    hitPublishedArticleApi();
                    isReuqestRunning = true;
                }
                if (firstVisibleItem == 0) {
                    hidefloatingbutton(false);
                } else {
                    hidefloatingbutton(true);
                }
            }
        });
        moreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moreTextView.getText().toString().equalsIgnoreCase("MORE")) {
                    userBio.setMaxLines(100);
                    userBio.setEllipsize(null);
                    moreTextView.setText("LESS");
                } else {
                    userBio.setMaxLines(3);
                    userBio.setEllipsize(null);
                    moreTextView.setText("MORE");
                }
            }
        });
        FollowersLinearLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BloggerDashboardActivity.this, FollowersAndFollowingListActivity.class);
                intent.putExtra(AppConstants.FOLLOW_LIST_TYPE, AppConstants.FOLLOWER_LIST);
                intent.putExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS, userId);
                startActivity(intent);

            }
        });
        FollowingLinearLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BloggerDashboardActivity.this, FollowersAndFollowingListActivity.class);
                intent.putExtra(AppConstants.FOLLOW_LIST_TYPE, AppConstants.FOLLOWING_LIST);
                intent.putExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS, userId);
                startActivity(intent);
            }
        });
        addDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > 15) {
                    Utils.pushEvent(BloggerDashboardActivity.this, GTMEventType.ADD_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getDynamoId() + "", "User Profile");
                    Intent intent1 = new Intent(BloggerDashboardActivity.this, EditorPostActivity.class);
                    Bundle bundle5 = new Bundle();
                    bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
                    bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
                    bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_title_placeholder));
                    bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_content_placeholder));
                    bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                    bundle5.putString("from", "DraftListViewActivity");
                    intent1.putExtras(bundle5);
                    startActivity(intent1);
                } else {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("http://www.mycity4kids.com/parenting/admin/setupablog"));
                    startActivity(viewIntent);
                }
            }
        });
    }


    private void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever) {

        //imageView <-- The View which displays the images
        //images[] <-- Holds R references to the images to display
        //imageIndex <-- index of the first image to show in images[]
        //forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.

        int fadeInDuration = 500; // Configure time values here
        int timeBetween = 3000;
        int fadeOutDuration = 1000;

        blogTitle.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        blogTitle.setText(""+arr.get(imageIndex).getRank());

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        blogTitle.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (arr.size() - 1 > imageIndex) {
                    animate(imageView, images, imageIndex + 1, forever); //Calls itself until it gets to the end of the array
                } else {
                    if (forever) {
                        animate(imageView, images, 0, forever);  //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        rankViewFlipper.clearAnimation();
    }

    private void checkFollowingStatusAPI() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
        articleDetailRequest.setArticleId("");
        Call<ArticleDetailResponse> callBookmark = articleDetailsAPI.checkFollowingBookmarkStatus("0", userId);
        callBookmark.enqueue(isFollowedResponseCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBloggerDashboardDetails();
        hitDraftListingApi();
        hitPublishedArticleApi();
        if (!isLastPageCommentsReached) {
            hitCommentsApi();
        }
        hitReviewApi();
        checkFollowingStatusAPI();

        draftListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Header Item
                } else {
                    // List view items
                    if (Build.VERSION.SDK_INT > 15) {
                        Intent intent = new Intent(BloggerDashboardActivity.this, EditorPostActivity.class);
                        intent.putExtra("draftItem", draftList.get(position - 1));
                        intent.putExtra("from", "draftList");
                        startActivity(intent);
                    } else {
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse("http://www.mycity4kids.com/parenting/admin/setupablog"));
                        startActivity(viewIntent);
                    }
                }
            }
        });

        draftListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    hidefloatingbutton(false);
                } else {
                    hidefloatingbutton(true);
                }
            }
        });

        publishedArticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Header Item
                } else {
                    // List view items
                    Intent intent = new Intent(BloggerDashboardActivity.this, ArticlesAndBlogsDetailsActivity.class);
                    intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position - 1).getUserId());
                    intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position - 1).getId());
                    intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position - 1).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position - 1).getTitleSlug());
                    if (isPrivateProfile) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Self Published Articles");
                        intent.putExtra(Constants.FROM_SCREEN, "Private User Profile");
                    } else {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Other Authors Published Articles");
                        intent.putExtra(Constants.FROM_SCREEN, "Public User Profile");
                    }
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + (position - 1));
                    startActivity(intent);

                }

            }
        });

        commentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Header Item

                } else {
                    if (commentList.get(position - 1).getArticleId().startsWith("videos")) {
                        Intent intent1 = new Intent(BloggerDashboardActivity.this, VlogsDetailActivity.class);
                        intent1.putExtra(Constants.VIDEO_ID, commentList.get(position - 1).getArticleId());
                        intent1.putExtra(Constants.AUTHOR_ID, commentList.get(position - 1).getUserId());
                        intent1.putExtra(Constants.FROM_SCREEN, "User Profile");
                        if (isPrivateProfile) {
                            intent1.putExtra(Constants.ARTICLE_OPENED_FROM, "Private Comments");
                            intent1.putExtra(Constants.FROM_SCREEN, "Private User Profile");
                        } else {
                            intent1.putExtra(Constants.ARTICLE_OPENED_FROM, "Public Comments");
                            intent1.putExtra(Constants.FROM_SCREEN, "Public User Profile");
                        }
                        intent1.putExtra(Constants.ARTICLE_INDEX, "" + (position - 1));
                        startActivity(intent1);
                    } else {
                        Intent intent = new Intent(BloggerDashboardActivity.this, ArticleDetailsContainerActivity.class);
                        intent.putExtra(Constants.ARTICLE_ID, commentList.get(position - 1).getArticleId());
                        intent.putExtra(Constants.AUTHOR_ID, commentList.get(position - 1).getUserId());
                        intent.putExtra(Constants.BLOG_SLUG, commentList.get(position - 1).getBlogTitleSlug());
                        intent.putExtra(Constants.TITLE_SLUG, commentList.get(position - 1).getTitleSlug());
                        intent.putExtra(Constants.FROM_SCREEN, "User Profile");
                        if (isPrivateProfile) {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Private Comments");
                            intent.putExtra(Constants.FROM_SCREEN, "Private User Profile");
                        } else {
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Public Comments");
                            intent.putExtra(Constants.FROM_SCREEN, "Public User Profile");
                        }
                        intent.putExtra(Constants.ARTICLE_INDEX, "" + (position - 1));
                        startActivity(intent);
                    }
                }
            }
        });

        commentsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestCommentsRunning && !isLastPageCommentsReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    hitCommentsApi();
                    isReuqestCommentsRunning = true;

                }
                if (firstVisibleItem == 0) {
                    hidefloatingbutton(false);
                } else {
                    hidefloatingbutton(true);
                }
            }
        });

        reviewsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isRequestReviewRunning && !isLastPageReviewsReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    //caching enabled only for page 1. so disabling it here for all other pages by passing false.
                    //  hitArticleListingApi(nextPageNumber, sortType, false);
                    hitReviewApi();
                    isRequestReviewRunning = true;
                }
                if (firstVisibleItem == 0) {
                    hidefloatingbutton(false);
                } else {
                    hidefloatingbutton(true);
                }
            }
        });

        reviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Header Item

                } else {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(BloggerDashboardActivity.this, BusinessDetailsActivity.class);
                    if (reviewList.get(position - 1).getType().equals("business")) {

                        bundle.putInt(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(BloggerDashboardActivity.this));
                        bundle.putString(Constants.BUSINESS_OR_EVENT_ID, reviewList.get(position - 1).getReviewId());
                        bundle.putInt(Constants.PAGE_TYPE, 0);
                        bundle.putBoolean("isbusiness", true);
                        bundle.putString(Constants.DISTANCE, "0");
                    } else {
                        bundle.putInt(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(BloggerDashboardActivity.this));
                        bundle.putString(Constants.BUSINESS_OR_EVENT_ID, reviewList.get(position - 1).getReviewId());
                        bundle.putInt(Constants.PAGE_TYPE, 1);
                        bundle.putBoolean("isbusiness", false);
                        bundle.putString(Constants.DISTANCE, "0");
                    }
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private void hitReviewApi() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI getReviewList = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }

        int from = (nextPageNumberReview - 1) * limit + 1;
        Call<ReviewResponse> call = getReviewList.getUserReview(AppConstants.LIVE_URL + "apiservices/getUserReviews?userId=" + userId + "&start=" + from + "&end=" + (from + limit - 1));
//asynchronous call
        call.enqueue(new Callback<ReviewResponse>() {
                         @Override
                         public void onResponse(Call<ReviewResponse> call, retrofit2.Response<ReviewResponse> response) {
                             int statusCode = response.code();
                             removeProgressDialog();
                             if (mLodingView.getVisibility() == View.VISIBLE) {
                                 mLodingView.setVisibility(View.GONE);
                             }
                             if (response == null || response.body() == null) {
                                 showToast(getString(R.string.went_wrong));
                                 return;
                             }
                             ReviewResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                     Log.i("Draft message", responseModel.getData().getMsg());
                                 }
                                 processReviewsResponse(responseModel);
                             }
                         }

                         @Override
                         public void onFailure(Call<ReviewResponse> call, Throwable t) {
                             if (mLodingView.getVisibility() == View.VISIBLE) {
                                 mLodingView.setVisibility(View.GONE);
                             }
                             removeProgressDialog();
                             Crashlytics.logException(t);
                             Log.d("MC4kException", Log.getStackTraceString(t));
                         }
                     }
        );
    }

    private void hitCommentsApi() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI getCommentsAPI = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<UserCommentsResponse> call = getCommentsAPI.getUserComments(AppConstants.LIVE_URL + "v1/comments/" + userId + "?limit=" + limit + "&pagination=" + paginationValue);
//asynchronous call
        call.enqueue(new Callback<UserCommentsResponse>() {
                         @Override
                         public void onResponse(Call<UserCommentsResponse> call, retrofit2.Response<UserCommentsResponse> response) {
                             int statusCode = response.code();
                             removeProgressDialog();
                             if (mLodingView.getVisibility() == View.VISIBLE) {
                                 mLodingView.setVisibility(View.GONE);
                             }
                             if (response == null || response.body() == null) {
                                 showToast(getString(R.string.went_wrong));
                                 return;
                             }
                             UserCommentsResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                     Log.i("Draft message", responseModel.getData().getMsg());
                                 }
                                 processCommentsResponse(responseModel);
                                 isReuqestCommentsRunning = false;
                             }
                         }

                         @Override
                         public void onFailure(Call<UserCommentsResponse> call, Throwable t) {
                             if (mLodingView.getVisibility() == View.VISIBLE) {
                                 mLodingView.setVisibility(View.GONE);
                             }
                             removeProgressDialog();
                             Crashlytics.logException(t);
                             Log.d("MC4kException", Log.getStackTraceString(t));
                         }
                     }
        );
    }

    private void hitPublishedArticleApi() {
        if (nextPageNumber == 1 && null != progressBar) {
            progressBar.setVisibility(View.VISIBLE);
        }
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI getPublishedArticles = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        int from = (nextPageNumber - 1) * 15 + 1;
        Call<ArticleListingResponse> call = getPublishedArticles.getPublishedArticles(AppConstants.LIVE_URL + "v1/articles/user/" + userId + "?sort=0&start=" + from + "&end=" + (from + 14));
//asynchronous call
        call.enqueue(new Callback<ArticleListingResponse>() {
                         @Override
                         public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
                             int statusCode = response.code();
                             removeProgressDialog();
                             isReuqestRunning = false;
                             progressBar.setVisibility(View.INVISIBLE);
                             if (mLodingView.getVisibility() == View.VISIBLE) {
                                 mLodingView.setVisibility(View.GONE);
                             }
                             if (response == null || response.body() == null) {
                                 showToast(getString(R.string.went_wrong));
                                 return;
                             }
                             ArticleListingResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().get(0).getMsg())) {
                                     Log.i("Draft message", responseModel.getData().get(0).getMsg());
                                 }
                                 processPublisedArticlesResponse(responseModel);

                             }
                         }

                         @Override
                         public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
                             if (mLodingView.getVisibility() == View.VISIBLE) {
                                 mLodingView.setVisibility(View.GONE);
                             }
                             removeProgressDialog();
                             Crashlytics.logException(t);
                             Log.d("MC4kException", Log.getStackTraceString(t));
                         }
                     }
        );
    }

    private void hitDraftListingApi() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticleDraftAPI getDraftListAPI = retrofit.create(ArticleDraftAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }

        Call<ResponseBody> call = getDraftListAPI.getDraftsList("0,1,2,4");
        //asynchronous call
        call.enqueue(new Callback<ResponseBody>() {
                         @Override
                         public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                             int statusCode = response.code();
                             removeProgressDialog();
                             if (response == null || response.body() == null) {
                                 showToast(getString(R.string.went_wrong));
                                 return;
                             }
                             try {
                                 String resData = new String(response.body().bytes());
                                 JSONObject jObject = new JSONObject(resData);
                                 int code = jObject.getInt("code");
                                 String status = jObject.getString("status");
                                 if (code == 200 && Constants.SUCCESS.equals(status)) {
                                     DraftListResponse draftListResponse = new DraftListResponse();
                                     DraftListData draftListData = new DraftListData();
                                     JSONArray dataObj = jObject.optJSONArray("data");
                                     if (null != dataObj) {
                                         //Empty Draft List Handling
                                         ArrayList<DraftListResult> emptyDraftList = new ArrayList<DraftListResult>();
                                         draftListData.setResult(emptyDraftList);
                                         draftListResponse.setData(draftListData);
                                         processDraftResponse(draftListResponse);
                                         return;
                                     }

                                     JSONArray resultJsonObject = jObject.getJSONObject("data").optJSONArray("result");
                                     ArrayList<DraftListResult> draftList = new ArrayList<DraftListResult>();
                                     ArrayList<Map<String, String>> retMap;
                                     for (int i = 0; i < resultJsonObject.length(); i++) {
                                         DraftListResult draftitem = new DraftListResult();
                                         draftitem.setId(resultJsonObject.getJSONObject(i).getString("id"));
                                         draftitem.setArticleType(resultJsonObject.getJSONObject(i).getString("articleType"));
                                         draftitem.setCreatedTime(resultJsonObject.getJSONObject(i).getString("createdTime"));
                                         draftitem.setUpdatedTime(resultJsonObject.getJSONObject(i).getLong("updatedTime"));
                                         draftitem.setBody(resultJsonObject.getJSONObject(i).getString("body"));
                                         draftitem.setTitle(resultJsonObject.getJSONObject(i).getString("title"));
                                         if (resultJsonObject.getJSONObject(i).has("itemType")) {
                                             draftitem.setItemType(resultJsonObject.getJSONObject(i).getInt("itemType"));
                                         }
                                         //Different formats of tags array Handling :(
                                         if (resultJsonObject.getJSONObject(i).has("tags")) {
                                             JSONArray tagsArray = resultJsonObject.getJSONObject(i).optJSONArray("tags");
                                             if (null != tagsArray) {
                                                 retMap = new Gson().fromJson(tagsArray.toString(), new TypeToken<ArrayList<HashMap<String, String>>>() {
                                                 }.getType());
                                                 draftitem.setTags(retMap);
                                             } else {
                                                 JSONArray jsArray = resultJsonObject.getJSONObject(i).getJSONObject("tags").optJSONArray("tagsArr");
                                                 retMap = new Gson().fromJson(jsArray.toString(), new TypeToken<ArrayList<HashMap<String, String>>>() {
                                                 }.getType());
                                                 draftitem.setTags(retMap);
                                             }
                                         } else {
                                             // no tags key in the json
                                             retMap = new ArrayList<Map<String, String>>();
                                             draftitem.setTags(retMap);
                                         }
                                         draftList.add(draftitem);
                                     }
                                     draftListData.setResult(draftList);
                                     draftListResponse.setData(draftListData);
                                     processDraftResponse(draftListResponse);
                                 } else {
                                     showToast(jObject.getString("reason"));
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
                             removeProgressDialog();
                             Crashlytics.logException(t);
                             Log.d("MC4kException", Log.getStackTraceString(t));
                         }
                     }
        );
    }

    public void deleteDraftAPI(DraftListResult draftObject, int p) {
        position = p;
        showProgressDialog(getResources().getString(R.string.please_wait));
        ArticleDraftRequest articleDraftRequest = new ArticleDraftRequest();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticleDraftAPI articleDraftAPI = retrofit.create(ArticleDraftAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            showToast(getString(R.string.error_network));
            return;
        }

        Call<ArticleDraftResponse> call = articleDraftAPI.deleteDraft(
                AppConstants.LIVE_URL + "v1/articles/" + draftObject.getId());

        call.enqueue(new Callback<ArticleDraftResponse>() {
                         @Override
                         public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
                             removeProgressDialog();
                             if (response == null || response.body() == null) {
                                 showToast(getString(R.string.went_wrong));
                                 return;
                             }
                             ArticleDraftResponse responseModel = (ArticleDraftResponse) response.body();

                             if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                                 draftList.remove(position);
                                 adapter.notifyDataSetChanged();
                             } else {
                                 if (StringUtils.isNullOrEmpty(responseModel.getReason())) {
                                     showToast(getString(R.string.toast_response_error));
                                 } else {
                                     showToast(responseModel.getReason());
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {
                             Crashlytics.logException(t);
                             Log.d("MC4kException", Log.getStackTraceString(t));
                         }
                     }
        );
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.unfollowTextView:
            case R.id.followTextView:
                hitFollowUnfollowAPI();
                break;
            case R.id.analyticsTextView:
                Intent intent = new Intent(BloggerDashboardActivity.this, BloggersAnalyticsActivity.class);
                startActivity(intent);
                break;
            default:
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        showPopupMenu(v);
                    }
                });
                break;
        }
    }

    private void hitFollowUnfollowAPI() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(userId);

        if (isFollowing) {
            isFollowing = false;
            followButton.setVisibility(View.VISIBLE);
            unfollowButton.setVisibility(View.INVISIBLE);
            int followerCount = Integer.parseInt(followersTextView.getText().toString()) - 1;
            followersTextView.setText("" + followerCount);
            Utils.pushAuthorFollowUnfollowEvent(BloggerDashboardActivity.this, GTMEventType.UNFOLLOW_AUTHOR_CLICK_EVENT, "User Profile", SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getDynamoId(),
                    "", firstName + " " + lastName + "-" + userId);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            followButton.setVisibility(View.INVISIBLE);
            unfollowButton.setVisibility(View.VISIBLE);
            int followerCount = Integer.parseInt(followersTextView.getText().toString()) + 1;
            followersTextView.setText("" + followerCount);
            Utils.pushAuthorFollowUnfollowEvent(BloggerDashboardActivity.this, GTMEventType.FOLLOW_AUTHOR_CLICK_EVENT, "User Profile", SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getDynamoId(),
                    "", firstName + " " + lastName + "-" + userId);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.followUser(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }

    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.pop_menu_draft, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                return true;
            case R.id.delete:
                return true;
            default:
                return false;
        }
    }

    private void processDraftResponse(DraftListResponse responseModel) {
        draftList = responseModel.getData().getResult();

        if (draftList.size() == 0 && draftListview.getVisibility() == View.VISIBLE) {
            noDraftTextView.setVisibility(View.VISIBLE);
        } else {
            noDraftTextView.setVisibility(View.GONE);
            adapter = new DraftListAdapter(this, draftList);
            draftListview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    private void processPublisedArticlesResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {

            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results for search
                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                publishedArticleListView.setAdapter(articlesListingAdapter);
                articlesListingAdapter.setListData(dataList);
                articlesListingAdapter.notifyDataSetChanged();
                if (publishedArticleListView.getVisibility() == View.VISIBLE) {
                    noArticleTextView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                publishedArticleListView.setAdapter(articlesListingAdapter);
                articlesListingAdapter.notifyDataSetChanged();
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            articlesListingAdapter.setListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }

    }

    private void processCommentsResponse(UserCommentsResponse responseModel) {
        ArrayList<UserCommentsResult> dataCommentList = responseModel.getData().getResult();

        if (dataCommentList.size() == 0 && commentsListView.getVisibility() == View.VISIBLE) {
            isLastPageCommentsReached = true;
            //  noDrafts.setVisibility(View.VISIBLE);
            if (null != commentList && !commentList.isEmpty()) {
                //No more next results for search from pagination

            } else {
                // No results for search
                commentList.clear();
                commentList.addAll(dataCommentList);
                commentsListAdapter.notifyDataSetChanged();
                noCommentsTextView.setVisibility(View.VISIBLE);
            }
        } else {
            if (StringUtils.isNullOrEmpty(paginationValue)) {
                commentList.clear();
                commentList.addAll(dataCommentList);
            } else {
                commentList.addAll(dataCommentList);
            }
            commentsListAdapter.notifyDataSetChanged();
            if (null != responseModel.getData().getPagination()) {
                paginationValue = responseModel.getData().getPagination();
            }
            if (AppConstants.PAGINATION_END_VALUE.equals(paginationValue)) {
                isLastPageCommentsReached = true;
            }
        }

    }

    private void processReviewsResponse(ReviewResponse responseModel) {
        ArrayList<ReviewListingResult> dataReviewList = responseModel.getData().getResult();
        if (dataReviewList.size() == 0) {
            if (reviewList.size() == 0 && reviewsListView.getVisibility() == View.VISIBLE) {
                reviewList.clear();
                reviewList.addAll(dataReviewList);
                reviewsListAdapter.notifyDataSetChanged();
                noReviewsTextView.setVisibility(View.VISIBLE);
                //   noDrafts.setVisibility(View.VISIBLE);
            }
        } else {
            noReviewsTextView.setVisibility(View.GONE);
            reviewList.addAll(dataReviewList);
            nextPageNumberReview = nextPageNumberReview + 1;
            reviewsListAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<LanguageRanksModel> arr = new ArrayList<>();

    private void getBloggerDashboardDetails() {

        showProgressDialog("please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }

        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(userId);
        //asynchronous call
        call.enqueue(new Callback<UserDetailResponse>() {
                         @Override
                         public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
                             removeProgressDialog();
                             UserDetailResponse responseData = (UserDetailResponse) response.body();
                             if (response == null || null == response.body()) {
                                 showToast("Something went wrong from server");
//                                 Crashlytics.log(Log.ERROR, "NULL", "blogger dashboard");
                                 return;
                             }
                             if (responseData.getCode() != 200 || Constants.FAILURE.equals(responseData.getStatus())) {
                                 showToast(responseData.getReason());
                                 return;
                             } else {
                                 if (responseData.getData().get(0).getResult().getRanks() == null || responseData.getData().get(0).getResult().getRanks().size() == 0) {
                                     LanguageRanksModel languageRanksModel = new LanguageRanksModel();
                                     languageRanksModel.setRank(-1);
                                     languageRanksModel.setLangKey("");
                                     addRankView(languageRanksModel);
                                     rankViewFlipper.setAutoStart(false);
                                     rankViewFlipper.stopFlipping();
                                 } else if (responseData.getData().get(0).getResult().getRanks().size() < 2) {
                                     addRankView(responseData.getData().get(0).getResult().getRanks().get(0));
                                     rankViewFlipper.setAutoStart(false);
                                     rankViewFlipper.stopFlipping();
                                 } else {
                                     for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                                         if (AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
                                             addRankView(responseData.getData().get(0).getResult().getRanks().get(i));
                                             arr.add(responseData.getData().get(0).getResult().getRanks().get(i));
                                         }
                                     }
                                     Collections.sort(responseData.getData().get(0).getResult().getRanks());
                                     for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                                         if (!AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
                                             addRankView(responseData.getData().get(0).getResult().getRanks().get(i));
                                             arr.add(responseData.getData().get(0).getResult().getRanks().get(i));
                                         }
                                     }
                                     int imagesToShow[] = {R.drawable.default_article, R.drawable.default_blogger, R.drawable.default_commentor_img};
                                     animate(null, imagesToShow, 0, true);
                                 }

                                 int followerCount = Integer.parseInt(responseData.getData().get(0).getResult().getFollowersCount());
                                 if (followerCount > 999) {
                                     float singleFollowerCount = ((float) followerCount) / 1000;
                                     followersTextView.setText("" + singleFollowerCount + "k");
                                 } else {
                                     followersTextView.setText("" + followerCount);
                                 }

                                 int followingCount = Integer.parseInt(responseData.getData().get(0).getResult().getFollowingCount());
                                 if (followingCount > 999) {
                                     float singleFollowingCount = ((float) followingCount) / 1000;
                                     followingTextView.setText("" + singleFollowingCount + "k");
                                 } else {
                                     followingTextView.setText("" + followingCount);
                                 }

                                 blogTitle.setText(responseData.getData().get(0).getResult().getBlogTitle());
                                 getSupportActionBar().setTitle(responseData.getData().get(0).getResult().getFirstName());
                                 Bio = responseData.getData().get(0).getResult().getUserBio();
                                 firstName = responseData.getData().get(0).getResult().getFirstName();
                                 lastName = responseData.getData().get(0).getResult().getLastName();
                                 if (isPrivateProfile && AppConstants.USER_TYPE_BLOGGER.equals(responseData.getData().get(0).getResult().getUserType())) {
                                     analyticsTextView.setVisibility(View.VISIBLE);
                                     analyticsTextView.setOnClickListener(BloggerDashboardActivity.this);
                                 } else if (null != analyticsTextView) {
                                     analyticsTextView.setVisibility(View.GONE);
                                 }
                                 if (null == responseData.getData().get(0).getResult().getPhone()) {
                                     phoneNumber = " ";
                                 } else {
                                     phoneNumber = responseData.getData().get(0).getResult().getPhone().getMobile();
                                 }

                                 if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())) {
                                     if (isPrivateProfile) {
                                         SharedPrefUtils.setProfileImgUrl(BloggerDashboardActivity.this, responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                                     }
                                     Picasso.with(BloggerDashboardActivity.this).load(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())
                                             .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(bloggerImageView);
                                 }

                                 if (responseData.getData().get(0).getResult().getUserBio() == null || responseData.getData().get(0).getResult().getUserBio().isEmpty()) {
                                     userBio.setVisibility(View.GONE);
//                                     moreTextView.setVisibility(View.GONE);
                                 } else {
                                     userBio.setText(responseData.getData().get(0).getResult().getUserBio());
                                     userBio.setVisibility(View.VISIBLE);
                                 }
                                 if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                                     //token already expired or yet to connect using facebook
                                     SharedPrefUtils.setFacebookConnectedFlag(BloggerDashboardActivity.this, "1");
                                 } else {
                                     SharedPrefUtils.setFacebookConnectedFlag(BloggerDashboardActivity.this,
                                             responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                                 }

                             }
                         }

                         @Override
                         public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                             removeProgressDialog();
                             showToast(getString(R.string.server_went_wrong));
                             Crashlytics.logException(t);
                             Log.d("MC4kException", Log.getStackTraceString(t));
                         }
                     }
        );
    }

    private void addRankView(LanguageRanksModel languageRanksModel) {
        View rankItem = getLayoutInflater().inflate(R.layout.rank_flipping_item, null);
        TextView rankTextView = (TextView) rankItem.findViewById(R.id.rankingTextView);
        TextView rankLabelTextView = (TextView) rankItem.findViewById(R.id.rankingLabelTextView);
        if (languageRanksModel.getRank() == -1) {
            rankTextView.setText("--");
            rankLabelTextView.setText("Rank");
        } else {
            rankTextView.setText("" + languageRanksModel.getRank());
            if (AppConstants.LANG_KEY_ENGLISH.equals(languageRanksModel.getLangKey())) {
                rankLabelTextView.setText("Rank in English");
            } else {
                rankLabelTextView.setText("Rank in " + AppUtils.getLangModelForLanguage(BloggerDashboardActivity.this, languageRanksModel.getLangKey()).getDisplay_name());
            }

        }
        rankViewFlipper.addView(rankItem);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ADD_MEDIA_ACTIVITY_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                imageUri = data.getData();

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        startCropActivity(imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE:

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        startCropActivity(Uri.parse(mCurrentPhotoPath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UCrop.REQUEST_CROP: {
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    Log.e("resultUri", resultUri.toString());
                    File file2 = FileUtils.getFile(this, resultUri);
                    sendUploadProfileImageRequest(file2);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "0");
        // prepare call in Retrofit 2.0
        ImageUploadAPI imageUploadAPI = retro.create(ImageUploadAPI.class);

        Call<ImageUploadResponse> call = imageUploadAPI.uploadImage(//userId,
                imageType,
                requestBodyFile);
        //asynchronous call
        call.enqueue(new Callback<ImageUploadResponse>() {
                         @Override
                         public void onResponse(Call<ImageUploadResponse> call, retrofit2.Response<ImageUploadResponse> response) {
                             int statusCode = response.code();
                             ImageUploadResponse responseModel = response.body();

                             removeProgressDialog();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                     Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getResult().getUrl());
                                 }
                                 setProfileImage(responseModel.getData().getResult().getUrl());
                                 Picasso.with(BloggerDashboardActivity.this).invalidate(SharedPrefUtils.getProfileImgUrl(BloggerDashboardActivity.this));
                                 Picasso.with(BloggerDashboardActivity.this).load(responseModel.getData().getResult().getUrl())
                                         .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.family_xxhdpi)
                                         .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(bloggerImageView);
                                 SharedPrefUtils.setProfileImgUrl(BloggerDashboardActivity.this, responseModel.getData().getResult().getUrl());

                                 showToast("Image successfully uploaded!");
                                 // ((BaseActivity) this()).showSnackbar(getView().findViewById(R.id.root), "You have successfully uploaded an image.");
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                             showToast("unable to upload image, please try again later");
                             Crashlytics.logException(t);
                             Log.d("MC4kException", Log.getStackTraceString(t));
                         }
                     }
        );

    }

    public void setProfileImage(String url) {
        UpdateUserDetailsRequest updateUserDetail = new UpdateUserDetailsRequest();
        updateUserDetail.setAttributeName("profilePicUrl");
        updateUserDetail.setAttributeValue(url);
        updateUserDetail.setAttributeType("S");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateProfilePic(updateUserDetail);
        call.enqueue(new Callback<UserDetailResponse>() {
            @Override
            public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
                if (!response.body().getStatus().equals("success")) {
                    showToast(getString(R.string.toast_response_error));
                }
            }

            @Override
            public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                Crashlytics.logException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isPrivateProfile) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.user_profile_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.bookmarkListItem:
                //  newGame();
                Intent intent = new Intent(BloggerDashboardActivity.this, BookmarkListActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings:

                // showHelp();
                Intent intent1 = new Intent(BloggerDashboardActivity.this, SettingsActivity.class);
                intent1.putExtra("load_fragment", Constants.SETTINGS_FRAGMENT);
//                intent1.putExtra("bio", Bio);
//                intent1.putExtra("firstName", firstName);
//                intent1.putExtra("lastName", lastName);
//                intent1.putExtra("phoneNumber", phoneNumber);
                startActivity(intent1);

                return true;
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (stackClearRequired) {
            Intent intent = new Intent(BloggerDashboardActivity.this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";
        Log.e("instartCropActivity", "test");

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(300, 300);
        uCrop.start(BloggerDashboardActivity.this);
    }

    private Callback<ArticleDetailResponse> isFollowedResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }

            ArticleDetailResponse responseData = (ArticleDetailResponse) response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                if (SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getDynamoId().equals(userId)) {
//                    followButton.setVisibility(View.INVISIBLE);
//                    unfollowButton.setVisibility(View.INVISIBLE);
                } else {
                    followButton.setEnabled(true);
                    unfollowButton.setEnabled(true);
                    if ("0".equals(responseData.getData().getResult().getIsFollowed())) {
                        followButton.setVisibility(View.VISIBLE);
                        unfollowButton.setVisibility(View.INVISIBLE);
                        isFollowing = false;
                    } else {
                        followButton.setVisibility(View.INVISIBLE);
                        unfollowButton.setVisibility(View.VISIBLE);
                        isFollowing = true;
                    }
                }
            } else {
                showToast(getString(R.string.server_went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
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
    };

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
                    followButton.setVisibility(View.VISIBLE);
                    unfollowButton.setVisibility(View.INVISIBLE);
                    isFollowing = false;
                    int followerCount = Integer.parseInt(followersTextView.getText().toString()) - 1;
                    followersTextView.setText("" + followerCount);
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
                    followButton.setVisibility(View.INVISIBLE);
                    unfollowButton.setVisibility(View.VISIBLE);
                    int followerCount = Integer.parseInt(followersTextView.getText().toString()) + 1;
                    followersTextView.setText("" + followerCount);
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

    Callback<ArticleDetailResult> articleDetailResponseCallback = new Callback<ArticleDetailResult>() {
        @Override
        public void onResponse(Call<ArticleDetailResult> call, retrofit2.Response<ArticleDetailResult> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }
            try {
                ArticleDetailResult responseData = (ArticleDetailResult) response.body();
                getResponseUpdateUi(responseData);
            } catch (Exception e) {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailResult> call, Throwable t) {
            removeProgressDialog();
            if (t instanceof UnknownHostException) {
                showToast(getString(R.string.error_network));
            } else if (t instanceof SocketTimeoutException) {
                showToast("connection timed out");
            } else {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        }
    };

    private void getResponseUpdateUi(ArticleDetailResult detailsResponse) {
        ArticleDetailResult detailData = detailsResponse;
        ArrayList<ImageData> imageList = detailData.getBody().getImage();

        String bodyDescription = detailData.getBody().getText();
        String bodyDesc = bodyDescription;
        String content;
        if (imageList.size() > 0) {
            for (ImageData images : imageList) {
                if (bodyDescription.contains(images.getKey())) {
                    bodyDesc = bodyDesc.replace(images.getKey(), "<p style='text-align:center'><img src=" + images.getValue() + " style=\"width: 100%;\"+></p>");
                }
            }

            String bodyImgTxt = "<html><head></head><body>" + bodyDesc + "</body></html>";
            content = bodyImgTxt;

        } else {
            String bodyImgTxt = "<html><head></head><body>" + bodyDesc + "</body></html>";
            content = bodyImgTxt;
        }

        Intent intent = new Intent(this, EditorPostActivity.class);
        intent.putExtra("from", "publishedList");
        intent.putExtra("title", detailData.getTitle());
        intent.putExtra("content", content);
        intent.putExtra("thumbnailUrl", detailData.getImageUrl().getClientApp());
        intent.putExtra("articleId", detailData.getId());
        intent.putExtra("tag", new Gson().toJson(detailData.getTags()));
        intent.putExtra("cities", new Gson().toJson(detailData.getCities()));
        startActivity(intent);
    }

    public void chooseImageOptionPopUp(ImageView profileImageView) {
        final PopupMenu popup = new PopupMenu(this, profileImageView);
        popup.getMenuInflater().inflate(R.menu.profile_image_upload_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.camera) {
//                    mClickListener.onBtnClick(position);
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            Log.i("TAG", "IOException");
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            startActivityForResult(cameraIntent, ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE);
                        }
                    }
                    return true;
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                    return true;
                }
            }

        });
        popup.show();
    }

    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestCameraPermission() {
        Log.i("Permissions", "CAMERA permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i("Permissions",
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(BloggerDashboardActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
        // END_INCLUDE(camera_permission_request)
    }

    /**
     * Requests the Storage permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestCameraAndStoragePermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i("Permissions",
                    "Displaying stoage permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(BloggerDashboardActivity.this, PERMISSIONS_EDIT_PICTURE,
                                            REQUEST_EDIT_PICTURE);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_EDIT_PICTURE, REQUEST_EDIT_PICTURE);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i("Permissions", "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i("Permissions", "CAMERA permission has now been granted. Showing preview.");
                Snackbar.make(mLayout, R.string.permision_available_camera,
                        Snackbar.LENGTH_SHORT).show();
                chooseImageOptionPopUp(bloggerImageView);
            } else {
                Log.i("Permissions", "CAMERA permission was NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)

        } else if (requestCode == REQUEST_EDIT_PICTURE) {
            Log.i("Permissions", "Received response for storage permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_storage,
                        Snackbar.LENGTH_SHORT)
                        .show();
                chooseImageOptionPopUp(bloggerImageView);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                dir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        absoluteImagePath = image.getAbsolutePath();
        return image;
    }

    public void hidefloatingbutton(Boolean b) {
        if (b == true) {
            addDraft.setVisibility(View.INVISIBLE);
        } else {
            if (isPrivateProfile) {
                addDraft.setVisibility(View.VISIBLE);
            } else {
                addDraft.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void updateUi(Response response) {

    }
}