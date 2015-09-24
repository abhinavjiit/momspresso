package com.mycity4kids.models.parentingstop;

import java.util.ArrayList;

public class CommonParentingData {
    /**
     * in case of normal parenting artilcles or blogs listing:
     */
    private int total_articles;
    private int page_count;
    private ArrayList<CommonParentingList> data;
    private ArrayList<ParentingSort> sort;
    /**
     * in case of filter they are giving total count
     */
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotal_articles() {
        return total_articles;
    }

    public void setTotal_articles(int total_articles) {
        this.total_articles = total_articles;
    }

    public ArrayList<CommonParentingList> getData() {
        return data;
    }

    public void setData(ArrayList<CommonParentingList> data) {
        this.data = data;
    }

    public ArrayList<ParentingSort> getSort() {
        return sort;
    }

    public void setSort(ArrayList<ParentingSort> sort) {
        this.sort = sort;
    }

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }
}
