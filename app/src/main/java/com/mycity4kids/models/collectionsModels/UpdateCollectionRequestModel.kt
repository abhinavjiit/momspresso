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
    var isPublic: Boolean? = null
    @SerializedName("summary")
    var summary: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateCollectionRequestModel

        if (item != other.item) return false

        return true
    }

    override fun hashCode(): Int {
        return item?.hashCode() ?: 0
    }


}
