package com.mycity4kids.ui.activity.collection

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.ui.adapter.CollectionPagerAdapter
import com.mycity4kids.utils.AppUtils

class CollectionsActivity : BaseActivity() {

    private lateinit var collectionsViewPager: ViewPager
    private lateinit var adapter: CollectionPagerAdapter
    private lateinit var tabs: TabLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var isPrivate: Boolean = true
    private lateinit var back: ImageView
    private var userId: String = "4347159ff74b46e4ae43df01a0b405c0"

    override fun updateUi(response: Response?) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections)
        collectionsViewPager = findViewById<ViewPager>(R.id.collectionsViewPager)
        toolbar = findViewById(R.id.toolbar)
        back = findViewById(R.id.back)
        tabs = findViewById(R.id.collectionTabLayout)


        if (AppUtils.isPrivateProfile(intent.getStringExtra("userId"))) {
            isPrivate = true
            userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
        } else {
            isPrivate = false
            userId = intent.getStringExtra("userId")
        }


        back.setOnClickListener {
            onBackPressed()
        }
        if (isPrivate) {
            tabs.apply {
                addTab(tabs.newTab().setText("CREATED"))
                addTab(tabs.newTab().setText("FOLLOWED"))
            }
        } else {
            tabs.visibility = View.GONE
        }

        adapter = CollectionPagerAdapter(supportFragmentManager, isPrivate, userId)
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