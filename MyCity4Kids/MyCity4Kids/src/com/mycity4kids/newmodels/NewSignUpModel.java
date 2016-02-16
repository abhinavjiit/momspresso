package com.mycity4kids.newmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.mycity4kids.models.basemodel.BaseDataModel;

/**
 * Created by hemant on 19/1/16.
 */
public class NewSignUpModel extends BaseDataModel implements Parcelable {
    private String username;
    private String color_code;
    private String mobileNumber;
    private String email;
    private String password;
    private String cityId;
    private String socialMode;
    private String socialToken;
    private String profileImgUrl;

    public NewSignUpModel() {
        super();
    }

    protected NewSignUpModel(Parcel in) {
        username = in.readString();
        color_code = in.readString();
        mobileNumber = in.readString();
        email = in.readString();
        password = in.readString();
        cityId = in.readString();
        socialMode = in.readString();
        socialToken = in.readString();
        profileImgUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(color_code);
        dest.writeString(mobileNumber);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(cityId);
        dest.writeString(socialMode);
        dest.writeString(socialToken);
        dest.writeString(profileImgUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NewSignUpModel> CREATOR = new Creator<NewSignUpModel>() {
        @Override
        public NewSignUpModel createFromParcel(Parcel in) {
            return new NewSignUpModel(in);
        }

        @Override
        public NewSignUpModel[] newArray(int size) {
            return new NewSignUpModel[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getSocialMode() {
        return socialMode;
    }

    public void setSocialMode(String socialMode) {
        this.socialMode = socialMode;
    }

    public String getSocialToken() {
        return socialToken;
    }

    public void setSocialToken(String socialToken) {
        this.socialToken = socialToken;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }
}
