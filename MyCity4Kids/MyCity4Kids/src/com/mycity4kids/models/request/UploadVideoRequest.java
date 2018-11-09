package com.mycity4kids.models.request;

import java.util.ArrayList;

/**
 * Created by hemant on 12/1/17.
 */
public class UploadVideoRequest {
    private String video_id;
    private String title;
    private String filename;
    private String file_location;
    private String publication_status;
    private String uploaded_url;
    private String description;
    private String user_id;
    private String user_agent;
    private String published_url;
//    private boolean is_popular;
    private ArrayList<String> category_id;
//    private boolean has_special_cat;
    private String thumbnail;

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

//    public boolean isIs_popular() {
//        return is_popular;
//    }
//
//    public void setIs_popular(boolean is_popular) {
//        this.is_popular = is_popular;
//    }

    public ArrayList<String> getCategory_id() {
        return category_id;
    }

    public void setCategory_id(ArrayList<String> category_id) {
        this.category_id = category_id;
    }

//    public boolean isHas_special_cat() {
//        return has_special_cat;
//    }
//
//    public void setHas_special_cat(boolean has_special_cat) {
//        this.has_special_cat = has_special_cat;
//    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
