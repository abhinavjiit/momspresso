package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 21/5/18.
 */

public class GroupsCategoryMappingResult implements Parcelable {
    @SerializedName("id")
    private int id;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("categoryId")
    private String categoryId;
    @SerializedName("categoryName")
    private String categoryName;
    @SerializedName("createdBy")
    private String createdBy;
    @SerializedName("isActive")
    private int isActive;
    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("updatedAt")
    private long updatedAt;
    @SerializedName("isSelected")
    private boolean isSelected;

    public GroupsCategoryMappingResult() {
    }

    protected GroupsCategoryMappingResult(Parcel in) {
        id = in.readInt();
        groupId = in.readInt();
        categoryId = in.readString();
        categoryName = in.readString();
        createdBy = in.readString();
        isActive = in.readInt();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<GroupsCategoryMappingResult> CREATOR = new Creator<GroupsCategoryMappingResult>() {
        @Override
        public GroupsCategoryMappingResult createFromParcel(Parcel in) {
            return new GroupsCategoryMappingResult(in);
        }

        @Override
        public GroupsCategoryMappingResult[] newArray(int size) {
            return new GroupsCategoryMappingResult[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
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
        dest.writeInt(id);
        dest.writeInt(groupId);
        dest.writeString(categoryId);
        dest.writeString(categoryName);
        dest.writeString(createdBy);
        dest.writeInt(isActive);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
