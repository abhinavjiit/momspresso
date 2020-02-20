package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 26/5/17.
 */
public class TrendingListingData extends BaseData {
    @SerializedName("result")
    private ArrayList<TrendingListingResult> result;
    @SerializedName("chunks")
    private String chunks;
    @SerializedName("pagination")
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
