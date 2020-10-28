package com.mycity4kids.models.response


import com.google.gson.annotations.SerializedName
import com.mycity4kids.profile.Author

data class SuggestedCreatorsResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("data")
    var `data`: SuggestedCreatorsData,
    @SerializedName("error")
    var error: Boolean,
    @SerializedName("error_code")
    var errorCode: Any,
    @SerializedName("reason")
    var reason: String,
    @SerializedName("status")
    var status: String
)

data class SuggestedCreatorsData(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("result")
    var result: SuggestedCreatorsResult
)

data class SuggestedCreatorsResult(
    @SerializedName("suggestion")
    var suggestion: List<SuggestedCreators>
)

data class SuggestedCreators(
    @SerializedName("adId")
    var adId: String,
    @SerializedName("appLang")
    var appLang: String,
    @SerializedName("blogTitle")
    var blogTitle: String,
    @SerializedName("blogTitleSlug")
    var blogTitleSlug: String,
    @SerializedName("cityId")
    var cityId: String,
    @SerializedName("cityName")
    var cityName: String,
    @SerializedName("colorCode")
    var colorCode: String,
    @SerializedName("createdTime")
    var createdTime: String,
    @SerializedName("dob")
    var dob: String,
    @SerializedName("emailValidated")
    var emailValidated: String,
    @SerializedName("expectedDate")
    var expectedDate: String,
    @SerializedName("firstName")
    var firstName: String,
    @SerializedName("followersCount")
    var followersCount: String,
    @SerializedName("followingCount")
    var followingCount: String,
    @SerializedName("friendscount")
    var friendscount: String,
    @SerializedName("friendsuserList")
    var friendsuserList: List<Author>,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("is_email_invalid")
    var isEmailInvalid: String,
    @SerializedName("isLangSelection")
    var isLangSelection: String,
    @SerializedName("isMother")
    var isMother: String,
    @SerializedName("isUserHandleUpdated")
    var isUserHandleUpdated: String,
    @SerializedName("isValidated")
    var isValidated: String,
    @SerializedName("lastName")
    var lastName: String,
    @SerializedName("latitude")
    var latitude: Double,
    @SerializedName("longitude")
    var longitude: Double,
    @SerializedName("preferredLanguages")
    var preferredLanguages: List<String>,
    @SerializedName("profilePicUrl")
    var profilePicUrl: ProfilePic,
    @SerializedName("rank")
    var rank: String,
    @SerializedName("ranks")
    var ranks: List<Rank>,
    @SerializedName("rewardsAdded")
    var rewardsAdded: String,
    @SerializedName("totalArticles")
    var totalArticles: String,
    @SerializedName("totalArticlesViews")
    var totalArticlesViews: String,
    @SerializedName("updatedTime")
    var updatedTime: String,
    @SerializedName("userAppLang")
    var userAppLang: String,
    @SerializedName("userBio")
    var userBio: String,
    @SerializedName("userHandle")
    var userHandle: String,
    @SerializedName("userType")
    var userType: String,
    @SerializedName("workStatus")
    var workStatus: String,
    @SerializedName("isfollowing")
    var isfollowing: String? = "0"
)

data class Rank(
    @SerializedName("langKey")
    var langKey: String,
    @SerializedName("rank")
    var rank: String
)