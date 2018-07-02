package com.mycity4kids.models.response;

/**
 * Created by hemant on 12/6/18.
 */

public class ReportStoryOrCommentResponse extends BaseResponse {

    private ReportStoryOrCommentData data;

    public ReportStoryOrCommentData getData() {
        return data;
    }

    public void setData(ReportStoryOrCommentData data) {
        this.data = data;
    }

    public class ReportStoryOrCommentData {
        private ReportStoryOrCommentResult result;

        public ReportStoryOrCommentResult getResult() {
            return result;
        }

        public void setResult(ReportStoryOrCommentResult result) {
            this.result = result;
        }

        public class ReportStoryOrCommentResult {
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
