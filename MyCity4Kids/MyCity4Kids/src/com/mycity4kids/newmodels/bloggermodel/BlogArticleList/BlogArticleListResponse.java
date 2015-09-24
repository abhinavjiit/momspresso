package com.mycity4kids.newmodels.bloggermodel.BlogArticleList;

import android.os.Parcel;
import android.os.Parcelable;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.models.CommonMessage;

import java.util.ArrayList;

/**
 * Created by manish.soni on 28-07-2015.
 */
public class BlogArticleListResponse extends BaseModel {

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
        SortArticleBlogList recent;
        SortArticleBlogList popular;

        public String getAuthor_follwers_count() {
            return author_follwers_count;
        }

        public void setAuthor_follwers_count(String author_follwers_count) {
            this.author_follwers_count = author_follwers_count;
        }

        public SortArticleBlogList getRecent() {
            return recent;
        }

        public void setRecent(SortArticleBlogList recent) {
            this.recent = recent;
        }

        public SortArticleBlogList getPopular() {
            return popular;
        }

        public void setPopular(SortArticleBlogList popular) {
            this.popular = popular;
        }
    }

    public class SortArticleBlogList implements Parcelable {

        String page_count;
        String total_articles;
        ArrayList<BlogArticleModel> data;

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

        public SortArticleBlogList(Parcel parcel) {
            this.page_count = parcel.readString();
            this.total_articles = parcel.readString();
            this.data = parcel.readArrayList(BlogArticleModel.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(page_count);
            parcel.writeString(total_articles);
            parcel.writeList(data);
        }

        public Creator<SortArticleBlogList> CREATOR = new Creator<SortArticleBlogList>() {
            @Override
            public SortArticleBlogList createFromParcel(Parcel source) {
                return new SortArticleBlogList(source);
            }

            @Override
            public SortArticleBlogList[] newArray(int size) {
                return new SortArticleBlogList[size];
            }
        };
    }
}
