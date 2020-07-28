package com.mycity4kids.tagging;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.response.BaseResponse;
import java.util.ArrayList;

public class MentionsResponse extends BaseResponse {

    @SerializedName("data")
    private MentionsData data;

    public MentionsData getData() {
        return data;
    }

    public void setData(MentionsData data) {
        this.data = data;
    }

    public class MentionsData {

        @SerializedName("result")
        private ArrayList<Mentions> result;

        public ArrayList<Mentions> getResult() {
            return result;
        }

        public void setResult(ArrayList<Mentions> result) {
            this.result = result;
        }
    }
}
