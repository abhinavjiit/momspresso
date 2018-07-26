package com.mycity4kids.models.response;

public class GroupsAllSearchResponse extends BaseResponse {

    private int total;
    private int skip;
    private int limit;
    private GroupsAllSearchData data;

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

    public GroupsAllSearchData getData() {
        return data;
    }

    public void setData(GroupsAllSearchData data) {
        this.data = data;
    }
}