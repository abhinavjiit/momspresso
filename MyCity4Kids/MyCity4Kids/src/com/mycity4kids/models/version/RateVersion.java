package com.mycity4kids.models.version;

import com.google.gson.annotations.SerializedName;

public class RateVersion {
    @SerializedName("appRateVersion")
    private int appRateVersion;
    @SerializedName("isAppRateComplete")
    private boolean isAppRateComplete;

    public int getAppRateVersion() {
        return appRateVersion;
    }

    public void setAppRateVersion(int appRateVersion) {
        this.appRateVersion = appRateVersion;
    }

    public boolean isAppRateComplete() {
        return isAppRateComplete;
    }

    public void setAppRateComplete(boolean isAppRateComplete) {
        this.isAppRateComplete = isAppRateComplete;
    }

}
