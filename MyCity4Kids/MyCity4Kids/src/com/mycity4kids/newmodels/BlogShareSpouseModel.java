package com.mycity4kids.newmodels;

import com.mycity4kids.models.basemodel.BaseDataModel;

import java.util.ArrayList;

/**
 * Created by thehi on 02-11-2015.
 */
public class BlogShareSpouseModel extends BaseDataModel {

    String articleId;
    ArrayList<String> sharedWithUserList;

    public ArrayList<String> getSharedWithUserList() {
        return sharedWithUserList;
    }

    public void setSharedWithUserList(ArrayList<String> sharedWithUserList) {
        this.sharedWithUserList = sharedWithUserList;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
}

