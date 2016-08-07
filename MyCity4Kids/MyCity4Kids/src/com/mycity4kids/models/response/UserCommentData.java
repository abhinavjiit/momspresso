package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by anshul on 8/2/16.
 */
public class UserCommentData  extends BaseData{
    public ArrayList<UserCommentsResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<UserCommentsResult> result) {
        this.result = result;
    }

    ArrayList<UserCommentsResult> result;
    public String getPagination() {
        return pagination;
    }

    public void setPagination(String pagination) {
        this.pagination = pagination;
    }

    String pagination;

}
