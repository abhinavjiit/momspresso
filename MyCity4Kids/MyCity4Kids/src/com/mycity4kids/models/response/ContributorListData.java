package com.mycity4kids.models.response;

/**
 * Created by anshul on 8/7/16.
 */
public class ContributorListData extends BaseData {
    public ContributorListResult getResult() {
        return result;
    }

    public void setResult(ContributorListResult result) {
        this.result = result;
    }

    ContributorListResult result;
}
