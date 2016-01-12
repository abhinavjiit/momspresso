package com.chatPlatform.models;

import com.mycity4kids.models.basemodel.BaseDataModel;

import java.util.ArrayList;

/**
 * Created by anshul on 1/6/16.
 */
public class GroupInfo extends BaseDataModel {
    int userId;
    String groupId;
    ArrayList<String> inviteList;

    public ArrayList<String> getInviteList() {
        return inviteList;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setInviteList(ArrayList<String> inviteList) {
        this.inviteList = inviteList;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getGroupId() {
        return groupId;
    }
}
