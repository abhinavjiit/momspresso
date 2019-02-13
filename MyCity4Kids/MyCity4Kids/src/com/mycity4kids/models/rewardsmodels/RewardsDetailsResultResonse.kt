package com.mycity4kids.models.rewardsmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class RewardsDetailsResultResonse(
        @SerializedName("contact")
        @Expose
        var contact: String? = null,
        @SerializedName("device_details")
        @Expose
        var deviceDetails: Any? = null,
        @SerializedName("dob")
        @Expose
        var dob: Long? = null,
        @SerializedName("durables")
        @Expose
        var durables: ArrayList<String>? = null,
        @SerializedName("email")
        @Expose
        var email: String? = null,
        @SerializedName("family_type")
        @Expose
        var familyType: Int? = null,
        @SerializedName("first_name")
        @Expose
        var firstName: String? = null,
        @SerializedName("gender")
        @Expose
        var gender: Int? = null,
        @SerializedName("id")
        @Expose
        var id: Int? = null,
        @SerializedName("income_slab")
        @Expose
        var incomeSlab: Any? = null,
        @SerializedName("interest")
        @Expose
        var interest: ArrayList<String>? = null,
        @SerializedName("is_mother")
        @Expose
        var isMother: Int? = null,
        @SerializedName("kids_info")
        @Expose
        var kidsInfo: ArrayList<KidsInfoResponse>? = null,
        @SerializedName("last_name")
        @Expose
        var lastName: String? = null,
        @SerializedName("latitude")
        @Expose
        var latitude: Double? = null,
        @SerializedName("location")
        @Expose
        var location: String? = null,
        @SerializedName("longitude")
        @Expose
        var longitude: Double? = null,
        @SerializedName("preferred_languages")
        @Expose
        var preferred_languages: ArrayList<String>? = null,
        @SerializedName("profession_type")
        @Expose
        var professionType: Any? = null,
        @SerializedName("social_accounts")
        @Expose
        var socialAccounts: List<SocialAccountObject>? = null,
        @SerializedName("user_id")
        @Expose
        var userId: String? = null,
        @SerializedName("work_status")
        @Expose
        var workStatus: Int? = null,
        @SerializedName("mobile_token")
        @Expose
        var mobile_token : String? = null
)



