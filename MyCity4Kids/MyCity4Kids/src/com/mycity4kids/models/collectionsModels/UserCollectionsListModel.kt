package com.mycity4kids.models.collectionsModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserCollectionsListModel {
    @SerializedName("collections_list", alternate = ["collection_list"])
    var collectionsList = ArrayList<UserCollectionsModel>()
    var collectionItems = ArrayList<UserCollectionsModel>()
    @SerializedName("total_collections")
    @Expose
    lateinit var totalCollections: String
    var isPublic: Boolean = false
    var name: String = ""
    var imageUrl: String? = null
    @SerializedName("total_collection_followers")
    @Expose
    var totalCollectionFollowers: Int? = 0
    var userId: String? = null
    var isFollowed: String? = null
    var collectionType: Int = 0
    var shareUrl: String? = null
    var summary: String? = null


}