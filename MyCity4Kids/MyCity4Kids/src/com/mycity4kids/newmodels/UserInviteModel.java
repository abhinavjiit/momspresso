package com.mycity4kids.newmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.models.user.UserInfo;

import java.util.ArrayList;

/**
 * Created by hemant on 1/2/16.
 */
public class UserInviteModel extends BaseModel implements Parcelable {

    private String userId;
    private String email;
    private String mobile;
    private String profileImgUrl;
    private String colorCode;
    private ArrayList<FamilyInvites> familyInvites;

    public UserInviteModel() {
        super();
    }

    public UserInviteModel(Parcel in) {
        super(in);
        userId = in.readString();
        email = in.readString();
        mobile = in.readString();
        profileImgUrl = in.readString();
        colorCode = in.readString();
        familyInvites = in.readArrayList(FamilyInvites.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(userId);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(profileImgUrl);
        dest.writeString(colorCode);
        dest.writeList(familyInvites);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInviteModel> CREATOR = new Creator<UserInviteModel>() {
        @Override
        public UserInviteModel createFromParcel(Parcel in) {
            return new UserInviteModel(in);
        }

        @Override
        public UserInviteModel[] newArray(int size) {
            return new UserInviteModel[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public ArrayList<FamilyInvites> getFamilyInvites() {
        return familyInvites;
    }

    public void setFamilyInvites(ArrayList<FamilyInvites> familyInvites) {
        this.familyInvites = familyInvites;
    }

}
