package com.mycity4kids.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mycity4kids.models.CollectionsModels.UserCollectiosModel

data class FeaturedItem(
        @SerializedName("collectionListTotal")
        @Expose
        var collectionListTotal: Int? = null,
        @SerializedName("id")
        @Expose
        var id: String? = null,
        @SerializedName("itemType")
        @Expose
        var itemType: String? = null,
        @SerializedName("title")
        @Expose
        var title: String? = null,
        @SerializedName("collectionList")
        @Expose
        var collectionList: ArrayList<UserCollectiosModel>? = null,
        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null

)