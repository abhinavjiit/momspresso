package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.editor.NewEditor;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.NotificationReadRequest;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.NotificationCenterListResponse;
import com.mycity4kids.models.response.NotificationCenterResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ArticleListingActivity;
import com.mycity4kids.ui.activity.BadgeActivity;
import com.mycity4kids.ui.activity.CategoryVideosListingActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.ShortStoriesListingContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryChallengeDetailActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryModerationOrShareActivity;
import com.mycity4kids.ui.activity.SuggestedTopicsActivity;
import com.mycity4kids.ui.activity.TopicsListingActivity;
import com.mycity4kids.ui.activity.ViewGroupPostCommentsRepliesActivity;
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.ui.fragment.GroupsViewFragment;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 21/12/16.
 */
public class NotificationCenterListAdapter extends BaseAdapter implements GroupMembershipStatus.IMembershipStatus {

    private static final String EDITOR_TYPE = "editor_type";
    private Context mainContext;
    private LayoutInflater layoutInflater;
    private ArrayList<NotificationCenterResult> notificationList;
    private FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    public NotificationCenterListAdapter(Context mainContext, ArrayList<NotificationCenterResult> notificationList) {
        layoutInflater = (LayoutInflater) mainContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mainContext = mainContext;
        this.notificationList = notificationList;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.notification_center_list_item, null);
            holder = new ViewHolder();
            holder.rootView = (RelativeLayout) view.findViewById(R.id.rootView);
            holder.notificationTitleTextView = (TextView) view.findViewById(R.id.notificationTitleTextView);
            holder.notificationBodyTextView = (TextView) view.findViewById(R.id.notificationBodyTextView);
            holder.notificationDateTextView = (TextView) view.findViewById(R.id.notificationDateTextView);
            holder.notificationImageView = (ImageView) view.findViewById(R.id.notificationImageView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.notificationTitleTextView.setText(("" + notificationList.get(position).getTitle()).toUpperCase());
        holder.notificationBodyTextView.setText("" + notificationList.get(position).getBody());
        holder.notificationDateTextView
                .setText("" + DateTimeUtils.getMMMDDFormatDate(notificationList.get(position).getCreatedTime()));

        if (!StringUtils.isNullOrEmpty(notificationList.get(position).getThumbNail())) {
            Picasso.get().load(notificationList.get(position).getThumbNail())
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .into(holder.notificationImageView);
        } else {
            Picasso.get().load(R.drawable.default_article).into(holder.notificationImageView);
        }

        if (AppConstants.NOTIFICATION_STATUS_UNREAD.equals(notificationList.get(position).getIsRead())) {
            holder.rootView
                    .setBackgroundColor(ContextCompat.getColor(mainContext, R.color.notification_center_unread_bg));
        } else {
            holder.rootView
                    .setBackgroundColor(ContextCompat.getColor(mainContext, R.color.notification_center_read_bg));
        }

        holder.rootView.setOnClickListener(v -> {
            notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
            hitNotificationReadApi(notificationList.get(position).getId());
            notifyDataSetChanged();

            String notifType = notificationList.get(position).getNotifType();
            if (StringUtils.isNullOrEmpty(notifType)) {
                pushEvent("-");
                return;
            }

            switch (notifType) {
                case AppConstants.NOTIFICATION_CENTER_APP_SETTINGS: {

                }
                break;
                case AppConstants.NOTIFICATION_CENTER_WEB_VIEW: {
                    Intent intent1 = new Intent(mainContext, LoadWebViewActivity.class);
                    intent1.putExtra(Constants.WEB_VIEW_URL, notificationList.get(position).getUrl());
                    mainContext.startActivity(intent1);
                    pushEvent("NOTIFICATION_CENTER_WEB_VIEW");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_ARTICLE_DETAIL: {
                    String authorId;
                    if (StringUtils.isNullOrEmpty(notificationList.get(position).getAuthorId()) || "0"
                            .equals(notificationList.get(position).getAuthorId())) {
                        authorId = notificationList.get(position).getUserId();
                    } else {
                        authorId = notificationList.get(position).getAuthorId();
                    }
                    Intent intent = new Intent(mainContext, ArticleDetailsContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, notificationList.get(position).getArticleId());
                    intent.putExtra(Constants.AUTHOR_ID, authorId);
                    intent.putExtra(Constants.BLOG_SLUG, notificationList.get(position).getBlogTitleSlug());
                    intent.putExtra(Constants.TITLE_SLUG, notificationList.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "NotificationsScreen");
                    intent.putExtra(Constants.FROM_SCREEN, "NotificationsScreen");
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                    intent.putExtra(Constants.AUTHOR, authorId + "~");
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_ARTICLE_DETAIL");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_PROFILE: {
                    String authorId;
                    if (StringUtils.isNullOrEmpty(notificationList.get(position).getAuthorId()) || "0"
                            .equals(notificationList.get(position).getAuthorId())) {
                        authorId = notificationList.get(position).getUserId();
                    } else {
                        authorId = notificationList.get(position).getAuthorId();
                    }
                    Intent profileIntent = new Intent(mainContext, UserProfileActivity.class);
                    profileIntent.putExtra(Constants.USER_ID, authorId);
                    profileIntent.putExtra(AppConstants.BADGE_ID, notificationList.get(position).getBadgeId());
                    profileIntent.putExtra(AppConstants.MILESTONE_ID, notificationList.get(position).getMilestoneId());
                    mainContext.startActivity(profileIntent);
                    pushEvent("NOTIFICATION_CENTER_PROFILE");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_VIDEO_DETAIL: {
                    Intent intent = new Intent(mainContext, ParallelFeedActivity.class);
                    intent.putExtra(Constants.VIDEO_ID, notificationList.get(position).getArticleId());
                    intent.putExtra(Constants.AUTHOR_ID, notificationList.get(position).getAuthorId());
                    intent.putExtra(Constants.FROM_SCREEN, "Home Screen");
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Funny Videos");
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_VIDEO_DETAIL");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_LAUNCH_EDITOR: {
                    launchEditor();
                    pushEvent("NOTIFICATION_CENTER_LAUNCH_EDITOR");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_SUGGESTED_TOPICS: {
                    Intent intent = new Intent(mainContext, SuggestedTopicsActivity.class);
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_SUGGESTED_TOPICS");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_TODAYS_BEST: {
                    Intent intent = new Intent(mainContext, ArticleListingActivity.class);
                    intent.putExtra(Constants.SORT_TYPE, Constants.KEY_TODAYS_BEST);
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_TODAYS_BEST");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_SHORT_STORY_LIST: {
                    Intent resultIntent = new Intent(mainContext, ShortStoriesListingContainerActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    resultIntent.putExtra("fromNotification", true);
                    resultIntent.putExtra("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
                    resultIntent.putExtra("selectedTabCategoryId", "" + notificationList.get(position).getCategoryId());
                    pushEvent("NOTIFICATION_CENTER_SHORT_STORY_LIST");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_SHORT_STORY_DETAILS: {
                    String authorId;
                    if (StringUtils.isNullOrEmpty(notificationList.get(position).getAuthorId())
                            || "0".equals(notificationList.get(position).getAuthorId())) {
                        authorId = notificationList.get(position).getUserId();
                    } else {
                        authorId = notificationList.get(position).getAuthorId();
                    }
                    Intent intent = new Intent(mainContext, ShortStoryContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, notificationList.get(position).getArticleId());
                    intent.putExtra(Constants.AUTHOR_ID, authorId);
                    intent.putExtra(Constants.BLOG_SLUG, notificationList.get(position).getBlogTitleSlug());
                    intent.putExtra(Constants.TITLE_SLUG, notificationList.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "NotificationsScreen");
                    intent.putExtra(Constants.FROM_SCREEN, "NotificationsScreen");
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                    intent.putExtra(Constants.AUTHOR, authorId + "~");
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_SHORT_STORY_DETAILS");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_MEMBERSHIP: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(
                            NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_MEMBERSHIP");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_NEW_POST: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(
                            NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_NEW_POST");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_NEW_RESPONSE: {
                    Intent intent = new Intent(mainContext, GroupPostDetailActivity.class);
                    intent.putExtra("postId", notificationList.get(position).getPostId());
                    intent.putExtra("groupId", notificationList.get(position).getGroupId());
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_GROUP_NEW_RESPONSE");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_NEW_REPLY: {
                    Intent intent = new Intent(mainContext, ViewGroupPostCommentsRepliesActivity.class);
                    intent.putExtra("postId", notificationList.get(position).getPostId());
                    intent.putExtra("groupId", notificationList.get(position).getGroupId());
                    intent.putExtra("responseId", notificationList.get(position).getResponseId());
                    intent.putExtra("action", "commentReply");
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_GROUP_NEW_REPLY");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN_MEMBERSHIP: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(
                            NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN_MEMBERSHIP");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN_REPORTED: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(
                            NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN_REPORTED");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN_EDIT_GROUP: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(
                            NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN_EDIT_GROUP");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(
                            NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_LISTING: {
                    GroupsViewFragment fragment0 = new GroupsViewFragment();
                    Bundle bundle = new Bundle();
                    fragment0.setArguments(bundle);
                    ((DashboardActivity) mainContext).addFragment(fragment0, bundle);
                    pushEvent("NOTIFICATION_CENTER_GROUP_LISTING");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CREATE_SECTION: {

                    pushEvent("NOTIFICATION_CENTER_CREATE_SECTION");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_MY_MONEY_PERSONAL_INFO: {
                    Intent intent = new Intent(mainContext, RewardsContainerActivity.class);
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_MY_MONEY_PERSONAL_INFO");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_TOPICS_ARTICLE_LISTING: {
                    Intent intent = new Intent(mainContext, TopicsListingActivity.class);
                    intent.putExtra("parentTopicId", "" + notificationList.get(position).getCategoryId());
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_TOPICS_ARTICLE_LISTING");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CAMPAIGN_LISTING: {
                    Intent campaignIntent = new Intent(mainContext, CampaignContainerActivity.class);
                    campaignIntent.putExtra("campaign_listing", "campaign_listing");
                    mainContext.startActivity(campaignIntent);
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_LISTING");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CAMPAIGN_DETAIL: {
                    Intent campaignIntent = new Intent(mainContext, CampaignContainerActivity.class);
                    campaignIntent.putExtra("campaign_id", notificationList.get(position).getCampaign_id() + "");
                    campaignIntent.putExtra("campaign_detail", "campaign_detail");
                    campaignIntent.putExtra("fromNotification", true);
                    mainContext.startActivity(campaignIntent);
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_DETAIL");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CAMPAIGN_SUBMIT_PROOF: {
                    Intent campaignIntent = new Intent(mainContext, CampaignContainerActivity.class);
                    campaignIntent.putExtra("campaign_Id", notificationList.get(position).getCampaign_id() + "");
                    campaignIntent.putExtra("campaign_submit_proof", "campaign_submit_proof");
                    mainContext.startActivity(campaignIntent);
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_SUBMIT_PROOF");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CAMPAIGN_PANCARD: {
                    Intent campaignIntent = new Intent(mainContext, RewardsContainerActivity.class);
                    campaignIntent.putExtra("isComingFromRewards", true);
                    campaignIntent.putExtra("pageLimit", 5);
                    campaignIntent.putExtra("pageNumber", 5);
                    campaignIntent.putExtra("panCardFormNotification", "mymoney_pancard");
                    campaignIntent.putExtra("mymoney_pancard", "mymoney_pancard");
                    mainContext.startActivity(campaignIntent);
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_PANCARD");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CAMPAIGN_BANKDETAIL: {
                    Intent campaignIntent = new Intent(mainContext, RewardsContainerActivity.class);
                    campaignIntent.putExtra("isComingfromCampaign", true);
                    campaignIntent.putExtra("pageLimit", 4);
                    campaignIntent.putExtra("pageNumber", 4);
                    campaignIntent.putExtra("campaign_Id", notificationList.get(position).getCampaign_id() + "");
                    campaignIntent.putExtra("mymoney_bankdetails", "mymoney_bankdetails");
                    mainContext.startActivity(campaignIntent);
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_BANKDETAIL");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_VIDEO_CHALLENGE_DETAIL: {
                    Intent videoChallengeIntent = new Intent(mainContext, NewVideoChallengeActivity.class);
                    videoChallengeIntent
                            .putExtra(Constants.CHALLENGE_ID, "" + notificationList.get(position).getChallengeId());
                    videoChallengeIntent.putExtra("comingFrom", "notification");
                    mainContext.startActivity(videoChallengeIntent);
                    pushEvent("NOTIFICATION_CENTER_VIDEO_CHALLENGE_DETAIL");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_COLLECTION_DETAIL: {
                    Intent intent = new Intent(mainContext, UserCollectionItemListActivity.class);
                    intent.putExtra("id", "" + notificationList.get(position).getCollectionId());
                    intent.putExtra("comingFrom", "notification");
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_COLLECTION_DETAIL");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_BADGE_LISTING: {
                    Intent intent = new Intent(mainContext, BadgeActivity.class);
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_BADGE_LISTING");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_STORY_PUBLISH_SUCCESS: {
                    Intent intent = new Intent(mainContext, ShortStoryModerationOrShareActivity.class);
                    intent.putExtra("shareUrl", "");
                    intent.putExtra(Constants.ARTICLE_ID, notificationList.get(position).getArticleId());
                    mainContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_STORY_PUBLISH_SUCCESS");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_STORY_CHALLENGE_DETAIL: {
                    if (!StringUtils.isNullOrEmpty(notificationList.get(position).getChallengeId())) {
                        Intent intent = new Intent(mainContext, ShortStoryChallengeDetailActivity.class);
                        intent.putExtra("challenge", notificationList.get(position).getChallengeId());
                        mainContext.startActivity(intent);
                        pushEvent("NOTIFICATION_CENTER_STORY_CHALLENGE_DETAIL");
                    }
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_INVITE_FRIENDS: {
                    Intent profileIntent = new Intent(mainContext, UserProfileActivity.class);
                    profileIntent.putExtra(AppConstants.SHOW_INVITE_DIALOG_FLAG, true);
                    mainContext.startActivity(profileIntent);
                    pushEvent("NOTIFICATION_CENTER_INVITE_FRIENDS");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_VIDEO_LISTING: {
                    Intent vlogsIntent = new Intent(mainContext, CategoryVideosListingActivity.class);
                    vlogsIntent.putExtra("categoryId", "" + notificationList.get(position).getCategoryId());
                    mainContext.startActivity(vlogsIntent);
                    pushEvent("NOTIFICATION_CENTER_VIDEO_LISTING");
                }
                break;
                default:
                    break;
            }
        });
        return view;
    }

    private void pushEvent(String type) {
        Utils.pushNotificationCenterItemClickEvent(mainContext, type,
                SharedPrefUtils.getUserDetailModel(mainContext).getDynamoId(), "NotificationCenterListAdapter");
    }

    private void launchEditor() {
        String editorType = firebaseRemoteConfig.getString(EDITOR_TYPE);
        if ((!StringUtils.isNullOrEmpty(editorType) && "1".equals(editorType)) || AppUtils
                .isUserBucketedInNewEditor(firebaseRemoteConfig)) {
            Bundle bundle5 = new Bundle();
            bundle5.putString("TITLE_PARAM", "");
            bundle5.putString("CONTENT_PARAM", "");
            bundle5.putString("TITLE_PLACEHOLDER_PARAM",
                    mainContext.getString(R.string.example_post_title_placeholder));
            bundle5.putString("CONTENT_PLACEHOLDER_PARAM",
                    mainContext.getString(R.string.example_post_content_placeholder));
            bundle5.putInt("EDITOR_PARAM", NewEditor.USE_NEW_EDITOR);
            bundle5.putString("from", "dashboard");
            Intent intent1 = new Intent(mainContext, NewEditor.class);
            intent1.putExtras(bundle5);
            mainContext.startActivity(intent1);
        } else {
            Bundle bundle5 = new Bundle();
            bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
            bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
            bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                    mainContext.getString(R.string.example_post_title_placeholder));
            bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                    mainContext.getString(R.string.example_post_content_placeholder));
            bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
            bundle5.putString("from", "dashboard");
            Intent intent1 = new Intent(mainContext, EditorPostActivity.class);
            intent1.putExtras(bundle5);
            mainContext.startActivity(intent1);
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
                Toast.makeText(mainContext, mainContext.getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                if (!BuildConfig.DEBUG && !AppConstants.DEBUGGING_USER_ID
                        .contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {
                    return;
                }
            }
        }

        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
            Intent intent = new Intent(mainContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mainContext.startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            Toast.makeText(mainContext, mainContext.getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT)
                    .show();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(mainContext, GroupDetailsActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mainContext.startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION
                .equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(mainContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            intent.putExtra("pendingMembershipFlag", true);
            mainContext.startActivity(intent);
        } else {
            Intent intent = new Intent(mainContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mainContext.startActivity(intent);
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

    }

    class ViewHolder {

        RelativeLayout rootView;
        TextView notificationTitleTextView;
        TextView notificationBodyTextView;
        TextView notificationDateTextView;
        ImageView notificationImageView;
    }

    private void hitNotificationReadApi(String notificationCenterId) {
        NotificationReadRequest notificationReadRequest = new NotificationReadRequest();
        notificationReadRequest.setNotifId(notificationCenterId);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsApi = retrofit.create(NotificationsAPI.class);
        Call<NotificationCenterListResponse> call = notificationsApi.markNotificationAsRead(notificationReadRequest);
        call.enqueue(markNotificationReadResponseCallback);
    }

    private Callback<NotificationCenterListResponse> markNotificationReadResponseCallback =
            new Callback<NotificationCenterListResponse>() {
                @Override
                public void onResponse(Call<NotificationCenterListResponse> call,
                        retrofit2.Response<NotificationCenterListResponse> response) {
                    if (response.body() == null) {
                        return;
                    }
                    try {
                        NotificationCenterListResponse responseData = response.body();
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<NotificationCenterListResponse> call, Throwable t) {
                    Crashlytics.logException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };
}




