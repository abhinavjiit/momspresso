package com.mycity4kids.models.response

import com.google.gson.annotations.SerializedName
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.mycity4kids.models.parentingdetails.DetailsBody
import com.mycity4kids.profile.Author
import com.mycity4kids.ui.livestreaming.LiveStreamResult

data class MixFeedResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: MixFeedData?,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val error_code: Any,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("status")
    val status: String
)

data class MixFeedData(
    @SerializedName("result")
    val result: List<MixFeedResult>?,
    @SerializedName("chunks")
    var chunks: String?
)

data class MixFeedResult(
    @SerializedName("isCollectionItemSelected")
    var isCollectionItemSelected: Boolean = false,
    @SerializedName("admin_action_status")
    val admin_action_status: Int = -1,
    @SerializedName("approval_time")
    val approval_time: String = "",
    @SerializedName("approved_by")
    val approved_by: String = "",
    @SerializedName("articleCount")
    val articleCount: Int = 0,
    @SerializedName("author")
    val author: Author? = null,
    @SerializedName("blogTitle", alternate = ["blog_title_slug"])
    val blogTitle: String = "",
    @SerializedName("blogTitleSlug")
    val blogTitleSlug: String = "",
    @SerializedName("body")
    val body: String = "",
    @SerializedName("category_id")
    val category_id: List<String> = emptyList(),
    @SerializedName("tags")
    var tags: ArrayList<Map<String, String>>? = null,
    @SerializedName("cities")
    private var cities: ArrayList<Map<String, String>>? = null,
    @SerializedName("colorCode")
    val colorCode: String = "",
    @SerializedName("commentUri")
    val commentUri: String = "",
    @SerializedName("comment_count")
    var comment_count: Int = 0,
    @SerializedName("commentsCount")
    val commentsCount: Int = 0,
    @SerializedName("contentType")
    val contentType: String = "",
    @SerializedName("createdTime")
    val createdTime: String = "",
    @SerializedName("created_at")
    val created_at: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("disableComment")
    val disableComment: String = "",
    @SerializedName("excerpt")
    val excerpt: String = "",
    @SerializedName("file_location")
    val file_location: String = "",
    @SerializedName("filename")
    val filename: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("imageUrl")
    val imageUrl: ImageURL = ImageURL(),
    @SerializedName("isFromDraft")
    val isFromDraft: String = "",
    @SerializedName("isMomspresso")
    val isMomspresso: String = "",
    @SerializedName("isSponsored")
    val isSponsored: String = "",
    @SerializedName("is_active")
    val is_active: Boolean = false,
    @SerializedName("is_auto_published")
    val is_auto_published: Boolean = false,
    @SerializedName("is_gold")
    val is_gold: String? = "0",
    @SerializedName("is_group")
    val is_group: Boolean = false,
    @SerializedName("is_popular")
    val is_popular: Boolean = false,
    @SerializedName("lang")
    val lang: Any = "",
    @SerializedName("like_count")
    var like_count: Int = 0,
    @SerializedName("likesCount")
    var likesCount: Int = 0,
    @SerializedName("old_video_id")
    val old_video_id: String = "",
    @SerializedName("orientation")
    val orientation: Int = -1,
    @SerializedName("processedBody")
    val processedBody: DetailsBody = DetailsBody(),
    @SerializedName("profilePic")
    val profilePic: ProfilePic = ProfilePic(),
    @SerializedName("publication_status")
    val publication_status: String = "",
    @SerializedName("published_lang")
    val published_lang: String = "",
    @SerializedName("raw_url")
    val raw_url: String = "",
    @SerializedName("reason")
    val reason: String = "",
    @SerializedName("storyImage")
    val storyImage: String = "",
    @SerializedName("thumbnail")
    val thumbnail: String = "",
    @SerializedName("thumbnail_milliseconds")
    val thumbnail_milliseconds: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("titleSlug")
    val titleSlug: String = "",
    @SerializedName("title_slug")
    val title_slug: String = "",
    @SerializedName("trendingCount")
    val trendingCount: String = "",
    @SerializedName("updated_at")
    val updated_at: String = "",
    @SerializedName("uploaded_url")
    val uploaded_url: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("userId")
    val userId: String = "",
    @SerializedName("userName")
    val userName: String = "",
    @SerializedName("userType")
    val userType: String = "",
    @SerializedName("user_agent")
    val user_agent: String = "",
    @SerializedName("user_id")
    val user_id: String = "",
    @SerializedName("videoUrl")
    val videoUrl: String = "",
    @SerializedName("view_count")
    var view_count: Int = 0,
    @SerializedName("winner")
    val winner: String? = "0",
    @SerializedName("itemType")
    val itemType: String = "",
    @SerializedName("collectionList")
    val collectionList: ArrayList<UserCollectionsModel>? = null,
    @SerializedName("collectionListTotal")
    val collectionListTotal: Int = 0,
    @SerializedName("isbookmark")
    var isbookmark: Int = 0,
    @SerializedName("isLiked")
    var isLiked: Boolean = false,
    @SerializedName("isfollowing")
    var isfollowing: String? = "0",
    @SerializedName("bookmarkId")
    var bookmarkId: String? = null,
    var isCarouselRequestRunning: Boolean = false,
    var responseReceived: Boolean = false,
    var carouselBloggerList: ArrayList<ContributorListResult>? = null,
    var recentLiveStreamsList: List<LiveStreamResult>? = null,
    var torcaiAdsData: String? = "",
    var suggestedTopicsList: List<Suggestion>? = null,
    var suggestedCreatorList: List<SuggestedCreators>? = null,
    var topCreatorList: List<ContributorListResult>? = null
)
