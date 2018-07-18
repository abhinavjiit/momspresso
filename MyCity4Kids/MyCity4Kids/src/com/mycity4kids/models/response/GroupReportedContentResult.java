package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 18/7/18.
 */

public class GroupReportedContentResult {

    private int id;
    private int groupId;
    private int postId;
    private int responseId;
    private int isModerated;
    private long createdAt;
    private long updatedAt;
    private ArrayList<GroupPostCounts> counts;
    private String actionResponse;
    private String actionBy;
    private GroupPostResult content;
    private int abusiveContentCount;
    private int itsASpamCount;
    private int notInterestingCount;
    private int otherCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getResponseId() {
        return responseId;
    }

    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }

    public int getIsModerated() {
        return isModerated;
    }

    public void setIsModerated(int isModerated) {
        this.isModerated = isModerated;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ArrayList<GroupPostCounts> getCounts() {
        return counts;
    }

    public void setCounts(ArrayList<GroupPostCounts> counts) {
        this.counts = counts;
    }

    public String getActionResponse() {
        return actionResponse;
    }

    public void setActionResponse(String actionResponse) {
        this.actionResponse = actionResponse;
    }

    public String getActionBy() {
        return actionBy;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }

    public GroupPostResult getContent() {
        return content;
    }

    public void setContent(GroupPostResult content) {
        this.content = content;
    }

    public int getAbusiveContentCount() {
        return abusiveContentCount;
    }

    public void setAbusiveContentCount(int abusiveContentCount) {
        this.abusiveContentCount = abusiveContentCount;
    }

    public int getItsASpamCount() {
        return itsASpamCount;
    }

    public void setItsASpamCount(int itsASpamCount) {
        this.itsASpamCount = itsASpamCount;
    }

    public int getNotInterestingCount() {
        return notInterestingCount;
    }

    public void setNotInterestingCount(int notInterestingCount) {
        this.notInterestingCount = notInterestingCount;
    }

    public int getOtherCount() {
        return otherCount;
    }

    public void setOtherCount(int otherCount) {
        this.otherCount = otherCount;
    }
}
