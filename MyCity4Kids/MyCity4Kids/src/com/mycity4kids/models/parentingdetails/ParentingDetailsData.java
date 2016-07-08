package com.mycity4kids.models.parentingdetails;

import java.util.ArrayList;

public class ParentingDetailsData {
    private String id;
    private String title;
    private String author_id;
    private String author_name;
    private String thumbnail_image;
    private String author_image;
    private ArrayList<CommentsData> comments;
    private DetailsBody body;
    private String created;
    private String url;
    private String author_type;
    private String user_following_status;
    private String is_bookmark;
    private String blog_title;
    private String tag;
    private String userId;
    private String imageUrl;

    public String getAuthor_type() {
        return author_type;
    }

    public void setAuthor_type(String author_type) {
        this.author_type = author_type;
    }

    public String getUser_following_status() {
        return user_following_status;
    }

    public void setUser_following_status(String user_following_status) {
        this.user_following_status = user_following_status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
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

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public String getAuthor_image() {
        return author_image;
    }

    public void setAuthor_image(String author_image) {
        this.author_image = author_image;
    }

    public ArrayList<CommentsData> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentsData> comments) {
        this.comments = comments;
    }

    public DetailsBody getBody() {
        return body;
    }

    public void setBody(DetailsBody body) {
        this.body = body;
    }

    public String getBookmarkStatus() {
        return is_bookmark;
    }

    public void setBookmarkStatus(String is_bookmark) {
        this.is_bookmark = is_bookmark;
    }

    public String getBlog_title() {
        return blog_title;
    }

    public void setBlog_title(String blog_title) {
        this.blog_title = blog_title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
