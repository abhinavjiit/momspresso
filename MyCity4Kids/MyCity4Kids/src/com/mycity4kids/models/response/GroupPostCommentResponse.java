package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 21/5/18.
 */

public class GroupPostCommentResponse extends BaseResponse {

    private int total;
    private int skip;
    private int limit;
    private List<GroupPostCommentData> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<GroupPostCommentData> getData() {
        return data;
    }

    public void setData(List<GroupPostCommentData> data) {
        this.data = data;
    }
}
