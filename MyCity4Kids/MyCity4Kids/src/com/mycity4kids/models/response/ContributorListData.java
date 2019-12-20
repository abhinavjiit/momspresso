package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by anshul on 8/7/16.
 */
public class ContributorListData extends BaseData {
    @SerializedName("result")
    ArrayList<ContributorListResult> result;

    @SerializedName("pagination")
    private String pagination;

    public ArrayList<ContributorListResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<ContributorListResult> result) {
        this.result = result;
    }

    public String getPagination() {
        return pagination;
    }

    public void setPagination(String pagination) {
        this.pagination = pagination;
    }


}
