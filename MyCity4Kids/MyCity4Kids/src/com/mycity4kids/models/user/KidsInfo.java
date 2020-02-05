package com.mycity4kids.models.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.basemodel.BaseDataModel;

public class KidsInfo extends BaseDataModel implements Parcelable {

    @SerializedName("name")
    private String name;
    @SerializedName("date_of_birth")
    private String date_of_birth;
    @SerializedName("color_code")
    private String color_code;
    @SerializedName("id")
    private String id;
    @SerializedName("age")
    private int age;
    @SerializedName("gender")
    private String gender;

    public KidsInfo() {
    }

    public KidsInfo(Parcel in) {
        name = in.readString();
        date_of_birth = in.readString();
        color_code = in.readString();
        id = in.readString();
        age = in.readInt();
        gender = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(date_of_birth);
        dest.writeString(color_code);
        dest.writeString(id);
        dest.writeInt(age);
        dest.writeString(gender);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<KidsInfo> CREATOR = new Creator<KidsInfo>() {
        @Override
        public KidsInfo createFromParcel(Parcel in) {
            return new KidsInfo(in);
        }

        @Override
        public KidsInfo[] newArray(int size) {
            return new KidsInfo[size];
        }
    };

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
