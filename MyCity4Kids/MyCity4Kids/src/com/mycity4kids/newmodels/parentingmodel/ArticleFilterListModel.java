package com.mycity4kids.newmodels.parentingmodel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 22-07-2015.
 */
public class ArticleFilterListModel {

    int responseCode;
    String response;
    FilterListResult result;

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

    public FilterListResult getResult() {
        return result;
    }

    public void setResult(FilterListResult result) {
        this.result = result;
    }

    public class FilterListResult {
        String message;
        FilterList data;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public FilterList getData() {
            return data;
        }

        public void setData(FilterList data) {
            this.data = data;
        }
    }

    public class FilterList {

        ArrayList<FilterTopic> Topics;

        NewsLetterModel Newsletter;

        public NewsLetterModel getNewsletter() {
            return Newsletter;
        }

        public void setNewsletter(NewsLetterModel newsletter) {
            Newsletter = newsletter;
        }

        public ArrayList<FilterTopic> getTopics() {
            return Topics;
        }

        public void setTopics(ArrayList<FilterTopic> topics) {
            Topics = topics;
        }
    }

    public class FilterTopic {

        String id;
        String name;
        ArrayList<SubFilerList> subcategory;
        Boolean isNewsletter = false;

        public Boolean getIsNewsletter() {
            return isNewsletter;
        }

        public void setIsNewsletter(Boolean isNewsletter) {
            this.isNewsletter = isNewsletter;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<SubFilerList> getSubcategory() {
            return subcategory;
        }

        public void setSubcategory(ArrayList<SubFilerList> subcategory) {
            this.subcategory = subcategory;
        }
    }

    public class SubFilerList {

        String id;
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class NewsLetterModel {

        String text;
        String url;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
