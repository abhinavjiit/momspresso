package com.mycity4kids.tagging;

import android.os.Parcel;
import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import com.mycity4kids.tagging.mentions.Mentionable;

public class Mentions implements Mentionable {

    @SerializedName("userId")
    private String userId;
    @SerializedName("name")
    private String name;
    @SerializedName("userHandle")
    private String userHandle;
    @SerializedName("profileUrl")
    private String profileUrl;

    public Mentions(String userId, String name, String userHandle, String profileUrl) {
        this.userId = userId;
        this.name = name;
        this.userHandle = userHandle;
        this.profileUrl = profileUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @NonNull
    @Override
    public String getTextForDisplayMode(@NonNull MentionDisplayMode mode) {
        switch (mode) {
            case FULL:
                return name;
            case PARTIAL:
            case NONE:
            default:
                return "";
        }
    }

    @NonNull
    @Override
    public MentionDeleteStyle getDeleteStyle() {
        return MentionDeleteStyle.PARTIAL_NAME_DELETE;
    }

    @Override
    public int getSuggestibleId() {
        return name.hashCode();
    }

    @Override
    public String getSuggestiblePrimaryText() {
        return name;
    }

    protected Mentions(Parcel in) {
        userId = in.readString();
        name = in.readString();
        userHandle = in.readString();
        profileUrl = in.readString();
    }

    public static final Creator<Mentions> CREATOR = new Creator<Mentions>() {
        @Override
        public Mentions createFromParcel(Parcel in) {
            return new Mentions(in);
        }

        @Override
        public Mentions[] newArray(int size) {
            return new Mentions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
        dest.writeString(userHandle);
        dest.writeString(profileUrl);
    }
}
