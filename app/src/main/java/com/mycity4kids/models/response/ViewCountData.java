package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 14/10/16.
 */
public class ViewCountData {
    @SerializedName("count")
    private String count;
    @SerializedName("commentCount")
    private String commentCount;
    @SerializedName("likeCount")
    private String likeCount;
    @SerializedName("result")
    private int result;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
