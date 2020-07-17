package com.mycity4kids.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.MessageEvent;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.NewEditor;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.response.DeepLinkingResposnse;
import com.mycity4kids.models.response.DeepLinkingResult;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.ShortStoryDetailResult;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.DeepLinkingAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.sync.SyncUserInfoService;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.AddShortStoryActivity;
import com.mycity4kids.ui.activity.AppSettingsActivity;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.BadgeActivity;
import com.mycity4kids.ui.activity.BlogSetupActivity;
import com.mycity4kids.ui.activity.CategoryVideosListingActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupMembershipActivity;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.activity.GroupsReportedContentActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.ShortStoriesListingContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.SuggestedTopicsActivity;
import com.mycity4kids.ui.activity.UserDraftsContentActivity;
import com.mycity4kids.ui.activity.ViewGroupPostCommentsRepliesActivity;
import com.mycity4kids.ui.activity.collection.CollectionsActivity;
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.CustomTabsHelper;
import com.mycity4kids.utils.StringUtils;
import com.squareup.picasso.Picasso;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * This class is used as base-class for application-base-activity.
 */
public abstract class BaseActivity extends AppCompatActivity implements GroupMembershipStatus.IMembershipStatus {

    public static boolean isAppInFg = false;
    public static boolean isScrInFg = false;
    public static boolean isChangeScrFg = false;
    BaseApplication baseApplication;
    private String userId;
    private String title = "";
    private String body = "";
    private String type = "";
    private String id = "";
    private String titleSlug = "";
    private String blogSlug = "";
    private String groupId = "";
    private String postId = "";
    private String responseId = "";
    private String campaignId = "";
    private String imageUrl = "";
    private String url = "";
    private DisplayMetrics displayMetrics;
    private Snackbar snackbar;
    private ProgressDialog progressDialog;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayMetrics = getResources().getDisplayMetrics();
        baseApplication = (BaseApplication) getApplication();
        String userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        try {
            if (BaseApplication.getMSocket() != null && !TextUtils.isEmpty(userId)) {
                JSONObject obj = new JSONObject();
                obj.put("pagename", this.getClass().getName());
                obj.put("user_id", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
                BaseApplication.getMSocket().emit("pageview", obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        emitter(event.getObject());
    }

    private void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    private void emitter(Object... object) {
        runOnUiThread(() -> {
            JSONObject data;
            try {
                data = new JSONObject(object[0].toString());
                userId = data.getString("user_id");
                title = data.getString("title");
                body = data.getString("body");
                type = data.getString("type");
                imageUrl = data.getString("image_url");
                id = data.getString("id");
                titleSlug = data.getString("title_slug");
                blogSlug = data.getString("blog_slug");
                groupId = data.getString("group_id");
                postId = data.getString("post_id");
                responseId = data.getString("response_id");
                campaignId = data.getString("campaign_id");
                url = data.getString("url");
            } catch (JSONException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        });

        runOnUiThread(() -> {
            try {
                Snackbar snackbar = showCustomSnackbar();
                snackbar.show();
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        });
    }

    private Snackbar showCustomSnackbar() { // Create the Snackbar
        snackbar = Snackbar.make(BaseApplication.getInstance().getView(), "", 60000);
        int height = displayMetrics.heightPixels;
        height = (int) (height * 0.22);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        FrameLayout.LayoutParams parentParams = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        // 15 is margin from all the sides for snackbar
        int marginFromSides = 0;
        parentParams.setMargins(marginFromSides, 0, marginFromSides, marginFromSides);
        parentParams.height = (int) height;
        parentParams.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        snackBarView.setLayoutParams(parentParams);

        View snackView = getLayoutInflater().inflate(R.layout.dialog_socket_notification, null);
        snackBarView.addView(snackView, 0);
        TextView textTitle = snackView.findViewById(R.id.textbody);
        TextView textAuthor = snackView.findViewById(R.id.textUpdate);
        ImageView image = snackView.findViewById(R.id.image);

        textTitle.setText(body);
        textAuthor.setText(title);
        if (!imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.article_default)
                    .error(R.drawable.article_default).into(image);
        } else {
            image.setVisibility(View.GONE);
        }
        ImageView cross = snackView.findViewById(R.id.cross);
        cross.setOnClickListener(view -> snackbar.dismiss());
        RelativeLayout bottomSheet = snackView.findViewById(R.id.bottom_sheet);
        bottomSheet.setOnClickListener(view -> {
            snackbar.dismiss();
            setPubSub();
        });
        return snackbar;
    }

    private void setPubSub() {
        if (type.equalsIgnoreCase("article_details")) {
            Intent intent1 = new Intent(this, ArticleDetailsContainerActivity.class);
            intent1.putExtra("fromNotification", true);
            intent1.putExtra(Constants.ARTICLE_ID, id);
            intent1.putExtra(Constants.AUTHOR_ID, userId);
            intent1.putExtra(Constants.BLOG_SLUG, blogSlug);
            intent1.putExtra(Constants.TITLE_SLUG, titleSlug);
            intent1.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
            intent1.putExtra(Constants.ARTICLE_INDEX, "-1");
            startActivity(intent1);
        } else if (type.equalsIgnoreCase("momsights_screen")) {
            Intent intent1 = new Intent(this, RewardsContainerActivity.class);
            startActivity(intent1);
        } else if (type.equalsIgnoreCase("campaign_listing")) {
            Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
            campaignIntent.putExtra("campaign_listing", "campaign_listing");
            startActivity(campaignIntent);
        } else if (type.equalsIgnoreCase("campaign_detail")) {
            Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
            campaignIntent.putExtra("campaign_id", campaignId);
            campaignIntent.putExtra("campaign_detail", "campaign_detail");
            campaignIntent.putExtra("fromNotification", false);
            startActivity(campaignIntent);
        } else if (type.equalsIgnoreCase("campaign_submit_proof")) {
            Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
            campaignIntent.putExtra("campaign_Id", campaignId);
            campaignIntent.putExtra("campaign_submit_proof", "campaign_submit_proof");
            startActivity(campaignIntent);
        } else if (type.equalsIgnoreCase("mymoney_bankdetails")) {
            Intent campaignIntent = new Intent(this, RewardsContainerActivity.class);
            campaignIntent.putExtra("isComingfromCampaign", true);
            campaignIntent.putExtra("pageLimit", 4);
            campaignIntent.putExtra("pageNumber", 4);
            campaignIntent.putExtra("campaign_Id", campaignId);
            campaignIntent.putExtra("mymoney_bankdetails", "mymoney_bankdetails");
            startActivity(campaignIntent);
        } else if (type.equalsIgnoreCase("mymoney_pancard")) {
            Intent campaignIntent = new Intent(this, RewardsContainerActivity.class);
            campaignIntent.putExtra("isComingFromRewards", true);
            campaignIntent.putExtra("pageLimit", 5);
            campaignIntent.putExtra("pageNumber", 5);
            campaignIntent.putExtra("panCardFormNotification", "mymoney_pancard");
            campaignIntent.putExtra("mymoney_pancard", "mymoney_pancard");
            startActivity(campaignIntent);
        } else if (type.equalsIgnoreCase("shortStoryDetails")) {
            Intent ssIntent = new Intent(this, ShortStoryContainerActivity.class);
            ssIntent.putExtra(Constants.AUTHOR_ID, userId);
            ssIntent.putExtra(Constants.ARTICLE_ID, id);
            ssIntent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
            ssIntent.putExtra(Constants.BLOG_SLUG, blogSlug);
            ssIntent.putExtra(Constants.TITLE_SLUG, titleSlug);
            ssIntent.putExtra(Constants.FROM_SCREEN, "Notification");
            ssIntent.putExtra(Constants.ARTICLE_INDEX, "-1");
            ssIntent.putExtra(Constants.AUTHOR, userId + "~");
            startActivity(ssIntent);
        } else if (type.equalsIgnoreCase("video_details")) {
            Intent intent1 = new Intent(this, ParallelFeedActivity.class);
            intent1.putExtra("fromNotification", true);
            intent1.putExtra(Constants.VIDEO_ID, id);
            intent1.putExtra(Constants.AUTHOR_ID, userId);
            intent1.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
            intent1.putExtra(Constants.ARTICLE_INDEX, "-1");
            startActivity(intent1);
        } else if (type.equalsIgnoreCase("group_membership")
                || type.equalsIgnoreCase("group_new_post")
                || type.equalsIgnoreCase("group_admin_group_edit")
                || type.equalsIgnoreCase("group_admin")) {
            GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(BaseActivity.this);
            groupMembershipStatus.checkMembershipStatus(Integer.parseInt(groupId),
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        } else if (type.equalsIgnoreCase("group_new_response")) {
            Intent gpPostIntent = new Intent(this, GroupPostDetailActivity.class);
            gpPostIntent.putExtra("postId", Integer.parseInt(postId));
            gpPostIntent.putExtra("groupId", Integer.parseInt(groupId));
            gpPostIntent.putExtra("responseId", Integer.parseInt(responseId));
            startActivity(gpPostIntent);
        } else if (type.equalsIgnoreCase("group_new_reply")) {
            Intent gpPostIntent = new Intent(this, ViewGroupPostCommentsRepliesActivity.class);
            gpPostIntent.putExtra("postId", Integer.parseInt(postId));
            gpPostIntent.putExtra("groupId", Integer.parseInt(groupId));
            gpPostIntent.putExtra("responseId", Integer.parseInt(responseId));
            startActivity(gpPostIntent);
        } else if (type.equalsIgnoreCase("group_admin_membership")) {
            Intent memberIntent = new Intent(this, GroupMembershipActivity.class);
            memberIntent.putExtra("groupId", Integer.parseInt(groupId));
            startActivity(memberIntent);
        } else if (type.equalsIgnoreCase("group_admin_reported")) {
            Intent reportIntent = new Intent(this, GroupsReportedContentActivity.class);
            reportIntent.putExtra("groupId", Integer.parseInt(type));
            startActivity(reportIntent);
        } else if (type.equalsIgnoreCase("webView")) {
            Intent intent1 = new Intent(this, LoadWebViewActivity.class);
            intent1.putExtra("fromNotification", true);
            intent1.putExtra(Constants.WEB_VIEW_URL, url);
            startActivity(intent1);
        } else if (type.equalsIgnoreCase("profile")) {
            if (!SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId().equals(userId)) {
                Intent intent1 = new Intent(this, UserProfileActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra(Constants.USER_ID, userId);
                startActivity(intent1);
            }
        } else if (type.equalsIgnoreCase(AppConstants.APP_SETTINGS_DEEPLINK)) {
            Intent intent1 = new Intent(this, AppSettingsActivity.class);
            intent1.putExtra("fromNotification", true);
            intent1.putExtra("load_fragment", Constants.SETTINGS_FRAGMENT);
            startActivity(intent1);
        }
    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        String userType = null;
        if (body.getData().getResult() != null && !body.getData().getResult().isEmpty()) {
            if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
            } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
            }
        }

        if (!AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(userType) && !AppConstants.GROUP_MEMBER_TYPE_ADMIN
                .equals(userType)) {
            if ("male".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())
                    ||
                    "m".equalsIgnoreCase(
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())) {
                Toast.makeText(this, getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                if (!BuildConfig.DEBUG && !AppConstants.DEBUGGING_USER_ID
                        .contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {
                    return;
                }
            }
        }

        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            Toast.makeText(this, getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT).show();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(this, GroupDetailsActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION
                .equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("pendingMembershipFlag", true);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

    }

    public void replaceFragment(final Fragment fragment, Bundle bundle) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        new Handler().post(() -> {
            try {
                String backStateName = fragment.getClass().getName();
                boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);
                if (!fragmentPopped) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(backStateName);
                    ft.commit();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        });
    }

    public void replaceFragmentWithAnimation(final Fragment fragment, Bundle bundle) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        new Handler().post(() -> {
            try {
                String backStateName = fragment.getClass().getName();
                boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);
                if (!fragmentPopped) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_left,
                            R.anim.transition_enter_from_left, R.anim.transition_exit_to_right);
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(backStateName);
                    ft.commit();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        });
    }

    public void addFragment(final Fragment fragment, Bundle bundle) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        new Handler().post(() -> {
            try {
                String backStateName = fragment.getClass().getName();
                boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);
                if (!fragmentPopped) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.content_frame, fragment);
                    ft.addToBackStack(backStateName);
                    ft.commit();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        });
    }

    public void addFragment(final Fragment fragment, Bundle bundle, final String animationType) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        new Handler().post(() -> {
            try {
                String backStateName = fragment.getClass().getName();
                boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);

                if (!fragmentPopped) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    if ("topToBottom".equals(animationType)) {
                        ft.setCustomAnimations(R.anim.transition_enter_from_bottom, R.anim.transition_exit_to_top,
                                R.anim.transition_enter_from_top, R.anim.transition_exit_to_bottom);
                    } else if ("bottomSheet".equals(animationType)) {
                        ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down,
                                R.anim.transition_enter_from_top, R.anim.transition_exit_to_bottom);
                    } else {
                        ft.setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_left,
                                R.anim.transition_enter_from_left, R.anim.transition_exit_to_right);
                    }
                    ft.add(R.id.content_frame, fragment);
                    ft.addToBackStack(backStateName);
                    ft.commit();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        });
    }

    public void startSyncingUserInfo() {
        Intent intent = new Intent(this, SyncUserInfoService.class);
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    @Override
    protected void onStart() {
        if (!isAppInFg) {
            isAppInFg = true;
            isChangeScrFg = false;
            onAppStart();
        } else {
            isChangeScrFg = true;
        }
        isScrInFg = true;
        registerEventBus();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isScrInFg || !isChangeScrFg) {
            isAppInFg = false;
            onAppPause();
        }
        isScrInFg = false;
        unregisterEventBus();
    }

    public void onAppStart() {
    }

    public void onAppPause() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public void showAlertDialog(String title, String message, final OnButtonClicked onButtonClicked) {
        try {
            new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        dialog.dismiss();
                        onButtonClicked.onButtonCLick(0);
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    public void showUpgradeAppAlertDialog(String title, String message) {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    String appPackage = getPackageName();
                    try {
                        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appPackage));
                        startActivity(rateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } catch (Exception e) {
                        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
                        startActivity(rateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                    dialog.dismiss();
                    finish();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void showProgressDialog(String bodyText) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(BaseActivity.this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setOnKeyListener((dialog, keyCode, event) ->
                    keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_SEARCH);
        }
        progressDialog.setMessage(bodyText);
        if (!progressDialog.isShowing()) {
            try {
                progressDialog.show();
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }
    }

    public void removeProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    public void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }

        if (!StringUtils.isNullOrEmpty(message)) {
            toast = Toast.makeText(BaseActivity.this, "" + message, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public void apiExceptions(Throwable t) {
        if (t instanceof UnknownHostException) {
            showToast(getString(R.string.error_network));
        } else if (t instanceof SocketTimeoutException) {
            showToast(getString(R.string.connection_timeout));
        } else {
            showToast(getString(R.string.server_went_wrong));
        }
        FirebaseCrashlytics.getInstance().recordException(t);
        Log.d("MC4kException", Log.getStackTraceString(t));
    }

    public void handleDeeplinks(String tempDeepLinkUrl) {
        try {
            if (matchRegex(tempDeepLinkUrl)) {
                // need to optimize this code
            } else if (tempDeepLinkUrl.endsWith(AppConstants.DEEPLINK_SELF_PROFILE_URL_1)
                    || tempDeepLinkUrl.endsWith(AppConstants.DEEPLINK_SELF_PROFILE_URL_2)
                    || tempDeepLinkUrl.contains(AppConstants.DEEPLINK_PROFILE_URL)
                    || tempDeepLinkUrl.contains(AppConstants.DEEPLINK_MOMSPRESSO_PROFILE_URL)) {
                Intent profileIntent = new Intent(this, UserProfileActivity.class);
                startActivity(profileIntent);
            } else if (tempDeepLinkUrl.contains(AppConstants.DEEPLINK_ADD_FUNNY_VIDEO_URL)
                    || tempDeepLinkUrl
                    .contains(AppConstants.DEEPLINK_MOMSPRESSO_ADD_FUNNY_VIDEO_URL)) {
                if (this instanceof DashboardActivity) {
                    ((DashboardActivity) this).launchAddVideoOptions();
                }
            } else if (tempDeepLinkUrl.contains(AppConstants.DEEPLINK_EDITOR_URL)
                    || tempDeepLinkUrl.contains(AppConstants.DEEPLINK_MOMSPRESSO_EDITOR_URL)) {
                launchEditor();
            } else if (tempDeepLinkUrl.contains(AppConstants.DEEPLINK_EDIT_SHORT_DRAFT_URL)) {
                Intent ssIntent = new Intent(this, UserDraftsContentActivity.class);
                ssIntent.putExtra("isPrivateProfile", true);
                ssIntent.putExtra("contentType", AppConstants.CONTENT_TYPE_SHORT_STORY);
                ssIntent.putExtra(Constants.AUTHOR_ID,
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                startActivity(ssIntent);
            } else if (tempDeepLinkUrl.contains(AppConstants.DEEPLINK_PROFILE_INVITE_FRIENDS)) {
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(AppConstants.SHOW_INVITE_DIALOG_FLAG, true);
                intent.putExtra("source", "deeplink");
                startActivity(intent);
            } else if (tempDeepLinkUrl.contains(AppConstants.DEEPLINK_EDIT_SHORT_STORY_URL)) {
                final String storyId = tempDeepLinkUrl
                        .substring(tempDeepLinkUrl.lastIndexOf("/") + 1,
                                tempDeepLinkUrl.length());
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                ShortStoryAPI shortStoryApi = retrofit.create(ShortStoryAPI.class);
                Call<ShortStoryDetailResult> call = shortStoryApi
                        .getShortStoryDetails(storyId, "articleId");
                call.enqueue(ssDetailResponseCallback);
            } else if (tempDeepLinkUrl.contains(AppConstants.DEEPLINK_SUGGESTED_TOPIC_URL)
                    || tempDeepLinkUrl
                    .contains(AppConstants.DEEPLINK_MOMSPRESSO_SUGGESTED_TOPIC_URL)) {
                Intent suggestedIntent = new Intent(this, SuggestedTopicsActivity.class);
                startActivity(suggestedIntent);
            } else if (tempDeepLinkUrl.endsWith(AppConstants.DEEPLINK_SETUP_BLOG)) {
                checkIsBlogSetup();
            } else if (tempDeepLinkUrl.contains(AppConstants.DEEPLINK_MOMSPRESSO_REWARD_PAGE)) {
                Intent rewardForm = new Intent(this, RewardsContainerActivity.class);
                final String referralCode = tempDeepLinkUrl
                        .substring(tempDeepLinkUrl.lastIndexOf("=") + 1,
                                tempDeepLinkUrl.length());
                rewardForm.putExtra("pageLimit", 1);
                rewardForm.putExtra("pageNumber", 1);
                rewardForm.putExtra("referral", referralCode);
                startActivity(rewardForm);
            } else if (tempDeepLinkUrl
                    .contains(AppConstants.DEEPLINK_MOMSPRESSO_REWARD_MYMONEY)) {
                Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
                startActivity(campaignIntent);
            } else if (tempDeepLinkUrl.contains(AppConstants.DEEPLINK_MOMSPRESSO_CAMPAIGN)) {
                if (tempDeepLinkUrl.contains("?")) {
                    final String campaignID = tempDeepLinkUrl
                            .substring(tempDeepLinkUrl.lastIndexOf("/") + 1,
                                    tempDeepLinkUrl.indexOf("?"));
                    if (!StringUtils.isNullOrEmpty(campaignID)) {
                        Intent campaignIntent = new Intent(this,
                                CampaignContainerActivity.class);
                        campaignIntent.putExtra("campaignID", Integer.parseInt(campaignID));
                        startActivity(campaignIntent);
                    }
                } else {
                    final String campaignID = tempDeepLinkUrl
                            .substring(tempDeepLinkUrl.lastIndexOf("/") + 1);
                    if (!StringUtils.isNullOrEmpty(campaignID)) {
                        Intent campaignIntent = new Intent(this,
                                CampaignContainerActivity.class);
                        campaignIntent.putExtra("campaignID", Integer.parseInt(campaignID));
                        startActivity(campaignIntent);
                    }
                }
            } else if (tempDeepLinkUrl.contains(AppConstants.DEEPLINK_GROUPS)) {
                String[] separated = tempDeepLinkUrl.split("/");
                if (separated[separated.length - 1].startsWith("comment-")) {
                    String[] commArray = separated[separated.length - 1].split("-");
                    long commentId = AppUtils.getIdFromHash(commArray[1]);
                    String[] postArray = separated[separated.length - 2].split("-");
                    long postId = AppUtils.getIdFromHash(postArray[1]);
                    String[] groupArray = separated[separated.length - 3].split("-");
                    long groupId = AppUtils.getIdFromHash(groupArray[groupArray.length - 1]);
                    Intent gpPostIntent = new Intent(this, GroupPostDetailActivity.class);
                    gpPostIntent.putExtra("postId", (int) postId);
                    gpPostIntent.putExtra("groupId", (int) groupId);
                    gpPostIntent.putExtra("responseId", (int) commentId);
                    startActivity(gpPostIntent);
                } else if (separated[separated.length - 1].startsWith("post-")) {
                    String[] postArray = separated[separated.length - 1].split("-");
                    long postId = AppUtils.getIdFromHash(postArray[1]);
                    String[] groupArray = separated[separated.length - 2].split("-");
                    long groupId = AppUtils.getIdFromHash(groupArray[groupArray.length - 1]);
                    Intent gpPostIntent = new Intent(this, GroupPostDetailActivity.class);
                    gpPostIntent.putExtra("postId", (int) postId);
                    gpPostIntent.putExtra("groupId", (int) groupId);
                    startActivity(gpPostIntent);
                } else if (separated[separated.length - 1].equals("join")) {
                    String[] groupArray = separated[separated.length - 2].split("-");
                    long groupId = AppUtils.getIdFromHash(groupArray[groupArray.length - 1]);
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(
                            this);
                    groupMembershipStatus.checkMembershipStatus((int) groupId,
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                                    .getDynamoId());
                } else {
                    String[] groupArray;
                    long groupId;
                    if (separated[separated.length - 1].contains("?")) {
                        groupArray = separated[separated.length - 1].split("[?]");
                        groupArray = groupArray[0].split("-");
                        groupId = AppUtils.getIdFromHash(groupArray[groupArray.length - 1]);
                    } else {
                        groupArray = separated[separated.length - 1].split("-");
                        groupId = AppUtils.getIdFromHash(groupArray[groupArray.length - 1]);
                    }
                    if (groupId == -1) {
                        return;
                    }
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(
                            this);
                    groupMembershipStatus.checkMembershipStatus((int) groupId,
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                                    .getDynamoId());
                }
            } else if (tempDeepLinkUrl.contains(AppConstants.DEEPLINK_MOMSPRESSO_REFERRAL)) {
                Intent intent1 = new Intent(this, RewardsContainerActivity.class);
                intent1.putExtra("pageNumber", 1);
                startActivity(intent1);
            } else if (tempDeepLinkUrl.contains(AppConstants.DEEPLINK_ADD_SHORT_STORY_URL)
                    || tempDeepLinkUrl.contains(AppConstants.DEEPLINK_ADD_SHORT_STORY_URL_1)) {
                Intent ssIntent = new Intent(this, AddShortStoryActivity.class);
                startActivity(ssIntent);
            } else {
                getDeepLinkData(tempDeepLinkUrl);
            }
        } catch (Exception e) {
            launchChromeTabs(tempDeepLinkUrl);
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
                Intent intent = new Intent(this, CollectionsActivity.class);
                intent.putExtra("userId", separated[separated.length - 2]);
                startActivity(intent);
                return true;
            }

            Pattern pattern1 = Pattern.compile(AppConstants.COLLECTION_DETAIL_REGEX);
            Matcher matcher1 = pattern1.matcher(urlWithNoParams);
            if (matcher1.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, UserCollectionItemListActivity.class);
                intent.putExtra("id", separated[separated.length - 1]);
                startActivity(intent);
                return true;
            }

            Pattern pattern2 = Pattern.compile(AppConstants.BADGES_LISTING_REGEX);
            Matcher matcher2 = pattern2.matcher(urlWithNoParams);
            if (matcher2.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, BadgeActivity.class);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 2]);
                startActivity(intent);
                return true;
            }

            Pattern pattern3 = Pattern.compile(AppConstants.BADGES_DETAIL_REGEX);
            Matcher matcher3 = pattern3.matcher(urlWithNoParams);
            if (matcher3.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(AppConstants.BADGE_ID, separated[separated.length - 1]);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 3]);
                startActivity(intent);
                return true;
            }

            Pattern pattern4 = Pattern.compile(AppConstants.MILESTONE_DETAIL_REGEX);
            Matcher matcher4 = pattern4.matcher(urlWithNoParams);
            if (matcher4.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(AppConstants.MILESTONE_ID, separated[separated.length - 1]);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 3]);
                startActivity(intent);
                return true;
            }

            Pattern pattern5 = Pattern.compile(AppConstants.USER_PROFILE_REGEX);
            Matcher matcher5 = pattern5.matcher(urlWithNoParams);
            if (matcher5.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 1]);
                startActivity(intent);
                return true;
            }

            Pattern pattern6 = Pattern.compile(AppConstants.USER_ANALYTICS_REGEX);
            Matcher matcher6 = pattern6.matcher(urlWithNoParams);
            if (matcher6.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("detail", "rank");
                intent.putExtra(Constants.USER_ID, separated[separated.length - 2]);
                startActivity(intent);
                return true;
            }

            Pattern pattern7 = Pattern.compile(AppConstants.USER_PROFILE_INVITE_FRIENDS_REGEX);
            Matcher matcher7 = pattern7.matcher(urlWithNoParams);
            if (matcher7.matches()) {
                String[] separated = urlWithNoParams.split("#")[0].split("/");
                Intent intent = new Intent(this, UserProfileActivity.class);
                if (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
                        .equals(separated[separated.length - 1])) {
                    intent.putExtra(AppConstants.SHOW_INVITE_DIALOG_FLAG, true);
                    intent.putExtra("source", "deeplink");
                }
                intent.putExtra(Constants.USER_ID, separated[separated.length - 1]);
                startActivity(intent);
                return true;
            }

            Pattern pattern8 = Pattern.compile(AppConstants.VLOG_CATEGORY_LISTING_REGEX);
            Matcher matcher8 = pattern8.matcher(urlWithNoParams);
            if (matcher8.matches()) {
                Intent intent = new Intent(this, CategoryVideosListingActivity.class);
                startActivity(intent);
                return true;
            }

            Pattern pattern9 = Pattern.compile(AppConstants.VLOGS_CHALLENGE_LISTING_REGEX);
            Matcher matcher9 = pattern9.matcher(urlWithNoParams);
            if (matcher9.matches()) {
                Intent intent = new Intent(this, CategoryVideosListingActivity.class);
                intent.putExtra("categoryId", AppConstants.VIDEO_CHALLENGE_ID);
                startActivity(intent);
                return true;
            }

            Pattern pattern10 = Pattern.compile(AppConstants.VLOGS_CHALLENGE_DETAIL_REGEX);
            Matcher matcher10 = pattern10.matcher(urlWithNoParams);
            if (matcher10.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, NewVideoChallengeActivity.class);
                intent.putExtra(Constants.CHALLENGE_ID, separated[separated.length - 1]);
                intent.putExtra("comingFrom", "deeplink");
                startActivity(intent);
                return true;
            }

            Pattern pattern11 = Pattern.compile(AppConstants.STORY_CATEGORY_LISTING_REGEX);
            Matcher matcher11 = pattern11.matcher(urlWithNoParams);
            if (matcher11.matches()) {
                Intent intent = new Intent(this, ShortStoriesListingContainerActivity.class);
                intent.putExtra("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
                startActivity(intent);
                return true;
            }

            Pattern pattern12 = Pattern.compile(AppConstants.PERSONAL_INFO_REGEX);
            Matcher matcher12 = pattern12.matcher(urlWithNoParams);
            if (matcher12.matches()) {
                Intent intent = new Intent(this, RewardsContainerActivity.class);
                intent.putExtra("showProfileInfo", true);
                startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        return false;
    }

    private void getDeepLinkData(final String deepLinkUrl) {
        String urlWithNoParams = deepLinkUrl.split("\\?")[0];
        if (urlWithNoParams.endsWith("/")) {
            urlWithNoParams = urlWithNoParams.substring(0, urlWithNoParams.length() - 1);
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        showProgressDialog("");
        DeepLinkingAPI deepLinkingApi = retrofit.create(DeepLinkingAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<DeepLinkingResposnse> call = deepLinkingApi.getUrlDetails(urlWithNoParams);
        String finalUrlWithNoParams = urlWithNoParams;
        call.enqueue(new Callback<DeepLinkingResposnse>() {
            @Override
            public void onResponse(Call<DeepLinkingResposnse> call,
                    retrofit2.Response<DeepLinkingResposnse> response) {
                removeProgressDialog();
                try {
                    DeepLinkingResposnse responseData = response.body();
                    if (responseData.getCode() == 200 && Constants.SUCCESS
                            .equals(responseData.getStatus())) {
                        identifyTargetScreen(responseData.getData().getResult());
                    } else {
                        launchChromeTabs(finalUrlWithNoParams);
                    }
                } catch (Exception e) {
                    launchChromeTabs(finalUrlWithNoParams);
                }
            }

            @Override
            public void onFailure(Call<DeepLinkingResposnse> call, Throwable t) {
                removeProgressDialog();
                launchChromeTabs(deepLinkUrl);
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

    private void launchChromeTabs(String deepLinkUrl) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            String packageName = CustomTabsHelper.getPackageNameToUse(this);
            if (packageName != null) {
                customTabsIntent.intent.setPackage(packageName);
            }
            customTabsIntent.launchUrl(BaseActivity.this, Uri.parse(deepLinkUrl));
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void identifyTargetScreen(DeepLinkingResult data) {
        switch (data.getType()) {
            case AppConstants.DEEP_LINK_ARTICLE_DETAIL:
                renderArticleDetailScreen(data);
                break;
            case AppConstants.DEEP_LINK_VLOG_DETAIL:
                renderVlogDetailScreen(data);
                break;
            case AppConstants.DEEP_LINK_STORY_DETAILS:
                navigateToShortStory(data);
                break;
            default:
                break;
        }
    }

    private void renderArticleDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getArticle_id())) {
            Intent intent = new Intent(this, ArticleDetailsContainerActivity.class);
            intent.putExtra(Constants.AUTHOR_ID, data.getAuthor_id());
            intent.putExtra(Constants.ARTICLE_ID, data.getArticle_id());
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "DeepLinking");
            intent.putExtra(Constants.ARTICLE_INDEX, "-1");
            intent.putExtra(Constants.FROM_SCREEN, "DeepLinking");
            intent.putExtra(Constants.AUTHOR, data.getAuthor_id() + "~" + data.getAuthor_name());
            startActivity(intent);
        }
    }

    private void renderVlogDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getId())) {
            Intent intent = new Intent(this, ParallelFeedActivity.class);
            intent.putExtra(Constants.VIDEO_ID, data.getId());
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Deep Linking");
            intent.putExtra(Constants.ARTICLE_INDEX, "-1");
            intent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
            intent.putExtra(Constants.AUTHOR, data.getAuthor_id() + "~" + data.getAuthor_name());
            startActivity(intent);
        }
    }

    private void navigateToShortStory(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getArticle_id())) {
            Intent intent = new Intent(this, ShortStoryContainerActivity.class);
            intent.putExtra(Constants.AUTHOR_ID, data.getAuthor_id());
            intent.putExtra(Constants.ARTICLE_ID, data.getArticle_id());
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "DeepLinking");
            intent.putExtra(Constants.ARTICLE_INDEX, "-1");
            intent.putExtra(Constants.FROM_SCREEN, "DeepLinking");
            intent.putExtra(Constants.AUTHOR, data.getAuthor_id() + "~" + data.getAuthor_name());
            startActivity(intent);
        }
    }

    Callback<ShortStoryDetailResult> ssDetailResponseCallback = new Callback<ShortStoryDetailResult>() {
        @Override
        public void onResponse(Call<ShortStoryDetailResult> call,
                retrofit2.Response<ShortStoryDetailResult> response) {
            removeProgressDialog();
            if (response.body() == null) {
                return;
            }
            try {
                ShortStoryDetailResult responseData = response.body();
                Intent intent = new Intent(BaseActivity.this, AddShortStoryActivity.class);
                intent.putExtra("from", "publishedList");
                intent.putExtra("title", responseData.getTitle());
                intent.putExtra("body", responseData.getBody());
                intent.putExtra("articleId", responseData.getId());
                intent.putExtra("tag", new Gson().toJson(responseData.getTags()));
                intent.putExtra("cities", new Gson().toJson(responseData.getCities()));
                startActivity(intent);
            } catch (Exception e) {
                removeProgressDialog();
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ShortStoryDetailResult> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            apiExceptions(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void checkIsBlogSetup() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardApi = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardApi
                .getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(getUserDetailsResponseCallback);
    }

    Callback<UserDetailResponse> getUserDetailsResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            Log.d("SUCCESS", "" + response);
            removeProgressDialog();
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getBlogTitleSlug())) {
                    launchBlogSetup(responseData);
                } else if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getBlogTitleSlug())) {
                    if (StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getEmail())) {
                        launchBlogSetup(responseData);
                    } else if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getEmail())) {
                        launchEditor();
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void launchBlogSetup(UserDetailResponse responseData) {
        Intent intent = new Intent(this, BlogSetupActivity.class);
        intent.putExtra("BlogTitle", responseData.getData().get(0).getResult().getBlogTitle());
        intent.putExtra("email", responseData.getData().get(0).getResult().getEmail());
        intent.putExtra("comingFrom", "ShortStoryAndArticle");
        startActivity(intent);
    }

    private void launchEditor() {
        Intent intent = new Intent(this, NewEditor.class);
        startActivity(intent);
    }
}