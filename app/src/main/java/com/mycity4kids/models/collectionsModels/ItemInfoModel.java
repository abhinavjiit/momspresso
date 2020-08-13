package com.mycity4kids.models.collectionsModels;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.response.ImageURL;
import com.mycity4kids.profile.Author;

public class ItemInfoModel {

    @SerializedName("author")
    private Author author;
    @SerializedName("title")
    private String title;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("userName")
    private String userName;
    @SerializedName("imageUrl")
    private ImageURL imageUrl;
    @SerializedName("storyImage")
    private String storyImage;
    @SerializedName("viewCount")
    private int viewCount;
    @SerializedName("id")
    private String id;
    @SerializedName("url")
    private String streamUrl;
    @SerializedName("blogTitleSlug")
    private String blogTitleSlug;
    @SerializedName("titleSlug")
    private String titleSlug;
    @SerializedName("userId")
    private String userId;
    @SerializedName("name")
    private String name;
    @SerializedName("collectionImageUrl")
    private String collectionImageUrl;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBlogTitleSlug() {
        return blogTitleSlug;
    }

    public void setBlogTitleSlug(String blogTitleSlug) {
        this.blogTitleSlug = blogTitleSlug;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public void setTitleSlug(String titleSlug) {
        this.titleSlug = titleSlug;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getStoryImage() {
        return storyImage;
    }

    public void setStoryImage(String storyImage) {
        this.storyImage = storyImage;
    }

    public ImageURL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(ImageURL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollectionImageUrl() {
        return collectionImageUrl;
    }

    public void setCollectionImageUrl(String collectionImageUrl) {
        this.collectionImageUrl = collectionImageUrl;
    }
}
