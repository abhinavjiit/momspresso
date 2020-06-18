package com.mycity4kids.ui.activity.collection

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.ui.adapter.CollectionPagerAdapter
import com.mycity4kids.utils.AppUtils

class CollectionsActivity : BaseActivity() {

    private lateinit var collectionsViewPager: ViewPager
    private lateinit var adapter: CollectionPagerAdapter
    private lateinit var tabs: TabLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var isPrivate: Boolean = true
    private lateinit var back: TextView
    private var userId: String? = null
    private var comingFrom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections)
        collectionsViewPager = findViewById<ViewPager>(R.id.collectionsViewPager)
        toolbar = findViewById(R.id.toolbar)
        back = findViewById(R.id.back)
        tabs = findViewById(R.id.collectionTabLayout)

        if (AppUtils.isPrivateProfile(intent.getStringExtra("userId"))) {
            isPrivate = true
            userId =
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
            Utils.pushGenericEvent(
                this,
                "Show_Private_Collection_Listing",
                userId,
                "CollectionsActivity"
            )
            comingFrom = intent.getStringExtra("comingFrom")
        } else {
            isPrivate = false
            userId = intent.getStringExtra("userId")
            Utils.pushGenericEvent(
                this,
                "Show_Public_Collection_Listing",
                userId,
                "CollectionsActivity"
            )
            comingFrom = intent.getStringExtra("comingFrom")
        }

        back.setOnClickListener {
            onBackPressed()
        }
        if (isPrivate) {
            tabs.apply {
                addTab(tabs.newTab().setText(resources.getString(R.string.collection_created_tab)))
                addTab(tabs.newTab().setText(resources.getString(R.string.collection_followed_tab)))
            }
        } else {
            tabs.visibility = View.GONE
        }
        if ("followed" == comingFrom) {

            collectionsViewPager.currentItem = 1
        } else {
            collectionsViewPager.currentItem = 0
        }

        adapter = CollectionPagerAdapter(supportFragmentManager, isPrivate, userId!!)
        collectionsViewPager.adapter = adapter
        collectionsViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                collectionsViewPager.currentItem = tab!!.position
            }
        })
    }
}
