package com.mycity4kids.newmodels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 15/11/16.
 */
public class FollowUnfollowCategoriesRequest {
    @SerializedName("categories")
    private ArrayList<String> categories;

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
}
