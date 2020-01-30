package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 12/4/18.
 */

public class GroupIdCategoryIdMappingResponse extends BaseResponse {

    @SerializedName("total")
    private int total;
    @SerializedName("skip")
    private int skip;
    @SerializedName("limit")
    private int limit;
    @SerializedName("data")
    private List<GroupsCategoriesMappingData> data;
    @SerializedName("isMember")
    private boolean isMember;

    public List<GroupsCategoriesMappingData> getData() {
        return data;
    }

    public void setData(List<GroupsCategoriesMappingData> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public class GroupsCategoriesMappingData {
        @SerializedName("result")
        private List<GroupsCategoriesMappingResult> result;

        public List<GroupsCategoriesMappingResult> getResult() {
            return result;
        }

        public void setResult(List<GroupsCategoriesMappingResult> result) {
            this.result = result;
        }

        public class GroupsCategoriesMappingResult {
            @SerializedName("id")
            private int id;
            @SerializedName("heading")
            private String heading;
            @SerializedName("subHeading")
            private String subHeading;
            @SerializedName("media")
            private String media;
            @SerializedName("platform")
            private String platform;
            @SerializedName("categoryId")
            private String categoryId;
            @SerializedName("groupId")
            private int groupId;
            @SerializedName("isActive")
            private int isActive;
            @SerializedName("cta")
            private ArrayList<String> cta = new ArrayList<>();
            @SerializedName("lang")
            private String lang;
            @SerializedName("createdAt")
            private long createdAt;
            @SerializedName("updatedAt")
            private long updatedAt;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getHeading() {
                return heading;
            }

            public void setHeading(String heading) {
                this.heading = heading;
            }

            public String getSubHeading() {
                return subHeading;
            }

            public void setSubHeading(String subHeading) {
                this.subHeading = subHeading;
            }

            public String getMedia() {
                return media;
            }

            public void setMedia(String media) {
                this.media = media;
            }

            public String getPlatform() {
                return platform;
            }

            public void setPlatform(String platform) {
                this.platform = platform;
            }

            public String getCategoryId() {
                return categoryId;
            }

            public void setCategoryId(String categoryId) {
                this.categoryId = categoryId;
            }

            public int getGroupId() {
                return groupId;
            }

            public void setGroupId(int groupId) {
                this.groupId = groupId;
            }

            public int isActive() {
                return isActive;
            }

            public void setActive(int active) {
                isActive = active;
            }

            public ArrayList<String> getCta() {
                return cta;
            }

            public void setCta(ArrayList<String> cta) {
                this.cta = cta;
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
        }
    }
}

