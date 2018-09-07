package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 12/4/18.
 */

public class GroupIdCategoryIdMappingResponse extends BaseResponse {

    private int total;
    private int skip;
    private int limit;
    private List<GroupsCategoriesMappingData> data;
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
        private List<GroupsCategoriesMappingResult> result;

        public List<GroupsCategoriesMappingResult> getResult() {
            return result;
        }

        public void setResult(List<GroupsCategoriesMappingResult> result) {
            this.result = result;
        }

        public class GroupsCategoriesMappingResult {
            private int id;
            private String heading;
            private String subHeading;
            private String media;
            private String platform;
            private String categoryId;
            private int groupId;
            private int isActive;
            private ArrayList<String> cta = new ArrayList<>();
            private String lang;
            private long createdAt;
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

