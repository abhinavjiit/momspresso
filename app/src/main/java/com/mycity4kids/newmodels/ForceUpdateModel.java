package com.mycity4kids.newmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 6/5/16.
 */
public class ForceUpdateModel {

    @SerializedName("responseCode")
    private int responseCode;
    @SerializedName("response")
    private String response;
    @SerializedName("result")
    private ForceUpdateResult result;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public ForceUpdateResult getResult() {
        return result;
    }

    public void setResult(ForceUpdateResult result) {
        this.result = result;
    }

    public class ForceUpdateResult {
        @SerializedName("message")
        private String message;
        @SerializedName("data")
        private ForceUpdateData data;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ForceUpdateData getData() {
            return data;
        }

        public void setData(ForceUpdateData data) {
            this.data = data;
        }

        public class ForceUpdateData {
            @SerializedName("isAppUpdateRequired")
            private int isAppUpdateRequired;
            @SerializedName("message")
            private String message;
            @SerializedName("onlineVersionCode")
            private String onlineVersionCode;

            public String getOnlineVersionCode() {
                return onlineVersionCode;
            }

            public void setOnlineVersionCode(String onlineVersionCode) {
                this.onlineVersionCode = onlineVersionCode;
            }

            public int getIsAppUpdateRequired() {
                return isAppUpdateRequired;
            }

            public void setIsAppUpdateRequired(int isAppUpdateRequired) {
                this.isAppUpdateRequired = isAppUpdateRequired;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }
        }
    }
}
