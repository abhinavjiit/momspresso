package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfilePic implements Parcelable {
    private String mobileWebMin;
    private String clientAppMin;
    private String webMin;
    private String web;
    private String mobileWeb;
    private String clientApp;

    protected ProfilePic(Parcel in) {
        mobileWebMin = in.readString();
        clientAppMin = in.readString();
        webMin = in.readString();
        web = in.readString();
        mobileWeb = in.readString();
        clientApp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mobileWebMin);
        dest.writeString(clientAppMin);
        dest.writeString(webMin);
        dest.writeString(web);
        dest.writeString(mobileWeb);
        dest.writeString(clientApp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProfilePic> CREATOR = new Creator<ProfilePic>() {
        @Override
        public ProfilePic createFromParcel(Parcel in) {
            return new ProfilePic(in);
        }

        @Override
        public ProfilePic[] newArray(int size) {
            return new ProfilePic[size];
        }
    };

    public String getMobileWebMin() {
        return mobileWebMin;
    }

    public void setMobileWebMin(String mobileWebMin) {
        this.mobileWebMin = mobileWebMin;
    }

    public String getClientAppMin() {
        return clientAppMin;
    }

    public void setClientAppMin(String clientAppMin) {
        this.clientAppMin = clientAppMin;
    }

    public String getWebMin() {
        return webMin;
    }

    public void setWebMin(String webMin) {
        this.webMin = webMin;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getMobileWeb() {
        return mobileWeb;
    }

    public void setMobileWeb(String mobileWeb) {
        this.mobileWeb = mobileWeb;
    }

    public String getClientApp() {
        return clientApp;
    }

    public void setClientApp(String clientApp) {
        this.clientApp = clientApp;
    }
}