package com.mycity4kids.models.rewardsmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class RewardsDetailsResultResonse(
        @SerializedName("contact")
        @Expose
        private val contact: String? = null,
        @SerializedName("device_details")
        @Expose
        private val deviceDetails: Any? = null,
        @SerializedName("dob")
        @Expose
        private val dob: Int? = null,
        @SerializedName("durables")
        @Expose
        private val durables: Any? = null,
        @SerializedName("email")
        @Expose
        private val email: String? = null,
        @SerializedName("family_type")
        @Expose
        private val familyType: Any? = null,
        @SerializedName("first_name")
        @Expose
        private val firstName: String? = null,
        @SerializedName("gender")
        @Expose
        private val gender: Int? = null,
        @SerializedName("id")
        @Expose
        private val id: Int? = null,
        @SerializedName("income_slab")
        @Expose
        private val incomeSlab: Any? = null,
        @SerializedName("interest")
        @Expose
        private val interest: Any? = null,
        @SerializedName("is_mother")
        @Expose
        private val isMother: Any? = null,
        @SerializedName("kids_info")
        @Expose
        private val kidsInfo: List<Any>? = null,
        @SerializedName("last_name")
        @Expose
        private val lastName: String? = null,
        @SerializedName("latitude")
        @Expose
        private val latitude: Double? = null,
        @SerializedName("location")
        @Expose
        private val location: String? = null,
        @SerializedName("longitude")
        @Expose
        private val longitude: Double? = null,
        @SerializedName("mother_tongue")
        @Expose
        private val motherTongue: Any? = null,
        @SerializedName("profession_type")
        @Expose
        private val professionType: Any? = null,
        @SerializedName("social_accounts")
        @Expose
        private val socialAccounts: List<Any>? = null,
        @SerializedName("user_id")
        @Expose
        private val userId: String? = null,
        @SerializedName("work_status")
        @Expose
        private val workStatus: Int? = null

)



