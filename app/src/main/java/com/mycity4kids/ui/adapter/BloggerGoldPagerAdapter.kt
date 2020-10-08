package com.mycity4kids.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.ui.fragment.BloggerGoldAboutFragment
import com.mycity4kids.ui.fragment.BloggerGoldDashboardFragment

class BloggerGoldPagerAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private var bloggerGoldAboutFragment: BloggerGoldAboutFragment? = null

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            if (bloggerGoldAboutFragment == null) {
                bloggerGoldAboutFragment = BloggerGoldAboutFragment()
            }
            val bundle = Bundle()
//            bundle.putString("rules", rules)
            bloggerGoldAboutFragment!!.arguments = bundle
            return bloggerGoldAboutFragment as BloggerGoldAboutFragment
        } else {
            val bloggerGoldDashboardFragment = BloggerGoldDashboardFragment()
            val bundle = Bundle()
//            bundle.putString("articleChallengeId", challengeId)
            bundle.putString("position", position.toString())
            bloggerGoldDashboardFragment.arguments = bundle
            return bloggerGoldDashboardFragment
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
