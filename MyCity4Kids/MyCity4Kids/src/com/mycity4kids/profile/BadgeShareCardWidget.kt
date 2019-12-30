package com.mycity4kids.profile

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.mycity4kids.R
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.response.BadgeListResponse
import com.squareup.picasso.Picasso

class BadgeShareCardWidget : LinearLayout {

    private lateinit var badgeBgImageViewBitmap: ImageView
    private lateinit var badgeImageViewBitmap: ImageView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeView()
    }

    private fun initializeView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.badges_share_card_view, this)

        badgeBgImageViewBitmap = findViewById(R.id.badgeBgImageViewBitmap)
        badgeImageViewBitmap = findViewById(R.id.badgeImageViewBitmap)
    }

    fun populateBadgesDetails(data: BadgeListResponse.BadgeListData.BadgeListResult?) {
        Picasso.with(context).load(data?.badge_image_url).error(R.drawable.default_article)
                .fit().into(badgeImageViewBitmap)
        Picasso.with(context).load(data?.badge_bg_url).error(R.drawable.default_article)
                .fit().into(badgeBgImageViewBitmap)
    }

    fun populateMilestonesDetails(data: MilestonesResult?) {
        Picasso.with(context).load(data?.milestone_bg_url).error(R.drawable.default_article)
                .fit().into(badgeBgImageViewBitmap)
        if (data?.item_type == AppConstants.CONTENT_TYPE_MYMONEY) {
            badgeImageViewBitmap.setImageDrawable(null)
        } else {
            Picasso.with(context).load(data?.milestone_image_url).error(R.drawable.default_article)
                    .fit().into(badgeImageViewBitmap)
        }
    }
}