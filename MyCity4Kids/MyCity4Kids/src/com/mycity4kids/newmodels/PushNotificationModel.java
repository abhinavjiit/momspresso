package com.mycity4kids.newmodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kapil.vij on 20-07-2015.
 */
public class PushNotificationModel implements Parcelable {

    private int family_id;
    private String action;
    private String calendar_items = "";
    private String todo_items = "";
    private String title;
    private String body;
    private String share_content;
    private String message_id;
    private String userId;
    private int appointment_id;
    private String id;
    private String type;
    private String url;
    private String article_cover_image_url = "";
    private String filter_type = "";
    private String blog_name = "";
    private String blogTitleSlug;
    private String titleSlug;
    private int campaign_id;
    private String mapped_category;
    private String rich_image_url;
    private String sound;
    private String challengeId;
    private String comingFrom;
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

    public int getFamily_id() {
        return family_id;
    }

    public void setFamily_id(int family_id) {
        this.family_id = family_id;
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

    public int getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCalendar_items(String calendar_items) {
        this.calendar_items = calendar_items;
    }

    public String getCalendar_items() {
        return calendar_items;
    }

    public String getTodo_items() {
        return todo_items;
    }

    public void setTodo_items(String todo_items) {
        this.todo_items = todo_items;
    }

    public String getShare_content() {
        return share_content;
    }

    public void setShare_content(String share_content) {
        this.share_content = share_content;
    }

    public String getArticle_cover_image_url() {
        return article_cover_image_url;
    }

    public void setArticle_cover_image_url(String article_cover_image_url) {
        this.article_cover_image_url = article_cover_image_url;
    }

    public String getFilter_type() {
        return filter_type;
    }

    public String getMapped_category() {
        return mapped_category;
    }

    public void setMapped_category(String mapped_category) {
        this.mapped_category = mapped_category;
    }

    public void setFilter_type(String filter_type) {
        this.filter_type = filter_type;
    }

    public String getBlog_name() {
        return blog_name;
    }

    public void setBlog_name(String blog_name) {
        this.blog_name = blog_name;
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

    public PushNotificationModel(Parcel in) {
        // TODO Auto-generated constructor stub
        super();
        action = in.readString();
        message_id = in.readString();
        type = in.readString();
        title = in.readString();
        share_content = in.readString();
        url = in.readString();
        family_id = in.readInt();
        userId = in.readString();
        appointment_id = in.readInt();
        id = in.readString();
        calendar_items = in.readString();
        todo_items = in.readString();
        article_cover_image_url = in.readString();
        filter_type = in.readString();
        blog_name = in.readString();
        blogTitleSlug = in.readString();
        titleSlug = in.readString();
        rich_image_url = in.readString();
        challengeId = in.readString();
        mapped_category = in.readString();
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
        dest.writeString(share_content);
        dest.writeString(url);
        dest.writeInt(family_id);
        dest.writeString(userId);
        dest.writeInt(appointment_id);
        dest.writeString(id);
        dest.writeString(calendar_items);
        dest.writeString(todo_items);
        dest.writeString(article_cover_image_url);
        dest.writeString(filter_type);
        dest.writeString(blog_name);
        dest.writeString(blogTitleSlug);
        dest.writeString(titleSlug);
        dest.writeString(rich_image_url);
        dest.writeString(challengeId);
        dest.writeString(mapped_category);
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
