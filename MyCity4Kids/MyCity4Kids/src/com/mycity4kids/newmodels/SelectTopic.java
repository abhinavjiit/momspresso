package com.mycity4kids.newmodels;

import com.mycity4kids.models.Topics;

import java.util.ArrayList;

public class SelectTopic {
    private String id;
    private String displayName;
    private String backgroundImageUrl;
    private ArrayList<Topics> childTopics;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public ArrayList<Topics> getChildTopics() {
        return childTopics;
    }

    public void setChildTopics(ArrayList<Topics> childTopics) {
        this.childTopics = childTopics;
    }
}