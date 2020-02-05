package com.mycity4kids.models.parentingdetails;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.response.ProfilePic;

import java.util.ArrayList;

public class CommentsData implements Parcelable {
    @SerializedName("id")
    private String id;
    @SerializedName("parentId")
    private String parentId;
    @SerializedName("articleId")
    private String articleId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("userComment")
    private String userComment;
    @SerializedName("createdTime")
    private String createdTime;
    @SerializedName("updatedTime")
    private String updatedTime;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userType")
    private String userType;
    @SerializedName("comment_type")
    private String comment_type;
    @SerializedName("profilePic")
    private ProfilePic profilePic;
    @SerializedName("replies")
    private ArrayList<CommentsData> replies;
    @SerializedName("commentLevel")
    private int commentLevel;
    @SerializedName("isLastConversation")
    private int isLastConversation = 0;

    public CommentsData() {

    }

    protected CommentsData(Parcel in) {
        id = in.readString();
        userId = in.readString();
        articleId = in.readString();
        parentId = in.readString();
        userName = in.readString();
        userComment = in.readString();
        createdTime = in.readString();
        updatedTime = in.readString();
        comment_type = in.readString();
        profilePic = in.readParcelable(ProfilePic.class.getClassLoader());
        replies = in.createTypedArrayList(CommentsData.CREATOR);
        commentLevel = in.readInt();
        userType = in.readString();
        isLastConversation = in.readInt();
    }

    public ArrayList<CommentsData> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<CommentsData> replies) {
        this.replies = replies;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getParent_id() {
        return parentId;
    }

    public void setParent_id(String parent_id) {
        this.parentId = parent_id;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public String getBody() {
        return userComment;
    }

    public void setBody(String body) {
        this.userComment = body;
    }

    public String getComment_type() {
        return comment_type;
    }

    public void setComment_type(String comment_type) {
        this.comment_type = comment_type;
    }

    public String getCreate() {
        return createdTime;
    }

    public void setCreate(String create) {
        this.createdTime = create;
    }

    public ProfilePic getProfile_image() {
        return profilePic;
    }

    public void setProfile_image(ProfilePic profile_image) {
        this.profilePic = profile_image;
    }

    public int getCommentLevel() {
        return commentLevel;
    }

    public void setCommentLevel(int commentLevel) {
        this.commentLevel = commentLevel;
    }

    public int getIsLastConversation() {
        return isLastConversation;
    }

    public void setIsLastConversation(int isLastConversation) {
        this.isLastConversation = isLastConversation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(articleId);
        dest.writeString(parentId);
        dest.writeString(userName);
        dest.writeString(userComment);
        dest.writeString(createdTime);
        dest.writeString(updatedTime);
        dest.writeString(comment_type);
        dest.writeParcelable(profilePic, flags);
        dest.writeTypedList(replies);
        dest.writeInt(commentLevel);
        dest.writeString(userType);
        dest.writeInt(isLastConversation);
    }

    public static final Creator<CommentsData> CREATOR = new Creator<CommentsData>() {
        @Override
        public CommentsData createFromParcel(Parcel in) {
            return new CommentsData(in);
        }

        @Override
        public CommentsData[] newArray(int size) {
            return new CommentsData[size];
        }
    };

}
