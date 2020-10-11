package com.mycity4kids.ui.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mycity4kids.R
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.ui.adapter.BloggerGoldPagerAdapter
import com.mycity4kids.utils.AppUtils
import com.squareup.picasso.Picasso

class BloggerGoldActivity : BaseActivity() {

    private lateinit var back: ImageView
    private lateinit var thumbNail: ImageView
    private lateinit var tabs: TabLayout
    private lateinit var viewpager: ViewPager
    private lateinit var bloggerGoldPagerAdapter: BloggerGoldPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blogger_gold_activity)
        back = findViewById(R.id.back)
        thumbNail = findViewById(R.id.thumbNail)
        viewpager = findViewById(R.id.viewpager)
        tabs = findViewById(R.id.tabs)

        tabs.apply {
            addTab(tabs.newTab().setText(getString(R.string.about_txt)))
            addTab(tabs.newTab().setText(getString(R.string.my_dashboard)))
        }

        Picasso.get().load(AppUtils.getBloggerGoldImageUrl()).into(thumbNail)

        bloggerGoldPagerAdapter = BloggerGoldPagerAdapter(
            supportFragmentManager
        )
        viewpager.adapter = bloggerGoldPagerAdapter
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tab?.position!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tab?.position!!
                if (tab.text == getString(R.string.about_txt)) {
                    Utils.shareEventTracking(
                        this@BloggerGoldActivity,
                        "BB Program Page",
                        "BirthdayBonanza_Android",
                        "AboutTab_BB"
                    )
                } else if (tab.text == getString(R.string.my_dashboard)) {
                    Utils.shareEventTracking(
                        this@BloggerGoldActivity,
                        "BB Program Page",
                        "BirthdayBonanza_Android",
                        "DashboardTab_BB"
                    )
                }
            }
        })
        back.setOnClickListener {
            onBackPressed()
        }
    }
}
