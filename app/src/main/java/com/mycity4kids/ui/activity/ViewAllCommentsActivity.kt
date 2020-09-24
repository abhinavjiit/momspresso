package com.mycity4kids.ui.activity

import android.os.Bundle
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.ui.fragment.ViewAllCommentsFragment

class ViewAllCommentsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_all_comment_activity)

        try {
            val bundle = Bundle()
            bundle.putString(Constants.ARTICLE_ID, intent.getStringExtra(Constants.ARTICLE_ID))
            bundle.putString(Constants.BLOG_SLUG, intent.getStringExtra(Constants.BLOG_SLUG))
            bundle.putString(Constants.TITLE_SLUG, intent.getStringExtra(Constants.TITLE_SLUG))
            bundle.putString(Constants.AUTHOR_ID, intent.getStringExtra(Constants.AUTHOR_ID))
            bundle.putStringArrayList("tags", intent.getStringArrayListExtra("tags"))
            bundle.putString("contentType", AppConstants.CONTENT_TYPE_SHORT_STORY)
            val viewAllCommentsFragment = ViewAllCommentsFragment()
            viewAllCommentsFragment.arguments = bundle
            addFragment(viewAllCommentsFragment, bundle)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}