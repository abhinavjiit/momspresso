package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by hemant on 6/6/18.
 */

public class ShortStoryCommentListData implements Parcelable {
    private String _id;
    private String userName;
    private String userId;
    private ProfilePic userPic;
    private String postId;
    private String parentCommentId;
    private String createdTime;
    private String message;
    private ArrayList<ShortStoryCommentListData> replies;
    private int replies_count;

    public ShortStoryCommentListData() {

    }

    protected ShortStoryCommentListData(Parcel in) {
        _id = in.readString();
        userName = in.readString();
        userId = in.readString();
        userPic = in.readParcelable(ProfilePic.class.getClassLoader());
        postId = in.readString();
        parentCommentId = in.readString();
        createdTime = in.readString();
        message = in.readString();
        replies = in.createTypedArrayList(ShortStoryCommentListData.CREATOR);
        replies_count = in.readInt();
    }

    public static final Creator<ShortStoryCommentListData> CREATOR = new Creator<ShortStoryCommentListData>() {
        @Override
        public ShortStoryCommentListData createFromParcel(Parcel in) {
            return new ShortStoryCommentListData(in);
        }

        @Override
        public ShortStoryCommentListData[] newArray(int size) {
            return new ShortStoryCommentListData[size];
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

    public ArrayList<ShortStoryCommentListData> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<ShortStoryCommentListData> replies) {
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
