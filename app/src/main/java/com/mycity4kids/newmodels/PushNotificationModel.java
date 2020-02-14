package com.mycity4kids.newmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kapil.vij on 20-07-2015.
 */
public class PushNotificationModel implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    private PushNotificationModel(Parcel in) {
        super();
        action = in.readString();
        message_id = in.readString();
        type = in.readString();
        title = in.readString();
        url = in.readString();
        userId = in.readString();
        id = in.readString();
        blogTitleSlug = in.readString();
        titleSlug = in.readString();
        rich_image_url = in.readString();
        challengeId = in.readString();
        body = in.readString();
        comingFrom = in.readString();
        categoryId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeString(message_id);
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(userId);
        dest.writeString(id);
        dest.writeString(blogTitleSlug);
        dest.writeString(titleSlug);
        dest.writeString(rich_image_url);
        dest.writeString(challengeId);
        dest.writeString(body);
        dest.writeString(comingFrom);
        dest.writeString(categoryId);
    }

    public static Parcelable.Creator<PushNotificationModel> CREATOR = new Parcelable.Creator<PushNotificationModel>() {
        @Override
        public PushNotificationModel createFromParcel(Parcel source) {
            return new PushNotificationModel(source);
        }

        @Override
        public PushNotificationModel[] newArray(int size) {
            return new PushNotificationModel[size];
        }
    };
}
