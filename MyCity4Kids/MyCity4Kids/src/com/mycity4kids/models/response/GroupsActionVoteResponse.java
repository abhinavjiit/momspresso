package com.mycity4kids.models.response;

public class GroupsActionVoteResponse extends BaseResponse {

    private int total;
    private int skip;
    private int limit;
    private GroupsActionVoteData data;

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

    public GroupsActionVoteData getData() {
        return data;
    }

    public void setData(GroupsActionVoteData data) {
        this.data = data;
    }
}