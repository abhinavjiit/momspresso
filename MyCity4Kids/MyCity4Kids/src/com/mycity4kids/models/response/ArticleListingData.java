package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by hemant on 5/7/16.
 */
public class ArticleListingData extends BaseData {

    private ArrayList<ArticleListingResult> result;
    private String chunks;
    private String pagination;

    public ArrayList<ArticleListingResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<ArticleListingResult> result) {
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
