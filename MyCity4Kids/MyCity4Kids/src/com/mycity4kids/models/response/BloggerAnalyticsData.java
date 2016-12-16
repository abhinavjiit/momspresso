package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 9/12/16.
 */
public class BloggerAnalyticsData {

    private ArrayList<BloggerAnalyticsViews> views;
    private BloggerAnalyticsSocial social;

    public ArrayList<BloggerAnalyticsViews> getViews() {
        return views;
    }

    public void setViews(ArrayList<BloggerAnalyticsViews> views) {
        this.views = views;
    }

    public BloggerAnalyticsSocial getSocial() {
        return social;
    }

    public void setSocial(BloggerAnalyticsSocial social) {
        this.social = social;
    }
}
