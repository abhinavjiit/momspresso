package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

public class VlogsEventRequest {
    @SerializedName("topic")
    private String topic;
    @SerializedName("payload")
    private Payload payload = new Payload();
    @SerializedName("createdTime")
    private long createdTime;
    @SerializedName("key")
    private String key;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public class Payload {

    }
}
