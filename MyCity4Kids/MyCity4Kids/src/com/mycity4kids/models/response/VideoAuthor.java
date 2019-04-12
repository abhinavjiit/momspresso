package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoAuthor implements Parcelable {
    private String blogTitleSlug;
    private String blogTitle;
    private String firstName;
    private String lastName;

    public VideoAuthor(){

    }

    protected VideoAuthor(Parcel in) {
        blogTitleSlug = in.readString();
        blogTitle = in.readString();
        firstName = in.readString();
        lastName = in.readString();
    }

    public static final Creator<VideoAuthor> CREATOR = new Creator<VideoAuthor>() {
        @Override
        public VideoAuthor createFromParcel(Parcel in) {
            return new VideoAuthor(in);
        }

        @Override
        public VideoAuthor[] newArray(int size) {
            return new VideoAuthor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(blogTitleSlug);
        parcel.writeString(blogTitle);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
    }

    public String getBlogTitleSlug() {
        return blogTitleSlug;
    }

    public void setBlogTitleSlug(String blogTitleSlug) {
        this.blogTitleSlug = blogTitleSlug;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
