package com.mycity4kids.models.CollectionsModels

import com.google.gson.annotations.SerializedName

class UserCollectionsListModel {
    var collections_list = ArrayList<UserCollectiosModel>()
    var collectionItems = ArrayList<UserCollectiosModel>()
    lateinit var total_collections: String
    var isPublic: Boolean = false
    var name: String = ""
    var imageUrl: String? = null
    @SerializedName("total_collection_followers")
    var totalCollectionFollowers: Int? = 0
    var userId: String? = null


}