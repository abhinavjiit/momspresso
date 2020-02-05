package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchTrendsAndHistoryResponse extends BaseResponse {
    @SerializedName("data")
    private SearchTrendsAndHistoryData data;

    public SearchTrendsAndHistoryData getData() {
        return data;
    }

    public void setData(SearchTrendsAndHistoryData data) {
        this.data = data;
    }

    public class SearchTrendsAndHistoryData {
        @SerializedName("result")
        private SearchTrendsAndHistoryResult result;

        public SearchTrendsAndHistoryResult getResult() {
            return result;
        }

        public void setResult(SearchTrendsAndHistoryResult result) {
            this.result = result;
        }

        public class SearchTrendsAndHistoryResult {
            @SerializedName("userResult")
            private ArrayList<String> userResult;
            @SerializedName("trendingResult")
            private ArrayList<String> trendingResult;

            public ArrayList<String> getUserResult() {
                return userResult;
            }

            public void setUserResult(ArrayList<String> userResult) {
                this.userResult = userResult;
            }

            public ArrayList<String> getTrendingResult() {
                return trendingResult;
            }

            public void setTrendingResult(ArrayList<String> trendingResult) {
                this.trendingResult = trendingResult;
            }
        }
    }
}
