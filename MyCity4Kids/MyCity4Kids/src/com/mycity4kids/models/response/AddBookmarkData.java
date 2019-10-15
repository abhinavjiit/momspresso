package com.mycity4kids.models.response;

/**
 * Created by hemant on 29/7/16.
 */
public class AddBookmarkData {
    private String msg;
    private String userId;
    private String articleId;
    private AddBookmarkResult result;
    private String id;

    public String getArticleId() {
        return id;
    }

    public void setArticleId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AddBookmarkResult getResult() {
        return result;
    }

    public void setResult(AddBookmarkResult result) {
        this.result = result;
    }
}
