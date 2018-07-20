package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 19/7/18.
 */

public class GroupsMembershipResponse {

    private int total;
    private int skip;
    private int limit;
    private GroupsMembershipData data;

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

    public GroupsMembershipData getData() {
        return data;
    }

    public void setData(GroupsMembershipData data) {
        this.data = data;
    }
}
