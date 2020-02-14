package com.mycity4kids.models.collectionsModels;

import com.google.gson.annotations.SerializedName;

public class FollowCollectionRequestModel {
    @SerializedName("userId")
    private String userId;
    @SerializedName("userCollectionId")
    private String userCollectionId;
    @SerializedName("sortOrder")
    private int sortOrder;
    @SerializedName("deleted")
    private boolean deleted;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserCollectionId() {
        return userCollectionId;
    }

    public void setUserCollectionId(String userCollectionId) {
        this.userCollectionId = userCollectionId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
