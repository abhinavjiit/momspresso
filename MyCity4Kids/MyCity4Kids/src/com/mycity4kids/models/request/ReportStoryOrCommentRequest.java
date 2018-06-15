package com.mycity4kids.models.request;

/**
 * Created by hemant on 12/6/18.
 */

public class ReportStoryOrCommentRequest {

    private String id;
    private String reason;
    private int type;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
