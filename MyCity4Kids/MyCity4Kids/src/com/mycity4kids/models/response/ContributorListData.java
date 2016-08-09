package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by anshul on 8/7/16.
 */
public class ContributorListData extends BaseData {
    public ArrayList<ContributorListResult>  getResult() {
        return result;
    }

    public void setResult(ArrayList<ContributorListResult>  result) {
        this.result = result;
    }

   ArrayList<ContributorListResult> result;

    public String getPagination() {
        return pagination;
    }

    public void setPagination(String pagination) {
        this.pagination = pagination;
    }

    String pagination;
}
