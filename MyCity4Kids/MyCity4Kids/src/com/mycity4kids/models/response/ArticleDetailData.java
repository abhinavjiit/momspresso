package com.mycity4kids.models.response;

import com.mycity4kids.models.parentingdetails.DetailsBody;

/**
 * Created by hemant on 6/7/16.
 */
public class ArticleDetailData {
    private String id;
    private String title;
    private DetailsBody bodys;
    private String imageUrl;
    private String excerpt;
    private String userId;
    private String titleSlug;
    private String authorImage;
    private String created;
    private String userType;
    private String userName;
    private String isBookmarked;
    private String isFollowing;
    private String url;
    private String author_image;
    private String commentUri;

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

    public DetailsBody getBody() {
        return bodys;
    }

    public void setBody(DetailsBody body) {
        this.bodys = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
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

    public String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
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

    public String getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(String isFollowing) {
        this.isFollowing = isFollowing;
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
}
