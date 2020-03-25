package com.mycity4kids.models.request

import com.google.gson.annotations.SerializedName

class PhoneContactRequest {
    @SerializedName("phoneContacts")
    lateinit var contactList: ArrayList<String>
    @SerializedName("notifType")
    lateinit var notifType: String
}
