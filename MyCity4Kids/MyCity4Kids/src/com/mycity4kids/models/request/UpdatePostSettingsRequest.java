package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 1/5/18.
 */

public class UpdatePostSettingsRequest {
    @SerializedName("userId")
    private String userId;
    @SerializedName("isActive")
    private int isActive;
    @SerializedName("isPinned")
    private int isPinned;
    @SerializedName("pinnedBy")
    private String pinnedBy;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(int isPinned) {
        this.isPinned = isPinned;
    }

    public String getPinnedBy() {
        return pinnedBy;
    }

    public void setPinnedBy(String pinnedBy) {
        this.pinnedBy = pinnedBy;
    }
}
