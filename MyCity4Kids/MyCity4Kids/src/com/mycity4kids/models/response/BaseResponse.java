package com.mycity4kids.models.response;

/**
 * Created by hemant on 22/6/16.
 */
public class BaseResponse {
    private int code;
    private String status;
    private String reason;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
