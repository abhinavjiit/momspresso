package com.mycity4kids.models.collectionsModels


class UpdateCollectionRequestModel {
    var name: String? = null
    var userId: String? = null
    var userCollectionId: ArrayList<String>? = null
    var item: String? = null
    var imageUrl: String? = null
    var itemType: String? = null
    var deleted: Boolean? = false
    var isPublic: Boolean? = false
    var summary: String? = null
}
