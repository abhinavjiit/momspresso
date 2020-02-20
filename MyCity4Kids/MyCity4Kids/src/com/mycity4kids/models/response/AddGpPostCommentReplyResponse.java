package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 21/5/18.
 */

public class AddGpPostCommentReplyResponse extends BaseResponse {
    @SerializedName("data")
    private AddGpPostCommentReplyData data;

    public AddGpPostCommentReplyData getData() {
        return data;
    }

    public void setData(AddGpPostCommentReplyData data) {
        this.data = data;
    }

    public class AddGpPostCommentReplyData {
        @SerializedName("result")
        public AddGpPostCommentReplyResult result;

        public AddGpPostCommentReplyResult getResult() {
            return result;
        }

        public void setResult(AddGpPostCommentReplyResult result) {
            this.result = result;
        }

        public class AddGpPostCommentReplyResult {
            @SerializedName("id")
            private int id;
            @SerializedName("content")
            private String content;
            @SerializedName("sentiment")
            private String sentiment;
            @SerializedName("mediaUrls")
            private Object mediaUrls;
            @SerializedName("parentId")
            private int parentId;
            @SerializedName("groupId")
            private int groupId;
            @SerializedName("postId")
            private int postId;
            @SerializedName("userId")
            private String userId;
            @SerializedName("isActive")
            private int isActive;
            @SerializedName("isAnnon")
            private int isAnnon;
            @SerializedName("moderationStatus")
            private String moderationStatus;
            @SerializedName("moderatedBy")
            private String moderatedBy;
            @SerializedName("moderatedon")
            private String moderatedon;
            @SerializedName("lang")
            private String lang;
            @SerializedName("createdAt")
            private long createdAt;
            @SerializedName("updatedAt")
            private long updatedAt;
            @SerializedName("childData")
            private ArrayList<GroupPostCommentResult> childData;
            @SerializedName("isLastConversation")
            private int isLastConversation = 0;
            @SerializedName("childCount")
            private int childCount;

            public AddGpPostCommentReplyResult() {

            }

            public Object getMediaUrls() {
                return mediaUrls;
            }

            public void setMediaUrls(Object mediaUrls) {
                this.mediaUrls = mediaUrls;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getSentiment() {
                return sentiment;
            }

            public void setSentiment(String sentiment) {
                this.sentiment = sentiment;
            }

            public int getParentId() {
                return parentId;
            }

            public void setParentId(int parentId) {
                this.parentId = parentId;
            }

            public int getGroupId() {
                return groupId;
            }

            public void setGroupId(int groupId) {
                this.groupId = groupId;
            }

            public int getPostId() {
                return postId;
            }

            public void setPostId(int postId) {
                this.postId = postId;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public int isActive() {
                return isActive;
            }

            public void setActive(int active) {
                isActive = active;
            }

            public int isAnnon() {
                return isAnnon;
            }

            public void setAnnon(int annon) {
                isAnnon = annon;
            }

            public String getModerationStatus() {
                return moderationStatus;
            }

            public void setModerationStatus(String moderationStatus) {
                this.moderationStatus = moderationStatus;
            }

            public String getModeratedBy() {
                return moderatedBy;
            }

            public void setModeratedBy(String moderatedBy) {
                this.moderatedBy = moderatedBy;
            }

            public String getModeratedon() {
                return moderatedon;
            }

            public void setModeratedon(String moderatedon) {
                this.moderatedon = moderatedon;
            }

            public String getLang() {
                return lang;
            }

            public void setLang(String lang) {
                this.lang = lang;
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

            public ArrayList<GroupPostCommentResult> getChildData() {
                return childData;
            }

            public void setChildData(ArrayList<GroupPostCommentResult> childData) {
                this.childData = childData;
            }

            public int getIsLastConversation() {
                return isLastConversation;
            }

            public void setIsLastConversation(int isLastConversation) {
                this.isLastConversation = isLastConversation;
            }

            public int getChildCount() {
                return childCount;
            }

            public void setChildCount(int childCount) {
                this.childCount = childCount;
            }

        }
    }
}
