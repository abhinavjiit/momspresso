package com.mycity4kids.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.googlecode.mp4parser.authoring.tracks.TextTrackImpl;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.animation.MyCityAnimationsUtil;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.LogoutController;
import com.mycity4kids.dbtable.ExternalCalendarTable;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableApiEvents;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableAttendee;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableFile;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableNotes;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.dbtable.TableWhoToRemind;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.logout.LogoutResponse;
import com.mycity4kids.models.response.LanguageRanksModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.fragment.UserBioDialogFragment;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.RoundedTransformation;
import com.mycity4kids.widget.RoundedHorizontalProgressBar;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 13/9/18.
 */

public class PrivateProfileActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int REQUEST_CAMERA = 0;

    private GoogleApiClient mGoogleApiClient;

    private String userId;
    private ArrayList<LanguageRanksModel> multipleRankList = new ArrayList<>();

    private ImageView imgProfile;
    private LinearLayout followerContainer, followingContainer, rankContainer, linearRewardsHeader;
    private TextView followingCountTextView, followerCountTextView, rankCountTextView;
    private TextView rankLanguageTextView;
    private TextView authorNameTextView, authorTypeTextView;
    private TextView authorBioTextView;
    private TextView publishedSectionTextView, draftSectionTextView, activitySectionTextView, signoutSectionTextView;
    private View rootView;
    private ImageView backArrowImageView;
    private TextView updateProfileTextView;
    private RoundedHorizontalProgressBar profileCompletionBar;
    private TextView profilePercentageTextView;
    private ImageView editProfileImageView;
    private TextView editProfileTextView, readArticlesTextView;
    private RelativeLayout menuCoachmark;
    private LinearLayout publishCoachmark1, publishCoachmark2;
    private TextView publishedSectionTextView1, textHeaderUpdate;
    private String isRewardsAdded;
    private RelativeLayout relative_profile_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_profile_activity);
        ((BaseApplication) getApplication()).setActivity(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception e) {

        }

        rootView = findViewById(R.id.rootView);
        menuCoachmark = (RelativeLayout) findViewById(R.id.menuCoachmark);
        updateProfileTextView = (TextView) findViewById(R.id.updateProfileTextView);
        backArrowImageView = (ImageView) findViewById(R.id.menuImageView);
        authorNameTextView = (TextView) findViewById(R.id.nameTextView);
        authorTypeTextView = (TextView) findViewById(R.id.authorTypeTextView);
        authorBioTextView = (TextView) findViewById(R.id.userbioTextView);
        followingCountTextView = (TextView) findViewById(R.id.followingCountTextView);
        followerCountTextView = (TextView) findViewById(R.id.followerCountTextView);
        rankCountTextView = (TextView) findViewById(R.id.rankCountTextView);
        rankLanguageTextView = (TextView) findViewById(R.id.rankLanguageTextView);
        publishedSectionTextView = (TextView) findViewById(R.id.publishedSectionTextView);
        draftSectionTextView = (TextView) findViewById(R.id.draftSectionTextView);
//        bookmarksSectionTextView = (TextView) findViewById(R.id.bookmarksSectionTextView);
        activitySectionTextView = (TextView) findViewById(R.id.activitySectionTextView);
//        rankingSectionTextView = (TextView) findViewById(R.id.rankingSectionTextView);
//        settingsSectionTextView = (TextView) findViewById(R.id.settingsSectionTextView);
        signoutSectionTextView = (TextView) findViewById(R.id.signoutSectionTextView);
        imgProfile = (ImageView) findViewById(R.id.profileImageView);
//        settingImageView = (ImageView) findViewById(R.id.settingImageView);
        followerContainer = (LinearLayout) findViewById(R.id.followerContainer);
        followingContainer = (LinearLayout) findViewById(R.id.followingContainer);
        rankContainer = (LinearLayout) findViewById(R.id.rankContainer);
        profileCompletionBar = (RoundedHorizontalProgressBar) findViewById(R.id.progress_bar_1);
        profilePercentageTextView = (TextView) findViewById(R.id.profilePercentageTextView);
        editProfileImageView = (ImageView) findViewById(R.id.editProfileImageView);
        editProfileTextView = (TextView) findViewById(R.id.editProfileTextView);
        publishedSectionTextView1 = (TextView) findViewById(R.id.publishedSectionTextView1);
        publishCoachmark1 = (LinearLayout) findViewById(R.id.publishCoachmark1);
        publishCoachmark2 = (LinearLayout) findViewById(R.id.publishCoachmark2);
        linearRewardsHeader = (LinearLayout) findViewById(R.id.linearRewardsHeader);
        textHeaderUpdate = (TextView) findViewById(R.id.textHeaderUpdate);
        relative_profile_progress = (RelativeLayout) findViewById(R.id.relative_profile_progress);
        readArticlesTextView = findViewById(R.id.readArticles);
        textHeaderUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.campaignEvent(PrivateProfileActivity.this, "Rewards 1st screen", "Profile", "Update", "", "android", SharedPrefUtils.getAppLocale(PrivateProfileActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Rewards_Detail");
                startActivity(new Intent(PrivateProfileActivity.this, RewardsContainerActivity.class));
            }
        });

        String isRewardsAdded = SharedPrefUtils.getIsRewardsAdded(PrivateProfileActivity.this);
        if (!isRewardsAdded.isEmpty() && isRewardsAdded.equalsIgnoreCase("0")) {
            linearRewardsHeader.setVisibility(View.VISIBLE);
            relative_profile_progress.setVisibility(View.INVISIBLE);
        } else {
            relative_profile_progress.setVisibility(View.VISIBLE);
            linearRewardsHeader.setVisibility(View.INVISIBLE);
        }

        linearRewardsHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(PrivateProfileActivity.this);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_rewards_sheet);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                dialog.findViewById(R.id.textUpdate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PrivateProfileActivity.this, RewardsContainerActivity.class));
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });

        ((TextView) findViewById(R.id.profileCompletionLabel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(PrivateProfileActivity.this, EditProfileNewActivity.class);
//                startActivity(intent);
            }
        });
        authorNameTextView.setOnClickListener(this);
//        locationTextView.setOnClickListener(this);
        authorBioTextView.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        publishedSectionTextView.setOnClickListener(this);
        draftSectionTextView.setOnClickListener(this);
//        bookmarksSectionTextView.setOnClickListener(this);
        activitySectionTextView.setOnClickListener(this);
//        rankingSectionTextView.setOnClickListener(this);
//        settingsSectionTextView.setOnClickListener(this);
        signoutSectionTextView.setOnClickListener(this);
//        settingImageView.setOnClickListener(this);
        followingContainer.setOnClickListener(this);
        followerContainer.setOnClickListener(this);
        backArrowImageView.setOnClickListener(this);
        updateProfileTextView.setOnClickListener(this);
        editProfileTextView.setOnClickListener(this);
        editProfileImageView.setOnClickListener(this);
        menuCoachmark.setOnClickListener(this);
        publishCoachmark1.setOnClickListener(this);
        publishCoachmark2.setOnClickListener(this);
        publishedSectionTextView1.setOnClickListener(this);
        readArticlesTextView.setOnClickListener(this);

        userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);
        }

        if (!SharedPrefUtils.isCoachmarksShownFlag(this, "Profile")) {
            menuCoachmark.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(rootView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getUserDetails();
            }
        }, 200);
    }

    private void getUserDetails() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            return;
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(userId);
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
                    if (responseData.getData() != null && responseData.getData().get(0) != null && responseData.getData().get(0).getResult() != null) {
                        isRewardsAdded = responseData.getData().get(0).getResult().getRewardsAdded();
                    }
                    if (responseData.getData().get(0).getResult().getRanks() == null || responseData.getData().get(0).getResult().getRanks().size() == 0) {
                        rankCountTextView.setText("--");
                        rankLanguageTextView.setText(getString(R.string.myprofile_rank_label));
                    } else if (responseData.getData().get(0).getResult().getRanks().size() < 2) {
                        rankCountTextView.setText("" + responseData.getData().get(0).getResult().getRanks().get(0).getRank());
                        if (AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(0).getLangKey())) {
                            rankLanguageTextView.setText(getString(R.string.blogger_profile_rank_in) + " ENGLISH");
                        } else {
                            rankLanguageTextView.setText(getString(R.string.blogger_profile_rank_in)
                                    + " " + AppUtils.getLangModelForLanguage(BaseApplication.getAppContext(), responseData.getData().get(0).getResult().getRanks().get(0).getLangKey()).getDisplay_name());
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
                        MyCityAnimationsUtil.animate(PrivateProfileActivity.this, rankContainer, multipleRankList, 0, true);
                    }

                    int followerCount = Integer.parseInt(responseData.getData().get(0).getResult().getFollowersCount());
                    if (followerCount > 999) {
                        float singleFollowerCount = ((float) followerCount) / 1000;
                        followerCountTextView.setText("" + singleFollowerCount + "k");
                    } else {
                        followerCountTextView.setText("" + followerCount);
                    }

                    int followingCount = Integer.parseInt(responseData.getData().get(0).getResult().getFollowingCount());
                    if (followingCount > 999) {
                        float singleFollowingCount = ((float) followingCount) / 1000;
                        followingCountTextView.setText("" + singleFollowingCount + "k");
                    } else {
                        followingCountTextView.setText("" + followingCount);
                    }
                    authorNameTextView.setText(responseData.getData().get(0).getResult().getFirstName() + " " + responseData.getData().get(0).getResult().getLastName());

                    switch (responseData.getData().get(0).getResult().getUserType()) {
                        case AppConstants.USER_TYPE_BLOGGER:
                            authorTypeTextView.setText(getString(R.string.author_type_blogger));
                            rankContainer.setOnClickListener(PrivateProfileActivity.this);
                            break;
                        case AppConstants.USER_TYPE_EDITOR:
                            authorTypeTextView.setText(getString(R.string.author_type_editor));
                            rankContainer.setOnClickListener(PrivateProfileActivity.this);
                            break;
                        case AppConstants.USER_TYPE_EDITORIAL:
                            authorTypeTextView.setText(getString(R.string.author_type_editorial));
                            rankContainer.setOnClickListener(PrivateProfileActivity.this);
                            break;
                        case AppConstants.USER_TYPE_EXPERT:
                            authorTypeTextView.setText(getString(R.string.author_type_expert));
                            rankContainer.setOnClickListener(PrivateProfileActivity.this);
                            break;
                        case AppConstants.USER_TYPE_FEATURED:
                            authorTypeTextView.setText(getString(R.string.author_type_featured));
                            rankContainer.setOnClickListener(PrivateProfileActivity.this);
                            break;
                        case AppConstants.USER_TYPE_USER:
                            authorTypeTextView.setText(getString(R.string.author_type_user));
                            if (AppConstants.DEBUGGING_USER_ID.contains(userId)) {
                                rankContainer.setOnClickListener(PrivateProfileActivity.this);
                            } else {
                                rankContainer.setOnClickListener(null);
                            }
                            break;
                        default:
                    }

                    if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())) {
                        Picasso.with(PrivateProfileActivity.this).load(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())
                                .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);
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
                        SharedPrefUtils.setFacebookConnectedFlag(PrivateProfileActivity.this, "1");
                    } else {
                        SharedPrefUtils.setFacebookConnectedFlag(PrivateProfileActivity.this,
                                responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                    }

                    profileCompletionBar.setProgress(getProfileCompletionPecentage(responseData.getData().get(0).getResult()));
                    profilePercentageTextView.setText(getProfileCompletionPecentage(responseData.getData().get(0).getResult()) + "%");
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

    private int getProfileCompletionPecentage(UserDetailResult result) {
        int totalProgress = 100;
        int progress = 0;
        if (result.getProfilePicUrl() == null || StringUtils.isNullOrEmpty(result.getProfilePicUrl().getClientApp())) {
            progress = progress + 10;
        }
        if (StringUtils.isNullOrEmpty(result.getFirstName())) {
            progress = progress + 10;
        }
        if (StringUtils.isNullOrEmpty(result.getLastName())) {
            progress = progress + 10;
        }
        if (StringUtils.isNullOrEmpty(result.getEmail())) {
            progress = progress + 10;
        }
        if (StringUtils.isNullOrEmpty(result.getPhone().getMobile())) {
            progress = progress + 10;
        }
        if (StringUtils.isNullOrEmpty(result.getBlogTitle())) {
            progress = progress + 10;
        }
        if (StringUtils.isNullOrEmpty(result.getUserBio())) {
            progress = progress + 10;
        }
        if (result.getKids() == null || result.getKids().isEmpty()) {
            progress = progress + 10;
        }
        if (StringUtils.isNullOrEmpty(SharedPrefUtils.getCurrentCityModel(this).getName())) {
            progress = progress + 10;
        }

        return totalProgress - progress;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menuCoachmark:
                menuCoachmark.setVisibility(View.GONE);
                publishCoachmark1.setVisibility(View.VISIBLE);
                publishCoachmark2.setVisibility(View.VISIBLE);
                publishedSectionTextView1.setVisibility(View.VISIBLE);
                break;
            case R.id.publishCoachmark1:
            case R.id.publishCoachmark2:
            case R.id.publishedSectionTextView1:
                publishedSectionTextView1.setVisibility(View.GONE);
                publishCoachmark2.setVisibility(View.GONE);
                publishCoachmark1.setVisibility(View.GONE);
                SharedPrefUtils.setCoachmarksShownFlag(PrivateProfileActivity.this, "Profile", true);
                break;
            case R.id.editProfileImageView:
                Intent intent = new Intent(PrivateProfileActivity.this, EditProfileNewActivity.class);
                intent.putExtra("isRewardAdded", isRewardsAdded);
                startActivity(intent);
                break;
            case R.id.editProfileTextView:
                Intent intent1 = new Intent(PrivateProfileActivity.this, EditProfileNewActivity.class);
                intent1.putExtra("isRewardAdded", isRewardsAdded);
                startActivity(intent1);
                break;
            case R.id.updateProfileTextView: {
                startActivity(new Intent(PrivateProfileActivity.this, RewardsContainerActivity.class));
                break;
            }
            case R.id.menuImageView:
                onBackPressed();
                break;
            case R.id.authorNameTextView: {
                Intent intent3 = new Intent(this, PrivateProfileActivity.class);
                startActivity(intent3);
            }
            break;
            case R.id.authorTypeTextView:
                break;
//            case R.id.authorBioTextView:
//                break;
            case R.id.settingImageView:
            case R.id.imgProfile:
                break;
            case R.id.publishedSectionTextView:
                Intent articleIntent = new Intent(this, UserPublishedContentActivity.class);
                articleIntent.putExtra("isPrivateProfile", true);
                articleIntent.putExtra(Constants.AUTHOR_ID, userId);
                startActivity(articleIntent);
                break;
            case R.id.readArticles:
                Intent readArticleIntent = new Intent(this, UserReadArticlesContentActivity.class);
                readArticleIntent.putExtra("isPrivateProfile", true);
                readArticleIntent.putExtra(Constants.AUTHOR_ID, userId);
                startActivity(readArticleIntent);
                break;
            case R.id.draftSectionTextView:
                Intent ssIntent = new Intent(this, UserDraftsContentActivity.class);
                ssIntent.putExtra("isPrivateProfile", true);
                ssIntent.putExtra("contentType", "shortStory");
                ssIntent.putExtra(Constants.AUTHOR_ID, userId);
                startActivity(ssIntent);
                break;
            case R.id.bookmarksSectionTextView: {
                Intent intent4 = new Intent(this, UserActivitiesActivity.class);
                intent4.putExtra(Constants.AUTHOR_ID, userId);
                startActivity(intent4);
            }
            case R.id.activitySectionTextView: {
                Intent intent5 = new Intent(this, UserActivitiesActivity.class);
                intent5.putExtra(Constants.AUTHOR_ID, userId);
                startActivity(intent5);
            }
            break;
            case R.id.rankContainer:
                if (AppConstants.DEBUGGING_USER_ID.contains(userId)) {
                    rankContainer.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            BaseApplication.getInstance().toggleGroupBaseURL();
                            return false;
                        }
                    });
                    Intent _intent = new Intent(this, IdTokenLoginActivity.class);
                    startActivity(_intent);
                    return;
                } else {
                    Intent intent6 = new Intent(this, RankingActivity.class);
                    startActivity(intent6);
                }
                break;
            case R.id.rankingSectionTextView: {
                Intent intent7 = new Intent(this, RankingActivity.class);
                startActivity(intent7);
            }
            break;
            case R.id.settingsSectionTextView:
                Intent settingsIntent = new Intent(this, AppSettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.signoutSectionTextView:
                logoutUser();
                break;
            case R.id.followingContainer: {
                Utils.pushOpenScreenEvent(this, "FollowingListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                Intent intent8 = new Intent(this, FollowersAndFollowingListActivity.class);
                intent8.putExtra(AppConstants.FOLLOW_LIST_TYPE, AppConstants.FOLLOWING_LIST);
                intent8.putExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS, userId);
                startActivity(intent8);
            }
            break;
            case R.id.followerContainer: {
                Utils.pushOpenScreenEvent(this, "FollowersListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                Intent intent9 = new Intent(this, FollowersAndFollowingListActivity.class);
                intent9.putExtra(AppConstants.FOLLOW_LIST_TYPE, AppConstants.FOLLOWER_LIST);
                intent9.putExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS, userId);
                startActivity(intent9);
            }
        }
    }

    private void logoutUser() {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            final LogoutController _controller = new LogoutController(this, this);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

            dialog.setMessage(getResources().getString(R.string.logout_msg)).setNegativeButton(R.string.new_yes
                    , new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            showProgressDialog(getResources().getString(R.string.please_wait));
                            _controller.getData(AppConstants.LOGOUT_REQUEST, "");
                        }
                    }).setPositiveButton(R.string.new_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                    dialog.cancel();
                }
            }).setIcon(android.R.drawable.ic_dialog_alert);
            AlertDialog alert11 = dialog.create();
            alert11.show();
            alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.app_red));
            alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));
        } else {
            ToastUtils.showToast(this, getString(R.string.error_network));
        }
    }

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();
        if (response == null) {
            Toast.makeText(this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            return;
        }
        LogoutResponse responseData = (LogoutResponse) response.getResponseObject();
        String message = responseData.getResult().getMessage();
        if (responseData.getResponseCode() == 200) {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                mixpanel.track("UserLogout", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            FacebookUtils.logout(this);
            gPlusSignOut();

            String pushToken = SharedPrefUtils.getDeviceToken(this);
            boolean homeCoach = SharedPrefUtils.isCoachmarksShownFlag(this, "home");
            boolean topicsCoach = SharedPrefUtils.isCoachmarksShownFlag(this, "topics");
            boolean topicsArticleCoach = SharedPrefUtils.isCoachmarksShownFlag(this, "topics_article");
            boolean articleCoach = SharedPrefUtils.isCoachmarksShownFlag(this, "article_details");
            boolean groupsCoach = SharedPrefUtils.isCoachmarksShownFlag(this, "groups");
            String appLocale = SharedPrefUtils.getAppLocale(this);

            SharedPrefUtils.clearPrefrence(this);
            SharedPrefUtils.setDeviceToken(this, pushToken);
            SharedPrefUtils.setCoachmarksShownFlag(this, "home", homeCoach);
            SharedPrefUtils.setCoachmarksShownFlag(this, "topics", topicsCoach);
            SharedPrefUtils.setCoachmarksShownFlag(this, "topics_article", topicsArticleCoach);
            SharedPrefUtils.setCoachmarksShownFlag(this, "article_details", articleCoach);
            SharedPrefUtils.setCoachmarksShownFlag(this, "groups", groupsCoach);
            SharedPrefUtils.setAppLocale(this, appLocale);
            /**
             * delete table from local also;
             */
            UserTable _tables = new UserTable((BaseApplication) this.getApplicationContext());
            _tables.deleteAll();

            TableFamily _familytables = new TableFamily((BaseApplication) this.getApplicationContext());
            _familytables.deleteAll();

            TableAdult _adulttables = new TableAdult((BaseApplication) this.getApplicationContext());
            _adulttables.deleteAll();

            TableKids _kidtables = new TableKids((BaseApplication) this.getApplicationContext());
            _kidtables.deleteAll();

            new TableAppointmentData(BaseApplication.getInstance()).deleteAll();
            new TableNotes(BaseApplication.getInstance()).deleteAll();
            new TableFile(BaseApplication.getInstance()).deleteAll();
            new TableAttendee(BaseApplication.getInstance()).deleteAll();
            new TableWhoToRemind(BaseApplication.getInstance()).deleteAll();


            new TableTaskData(BaseApplication.getInstance()).deleteAll();
            new TableTaskList(BaseApplication.getInstance()).deleteAll();
            new TaskTableAttendee(BaseApplication.getInstance()).deleteAll();
            new TaskTableWhoToRemind(BaseApplication.getInstance()).deleteAll();
            new TaskTableFile(BaseApplication.getInstance()).deleteAll();
            new TaskTableNotes(BaseApplication.getInstance()).deleteAll();
            new TaskCompletedTable(BaseApplication.getInstance()).deleteAll();
            new TableApiEvents(BaseApplication.getInstance()).deleteAll();

            new ExternalCalendarTable(BaseApplication.getInstance()).deleteAll();

            // clear cachee
            BaseApplication.setBlogResponse(null);
            BaseApplication.setBusinessREsponse(null);

            // clear all sessions

            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }

            // set logout flag
            SharedPrefUtils.setLogoutFlag(this, true);
            Intent intent = new Intent(this, ActivityLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            this.finish();

        } else if (responseData.getResponseCode() == 400) {
            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gPlusSignOut() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
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
                    if ((lineEndIndex - expandText.length() + 1) > 10) {
                        String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                        viewMore, userBio), TextView.BufferType.SPANNABLE);
                    } else {
                        // int lineEndIndex1 = tv.getLayout().getLineEnd(maxLine-1);
                        String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                        viewMore, userBio), TextView.BufferType.SPANNABLE);
                    }
                } else {

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
}