package com.mycity4kids.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mycity4kids.ui.fragment.AllBlogLeaderboardFragment
import com.mycity4kids.ui.fragment.AllVlogLeaderboardFragment

class AllLeaderboardPagerAdapter(
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private var allBlogLeaderboardFragment: AllBlogLeaderboardFragment? = null
    private var allVlogLeaderboardFragment: AllVlogLeaderboardFragment? = null

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        if (position == 0) {
            if (allBlogLeaderboardFragment == null) {
                allBlogLeaderboardFragment = AllBlogLeaderboardFragment()
            }
            allBlogLeaderboardFragment!!.arguments = bundle
            return allBlogLeaderboardFragment as AllBlogLeaderboardFragment
        } else {
            if (allVlogLeaderboardFragment == null) {
                allVlogLeaderboardFragment = AllVlogLeaderboardFragment()
            }
            allVlogLeaderboardFragment!!.arguments = bundle
            return allVlogLeaderboardFragment as AllVlogLeaderboardFragment
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
