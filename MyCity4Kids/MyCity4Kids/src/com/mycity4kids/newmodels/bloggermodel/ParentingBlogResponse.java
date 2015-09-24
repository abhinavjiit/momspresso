package com.mycity4kids.newmodels.bloggermodel;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.models.CommonMessage;

import java.util.ArrayList;

/**
 * Created by manish.soni on 27-07-2015.
 */
public class ParentingBlogResponse extends BaseModel {

    private int responseCode;
    private String response;
    private BlogResult result;

    public BlogResult getResult() {
        return result;
    }

    public void setResult(BlogResult result) {
        this.result = result;
    }

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

    public class BlogResult extends CommonMessage {

        BloggerData data;

        public BloggerData getData() {
            return data;
        }

        public void setData(BloggerData data) {
            this.data = data;
        }
    }

    public class BloggerData {

        String total_bloggers;
        String page_count;
        ArrayList<BlogItemModel> data;

        public String getTotal_bloggers() {
            return total_bloggers;
        }

        public void setTotal_bloggers(String total_bloggers) {
            this.total_bloggers = total_bloggers;
        }

        public String getPage_count() {
            return page_count;
        }

        public void setPage_count(String page_count) {
            this.page_count = page_count;
        }

        public ArrayList<BlogItemModel> getData() {
            return data;
        }

        public void setData(ArrayList<BlogItemModel> data) {
            this.data = data;
        }
    }
}
