package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hemant on 5/7/16.
 */
public class ArticleListingResult implements Parcelable {

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("excerpt")
    private String excerpt;
    @SerializedName("titleSlug")
    private String titleSlug;
    @SerializedName("imageUrl")
    private ImageURL imageUrl;
    @SerializedName("userName")
    private String userName;
    @SerializedName("profilePic")
    private List<ProfilePic> profilePic;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userType")
    private String userType;
    @SerializedName("commentCount")
    private String commentCount;
    @SerializedName("commentsCount")
    private String commentsCount;
    @SerializedName("trendingCount")
    private String trendingCount;
    @SerializedName("blogTitleSlug")
    private String blogTitleSlug;
    @SerializedName("createdTime")
    private Long createdTime = 0l;
    @SerializedName("articleCount")
    private String articleCount;
    @SerializedName("videoUrl")
    private String videoUrl;
    @SerializedName("tags")
    private ArrayList<Map<String, String>> tags;
    @SerializedName("likesCount")
    private String likesCount;
    @SerializedName("reason")
    private String reason;
    @SerializedName("bookmarkId")
    private String bookmarkId;
    @SerializedName("listingBookmarkStatus")
    private int listingBookmarkStatus = 0;
    @SerializedName("isMomspresso")
    private String isMomspresso;
    @SerializedName("listingWatchLaterStatus")
    private int listingWatchLaterStatus = 0;
    @SerializedName("lang")
    private String lang;
    @SerializedName("contentType")
    private String contentType = "0";
    @SerializedName("isLiked")
    private boolean isLiked;
    @SerializedName("url")
    private String url;
    @SerializedName("is_bookmark")
    private String is_bookmark = "0";
    @SerializedName("isCarouselRequestRunning")
    private boolean isCarouselRequestRunning = false;
    @SerializedName("responseReceived")
    private boolean responseReceived = false;
    @SerializedName("disableComment")
    private String disableComment = "";
    @SerializedName("storyImage")
    private String storyImage = "";
    @SerializedName("isCollectionItemSelected")
    private boolean isCollectionItemSelected = false;
    @SerializedName("carouselVideoList")
    private ArrayList<VlogsListingAndDetailResult> carouselVideoList;
    @SerializedName("isfollowing")
    private String isfollowing = "0";
    @SerializedName("winner")
    private String winner = "0";
    @SerializedName("is_gold")
    private String isGold = "0";
    @SerializedName(value = "event_id", alternate = {"eventId"})
    private String eventId;

    public ArticleListingResult() {
    }

    protected ArticleListingResult(Parcel in) {
        id = in.readString();
        title = in.readString();
        excerpt = in.readString();
        titleSlug = in.readString();
        imageUrl = in.readParcelable(ImageURL.class.getClassLoader());
        userName = in.readString();
        profilePic = new ArrayList<>();
        in.readTypedList(profilePic, ProfilePic.CREATOR);
        userId = in.readString();
        userType = in.readString();
        commentCount = in.readString();
        commentsCount = in.readString();
        trendingCount = in.readString();
        blogTitleSlug = in.readString();
        createdTime = in.readLong();
        articleCount = in.readString();
        videoUrl = in.readString();
        reason = in.readString();
        bookmarkId = in.readString();
        isMomspresso = in.readString();
        lang = in.readString();
        body = in.readString();
        url = in.readString();
        is_bookmark = in.readString();
        disableComment = in.readString();
        storyImage = in.readString();
        isfollowing = in.readString();
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public boolean isCollectionItemSelected() {
        return isCollectionItemSelected;
    }

    public void setCollectionItemSelected(boolean collectionItemSelected) {
        isCollectionItemSelected = collectionItemSelected;
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

    public List<ProfilePic> getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(List<ProfilePic> profilePic) {
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

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
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

    public int getListingWatchLaterStatus() {
        return listingWatchLaterStatus;
    }

    public void setListingWatchLaterStatus(int listingWatchLaterStatus) {
        this.listingWatchLaterStatus = listingWatchLaterStatus;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIs_bookmark() {
        return is_bookmark;
    }

    public void setIs_bookmark(String is_bookmark) {
        this.is_bookmark = is_bookmark;
    }

    public boolean isCarouselRequestRunning() {
        return isCarouselRequestRunning;
    }

    public void setCarouselRequestRunning(boolean carouselRequestRunning) {
        isCarouselRequestRunning = carouselRequestRunning;
    }

    public boolean isResponseReceived() {
        return responseReceived;
    }

    public void setResponseReceived(boolean responseReceived) {
        this.responseReceived = responseReceived;
    }

    public String getIsfollowing() {
        return isfollowing;
    }

    public void setIsfollowing(String isfollwing) {
        this.isfollowing = isfollwing;
    }

    public String getDisableComment() {
        return disableComment;
    }

    public void setDisableComment(String disableComment) {
        this.disableComment = disableComment;
    }

    public String getStoryImage() {
        return storyImage;
    }

    public void setStoryImage(String storyImage) {
        this.storyImage = storyImage;
    }

    public ArrayList<VlogsListingAndDetailResult> getCarouselVideoList() {
        return carouselVideoList;
    }

    public void setCarouselVideoList(ArrayList<VlogsListingAndDetailResult> carouselVideoList) {
        this.carouselVideoList = carouselVideoList;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getIsGold() {
        return isGold;
    }

    public void setIsGold(String isGold) {
        this.isGold = isGold;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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
        dest.writeTypedList(profilePic);
        dest.writeString(userId);
        dest.writeString(userType);
        dest.writeString(commentCount);
        dest.writeString(commentsCount);
        dest.writeString(trendingCount);
        dest.writeString(blogTitleSlug);
        dest.writeLong(createdTime);
        dest.writeString(articleCount);
        dest.writeString(videoUrl);
        dest.writeString(reason);
        dest.writeString(bookmarkId);
        dest.writeString(isMomspresso);
        dest.writeString(lang);
        dest.writeString(body);
        dest.writeString(url);
        dest.writeString(is_bookmark);
        dest.writeString(disableComment);
        dest.writeString(storyImage);
        dest.writeString(isfollowing);
    }
}
