package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.HashMap

class CampaignDetailSegment {

    @SerializedName("alloted_slots")
    @Expose
    var allotedSlots: Int? = null
    @SerializedName("created_by")
    @Expose
    var createdBy: Any? = null
    @SerializedName("created_time")
    @Expose
    var createdTime: Int? = null
    @SerializedName("email_list")
    @Expose
    var emailList: Int? = null
    @SerializedName("family_type")
    @Expose
    var familyType: Any? = null
    @SerializedName("gender_allowed")
    @Expose
    var genderAllowed: Int? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("interests")
    @Expose
    var interests: Any? = null
    @SerializedName("is_deleted")
    @Expose
    var isDeleted: Any? = null
    @SerializedName("is_expecting")
    @Expose
    var isExpecting: Int? = null
    @SerializedName("is_mother")
    @Expose
    var isMother: Int? = null
    @SerializedName("max_age")
    @Expose
    var maxAge: Int? = null
    @SerializedName("max_income")
    @Expose
    var maxIncome: Any? = null
    @SerializedName("max_kid_age")
    @Expose
    var maxKidAge: Int? = null
    @SerializedName("min_age")
    @Expose
    var minAge: Int? = null
    @SerializedName("min_fb_friend")
    @Expose
    var minFbFriend: Any? = null
    @SerializedName("min_income")
    @Expose
    var minIncome: Any? = null
    @SerializedName("min_insta_followers")
    @Expose
    var minInstaFollowers: Any? = null
    @SerializedName("min_kid_age")
    @Expose
    var minKidAge: Any? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("preferred_languages")
    @Expose
    var preferredLanguages: Any? = null
    @SerializedName("updated_time")
    @Expose
    var updatedTime: Int? = null
    @SerializedName("work_status")
    @Expose
    var workStatus: Any? = null

    /*@SerializedName("instructions")
    @Expose
    private val additionalProperties = HashMap<String, Any>()

    fun getAdditionalProperties(): Map<String, Any> {
        return this.additionalProperties
    }

    fun setAdditionalProperty(name: String, value: Any) {
        this.additionalProperties[name] = value
    }*/

}