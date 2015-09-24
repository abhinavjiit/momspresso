package com.mycity4kids.newmodels.bloggermodel.BlogArticleList;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.models.CommonMessage;

import java.util.ArrayList;

/**
 * Created by manish.soni on 28-07-2015.
 */
public class NewArticleListingResponse extends BaseModel {

    private int responseCode;
    private String response;
    private BlogArticleResult result;

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

    public BlogArticleResult getResult() {
        return result;
    }

    public void setResult(BlogArticleResult result) {
        this.result = result;
    }

    public class BlogArticleResult extends CommonMessage {

        BlogArticleListing data;

        public BlogArticleListing getData() {
            return data;
        }

        public void setData(BlogArticleListing data) {
            this.data = data;
        }
    }

    public class BlogArticleListing {

        String author_follwers_count;
        String page_count;
        String total_articles;
        ArrayList<BlogArticleModel> data;

        public String getAuthor_follwers_count() {
            return author_follwers_count;
        }

        public void setAuthor_follwers_count(String author_follwers_count) {
            this.author_follwers_count = author_follwers_count;
        }

        public String getPage_count() {
            return page_count;
        }

        public void setPage_count(String page_count) {
            this.page_count = page_count;
        }

        public String getTotal_articles() {
            return total_articles;
        }

        public void setTotal_articles(String total_articles) {
            this.total_articles = total_articles;
        }

        public ArrayList<BlogArticleModel> getData() {
            return data;
        }

        public void setData(ArrayList<BlogArticleModel> data) {
            this.data = data;
        }
    }
}
