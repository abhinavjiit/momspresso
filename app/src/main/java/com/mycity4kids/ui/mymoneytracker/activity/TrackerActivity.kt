package com.mycity4kids.ui.mymoneytracker.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.ui.mymoneytracker.fragment.TrackerFragment
import kotlinx.android.synthetic.main.activity_tracker.*

class TrackerActivity : BaseActivity() {

    private var campaignId = 0
    private var totalPayout = 0
    private lateinit var brandName: String
    private lateinit var campaignName: String
    private lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_tracker)
            setSupportActionBar(toolbar)

            findViewById<ImageView>(R.id.back).setOnClickListener {
                this@TrackerActivity.finish()
            }
            campaignId = if (intent.hasExtra("campaign_id")) {
                intent.getIntExtra("campaign_id", 0)
            } else {
                0
            }

            brandName = if (intent.hasExtra("brand_name")) {
                intent.getStringExtra("brand_name")
            } else {
                ""
            }

            campaignName = if (intent.hasExtra("campaign_name")) {
                intent.getStringExtra("campaign_name")
            } else {
                ""
            }

            totalPayout = if (intent.hasExtra("total_payout")) {
                intent.getIntExtra("total_payout", 0)
            } else {
                0
            }

            imageUrl = if (intent.hasExtra("image_url")) {
                intent.getStringExtra("image_url")
            } else {
                ""
            }

            initializeXML()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    fun initializeXML() {
        var fragment = TrackerFragment.newInstance(
            campaignId = campaignId,
            brandName = brandName,
            campaignName = campaignName,
            totalPayout = totalPayout,
            imageUrl = imageUrl
        )
        supportFragmentManager.beginTransaction().replace(
            R.id.container, fragment,
            TrackerFragment::class.java.simpleName
        )
            .commit()
    }
}
