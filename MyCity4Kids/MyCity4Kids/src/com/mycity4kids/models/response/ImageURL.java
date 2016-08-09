package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageURL implements Parcelable {

    private String mobileWebThumbnail;
    private String clientAppThumbnail;
    private String webThumbnail;
    private String clientApp;
    private String web;
    private String mobileWeb;


    protected ImageURL(Parcel in) {
        mobileWebThumbnail = in.readString();
        clientAppThumbnail = in.readString();
        webThumbnail = in.readString();
        clientApp = in.readString();
        web = in.readString();
        mobileWeb = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mobileWebThumbnail);
        dest.writeString(clientAppThumbnail);
        dest.writeString(webThumbnail);
        dest.writeString(clientApp);
        dest.writeString(web);
        dest.writeString(mobileWeb);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageURL> CREATOR = new Creator<ImageURL>() {
        @Override
        public ImageURL createFromParcel(Parcel in) {
            return new ImageURL(in);
        }

        @Override
        public ImageURL[] newArray(int size) {
            return new ImageURL[size];
        }
    };

    public String getMobileWebThumbnail() {
        return mobileWebThumbnail;
    }

    public void setMobileWebThumbnail(String mobileWebThumbnail) {
        this.mobileWebThumbnail = mobileWebThumbnail;
    }

    public String getClientAppThumbnail() {
        return clientAppThumbnail;
    }

    public void setClientAppThumbnail(String clientAppThumbnail) {
        this.clientAppThumbnail = clientAppThumbnail;
    }

    public String getWebThumbnail() {
        return webThumbnail;
    }

    public void setWebThumbnail(String webThumbnail) {
        this.webThumbnail = webThumbnail;
    }

    public String getClientApp() {
        return clientApp;
    }

    public void setClientApp(String clientApp) {
        this.clientApp = clientApp;
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
}