package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.List;

public class FeaturedOnListResponse extends BaseResponse {

    private List<FeaturedListData> data;

    public List<FeaturedListData> getData() {
        return data;
    }

    public void setData(List<FeaturedListData> data) {
        this.data = data;
    }


    public class FeaturedListData {

        private ArrayList<FeaturedListResult> result;
        private String msg;

        public ArrayList<FeaturedListResult> getResult() {
            return result;
        }

        public void setResult(ArrayList<FeaturedListResult> result) {
            this.result = result;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public class FeaturedListResult {
        private List<FeaturedCollectionList> collections_list;
        private int total_collections;

        public List<FeaturedCollectionList> getCollections_list() {
            return collections_list;
        }

        public void setCollections_list(List<FeaturedCollectionList> collections_list) {
            this.collections_list = collections_list;
        }

        public int getTotal_collections() {
            return total_collections;
        }

        public void setTotal_collections(int total_collections) {
            this.total_collections = total_collections;
        }
    }

    public class FeaturedCollectionList {
        private String created_at;
        private boolean deleted;
        private boolean enabled;
        private String imageUrl;
        private boolean isPublic;
        private String name;
        private String shareUrl;
        private String slugUrl;
        private int sortOrder;
        private String summary;
        private String updated_at;
        private String userCollectionId;
        private String userId;

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public boolean isPublic() {
            return isPublic;
        }

        public void setPublic(boolean aPublic) {
            isPublic = aPublic;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }

        public String getSlugUrl() {
            return slugUrl;
        }

        public void setSlugUrl(String slugUrl) {
            this.slugUrl = slugUrl;
        }

        public int getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(int sortOrder) {
            this.sortOrder = sortOrder;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getUserCollectionId() {
            return userCollectionId;
        }

        public void setUserCollectionId(String userCollectionId) {
            this.userCollectionId = userCollectionId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
