package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

public class KidsModel implements Parcelable {
    private String name;
    private String birthDay;
    private String colorCode;
    private String gender;

    public KidsModel() {
    }

    protected KidsModel(Parcel in) {
        name = in.readString();
        birthDay = in.readString();
        colorCode = in.readString();
        gender = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(birthDay);
        dest.writeString(colorCode);
        dest.writeString(gender);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<KidsModel> CREATOR = new Creator<KidsModel>() {
        @Override
        public KidsModel createFromParcel(Parcel in) {
            return new KidsModel(in);
        }

        @Override
        public KidsModel[] newArray(int size) {
            return new KidsModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}