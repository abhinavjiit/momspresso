package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 12/6/18.
 */

public class ReportStoryOrCommentResponse extends BaseResponse {
    @SerializedName("data")
    private ReportStoryOrCommentData data;

    public ReportStoryOrCommentData getData() {
        return data;
    }

    public void setData(ReportStoryOrCommentData data) {
        this.data = data;
    }

    public class ReportStoryOrCommentData {
        @SerializedName("result")
        private ReportStoryOrCommentResult result;

        public ReportStoryOrCommentResult getResult() {
            return result;
        }

        public void setResult(ReportStoryOrCommentResult result) {
            this.result = result;
        }

        public class ReportStoryOrCommentResult {
            @SerializedName("id")
            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }

}
