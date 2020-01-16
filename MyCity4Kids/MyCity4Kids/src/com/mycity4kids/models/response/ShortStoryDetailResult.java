package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.parentingdetails.DetailsBody;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 29/7/16.
 */
public class ShortStoryDetailResult {
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("imageUrl")
    private ImageURL imageUrl;
    @SerializedName("profilePic")
    private ProfilePic profilePic;
    @SerializedName("excerpt")
    private String excerpt;
    @SerializedName("userId")
    private String userId;
    @SerializedName("titleSlug")
    private String titleSlug;
    @SerializedName("blogTitleSlug")
    private String blogTitleSlug;
    @SerializedName("authorImage")
    private String authorImage;
    @SerializedName("createdTime")
    private String createdTime;
    @SerializedName("userType")
    private String userType;
    @SerializedName("userName")
    private String userName;
    @SerializedName("isBookmarked")
    private String isBookmarked;
    @SerializedName("isFollowed")
    private String isFollowed;
    @SerializedName("url")
    private String url;
    @SerializedName("author_image")
    private String author_image;
    @SerializedName("commentUri")
    private String commentUri;
    @SerializedName("tags")
    private ArrayList<Map<String, String>> tags;
    @SerializedName("cities")
    private ArrayList<Map<String, String>> cities;
    @SerializedName("bookmarkId")
    private String bookmarkId;
    @SerializedName("count")
    private String count;
    @SerializedName("commentCount")
    private String commentCount;
    @SerializedName("likeCount")
    private String likeCount;
    @SerializedName("lang")
    private String lang;
    @SerializedName("isLiked")
    private boolean isLiked;
    @SerializedName("storyImage")
    private String storyImage;

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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ImageURL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(ImageURL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    public ProfilePic getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(ProfilePic profilePic) {
        this.profilePic = profilePic;
    }

    public String getCreated() {
        return createdTime;
    }

    public void setCreated(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBookmarkStatus() {
        return isBookmarked;
    }

    public void setBookmarkStatus(String isBookmarked) {
        this.isBookmarked = isBookmarked;
    }

    public String getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(String isFollowed) {
        this.isFollowed = isFollowed;
    }

    public String getCommentUri() {
        return commentUri;
    }

    public void setCommentUri(String commentUri) {
        this.commentUri = commentUri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor_image() {
        return author_image;
    }

    public void setAuthor_image(String author_image) {
        this.author_image = author_image;
    }

    public String getCommentsUri() {
        return commentUri;
    }

    public void setCommentsUri(String commentsUri) {
        this.commentUri = commentsUri;
    }

    public ArrayList<Map<String, String>> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Map<String, String>> tags) {
        this.tags = tags;
    }

    public ArrayList<Map<String, String>> getCities() {
        return cities;
    }

    public void setCities(ArrayList<Map<String, String>> cities) {
        this.cities = cities;
    }

    public String getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(String bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getStoryImage() {
        return storyImage;
    }

    public void setStoryImage(String storyImage) {
        this.storyImage = storyImage;
    }
}
