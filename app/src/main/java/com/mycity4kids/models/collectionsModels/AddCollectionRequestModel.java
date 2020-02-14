package com.mycity4kids.models.collectionsModels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AddCollectionRequestModel {

    @SerializedName("name")
    private String name;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userCollectionId")
    private String userCollectionId;
    @SerializedName("item")
    private String item;
    @SerializedName("itemType")
    private String itemType;
    @SerializedName("itemId")
    private String itemId;
    @SerializedName("deleted")
    private Boolean deleted;
    @SerializedName("listItemId")
    private ArrayList<String> listItemId;

    public ArrayList<String> getListItemId() {
        return listItemId;
    }

    public void setListItemId(ArrayList<String> listItemId) {
        this.listItemId = listItemId;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getUserCollectionId() {
        return userCollectionId;
    }

    public void setUserCollectionId(String userCollectionId) {
        this.userCollectionId = userCollectionId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
