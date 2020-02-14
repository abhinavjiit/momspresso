package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 30/4/18.
 */

public class UpdateGroupPostRequest {
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("disableComments")
    private int disableComments;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getDisableComments() {
        return disableComments;
    }

    public void setDisableComments(int disableComments) {
        this.disableComments = disableComments;
    }
}
