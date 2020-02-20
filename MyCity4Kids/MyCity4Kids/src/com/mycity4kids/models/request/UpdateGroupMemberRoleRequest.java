package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 1/5/18.
 */

public class UpdateGroupMemberRoleRequest {
    @SerializedName("userId")
    private String userId;
    @SerializedName("isAdmin")
    private int isAdmin;
    @SerializedName("isModerator")
    private int isModerator;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(int isModerator) {
        this.isModerator = isModerator;
    }
}
