package com.mycity4kids.models.collectionsModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserCollectionsListModel {
    @SerializedName("collections_list", alternate = ["collection_list"])
    var collectionsList = ArrayList<UserCollectionsModel>()
    @SerializedName("collectionItems")
    var collectionItems = ArrayList<UserCollectionsModel>()
    @SerializedName("total_collections")
    @Expose
    lateinit var totalCollections: String
    @SerializedName("isPublic")
    var isPublic: Boolean = false
    @SerializedName("name")
    var name: String = ""
    @SerializedName("imageUrl")
    var imageUrl: String? = null
    @SerializedName("total_collection_followers")
    @Expose
    var totalCollectionFollowers: Int? = 0
    @SerializedName("userId")
    var userId: String? = null
    @SerializedName("isFollowed")
    var isFollowed: String? = null
    @SerializedName("collectionType")
    var collectionType: Int = 0
    @SerializedName("shareUrl")
    var shareUrl: String? = null
    @SerializedName("summary")
    var summary: String? = null
}