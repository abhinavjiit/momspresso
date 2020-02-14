package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

public class ReportSpamRequest {
    @SerializedName("subject")
    private String subject;
    @SerializedName("body")
    private String body;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
