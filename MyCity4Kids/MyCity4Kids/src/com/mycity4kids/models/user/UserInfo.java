package com.mycity4kids.models.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class UserInfo extends BaseDataModel implements Parcelable {
    private int id;
    private String mobile_number;
    private String mcity_id;
    private String first_name;

    public UserInfo() {
        super();
    }

    protected UserInfo(Parcel in) {
        id = in.readInt();
        mobile_number = in.readString();
        mcity_id = in.readString();
        first_name = in.readString();
        name = in.readString();
        last_name = in.readString();
        email = in.readString();
        sessionId = in.readString();
        profileId = in.readString();
        pincode = in.readString();
        family_id = in.readInt();
        color_code = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(mobile_number);
        dest.writeString(mcity_id);
        dest.writeString(first_name);
        dest.writeString(name);
        dest.writeString(last_name);
        dest.writeString(email);
        dest.writeString(sessionId);
        dest.writeString(profileId);
        dest.writeString(pincode);
        dest.writeInt(family_id);
        dest.writeString(color_code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private String last_name;
    private String email;
    private String sessionId;
    private String profileId;

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    private String pincode;
    private int family_id = 0;
    private String color_code = "";

    public int getFamily_id() {
        return family_id;
    }

    public void setFamily_id(int family_id) {
        this.family_id = family_id;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }


    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getMcity_id() {
        return mcity_id;
    }

    public void setMcity_id(String mcity_id) {
        this.mcity_id = mcity_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
