package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/12/16.
 */
public class ConfigCategory {
    int version;
    String location;

    int popularVersion;
    String popularLocation;

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
}
