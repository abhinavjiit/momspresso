package com.mycity4kids.profile

data class MilestonesResponse(
        val code: Int?,
        val `data`: MilestonesData?,
        val error: Boolean?,
        val error_code: Any?,
        val reason: String?,
        val status: String?
)

data class MilestonesData(
        val result: List<MilestonesResult>?
)

data class MilestonesResult(
        val content_id: String?,
        val id: String?,
        val item_type: String?,
        val meta_image: String?,
        val milestone_bg_url: String?,
        val milestone_desc: String?,
        val milestone_image_url: String?,
        val milestone_metaclass: String?,
        val milestone_name: String?,
        val milestone_sharing_url: String?,
        val milestone_title: String?,
        val user_id: String?
)