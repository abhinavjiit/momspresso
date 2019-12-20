package com.mycity4kids.models.collectionsModels

import com.google.gson.annotations.SerializedName


class UpdateCollectionRequestModel {
    @SerializedName("name")
    var name: String? = null
    @SerializedName("userId")
    var userId: String? = null
    @SerializedName("userCollectionId")
    var userCollectionId: ArrayList<String>? = null
    @SerializedName("item")
    var item: String? = null
    @SerializedName("imageUrl")
    var imageUrl: String? = null
    @SerializedName("itemType")
    var itemType: String? = null
    @SerializedName("deleted")
    var deleted: Boolean? = false
    @SerializedName("isPublic")
    var isPublic: Boolean? = false
    @SerializedName("summary")
    var summary: String? = null
}
