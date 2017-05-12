package com.mycity4kids.models.response;

import com.kelltontech.utils.StringUtils;

/**
 * Created by hemant on 19/4/17.
 */
public class LanguageConfigModel {
    private String langKey;
    private String name;
    private String id;
    private String tag;
    private String display_name;

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
}
