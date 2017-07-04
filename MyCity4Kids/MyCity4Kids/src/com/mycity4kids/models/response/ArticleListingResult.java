package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 5/7/16.
 */
public class ArticleListingResult implements Parcelable {

    private String id;
    private String title;
    private String excerpt;
    private String titleSlug;
    private ImageURL imageUrl;
    private String userName;
    private ProfilePic profilePic;
    private String userId;
    private String userType;
    private String commentsCount;
    private String trendingCount;
    private String blogTitleSlug;
    private Long createdTime;
    private String articleCount;
    private String videoUrl;
    private ArrayList<Map<String, String>> tags;
    private String likesCount;
    private String reason;
    private int listingBookmarkStatus = 0;

    public ArticleListingResult() {
    }

    protected ArticleListingResult(Parcel in) {
        id = in.readString();
        title = in.readString();
        excerpt = in.readString();
        titleSlug = in.readString();
        imageUrl = in.readParcelable(ImageURL.class.getClassLoader());
        userName = in.readString();
        profilePic = in.readParcelable(ProfilePic.class.getClassLoader());
        userId = in.readString();
        userType = in.readString();
        commentsCount = in.readString();
        trendingCount = in.readString();
        blogTitleSlug = in.readString();
        createdTime = in.readLong();
        articleCount = in.readString();
        videoUrl = in.readString();
        reason = in.readString();
    }

    public static final Creator<ArticleListingResult> CREATOR = new Creator<ArticleListingResult>() {
        @Override
        public ArticleListingResult createFromParcel(Parcel in) {
            return new ArticleListingResult(in);
        }

        @Override
        public ArticleListingResult[] newArray(int size) {
            return new ArticleListingResult[size];
        }
    };

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

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public void setTitleSlug(String titleSlug) {
        this.titleSlug = titleSlug;
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

    public ProfilePic getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(ProfilePic profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(String commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getTrendingCount() {
        return trendingCount;
    }

    public void setTrendingCount(String trendingCount) {
        this.trendingCount = trendingCount;
    }

    public String getBlogPageSlug() {
        return blogTitleSlug;
    }

    public void setBlogPageSlug(String blogPageSlug) {
        this.blogTitleSlug = blogPageSlug;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public String getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(String articleCount) {
        this.articleCount = articleCount;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public ArrayList<Map<String, String>> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Map<String, String>> tags) {
        this.tags = tags;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getListingBookmarkStatus() {
        return listingBookmarkStatus;
    }

    public void setListingBookmarkStatus(int listingBookmarkStatus) {
        this.listingBookmarkStatus = listingBookmarkStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(excerpt);
        dest.writeString(titleSlug);
        dest.writeParcelable(imageUrl, flags);
        dest.writeString(userName);
        dest.writeParcelable(profilePic, flags);
        dest.writeString(userId);
        dest.writeString(userType);
        dest.writeString(commentsCount);
        dest.writeString(trendingCount);
        dest.writeString(blogTitleSlug);
        dest.writeLong(createdTime);
        dest.writeString(articleCount);
        dest.writeString(videoUrl);
        dest.writeString(reason);
    }


}
