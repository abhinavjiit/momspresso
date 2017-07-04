package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 26/5/17.
 */
public class TrendingListingData extends BaseData {

    private ArrayList<TrendingListingResult> result;
    private String chunks;
    private String pagination;

    public ArrayList<TrendingListingResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<TrendingListingResult> result) {
        this.result = result;
    }

    public String getChunks() {
        return chunks;
    }

    public void setChunks(String chunks) {
        this.chunks = chunks;
    }

    public String getPagination() {
        return pagination;
    }

    public void setPagination(String pagination) {
        this.pagination = pagination;
    }
}
