package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

public class BloggerRankResponse extends BaseResponse {

    @SerializedName("data")
    private BloggerRankData data;

    public BloggerRankData getData() {
        return data;
    }

    public void setData(BloggerRankData data) {
        this.data = data;
    }


    public class BloggerRankData {

        @SerializedName("result")
        public BloggerRankResult result;

        public BloggerRankResult getResult() {
            return result;
        }

        public void setResult(
                BloggerRankResult result) {
            this.result = result;
        }

        public class BloggerRankResult {

            @SerializedName("about")
            private String about;
            @SerializedName("article")
            public BloggerRankList article;
            @SerializedName("video")
            public BloggerRankList video;
            @SerializedName("updated_at")
            private String updated_at;

            public String getAbout() {
                return about;
            }

            public void setAbout(String about) {
                this.about = about;
            }

            public BloggerRankList getArticle() {
                return article;
            }

            public void setArticle(BloggerRankList article) {
                this.article = article;
            }

            public BloggerRankList getVideo() {
                return video;
            }

            public void setVideo(BloggerRankList video) {
                this.video = video;
            }

            public String getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
            }
        }


        public class BloggerRankList {

            @SerializedName("author_id")
            private String author_id;
            @SerializedName("type")
            private String type;
            @SerializedName("average_daily_views")
            private long average_daily_views = 0;
            @SerializedName("total_views")
            private long total_views = 0;
            @SerializedName("yesterday_views")
            private long yesterday_views = 0;
            @SerializedName("content_created")
            private boolean content_created;

            public String getAuthor_id() {
                return author_id;
            }

            public void setAuthor_id(String author_id) {
                this.author_id = author_id;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public long getTotal_views() {
                return total_views;
            }

            public void setTotal_views(long total_views) {
                this.total_views = total_views;
            }

            public long getYesterday_views() {
                return yesterday_views;
            }

            public void setYesterday_views(long yesterday_views) {
                this.yesterday_views = yesterday_views;
            }

            public boolean isContent_created() {
                return content_created;
            }

            public void setContent_created(boolean content_created) {
                this.content_created = content_created;
            }

            public long getAverage_daily_views() {
                return average_daily_views;
            }

            public void setAverage_daily_views(long average_daily_views) {
                this.average_daily_views = average_daily_views;
            }
        }
    }
}
