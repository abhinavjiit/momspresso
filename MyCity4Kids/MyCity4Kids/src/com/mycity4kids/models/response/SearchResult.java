package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchResult {
    private ArrayList<SearchArticleResult> article;
    private ArrayList<SearchAuthorResult> author;
    private ArrayList<SearchBlogResult> blog;
    private ArrayList<SearchTopicResult> topic;

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
}
