package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by hemant on 26/5/17.
 */
public class TrendingListingResult implements Parcelable {
    private ArrayList<ArticleListingResult> articleList;
    private String id;
    private String slug;
    private String display_name;
    private String title;

    protected TrendingListingResult(Parcel in) {
        articleList = in.createTypedArrayList(ArticleListingResult.CREATOR);
        id = in.readString();
        slug = in.readString();
        display_name = in.readString();
        title = in.readString();
    }

    public ArrayList<ArticleListingResult> getArticleList() {
        return articleList;
    }

    public void setArticleList(ArrayList<ArticleListingResult> articleList) {
        this.articleList = articleList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static final Creator<TrendingListingResult> CREATOR = new Creator<TrendingListingResult>() {
        @Override
        public TrendingListingResult createFromParcel(Parcel in) {
            return new TrendingListingResult(in);
        }

        @Override
        public TrendingListingResult[] newArray(int size) {
            return new TrendingListingResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(articleList);
        dest.writeString(id);
        dest.writeString(slug);
        dest.writeString(display_name);
        dest.writeString(title);
    }
}
