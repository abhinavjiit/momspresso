package com.mycity4kids.newmodels.parentingmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by manish.soni on 20-07-2015.
 */
public class ArticleDataModel implements Parcelable {

    int id;
    String title;
    String created;
    int author_id;
    String author_name;
    String thumbnail_image;
    String author_image;
    String author_type;
    String author_color_code;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_image() {
        return author_image;
    }

    public void setAuthor_image(String author_image) {
        this.author_image = author_image;
    }

    public String getAuthor_type() {
        return author_type;
    }

    public void setAuthor_type(String author_type) {
        this.author_type = author_type;
    }

    public String getAuthor_color_code() {
        return author_color_code;
    }

    public void setAuthor_color_code(String author_color_code) {
        this.author_color_code = author_color_code;
    }

    public ArticleDataModel(Parcel parcel) {
        this.id = parcel.readInt();
        this.title = parcel.readString();
        this.created = parcel.readString();
        this.author_id = parcel.readInt();
        this.author_name = parcel.readString();
        this.thumbnail_image = parcel.readString();
        this.author_image = parcel.readString();
        this.author_type = parcel.readString();
        this.author_color_code = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(created);
        dest.writeInt(author_id);
        dest.writeString(author_name);
        dest.writeString(thumbnail_image);
        dest.writeString(author_image);
        dest.writeString(author_type);
        dest.writeString(author_color_code);
    }

    public Creator<ArticleDataModel> CREATOR = new Creator<ArticleDataModel>() {
        @Override
        public ArticleDataModel createFromParcel(Parcel source) {
            return new ArticleDataModel(source);
        }

        @Override
        public ArticleDataModel[] newArray(int size) {
            return new ArticleDataModel[size];
        }

    };
}
