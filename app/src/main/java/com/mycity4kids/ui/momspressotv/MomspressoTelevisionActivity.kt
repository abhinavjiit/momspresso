package com.mycity4kids.ui.momspressotv

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mycity4kids.R
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.gtmutils.Utils

class MomspressoTelevisionActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.momspresso_television_activity)

        toolbar = findViewById(R.id.toolbar)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        tabLayout.addTab(tabLayout.newTab().setText("Live & Upcoming"))
        tabLayout.addTab(tabLayout.newTab().setText("Library"))
        val mtvPagerAdapter = MomspressoTelevisionPagerAdapter(supportFragmentManager)
        viewPager.adapter = mtvPagerAdapter
        viewPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tabLayout
            )
        )
        tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    Utils.shareEventTracking(
                        this@MomspressoTelevisionActivity,
                        "Momspresso TV",
                        "Live_Android",
                        "TVL_LiveTab_Live"
                    )
                } else {
                    Utils.shareEventTracking(
                        this@MomspressoTelevisionActivity,
                        "Momspresso TV",
                        "Live_Android",
                        "TVL_LibraryTab_Live"
                    )
                }
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> {
            }
        }
        return true
    }
}
