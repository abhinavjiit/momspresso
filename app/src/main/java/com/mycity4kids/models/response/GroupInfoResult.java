package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 28/5/18.
 */

public class GroupInfoResult implements Parcelable {

    @SerializedName("title")
    private String title;
    @SerializedName("color")
    private String color;
    @SerializedName("annonAllowed")
    private int annonAllowed = 0;

    public String getName() {
        return title;
    }

    public void setName(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAnnonAllowed() {
        return annonAllowed;
    }

    public void setAnnonAllowed(int annonAllowed) {
        this.annonAllowed = annonAllowed;
    }

    protected GroupInfoResult(Parcel in) {
        title = in.readString();
        color = in.readString();
        annonAllowed = in.readInt();
    }

    public static final Creator<GroupInfoResult> CREATOR = new Creator<GroupInfoResult>() {
        @Override
        public GroupInfoResult createFromParcel(Parcel in) {
            return new GroupInfoResult(in);
        }

        @Override
        public GroupInfoResult[] newArray(int size) {
            return new GroupInfoResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(color);
        dest.writeInt(annonAllowed);
    }
}
