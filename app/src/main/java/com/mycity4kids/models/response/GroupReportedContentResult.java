package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 18/7/18.
 */

public class GroupReportedContentResult {

    @SerializedName("id")
    private int id;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("postId")
    private int postId;
    @SerializedName("responseId")
    private int responseId;
    @SerializedName("isModerated")
    private int isModerated;
    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("updatedAt")
    private long updatedAt;
    @SerializedName("counts")
    private ArrayList<GroupPostCounts> counts;
    @SerializedName("actionResponse")
    private String actionResponse;
    @SerializedName("actionBy")
    private String actionBy;
    @SerializedName("content")
    private GroupPostResult content;
    @SerializedName("abusiveContentCount")
    private int abusiveContentCount;
    @SerializedName("itsASpamCount")
    private int itsASpamCount;
    @SerializedName("notInterestingCount")
    private int notInterestingCount;
    @SerializedName("itHurtsReligiousSentimentCount")
    private int itHurtsReligiousSentimentCount;
    @SerializedName("otherCount")
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

    public int getItHurtsReligiousSentimentCount() {
        return itHurtsReligiousSentimentCount;
    }

    public void setItHurtsReligiousSentimentCount(int itHurtsReligiousSentimentCount) {
        this.itHurtsReligiousSentimentCount = itHurtsReligiousSentimentCount;
    }

    public int getOtherCount() {
        return otherCount;
    }

    public void setOtherCount(int otherCount) {
        this.otherCount = otherCount;
    }
}
