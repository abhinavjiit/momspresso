package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;


/**
 * Created by hemant on 21/12/16.
 */
public class NotificationCenterResult {

    @SerializedName(value = "id", alternate = {"ID"})
    private String id;
    @SerializedName("contentId")
    private String contentId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("title")
    private String title;
    @SerializedName("articleId")
    private String articleId;
    @SerializedName("videoId")
    private String videoId;
    @SerializedName("authorId")
    private String authorId;
    @SerializedName("titleSlug")
    private String titleSlug;
    @SerializedName("blogTitleSlug")
    private String blogTitleSlug;
    @SerializedName("body")
    private String body;
    @SerializedName("createdTime")
    private String createdTime;
    @SerializedName("isRead")
    private String isRead;
    @SerializedName("notifType")
    private String notifType;
    @SerializedName("sharingUrl")
    private String sharingUrl;
    @SerializedName("url")
    private String url;
    @SerializedName("thumbNail")
    private String thumbNail;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("postId")
    private int postId;
    @SerializedName("responseId")
    private int responseId;
    @SerializedName("campaign_id")
    private int campaignId;
    @SerializedName("categoryId")
    private String categoryId;
    @SerializedName("badgeId")
    private String badgeId;
    @SerializedName("milestoneId")
    private String milestoneId;
    @SerializedName("collectionId")
    private String collectionId;
    @SerializedName("challengeId")
    private String challengeId;
    @SerializedName("commentId")
    private String commentId;
    @SerializedName("replyId")
    private String replyId;
    @SerializedName("type")
    private String type;
    @SerializedName("contentType")
    private String contentType;
    @SerializedName("htmlBody")
    private String htmlBody;
    @SerializedName("followBack")
    private String followBack;
    @SerializedName("service_type")
    private String serviceType;
    @SerializedName("isFollowing")
    private boolean isFollowing = false;
    @SerializedName("userInfo")
    private NotificationUserInfo userInfo;
    @SerializedName("notifiedBy")
    private ArrayList<GroupedUsers> notifiedBy;
    @SerializedName("recyclerItemType")
    private String recyclerItemType;
    @SerializedName("content_author")
    private String contentAuthor;
    @SerializedName(value = "event_id", alternate = {"eventId"})
    private int eventId;
    @SerializedName("Name")
    private String name;
    @SerializedName("Disabled")
    private Boolean disabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public NotificationCenterResult(@NotNull String recyclerItemType) {
        this.recyclerItemType = recyclerItemType;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public void setTitleSlug(String titleSlug) {
        this.titleSlug = titleSlug;
    }

    public String getBlogTitleSlug() {
        return blogTitleSlug;
    }

    public void setBlogTitleSlug(String blogTitleSlug) {
        this.blogTitleSlug = blogTitleSlug;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getNotifType() {
        return notifType;
    }

    public void setNotifType(String notifType) {
        this.notifType = notifType;
    }

    public String getSharingUrl() {
        return sharingUrl;
    }

    public void setSharingUrl(String sharingUrl) {
        this.sharingUrl = sharingUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(String thumbNail) {
        this.thumbNail = thumbNail;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getResponseId() {
        return responseId;
    }

    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }

    public String getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    public String getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(String milestoneId) {
        this.milestoneId = milestoneId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public NotificationUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(NotificationUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public ArrayList<GroupedUsers> getNotifiedBy() {
        return notifiedBy;
    }

    public void setNotifiedBy(
            ArrayList<GroupedUsers> notifiedBy) {
        this.notifiedBy = notifiedBy;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    public String getFollowBack() {
        return followBack;
    }

    public void setFollowBack(String followBack) {
        this.followBack = followBack;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public String getRecyclerItemType() {
        return recyclerItemType;
    }

    public void setRecyclerItemType(String recyclerItemType) {
        this.recyclerItemType = recyclerItemType;
    }

    public String getContentAuthor() {
        return contentAuthor;
    }

    public void setContentAuthor(String contentAuthor) {
        this.contentAuthor = contentAuthor;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public class NotificationUserInfo {

        @SerializedName("userProfilePicUrl")
        private ProfilePic userProfilePicUrl;

        public ProfilePic getUserProfilePicUrl() {
            return userProfilePicUrl;
        }

        public void setUserProfilePicUrl(ProfilePic userProfilePicUrl) {
            this.userProfilePicUrl = userProfilePicUrl;
        }
    }

    public class GroupedUsers {

        @SerializedName("firstName")
        private String firstName;
        @SerializedName("lastName")
        private String lastName;
        @SerializedName("userId")
        private String userId;
        @SerializedName("userProfilePicUrl")
        private ProfilePic userProfilePicUrl;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public ProfilePic getUserProfilePicUrl() {
            return userProfilePicUrl;
        }

        public void setUserProfilePicUrl(ProfilePic userProfilePicUrl) {
            this.userProfilePicUrl = userProfilePicUrl;
        }
    }
}
