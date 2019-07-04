package com.kelltontech.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comscore.analytics.comScore;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.Tracker;
import com.kelltontech.network.Response;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ArticleBlogFollowController;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.parentingstop.ArticleBlogFollowRequest;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.sync.SyncUserInfoService;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.AppSettingsActivity;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupMembershipActivity;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.activity.GroupsReportedContentActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.PublicProfileActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.ViewGroupPostCommentsRepliesActivity;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.mycity4kids.utils.LocaleManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/*import com.mycity4kids.utils.AnalyticsHelper;*/

/**
 * This class is used as base-class for application-base-activity.
 */
public abstract class BaseActivity extends AppCompatActivity implements IScreen, GroupMembershipStatus.IMembershipStatus {

    //    private int activitiesCount = 0;
    public static boolean isAppInFg = false;
    public static boolean isScrInFg = false;
    public static boolean isChangeScrFg = false;
    BaseApplication baseApplication;
    Tracker mTracker;
    private Socket mSocket;
    private Dialog dialog;
    private String userId, title = "", body = "", type = "", id = "", titleSlug = "", blogSlug = "", groupId = "", postId = "", responseId = "", campaignId = "", image_url = "", url = "";
    private int width, height;
    public View layout;
    private WindowManager.LayoutParams params;
    private WindowManager mWindowManager;
    private DisplayMetrics displayMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        baseApplication = (BaseApplication) getApplication();
        checkDrawOverlayPermission();
        //  mTracker=baseApplication.getTracker(BaseApplication.TrackerName.APP_TRACKER);
        Log.i(getClass().getSimpleName(), "onCreate()");
        try {
            mSocket = IO.socket("https://socketio.momspresso.com/?user_id=" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId() + "&mc4kToken=" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getMc4kToken() + "&lang=" + Locale.getDefault().getLanguage() + "&agent=android");
            mSocket.on(SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId(), onNewMessage);
            if (mSocket != null && !mSocket.connected()) {
                mSocket.connect();
            }
        } catch (URISyntaxException e) {
            System.out.println("e--------" + e);
        }
    }

    public void checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 5463);
            }
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String str;
                    JSONObject data = null;

                    try {
                        data = new JSONObject(args[0].toString());
                        System.out.println("data--------" + data);
                        userId = data.getString("user_id");
                        title = data.getString("title");
                        body = data.getString("body");
                        type = data.getString("type");
                        image_url = data.getString("image_url");
                        id = data.getString("id");
                        titleSlug = data.getString("title_slug");
                        blogSlug = data.getString("blog_slug");
                        groupId = data.getString("group_id");
                        postId = data.getString("post_id");
                        responseId = data.getString("response_id");
                        campaignId = data.getString("campaign_id");
                        url = data.getString("url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    LayoutInflater inflater = getLayoutInflater();
                    layout = inflater.inflate(R.layout.dialog_socket_notification, null);
                    int LAYOUT_FLAG;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                    } else {
                        LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
                    }
                    height = displayMetrics.heightPixels;
                    height = (int) (height * 0.18);
                    params = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            height,
                            LAYOUT_FLAG,
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.BOTTOM;
                    mWindowManager = (WindowManager) BaseApplication.getInstance().getActivity().getSystemService(Context.WINDOW_SERVICE);
                    mWindowManager.addView(layout, params);
                    TextView textTitle = layout.findViewById(R.id.textbody);
                    TextView textAuthor = layout.findViewById(R.id.textUpdate);
                    RelativeLayout bottomSheet = layout.findViewById(R.id.bottom_sheet);
                    ImageView cross = layout.findViewById(R.id.cross);
                    ImageView image = layout.findViewById(R.id.image);

                    textTitle.setText(body);
                    textAuthor.setText(title);
                    if (!image_url.isEmpty()) {
                        Picasso.with(BaseActivity.this).load(image_url).placeholder(R.drawable.article_default)
                                .error(R.drawable.article_default).into(image);
                    } else {
                        image.setVisibility(View.GONE);
                    }
                    bottomSheet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            layout.setVisibility(View.GONE);
                            setPubSub();
                        }
                    });
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            layout.setVisibility(View.GONE);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layout.setVisibility(View.GONE);
                        }
                    }, 5000);
                }
            });
        }
    };

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
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "article_details");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("momsights_screen")) {
            Intent intent1 = new Intent(this, RewardsContainerActivity.class);
            startActivity(intent1);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "momsights_screen");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("campaign_listing")) {
            Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
            campaignIntent.putExtra("campaign_listing", "campaign_listing");
            startActivity(campaignIntent);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "campaign_listing");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("campaign_detail")) {
            Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
            campaignIntent.putExtra("campaign_id", campaignId);
            campaignIntent.putExtra("campaign_detail", "campaign_detail");
            campaignIntent.putExtra("fromNotification", false);
            startActivity(campaignIntent);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "campaign_detail");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("campaign_submit_proof")) {
            Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
            campaignIntent.putExtra("campaign_Id", campaignId);
            campaignIntent.putExtra("campaign_submit_proof", "campaign_submit_proof");
            startActivity(campaignIntent);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "campaign_submit_proof");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (type.equalsIgnoreCase("mymoney_bankdetails")) {
            Intent campaignIntent = new Intent(this, RewardsContainerActivity.class);
            campaignIntent.putExtra("isComingfromCampaign", true);
            campaignIntent.putExtra("pageLimit", 4);
            campaignIntent.putExtra("pageNumber", 4);
            campaignIntent.putExtra("campaign_Id", campaignId);
            campaignIntent.putExtra("mymoney_bankdetails", "mymoney_bankdetails");
            startActivity(campaignIntent);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "campaign_submit_proof");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (type.equalsIgnoreCase("mymoney_pancard")) {
            Intent campaignIntent = new Intent(this, RewardsContainerActivity.class);
            campaignIntent.putExtra("isComingFromRewards", true);
            campaignIntent.putExtra("pageLimit", 5);
            campaignIntent.putExtra("pageNumber", 5);
            campaignIntent.putExtra("panCardFormNotification", "mymoney_pancard");
            campaignIntent.putExtra("mymoney_pancard", "mymoney_pancard");
            startActivity(campaignIntent);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "campaign_submit_proof");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "shortStoryDetails");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("video_details")) {
//            String articleId = notificationExtras.getString("id");
//            String authorId = notificationExtras.getString("userId");
            Intent intent1 = new Intent(this, ParallelFeedActivity.class);
            intent1.putExtra("fromNotification", true);
            intent1.putExtra(Constants.VIDEO_ID, id);
            intent1.putExtra(Constants.AUTHOR_ID, userId);
            intent1.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
            intent1.putExtra(Constants.ARTICLE_INDEX, "-1");
            startActivity(intent1);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "video_details");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("group_membership")
                || type.equalsIgnoreCase("group_new_post")
                || type.equalsIgnoreCase("group_admin_group_edit")
                || type.equalsIgnoreCase("group_admin")) {
            GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(BaseActivity.this);
            groupMembershipStatus.checkMembershipStatus(Integer.parseInt(groupId), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "" + type);
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("group_new_response")) {
            Intent gpPostIntent = new Intent(this, GroupPostDetailActivity.class);
            gpPostIntent.putExtra("postId", Integer.parseInt(postId));
            gpPostIntent.putExtra("groupId", Integer.parseInt(groupId));
            gpPostIntent.putExtra("responseId", Integer.parseInt(responseId));
            startActivity(gpPostIntent);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "group_new_response");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("group_new_reply")) {
            Intent gpPostIntent = new Intent(this, ViewGroupPostCommentsRepliesActivity.class);
            gpPostIntent.putExtra("postId", Integer.parseInt(postId));
            gpPostIntent.putExtra("groupId", Integer.parseInt(groupId));
            gpPostIntent.putExtra("responseId", Integer.parseInt(responseId));
            startActivity(gpPostIntent);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "group_new_reply");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("group_admin_membership")) {
            Intent memberIntent = new Intent(this, GroupMembershipActivity.class);
            memberIntent.putExtra("groupId", Integer.parseInt(groupId));
            startActivity(memberIntent);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "group_admin_membership");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("group_admin_reported")) {
            Intent reportIntent = new Intent(this, GroupsReportedContentActivity.class);
            reportIntent.putExtra("groupId", Integer.parseInt(type));
            startActivity(reportIntent);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "group_admin_reported");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("event_details")) {
//            String eventId = notificationExtras.getString("id");
            Intent resultIntent = new Intent(getApplicationContext(), BusinessDetailsActivity.class);
            resultIntent.putExtra("fromNotification", true);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getApplication()));
            resultIntent.putExtra(Constants.BUSINESS_OR_EVENT_ID, id + "");
            resultIntent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
            resultIntent.putExtra(Constants.DISTANCE, "0");
            startActivity(resultIntent);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "event_details");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("webView")) {
//            String url = notificationExtras.getString("url");
            Intent intent1 = new Intent(this, LoadWebViewActivity.class);
            intent1.putExtra("fromNotification", true);
            intent1.putExtra(Constants.WEB_VIEW_URL, url);
            startActivity(intent1);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "webView");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("write_blog")) {
//            launchEditor();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "write_blog");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("profile")) {
//            String u_id = notificationExtras.getString("userId");
            if (!SharedPrefUtils.getUserDetailModel(this).getDynamoId().equals(userId)) {
                Intent intent1 = new Intent(this, PublicProfileActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, userId);
                intent1.putExtra(AppConstants.AUTHOR_NAME, "");
                intent1.putExtra(Constants.FROM_SCREEN, "Notification");
                startActivity(intent1);
            } else {
//                fragmentToLoad = Constants.PROFILE_FRAGMENT;
            }
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "profile");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("upcoming_event_list")) {
//            fragmentToLoad = Constants.BUSINESS_EVENTLIST_FRAGMENT;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "upcoming_event_list");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("suggested_topics")) {
//            fragmentToLoad = Constants.SUGGESTED_TOPICS_FRAGMENT;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "suggested_topics");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase(AppConstants.APP_SETTINGS_DEEPLINK)) {
            Intent intent1 = new Intent(this, AppSettingsActivity.class);
            intent1.putExtra("fromNotification", true);
            intent1.putExtra("load_fragment", Constants.SETTINGS_FRAGMENT);
            startActivity(intent1);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", AppConstants.APP_SETTINGS_DEEPLINK);
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("shortStoryListing")) {
//            fragmentToLoad = Constants.SHORT_STOY_FRAGMENT;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "shortStoryListing");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("group_listing")) {
//            fragmentToLoad = Constants.GROUP_LISTING_FRAGMENT;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("type", "group_listing");
                dialog.dismiss();
//                mMixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        String userType = null;
        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {

        } else {
            if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
            } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
            }
        }

        if (!AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(userType) && !AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(userType)) {
            if ("male".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender()) ||
                    "m".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())) {
                Toast.makeText(this, getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                if (BuildConfig.DEBUG || AppConstants.DEBUGGING_USER_ID.contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {

                } else {
                    return;
                }
            } else {

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
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
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


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    public void replaceFragment(final Fragment fragment, Bundle bundle, boolean isAddToBackStack) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                try {
                    String backStateName = fragment.getClass().getName();
                    boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);

                    if (!fragmentPopped) { // fragment not in back stack, create
                        // it.
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(backStateName);
                        ft.commit();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });

    }

    public void replaceFragmentWithAnimation(final Fragment fragment, Bundle bundle, boolean isAddToBackStack) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                try {
                    String backStateName = fragment.getClass().getName();
                    boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);

                    if (!fragmentPopped) { // fragment not in back stack, create
                        // it.
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_left, R.anim.transition_enter_from_left, R.anim.transition_exit_to_right);
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(backStateName);
                        ft.commit();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });

    }

    public void addFragment(final Fragment fragment, Bundle bundle, boolean isAddToBackStack) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                try {
                    String backStateName = fragment.getClass().getName();
                    boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);

                    if (!fragmentPopped) { // fragment not in back stack, create
                        // it.
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                        ft.setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_left, R.anim.transition_enter_from_left, R.anim.transition_exit_to_right);
                        ft.add(R.id.content_frame, fragment);
                        ft.addToBackStack(backStateName);
                        ft.commit();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });

    }

    public void addFragment(final Fragment fragment, Bundle bundle, boolean isAddToBackStack, final String animationType) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                try {
                    String backStateName = fragment.getClass().getName();
                    boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);

                    if (!fragmentPopped) { // fragment not in back stack, create
                        // it.
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        if (animationType.equals("topToBottom")) {
                            ft.setCustomAnimations(R.anim.transition_enter_from_bottom, R.anim.transition_exit_to_top, R.anim.transition_enter_from_top, R.anim.transition_exit_to_bottom);
                        } else if (animationType.equals("bottomSheet")) {
                            ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down, R.anim.transition_enter_from_top, R.anim.transition_exit_to_bottom);
                        } else {
                            ft.setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_left, R.anim.transition_enter_from_left, R.anim.transition_exit_to_right);
                        }
                        ft.add(R.id.content_frame, fragment);
                        ft.addToBackStack(backStateName);
                        ft.commit();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });

    }

    public void startSyncingUserInfo() {
        Intent intent = new Intent(this, SyncUserInfoService.class);
        startService(intent);
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
        super.onStart();
        /*AnalyticsHelper.onActivityStart(this);
        AnalyticsHelper.setLogEnabled(Constants.IS_GOOGLE_ANALYTICS_ENABLED);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (layout != null) {
            layout.setVisibility(View.GONE);
        }
        //  AnalyticsHelper.onActivityStop(this);
        if (!isScrInFg || !isChangeScrFg) {
            isAppInFg = false;
            onAppPause();
        }
        isScrInFg = false;
    }

    public void onAppStart() {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
//            if (SharedPrefUtils.getUserDetailModel(this).getId() > 0) {
//                startSyncing();
//                startSyncingUserInfo();
//            }

        }
    }

    public void onAppPause() {
        // Code here if required any event, when app geoes to background
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off(SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
            mSocket.disconnect();
        }
        if (layout != null) {
            layout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        comScore.onEnterForeground();
        Log.i(getClass().getSimpleName(), "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        comScore.onExitForeground();
        if (layout != null) {
            layout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(getClass().getSimpleName(), "onNewIntent()");
    }


    /**
     * this method should be called only from UI thread.
     *
     * @param response
     */
    @Override
    public final void handleUiUpdate(final Response response) {
        if (isFinishing()) {
            return;
        }
        if (BuildConfig.DEBUG) {
            updateUi(response);
        } else {
            try {
                updateUi(response);
            } catch (Exception e) {
                Log.i(getClass().getSimpleName(), "updateUi()", e);
            }
        }

    }

    public void showAlertDialog(String title, String message, final OnButtonClicked onButtonClicked) {
        try {
            new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.dismiss();
                            onButtonClicked.onButtonCLick(0);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } catch (Exception e) {

        }

    }

    public void showOkDialog(String title, String message, final OnButtonClicked onButtonClicked) {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                        onButtonClicked.onButtonCLick(0);
                    }
                })
                .show();
    }

    public void showUpgradeAppAlertDialog(String title, String message, final OnButtonClicked onButtonClicked) {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        String appPackage = getPackageName();
                        try {
                            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
                            startActivity(rateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } catch (Exception e) {
                            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
                            startActivity(rateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                        dialog.dismiss();
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Subclass should over-ride this method to update the UI with response
     *
     * @param response
     */
    protected abstract void updateUi(Response response);

    // ////////////////////////////// show and hide ProgressDialog

    private ProgressDialog mProgressDialog;

    /**
     * @param bodyText
     */
    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(BaseActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_SEARCH;
                }
            });
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            try {
                mProgressDialog.show();
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }
    }

    /**
     *
     */
    public void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ////////////////////////////// show and hide key-board

    /**
     *
     */
    protected void showVirturalKeyboard() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (m != null) {
                    m.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        }, 100);
    }

    /**
     *
     */
    protected void hideVirturalKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            if (w != null) {
                w.getLocationOnScreen(scrcoords);
                float x = event.getRawX() + w.getLeft() - scrcoords[0];
                float y = event.getRawY() + w.getTop() - scrcoords[1];

                if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                }
            }
        }
        return ret;
    }

    /**
     * @param message
     */
    Toast toast;

    public void showToast(String message) {
        if (toast != null)
            toast.cancel();

        if (!StringUtils.isNullOrEmpty(message)) {
            toast = Toast.makeText(BaseActivity.this, "" + message, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void showSnackbar(View view, String message) {
        Snackbar
                .make(view, message, Snackbar.LENGTH_LONG)
//                .setAction(R.string.snackbar_action, myOnClickListener)
                .show(); // Don’t forget to show!
    }

    public void followAPICall(String id) {

        ArticleBlogFollowRequest _followRequest = new ArticleBlogFollowRequest();
        _followRequest.setSessionId("" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getSessionId());
        _followRequest.setUserId("" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId());
        _followRequest.setAuthorId("" + id);
        ArticleBlogFollowController _followController = new ArticleBlogFollowController(this, this);
        showProgressDialog(getString(R.string.please_wait));
        _followController.getData(AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST, _followRequest);

    }

}
