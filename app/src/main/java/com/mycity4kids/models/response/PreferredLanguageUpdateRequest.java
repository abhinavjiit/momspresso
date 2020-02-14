package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by hemant on 22/3/17.
 */
public class PreferredLanguageUpdateRequest {

    @SerializedName("langSubscription")
    private HashMap<String, String> langSubscription;

    public HashMap<String, String> getLangSubscription() {
        return langSubscription;
    }

    public void setLangSubscription(HashMap<String, String> langSubscription) {
        this.langSubscription = langSubscription;
    }
}
