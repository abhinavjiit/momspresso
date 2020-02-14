package com.mycity4kids.newmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.Topics;

import java.util.ArrayList;

public class SelectTopic implements Parcelable {
    @SerializedName("id")
    private String id;
    @SerializedName("displayName")
    private String displayName;
    @SerializedName("backgroundImageUrl")
    private String backgroundImageUrl;
    @SerializedName("childTopics")
    private ArrayList<Topics> childTopics;

    public SelectTopic() {

    }

    protected SelectTopic(Parcel in) {
        id = in.readString();
        displayName = in.readString();
        backgroundImageUrl = in.readString();
        childTopics = in.createTypedArrayList(Topics.CREATOR);
    }

    public static final Creator<SelectTopic> CREATOR = new Creator<SelectTopic>() {
        @Override
        public SelectTopic createFromParcel(Parcel in) {
            return new SelectTopic(in);
        }

        @Override
        public SelectTopic[] newArray(int size) {
            return new SelectTopic[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public ArrayList<Topics> getChildTopics() {
        return childTopics;
    }

    public void setChildTopics(ArrayList<Topics> childTopics) {
        this.childTopics = childTopics;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(displayName);
        dest.writeString(backgroundImageUrl);
        dest.writeTypedList(childTopics);
    }
}