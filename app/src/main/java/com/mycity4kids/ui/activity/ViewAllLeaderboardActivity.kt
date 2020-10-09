package com.mycity4kids.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mycity4kids.R
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.ui.adapter.AllLeaderboardPagerAdapter

class ViewAllLeaderboardActivity : BaseActivity() {

    private lateinit var toolbarTitleTextView: TextView
    private lateinit var back: ImageView
    private lateinit var tabs: TabLayout
    private lateinit var viewpager: ViewPager
    private lateinit var allLeaderboardPagerAdapter: AllLeaderboardPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_leaderboard_activity)
        back = findViewById(R.id.back)
        viewpager = findViewById(R.id.viewpager)
        toolbarTitleTextView = findViewById(R.id.toolbarTitleTextView)
        tabs = findViewById(R.id.tabs)

        val tabName = intent.getStringExtra("tab")

        tabs.apply {
            addTab(tabs.newTab().setText(R.string.leaderboard_blogs))
            addTab(tabs.newTab().setText(R.string.leaderboard_vlogs))
        }

        back.setOnClickListener {
            onBackPressed()
        }
        setAdapter(tabName)
    }

    private fun setAdapter(tabName: String?) {
        allLeaderboardPagerAdapter = AllLeaderboardPagerAdapter(
            supportFragmentManager
        )
        viewpager.adapter = allLeaderboardPagerAdapter

        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tab?.position!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tab?.position!!
            }
        })
        if ("vlogs" == tabName) {
            viewpager.currentItem = 1
        }
    }
}
