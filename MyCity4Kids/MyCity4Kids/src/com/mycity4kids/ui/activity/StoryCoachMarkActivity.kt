package com.mycity4kids.ui.activity

import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.ui.fragment.ShortStoryLibraryFragment

class StoryCoachMarkActivity : BaseActivity() {


    private lateinit var storyCoachmark: RelativeLayout
    private lateinit var gotIt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_coachmark_activity)
        gotIt = findViewById(R.id.got_it)
        storyCoachmark = findViewById(R.id.storyCoachmark)

        gotIt.setOnClickListener {
            SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "storyCoachmark", true)
            finish()
        }

    }

    override fun onBackPressed() {

    }

}