package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.mycity4kids.R
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.ui.adapter.SelectContentTopicsPagerAdapter

class SelectContentTopicsActivity : BaseActivity() {

    private lateinit var viewPager: ViewPager
    private var selectedContent: ArrayList<String>? = null
    private lateinit var selectContentTopicsPagerAdapter: SelectContentTopicsPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_content_topics_activity_layout)
        viewPager = findViewById(R.id.viewPager)
        selectedContent = ArrayList()
        selectedContent = intent.getStringArrayListExtra("selectedContentContainer")
        selectContentTopicsPagerAdapter =
            SelectContentTopicsPagerAdapter(supportFragmentManager, selectedContent)
        viewPager.adapter = selectContentTopicsPagerAdapter

    }

    fun nextPageOnContinueClick() {
        when (viewPager.adapter?.count) {
            1 -> {
                gotoMyFollowingFeed()
            }
            2 -> {
                if (viewPager.currentItem < viewPager.adapter?.count!!.minus(1))
                    viewPager.currentItem = viewPager.currentItem + 1
                else
                    gotoMyFollowingFeed()

            }
            3 -> {

            }
        }

    }

    private fun gotoMyFollowingFeed() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("comingFor", "followingFeed")
        startActivity(intent)
    }

    fun previousPageOnBackClick() {
        if (viewPager.currentItem > 0) {
            viewPager.currentItem = viewPager.currentItem.minus(1)
        } else {
            finish()
        }
    }
}