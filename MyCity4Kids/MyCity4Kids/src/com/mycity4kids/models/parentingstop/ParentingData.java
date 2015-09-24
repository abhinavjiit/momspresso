package com.mycity4kids.models.parentingstop;

import java.io.Serializable;
import java.util.ArrayList;

public class ParentingData implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1834068184477924094L;
    int total_articles;
    int page_count;
    private ArrayList<CommonParentingList> data;
    private ArrayList<Sort> sort;


    public int getTotal_articles() {
        return total_articles;
    }

    public void setTotal_articles(int total_articles) {
        this.total_articles = total_articles;
    }

    public ArrayList<Sort> getSort() {
        return sort;
    }

    public void setSort(ArrayList<Sort> sort) {
        this.sort = sort;
    }

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }

    public ArrayList<CommonParentingList> getData() {
        return data;
    }

    public void setData(ArrayList<CommonParentingList> data) {
        this.data = data;
    }
}
