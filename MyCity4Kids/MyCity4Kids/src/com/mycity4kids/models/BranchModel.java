package com.mycity4kids.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BranchModel implements Parcelable {

    String type;
    String id;
    private String mapped_category;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMapped_category() {
        return mapped_category;
    }

    public void setMapped_category(String mapped_category) {
        this.mapped_category = mapped_category;
    }

    protected BranchModel(Parcel in) {
        type = in.readString();
        id = in.readString();
        mapped_category = in.readString();
    }

    public static final Creator<BranchModel> CREATOR = new Creator<BranchModel>() {
        @Override
        public BranchModel createFromParcel(Parcel in) {
            return new BranchModel(in);
        }

        @Override
        public BranchModel[] newArray(int size) {
            return new BranchModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(id);
        parcel.writeString(mapped_category);
    }
}
