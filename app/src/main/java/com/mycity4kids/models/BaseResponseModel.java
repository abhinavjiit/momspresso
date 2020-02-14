package com.mycity4kids.models;

import com.google.gson.annotations.SerializedName;

public class BaseResponseModel {
    @SerializedName("responseCode")
    private int responseCode;
    @SerializedName("response")
    private String response;

    /**
     * @return the responseCode
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode the responseCode to set
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(String response) {
        this.response = response;
    }


}
