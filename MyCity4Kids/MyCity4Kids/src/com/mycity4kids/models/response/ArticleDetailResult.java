package com.mycity4kids.models.response;

import com.mycity4kids.models.parentingdetails.DetailsBody;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 29/7/16.
 */
public class ArticleDetailResult {
    private String id;
    private String title;
    private DetailsBody processedBody;
    private ImageURL imageUrl;
    private ProfilePic profilePic;
    private String excerpt;
    private String userId;
    private String titleSlug;
    private String blogTitleSlug;
    private String authorImage;
    private String createdTime;
    private String userType;
    private String userName;
    private String isBookmarked;
    private String isFollowed;
    private String url;
    private String author_image;
    private String commentUri;
    private ArrayList<Map<String, String>> tags;
    private ArrayList<Map<String, String>> cities;
    private String bookmarkId;
    private String isMomspresso;
    private String userAgent;
    private String isSponsored;
    private String sponsoredImage;
    private String sponsoredBadge;

    public String getSponsoredImage() {
        return sponsoredImage;
    }

    public void setSponsoredImage(String sponsoredImage) {
        this.sponsoredImage = sponsoredImage;
    }

    public String getSponsoredBadge() {
        return sponsoredBadge;
    }

    public void setSponsoredBadge(String sponsoredBadge) {
        this.sponsoredBadge = sponsoredBadge;
    }

    public String getIsSponsored() {
        return isSponsored;
    }

    public void setIsSponsored(String isSponsored) {
        this.isSponsored = isSponsored;
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

    public DetailsBody getBody() {
        return processedBody;
    }

    public void setBody(DetailsBody body) {
        this.processedBody = processedBody;
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

    public String getIsMomspresso() {
        return isMomspresso;
    }

    public void setIsMomspresso(String isMomspresso) {
        this.isMomspresso = isMomspresso;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
