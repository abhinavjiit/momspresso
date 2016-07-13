package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/12/16.
 */
public class ConfigCategory {
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    int version;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    String location;
}
