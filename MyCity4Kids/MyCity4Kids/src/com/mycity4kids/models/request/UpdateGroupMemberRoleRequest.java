package com.mycity4kids.models.request;

/**
 * Created by hemant on 1/5/18.
 */

public class UpdateGroupMemberRoleRequest {
    private String userId;
    private int isAdmin;
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
