package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.kelltontech.utils.StringUtils;

/**
 * Created by hemant on 19/4/17.
 */
public class LanguageConfigModel implements Parcelable {
    private String langKey;
    private String name;
    private String id;
    private String tag;
    private String display_name;
    private boolean isSelected;

    public LanguageConfigModel() {

    }

    protected LanguageConfigModel(Parcel in) {
        langKey = in.readString();
        name = in.readString();
        id = in.readString();
        tag = in.readString();
        display_name = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<LanguageConfigModel> CREATOR = new Creator<LanguageConfigModel>() {
        @Override
        public LanguageConfigModel createFromParcel(Parcel in) {
            return new LanguageConfigModel(in);
        }

        @Override
        public LanguageConfigModel[] newArray(int size) {
            return new LanguageConfigModel[size];
        }
    };

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(langKey);
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(tag);
        dest.writeString(display_name);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
