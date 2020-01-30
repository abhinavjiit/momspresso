package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 18/7/18.
 */

public class GroupsReportedContentResponse {

    @SerializedName("total")
    private int total;
    @SerializedName("skip")
    private int skip;
    @SerializedName("limit")
    private int limit;
    @SerializedName("data")
    private GroupsReportedContentData data;

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

    public GroupsReportedContentData getData() {
        return data;
    }

    public void setData(GroupsReportedContentData data) {
        this.data = data;
    }
}
