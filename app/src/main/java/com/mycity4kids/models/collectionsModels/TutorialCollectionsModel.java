package com.mycity4kids.models.collectionsModels;


import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;

public class TutorialCollectionsModel {
    @SerializedName("userCollectionId")
    private String userCollectionId;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("userId")
    private String userId;
    @SerializedName("enabled")
    private String enabled;
    @SerializedName("deleted")
    private String deleted;
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("sortOrder")
    private int sortOrder;
    @SerializedName("totalCollectionItems")
    private String totalCollectionItems;
    @SerializedName("name")
    private String name;
    @SerializedName("isPublic")
    private String isPublic;
    @SerializedName("slugUrl")
    private String slugUrl;
    @SerializedName("itemId")
    private String itemId;
    @SerializedName("item")
    private String item; //articleId
    @SerializedName("itemType")
    private String itemType;
    @SerializedName("isFollowing")
    private boolean isFollowing;
    @SerializedName("item_info")
    private VlogsListingAndDetailResult item_info;
    @SerializedName("isFollowed")
    private String isFollowed;
    @SerializedName("user_info")
    private CollectionUserInfo user_info;

    public VlogsListingAndDetailResult getItem_info() {
        return item_info;
    }

    public void setItem_info(VlogsListingAndDetailResult item_info) {
        this.item_info = item_info;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUserCollectionId() {
        return userCollectionId;
    }

    public void setUserCollectionId(String userCollectionId) {
        this.userCollectionId = userCollectionId;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getTotalCollectionItems() {
        return totalCollectionItems;
    }

    public void setTotalCollectionItems(String totalCollectionItems) {
        this.totalCollectionItems = totalCollectionItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getSlugUrl() {
        return slugUrl;
    }

    public void setSlugUrl(String slugUrl) {
        this.slugUrl = slugUrl;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }


    public String getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(String isFollowed) {
        this.isFollowed = isFollowed;
    }

    public CollectionUserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(CollectionUserInfo user_info) {
        this.user_info = user_info;
    }

    public class CollectionUserInfo {
        @SerializedName("id")
        private String id;
        @SerializedName("firstName")
        private String firstName;
        @SerializedName("lastName")
        private String lastName;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}
