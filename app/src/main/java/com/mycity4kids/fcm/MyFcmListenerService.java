package com.mycity4kids.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.newmodels.PushNotificationModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.ui.ContentCommentReplyNotificationActivity;
import com.mycity4kids.ui.activity.AppSettingsActivity;
import com.mycity4kids.ui.activity.ArticleChallengeDetailActivity;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.BadgeActivity;
import com.mycity4kids.ui.activity.CategoryVideosListingActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.ui.activity.MyTotalEarningActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.ShortStoriesListingContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryChallengeDetailActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryModerationOrShareActivity;
import com.mycity4kids.ui.activity.SplashActivity;
import com.mycity4kids.ui.activity.TopicsListingActivity;
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.vlogs.VideoCategoryAndChallengeSelectionActivity;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by anshul on 5/26/16.
 */
public class MyFcmListenerService extends FirebaseMessagingService {

    Bitmap bitmap;

    @Override
    public void onNewToken(@NonNull String fcmToken) {
        super.onNewToken(fcmToken);
        SharedPrefUtils.setDeviceToken(BaseApplication.getAppContext(), fcmToken);
        Intent intent = new Intent(this, PushTokenService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                startForegroundService(intent);
            } catch (IllegalArgumentException e) {
                startService(intent);
            }
        } else {
            startService(intent);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String msg = remoteMessage.getData().get("message");
        if (msg == null) {
            msg = remoteMessage.getData().toString();
        }

        if (StringUtils.isNullOrEmpty(msg)) {
            return;
        }
        PushNotificationModel pushNotificationModel;
        try {
            pushNotificationModel = new Gson().fromJson(msg, PushNotificationModel.class);
        } catch (JsonSyntaxException jse) {
            try {
                Log.e("PUSH_DATA", "***---***--msg--" + msg);
                Log.e("PUSH_DATA", "***---***--remoteMsg--" + remoteMessage.getData().toString());
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
            try {
                pushNotificationModel = new Gson()
                        .fromJson(new Gson().toJson(remoteMessage.getData()), PushNotificationModel.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                return;
            }
        }
        try {
            if (pushNotificationModel != null) {
                String type = pushNotificationModel.getType();
                Log.d("NOTI_TYPE", "CONTENT ===== " + msg);
                Log.d("NOTI_TYPE", "type ===== " + type);
                Intent intent;
                PendingIntent contentIntent;
                if (AppConstants.NOTIFICATION_TYPE_VIDEO_CHALLENGE_DETAILS.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), NewVideoChallengeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(Constants.CHALLENGE_ID, "" + pushNotificationModel.getChallengeId());
                        intent.putExtra("comingFrom", "notification");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        stackBuilder.editIntentAt(1).putExtra("parentTopicId", AppConstants.HOME_VIDEOS_CATEGORYID);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "video_challenge_details ----- Notification Message --- ",
                            "video_challenge_details ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_CHOOSE_VIDEO_CATEGORY.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), VideoCategoryAndChallengeSelectionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra("comingFrom", "notification");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "momvlog_challenge_details ----- Notification Message --- ",
                            "momvlog_challenge_details ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_ARTICLE_DETAILS.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), ArticleDetailsContainerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(Constants.ARTICLE_ID, "" + pushNotificationModel.getId());
                        intent.putExtra(Constants.AUTHOR_ID, "" + pushNotificationModel.getUser_id());
                        intent.putExtra(Constants.BLOG_SLUG, pushNotificationModel.getBlogPageSlug());
                        intent.putExtra(Constants.TITLE_SLUG, pushNotificationModel.getTitleSlug());
                        intent.putExtra(Constants.FROM_SCREEN, "Notification");
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                        intent.putExtra(Constants.ARTICLE_INDEX, "-1");
                        intent.putExtra(Constants.AUTHOR, pushNotificationModel.getUser_id() + "~");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(ArticleDetailsContainerActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "article_details ----- Notification Message --- ",
                            "article_details ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_COLLECTION_DETAILS.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), UserCollectionItemListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra("id", pushNotificationModel.getCollectionId());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(UserCollectionItemListActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "collection_detail ----- Notification Message --- ",
                            "collection_detail ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_VIDEO_DETAILS.equalsIgnoreCase(type)) {
                    Utils.pushNotificationClickEvent(this, "video_details",
                            SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "MyFcmListenerService");
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), ParallelFeedActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(Constants.VIDEO_ID, "" + pushNotificationModel.getId());
                        intent.putExtra(Constants.STREAM_URL, pushNotificationModel.getUrl());
                        intent.putExtra(Constants.AUTHOR_ID, "" + pushNotificationModel.getUser_id());
                        intent.putExtra(Constants.FROM_SCREEN, "Notification");
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                        intent.putExtra(Constants.ARTICLE_INDEX, "-1");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(ParallelFeedActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "video_details ----- Notification Message --- ",
                            "video_details ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_WEBVIEW.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), LoadWebViewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(Constants.WEB_VIEW_URL, pushNotificationModel.getUrl());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(LoadWebViewActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "webView ----- Notification Message --- ", "webView ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_PROFILE.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(Constants.USER_ID, pushNotificationModel.getUser_id());
                        intent.putExtra(AppConstants.BADGE_ID, pushNotificationModel.getBadgeId());
                        intent.putExtra(AppConstants.MILESTONE_ID, pushNotificationModel.getMilestoneId());
                        intent.putExtra(Constants.FROM_SCREEN, "Notification");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(UserProfileActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "profile ----- Notification Message --- ", "profile ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_BADGE_LIST.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), BadgeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(Constants.FROM_SCREEN, "Notification");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(BadgeActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "badge_list ----- Notification Message --- ", "profile ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_APP_SETTINGS.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), AppSettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra("load_fragment", Constants.SETTINGS_FRAGMENT);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(AppSettingsActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "app_settings ----- Notification  --- ", "app_settings ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_MOMSIGHT_REWARD_LISTING.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), RewardsContainerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(RewardsContainerActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "momsights_screen ----- Notification Message --- ",
                            "momsights_screen ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_CAMPAIGN_LISTING.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), CampaignContainerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(CampaignContainerActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "campaign_listing ----- Notification Message --- ",
                            "campaign_listing ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_CATEGORY_LISTING.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), TopicsListingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("parentTopicId", pushNotificationModel.getCategoryId());
                        intent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(TopicsListingActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "category_listing ----- Notification Message --- ",
                            "category_listing ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_CAMPAIGN_SUBMIT_PROOF.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), CampaignContainerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra("campaign_id", pushNotificationModel.getCampaign_id());
                        intent.putExtra("campaign_submit_proof", "campaign_submit_proof");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(CampaignContainerActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "campaign_submit_proof ----- Notification Message --- ",
                            "campaign_submit_proof ----- Notification MixFeedData");
                } else if (type.equalsIgnoreCase("mymoney_bankdetails")) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), RewardsContainerActivity.class);
                        intent.putExtra("isComingfromCampaign", true);
                        intent.putExtra("pageLimit", 4);
                        intent.putExtra("pageNumber", 4);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(RewardsContainerActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "mymoney_bankdetails ----- Notification Message --- ",
                            "mymoney_bankdetails ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_CAMPAIGN_DETAIL.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), CampaignContainerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra("campaign_id", "" + pushNotificationModel.getCampaign_id());
                        intent.putExtra("campaign_detail", "campaign_detail");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "campaign_detail ----- Notification Message --- ",
                            "campaign_detail ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_MY_MONEY_EARNINGS.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), MyTotalEarningActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "my_money_earnings ----- Notification Message --- ",
                            "my_money_earnings ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_MY_MONEY_PROFILE.equalsIgnoreCase(type)) {
                    //Add my money profile edit option
                } else if (AppConstants.NOTIFICATION_TYPE_SHORT_STORY_LIST.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), ShortStoriesListingContainerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
                        intent.putExtra("selectedTabCategoryId", pushNotificationModel.getCategoryId());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "shortStoryListing ----- Notification Message --- ",
                            "shortStoryListing ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_STORY_LIST_IN_CHALLENGE.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), ShortStoryChallengeDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra("challenge", pushNotificationModel.getCategoryId());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        stackBuilder.editIntentAt(0).putExtra("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "shortStoryListingInChallengeListing ----- Notification Message --- ",
                            "shortStoryListingInChallengeListing ----- Notification MixFeedData");
                } else if (type.equalsIgnoreCase("shortStoryPublishSuccess")) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), ShortStoryModerationOrShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra("shareUrl", "");
                        intent.putExtra(Constants.ARTICLE_ID, pushNotificationModel.getId());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "shortStoryPublishSuccess ----- Notification Message --- ",
                            "shortStoryPublishSuccess ----- Notification MixFeedData");
                } else if (type.equalsIgnoreCase("group_membership") || type.equalsIgnoreCase("group_new_post")
                        || type.equalsIgnoreCase("group_admin_group_edit") || type.equalsIgnoreCase("group_admin")
                        || type.equalsIgnoreCase("group_new_response") || type.equalsIgnoreCase("group_new_reply")
                        || type.equalsIgnoreCase("group_admin_membership")
                        || type.equalsIgnoreCase("group_admin_reported") || "write_blog".equalsIgnoreCase(type)
                        || "suggested_topics".equalsIgnoreCase(type) || "group_listing".equalsIgnoreCase(type)) {
                    //No notification pop for these type when app is open.
                } else if (AppConstants.NOTIFICATION_TYPE_REMOTE_CONFIG_SILENT_UPDATE.equalsIgnoreCase(type)) {
                    SharedPrefUtils.setFirebaseRemoteConfigUpdateFlag(BaseApplication.getAppContext(), true);
                } else if (AppConstants.NOTIFICATION_TYPE_SHORT_STORY_DETAILS.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), ShortStoryContainerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(Constants.ARTICLE_ID, "" + pushNotificationModel.getId());
                        intent.putExtra(Constants.AUTHOR_ID, "" + pushNotificationModel.getUser_id());
                        intent.putExtra(Constants.BLOG_SLUG, pushNotificationModel.getBlogPageSlug());
                        intent.putExtra(Constants.TITLE_SLUG, pushNotificationModel.getTitleSlug());
                        intent.putExtra(Constants.FROM_SCREEN, "Notification");
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                        intent.putExtra(Constants.ARTICLE_INDEX, "-1");
                        intent.putExtra(Constants.AUTHOR, pushNotificationModel.getUser_id() + "~");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(ArticleDetailsContainerActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "shortStoryDetails ----- Notification Message --- ",
                            "shortStoryDetails ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_INVITE_FRIENDS.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra(AppConstants.SHOW_INVITE_DIALOG_FLAG, true);
                        intent.putExtra("source", "notification");
                        intent.putExtra(Constants.FROM_SCREEN, "Notification");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(UserProfileActivity.class);
                        stackBuilder.addNextIntent(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "showInviteDialogFlag ----- Notification Message --- ",
                            "showInviteDialogFlag ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_VIDEO_LISTING.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), CategoryVideosListingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fromNotification", true);
                        intent.putExtra("categoryId", pushNotificationModel.getCategoryId());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "videoListing ----- Notification Message --- ",
                            "videoListing ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_CONTENT_COMMENTS.equalsIgnoreCase(type)
                        || AppConstants.NOTIFICATION_TYPE_CONTENT_REPLY.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), ContentCommentReplyNotificationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("articleId", pushNotificationModel.getId());
                        intent.putExtra("commentId", pushNotificationModel.getCommentId());
                        intent.putExtra("type", pushNotificationModel.getType());
                        intent.putExtra("contentType", pushNotificationModel.getContentType());
                        intent.putExtra("replyId", pushNotificationModel.getReplyId());
                        intent.putExtra("authorId", pushNotificationModel.getContentAuthor());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "commentListing ----- Notification Message --- ",
                            "commentListing ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_PERSONAL_INFO.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), RewardsContainerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("showProfileInfo", true);
                        intent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "personal_info ----- Notification Message --- ",
                            "personal_info ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_ARTICLE_CHALLENGE.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), ArticleChallengeDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("articleChallengeId", pushNotificationModel.getChallengeId());
                        intent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "personal_info ----- Notification Message --- ",
                            "personal_info ----- Notification MixFeedData");
                } else if (AppConstants.NOTIFICATION_TYPE_LIVE_STREAM.equalsIgnoreCase(type)) {
                    if (SharedPrefUtils.getAppUpgrade(BaseApplication.getAppContext())) {
                        contentIntent = handleForcedUpdate();
                    } else {
                        intent = new Intent(getApplicationContext(), BaseActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("eventId", pushNotificationModel.getEventId());
                        intent.putExtra("fromNotification", true);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    handleNotificationAccordingToStructure(remoteMessage, pushNotificationModel, contentIntent,
                            "personal_info ----- Notification Message --- ",
                            "personal_info ----- Notification MixFeedData");
                } else {
                    Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT,
                            SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "default");
                    final String message = pushNotificationModel.getMessage_id();
                    final String title = pushNotificationModel.getTitle();
                    intent = new Intent(getApplicationContext(), SplashActivity.class);
                    intent.putExtra("fromNotification", true);
                    contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Log.e("NOTIFICATION_TYPE", "upcoming_event_list ----- Notification Message");
                    prepareNotification(title, message, pushNotificationModel.getRich_image_url(), contentIntent,
                            pushNotificationModel.getSound());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PendingIntent handleForcedUpdate() {
        Intent intent;
        PendingIntent contentIntent;
        intent = new Intent(getApplicationContext(), SplashActivity.class);
        intent.putExtra("fromNotification", true);
        contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return contentIntent;
    }

    private void handleNotificationAccordingToStructure(RemoteMessage remoteMessage,
            PushNotificationModel pushNotificationModel, PendingIntent contentIntent, String s, String s2) {
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.e("NOTIFICATION_TYPE",
                    s + remoteMessage
                            .getNotification().getImageUrl());
            if (remoteMessage.getNotification().getImageUrl() != null) {
                prepareNotification(title, body, remoteMessage.getNotification().getImageUrl().toString(),
                        contentIntent, pushNotificationModel.getSound());
            } else {
                prepareNotification(title, body, pushNotificationModel.getRich_image_url(), contentIntent,
                        pushNotificationModel.getSound());
            }
        } else {
            Log.e("NOTIFICATION_TYPE", s2);
            prepareNotification(pushNotificationModel.getTitle(), pushNotificationModel.getBody(),
                    pushNotificationModel.getRich_image_url(), contentIntent, pushNotificationModel.getSound());
        }
    }

    void prepareNotification(String title, String message, String imageUrl, PendingIntent pendingIntent, String sound) {
        try {
            URL url = new URL(imageUrl);
            bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }

        Uri soundUri = Uri
                .parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/"
                        + R.raw.coin);
        NotificationManager notificationManager1 =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);
            notificationManager1.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            builder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                    .setSmallIcon(R.drawable.icon_notify) // notification icon
                    .setContentTitle(title) // title for notification
                    .setContentText(message)// message for notification
                    .setAutoCancel(true) // clear notification after click
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setContentIntent(pendingIntent);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                    .setSmallIcon(R.drawable.icon_notify) // notification icon
                    .setContentTitle(title) // title for notification
                    .setContentText(message)// message for notification
                    .setAutoCancel(true) // clear notification after click
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setContentIntent(pendingIntent);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }
}