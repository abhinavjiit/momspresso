package com.mycity4kids.vlogs;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.Topics;
import java.util.ArrayList;

/**
 * Created by hemant on 1/6/16.
 */
public class VlogsCategoryWiseChallengesResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("code")
    private int code;
    @SerializedName("reason")
    private String reason;
    @SerializedName("data")
    private VlogsCategoryWiseChallengesData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public VlogsCategoryWiseChallengesData getData() {
        return data;
    }

    public void setData(VlogsCategoryWiseChallengesData data) {
        this.data = data;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public class VlogsCategoryWiseChallengesData {

        @SerializedName("result")
        private ArrayList<Topics> result;

        public ArrayList<Topics> getResult() {
            return result;
        }

        public void setResult(ArrayList<Topics> result) {
            this.result = result;
        }
    }
}
