package com.mycity4kids.newmodels.bloggermodel.BlogArticleList;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.models.CommonMessage;
import com.mycity4kids.newmodels.bloggermodel.BlogItemModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 25-08-2015.
 */
public class BlogDetailWithArticleModel extends BaseModel {

    private int responseCode;
    private String response;
    BlogData result;

    public BlogData getResult() {
        return result;
    }

    public void setResult(BlogData result) {
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

    public class BlogData extends CommonMessage {

        BlogDetailData data;

        public BlogDetailData getData() {
            return data;
        }

        public void setData(BlogDetailData data) {
            this.data = data;
        }
    }

    public class BlogDetailData extends CommonMessage {
        BlogArticleListResponse.SortArticleBlogList recent;
        BlogArticleListResponse.SortArticleBlogList popular;
        BlogItemModel author_details;


        public BlogArticleListResponse.SortArticleBlogList getRecent() {
            return recent;
        }

        public void setRecent(BlogArticleListResponse.SortArticleBlogList recent) {
            this.recent = recent;
        }

        public BlogArticleListResponse.SortArticleBlogList getPopular() {
            return popular;
        }

        public void setPopular(BlogArticleListResponse.SortArticleBlogList popular) {
            this.popular = popular;
        }

        public BlogItemModel getAuthor_details() {
            return author_details;
        }

        public void setAuthor_details(BlogItemModel author_details) {
            this.author_details = author_details;
        }

    }

    public class BlogArticleListing {
        String author_follwers_count;
        String page_count;
        String total_articles;
        ArrayList<BlogArticleModel> data;
    }

}
