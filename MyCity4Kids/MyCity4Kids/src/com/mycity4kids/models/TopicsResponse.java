package com.mycity4kids.models;

import java.util.ArrayList;

/**
 * Created by hemant on 1/6/16.
 */
public class TopicsResponse {
//    private int responseCode;
//    private String response;
//    private TopicResult result;
//
//    public int getResponseCode() {
//        return responseCode;
//    }
//
//    public void setResponseCode(int responseCode) {
//        this.responseCode = responseCode;
//    }
//
//    public String getResponse() {
//        return response;
//    }
//
//    public void setResponse(String response) {
//        this.response = response;
//    }
//
//    public TopicResult getResult() {
//        return result;
//    }
//
//    public void setResult(TopicResult result) {
//        this.result = result;
//    }
//
//    public class TopicResult {
//        private String message;
//        private ArrayList<Topics> data;
//
//        public String getMessage() {
//            return message;
//        }
//
//        public void setMessage(String message) {
//            this.message = message;
//        }
//
//        public ArrayList<Topics> getData() {
//            return data;
//        }
//
//        public void setData(ArrayList<Topics> data) {
//            this.data = data;
//        }
//    }

    private String status;
    private int code;
    private ArrayList<Topics> data;

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

    public ArrayList<Topics> getData() {
        return data;
    }

    public void setData(ArrayList<Topics> data) {
        this.data = data;
    }
}
