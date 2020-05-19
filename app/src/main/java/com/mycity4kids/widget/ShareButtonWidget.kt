package com.mycity4kids.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.mycity4kids.R
import com.mycity4kids.utils.AppUtils

class ShareButtonWidget : CardView {

    lateinit var shareImageView: ImageView
    lateinit var shareTextView: TextView
    lateinit var cardViewContainer: CardView
    private var shareText: String? = null
    private var shareImage: Drawable? = null
    private var bgDrawable: Drawable? = null
    private var shareImageTint: Int = 0
    private var textColor: Int = 0
    private var shareBg: Int = 0
    private var gravity: Int = 0
    private var buttonRadius: Int = 0
    private var buttonElevation: Int = 0
    private var borderThickness: Int = 0
    private var borderColor: Int = 0
    private var textStyle: Int = 0
    private var textSize: Int = 14

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initializeView(attrs, context)
    }

    private fun initializeView(attrs: AttributeSet?, context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.share_button_widget, this)
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.ShareButtonWidget, 0, 0
        )
        shareTextView = findViewById<View>(R.id.shareTextView) as TextView
        shareImageView = findViewById<View>(R.id.shareImageView) as ImageView
        cardViewContainer = findViewById<View>(R.id.cardViewContainer) as CardView
        try {
            shareText = a.getString(R.styleable.ShareButtonWidget_shareText)
            shareImage = a.getDrawable(R.styleable.ShareButtonWidget_shareImage)
            shareBg = a.getColor(R.styleable.ShareButtonWidget_shareBgColor, Color.TRANSPARENT)
            bgDrawable = a.getDrawable(R.styleable.ShareButtonWidget_shareBgDrawable)
            shareImageTint =
                a.getColor(R.styleable.ShareButtonWidget_shareImageTint, Color.TRANSPARENT)
            borderColor =
                a.getColor(R.styleable.ShareButtonWidget_borderColor, Color.TRANSPARENT)
            gravity = a.getInt(R.styleable.ShareButtonWidget_gravity, Gravity.NO_GRAVITY)
            buttonRadius =
                a.getDimensionPixelSize(R.styleable.ShareButtonWidget_radius, buttonRadius)
            buttonElevation =
                a.getDimensionPixelSize(R.styleable.ShareButtonWidget_elevation, buttonElevation)
            borderThickness =
                a.getDimensionPixelSize(
                    R.styleable.ShareButtonWidget_borderthickness,
                    borderThickness
                )
            val defaultTextSize = textSize * context.resources.displayMetrics.scaledDensity
            textSize = a.getDimensionPixelSize(
                R.styleable.ShareButtonWidget_textSize,
                defaultTextSize.toInt()
            )
            textStyle = a.getInt(R.styleable.ShareButtonWidget_textStyle, textStyle)
            textColor = a.getColor(R.styleable.ShareButtonWidget_shareTextColor, Color.WHITE)
        } finally {
            a.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (shareImage == null) {
            shareImageView.visibility = View.GONE
        }
        shareTextView.text = shareText
        shareTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        shareTextView.setTypeface(null, textStyle)
        if (shareImage == null) {
            shareImageView.visibility = View.GONE
        }
        shareTextView.gravity = gravity
        shareTextView.setTextColor(textColor)
        shareImageView.setImageDrawable(shareImage)
        shareImageView.setColorFilter(shareImageTint)
        //        this.elevation = AppUtils.dpTopx(buttonElevation.toFloat()).toFloat()
        this.radius = buttonRadius.toFloat()
        cardViewContainer.radius = buttonRadius.toFloat()
        cardViewContainer.setCardBackgroundColor(shareBg)
        this.setCardBackgroundColor(borderColor)
        this.setContentPadding(borderThickness, borderThickness, borderThickness, borderThickness)
        this.clipToOutline = true
        bgDrawable?.let {
            cardViewContainer.background = it
        }
    }

    fun setText(text: String) {
        shareText = text
        shareTextView.text = shareText
    }

    fun setTextSizeInSP(textSizeSP: Int) {
        val textSizePX = textSizeSP * context.resources.displayMetrics.scaledDensity
        shareTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePX)
    }

    fun setTextGravity(gravity: Int) {
        shareTextView.gravity = gravity
    }

    fun setButtonStartImage(drawable: Drawable?) {
        if (drawable == null) {
            shareImageView.visibility = View.GONE
        } else {
            shareImageView.visibility = View.VISIBLE
            shareImageView.setImageDrawable(drawable)
        }
    }

    fun setTextColor(color: Int) {
        shareTextView.setTextColor(color)
    }

    fun setButtonRadiusInDP(radius: Float) {
        cardViewContainer.radius = AppUtils.dpTopx(radius).toFloat()
        this.radius = AppUtils.dpTopx(radius).toFloat()
    }

    fun setButtonBackgroundColor(color: Int) {
        cardViewContainer.setCardBackgroundColor(color)
    }

    fun setBorderColor(color: Int) {
        this.setCardBackgroundColor(color)
    }

    fun setBorderThicknessInDP(thickness: Float) {
        val pxThickness = AppUtils.dpTopx(thickness)
        this.setContentPadding(pxThickness, pxThickness, pxThickness, pxThickness)
        this.clipToOutline = true
    }
}
