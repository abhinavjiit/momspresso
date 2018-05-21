package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

/**
 * Created by hemant on 25/4/18.
 */

public class GroupSettingResult implements Parcelable {
    private String id;
    private String groupId;
    private int dmAllowed;
    private int notificationOn;
    private int annonAllowed;
    private String createdAt;
    private String updatedAt;
    private Map<String, String> questionnaire;

    protected GroupSettingResult(Parcel in) {
        id = in.readString();
        groupId = in.readString();
        dmAllowed = in.readInt();
        notificationOn = in.readInt();
        annonAllowed = in.readInt();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(groupId);
        dest.writeInt(dmAllowed);
        dest.writeInt(notificationOn);
        dest.writeInt(annonAllowed);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupSettingResult> CREATOR = new Creator<GroupSettingResult>() {
        @Override
        public GroupSettingResult createFromParcel(Parcel in) {
            return new GroupSettingResult(in);
        }

        @Override
        public GroupSettingResult[] newArray(int size) {
            return new GroupSettingResult[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getDmAllowed() {
        return dmAllowed;
    }

    public void setDmAllowed(int dmAllowed) {
        this.dmAllowed = dmAllowed;
    }

    public int getNotificationOn() {
        return notificationOn;
    }

    public void setNotificationOn(int notificationOn) {
        this.notificationOn = notificationOn;
    }

    public int getAnnonAllowed() {
        return annonAllowed;
    }

    public void setAnnonAllowed(int annonAllowed) {
        this.annonAllowed = annonAllowed;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, String> getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Map<String, String> questionnaire) {
        this.questionnaire = questionnaire;
    }
}
