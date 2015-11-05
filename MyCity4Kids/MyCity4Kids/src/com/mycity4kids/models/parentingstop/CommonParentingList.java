package com.mycity4kids.models.parentingstop;

import android.os.Parcel;
import android.os.Parcelable;

public class CommonParentingList implements Parcelable {
    private String id;
    private String title;
    private String created;
    private String author_id;
    private String author_name;
    private String author_image;
    private String thumbnail_image;
    private String author_type;
    private String author_color_code;
    private String blog_name;
    private String user_following_status;
    private String is_bookmark;

    public String getUser_following_status() {
        return user_following_status;
    }

    public void setUser_following_status(String user_following_status) {
        this.user_following_status = user_following_status;
    }

    public String getBlog_name() {
        return blog_name;
    }

    public void setBlog_name(String blog_name) {
        this.blog_name = blog_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_image() {
        return author_image;
    }

    public void setAuthor_image(String author_image) {
        this.author_image = author_image;
    }

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public String getAuthor_type() {
        return author_type;
    }

    public void setAuthor_type(String author_type) {
        this.author_type = author_type;
    }

    public String getAuthor_color_code() {
        return author_color_code;
    }

    public void setAuthor_color_code(String author_color_code) {
        this.author_color_code = author_color_code;
    }

    public String getBookmarkStatus() {
        return is_bookmark;
    }

    public void setBookmarkStatus(String is_bookmark) {
        this.is_bookmark = is_bookmark;
    }

    public CommonParentingList(Parcel parcel) {
        this.id = parcel.readString();
        this.title = parcel.readString();
        this.created = parcel.readString();
        this.author_id = parcel.readString();
        this.author_name = parcel.readString();
        this.thumbnail_image = parcel.readString();
        this.author_image = parcel.readString();
        this.author_type = parcel.readString();
        this.author_color_code = parcel.readString();
        this.user_following_status = parcel.readString();
        this.is_bookmark = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(created);
        dest.writeString(author_id);
        dest.writeString(author_name);
        dest.writeString(thumbnail_image);
        dest.writeString(author_image);
        dest.writeString(author_type);
        dest.writeString(author_color_code);
        dest.writeString(user_following_status);
        dest.writeString(is_bookmark);
    }

    public static Creator<CommonParentingList> CREATOR = new Creator<CommonParentingList>() {
        @Override
        public CommonParentingList createFromParcel(Parcel source) {
            return new CommonParentingList(source);
        }

        @Override
        public CommonParentingList[] newArray(int size) {
            return new CommonParentingList[size];
        }

    };

}
