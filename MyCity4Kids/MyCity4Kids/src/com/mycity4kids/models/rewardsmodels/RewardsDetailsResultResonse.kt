package com.mycity4kids.models.rewardsmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class RewardsDetailsResultResonse(
        @SerializedName("contact")
        @Expose
        val contact: String? = null,
        @SerializedName("device_details")
        @Expose
        val deviceDetails: Any? = null,
        @SerializedName("dob")
        @Expose
        val dob: Int? = null,
        @SerializedName("durables")
        @Expose
        val durables: Any? = null,
        @SerializedName("email")
        @Expose
        val email: String? = null,
        @SerializedName("family_type")
        @Expose
        val familyType: Any? = null,
        @SerializedName("first_name")
        @Expose
        val firstName: String? = null,
        @SerializedName("gender")
        @Expose
        val gender: Int? = null,
        @SerializedName("id")
        @Expose
        val id: Int? = null,
        @SerializedName("income_slab")
        @Expose
        val incomeSlab: Any? = null,
        @SerializedName("interest")
        @Expose
        val interest: Any? = null,
        @SerializedName("is_mother")
        @Expose
        val isMother: Any? = null,
        @SerializedName("kids_info")
        @Expose
        val kidsInfo: List<Any>? = null,
        @SerializedName("last_name")
        @Expose
        val lastName: String? = null,
        @SerializedName("latitude")
        @Expose
        val latitude: Double? = null,
        @SerializedName("location")
        @Expose
        val location: String? = null,
        @SerializedName("longitude")
        @Expose
        val longitude: Double? = null,
        @SerializedName("mother_tongue")
        @Expose
        val motherTongue: String? = null,
        @SerializedName("profession_type")
        @Expose
        val professionType: Any? = null,
        @SerializedName("social_accounts")
        @Expose
        val socialAccounts: List<Any>? = null,
        @SerializedName("user_id")
        @Expose
        val userId: String? = null,
        @SerializedName("work_status")
        @Expose
        val workStatus: Int? = null

)



