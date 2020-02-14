package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

public class SearchVideoResult {
    @SerializedName("title")
    private String title;
    @SerializedName("id")
    private String id;
    @SerializedName("title_slug")
    private String  title_slug;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("author")
    private VideoAuthor author;
    @SerializedName("imageUrl")
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle_slug() {
        return title_slug;
    }

    public void setTitle_slug(String title_slug) {
        this.title_slug = title_slug;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public VideoAuthor getAuthor() {
        return author;
    }

    public void setAuthor(VideoAuthor author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
