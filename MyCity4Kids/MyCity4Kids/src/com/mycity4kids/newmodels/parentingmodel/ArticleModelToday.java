package com.mycity4kids.newmodels.parentingmodel;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.models.parentingstop.CommonParentingList;

import java.util.ArrayList;

/**
 * Created by manish.soni on 20-07-2015.
 */
public class ArticleModelToday extends BaseModel {


    int responseCode;
    String response;
    AllArticleResponse result;

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

    public AllArticleResponse getResult() {
        return result;
    }

    public void setResult(AllArticleResponse result) {
        this.result = result;
    }

    public class AllArticleResponse {

        String message;
        ArticleClass data;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArticleClass getData() {
            return data;
        }

        public void setData(ArticleClass data) {
            this.data = data;
        }
    }

    public class ArticleClass {

        int total_articles;
        int page_count;
        AllArticles data;

        public int getTotal_articles() {
            return total_articles;
        }

        public void setTotal_articles(int total_articles) {
            this.total_articles = total_articles;
        }

        public int getPage_count() {
            return page_count;
        }

        public void setPage_count(int page_count) {
            this.page_count = page_count;
        }

        public AllArticles getData() {
            return data;
        }

        public void setData(AllArticles data) {
            this.data = data;
        }
    }

    public class AllArticles {

        ArrayList<CommonParentingList> recent;
        ArrayList<CommonParentingList> popular;
        ArrayList<CommonParentingList> trending;

        public ArrayList<CommonParentingList> getRecent() {
            return recent;
        }

        public void setRecent(ArrayList<CommonParentingList> recent) {
            this.recent = recent;
        }

        public ArrayList<CommonParentingList> getPopular() {
            return popular;
        }

        public void setPopular(ArrayList<CommonParentingList> popular) {
            this.popular = popular;
        }

        public ArrayList<CommonParentingList> getTrending() {
            return trending;
        }

        public void setTrending(ArrayList<CommonParentingList> trending) {
            this.trending = trending;
        }

//        public AllArticles(Parcel parcel) {
//            this.recent = parcel.readArrayList(null);
//            this.popular = parcel.readArrayList(null);
//            this.trending = parcel.readArrayList(null);
//        }
//
//        @Override
//        public int describeContents() {
//            return 0;
//        }
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            dest.writeList(recent);
//            dest.writeList(popular);
//            dest.writeList(trending);
//        }
//
//        public Creator<AllArticles> CREATOR = new Creator<AllArticles>() {
//            @Override
//            public AllArticles createFromParcel(Parcel source) {
//                return new AllArticles(source);
//            }
//
//            @Override
//            public AllArticles[] newArray(int size) {
//                return new AllArticles[size];
//            }
//
//        };

    }

}
