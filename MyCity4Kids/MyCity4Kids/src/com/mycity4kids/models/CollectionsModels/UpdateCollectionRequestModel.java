package com.mycity4kids.models.CollectionsModels;

import java.util.ArrayList;

public class UpdateCollectionRequestModel {
    private String name;
    private String userId;
    private ArrayList<String> userCollectionId;
    private String item;
    private String imageUrl;
    private String itemType;
    private Boolean deleted = false;
    private Boolean isPublic = false;

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getUserCollectionId() {
        return userCollectionId;
    }

    public void setUserCollectionId(ArrayList<String> userCollectionId) {
        this.userCollectionId = userCollectionId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
