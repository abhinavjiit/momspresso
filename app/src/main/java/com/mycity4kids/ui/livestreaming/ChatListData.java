package com.mycity4kids.ui.livestreaming;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.mycity4kids.tagging.Mentions;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 6/6/18.
 */

public class ChatListData implements Parcelable {

    @SerializedName("_id")
    private String id;
    @SerializedName("userName")
    private String userName;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userPic")
    private String userPic;
    @SerializedName("postId")
    private String postId;
    @SerializedName("parentCommentId")
    private String parentCommentId;
    @SerializedName("createdTime")
    private Long createdTime;
    @SerializedName("message")
    private String message;
    @SerializedName("replies")
    private ArrayList<ChatListData> replies;
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


    public ChatListData() {

    }

    protected ChatListData(Parcel in) {
        id = in.readString();
        userName = in.readString();
        userId = in.readString();
        userPic = in.readString();
        postId = in.readString();
        parentCommentId = in.readString();
        createdTime = in.readLong();
        message = in.readString();
        replies = in.createTypedArrayList(ChatListData.CREATOR);
        repliesCount = in.readInt();
        likeCount = in.readInt();
        is_top_comment = in.readByte() != 0;
        topCommentMarked = in.readByte() != 0;
    }

    public static final Creator<ChatListData> CREATOR = new Creator<ChatListData>() {
        @Override
        public ChatListData createFromParcel(Parcel in) {
            return new ChatListData(in);
        }

        @Override
        public ChatListData[] newArray(int size) {
            return new ChatListData[size];
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

    public String getUserPic() {
        return userPic;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public void setUserPic(String userPic) {
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

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ChatListData> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<ChatListData> replies) {
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
        dest.writeString(userPic);
        dest.writeString(postId);
        dest.writeString(parentCommentId);
        dest.writeLong(createdTime);
        dest.writeString(message);
        dest.writeTypedList(replies);
        dest.writeInt(repliesCount);
        dest.writeInt(likeCount);
        dest.writeByte((byte) (is_top_comment ? 1 : 0));
        dest.writeByte((byte) (topCommentMarked ? 1 : 0));
    }
}
