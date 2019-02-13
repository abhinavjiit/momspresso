package com.mycity4kids.models.request;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hemant on 30/4/18.
 */

public class AddGroupPostRequest {

    private String content;
    private String type;
    private String pollType;
    private Map<String, String> pollOptions;
    private LinkedHashMap<String, String> mediaUrls;
    private int groupId;
    private String userId;
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
