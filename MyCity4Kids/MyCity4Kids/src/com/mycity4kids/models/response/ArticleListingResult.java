package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hemant on 5/7/16.
 */
public class ArticleListingResult implements Parcelable {

    private String id;
    private String title;
    private String excerpt;
    private String titleSlug;
    private String imageUrl;
    private String userName;
    private String profilePic;
    private String userId;
    private String userType;
    private String commentsCount;
    private String trendingCount;
    private String blogPageSlug;
    private String createdTime;
    private String articleCount;

    protected ArticleListingResult(Parcel in) {
        id = in.readString();
        title = in.readString();
        excerpt = in.readString();
        titleSlug = in.readString();
        imageUrl = in.readString();
        userName = in.readString();
        profilePic = in.readString();
        userId = in.readString();
        userType = in.readString();
        commentsCount = in.readString();
        trendingCount = in.readString();
        blogPageSlug = in.readString();
        createdTime = in.readString();
        articleCount = in.readString();
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
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
        return blogPageSlug;
    }

    public void setBlogPageSlug(String blogPageSlug) {
        this.blogPageSlug = blogPageSlug;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(String articleCount) {
        this.articleCount = articleCount;
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
        dest.writeString(imageUrl);
        dest.writeString(userName);
        dest.writeString(profilePic);
        dest.writeString(userId);
        dest.writeString(userType);
        dest.writeString(commentsCount);
        dest.writeString(trendingCount);
        dest.writeString(blogPageSlug);
        dest.writeString(createdTime);
        dest.writeString(articleCount);
    }
}
