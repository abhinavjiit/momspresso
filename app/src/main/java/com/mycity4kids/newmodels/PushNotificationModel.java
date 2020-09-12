package com.mycity4kids.newmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kapil.vij on 20-07-2015.
 */
public class PushNotificationModel {

    @SerializedName("action")
    private String action;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("message_id")
    private String message_id;
    @SerializedName("userId")
    private String userId;
    @SerializedName("id")
    private String id;
    @SerializedName("type")
    private String type;
    @SerializedName("url")
    private String url;
    @SerializedName("blogTitleSlug")
    private String blogTitleSlug;
    @SerializedName("titleSlug")
    private String titleSlug;
    @SerializedName("campaign_id")
    private int campaign_id;
    @SerializedName("rich_image_url")
    private String rich_image_url;
    @SerializedName("sound")
    private String sound;
    @SerializedName("challengeId")
    private String challengeId;
    @SerializedName("comingFrom")
    private String comingFrom;
    @SerializedName("categoryId")
    private String categoryId;
    @SerializedName("collectionId")
    private String collectionId;
    @SerializedName("badgeId")
    private String badgeId;
    @SerializedName("milestoneId")
    private String milestoneId;
    @SerializedName("commentId")
    private String commentId;
    @SerializedName("replyId")
    private String replyId;
    @SerializedName("contentType")
    private String contentType;
    @SerializedName("authorId")
    private String authorId;
    @SerializedName("content_author")
    private String contentAuthor;
    @SerializedName(value = "event_id", alternate = {"eventId"})
    private String eventId;

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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getRich_image_url() {
        return rich_image_url;
    }

    public void setRich_image_url(String rich_image_url) {
        this.rich_image_url = rich_image_url;
    }

    public int getCampaign_id() {
        return campaign_id;
    }

    public void setCampaign_id(int campaign_id) {
        this.campaign_id = campaign_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage_id() {
        return message_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getUser_id() {
        return userId;
    }

    public void setUser_id(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBlogPageSlug() {
        return blogTitleSlug;
    }

    public void setBlogPageSlug(String blogTitleSlug) {
        this.blogTitleSlug = blogTitleSlug;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public void setTitleSlug(String titleSlug) {
        this.titleSlug = titleSlug;
    }

    public String getComingFrom() {
        return comingFrom;
    }

    public void setComingFrom(String comingFrom) {
        this.comingFrom = comingFrom;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
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

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContentAuthor() {
        return contentAuthor;
    }

    public void setContentAuthor(String contentAuthor) {
        this.contentAuthor = contentAuthor;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
