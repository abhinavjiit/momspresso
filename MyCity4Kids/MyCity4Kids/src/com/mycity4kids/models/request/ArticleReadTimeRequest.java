package com.mycity4kids.models.request;

/**
 * Created by hemant on 2/12/16.
 */
public class ArticleReadTimeRequest {

    private String articleId;
    private String startTime;
    private String timeSpent;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }
}
