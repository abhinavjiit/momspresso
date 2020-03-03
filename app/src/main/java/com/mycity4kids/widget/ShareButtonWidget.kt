package com.mycity4kids.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.mycity4kids.R
import com.mycity4kids.utils.AppUtils

class ShareButtonWidget : CardView {

    private lateinit var shareImageView: ImageView
    private lateinit var shareTextView: TextView
    private lateinit var shareContainer: RelativeLayout
    var shareText: String? = null
    var shareImage: Drawable? = null
    var bgDrawable: Drawable? = null
    private var shareBg: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeView(attrs, context)
    }

    private fun initializeView(attrs: AttributeSet?, context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.share_button_widget, this)
        if (null != attrs) {
            val a = context.obtainStyledAttributes(attrs,
                    R.styleable.ShareButtonWidget, 0, 0)
            shareText = a.getString(R.styleable.ShareButtonWidget_shareText)
            shareImage = a.getDrawable(R.styleable.ShareButtonWidget_shareImage)
            shareBg = a.getColor(R.styleable.ShareButtonWidget_shareBgColor, Color.TRANSPARENT)
            bgDrawable = a.getDrawable(R.styleable.ShareButtonWidget_shareBgDrawable)
            a.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        shareTextView = findViewById<View>(R.id.shareTextView) as TextView
        shareImageView = findViewById<View>(R.id.shareImageView) as ImageView
        shareContainer = findViewById<View>(R.id.shareContainer) as RelativeLayout
        shareTextView.text = shareText
        shareImageView.setImageDrawable(shareImage)
        this.elevation = 0.0f
        this.radius = AppUtils.dpTopx(8.0f).toFloat()
        this.setCardBackgroundColor(shareBg)
        bgDrawable?.let {
            shareContainer.background = it
        }
    }
}
