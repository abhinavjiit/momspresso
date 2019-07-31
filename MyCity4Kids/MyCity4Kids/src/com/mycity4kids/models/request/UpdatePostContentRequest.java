package com.mycity4kids.models.request;

import java.util.LinkedHashMap;

/**
 * Created by hemant on 1/5/18.
 */

public class UpdatePostContentRequest {
    private String content;
    private LinkedHashMap<String, String> mediaUrls;
    private String type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LinkedHashMap<String, String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(LinkedHashMap<String, String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
