package com.mycity4kids.models.CollectionsModels

import com.google.gson.annotations.SerializedName

class UserCollectionsListModel {
    @SerializedName("collections_list")
    var collectionsList = ArrayList<UserCollectionsModel>()
    var collectionItems = ArrayList<UserCollectionsModel>()
    @SerializedName("total_collections")
    lateinit var totalCollections: String
    var isPublic: Boolean = false
    var name: String = ""
    var imageUrl: String? = null
    @SerializedName("total_collection_followers")
    var totalCollectionFollowers: Int? = 0
    var userId: String? = null
    var isFollowed: String? = null
    var collectionType: Int = 0
    var shareUrl: String? = null


}