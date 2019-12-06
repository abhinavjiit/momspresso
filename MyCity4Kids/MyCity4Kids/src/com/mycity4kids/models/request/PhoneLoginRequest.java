package com.mycity4kids.models.request;

/**
 * Created by hemant on 27/9/18.
 */

public class PhoneLoginRequest {

    private String code;
    private String phone;
    private String verification_code;
    private String sms_token;
    private String auth_token;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(String verification_code) {
        this.verification_code = verification_code;
    }

    public String getSms_token() {
        return sms_token;
    }

    public void setSms_token(String sms_token) {
        this.sms_token = sms_token;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }
}
