package com.mycity4kids.profile



import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import java.util.ArrayList


data class FeaturedItem(
        val reason: String? = null,
        val itemType: String? = null,
        val is_gold: String? = null,
        val collectionListTotal: String? = null,
        val created_at: String? = null,
        val description: String? = null,
        val title: String? = null,
        val old_video_id: String? = null,
        val category_id: Array<String>? = null,
        val updated_at: String? = null,
        val approval_time: String? = null,
        val collectionList: ArrayList<UserCollectionsModel>? = null,
        val thumbnail_milliseconds: String? = null,
        val id: String? = null,
        val lang: Any? = null,
        val publication_status: String? = null,
        val raw_url: String? = null,
        val user_agent: String? = null,
        val item: String? = null,
        val orientation: String? = null,
        val thumbnail: String? = null,
        val file_location: String? = null,
        val is_active: String? = null,
        val author: Author? = null,
        val admin_action_status: String? = null,
        val is_auto_published: String? = null,
        val url: String? = null,
        val is_group: String? = null,
        val approved_by: String? = null,
        val filename: String? = null,
        val winner: String? = null,
        val is_popular: String? = null,
        val user_id: String? = null,
        val published_lang: String? = null,
        val title_slug: String? = null,
        val uploaded_url: String? = null)
