package com.mycity4kids.models.response

import com.mycity4kids.models.parentingdetails.DetailsBody
import java.util.*

data class MixFeedResponse(
        val code: Int,
        val data: MixFeedData,
        val error: Boolean,
        val error_code: Any,
        val reason: String,
        val status: String
)

data class MixFeedData(
        val result: List<MixFeedResult>?
)

data class MixFeedResult(
        val admin_action_status: Int,
        val approval_time: String,
        val approved_by: String,
        val articleCount: Int,
        val blogTitle: String,
        val blogTitleSlug: String,
        val body: String,
        val category_id: List<String>,
        private var tags: ArrayList<Map<String, String>>? = null,
        private var cities: ArrayList<Map<String, String>>? = null,
        val colorCode: String,
        val commentUri: String,
        val comment_count: Int,
        val commentsCount: Int,
        val contentType: String,
        val createdTime: String,
        val created_at: String,
        val description: String,
        val disableComment: String,
        val excerpt: String,
        val file_location: String,
        val filename: String,
        val id: String,
        val imageUrl: ImageURL,
        val isFromDraft: String,
        val isMomspresso: String,
        val isSponsored: String,
        val is_active: Boolean,
        val is_auto_published: Boolean,
        val is_gold: Boolean,
        val is_group: Boolean,
        val is_popular: Boolean,
        val lang: Any,
        val like_count: Int,
        val likesCount: Int,
        val old_video_id: String,
        val orientation: Int,
        val processedBody: DetailsBody,
        val profilePic: ProfilePic,
        val publication_status: String,
        val published_lang: String,
        val raw_url: String,
        val reason: String,
        val storyImage: String,
        val thumbnail: String,
        val thumbnail_milliseconds: Int,
        val title: String,
        val titleSlug: String,
        val title_slug: String,
        val trendingCount: String,
        val updated_at: String,
        val uploaded_url: String,
        val url: String,
        val userId: String,
        val userName: String,
        val userType: String,
        val user_agent: String,
        val user_id: String,
        val videoUrl: String,
        val view_count: Int,
        val winner: Boolean
)
