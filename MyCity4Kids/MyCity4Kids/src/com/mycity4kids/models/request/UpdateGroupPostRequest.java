package com.mycity4kids.models.request;

import java.util.Map;

/**
 * Created by hemant on 30/4/18.
 */

public class UpdateGroupPostRequest {

    private int groupId;
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
