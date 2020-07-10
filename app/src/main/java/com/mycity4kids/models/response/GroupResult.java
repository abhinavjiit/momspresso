package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * Created by hemant on 25/4/18.
 */

public class GroupResult implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("logoImage")
    private String logoImage;
    @SerializedName("headerImage")
    private String headerImage;
    @SerializedName("description")
    private String description;
    @SerializedName("type")
    private String type;
    @SerializedName("url")
    private String url;
    @SerializedName("createdBy")
    private String createdBy;
    @SerializedName("createdOn")
    private String createdOn;
    @SerializedName("hashId")
    private String hashId;
    @SerializedName("lang")
    private String lang;
    @SerializedName("isActive")
    private int isActive;
    @SerializedName("notificationOn")
    private int notificationOn;
    @SerializedName("annonAllowed")
    private int annonAllowed;
    @SerializedName("dmAllowed")
    private int dmAllowed;
    @SerializedName("sentiment")
    private String sentiment;
    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("updatedAt")
    private long updatedAt;
    @SerializedName("questionnaire")
    private Map<String, String> questionnaire;
    @SerializedName("memberCount")
    private int memberCount;
    @SerializedName("adminMembers")
    private AdminMembers adminMembers;
    @SerializedName("highlight")
    private int highlight;
    @SerializedName("collectionId")
    private String collectionId;

    protected GroupResult(Parcel in) {
        id = in.readInt();
        title = in.readString();
        logoImage = in.readString();
        headerImage = in.readString();
        description = in.readString();
        type = in.readString();
        url = in.readString();
        createdBy = in.readString();
        createdOn = in.readString();
        hashId = in.readString();
        lang = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        memberCount = in.readInt();
        isActive = in.readInt();
        notificationOn = in.readInt();
        annonAllowed = in.readInt();
        dmAllowed = in.readInt();
        sentiment = in.readString();
        highlight = in.readInt();
        collectionId = in.readString();
    }

    public static final Creator<GroupResult> CREATOR = new Creator<GroupResult>() {
        @Override
        public GroupResult createFromParcel(Parcel in) {
            return new GroupResult(in);
        }

        @Override
        public GroupResult[] newArray(int size) {
            return new GroupResult[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public int getHighlight() {
        return highlight;
    }

    public void setHighlight(int highlight) {
        this.highlight = highlight;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
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

    public Map<String, String> getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Map<String, String> questionnaire) {
        this.questionnaire = questionnaire;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public AdminMembers getAdminMembers() {
        return adminMembers;
    }

    public void setAdminMembers(AdminMembers adminMembers) {
        this.adminMembers = adminMembers;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getNotificationOn() {
        return notificationOn;
    }

    public void setNotificationOn(int notificationOn) {
        this.notificationOn = notificationOn;
    }

    public int getAnnonAllowed() {
        return annonAllowed;
    }

    public void setAnnonAllowed(int annonAllowed) {
        this.annonAllowed = annonAllowed;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public int getDmAllowed() {
        return dmAllowed;
    }

    public void setDmAllowed(int dmAllowed) {
        this.dmAllowed = dmAllowed;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(logoImage);
        dest.writeString(headerImage);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeString(url);
        dest.writeString(createdBy);
        dest.writeString(createdOn);
        dest.writeString(hashId);
        dest.writeString(lang);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeInt(memberCount);
        dest.writeInt(isActive);
        dest.writeInt(notificationOn);
        dest.writeInt(annonAllowed);
        dest.writeInt(dmAllowed);
        dest.writeString(sentiment);
        dest.writeInt(highlight);
        dest.writeString(collectionId);
    }

    public class AdminMembers {

        @SerializedName("total")
        private int total;
        @SerializedName("limit")
        private int limit;
        @SerializedName("skip")
        private int skip;

        private List<AdminMemberData> data;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getSkip() {
            return skip;
        }

        public void setSkip(int skip) {
            this.skip = skip;
        }

        public List<AdminMemberData> getData() {
            return data;
        }

        public void setData(List<AdminMemberData> data) {
            this.data = data;
        }

        public class AdminMemberData {

            @SerializedName("id")
            private int id;
            @SerializedName("groupId")
            private int groupId;
            @SerializedName("userId")
            private String userId;
            @SerializedName("status")
            private String status;
            @SerializedName("lastActivityOn")
            private String lastActivityOn;
            @SerializedName("isOwner")
            private int isOwner;
            @SerializedName("isAdmin")
            private int isAdmin;
            @SerializedName("isModerator")
            private int isModerator;
            @SerializedName("referedBy")
            private String referedBy;
            @SerializedName("questionnaireResponse")
            private Map<String, String> questionnaireResponse;
            @SerializedName("inviteCode")
            private String inviteCode;
            @SerializedName("createdAt")
            private long createdAt;
            @SerializedName("updatedAt")
            private long updatedAt;
            @SerializedName("userInfo")
            private UserDetailResult userInfo;

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

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getLastActivityOn() {
                return lastActivityOn;
            }

            public void setLastActivityOn(String lastActivityOn) {
                this.lastActivityOn = lastActivityOn;
            }

            public int getIsOwner() {
                return isOwner;
            }

            public void setIsOwner(int isOwner) {
                this.isOwner = isOwner;
            }

            public int getIsAdmin() {
                return isAdmin;
            }

            public void setIsAdmin(int isAdmin) {
                this.isAdmin = isAdmin;
            }

            public int getIsModerator() {
                return isModerator;
            }

            public void setIsModerator(int isModerator) {
                this.isModerator = isModerator;
            }

            public String getReferedBy() {
                return referedBy;
            }

            public void setReferedBy(String referedBy) {
                this.referedBy = referedBy;
            }

            public Map<String, String> getQuestionnaireResponse() {
                return questionnaireResponse;
            }

            public void setQuestionnaireResponse(Map<String, String> questionnaireResponse) {
                this.questionnaireResponse = questionnaireResponse;
            }

            public String getInviteCode() {
                return inviteCode;
            }

            public void setInviteCode(String inviteCode) {
                this.inviteCode = inviteCode;
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

            public UserDetailResult getUserInfo() {
                return userInfo;
            }

            public void setUserInfo(UserDetailResult userInfo) {
                this.userInfo = userInfo;
            }
        }
    }
}
