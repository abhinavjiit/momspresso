package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 6/6/18.
 */

public class CommentListData implements Parcelable {
    @SerializedName("_id")
    private String _id;
    @SerializedName("userName")
    private String userName;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userPic")
    private ProfilePic userPic;
    @SerializedName("postId")
    private String postId;
    @SerializedName("parentCommentId")
    private String parentCommentId;
    @SerializedName("createdTime")
    private String createdTime;
    @SerializedName("message")
    private String message;
    @SerializedName("replies")
    private ArrayList<CommentListData> replies;
    @SerializedName("replies_count")
    private int replies_count;

    public CommentListData() {

    }

    protected CommentListData(Parcel in) {
        _id = in.readString();
        userName = in.readString();
        userId = in.readString();
        userPic = in.readParcelable(ProfilePic.class.getClassLoader());
        postId = in.readString();
        parentCommentId = in.readString();
        createdTime = in.readString();
        message = in.readString();
        replies = in.createTypedArrayList(CommentListData.CREATOR);
        replies_count = in.readInt();
    }

    public static final Creator<CommentListData> CREATOR = new Creator<CommentListData>() {
        @Override
        public CommentListData createFromParcel(Parcel in) {
            return new CommentListData(in);
        }

        @Override
        public CommentListData[] newArray(int size) {
            return new CommentListData[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ProfilePic getUserPic() {
        return userPic;
    }

    public void setUserPic(ProfilePic userPic) {
        this.userPic = userPic;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<CommentListData> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<CommentListData> replies) {
        this.replies = replies;
    }

    public int getReplies_count() {
        return replies_count;
    }

    public void setReplies_count(int replies_count) {
        this.replies_count = replies_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeParcelable(userPic, flags);
        dest.writeString(postId);
        dest.writeString(parentCommentId);
        dest.writeString(createdTime);
        dest.writeString(message);
        dest.writeTypedList(replies);
        dest.writeInt(replies_count);
    }
}
