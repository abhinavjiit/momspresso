package com.mycity4kids.newmodels;

/**
 * Created by hemant on 17/3/16.
 */
public class BloggerDashboardModel {

    private int responseCode;
    private String response;
    private BloggerDashboardResult result;

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

    public BloggerDashboardResult getResult() {
        return result;
    }

    public void setResult(BloggerDashboardResult result) {
        this.result = result;
    }

    public class BloggerDashboardResult {
        private String message;
        private BloggerDashboardData data;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public BloggerDashboardData getData() {
            return data;
        }

        public void setData(BloggerDashboardData data) {
            this.data = data;
        }
    }

    public class BloggerDashboardData {
        private int views;
        private int rank;
        private int followers;
        private String blogTitle;
        private int articleCount;
        private int bookmarkCount;

        public int getArticleCount() {
            return articleCount;
        }

        public void setArticleCount(int articleCount) {
            this.articleCount = articleCount;
        }

        public int getBookmarkCount() {
            return bookmarkCount;
        }

        public void setBookmarkCount(int bookmarkCount) {
            this.bookmarkCount = bookmarkCount;
        }

        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public int getFollowers() {
            return followers;
        }

        public void setFollowers(int followers) {
            this.followers = followers;
        }

        public String getBlogTitle() {
            return blogTitle;
        }

        public void setBlogTitle(String blogTitle) {
            this.blogTitle = blogTitle;
        }
    }
}
