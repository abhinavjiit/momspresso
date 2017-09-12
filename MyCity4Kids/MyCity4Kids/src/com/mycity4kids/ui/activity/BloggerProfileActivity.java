package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.animation.MyCityAnimationsUtil;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.LanguageRanksModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 18/7/17.
 */
public class BloggerProfileActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView followingCountTextView, followerCountTextView, rankCountTextView;
    private TextView authorNameTextView, authorTypeTextView, authorBioTextView;
    private ImageView imgProfile;
    private RelativeLayout topArticle_1, topArticle_2, topArticle_3;
    private ImageView imgTopArticle_1, imgTopArticle_2, imgTopArticle_3;
    private TextView txvTopArticle_1, txvTopArticle_2, txvTopArticle_3;
    private TextView articleSectionTextView, videosSectionTextView, activitySectionTextView, rankingSectionTextView;
    private TextView followButton, unfollowButton;
    private TextView rankLanguageTextView;
    private LinearLayout followerContainer, followingContainer, rankContainer;

    private ArrayList<LanguageRanksModel> multipleRankList = new ArrayList<>();
    private Boolean isFollowing = false;
    private String userId;
    private String authorId;
    private TextView toolbarTitle;
    private LinearLayout topArticleContainer;
    private TextView topArticleLabel;
    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blogger_profile_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        authorNameTextView = (TextView) findViewById(R.id.authorNameTextView);
        authorTypeTextView = (TextView) findViewById(R.id.authorTypeTextView);
        authorBioTextView = (TextView) findViewById(R.id.authorBioTextView);
        followingCountTextView = (TextView) findViewById(R.id.followingCountTextView);
        followerCountTextView = (TextView) findViewById(R.id.followerCountTextView);
        rankCountTextView = (TextView) findViewById(R.id.rankCountTextView);
        rankLanguageTextView = (TextView) findViewById(R.id.rankLanguageTextView);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        imgTopArticle_1 = (ImageView) findViewById(R.id.imgTopArticle_1);
        imgTopArticle_2 = (ImageView) findViewById(R.id.imgTopArticle_2);
        imgTopArticle_3 = (ImageView) findViewById(R.id.imgTopArticle_3);
        txvTopArticle_1 = (TextView) findViewById(R.id.txvTopArticle_1);
        txvTopArticle_2 = (TextView) findViewById(R.id.txvTopArticle_2);
        txvTopArticle_3 = (TextView) findViewById(R.id.txvTopArticle_3);
        topArticle_1 = (RelativeLayout) findViewById(R.id.topArticle_1);
        topArticle_2 = (RelativeLayout) findViewById(R.id.topArticle_2);
        topArticle_3 = (RelativeLayout) findViewById(R.id.topArticle_3);
        articleSectionTextView = (TextView) findViewById(R.id.articleSectionTextView);
        videosSectionTextView = (TextView) findViewById(R.id.videosSectionTextView);
        activitySectionTextView = (TextView) findViewById(R.id.activitySectionTextView);
        rankingSectionTextView = (TextView) findViewById(R.id.rankingSectionTextView);
        followButton = (TextView) findViewById(R.id.followTextView);
        unfollowButton = (TextView) findViewById(R.id.unfollowTextView);
        topArticleLabel = (TextView) findViewById(R.id.topArticleLabel);
        topArticleContainer = (LinearLayout) findViewById(R.id.topArticleContainer);
        followerContainer = (LinearLayout) findViewById(R.id.followerContainer);
        followingContainer = (LinearLayout) findViewById(R.id.followingContainer);
        rankContainer = (LinearLayout) findViewById(R.id.rankContainer);

        authorNameTextView.setOnClickListener(this);
        authorTypeTextView.setOnClickListener(this);
        authorBioTextView.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        followButton.setOnClickListener(this);
        unfollowButton.setOnClickListener(this);
        articleSectionTextView.setOnClickListener(this);
        videosSectionTextView.setOnClickListener(this);
        activitySectionTextView.setOnClickListener(this);
        rankingSectionTextView.setOnClickListener(this);
        followingContainer.setOnClickListener(this);
        followerContainer.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        authorId = getIntent().getStringExtra(AppConstants.PUBLIC_PROFILE_USER_ID);

        if (AppConstants.DEBUGGING_USER_ID.equals(userId)) {
            rankingSectionTextView.setVisibility(View.VISIBLE);
            findViewById(R.id.underline_4).setVisibility(View.VISIBLE);
        }

        getUserDetails();
        checkFollowingStatusAPI();
        getTop3ArticleOfAuthor();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getUserDetails() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
//            showToast(getString(R.string.error_network));
            return;
        }
        showProgressDialog("please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(authorId);
        call.enqueue(userDetailsResponseListener);
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
        Call<ArticleDetailResponse> callBookmark = articleDetailsAPI.checkFollowingBookmarkStatus("0", authorId);
        callBookmark.enqueue(isFollowedResponseCallback);
    }

    private void getTop3ArticleOfAuthor() {
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI top3ArticlesAPI = retro.create(BloggerDashboardAPI.class);
        Call<ArticleListingResponse> callBookmark = top3ArticlesAPI.getAuthorsPublishedArticles(authorId, 1, 1, 3);
        callBookmark.enqueue(top3ArticleResponseListener);
    }

    private void hitFollowUnfollowAPI() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);

        if (isFollowing) {
            isFollowing = false;
            followButton.setVisibility(View.VISIBLE);
            unfollowButton.setVisibility(View.INVISIBLE);
//            Utils.pushAuthorFollowUnfollowEvent(BloggerProfileActivity.this, GTMEventType.UNFOLLOW_AUTHOR_CLICK_EVENT, "User Profile", SharedPrefUtils.getUserDetailModel(BloggerProfileActivity.this).getDynamoId(),
//                    "", firstName + " " + lastName + "-" + userId);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            followButton.setVisibility(View.INVISIBLE);
            unfollowButton.setVisibility(View.VISIBLE);
//            Utils.pushAuthorFollowUnfollowEvent(BloggerProfileActivity.this, GTMEventType.FOLLOW_AUTHOR_CLICK_EVENT, "User Profile", SharedPrefUtils.getUserDetailModel(BloggerProfileActivity.this).getDynamoId(),
//                    "", firstName + " " + lastName + "-" + userId);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.followUser(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }

    }

    private Callback<UserDetailResponse> userDetailsResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
//                showToast("Something went wrong from server");
                return;
            }
            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (responseData.getData().get(0).getResult().getRanks() == null || responseData.getData().get(0).getResult().getRanks().size() == 0) {
                    rankCountTextView.setText("--");
                    rankLanguageTextView.setText(getString(R.string.blogger_profile_rank_label));
                } else if (responseData.getData().get(0).getResult().getRanks().size() < 2) {
                    rankCountTextView.setText("" + responseData.getData().get(0).getResult().getRanks().get(0).getRank());
                    if (AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(0).getLangKey())) {
                        rankLanguageTextView.setText(getString(R.string.blogger_profile_rank_in) + " ENGLISH");
                    } else {
                        rankLanguageTextView.setText(getString(R.string.blogger_profile_rank_in)
                                + " " + AppUtils.getLangModelForLanguage(BloggerProfileActivity.this, responseData.getData().get(0).getResult().getRanks().get(0).getLangKey()).getDisplay_name());
                    }
                } else {
                    for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                        if (AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
                            multipleRankList.add(responseData.getData().get(0).getResult().getRanks().get(i));
                            break;
                        }
                    }
                    Collections.sort(responseData.getData().get(0).getResult().getRanks());
                    for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                        if (!AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
                            multipleRankList.add(responseData.getData().get(0).getResult().getRanks().get(i));
                        }
                    }
                    MyCityAnimationsUtil.animate(BloggerProfileActivity.this, rankContainer, multipleRankList, 0, true);
                }

                long totalArticleViewsCount = Long.parseLong(responseData.getData().get(0).getResult().getTotalArticlesViews());
                followerCountTextView.setText(AppUtils.withSuffix(totalArticleViewsCount));

                int totalArticlesCount = Integer.parseInt(responseData.getData().get(0).getResult().getTotalArticles());
                followingCountTextView.setText(AppUtils.withSuffix(totalArticlesCount));

                authorNameTextView.setText(responseData.getData().get(0).getResult().getFirstName() + " " + responseData.getData().get(0).getResult().getLastName());
                toolbarTitle.setText(responseData.getData().get(0).getResult().getFirstName() + " " + responseData.getData().get(0).getResult().getLastName());

                switch (responseData.getData().get(0).getResult().getUserType()) {
                    case AppConstants.USER_TYPE_BLOGGER:
                        authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
                        break;
                    case AppConstants.USER_TYPE_EDITOR:
                        authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITOR.toUpperCase());
                        break;
                    case AppConstants.USER_TYPE_EDITORIAL:
                        authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITORIAL.toUpperCase());
                        break;
                    case AppConstants.USER_TYPE_EXPERT:
                        authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EXPERT.toUpperCase());
                        break;
                    case AppConstants.USER_TYPE_USER:
                        authorTypeTextView.setVisibility(View.GONE);
                        break;
                }
//                blogTitle.setText(responseData.getData().get(0).getResult().getBlogTitle());
//                getSupportActionBar().setTitle(responseData.getData().get(0).getResult().getFirstName());
//                Bio = responseData.getData().get(0).getResult().getUserBio();
//                firstName = responseData.getData().get(0).getResult().getFirstName();
//                lastName = responseData.getData().get(0).getResult().getLastName();
//                if (isPrivateProfile && AppConstants.USER_TYPE_BLOGGER.equals(responseData.getData().get(0).getResult().getUserType())) {
//                    analyticsTextView.setVisibility(View.VISIBLE);
//                    analyticsTextView.setOnClickListener(MyAccountProfileFragment.this);
//                } else if (null != analyticsTextView) {
//                    analyticsTextView.setVisibility(View.GONE);
//                }
//                if (null == responseData.getData().get(0).getResult().getPhone()) {
//                    phoneNumber = " ";
//                } else {
//                    phoneNumber = responseData.getData().get(0).getResult().getPhone().getMobile();
//                }

                if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())) {
                    Picasso.with(BloggerProfileActivity.this).load(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);
                }

                if (responseData.getData().get(0).getResult().getUserBio() == null || responseData.getData().get(0).getResult().getUserBio().isEmpty()) {
                    authorBioTextView.setVisibility(View.GONE);
                } else {
                    authorBioTextView.setText(responseData.getData().get(0).getResult().getUserBio());
                    authorBioTextView.setVisibility(View.VISIBLE);
                }
                if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                    //token already expired or yet to connect using facebook
                    SharedPrefUtils.setFacebookConnectedFlag(BloggerProfileActivity.this, "1");
                } else {
                    SharedPrefUtils.setFacebookConnectedFlag(BloggerProfileActivity.this,
                            responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                }
            } else {
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {

        }
    };

    private Callback<ArticleDetailResponse> isFollowedResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }

            ArticleDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

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
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followButton.setVisibility(View.VISIBLE);
                    unfollowButton.setVisibility(View.INVISIBLE);
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
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followButton.setVisibility(View.INVISIBLE);
                    unfollowButton.setVisibility(View.VISIBLE);
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

    private Callback<ArticleListingResponse> top3ArticleResponseListener = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    final ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
                    if (dataList == null || dataList.size() == 0) {

                    } else if (dataList.size() == 1) {
                        topArticleLabel.setVisibility(View.VISIBLE);
                        topArticleContainer.setVisibility(View.VISIBLE);
                        txvTopArticle_1.setText(dataList.get(0).getTitle());
                        topArticle_2.setVisibility(View.INVISIBLE);
                        topArticle_3.setVisibility(View.INVISIBLE);
                        try {
                            Picasso.with(BloggerProfileActivity.this).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imgTopArticle_1);
                        } catch (Exception e) {
                            imgTopArticle_1.setBackgroundResource(R.drawable.article_default);
                        }
                        topArticle_1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BloggerProfileActivity.this, ArticleDetailsContainerActivity.class);
                                intent.putExtra(Constants.ARTICLE_ID, dataList.get(0).getId());
                                intent.putExtra(Constants.AUTHOR_ID, dataList.get(0).getUserName());
                                intent.putExtra(Constants.BLOG_SLUG, dataList.get(0).getBlogPageSlug());
                                intent.putExtra(Constants.TITLE_SLUG, dataList.get(0).getTitleSlug());
                                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Center");
                                intent.putExtra(Constants.FROM_SCREEN, "Notification Center List");
                                intent.putExtra(Constants.ARTICLE_INDEX, "" + 0);
                                startActivity(intent);
                            }
                        });
                    } else if (dataList.size() == 2) {
                        topArticleLabel.setVisibility(View.VISIBLE);
                        topArticleContainer.setVisibility(View.VISIBLE);
                        txvTopArticle_1.setText(dataList.get(0).getTitle());
                        txvTopArticle_2.setText(dataList.get(1).getTitle());
                        topArticle_3.setVisibility(View.INVISIBLE);
                        try {
                            Picasso.with(BloggerProfileActivity.this).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imgTopArticle_1);
                        } catch (Exception e) {
                            imgTopArticle_1.setBackgroundResource(R.drawable.article_default);
                        }
                        try {
                            Picasso.with(BloggerProfileActivity.this).load(dataList.get(1).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imgTopArticle_2);
                        } catch (Exception e) {
                            imgTopArticle_2.setBackgroundResource(R.drawable.article_default);
                        }
                        topArticle_1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BloggerProfileActivity.this, ArticleDetailsContainerActivity.class);
                                intent.putExtra(Constants.ARTICLE_ID, dataList.get(0).getId());
                                intent.putExtra(Constants.AUTHOR_ID, dataList.get(0).getUserName());
                                intent.putExtra(Constants.BLOG_SLUG, dataList.get(0).getBlogPageSlug());
                                intent.putExtra(Constants.TITLE_SLUG, dataList.get(0).getTitleSlug());
                                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Center");
                                intent.putExtra(Constants.FROM_SCREEN, "Notification Center List");
                                intent.putExtra(Constants.ARTICLE_INDEX, "" + 0);
                                startActivity(intent);
                            }
                        });
                        topArticle_2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BloggerProfileActivity.this, ArticleDetailsContainerActivity.class);
                                intent.putExtra(Constants.ARTICLE_ID, dataList.get(1).getId());
                                intent.putExtra(Constants.AUTHOR_ID, dataList.get(1).getUserName());
                                intent.putExtra(Constants.BLOG_SLUG, dataList.get(1).getBlogPageSlug());
                                intent.putExtra(Constants.TITLE_SLUG, dataList.get(1).getTitleSlug());
                                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Center");
                                intent.putExtra(Constants.FROM_SCREEN, "Notification Center List");
                                intent.putExtra(Constants.ARTICLE_INDEX, "" + 1);
                                startActivity(intent);
                            }
                        });
                    } else {
                        topArticleLabel.setVisibility(View.VISIBLE);
                        topArticleContainer.setVisibility(View.VISIBLE);
                        txvTopArticle_1.setText(dataList.get(0).getTitle());
                        txvTopArticle_2.setText(dataList.get(1).getTitle());
                        txvTopArticle_3.setText(dataList.get(2).getTitle());
                        try {
                            Picasso.with(BloggerProfileActivity.this).load(dataList.get(0).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imgTopArticle_1);
                        } catch (Exception e) {
                            imgTopArticle_1.setBackgroundResource(R.drawable.article_default);
                        }
                        try {
                            Picasso.with(BloggerProfileActivity.this).load(dataList.get(1).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imgTopArticle_2);
                        } catch (Exception e) {
                            imgTopArticle_2.setBackgroundResource(R.drawable.article_default);
                        }
                        try {
                            Picasso.with(BloggerProfileActivity.this).load(dataList.get(2).getImageUrl().getThumbMin()).
                                    placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imgTopArticle_3);
                        } catch (Exception e) {
                            imgTopArticle_3.setBackgroundResource(R.drawable.article_default);
                        }
                        topArticle_1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BloggerProfileActivity.this, ArticleDetailsContainerActivity.class);
                                intent.putExtra(Constants.ARTICLE_ID, dataList.get(0).getId());
                                intent.putExtra(Constants.AUTHOR_ID, dataList.get(0).getUserName());
                                intent.putExtra(Constants.BLOG_SLUG, dataList.get(0).getBlogPageSlug());
                                intent.putExtra(Constants.TITLE_SLUG, dataList.get(0).getTitleSlug());
                                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Center");
                                intent.putExtra(Constants.FROM_SCREEN, "Notification Center List");
                                intent.putExtra(Constants.ARTICLE_INDEX, "" + 0);
                                startActivity(intent);
                            }
                        });
                        topArticle_2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BloggerProfileActivity.this, ArticleDetailsContainerActivity.class);
                                intent.putExtra(Constants.ARTICLE_ID, dataList.get(1).getId());
                                intent.putExtra(Constants.AUTHOR_ID, dataList.get(1).getUserName());
                                intent.putExtra(Constants.BLOG_SLUG, dataList.get(1).getBlogPageSlug());
                                intent.putExtra(Constants.TITLE_SLUG, dataList.get(1).getTitleSlug());
                                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Center");
                                intent.putExtra(Constants.FROM_SCREEN, "Notification Center List");
                                intent.putExtra(Constants.ARTICLE_INDEX, "" + 1);
                                startActivity(intent);
                            }
                        });
                        topArticle_3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BloggerProfileActivity.this, ArticleDetailsContainerActivity.class);
                                intent.putExtra(Constants.ARTICLE_ID, dataList.get(2).getId());
                                intent.putExtra(Constants.AUTHOR_ID, dataList.get(2).getUserName());
                                intent.putExtra(Constants.BLOG_SLUG, dataList.get(2).getBlogPageSlug());
                                intent.putExtra(Constants.TITLE_SLUG, dataList.get(2).getTitleSlug());
                                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Center");
                                intent.putExtra(Constants.FROM_SCREEN, "Notification Center List");
                                intent.putExtra(Constants.ARTICLE_INDEX, "" + 2);
                                startActivity(intent);
                            }
                        });
                    }
                } else {

                }
            } catch (Exception e) {
//                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unfollowTextView:
            case R.id.followTextView:
                hitFollowUnfollowAPI();
            case R.id.authorNameTextView:
                break;
            case R.id.authorTypeTextView:
                break;
            case R.id.authorBioTextView:
                if (isExpanded) {
                    authorBioTextView.setMaxLines(3);
                    authorBioTextView.setEllipsize(TextUtils.TruncateAt.END);
                } else {
                    authorBioTextView.setMaxLines(Integer.MAX_VALUE);
                    authorBioTextView.setEllipsize(null);
                }
                isExpanded = !isExpanded;
                break;
            case R.id.articleSectionTextView:
                Intent articleIntent = new Intent(this, UserPublishedAndDraftsActivity.class);
                articleIntent.putExtra(Constants.AUTHOR_ID, authorId);
                startActivity(articleIntent);
                break;
            case R.id.videosSectionTextView:
                Intent funnyIntent = new Intent(this, MyFunnyVideosListingActivity.class);
                funnyIntent.putExtra(Constants.AUTHOR_ID, authorId);
                funnyIntent.putExtra(Constants.FROM_SCREEN, "Navigation Menu");
                startActivity(funnyIntent);
                break;
            case R.id.activitySectionTextView: {
                Intent intent = new Intent(BloggerProfileActivity.this, UserActivitiesActivity.class);
                intent.putExtra(Constants.AUTHOR_ID, authorId);
                startActivity(intent);
            }
            break;
            case R.id.rankingSectionTextView: {
                Intent intent = new Intent(this, RankingActivity.class);
                intent.putExtra("authorId", authorId);
                startActivity(intent);
            }
            break;
            case R.id.followingContainer: {
//                Intent intent = new Intent(this, FollowersAndFollowingListActivity.class);
//                intent.putExtra(AppConstants.FOLLOW_LIST_TYPE, AppConstants.FOLLOWING_LIST);
//                intent.putExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS, authorId);
//                startActivity(intent);
            }
            break;
            case R.id.followerContainer: {
//                Intent intent = new Intent(this, FollowersAndFollowingListActivity.class);
//                intent.putExtra(AppConstants.FOLLOW_LIST_TYPE, AppConstants.FOLLOWER_LIST);
//                intent.putExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS, authorId);
//                startActivity(intent);
            }
            break;
        }
    }

}
