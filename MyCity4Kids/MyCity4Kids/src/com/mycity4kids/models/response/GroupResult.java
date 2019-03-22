package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

/**
 * Created by hemant on 25/4/18.
 */

public class GroupResult implements Parcelable {
    private int id;
    private String title;
    private String logoImage;
    private String headerImage;
    private String description;
    private String type;
    private String url;
    private String createdBy;
    private String createdOn;
    private String hashId;
    private String lang;
    private int isActive;
    private int notificationOn;
    private int annonAllowed;
    private int dmAllowed;
    private String sentiment;
    private long createdAt;
    private long updatedAt;
    private Map<String, String> questionnaire;
    private int memberCount;
    private AdminMembers adminMembers;
    private int highlight;

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
    }

    public class AdminMembers {
        private int total;
        private int limit;
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
            private int id;
            private int groupId;
            private String userId;
            private String status;
            private String lastActivityOn;
            private int isOwner;
            private int isAdmin;
            private int isModerator;
            private String referedBy;
            private Map<String, String> questionnaireResponse;
            private String inviteCode;
            private long createdAt;
            private long updatedAt;
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
