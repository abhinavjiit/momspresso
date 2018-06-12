package com.mycity4kids.models.request;

/**
 * Created by hemant on 12/6/18.
 */

public class ReportStoryOrCommentRequest {

    private String id;
    private String reason;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
