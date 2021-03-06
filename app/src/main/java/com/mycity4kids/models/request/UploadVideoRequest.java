package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Created by hemant on 12/1/17.
 */
public class UploadVideoRequest {

    @SerializedName("video_id")
    private String video_id;
    @SerializedName("title")
    private String title;
    @SerializedName("filename")
    private String filename;
    @SerializedName("file_location")
    private String file_location;
    @SerializedName("publication_status")
    private String publication_status;
    @SerializedName("uploaded_url")
    private String uploaded_url;
    @SerializedName("description")
    private String description;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("user_agent")
    private String user_agent;
    @SerializedName("published_url")
    private String published_url;
    @SerializedName("thumbnail_milliseconds")
    private String thumbnail_milliseconds;
    @SerializedName("reason")
    private String reason;
    @SerializedName("category_id")
    private ArrayList<String> category_id;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("lang")
    private String[] lang;

    public String[] getLang() {
        return lang;
    }

    public void setLang(String[] lang) {
        this.lang = lang;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFile_location() {
        return file_location;
    }

    public void setFile_location(String file_location) {
        this.file_location = file_location;
    }

    public String getPublication_status() {
        return publication_status;
    }

    public void setPublication_status(String publication_status) {
        this.publication_status = publication_status;
    }

    public String getUploaded_url() {
        return uploaded_url;
    }

    public void setUploaded_url(String uploaded_url) {
        this.uploaded_url = uploaded_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public String getPublished_url() {
        return published_url;
    }

    public void setPublished_url(String published_url) {
        this.published_url = published_url;
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

    public String getThumbnail_milliseconds() {
        return thumbnail_milliseconds;
    }

    public void setThumbnail_milliseconds(String thumbnail_milliseconds) {
        this.thumbnail_milliseconds = thumbnail_milliseconds;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
