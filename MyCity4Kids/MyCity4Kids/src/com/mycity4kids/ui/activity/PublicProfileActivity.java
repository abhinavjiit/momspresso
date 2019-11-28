package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.LanguageRanksModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.ui.activity.collection.CollectionsActivity;
import com.mycity4kids.ui.fragment.UserBioDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 13/9/18.
 */

public class PublicProfileActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<LanguageRanksModel> multipleRankList = new ArrayList<>();
    private Boolean isFollowing = false;

    private ImageView imgProfile;
    private LinearLayout followerContainer, followingContainer, rankContainer;
    private TextView followingCountTextView, followerCountTextView, rankCountTextView;
    private TextView rankLanguageTextView;
    private TextView authorNameTextView, authorBioTextView;
    private TextView publishedSectionTextView, activitySectionTextView;
    private ImageView backArrowImageView;
    private String authorId;
    private TextView followAuthorTextView;
    private boolean isRequestRunning = false;
    private TextView authorTypeTextView;
    private ScrollView root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_profile_activity);
        root = findViewById(R.id.rootView);
        ((BaseApplication) getApplication()).setActivity(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        backArrowImageView = (ImageView) findViewById(R.id.menuImageView);
        authorNameTextView = (TextView) findViewById(R.id.nameTextView);
        followAuthorTextView = (TextView) findViewById(R.id.followAuthorTextView);
        authorBioTextView = (TextView) findViewById(R.id.userbioTextView);
        authorTypeTextView = (TextView) findViewById(R.id.authorTypeTextView);
        followingCountTextView = (TextView) findViewById(R.id.followingCountTextView);
        followerCountTextView = (TextView) findViewById(R.id.followerCountTextView);
        rankCountTextView = (TextView) findViewById(R.id.rankCountTextView);
        rankLanguageTextView = (TextView) findViewById(R.id.rankLanguageTextView);
        publishedSectionTextView = (TextView) findViewById(R.id.publishedSectionTextView);
        activitySectionTextView = (TextView) findViewById(R.id.activitySectionTextView);
        imgProfile = (ImageView) findViewById(R.id.profileImageView);
        followerContainer = (LinearLayout) findViewById(R.id.followerContainer);
        followingContainer = (LinearLayout) findViewById(R.id.followingContainer);
        rankContainer = (LinearLayout) findViewById(R.id.rankContainer);

        authorNameTextView.setOnClickListener(this);
        authorBioTextView.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        publishedSectionTextView.setOnClickListener(this);
        activitySectionTextView.setOnClickListener(this);
        rankContainer.setOnClickListener(this);
        followingContainer.setOnClickListener(this);
        followerContainer.setOnClickListener(this);
        backArrowImageView.setOnClickListener(this);
        followAuthorTextView.setOnClickListener(this);

//        userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        authorId = getIntent().getStringExtra(AppConstants.PUBLIC_PROFILE_USER_ID);

        getUserDetails();
        checkFollowingStatusAPI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getUserDetails();
//            }
//        }, 200);
    }

    private void getUserDetails() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            return;
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(authorId);
        call.enqueue(userDetailsResponseListener);
    }

    private Callback<UserDetailResponse> userDetailsResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                return;
            }
            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (responseData.getData().get(0).getResult().getRanks() == null || responseData.getData().get(0).getResult().getRanks().size() == 0) {
                        rankCountTextView.setText("--");
                        rankLanguageTextView.setText(getString(R.string.myprofile_rank_label));
                    } else if (responseData.getData().get(0).getResult().getRanks().size() < 2) {
                        rankCountTextView.setText("" + responseData.getData().get(0).getResult().getRanks().get(0).getRank());
                        if (AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(0).getLangKey())) {
                            rankLanguageTextView.setText(getString(R.string.blogger_profile_rank_in) + " ENGLISH");
                        } else {
                            rankLanguageTextView.setText(getString(R.string.blogger_profile_rank_in)
                                    + " " + responseData.getData().get(0).getResult().getRanks().get(0).getLangValue().toUpperCase());
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
                        MyCityAnimationsUtil.animate(PublicProfileActivity.this, rankContainer, multipleRankList, 0, true);
                    }

                    long totalArticleViewsCount = Long.parseLong(responseData.getData().get(0).getResult().getTotalArticlesViews());
                    followerCountTextView.setText(AppUtils.withSuffix(totalArticleViewsCount));
                    int totalArticlesCount = Integer.parseInt(responseData.getData().get(0).getResult().getTotalArticles());
                    followingCountTextView.setText(AppUtils.withSuffix(totalArticlesCount));

                    authorNameTextView.setText(responseData.getData().get(0).getResult().getFirstName() + " " + responseData.getData().get(0).getResult().getLastName());
                    switch (responseData.getData().get(0).getResult().getUserType()) {
                        case AppConstants.USER_TYPE_BLOGGER:
                            authorTypeTextView.setText(getString(R.string.author_type_blogger));
                            break;
                        case AppConstants.USER_TYPE_EDITOR:
                            authorTypeTextView.setText(getString(R.string.author_type_editor));
                            break;
                        case AppConstants.USER_TYPE_EDITORIAL:
                            authorTypeTextView.setText(getString(R.string.author_type_editorial));
                            break;
                        case AppConstants.USER_TYPE_EXPERT:
                            authorTypeTextView.setText(getString(R.string.author_type_expert));
                            break;
                        case AppConstants.USER_TYPE_FEATURED:
                            authorTypeTextView.setText(getString(R.string.author_type_featured));
                            break;
                        case AppConstants.USER_TYPE_USER:
                            authorTypeTextView.setText(getString(R.string.author_type_user));
                            break;
                        case AppConstants.USER_TYPE_COLLABORATION:
                            authorTypeTextView.setText(getString(R.string.author_type_collaboration));
//                            if (AppConstants.DEBUGGING_USER_ID.contains(userId)) {
//                                rankContainer.setOnClickListener(PublicProfileActivity.this);
//                            } else {
//                                rankContainer.setOnClickListener(null);
//                            }
                            break;
                        default:
                    }
                    if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())) {
                        Picasso.with(PublicProfileActivity.this).load(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())
                                .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).into(imgProfile);
                    }

                    if (responseData.getData().get(0).getResult().getUserBio() == null || responseData.getData().get(0).getResult().getUserBio().isEmpty()) {
                        authorBioTextView.setVisibility(View.GONE);
                    } else {
                        authorBioTextView.setText(responseData.getData().get(0).getResult().getUserBio());
                        authorBioTextView.setVisibility(View.VISIBLE);
                        makeTextViewResizable(authorBioTextView, 2, "See More", true, responseData.getData().get(0).getResult().getUserBio());
                    }
                    if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                        //token already expired or yet to connect using facebook
                        SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(), "1");
                    } else {
                        SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(),
                                responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                    }
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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

    private Callback<ArticleDetailResponse> isFollowedResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            if (response == null || null == response.body()) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }

            ArticleDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if ("0".equals(responseData.getData().getResult().getIsFollowed())) {
                    followAuthorTextView.setText(R.string.ad_follow_author);
                    isFollowing = false;
                } else {
                    followAuthorTextView.setText(R.string.ad_following_author);
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
                showToast(getString(R.string.connection_timeout));
            } else {
                showToast(getString(R.string.server_went_wrong));
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menuImageView:
                onBackPressed();
                break;
            case R.id.authorNameTextView: {
                Intent intent = new Intent(this, PublicProfileActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.followAuthorTextView:
                if (!isRequestRunning) {
                    isRequestRunning = true;
                    hitFollowUnfollowAPI();
                }
                break;
            case R.id.publishedSectionTextView:
                Intent articleIntent = new Intent(this, UserPublishedContentActivity.class);
                articleIntent.putExtra(Constants.AUTHOR_ID, authorId);
                startActivity(articleIntent);
                break;
            case R.id.activitySectionTextView: {

                Intent intent1 = new Intent(this, CollectionsActivity.class);
                intent1.putExtra("userId", authorId);
                startActivity(intent1);
             /*   Intent intent = new Intent(this, UserActivitiesActivity.class);
                intent.putExtra(Constants.AUTHOR_ID, authorId);
                startActivity(intent);*/
            }
            break;
            case R.id.rankContainer:
                break;
            case R.id.followingContainer: {
            }
            break;
            case R.id.followerContainer: {
            }
        }
    }

    private void hitFollowUnfollowAPI() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);

        if (isFollowing) {
            isFollowing = false;
            followAuthorTextView.setText(R.string.ad_follow_author);
//            Utils.pushUnfollowAuthorEvent(this, "PublicProfileScreen", userId, authorId + "~" + authorName);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            followAuthorTextView.setText(R.string.ad_following_author);
//            Utils.pushFollowAuthorEvent(this, "PublicProfileScreen", userId, authorId + "~" + authorName);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.followUser(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }
    }

    Callback<FollowUnfollowUserResponse> followUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            isRequestRunning = false;
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followAuthorTextView.setText(R.string.ad_follow_author);
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
            isRequestRunning = false;
            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    Callback<FollowUnfollowUserResponse> unfollowUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            isRequestRunning = false;
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followAuthorTextView.setText(R.string.ad_following_author);
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
            isRequestRunning = false;
            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore, final String userBio) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore, userBio), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() > maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore, userBio), TextView.BufferType.SPANNABLE);
                } else {
//                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
//                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
//                    tv.setText(text);
//                    tv.setMovementMethod(LinkMovementMethod.getInstance());
//                    tv.setText(
//                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
//                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                     final int maxLine, final String spanableText, final boolean viewMore, final String userBio) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    UserBioDialogFragment userBioDialogFragment = new UserBioDialogFragment();
                    FragmentManager fm = getSupportFragmentManager();
                    Bundle _args = new Bundle();
                    _args.putString("userBio", userBio);
                    userBioDialogFragment.setArguments(_args);
                    userBioDialogFragment.setCancelable(true);
                    userBioDialogFragment.show(fm, "Choose video option");
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    public class MySpannable extends ClickableSpan {

        private boolean isUnderline = true;

        /**
         * Constructor
         */
        public MySpannable(boolean isUnderline) {
            this.isUnderline = isUnderline;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(isUnderline);
            ds.setColor(Color.parseColor("#1b76d3"));
        }

        @Override
        public void onClick(View widget) {


        }
    }

    @Override
    protected void updateUi(Response response) {

    }

}
