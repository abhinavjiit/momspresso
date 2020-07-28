package com.mycity4kids.ui.activity

import android.os.Bundle
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants

class DeeplinkActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(Constants.URL)
        handleDeeplinks(url)
        finish()
    }
}
