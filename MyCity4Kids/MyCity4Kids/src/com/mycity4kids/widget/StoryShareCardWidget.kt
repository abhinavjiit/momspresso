package com.mycity4kids.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mycity4kids.R
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.preference.SharedPrefUtils
import com.squareup.picasso.Picasso

class StoryShareCardWidget : LinearLayout {

    private lateinit var storyImageView: ImageView
    private lateinit var logoImageView: ImageView
    private lateinit var storyAuthorTextView: TextView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeView()
    }

    private fun initializeView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.story_share_card_widget, this)

        storyImageView = findViewById(R.id.storyImageView)
        logoImageView = findViewById(R.id.logoImageView)
        storyAuthorTextView = findViewById(R.id.storyAuthorTextView)

        if (AppConstants.LOCALE_ENGLISH == SharedPrefUtils.getAppLocale(context)) {
            logoImageView.setImageResource(R.drawable.app_logo)
        } else if (AppConstants.LOCALE_HINDI == SharedPrefUtils.getAppLocale(context)) {
            logoImageView.setImageResource(R.drawable.app_logo)
        } else if (AppConstants.LOCALE_MARATHI == SharedPrefUtils.getAppLocale(context)) {
            logoImageView.setImageResource(R.drawable.app_logo)
        } else if (AppConstants.LOCALE_BENGALI == SharedPrefUtils.getAppLocale(context)) {
            logoImageView.setImageResource(R.drawable.app_logo)
        } else if (AppConstants.LOCALE_TAMIL == SharedPrefUtils.getAppLocale(context)) {
            logoImageView.setImageResource(R.drawable.app_logo)
        } else if (AppConstants.LOCALE_TELUGU == SharedPrefUtils.getAppLocale(context)) {
            logoImageView.setImageResource(R.drawable.app_logo)
        } else if (AppConstants.LOCALE_KANNADA == SharedPrefUtils.getAppLocale(context)) {
            logoImageView.setImageResource(R.drawable.app_logo)
        } else if (AppConstants.LOCALE_MALAYALAM == SharedPrefUtils.getAppLocale(context)) {
            logoImageView.setImageResource(R.drawable.app_logo)
        } else if (AppConstants.LOCALE_GUJARATI == SharedPrefUtils.getAppLocale(context)) {
            logoImageView.setImageResource(R.drawable.app_logo)
        } else if (AppConstants.LOCALE_PUNJABI == SharedPrefUtils.getAppLocale(context)) {
            logoImageView.setImageResource(R.drawable.app_logo)
        } else {
            logoImageView.setImageResource(R.drawable.app_logo)
        }
    }

    fun populateStoryDetails(storyImageUrl: String?, storyAuthor: String?) {
        storyImageUrl?.let {
            Picasso.with(context).load(it).error(R.drawable.default_article)
                    .fit().into(storyImageView)
        }
        storyAuthorTextView.text = storyAuthor
    }
}