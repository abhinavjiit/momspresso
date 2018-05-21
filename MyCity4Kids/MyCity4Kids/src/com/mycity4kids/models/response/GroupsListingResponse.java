package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 12/4/18.
 */

public class GroupsListingResponse extends BaseResponse {

    private int total;
    private int skip;
    private int limit;
    private List<GroupsListingData> data;

    public List<GroupsListingData> getData() {
        return data;
    }

    public void setData(List<GroupsListingData> data) {
        this.data = data;
    }

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
}

