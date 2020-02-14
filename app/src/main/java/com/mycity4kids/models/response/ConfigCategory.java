package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 7/12/16.
 */

public class ConfigCategory {

    @SerializedName("version")
    private int version;
    @SerializedName("location")
    private String location;
    @SerializedName("popularVersion")
    private int popularVersion;
    @SerializedName("userTypeVersion")
    private int userTypeVersion;
    @SerializedName("popularLocation")
    private String popularLocation;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPopularVersion() {
        return popularVersion;
    }

    public void setPopularVersion(int popularVersion) {
        this.popularVersion = popularVersion;
    }

    public String getPopularLocation() {
        return popularLocation;
    }

    public void setPopularLocation(String popularLocation) {
        this.popularLocation = popularLocation;
    }

    public int getUserTypeVersion() {
        return userTypeVersion;
    }

    public void setUserTypeVersion(int userTypeVersion) {
        this.userTypeVersion = userTypeVersion;
    }
}
