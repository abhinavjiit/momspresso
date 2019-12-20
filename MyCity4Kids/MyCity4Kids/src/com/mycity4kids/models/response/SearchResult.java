package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchResult {
    @SerializedName("article")
    private ArrayList<SearchArticleResult> article;
    @SerializedName("author")
    private ArrayList<SearchAuthorResult> author;
    @SerializedName("blog")
    private ArrayList<SearchBlogResult> blog;
    @SerializedName("topic")
    private ArrayList<SearchTopicResult> topic;
    @SerializedName("video")
    private ArrayList<SearchVideoResult> video;

    public ArrayList<SearchArticleResult> getArticle() {
        return article;
    }

    public void setArticle(ArrayList<SearchArticleResult> article) {
        this.article = article;
    }

    public ArrayList<SearchAuthorResult> getAuthor() {
        return author;
    }

    public void setAuthor(ArrayList<SearchAuthorResult> author) {
        this.author = author;
    }

    public ArrayList<SearchBlogResult> getBlog() {
        return blog;
    }

    public void setBlog(ArrayList<SearchBlogResult> blog) {
        this.blog = blog;
    }

    public ArrayList<SearchTopicResult> getTopic() {
        return topic;
    }

    public void setTopic(ArrayList<SearchTopicResult> topic) {
        this.topic = topic;
    }

    public ArrayList<SearchVideoResult> getVideo() {
        return video;
    }

    public void setVideo(ArrayList<SearchVideoResult> video) {
        this.video = video;
    }
}
