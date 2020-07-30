package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class UserHandleSuggestionResponse extends BaseResponse {
    @SerializedName("data")
    private UserHandleData data;

    public UserHandleData getData() {
        return data;
    }

    public void setData(UserHandleData data) {
        this.data = data;
    }


    public class UserHandleData {
        @SerializedName("msg")
        private String msg;
        @SerializedName("result")
        private UserHandleResult result;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public UserHandleResult getResult() {
            return result;
        }

        public void setResult(UserHandleResult result) {
            this.result = result;
        }

        public class UserHandleResult {

            @SerializedName("suggestions")
            public ArrayList<String> suggestionData;

            public ArrayList<String> getSuggestionData() {
                return suggestionData;
            }

            public void setSuggestionData(
                    ArrayList<String> suggestionData) {
                this.suggestionData = suggestionData;
            }
        }
    }

}
