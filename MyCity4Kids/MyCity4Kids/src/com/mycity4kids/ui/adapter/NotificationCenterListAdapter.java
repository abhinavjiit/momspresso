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
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.EditorPostActivity;
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
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.ShortStoriesListingContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.SuggestedTopicsActivity;
import com.mycity4kids.ui.activity.TopicsListingActivity;
import com.mycity4kids.ui.activity.ViewGroupPostCommentsRepliesActivity;
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.ui.fragment.GroupsViewFragment;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 21/12/16.
 */
public class NotificationCenterListAdapter extends BaseAdapter implements GroupMembershipStatus.IMembershipStatus {

    private MixpanelAPI mixpanel;
    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<NotificationCenterResult> notificationList;

    public NotificationCenterListAdapter(Context pContext, ArrayList<NotificationCenterResult> notificationList) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.notificationList = notificationList;
        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
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
            view = mInflator.inflate(R.layout.notification_center_list_item, null);
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
        holder.notificationDateTextView.setText("" + DateTimeUtils.getMMMDDFormatDate(notificationList.get(position).getCreatedTime()));

        if (!StringUtils.isNullOrEmpty(notificationList.get(position).getThumbNail())) {
            Picasso.with(mContext).load(notificationList.get(position).getThumbNail())
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.notificationImageView);
        } else {
            Picasso.with(mContext).load(R.drawable.default_article).into(holder.notificationImageView);
        }

        if (AppConstants.NOTIFICATION_STATUS_UNREAD.equals(notificationList.get(position).getIsRead())) {
            holder.rootView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.notification_center_unread_bg));
        } else {
            holder.rootView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.notification_center_read_bg));
        }

        holder.rootView.setOnClickListener(v -> {
            notificationList.get(position).setIsRead(AppConstants.NOTIFICATION_STATUS_READ);
            hitNotificationReadAPI(notificationList.get(position).getId());
            notifyDataSetChanged();

            String nType = notificationList.get(position).getNotifType();
            if (StringUtils.isNullOrEmpty(nType)) {
                pushEvent("-");
                return;
            }

            switch (nType) {
                case AppConstants.NOTIFICATION_CENTER_APP_SETTINGS: {

                }
                break;
                case AppConstants.NOTIFICATION_CENTER_WEB_VIEW: {
                    Intent intent1 = new Intent(mContext, LoadWebViewActivity.class);
                    intent1.putExtra(Constants.WEB_VIEW_URL, notificationList.get(position).getUrl());
                    mContext.startActivity(intent1);
                    pushEvent("NOTIFICATION_CENTER_WEB_VIEW");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_ARTICLE_DETAIL: {
                    String authorId;
                    if (StringUtils.isNullOrEmpty(notificationList.get(position).getAuthorId()) || "0".equals(notificationList.get(position).getAuthorId())) {
                        authorId = notificationList.get(position).getUserId();
                    } else {
                        authorId = notificationList.get(position).getAuthorId();
                    }
                    Intent intent = new Intent(mContext, ArticleDetailsContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, notificationList.get(position).getArticleId());
                    intent.putExtra(Constants.AUTHOR_ID, authorId);
                    intent.putExtra(Constants.BLOG_SLUG, notificationList.get(position).getBlogTitleSlug());
                    intent.putExtra(Constants.TITLE_SLUG, notificationList.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "NotificationsScreen");
                    intent.putExtra(Constants.FROM_SCREEN, "NotificationsScreen");
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                    intent.putExtra(Constants.AUTHOR, authorId + "~");
                    mContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_ARTICLE_DETAIL");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_PROFILE: {
                    String authorId;
                    if (StringUtils.isNullOrEmpty(notificationList.get(position).getAuthorId()) || "0".equals(notificationList.get(position).getAuthorId())) {
                        authorId = notificationList.get(position).getUserId();
                    } else {
                        authorId = notificationList.get(position).getAuthorId();
                    }
                    Intent pIntent = new Intent(mContext, UserProfileActivity.class);
                    pIntent.putExtra(Constants.USER_ID, authorId);
                    pIntent.putExtra(AppConstants.BADGE_ID, notificationList.get(position).getBadgeId());
                    pIntent.putExtra(AppConstants.BADGE_ID, notificationList.get(position).getMilestoneId());
                    mContext.startActivity(pIntent);
                    pushEvent("NOTIFICATION_CENTER_PROFILE");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_VIDEO_DETAIL: {
                    Intent intent = new Intent(mContext, ParallelFeedActivity.class);
                    intent.putExtra(Constants.VIDEO_ID, notificationList.get(position).getArticleId());
                    intent.putExtra(Constants.AUTHOR_ID, notificationList.get(position).getAuthorId());
                    intent.putExtra(Constants.FROM_SCREEN, "Home Screen");
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Funny Videos");
                    mContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_VIDEO_DETAIL");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_LAUNCH_EDITOR: {
                    launchEditor();
                    pushEvent("NOTIFICATION_CENTER_LAUNCH_EDITOR");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_SUGGESTED_TOPICS: {
                    Intent intent = new Intent(mContext, SuggestedTopicsActivity.class);
                    mContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_SUGGESTED_TOPICS");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_TODAYS_BEST: {
                    Intent intent = new Intent(mContext, ArticleListingActivity.class);
                    intent.putExtra(Constants.SORT_TYPE, Constants.KEY_TODAYS_BEST);
                    mContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_TODAYS_BEST");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_SHORT_STORY_LIST: {
                    Intent resultIntent = new Intent(mContext, ShortStoriesListingContainerActivity.class);
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
                    Intent intent = new Intent(mContext, ShortStoryContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, notificationList.get(position).getArticleId());
                    intent.putExtra(Constants.AUTHOR_ID, authorId);
                    intent.putExtra(Constants.BLOG_SLUG, notificationList.get(position).getBlogTitleSlug());
                    intent.putExtra(Constants.TITLE_SLUG, notificationList.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "NotificationsScreen");
                    intent.putExtra(Constants.FROM_SCREEN, "NotificationsScreen");
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                    intent.putExtra(Constants.AUTHOR, authorId + "~");
                    mContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_SHORT_STORY_DETAILS");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_MEMBERSHIP: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_MEMBERSHIP");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_NEW_POST: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_NEW_POST");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_NEW_RESPONSE: {
                    Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                    intent.putExtra("postId", notificationList.get(position).getPostId());
                    intent.putExtra("groupId", notificationList.get(position).getGroupId());
                    mContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_GROUP_NEW_RESPONSE");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_NEW_REPLY: {
                    Intent intent = new Intent(mContext, ViewGroupPostCommentsRepliesActivity.class);
                    intent.putExtra("postId", notificationList.get(position).getPostId());
                    intent.putExtra("groupId", notificationList.get(position).getGroupId());
                    intent.putExtra("responseId", notificationList.get(position).getResponseId());
                    intent.putExtra("action", "commentReply");
                    mContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_GROUP_NEW_REPLY");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN_MEMBERSHIP: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN_MEMBERSHIP");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN_REPORTED: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN_REPORTED");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN_EDIT_GROUP: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN_EDIT_GROUP");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN: {
                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(NotificationCenterListAdapter.this);
                    groupMembershipStatus.checkMembershipStatus(notificationList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_GROUP_LISTING: {
                    GroupsViewFragment fragment0 = new GroupsViewFragment();
                    Bundle mBundle0 = new Bundle();
                    fragment0.setArguments(mBundle0);
                    ((DashboardActivity) mContext).addFragment(fragment0, mBundle0, true);
                    pushEvent("NOTIFICATION_CENTER_GROUP_LISTING");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CREATE_SECTION: {

                    pushEvent("NOTIFICATION_CENTER_CREATE_SECTION");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_MY_MONEY_PERSONAL_INFO: {
                    Intent intent = new Intent(mContext, RewardsContainerActivity.class);
                    mContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_MY_MONEY_PERSONAL_INFO");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_TOPICS_ARTICLE_LISTING: {
                    Intent intent = new Intent(mContext, TopicsListingActivity.class);
                    intent.putExtra("parentTopicId", "" + notificationList.get(position).getCategoryId());
                    mContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_TOPICS_ARTICLE_LISTING");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CAMPAIGN_LISTING: {
                    Intent campaignIntent = new Intent(mContext, CampaignContainerActivity.class);
                    campaignIntent.putExtra("campaign_listing", "campaign_listing");
                    mContext.startActivity(campaignIntent);
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_LISTING");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CAMPAIGN_DETAIL: {
                    Intent campaignIntent = new Intent(mContext, CampaignContainerActivity.class);
                    campaignIntent.putExtra("campaign_id", notificationList.get(position).getCampaign_id() + "");
                    campaignIntent.putExtra("campaign_detail", "campaign_detail");
                    campaignIntent.putExtra("fromNotification", true);
                    mContext.startActivity(campaignIntent);
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_DETAIL");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CAMPAIGN_SUBMIT_PROOF: {
                    Intent campaignIntent = new Intent(mContext, CampaignContainerActivity.class);
                    campaignIntent.putExtra("campaign_Id", notificationList.get(position).getCampaign_id() + "");
                    campaignIntent.putExtra("campaign_submit_proof", "campaign_submit_proof");
                    mContext.startActivity(campaignIntent);
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_SUBMIT_PROOF");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CAMPAIGN_PANCARD: {
                    Intent campaignIntent = new Intent(mContext, RewardsContainerActivity.class);
                    campaignIntent.putExtra("isComingFromRewards", true);
                    campaignIntent.putExtra("pageLimit", 5);
                    campaignIntent.putExtra("pageNumber", 5);
                    campaignIntent.putExtra("panCardFormNotification", "mymoney_pancard");
                    campaignIntent.putExtra("mymoney_pancard", "mymoney_pancard");
                    mContext.startActivity(campaignIntent);
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_PANCARD");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_CAMPAIGN_BANKDETAIL: {
                    Intent campaignIntent = new Intent(mContext, RewardsContainerActivity.class);
                    campaignIntent.putExtra("isComingfromCampaign", true);
                    campaignIntent.putExtra("pageLimit", 4);
                    campaignIntent.putExtra("pageNumber", 4);
                    campaignIntent.putExtra("campaign_Id", notificationList.get(position).getCampaign_id() + "");
                    campaignIntent.putExtra("mymoney_bankdetails", "mymoney_bankdetails");
                    mContext.startActivity(campaignIntent);
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_BANKDETAIL");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_VIDEO_CHALLENGE_DETAIL: {
                    Intent videoChallengeIntent = new Intent(mContext, NewVideoChallengeActivity.class);
                    videoChallengeIntent.putExtra(Constants.CHALLENGE_ID, "" + notificationList.get(position).getCategoryId());
                    videoChallengeIntent.putExtra("comingFrom", "notification");
                    mContext.startActivity(videoChallengeIntent);
                    pushEvent("NOTIFICATION_CENTER_VIDEO_CHALLENGE_DETAIL");
                }
                break;
                case AppConstants.NOTIFICATION_CENTER_COLLECTION_DETAIL: {
                    Intent intent = new Intent(mContext, UserCollectionItemListActivity.class);
                    intent.putExtra("id", "" + notificationList.get(position).getId());
                    intent.putExtra("comingFrom", "notification");
                    mContext.startActivity(intent);
                    pushEvent("NOTIFICATION_CENTER_COLLECTION_DETAIL");
                }
                break;
            }
        });
        return view;
    }

    private void pushEvent(String type) {
        Utils.pushNotificationCenterItemClickEvent(mContext, type,
                SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), "NotificationCenterListAdapter");
    }

    private void launchEditor() {
        Intent intent1 = new Intent(mContext, EditorPostActivity.class);
        Bundle bundle5 = new Bundle();
        bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
        bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
        bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                mContext.getString(R.string.example_post_title_placeholder));
        bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                mContext.getString(R.string.example_post_content_placeholder));
        bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
        bundle5.putString("from", "dashboard");
        intent1.putExtras(bundle5);
        mContext.startActivity(intent1);
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
                Toast.makeText(mContext, mContext.getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                if (BuildConfig.DEBUG || AppConstants.DEBUGGING_USER_ID.contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {

                } else {
                    return;
                }
            } else {

            }
        }

        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
            Intent intent = new Intent(mContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mContext.startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            Toast.makeText(mContext, mContext.getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT).show();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(mContext, GroupDetailsActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mContext.startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(mContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            intent.putExtra("pendingMembershipFlag", true);
            mContext.startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mContext.startActivity(intent);
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

    private void hitNotificationReadAPI(String notificationCenterId) {
        NotificationReadRequest notificationReadRequest = new NotificationReadRequest();
        notificationReadRequest.setNotifId(notificationCenterId);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);
        Call<NotificationCenterListResponse> call = notificationsAPI.markNotificationAsRead(notificationReadRequest);
        call.enqueue(markNotificationReadResponseCallback);
    }

    private Callback<NotificationCenterListResponse> markNotificationReadResponseCallback = new Callback<NotificationCenterListResponse>() {
        @Override
        public void onResponse(Call<NotificationCenterListResponse> call, retrofit2.Response<NotificationCenterListResponse> response) {
            if (response == null || response.body() == null) {
                return;
            }
            try {
                NotificationCenterListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {

                }
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




