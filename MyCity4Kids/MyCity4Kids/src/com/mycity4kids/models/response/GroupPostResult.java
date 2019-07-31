package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.internal.LinkedTreeMap;
import com.mycity4kids.models.user.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hemant on 1/5/18.
 */

public class GroupPostResult extends BaseResponse implements Parcelable {

    private int id;
    private String content;
    private String url;
    private String type;
    private String lang;
    private int isActive;
    //    private Object mediaUrls;
    private LinkedTreeMap<String, String> mediaUrls;
    private int disableComments;
    private int isAnnon;
    private String pinnedBy;
    private int isPinned;
    private String moderatedBy;
    private String moderationStatus;
    private String moderationOn;
    private String createdOn;
    private int groupId;


    private int markedHelpful;
    private String userId;
    private boolean hasVoted;
    private long createdAt;
    private long updatedAt;
    private String pollType;
    private Object pollOptions;
    private boolean isVoted;
    private ArrayList<GroupPostCounts> counts;
    private UserDetailResult userInfo;
    private int notHelpfullCount;
    private int helpfullCount;
    private int shareCount;
    private int responseCount;
    private int totalVotesCount;
    private int option1VoteCount;
    private int option2VoteCount;
    private int option3VoteCount;
    private int option4VoteCount;
    private int commentType;
    private GroupInfoResult groupInfo;

    public GroupPostResult() {
    }

    protected GroupPostResult(Parcel in) {
        id = in.readInt();
        content = in.readString();
        url = in.readString();
        type = in.readString();
        lang = in.readString();
        isActive = in.readInt();
        disableComments = in.readInt();
        isAnnon = in.readInt();
        pinnedBy = in.readString();
        isPinned = in.readInt();
        moderatedBy = in.readString();
        moderationStatus = in.readString();
        moderationOn = in.readString();
        createdOn = in.readString();
        groupId = in.readInt();
        userId = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        pollType = in.readString();
        counts = new ArrayList<>();
        in.readTypedList(counts, GroupPostCounts.CREATOR);
        userInfo = in.readParcelable(UserInfo.class.getClassLoader());
        helpfullCount = in.readInt();
        notHelpfullCount = in.readInt();
        commentType = in.readInt();
        markedHelpful = in.readInt();
        groupInfo = in.readParcelable(GroupInfoResult.class.getClassLoader());
        int mediaUrlsSize = in.readInt();
        mediaUrls = new LinkedTreeMap<String, String>();
        for (int i = 0; i < mediaUrlsSize; i++) {
            String key = in.readString();
            String value = (String) in.readValue(String.class.getClassLoader());
            mediaUrls.put(key, value);
        }
    }

    public static final Creator<GroupPostResult> CREATOR = new Creator<GroupPostResult>() {
        @Override
        public GroupPostResult createFromParcel(Parcel in) {
            return new GroupPostResult(in);
        }

        @Override
        public GroupPostResult[] newArray(int size) {
            return new GroupPostResult[size];
        }
    };

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public LinkedTreeMap<String, String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(LinkedTreeMap<String, String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    /*public Object getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(Object mediaUrls) {
        this.mediaUrls = mediaUrls;
    }*/

    public int getDisableComments() {
        return disableComments;
    }

    public void setDisableComments(int disableComments) {
        this.disableComments = disableComments;
    }

    public int getIsAnnon() {
        return isAnnon;
    }

    public void setIsAnnon(int isAnnon) {
        this.isAnnon = isAnnon;
    }

    public String getPinnedBy() {
        return pinnedBy;
    }

    public void setPinnedBy(String pinnedBy) {
        this.pinnedBy = pinnedBy;
    }

    public int getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(int isPinned) {
        this.isPinned = isPinned;
    }

    public String getModeratedBy() {
        return moderatedBy;
    }

    public void setModeratedBy(String moderatedBy) {
        this.moderatedBy = moderatedBy;
    }

    public String getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(String moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public int getMarkedHelpful() {
        return markedHelpful;
    }

    public void setMarkedHelpful(int markedHelpful) {
        this.markedHelpful = markedHelpful;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public String getModerationOn() {
        return moderationOn;
    }

    public void setModerationOn(String moderationOn) {
        this.moderationOn = moderationOn;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPollType() {
        return pollType;
    }

    public void setPollType(String pollType) {
        this.pollType = pollType;
    }

    public Object getPollOptions() {
        return pollOptions;
    }

    public void setPollOptions(Object pollOptions) {
        this.pollOptions = pollOptions;
    }

    public boolean isVoted() {
        return isVoted;
    }

    public void setVoted(boolean voted) {
        isVoted = voted;
    }

    public ArrayList<GroupPostCounts> getCounts() {
        return counts;
    }

    public void setCounts(ArrayList<GroupPostCounts> counts) {
        this.counts = counts;
    }

    public UserDetailResult getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserDetailResult userInfo) {
        this.userInfo = userInfo;
    }


    public GroupInfoResult getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfoResult groupInfo) {
        this.groupInfo = groupInfo;
    }

    public int getNotHelpfullCount() {
        return notHelpfullCount;
    }

    public void setNotHelpfullCount(int notHelpfullCount) {
        this.notHelpfullCount = notHelpfullCount;
    }

    public int getHelpfullCount() {
        return helpfullCount;
    }

    public void setHelpfullCount(int helpfullCount) {
        this.helpfullCount = helpfullCount;
    }

    public int getCommentType() {
        return commentType;
    }

    public void setCommentType(int commentType) {
        this.commentType = commentType;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getResponseCount() {
        return responseCount;
    }

    public void setResponseCount(int responseCount) {
        this.responseCount = responseCount;
    }

    public int getTotalVotesCount() {
        return totalVotesCount;
    }

    public void setTotalVotesCount(int totalVotesCount) {
        this.totalVotesCount = totalVotesCount;
    }

    public int getOption1VoteCount() {
        return option1VoteCount;
    }

    public void setOption1VoteCount(int option1VoteCount) {
        this.option1VoteCount = option1VoteCount;
    }

    public int getOption2VoteCount() {
        return option2VoteCount;
    }

    public void setOption2VoteCount(int option2VoteCount) {
        this.option2VoteCount = option2VoteCount;
    }

    public int getOption3VoteCount() {
        return option3VoteCount;
    }

    public void setOption3VoteCount(int option3VoteCount) {
        this.option3VoteCount = option3VoteCount;
    }

    public int getOption4VoteCount() {
        return option4VoteCount;
    }

    public void setOption4VoteCount(int option4VoteCount) {
        this.option4VoteCount = option4VoteCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(type);
        dest.writeString(lang);
        dest.writeInt(isActive);
        dest.writeInt(disableComments);
        dest.writeInt(isAnnon);
        dest.writeString(pinnedBy);
        dest.writeInt(isPinned);
        dest.writeString(moderatedBy);
        dest.writeString(moderationStatus);
        dest.writeString(moderationOn);
        dest.writeString(createdOn);
        dest.writeInt(groupId);
        dest.writeString(userId);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeString(pollType);
        dest.writeTypedList(counts);
        dest.writeParcelable(userInfo, flags);
        dest.writeInt(helpfullCount);
        dest.writeInt(notHelpfullCount);
        dest.writeInt(commentType);
        dest.writeInt(markedHelpful);
        dest.writeParcelable(groupInfo, flags);
        dest.writeInt(this.mediaUrls.size());
        for (Map.Entry<String, String> entry : this.mediaUrls.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
    }
}

