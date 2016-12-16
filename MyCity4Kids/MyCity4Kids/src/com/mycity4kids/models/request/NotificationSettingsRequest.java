package com.mycity4kids.models.request;

/**
 * Created by hemant on 7/12/16.
 */
public class NotificationSettingsRequest {
    private String topBloggers;
    private String topArticles;
    private String fbPublish;
    private String increasedViews;
    private String topThings;
    private String bloggerAnalytics;
    private String weeklyEvents;
    private String comments;
    private String follow;

    public String getTopBloggers() {
        return topBloggers;
    }

    public void setTopBloggers(String topBloggers) {
        this.topBloggers = topBloggers;
    }

    public String getTopArticles() {
        return topArticles;
    }

    public void setTopArticles(String topArticles) {
        this.topArticles = topArticles;
    }

    public String getFbPublish() {
        return fbPublish;
    }

    public void setFbPublish(String fbPublish) {
        this.fbPublish = fbPublish;
    }

    public String getIncreasedViews() {
        return increasedViews;
    }

    public void setIncreasedViews(String increasedViews) {
        this.increasedViews = increasedViews;
    }

    public String getTopThings() {
        return topThings;
    }

    public void setTopThings(String topThings) {
        this.topThings = topThings;
    }

    public String getBloggerAnalytics() {
        return bloggerAnalytics;
    }

    public void setBloggerAnalytics(String bloggerAnalytics) {
        this.bloggerAnalytics = bloggerAnalytics;
    }

    public String getWeeklyEvents() {
        return weeklyEvents;
    }

    public void setWeeklyEvents(String weeklyEvents) {
        this.weeklyEvents = weeklyEvents;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }
}
