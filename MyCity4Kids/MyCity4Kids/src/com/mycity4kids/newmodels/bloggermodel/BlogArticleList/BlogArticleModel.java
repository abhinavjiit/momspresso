package com.mycity4kids.newmodels.bloggermodel.BlogArticleList;

import android.os.Parcel;
import android.os.Parcelable;

import com.kelltontech.model.BaseModel;

/**
 * Created by manish.soni on 28-07-2015.
 */
public class BlogArticleModel extends BaseModel implements Parcelable {

    int id;
    int author_id;
    String title;
    int current_views;
    String thumbnail_image;
    String cover_image;
    String created;
    String author_name;

    public BlogArticleModel(Parcel parcel) {
        this.id = parcel.readInt();
        this.author_id = parcel.readInt();
        this.title = parcel.readString();
        this.thumbnail_image = parcel.readString();
        this.current_views = parcel.readInt();
        this.cover_image = parcel.readString();
        this.created = parcel.readString();
        this.author_name = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(id);
        parcel.writeInt(author_id);
        parcel.writeString(title);
        parcel.writeString(thumbnail_image);
        parcel.writeInt(current_views);
        parcel.writeString(cover_image);
        parcel.writeString(created);
        parcel.writeString(author_name);

    }

    @Override
    public int describeContents() {
        return 0;
    }


    public Creator<BlogArticleModel> CREATOR = new Creator<BlogArticleModel>() {
        @Override
        public BlogArticleModel createFromParcel(Parcel source) {
            return new BlogArticleModel(source);
        }

        @Override
        public BlogArticleModel[] newArray(int size) {
            return new BlogArticleModel[size];
        }

    };

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCurrent_views() {
        return current_views;
    }

    public void setCurrent_views(int current_views) {
        this.current_views = current_views;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }


}
