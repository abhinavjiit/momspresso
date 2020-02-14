package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hemant on 30/4/18.
 */

public class AddGroupPostRequest {

    @SerializedName("content")
    private String content;
    @SerializedName("type")
    private String type;
    @SerializedName("pollType")
    private String pollType;
    @SerializedName("pollOptions")
    private Map<String, String> pollOptions;
    @SerializedName("mediaUrls")
    private LinkedHashMap<String, String> mediaUrls;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("isAnnon")
    private int isAnnon;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPollType() {
        return pollType;
    }

    public void setPollType(String pollType) {
        this.pollType = pollType;
    }

    public Map<String, String> getPollOptions() {
        return pollOptions;
    }

    public void setPollOptions(Map<String, String> pollOptions) {
        this.pollOptions = pollOptions;
    }

    public LinkedHashMap<String, String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(LinkedHashMap<String, String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int isAnnon() {
        return isAnnon;
    }

    public void setAnnon(int annon) {
        isAnnon = annon;
    }
}
