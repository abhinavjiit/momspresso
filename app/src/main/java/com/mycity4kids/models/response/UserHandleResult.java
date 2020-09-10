package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 22/6/16.
 */
public class UserHandleResult implements Parcelable {

    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("mobileAuthToken")
    private String mobileAuthToken;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("userHandle")
    private String userHandle;

    protected UserHandleResult(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        mobileAuthToken = in.readString();
        mobile = in.readString();
        userHandle = in.readString();
    }

    public static final Creator<UserHandleResult> CREATOR = new Creator<UserHandleResult>() {
        @Override
        public UserHandleResult createFromParcel(Parcel in) {
            return new UserHandleResult(in);
        }

        @Override
        public UserHandleResult[] newArray(int size) {
            return new UserHandleResult[size];
        }
    };

    public UserHandleResult() {

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileToken() {
        return mobileAuthToken;
    }

    public void setMobileToken(String mobileToken) {
        this.mobileAuthToken = mobileToken;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(email);
        parcel.writeString(mobileAuthToken);
        parcel.writeString(mobile);
        parcel.writeString(userHandle);
    }
}
