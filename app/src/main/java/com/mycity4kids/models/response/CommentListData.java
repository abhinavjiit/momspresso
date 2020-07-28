package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.mycity4kids.tagging.Mentions;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 6/6/18.
 */

public class CommentListData implements Parcelable {

    @SerializedName("_id")
    private String id;
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
    private int repliesCount;
    @SerializedName("likes_count")
    private int likeCount;
    @SerializedName("is_liked")
    private Boolean isLiked = false;
    @SerializedName("is_top_comment")
    private boolean is_top_comment;
    @SerializedName("mentions")
    private Map<String, Mentions> mentions;

    private boolean topCommentMarked = false;
    private String reaction;
    private boolean status;


    public CommentListData() {

    }

    protected CommentListData(Parcel in) {
        id = in.readString();
        userName = in.readString();
        userId = in.readString();
        userPic = in.readParcelable(ProfilePic.class.getClassLoader());
        postId = in.readString();
        parentCommentId = in.readString();
        createdTime = in.readString();
        message = in.readString();
        replies = in.createTypedArrayList(CommentListData.CREATOR);
        repliesCount = in.readInt();
        likeCount = in.readInt();
        is_top_comment = in.readByte() != 0;
        topCommentMarked = in.readByte() != 0;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
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

    public int getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(int repliesCount) {
        this.repliesCount = repliesCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public boolean isIs_top_comment() {
        return is_top_comment;
    }

    public boolean isTopCommentMarked() {
        return topCommentMarked;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setTopCommentMarked(boolean topCommentMarked) {
        this.topCommentMarked = topCommentMarked;
    }

    public void setIs_top_comment(boolean is_top_comment) {
        this.is_top_comment = is_top_comment;
    }

    public Map<String, Mentions> getMentions() {
        return mentions;
    }

    public void setMentions(Map<String, Mentions> mentions) {
        this.mentions = mentions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeParcelable(userPic, flags);
        dest.writeString(postId);
        dest.writeString(parentCommentId);
        dest.writeString(createdTime);
        dest.writeString(message);
        dest.writeTypedList(replies);
        dest.writeInt(repliesCount);
        dest.writeInt(likeCount);
        dest.writeByte((byte) (is_top_comment ? 1 : 0));
        dest.writeByte((byte) (topCommentMarked ? 1 : 0));
    }
}
