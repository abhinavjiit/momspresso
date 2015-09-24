package com.mycity4kids.newmodels.bloggermodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.kelltontech.model.BaseModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 27-07-2015.
 */

public class BlogItemModel extends BaseModel implements Parcelable {

    int id;
    String first_name;
    String last_name;
    String about_user;
    String profile_image;
    String cover_image;
    String author_type;
    int author_rank;
    ArrayList<RecentArticleModel> recent_articles;
    String author_color_code;
    String facebook_id;
    String twitter_id;
    String blog_title;
    int maxLineCount = 0;
    String user_following_status;

    public String getUser_following_status() {
        return user_following_status;
    }

    public void setUser_following_status(String user_following_status) {
        this.user_following_status = user_following_status;
    }

    public int getMaxLineCount() {
        return maxLineCount;
    }

    public void setMaxLineCount(int maxLineCount) {
        this.maxLineCount = maxLineCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAbout_user() {
        return about_user;
    }

    public void setAbout_user(String about_user) {
        this.about_user = about_user;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getAuthor_type() {
        return author_type;
    }

    public void setAuthor_type(String author_type) {
        this.author_type = author_type;
    }

    public int getAuthor_rank() {
        return author_rank;
    }

    public void setAuthor_rank(int author_rank) {
        this.author_rank = author_rank;
    }

    public ArrayList<RecentArticleModel> getRecent_articles() {
        return recent_articles;
    }

    public void setRecent_articles(ArrayList<RecentArticleModel> recent_articles) {
        this.recent_articles = recent_articles;
    }

    public String getAuthor_color_code() {
        return author_color_code;
    }

    public void setAuthor_color_code(String author_color_code) {
        this.author_color_code = author_color_code;
    }

    public String getTwitter_id() {
        return twitter_id;
    }

    public void setTwitter_id(String twitter_id) {
        this.twitter_id = twitter_id;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public String getBlog_title() {
        return blog_title;
    }

    public void setBlog_title(String blog_title) {
        this.blog_title = blog_title;
    }

    public BlogItemModel(Parcel parcel) {
        this.id = parcel.readInt();
        this.first_name = parcel.readString();
        this.last_name = parcel.readString();
        this.about_user = parcel.readString();
        this.profile_image = parcel.readString();
        this.cover_image = parcel.readString();
        this.author_type = parcel.readString();
        this.author_rank = parcel.readInt();
//        this.recent_articles = parcel.readString();
        this.author_color_code = parcel.readString();
        this.facebook_id = parcel.readString();
        this.twitter_id = parcel.readString();
        this.blog_title = parcel.readString();
        this.user_following_status = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(id);
        parcel.writeString(first_name);
        parcel.writeString(last_name);
        parcel.writeString(about_user);
        parcel.writeString(profile_image);
        parcel.writeString(cover_image);
        parcel.writeString(author_type);
        parcel.writeInt(author_rank);
        parcel.writeString(author_color_code);
        parcel.writeString(facebook_id);
        parcel.writeString(twitter_id);
        parcel.writeString(blog_title);
        parcel.writeString(user_following_status);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Creator<BlogItemModel> CREATOR = new Creator<BlogItemModel>() {
        @Override
        public BlogItemModel createFromParcel(Parcel source) {
            return new BlogItemModel(source);
        }

        @Override
        public BlogItemModel[] newArray(int size) {
            return new BlogItemModel[size];
        }

    };

}
