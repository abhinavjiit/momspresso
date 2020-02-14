package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 21/5/18.
 */

public class GroupsReportContentResult implements Parcelable {
    @SerializedName("id")
    private int id;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("postId")
    private String postId;
    @SerializedName("reportedBy")
    private String reportedBy;
    @SerializedName("type")
    private String type;
    @SerializedName("reason")
    private String reason;
    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("updatedAt")
    private long updatedAt;

    public GroupsReportContentResult() {
    }

    protected GroupsReportContentResult(Parcel in) {
        id = in.readInt();
        groupId = in.readInt();
        postId = in.readString();
        reportedBy = in.readString();
        type = in.readString();
        reason = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(groupId);
        dest.writeString(postId);
        dest.writeString(reportedBy);
        dest.writeString(type);
        dest.writeString(reason);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupsReportContentResult> CREATOR = new Creator<GroupsReportContentResult>() {
        @Override
        public GroupsReportContentResult createFromParcel(Parcel in) {
            return new GroupsReportContentResult(in);
        }

        @Override
        public GroupsReportContentResult[] newArray(int size) {
            return new GroupsReportContentResult[size];
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
}
