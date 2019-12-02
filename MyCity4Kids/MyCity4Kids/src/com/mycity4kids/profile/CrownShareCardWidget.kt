package com.mycity4kids.profile

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.mycity4kids.R
import com.squareup.picasso.Picasso

class CrownShareCardWidget : LinearLayout {

    private lateinit var crownBgImageViewBitmap: ImageView
    private lateinit var crownImageViewBitmap: ImageView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeView()
    }

    private fun initializeView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.crown_share_card_view, this)

        crownBgImageViewBitmap = findViewById(R.id.crownBgImageViewBitmap)
        crownImageViewBitmap = findViewById(R.id.crownImageViewBitmap)
    }

    fun populateCrownDetails(data: Crown?) {
        Picasso.with(context).load(data?.image_url).error(R.drawable.default_article)
                .fit().into(crownImageViewBitmap)
        Picasso.with(context).load(data?.bg_url).error(R.drawable.default_article)
                .fit().into(crownBgImageViewBitmap)
    }
}