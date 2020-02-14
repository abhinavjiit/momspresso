package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 9/12/16.
 */
public class BloggerAnalyticsViews {
    @SerializedName("views")
    private String views;
    @SerializedName("date")
    private String date;

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
