package com.mycity4kids.newmodels;

import java.util.ArrayList;

/**
 * Created by hemant on 19/3/16.
 */
public class PublishedArticlesModel {
    private int responseCode;
    private String response;
    private PublishedArticleResult result;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public PublishedArticleResult getResult() {
        return result;
    }

    public void setResult(PublishedArticleResult result) {
        this.result = result;
    }

    public class PublishedArticleResult {
        private String message;
        private ArrayList<PublishedArticleData> data;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<PublishedArticleData> getData() {
            return data;
        }

        public void setData(ArrayList<PublishedArticleData> data) {
            this.data = data;
        }
    }

    public class PublishedArticleData {
        private String id;
        private String title;
        private String created;
        private String thumbnail_image;
        private int current_views;

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

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getThumbnail_image() {
            return thumbnail_image;
        }

        public void setThumbnail_image(String thumbnail_image) {
            this.thumbnail_image = thumbnail_image;
        }

        public int getCurrent_views() {
            return current_views;
        }

        public void setCurrent_views(int current_views) {
            this.current_views = current_views;
        }
    }
}
