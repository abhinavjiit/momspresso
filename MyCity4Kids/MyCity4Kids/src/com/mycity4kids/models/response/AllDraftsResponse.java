package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hemant on 11/10/18.
 */

public class AllDraftsResponse extends BaseResponse {

    private AllDraftsData data;

    public AllDraftsData getData() {
        return data;
    }

    public void setData(AllDraftsData data) {
        this.data = data;
    }

    public static class AllDraftsData {

        private ArrayList<AllDraftsResult> result;

        public ArrayList<AllDraftsResult> getResult() {
            return result;
        }

        public void setResult(ArrayList<AllDraftsResult> result) {
            this.result = result;
        }

        public static class AllDraftsResult {
            private String userId;
            private String articleType;
            private String createdTime;
            private String id;
            private long updatedTime;
            private String body;
            private String title;
            private String contentType;
            private String userAgent;
            private String lang;
            private ArrayList<Map<String,String>> tags;

            public ArrayList<Map<String, String>> getTags() {
                return tags;
            }

            public void setTags(ArrayList<Map<String, String>> tags) {
                this.tags = tags;
            }

       /*     public Tags getTags() {
                return tags;

            }

            public void setTags(Tags tags) {
                this.tags = tags;
            }

            public class Tags {
                private ArrayList<Map<String, String>> tagsArr;

                public ArrayList<Map<String, String>> getTagsArr() {
                    return tagsArr;
                }

                public void setTagsArr(ArrayList<Map<String, String>> tagsArr) {
                    this.tagsArr = tagsArr;
                }

            }*/


            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getArticleType() {
                return articleType;
            }

            public void setArticleType(String articleType) {
                this.articleType = articleType;
            }

            public String getCreatedTime() {
                return createdTime;
            }

            public void setCreatedTime(String createdTime) {
                this.createdTime = createdTime;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public long getUpdatedTime() {
                return updatedTime;
            }

            public void setUpdatedTime(long updatedTime) {
                this.updatedTime = updatedTime;
            }

            public String getBody() {
                return body;
            }

            public void setBody(String body) {
                this.body = body;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContentType() {
                return contentType;
            }

            public void setContentType(String contentType) {
                this.contentType = contentType;
            }

            public String getUserAgent() {
                return userAgent;
            }

            public void setUserAgent(String userAgent) {
                this.userAgent = userAgent;
            }

            public String getLang() {
                return lang;
            }

            public void setLang(String lang) {
                this.lang = lang;
            }
        }
    }
}
