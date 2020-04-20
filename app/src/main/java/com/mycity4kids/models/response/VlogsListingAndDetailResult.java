package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.profile.Author;

import java.util.ArrayList;

/**
 * Created by hemant on 3/1/17.
 */
public class VlogsListingAndDetailResult {

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("title_slug")
    private String title_slug;
    @SerializedName("url")
    private String url;
    @SerializedName("published_time")
    private String published_time;
    @SerializedName("approval_time")
    private String approval_time;
    @SerializedName("published_status")
    private String published_status;
    @SerializedName("publication_status")
    private String publication_status;
    @SerializedName("commentUri")
    private String commentUri;
    @SerializedName("author")
    private Author author;
    @SerializedName("sharing_url")
    private String sharing_url;
    @SerializedName("view_count")
    private String view_count;
    @SerializedName("category_id")
    private ArrayList<String> category_id;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("like_count")
    private String like_count;
    @SerializedName("comment_count")
    private String comment_count;
    @SerializedName("is_liked")
    private String is_liked;
    @SerializedName("bookmark_id")
    private String bookmark_id;
    @SerializedName("is_bookmark")
    private String is_bookmark;
    @SerializedName("isLiked")
    private Boolean isLiked = false;
    @SerializedName("isBookmarked")
    private boolean isBookmarked = false;
    @SerializedName("isFollowed")
    private boolean isFollowed = false;
    @SerializedName("is_gold")
    private boolean is_gold = false;
    @SerializedName("winner")
    private int winner = 0;
    private int itemType = 0;
    private ArrayList<UserDetailResult> carouselVideoList;
    private boolean responseReceived = false;
    private boolean isCarouselRequestRunning = false;
    private int start = 0;
    private int index = 0;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public VlogsListingAndDetailResult(int itemType) {
        this.itemType = itemType;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public boolean isResponseReceived() {
        return responseReceived;
    }

    public void setResponseReceived(boolean responseReceived) {
        this.responseReceived = responseReceived;
    }

    public boolean isCarouselRequestRunning() {
        return isCarouselRequestRunning;
    }

    public void setCarouselRequestRunning(boolean carouselRequestRunning) {
        isCarouselRequestRunning = carouselRequestRunning;
    }

    public ArrayList<UserDetailResult> getCarouselVideoList() {
        return carouselVideoList;
    }

    public void setCarouselVideoList(ArrayList<UserDetailResult> carouselVideoList) {
        this.carouselVideoList = carouselVideoList;
    }

    public String getSharing_url() {
        return sharing_url;
    }

    public void setSharing_url(String sharing_url) {
        this.sharing_url = sharing_url;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public boolean isIs_gold() {
        return is_gold;
    }

    public void setIs_gold(boolean is_gold) {
        this.is_gold = is_gold;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public String getBookmark_id() {
        return bookmark_id;
    }

    public void setBookmark_id(String bookmark_id) {
        this.bookmark_id = bookmark_id;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
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

    public String getTitleSlug() {
        return title_slug;
    }

    public void setTitleSlug(String titleSlug) {
        this.title_slug = titleSlug;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublished_time() {
        return published_time;
    }

    public void setPublished_time(String published_time) {
        this.published_time = published_time;
    }

    public String getPublished_status() {
        return published_status;
    }

    public void setPublished_status(String published_status) {
        this.published_status = published_status;
    }

    public String getPublication_status() {
        return publication_status;
    }

    public void setPublication_status(String publication_status) {
        this.publication_status = publication_status;
    }

    public String getCommentUri() {
        return commentUri;
    }

    public void setCommentUri(String commentUri) {
        this.commentUri = commentUri;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public ArrayList<String> getCategory_id() {
        return category_id;
    }

    public void setCategory_id(ArrayList<String> category_id) {
        this.category_id = category_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(String is_liked) {
        this.is_liked = is_liked;
    }

    public String getIs_bookmark() {
        return is_bookmark;
    }

    public void setIs_bookmark(String is_bookmark) {
        this.is_bookmark = is_bookmark;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
