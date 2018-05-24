package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 21/5/18.
 */

public class AddGpPostCommentReplyResponse extends BaseResponse {

    private AddGpPostCommentReplyData data;

    public AddGpPostCommentReplyData getData() {
        return data;
    }

    public void setData(AddGpPostCommentReplyData data) {
        this.data = data;
    }

    public class AddGpPostCommentReplyData {
        public AddGpPostCommentReplyResult result;

        public AddGpPostCommentReplyResult getResult() {
            return result;
        }

        public void setResult(AddGpPostCommentReplyResult result) {
            this.result = result;
        }

        public class AddGpPostCommentReplyResult {
            private int id;
            private String content;
            private String sentiment;
            private int parentId;
            private int groupId;
            private int postId;
            private String userId;
            private boolean isActive;
            private boolean isAnnon;
            private String moderationStatus;
            private String moderatedBy;
            private String moderatedon;
            private String lang;
            private long createdAt;
            private long updatedAt;
            private ArrayList<GroupPostCommentResult> childData;
            private int isLastConversation = 0;
            private int childCount;

            public AddGpPostCommentReplyResult() {

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

            public boolean isActive() {
                return isActive;
            }

            public void setActive(boolean active) {
                isActive = active;
            }

            public boolean isAnnon() {
                return isAnnon;
            }

            public void setAnnon(boolean annon) {
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
