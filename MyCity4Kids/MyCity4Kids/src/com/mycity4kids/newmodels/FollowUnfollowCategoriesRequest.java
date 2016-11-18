package com.mycity4kids.newmodels;

import com.mycity4kids.models.response.BaseResponse;

import java.util.ArrayList;

/**
 * Created by hemant on 15/11/16.
 */
public class FollowUnfollowCategoriesRequest {
    private ArrayList<String> categories;

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
}
